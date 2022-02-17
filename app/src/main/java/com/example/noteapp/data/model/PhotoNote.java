package com.example.noteapp.data.model;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Entity;

import java.io.Serializable;
@Entity(tableName = "photo_note_table")
public class PhotoNote extends JustNote implements Serializable {
    @NonNull
    String imageUri;

    public PhotoNote(long id, String text, int color, String imageUri) {
        super(id, text, color);
        this.imageUri = imageUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
