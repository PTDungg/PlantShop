package com.example.plantshop.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.plantshop.ui.auth.LoginActivity;
import com.example.plantshop.utils.RoleManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class AuthRepository {
    private static final String TAG = "SigupActivity";
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String PREFS_NAME = "MyPrefs";
    private static final String ANONYMOUS_UID_KEY = "anonymous_uid";

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public interface EmailLoginCallback {

        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }
    public interface RoleCallback {

        void onSuccess(String role);
        void onFailure(Exception e);
    }
    public interface UserInfoCallback {
        void onSuccess(Map<String, String> userData);
        void onFailure(Exception e);
    }
    public interface GuestLoginCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void loginWithEmail(String email, String password, EmailLoginCallback callback){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            callback.onSuccess(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            callback.onFailure(task.getException().getMessage());
                        }
                    }
                });

    }

    public void getUserRole(String uid, RoleCallback callback) {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        callback.onSuccess(role != null ? role : "USER");
                    } else {
                        callback.onSuccess("USER"); // Mặc định nếu không có document
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "getUserRole:failure", e);
                    callback.onFailure(e);
                });
    }
    public void getUserInfo(String uid, UserInfoCallback callback) {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, String> userData = new HashMap<>();
                        userData.put("id", documentSnapshot.getString("id"));
                        userData.put("email", documentSnapshot.getString("email"));
                        userData.put("name", documentSnapshot.getString("name"));
                        userData.put("phone", documentSnapshot.getString("phone"));
                        userData.put("address", documentSnapshot.getString("address"));
                        userData.put("role", documentSnapshot.getString("role"));
                        callback.onSuccess(userData);
                    } else {
                        callback.onFailure(new Exception("Không tìm thấy thông tin người dùng"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
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
                guestData.put("uid","guest"+uid);
                guestData.put("role", RoleManager.Role.GUEST.name());
//                guestData.put("created_at", FieldValue.serverTimestamp());

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
