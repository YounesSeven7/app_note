package com.example.noteapp.ui.listeners;

import android.widget.TextView;

import com.example.noteapp.data.database.NoteEntity;
import com.example.noteapp.ui.adapter.NotesAdapter;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public interface OnItemListenerChangeMenuItem {
    public boolean onItemAfterFirstLongClickListener(boolean add);
    public void onItemFirstLongClickListener();
    public void onItemUpdateClickListener (NoteEntity noteEntity, int position);
}
