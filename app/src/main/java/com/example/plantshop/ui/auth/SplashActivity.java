package com.example.plantshop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantshop.R;
import com.example.plantshop.ui.user.home.HomeActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        // Kiểm tra kết nối Firebase
        if (!FirebaseApp.getApps(this).isEmpty()) {
            Log.d("FirebaseCheck", "Firebase đã kết nối thành công!");
        } else {
            Log.e("FirebaseCheck", "Firebase chưa kết nối!");
        }

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // Nếu đã đăng nhập, chuyển tới HomeFragment
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
            } else {
                // Nếu chưa đăng nhập, chuyển tới màn hình Welcome
                Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
            finish();
        }, 3000);
//        new Handler().postDelayed(() -> {
//            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
//            startActivity(intent);
//            finish();
//        }, 3000);
    }

}