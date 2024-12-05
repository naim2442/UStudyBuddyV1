package com.example.ustudybuddyv1.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.Adapter.LatestGroupsAdapter;
import com.example.ustudybuddyv1.Model.StudyGroup;
import com.example.ustudybuddyv1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllRecommendedGroupsFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<StudyGroup> allStudyGroups = new ArrayList<>();
    private List<StudyGroup> recommendedGroups = new ArrayList<>();
    private LatestGroupsAdapter adapter;
    private TextView userLocationTextView;

    private String currentUserId;
    private String userCourse;
    private String userUniversity;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_recommended_groups, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_all_recommended_groups);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        userLocationTextView = view.findViewById(R.id.user_location);

        // Get the current user ID from Firebase Auth
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch user's details (location, course, and university)
        fetchUserDetails();

        return view;
    }

    private void fetchUserDetails() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Fetch location
                    String location = snapshot.child("locationPreference").getValue(String.class);
                    userLocationTextView.setText(location != null ? location : "Location not available.");

                    // Fetch course and university
                    userCourse = snapshot.child("course").getValue(String.class);
                    userUniversity = snapshot.child("university").getValue(String.class);

                    // Fetch all study groups after user data is fetched
                    fetchAllStudyGroups();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                userLocationTextView.setText("Error fetching location.");
            }
        });
    }

    private void fetchAllStudyGroups() {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("study_groups");
        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allStudyGroups.clear();  // Clear previous data before adding new groups

                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    StudyGroup group = groupSnapshot.getValue(StudyGroup.class);
                    if (group != null) {
                        allStudyGroups.add(group);
                        Log.d("StudyGroup", "Group found: " + group.getGroupName());

                        // Check if the group should be recommended based on creator's details
                        checkGroupForRecommendation(group);
                    }
                }

                // If no groups are found, log a message
                if (allStudyGroups.isEmpty()) {
                    Log.d("StudyGroup", "No study groups found in the database.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("FirebaseError", "Error fetching study groups: " + error.getMessage());
            }
        });
    }

    private void checkGroupForRecommendation(StudyGroup group) {
        String creatorId = group.getCreatorId(); // Get the creator's ID from the group
        DatabaseReference creatorRef = FirebaseDatabase.getInstance().getReference("users").child(creatorId);
        creatorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Fetch the creator's course and university
                    String creatorCourse = snapshot.child("course").getValue(String.class);
                    String creatorUniversity = snapshot.child("university").getValue(String.class);

                    // Log for debugging
                    Log.d("CreatorDetails", "Creator ID: " + creatorId);
                    Log.d("CreatorDetails", "Creator Course: " + creatorCourse);
                    Log.d("CreatorDetails", "Creator University: " + creatorUniversity);

                    // Check if the creator's course or university matches the user's course/university
                    if ((creatorCourse != null && creatorCourse.equals(userCourse)) ||
                            (creatorUniversity != null && creatorUniversity.equals(userUniversity))) {
                        // Only add to recommendedGroups if they match the user's course/university
                        recommendedGroups.add(group);
                        Log.d("FilteredGroup", "Group added: " + group.getGroupName());
                    }

                    // Update the RecyclerView after processing all groups
                    updateRecyclerView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error fetching creator details: " + error.getMessage());
            }
        });
    }

    private void updateRecyclerView() {
        // Initialize adapter with recommended groups
        if (recommendedGroups.isEmpty()) {
            Log.d("FilteredGroup", "No recommended groups to display.");
        }

        // Update data in the adapter
        adapter = new LatestGroupsAdapter(recommendedGroups, currentUserId);
        recyclerView.setAdapter(adapter);
    }
}

