package com.example.plantshop.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantshop.R;
import com.example.plantshop.data.model.Product;
import com.example.plantshop.ui.admin.ProductAdapter;
import com.example.plantshop.ui.admin.ProductViewModel;
import com.example.plantshop.ui.guest.LoginPromptDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ListProductActivity extends AppCompatActivity {

    private RecyclerView rvProductList;
    private TextView tvSearch, tvNoResults;
    private ImageView btnBack, btnCart;
    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;
    private String searchQuery;
    private boolean isGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        isGuest = (currentUser == null || currentUser.isAnonymous());
        searchQuery = getIntent().getStringExtra("SEARCH_QUERY");
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            Toast.makeText(this, "Từ khóa tìm kiếm không hợp lệ.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupHeader();
        setupRecyclerView();

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        observeProducts();
        productViewModel.loadProducts();
    }

    private void showLoginPrompt() {
        new LoginPromptDialogFragment().show(getSupportFragmentManager(), LoginPromptDialogFragment.TAG);
    }

    private void initViews() {

        tvSearch = findViewById(R.id.tvSearch);
        btnBack = findViewById(R.id.btnBack);
        btnCart = findViewById(R.id.btnCart);
        rvProductList = findViewById(R.id.rvProductList);
        tvNoResults = findViewById(R.id.tvNoResults);
    }

    private void setupHeader() {
        tvSearch.setText(searchQuery);

        btnBack.setOnClickListener(v -> onBackPressed());


        btnCart.setOnClickListener(v -> {
            if (isGuest) {
                showLoginPrompt(); // Nếu là khách, hiển thị popup
            } else {
                Toast.makeText(this, "Chức năng chưa được cài đặt", Toast.LENGTH_SHORT).show();
                // todo
            }
        });
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(new ArrayList<>());
        productAdapter.setOnItemClickListener(product -> {
            Intent intent = new Intent(ListProductActivity.this, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            startActivity(intent);
        });
        rvProductList.setAdapter(productAdapter);
    }

    private void observeProducts() {
        productViewModel.getAllProducts().observe(this, allProducts -> {
            if (allProducts == null) return;

            String unaccentedQuery = unAccent(searchQuery);
            List<Product> filteredList = new ArrayList<>();

            for (Product product : allProducts) {
                if (product.getName() != null && unAccent(product.getName()).contains(unaccentedQuery)) {
                    filteredList.add(product);
                }
            }

            productAdapter.setProductList(filteredList);

            if (filteredList.isEmpty()) {
                tvNoResults.setVisibility(View.VISIBLE);
                rvProductList.setVisibility(View.GONE);
            } else {
                tvNoResults.setVisibility(View.GONE);
                rvProductList.setVisibility(View.VISIBLE);
            }
        });
    }

    public static String unAccent(String s) {
        if (s == null) return "";
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").toLowerCase();
    }
}