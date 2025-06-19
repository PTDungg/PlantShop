package com.example.plantshop.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.plantshop.R;
import com.example.plantshop.ui.auth.WelcomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {
    private Bundle userBundle;
    private Button btnSigOut;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);


        AdminViewModel viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        Map<String, String> userMap = new HashMap<>();
        userMap.put("id", getIntent().getStringExtra("id"));
        userMap.put("email", getIntent().getStringExtra("email"));
        userMap.put("name", getIntent().getStringExtra("name"));
        userMap.put("phone", getIntent().getStringExtra("phone"));
        userMap.put("address", getIntent().getStringExtra("address"));
        userMap.put("role", getIntent().getStringExtra("role"));

        viewModel.setUserData(userMap);


        // Thiết lập ViewPager2
        viewPager = findViewById(R.id.view_page2_home_admin);
        if (viewPager != null) {
            viewPager.setAdapter(new ViewPagerAdapter(this));
            viewPager.setUserInputEnabled(true); // Trượt tay

            // Thêm listener để đồng bộ với BottomNavigationView khi trượt
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
                    if (bottomNav != null) {
                        switch (position) {
                            case 0:
                                bottomNav.setSelectedItemId(R.id.nav_home);
                                break;
                            case 1:
                                bottomNav.setSelectedItemId(R.id.nav_add);
                                break;
                            case 2:
                                bottomNav.setSelectedItemId(R.id.nav_noti);
                                break;
                            case 3:
                                bottomNav.setSelectedItemId(R.id.nav_profile);
                                break;
                        }
                    }
                }
            });
        }

        // Thiết lập BottomNavigationView và đồng bộ với ViewPager2
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    if (viewPager != null) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.nav_home) {
                            viewPager.setCurrentItem(0);
                        } else if (itemId == R.id.nav_add) {
                            viewPager.setCurrentItem(1);
                        } else if (itemId == R.id.nav_noti) {
                            viewPager.setCurrentItem(2);
                        } else if (itemId == R.id.nav_profile) {
                            viewPager.setCurrentItem(3);
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

        // Thiết lập Button Đăng xuất
        btnSigOut = findViewById(R.id.btnSigOut);
        if (btnSigOut != null) {
            btnSigOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(AdminActivity.this, WelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            android.util.Log.e("AdminActivity", "Button btnSigOut not found in layout");
        }
    }
}