package com.corelabsplus.journalapp.activities;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.corelabsplus.journalapp.R;
import com.corelabsplus.journalapp.adapters.EntriesAdapter;
import com.corelabsplus.journalapp.utils.Entry;
import com.corelabsplus.journalapp.utils.EntryViewModal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    @BindView(R.id.empty) RelativeLayout empty;
    @BindView(R.id.new_entry_fab) FloatingActionButton newEntryFab;
    @BindView(R.id.toolbar) Toolbar toolbar;

    //Creating fields

    private List<Entry> entries = new ArrayList<>();
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private EntryViewModal entryViewModal;
    private EntriesAdapter adapter;

    private static final int NEW_ENTRY_ADD_REQUEST_CODE = 1;
    private static final int UPDATE_ENTRY_REQUEST_CODE = 2;
    private boolean isFromLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entries);
        ButterKnife.bind(this);

        context = this;
        entryViewModal = ViewModelProviders.of(this).get(EntryViewModal.class);
        isFromLogin = getIntent().getBooleanExtra("isFromLogin", false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_db));

        setSupportActionBar(toolbar);

        adapter = new EntriesAdapter(entries, context);

        entriesRecyclerView.setHasFixedSize(true);
        entriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        entriesRecyclerView.setAdapter(adapter);

        getEntries();

        newEntryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EntryActivity.class);
                intent.putExtra("newEntry", true);
                startActivityForResult(intent, NEW_ENTRY_ADD_REQUEST_CODE);
            }
        });
    }

    private void getEntries() {

        if (isFromLogin){
            entries = (List<Entry>) getIntent().getSerializableExtra("entries");

            if (entries.size() > 0){
                saveToLocal(entries);
            }
        }

        entryViewModal.getAllEntries().observe(this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                adapter.setEntries(entries);

                if (entries.size() < 1){
                    empty.setVisibility(View.VISIBLE);
                }

                else {
                    empty.setVisibility(View.GONE);
                }

                syncEntries(entries);
            }
        });

    }

    private void syncEntries(List<Entry> entries) {
        for (final Entry entry : entries){

            if (entry.getSynced().equals(getString(R.string.synced_false))) {

                entry.setSynced(getString(R.string.synced_true));

                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child(getString(R.string.entries_dir)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child(getString(R.string.entries_dir)).push().setValue(entry).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        entryViewModal.updateEntry(entry);
                                    } else {
                                        entry.setSynced(getString(R.string.synced_false));
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }

        Toast.makeText(this, "All entries have been synced successfully", Toast.LENGTH_SHORT).show();
    }


    public void saveToLocal(List<Entry> entriesTolocal){
        for (Entry entry : entriesTolocal){
            entryViewModal.addEntry(entry);
        }

        Toast.makeText(context, "Successfully saved to local", Toast.LENGTH_SHORT).show();
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

            adapter.setEntries(filteredEntries);

        }

        else {
            adapter.setEntries(entries);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Entry entry = (Entry) data.getSerializableExtra("entry");

        if (resultCode == NEW_ENTRY_ADD_REQUEST_CODE){

            entryViewModal.addEntry(entry);
        }

        else if (resultCode == UPDATE_ENTRY_REQUEST_CODE){

            entryViewModal.updateEntry(entry);
        }
    }
}
