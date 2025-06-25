package com.example.plantshop.ui.admin;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.plantshop.R;
import com.example.plantshop.data.model.Order;

public class AdminOrderFragment extends Fragment {
    private OrderViewModel orderViewModel;
    private OrderAdapter orderAdapter;
    private RecyclerView rvOrders;

    public AdminOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_order, container, false);

        rvOrders = view.findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(new OrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Order order) {
                if (getActivity() instanceof AdminActivity) {
                    AdminOrderDetailFragment detailFragment = new AdminOrderDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("order", order);
                    detailFragment.setArguments(bundle);
                    ((AdminActivity) getActivity()).showOverlayFragment(detailFragment);
                }
            }
        });
        rvOrders.setAdapter(orderAdapter);

        orderViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            orderAdapter.setOrders(orders);
        });

        return view;
    }
}