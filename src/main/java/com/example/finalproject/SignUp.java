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
import androidx.core.view.WindowInsetsCompat;

public class SignUp extends AppCompatActivity {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set up window insets for edge-to-edge experience
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);

        EditText usernameField = findViewById(R.id.newEmail);
        EditText passwordField = findViewById(R.id.newPassword);
        Button signUpButton = findViewById(R.id.signupButton);

        signUpButton.setOnClickListener(view -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignUp.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.addUser(username, password);
                if (success) {
                    Toast.makeText(SignUp.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SignUp.this, "Email already in use", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void moveToLogIn(View view) {
        Intent intent = new Intent(SignUp.this, LogIn.class);
        startActivity(intent);
    }
}

