package com.example.ustudybuddyv1.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.Activity.StudyGroupDetailActivity;
import com.example.ustudybuddyv1.R;
import com.example.ustudybuddyv1.Model.StudyGroup;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class StudyGroupAdapter extends RecyclerView.Adapter<StudyGroupAdapter.ViewHolder> {
    private final List<StudyGroup> studyGroups;

    public StudyGroupAdapter(List<StudyGroup> studyGroups) {
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
        holder.membersCount.setText(String.format("%d members", group.getMembersCount()));

        holder.groupName.setText(group.getGroupName());
        holder.membersCount.setText(group.getMembersCount() + " members");

        // Get the current user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Check if the current user is the creator of the group
        if (group.getCreatorId().equals(userId)) {
            // If the user is the creator, hide the "Join" button
            holder.buttonJoinGroup.setVisibility(View.GONE);
        }  if (group.isMember(userId)) {
            // If the user is already a member, hide the "Join" button
            holder.buttonJoinGroup.setVisibility(View.GONE);
        }



        holder.itemView.setOnClickListener(v -> {
            // Ensure you're passing the object correctly
            Intent intent = new Intent(holder.itemView.getContext(), StudyGroupDetailActivity.class);
            intent.putExtra("group", group); // Pass the StudyGroup object
            holder.itemView.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return studyGroups.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupName, membersCount, buttonJoinGroup;

        ViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.text_group_name);
            membersCount = itemView.findViewById(R.id.text_members_count);
            buttonJoinGroup = itemView.findViewById(R.id.button_join_group); // Initialize the button
        }
    }


}
