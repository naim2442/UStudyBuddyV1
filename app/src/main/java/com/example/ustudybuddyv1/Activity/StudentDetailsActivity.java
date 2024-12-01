package com.example.ustudybuddyv1.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ustudybuddyv1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentDetailsActivity extends AppCompatActivity {

    private ImageView profilePicture;
    private TextView fullNameTextView, studentIdTextView, emailTextView, universityTextView, courseTextView;
    private TextView cgpaTextView, highestEducationLevelTextView, semesterYearTextView; // New TextViews
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details); // Use your provided layout

        // Initialize views
        profilePicture = findViewById(R.id.image_profile_picture);
        fullNameTextView = findViewById(R.id.full_name_text_view);
        studentIdTextView = findViewById(R.id.student_id_text_view);
        emailTextView = findViewById(R.id.email_text_view);
        universityTextView = findViewById(R.id.university_text_view);
        courseTextView = findViewById(R.id.course_text_view);

        // New views for CGPA, highest education level, and semester year
        cgpaTextView = findViewById(R.id.cgpa_text_view);
        highestEducationLevelTextView = findViewById(R.id.highest_education_level_text_view);
        semesterYearTextView = findViewById(R.id.semester_year_text_view);

        // Get the USER_ID passed from ChatAdapter
        String userId = getIntent().getStringExtra("USER_ID");

        if (userId != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            fetchStudentDetails();
        } else {
            Toast.makeText(this, "No User ID provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchStudentDetails() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Fetching the details
                    String name = snapshot.child("name").getValue(String.class);
                    String studentId = snapshot.child("studentId").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String university = snapshot.child("university").getValue(String.class);
                    String course = snapshot.child("course").getValue(String.class);
                    String profileImageUrl = snapshot.child("profilePicture").getValue(String.class);

                    // Fetching the new fields
                    String cgpa = snapshot.child("cgpa").getValue(String.class);
                    String highestEducationLevel = snapshot.child("highestEducationLevel").getValue(String.class);
                    String semesterYear = snapshot.child("semesterYear").getValue(String.class);

                    // Setting the details to views
                    fullNameTextView.setText(name != null ? name : "N/A");
                    studentIdTextView.setText(studentId != null ? studentId : "N/A");
                    emailTextView.setText(email != null ? email : "N/A");
                    universityTextView.setText(university != null ? university : "N/A");
                    courseTextView.setText(course != null ? course : "N/A");

                    // Setting the new fields
                    cgpaTextView.setText(cgpa != null ? cgpa : "CGPA: N/A");
                    highestEducationLevelTextView.setText(highestEducationLevel != null ?  highestEducationLevel : "Highest Education Level: N/A");
                    semesterYearTextView.setText(semesterYear != null ?  semesterYear : "Semester Year: N/A");

                    // Loading profile picture using Glide
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(StudentDetailsActivity.this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.ic_profile)
                                .into(profilePicture);
                    } else {
                        profilePicture.setImageResource(R.drawable.ic_profile);
                    }
                } else {
                    Toast.makeText(StudentDetailsActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentDetailsActivity.this, "Failed to fetch details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
