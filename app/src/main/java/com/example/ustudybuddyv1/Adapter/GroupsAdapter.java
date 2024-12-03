package com.example.ustudybuddyv1.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.Model.StudyGroup;
import com.example.ustudybuddyv1.R;

import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupViewHolder> {

    private List<StudyGroup> groupsList;
    private OnDeleteClickListener deleteClickListener;

    public GroupsAdapter(List<StudyGroup> groupsList, OnDeleteClickListener deleteClickListener) {
        this.groupsList = groupsList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        StudyGroup group = groupsList.get(position);
        holder.tvGroupName.setText(group.getGroupName());
        holder.tvDescription.setText(group.getDescription());

        // Set up the delete button
        holder.btnDelete.setOnClickListener(v -> deleteClickListener.onDeleteClick(group));
    }

    @Override
    public int getItemCount() {
        return groupsList.size();
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(StudyGroup group);
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName, tvDescription;
        Button btnDelete;

        public GroupViewHolder(View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnDelete = itemView.findViewById(R.id.btnDeleteGroup);
        }
    }
}
