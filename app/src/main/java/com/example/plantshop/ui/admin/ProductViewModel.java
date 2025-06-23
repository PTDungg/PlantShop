package com.example.plantshop.ui.admin;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.plantshop.data.Model.Product;
import com.example.plantshop.data.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ProductViewModel extends ViewModel {

    private final ProductRepository productRepository = new ProductRepository();

    private final MutableLiveData<List<Product>> allProducts = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Product>> filteredProducts = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Product> selectedProduct = new MutableLiveData<>();

    public LiveData<List<Product>> getFilteredProducts() {
        return filteredProducts;
    }
    public LiveData<Product> getSelectedProduct() {
        return selectedProduct;
    }
    public void selectProduct(Product product) {
        selectedProduct.setValue(product);
    }

    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }

    public void loadProducts() {
        productRepository.getAllProducts(products -> {
            allProducts.setValue(products);
            filteredProducts.setValue(products); // Mặc định là tất cả
        });
    }

    public void filterByCategory(String category) {
        List<Product> all = allProducts.getValue();
        if (all == null) return;

        if (category.equals("all")) {
            filteredProducts.setValue(new ArrayList<>(all));
        } else {
            List<Product> filtered = new ArrayList<>();
            for (Product p : all) {
                if (p.getCategory() != null && p.getCategory().equalsIgnoreCase(category)) {
                    filtered.add(p);
                }
            }
            filteredProducts.setValue(filtered);
        }
    }

    public void checkProductExists(String productId, Consumer<Boolean> callback) {
        productRepository.checkProductExists(productId, callback);
    }

    public void addProduct(Product product, Consumer<Boolean> callback) {
        productRepository.addProduct(product, callback);
        loadProducts();
    }
    public void updateProduct(Product product, Consumer<Boolean> callback) {
        productRepository.updateProduct(product, callback);
        loadProducts();
    }

    public void deleteProduct(String productId, Consumer<Boolean> callback) {
        productRepository.deleteProduct(productId, success -> {
            if (success) {
                loadProducts(); // Làm mới danh sách sau khi xóa thành công
            }
            callback.accept(success);
        });
    }
    public void uploadImage(Uri imageUri, Consumer<String> callback) {
        productRepository.uploadImage(imageUri, callback);
    }
    public void uploadImageAndUpdateProduct(Uri imageUri, Product product, Consumer<Boolean> callback) {
        productRepository.uploadImage(imageUri, imageUrl -> {
            if (imageUrl != null) {
                product.setImageUrl(imageUrl);
                productRepository.updateProduct(product, callback);
            } else {
                callback.accept(false);
            }
        });
    }
}

