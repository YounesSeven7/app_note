package com.example.noteapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    void insert(NoteEntity noteEntity);

    @Insert
    void insert(List<NoteEntity> noteEntities);

    @Query("select * from note_table")
    LiveData<List<NoteEntity>> getAllNote();

    @Query("select * from note_table where text like '%' || :text || '%'")
    List<NoteEntity> getSearchNote(String text);
    // just text notes ................................................................................................................................
    @Query("select * from note_table where imageUrl is null and isChecked is null")
    List<NoteEntity> getAllJustTextNotesWithoutColorWithoutText();

    @Query("select * from note_table where imageUrl is null and isChecked is null and color =:color")
    List<NoteEntity> getAllJustTextNotesWithColorWithoutText(int color);

    @Query("select * from note_table where imageUrl is null and isChecked is null and text like '%' || :text || '%'")
    List<NoteEntity> getAllJustTextNotesWithoutColorWithText(String text);

    @Query("select * from note_table where imageUrl is null and isChecked is null and color =:color and text like '%' || :text || '%'")
    List<NoteEntity> getAllJustTextNotesWithColorWithText(int color, String text);
    // Check Notes notes .............................................................................................................................
    @Query("select * from note_table where imageUrl is null and isChecked is not null")
    List<NoteEntity> getAllCheckNotesWithoutColorWithoutText();

    @Query("select * from note_table where imageUrl is null and isChecked is not null and color =:color")
    List<NoteEntity> getAllCheckNotesWithColorWithoutText(int color);

    @Query("select * from note_table where imageUrl is null and isChecked is not null and text like '%' || :text || '%'")
    List<NoteEntity> getAllCheckNotesWithoutColorWithText(String text);

    @Query("select * from note_table where imageUrl is null and isChecked is not null and color =:color and text like '%' || :text || '%'")
    List<NoteEntity> getAllCheckNotesWithColorWithText(int color, String text);
    // Photo Notes notes .............................................................................................................................
    @Query("select * from note_table where imageUrl is not null and isChecked is null")
    List<NoteEntity> getAllPhotoNotesWithoutColorWithoutText();

    @Query("select * from note_table where imageUrl is not null and isChecked is null and color =:color")
    List<NoteEntity> getAllPhotoNotesWithColorWithoutText(int color);

    @Query("select * from note_table where imageUrl is not null and isChecked is null and text like '%' || :text || '%'")
    List<NoteEntity> getAllPhotoNotesWithoutColorWithText(String text);

    @Query("select * from note_table where imageUrl is not null and isChecked is null and color =:color and text like '%' || :text || '%'")
    List<NoteEntity> getAllPhotoNotesWithColorWithText(int color, String text);



    @Update
    void updateCheckBoxNotes(NoteEntity noteEntity);

    @Update
    void update(List<NoteEntity> noteEntities);

    @Delete
    void delete(NoteEntity noteEntity);


    @Delete
    void delete(List<NoteEntity> noteEntities);
}
