package com.example.ustudybuddyv1.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ustudybuddyv1.Helper.FirebaseStudyGroupHelper;
import com.example.ustudybuddyv1.Helper.FirebaseStorageHelper;
import com.example.ustudybuddyv1.Model.StudyGroup;
import com.example.ustudybuddyv1.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ConfirmationActivity extends AppCompatActivity {

    private TextView groupNameDisplay, groupSubjectDisplay, groupDescriptionDisplay, locationDisplay, tagsDisplay, dateTimeDisplay;
    private ImageView groupImageDisplay;
    private Button discardButton, confirmButton;
    private StudyGroup studyGroup;
    private Uri imageUri; // Uri of the image selected by the user

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        // Initialize views
        groupNameDisplay = findViewById(R.id.group_name_display_confirm);
        groupSubjectDisplay = findViewById(R.id.group_subject_display_confirm);
        groupDescriptionDisplay = findViewById(R.id.group_description_display_confirm);
        locationDisplay = findViewById(R.id.location_display_confirm);
        tagsDisplay = findViewById(R.id.tags_display_confirm);
        dateTimeDisplay = findViewById(R.id.date_time_display_confirm);  // New TextView for displaying date and time
        groupImageDisplay = findViewById(R.id.group_image_display_confirm);
        discardButton = findViewById(R.id.discard_button_confirm);
        confirmButton = findViewById(R.id.confirm_button_confirm);

        // Get the StudyGroup object passed from the GroupCreationActivity
        studyGroup = (StudyGroup) getIntent().getSerializableExtra("studyGroup");

        // Display group details
        groupNameDisplay.setText("Group Name: " + studyGroup.getGroupName());
        groupSubjectDisplay.setText("Subject: " + studyGroup.getSubject());
        groupDescriptionDisplay.setText("Description: " + studyGroup.getDescription());
        locationDisplay.setText("Location: " + studyGroup.getDecodedLocationName());

        // Display selected tags
        StringBuilder tags = new StringBuilder("Tags: ");
        for (String tag : studyGroup.getTags()) {
            tags.append(tag).append(" ");
        }
        tagsDisplay.setText(tags.toString().trim());

        // Display the group image or a placeholder
        if (studyGroup.getImageUrl() != null) {
            // If there is an image URL, load the image here
            Glide.with(this)
                    .load(studyGroup.getImageUrl())  // Load the image URL from the StudyGroup object
                    .into(groupImageDisplay);  // Set it into the ImageView
        } else {
            groupImageDisplay.setImageResource(R.drawable.logo);  // Use placeholder if no image URL
        }

        // Display the formatted date and time
        String dateTime = studyGroup.getDateTime();
        if (dateTime != null && !dateTime.isEmpty()) {
            // Format the date and time for display
            String formattedDateTime = formatDateTime(dateTime);
            dateTimeDisplay.setText("Date & Time: " + formattedDateTime);
        }

        // Set up Discard button (go back to the creation activity with existing data)
        discardButton.setOnClickListener(v -> {
            Intent returnIntent = new Intent(ConfirmationActivity.this, StudyGroupCRUDActivity.class);
            returnIntent.putExtra("studyGroup", studyGroup);
            startActivity(returnIntent);
            finish();
        });

        // Set up Confirm button (save group and proceed)
        confirmButton.setOnClickListener(v -> {
            // Set the creatorId to the current user's UID
            String creatorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            studyGroup.setCreatorId(creatorId); // Adding the creatorId to the studyGroup

            studyGroup.setPublic(true);  // Set this group as public

            // Check if an image is selected, and upload it
            if (imageUri != null) {
                // Use a random name for the image (you could also use the group ID)
                String imageName = studyGroup.getGroupName() + "_image";
                FirebaseStorageHelper.uploadImage(ConfirmationActivity.this, imageUri, imageName, new FirebaseStorageHelper.UploadCallback() {
                    @Override
                    public void onUploadSuccess(String imageUrl) {
                        studyGroup.setImageUrl(imageUrl); // Set the image URL in the model
                        saveGroupToFirebase(); // Save the study group with the image URL
                    }

                    @Override
                    public void onUploadFailure(Exception e) {
                        Toast.makeText(ConfirmationActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // No image selected, save the group without image URL
                saveGroupToFirebase();
            }
        });
    }


    private void saveGroupToFirebase() {

        // Initialize the files node if not already done
        if (studyGroup.getFiles() == null) {
            studyGroup.setFiles(new HashMap<>());  // Ensure files is initialized
        }
        // Save the study group object to Firebase Realtime Database
        FirebaseStudyGroupHelper.saveStudyGroup(studyGroup);

        Toast.makeText(this, "Group created successfully!", Toast.LENGTH_SHORT).show();

        // After saving, navigate to a success or home activity
        Intent intent = new Intent(ConfirmationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Helper method to format date and time for display
    private String formatDateTime(String dateTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a");
            Date date = inputFormat.parse(dateTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;  // Return the original string if parsing fails
        }
    }
}
