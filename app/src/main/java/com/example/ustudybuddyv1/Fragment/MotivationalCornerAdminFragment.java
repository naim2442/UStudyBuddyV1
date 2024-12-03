package com.example.ustudybuddyv1.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ustudybuddyv1.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MotivationalCornerAdminFragment extends Fragment {

    private EditText etQuote, etAuthor;
    private Button btnAddQuote;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_motivational_corner_admin, container, false);

        etQuote = view.findViewById(R.id.etQuote);
        etAuthor = view.findViewById(R.id.etAuthor);
        btnAddQuote = view.findViewById(R.id.btnAddQuote);

        // Firebase reference to "motivational_quotes" node
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("motivational_quotes");

        btnAddQuote.setOnClickListener(v -> {
            String quote = etQuote.getText().toString().trim();
            String author = etAuthor.getText().toString().trim();

            if (TextUtils.isEmpty(quote) || TextUtils.isEmpty(author)) {
                Toast.makeText(getContext(), "Please enter both quote and author", Toast.LENGTH_SHORT).show();
            } else {
                String id = database.push().getKey(); // Generate unique ID for the quote
                MotivationalQuote newQuote = new MotivationalQuote(quote, author);

                if (id != null) {
                    database.child(id).setValue(newQuote).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Quote added successfully!", Toast.LENGTH_SHORT).show();
                            etQuote.setText("");
                            etAuthor.setText("");
                        } else {
                            Toast.makeText(getContext(), "Failed to add quote. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        return view;
    }

    // Model class for Motivational Quote
    public static class MotivationalQuote {
        public String quote;
        public String author;

        public MotivationalQuote(String quote, String author) {
            this.quote = quote;
            this.author = author;
        }
    }
}
