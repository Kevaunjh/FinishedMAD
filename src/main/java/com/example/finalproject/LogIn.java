package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class LogIn extends AppCompatActivity {

    private DBHelper dbHelper;
    public static final String EXTRA_USERNAME = "com.example.finalproject.USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Edge-to-Edge rendering
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_log_in);

        // Apply window insets for proper padding
        View rootView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(
                    systemBarsInsets.left,
                    systemBarsInsets.top,
                    systemBarsInsets.right,
                    systemBarsInsets.bottom
            );
            return insets;
        });

        // Initialize database helper
        dbHelper = new DBHelper(this);

        // Bind UI elements using consistent IDs
        EditText usernameField = findViewById(R.id.emailText);
        EditText passwordField = findViewById(R.id.passwordText);
        Button loginButton = findViewById(R.id.signupButton);

        // Set login button click listener
        loginButton.setOnClickListener(view -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LogIn.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            } else {
                if (dbHelper.validateUser(username, password)) {
                    Intent intent = new Intent(LogIn.this, MainActivity.class);
                    intent.putExtra(EXTRA_USERNAME, username);
                    Toast.makeText(LogIn.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LogIn.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Navigate to SignUp activity
    public void moveToSignup(View view) {
        Intent intent = new Intent(LogIn.this, SignUp.class);
        startActivity(intent);
    }
}
