package com.example.ustudybuddyv1.Adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.Activity.StudyTipDetailActivity;
import com.example.ustudybuddyv1.Model.StudyTip;
import com.example.ustudybuddyv1.R;
import java.util.ArrayList;
import java.util.List;

public class StudyTipAdapter extends RecyclerView.Adapter<StudyTipAdapter.StudyTipViewHolder> {

    private List<StudyTip> studyTips = new ArrayList<>(); // List to store the study tips

    // Constructor (optional, if you're initializing with data)
    public StudyTipAdapter(List<StudyTip> studyTips) {
        this.studyTips = studyTips;
    }

    // Method to update the data (called in your fragment)
    public void setStudyTips(List<StudyTip> studyTips) {
        this.studyTips = studyTips;
        notifyDataSetChanged(); // This will refresh the RecyclerView when the list is updated
    }

    @Override
    public StudyTipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_tip, parent, false);
        return new StudyTipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudyTipViewHolder holder, int position) {
        StudyTip studyTip = studyTips.get(position);
        holder.tvTitle.setText(studyTip.getTitle());
        holder.tvAuthor.setText(studyTip.getAuthorName());


//        // Make the LinearLayout clickable
//        holder.itemView.setOnClickListener(v -> {
//            // Create an Intent to navigate to the StudyTipDetailActivity
//            Intent intent = new Intent(v.getContext(), StudyTipDetailActivity.class);
//
//            // Pass the study tip ID or other necessary data to the detail activity
//            intent.putExtra("studyTipId", studyTip.getId());
//            v.getContext().startActivity(intent);
//        });

    }


    @Override
    public int getItemCount() {
        return studyTips != null ? studyTips.size() : 0;
    }

    public static class StudyTipViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor;

        public StudyTipViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
        }
    }
}