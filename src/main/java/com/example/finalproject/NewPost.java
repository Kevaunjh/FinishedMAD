package com.example.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NewPost extends AppCompatActivity {

    private DBHelper dbHelper;
    private EditText nameCreatePost, descriptionCreatePost, latitudeInput, longitudeInput;
    private ImageView imageCreatePost;
    private Uri selectedImageUri;
    private int userId;
    private String username;
    private PostViewModel postViewModel;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result != null && result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        // Request persistent permission for the selected URI
                        getContentResolver().takePersistableUriPermission(
                                selectedImageUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );

                        // Update the ImageView with the selected image
                        imageCreatePost.setImageURI(selectedImageUri);
                        findViewById(R.id.imagePlaceholderText).setVisibility(View.GONE); // Hide placeholder text
                    } else {
                        Toast.makeText(this, "Error getting selected file. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No file selected. Please choose an image.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        // Initialize UI elements
        dbHelper = new DBHelper(this);
        nameCreatePost = findViewById(R.id.nameCreatePost);
        descriptionCreatePost = findViewById(R.id.descriptionCreatePost);
        latitudeInput = findViewById(R.id.latitudeInput); // New input for latitude
        longitudeInput = findViewById(R.id.longitudeInput); // New input for longitude
        imageCreatePost = findViewById(R.id.imageCreatePost);

        username = getIntent().getStringExtra(LogIn.EXTRA_USERNAME);
        userId = (username != null) ? dbHelper.getUserId(username) : -1;

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        if (postViewModel == null) {
            Toast.makeText(this, "Error initializing PostViewModel", Toast.LENGTH_SHORT).show();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button setup
        Button confirmCreatePost = findViewById(R.id.confirmCreatePost);
        Button cancelCreatePost = findViewById(R.id.cancelCreatePost);

        confirmCreatePost.setOnClickListener(v -> createPost());
        cancelCreatePost.setOnClickListener(v -> finish());

        // Set up navbar buttons
        setupNavBar();
    }

    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE); // Ensures only files that can be opened are shown
        if (intent.resolveActivity(getPackageManager()) != null) {
            imagePickerLauncher.launch(intent);
        } else {
            Toast.makeText(this, "No app available to select images.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createPost() {
        String postTitle = nameCreatePost.getText().toString().trim();
        String postDescription = descriptionCreatePost.getText().toString().trim();
        String latitudeText = latitudeInput.getText().toString().trim();
        String longitudeText = longitudeInput.getText().toString().trim();

        if (postTitle.isEmpty() || postDescription.isEmpty() || latitudeText.isEmpty() || longitudeText.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double latitude = Double.parseDouble(latitudeText);
            double longitude = Double.parseDouble(longitudeText);

            String imageUri = selectedImageUri != null ? selectedImageUri.toString() : null;
            Post newPost = new Post(postTitle, postDescription, username, imageUri, latitude, longitude);

            if (postViewModel != null) {
                postViewModel.addPost(newPost);
            }

            dbHelper.addPost(postTitle, postDescription, userId, imageUri, latitude, longitude);

            Toast.makeText(this, "Post created successfully", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupNavBar() {
        ImageButton homeIcon = findViewById(R.id.icon1);
        ImageButton searchIcon = findViewById(R.id.icon2);
        ImageButton createIcon = findViewById(R.id.icon3);
        ImageButton bellIcon = findViewById(R.id.icon4);
        ImageButton userIcon = findViewById(R.id.icon5);

        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(NewPost.this, MainActivity.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, username);
            startActivity(intent);
        });

        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(NewPost.this, SearchPage.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, username);
            startActivity(intent);
        });

        bellIcon.setOnClickListener(v -> {
            Intent intent = new Intent(NewPost.this, NotificationPage.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, username);
            startActivity(intent);
        });

        userIcon.setOnClickListener(v -> {
            if (username == null || username.isEmpty()) {
                Toast.makeText(this, "You are not signed in; this is not accessible.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(NewPost.this, MyProfile.class);
            intent.putExtra(LogIn.EXTRA_USERNAME, username);
            startActivity(intent);
        });
    }

    public void moveBack(View view) {
        Intent intent = new Intent(NewPost.this, MainActivity.class);
        intent.putExtra(LogIn.EXTRA_USERNAME, username);
        startActivity(intent);
    }
}
