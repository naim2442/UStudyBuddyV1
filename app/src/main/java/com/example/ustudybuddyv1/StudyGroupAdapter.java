package com.example.ustudybuddyv1;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        // Set the group name and member count
        holder.groupName.setText(group.getGroupName());
        holder.membersCount.setText(String.format("%d members", group.getMembersCount()));

        // Handle item click to open StudyGroupDetailActivity
        holder.itemView.setOnClickListener(v -> {
            // Pass the clicked group to the detail activity
            Intent intent = new Intent(holder.itemView.getContext(), StudyGroupDetailActivity.class);
            intent.putExtra("group", (CharSequence) group); // Passing the StudyGroup object
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return studyGroups.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupName, membersCount;

        ViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.text_group_name);
            membersCount = itemView.findViewById(R.id.text_members_count);
        }
    }
}
