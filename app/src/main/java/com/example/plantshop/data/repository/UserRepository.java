package com.example.plantshop.data.repository;

import com.example.plantshop.utils.RoleManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public UserRepository() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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
        userMap.put("role", RoleManager.Role.USER);
        userMap.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users")
                .document(user.getUid())
                .set(userMap)
                .addOnSuccessListener(aVoid -> callback.onSuccess(user))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface SignUpCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }
}

