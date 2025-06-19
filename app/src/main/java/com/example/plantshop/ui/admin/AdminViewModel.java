package com.example.plantshop.ui.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plantshop.data.repository.AdminRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class AdminViewModel extends ViewModel {
    private final AdminRepository adminRepository = new AdminRepository();
    private final MutableLiveData<Map<String, String>> userData = new MutableLiveData<>();

    public void setUserData(Map<String, String> data) {
        userData.setValue(data);
    }

    public LiveData<Map<String, String>> getUserData() {
        return userData;
    }

    public void loadUserData() {
        adminRepository.getCurrentUserInfo(data -> {
            if (data != null) {
                userData.setValue(data);
            }
        });
    }

    public void updateUserInfo(String name, String phone, OnUserUpdateCallback callback) {
        adminRepository.updateUserInfo(name, phone, callback);
    }

    public interface OnUserUpdateCallback {
        void onComplete(boolean success);
    }

}
