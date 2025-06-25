package com.example.plantshop.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantshop.R;
import com.example.plantshop.data.Model.OrderItem;
import com.example.plantshop.data.Utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CartFragment extends Fragment implements CartAdapter.CartItemListener {

    private CartViewModel viewModel;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView tvTotalPrice, tvEmptyCart;
    private AppCompatButton btnCheckout;
    private TextView btnClearCart;
    private ImageView btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        tvEmptyCart = view.findViewById(R.id.tvEmptyCart);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        btnClearCart = view.findViewById(R.id.btnClearCart);
        btnBack = view.findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        observeViewModel();

        viewModel.loadCartItems();

        btnCheckout.setOnClickListener(v -> {
            // Lấy danh sách sản phẩm đã chọn
            Set<String> selectedIds = adapter.getSelectedItemIds();
            List<OrderItem> selectedItems = new ArrayList<>();
            int totalPrice = 0;
            for (OrderItem item : adapter.getItems()) {
                if (selectedIds.contains(item.getProductId())) {
                    selectedItems.add(item);
                    totalPrice += item.getPrice() * item.getQuantity();
                }
            }
            // Nếu không chọn sản phẩm nào thì không cho vào trang Checkout
            if (selectedItems.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn sản phẩm để thanh toán!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chuyển sang trang Checkout
            Intent intent = new Intent(requireContext(), CheckoutActivity.class);
            intent.putExtra("order_items", (java.io.Serializable) selectedItems);
            intent.putExtra("total_price", totalPrice);
            requireContext().startActivity(intent);

        });
        btnClearCart.setOnClickListener(v -> {
            viewModel.clearCart();
        });
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void observeViewModel() {
        viewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            if (items == null || items.isEmpty()) {
                tvEmptyCart.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                btnCheckout.setEnabled(false);
                adapter.updateItems(new ArrayList<>());
                tvTotalPrice.setText(FormatUtils.formatPrice(0));
            } else {
                tvEmptyCart.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.updateItems(items);
                btnCheckout.setEnabled(true);
                updateSelectedTotalPrice();
            }
        });

        viewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                viewModel.resetMessage();
            }
        });
    }

    @Override
    public void onQuantityChanged(OrderItem item, int newQuantity) {
        viewModel.updateItemQuantity(item.getProductId(), newQuantity);
    }

    @Override
    public void onRemoveItem(OrderItem item) {
        viewModel.removeFromCart(item.getProductId());
    }

    @Override
    public void onItemCheckedChanged(OrderItem item, boolean isChecked) {
        updateSelectedTotalPrice();
    }

    private void updateSelectedTotalPrice() {
        int total = 0;
        Set<String> selectedIds = adapter.getSelectedItemIds();
        for (OrderItem item : adapter.getItems()) {
            if (selectedIds.contains(item.getProductId())) {
                total += item.getPrice() * item.getQuantity();
            }
        }
        tvTotalPrice.setText(FormatUtils.formatPrice(total));
    }
}