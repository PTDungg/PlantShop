package com.example.plantshop.ui.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.plantshop.data.Model.Order;
import com.example.plantshop.data.repository.OrderRepository;

import java.util.List;

public class OrderViewModel extends ViewModel {
    private OrderRepository repository;
    private LiveData<List<Order>> orders;

    public OrderViewModel() {
        repository = new OrderRepository();
        orders = repository.getOrders();
    }

    public LiveData<List<Order>> getOrders() {
        return orders;
    }

    public void updateOrderStatus(String orderId, boolean status) {
        repository.updateOrderStatus(orderId, status);
    }
}