package com.example.ustudybuddyv1.Helper;

import com.example.ustudybuddyv1.Model.StudyGroup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseStudyGroupHelper {

    private static DatabaseReference databaseReference;

    // Initialize Firebase Database
    static {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("study_groups");

    }

    // Save StudyGroup to Firebase
    public static void saveStudyGroup(StudyGroup studyGroup) {
        // Generate a unique group ID
        String groupId = databaseReference.push().getKey();
        studyGroup.setGroupId(groupId);

        // Save the StudyGroup object under the generated groupId
        databaseReference.child(groupId).setValue(studyGroup)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Optionally: notify that the group was saved successfully
                    } else {
                        // Handle failure (notify user, log error)
                    }
                });
    }
}
