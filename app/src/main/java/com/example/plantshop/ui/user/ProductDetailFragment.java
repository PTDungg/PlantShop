package com.example.plantshop.ui.user;

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
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.plantshop.R;
import com.example.plantshop.ui.guest.LoginPromptDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.example.plantshop.data.Utils.FormatUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProductDetailFragment extends Fragment {
    private ProductDetailViewModel viewModel;
    private ImageView btnBack, btnCart, ivProductImage;
    private TextView tvTitle, tvProductName, tvPrice, tvStatus, tvQuantity, tvTotalPrice;
    private MaterialButton btnDecrease, btnIncrease;
    private AppCompatButton btnAddToCart;
    private ProgressBar progressBar;
    private String productId;
    private boolean isGuest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
        
        // Kiểm tra trạng thái đăng nhập
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        isGuest = (currentUser == null || currentUser.isAnonymous());
        
        if (getArguments() != null) {
            productId = getArguments().getString("productId");
        }
        if (productId == null) {
            Toast toast = Toast.makeText(requireContext(), "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT);
            toast.show();
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
        
        // Ẩn/hiện các element dựa trên trạng thái đăng nhập
        setupGuestMode();
        
        if (productId != null) {
            viewModel.loadProductById(productId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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

    private void setupGuestMode() {
        if (isGuest) {
            // Ẩn các element liên quan đến mua hàng cho guest
            btnDecrease.setVisibility(View.GONE);
            btnIncrease.setVisibility(View.GONE);
            tvQuantity.setVisibility(View.GONE);
            tvTotalPrice.setVisibility(View.GONE);
            
            // Thay đổi text nút "Thêm vào giỏ hàng" thành "Đăng nhập để mua"
            btnAddToCart.setText("ĐĂNG NHẬP ĐỂ MUA");
            btnAddToCart.setBackgroundTintList(requireContext().getColorStateList(R.color.primary_green));
            
            // Ẩn nút giỏ hàng
            btnCart.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        btnCart.setOnClickListener(v -> {
            if (isGuest) {
                showLoginPrompt();
            } else {
                // Mở CartActivity
                android.content.Intent intent = new android.content.Intent(requireContext(), CartActivity.class);
                startActivity(intent);
            }
        });
        btnDecrease.setOnClickListener(v -> viewModel.decreaseQuantity());
        btnIncrease.setOnClickListener(v -> viewModel.increaseQuantity());
        btnAddToCart.setOnClickListener(v -> {
            if (isGuest) {
                showLoginPrompt();
            } else {
                viewModel.addToCart();
            }
        });
    }

    private void showLoginPrompt() {
        new LoginPromptDialogFragment().show(getChildFragmentManager(), LoginPromptDialogFragment.TAG);
    }

    private void observeViewModel() {
        viewModel.getProduct().observe(getViewLifecycleOwner(), product -> {
            if (product != null) {
                tvTitle.setText(product.getName());
                tvProductName.setText(product.getName());
                tvPrice.setText(formatPrice(product.getPrice()));
                if (product.getQuantity() > 0) {
                    tvStatus.setText("Còn hàng");
                    tvStatus.setTextColor(requireContext().getColor(R.color.primary_green));
                    if (!isGuest) {
                        btnAddToCart.setEnabled(true);
                    }
                } else {
                    tvStatus.setText("Hết hàng");
                    tvStatus.setTextColor(requireContext().getColor(android.R.color.holo_red_dark));
                    btnAddToCart.setEnabled(false);
                }
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
        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), total -> tvTotalPrice.setText(formatPrice(total.intValue())));
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));
        viewModel.getMessage().observe(getViewLifecycleOwner(), msg -> {
            if (!TextUtils.isEmpty(msg)) {
                Toast toast = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT);
                toast.show();
                viewModel.resetMessage();
            }
        });
    }

    private static String formatPrice(int price) {
        return FormatUtils.formatPrice(price);
    }
}