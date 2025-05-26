package com.example.plantshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.plantshop.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {

    private Button btnLogIn, btnSiUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        btnLogIn = findViewById(R.id.btnLogIn);
        btnSiUp = findViewById(R.id.btnSignUp);

        btnLogIn.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
            startActivity(intent);
        });

        btnSiUp.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this,SigupActivity.class);
            startActivity(intent);
        });

    }
}