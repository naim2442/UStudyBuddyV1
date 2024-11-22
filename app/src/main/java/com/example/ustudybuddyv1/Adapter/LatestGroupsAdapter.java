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

    public LatestGroupsAdapter(List<StudyGroup> studyGroups) {
        this.studyGroups = studyGroups;
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

        // Get the current user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Check if the current user is the creator of the group
        if (group.getCreatorId().equals(userId)) {
            // If the user is the creator, hide the "Join" button
            holder.buttonJoinGroup.setVisibility(View.GONE);
        } else {
            // Otherwise, show the "Join" button
            holder.buttonJoinGroup.setVisibility(View.VISIBLE);

            holder.buttonJoinGroup.setOnClickListener(v -> {
                // Check if the user is already a member
                if (!group.isMember(userId)) {
                    // Add the user to the group
                    group.joinGroup(userId);

                    // Update the group in Firebase
                    DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("study_groups").child(group.getGroupName());
                    groupRef.setValue(group).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Notify the user
                            Toast.makeText(v.getContext(), "You joined the group!", Toast.LENGTH_SHORT).show();

                            // Update the RecyclerView with the new member count
                            updateGroupCount(holder, group);  // Refresh the group info in the adapter
                        } else {
                            Toast.makeText(v.getContext(), "Failed to join the group.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(v.getContext(), "You are already a member of this group.", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
