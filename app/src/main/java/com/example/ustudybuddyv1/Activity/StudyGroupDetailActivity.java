package com.example.ustudybuddyv1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.Adapter.ChatAdapter;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StudyGroupDetailActivity extends AppCompatActivity {

    private TextView groupNameTextView;
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendMessageButton;
    private Button viewMembersButton, viewDetailsButton, viewFilesButton; // New button for viewing members

    private DatabaseReference studyGroupsRef, chatRef;
    private String currentUserId;
    private ChatAdapter chatAdapter;
    private List<Message> messagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group_detail);

        // Firebase initializationa
        studyGroupsRef = FirebaseDatabase.getInstance().getReference("study_groups");
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Initialize views
        groupNameTextView = findViewById(R.id.text_group_name);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.message_input);
        sendMessageButton = findViewById(R.id.send_message_button);
        viewMembersButton = findViewById(R.id.button_view_members); // Initialize new button
        viewDetailsButton = findViewById(R.id.button_view_details);

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

        viewDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to GroupDetailsActivity
                Intent intent = new Intent(StudyGroupDetailActivity.this, GroupDetailsActivity.class);

                // Pass the StudyGroup object via Intent
                intent.putExtra("studyGroup", group);  // 'group' is the StudyGroup object from the intent

                // Start the GroupDetailsActivity
                startActivity(intent);
            }
        });

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



    // Show members in a dialog
    private void showMembersDialog(List<String> membersList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Members");

        if (membersList.isEmpty()) {
            builder.setMessage("No members have joined this group yet.");
        } else {
            String[] membersArray = membersList.toArray(new String[0]);
            builder.setItems(membersArray, null);
        }

        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // Load chat messages from Firebase
    private void loadChatMessages() {
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    messagesList.add(message);
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.smoothScrollToPosition(messagesList.size() - 1);
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

    // Send a message
    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            Message message = new Message(currentUserId, messageText, System.currentTimeMillis());
            chatRef.push().setValue(message);
            messageInput.setText("");
        }
    }
}