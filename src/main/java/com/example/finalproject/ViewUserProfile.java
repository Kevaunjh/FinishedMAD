package com.example.finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ViewUserProfile extends AppCompatActivity {

    private TextView usernameTextView, followersCountTextView, followingCountTextView, noPostsTextView;
    private Button followButton;
    private RecyclerView userPostsRecyclerView;
    private PostAdapter postAdapter;
    private ArrayList<Post> userPosts;
    private DBHelper dbHelper;
    private String viewedUsername, loggedInUsername;
    private int followerId, viewedUserId;
    private boolean isFollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);

        dbHelper = new DBHelper(this);

        // Initialize views
        usernameTextView = findViewById(R.id.usernameTextView);
        followersCountTextView = findViewById(R.id.followersCountTextView);
        followingCountTextView = findViewById(R.id.followingCountTextView);
        followButton = findViewById(R.id.followButton);
        noPostsTextView = findViewById(R.id.noPostsTextView);
        userPostsRecyclerView = findViewById(R.id.userPostsRecyclerView);

        // Set up RecyclerView
        userPostsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userPosts = new ArrayList<>();
        postAdapter = new PostAdapter(userPosts);
        userPostsRecyclerView.setAdapter(postAdapter);

        // Get intent extras
        viewedUsername = getIntent().getStringExtra("VIEWED_USERNAME");
        loggedInUsername = getIntent().getStringExtra(LogIn.EXTRA_USERNAME);

        if (viewedUsername == null || loggedInUsername == null) {
            finish();
            return;
        }

        loadUserProfile();
        setupFollowButton();
        setupNavBar();
    }

    private void loadUserProfile() {
        viewedUserId = dbHelper.getUserId(viewedUsername);
        followerId = dbHelper.getUserId(loggedInUsername);

        // Set profile name
        usernameTextView.setText(viewedUsername);

        // Load follower/following counts
        int followersCount = dbHelper.getFollowersCount(viewedUserId);
        int followingCount = dbHelper.getFollowingCount(viewedUserId);

        followersCountTextView.setText(followersCount + " Followers");
        followingCountTextView.setText(followingCount + " Following");

        // Load posts for the viewed user
        loadUserPosts(viewedUserId);
    }

    private void loadUserPosts(int userId) {
        userPosts.clear();
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

    private void setupFollowButton() {
        // If viewing own profile, hide the follow button
        if (followerId == viewedUserId || loggedInUsername.equals("") || loggedInUsername.isEmpty()) {
            followButton.setVisibility(View.GONE);
            return;
        }

        // Initialize follow state
        isFollowing = dbHelper.isFollowing(followerId, viewedUserId);
        updateFollowButton(isFollowing);

        followButton.setOnClickListener(v -> {
            // Toggle follow status
            if (isFollowing) {
                dbHelper.removeFollowing(followerId, viewedUserId);
                Toast.makeText(this, "Unfollowed " + viewedUsername, Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addFollowing(followerId, viewedUserId);
                Toast.makeText(this, "Followed " + viewedUsername, Toast.LENGTH_SHORT).show();
            }
            isFollowing = !isFollowing; // Update follow state
            updateFollowButton(isFollowing);

            // Update follower count
            int followersCount = dbHelper.getFollowersCount(viewedUserId);
            followersCountTextView.setText(followersCount + " Followers");
        });
    }

    private void updateFollowButton(boolean isFollowing) {
        if (isFollowing) {
            followButton.setText("Following");
            followButton.setBackgroundColor(Color.GRAY);
        } else {
            followButton.setText("Follow");
            followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void setupNavBar() {
        ImageButton homeIcon = findViewById(R.id.icon1);
        ImageButton searchIcon = findViewById(R.id.icon2);
        ImageButton createIcon = findViewById(R.id.icon3);
        ImageButton bellIcon = findViewById(R.id.icon4);
        ImageButton userIcon = findViewById(R.id.icon5);

        // Home button
        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ViewUserProfile.this, MainActivity.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
            startActivity(intent);
        });

        // Search button
        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ViewUserProfile.this, SearchPage.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
            startActivity(intent);
        });

        // Create button
        createIcon.setOnClickListener(v -> {
            if (loggedInUsername == null || loggedInUsername.isEmpty() || loggedInUsername.equals("")) {
                Toast.makeText(this, "You are not signed in this is not accessible.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(ViewUserProfile.this, NewPost.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
            startActivity(intent);
        });

        // Notifications button
        bellIcon.setOnClickListener(v -> {
            if (loggedInUsername == null || loggedInUsername.isEmpty() || loggedInUsername.equals("")) {
                Toast.makeText(this, "You are not signed in this is not accessible.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(ViewUserProfile.this, NotificationPage.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
            startActivity(intent);
        });

        // User profile button
        userIcon.setOnClickListener(v -> {
            if (loggedInUsername == null || loggedInUsername.isEmpty() || loggedInUsername.equals("")) {
                Toast.makeText(this, "You are not signed in this is not accessible.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(ViewUserProfile.this, MyProfile.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
            startActivity(intent);
        });
    }

    public void moveBack(View view) {

        Intent intent = new Intent(ViewUserProfile.this, MainActivity.class);
        intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
        startActivity(intent);
    }
}
