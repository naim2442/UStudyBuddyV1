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

import com.example.ustudybuddyv1.Adapter.AnnouncementsAdapter;
import com.example.ustudybuddyv1.Adapter.LatestGroupsAdapter;
import com.example.ustudybuddyv1.Adapter.MotivationalQuotesAdapter;
import com.example.ustudybuddyv1.Adapter.RecommendedVideosAdapter;
import com.example.ustudybuddyv1.Adapter.StudyGroupAdapter;
import com.example.ustudybuddyv1.Adapter.StudyTipAdapter;
import com.example.ustudybuddyv1.Model.Announcement;
import com.example.ustudybuddyv1.Model.MotivationalQuote;
import com.example.ustudybuddyv1.Model.StudyTip;
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
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewLatestGroups, recyclerViewRecommendedVideos, recyclerViewMotivationalQuotes;
    private RecyclerView recyclerViewAnnouncements;
    private AnnouncementsAdapter announcementsAdapter;
    private List<Announcement> announcements = new ArrayList<>();
    private TextView welcomeMessage;

    private List<Video> recommendedVideos = new ArrayList<>();

    private LatestGroupsAdapter latestGroupsAdapter;

    private List<StudyGroup> yourGroups = new ArrayList<>();
    private List<StudyGroup> upcomingGroups = new ArrayList<>();

    private RecyclerView recyclerViewDailyTips;
    private List<StudyTip> studyTips = new ArrayList<>(); // List to store study tips
    private StudyTipAdapter dailyTipsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        welcomeMessage = view.findViewById(R.id.welcome_message);
        recyclerViewLatestGroups = view.findViewById(R.id.recycler_view_latest_groups);
        recyclerViewRecommendedVideos = view.findViewById(R.id.recycler_view_recommended);
        recyclerViewMotivationalQuotes = view.findViewById(R.id.recycler_view_motivational_quotes);
        TextView announcementsTitle = view.findViewById(R.id.announcements_title);
        announcementsTitle.setOnClickListener(v -> {
            AnnouncementsFragment announcementsFragment = new AnnouncementsFragment();

            // Navigate to AnnouncementsFragment
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, announcementsFragment)
                    .addToBackStack(null) // Optional: Adds to the back stack
                    .commit();
        });


        TextView latestStudyTipsTitle = view.findViewById(R.id.latest_study_tips_title);
        latestStudyTipsTitle.setOnClickListener(v -> {
            DailyTipsFragment dailyTipsFragment = new DailyTipsFragment();

//            // Pass study tips to DailyTipsFragment (if needed)
//            Bundle bundle = new Bundle();
//            bundle.putParcelableArrayList("studyTips", new ArrayList<>(studyTips));
//            dailyTipsFragment.setArguments(bundle);

            // Navigate to DailyTipsFragment
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, dailyTipsFragment)
                    .addToBackStack(null) // Optional: Adds to the back stack
                    .commit();
        });


        recyclerViewAnnouncements = view.findViewById(R.id.recycler_view_announcements);
        recyclerViewAnnouncements.setLayoutManager(new LinearLayoutManager(getActivity()));
        fetchAnnouncements();


        recyclerViewMotivationalQuotes = view.findViewById(R.id.recycler_view_motivational_quotes);
        recyclerViewMotivationalQuotes.setLayoutManager(new LinearLayoutManager(getActivity()));
        fetchMotivationalQuotes();


        // Initialize the RecyclerView for daily tips
        recyclerViewDailyTips = view.findViewById(R.id.recycler_view_daily_tips);
        recyclerViewDailyTips.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        // Fetch daily tips from Firebase
        fetchDailyTips();





        // Set up layout manager for RecyclerView
        recyclerViewMotivationalQuotes.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewAnnouncements.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
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

    private void fetchMotivationalQuotes() {
        DatabaseReference quotesRef = FirebaseDatabase.getInstance().getReference("motivational_quotes");

        // Order by timestamp (or any field you use to track creation date)
        quotesRef.orderByChild("timestamp").limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MotivationalQuote> quotes = new ArrayList<>();
                for (DataSnapshot quoteSnapshot : snapshot.getChildren()) {
                    MotivationalQuote quote = quoteSnapshot.getValue(MotivationalQuote.class);
                    if (quote != null) {
                        quotes.add(quote);
                    }
                }

                // Reverse the list to show the latest quotes first (most recent on the left)
                Collections.reverse(quotes);

                // Set up the adapter for motivational quotes
                MotivationalQuotesAdapter adapter = new MotivationalQuotesAdapter(quotes);
                recyclerViewMotivationalQuotes.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load motivational quotes", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void fetchAnnouncements() {
        DatabaseReference announcementsRef = FirebaseDatabase.getInstance().getReference("announcements");

        // Order by date, assuming "date" is a field in your Firebase model
        announcementsRef.orderByChild("date").limitToLast(3).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                announcements.clear();
                for (DataSnapshot announcementSnapshot : snapshot.getChildren()) {
                    Announcement announcement = announcementSnapshot.getValue(Announcement.class);
                    if (announcement != null) {
                        announcements.add(announcement);
                    }
                }

                // Reverse the list to show latest first
                Collections.reverse(announcements);

                // Update the adapter
                if (announcementsAdapter == null) {
                    announcementsAdapter = new AnnouncementsAdapter(announcements);
                    recyclerViewAnnouncements.setAdapter(announcementsAdapter);
                } else {
                    announcementsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load announcements", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDailyTips() {
        DatabaseReference tipsRef = FirebaseDatabase.getInstance().getReference("study_tips");
        tipsRef.orderByChild("timestamp").limitToLast(3).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studyTips.clear();
                for (DataSnapshot tipSnapshot : snapshot.getChildren()) {
                    StudyTip studyTip = tipSnapshot.getValue(StudyTip.class);
                    if (studyTip != null) {
                        studyTips.add(studyTip);
                    }
                }

                // Reverse the list to show the latest tips first
                Collections.reverse(studyTips);

                // Set up the adapter for the latest study tips
                dailyTipsAdapter = new StudyTipAdapter(studyTips);
                recyclerViewDailyTips.setAdapter(dailyTipsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load study tips", Toast.LENGTH_SHORT).show();
            }
        });
    }







}


