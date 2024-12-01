package com.example.ustudybuddyv1.Fragment;

import android.annotation.SuppressLint;
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

import com.example.ustudybuddyv1.Adapter.LatestGroupsAdapter;
import com.example.ustudybuddyv1.Adapter.RecommendedVideosAdapter;
import com.example.ustudybuddyv1.Adapter.StudyGroupAdapter;
import com.example.ustudybuddyv1.Model.Video;
import com.example.ustudybuddyv1.R;
import com.example.ustudybuddyv1.Model.StudyGroup;
import com.example.ustudybuddyv1.Utils.YoutubeManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewLatestGroups, recyclerViewRecommendedVideos;
    private TextView welcomeMessage;

    private List<Video> recommendedVideos = new ArrayList<>();

    private LatestGroupsAdapter latestGroupsAdapter;

    private List<StudyGroup> yourGroups = new ArrayList<>();
    private List<StudyGroup> upcomingGroups = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        welcomeMessage = view.findViewById(R.id.welcome_message);
        recyclerViewLatestGroups = view.findViewById(R.id.recycler_view_latest_groups);
        recyclerViewRecommendedVideos = view.findViewById(R.id.recycler_view_recommended);

        // Set up layout manager for RecyclerView
        recyclerViewLatestGroups.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewRecommendedVideos.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

// Initialize the recommended videos title
        TextView recommendedVideosTitle = view.findViewById(R.id.recommended_videos_title);

// Set click listener to navigate to AllRecommendedVideosFragment
        recommendedVideosTitle.setOnClickListener(v -> {
            AllVideosFragment allRecommendedVideosFragment = new AllVideosFragment();

            // Navigate to the new fragment
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, allRecommendedVideosFragment)
                    .addToBackStack(null) // Adds to back stack for proper navigation
                    .commit();
        });







        TextView latestStudyGroupsTitle = view.findViewById(R.id.latest_study_groups_title);
        latestStudyGroupsTitle.setOnClickListener(v -> {
            // Navigate to the fragment that shows all study groups
            AllStudyGroupsFragment allStudyGroupsFragment = new AllStudyGroupsFragment();

            // If you're using Fragment, replace the current fragment with the new one
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, allStudyGroupsFragment)
                    .addToBackStack(null) // Optional: adds to back stack for navigation
                    .commit();
        });



        // Fetch user data to update welcome message
        fetchUserData();

        // Fetch public groups to display in the latest groups RecyclerView
        fetchPublicGroups();

        // Fetch recommended videos
        fetchRecommendedVideos();

        return view;
    }

    private void fetchUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("name").getValue(String.class);
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
        groupsRef.orderByChild("public").equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<StudyGroup> groups = new ArrayList<>();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Fetch user ID here
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    StudyGroup group = groupSnapshot.getValue(StudyGroup.class);
                    if (group != null) {
                        groups.add(group);
                    }
                }

                // Limit the groups to only 3 for the home page
                List<StudyGroup> limitedGroups = new ArrayList<>();
                for (int i = 0; i < Math.min(groups.size(), 3); i++) {
                    limitedGroups.add(groups.get(i));
                }

                // Set up RecyclerView to scroll horizontally
                LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                recyclerViewLatestGroups.setLayoutManager(horizontalLayoutManager);

                // Pass the limited groups list and user ID to the adapter
                LatestGroupsAdapter adapter = new LatestGroupsAdapter(limitedGroups, userId);
                recyclerViewLatestGroups.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
    private void fetchRecommendedVideos() {
        YoutubeManager youTubeManager = new YoutubeManager();

        // Replace with your desired playlist ID
        String playlistId = "RDQMGvnpkZz7INY";  // Example Playlist ID

        youTubeManager.fetchVideosFromPlaylist(playlistId, new YoutubeManager.VideoSearchCallback() {
            @Override
            public void onVideosFetched(List<Video> videos) {
                if (videos != null && !videos.isEmpty()) {
                    recommendedVideos = videos;

                    // Set up the adapter for recommended videos
                    RecommendedVideosAdapter adapter = new RecommendedVideosAdapter(recommendedVideos);
                    recyclerViewRecommendedVideos.setAdapter(adapter);
                } else {
                    // Safely check if the fragment's context is available
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "No videos found in the playlist!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("HomeFragment", "Context is null, cannot show Toast");
                    }
                }
            }
        });
    }






}


