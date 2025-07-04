package com.example.plantshop.ui.admin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.plantshop.R;
import com.example.plantshop.data.Model.Product;
import com.example.plantshop.data.repository.ProductRepository;

public class AdminAddFragment extends Fragment {
    private EditText edtId, edtName, edtPrice, edtQuantity;
    private Spinner spnCategory, spnStatus;
    private ImageView imgProduct;
    private Button btnAdd;
    private Uri selectedImageUri;
    private ProductViewModel productViewModel;
    private ProductRepository productRepository;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private final String[] categories = {"", "sen đá", "xương rồng", "cây cảnh", "hoa"};
    private final String[] statuses = {"Còn hàng", "Hết hàng"};

    public AdminAddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_add, container, false);

        edtId = view.findViewById(R.id.edtProductId);
        edtName = view.findViewById(R.id.edtProductName);
        edtPrice = view.findViewById(R.id.edtProductPrice);
        edtQuantity = view.findViewById(R.id.edtQuantity);
        spnCategory = view.findViewById(R.id.spnCategory);
        spnStatus = view.findViewById(R.id.spnStatus);
        imgProduct = view.findViewById(R.id.imgProduct);
        btnAdd = view.findViewById(R.id.btnAdd);

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        productRepository = new ProductRepository();

        setupSpinners();
        setupImagePicker();
        setUpTextWatchers();

        imgProduct.setOnClickListener(v -> pickImage());

        btnAdd.setEnabled(false);
        btnAdd.setOnClickListener(v -> {
            if (validateInputs()) {
                checkIdExistsAndAdd();
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
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
                updateAddButtonState();
            }
        });
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imgProduct.setImageURI(selectedImageUri);
                    }
                }
        );
    }

    private void setUpTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                updateAddButtonState();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        edtId.addTextChangedListener(textWatcher);
        edtName.addTextChangedListener(textWatcher);
        edtPrice.addTextChangedListener(textWatcher);
        edtQuantity.addTextChangedListener(textWatcher);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private boolean validateInputs() {
        return selectedImageUri != null &&
                !TextUtils.isEmpty(edtId.getText().toString().trim()) &&
                !TextUtils.isEmpty(edtName.getText().toString().trim()) &&
                !TextUtils.isEmpty(edtPrice.getText().toString().trim()) &&
                !TextUtils.isEmpty(edtQuantity.getText().toString().trim());
    }

    private void updateAddButtonState() {
        boolean isValid = validateInputs();
        btnAdd.setEnabled(isValid);
    }

    private void checkIdExistsAndAdd() {
        String id = edtId.getText().toString().trim();
        productViewModel.checkProductExists(id, exists -> {
            if (exists) {
                Toast.makeText(getContext(), "ID đã tồn tại, vui lòng chọn ID khác", Toast.LENGTH_SHORT).show();
            } else {
                addProduct();
            }
        });
    }

    private void addProduct() {
        String id = edtId.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        int price = Integer.parseInt(edtPrice.getText().toString().trim());
        int quantity = Integer.parseInt(edtQuantity.getText().toString().trim());
        String category = spnCategory.getSelectedItem().toString().trim();
        boolean available = spnStatus.getSelectedItemPosition() == 0;

        productViewModel.uploadImage(selectedImageUri, imageUrl -> {
            if (imageUrl != null) {
                Product product = new Product();
                product.setId(id);
                product.setName(name);
                product.setPrice(price);
                product.setQuantity(quantity);
                product.setCategory(category.isEmpty() ? "" : category);
                product.setAvailable(available);
                product.setImageUrl(imageUrl);
                productViewModel.addProduct(product, isSuccess -> {
                    if (isSuccess) {
                        showSuccessDialog();
                        clearInputs();
                    } else {
                        Toast.makeText(getContext(), "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Lỗi tải ảnh lên", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearInputs() {
        edtId.setText("");
        edtName.setText("");
        edtPrice.setText("");
        edtQuantity.setText("");
        spnCategory.setSelection(0);
        spnStatus.setSelection(0);
        imgProduct.setImageResource(R.drawable.img_placeholder1);
        selectedImageUri = null;
    }

    private void showSuccessDialog() {
        SuccessDialogFragment dialog = new SuccessDialogFragment();
        dialog.show(getParentFragmentManager(), "success_dialog");
    }
}