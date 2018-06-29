package com.corelabsplus.journalapp.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.corelabsplus.journalapp.R;
import com.corelabsplus.journalapp.utils.Entry;
import com.corelabsplus.journalapp.utils.EntryViewModal;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EntryActivity extends AppCompatActivity implements TextWatcher {

    //BINDING VIEWS

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.entry_title) MaterialEditText titleField;
    @BindView(R.id.entry_tags) MaterialEditText tagsField;
    @BindView(R.id.editor) MaterialEditText contentField;



    //FIELDS

    private Context context;
    private boolean isNewEntry;

    private String title, caption, content, created, modified, synced;
    int id;
    private Entry entry = new Entry();

    private static final int NEW_ENTRY_ADD_REQUEST_CODE = 1;
    private static final int UPDATE_ENTRY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        ButterKnife.bind(this);
        context = this;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        isNewEntry = getIntent().getBooleanExtra("newEntry", true);

        if (isNewEntry){
            getSupportActionBar().setTitle("Untitled Entry");
        }

        else{
            getEntry();
        }

        titleField.addTextChangedListener(this);
        tagsField.addTextChangedListener(this);
        contentField.addTextChangedListener(this);

    }

    //GET CURRENT ENTRY AND SET TO VIEWS
    private void getEntry() {
        entry = (Entry) getIntent().getSerializableExtra("entry");
        title = entry.getTitle();
        content = entry.getContent();
        synced = entry.getSynced();
        created = entry.getCreated();
        modified = entry.getModified();
        id = entry.getId();
        caption = entry.getTags();

        getSupportActionBar().setTitle(title);

        titleField.setText(title);
        tagsField.setText(caption);
        contentField.setText(content);

    }





    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    //BACK BUTTON PRESSED
    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("entry", entry);

        if (isNewEntry){
            setResult(NEW_ENTRY_ADD_REQUEST_CODE, intent);
        }

        else {
            setResult(UPDATE_ENTRY_REQUEST_CODE, intent);
        }

        finish();
    }





    //TEXT WATCHER TO AUTO SAVE

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        title = titleField.getText().toString().trim();
        caption = tagsField.getText().toString().trim();
        content = contentField.getText().toString().trim();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Date date = new Date();
        modified = dateFormat.format(date);
        created = dateFormat.format(date);

        synced = "false";


        if (title.length() == 0){
            title = "Untitled";
        }

        else if (caption.length() == 0){
            caption = "No tags";
        }

        else if (content.length() == 0){
            content = "No content";
        }

        getSupportActionBar().setTitle(title);

        if (!isNewEntry){

            entry.setTitle(title);
            entry.setContent(content);
            entry.setSynced(synced);
            entry.setTags(caption);
            entry.setModified(modified);

//            entryViewModal.updateEntry(entry);
//            Toast.makeText(context, "saved", Toast.LENGTH_SHORT).show();
        }

        else if (isNewEntry){
            entry.setTitle(title);
            entry.setSynced(synced);
            entry.setModified(modified);
            entry.setContent(content);
            entry.setCreated(created);
            entry.setTags(caption);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}

