package com.example.ustudybuddyv1.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.ustudybuddyv1.Activity.ChangePasswordActivity;
import com.example.ustudybuddyv1.Activity.LoginActivity;
import com.example.ustudybuddyv1.Activity.StudentDetailsEditActivity;
import com.example.ustudybuddyv1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    private TextView fullNameTextView, studentIdTextView, emailTextView, universityTextView, locationPreferenceTextView, courseTextView;
    private TextView cgpaTextView, semesterYearTextView, highestEducationTextView;
    private Button changePasswordButton, logoutButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ImageView profileImageView; // Declare ImageView for profile picture
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PICTURE_REQUEST = 2;
    private Uri imageUri;

    @SuppressLint("MissingInflatedId")
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

            // Initialize TextViews
            fullNameTextView = view.findViewById(R.id.full_name_text_view);
            studentIdTextView = view.findViewById(R.id.student_id_text_view);
            emailTextView = view.findViewById(R.id.email_text_view);
            universityTextView = view.findViewById(R.id.university_text_view);
            locationPreferenceTextView = view.findViewById(R.id.location_text_view);
            courseTextView = view.findViewById(R.id.course_text_view);
            cgpaTextView = view.findViewById(R.id.cgpa_text_view);
            semesterYearTextView = view.findViewById(R.id.semester_year_text_view);
            highestEducationTextView = view.findViewById(R.id.highest_education_level_text_view);


            // Initialize Buttons
            changePasswordButton = view.findViewById(R.id.edit_button);
            logoutButton = view.findViewById(R.id.logout_button);

            // Initialize ImageView for Profile Picture
            profileImageView = view.findViewById(R.id.image_profile_picture);

            // Load user data
            loadUserData();

            // Set up buttons
            changePasswordButton.setOnClickListener(v -> {
                // Navigate to the StudentDetailsEditActivity
                Intent intent = new Intent(getActivity(), StudentDetailsEditActivity.class);
                startActivity(intent);
            });

            logoutButton.setOnClickListener(v -> logoutUser());

            // Set onClickListener for Profile Image to open a dialog for image selection
            profileImageView.setOnClickListener(v -> showImageSelectionDialog());

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
                    String fullName = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String location = dataSnapshot.child("locationPreference").getValue(String.class);
                    String studentId = dataSnapshot.child("studentId").getValue(String.class);
                    String university = dataSnapshot.child("university").getValue(String.class);
                    String course = dataSnapshot.child("course").getValue(String.class);
                    String cgpa = dataSnapshot.child("cgpa").getValue(String.class);  // Fetch CGPA
                    String semesterYear = dataSnapshot.child("semesterYear").getValue(String.class);  // Fetch Semester Year
                    String highestEducation = dataSnapshot.child("highestEducationLevel").getValue(String.class);  // Fetch Highest Education Level

                    // Set data to TextViews
                    fullNameTextView.setText(fullName);
                    studentIdTextView.setText(studentId);
                    emailTextView.setText(email);
                    locationPreferenceTextView.setText(location);
                    universityTextView.setText(university);
                    courseTextView.setText(course);

                    cgpaTextView.setText(cgpa);
                    semesterYearTextView.setText(semesterYear);
                    highestEducationTextView.setText(highestEducation);
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

    private void showImageSelectionDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Choose Profile Picture")
                .setItems(new CharSequence[]{"Take a Photo", "Choose from Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        // Take a photo
                        openCamera();
                    } else {
                        // Choose from gallery
                        openGallery();
                    }
                })
                .create()
                .show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_PICTURE_REQUEST);
        } else {
            Toast.makeText(getContext(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    profileImageView.setImageBitmap(bitmap);
                    saveImageLocally(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == TAKE_PICTURE_REQUEST && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profileImageView.setImageBitmap(photo);
                saveImageLocally(photo);
            }
        }
    }

    private void saveImageLocally(Bitmap bitmap) {
        File directory = getContext().getFilesDir(); // Use internal storage
        File file = new File(directory, "profile_picture.png");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // Save the bitmap as a PNG file
            Toast.makeText(getContext(), "Profile picture saved locally", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }
}
