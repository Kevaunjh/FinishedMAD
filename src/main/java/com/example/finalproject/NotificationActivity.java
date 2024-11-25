package com.example.finalproject;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView notificationsRecyclerView;
    private NotificationAdapter notificationAdapter;
    private DBHelper dbHelper;
    private List<String> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_page);

        dbHelper = new DBHelper(this);

        // Initialize RecyclerView
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch logged-in username
        String loggedInUsername = getIntent().getStringExtra(LogIn.EXTRA_USERNAME);
        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            Toast.makeText(this, "Error: Username not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get userId from username
        int userId = dbHelper.getUserId(loggedInUsername);

        // Load notifications
        notifications = dbHelper.getFollowNotifications(userId);
        if (notifications.isEmpty()) {
            Toast.makeText(this, "No notifications available", Toast.LENGTH_SHORT).show();
        } else {
            notificationAdapter = new NotificationAdapter(notifications);
            notificationsRecyclerView.setAdapter(notificationAdapter);
        }
    }
}
