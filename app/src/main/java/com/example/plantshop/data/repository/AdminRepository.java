package com.example.plantshop.data.repository;

import com.example.plantshop.ui.admin.AdminViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public interface UserInfoCallback {
        void onComplete(Map<String, String> userData);
    }

    public void getCurrentUserInfo(UserInfoCallback callback) {
        String uid = auth.getCurrentUser().getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        Map<String, String> result = new HashMap<>();
                        result.put("id", (String) data.get("id"));
                        result.put("email", (String) data.get("email"));
                        result.put("name", (String) data.get("name"));
                        result.put("phone", (String) data.get("phone"));
                        result.put("address", (String) data.get("address"));
                        result.put("role", (String) data.get("role"));
                        callback.onComplete(result);
                    } else {
                        callback.onComplete(null);
                    }
                })
                .addOnFailureListener(e -> callback.onComplete(null));
    }

    public void updateUserInfo(String name, String phone, AdminViewModel.OnUserUpdateCallback callback) {
        String uid = auth.getCurrentUser().getUid();
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);

        db.collection("users").document(uid).update(updates)
                .addOnSuccessListener(aVoid -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }
}
