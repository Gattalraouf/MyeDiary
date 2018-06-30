package com.gattal.asta.myediary.dataBase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity (tableName = "diary")
public class DiaryEntry {

    @PrimaryKey (autoGenerate = true)
    private
    int id;

    @NonNull
    private String uid;
    private Date date;
    private String title;
    private String content;


    public DiaryEntry(int id, @NonNull String uid, Date date, String title, String content) {
        this.id = id;
        this.uid = uid;
        this.date = date;
        this.title = title;
        this.content = content;
    }

    @Ignore
    public DiaryEntry( @NonNull String uid, Date date, String title, String content) {
        this.uid = uid;
        this.date = date;
        this.title = title;
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }
}
