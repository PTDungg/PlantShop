package com.example.plantshop.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.plantshop.utils.RoleManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class AuthRepository {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String PREFS_NAME = "MyPrefs";
    private static final String ANONYMOUS_UID_KEY = "anonymous_uid";

    public interface GuestLoginCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void loginAsGuest(Context context, GuestLoginCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedUid = prefs.getString(ANONYMOUS_UID_KEY, null);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Nếu user hiện tại tồn tại và là anonymous, và UID khớp với UID đã lưu → tiếp tục
        if (currentUser != null && currentUser.isAnonymous()) {
            String currentUid = currentUser.getUid();
            if (savedUid == null || savedUid.equals(currentUid)) {
                // Lưu lại UID nếu chưa lưu
                prefs.edit().putString(ANONYMOUS_UID_KEY, currentUid).apply();
                callback.onSuccess();
                return;
            }
        }

        // Tạo tài khoản anonymous mới
        mAuth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getUser() != null) {
                FirebaseUser newUser = task.getResult().getUser();
                String uid = newUser.getUid();

                // Lưu UID vào SharedPreferences
                prefs.edit().putString(ANONYMOUS_UID_KEY, uid).apply();

                // Lưu thông tin guest lên Firestore
                Map<String, Object> guestData = new HashMap<>();
                guestData.put("role", RoleManager.Role.GUEST.name());
                guestData.put("created_at", FieldValue.serverTimestamp());

                db.collection("users").document(uid)
                        .set(guestData, SetOptions.merge())
                        .addOnSuccessListener(unused -> callback.onSuccess())
                        .addOnFailureListener(callback::onFailure);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }
}
