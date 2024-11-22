package com.example.ustudybuddyv1;
import com.example.ustudybuddyv1.Model.StudyGroup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseStudyGroupHelper {

    private DatabaseReference studyGroupRef;

    public FirebaseStudyGroupHelper() {
        // Initialize Firebase reference to "StudyGroups" path
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        studyGroupRef = database.getReference("StudyGroups");
    }

    // Add a new StudyGroup to Firebase
    public void addStudyGroup(StudyGroup studyGroup) {
        String key = studyGroupRef.push().getKey();
        if (key != null) {
            studyGroupRef.child(key).setValue(studyGroup);
        }
    }

    // Retrieve all StudyGroups
    public void getStudyGroups(ValueEventListener listener) {
        studyGroupRef.addListenerForSingleValueEvent(listener);
    }

    // Update an existing StudyGroup
    public void updateStudyGroup(String groupId, StudyGroup updatedGroup) {
        studyGroupRef.child(groupId).setValue(updatedGroup);
    }

    // Delete a StudyGroup
    public void deleteStudyGroup(String groupId) {
        studyGroupRef.child(groupId).removeValue();
    }
}
