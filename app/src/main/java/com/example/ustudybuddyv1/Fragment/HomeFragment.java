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
import com.example.ustudybuddyv1.Adapter.RecommendedGroupsAdapter;
import com.example.ustudybuddyv1.Adapter.RecommendedVideosAdapter;
import com.example.ustudybuddyv1.Adapter.StudyGroupAdapter;
import com.example.ustudybuddyv1.Adapter.StudyTipAdapter;
import com.example.ustudybuddyv1.Database.TaskDao;
import com.example.ustudybuddyv1.Database.TaskDatabase;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewLatestGroups, recyclerViewRecommendedVideos, recyclerViewMotivationalQuotes;
    private RecyclerView recyclerViewAnnouncements;
    private AnnouncementsAdapter announcementsAdapter;
    private List<Announcement> announcements = new ArrayList<>();
    private TextView welcomeMessage;

    private RecommendedGroupsAdapter recommendedGroupsAdapter;

    private List<Video> recommendedVideos = new ArrayList<>();

    private LatestGroupsAdapter latestGroupsAdapter;

    private List<StudyGroup> yourGroups = new ArrayList<>();
    private List<StudyGroup> upcomingGroups = new ArrayList<>();

    private RecyclerView recyclerViewRecommended;
    private LatestGroupsAdapter adapter;
    private List<StudyGroup> allStudyGroups = new ArrayList<>();
    private List<StudyGroup> recommendedGroups = new ArrayList<>();
    private String userCourse, userUniversity, currentUserId;


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


        //navigate to todolist
        TextView todoListTitle = view.findViewById(R.id.todo_list_title);
        todoListTitle.setOnClickListener(v -> {
            ToDoListFragment toDoListFragment = new ToDoListFragment();

            // Navigate to ToDoListFragment
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, toDoListFragment)
                    .addToBackStack(null) // Optional: Adds to the back stack for proper navigation
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

        recyclerViewRecommended = view.findViewById(R.id.recycler_view_recommended);
        recyclerViewRecommended.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        fetchRecommendedGroups(); // Call this method to fetch and display recommended groups


        // Set up layout manager for RecyclerView
        recyclerViewMotivationalQuotes.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewAnnouncements.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewLatestGroups.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewRecommendedVideos.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        // Initialize the recommended groups title
        TextView recommendedGroupsTitle = view.findViewById(R.id.recommended_videos_title);

        // Set click listener to navigate to AllRecommendedGroupsFragment
        recommendedGroupsTitle.setOnClickListener(v -> {
            AllRecommendedGroupsFragment allRecommendedGroupsFragment = new AllRecommendedGroupsFragment();

            // Navigate to the new fragment
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, allRecommendedGroupsFragment)
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

//fetch user statistics
        loadUserStatistics();


        return view;
    }
    private void loadUserStatistics() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get reference to the study groups from Firebase
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("study_groups");

        // Retrieve study groups
        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // **Fix**: Null check for the root view to avoid NullPointerException
                View rootView = getView();
                if (rootView == null) {
                    return; // If the view is not available, exit the method to avoid exceptions
                }

                int totalGroupsJoined = 0;

                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    StudyGroup group = groupSnapshot.getValue(StudyGroup.class);
                    if (group != null && group.getMembers() != null && group.getMembers().contains(userId)) {
                        totalGroupsJoined++;
                    }
                }

                // **Fix**: Correctly reference the TextView for total groups joined by the user inside Fragment
                TextView totalGroupsText = rootView.findViewById(R.id.total_groups_joined);
                totalGroupsText.setText("Total Groups Joined: " + totalGroupsJoined);

                // Initialize variable to store the nearest upcoming session
                Date nearestSessionDate = null;

                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    StudyGroup group = groupSnapshot.getValue(StudyGroup.class);
                    if (group != null) {
                        try {
                            // Convert group date to Date object
                            Date groupDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(group.getDateTime());

                            // Check if the group is upcoming
                            if (groupDate != null && groupDate.after(new Date())) {  // After current date
                                if (nearestSessionDate == null || groupDate.before(nearestSessionDate)) {
                                    nearestSessionDate = groupDate;
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // **Fix**: Correctly reference the TextView for upcoming sessions inside Fragment
                TextView upcomingSessionsText = rootView.findViewById(R.id.upcoming_sessions);
                if (nearestSessionDate != null) {
                    String dateStr = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(nearestSessionDate);
                    upcomingSessionsText.setText("Upcoming Session: " + dateStr);
                } else {
                    upcomingSessionsText.setText("No upcoming sessions.");
                }

                // **Fix**: Retrieve total To-Do list count from Room database asynchronously
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // **Fix**: Ensure to call database operation on background thread
                        int totalTodos = getTotalTodosFromRoomDatabase(userId); // Pass userId if needed
                        // Update UI on the main thread
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // **Fix**: Correctly reference the TextView for to-do list inside Fragment
                                TextView totalTodosText = rootView.findViewById(R.id.total_todo_list);
                                totalTodosText.setText("To-Do List: " + totalTodos);
                            }
                        });
                    }
                }).start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error (for example, show a Toast or log the error)
                Log.e("UserStatistics", "Failed to load data: " + databaseError.getMessage());
            }
        });
    }

    // Helper method to get the total number of to-dos from Room database
    private int getTotalTodosFromRoomDatabase(String userId) {
        // **Fix**: Make sure you're running the database query on a background thread (which is already done)
        TaskDao taskDao = TaskDatabase.getInstance(getContext()).taskDao();

        // Perform database query synchronously on a background thread (no UI updates here)
        return taskDao.getTotalTodos() - 1 ;  // You could also pass userId if you want user-specific tasks
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


    private void fetchRecommendedGroups() {
        // Fetch current user ID
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the "users" node to get the current user's course and university
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Fetch user's course and university from the snapshot
                    userCourse = snapshot.child("course").getValue(String.class);
                    userUniversity = snapshot.child("university").getValue(String.class);


                    // Now fetch study groups
                    DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("study_groups");
                    groupsRef.orderByChild("public").equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<StudyGroup> groups = new ArrayList<>();

                            for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                                StudyGroup group = groupSnapshot.getValue(StudyGroup.class);
                                if (group != null) {
                                    // Fetch creator details for filtering
                                    String creatorId = group.getCreatorId();
                                    fetchCreatorDetails(creatorId, group, userCourse, userUniversity, groups);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void fetchCreatorDetails(String creatorId, StudyGroup group, String userCourse, String userUniversity, List<StudyGroup> groups) {
        // Reference to the "users" node to get the creator's course and university
        DatabaseReference creatorRef = FirebaseDatabase.getInstance().getReference("users").child(creatorId);
        creatorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Fetch creator's course and university
                    String creatorCourse = snapshot.child("course").getValue(String.class);
                    String creatorUniversity = snapshot.child("university").getValue(String.class);

                    // Filter the study groups by matching course or university
                    if ((creatorCourse != null && creatorCourse.equals(userCourse)) ||
                            (creatorUniversity != null && creatorUniversity.equals(userUniversity))) {
                        groups.add(group);
                    }

                    // After processing all groups, update the RecyclerView
                    // Limit the groups to only 3 for the home page
                    List<StudyGroup> limitedGroups = new ArrayList<>();
                    for (int i = 0; i < Math.min(groups.size(), 3); i++) {
                        limitedGroups.add(groups.get(i));
                    }

                    // Set up RecyclerView to scroll horizontally
                    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewRecommended.setLayoutManager(horizontalLayoutManager);

                    // Pass the limited groups list and user ID to the adapter
                    recommendedGroupsAdapter = new RecommendedGroupsAdapter(limitedGroups, currentUserId);
                    recyclerViewRecommended.setAdapter(recommendedGroupsAdapter);

                    // Notify the adapter of data changes
                    if (recommendedGroupsAdapter != null) {
                        recommendedGroupsAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
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


