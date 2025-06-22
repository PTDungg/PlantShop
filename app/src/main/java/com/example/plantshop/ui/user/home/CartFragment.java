package com.example.plantshop.ui.user.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ImageView;
import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantshop.R;
import com.example.plantshop.data.Model.Item;

import java.text.NumberFormat;
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

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // Observe LiveData
        observeViewModel();

        // Load cart items
        viewModel.loadCartItems();

        // Setup click listeners
        btnCheckout.setOnClickListener(v -> {
            // Handle checkout logic
            Toast.makeText(getContext(), "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
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
            } else {
                tvEmptyCart.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.updateItems(items);
                btnCheckout.setEnabled(true);
            }
            updateSelectedTotalPrice();
        });

        viewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                viewModel.resetMessage();
            }
        });
    }

    @Override
    public void onQuantityChanged(Item item, int newQuantity) {
        viewModel.updateItemQuantity(item.getProductId(), newQuantity);
    }

    @Override
    public void onRemoveItem(Item item) {
        viewModel.removeFromCart(item.getProductId());
    }

    @Override
    public void onItemCheckedChanged(Item item, boolean isChecked) {
        updateSelectedTotalPrice();
    }

    private void updateSelectedTotalPrice() {
        double total = 0.0;
        Set<String> selectedIds = adapter.getSelectedItemIds();
        for (Item item : adapter.getItems()) {
            if (selectedIds.contains(item.getProductId())) {
                total += item.getPrice() * item.getQuantity();
            }
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalPrice.setText(formatter.format(total));
    }
}
