package com.example.ustudybuddyv1.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.Adapter.GroupsAdapter;
import com.example.ustudybuddyv1.Model.StudyGroup;
import com.example.ustudybuddyv1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageGroupsAdminFragment extends Fragment {

    private RecyclerView recyclerViewGroups;
    private GroupsAdapter groupsAdapter;
    private List<StudyGroup> groupsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_groups_admin, container, false);

        recyclerViewGroups = view.findViewById(R.id.recyclerViewGroups);
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(requireContext()));
        groupsList = new ArrayList<>();
        groupsAdapter = new GroupsAdapter(groupsList, this::deleteGroup);
        recyclerViewGroups.setAdapter(groupsAdapter);

        fetchGroups();

        return view;
    }

    // Fetch the study groups from Firebase
    private void fetchGroups() {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("study_groups");

        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    StudyGroup group = snapshot.getValue(StudyGroup.class);
                    if (group != null) {
                        group.setGroupId(snapshot.getKey());
                        groupsList.add(group);
                    }
                }
                groupsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
    }

    // Method to delete a group
    private void deleteGroup(StudyGroup group) {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("study_groups").child(group.getGroupId());
        groupRef.removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Group deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete group", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
