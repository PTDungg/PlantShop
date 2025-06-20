package com.example.plantshop.data.repository;

import com.example.plantshop.data.Model.Product;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface ProductCallback {
        void onResult(List<Product> products);
    }

    public void getAllProducts(ProductCallback callback) {
        db.collection("product")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> productList = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Product p = doc.toObject(Product.class);
                        p.setId(doc.getId());
                        productList.add(p);
                    }
                    callback.onResult(productList);
                })
                .addOnFailureListener(e -> {
                    callback.onResult(new ArrayList<>());
                });
    }
}


