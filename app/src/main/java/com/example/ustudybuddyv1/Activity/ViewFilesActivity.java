package com.example.ustudybuddyv1.Activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ustudybuddyv1.Adapter.FirebaseFileAdapter;
import com.example.ustudybuddyv1.Model.FileDetails;
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
    private FirebaseFileAdapter firebaseFileAdapter;
    private List<FileDetails> fileList;
    private DatabaseReference filesRef;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_files);

        // Initialize views
        filesListView = findViewById(R.id.files_list_view);

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
        fetchFiles();
    }

    private void fetchFiles() {
        filesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                FileDetails fileDetails = dataSnapshot.getValue(FileDetails.class);
                if (fileDetails != null) {
                    fileList.add(fileDetails);
                    firebaseFileAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle changes (optional)
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Handle removal (optional)
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle moved items (optional)
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewFilesActivity.this, "Error loading files: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
