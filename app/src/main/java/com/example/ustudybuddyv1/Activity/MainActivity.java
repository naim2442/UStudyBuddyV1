package com.example.ustudybuddyv1.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import com.example.ustudybuddyv1.R;


import com.google.android.material.bottomnavigation.BottomNavigationView;

// Import your fragment classes
import com.example.ustudybuddyv1.Fragment.HomeFragment;
import com.example.ustudybuddyv1.Fragment.GroupFragment;
import com.example.ustudybuddyv1.Fragment.LocationFragment;
import com.example.ustudybuddyv1.Fragment.FolderFragment;
import com.example.ustudybuddyv1.Fragment.ProfileFragment;


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
