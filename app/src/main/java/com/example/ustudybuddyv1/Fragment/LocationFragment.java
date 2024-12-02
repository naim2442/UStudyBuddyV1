package com.example.ustudybuddyv1.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.Manifest;
import android.widget.Toast;

import com.example.ustudybuddyv1.Activity.LocationDetailsActivity;
import com.example.ustudybuddyv1.Adapter.CustomInfoWindowAdapter;
import com.example.ustudybuddyv1.Model.StudyGroup; // Make sure this import is included
import com.example.ustudybuddyv1.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocationFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize the location provider client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Set custom info window adapter
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(inflater));

        // Enable click listener for info window
        mMap.setOnInfoWindowClickListener(marker -> {
            // Get the group ID from marker's tag
            String groupId = (String) marker.getTag();

            if (groupId != null) {
                // Fetch the StudyGroup object from Firebase using groupId
                fetchStudyGroupDetails(groupId);
            } else {
                Toast.makeText(requireContext(), "Group details unavailable", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch and display study groups
        fetchStudyGroups();

        // Get and display user's current location
        displayUserLocation();
    }

    private void fetchStudyGroups() {
        DatabaseReference studyGroupsRef = FirebaseDatabase.getInstance().getReference("study_groups");

        studyGroupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    String groupName = groupSnapshot.child("groupName").getValue(String.class);
                    String locationString = groupSnapshot.child("location").getValue(String.class);
                    String description = groupSnapshot.child("description").getValue(String.class);
                    String groupId = groupSnapshot.child("groupId").getValue(String.class);
                    int membersCount = groupSnapshot.child("membersCount").getValue(Integer.class);

                    if (locationString != null && groupName != null) {
                        String[] latLngParts = locationString.split(",");
                        if (latLngParts.length == 2) {
                            try {
                                double latitude = Double.parseDouble(latLngParts[0].trim());
                                double longitude = Double.parseDouble(latLngParts[1].trim());

                                LatLng groupLocation = new LatLng(latitude, longitude);
                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(groupLocation)
                                        .title(groupName)
                                        .snippet(description + "|" + membersCount + " members"));

                                if (marker != null) {
                                    marker.setTag(groupId); // Set group ID as marker tag
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
    }

    private void fetchStudyGroupDetails(String groupId) {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("study_groups").child(groupId);

        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StudyGroup studyGroup = dataSnapshot.getValue(StudyGroup.class);
                if (studyGroup != null) {
                    // Now pass the StudyGroup to LocationDetailsActivity
                    Intent intent = new Intent(requireContext(), LocationDetailsActivity.class);
                    intent.putExtra("studyGroup", studyGroup);  // Pass StudyGroup object
                    startActivity(intent);
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch group details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to fetch group details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If permissions are not granted, request permissions
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        // Create LatLng for the current location
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        // Add a marker for the user's current location
                        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));

                        // Move the camera to the user's current location
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Unable to get current location", Toast.LENGTH_SHORT).show());
    }
}
