package com.example.ustudybuddyv1.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ustudybuddyv1.Model.StudyTip;
import com.example.ustudybuddyv1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateStudyTipFragment extends Fragment {

    private EditText etTitle, etMessage;
    private Button btnSubmit;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_study_tip, container, false);

        etTitle = root.findViewById(R.id.etTitle);
        etMessage = root.findViewById(R.id.etMessage);
        btnSubmit = root.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            String title = etTitle.getText().toString();
            String message = etMessage.getText().toString();

            if (!title.isEmpty() && !message.isEmpty()) {
                submitStudyTip(title, message);
            } else {
                Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private void submitStudyTip(String title, String message) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String authorId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "UnknownId";
        String authorName = auth.getCurrentUser() != null ? auth.getCurrentUser().getDisplayName() : "Unknown Author";

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("study_tips");
        String tipId = ref.push().getKey(); // Generate a unique ID for the tip

        // Create a StudyTip object with the entered data
        StudyTip studyTip = new StudyTip(title, message, authorId, authorName);

        // Save the study tip to Firebase
        ref.child(tipId).setValue(studyTip).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Study Tip submitted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error submitting tip", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
