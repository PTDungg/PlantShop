package com.example.plantshop.ui.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plantshop.R;
import com.example.plantshop.data.Model.User;

import java.io.Serializable;

public class CheckoutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Serializable orderItems = getIntent().getSerializableExtra("order_items");
        int totalPrice = getIntent().getIntExtra("total_price", 0);
        User user = (User) getIntent().getSerializableExtra("user");

        if (savedInstanceState == null) {
            CheckoutFragment fragment = new CheckoutFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("order_items", orderItems);
            bundle.putInt("total_price", totalPrice);
            bundle.putSerializable("user", user);
            fragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.checkout_fragment_container, fragment)
                    .commit();
        }
    }
} 