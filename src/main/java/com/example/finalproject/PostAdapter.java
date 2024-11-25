package com.example.finalproject;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.InputStream;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);

        // Set post name and description
        holder.postName.setText(post.getPostName());
        holder.postDescription.setText(post.getPostDescription());

        // Log post information for debugging
        Log.d("PostAdapter", "Post Name: " + post.getPostName());
        Log.d("PostAdapter", "Image URI: " + post.getPostImageUri());

        // Display the image only if the URI is valid
        if (post.getPostImageUri() != null && !post.getPostImageUri().isEmpty()) {
            try {
                Uri imageUri = Uri.parse(post.getPostImageUri());

                // Check if Glide can load the image directly
                holder.postImage.setVisibility(View.VISIBLE);
                Glide.with(holder.itemView.getContext())
                        .load(imageUri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Optimize caching
                        .into(holder.postImage);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PostAdapter", "Error loading image: " + post.getPostImageUri(), e);
                holder.postImage.setVisibility(View.GONE); // Hide if there's an error
            }
        } else {
            holder.postImage.setVisibility(View.GONE); // Hide the ImageView if no image exists
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updatePosts(List<Post> updatedPosts) {
        this.postList = updatedPosts;
        notifyDataSetChanged();
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
