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
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
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
    private List<Entry> syncedEntries = new ArrayList<>();
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

        if (savedInstanceState != null){
            entries = (List<Entry>) savedInstanceState.getSerializable("entries");
        }

        context = this;
        entryViewModal = ViewModelProviders.of(this).get(EntryViewModal.class);
        isFromLogin = getIntent().getBooleanExtra("isFromLogin", false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_db));

        setSupportActionBar(toolbar);

        adapter = new EntriesAdapter(entries, context);

        entriesRecyclerView.setHasFixedSize(true);
        entriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Swipe to delete

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                adapter.notifyItemRemoved(position);
                Entry entryToRemove = entries.get(position);

                //delete the swipped entry
                entryViewModal.deleteEntry(entryToRemove);

                //remove the selected entry from the entries list
                entries.remove(position);

                if (entryToRemove.getSynced().equals(getString(R.string.synced_true))){
                    deleteFromCloud(entryToRemove.getId());
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(entriesRecyclerView);

        // Check if the previous activity is LoginActivity

        if (isFromLogin){
            entries = (List<Entry>) getIntent().getSerializableExtra("entries");

            if (entries.size() > 0){
                saveToLocal(entries);
            }
        }


        //Get all entries from local
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

    //Get Entries from Local DB
    public void getEntries(){
        entryViewModal.getAllEntries().observe(this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> lEntries) {
                entries = lEntries;

                adapter.setEntries(entries);
                entriesRecyclerView.setAdapter(adapter);

                if (entries.size() < 1){
                    empty.setVisibility(View.VISIBLE);
                }

                else {
                    empty.setVisibility(View.GONE);
                }

                syncEntries(lEntries);
            }
        });
    }

    //Delete from cloud
    public void deleteFromCloud(int id){
        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child(getString(R.string.entries_dir)).child(String.valueOf(id)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, getString(R.string.sync_success), Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(context, getString(R.string.sync_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Sync Entries to Firebase

    private void syncEntries(final List<Entry> sEntries) {

        for (final Entry entry : sEntries) {
            if (entry.getSynced().equals(getString(R.string.synced_false))){

                entry.setSynced(getString(R.string.synced_true));

                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child(getString(R.string.entries_dir)).child(String.valueOf(entry.getId())).setValue(entry).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            syncedEntries.add(entry);
                            Toast.makeText(EntriesActivity.this, getString(R.string.sync_success), Toast.LENGTH_SHORT).show();
                        } else {
                            entry.setSynced(getString(R.string.synced_false));
                            Toast.makeText(EntriesActivity.this, getString(R.string.sync_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EntriesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        syncToLocal(syncedEntries);
        syncedEntries.clear();
    }




    //Save instances

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("entries", (Serializable) entries);
    }

    //On Restore intances

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        entries = (List<Entry>) savedInstanceState.getSerializable("entries");
    }


    //Sync to local

    public void syncToLocal(List<Entry> entries){
        for (Entry entry : entries){
            entryViewModal.updateEntry(entry);
        }

        Toast.makeText(context, getString(R.string.sync_success), Toast.LENGTH_SHORT).show();
    }

    //Save to local
    public void saveToLocal(List<Entry> entriesTolocal){
        for (Entry entry : entriesTolocal){
            entryViewModal.addEntry(entry);
        }

        Toast.makeText(context, getString(R.string.sync_success), Toast.LENGTH_SHORT).show();
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
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout){
            
            entryViewModal.deleteAllEntries();

            mAuth.signOut();

            Intent intent = new Intent(context, SplashActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
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
            entriesRecyclerView.setAdapter(adapter);

        }

        else {
            adapter.setEntries(entries);
            entriesRecyclerView.setAdapter(adapter);
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
