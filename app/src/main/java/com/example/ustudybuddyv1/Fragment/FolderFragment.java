package com.example.ustudybuddyv1.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ustudybuddyv1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class FolderFragment extends Fragment {

    private static final String PREFS_NAME = "FolderPrefs";
    private static final String FOLDERS_KEY = "folders";

    private ListView fileList;
    private Button createFolderButton;
    private ArrayList<String> folderNames;
    private ArrayAdapter<String> adapter;

    private FirebaseDatabase database;
    private DatabaseReference foldersRef;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        foldersRef = database.getReference("users").child(auth.getCurrentUser().getUid()).child("folders");

        // Initialize UI components
        fileList = view.findViewById(R.id.file_list);
        createFolderButton = view.findViewById(R.id.create_folder_button);

        // Load folder names from Firebase
        folderNames = new ArrayList<>();
        loadFoldersFromFirebase();

        // Set up the custom adapter for the ListView
        adapter = new ArrayAdapter<>(getActivity(), R.layout.folder_item, R.id.folder_name, folderNames);
        fileList.setAdapter(adapter);

        // Set button click listener to create a new folder
        createFolderButton.setOnClickListener(v -> showCreateFolderDialog());

        // Set up item click listener for opening folders
        fileList.setOnItemClickListener((parent, view1, position, id) -> {
            String folderName = folderNames.get(position);
            openFolder(folderName);
        });

        // Set up long click listener for deleting folder
        fileList.setOnItemLongClickListener((parent, view12, position, id) -> {
            String folderName = folderNames.get(position);
            showDeleteConfirmation(folderName, position);
            return true; // Return true to indicate the event is consumed
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload folder names when the fragment is resumed
        loadFoldersFromFirebase();
    }

    private void loadFoldersFromFirebase() {
        foldersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                folderNames.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    folderNames.add(snapshot.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load folders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show a dialog to input a custom folder name
    private void showCreateFolderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Folder Name");

        final EditText input = new EditText(getActivity());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String folderName = input.getText().toString().trim();
            if (!folderName.isEmpty()) {
                // Check if folder name already exists
                if (!folderNames.contains(folderName)) {
                    createNewFolder(folderName);
                } else {
                    Toast.makeText(getActivity(), "Folder name already exists", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Folder name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Create a new folder in Firebase
    private void createNewFolder(String folderName) {
        foldersRef.child(folderName).setValue(true)
                .addOnSuccessListener(aVoid -> {
                    folderNames.add(folderName);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), folderName + " created", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to create folder", Toast.LENGTH_SHORT).show());
    }

    private void showRenameDialog(String oldName, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Rename Folder");

        final EditText input = new EditText(getActivity());
        input.setText(oldName);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newFolderName = input.getText().toString().trim();
            if (!newFolderName.isEmpty()) {
                renameFolder(oldName, newFolderName, position);
            } else {
                Toast.makeText(getActivity(), "Folder name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void renameFolder(String oldName, String newName, int position) {
        // Firebase does not directly support renaming keys, so delete and re-add the folder
        foldersRef.child(oldName).removeValue()
                .addOnSuccessListener(aVoid -> {
                    foldersRef.child(newName).setValue(true)
                            .addOnSuccessListener(aVoid1 -> {
                                folderNames.set(position, newName);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "Folder renamed to: " + newName, Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to rename folder", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to rename folder", Toast.LENGTH_SHORT).show());
    }

    private void showDeleteConfirmation(String folderName, int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Folder")
                .setMessage("Are you sure you want to delete the folder: " + folderName + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    foldersRef.child(folderName).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                folderNames.remove(position);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), folderName + " deleted", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to delete folder", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .show();
    }

    private void openFolder(String folderName) {
        FileViewerFragment fileViewerFragment = FileViewerFragment.newInstance(folderName);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fileViewerFragment)
                .addToBackStack(null)
                .commit();
    }
}
