package com.example.plantshop.ui.user.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.plantshop.data.Model.User;
import com.example.plantshop.R;

public class UserProfileFragment extends Fragment {

    private UserProfileViewModel viewModel;
    private EditText etName, etEmail, etAddress, etPhone;
    private AppCompatButton btnSave;
    private ProgressBar progressBar;

    private String originalName = "", originalAddress = "", originalPhone = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(UserProfileViewModel.class);
        initViews(view);
        setupObservers();
        setupListeners();
        viewModel.loadUserData();

        // Xử lý nút back trên app bar
        View btnBack = view.findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.et_name);
        etEmail = view.findViewById(R.id.et_email);
        etAddress = view.findViewById(R.id.et_address);
        etPhone = view.findViewById(R.id.et_phone);
        btnSave = view.findViewById(R.id.btn_save);
        progressBar = view.findViewById(R.id.progressBar);

        etEmail.setEnabled(false);
        btnSave.setEnabled(false);
        progressBar.setVisibility(View.GONE);
    }

    private void setupObservers() {
        viewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                return;
            }
            etName.setText(user.getName());
            etEmail.setText(user.getEmail());
            etAddress.setText(user.getAddress());
            etPhone.setText(user.getPhone());

            originalName = user.getName();
            originalAddress = user.getAddress();
            originalPhone = user.getPhone();
            checkForChanges();
        });

        viewModel.getMessage().observe(getViewLifecycleOwner(), msg ->
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());

        viewModel.getIsSaved().observe(getViewLifecycleOwner(), saved -> {
            if (saved != null && saved) {
                originalName = getText(etName);
                originalAddress = getText(etAddress);
                originalPhone = getText(etPhone);
                checkForChanges();
                viewModel.resetIsSaved();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                btnSave.setEnabled(false);
                setInputsEnabled(false);
            } else {
                progressBar.setVisibility(View.GONE);
                setInputsEnabled(true);
                checkForChanges();
            }
        });
    }

    private void setupListeners() {
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

        btnSave.setOnClickListener(v -> {
            viewModel.saveUserData(
                    getText(etName),
                    getText(etAddress),
                    getText(etPhone)
            );
        });
    }

    private void checkForChanges() {
        boolean changed = !getText(etName).equals(originalName)
                || !getText(etAddress).equals(originalAddress)
                || !getText(etPhone).equals(originalPhone);

        btnSave.setEnabled(changed && !getText(etName).isEmpty());
    }

    private String getText(EditText et) {
        return et.getText().toString().trim();
    }

    private void setInputsEnabled(boolean enabled) {
        etName.setEnabled(enabled);
        etAddress.setEnabled(enabled);
        etPhone.setEnabled(enabled);
    }
}