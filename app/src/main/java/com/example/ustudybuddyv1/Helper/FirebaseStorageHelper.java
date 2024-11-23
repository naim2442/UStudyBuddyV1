package com.example.ustudybuddyv1.Helper;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseStorageHelper {

    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static StorageReference storageReference = storage.getReference();

    // Upload Image to Firebase Storage
    public static void uploadImage(Context context, Uri imageUri, String imageName, UploadCallback callback) {
        if (imageUri != null) {
            // Create a reference to the file
            StorageReference imageRef = storageReference.child("study_groups_images/" + imageName);

            // Upload the image
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Image uploaded successfully, get the download URL
                        String imageUrl = uri.toString();
                        callback.onUploadSuccess(imageUrl);
                    }))
                    .addOnFailureListener(e -> {
                        // Upload failed
                        Toast.makeText(context, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        callback.onUploadFailure(e);
                    });
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Callback interface to handle upload result
    public interface UploadCallback {
        void onUploadSuccess(String imageUrl);
        void onUploadFailure(Exception e);
    }
}
