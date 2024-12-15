package com.example.ustudybuddyv1.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ustudybuddyv1.Adapter.FirebaseFileAdapter;
import com.example.ustudybuddyv1.Model.FileDetails;
import com.example.ustudybuddyv1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ViewFilesActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;

    private ListView filesListView;
    private FirebaseFileAdapter firebaseFileAdapter;
    private List<FileDetails> fileList;
    private DatabaseReference filesRef;
    private String groupId;
    private Button closeButton;
    private Button uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_files);

        // Initialize views
        filesListView = findViewById(R.id.files_list_view);
        closeButton = findViewById(R.id.close_button);
        uploadButton = findViewById(R.id.upload_button);

        // Get group ID from the intent
        groupId = getIntent().getStringExtra("GROUP_ID");
        if (groupId == null) {
            Toast.makeText(this, "Group ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fileList = new ArrayList<>();
        firebaseFileAdapter = new FirebaseFileAdapter(this, fileList);
        filesListView.setAdapter(firebaseFileAdapter);

        // Reference to the Firebase database
        filesRef = FirebaseDatabase.getInstance().getReference("study_groups").child(groupId).child("files");

        // Fetch and display files
        fetchFilesFromStorage();

        // Set up Close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the current activity
            }
        });

        // Set up Upload Files button
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });
    }

    private void fetchFilesFromStorage() {
        // Reference to the files node in the specific group's database
        filesRef = FirebaseDatabase.getInstance().getReference("study_groups").child(groupId).child("files");

        filesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                FileDetails fileDetails = dataSnapshot.getValue(FileDetails.class);
                if (fileDetails != null) {
                    String fileName = fileDetails.getFileName();
                    String fileUrl = fileDetails.getFileUrl();
                    String uploadedBy = fileDetails.getUploadedBy();  // Fetch uploaded by info

                    // Reference to the specific folder in Firebase Storage
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads/" + groupId);

                    // Try to get the file from Firebase Storage using the file's path
                    StorageReference fileRef = storageRef.child(fileName);
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update the file details with the correct file URL
                        fileList.add(new FileDetails(fileName, uri.toString(), uploadedBy));
                        firebaseFileAdapter.notifyDataSetChanged();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(ViewFilesActivity.this, "No files found " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle file updates if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Handle file removal if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle file move if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewFilesActivity.this, "No files fetched: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select a File"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            uploadFileToFirebase(fileUri);
        }
    }

    private void uploadFileToFirebase(Uri fileUri) {
        // Get the current user's UID (assuming Firebase Authentication is used)
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch the user's name from Firebase (assuming it's stored under 'users' node in Firebase)
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        usersRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get the user's name
                String uploadedBy = snapshot.getValue(String.class);
                if (uploadedBy == null) {
                    uploadedBy = "Unknown User"; // Fallback if name is not found
                }

                // Get the groupId from the activity or intent
                String groupId = getIntent().getStringExtra("GROUP_ID");

                // Reference to the group-specific folder in Firebase Storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                String fileName = getFileName(fileUri);
                StorageReference fileRef = storageRef.child("uploads/" + groupId + "/" + fileName);

                // Upload the file to Firebase Storage
                String finalUploadedBy = uploadedBy;
                fileRef.putFile(fileUri)
                        .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String fileUrl = uri.toString();

                            // Save the file details to the database under the specific group
                            saveFileDetailsToDatabase(groupId, fileName, fileUrl, finalUploadedBy);

                            // Optionally fetch the files again after uploading
                            fetchFilesFromStorage();

                            Toast.makeText(ViewFilesActivity.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                        }))
                        .addOnFailureListener(exception -> {
                            Toast.makeText(ViewFilesActivity.this, "File upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFilesActivity.this, "Error fetching user name: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveFileDetailsToDatabase(String groupId, String fileName, String fileUrl, String uploadedBy) {
        // Create a new FileDetails object with the uploader's name
        FileDetails fileDetails = new FileDetails(fileName, fileUrl, uploadedBy);

        // Save the file details under the specific group's files node
        DatabaseReference groupFilesRef = FirebaseDatabase.getInstance().getReference("study_groups")
                .child(groupId).child("files");

        groupFilesRef.push().setValue(fileDetails)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ViewFilesActivity.this, "File details saved successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ViewFilesActivity.this, "Failed to save file details", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private String getFileName(Uri fileUri) {
        String fileName = "unknown_file";
        try (Cursor cursor = getContentResolver().query(fileUri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
            }
        }
        return fileName;
    }
}
