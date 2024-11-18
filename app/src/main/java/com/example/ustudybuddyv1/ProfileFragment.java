package com.example.ustudybuddyv1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView fullNameTextView, facultyTextView, emailTextView;
    private ImageView profileImageView;
    private Button changePasswordButton, logoutButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Get user reference from Realtime Database
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

            fullNameTextView = view.findViewById(R.id.full_name_text_view);
            facultyTextView = view.findViewById(R.id.faculty_text_view);
            emailTextView = view.findViewById(R.id.email_text_view);
            profileImageView = view.findViewById(R.id.profile_image);
            changePasswordButton = view.findViewById(R.id.change_password_button);
            logoutButton = view.findViewById(R.id.logout_button);

            // Load user data
            loadUserData();

            // Set up buttons
            changePasswordButton.setOnClickListener(v -> changePassword());
            logoutButton.setOnClickListener(v -> logoutUser());
        } else {
            // Handle user not logged in
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadUserData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    String faculty = dataSnapshot.child("faculty").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);

                    fullNameTextView.setText(fullName);
                    facultyTextView.setText(faculty);
                    emailTextView.setText(email);
                } else {
                    Toast.makeText(getActivity(), "User profile not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePassword() {
        Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
        startActivity(intent);
    }


    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        // Redirect to login page
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish(); // Close current activity
    }
}
