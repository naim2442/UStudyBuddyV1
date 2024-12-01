package com.example.ustudybuddyv1.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ustudybuddyv1.Adapter.RecommendedVideosAdapter;
import com.example.ustudybuddyv1.Model.Video;
import com.example.ustudybuddyv1.R;
import com.example.ustudybuddyv1.Utils.YoutubeManager;

import java.util.ArrayList;
import java.util.List;

public class AllVideosFragment extends Fragment {

    private RecyclerView recyclerViewAllVideos;

    ArrayList<Video> arrayListVideos;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_videos, container, false);

        // Initialize RecyclerView
        recyclerViewAllVideos = view.findViewById(R.id.recycler_view_all_videos);
        recyclerViewAllVideos.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Fetch videos using YoutubeManager
        YoutubeManager youtubeManager = new YoutubeManager();
        String playlistId = "RDQMGvnpkZz7INY";  // Replace with your playlist ID
        youtubeManager.fetchVideosFromPlaylist(playlistId, new YoutubeManager.VideoSearchCallback() {
            @Override
            public void onVideosFetched(List<Video> videos) {
                if (videos != null && !videos.isEmpty()) {
                    // Use the RecommendedVideosAdapter to display videos
                    RecommendedVideosAdapter videoAdapter = new RecommendedVideosAdapter(videos);
                    recyclerViewAllVideos.setAdapter(videoAdapter);
                }
            }
        });


        return view;
    }
}
