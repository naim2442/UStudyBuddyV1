package com.example.ustudybuddyv1.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ustudybuddyv1.Model.StudyTip;
import com.example.ustudybuddyv1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudyTipDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvAuthor, tvMessage;
    private ImageView ivProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_tip_detail);

        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvMessage = findViewById(R.id.tvMessage);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);

        // Fetch tip data from Firebase
        String studyTipId = getIntent().getStringExtra("studyTipId");


        DatabaseReference database = FirebaseDatabase.getInstance().getReference("study_tips").child(studyTipId);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StudyTip tip = dataSnapshot.getValue(StudyTip.class);

                if (tip != null) {
                    tvTitle.setText(tip.getTitle());
                    tvAuthor.setText(tip.getAuthorName());
                    tvMessage.setText(tip.getMessage());

                    // Optionally, fetch profile image here
                    // ivProfilePicture.setImageURI(Uri.parse(tip.getAuthorProfilePictureUrl()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StudyTipDetailActivity.this, "Failed to load tip", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
