package com.example.ustudybuddyv1.Utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.example.ustudybuddyv1.Model.Video;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.ArrayList;
import java.util.List;

public class YoutubeManager {
    private static final String API_KEY = "AIzaSyDb7mrQj7SpqtNRl9WMfi_17zTckwGg0QY";
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
    }

    @SuppressLint("StaticFieldLeak")
    public void searchVideos(String query, VideoSearchCallback callback) {
        new AsyncTask<Void, Void, List<Video>>() {
            @Override
            protected List<Video> doInBackground(Void... voids) {
                List<Video> videoList = new ArrayList<>();
                try {
                    YouTube.Search.List search = youtube.search().list("snippet");
                    search.setKey(API_KEY);
                    search.setQ(query); // The search query
                    search.setType("video");
                    search.setMaxResults(10L); // Number of videos to fetch

                    SearchListResponse response = search.execute();
                    List<SearchResult> results = response.getItems();

                    for (SearchResult result : results) {
                        String videoId = result.getId().getVideoId();
                        String title = result.getSnippet().getTitle();
                        String thumbnailUrl = result.getSnippet().getThumbnails().getDefault().getUrl();
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
