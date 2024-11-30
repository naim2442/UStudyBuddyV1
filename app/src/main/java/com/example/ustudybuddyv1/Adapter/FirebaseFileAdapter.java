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

import com.example.ustudybuddyv1.Activity.ViewFilesActivity;
import com.example.ustudybuddyv1.Model.FileDetails;
import com.example.ustudybuddyv1.R;

import java.util.List;

public class FirebaseFileAdapter extends BaseAdapter {
    private Context context;
    private List<FileDetails> fileList;

    public FirebaseFileAdapter(Context context, List<FileDetails> fileList) {
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

        TextView fileNameTextView = convertView.findViewById(R.id.file_name);

        FileDetails fileDetails = fileList.get(position);

        fileNameTextView.setText(fileDetails.getFileName());

        convertView.setOnClickListener(v -> {
            String fileUrl = fileDetails.getFileUrl();
            if (fileUrl != null && !fileUrl.isEmpty()) {
                openFile(fileUrl);
            } else {
                Toast.makeText(context, "Invalid file URL", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    private void openFile(String fileUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
        context.startActivity(intent);
    }
}
