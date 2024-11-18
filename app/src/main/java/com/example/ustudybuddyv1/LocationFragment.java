package com.example.ustudybuddyv1;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LocationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        // Set up any UI elements here (e.g., TextView)
        TextView locationInfo = view.findViewById(R.id.location_info);
        // You can set the current location info or any other data here
        locationInfo.setText("Current Location: [Your Location Here]");

        return view;
    }
}
