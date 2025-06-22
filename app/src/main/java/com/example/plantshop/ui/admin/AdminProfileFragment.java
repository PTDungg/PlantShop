package com.example.plantshop.ui.admin;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.example.plantshop.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminProfileFragment extends Fragment {

    private EditText edtID, edtEmail, edtPhone, edtName;
    private AppCompatButton btnSave;
    AdminViewModel adminViewModel;
    private String originalName = "", originalPhone = "";



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminProfileFragment newInstance(String param1, String param2) {
        AdminProfileFragment fragment = new AdminProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);

        edtID = view.findViewById(R.id.edtID);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtName = view.findViewById(R.id.edtName);
        edtPhone = view.findViewById(R.id.edtPhone);
        btnSave = view.findViewById(R.id.btnSave);

        edtName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                moveFocusTo(edtPhone);
                return true;
            }
            return false;
        });

        adminViewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
        adminViewModel.getUserData().observe(getViewLifecycleOwner(), userData -> {
            if (userData != null) {
                edtID.setText(userData.get("id"));
                edtEmail.setText(userData.get("email"));

                originalName = userData.get("name");
                originalPhone = userData.get("phone");

                edtName.setText(originalName);
                edtPhone.setText(originalPhone);

                btnSave.setEnabled(false);
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = edtName.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();

                boolean hasChanged = !name.equals(originalName) || !phone.equals(originalPhone);
                btnSave.setEnabled(hasChanged);
            }
        };

        edtName.addTextChangedListener(textWatcher);
        edtPhone.addTextChangedListener(textWatcher);


        btnSave.setOnClickListener(v -> {
            String updatedName = edtName.getText().toString().trim();
            String updatedPhone = edtPhone.getText().toString().trim();

            adminViewModel.updateUserInfo(updatedName, updatedPhone, success -> {
                if (success) {
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    originalName = updatedName;
                    originalPhone = updatedPhone;
                    btnSave.setEnabled(false);
                } else {
                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }

    private void moveFocusTo(EditText targetEditText) {
        targetEditText.requestFocus();
        targetEditText.setSelection(targetEditText.getText().length());
    }


}