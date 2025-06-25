package com.example.plantshop.ui.user.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plantshop.data.Model.OrderItem;
import com.example.plantshop.data.Model.User;
import com.example.plantshop.data.repository.CheckoutRepository;

import java.util.List;

public class CheckoutViewModel extends ViewModel {
    private final CheckoutRepository checkoutRepository = new CheckoutRepository();
    
    private final MutableLiveData<List<OrderItem>> orderItems = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalPrice = new MutableLiveData<>();
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isOrderSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public LiveData<List<OrderItem>> getOrderItems() {
        return orderItems;
    }
    
    public LiveData<Integer> getTotalPrice() {
        return totalPrice;
    }
    
    public LiveData<User> getUser() { 
        return user; 
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<Boolean> getIsOrderSuccess() {
        return isOrderSuccess;
    }
    
    public LiveData<String> getMessage() {
        return message;
    }

    public void setOrderItems(List<OrderItem> items) {
        orderItems.setValue(items);
    }
    
    public void setTotalPrice(int price) {
        totalPrice.setValue(price);
    }
    
    public void setUser(User u) { 
        user.setValue(u); 
    }
    
    public void placeOrder() {
        User currentUser = user.getValue();
        List<OrderItem> items = orderItems.getValue();
        Integer price = totalPrice.getValue();
        
        // Validation
        if (currentUser == null) {
            message.setValue("Vui lòng đăng nhập để đặt hàng!");
            return;
        }
        
        if (items == null || items.isEmpty()) {
            message.setValue("Không có sản phẩm nào để đặt hàng!");
            return;
        }
        
        if (price == null || price <= 0) {
            message.setValue("Tổng tiền không hợp lệ!");
            return;
        }
        
        // loading
        isLoading.setValue(true);
        isOrderSuccess.setValue(false);
        
        checkoutRepository.placeOrder(currentUser, items, price, new CheckoutRepository.PlaceOrderCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                isOrderSuccess.setValue(true);
            }

            @Override
            public void onFailure(String errorMessage) {
                isLoading.setValue(false);
                message.setValue("Lỗi đặt hàng: " + errorMessage);
            }
        });
    }
    
    public void resetMessage() {
        message.setValue(null);
    }
    
    public void resetOrderSuccess() {
        isOrderSuccess.setValue(false);
    }
} 