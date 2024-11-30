package com.example.ustudybuddyv1.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FileViewerFragment extends Fragment {

    private static final String ARG_FOLDER_NAME = "folder_name";
    private static final int PICK_FILE_REQUEST = 1; // Request code for file picker
    private String folderName;

    private TextView folderNameTextView;
    private ListView fileListView;
    private ArrayList<String> fileNames;
    private ArrayAdapter<String> adapter;

    public static FileViewerFragment newInstance(String folderName) {
        FileViewerFragment fragment = new FileViewerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FOLDER_NAME, folderName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            folderName = getArguments().getString(ARG_FOLDER_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_viewer, container, false);

        folderNameTextView = view.findViewById(R.id.folder_name_text_view);
        fileListView = view.findViewById(R.id.file_list_view);
        Button uploadFileButton = view.findViewById(R.id.upload_file_button);

        // Display the current folder name
        folderNameTextView.setText(folderName);

        // Initialize file list and adapter
        fileNames = loadFilesForFolder(folderName);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, fileNames);
        fileListView.setAdapter(adapter);

        // Set up item click listener for file items
        fileListView.setOnItemClickListener((parent, view1, position, id) -> {
            String fileName = fileNames.get(position);
            // Handle file click (e.g., view file details or open the file)
            Toast.makeText(getActivity(), "Clicked on: " + fileName, Toast.LENGTH_SHORT).show();
        });

        // Set up the upload button click listener
        uploadFileButton.setOnClickListener(v -> uploadNewFile());

        return view;
    }

    private ArrayList<String> loadFilesForFolder(String folderName) {
        // Simulated loading of files for the specified folder
        ArrayList<String> files = new ArrayList<>();
        // Replace this with actual file retrieval logic (e.g., from database or file system)
        files.add("File1.txt");
        files.add("File2.txt");
        files.add("File3.txt");
        return files;
    }

    private void uploadNewFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow all file types initially
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter for image, pdf, docx, and xls files by MIME type
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {
                "image/jpeg", "image/png", "application/pdf", "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        });

        startActivityForResult(Intent.createChooser(intent, "Select a file"), PICK_FILE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                // Save the file locally
                saveFileLocally(fileUri);
            }
        }
    }

    private void saveFileLocally(Uri fileUri) {
        // Get the content resolver and the file name
        ContentResolver resolver = getActivity().getContentResolver();
        String fileName = getFileName(fileUri);

        try (InputStream inputStream = resolver.openInputStream(fileUri);
             FileOutputStream outputStream = getActivity().openFileOutput(fileName, getActivity().MODE_PRIVATE)) {

            if (inputStream != null) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }

            // Notify the user and update the file list
            Toast.makeText(getActivity(), "File saved locally", Toast.LENGTH_SHORT).show();
            fileNames.add(fileName); // Add the new file to the list
            adapter.notifyDataSetChanged(); // Refresh the adapter

        } catch (IOException e) {
            Toast.makeText(getActivity(), "Error saving file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("FileViewer", "Error saving file", e);
        }
    }

    // Helper method to get the file name from Uri
    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String fileName = "";
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                cursor.close();
            }
        } else {
            fileName = uri.getPath();
            int cut = fileName.lastIndexOf('/');
            if (cut != -1) {
                fileName = fileName.substring(cut + 1);
            }
        }
        return fileName;
    }
}
