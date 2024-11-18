package com.example.ustudybuddyv1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import com.example.ustudybuddyv1.R;


import com.example.ustudybuddyv1.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Import your fragment classes
import com.example.ustudybuddyv1.HomeFragment;
import com.example.ustudybuddyv1.GroupFragment;
import com.example.ustudybuddyv1.LocationFragment;
import com.example.ustudybuddyv1.FolderFragment;
import com.example.ustudybuddyv1.ProfileFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment to Home
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_group) {
                selectedFragment = new GroupFragment();
            } else if (id == R.id.nav_location) {
                selectedFragment = new LocationFragment();
            } else if (id == R.id.nav_folder) {
                selectedFragment = new FolderFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            // Replace the fragment
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });

    }
}
