package com.example.ustudybuddyv1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ustudybuddyv1.Model.User;
import com.example.ustudybuddyv1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupDetailsActivity extends AppCompatActivity {

    private Spinner universitySpinner, courseSpinner, locationPreferenceSpinner;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.university_details);

        // Get the passed data
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        String studentId = intent.getStringExtra("studentId");

        // Initialize views and spinners
        universitySpinner = findViewById(R.id.university_spinner);
        courseSpinner = findViewById(R.id.course_spinner);
        locationPreferenceSpinner = findViewById(R.id.location_spinner);
        signupButton = findViewById(R.id.signup_button);

        setUpSpinners();

        signupButton.setOnClickListener(view -> saveDetailsAndCompleteSignup(name, email, studentId, password));
    }

    private void setUpSpinners() {
        // Mock data for the spinners
        String[] universities = {"UiTM", "MSU", "KUIS", "UNISEL", "POLITEKNIK"};
        ArrayAdapter<String> universityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, universities);
        universitySpinner.setAdapter(universityAdapter);

        String[] courses = {"Computer Science", "Information Technology", "Business Management", "Engineering", "Psychology"};
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, courses);
        courseSpinner.setAdapter(courseAdapter);

        String[] locations = {"SEKSYEN 7", "SEKSYEN 13", "SETIA ALAM", "KOTA KEMUNING", "TTDI JAYA"};
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locations);
        locationPreferenceSpinner.setAdapter(locationAdapter);
    }

    private void saveDetailsAndCompleteSignup(String name, String email, String studentId, String password) {
        String university = universitySpinner.getSelectedItem().toString();
        String course = courseSpinner.getSelectedItem().toString();
        String locationPreference = locationPreferenceSpinner.getSelectedItem().toString();

        if (university.isEmpty() || course.isEmpty() || locationPreference.isEmpty()) {
            Toast.makeText(SignupDetailsActivity.this, "All fields must be selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Save all details
            userRef.setValue(new User(userId, name, email, password, studentId, university, course, locationPreference))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SignupDetailsActivity.this, "Registration Completed!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupDetailsActivity.this, LocationDetailsActivity.LoginActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SignupDetailsActivity.this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(SignupDetailsActivity.this, "User not authenticated. Please sign in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignupDetailsActivity.this, LocationDetailsActivity.LoginActivity.class));
        }
    }
}
