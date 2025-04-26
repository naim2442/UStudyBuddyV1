package com.example.ustudybuddyv1.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.example.ustudybuddyv1.R;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText nameEditText, emailEditText, passwordEditText, studentIdEditText;
    private Button signupButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        studentIdEditText = findViewById(R.id.student_id);
        signupButton = findViewById(R.id.next_button);
        TextView subtitle = findViewById(R.id.subtitle);

        subtitle = findViewById(R.id.subtitle);
        subtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LocationDetailsActivity.LoginActivity.class);
                startActivity(intent);
            }
        });


        signupButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String studentId = studentIdEditText.getText().toString();

            if (isInputValid(name, email, password, studentId)) {
                // Register the user with Firebase Authentication
                registerUser(name, email, password, studentId);
            }
        });



    }

    private boolean isInputValid(String name, String email, String password, String studentId) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || studentId.isEmpty()) {
            Toast.makeText(SignupActivity.this, "All fields must be filled.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void registerUser(String name, String email, String password, String studentId) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Pass user info to SignupDetailsActivity
                        Intent intent = new Intent(SignupActivity.this, SignupDetailsActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("email", email);
                        intent.putExtra("studentId", studentId);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, "Authentication failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
