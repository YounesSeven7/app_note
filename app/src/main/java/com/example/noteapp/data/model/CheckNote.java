package com.example.noteapp.data.model;

import androidx.room.Entity;
import java.io.Serializable;

@Entity(tableName = "check_note_table")
public class CheckNote extends JustNote implements Serializable {

    boolean didCheck;

    public CheckNote(long id, String text, int color, boolean didCheck) {
        super(id, text, color);
        this.didCheck = didCheck;
    }

    public boolean getDidCheck() {
        return didCheck;
    }

    public void setDidCheck(boolean didCheck) {
        this.didCheck = didCheck;
    }
}
