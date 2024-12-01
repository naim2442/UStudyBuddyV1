package com.example.ustudybuddyv1.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ustudybuddyv1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class StudentDetailsEditActivity extends AppCompatActivity {

    private EditText cgpaEditText, semesterYearEditText;
    private Spinner highestEducationLevelSpinner;
    private Button saveButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details_edit);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize EditTexts and Button
        cgpaEditText = findViewById(R.id.cgpa_edit_text);
        semesterYearEditText = findViewById(R.id.semester_year_edit_text);
        saveButton = findViewById(R.id.save_button);

        // Initialize the Spinner for Education Level
        highestEducationLevelSpinner = findViewById(R.id.highest_education_level_spinner);

        // Education Levels Array
        String[] educationLevels = {
                "Diploma in Architecture",
                "Diploma in Computer Science",
                "Diploma in Engineering",
                "Degree in Accounting",
                "Degree in Computer Science",
                "Degree in Engineering",
                "Degree in Finance",
                "Degree in Netcentric Computing",
                "Degree in Psychology",
                "Foundation in Science",
                "Master in Engineering",
                "Master in Computer Science"
        };

        // Sort the array alphabetically
        Arrays.sort(educationLevels);

        // Create an ArrayAdapter to populate the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, educationLevels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        highestEducationLevelSpinner.setAdapter(adapter);

        saveButton.setOnClickListener(v -> saveStudentDetails());
    }

    private void saveStudentDetails() {
        String cgpa = cgpaEditText.getText().toString().trim();
        String highestEducationLevel = highestEducationLevelSpinner.getSelectedItem().toString();
        String semesterYear = semesterYearEditText.getText().toString().trim();

        if (cgpa.isEmpty() || highestEducationLevel.isEmpty() || semesterYear.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        // Update user details in the Firebase Realtime Database
        databaseReference.child(userId).child("cgpa").setValue(cgpa);
        databaseReference.child(userId).child("highestEducationLevel").setValue(highestEducationLevel);
        databaseReference.child(userId).child("semesterYear").setValue(semesterYear);

        Toast.makeText(this, "Student details updated successfully", Toast.LENGTH_SHORT).show();
        finish();  // Close the activity after saving
    }
}
