package com.example.ustudybuddyv1.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.Adapter.UserAdapter;
import com.example.ustudybuddyv1.Model.User;
import com.example.ustudybuddyv1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageUsersFragment extends Fragment implements UserAdapter.OnDeleteClickListener {

    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private List<User> userList;
    private TextView tvTotalUsers;  // Declare TextView for total users

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_users, container, false);

        // Initialize Views
        rvUsers = view.findViewById(R.id.rvUsers);
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);  // Initialize total users TextView

        rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);  // Pass 'this' to handle delete
        rvUsers.setAdapter(userAdapter);

        fetchUsers(); // Fetch user data from Firebase

        return view;
    }

    private void fetchUsers() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        user.setUserId(snapshot.getKey());
                        userList.add(user);
                    }
                }

                // Update the total users count in the TextView
                tvTotalUsers.setText("Total Users: " + userList.size());

                // Notify the adapter to update the RecyclerView
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void onDeleteClick(User user) {
        // Show confirmation dialog before deletion
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes", (dialog, which) -> deleteUser(user))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteUser(User user) {
        // Get reference to the user node in Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUserId());

        // Remove the user from Firebase
        userRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "User deleted", Toast.LENGTH_SHORT).show();
                fetchUsers();  // Refresh the list
            } else {
                Toast.makeText(requireContext(), "Failed to delete user", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
