package com.example.ustudybuddyv1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecommendedVideosAdapter extends RecyclerView.Adapter<RecommendedVideosAdapter.ViewHolder> {
    private List<Video> videos; // Use the custom Video class

    public RecommendedVideosAdapter(List<Video> videos) {
        this.videos = videos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video video = videos.get(position);
        holder.title.setText(video.getTitle());
        holder.thumbnail.setImageResource(video.getThumbnailResId());

        holder.itemView.setOnClickListener(v -> {
            // Handle video click, e.g., start a new activity or fragment to play the video
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView thumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.video_title);
            thumbnail = itemView.findViewById(R.id.video_thumbnail);
        }
    }
}
