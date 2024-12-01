package com.example.ustudybuddyv1.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.Activity.StudentDetailsActivity;
import com.example.ustudybuddyv1.Model.Message;
import com.example.ustudybuddyv1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<Message> messages;
    private final String currentUserId;

    public ChatAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return message.getSenderId().equals(currentUserId) ? 1 : 0;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) { // Message sent by the current user
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
        } else { // Message received from others
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messages.get(position);

        // Set message text and timestamp
        holder.messageTextView.setText(message.getMessageText());
        holder.timestampTextView.setText(formatTimestamp(message.getTimestamp()));

        // Fetch the username and user details from Firebase using the senderId
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(message.getSenderId());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Fetch user details
                    String username = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String location = snapshot.child("locationPreference").getValue(String.class);
                    String university = snapshot.child("university").getValue(String.class);
                    String course = snapshot.child("course").getValue(String.class);

                    // Set username
                    holder.usernameTextView.setText(username != null ? username : "Unknown User");

                    // Add click listener to usernameTextView
                    holder.usernameTextView.setOnClickListener(v -> {
                        // Navigate to StudentDetailsActivity
                        Intent intent = new Intent(holder.itemView.getContext(), StudentDetailsActivity.class);

                        // Pass all user details
                        intent.putExtra("USER_ID", message.getSenderId());
                        intent.putExtra("USER_NAME", username);
                        intent.putExtra("USER_EMAIL", email);
                        intent.putExtra("USER_LOCATION", location);
                        intent.putExtra("USER_UNIVERSITY", university);
                        intent.putExtra("USER_COURSE", course);

                        holder.itemView.getContext().startActivity(intent);
                    });
                } else {
                    holder.usernameTextView.setText("Unknown User");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.usernameTextView.setText("Error Loading User");
            }
        });
    }



    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, timestampTextView, usernameTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageText);
            timestampTextView = itemView.findViewById(R.id.messageTimestamp);
            usernameTextView = itemView.findViewById(R.id.userName); // Initialize the username TextView
        }
    }

    // Helper method to format the timestamp
    private String formatTimestamp(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a");
        return sdf.format(new java.util.Date(timestamp));
    }
}
