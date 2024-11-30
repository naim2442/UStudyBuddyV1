package com.example.ustudybuddyv1.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.ArrayList;

public class FolderFragment extends Fragment {

    private static final String PREFS_NAME = "FolderPrefs";
    private static final String FOLDER_KEY = "folders";  // SharedPreferences key

    private ListView fileList;
    private Button createFolderButton;
    private ArrayList<String> folderNames;
    private ArrayAdapter<String> adapter;

    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        // Initialize SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Initialize UI components
        fileList = view.findViewById(R.id.file_list);
        createFolderButton = view.findViewById(R.id.create_folder_button);

        // Initialize the folder names list and load data
        folderNames = new ArrayList<>();
        loadFoldersFromPreferences();

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
        loadFoldersFromPreferences();
    }

    private void loadFoldersFromPreferences() {
        // Clear the existing list before loading new data
        folderNames.clear();

        // Load the stored folder names from SharedPreferences
        String foldersString = sharedPreferences.getString(FOLDER_KEY, "");
        if (!foldersString.isEmpty()) {
            String[] folders = foldersString.split(",");
            for (String folder : folders) {
                if (!folder.trim().isEmpty()) {
                    folderNames.add(folder);
                }
            }
        }

        // Notify adapter that data has changed
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
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

    // Create a new folder locally and store it in SharedPreferences
    private void createNewFolder(String folderName) {
        folderNames.add(folderName);
        saveFoldersToPreferences();
        adapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), folderName + " created", Toast.LENGTH_SHORT).show();
    }

    // Save the folder list back to SharedPreferences
    private void saveFoldersToPreferences() {
        // Convert the list of folder names into a comma-separated string
        StringBuilder foldersString = new StringBuilder();
        for (String folder : folderNames) {
            foldersString.append(folder).append(",");
        }

        // Remove the last comma if there are folders in the list
        if (foldersString.length() > 0) {
            foldersString.deleteCharAt(foldersString.length() - 1);
        }

        sharedPreferences.edit().putString(FOLDER_KEY, foldersString.toString()).apply();
    }

    private void showDeleteConfirmation(String folderName, int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Folder")
                .setMessage("Are you sure you want to delete the folder: " + folderName + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    folderNames.remove(position);
                    saveFoldersToPreferences();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), folderName + " deleted", Toast.LENGTH_SHORT).show();
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
