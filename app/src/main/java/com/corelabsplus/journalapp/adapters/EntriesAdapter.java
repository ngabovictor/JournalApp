package com.corelabsplus.journalapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.corelabsplus.journalapp.R;
import com.corelabsplus.journalapp.activities.EntryActivity;
import com.corelabsplus.journalapp.utils.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EntriesAdapter extends RecyclerView.Adapter<EntriesAdapter.ViewHolder>{

    private List<Entry> entries = new ArrayList<>();
    private Context context;

    private static final int UPDATE_ENTRY_REQUEST_CODE = 2;

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

        final Entry entry = entries.get(position);

        String title, caption, created, modified;

        title = entry.getTitle();
        caption = entry.getTags();
        created = "Created " + entry.getCreated();
        modified = "Modified " + entry.getModified();

        //Binding data to views

        holder.entryTitleView.setText(title);
        holder.entryCaption.setText(caption);
        holder.entryCreated.setText(created);
        holder.entryModified.setText(modified);

        //Setting labelColor to random color

        holder.labelColor.setBackgroundColor(getColor());

        //On item click listener

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EntryActivity.class);
                intent.putExtra("entry", entry);
                intent.putExtra("newEntry", false);
                ((Activity) context).startActivityForResult(intent, UPDATE_ENTRY_REQUEST_CODE);
            }
        });

        //On item swipped right

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });

    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.label_color) RelativeLayout labelColor;
        @BindView(R.id.entry_title) TextView entryTitleView;
        @BindView(R.id.entry_caption) TextView entryCaption;
        @BindView(R.id.created) TextView entryCreated;
        @BindView(R.id.modified) TextView entryModified;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //Generate random color

    public int getColor(){
        Random random = new Random();

        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}
