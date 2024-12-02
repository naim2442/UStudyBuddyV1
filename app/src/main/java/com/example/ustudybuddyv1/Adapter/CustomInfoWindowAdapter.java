package com.example.ustudybuddyv1.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.ustudybuddyv1.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;

    public CustomInfoWindowAdapter(LayoutInflater inflater) {
        mWindow = inflater.inflate(R.layout.custom_info_window, null);
    }

    private void renderWindowText(Marker marker, View view) {
        // Set the title
        TextView groupName = view.findViewById(R.id.text_group_name);
        groupName.setText(marker.getTitle());

        // Set additional snippet details
        TextView groupLocation = view.findViewById(R.id.text_group_location);
        TextView membersCount = view.findViewById(R.id.text_members_count);

        String snippet = marker.getSnippet();
        if (snippet != null) {
            String[] details = snippet.split("\\|");
            groupLocation.setText(details[0]); // Location
            membersCount.setText(details[1]); // Members count
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}
