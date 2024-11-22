package com.example.ustudybuddyv1.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ustudybuddyv1.R;
import com.example.ustudybuddyv1.Model.StudyGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class StudyGroupDetailActivity extends AppCompatActivity {

    private TextView groupNameTextView, groupLocationTextView, groupMembersTextView;
    private Button joinGroupButton;
    private DatabaseReference studyGroupsRef;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group_detail);

        // Firebase initialization
        studyGroupsRef = FirebaseDatabase.getInstance().getReference("study_groups");
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Initialize views
        groupNameTextView = findViewById(R.id.text_group_name);
        groupLocationTextView = findViewById(R.id.text_group_location);
        groupMembersTextView = findViewById(R.id.text_group_members);
        joinGroupButton = findViewById(R.id.button_join_group);

        // Get the group data passed from the previous activity
        StudyGroup group = (StudyGroup) getIntent().getSerializableExtra("group");

        if (group != null) {
            displayGroupDetails(group);

            // Join the group when the button is clicked
            joinGroupButton.setOnClickListener(v -> joinStudyGroup(group.getGroupId()));
        } else {
            Toast.makeText(this, "Failed to load group details.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Display the group details
    private void displayGroupDetails(StudyGroup group) {
        groupNameTextView.setText(group.getGroupName());
        groupLocationTextView.setText(group.getLocation());

        if (group.getMembers() != null && !group.getMembers().isEmpty()) {
            StringBuilder membersList = new StringBuilder();
            for (String member : group.getMembers()) {
                membersList.append("- ").append(member).append("\n");
            }
            groupMembersTextView.setText(membersList.toString().trim());
        } else {
            groupMembersTextView.setText("No members have joined this group yet.");
        }
    }

    // Join the study group
    private void joinStudyGroup(String groupId) {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("study_groups").child(groupId);

        // Retrieve the group
        groupRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StudyGroup group = task.getResult().getValue(StudyGroup.class);

                if (group != null) {
                    // Check if the user is already a member
                    if (group.getMembers() != null && !group.getMembers().contains(currentUserId)) {
                        group.getMembers().add(currentUserId);  // Add the user to the group
                        group.setMembersCount(group.getMembersCount() + 1);  // Increment member count

                        // Update the group in Firebase
                        groupRef.setValue(group)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Joined the group successfully!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to join the group", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "You are already a member of this group.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Failed to retrieve group data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
