package com.example.plantshop.data.viewmodel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantshop.R; // Đảm bảo import R từ đúng package của bạn

import java.util.List;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.ViewHolder> {

    private List<String> searchHistory;
    private final OnItemClickListener listener;

    // Interface để xử lý sự kiện click từ Activity
    public interface OnItemClickListener {
        void onQueryClick(String query);
        void onDeleteClick(String query);
    }

    public RecentSearchAdapter(List<String> searchHistory, OnItemClickListener listener) {
        this.searchHistory = searchHistory;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng layout item_recent_search.xml bạn đã cung cấp
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String query = searchHistory.get(position);
        holder.tvSearchQuery.setText(query);

        // Gán sự kiện cho cả dòng item
        holder.itemView.setOnClickListener(v -> listener.onQueryClick(query));

        // Gán sự kiện cho nút xóa
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(query));
    }

    @Override
    public int getItemCount() {
        return searchHistory.size();
    }

    // Hàm để cập nhật dữ liệu cho adapter
    public void updateData(List<String> newHistory) {
        this.searchHistory = newHistory;
        notifyDataSetChanged();
    }

    // ViewHolder chứa các view của một item
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSearchQuery;
        ImageView btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view từ layout item_recent_search.xml
            tvSearchQuery = itemView.findViewById(R.id.tvSearchQuery);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
