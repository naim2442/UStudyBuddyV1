package com.example.ustudybuddyv1.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.ustudybuddyv1.Model.Announcement;
import com.example.ustudybuddyv1.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AnnouncementsAdminFragment extends Fragment {

    private EditText etAnnouncementTitle, etAnnouncementMessage;
    private Button btnCreateAnnouncement;

    private DatabaseReference announcementsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcement_admin, container, false);

        // Initialize views
        etAnnouncementTitle = view.findViewById(R.id.etAnnouncementTitle);
        etAnnouncementMessage = view.findViewById(R.id.etAnnouncementMessage);
        btnCreateAnnouncement = view.findViewById(R.id.btnCreateAnnouncement);

        // Firebase database reference for announcements
        announcementsRef = FirebaseDatabase.getInstance().getReference("announcements");

        // Set button click listener to create announcement
        btnCreateAnnouncement.setOnClickListener(v -> createAnnouncement());

        return view;
    }

    private void createAnnouncement() {
        String title = etAnnouncementTitle.getText().toString().trim();
        String message = etAnnouncementMessage.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(message)) {
            Toast.makeText(getContext(), "Please fill in both fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new announcement object
        String announcementId = announcementsRef.push().getKey();
        Announcement announcement = new Announcement(title, message);

        // Save announcement to Firebase database
        if (announcementId != null) {
            announcementsRef.child(announcementId).setValue(announcement)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Announcement created successfully!", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(getContext(), "Failed to create announcement.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearFields() {
        etAnnouncementTitle.setText("");
        etAnnouncementMessage.setText("");
    }
}
