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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        // Listen for updates to the group's status in Firebase
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("study_groups").child(group.getGroupId());
        groupRef.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                // If the status is "requested_for_deletion", make the "Requested for Deletion" text visible
                if ("requested_for_deletion".equals(status)) {
                    holder.tvRequestedForDeletion.setVisibility(View.VISIBLE);
                } else {
                    holder.tvRequestedForDeletion.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here if needed
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupsList.size();
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(StudyGroup group);
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName, tvDescription, tvRequestedForDeletion;
        Button btnDelete;

        public GroupViewHolder(View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnDelete = itemView.findViewById(R.id.btnDeleteGroup);
            tvRequestedForDeletion = itemView.findViewById(R.id.tvRequestedForDeletion);
        }
    }
}
