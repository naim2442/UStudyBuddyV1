package com.example.ustudybuddyv1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ustudybuddyv1.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText editTextEmail, editTextPassword;

    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        Button buttonLogin = findViewById(R.id.login_button);
        Button registerButton = findViewById(R.id.register_button);

        TextView textViewForgotPassword = findViewById(R.id.forgot_password);

        // Set up login button click listener
        buttonLogin.setOnClickListener(view -> loginUser());

        // Set up sign-up text click listener
        registerButton.setOnClickListener(view -> {
            // Redirect to Sign Up Activity
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });

//        // Set up forgot password text click listener
//        textViewForgotPassword.setOnClickListener(view -> {
//            // Redirect to Forgot Password Activity (optional - create this if needed)
//            startActivity(new Intent(LoginActivity.this, ChangePasswordActivity.class));
//        });
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Input validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase login
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, navigate to Main Activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Close the login activity
                    } else {
                        // If sign in fails, display a message to the user
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(LoginActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
