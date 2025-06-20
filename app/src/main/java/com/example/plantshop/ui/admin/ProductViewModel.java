package com.example.plantshop.ui.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.plantshop.data.Model.Product;
import com.example.plantshop.data.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class ProductViewModel extends ViewModel {

    private final ProductRepository productRepository = new ProductRepository();

    private final MutableLiveData<List<Product>> allProducts = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Product>> filteredProducts = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Product>> getFilteredProducts() {
        return filteredProducts;
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
}

