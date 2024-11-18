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
            groupCreatorTextView.setText(group.getCreatorId());  // You can replace with the creator's name if you have it
            groupMembersTextView.setText(group.getMembers().toString());  // Display members
        } else {
            // Log the issue or show a Toast if the object is null
            Toast.makeText(this, "Failed to load group details.", Toast.LENGTH_SHORT).show();
        }
    }
}
