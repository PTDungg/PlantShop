package com.example.plantshop.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plantshop.data.repository.AuthRepository;
import com.example.plantshop.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AuthViewModel extends ViewModel {
    private AuthRepository authRepository;
    private final UserRepository userRepository = new UserRepository();
    private final MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> userRoleLiveData = new MutableLiveData<>(); // LiveData cho vai trò
    private final MutableLiveData<Map<String, String>> userInfoLiveData = new MutableLiveData<>();
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
    public LiveData<Map<String, String>> getUserInfo() {
        return userInfoLiveData;
    }


    public void checkUserStatus() {
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser != null) {
            isUserLoggedIn.setValue(true);
            String uid = currentUser.getUid();
            authRepository.getUserInfo(uid, new AuthRepository.UserInfoCallback() {
                @Override
                public void onSuccess(Map<String, String> userData) {
                    if (userData != null) {
                        userInfoLiveData.setValue(userData); // LiveData mới để trả về toàn bộ thông tin user
                        String role = (String) userData.get("role");
                        userRoleLiveData.setValue(role != null ? role : "USER");
                    } else {
                        errorLiveData.setValue("Không tìm thấy thông tin người dùng");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    errorLiveData.setValue("Lỗi khi lấy thông tin người dùng: " + e.getMessage());
                    userRoleLiveData.setValue("USER");
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
                    authRepository.getUserInfo(user.getUid(), new AuthRepository.UserInfoCallback() {
                        @Override
                        public void onSuccess(Map<String, String> userData) {
                            userInfoLiveData.setValue(userData);
                            userRoleLiveData.setValue(userData.get("role"));
                        }

                        @Override
                        public void onFailure(Exception e) {
                            errorLiveData.setValue("Lỗi khi lấy thông tin người dùng: " + e.getMessage());
                            userRoleLiveData.setValue("USER");
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

