package com.example.ustudybuddyv1;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StudyGroupDetailActivity extends AppCompatActivity {

    private TextView groupNameTextView, groupLocationTextView, groupCreatorTextView, groupMembersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group_detail);

        // Initialize views
        groupNameTextView = findViewById(R.id.text_group_name);
        groupLocationTextView = findViewById(R.id.text_group_location);
        groupCreatorTextView = findViewById(R.id.text_group_creator);
        groupMembersTextView = findViewById(R.id.text_group_members);

        // Get the group data passed from the previous activity/fragment
        StudyGroup group = (StudyGroup) getIntent().getSerializableExtra("group");

        if (group != null) {
            // Populate the UI with group details
            groupNameTextView.setText(group.getGroupName());
            groupLocationTextView.setText(group.getLocation());

            // Show the creator's ID or replace it with a placeholder if necessary
            String creatorInfo = "Created by: " + group.getCreatorId();
            groupCreatorTextView.setText(creatorInfo);

            // Display members if the list is not null or empty
            if (group.getMembers() != null && !group.getMembers().isEmpty()) {
                StringBuilder membersList = new StringBuilder();
                for (String member : group.getMembers()) {
                    membersList.append("- ").append(member).append("\n");
                }
                groupMembersTextView.setText(membersList.toString().trim());
            } else {
                groupMembersTextView.setText("No members have joined this group yet.");
            }
        } else {
            // Log the issue or show a Toast if the object is null
            Toast.makeText(this, "Failed to load group details. Please try again.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if data is not available
        }
    }
}
