package com.example.ustudybuddyv1.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.Activity.StudyTipDetailActivity;
import com.example.ustudybuddyv1.Adapter.StudyTipAdapter;
import com.example.ustudybuddyv1.Model.StudyTip;
import com.example.ustudybuddyv1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DailyTipsFragment extends Fragment {

    private RecyclerView rvStudyTips;
    private TextView tvCreateTip;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_daily_tips, container, false);

        rvStudyTips = root.findViewById(R.id.rvStudyTips);
        tvCreateTip = root.findViewById(R.id.tvCreateTip);

        // Set up RecyclerView with a GridLayoutManager
        rvStudyTips.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 columns
        // Initialize with an empty list
        StudyTipAdapter adapter = new StudyTipAdapter(new ArrayList<>());
        rvStudyTips.setAdapter(adapter);


        // Load study tips from Firebase
        FirebaseDatabase.getInstance().getReference("study_tips")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<StudyTip> studyTips = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            StudyTip studyTip = snapshot.getValue(StudyTip.class);
                            if (studyTip != null) {
                                studyTips.add(studyTip); // Add study tips to the list
                            }
                        }
                        adapter.setStudyTips(studyTips); // Update the adapter with new data
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle database errors here if necessary
                    }
                });



        // Navigate to Create Study Tip Fragment when clicked
        tvCreateTip.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new CreateStudyTipFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return root;
    }
}
