package com.example.plantshop.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plantshop.R;
import com.example.plantshop.data.Model.Product;
import com.example.plantshop.ui.admin.ProductAdapter;
import com.example.plantshop.ui.admin.ProductViewModel;
import com.example.plantshop.ui.user.ProductDetailActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
        productAdapter = new ProductAdapter(new ArrayList<>());
        productAdapter.setOnItemClickListener(product -> {
            // Kiểm tra xem có phải user không
            if (FirebaseAuth.getInstance().getCurrentUser() != null && !FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
                // User: dùng Navigation
                // Ẩn ViewPager2 và TabLayout, hiện NavHostFragment
                requireActivity().findViewById(R.id.viewPager).setVisibility(View.GONE);
                requireActivity().findViewById(R.id.tab_layout).setVisibility(View.GONE);
                requireActivity().findViewById(R.id.nav_host_fragment_user).setVisibility(View.VISIBLE);
                // Chuyển sang Fragment chi tiết sản phẩm
                Bundle bundle = new Bundle();
                bundle.putString("productId", product.getId());
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_user);
                navController.navigate(R.id.nav_product_detail, bundle);
            } else {
                // Guest: dùng Activity
                Intent intent = new Intent(getContext(), ProductDetailActivity.class);
                intent.putExtra("PRODUCT_ID", product.getId());
                startActivity(intent);
            }
        });
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

    private void showHeaderAndBottomNav() {
        View appBar = requireActivity().findViewById(R.id.app_bar_layout);
        View bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (appBar != null) appBar.setVisibility(View.VISIBLE);
        if (bottomNav != null) bottomNav.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        showHeaderAndBottomNav();
        // Hiện lại tabLayout và viewPager khi về Home
        View tabLayout = requireActivity().findViewById(R.id.tab_layout);
        View viewPager = requireActivity().findViewById(R.id.viewPager);
        View navHostFragment = requireActivity().findViewById(R.id.nav_host_fragment_user);
        if (tabLayout != null) tabLayout.setVisibility(View.VISIBLE);
        if (viewPager != null) viewPager.setVisibility(View.VISIBLE);
        if (navHostFragment != null) navHostFragment.setVisibility(View.GONE);
    }
}



