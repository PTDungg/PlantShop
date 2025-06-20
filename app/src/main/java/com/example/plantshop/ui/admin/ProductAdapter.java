package com.example.plantshop.ui.admin;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.plantshop.R;
import com.example.plantshop.data.Model.Product;
import java.text.NumberFormat;
import java.util.*;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public void setProductList(List<Product> list) {
        this.productList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = productList.get(position);

        holder.tvName.setText(p.getName());
        holder.tvPrice.setText(NumberFormat.getInstance().format(p.getPrice()) + " VNĐ");
        holder.tvStatus.setText(p.isAvailable() ? "Còn hàng" : "Hết hàng");
        holder.tvStatus.setTextColor(
                p.isAvailable() ? holder.itemView.getContext().getColor(R.color.primary_green)
                        : holder.itemView.getContext().getColor(android.R.color.holo_red_dark));

        Glide.with(holder.itemView.getContext())
                .load(p.getImageUrl())
                .placeholder(R.drawable.img_placeholder)
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice, tvStatus;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}

