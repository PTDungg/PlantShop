package com.example.plantshop;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Kiểm tra kết nối Firebase
        if (!FirebaseApp.getApps(this).isEmpty()) {
            Log.d("FirebaseCheck", "Firebase đã kết nối thành công!");
        } else {
            Log.e("FirebaseCheck", "Firebase chưa kết nối!");
        }
    }
}
