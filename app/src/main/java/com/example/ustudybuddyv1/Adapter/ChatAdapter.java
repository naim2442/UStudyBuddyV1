package com.example.ustudybuddyv1.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        // Fetch the username from Firebase using the senderId
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(message.getSenderId());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("name").getValue(String.class);
                    holder.usernameTextView.setText(username); // Set the username
                } else {
                    holder.usernameTextView.setText("Unknown User"); // Fallback for missing users
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.usernameTextView.setText("Error"); // Fallback for errors
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
