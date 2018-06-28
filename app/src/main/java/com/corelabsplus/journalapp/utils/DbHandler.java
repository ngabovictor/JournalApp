package com.corelabsplus.journalapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victor on 6/27/18.
 */

public class DbHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "entries.db";
    private static final String TABLE_ENTRIES = "entries";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TIME_CREATED = "created";
    private static final String COLUMN_TIME_MODIFIED = "modified";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_CAPTION = "caption";
    private static final String COLUMN_SYNCED = "synced";

    public DbHandler(Context context) {
        super(context, TABLE_ENTRIES, null, DATABASE_VERSION);
    }

//    public DbHandler(Context context) {
//        super(context, TABLE_ENTRIES, null, 1);
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_ENTRIES +  "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_TIME_CREATED + " TEXT, " +
                COLUMN_TIME_MODIFIED + " TEXT, " +
                COLUMN_CAPTION + " TEXT, " +
                COLUMN_SYNCED + " TEXT, " +
                COLUMN_CONTENT + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);
        onCreate(db);
    }

//    Adding a new note

    public boolean addEntry(Entry item){
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, item.getTitle());
        values.put(COLUMN_TIME_CREATED, item.getCreated());
        values.put(COLUMN_TIME_MODIFIED, item.getModified());
        values.put(COLUMN_CAPTION, item.getTags());
        values.put(COLUMN_SYNCED, item.getSynced());
        values.put(COLUMN_CONTENT, item.getContent());

        SQLiteDatabase db = getWritableDatabase();

        long result = db.insert(TABLE_ENTRIES, null, values);


        if (result == -1){
            return false;
        }

        else {
            return true;
        }
    }

//    Editing a note

    public void updateEntry(Entry item, int id){
//        String query = "UPDATE " + TABLE_ENTRIES +
//                " SET " + COLUMN_TITLE + " =  " + item.getTitle() +
//                ", " + COLUMN_TIME_CREATED + " = " + item.getTime() +
//                ", " + COLUMN_DATA + " = " + item.getData() +
//                " WHERE "+ COLUMN_ID + " = " + id;
//
//        SQLiteDatabase db = getWritableDatabase();
//        db.execSQL(query);

        //deleteNote(id);

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, item.getTitle());
        values.put(COLUMN_TIME_MODIFIED, item.getModified());
        values.put(COLUMN_CAPTION, item.getTags());
        values.put(COLUMN_SYNCED, item.getSynced());
        values.put(COLUMN_CONTENT, item.getContent());

        SQLiteDatabase db = getWritableDatabase();
        //db.update(TABLE_ENTRIES, values, COLUMN_ID + "=?", id);
        //deleteEntry(id);

        long result = db.update(TABLE_ENTRIES, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});


//        if (result == -1){
//            Toast.makeText(getCon, "", Toast.LENGTH_SHORT).show();
//        }
//
//        else {
//            return true;
//        }
    }

//    Delete a note

    public void deleteEntry(int nId){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ENTRIES + " WHERE " + COLUMN_ID + " = " + nId + ";");
    }

    //    Get Notes
    public Cursor getEntries(){
        List<Entry> result = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_ENTRIES + ";";
        //db.execSQL(query);

        Cursor cursor = db.rawQuery(query, null);


        return cursor;
    }
}
