package com.example.ustudybuddyv1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPasswordEditText, newPasswordEditText, retypeNewPasswordEditText;
    private Button changePasswordButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize views
        oldPasswordEditText = findViewById(R.id.old_password_edit_text);
        newPasswordEditText = findViewById(R.id.new_password_edit_text);
        retypeNewPasswordEditText = findViewById(R.id.retype_new_password_edit_text);
        changePasswordButton = findViewById(R.id.change_password_button);

        // Set up the change password button listener
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String retypeNewPassword = retypeNewPasswordEditText.getText().toString().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || retypeNewPassword.isEmpty()) {
            Toast.makeText(ChangePasswordActivity.this, "All fields must be filled.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(retypeNewPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "New passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Re-authenticate the user to change the password
        // Assuming the user is already logged in
        mAuth.signInWithEmailAndPassword(currentUser.getEmail(), oldPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User re-authenticated, proceed to change the password
                        currentUser.updatePassword(newPassword)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(ChangePasswordActivity.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                                        finish(); // Close the activity
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this, "Error changing password: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Old password is incorrect.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
