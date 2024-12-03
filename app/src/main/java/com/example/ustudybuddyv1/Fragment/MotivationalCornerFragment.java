package com.example.ustudybuddyv1.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.Adapter.MotivationalQuotesAdapter;
import com.example.ustudybuddyv1.Model.MotivationalQuote;
import com.example.ustudybuddyv1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MotivationalCornerFragment extends Fragment {

    private RecyclerView recyclerView;
    private MotivationalQuotesAdapter motivationalQuotesAdapter;
    private List<MotivationalQuote> motivationalQuoteList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_motivational_corner, container, false);

        recyclerView = view.findViewById(R.id.rvMotivationalQuotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        motivationalQuoteList = new ArrayList<>();
        motivationalQuotesAdapter = new MotivationalQuotesAdapter(motivationalQuoteList);
        recyclerView.setAdapter(motivationalQuotesAdapter);

        // Fetch motivational quotes from Firebase
        fetchMotivationalQuotes();

        return view;
    }

    private void fetchMotivationalQuotes() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("motivational_quotes");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                motivationalQuoteList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MotivationalQuote motivationalQuote = snapshot.getValue(MotivationalQuote.class);
                    motivationalQuoteList.add(motivationalQuote);
                }
                motivationalQuotesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
