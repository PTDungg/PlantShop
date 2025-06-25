package com.example.plantshop.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantshop.R;
import com.example.plantshop.data.viewmodel.RecentSearchAdapter;
import com.example.plantshop.data.viewmodel.SearchViewModel;
import com.example.plantshop.data.viewmodel.SearchViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageView btnBack, btnPerformSearch;
    private RecyclerView rvRecentSearches;
    private TextView tvRecentSearchTitle;
    private RecentSearchAdapter adapter;
    private SearchViewModel viewModel;
    private boolean isGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Lấy userId hiện tại
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        isGuest = (currentUser == null || currentUser.isAnonymous());
        String userId = (currentUser != null) ? currentUser.getUid() : null;

        // Khởi tạo ViewModel bằng Factory
        SearchViewModelFactory factory = new SearchViewModelFactory(getApplication(), userId);
        viewModel = new ViewModelProvider(this, factory).get(SearchViewModel.class);

        initViews();
        setupRecyclerView();
        setupListeners();
        observeViewModel();

        // Yêu cầu ViewModel tải lịch sử
        viewModel.loadHistory();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        btnBack = findViewById(R.id.btnBack);
        btnPerformSearch = findViewById(R.id.btnPerformSearch);
        rvRecentSearches = findViewById(R.id.rvRecentSearches);
        tvRecentSearchTitle = findViewById(R.id.tvRecentSearchTitle);
    }

    private void setupRecyclerView() {
        adapter = new RecentSearchAdapter(new ArrayList<>(), new RecentSearchAdapter.OnItemClickListener() {
            @Override
            public void onQueryClick(String query) {
                etSearch.setText(query);
                performSearch(query);
            }

            @Override
            public void onDeleteClick(String query) {
                viewModel.removeSearchTerm(query);
            }
        });
        rvRecentSearches.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnPerformSearch.setOnClickListener(v -> performSearch(etSearch.getText().toString()));

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(etSearch.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void observeViewModel() {
        viewModel.searchHistory.observe(this, history -> {
            // Cập nhật UI mỗi khi LiveData thay đổi
            adapter.updateData(history);
            tvRecentSearchTitle.setVisibility(history.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    private void performSearch(String query) {
        String trimmedQuery = query.trim();
        if (!trimmedQuery.isEmpty()) {
            viewModel.addSearchTerm(trimmedQuery); // ViewModel sẽ lo việc lưu
            Intent intent = new Intent(this, ListProductActivity.class);
            intent.putExtra("SEARCH_QUERY", trimmedQuery);
            startActivity(intent);
        }
    }
}