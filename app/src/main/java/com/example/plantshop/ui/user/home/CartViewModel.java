package com.example.plantshop.ui.user.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plantshop.data.Model.Item;
import com.example.plantshop.data.repository.CartRepository;

import java.util.List;

public class CartViewModel extends ViewModel {

    private final CartRepository cartRepository = CartRepository.getInstance();

    private final MutableLiveData<List<Item>> cartItems = new MutableLiveData<>();
    private final MutableLiveData<Double> totalPrice = new MutableLiveData<>(0.0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public LiveData<List<Item>> getCartItems() { return cartItems; }
    public LiveData<Double> getTotalPrice() { return totalPrice; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getMessage() { return message; }

    public void loadCartItems() {
        isLoading.setValue(true);
        cartRepository.getCartItems(new CartRepository.CartItemsCallback() {
            @Override
            public void onSuccess(List<Item> items) {
                cartItems.setValue(items);
                calculateTotalPrice(items);
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(String error) {
                message.setValue("Lỗi tải giỏ hàng: " + error);
                isLoading.setValue(false);
            }
        });
    }

    public void updateItemQuantity(String productId, int newQuantity) {
        isLoading.setValue(true);
        cartRepository.updateCartItemQuantity(productId, newQuantity, new CartRepository.CartCallback() {
            @Override
            public void onSuccess() {
                // Reload cart items to get updated data
                loadCartItems();
            }

            @Override
            public void onFailure(String error) {
                message.setValue("Lỗi cập nhật số lượng: " + error);
                isLoading.setValue(false);
            }
        });
    }

    public void removeFromCart(String productId) {
        isLoading.setValue(true);
        cartRepository.removeFromCart(productId, new CartRepository.CartCallback() {
            @Override
            public void onSuccess() {
                // Reload cart items to get updated data
                loadCartItems();
            }

            @Override
            public void onFailure(String error) {
                message.setValue("Lỗi xóa sản phẩm: " + error);
                isLoading.setValue(false);
            }
        });
    }

    public void clearCart() {
        isLoading.setValue(true);
        cartRepository.clearCart(new CartRepository.CartCallback() {
            @Override
            public void onSuccess() {
                cartItems.setValue(null);
                totalPrice.setValue(0.0);
                message.setValue("Đã xóa tất cả sản phẩm khỏi giỏ hàng");
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(String error) {
                message.setValue("Lỗi xóa giỏ hàng: " + error);
                isLoading.setValue(false);
            }
        });
    }

    private void calculateTotalPrice(List<Item> items) {
        double total = 0.0;
        if (items != null) {
            for (Item item : items) {
                total += item.getPrice() * item.getQuantity();
            }
        }
        totalPrice.setValue(total);
    }

    public void resetMessage() {
        message.setValue("");
    }
}