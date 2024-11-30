package com.example.ustudybuddyv1.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ustudybuddyv1.Adapter.FileAdapter;
import java.io.File;  // Ensure this is the java.io.File class for file handling
import com.example.ustudybuddyv1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ChildEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewFilesActivity extends AppCompatActivity {

    private ListView filesListView;
    private FileAdapter fileAdapter;
    private List<File> fileList;
    private DatabaseReference filesRef;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_files);

        // Initialize views
        filesListView = findViewById(R.id.files_list_view);
        Button closeButton = findViewById(R.id.close_button);

        // Get group ID from the intent
        groupId = getIntent().getStringExtra("GROUP_ID");
        if (groupId == null) {
            // Handle error
            Toast.makeText(this, "Group ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fileList = new ArrayList<>();
        fileAdapter = new FileAdapter(this, fileList);
        filesListView.setAdapter(fileAdapter);

        // Reference to the Firebase database
        filesRef = FirebaseDatabase.getInstance().getReference("study_groups").child(groupId).child("files");

        // Fetch and display files
        fetchFiles();

        // Close button listener
        closeButton.setOnClickListener(v -> finish());
    }

    private void fetchFiles() {
        // Listen for added, removed, or changed files in the Firebase database
        filesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                File file = dataSnapshot.getValue(File.class);
                if (file != null) {
                    fileList.add(file);
                    fileAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle file updates if necessary
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Handle file removal (if needed)
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle file moved (optional)
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewFilesActivity.this, "Failed to load files", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
