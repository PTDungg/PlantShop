package com.example.plantshop.data.repository;

import android.net.Uri;
import android.util.Log;

import com.example.plantshop.data.model.Product;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ProductRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference("product_images");

    public interface SingleProductCallback {
        void onCallback(Product product);
    }

    public interface ProductCallback {
        void onResult(List<Product> products);
    }

    public void getAllProducts(ProductCallback callback) {
        db.collection("product")
                .get(Source.SERVER)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> productList = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Product p = doc.toObject(Product.class);
                        p.setId(doc.getId());
                        productList.add(p);
                    }
                    // Trả về danh sách sản phẩm
                    callback.onResult(productList);
                })
                .addOnFailureListener(e -> {
                    // Trả về danh sách rỗng nếu thất bại
                    callback.onResult(new ArrayList<>());
                });
    }

    public void getProductById(String productId, SingleProductCallback callback) {
        db.collection("product").document(productId)
                .get(Source.SERVER)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null) {
                            product.setId(documentSnapshot.getId());
                        }
                        callback.onCallback(product);
                    } else {
                        callback.onCallback(null);
                    }
                })
                .addOnFailureListener(e -> callback.onCallback(null));
    }

    public void checkProductExists(String productId, Consumer<Boolean> callback) {
        // Kiểm tra xem sản phẩm có tồn tại trong Firestore không
        db.collection("product").document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> callback.accept(documentSnapshot.exists()))
                .addOnFailureListener(e -> callback.accept(false));
    }

    public void uploadImage(Uri imageUri, Consumer<String> callback) {
        // Tạo tên tệp ảnh duy nhất
        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        // Tải ảnh lên Firebase Storage và lấy URL
        fileRef.putFile(imageUri)
                .continueWithTask(task -> fileRef.getDownloadUrl())
                .addOnSuccessListener(uri -> callback.accept(uri.toString()))
                .addOnFailureListener(e -> callback.accept(null));
    }

    public void addProduct(Product product, Consumer<Boolean> callback) {
        // Thêm sản phẩm mới vào Firestore
        db.collection("product").document(product.getId())
                .set(product)
                .addOnSuccessListener(unused -> callback.accept(true))
                .addOnFailureListener(e -> {
                    // Ghi log lỗi nếu thêm thất bại
                    Log.e("ProductAdd", "Thêm sản phẩm thất bại", e);
                    callback.accept(false);
                });
    }

    public void updateProduct(Product product, Consumer<Boolean> callback) {
        // Cập nhật thông tin sản phẩm trong Firestore
        db.collection("product").document(product.getId())
                .set(product)
                .addOnSuccessListener(unused -> callback.accept(true))
                .addOnFailureListener(e -> {
                    // Ghi log lỗi nếu cập nhật thất bại
                    Log.e("ProductUpdate", "Cập nhật sản phẩm thất bại", e);
                    callback.accept(false);
                });
    }

    public void deleteProduct(String productId, Consumer<Boolean> callback) {
        // Truy xuất sản phẩm để lấy URL ảnh
        db.collection("product").document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        String imageUrl = product.getImageUrl();

                        // Xóa tài liệu sản phẩm từ Firestore
                        db.collection("product").document(productId)
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    // Nếu sản phẩm có URL ảnh, tiến hành xóa ảnh từ Storage
                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        try {
                                            // Lấy tham chiếu đến tệp ảnh từ URL
                                            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                            // Xóa ảnh từ Firebase Storage
                                            imageRef.delete()
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Ghi log khi xóa ảnh thành công
                                                        Log.i("ProductDelete", "Xóa ảnh sản phẩm thành công");
                                                        callback.accept(true);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Ghi log lỗi nếu xóa ảnh thất bại
                                                        Log.e("ProductDelete", "Xóa ảnh sản phẩm thất bại", e);
                                                        // Vẫn trả về true vì sản phẩm đã xóa thành công
                                                        callback.accept(true);
                                                    });
                                        } catch (IllegalArgumentException e) {
                                            // Ghi log nếu URL ảnh không hợp lệ
                                            Log.e("ProductDelete", "URL ảnh không hợp lệ: " + imageUrl, e);
                                            // Vẫn trả về true vì sản phẩm đã xóa thành công
                                            callback.accept(true);
                                        }
                                    } else {
                                        // Không có ảnh để xóa, trả về true
                                        callback.accept(true);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Ghi log lỗi nếu xóa sản phẩm thất bại
                                    Log.e("ProductDelete", "Xóa sản phẩm thất bại", e);
                                    callback.accept(false);
                                });
                    } else {
                        // Ghi log nếu sản phẩm không tồn tại
                        Log.e("ProductDelete", "Sản phẩm không tồn tại");
                        callback.accept(false);
                    }
                })
                .addOnFailureListener(e -> {
                    // Ghi log lỗi nếu truy xuất sản phẩm thất bại
                    Log.e("ProductDelete", "Lỗi khi truy xuất sản phẩm", e);
                    callback.accept(false);
                });
    }

    // Phương thức hỗ trợ: Trích xuất đường dẫn tệp từ URL ảnh
    private String extractFilePathFromUrl(String imageUrl) {
        try {
            Uri uri = Uri.parse(imageUrl);
            String path = uri.getPath();
            // Loại bỏ ký tự '/' đầu tiên và phần tiền tố trước đường dẫn tệp
            return path.substring(path.indexOf("product_images/"));
        } catch (Exception e) {
            // Ghi log lỗi nếu phân tích URL thất bại
            Log.e("ProductDelete", "Lỗi khi phân tích URL ảnh: " + imageUrl, e);
            return null;
        }
    }

    // Cập nhật tồn kho sản phẩm sau khi đặt hàng
    public interface ProductUpdateCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public void updateProductQuantity(String productId, int quantityToSubtract, ProductUpdateCallback callback) {
        db.collection("product").document(productId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Long currentQuantity = documentSnapshot.getLong("quantity");
                    if (currentQuantity == null) currentQuantity = 0L;
                    long newQuantity = currentQuantity - quantityToSubtract;
                    if (newQuantity < 0) newQuantity = 0;
                    db.collection("product").document(productId)
                        .update("quantity", newQuantity)
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                } else {
                    callback.onFailure("Sản phẩm không tồn tại");
                }
            })
            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

}