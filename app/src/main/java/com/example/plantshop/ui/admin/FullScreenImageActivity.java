package com.example.plantshop.ui.admin;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.plantshop.R;

public class FullScreenImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView fullImage = findViewById(R.id.fullImage);
        String imageUrl = getIntent().getStringExtra("imageUrl");

        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(fullImage);
        }
    }
}