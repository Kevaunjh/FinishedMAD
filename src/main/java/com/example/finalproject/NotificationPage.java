package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationPage extends AppCompatActivity {

    private RecyclerView notificationsRecyclerView;
    private NotificationAdapter notificationAdapter;
    private DBHelper dbHelper;
    private List<String> notifications;
    private String loggedInUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_page);

        // Initialize RecyclerView
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);

        // Fetch logged-in username
        loggedInUsername = getIntent().getStringExtra(LogIn.EXTRA_USERNAME);

        if (notificationsRecyclerView != null) {
            notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            dbHelper = new DBHelper(this);
            int userId = dbHelper.getUserId(loggedInUsername);

            // Load notifications
            notifications = dbHelper.getFollowNotifications(userId);
            notificationAdapter = new NotificationAdapter(notifications);
            notificationsRecyclerView.setAdapter(notificationAdapter);
        } else {
            throw new NullPointerException("RecyclerView is not found in notification_page.xml");
        }

        // Set up navbar buttons
        setupNavBar();
    }

    private void setupNavBar() {
        ImageButton homeIcon = findViewById(R.id.icon1);
        ImageButton searchIcon = findViewById(R.id.icon2);
        ImageButton createIcon = findViewById(R.id.icon3);
        ImageButton bellIcon = findViewById(R.id.icon4);
        ImageButton userIcon = findViewById(R.id.icon5);

        // Home button
        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationPage.this, MainActivity.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
            startActivity(intent);
        });

        // Search button
        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationPage.this, SearchPage.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
            startActivity(intent);
        });

        // Create button
        createIcon.setOnClickListener(v -> {
            if (loggedInUsername == null || loggedInUsername.isEmpty() || loggedInUsername.equals("")) {
                Toast.makeText(this, "You are not signed in this is not accessible.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(NotificationPage.this, NewPost.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
            startActivity(intent);
        });


        // User profile button
        userIcon.setOnClickListener(v -> {
            if (loggedInUsername == null || loggedInUsername.isEmpty() || loggedInUsername.equals("")) {
                Toast.makeText(this, "You are not signed in this is not accessible.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(NotificationPage.this, MyProfile.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
            startActivity(intent);
        });
    }
    public void moveBack(View view) {

        Intent intent = new Intent(NotificationPage.this, MainActivity.class);
        intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
        startActivity(intent);
    }

}
