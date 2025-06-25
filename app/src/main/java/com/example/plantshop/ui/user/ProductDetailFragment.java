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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
    private TextView tvLabelQuantity, tvLabelTotal;
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

        // Lấy productId từ arguments (Bundle)
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
        setupGuestMode();
        if (productId != null) {
            viewModel.loadProductById(productId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideHeaderAndBottomNav();
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
        tvLabelQuantity = view.findViewById(R.id.tvLabelQuantity);
        tvLabelTotal = view.findViewById(R.id.tvTotalLabel);
    }

    private void setupGuestMode() {
        if (isGuest) {
            btnDecrease.setVisibility(View.GONE);
            btnIncrease.setVisibility(View.GONE);
            tvQuantity.setVisibility(View.GONE);
            tvTotalPrice.setVisibility(View.GONE);
            btnAddToCart.setText("ĐĂNG NHẬP ĐỂ MUA");
            btnAddToCart.setEnabled(true);
            btnCart.setVisibility(View.GONE);
            if (tvLabelQuantity != null) tvLabelQuantity.setVisibility(View.GONE);
            if (tvLabelTotal != null) tvLabelTotal.setVisibility(View.GONE);
        } else {
            btnDecrease.setVisibility(View.VISIBLE);
            btnIncrease.setVisibility(View.VISIBLE);
            tvQuantity.setVisibility(View.VISIBLE);
            tvTotalPrice.setVisibility(View.VISIBLE);
            btnAddToCart.setText("THÊM VÀO GIỎ HÀNG");
            btnAddToCart.setEnabled(true);
            btnCart.setVisibility(View.VISIBLE);
            if (tvLabelQuantity != null) tvLabelQuantity.setVisibility(View.VISIBLE);
            if (tvLabelTotal != null) tvLabelTotal.setVisibility(View.VISIBLE);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        btnCart.setOnClickListener(v -> {
            if (isGuest) {
                showLoginPrompt();
            } else {
                // Chuyển sang Fragment Cart bằng NavController
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_user);
                navController.navigate(R.id.nav_cart);
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
                if (product.isAvailable() && product.getQuantity() > 0) {
                    tvStatus.setText("Còn hàng");
                    tvStatus.setTextColor(requireContext().getColor(R.color.primary_green));
                    if (isGuest) {
                        btnAddToCart.setText("ĐĂNG NHẬP ĐỂ MUA");
                        btnAddToCart.setEnabled(true);
                        btnDecrease.setVisibility(View.GONE);
                        btnIncrease.setVisibility(View.GONE);
                        tvQuantity.setVisibility(View.GONE);
                        tvTotalPrice.setVisibility(View.GONE);
                        btnCart.setVisibility(View.GONE);
                    } else {
                        btnAddToCart.setText("THÊM VÀO GIỎ HÀNG");
                        btnAddToCart.setEnabled(true);
                        btnDecrease.setVisibility(View.VISIBLE);
                        btnIncrease.setVisibility(View.VISIBLE);
                        tvQuantity.setVisibility(View.VISIBLE);
                        tvTotalPrice.setVisibility(View.VISIBLE);
                        btnCart.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvStatus.setText("Hết hàng");
                    tvStatus.setTextColor(requireContext().getColor(android.R.color.holo_red_dark));
                    btnAddToCart.setText("HẾT HÀNG");
                    btnAddToCart.setEnabled(false);
                    btnDecrease.setVisibility(View.GONE);
                    btnIncrease.setVisibility(View.GONE);
                    tvQuantity.setVisibility(View.GONE);
                    tvTotalPrice.setVisibility(View.GONE);
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

    private void hideHeaderAndBottomNav() {
        View appBar = requireActivity().findViewById(R.id.app_bar_layout);
        View bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (appBar != null) appBar.setVisibility(View.GONE);
        if (bottomNav != null) bottomNav.setVisibility(View.GONE);
    }
}