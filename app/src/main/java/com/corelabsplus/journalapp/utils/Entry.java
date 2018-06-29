package com.corelabsplus.journalapp.utils;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "entries")
public class Entry implements Serializable{

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "caption")
    private String tags;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "date_created")
    private String created;

    @ColumnInfo(name = "date_modified")
    private String modified;

    @ColumnInfo(name = "synced")
    private String synced = "false";


    public Entry() {
    }

    public Entry(@NonNull int id, String title, String tags, String content, String created, String modified, String synced) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.content = content;
        this.created = created;
        this.modified = modified;
        this.synced = synced;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getSynced() {
        return synced;
    }

    public void setSynced(String synced) {
        this.synced = synced;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
