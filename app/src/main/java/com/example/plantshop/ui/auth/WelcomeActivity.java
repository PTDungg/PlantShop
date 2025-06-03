package com.example.plantshop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.plantshop.R;
import com.example.plantshop.ui.guest.HomeGuestActivity;
import com.example.plantshop.ui.user.home.HomeUserActivity;
import com.example.plantshop.utils.RoleManager;
import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnLogIn, btnSiUp;
    private TextView txtGuest;
    AuthViewModel authViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);


        btnLogIn = findViewById(R.id.btnLogIn);
        btnSiUp = findViewById(R.id.btnSignUp);
        txtGuest = findViewById(R.id.txtGuest);


        btnLogIn.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
            startActivity(intent);
        });

        btnSiUp.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this,SigupActivity.class);
            startActivity(intent);
        });

        txtGuest.setOnClickListener(v -> {
            authViewModel.loginAsGuest(WelcomeActivity.this);
        });


        authViewModel.getGuestLoginState().observe(this, success -> {
            if (success) {
                RoleManager.setCurrentRole(RoleManager.Role.GUEST);
                Intent intent = new Intent(WelcomeActivity.this, HomeGuestActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        authViewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}