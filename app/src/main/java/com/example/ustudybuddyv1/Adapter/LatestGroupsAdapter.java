package com.example.ustudybuddyv1.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.R;
import com.example.ustudybuddyv1.Model.StudyGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class LatestGroupsAdapter extends RecyclerView.Adapter<LatestGroupsAdapter.ViewHolder> {
    private List<StudyGroup> studyGroups;
    private String userId;

    public LatestGroupsAdapter(List<StudyGroup> studyGroups, String userId) {
        this.studyGroups = studyGroups;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudyGroup group = studyGroups.get(position);
        holder.groupName.setText(group.getGroupName());
        holder.membersCount.setText(group.getMembersCount() + " members");

        // Check if the current user is the creator of the group
        if (group.getCreatorId().equals(userId)) {
            holder.buttonJoinGroup.setVisibility(View.GONE); // Hide button if user is creator
        } else {
            // Check if the user is already a member of the group
            if (group.isMember(userId)) {
                holder.buttonJoinGroup.setVisibility(View.GONE); // Hide button if user is already a member
            } else {
                holder.buttonJoinGroup.setVisibility(View.VISIBLE); // Show button if user is not a member

                // Set the onClickListener for joining the group
                holder.buttonJoinGroup.setOnClickListener(v -> {
                    group.joinGroup(userId);

                    // Update the group in Firebase
                    DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("study_groups").child(group.getGroupId());
                    groupRef.setValue(group).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(v.getContext(), "You joined the group!", Toast.LENGTH_SHORT).show();
                            updateGroupCount(holder, group);  // Update member count
                            holder.buttonJoinGroup.setVisibility(View.GONE); // Hide button after joining
                        } else {
                            Toast.makeText(v.getContext(), "Failed to join the group.", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        }
    }

    private void updateGroupCount(ViewHolder holder, StudyGroup group) {
        holder.membersCount.setText(group.getMembersCount() + " members");
    }

    @Override
    public int getItemCount() {
        return studyGroups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        TextView membersCount;
        Button buttonJoinGroup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.text_group_name);
            membersCount = itemView.findViewById(R.id.text_members_count);
            buttonJoinGroup = itemView.findViewById(R.id.button_join_group);
        }
    }
}
