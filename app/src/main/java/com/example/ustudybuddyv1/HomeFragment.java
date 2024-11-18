package com.example.ustudybuddyv1;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewRecommended;
    private RecyclerView recyclerViewLatestGroups;
    private RecyclerView recyclerViewContinueStudies;
    private TextView welcomeMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        welcomeMessage = view.findViewById(R.id.welcome_message);
        recyclerViewRecommended = view.findViewById(R.id.recycler_view_recommended);
        recyclerViewLatestGroups = view.findViewById(R.id.recycler_view_latest_groups);
        recyclerViewContinueStudies = view.findViewById(R.id.recycler_view_continue_studies);

        // Set up layout managers for RecyclerViews
        recyclerViewRecommended.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewLatestGroups.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewContinueStudies.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        // Fetch user data to update welcome message
        fetchUserData();

        return view;
    }

    private void fetchUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("username").getValue(String.class);
                    updateWelcomeMessage(userName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void updateWelcomeMessage(String userName) {
        welcomeMessage.setText("Welcome back, " + userName + "!");
    }
}
