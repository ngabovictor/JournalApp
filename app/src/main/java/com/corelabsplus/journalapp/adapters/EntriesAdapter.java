package com.corelabsplus.journalapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.corelabsplus.journalapp.R;
import com.corelabsplus.journalapp.utils.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EntriesAdapter extends RecyclerView.Adapter<EntriesAdapter.ViewHolder> implements Filterable{

    private List<Entry> entries = new ArrayList<>();
    private Context context;

    public EntriesAdapter(List<Entry> entries, Context context) {
        this.entries = entries;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.entry_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.label_color) RelativeLayout labelColor;
        @BindView(R.id.entry_title) TextView entryTitleView;
        @BindView(R.id.entry_caption) TextView entryCaption;
        @BindView(R.id.created) TextView entryCreated;
        @BindView(R.id.modified) TextView entryModied;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(context, itemView);
        }
    }

    //Generate random color

    public int getColor(){
        Random random = new Random();

        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}
