package com.example.plantshop.ui.auth;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import com.example.plantshop.R;
import com.example.plantshop.utils.RoleManager;
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

//        //
//        // Lấy NavHostFragment từ layout
//        NavHostFragment navHostFragment = (NavHostFragment)
//                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
//
//        if (navHostFragment == null) {
//            Log.e("MainActivity", "Không tìm thấy NavHostFragment!");
//            return;
//        }
//
//        NavController navController = navHostFragment.getNavController();
//
//        // Xác định NavGraph phù hợp với vai trò người dùng
//        RoleManager.Role role = RoleManager.getCurrentRole();
//        int navGraphResId;
//
//        if (role == null) {
//            Log.w("RoleManager", "Vai trò chưa được thiết lập. Mặc định là Guest.");
//            navGraphResId = R.navigation.nav_graph_guest;
//        } else {
//            switch (role) {
//                case ADMIN:
//                    navGraphResId = R.navigation.Admin  ;
//                    break;
//                case USER:
//                    navGraphResId = R.navigation.nav_graph_user;
//                    break;
//                case GUEST:
//                default:
//                    navGraphResId = R.navigation.nav_graph_guest;
//                    break;
//            }
//        }
//
//        // Thiết lập NavGraph theo vai trò
//        NavGraph navGraph = navController.getNavInflater().inflate(navGraphResId);
//        navController.setGraph(navGraph);
    }

}
