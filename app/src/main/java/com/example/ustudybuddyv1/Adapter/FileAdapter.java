package com.example.ustudybuddyv1.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ustudybuddyv1.Model.File;
import com.example.ustudybuddyv1.R;

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
        fileNameTextView.setText(file.getName());

        convertView.setOnClickListener(v -> {
            // Handle file click (maybe open or download the file)
            openFile(file.getUrl());
        });

        return convertView;
    }

    private void openFile(String fileUrl) {
        // Handle the file opening, such as viewing or downloading
        // For now, it can just show a Toast or open in an external app
    }
}
