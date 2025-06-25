package com.example.plantshop.ui.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plantshop.data.model.User;
import com.example.plantshop.data.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

public class UserProfileViewModel extends ViewModel {

    private final UserRepository userRepository = new UserRepository();

    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSaved = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<User> getUserLiveData() { return userLiveData; }
    public LiveData<String> getMessage() { return message; }
    public LiveData<Boolean> getIsSaved() { return isSaved; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadUserData() {
        isLoading.setValue(true);
        userRepository.getUserData(new UserRepository.LoadUserCallback() {
            @Override
            public void onSuccess(User user) {
                userLiveData.setValue(user);
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                message.setValue(errorMessage);
                isLoading.setValue(false);
            }
        });
    }

    public void saveUserData(String name, String address, String phone) {
        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            message.setValue("Vui lòng điền đầy đủ thông tin.");
            return;
        }

        isLoading.setValue(true);
        User currentUser = userLiveData.getValue();
        if (currentUser == null) {
            message.setValue("Lỗi: Không có dữ liệu người dùng để lưu.");
            isLoading.setValue(false);
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("address", address);
        updates.put("phone", phone);

        userRepository.updateUserData(updates, new UserRepository.UpdateUserCallback() {
            @Override
            public void onSuccess() {
                message.setValue("Lưu thành công!");
                isSaved.setValue(true);
                isLoading.setValue(false);
                currentUser.setName(name);
                currentUser.setAddress(address);
                currentUser.setPhone(phone);
                userLiveData.setValue(currentUser);
            }

            @Override
            public void onFailure(String errorMessage) {
                message.setValue("Lỗi khi lưu: " + errorMessage);
                isLoading.setValue(false);
            }
        });
    }

    public void resetIsSaved() {
        isSaved.setValue(false);
    }
}