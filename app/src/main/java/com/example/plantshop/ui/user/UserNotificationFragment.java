package com.example.plantshop.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantshop.R;
import com.example.plantshop.data.Model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserNotificationFragment extends Fragment {
    private RecyclerView rvNotifications;
    private NotificationAdapter notificationAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public UserNotificationFragment() {
        // Required empty public constructor
    }

    public static UserNotificationFragment newInstance() {
        return new UserNotificationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvNotifications = view.findViewById(R.id.rvNoti);
        notificationAdapter = new NotificationAdapter(new ArrayList<>());
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotifications.setAdapter(notificationAdapter);

        loadNotifications();
    }

    private void loadNotifications() {
        if (auth.getCurrentUser() == null) {
            return;
        }
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        return;
                    }
                    List<Notification> notifications = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        notifications.add(doc.toObject(Notification.class));
                    }
                    notificationAdapter.setNotifications(notifications);
                });
    }
}