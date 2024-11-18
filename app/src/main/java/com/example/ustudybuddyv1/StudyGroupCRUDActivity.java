package com.example.ustudybuddyv1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudyGroupCRUDActivity extends AppCompatActivity {

    private EditText editGroupName, editLocation;
    private Button btnSaveGroup;
    private DatabaseReference studyGroupRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_study_group);

        // Firebase initialization
        studyGroupRef = FirebaseDatabase.getInstance().getReference("study_groups");

        // UI components
        editGroupName = findViewById(R.id.edit_group_name);
        editLocation = findViewById(R.id.edit_location);
        btnSaveGroup = findViewById(R.id.btn_create_group);

        // Save group setup
        btnSaveGroup.setOnClickListener(v -> saveStudyGroup());
    }

    // Save study group data
    private void saveStudyGroup() {
        String groupName = editGroupName.getText().toString().trim();
        String location = editLocation.getText().toString().trim();

        // Validate inputs
        if (groupName.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create and save group in Firebase
        String groupId = studyGroupRef.push().getKey();
        if (groupId != null) {
            // Use simplified constructor with only group name, location, createdBy, and members count
            StudyGroup newGroup = new StudyGroup(groupName, location, FirebaseAuth.getInstance().getCurrentUser().getUid(), 0);

            // Save to Firebase
            studyGroupRef.child(groupId).setValue(newGroup)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Group created successfully!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);  // Return success result to notify the fragment
                        finish();  // Close the activity
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to create group", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
