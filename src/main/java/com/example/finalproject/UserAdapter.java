package com.example.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> users; // List of user data to display
    private final OnUserClickListener userClickListener; // Listener for user item clicks
    private final String loggedInUsername; // Username of the logged-in user

    // Constructor
    public UserAdapter(List<User> users, String loggedInUsername, OnUserClickListener userClickListener) {
        this.users = users;
        this.loggedInUsername = loggedInUsername;
        this.userClickListener = userClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each user item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position); // Get the current user
        holder.usernameTextView.setText(user.getUsername()); // Set the username text

        // Handle item clicks to navigate to user profile
        holder.itemView.setOnClickListener(v -> {
            if (userClickListener != null) {
                userClickListener.onUserClick(user.getUsername(), loggedInUsername); // Pass the clicked user's data
            }
        });
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0; // Return the number of users in the list
    }

    // ViewHolder class to hold references to each user item view
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView; // TextView to display username

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView); // Find the TextView in the layout
        }
    }

    // Interface for handling user item click events
    public interface OnUserClickListener {
        void onUserClick(String viewedUsername, String loggedInUsername); // Triggered when a user profile is clicked
    }
}
