package com.example.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "socialmedia_db";
    private static final int DATABASE_VERSION = 11;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_POSTS = "posts";
    private static final String TABLE_USERS_FOLLOWING = "users_following";

    // User table columns
    private static final String COLUMN_USERID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_LOCATION = "location";

    // Post table columns
    private static final String COLUMN_POSTID = "post_id";
    private static final String COLUMN_POST = "post";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE_URI = "image_uri";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_POSTUSERID = "post_user_id";

    // Following table columns
    private static final String COLUMN_FOLLOWER_ID = "follower_id";
    private static final String COLUMN_FOLLOWING_ID = "following_id";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USERID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_LOCATION + " TEXT)";
        db.execSQL(CREATE_TABLE_USERS);

        String CREATE_TABLE_POSTS = "CREATE TABLE " + TABLE_POSTS + " (" +
                COLUMN_POSTID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_POST + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_IMAGE_URI + " TEXT, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL, " +
                COLUMN_POSTUSERID + " INTEGER, " +
                "FOREIGN KEY (" + COLUMN_POSTUSERID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USERID + "))";
        db.execSQL(CREATE_TABLE_POSTS);

        String CREATE_TABLE_USERS_FOLLOWING = "CREATE TABLE " + TABLE_USERS_FOLLOWING + " (" +
                COLUMN_FOLLOWER_ID + " INTEGER, " +
                COLUMN_FOLLOWING_ID + " INTEGER, " +
                "FOREIGN KEY (" + COLUMN_FOLLOWER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USERID + "), " +
                "FOREIGN KEY (" + COLUMN_FOLLOWING_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USERID + "))";
        db.execSQL(CREATE_TABLE_USERS_FOLLOWING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS_FOLLOWING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Add a user
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Validate a user
    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COLUMN_USERID},
                COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password},
                null, null, null
        );

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    public GeoPoint getLocationCoordinates(String locationName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_LATITUDE + ", " + COLUMN_LONGITUDE +
                " FROM " + TABLE_POSTS +
                " WHERE " + COLUMN_DESCRIPTION + " LIKE ?";

        Cursor cursor = db.rawQuery(query, new String[]{"%" + locationName + "%"});
        GeoPoint location = null;

        if (cursor.moveToFirst()) {
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE));
            location = new GeoPoint(latitude, longitude);
        }

        cursor.close();
        return location;
    }

    // Get user ID by username
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COLUMN_USERID},
                COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null
        );

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USERID));
        }
        cursor.close();
        return userId;
    }

    // Add a post
    public void addPost(String postTitle, String postDescription, int userId, String imageUri, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST, postTitle);
        values.put(COLUMN_DESCRIPTION, postDescription);
        values.put(COLUMN_IMAGE_URI, imageUri);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_POSTUSERID, userId);

        db.insert(TABLE_POSTS, null, values);
        db.close();
    }

    // Search users by username
    public List<User> searchUsers(String usernameQuery, String locationQuery) {
        List<User> matchedUsers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM " + TABLE_USERS + " WHERE 1=1");
        List<String> queryArgs = new ArrayList<>();

        if (usernameQuery != null && !usernameQuery.trim().isEmpty()) {
            queryBuilder.append(" AND ").append(COLUMN_USERNAME).append(" LIKE ?");
            queryArgs.add("%" + usernameQuery.trim() + "%");
        }

        Cursor cursor = db.rawQuery(queryBuilder.toString(), queryArgs.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USERID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                matchedUsers.add(new User(userId, username));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return matchedUsers;
    }

    // Get all posts
    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_POSTS, null);

        if (cursor.moveToFirst()) {
            do {
                posts.add(new Post(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        null,
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return posts;
    }

    // Get posts by proximity
    public List<Post> getPostsByProximity(double latitude, double longitude, double radius) {
        List<Post> posts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_POSTS +
                " WHERE (ABS(" + COLUMN_LATITUDE + " - ?) <= ?) AND (ABS(" + COLUMN_LONGITUDE + " - ?) <= ?)";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(latitude), String.valueOf(radius),
                String.valueOf(longitude), String.valueOf(radius)
        });

        if (cursor.moveToFirst()) {
            do {
                posts.add(new Post(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        null,
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return posts;
    }

    // Get user posts by ID
    public List<Post> getUserPostsById(int userId) {
        List<Post> userPosts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_POSTS,
                null,
                COLUMN_POSTUSERID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                userPosts.add(new Post(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        null,
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userPosts;
    }

    // Get posts from users being followed
    public List<Post> getPostsFromFollowing(int userId) {
        List<Post> posts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT p." + COLUMN_POST + ", p." + COLUMN_DESCRIPTION + ", p." + COLUMN_IMAGE_URI + ", " +
                "p." + COLUMN_LATITUDE + ", p." + COLUMN_LONGITUDE + ", u." + COLUMN_USERNAME +
                " FROM " + TABLE_POSTS + " p " +
                "JOIN " + TABLE_USERS_FOLLOWING + " uf ON p." + COLUMN_POSTUSERID + " = uf." + COLUMN_FOLLOWING_ID + " " +
                "JOIN " + TABLE_USERS + " u ON p." + COLUMN_POSTUSERID + " = u." + COLUMN_USERID + " " +
                "WHERE uf." + COLUMN_FOLLOWER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                posts.add(new Post(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return posts;
    }
    // Get all users with optional location
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);

        if (cursor.moveToFirst()) {
            do {
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USERID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
                userList.add(new User(userId, username, location));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }


    // Get follow notifications
    public List<String> getFollowNotifications(int userId) {
        List<String> notifications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT u." + COLUMN_USERNAME + " FROM " + TABLE_USERS + " u " +
                "JOIN " + TABLE_USERS_FOLLOWING + " uf ON u." + COLUMN_USERID + " = uf." + COLUMN_FOLLOWER_ID +
                " WHERE uf." + COLUMN_FOLLOWING_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                notifications.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)) + " started following you.");
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notifications;
    }

    // Add a following relationship
    public boolean addFollowing(int followerId, int followingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOLLOWER_ID, followerId);
        values.put(COLUMN_FOLLOWING_ID, followingId);

        long result = db.insert(TABLE_USERS_FOLLOWING, null, values);
        db.close();
        return result != -1;
    }

    // Remove a following relationship
    public boolean removeFollowing(int followerId, int followingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(
                TABLE_USERS_FOLLOWING,
                COLUMN_FOLLOWER_ID + " = ? AND " + COLUMN_FOLLOWING_ID + " = ?",
                new String[]{String.valueOf(followerId), String.valueOf(followingId)}
        );
        db.close();
        return rowsDeleted > 0;
    }

    // Get followers count
    public int getFollowersCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_USERS_FOLLOWING +
                        " WHERE " + COLUMN_FOLLOWING_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Get following count
    public int getFollowingCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_USERS_FOLLOWING +
                        " WHERE " + COLUMN_FOLLOWER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Check if a user is following another user
    public boolean isFollowing(int followerId, int followingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_USERS_FOLLOWING +
                " WHERE " + COLUMN_FOLLOWER_ID + " = ? AND " + COLUMN_FOLLOWING_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(followerId), String.valueOf(followingId)});

        boolean isFollowing = false;
        if (cursor.moveToFirst()) {
            isFollowing = cursor.getInt(0) > 0;
        }
        cursor.close();
        return isFollowing;
    }
}

