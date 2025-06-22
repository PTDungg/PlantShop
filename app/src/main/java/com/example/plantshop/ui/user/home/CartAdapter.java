package com.example.plantshop.ui.user.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.plantshop.R;
import com.example.plantshop.data.Model.Item;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.HashSet;
import java.util.Set;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<Item> items;
    private final CartItemListener listener;
    private Set<String> selectedItemIds = new HashSet<>();

    public interface CartItemListener {
        void onQuantityChanged(Item item, int newQuantity);
        void onRemoveItem(Item item);
        void onItemCheckedChanged(Item item, boolean isChecked);
    }

    public CartAdapter(List<Item> items, CartItemListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<Item> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public Set<String> getSelectedItemIds() {
        return selectedItemIds;
    }

    public List<Item> getItems() {
        return items;
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvProductName, tvPrice, tvQuantity, tvTotalPrice;
        private MaterialButton btnDecrease, btnIncrease, btnRemove;
        private CheckBox checkbox;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            checkbox = itemView.findViewById(R.id.checkbox);
        }

        public void bind(Item item) {
            tvProductName.setText(item.getProductName());
            tvPrice.setText(formatPrice(item.getPrice()));
            tvQuantity.setText(String.valueOf(item.getQuantity()));
            tvTotalPrice.setText(formatPrice(item.getPrice() * item.getQuantity()));

            // Set click listeners
            btnDecrease.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() - 1;
                listener.onQuantityChanged(item, newQuantity);
            });

            btnIncrease.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() + 1;
                listener.onQuantityChanged(item, newQuantity);
            });

            btnRemove.setOnClickListener(v -> listener.onRemoveItem(item));

            // Load product image if available
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getImageUrl())
                        .placeholder(R.drawable.img_placeholder)
                        .error(R.drawable.img_placeholder)
                        .into(ivProductImage);
            } else {
                ivProductImage.setImageResource(R.drawable.img_placeholder);
            }

            checkbox.setOnCheckedChangeListener(null);
            checkbox.setChecked(selectedItemIds.contains(item.getProductId()));
            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedItemIds.add(item.getProductId());
                } else {
                    selectedItemIds.remove(item.getProductId());
                }
                listener.onItemCheckedChanged(item, isChecked);
            });
        }

        private String formatPrice(double price) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            return formatter.format(price);
        }
    }
}