package com.example.noteapp.ui.listeners;

import com.example.noteapp.data.database.NoteEntity;

import java.util.List;

public interface OnGetSearchNotes {
    public void onGetSearchNotes(List<NoteEntity> noteEntities);
}
