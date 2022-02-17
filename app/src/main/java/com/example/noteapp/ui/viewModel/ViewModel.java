package com.example.noteapp.ui.viewModel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.noteapp.data.database.Database;
import com.example.noteapp.data.database.NoteEntity;
import com.example.noteapp.data.repository.Repository;
import com.example.noteapp.ui.listeners.OnGetSearchNotes;

import java.util.List;

public class ViewModel extends AndroidViewModel {

    private Repository repository;
    private Context context;
    public boolean doingAnimation = true;

    public ViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
    }

    public void insert(NoteEntity noteEntity) {
        doingAnimation = true;
        repository.insert(noteEntity);
    }

    public void insert (List<NoteEntity> noteEntities) {
        repository.insert(noteEntities);
    }

    public LiveData<List<NoteEntity>> getAllNote() {
        return repository.getAllNote();
    }

    public void  getSearchNote(OnGetSearchNotes onGetSearchNotes, String text) {
        repository.getSearchNote(onGetSearchNotes, text);
    }

    public void getSearchNotes(OnGetSearchNotes onGetSearchNotes, int color, int type, String text) {
        repository.getSearchNotes(onGetSearchNotes, color, type, text);
    }

    public void delete(NoteEntity noteEntity) {
        doingAnimation = true;
        repository.deleteThisNote(noteEntity);
    }

    public void deleteAll(List<NoteEntity> noteEntities) {
        repository.deleteAll(noteEntities);
    }


    public int position = -1;
    // this method save case of checked and update case from database.
    public void update(NoteEntity noteEntity, int position) {
        repository.update(noteEntity);
        this.position = position;
    }

    public void update(NoteEntity noteEntity) {
        repository.update(noteEntity);
    }


    public void update(List<NoteEntity> noteEntities) {
        repository.update(noteEntities);
    }




}
