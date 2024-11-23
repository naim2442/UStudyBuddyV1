package com.example.ustudybuddyv1.Fragment;

import android.os.Bundle;
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

public class AllStudyGroupsFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<StudyGroup> allStudyGroups = new ArrayList<>();
    private LatestGroupsAdapter adapter;
    private TextView userLocationTextView;

    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_study_groups, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_all_study_groups);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        userLocationTextView = view.findViewById(R.id.user_location);

        // Get the current user ID from Firebase Auth
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch user's state from the database
        fetchUserLocation();

        // Fetch all study groups
        fetchAllStudyGroups();

        return view;
    }

    private void fetchUserLocation() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("locationPreference")) {
                    String state = snapshot.child("locationPreference").getValue(String.class);
                    if (state != null) {
                        userLocationTextView.setText(state);
                    } else {
                        userLocationTextView.setText("Location not available.");
                    }
                } else {
                    userLocationTextView.setText("Location not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                userLocationTextView.setText("Error fetching location.");
            }
        });
    }

    private void fetchAllStudyGroups() {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("study_groups");
        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    StudyGroup group = groupSnapshot.getValue(StudyGroup.class);
                    if (group != null) {
                        allStudyGroups.add(group);
                    }
                }

                // Initialize adapter with all groups
                adapter = new LatestGroupsAdapter(allStudyGroups, currentUserId);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
