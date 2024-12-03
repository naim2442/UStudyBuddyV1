package com.example.ustudybuddyv1.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.Model.MotivationalQuote;
import com.example.ustudybuddyv1.R;

import java.util.List;

public class MotivationalQuotesAdapter extends RecyclerView.Adapter<MotivationalQuotesAdapter.MotivationalViewHolder> {

    private List<MotivationalQuote> motivationalQuoteList;

    public MotivationalQuotesAdapter(List<MotivationalQuote> motivationalQuoteList) {
        this.motivationalQuoteList = motivationalQuoteList;
    }

    @Override
    public MotivationalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_motivational_quotes, parent, false);
        return new MotivationalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MotivationalViewHolder holder, int position) {
        MotivationalQuote motivationalQuote = motivationalQuoteList.get(position);
        holder.tvQuote.setText(motivationalQuote.getQuote());
        holder.tvAuthor.setText("- " + motivationalQuote.getAuthor());
    }

    @Override
    public int getItemCount() {
        return motivationalQuoteList.size();
    }

    public static class MotivationalViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuote, tvAuthor;

        public MotivationalViewHolder(View itemView) {
            super(itemView);
            tvQuote = itemView.findViewById(R.id.tvQuote);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
        }
    }
}
