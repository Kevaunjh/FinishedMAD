package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyProfile extends AppCompatActivity {

    private TextView profileNameTextView;
    private TextView noPostsTextView;
    private TextView followerCountTextView;
    private TextView followingCountTextView;
    private RecyclerView userPostsRecyclerView;
    private PostAdapter postAdapter;
    private DBHelper dbHelper;
    private String username;
    private int userId;
    private ArrayList<Post> userPosts;

    @Override
    protected void onResume() {
        super.onResume();
        loadUserPosts();
        loadFollowCounts();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        // Initialize views
        profileNameTextView = findViewById(R.id.profileName);
        noPostsTextView = findViewById(R.id.noPostsTextView);
        followerCountTextView = findViewById(R.id.followerCount);
        followingCountTextView = findViewById(R.id.followingCount);
        userPostsRecyclerView = findViewById(R.id.userPostsRecyclerView);

        dbHelper = new DBHelper(this);

        username = getIntent().getStringExtra(LogIn.EXTRA_USERNAME);

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Invalid user. Returning to main menu.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display username on profile
        String displayName = username.contains("@") ? username.split("@")[0] : username;
        profileNameTextView.setText(displayName);

        // Retrieve user ID
        userId = dbHelper.getUserId(username);

        // Set up RecyclerView
        userPostsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userPosts = new ArrayList<>();
        postAdapter = new PostAdapter(userPosts);
        userPostsRecyclerView.setAdapter(postAdapter);

        // Load posts and follower/following counts
        loadUserPosts();
        loadFollowCounts();

        // Set up navbar buttons
        setupNavBar();
    }

    private void loadUserPosts() {
        userPosts.clear();

        // Load posts based on user ID
        List<Post> posts = dbHelper.getUserPostsById(userId);

        if (posts.isEmpty()) {
            noPostsTextView.setVisibility(View.VISIBLE);
            userPostsRecyclerView.setVisibility(View.GONE);
        } else {
            noPostsTextView.setVisibility(View.GONE);
            userPostsRecyclerView.setVisibility(View.VISIBLE);
            userPosts.addAll(posts);
            postAdapter.notifyDataSetChanged();
        }
    }

    private void loadFollowCounts() {
        int followersCount = dbHelper.getFollowersCount(userId);
        int followingCount = dbHelper.getFollowingCount(userId);

        followerCountTextView.setText(String.format("Followers: %d", followersCount));
        followingCountTextView.setText(String.format("Following: %d", followingCount));
    }

    private void setupNavBar() {
        ImageButton homeIcon = findViewById(R.id.icon1);
        ImageButton searchIcon = findViewById(R.id.icon2);
        ImageButton createIcon = findViewById(R.id.icon3);
        ImageButton bellIcon = findViewById(R.id.icon4);
        ImageButton userIcon = findViewById(R.id.icon5);

        // Home button
        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfile.this, MainActivity.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, username);
            startActivity(intent);
        });

        // Search button
        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfile.this, SearchPage.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, username);
            startActivity(intent);
        });

        // Create button
        createIcon.setOnClickListener(v -> {
            if (username == null || username.isEmpty()) {
                Toast.makeText(this, "You are not signed in. This is not accessible.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MyProfile.this, NewPost.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, username);
            startActivity(intent);
        });

        // Notifications button
        bellIcon.setOnClickListener(v -> {
            if (username == null || username.isEmpty()) {
                Toast.makeText(this, "You are not signed in. This is not accessible.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MyProfile.this, NotificationPage.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, username);
            startActivity(intent);
        });
    }

    public void moveBack(View view) {
        Intent intent = new Intent(MyProfile.this, MainActivity.class);
        intent.putExtra(LogIn.EXTRA_USERNAME, username);
        startActivity(intent);
    }

    public void logOut(View view) {
        Intent intent = new Intent(MyProfile.this, MainMenu.class);
        startActivity(intent);
    }
}
