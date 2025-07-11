package com.example.ustudybuddyv1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ustudybuddyv1.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Make sure you have a layout for the splash screen

        // Delay for a short time, then start the LoginActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LocationDetailsActivity.LoginActivity.class);
            startActivity(intent);
            finish(); // Close the SplashActivity
        }, 2000); // 2000 milliseconds = 2 seconds
    }
}
