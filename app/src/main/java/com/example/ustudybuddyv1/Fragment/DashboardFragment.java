package com.example.ustudybuddyv1.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.Adapter.AnnouncementsAdapter;
import com.example.ustudybuddyv1.Adapter.MotivationalQuotesAdapter;
import com.example.ustudybuddyv1.Adapter.StudyGroupAdapter;
import com.example.ustudybuddyv1.Model.Announcement;
import com.example.ustudybuddyv1.Model.MotivationalQuote;
import com.example.ustudybuddyv1.Model.StudyGroup;
import com.example.ustudybuddyv1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private TextView tvTotalUsers, tvTotalGroups;
    private TextView tvAnnouncementsAdmin, tvMotivationalCorner, tvViewGroups;
    private RecyclerView recyclerCreateAnnouncements, recyclerMotivationalCorner;
    private RecyclerView recyclerViewGroups;
    private StudyGroupAdapter studyGroupsAdapter;
    private List<StudyGroup> studyGroupList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
        tvTotalGroups = view.findViewById(R.id.tvTotalGroups);
        tvAnnouncementsAdmin = view.findViewById(R.id.tvCreateAnnouncementsTitle);
        tvMotivationalCorner = view.findViewById(R.id.tvMotivationalCornerTitle);
        tvViewGroups = view.findViewById(R.id.tvViewGroupsTitle);

        recyclerCreateAnnouncements = view.findViewById(R.id.recyclerCreateAnnouncements);
        recyclerMotivationalCorner = view.findViewById(R.id.recyclerMotivationalCorner);

        recyclerCreateAnnouncements.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerMotivationalCorner.setLayoutManager(new LinearLayoutManager(getContext()));


        fetchDashboardData();
        fetchAnnouncements();
        fetchMotivationalQuotes();

        tvViewGroups.setOnClickListener(v -> {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new ManageGroupsAdminFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });


        // Set onClick listeners for TextViews to navigate to different fragments
        tvAnnouncementsAdmin.setOnClickListener(v -> navigateToAnnouncementsAdmin());
        tvMotivationalCorner.setOnClickListener(v -> navigateToMotivationalCornerAdmin());
        tvViewGroups.setOnClickListener(v -> navigateToViewGroups());

        return view;
    }

    private void fetchDashboardData() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        // Fetch total users
        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvTotalUsers.setText("Total Users: " + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tvTotalUsers.setText("Failed to fetch users");
            }
        });

        // Fetch total groups
        database.child("study_groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvTotalGroups.setText("Total Groups: " + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tvTotalGroups.setText("Failed to fetch groups");
            }
        });
    }

    private void fetchAnnouncements() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("announcements");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Announcement> announcements = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Announcement announcement = snapshot.getValue(Announcement.class);
                    announcements.add(announcement);
                }
                // Set up the RecyclerView
                AnnouncementsAdapter adapter = new AnnouncementsAdapter(announcements);
                recyclerCreateAnnouncements.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void fetchMotivationalQuotes() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("motivational_quotes");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<MotivationalQuote> quotes = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MotivationalQuote quote = snapshot.getValue(MotivationalQuote.class);
                    quotes.add(quote);
                }
                // Set up the RecyclerView
                MotivationalQuotesAdapter adapter = new MotivationalQuotesAdapter(quotes);
                recyclerMotivationalCorner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }



    // Navigation methods
    private void navigateToAnnouncementsAdmin() {
        // Navigate to AnnouncementsAdminFragment
        AnnouncementsAdminFragment announcementsAdminFragment = new AnnouncementsAdminFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, announcementsAdminFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToMotivationalCornerAdmin() {
        // Navigate to MotivationalCornerAdminFragment
        MotivationalCornerAdminFragment motivationalCornerAdminFragment = new MotivationalCornerAdminFragment();

        // Begin the transaction
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        // Replace the existing fragment with the new one
        transaction.replace(R.id.fragment_container, motivationalCornerAdminFragment);

        // Add the transaction to the back stack so the user can navigate back
        transaction.addToBackStack(null);

        // Commit the transaction to apply changes
        transaction.commit();
    }


    private void navigateToViewGroups() {
        tvViewGroups.setOnClickListener(v -> {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new ManageGroupsAdminFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

    }
}