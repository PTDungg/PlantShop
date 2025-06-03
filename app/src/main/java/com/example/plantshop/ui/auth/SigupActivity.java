package com.example.plantshop.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.plantshop.R;
import com.example.plantshop.ui.user.home.HomeUserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SigupActivity extends AppCompatActivity {
    AuthViewModel authViewModel;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "SigupActivity";
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtName;
    private TextView txtForget;
    private TextView txtLogIn;
    private Button btnSigUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sigup);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtName = findViewById(R.id.edtName);
        txtForget = findViewById(R.id.txtForget);
        txtLogIn = findViewById(R.id.txtLogIn);
        btnSigUp = findViewById(R.id.btnSignUp);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.getUser().observe(this, user -> {
            if (user != null) {
                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HomeUserActivity.class));
                finish();
            }
        });
        authViewModel.getError().observe(this, error -> {
            Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
        });

        btnSigUp.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String name = edtName.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(SigupActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(SigupActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(SigupActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }
            authViewModel.signUp(email,password,name);

        });

        txtLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

//    private void handleSigUp() {
//        String email = edtEmail.getText().toString().trim();
//        String password = edtPassword.getText().toString().trim();
//        String name = edtName.getText().toString().trim();
//
//        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
//            Toast.makeText(SigupActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            Toast.makeText(SigupActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (password.length() < 6) {
//            Toast.makeText(SigupActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(SigupActivity.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "createUserWithEmail:success");
//                            Toast.makeText(SigupActivity.this, "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            if (user != null) {
//                                saveUserToFirestore(user, name);
//                                }
//                            updateUI(user);
//                        } else {
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(SigupActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
//    }
//    private void saveUserToFirestore(FirebaseUser user, String name) {
//        String uid = user.getUid();
//        String email = user.getEmail();
//
//        Map<String, Object> userMap = new HashMap<>();
//        userMap.put("uid", uid);
//        userMap.put("email", email);
//        userMap.put("name", name);
//        userMap.put("role", "user"); // Gán role mặc định là "user"
//        userMap.put("createdAt", FieldValue.serverTimestamp());
//
//        db.collection("users")
//                .document(uid)
//                .set(userMap)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(SigupActivity.this, "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
//                    updateUI(user);
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(SigupActivity.this, "Lưu thông tin thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//    private void updateUI(FirebaseUser user) {
//        if (user != null) {
//            startActivity(new Intent(SigupActivity.this, HomeFragment.class));
//            finish();
//        }
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    view.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
