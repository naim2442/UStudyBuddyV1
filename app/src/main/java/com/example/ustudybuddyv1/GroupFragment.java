package com.example.ustudybuddyv1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {

    private RecyclerView recyclerViewYourGroups, recyclerViewUpcomingGroups;
    private StudyGroupAdapter yourGroupsAdapter, upcomingGroupsAdapter;
    private List<StudyGroup> yourGroups = new ArrayList<>();
    private List<StudyGroup> upcomingGroups = new ArrayList<>();
    private TextView textCreateGroup;
    private Button btnCreateGroup;

    private StudyGroupCRUDActivity studyGroupCRUDActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        recyclerViewYourGroups = view.findViewById(R.id.recycler_view_your_study_groups);
        recyclerViewUpcomingGroups = view.findViewById(R.id.recycler_view_upcoming_study_groups);
        textCreateGroup = view.findViewById(R.id.text_create_group);
        btnCreateGroup = view.findViewById(R.id.btn_create_group);

        yourGroupsAdapter = new StudyGroupAdapter(yourGroups);
        upcomingGroupsAdapter = new StudyGroupAdapter(upcomingGroups);

        recyclerViewYourGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewYourGroups.setAdapter(yourGroupsAdapter);

        recyclerViewUpcomingGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewUpcomingGroups.setAdapter(upcomingGroupsAdapter);

        btnCreateGroup.setOnClickListener(v -> createNewGroup());

        // Ensure the parent activity is an instance of StudyGroupCRUDActivity
        if (getActivity() instanceof StudyGroupCRUDActivity) {
            studyGroupCRUDActivity = (StudyGroupCRUDActivity) getActivity();
        }

        fetchStudyGroups();



        return view;
    }

    private void fetchStudyGroups() {
        DatabaseReference studyGroupsRef = FirebaseDatabase.getInstance().getReference("study_groups");

        studyGroupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                yourGroups.clear();  // Clear previous data
                upcomingGroups.clear();  // Clear previous data

                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for (DataSnapshot data : snapshot.getChildren()) {
                    StudyGroup group = data.getValue(StudyGroup.class);
                    if (group != null) {
                        if (group.getCreatorId().equals(currentUserId)) {
                            yourGroups.add(group);
                        } else if (group.getMembers() != null && group.getMembers().contains(currentUserId)) {
                            upcomingGroups.add(group);
                        }
                    }
                }
                // Notify the adapters to refresh the list
                yourGroupsAdapter.notifyDataSetChanged();
                upcomingGroupsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch study groups: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            // Re-fetch the study groups to reflect the newly created group
            fetchStudyGroups();
        }
    }

    private void createNewGroup() {
        // Start the activity to create a new group
        startActivity(new Intent(getContext(), StudyGroupCRUDActivity.class));
    }

    private void toggleCreateGroupVisibility() {
        if (yourGroups.isEmpty()) {
            textCreateGroup.setVisibility(View.VISIBLE);
            btnCreateGroup.setVisibility(View.VISIBLE);
        } else {
            textCreateGroup.setVisibility(View.GONE);
            btnCreateGroup.setVisibility(View.GONE);
        }
    }
}
