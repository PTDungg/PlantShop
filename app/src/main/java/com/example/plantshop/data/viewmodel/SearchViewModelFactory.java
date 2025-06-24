package com.example.plantshop.data.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.plantshop.data.Utils.SearchHistoryManager;

public class SearchViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;
    private final String userId;

    public SearchViewModelFactory(Application application, String userId) {
        this.application = application;
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            SearchHistoryManager historyManager = new SearchHistoryManager(application, userId);
            return (T) new SearchViewModel(historyManager);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}