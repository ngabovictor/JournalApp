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
}
