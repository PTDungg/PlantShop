package com.example.plantshop.data.repository;

import com.example.plantshop.data.model.User;
import com.example.plantshop.utils.RoleManager; // Đảm bảo RoleManager tồn tại và đúng package
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions; // Import SetOptions

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    public UserRepository() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public interface SignUpCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }

    public interface LoadUserCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }

    public interface UpdateUserCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public void signUp(String email, String password, String name, SignUpCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user, name, callback);
                        } else {
                            callback.onFailure("Người dùng không tồn tại sau khi đăng ký.");
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    private void saveUserToFirestore(FirebaseUser user, String name, SignUpCallback callback) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", user.getUid());
        userMap.put("email", user.getEmail());
        userMap.put("name", name);
        userMap.put("role", RoleManager.Role.USER.name());
        userMap.put("phone", "");
        userMap.put("address", "");

        db.collection("users")
                .document(user.getUid())
                .set(userMap)
                .addOnSuccessListener(aVoid -> callback.onSuccess(user))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getUserData(LoadUserCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("Người dùng chưa đăng nhập.");
            return;
        }

        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            user.setUid(currentUser.getUid());
                            user.setEmail(currentUser.getEmail());
                            callback.onSuccess(user);
                        } else {
                            callback.onFailure("Không thể chuyển đổi dữ liệu người dùng.");
                        }
                    } else {
                        callback.onFailure("Không tìm thấy thông tin người dùng.");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updateUserData(Map<String, Object> data, UpdateUserCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("Người dùng chưa đăng nhập.");
            return;
        }

        db.collection("users").document(currentUser.getUid())
                .set(data, SetOptions.merge())
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}