package com.example.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {
    private final String postName;
    private final String postDescription;
    private final String postUsername;
    private final String postImageUri;
    private final double latitude;
    private final double longitude;

    public Post(String postName, String postDescription, String postUsername, String postImageUri, double latitude, double longitude) {
        this.postName = postName;
        this.postDescription = postDescription;
        this.postUsername = postUsername;
        this.postImageUri = postImageUri;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Post(Parcel in) {
        postName = in.readString();
        postDescription = in.readString();
        postUsername = in.readString();
        postImageUri = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getPostName() {
        return postName;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public String getPostUsername() {
        return postUsername;
    }

    public String getPostImageUri() {
        return postImageUri;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postName);
        dest.writeString(postDescription);
        dest.writeString(postUsername);
        dest.writeString(postImageUri);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
