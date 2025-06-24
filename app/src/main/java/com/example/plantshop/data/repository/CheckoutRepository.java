package com.example.plantshop.data.repository;

import com.example.plantshop.data.Model.OrderItem;
import com.example.plantshop.data.Model.User;
import java.util.List;

public class CheckoutRepository {
    public interface PlaceOrderCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public void placeOrder(User user, List<OrderItem> items, int totalPrice, PlaceOrderCallback callback) {
        // TODO: Thực hiện lưu đơn hàng lên Firebase
        // callback.onSuccess(); hoặc callback.onFailure("Lỗi ...");
    }
} 