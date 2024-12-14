package com.example.ustudybuddyv1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ustudybuddyv1.Model.StudyGroup;
import com.example.ustudybuddyv1.Model.User;  // Assuming User model exists
import com.example.ustudybuddyv1.R;
import com.example.ustudybuddyv1.databinding.ActivityGroupDetailsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import java.util.Objects;

public class GroupDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityGroupDetailsBinding binding;
    private GoogleMap map;
    private StudyGroup studyGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate layout using ViewBinding
        binding = ActivityGroupDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        // Fetch the group data from intent
        studyGroup = (StudyGroup) getIntent().getSerializableExtra("studyGroup");

        if (studyGroup != null) {
            setupUI();
        }

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // This will trigger onMapReady when the map is ready
        }

        // Handle Join button click
        binding.buttonJoinGroup.setOnClickListener(v -> joinGroup());
    }

    private void setupUI() {
        // Set group details
        binding.textGroupName.setText(studyGroup.getGroupName());
        binding.textGroupSubject.setText(studyGroup.getSubject());
        binding.textGroupDescription.setText(studyGroup.getDescription());
        binding.textGroupDatetime.setText(studyGroup.getDateTime());

        // Set tags
        if (studyGroup.getTags() != null && !studyGroup.getTags().isEmpty()) {
            StringBuilder tagsText = new StringBuilder("Tags: ");
            for (String tag : studyGroup.getTags()) {
                tagsText.append(tag).append(", ");
            }
            // Remove the last comma
            tagsText.setLength(tagsText.length() - 2);
            binding.textGroupTags.setText(tagsText.toString());
        } else {
            binding.textGroupTags.setText("No tags available");
        }

        // Fetch creator's name and course from User model using Firebase
        String creatorId = studyGroup.getCreatorId();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(creatorId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User creator = dataSnapshot.getValue(User.class);
                if (creator != null) {
                    // Set creator's name and course
                    binding.textGroupCreator.setText(creator.getName());  // Assuming getName() returns creator's name
                    binding.textGroupCourse.setText(creator.getCourse());  // Assuming getCourse() returns creator's course
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("GroupDetailsActivity", "Failed to fetch creator's details.", databaseError.toException());
            }
        });

        // Decoded location or fallback to location if decoding is unavailable
        String locationName = studyGroup.getDecodedLocationName() != null
                ? studyGroup.getDecodedLocationName()
                : studyGroup.getLocation();
        binding.textGroupLocation.setText(locationName);

        // Set the date and time
        if (studyGroup.getDateTime() != null) {
            binding.textGroupDatetime.setText(studyGroup.getDateTime());  // Assuming you have a TextView with id textGroupDateTime
        }

        // Check if the user is the creator or already a member
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        // Check if the user is the group creator
        if (studyGroup.getCreatorId().equals(userId)) {
            // Hide both Join and Leave buttons for the creator
            binding.buttonJoinGroup.setVisibility(View.GONE);
            binding.buttonLeaveGroup.setVisibility(View.GONE);
        } else if (studyGroup.isMember(userId)) {
            // If the user is a member, show the Leave button and hide the Join button
            binding.buttonJoinGroup.setVisibility(View.GONE);
            binding.buttonLeaveGroup.setVisibility(View.VISIBLE);
        } else {
            // If the user is not a member, show the Join button and hide the Leave button
            binding.buttonJoinGroup.setVisibility(View.VISIBLE);
            binding.buttonLeaveGroup.setVisibility(View.GONE);
        }

        // Handle Leave Group button click
        binding.buttonLeaveGroup.setOnClickListener(v -> leaveGroup());
    }

    private void joinGroup() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Add the user to the group and update Firebase
        studyGroup.joinGroup(userId);
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("study_groups").child(studyGroup.getGroupId());
        groupRef.setValue(studyGroup).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "You joined the group!", Toast.LENGTH_SHORT).show();
                binding.buttonJoinGroup.setVisibility(View.GONE);  // Hide the button after joining
            } else {
                Toast.makeText(this, "Failed to join the group.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void leaveGroup() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Create an AlertDialog to confirm leaving the group
        new AlertDialog.Builder(this)
                .setTitle("Confirm Leave")
                .setMessage("Are you sure you want to leave this group?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Remove the user from the group
                    studyGroup.leaveGroup(userId);
                    DatabaseReference groupRef = FirebaseDatabase.getInstance()
                            .getReference("study_groups")
                            .child(studyGroup.getGroupId());

                    groupRef.setValue(studyGroup).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "You left the group!", Toast.LENGTH_SHORT).show();
                            binding.buttonLeaveGroup.setVisibility(View.GONE); // Hide leave button
                            binding.buttonJoinGroup.setVisibility(View.VISIBLE); // Show join button
                        } else {
                            Toast.makeText(this, "Failed to leave the group.", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Do nothing if user clicks "No"
                    dialog.dismiss();
                })
                .create()
                .show(); // Show the dialog
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        if (studyGroup != null) {
            String location = studyGroup.getLocation();  // Access location only if studyGroup is not null
            if (location != null) {
                // Handle the map setup using the location
            } else {
                // Handle case where location is null (optional)
            }
            // Add a pin for the group location
            if (studyGroup.getLocation() != null ) {
                String[] latLng = studyGroup.getLocation().split(",");
                if (latLng.length == 2) {
                    double latitude = Double.parseDouble(latLng[0]);
                    double longitude = Double.parseDouble(latLng[1]);
                    LatLng groupLocation = new LatLng(latitude, longitude);

                    map.addMarker(new MarkerOptions().position(groupLocation).title(studyGroup.getGroupName()));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(groupLocation, 15f));
                }
            }
        }

        // Disable scrolling for the map
        map.getUiSettings().setScrollGesturesEnabled(false);
    }
}
