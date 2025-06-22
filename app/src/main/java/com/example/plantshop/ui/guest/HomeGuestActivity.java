package com.example.plantshop.ui.guest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.plantshop.R;
import com.example.plantshop.ui.admin.ProductViewModel;
import com.example.plantshop.ui.auth.WelcomeActivity;
import com.example.plantshop.ui.user.home.HomeUserActivity;
import com.example.plantshop.ui.user.home.ListProductFragment;
import com.example.plantshop.ui.user.home.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class HomeGuestActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private ImageView btnMenu, btnSearch;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;

    private final List<String> tabTitles = List.of("Tất cả", "Sen đá", "Xương rồng", "Cây cảnh", "Hoa");
    private ProductViewModel productViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_guest);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        hideSystemUI();
        hideSystemBar();
        initViews();
        setupListeners();
        setupViewPagerWithTabs();
        updateNavHeader();

        if (savedInstanceState == null) {
            productViewModel.loadProducts();
        }
    }

    private void hideSystemBar() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());

        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
    }


    private void hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // Ẩn cả status bar và navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
    }


    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        tabLayout = findViewById(R.id.tab_layout);
        btnMenu = findViewById(R.id.btnMenu);
        btnSearch = findViewById(R.id.btnSearch);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottom_navigation); // Ánh xạ BottomNavigationView

    }

    private void setupListeners() {
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(HomeGuestActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        btnMenu.setOnClickListener(v -> {
            showPopupLogin();
        });
        bottomNavigation.setOnItemSelectedListener(item -> {
            // Bất kể người dùng chọn item nào, cũng hiển thị popup
            showPopupLogin();
            return false; // Trả về false để item không được chọn (không đổi màu)
        });

    }

    private void showPopupLogin() {
        new LoginPromptDialogFragment().show(getSupportFragmentManager(), LoginPromptDialogFragment.TAG);
    }

    private void setupViewPagerWithTabs() {
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                String productType = tabTitles.get(position);
                return ListProductFragment.newInstance(productType);
            }

            @Override
            public int getItemCount() {
                return tabTitles.size();
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles.get(position));
        }).attach();
    }

    private void updateNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.user_name);
        TextView userEmail = headerView.findViewById(R.id.user_email);
        ShapeableImageView profileImage = headerView.findViewById(R.id.profile_image);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                userName.setText(currentUser.getDisplayName());
            } else {
                userName.setText("Người dùng mới");
            }
            userEmail.setText(currentUser.getEmail());

            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.ic_default_avatar)
                        .into(profileImage);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Nếu drawer đang mở, nhấn back sẽ đóng drawer lại thay vì thoát Activity
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}