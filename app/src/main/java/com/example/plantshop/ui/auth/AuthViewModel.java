package com.example.plantshop.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plantshop.data.repository.AuthRepository;
import com.example.plantshop.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthViewModel extends ViewModel {
    private AuthRepository authRepository;
    private final UserRepository userRepository = new UserRepository();
    private final MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> userRoleLiveData = new MutableLiveData<>(); // LiveData cho vai trò
    private final MutableLiveData<Boolean> guestLoginSuccess= new MutableLiveData<>();
    private final MutableLiveData<Boolean> isUserLoggedIn = new MutableLiveData<>(); // Thêm LiveData cho trạng thái đăng nhập


    public AuthViewModel(){
        authRepository = new AuthRepository();
    }
    public LiveData<FirebaseUser> getUser() {
        return userLiveData;
    }
    public LiveData<String> getError() {
        return errorLiveData;
    }
    public LiveData<Boolean> getGuestLoginState() {
        return guestLoginSuccess;
    }
    public LiveData<String> getUserRole() {
        return userRoleLiveData;
    }
    public LiveData<Boolean> getIsUserLoggedIn() {
        return isUserLoggedIn;
    }

    public void checkUserStatus() {
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser != null) {
            isUserLoggedIn.setValue(true);
            authRepository.getUserRole(currentUser.getUid(), new AuthRepository.RoleCallback() {
                @Override
                public void onSuccess(String role) {
                    userRoleLiveData.setValue(role);
                }

                @Override
                public void onFailure(Exception e) {
                    errorLiveData.setValue("Lỗi khi lấy vai trò: " + e.getMessage());
                    userRoleLiveData.setValue("USER"); // Mặc định nếu lỗi
                }
            });
        } else {
            isUserLoggedIn.setValue(false);
        }
    }
    public void loginWithEmail(String email, String password){
        authRepository.loginWithEmail(email,password, new AuthRepository.EmailLoginCallback(){
            @Override
            public void onSuccess(FirebaseUser user) {
                if (user != null) {
                    userLiveData.setValue(user);
                    // Lấy vai trò từ repository
                    authRepository.getUserRole(user.getUid(), new AuthRepository.RoleCallback() {
                        @Override
                        public void onSuccess(String role) {
                            userRoleLiveData.setValue(role);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            errorLiveData.setValue("Lỗi khi lấy vai trò: " + e.getMessage());
                            userRoleLiveData.setValue("USER"); // Mặc định nếu lỗi
                        }
                    });
                } else {
                    errorLiveData.setValue("Không tìm thấy người dùng sau khi đăng nhập");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                errorLiveData.setValue(errorMessage);
            }
        });
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
}

