package com.example.ustudybuddyv1.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ustudybuddyv1.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FileViewerFragment extends Fragment {

    private static final int PICK_FILE_REQUEST = 1;

    private ListView fileList;
    private Button uploadFileButton;

    private TextView folderNameText;
    private ArrayList<String> fileNames;
    private ArrayAdapter<String> adapter;
    private String folderName;

    public static FileViewerFragment newInstance(String folderName) {
        FileViewerFragment fragment = new FileViewerFragment();
        Bundle args = new Bundle();
        args.putString("folderName", folderName);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_viewer, container, false);

        // Retrieve the folder name from arguments
        folderName = getArguments().getString("folderName");

        // Initialize UI components
        fileList = view.findViewById(R.id.file_list);
        uploadFileButton = view.findViewById(R.id.upload_file_button);
        folderNameText = view.findViewById(R.id.folder_name_text);

        // Set the folder name text
        folderNameText.setText(folderName);


        // Initialize file names list
        fileNames = new ArrayList<>();

        // Initialize the adapter and set it to the ListView
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, fileNames);
        fileList.setAdapter(adapter);

        // Load files from local storage
        loadFilesFromLocalStorage();

        // Set button click listener for file uploads
        uploadFileButton.setOnClickListener(v -> openFilePicker());

        // Set up item click listener to open files
        fileList.setOnItemClickListener((parent, view1, position, id) -> openFile(fileNames.get(position)));

        // Set up long click listener for deleting files
        fileList.setOnItemLongClickListener((parent, view12, position, id) -> {
            String fileName = fileNames.get(position);
            showDeleteConfirmation(fileName, position);
            return true; // Return true to indicate the event is consumed
        });

        return view;
    }

    private void loadFilesFromLocalStorage() {
        // Create folder to store files locally (using app's internal storage)
        File folder = new File(getActivity().getFilesDir(), folderName);
        fileNames.clear();

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    fileNames.add(file.getName());
                }
            }
        }

        // Check if the adapter is properly initialized before calling notifyDataSetChanged
        if (adapter != null && fileNames != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                saveFileToLocalStorage(fileUri);
            }
        }
    }

    private void saveFileToLocalStorage(Uri fileUri) {
        // Create a folder on the device to store the files
        File folder = new File(getActivity().getFilesDir(), folderName);
        if (!folder.exists() && !folder.mkdirs()) {
            Toast.makeText(getActivity(), "Failed to create folder", Toast.LENGTH_SHORT).show();
            return;
        }

        try (InputStream inputStream = getActivity().getContentResolver().openInputStream(fileUri)) {
            String fileName = getFileName(fileUri);
            File newFile = new File(folder, fileName);

            try (FileOutputStream outputStream = new FileOutputStream(newFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                fileNames.add(fileName);
                // Ensure adapter is updated
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                Toast.makeText(getActivity(), "File uploaded: " + fileName, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Failed to upload file", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String[] projection = {android.provider.OpenableColumns.DISPLAY_NAME};
        try (android.database.Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(android.provider.OpenableColumns.DISPLAY_NAME));
            }
        }
        return "unknown_file";
    }

    private void showDeleteConfirmation(String fileName, int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete File")
                .setMessage("Are you sure you want to delete the file: " + fileName + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    File file = new File(getActivity().getFilesDir(), folderName + "/" + fileName);
                    if (file.delete()) {
                        fileNames.remove(position);
                        // Notify the adapter of the changes
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        Toast.makeText(getActivity(), fileName + " deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Failed to delete file", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .show();
    }

    private void openFile(String fileName) {
        File file = new File(getActivity().getFilesDir(), folderName + "/" + fileName);
        Uri fileUri = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "*/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "No app available to open this file", Toast.LENGTH_SHORT).show();
        }
    }
}
