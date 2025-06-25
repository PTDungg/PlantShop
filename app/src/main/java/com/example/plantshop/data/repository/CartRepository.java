package com.example.plantshop.data.repository;

import com.example.plantshop.data.Model.OrderItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartRepository {

    private static CartRepository instance;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private CartRepository() {}

    public static CartRepository getInstance() {
        if (instance == null) {
            instance = new CartRepository();
        }
        return instance;
    }

    public interface CartCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface CartItemsCallback {
        void onSuccess(List<OrderItem> items);
        void onFailure(String error);
    }

    public void addToCart(OrderItem item, CartCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure("Người dùng chưa đăng nhập");
            return;
        }

        // Lấy số lượng còn lại thực tế từ Firestore
        FirebaseFirestore.getInstance().collection("product")
                .document(item.getProductId())
                .get()
                .addOnSuccessListener(productDoc -> {
                    if (!productDoc.exists()) {
                        callback.onFailure("Sản phẩm không tồn tại");
                        return;
                    }
                    com.example.plantshop.data.Model.Product product = productDoc.toObject(com.example.plantshop.data.Model.Product.class);
                    int stock = product != null ? product.getQuantity() : 0;

                    // Kiểm tra số lượng đã có trong giỏ hàng
                    db.collection("users").document(userId)
                            .collection("cart")
                            .document(item.getProductId())
                            .get()
                            .addOnSuccessListener(cartDoc -> {
                                int currentInCart = 0;
                                if (cartDoc.exists()) {
                                    OrderItem existingItem = cartDoc.toObject(OrderItem.class);
                                    if (existingItem != null) {
                                        currentInCart = existingItem.getQuantity();
                                    }
                                }
                                int total = currentInCart + item.getQuantity();
                                if (total > stock) {
                                    callback.onFailure("Số lượng vượt quá tồn kho!");
                                } else {
                                    // Thực hiện thêm/cập nhật như cũ
                                    if (cartDoc.exists()) {
                                        OrderItem existingItem = cartDoc.toObject(OrderItem.class);
                                        if (existingItem != null) {
                                            existingItem.setQuantity(total);
                                            updateCartItem(userId, existingItem, callback);
                                        } else {
                                            callback.onFailure("Lỗi khi đọc dữ liệu giỏ hàng");
                                        }
                                    } else {
                                        addNewCartItem(userId, item, callback);
                                    }
                                }
                            })
                            .addOnFailureListener(e -> callback.onFailure("Lỗi kiểm tra giỏ hàng: " + e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onFailure("Lỗi kiểm tra tồn kho: " + e.getMessage()));
    }

    private void addNewCartItem(String userId, OrderItem item, CartCallback callback) {
        db.collection("users").document(userId)
                .collection("cart")
                .document(item.getProductId())
                .set(item)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure("Lỗi thêm vào giỏ hàng: " + e.getMessage()));
    }

    private void updateCartItem(String userId, OrderItem item, CartCallback callback) {
        db.collection("users").document(userId)
                .collection("cart")
                .document(item.getProductId())
                .set(item)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure("Lỗi cập nhật giỏ hàng: " + e.getMessage()));
    }

    public void getCartItems(CartItemsCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure("Người dùng chưa đăng nhập");
            return;
        }

        db.collection("users").document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<OrderItem> items = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        OrderItem item = document.toObject(OrderItem.class);
                        if (item != null) {
                            items.add(item);
                        }
                    }
                    callback.onSuccess(items);
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Lỗi lấy giỏ hàng: " + e.getMessage());
                });
    }

    public void removeFromCart(String productId, CartCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure("Người dùng chưa đăng nhập");
            return;
        }

        db.collection("users").document(userId)
                .collection("cart")
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure("Lỗi xóa khỏi giỏ hàng: " + e.getMessage()));
    }

    public void updateCartItemQuantity(String productId, int newQuantity, CartCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure("Người dùng chưa đăng nhập");
            return;
        }

        if (newQuantity <= 0) {
            removeFromCart(productId, callback);
            return;
        }

        // Lấy số lượng tồn kho thực tế từ Firestore
        FirebaseFirestore.getInstance().collection("product")
                .document(productId)
                .get()
                .addOnSuccessListener(productDoc -> {
                    if (!productDoc.exists()) {
                        callback.onFailure("Sản phẩm không tồn tại");
                        return;
                    }
                    com.example.plantshop.data.Model.Product product = productDoc.toObject(com.example.plantshop.data.Model.Product.class);
                    int stock = product != null ? product.getQuantity() : 0;

                    if (newQuantity > stock) {
                        callback.onFailure("Số lượng vượt quá tồn kho!");
                        return;
                    }

                    // Nếu hợp lệ, cập nhật số lượng trong giỏ hàng
                    db.collection("users").document(userId)
                            .collection("cart")
                            .document(productId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    OrderItem item = documentSnapshot.toObject(OrderItem.class);
                                    if (item != null) {
                                        item.setQuantity(newQuantity);
                                        updateCartItem(userId, item, callback);
                                    } else {
                                        callback.onFailure("Lỗi khi đọc dữ liệu sản phẩm");
                                    }
                                } else {
                                    callback.onFailure("Không tìm thấy sản phẩm trong giỏ hàng");
                                }
                            })
                            .addOnFailureListener(e -> {
                                callback.onFailure("Lỗi cập nhật số lượng: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> callback.onFailure("Lỗi kiểm tra tồn kho: " + e.getMessage()));
    }

    public void clearCart(CartCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure("Người dùng chưa đăng nhập");
            return;
        }

        db.collection("users").document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        callback.onSuccess();
                        return;
                    }

                    // Xóa tất cả sản phẩm trong giỏ hàng
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Lỗi xóa giỏ hàng: " + e.getMessage());
                });
    }

    private String getCurrentUserId() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }
        return null;
    }
}