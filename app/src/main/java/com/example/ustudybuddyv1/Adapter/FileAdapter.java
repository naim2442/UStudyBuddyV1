package com.example.ustudybuddyv1.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.example.ustudybuddyv1.R;

import java.io.File;  // Ensure this is the java.io.File class for file handling
import java.util.List;

public class FileAdapter extends BaseAdapter {
    private Context context;
    private List<File> fileList;

    public FileAdapter(Context context, List<File> fileList) {
        this.context = context;
        this.fileList = fileList;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_file, parent, false);
        }

        File file = fileList.get(position);

        TextView fileNameTextView = convertView.findViewById(R.id.file_name);
        fileNameTextView.setText(file.getName());  // Display file name

        convertView.setOnClickListener(v -> {
            // Handle file click to open the file
            openFile(file);
        });

        return convertView;
    }

    private void openFile(File file) {
        File localFile = new File(context.getFilesDir(), file.getName()); // Adjust to get file from internal storage

        if (localFile.exists()) {
            Uri fileUri = Uri.fromFile(localFile);  // Create URI for the file
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "*/*");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                context.startActivity(intent);  // Start activity to open the file
            } catch (Exception e) {
                Toast.makeText(context, "No app available to open this file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
        }
    }
}
