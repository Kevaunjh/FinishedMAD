package com.example.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private int userId;
    private String username;
    private String location;

    // Constructor with userId, username, and location
    public User(int userId, String username, String location) {
        this.userId = userId;
        this.username = username;
        this.location = location;
    }

    // New Constructor with userId and username only
    public User(int userId, String username) {
        this.userId = userId;
        this.username = username;
        this.location = null; // Set location to null or empty if not provided
    }

    // Constructor for Parcelable
    protected User(Parcel in) {
        userId = in.readInt();
        username = in.readString();
        location = in.readString();
    }

    // Parcelable.Creator implementation
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getLocation() {
        return location;
    }

    // Setters
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(username);
        dest.writeString(location);
    }
}
