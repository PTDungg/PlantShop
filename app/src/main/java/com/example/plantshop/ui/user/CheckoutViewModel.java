package com.example.plantshop.ui.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plantshop.data.Model.OrderItem;
import com.example.plantshop.data.Model.User;

import java.util.List;

public class CheckoutViewModel extends ViewModel {
    private final MutableLiveData<List<OrderItem>> orderItems = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalPrice = new MutableLiveData<>();
    private final MutableLiveData<User> user = new MutableLiveData<>();

    public LiveData<List<OrderItem>> getOrderItems() {
        return orderItems;
    }
    public LiveData<Integer> getTotalPrice() {
        return totalPrice;
    }
    public LiveData<User> getUser() { return user; }

    public void setOrderItems(List<OrderItem> items) {
        orderItems.setValue(items);
    }
    public void setTotalPrice(int price) {
        totalPrice.setValue(price);
    }
    public void setUser(User u) { user.setValue(u); }
} 