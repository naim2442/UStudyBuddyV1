package com.example.ustudybuddyv1.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ustudybuddyv1.Model.StudyGroup;
import com.example.ustudybuddyv1.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudyGroupCRUDActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText groupNameInput, groupSubjectInput, groupDescriptionInput, groupDateInput, groupTimeInput;
    private TextView locationDisplay;
    private ChipGroup tagsChipGroup; // ChipGroup to display chips dynamically
    private GoogleMap googleMap;
    private double selectedLatitude, selectedLongitude;
    private String selectedLocationName = "Not set"; // default location text
    private static final double DEFAULT_LAT = 3.0736; // Example lat
    private static final double DEFAULT_LNG = 101.6073; // Example lng
    private List<String> customTags = new ArrayList<>(); // Store custom tags

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_study_group);

        // Initialize views
        groupNameInput = findViewById(R.id.group_name_input);
        groupSubjectInput = findViewById(R.id.group_subject_input);
        groupDescriptionInput = findViewById(R.id.group_description_input);
        groupDateInput = findViewById(R.id.group_date_input);  // Date input
        groupTimeInput = findViewById(R.id.group_time_input);  // Time input
        locationDisplay = findViewById(R.id.location_display);
        tagsChipGroup = findViewById(R.id.tags_container); // ChipGroup to hold tags

        // Set up map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Button to create group
        Button createGroupButton = findViewById(R.id.create_group_button);
        createGroupButton.setOnClickListener(v -> createGroup());

        // Button for adding custom tags
        FloatingActionButton addTagButton = findViewById(R.id.add_tag_button);

        addTagButton.setOnClickListener(v -> showAddTagDialog());

        // Set up initial map view
        selectedLatitude = DEFAULT_LAT;
        selectedLongitude = DEFAULT_LNG;

        // Add time picker to the groupTimeInput field
        groupTimeInput.setOnClickListener(v -> showTimePicker());

        // Add Date Picker
        groupDateInput.setOnClickListener(v -> showDatePicker());
    }

    private void showTimePicker() {
        // Get current time to set default time
        int hour = 12; // Default hour
        int minute = 0; // Default minute

        // If the groupTimeInput has a value, use it as the default time
        String currentTime = groupTimeInput.getText().toString();
        if (!currentTime.isEmpty()) {
            String[] timeParts = currentTime.split(":");
            if (timeParts.length == 2) {
                hour = Integer.parseInt(timeParts[0]);
                minute = Integer.parseInt(timeParts[1]);
            }
        }

        // Create a TimePickerDialog to pick time
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minuteOfHour) -> {
                    // Format the time as HH:mm
                    String formattedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    groupTimeInput.setText(formattedTime);  // Set the selected time in the EditText
                },
                hour, minute, true); // true for 24-hour format

        // Show the time picker dialog
        timePickerDialog.show();
    }

    private void showDatePicker() {
        // Get current date to set default date
        int year = 2024;  // Default year
        int month = 11;   // Default month (December)
        int day = 1;      // Default day

        // If the groupDateInput has a value, use it as the default date
        String currentDate = groupDateInput.getText().toString();
        if (!currentDate.isEmpty()) {
            String[] dateParts = currentDate.split("-");
            if (dateParts.length == 3) {
                year = Integer.parseInt(dateParts[0]);
                month = Integer.parseInt(dateParts[1]) - 1; // Month is 0-based
                day = Integer.parseInt(dateParts[2]);
            }
        }

        // Create a DatePickerDialog to pick the date
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    // Format the date as yyyy-MM-dd
                    String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth);
                    groupDateInput.setText(formattedDate);  // Set the selected date in the EditText
                },
                year, month, day); // Initial date values

        // Show the date picker dialog
        datePickerDialog.show();
    }



    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Set initial map location
        LatLng initialLocation = new LatLng(DEFAULT_LAT, DEFAULT_LNG);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15));

        // Add marker for selected location
        googleMap.addMarker(new MarkerOptions().position(initialLocation).title("Select Location"));

        // On map click, set marker and get coordinates
        googleMap.setOnMapClickListener(latLng -> {
            googleMap.clear(); // Clear previous markers
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));

            selectedLatitude = latLng.latitude;
            selectedLongitude = latLng.longitude;

            // Decode lat/long into location name using Geocoder
            decodeLocation(selectedLatitude, selectedLongitude);
        });
    }

    private void decodeLocation(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(this);
            List<android.location.Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                android.location.Address address = addresses.get(0);
                StringBuilder addressString = new StringBuilder();

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressString.append(address.getAddressLine(i)).append(", ");
                }

                // Remove last comma and space
                selectedLocationName = addressString.toString().trim();
                if (selectedLocationName.endsWith(",")) {
                    selectedLocationName = selectedLocationName.substring(0, selectedLocationName.length() - 1);
                }

                // Display location name
                locationDisplay.setText("Selected Location: " + selectedLocationName);
            } else {
                selectedLocationName = "Unable to get location name";
                locationDisplay.setText("Selected Location: " + selectedLocationName);
                Toast.makeText(this, "Unable to get location name", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            selectedLocationName = "Error decoding location";
            locationDisplay.setText("Selected Location: " + selectedLocationName);
            Toast.makeText(this, "Error decoding location", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddTagDialog() {
        // Create an input field for the user to enter a custom tag
        final EditText tagInput = new EditText(this);
        tagInput.setHint("Enter your tag");

        // Show the dialog to input a tag
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a Custom Tag")
                .setView(tagInput)
                .setPositiveButton("Done", (dialog, which) -> {
                    String tag = tagInput.getText().toString().trim();
                    if (!tag.isEmpty()) {
                        customTags.add(tag);
                        updateTagsDisplay();
                    } else {
                        Toast.makeText(this, "Tag cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateTagsDisplay() {
        // Clear the current chip group
        tagsChipGroup.removeAllViews();

        // Add chips for each tag in the customTags list
        for (String tag : customTags) {
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(view -> {
                // Remove the tag when the close icon is clicked
                customTags.remove(tag);
                updateTagsDisplay(); // Refresh the display
            });

            tagsChipGroup.addView(chip); // Add chip to ChipGroup
        }
    }

    private void createGroup() {
        String groupName = groupNameInput.getText().toString().trim();
        String groupSubject = groupSubjectInput.getText().toString().trim();
        String groupDescription = groupDescriptionInput.getText().toString().trim();
        String groupDate = groupDateInput.getText().toString().trim();
        String groupTime = groupTimeInput.getText().toString().trim();

        // Combine date and time into a single string (if both fields are filled)
        String dateTime = groupDate + " " + groupTime;

        // Create the StudyGroup object
        StudyGroup group = new StudyGroup();
        group.setGroupName(groupName);
        group.setSubject(groupSubject);
        group.setDescription(groupDescription);
        group.setLocation(selectedLatitude + "," + selectedLongitude); // Store coordinates
        group.setDecodedLocationName(selectedLocationName);
        group.setTags(customTags); // Use custom tags list
        group.setDateTime(dateTime);  // Set the combined dateTime

        // Set the creatorId to the current user's UID
        String creatorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        group.setCreatorId(creatorId); // Add creatorId to the group

        // Pass to the confirmation activity
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra("studyGroup", group);
        startActivity(intent);
    }
}
