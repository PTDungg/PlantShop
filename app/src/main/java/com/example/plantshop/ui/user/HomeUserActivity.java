package com.example.plantshop.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.plantshop.R;
import com.example.plantshop.ui.admin.ProductViewModel;
import com.example.plantshop.ui.auth.WelcomeActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class HomeUserActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private ImageView btnMenu, btnSearch;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;
    private NavController navController;

    private final List<String> tabTitles = List.of("Tất cả", "Sen đá", "Xương rồng", "Cây cảnh", "Hoa");
    private ProductViewModel productViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_user);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        hideSystemUI();
        hideSystemBar();
        initViews();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_user);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
        setupListeners();
        setupViewPagerWithTabs();
        updateNavHeader();

        // Mặc định hiển thị tab Home, ẩn NavHostFragment
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.nav_host_fragment_user).setVisibility(View.GONE);

        if (savedInstanceState == null) {
            productViewModel.loadProducts();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.bottom_nav_home);
        }
    }

    private void hideSystemBar() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {

            windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());

            windowInsetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
        }
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
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupListeners() {
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(HomeUserActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        btnMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Sự kiện xử lý khi click vào các item trong NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_orders) {
                Toast.makeText(this, "Chuyển đến màn hình Đơn hàng", Toast.LENGTH_SHORT).show();
                // Intent intent = new Intent(this, OrdersActivity.class);
                // startActivity(intent);
            } else if (itemId == R.id.nav_messages) {
                Toast.makeText(this, "Chuyển đến màn hình Tin nhắn", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_notifications) {
                Toast.makeText(this, "Chuyển đến màn hình Thông báo", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_change_password) {
                Toast.makeText(this, "Chuyển đến màn hình Đổi mật khẩu", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_profile_info) {
                Toast.makeText(this, "Chuyển đến màn hình Thông tin cá nhân", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_logout) {
                // Xử lý đăng xuất
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeUserActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            // Đóng drawer sau khi một item được chọn
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_nav_home) {
                // Clear back stack về Home
                if (navController != null) {
                    navController.popBackStack(R.id.nav_list_product, false);
                }
                viewPager.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.nav_host_fragment_user).setVisibility(View.GONE);
                return true;
            } else {
                viewPager.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                findViewById(R.id.nav_host_fragment_user).setVisibility(View.VISIBLE);
                if (navController != null) {
                    if (itemId == R.id.bottom_nav_cart) {
                        navController.popBackStack(R.id.nav_cart, false);
                        navController.navigate(R.id.nav_cart);
                        return true;
                    } else if (itemId == R.id.bottom_nav_account) {
                        navController.popBackStack(R.id.nav_profile, false);
                        navController.navigate(R.id.nav_profile);
                        return true;
                    } else if (itemId == R.id.bottom_nav_bell) {
                        Toast.makeText(this, "Chức năng Thông báo đang phát triển", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
            return false;
        });
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
        if (bottomNavigation != null && bottomNavigation.getSelectedItemId() != R.id.bottom_nav_home) {
            // Nếu không ở Home, chuyển về Home
            bottomNavigation.setSelectedItemId(R.id.bottom_nav_home);
        } else {
            // Nếu đã ở Home, thoát app
            super.onBackPressed();
        }
    }
}