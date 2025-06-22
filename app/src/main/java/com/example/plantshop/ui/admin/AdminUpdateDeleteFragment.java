package com.example.plantshop.ui.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.plantshop.R;
import com.example.plantshop.data.Model.Product;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminUpdateDeleteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminUpdateDeleteFragment extends Fragment {
    private ImageView imgProduct;
    private EditText edtId, edtName, edtPrice;
    private Spinner spnCategory, spnStatus;
    private AppCompatButton btnUpdate, btnDelete;

    private Uri selectedImageUri;
    private Product originalProduct;
    private boolean hasChanged = false;
    private ProductViewModel productViewModel;

    private final String[] categories = {"", "sen đá", "xương rồng", "cây cảnh", "hoa"};
    private final String[] statuses = {"Còn hàng", "Hết hàng"};
    private ActivityResultLauncher<Intent> imagePickerLauncher;




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminUpdateDeleteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminUpdateDeleteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminUpdateDeleteFragment newInstance(String param1, String param2) {
        AdminUpdateDeleteFragment fragment = new AdminUpdateDeleteFragment();
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
        View view = inflater.inflate(R.layout.fragment_admin_update_delete, container, false);

        // Ánh xạ view
        imgProduct = view.findViewById(R.id.imgProduct);
        edtId = view.findViewById(R.id.edtProductId);
        edtName = view.findViewById(R.id.edtProductName);
        edtPrice = view.findViewById(R.id.edtProductPrice);
        spnCategory = view.findViewById(R.id.spnCategory);
        spnStatus = view.findViewById(R.id.spnStatus);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnDelete = view.findViewById(R.id.btnDelete);

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(true);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imgProduct.setImageURI(selectedImageUri);
                        checkForChanges();
                    }
                }
        );


        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        productViewModel.getSelectedProduct().observe(getViewLifecycleOwner(), product -> {
            if (product != null) {
                originalProduct = product;
                setupSpinners();
                setupTextWatchers();
                setupImageView();
                displayProduct(originalProduct);
            }
        });

        // Load sản phẩm
        if (originalProduct != null) {
            displayProduct(originalProduct);
        }

        btnUpdate.setOnClickListener(v -> updateProduct());
        btnDelete.setOnClickListener(v -> deleteProduct());
//        btnDelete.setOnClickListener(v -> {
//            requireActivity().onBackPressed();
//        });

        return view;
    }

    private void displayProduct(Product p) {
        edtId.setText(p.getId());
        edtId.setEnabled(false);
        edtName.setText(p.getName());
        edtPrice.setText(String.valueOf(p.getPrice()));

        // Set spinner category
        int catIndex = Arrays.asList(categories).indexOf(p.getCategory());
        spnCategory.setSelection(catIndex >= 0 ? catIndex : 0);

        // Status
        spnStatus.setSelection(p.isAvailable() ? 0 : 1);

        // Load ảnh
        Glide.with(this).load(p.getImageUrl()).into(imgProduct);
    }

    private void setupSpinners() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(categoryAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(statusAdapter);
    }

    private void setupTextWatchers() {
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkForChanges();
            }
        };

        edtName.addTextChangedListener(watcher);
        edtPrice.addTextChangedListener(watcher);
        spnCategory.setOnItemSelectedListener(new SpinnerChangedListener());
        spnStatus.setOnItemSelectedListener(new SpinnerChangedListener());
    }

    private class SpinnerChangedListener implements AdapterView.OnItemSelectedListener {
        @Override public void onNothingSelected(AdapterView<?> parent) {}
        @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            checkForChanges();
        }
    }

    private void checkForChanges() {
        String name = edtName.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        String category = spnCategory.getSelectedItem().toString();
        boolean available = spnStatus.getSelectedItemPosition() == 0;

        boolean changed =
                !name.equals(originalProduct.getName()) ||
                        !priceStr.equals(String.valueOf(originalProduct.getPrice())) ||
                        !(category.equals(originalProduct.getCategory()) || (category.isEmpty() && originalProduct.getCategory() == null)) ||
                        (available != originalProduct.isAvailable()) ||
                        selectedImageUri != null;

        btnUpdate.setEnabled(changed);
    }

    private void setupImageView() {
        imgProduct.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Chọn ảnh")
                    .setItems(new CharSequence[]{"Xem toàn màn hình", "Chọn ảnh từ thiết bị"}, (dialog, which) -> {
                        if (which == 0) {
                            Intent intent = new Intent(getContext(), FullScreenImageActivity.class);
                            intent.putExtra("imageUrl", originalProduct.getImageUrl());
                            startActivity(intent);
                        } else {
                            Intent pick = new Intent(Intent.ACTION_PICK);
                            pick.setType("image/*");
                            imagePickerLauncher.launch(pick); // 🔁 dùng launcher mới
                        }
                    })
                    .show();
        });

    }

    private void updateProduct() {
        String name = edtName.getText().toString().trim();
        int price = Integer.parseInt(edtPrice.getText().toString().trim());
        String category = spnCategory.getSelectedItem().toString().trim();
        boolean available = spnStatus.getSelectedItemPosition() == 0;

        Product updated = new Product();
        updated.setId(originalProduct.getId());
        updated.setName(name);
        updated.setPrice(price);
        updated.setCategory(category.isEmpty() ? "" : category);
        updated.setAvailable(available);
        updated.setImageUrl(originalProduct.getImageUrl()); // tạm thời

        if (selectedImageUri != null) {
            productViewModel.uploadImageAndUpdateProduct(selectedImageUri, updated, success -> {
                if (success) {
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    btnUpdate.setEnabled(false);
                }
            });
        } else {
            productViewModel.updateProduct(updated, success -> {
                if (success) {
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    btnUpdate.setEnabled(false);
                }
            });
        }
    }

    private void deleteProduct() {
        productViewModel.deleteProduct(originalProduct.getId(), success -> {
                if (success) {
                    Toast.makeText(getContext(), "Đã xoá sản phẩm", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                }
        });
    }
}