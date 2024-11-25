package com.example.finalproject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class PostViewModel extends ViewModel {
    private final MutableLiveData<List<Post>> postsLiveData = new MutableLiveData<>(new ArrayList<>()); // Initialize with an empty list

    public LiveData<List<Post>> getPosts() {
        return postsLiveData;
    }

    public void addPost(Post newPost) {
        List<Post> currentPosts = postsLiveData.getValue();
        if (currentPosts != null) {
            currentPosts.add(newPost);
            postsLiveData.setValue(currentPosts); // Notify observers
        }
    }
}
