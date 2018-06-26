package com.corelabsplus.journalapp.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.corelabsplus.journalapp.R;
import com.corelabsplus.journalapp.adapters.EntriesAdapter;
import com.corelabsplus.journalapp.utils.Entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EntriesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    //Binding Views

    @BindView(R.id.entries_recycler_view) RecyclerView entriesRecyclerView;
    @BindView(R.id.empty) ImageView empty;
    @BindView(R.id.new_entry_fab) FloatingActionButton newEntryFab;
    @BindView(R.id.toolbar) Toolbar toolbar;

    //Creating fields

    private List<Entry> entries = new ArrayList<>();
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entries);
        ButterKnife.bind(this);

        context = this;
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_db));

        setSupportActionBar(toolbar);

        entriesRecyclerView.setHasFixedSize(true);
        entriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getEntries();

        newEntryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EntryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getEntries() {
        for (int i = 0; i < 100; i++) {
            Entry entry = new Entry();

            entry.setTitle("Title " + String.valueOf(i));
            entry.setTags("Caption " + String.valueOf(i));
            entry.setCreated("June 25, 2018 3:00PM");
            entry.setModified("June 25, 2018 3:00PM");
            entry.setContent(getString(R.string.lorem));

            entries.add(entry);

            if (entries.size() < 1){
                empty.setVisibility(View.VISIBLE);
            }

            else {
                empty.setVisibility(View.GONE);

                EntriesAdapter adapter = new EntriesAdapter(entries, context);
                entriesRecyclerView.setAdapter(adapter);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.entries_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) EntriesActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(EntriesActivity.this.getComponentName()));
            searchView.setOnQueryTextListener(this);

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextChange(String query) {

        if (query.length() > 0){
            List<Entry> filteredEntries = new ArrayList<>();
            String queryLowerCase = query.toLowerCase();

            for (Entry entry : entries){
                if (entry.getTitle().toLowerCase().contains(queryLowerCase) ||
                        entry.getTags().contains(queryLowerCase) ||
                        entry.getContent().contains(queryLowerCase) ||
                        entry.getCreated().contains(queryLowerCase) ||
                        entry.getModified().contains(queryLowerCase)){
                    filteredEntries.add(entry);
                }
            }

            EntriesAdapter adapter = new EntriesAdapter(filteredEntries, context);
            entriesRecyclerView.setAdapter(adapter);

        }

        else {
            EntriesAdapter adapter = new EntriesAdapter(entries, context);
            entriesRecyclerView.setAdapter(adapter);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}
