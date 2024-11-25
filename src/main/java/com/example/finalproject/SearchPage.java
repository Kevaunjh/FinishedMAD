package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class SearchPage extends AppCompatActivity {

    private EditText latitudeInput, longitudeInput, radiusInput;
    private Button searchByLocationButton;
    private SearchView userSearchView;
    private RecyclerView recyclerViewUsers;
    private TextView tabUser, tabLocation, noPostsTextView;
    private MapView mapView;
    private DBHelper dbHelper;
    private String loggedInUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OSMDroid configuration
        Configuration.getInstance().setUserAgentValue(getApplicationContext().getPackageName());
        Locale.setDefault(Locale.ENGLISH);

        setContentView(R.layout.activity_search_page);

        // Initialize components
        latitudeInput = findViewById(R.id.latitudeInput);
        longitudeInput = findViewById(R.id.longitudeInput);
        radiusInput = findViewById(R.id.radiusInput);
        searchByLocationButton = findViewById(R.id.searchByLocationButton);
        userSearchView = findViewById(R.id.userSearchView);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        tabUser = findViewById(R.id.tabUser);
        tabLocation = findViewById(R.id.tabLocation);
        noPostsTextView = findViewById(R.id.noPostsTextView);
        mapView = findViewById(R.id.mapView);
        dbHelper = new DBHelper(this);

        // Retrieve logged-in username from intent
        loggedInUsername = getIntent().getStringExtra(LogIn.EXTRA_USERNAME);

        // RecyclerView setup
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        // MapView setup
        mapView.setMultiTouchControls(true);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        setDefaultMapView();

        // Default to User tab
        showUserTab();

        // Set up tab switching
        setupTabs();

        // Set up search functionality
        searchByLocationButton.setOnClickListener(v -> searchPostsByLocation());
        setupUserSearch();
    }

    private void setupTabs() {
        tabUser.setOnClickListener(v -> showUserTab());
        tabLocation.setOnClickListener(v -> showLocationTab());
    }

    private void showUserTab() {
        tabUser.setAlpha(1f);
        tabLocation.setAlpha(0.5f);
        userSearchView.setVisibility(View.VISIBLE);
        recyclerViewUsers.setVisibility(View.VISIBLE);
        latitudeInput.setVisibility(View.GONE);
        longitudeInput.setVisibility(View.GONE);
        radiusInput.setVisibility(View.GONE);
        searchByLocationButton.setVisibility(View.GONE);
        mapView.setVisibility(View.GONE);
        noPostsTextView.setVisibility(View.GONE);

        // Load all users
        List<User> users = dbHelper.getAllUsers();
        if (users.isEmpty()) {
            noPostsTextView.setVisibility(View.VISIBLE);
            noPostsTextView.setText("No users found.");
            recyclerViewUsers.setVisibility(View.GONE);
        } else {
            noPostsTextView.setVisibility(View.GONE);
            recyclerViewUsers.setVisibility(View.VISIBLE);
            recyclerViewUsers.setAdapter(new UserAdapter(users, loggedInUsername, this::navigateToUserProfile));
        }
    }

    private void showLocationTab() {
        tabLocation.setAlpha(1f);
        tabUser.setAlpha(0.5f);
        userSearchView.setVisibility(View.GONE);
        recyclerViewUsers.setVisibility(View.GONE);
        latitudeInput.setVisibility(View.VISIBLE);
        longitudeInput.setVisibility(View.VISIBLE);
        radiusInput.setVisibility(View.VISIBLE);
        searchByLocationButton.setVisibility(View.VISIBLE);
        mapView.setVisibility(View.VISIBLE);
        noPostsTextView.setVisibility(View.GONE);
    }

    private void setupUserSearch() {
        userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUsers(newText);
                return true;
            }
        });
    }

    private void searchUsers(String query) {
        List<User> matchedUsers = dbHelper.searchUsers(query, null);
        if (matchedUsers.isEmpty()) {
            noPostsTextView.setVisibility(View.VISIBLE);
            noPostsTextView.setText("No users found.");
            recyclerViewUsers.setVisibility(View.GONE);
        } else {
            noPostsTextView.setVisibility(View.GONE);
            recyclerViewUsers.setVisibility(View.VISIBLE);
            recyclerViewUsers.setAdapter(new UserAdapter(matchedUsers, loggedInUsername, this::navigateToUserProfile));
        }
    }

    public void moveBack(View view) {
        Intent intent = new Intent(SearchPage.this, MainActivity.class);
        intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
        startActivity(intent);
    }

    private void searchPostsByLocation() {
        String latitudeText = latitudeInput.getText().toString().trim();
        String longitudeText = longitudeInput.getText().toString().trim();
        String radiusText = radiusInput.getText().toString().trim();

        if (latitudeText.isEmpty() || longitudeText.isEmpty() || radiusText.isEmpty()) {
            Toast.makeText(this, "Please enter all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double latitude = Double.parseDouble(latitudeText);
            double longitude = Double.parseDouble(longitudeText);
            double radius = Double.parseDouble(radiusText);

            // Clear existing markers
            mapView.getOverlays().clear();

            List<Post> nearbyPosts = dbHelper.getPostsByProximity(latitude, longitude, radius);
            if (nearbyPosts.isEmpty()) {
                Toast.makeText(this, "No posts found near this location.", Toast.LENGTH_SHORT).show();
            } else {
                for (Post post : nearbyPosts) {
                    if (isValidGeoPoint(post.getLatitude(), post.getLongitude())) {
                        Marker postMarker = new Marker(mapView);
                        GeoPoint postPoint = new GeoPoint(post.getLatitude(), post.getLongitude());
                        postMarker.setPosition(postPoint);
                        postMarker.setTitle(post.getPostName());
                        postMarker.setSubDescription(post.getPostDescription());
                        postMarker.setSnippet("Posted by: " + post.getPostUsername());
                        mapView.getOverlays().add(postMarker);
                    }
                }
            }

            // Center map to the search location
            GeoPoint searchCenter = new GeoPoint(latitude, longitude);
            mapView.getController().setCenter(searchCenter);
            mapView.getController().setZoom(15.0); // Reasonable zoom level
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input. Please enter valid numbers.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setDefaultMapView() {
        GeoPoint defaultPoint = new GeoPoint(43.9448472, -78.8917028); // Example default location
        mapView.getController().setZoom(12.0); // Default zoom level
        mapView.getController().setCenter(defaultPoint);
    }

    private boolean isValidGeoPoint(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }

    private void navigateToUserProfile(String viewedUsername, String loggedInUsername) {
        Intent intent = new Intent(SearchPage.this, ViewUserProfile.class);
        intent.putExtra("VIEWED_USERNAME", viewedUsername);
        intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
        startActivity(intent);
    }

    public void moveToCreatePost(View view) {
        if (loggedInUsername.isEmpty()) {
            Toast.makeText(this, "You are not signed in.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(SearchPage.this, NewPost.class);
        intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
        startActivity(intent);
    }

    public void moveToMyProfile(View view) {
        if (loggedInUsername.isEmpty()) {
            Toast.makeText(this, "You are not signed in.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(SearchPage.this, MyProfile.class);
        intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
        startActivity(intent);
    }

    public void moveToHome(View view) {
        Intent intent = new Intent(SearchPage.this, MainActivity.class);
        intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
        startActivity(intent);
    }

    public void moveToNotification(View view) {
        if (loggedInUsername.isEmpty()) {
            Toast.makeText(this, "You are not signed in.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(SearchPage.this, NotificationPage.class);
        intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
        startActivity(intent);
    }

    public void moveToPostProfile(String postUsername) {
        Intent intent = new Intent(SearchPage.this, ViewUserProfile.class);
        intent.putExtra("VIEWED_USERNAME", postUsername);
        intent.putExtra(LogIn.EXTRA_USERNAME, loggedInUsername);
        startActivity(intent);
    }


}
