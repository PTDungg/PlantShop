package com.example.plantshop.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.plantshop.data.Model.Notification;
import com.example.plantshop.data.Model.Order;
import com.example.plantshop.data.Model.OrderItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderRepository {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MutableLiveData<List<Order>> orders = new MutableLiveData<>();

    public OrderRepository() {
        loadOrders();
    }

    public LiveData<List<Order>> getOrders() {
        return orders;
    }

    private void loadOrders() {
        db.collection("order").addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            List<Order> orderList = new ArrayList<>();
            for (QueryDocumentSnapshot document : value) {
                Order order = document.toObject(Order.class);
                db.collection("order").document(document.getId())
                        .collection("items")
                        .get()
                        .addOnSuccessListener(itemSnapshots -> {
                            List<OrderItem> items = new ArrayList<>();
                            for (QueryDocumentSnapshot itemDoc : itemSnapshots) {
                                items.add(itemDoc.toObject(OrderItem.class));
                            }
                            order.setItems(items);
                            orderList.add(order);
                            orders.setValue(orderList);
                        });
            }
        });
    }

    public void updateOrderStatus(String orderId, boolean status) {
        db.collection("order").document(orderId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    if (status) {
                        // Lấy thông tin đơn hàng để gửi thông báo
                        db.collection("order").document(orderId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    Order order = documentSnapshot.toObject(Order.class);
                                    if (order != null) {
                                        String imageUrl = order.getItems() != null && !order.getItems().isEmpty()
                                                ? order.getItems().get(0).getImageUrl()
                                                : "";
                                        Notification notification = new Notification(
                                                db.collection("users").document(order.getCustomerEmail())
                                                        .collection("notifications").document().getId(),
                                                orderId,
                                                "Đơn hàng đã được xác nhận, chờ giao hàng",
                                                imageUrl,
                                                new Date()
                                        );
                                        db.collection("users").document(order.getCustomerEmail())
                                                .collection("notifications")
                                                .document(notification.getId())
                                                .set(notification);
                                    }
                                });
                    }
                });

    }
}

