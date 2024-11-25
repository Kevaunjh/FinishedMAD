package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView postRecyclerView;
    private MainPostAdapter postAdapter;
    private ArrayList<Post> postList;
    private DBHelper dbHelper;
    private TextView noPostsTextView;
    private TextView tabFollowing, tabForYou;
    private String username;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postRecyclerView = findViewById(R.id.postRecyclerView);
        noPostsTextView = findViewById(R.id.noPostsTextView);
        tabFollowing = findViewById(R.id.tabFollowing);
        tabForYou = findViewById(R.id.tabForYou);

        dbHelper = new DBHelper(this);
        username = getIntent().getStringExtra(LogIn.EXTRA_USERNAME);

        if (username == null) {
            username = "";
        }

        userId = dbHelper.getUserId(username);

        setupRecyclerView();
        setActiveTab(tabForYou, tabFollowing);

        tabFollowing.setOnClickListener(v -> {
            setActiveTab(tabFollowing, tabForYou);
            loadFollowingPosts();
        });

        tabForYou.setOnClickListener(v -> {
            setActiveTab(tabForYou, tabFollowing);
            loadAllPosts();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFollowingPosts();
        loadAllPosts();
        setActiveTab(tabForYou, tabFollowing);
    }

    private void setupRecyclerView() {
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new MainPostAdapter(postList, this::moveToPostProfile);
        postRecyclerView.setAdapter(postAdapter);
    }

    private void loadFollowingPosts() {
        postList.clear();
        postList.addAll(dbHelper.getPostsFromFollowing(userId));
        noPostsTextView.setVisibility(postList.isEmpty() ? View.VISIBLE : View.GONE);
        postAdapter.notifyDataSetChanged();
    }

    private void loadAllPosts() {
        postList.clear();
        postList.addAll(dbHelper.getAllPosts());
        noPostsTextView.setVisibility(postList.isEmpty() ? View.VISIBLE : View.GONE);
        postAdapter.notifyDataSetChanged();
    }

    private void setActiveTab(TextView activeTab, TextView inactiveTab) {
        activeTab.setAlpha(1f);
        inactiveTab.setAlpha(0.5f);
    }

    public void moveToCreatePost(View view) {
        if (username.isEmpty()) {
            Toast.makeText(this, "You are not signed in.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(MainActivity.this, NewPost.class);
        intent.putExtra(LogIn.EXTRA_USERNAME, username);
        startActivity(intent);
    }

    public void moveToMyProfile(View view) {
        if (username.isEmpty()) {
            Toast.makeText(this, "You are not signed in.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(MainActivity.this, MyProfile.class);
        intent.putExtra(LogIn.EXTRA_USERNAME, username);
        startActivity(intent);
    }

    public void moveToSearch(View view) {
        Intent intent = new Intent(MainActivity.this, SearchPage.class);
        intent.putExtra(LogIn.EXTRA_USERNAME, username);
        startActivity(intent);
    }

    public void moveToNotification(View view) {
        if (username.isEmpty()) {
            Toast.makeText(this, "You are not signed in.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(MainActivity.this, NotificationPage.class);
        intent.putExtra(LogIn.EXTRA_USERNAME, username);
        startActivity(intent);
    }

    public void moveToPostProfile(String postUsername) {
        Intent intent = new Intent(MainActivity.this, ViewUserProfile.class);
        intent.putExtra("VIEWED_USERNAME", postUsername);
        intent.putExtra(LogIn.EXTRA_USERNAME, username);
        startActivity(intent);
    }
}
