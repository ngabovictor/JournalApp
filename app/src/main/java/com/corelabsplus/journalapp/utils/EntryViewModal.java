package com.corelabsplus.journalapp.utils;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class EntryViewModal extends AndroidViewModel {

    private EntryRepository entryRepository;

    private LiveData<List<Entry>> allEntries;

    public EntryViewModal(@NonNull Application application) {
        super(application);
        entryRepository = new EntryRepository(application);

        allEntries = entryRepository.getAllEntries();

    }

    public LiveData<List<Entry>> getAllEntries(){
        return allEntries;
    }

    public void addEntry(Entry entry){
        entryRepository.addEntry(entry);
    }

    public void updateEntry(Entry entry){
        entryRepository.updateEntry(entry);
    }

    public void deleteEntry(Entry entry){
        entryRepository.deleteEntry(entry);
    }

    public void deleteAllEntries() { entryRepository.deleteAll();}
}

