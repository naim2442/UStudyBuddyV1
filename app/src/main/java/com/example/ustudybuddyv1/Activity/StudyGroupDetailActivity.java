package com.example.ustudybuddyv1.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StudyGroupDetailActivity extends AppCompatActivity {

    private TextView groupNameTextView, groupLocationTextView, groupMembersTextView;
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendMessageButton;

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
        groupLocationTextView = findViewById(R.id.text_group_location);
        groupMembersTextView = findViewById(R.id.text_group_members);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.message_input);
        sendMessageButton = findViewById(R.id.send_message_button);

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
    }

    // Display the group details
    private void displayGroupDetails(StudyGroup group) {
        groupNameTextView.setText(group.getGroupName());
        groupLocationTextView.setText(group.getLocation());

        if (group.getMembers() != null && !group.getMembers().isEmpty()) {
            StringBuilder membersList = new StringBuilder();
            for (String member : group.getMembers()) {
                membersList.append("- ").append(member).append("\n");
            }
            groupMembersTextView.setText(membersList.toString().trim());
        } else {
            groupMembersTextView.setText("No members have joined this group yet.");
        }
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
