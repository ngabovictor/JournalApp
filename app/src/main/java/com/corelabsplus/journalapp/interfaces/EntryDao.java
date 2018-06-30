package com.corelabsplus.journalapp.interfaces;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.corelabsplus.journalapp.utils.Entry;

import java.util.List;

@Dao
public interface EntryDao {

    @Query("SELECT * FROM entries ORDER BY id DESC")
    LiveData<List<Entry>> getEntries();


    @Insert
    void addEntry(Entry entry);

    @Update
    void updateEntry(Entry entry);

    @Delete
    void deleteEntry(Entry entry);

    @Query("DELETE FROM entries")
    void deleteAllEntries();
}
