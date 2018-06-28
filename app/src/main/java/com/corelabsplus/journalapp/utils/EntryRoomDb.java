package com.corelabsplus.journalapp.utils;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.corelabsplus.journalapp.interfaces.EntryDao;

@Database(entities = {Entry.class}, version = 2)
public abstract class EntryRoomDb extends RoomDatabase {

    public abstract EntryDao entryDao();

    private static EntryRoomDb INSTANCE;

    static EntryRoomDb getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (EntryRoomDb.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), EntryRoomDb.class, "entries_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }

        return INSTANCE;
    }



    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            new PopulateDbAsync(INSTANCE).execute();
        }
    };



    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void>{

        private final EntryDao entryDao;

        PopulateDbAsync(EntryRoomDb db){
            entryDao = db.entryDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            return null;
        }
    }
}
