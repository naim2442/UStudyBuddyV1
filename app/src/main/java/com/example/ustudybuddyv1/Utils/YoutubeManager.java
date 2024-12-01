package com.example.ustudybuddyv1.Utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ustudybuddyv1.Model.Video;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.ArrayList;
import java.util.List;

public class YoutubeManager {

    private static final String API_KEY = "AIzaSyDb7mrQj7SpqtNRl9WMfi_17zTckwGg0QY";  // Use your API key
    private static final String APPLICATION_NAME = "UStudyBuddy";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private YouTube youtube;

    public YoutubeManager() {
        try {
            youtube = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    null
            ).setApplicationName(APPLICATION_NAME).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Log the API Key
            Log.d("YoutubeManager", "API Key: " + API_KEY);

            youtube = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    null
            ).setApplicationName(APPLICATION_NAME).build();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("YoutubeManager", "Error initializing YouTube service: " + e.getMessage());
        }


    }

    public void fetchVideosFromPlaylist(String playlistId, final VideoSearchCallback callback) {
        new AsyncTask<Void, Void, List<Video>>() {
            @Override
            protected List<Video> doInBackground(Void... voids) {
                List<Video> videoList = new ArrayList<>();
                try {
                    YouTube.PlaylistItems.List request = youtube.playlistItems().list("snippet");
                    request.setKey(API_KEY);
                    request.setPlaylistId("RDQMGvnpkZz7INY"); // Set the playlist ID here
                    request.setMaxResults(10L);  // Limit to 10 videos (or change as needed)

                    PlaylistItemListResponse response = request.execute();
                    List<PlaylistItem> items = response.getItems();

                    for (PlaylistItem item : items) {
                        String videoId = item.getSnippet().getResourceId().getVideoId();
                        String title = item.getSnippet().getTitle();
                        String thumbnailUrl = item.getSnippet().getThumbnails().getDefault().getUrl();
                        videoList.add(new Video(title, "https://www.youtube.com/watch?v=" + videoId, thumbnailUrl));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return videoList;
            }

            @Override
            protected void onPostExecute(List<Video> videoList) {
                callback.onVideosFetched(videoList);
            }
        }.execute();
    }

    public interface VideoSearchCallback {
        void onVideosFetched(List<Video> videos);
    }
}
