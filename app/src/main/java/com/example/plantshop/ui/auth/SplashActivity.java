package com.example.plantshop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.plantshop.R;
import com.example.plantshop.ui.admin.AdminActivity;
import com.example.plantshop.ui.guest.HomeGuestActivity;
import com.example.plantshop.ui.user.home.HomeUserActivity;

public class SplashActivity extends AppCompatActivity {
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            viewModel.checkUserStatus();

            // Quan sát trạng thái đăng nhập
            viewModel.getIsUserLoggedIn().observe(this, isLoggedIn -> {
                if (isLoggedIn == null) return;

                if (!isLoggedIn) {
                    // Chưa đăng nhập, chuyển đến WelcomeActivity
                    startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                    finish();
                    return;
                }

                // Đã đăng nhập, kiểm tra vai trò
                viewModel.getUserRole().observe(this, role -> {
                    Intent intent;
                    if (role != null) {
                        if (role.equals("ADMIN")) {
                            intent = new Intent(SplashActivity.this, AdminActivity.class);
                        } else if (role.equals("USER")) {
                            intent = new Intent(SplashActivity.this, HomeUserActivity.class);
                        } else if (role.equals("GUEST")) {
                            intent = new Intent(SplashActivity.this, HomeGuestActivity.class);
                        } else {
                            // Vai trò không hợp lệ
                            intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                            Toast.makeText(SplashActivity.this, "Vai trò không hợp lệ: " + role, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Không lấy được vai trò
                        intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    }
                    startActivity(intent);
                    finish();
                });

                // Quan sát lỗi
                viewModel.getError().observe(this, error -> {
                    if (error != null) {
                        Toast.makeText(SplashActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }, 3000); //
    }
}