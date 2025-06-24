package com.example.plantshop.ui.user.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plantshop.data.Model.OrderItem;
import com.example.plantshop.data.Model.Product;
import com.example.plantshop.data.repository.CartRepository;
import com.example.plantshop.data.repository.ProductRepository;

public class ProductDetailViewModel extends ViewModel {
    private final ProductRepository repository = new ProductRepository();
    private final CartRepository cartRepository = CartRepository.getInstance();

    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<Integer> quantity = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> totalPrice = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public LiveData<Product> getProduct() {
        return product;
    }

    public LiveData<Integer> getQuantity() {
        return quantity;
    }

    public LiveData<Integer> getTotalPrice() {
        return totalPrice;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsSuccess() {
        return isSuccess;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void loadProductById(String productId) {
        isLoading.setValue(true);
        repository.getProductById(productId, new ProductRepository.SingleProductCallback() {
            @Override
            public void onCallback(Product result) {
                if (result != null) {
                    product.setValue(result);
                    updateTotalPrice();
                } else {
                    message.setValue("Không tìm thấy sản phẩm");
                }
                isLoading.setValue(false);
            }
        });
    }

    public void increaseQuantity() {
        int currentQuantity = quantity.getValue() != null ? quantity.getValue() : 1;
        Product p = product.getValue();
        if (p != null && currentQuantity < p.getQuantity()) {
            quantity.setValue(currentQuantity + 1);
            updateTotalPrice();
        } else {
            message.setValue("Số lượng sản phẩm đã đạt tối đa");
        }
    }

    public void decreaseQuantity() {
        int currentQuantity = quantity.getValue() != null ? quantity.getValue() : 1;
        if (currentQuantity > 1) {
            quantity.setValue(currentQuantity - 1);
            updateTotalPrice();
        }
    }

    private void updateTotalPrice() {
        Product p = product.getValue();
        Integer q = quantity.getValue();
        if (p != null && q != null) {
            totalPrice.setValue(p.getPrice() * q);
        }
    }


    public void addToCart() {
        Product p = product.getValue();
        Integer q = quantity.getValue();

        if (p == null || q == null || q <= 0) {
            message.setValue("Thông tin sản phẩm không hợp lệ");
            return;
        }
        if (!p.isAvailable() || p.getQuantity() <= 0) {
            message.setValue("Sản phẩm đã hết hàng, không thể thêm vào giỏ!");
            return;
        }

        isLoading.setValue(true);
        isSuccess.setValue(false);

        OrderItem item = new OrderItem(p.getId(), p.getName(), q, p.getPrice(), p.getImageUrl());

        cartRepository.addToCart(item, new CartRepository.CartCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                isSuccess.setValue(true);
                message.setValue("Thêm vào giỏ hàng thành công!");
                quantity.setValue(1); // Reset quantity
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                message.setValue(error);
            }
        });
    }


    public void resetMessage() {
        message.setValue(null);
    }
}
