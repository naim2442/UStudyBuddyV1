package com.example.ustudybuddyv1.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.Activity.GroupDetailsActivity;
import com.example.ustudybuddyv1.R;
import com.example.ustudybuddyv1.Model.StudyGroup;
import com.google.android.gms.common.util.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LatestGroupsAdapter extends RecyclerView.Adapter<LatestGroupsAdapter.ViewHolder> {
    private final List<StudyGroup> studyGroups;

    public LatestGroupsAdapter(List<StudyGroup> studyGroups, String userId) {
        this.studyGroups = studyGroups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_group, parent, false);
        return new ViewHolder(view);


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudyGroup group = studyGroups.get(position);
        holder.groupName.setText(group.getGroupName());
        holder.membersCount.setText(group.getMembersCount() + " members");

        // Get the current user's ID
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Handle item click to navigate to Group Details Activity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), GroupDetailsActivity.class);
            intent.putExtra("studyGroup", group); // Pass the study group data
            holder.itemView.getContext().startActivity(intent);
        });
        // Handle Join Button visibility
        if (group.getCreatorId().equals(userId) || group.isMember(userId)) {
            holder.buttonJoinGroup.setVisibility(View.GONE);
        } else {
            holder.buttonJoinGroup.setVisibility(View.VISIBLE);
            holder.buttonJoinGroup.setOnClickListener(v -> joinGroup(holder, group, userId));
        }
    }

    private void joinGroup(ViewHolder holder, StudyGroup group, String userId) {
        // Add the user to the group and update Firebase
        group.joinGroup(userId);
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("study_groups").child(group.getGroupId());
        groupRef.setValue(group).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(holder.itemView.getContext(), "You joined the group!", Toast.LENGTH_SHORT).show();
                updateGroupCount(holder, group);  // Update the count after successful join
                holder.buttonJoinGroup.setVisibility(View.GONE);  // Hide the button after joining
            } else {
                Toast.makeText(holder.itemView.getContext(), "Failed to join the group.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void updateGroupCount(ViewHolder holder, StudyGroup group) {
        // Update the view with the new member count
        holder.membersCount.setText(group.getMembersCount() + " members");
    }

    @Override
    public int getItemCount() {
        return studyGroups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        TextView membersCount;
        Button buttonJoinGroup; // Declare the Join button

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.text_group_name);
            membersCount = itemView.findViewById(R.id.text_members_count);
            buttonJoinGroup = itemView.findViewById(R.id.button_join_group); // Initialize the button
        }
    }
}