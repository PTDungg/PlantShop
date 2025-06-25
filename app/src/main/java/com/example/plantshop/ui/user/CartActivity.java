package com.example.plantshop.ui.user;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.plantshop.R;

public class CartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.cart_fragment_container, new CartFragment())
                .commit();
        }
    }
}