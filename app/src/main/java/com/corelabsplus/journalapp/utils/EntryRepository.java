package com.corelabsplus.journalapp.utils;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.corelabsplus.journalapp.interfaces.EntryDao;

import java.util.List;

public class EntryRepository {
    private EntryDao entryDao;

    private LiveData<List<Entry>> allEntries;

    EntryRepository(Application application){
        EntryRoomDb db = EntryRoomDb.getDatabase(application);
        entryDao = db.entryDao();
        allEntries = entryDao.getEntries();
    }

    LiveData<List<Entry>> getAllEntries(){
        return allEntries;
    }

    public void addEntry(Entry entry){
        new insertAsyncTask(entryDao).execute(entry);
    }

    private static class insertAsyncTask extends AsyncTask<Entry, Void, Void>{

        private EntryDao asyncTaskDao;

        public insertAsyncTask(EntryDao asyncTaskDao) {
            this.asyncTaskDao = asyncTaskDao;
        }


        @Override
        protected Void doInBackground(final Entry... entries) {
            asyncTaskDao.addEntry(entries[0]);
            return null;
        }
    }
}
