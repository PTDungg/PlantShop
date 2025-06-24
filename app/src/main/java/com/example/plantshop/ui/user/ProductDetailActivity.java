package com.example.plantshop.ui.user;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantshop.R;

public class ProductDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        String productId = getIntent().getStringExtra("PRODUCT_ID");
        if (productId == null) {
            finish();
            return;
        }
        if (savedInstanceState == null) {
            ProductDetailFragment fragment = new ProductDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("productId", productId);
            fragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }
}