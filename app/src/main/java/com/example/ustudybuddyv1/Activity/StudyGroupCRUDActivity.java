package com.example.ustudybuddyv1.Activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ustudybuddyv1.Model.StudyGroup;
import com.example.ustudybuddyv1.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudyGroupCRUDActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText groupNameInput, groupSubjectInput, groupDescriptionInput;
    private TextView locationDisplay;
    private CheckBox tagItt626, tagBackend, tagSemester5;
    private GoogleMap googleMap;
    private double selectedLatitude, selectedLongitude;
    private String selectedLocationName = "Not set"; // default location text
    private static final double DEFAULT_LAT = 3.0736; // Example lat
    private static final double DEFAULT_LNG = 101.6073; // Example lng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_study_group);

        // Initialize views
        groupNameInput = findViewById(R.id.group_name_input);
        groupSubjectInput = findViewById(R.id.group_subject_input);
        groupDescriptionInput = findViewById(R.id.group_description_input);
        locationDisplay = findViewById(R.id.location_display);
        tagItt626 = findViewById(R.id.tag_itt626);
        tagBackend = findViewById(R.id.tag_backend);
        tagSemester5 = findViewById(R.id.tag_semester5);

        // Set up map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Button to create group
        Button createGroupButton = findViewById(R.id.create_group_button);
        createGroupButton.setOnClickListener(v -> createGroup());

        // Set up initial map view
        selectedLatitude = DEFAULT_LAT;
        selectedLongitude = DEFAULT_LNG;
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
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
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

    private void createGroup() {
        String groupName = groupNameInput.getText().toString().trim();
        String groupSubject = groupSubjectInput.getText().toString().trim();
        String groupDescription = groupDescriptionInput.getText().toString().trim();

        List<String> tags = new ArrayList<>();
        if (tagItt626.isChecked()) tags.add("ITT626");
        if (tagBackend.isChecked()) tags.add("Backend");
        if (tagSemester5.isChecked()) tags.add("Semester 5");

        // Create the StudyGroup object
        StudyGroup group = new StudyGroup();
        group.setGroupName(groupName);
        group.setSubject(groupSubject);
        group.setDescription(groupDescription);
        group.setLocation(selectedLatitude + "," + selectedLongitude); // Store coordinates
        group.setDecodedLocationName(selectedLocationName);
        group.setTags(tags);

        // Set the creatorId to the current user's UID
        String creatorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        group.setCreatorId(creatorId); // Add creatorId to the group

        // Pass to the confirmation activity
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra("studyGroup", group);
        startActivity(intent);
    }
}
