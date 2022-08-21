package com.dahdotech.thenotes.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.dahdotech.thenotes.model.Note;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Note note);

    @Query("DELETE FROM note_table")
    void deleteAll();

    @Query("SELECT * FROM note_table")
    LiveData<List<Note>> getAllNotes();

    @Delete
    void delete(Note note);

    @Update
    void update(Note note);

    @Query("SELECT * FROM note_table WHERE note_table.id ==:id")
    LiveData<Note> get(int id);
}
