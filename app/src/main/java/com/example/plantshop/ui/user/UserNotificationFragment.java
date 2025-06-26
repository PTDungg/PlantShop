package com.example.plantshop.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.plantshop.R;


public class UserNotificationFragment extends Fragment {

    public UserNotificationFragment() {
        // Required empty public constructor
    }

    public static UserNotificationFragment newInstance() {
        return new UserNotificationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_notification, container, false);
    }

}