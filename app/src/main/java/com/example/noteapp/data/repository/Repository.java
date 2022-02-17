package com.example.noteapp.data.repository;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import androidx.lifecycle.LiveData;


import com.example.noteapp.data.database.Database;
import com.example.noteapp.data.database.NoteDao;
import com.example.noteapp.data.database.NoteEntity;
import com.example.noteapp.ui.activity.AddAndChangeNote;
import com.example.noteapp.ui.listeners.OnGetSearchNotes;

import java.util.ArrayList;
import java.util.List;

public class Repository {

    NoteDao noteDao;
    public Repository(Application application) {
        Database database = Database.getInstance(application);
        noteDao = database.getNoteDao();
    }

    public void insert(NoteEntity noteEntity) {
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                noteDao.insert(noteEntity);
            }
        });
    }

    public void insert (List<NoteEntity> noteEntities) {
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < noteEntities.size(); i++) {
                    noteEntities.get(i).setId(0);
                }
                noteDao.insert(noteEntities);
                noteEntities.clear();
            }
        });
    }

    public LiveData<List<NoteEntity>> getAllNote() {
        return noteDao.getAllNote();
    }

    public void getSearchNote(OnGetSearchNotes onGetSearchNotes, String text) {
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                onGetSearchNotes.onGetSearchNotes(noteDao.getSearchNote(text));
            }
        });
        return ;
    }

    public void getSearchNotes(OnGetSearchNotes onGetSearchNotes, int color, int type, String text) {
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                searchNotes(onGetSearchNotes, color, type, text);
            }
        });
    }
    void searchNotes(OnGetSearchNotes onGetSearchNotes, int color, int type, String text) {
        switch (type){
            case AddAndChangeNote.JUST_NOTE:
                if (color == AddAndChangeNote.COLOR_RED || color == AddAndChangeNote.COLOR_YELLOW || color == AddAndChangeNote.COLOR_BLUE){
                    if (TextUtils.isEmpty(text)) {
                        onGetSearchNotes.onGetSearchNotes(noteDao.getAllJustTextNotesWithColorWithoutText(color));
                    } else {
                        onGetSearchNotes.onGetSearchNotes(noteDao.getAllJustTextNotesWithColorWithText(color, text));
                    }

                } else {
                    if (TextUtils.isEmpty(text)) {
                        onGetSearchNotes.onGetSearchNotes(noteDao.getAllJustTextNotesWithoutColorWithoutText());
                    } else {
                        onGetSearchNotes.onGetSearchNotes(noteDao.getAllJustTextNotesWithoutColorWithText(text));
                    }

                }
                break;
            case AddAndChangeNote.CHECK_NOTE:
                if (color == AddAndChangeNote.COLOR_RED || color == AddAndChangeNote.COLOR_YELLOW || color == AddAndChangeNote.COLOR_BLUE){
                    if (TextUtils.isEmpty(text)) {
                        onGetSearchNotes.onGetSearchNotes(noteDao.getAllCheckNotesWithColorWithoutText(color));
                    } else {
                        onGetSearchNotes.onGetSearchNotes(noteDao.getAllCheckNotesWithColorWithText(color, text));
                    }

                } else {
                    if (TextUtils.isEmpty(text)) {
                        onGetSearchNotes.onGetSearchNotes(noteDao.getAllCheckNotesWithoutColorWithoutText());
                    } else {
                        onGetSearchNotes.onGetSearchNotes(noteDao.getAllCheckNotesWithoutColorWithText(text));
                    }

                }
                break;
            case AddAndChangeNote.PHOTO_NOTE:
                if (color == AddAndChangeNote.COLOR_RED || color == AddAndChangeNote.COLOR_YELLOW || color == AddAndChangeNote.COLOR_BLUE){
                    if (TextUtils.isEmpty(text)) {
                        onGetSearchNotes.onGetSearchNotes(noteDao.getAllPhotoNotesWithColorWithoutText(color));
                    } else {
                        Log.d("younes", "younes");
                        onGetSearchNotes.onGetSearchNotes(noteDao.getAllPhotoNotesWithColorWithText(color, text));
                    }

                } else {
                    if (TextUtils.isEmpty(text)) {
                        onGetSearchNotes.onGetSearchNotes(noteDao.getAllPhotoNotesWithoutColorWithoutText());
                    } else {
                        onGetSearchNotes.onGetSearchNotes(noteDao.getAllPhotoNotesWithoutColorWithText(text));
                    }

                }
                break;
        }
    }
    public void deleteThisNote(NoteEntity noteEntity) {
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                noteDao.delete(noteEntity);
            }
        });
    }

    public void update(NoteEntity noteEntity) {
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                noteDao.updateCheckBoxNotes(noteEntity);
            }
        });

    }

    public void update(List<NoteEntity> noteEntities) {
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                noteDao.update(noteEntities);
            }
        });
    }

    public void deleteAll(List<NoteEntity> noteEntities) {
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                noteDao.delete(noteEntities);
                noteEntities.clear();
            }
        });
    }


    public List<NoteEntity> updateCheckBox (List<NoteEntity> noteEntities, Context context) {
        List<NoteEntity> checkNoteEntities = new ArrayList<>();
        while (noteEntities.size() >= 2) {
            int id = noteEntities.get(0).getId();
            for (int j = 1; j < noteEntities.size(); j++) {
                if (id == noteEntities.get(j).getId()) {
                    noteEntities.remove(0);
                    break;
                }
                int k = j;
                k++;
                if (k == noteEntities.size()) {
                    checkNoteEntities.add(noteEntities.get(0));
                    noteEntities.remove(0);
                    break;
                }
            }
            if (noteEntities.size() == 1) {
                noteEntities.add(noteEntities.get(0));
                break;
            }
        }
        return checkNoteEntities;
    }
}
