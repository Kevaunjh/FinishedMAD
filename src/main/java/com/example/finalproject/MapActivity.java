package com.example.finalproject;

import android.os.Bundle;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize OSMDroid configuration
        Configuration.getInstance().setUserAgentValue(getApplicationContext().getPackageName());

        // Set language to English globally
        Locale.setDefault(Locale.ENGLISH);

        setContentView(R.layout.activity_map);

        // Initialize the map view
        mapView = findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true); // Enable pinch-to-zoom and other multi-touch gestures
        mapView.setTileSource(TileSourceFactory.MAPNIK); // Default OpenStreetMap tile source

        // Retrieve the list of posts passed from SearchPage
        List<Post> posts = getIntent().getParcelableArrayListExtra("posts");

        // Add markers to the map or show a default view
        if (posts != null && !posts.isEmpty()) {
            addMarkersToMap(posts);
        } else {
            showDefaultView();
            Toast.makeText(this, "No posts available to display on the map.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addMarkersToMap(List<Post> posts) {
        boolean validMarkerAdded = false;

        for (Post post : posts) {
            if (isValidGeoPoint(post.getLatitude(), post.getLongitude())) {
                GeoPoint point = new GeoPoint(post.getLatitude(), post.getLongitude());
                Marker marker = new Marker(mapView);
                marker.setPosition(point);
                marker.setTitle(post.getPostName());
                marker.setSubDescription(post.getPostDescription());
                mapView.getOverlays().add(marker);

                validMarkerAdded = true;
            }
        }

        if (validMarkerAdded) {
            // Center the map on the first valid post and adjust zoom
            GeoPoint startPoint = new GeoPoint(posts.get(0).getLatitude(), posts.get(0).getLongitude());
            mapView.getController().setZoom(15.0); // Adjust zoom level
            mapView.getController().setCenter(startPoint);
        } else {
            // If no valid markers were added, show the default view
            showDefaultView();
            Toast.makeText(this, "No valid post locations available for the map.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDefaultView() {
        // Center the map on a default location if no posts are available
        GeoPoint defaultPoint = new GeoPoint(43.9448472, -78.8917028); // Default: Ontario Tech University
        mapView.getController().setZoom(12.0); // Adjust zoom level
        mapView.getController().setCenter(defaultPoint);
    }

    private boolean isValidGeoPoint(double latitude, double longitude) {
        // Check if latitude and longitude are within valid ranges
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume(); // Resume the map view
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause(); // Pause the map view
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDetach(); // Cleanup resources
    }
}
