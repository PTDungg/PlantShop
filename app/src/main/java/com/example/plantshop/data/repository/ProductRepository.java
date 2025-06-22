package com.example.plantshop.data.repository;

import android.net.Uri;
import android.util.Log;

import com.example.plantshop.data.Model.Product;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ProductRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference("product_images");

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

    public void checkProductExists(String productId, Consumer<Boolean> callback) {
        db.collection("product").document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> callback.accept(documentSnapshot.exists()))
                .addOnFailureListener(e -> callback.accept(false));
    }

    public void uploadImage(Uri imageUri, Consumer<String> callback) {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(imageUri)
                .continueWithTask(task -> fileRef.getDownloadUrl())
                .addOnSuccessListener(uri -> callback.accept(uri.toString()))
                .addOnFailureListener(e -> callback.accept(null));
    }

    public void addProduct(Product product, Consumer<Boolean> callback) {
        db.collection("product").document(product.getId())
                .set(product)
                .addOnSuccessListener(unused -> callback.accept(true))
                .addOnFailureListener(e -> {
                    Log.e("ProductAdd", "Thêm sản phẩm thất bại", e);
                    callback.accept(false);
                });
    }

    public void updateProduct(Product product, Consumer<Boolean> callback) {
        db.collection("product").document(product.getId())
                .set(product)
                .addOnSuccessListener(unused -> callback.accept(true))
                .addOnFailureListener(e -> {
                    Log.e("ProductUpdate", "Cập nhật sản phẩm thất bại", e);
                    callback.accept(false);
                });
    }

    public void deleteProduct(String productId, Consumer<Boolean> callback) {
        db.collection("product").document(productId)
                .delete()
                .addOnSuccessListener(unused -> callback.accept(true))
                .addOnFailureListener(e -> {
                    Log.e("ProductDelete", "Xóa sản phẩm thất bại", e);
                    callback.accept(false);
                });
    }
}