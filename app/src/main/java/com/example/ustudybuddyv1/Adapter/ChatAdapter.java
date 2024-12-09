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

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<Message> messages;
    private final String currentUserId;
    private final FirebaseDatabase database;

    // Store user data to avoid redundant database calls
    private final List<User> usersCache;

    public ChatAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
        this.database = FirebaseDatabase.getInstance();
        this.usersCache = new ArrayList<>();
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

        // Check if the user is already in the cache
        User user = getUserFromCache(message.getSenderId());

        if (user == null) {
            // Fetch the user from Firebase if not in the cache
            fetchUserFromFirebase(message.getSenderId(), holder);
        } else {
            // If the user is cached, use it directly
            holder.usernameTextView.setText(user.getName());
            setUpUsernameClickListener(holder, user);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private void fetchUserFromFirebase(String userId, ChatViewHolder holder) {
        DatabaseReference userRef = database.getReference("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Extract user details from snapshot
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String location = snapshot.child("locationPreference").getValue(String.class);
                    String university = snapshot.child("university").getValue(String.class);
                    String course = snapshot.child("course").getValue(String.class);

                    // Create user object
                    User user = new User(userId, name, email, location, university, course);

                    // Cache the user for future use
                    usersCache.add(user);

                    // Update UI with user details
                    holder.usernameTextView.setText(name);
                    setUpUsernameClickListener(holder, user);
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

    private void setUpUsernameClickListener(ChatViewHolder holder, User user) {
        holder.usernameTextView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), StudentDetailsActivity.class);
            intent.putExtra("USER_ID", user.getId());
            intent.putExtra("USER_NAME", user.getName());
            intent.putExtra("USER_EMAIL", user.getEmail());
            intent.putExtra("USER_LOCATION", user.getLocation());
            intent.putExtra("USER_UNIVERSITY", user.getUniversity());
            intent.putExtra("USER_COURSE", user.getCourse());

            holder.itemView.getContext().startActivity(intent);
        });
    }

    private User getUserFromCache(String userId) {
        for (User user : usersCache) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, timestampTextView, usernameTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageText);
            timestampTextView = itemView.findViewById(R.id.messageTimestamp);
            usernameTextView = itemView.findViewById(R.id.userName);
        }
    }

    private String formatTimestamp(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a");
        return sdf.format(new java.util.Date(timestamp));
    }

    // User class to store user data
    public static class User {
        private final String id;
        private final String name;
        private final String email;
        private final String location;
        private final String university;
        private final String course;

        public User(String id, String name, String email, String location, String university, String course) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.location = location;
            this.university = university;
            this.course = course;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getLocation() {
            return location;
        }

        public String getUniversity() {
            return university;
        }

        public String getCourse() {
            return course;
        }
    }
}
