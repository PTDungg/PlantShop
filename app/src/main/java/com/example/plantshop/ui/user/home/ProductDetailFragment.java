package com.example.plantshop.ui.user.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.plantshop.R;
import com.google.android.material.button.MaterialButton;

public class ProductDetailFragment extends Fragment {
    private ProductDetailViewModel viewModel;
    private ImageView btnBack, btnCart, ivProductImage;
    private TextView tvTitle, tvProductName, tvPrice, tvStatus, tvQuantity, tvTotalPrice;
    private MaterialButton btnDecrease, btnIncrease, btnAddToCart;
    private ProgressBar progressBar;
    private String productId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
        if (getArguments() != null) {
            productId = getArguments().getString("productId");
        }
        if (productId == null) {
            Toast.makeText(requireContext(), "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupListeners();
        observeViewModel();
        if (productId != null) {
            viewModel.loadProductById(productId);
        }
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        btnCart = view.findViewById(R.id.btnCart);
        ivProductImage = view.findViewById(R.id.ivProductImage);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvProductName = view.findViewById(R.id.tvProductName);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvQuantity = view.findViewById(R.id.tvQuantity);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnDecrease = view.findViewById(R.id.btnDecrease);
        btnIncrease = view.findViewById(R.id.btnIncrease);
        btnAddToCart = view.findViewById(R.id.btnAddToCart);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        btnCart.setOnClickListener(v -> {
            // Mở CartActivity
            android.content.Intent intent = new android.content.Intent(requireContext(), CartActivity.class);
            startActivity(intent);
        });
        btnDecrease.setOnClickListener(v -> viewModel.decreaseQuantity());
        btnIncrease.setOnClickListener(v -> viewModel.increaseQuantity());
        btnAddToCart.setOnClickListener(v -> viewModel.addToCart());
    }

    private void observeViewModel() {
        viewModel.getProduct().observe(getViewLifecycleOwner(), product -> {
            if (product != null) {
                tvTitle.setText(product.getName());
                tvProductName.setText(product.getName());
                tvPrice.setText(formatPrice(product.getPrice()));
                tvStatus.setText(product.isAvailable() ? "Còn hàng" : "Hết hàng");
                tvStatus.setTextColor(requireContext().getColor(product.isAvailable() ? R.color.primary_green : android.R.color.holo_red_dark));
                if (!TextUtils.isEmpty(product.getImageUrl())) {
                    Glide.with(this)
                            .load(product.getImageUrl())
                            .placeholder(R.drawable.img_placeholder)
                            .error(R.drawable.img_placeholder)
                            .into(ivProductImage);
                } else {
                    ivProductImage.setImageResource(R.drawable.img_placeholder);
                }
            }
        });
        viewModel.getQuantity().observe(getViewLifecycleOwner(), quantity -> tvQuantity.setText(String.valueOf(quantity)));
        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), total -> tvTotalPrice.setText(formatPrice(total)));
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));
        viewModel.getMessage().observe(getViewLifecycleOwner(), msg -> {
            if (!TextUtils.isEmpty(msg)) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                viewModel.resetMessage();
            }
        });
    }

    private static String formatPrice(Integer price) {
        if (price == null) return "";
        return String.format("%,.d VND", price);
    }
}