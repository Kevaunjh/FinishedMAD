package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainMenu extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "com.example.finalproject.EXTRA_USERNAME"; // Ensure this constant is defined

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    public void loginView(View view) {
        Intent intent = new Intent(MainMenu.this, LogIn.class);
        startActivity(intent);
    }

    public void signupView(View view) {
        Intent intent = new Intent(MainMenu.this, SignUp.class);
        startActivity(intent);
    }

    public void moveToMainPage(View view) {
        Intent intent = new Intent(MainMenu.this, MainActivity.class);
        intent.putExtra(EXTRA_USERNAME, "");
        startActivity(intent);
    }
}
