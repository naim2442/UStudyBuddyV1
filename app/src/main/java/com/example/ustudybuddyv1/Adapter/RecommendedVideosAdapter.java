package com.example.ustudybuddyv1.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ustudybuddyv1.Model.Video;
import com.example.ustudybuddyv1.R;

import java.util.List;

public class RecommendedVideosAdapter extends RecyclerView.Adapter<RecommendedVideosAdapter.VideoViewHolder> {

    private List<Video> videos;

    public RecommendedVideosAdapter(List<Video> videos) {
        this.videos = videos;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        Video video = videos.get(position);
        holder.videoTitle.setText(video.getTitle());
        Glide.with(holder.itemView.getContext())
                .load(video.getThumbnailUrl())
                .into(holder.videoThumbnail);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        TextView videoTitle;
        ImageView videoThumbnail;

        public VideoViewHolder(View itemView) {
            super(itemView);
            videoTitle = itemView.findViewById(R.id.video_title);
            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
        }
    }
}
