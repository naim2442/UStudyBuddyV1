package com.example.ustudybuddyv1.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ustudybuddyv1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardFragment extends Fragment {

    private TextView tvTotalUsers, tvTotalGroups;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
        tvTotalGroups = view.findViewById(R.id.tvTotalGroups);

        fetchDashboardData();

        return view;
    }

    private void fetchDashboardData() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        // Fetch total users
        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvTotalUsers.setText("Total Users: " + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tvTotalUsers.setText("Failed to fetch users");
            }
        });

        // Fetch total groups
        database.child("study_groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvTotalGroups.setText("Total Groups: " + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tvTotalGroups.setText("Failed to fetch groups");
            }
        });
    }
}
