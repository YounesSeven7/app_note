package com.example.noteapp.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
@Entity(tableName = "just_note_table")
public class JustNote implements Serializable {

    @PrimaryKey(autoGenerate = false)
    long id;
    @NonNull
    String text;
    int color;

    public JustNote(long id, @NonNull String text, int color) {
        this.id = id;
        this.text = text;
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
