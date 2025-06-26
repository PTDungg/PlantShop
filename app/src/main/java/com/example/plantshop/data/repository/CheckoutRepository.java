package com.example.plantshop.data.repository;

import com.example.plantshop.data.Model.OrderItem;
import com.example.plantshop.data.Model.User;
import com.example.plantshop.data.Model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.List;

public class CheckoutRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final ProductRepository productRepository = new ProductRepository();

    public interface PlaceOrderCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public void placeOrder(User user, List<OrderItem> items, int totalPrice, PlaceOrderCallback callback) {
        String orderId = db.collection("order").document().getId();
        String orderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        int totalQuantity = 0;
        for (OrderItem item : items) {
            totalQuantity += item.getQuantity();
        }
        Order order = new Order(
                orderId,
                orderDate,
                totalQuantity,
                totalPrice,
                false,
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                items
        );
        db.collection("order").document(orderId)
                .set(order)
                .addOnSuccessListener(aVoid -> {
                    // Lưu từng item vào subcollection items
                    if (items == null || items.isEmpty()) {
                        // Cập nhật số lượng sản phẩm và xóa giỏ hàng
                        updateProductQuantities(items, () -> removeItemsFromCart(items, callback));
                        return;
                    }
                    final int[] successCount = {0};
                    final boolean[] hasFailed = {false};
                    for (OrderItem item : items) {
                        db.collection("order").document(orderId)
                            .collection("items")
                            .document(item.getProductId())
                            .set(item)
                            .addOnSuccessListener(unused -> {
                                successCount[0]++;
                                if (successCount[0] == items.size() && !hasFailed[0]) {
                                    // Cập nhật số lượng sản phẩm và xóa giỏ hàng
                                    updateProductQuantities(items, () -> removeItemsFromCart(items, callback));
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (!hasFailed[0]) {
                                    hasFailed[0] = true;
                                    callback.onFailure("Lỗi lưu sản phẩm trong đơn hàng: " + e.getMessage());
                                }
                            });
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    private void updateProductQuantities(List<OrderItem> items, Runnable onComplete) {
        if (items == null || items.isEmpty()) {
            onComplete.run();
            return;
        }

        final int[] successCount = {0};
        final int[] failureCount = {0};
        final int totalItems = items.size();

        for (OrderItem item : items) {
            productRepository.updateProductQuantity(item.getProductId(), item.getQuantity(), new ProductRepository.ProductUpdateCallback() {
                @Override
                public void onSuccess() {
                    successCount[0]++;
                    if (successCount[0] + failureCount[0] == totalItems) {
                        onComplete.run();
                    }
                }

                @Override
                public void onFailure(String error) {
                    failureCount[0]++;
                    // Vẫn tiếp tục vì đơn hàng đã được tạo thành công
                    if (successCount[0] + failureCount[0] == totalItems) {
                        onComplete.run();
                    }
                }
            });
        }
    }

    private void removeItemsFromCart(List<OrderItem> items, PlaceOrderCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onSuccess();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        if (items == null || items.isEmpty()) {
            callback.onSuccess();
            return;
        }

        final int[] successCount = {0};
        final int[] failureCount = {0};
        final int totalItems = items.size();

        for (OrderItem item : items) {
            db.collection("users").document(userId)
                .collection("cart")
                .document(item.getProductId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    successCount[0]++;
                    if (successCount[0] + failureCount[0] == totalItems) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    failureCount[0]++;
                    if (successCount[0] + failureCount[0] == totalItems) {
                        // Vẫn gọi success vì đơn hàng đã được tạo thành công
                        callback.onSuccess();
                    }
                });
        }
    }
}