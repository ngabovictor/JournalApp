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

    public void deleteAll(){
        new deleteAllAsyncTask(entryDao);
    }

    public void addEntry(Entry entry){
        new insertAsyncTask(entryDao).execute(entry);
    }

    public void updateEntry(Entry entry){
        new updateAsyncTask(entryDao).execute(entry);
    }

    public void deleteEntry(Entry entry){
        new deleteAsyncTask(entryDao).execute(entry);
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

    private static class updateAsyncTask extends AsyncTask<Entry, Void, Void>{
        private EntryDao asyncTaskDao;

        public updateAsyncTask(EntryDao asyncTaskDao) {
            this.asyncTaskDao = asyncTaskDao;
        }

        @Override
        protected Void doInBackground(Entry... entries) {
            asyncTaskDao.updateEntry(entries[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Entry, Void, Void>{
        private EntryDao entryDao;

        public deleteAsyncTask(EntryDao entryDao) {
            this.entryDao = entryDao;
        }

        @Override
        protected Void doInBackground(Entry... entries) {
            entryDao.deleteEntry(entries[0]);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void>{
        private EntryDao entryDao;

        public deleteAllAsyncTask(EntryDao entryDao){
            this.entryDao = entryDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            entryDao.deleteAllEntries();
            return null;
        }
    }
}
