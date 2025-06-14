package com.example.plantshop;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import com.example.plantshop.data.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class UserProfileFragment extends Fragment {

    private ImageView btnBack;
    private EditText etName, etEmail, etAddress, etPhone;
    private AppCompatButton btnSave;
    private String originalName = "", originalAddress = "", originalPhone = "";
    private FirebaseFirestore db;

    public static UserProfileFragment newInstance() {
        return new UserProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        initViews(view);
        setupListeners();
        loadUserData();
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        etName = view.findViewById(R.id.et_name);
        etEmail = view.findViewById(R.id.et_email);
        etAddress = view.findViewById(R.id.et_address);
        etPhone = view.findViewById(R.id.et_phone);
        btnSave = view.findViewById(R.id.btn_save);

        etEmail.setEnabled(false);
        btnSave.setEnabled(false);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkForChanges();
            }
        };

        etName.addTextChangedListener(watcher);
        etAddress.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher);

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Bạn chưa đăng nhập.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        etEmail.setText(user.getEmail() != null ? user.getEmail() : "");

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        User u = doc.toObject(User.class);
                        etName.setText(u != null && u.getName() != null ? u.getName() : "");
                        etAddress.setText(u != null && u.getAddress() != null ? u.getAddress() : "");
                        etPhone.setText(u != null && u.getPhone() != null ? u.getPhone() : "");
                    } else {
                        etName.setText(""); etAddress.setText(""); etPhone.setText("");
                    }
                    saveOriginalValues();
                    checkForChanges();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi tải dữ liệu.", Toast.LENGTH_SHORT).show());
    }

    private void saveOriginalValues() {
        originalName = getText(etName);
        originalAddress = getText(etAddress);
        originalPhone = getText(etPhone);
    }

    private void checkForChanges() {
        boolean changed = !getText(etName).equals(originalName) ||
                !getText(etAddress).equals(originalAddress) ||
                !getText(etPhone).equals(originalPhone);
        btnSave.setEnabled(changed && !getText(etName).isEmpty());
    }

    private void saveProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Bạn chưa đăng nhập.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = getText(etName);
        String address = getText(etAddress);
        String phone = getText(etPhone);

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("email", getText(etEmail));
        data.put("address", address);
        data.put("phone", phone);

        db.collection("users").document(user.getUid())
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Lưu thành công!", Toast.LENGTH_SHORT).show();
                    saveOriginalValues();
                    checkForChanges();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi lưu.", Toast.LENGTH_SHORT).show());
    }

    private String getText(EditText et) {
        return et.getText().toString().trim();
    }
}



