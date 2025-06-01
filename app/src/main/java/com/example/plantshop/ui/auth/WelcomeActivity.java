package com.example.plantshop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
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