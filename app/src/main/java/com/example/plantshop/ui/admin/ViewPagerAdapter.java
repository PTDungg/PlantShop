package com.example.plantshop.ui.admin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_TABS = 4; // Số tab: Home, Add, Notification, Profile

    public ViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AdminHomeFragment();
            case 1:
                return new AdminAddFragment();
            case 2:
                return new AdminOrderFragment();
            case 3:
                return new AdminProfileFragment();
            default:
                return new AdminHomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }
}