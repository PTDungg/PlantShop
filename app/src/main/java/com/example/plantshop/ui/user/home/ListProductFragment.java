package com.example.plantshop.ui.user.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantshop.R;
import com.example.plantshop.data.Model.Product;
import com.example.plantshop.ui.admin.ProductAdapter; // Giả sử bạn có Adapter này
import com.example.plantshop.ui.admin.ProductViewModel;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ListProductFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;
    private String productType;


    private static final String ARG_PRODUCT_TYPE = "productType";

    public ListProductFragment() {
        // Required empty public constructor
    }

    public static ListProductFragment newInstance(String productType) {
        ListProductFragment fragment = new ListProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_TYPE, productType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productType = getArguments().getString(ARG_PRODUCT_TYPE);
            Log.d("ListProductFragment", "productType: " + productType);
        }
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        observeViewModel();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
    }

    private void setupRecyclerView() {
        // Khởi tạo adapter với danh sách rỗng ban đầu
        productAdapter = new ProductAdapter(new ArrayList<>());
        recyclerView.setAdapter(productAdapter);
    }

    private void observeViewModel() {
        productViewModel.getAllProducts().observe(getViewLifecycleOwner(), allProducts -> {
            if (allProducts != null) {
                List<Product> filteredList = new ArrayList<>();
                String unaccentedProductType = unAccent(productType);

                if (productType == null || productType.equals("Tất cả")) {
                    filteredList.addAll(allProducts);
                } else {
                    for (Product product : allProducts) {
                        String unaccentedDbCategory = unAccent(product.getCategory());

                        // So sánh 2 chuỗi đã được xử lý
                        if (unaccentedProductType.equals(unaccentedDbCategory)) {
                            filteredList.add(product);
                        }
                    }
                }
                Log.d("FilterFinal", "Type: '" + productType + "' | Filtered count: " + filteredList.size());
                productAdapter.setData(filteredList);
            }
        });
    }

    public static String unAccent(String s) {
        if (s == null) {
            return "";
        }
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").toLowerCase().replace("đ", "d");
    }
}