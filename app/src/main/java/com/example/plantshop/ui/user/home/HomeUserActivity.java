package com.example.plantshop.ui.user.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantshop.R;
import com.example.plantshop.data.Model.Product;
import com.example.plantshop.ui.auth.WelcomeActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class HomeUserActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ImageView btnMenu, btnSearch;

    private List<Product> productList;
    private final List<String> tabTitles = List.of("Tất cả", "Sen đá", "Xương rồng", "Cây cảnh", "Hoa");
    private Button btnSigOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_user);

//        btnSigOut = findViewById(R.id.btnSigOut);
//        btnSigOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//
//                Intent intent = new Intent(HomeUserActivity.this, WelcomeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                finish();
//            }
//        });
        initViews();
        setupData();

    }

    private void initViews() {
        tabLayout = findViewById(R.id.tab_layout);
        btnMenu = findViewById(R.id.btnMenu);
        btnSearch = findViewById(R.id.btnSearch);
        btnMenu.setOnClickListener(v -> {

        });
    }

    private void setupData() {
        for (String title : tabTitles) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
    }
}