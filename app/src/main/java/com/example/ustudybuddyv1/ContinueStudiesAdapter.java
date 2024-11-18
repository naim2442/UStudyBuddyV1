package com.example.ustudybuddyv1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ustudybuddyv1.Study;

import java.util.List;

public class ContinueStudiesAdapter extends RecyclerView.Adapter<ContinueStudiesAdapter.ViewHolder> {
    private List<Study> studies;

    public ContinueStudiesAdapter(List<Study> studies) {
        this.studies = studies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_continue_study, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Study study = studies.get(position);
        holder.studyTitle.setText(study.getTitle());
        holder.studyProgress.setText("Progress: " + study.getProgress() + "%");

        holder.itemView.setOnClickListener(v -> {
            // Handle continue studying click, e.g., open the study resource
        });
    }

    @Override
    public int getItemCount() {
        return studies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView studyTitle;
        TextView studyProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            studyTitle = itemView.findViewById(R.id.study_title);
            studyProgress = itemView.findViewById(R.id.study_progress);
        }
    }
}
