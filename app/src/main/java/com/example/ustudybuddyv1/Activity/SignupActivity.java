package com.example.ustudybuddyv1.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ustudybuddyv1.R;
import com.example.ustudybuddyv1.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText, fullNameEditText;
    private Spinner facultySpinner;
    private CheckBox termsCheckbox;
    private Button signupButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        // Initialize Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize views
        usernameEditText = findViewById(R.id.username_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        fullNameEditText = findViewById(R.id.full_name_edit_text);
        facultySpinner = findViewById(R.id.faculty_spinner);
        termsCheckbox = findViewById(R.id.terms_checkbox);
        signupButton = findViewById(R.id.signup_button);

        // Set up the faculty dropdown
        String[] faculties = {"Faculty Of Computer Science and Maths", "Law", "Halal", "Art Design"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, faculties);
        facultySpinner.setAdapter(adapter);

        signupButton.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String fullName = fullNameEditText.getText().toString().trim();
        String faculty = facultySpinner.getSelectedItem().toString();

        // Validate inputs
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullName.isEmpty()) {
            Toast.makeText(SignupActivity.this, "All fields must be filled.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignupActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!termsCheckbox.isChecked()) {
            Toast.makeText(SignupActivity.this, "You must agree to the Terms and Conditions.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user in Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                        // Get the user's UID
                        String userId = mAuth.getCurrentUser().getUid();

                        // Create a user object to store in Realtime Database
                        User user = new User(userId, username, fullName, email, faculty);

                        // Save the user data to Realtime Database
                        databaseReference.child(userId).setValue(user)
                                .addOnSuccessListener(aVoid -> {
                                    // Data saved successfully
                                    startActivity(new Intent(SignupActivity.this, LoginActivity.class)); // Change to LoginActivity
                                    finish(); // Close SignupActivity
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the error
                                    Toast.makeText(SignupActivity.this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(SignupActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
