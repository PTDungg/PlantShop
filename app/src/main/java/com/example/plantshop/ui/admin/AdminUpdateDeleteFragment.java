package com.example.plantshop.ui.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
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
import com.example.plantshop.data.model.Product;

import java.util.Arrays;

public class AdminUpdateDeleteFragment extends Fragment {
    private ImageView imgProduct;
    private EditText edtId, edtName, edtPrice, edtQuantity;
    private Spinner spnCategory, spnStatus;
    private AppCompatButton btnUpdate, btnDelete;

    private Uri selectedImageUri;
    private Product originalProduct;
    private boolean hasChanged = false;
    private ProductViewModel productViewModel;

    private final String[] categories = {"", "sen đá", "xương rồng", "cây cảnh", "hoa"};
    private final String[] statuses = {"Còn hàng", "Hết hàng"};
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    public AdminUpdateDeleteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        edtQuantity = view.findViewById(R.id.edtQuantity);
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

        return view;
    }

    private void displayProduct(Product p) {
        edtId.setText(p.getId());
        edtId.setEnabled(false);
        edtName.setText(p.getName());
        edtPrice.setText(String.valueOf(p.getPrice()));
        edtQuantity.setText(String.valueOf(p.getQuantity()));

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

        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Khi chọn "Hết hàng" (position = 1), đặt edtQuantity về 0
                // Khi chọn "Còn hàng" (position = 0), đặt edtQuantity thành 1 nếu đang là 0
                if (position == 1) {
                    edtQuantity.setText("0");
                } else if (position == 0 && edtQuantity.getText().toString().trim().equals("0")) {
                    edtQuantity.setText("1");
                }
                checkForChanges();
            }
        });
    }

    private void setupTextWatchers() {
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra edtQuantity để cập nhật spnStatus
                String quantityStr = edtQuantity.getText().toString().trim();
                if (!quantityStr.isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        // Nếu quantity = 0, đặt spnStatus thành "Hết hàng" (position = 1)
                        // Nếu quantity > 0, đặt spnStatus thành "Còn hàng" (position = 0)
                        spnStatus.setSelection(quantity == 0 ? 1 : 0);
                    } catch (NumberFormatException e) {
                        // Không làm gì nếu quantity không phải số hợp lệ
                    }
                }
                checkForChanges();
            }
        };

        edtName.addTextChangedListener(watcher);
        edtPrice.addTextChangedListener(watcher);
        edtQuantity.addTextChangedListener(watcher);
        spnCategory.setOnItemSelectedListener(new SpinnerChangedListener());
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
        String quantityStr = edtQuantity.getText().toString().trim();
        String category = spnCategory.getSelectedItem().toString();
        boolean available = spnStatus.getSelectedItemPosition() == 0;

        boolean changed =
                !name.equals(originalProduct.getName()) ||
                        !priceStr.equals(String.valueOf(originalProduct.getPrice())) ||
                        !quantityStr.equals(String.valueOf(originalProduct.getQuantity())) ||
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
                            imagePickerLauncher.launch(pick);
                        }
                    })
                    .show();
        });
    }

    private void updateProduct() {
        String name = edtName.getText().toString().trim();
        int price = Integer.parseInt(edtPrice.getText().toString().trim());
        int quantity = Integer.parseInt(edtQuantity.getText().toString().trim());
        String category = spnCategory.getSelectedItem().toString().trim();
        boolean available = spnStatus.getSelectedItemPosition() == 0;

        Product updated = new Product();
        updated.setId(originalProduct.getId());
        updated.setName(name);
        updated.setPrice(price);
        updated.setQuantity(quantity);
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