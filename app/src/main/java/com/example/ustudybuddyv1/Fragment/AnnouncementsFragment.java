package com.example.ustudybuddyv1.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.Adapter.AnnouncementsAdapter;
import com.example.ustudybuddyv1.Model.Announcement;
import com.example.ustudybuddyv1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AnnouncementsAdapter announcementsAdapter;
    private List<Announcement> announcementList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcement, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_announcements);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        announcementList = new ArrayList<>();
        announcementsAdapter = new AnnouncementsAdapter(announcementList);
        recyclerView.setAdapter(announcementsAdapter);

        // Mock Data - Replace with database/API calls
        loadAnnouncements();

        return view;
    }

    private void loadAnnouncements() {
        DatabaseReference announcementsRef = FirebaseDatabase.getInstance().getReference("announcements");

        // Fetch announcements from the database
        announcementsRef.orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                announcementList.clear(); // Clear the list to avoid duplicates

                for (DataSnapshot announcementSnapshot : snapshot.getChildren()) {
                    Announcement announcement = announcementSnapshot.getValue(Announcement.class);

                    if (announcement != null) {
                        announcementList.add(announcement);
                    }
                }

                // Notify the adapter of changes
                announcementsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors (e.g., show a Toast)
                Toast.makeText(getContext(), "Failed to load announcements", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
