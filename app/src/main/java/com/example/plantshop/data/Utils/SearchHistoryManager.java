package com.example.plantshop.data.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchHistoryManager {

    private static final String PREFS_NAME_PREFIX = "SearchHistoryPrefs_";
    private static final String GUEST_USER_ID = "GUEST"; // Dành cho user không đăng nhập
    private static final String HISTORY_KEY = "SearchHistory";
    private static final int MAX_HISTORY_SIZE = 10;

    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();


    public SearchHistoryManager(Context context, String userId) {
        String currentUserId = (userId == null || userId.isEmpty()) ? GUEST_USER_ID : userId;
        String prefsName = PREFS_NAME_PREFIX + currentUserId;
        sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    }

    public List<String> getSearchHistory() {
        String json = sharedPreferences.getString(HISTORY_KEY, "[]");
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void addSearchTerm(String term) {
        if (term == null || term.trim().isEmpty()) {
            return;
        }
        term = term.trim();
        List<String> history = getSearchHistory();
        history.remove(term);
        history.add(0, term);
        if (history.size() > MAX_HISTORY_SIZE) {
            history = history.subList(0, MAX_HISTORY_SIZE);
        }
        saveSearchHistory(history);
    }

    public void removeSearchTerm(String term) {
        List<String> history = getSearchHistory();
        history.remove(term);
        saveSearchHistory(history);
    }

    private void saveSearchHistory(List<String> history) {
        String json = gson.toJson(history);
        sharedPreferences.edit().putString(HISTORY_KEY, json).apply();
    }
}
