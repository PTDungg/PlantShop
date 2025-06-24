package com.example.plantshop.ui.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.plantshop.R;
import com.example.plantshop.data.Model.Order;
import com.example.plantshop.data.Model.OrderItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderDetailFragment extends Fragment {
    private OrderViewModel orderViewModel;
    private Order order;
    private RecyclerView rvOrderItems;
    private OrderItemAdapter itemAdapter;
    private Button btnConfirm;
    private TextView tvCustomerName, tvCustomerPhone, tvCustomerAddress, tvTotalAmount;

    public AdminOrderDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        if (getArguments() != null) {
            order = (Order) getArguments().getSerializable("order");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_order_detail, container, false);

        tvCustomerName = view.findViewById(R.id.tvCustomerName);
        tvCustomerPhone = view.findViewById(R.id.tvCustomerPhone);
        tvCustomerAddress = view.findViewById(R.id.tvCustomerAddress);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        rvOrderItems = view.findViewById(R.id.rvOrderItems);

        rvOrderItems.setLayoutManager(new LinearLayoutManager(getContext()));
        itemAdapter = new OrderItemAdapter();
        rvOrderItems.setAdapter(itemAdapter);

        if (order != null) {
            tvCustomerName.setText(order.getCustomerName());
            tvCustomerPhone.setText(String.valueOf(order.getCustomerPhone()));
            tvCustomerAddress.setText(order.getCustomerAddress());
            tvTotalAmount.setText(String.format("%,d VNĐ", order.getTotalAmount()));
            itemAdapter.setItems(order.getItems());

            btnConfirm.setEnabled(!order.isStatus());
            btnConfirm.setOnClickListener(v -> {
                orderViewModel.updateOrderStatus(order.getOrderId(), true);
                Toast.makeText(getContext(), "Xác nhận đơn hàng thành công", Toast.LENGTH_SHORT).show();
                btnConfirm.setEnabled(false);
                order.setStatus(true);
//                requireActivity().onBackPressed();
            });
        }

        return view;
    }

    static class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
        private List<OrderItem> items = new ArrayList<>();

        public void setItems(List<OrderItem> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_order_detail, parent, false);
            return new OrderItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
            OrderItem item = items.get(position);
            holder.tvProductName.setText(item.getProductName());
            holder.tvQuantity.setText(String.format("Số lượng: %d", item.getQuantity()));
            holder.tvPrice.setText(String.format("%,d VNĐ", item.getPrice()));
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .into(holder.ivProduct);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class OrderItemViewHolder extends RecyclerView.ViewHolder {
            ImageView ivProduct;
            TextView tvProductName, tvQuantity, tvPrice;

            public OrderItemViewHolder(@NonNull View itemView) {
                super(itemView);
                ivProduct = itemView.findViewById(R.id.imgProduct);
                tvProductName = itemView.findViewById(R.id.tvProductName);
                tvQuantity = itemView.findViewById(R.id.tvQuantity);
                tvPrice = itemView.findViewById(R.id.tvPrice);
            }
        }
    }
}