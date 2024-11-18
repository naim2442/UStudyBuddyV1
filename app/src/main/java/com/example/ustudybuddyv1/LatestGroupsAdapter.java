package com.example.ustudybuddyv1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.membersCount.setText(String.valueOf(group.getMembers()) + " members");

        holder.itemView.setOnClickListener(v -> {
            // Handle study group click, e.g., open the group details
        });
    }

    @Override
    public int getItemCount() {
        return studyGroups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        TextView membersCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.text_group_name);
            membersCount = itemView.findViewById(R.id.text_members_count);
        }
    }
}
