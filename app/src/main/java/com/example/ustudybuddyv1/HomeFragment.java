package com.example.ustudybuddyv1;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewLatestGroups;
    private TextView welcomeMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        welcomeMessage = view.findViewById(R.id.welcome_message);
        recyclerViewLatestGroups = view.findViewById(R.id.recycler_view_latest_groups);

        // Set up layout manager for RecyclerView
        recyclerViewLatestGroups.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));



        // Fetch user data to update welcome message
        fetchUserData();

        // Fetch public groups to display in the latest groups RecyclerView
        fetchPublicGroups();

        return view;
    }

    private void fetchUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("username").getValue(String.class);
                    updateWelcomeMessage(userName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void updateWelcomeMessage(String userName) {
        welcomeMessage.setText("Welcome back, " + userName + "!");
    }

    private void fetchPublicGroups() {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("study_groups");
        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<StudyGroup> groups = new ArrayList<>();
                if (!snapshot.exists()) {
                    Log.d("Firebase", "No groups data found.");
                }
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    StudyGroup group = groupSnapshot.getValue(StudyGroup.class);
                    if (group != null) {
                        group.setId(groupSnapshot.getKey()); // Save the key
                        groups.add(group);
                    }
                }


                if (groups.isEmpty()) {
                    Log.d("Firebase", "No groups added to the list.");
                } else {
                    Log.d("Firebase", "Fetched " + groups.size() + " groups.");
                }

                // Set up RecyclerView to scroll horizontally
                LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                recyclerViewLatestGroups.setLayoutManager(horizontalLayoutManager);

                // Set the adapter to display the groups
                LatestGroupsAdapter adapter = new LatestGroupsAdapter(groups);
                recyclerViewLatestGroups.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching data: " + error.getMessage());
            }
        });
    }





}
