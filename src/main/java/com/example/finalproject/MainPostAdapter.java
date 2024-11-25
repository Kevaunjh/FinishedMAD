package com.example.finalproject;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;

public class MainPostAdapter extends RecyclerView.Adapter<MainPostAdapter.ViewHolder> {

    private final List<Post> postList;
    private final OnPostClickListener listener;
    private String username;

    public MainPostAdapter(List<Post> postList, OnPostClickListener listener) {
        this.postList = postList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false); // Ensure the layout matches `PostAdapter`
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);

        // Set the actual post details
        holder.postName.setText( post.getPostName());
        holder.postDescription.setText(post.getPostDescription());

        // Load image if the URI is valid
        if (post.getPostImageUri() != null && !post.getPostImageUri().isEmpty()) {
            holder.postImage.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(post.getPostImageUri())) // Load image from URI
                    .into(holder.postImage);
        } else {
            holder.postImage.setVisibility(View.GONE); // Hide ImageView if no image exists
        }

        // Handle username click
        holder.postName.setOnClickListener(v -> listener.onPostClick(post.getPostUsername()));
    }




    @Override
    public int getItemCount() {
        return postList.size();
    }

    public interface OnPostClickListener {
        void onPostClick(String postUsername);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView postName, postDescription;
        ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postName = itemView.findViewById(R.id.postName);
            postDescription = itemView.findViewById(R.id.postDescription);
            postImage = itemView.findViewById(R.id.postImage);
        }
    }
}
