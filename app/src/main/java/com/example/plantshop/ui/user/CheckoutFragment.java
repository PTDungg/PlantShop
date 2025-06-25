package com.example.plantshop.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantshop.R;
import com.example.plantshop.data.Model.OrderItem;
import com.example.plantshop.data.Model.User;
import com.example.plantshop.data.Utils.FormatUtils;
import com.example.plantshop.data.repository.CheckoutRepository;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CheckoutFragment extends Fragment {
    private CheckoutViewModel viewModel;
    private TextView tvTotalPrice;
    private RecyclerView rvOrderItems;
    private TextView tvUserName, tvUserEmail, tvUserAddress, tvUserPhone, btnEditUserInfo;
    private OrderItemAdapter orderItemAdapter;
    private UserProfileViewModel userProfileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        rvOrderItems = view.findViewById(R.id.rvOrderItems);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvUserAddress = view.findViewById(R.id.tvUserAddress);
        tvUserPhone = view.findViewById(R.id.tvUserPhone);
        btnEditUserInfo = view.findViewById(R.id.btnEditUserInfo);
        ImageView btnBack = view.findViewById(R.id.btnBack);
        MaterialButton btnOrder = view.findViewById(R.id.btnOrder);

        viewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);
        userProfileViewModel = new ViewModelProvider(requireActivity()).get(UserProfileViewModel.class);

        // Đảm bảo luôn có dữ liệu user
        if (userProfileViewModel.getUserLiveData().getValue() == null) {
            userProfileViewModel.loadUserData();
        }

        orderItemAdapter = new OrderItemAdapter(new ArrayList<>());
        rvOrderItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrderItems.setAdapter(orderItemAdapter);

        // Nhận dữ liệu từ arguments và set vào ViewModel
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey("total_price")) {
                viewModel.setTotalPrice(args.getInt("total_price", 0));
            }
            if (args.containsKey("order_items")) {
                List<OrderItem> items = (ArrayList<OrderItem>) args.getSerializable("order_items");
                viewModel.setOrderItems(items);
            }
            if (args.containsKey("user")) {
                User user = (User) args.getSerializable("user");
                viewModel.setUser(user);
            }
        }
        // Observe LiveData và bind ra UI
        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), price -> {
            if (price != null) {
                tvTotalPrice.setText(FormatUtils.formatPrice(price));
            }
        });
        viewModel.getOrderItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                orderItemAdapter.setItems(items);
            }
        });
        // Observe LiveData user từ UserProfileViewModel (toàn cục)
        userProfileViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvUserName.setText(user.getName());
                tvUserEmail.setText(user.getEmail());
                tvUserAddress.setText(user.getAddress());
                tvUserPhone.setText(user.getPhone());
                viewModel.setUser(user);
            } else {
                tvUserName.setText("");
                tvUserEmail.setText("");
                tvUserAddress.setText("");
                tvUserPhone.setText("");
            }
        });

        // Observe CheckoutViewModel
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            btnOrder.setEnabled(!isLoading);
            btnOrder.setText(isLoading ? "Đang xử lý..." : "Đặt Hàng");
        });

        viewModel.getIsOrderSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                // Chuyển sang màn hình thành công
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.checkout_fragment_container, new OrderSuccessFragment())
                        .addToBackStack(null)
                        .commit();
                viewModel.resetOrderSuccess();
            }
        });

        viewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                viewModel.resetMessage();
            }
        });

        // Xử lý đặt hàng
        btnOrder.setOnClickListener(v -> {
            viewModel.placeOrder();
        });

        btnEditUserInfo.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.checkout_fragment_container, new UserProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }
}