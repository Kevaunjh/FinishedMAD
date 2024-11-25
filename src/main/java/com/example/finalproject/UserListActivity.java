package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private DBHelper dbHelper;
    private List<User> users;
    private UserAdapter userAdapter; // Use this adapter throughout
    private String loggedInUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        dbHelper = new DBHelper(this);
        loggedInUsername = getIntent().getStringExtra(LogIn.EXTRA_USERNAME);

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        // Initialize user list and adapter
        users = new ArrayList<>();
        userAdapter = new UserAdapter(users, loggedInUsername, new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(String viewedUsername, String loggedInUsername) {
                Intent intent = new Intent(UserListActivity.this, ViewUserProfile.class);
                intent.putExtra("VIEWED_USERNAME", viewedUsername);
                intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
                startActivity(intent);
            }

            public void onFollowClick(int viewedUserId, String viewedUsername) {
                if (dbHelper.isFollowing(viewedUserId, viewedUserId)) {
                    dbHelper.removeFollowing(viewedUserId, viewedUserId);
                    Toast.makeText(UserListActivity.this, "Unfollowed " + viewedUsername, Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.addFollowing(viewedUserId, viewedUserId);
                    Toast.makeText(UserListActivity.this, "Followed " + viewedUsername, Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerViewUsers.setAdapter(userAdapter); // Set the adapter to RecyclerView

        // Load users into the list
        loadUsers();
    }

    private void loadUsers() {
        users.clear();
        users.addAll(dbHelper.getAllUsers());
        userAdapter.notifyDataSetChanged(); // Notify changes to the correct adapter
    }
}
