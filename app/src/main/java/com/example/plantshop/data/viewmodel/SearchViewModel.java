package com.example.plantshop.data.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plantshop.data.Utils.SearchHistoryManager;

import java.util.List;

public class SearchViewModel extends ViewModel {

    private final SearchHistoryManager historyManager;
    private final MutableLiveData<List<String>> _searchHistory = new MutableLiveData<>();
    public final LiveData<List<String>> searchHistory = _searchHistory;

    public SearchViewModel(SearchHistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public void loadHistory() {
        _searchHistory.setValue(historyManager.getSearchHistory());
    }

    public void addSearchTerm(String term) {
        historyManager.addSearchTerm(term);
        loadHistory(); // Tải lại để cập nhật LiveData
    }

    public void removeSearchTerm(String term) {
        historyManager.removeSearchTerm(term);
        loadHistory(); // Tải lại để cập nhật LiveData
    }
}
