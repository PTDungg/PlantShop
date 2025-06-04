package com.example.plantshop.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantshop.ui.admin.dashboard.HomeAdminActivity;
import com.example.plantshop.ui.user.home.HomeUserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.plantshop.R;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {
//    private static final String TAG = "SigupActivity";
    private FirebaseAuth mAuth;
    private AuthViewModel authViewModel;
    private EditText edtEmail;
    private EditText edtPassword;
    private TextView txtForget;
    private TextView txtSigUp;
    private Button btnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

         edtEmail = findViewById(R.id.edtEmail);
         edtPassword = findViewById(R.id.edtPassword);
         txtForget = findViewById(R.id.txtForget);
         txtSigUp =  findViewById(R.id.txtSigUp);
         btnLogIn = findViewById(R.id.btnLogIn);

        mAuth = FirebaseAuth.getInstance();

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        // Quan sát userRoleLiveData để chuyển hướng
        authViewModel.getUserRole().observe(this, role -> {
            if (role != null) {
                if (role.equals("ADMIN")) {
                    Intent intent = new Intent(LoginActivity.this, HomeAdminActivity.class);
                    startActivity(intent);
                    finish();
                } else if (role.equals("USER")) {
                    Intent intent = new Intent(LoginActivity.this, HomeUserActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Vai trò không hợp lệ: " + role, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Quan sát lỗi
        authViewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(LoginActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }
                authViewModel.loginWithEmail(email,password);
//                handleLogIn();
            }
        });



        txtSigUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SigupActivity.class);
                startActivity(intent);
            }
        });

    }

//    private void handleLogIn() {
//        String email = edtEmail.getText().toString().trim();
//        String password = edtPassword.getText().toString().trim();
//        if (email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            Toast.makeText(LoginActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (password.length() < 6) {
//            Toast.makeText(LoginActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Toast.makeText(LoginActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
//    }
//
//    private void updateUI(FirebaseUser user) {
//        if (user != null) {
//            Intent intent = new Intent(LoginActivity.this, HomeUserActivity.class);
//            startActivity(intent);
//            finish();
//        } else {
//            Log.d(TAG, "User is null. Stay on SignUp screen.");
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