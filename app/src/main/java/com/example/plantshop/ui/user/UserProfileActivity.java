package com.example.plantshop.ui.user;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.plantshop.R;
import com.example.plantshop.ui.user.UserProfileFragment;

public class UserProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.user_profile_fragment_container, new UserProfileFragment())
                .commit();
        }
    }

} 