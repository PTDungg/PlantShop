package com.example.plantshop.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plantshop.data.repository.AuthRepository;
import com.example.plantshop.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {
    private AuthRepository authRepository;
    public AuthViewModel(){
        authRepository = new AuthRepository();
    }
    private final UserRepository userRepository = new UserRepository();
    private final MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> guestLoginSuccess= new MutableLiveData<>();


    public LiveData<FirebaseUser> getUser() {
        return userLiveData;
    }
    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void signUp(String email, String password, String name) {
        userRepository.signUp(email, password, name, new UserRepository.SignUpCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                userLiveData.setValue(user);
            }

            @Override
            public void onFailure(String errorMessage) {
                errorLiveData.setValue(errorMessage);
            }
        });
    }

    public LiveData<Boolean> getGuestLoginState() {
        return guestLoginSuccess;
    }
    public void loginAsGuest(WelcomeActivity context) {
        authRepository.loginAsGuest(context, new AuthRepository.GuestLoginCallback() {
            @Override
            public void onSuccess() {
                guestLoginSuccess.setValue(true);
            }

            @Override
            public void onFailure(Exception e) {
                guestLoginSuccess.setValue(false);
                errorLiveData.setValue(e.getMessage());
            }
        });
    }

}

