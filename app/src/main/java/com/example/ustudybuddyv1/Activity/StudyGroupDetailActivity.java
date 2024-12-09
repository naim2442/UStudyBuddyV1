package com.example.ustudybuddyv1.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.Adapter.ChatAdapter;
import com.example.ustudybuddyv1.Model.FileDetails;
import com.example.ustudybuddyv1.Model.Message;
import com.example.ustudybuddyv1.Model.StudyGroup;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StudyGroupDetailActivity extends AppCompatActivity {

    private ImageButton attachFileButton; // button for file attachment
    private static final int FILE_REQUEST_CODE = 123;
    private TextView groupNameTextView;
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendMessageButton;
    private Button viewMembersButton, viewDetailsButton, viewFilesButton;

    private DatabaseReference studyGroupsRef, chatRef;
    private String currentUserId;
    private ChatAdapter chatAdapter;
    private List<Message> messagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group_detail);

        // Firebase initialization
        studyGroupsRef = FirebaseDatabase.getInstance().getReference("study_groups");
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Initialize views
        groupNameTextView = findViewById(R.id.text_group_name);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.message_input);
        sendMessageButton = findViewById(R.id.send_message_button);
        viewMembersButton = findViewById(R.id.button_view_members);
        viewDetailsButton = findViewById(R.id.button_view_details);
        viewFilesButton = findViewById(R.id.button_view_files);
        attachFileButton = findViewById(R.id.attach_file_button);  // Initialize the attach button

        // Attach file button listener
        attachFileButton.setOnClickListener(v -> openFilePicker());

        // Get the group data passed from the previous activity
        StudyGroup group = (StudyGroup) getIntent().getSerializableExtra("group");

        if (group != null) {
            displayGroupDetails(group);

            // Initialize chat reference based on group ID
            chatRef = studyGroupsRef.child(group.getGroupId()).child("messages");
        }

        // Setup RecyclerView
        chatAdapter = new ChatAdapter(messagesList, currentUserId);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Load messages from Firebase
        loadChatMessages();

        // Send message button click listener
        sendMessageButton.setOnClickListener(v -> sendMessage());

        // View Members button click listener
        viewMembersButton.setOnClickListener(v -> {
            if (group != null) {
                fetchAndDisplayMembers(group.getGroupId());
            }
        });

        // View files
        viewFilesButton.setOnClickListener(v -> {
            if (group != null) {
                Intent intent = new Intent(StudyGroupDetailActivity.this, ViewFilesActivity.class);
                intent.putExtra("GROUP_ID", group.getGroupId()); // Pass the group ID
                startActivity(intent);
            }
        });

        // View group details button click listener
        viewDetailsButton.setOnClickListener(v -> {
            // Create an Intent to navigate to GroupDetailsActivity
            Intent intent = new Intent(StudyGroupDetailActivity.this, GroupDetailsActivity.class);

            // Pass the StudyGroup object via Intent
            intent.putExtra("studyGroup", group);  // 'group' is the StudyGroup object from the intent

            // Start the GroupDetailsActivity
            startActivity(intent);
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");  // Allow any file type
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                String fileExtension = getFileExtension(fileUri);
                String mimeType = getContentResolver().getType(fileUri);

                // Check if file type is valid
                if (isValidFileType(fileExtension, mimeType)) {
                    // Show confirmation dialog
                    showFileConfirmationDialog(fileUri, fileExtension, mimeType);
                } else {
                    Toast.makeText(this, "Invalid file type. Please upload an image, PDF, DOCX, or Excel file.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showFileConfirmationDialog(Uri fileUri, String fileExtension, String mimeType) {
        // Removed the unnecessary redeclaration of fileExtension

        if (isValidFileType(fileExtension, mimeType)) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm File Upload")
                    .setMessage("Are you sure you want to upload this " + fileExtension.toUpperCase() + " file?")
                    .setPositiveButton("Yes", (dialog, which) -> uploadFileToFirebase(fileUri))
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            Toast.makeText(this, "Invalid file type. Please upload an image, PDF, DOCX, or Excel file.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri fileUri) {
        String extension = null;
        String fileName = fileUri.getLastPathSegment();

        if (fileName != null && fileName.contains(".")) {
            extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return extension;
    }

    private boolean isValidFileType(String extension, String mimeType) {
        // Check file extension
        if (extension != null) {
            extension = extension.toLowerCase();
        }

        // Check if the file extension is allowed
        return extension != null &&
                (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") ||
                        extension.equals("pdf") || extension.equals("docx") || extension.equals("xls") ||
                        extension.equals("xlsx"));
    }


    // Upload to Firebase
    private void uploadFileToFirebase(Uri fileUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageRef.child("uploads/" + UUID.randomUUID().toString());

        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // File uploaded successfully
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        // Get the file's download URL
                        String fileUrl = uri.toString();
                        Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show();

                        // Save file details in Firebase Database
                        saveFileDetailsToDatabase(fileUrl);

                        // Send a message indicating the file is uploaded
                        sendFileMessage(fileUrl);
                    });
                })
                .addOnFailureListener(exception -> {
                    // File upload failed
                    Toast.makeText(this, "File upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }





    private void saveFileDetailsToDatabase(String fileUrl) {
        String fileName = "Uploaded File";  // Customize this based on the file name, for example, use fileUri.getLastPathSegment()

        // Create a FileDetails object to store metadata
        FileDetails fileDetails = new FileDetails(fileName, fileUrl, System.currentTimeMillis());

        // Store the file details in the correct study group
        DatabaseReference filesRef = studyGroupsRef.child("study_groups").child("files");
        filesRef.push().setValue(fileDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Optionally, send a message with the uploaded file URL
                sendFileMessage(fileUrl);
            } else {
                Toast.makeText(StudyGroupDetailActivity.this, "Error saving file details to database.", Toast.LENGTH_SHORT).show();
            }
        });
    }



// Send file message
    private void sendFileMessage(String fileUrl) {
        String messageText = "Sent a file";  // Customize this message as needed
        Message fileMessage = new Message(currentUserId, messageText, System.currentTimeMillis(), fileUrl);
        chatRef.push().setValue(fileMessage);  // Send the file message
    }


    // Display the group details
    private void displayGroupDetails(StudyGroup group) {
        groupNameTextView.setText(group.getGroupName());
    }

    private void fetchAndDisplayMembers(String groupId) {
        DatabaseReference membersRef = studyGroupsRef.child(groupId).child("members");

        membersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> membersList = new ArrayList<>();
                    int totalMembers = (int) snapshot.getChildrenCount();
                    final int[] membersFetched = {0}; // Track the number of members fetched

                    for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                        String memberUserId = memberSnapshot.getValue(String.class); // Assuming the member is stored by their userId

                        // Now fetch the member's name from the "users" node
                        fetchUserName(memberUserId, membersList, membersFetched, totalMembers);
                    }
                } else {
                    showMembersDialog(new ArrayList<>()); // Show empty list if no members
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error if needed
            }
        });
    }

    private void fetchUserName(String userId, List<String> membersList, int[] membersFetched, int totalMembers) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("name");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.getValue(String.class);
                    membersList.add(userName); // Add member's name to the list
                }

                // Increment the count of members fetched
                membersFetched[0]++;

                // Show members dialog after all names are retrieved
                if (membersFetched[0] == totalMembers) {
                    showMembersDialog(membersList);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error if needed
            }
        });
    }

    private void showMembersDialog(List<String> membersList) {
        String[] membersArray = membersList.toArray(new String[0]);

        new AlertDialog.Builder(this)
                .setTitle("Group Members")
                .setItems(membersArray, null)
                .setPositiveButton("OK", null)
                .show();
    }

    // Load chat messages
    private void loadChatMessages() {
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    messagesList.add(message);
                    chatAdapter.notifyDataSetChanged(); // Notify adapter about the new message
                    chatRecyclerView.scrollToPosition(messagesList.size() - 1);  // Scroll to the latest message
                }
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    // Send a text message
    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            Message newMessage = new Message(currentUserId, messageText, System.currentTimeMillis(), null);
            chatRef.push().setValue(newMessage); // Send the text message
            messageInput.setText(""); // Clear input field after sending
        } else {
            Toast.makeText(this, "Message can't be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
