package com.example.plantshop.ui.admin;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.plantshop.R;
import com.example.plantshop.data.Model.Order;
import com.example.plantshop.data.Model.OrderItem;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Order order);
    }

    public OrderAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order, listener);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvCustomerName, tvTotalQuantity, tvTotalAmount, tvStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.imgProduct);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvTotalQuantity = itemView.findViewById(R.id.tvTotalQuantity);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        void bind(Order order, OnItemClickListener listener) {
            tvCustomerName.setText(order.getCustomerName());
            tvTotalAmount.setText(String.format("%,d VNĐ", order.getTotalAmount()));

            int totalQuantity = 0;
            for (OrderItem item : order.getItems()) {
                totalQuantity += item.getQuantity();
            }
            tvTotalQuantity.setText(String.format("%d sản phẩm", totalQuantity));

            if (!order.getItems().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(order.getItems().get(0).getImageUrl())
                        .into(ivProduct);
            }

            if (order.isStatus()) {
                tvStatus.setText("Đã xác nhận");
                tvStatus.setTextColor(Color.GREEN);
            } else {
                tvStatus.setText("Chưa xác nhận");
                tvStatus.setTextColor(Color.RED);
            }

            itemView.setOnClickListener(v -> listener.onItemClick(order));
        }
    }
}