package com.example.plantshop.ui.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.plantshop.R;
import com.example.plantshop.ui.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;
    private List<AppCompatButton> categoryButtons = new ArrayList<>();

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        ImageView imgSignOut = view.findViewById(R.id.imgSigOut);

        imgSignOut.setOnClickListener(v -> {
            // Đăng xuất khỏi Firebase
            FirebaseAuth.getInstance().signOut();

            // Chuyển về màn hình đăng nhập (LoginActivity)
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductAdapter(new ArrayList<>());
        recyclerView.setAdapter(productAdapter);

        productAdapter.setOnItemClickListener(product -> {
            // Lưu vào ViewModel dùng chung
            productViewModel.selectProduct(product);

            // Gọi showOverlayFragment từ AdminActivity
            if (getActivity() instanceof AdminActivity) {
                ((AdminActivity) getActivity()).showOverlayFragment(new AdminUpdateDeleteFragment());
            }
        });


        // Phân loại sản phẩm
        AppCompatButton btnAll = view.findViewById(R.id.btnAll);
        AppCompatButton btnSenDa = view.findViewById(R.id.btnSenDa);
        AppCompatButton btnXuongRong = view.findViewById(R.id.btnXuongRong);
        AppCompatButton btnCayCanh = view.findViewById(R.id.btnCayCanh);
        AppCompatButton btnHoa = view.findViewById(R.id.btnHoa);

        categoryButtons = Arrays.asList(btnAll, btnSenDa, btnXuongRong, btnCayCanh, btnHoa);

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        //Quan sat du lieu
        productViewModel.getFilteredProducts().observe(getViewLifecycleOwner(), products -> {
            productAdapter.setProductList(products);
        });

        setCategoryButtonClick(btnAll, "all");
        setCategoryButtonClick(btnSenDa, "sen đá");
        setCategoryButtonClick(btnXuongRong, "xương rồng");
        setCategoryButtonClick(btnCayCanh, "cây cảnh");
        setCategoryButtonClick(btnHoa, "hoa");

        // Default chọn "Tất cả"
        btnAll.setSelected(true);
        productViewModel.loadProducts();

        return view;
    }

    private void setCategoryButtonClick(AppCompatButton button, String category) {
        button.setOnClickListener(v -> {
            for (AppCompatButton btn : categoryButtons) {
                btn.setSelected(false);
            }
            button.setSelected(true);
            productViewModel.filterByCategory(category);
        });
    }
}