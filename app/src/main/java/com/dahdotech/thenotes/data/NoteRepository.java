package com.dahdotech.thenotes.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.dahdotech.thenotes.model.Note;
import com.dahdotech.thenotes.util.NoteRoomDatabase;

import java.util.List;

public class NoteRepository {
    private LiveData<List<Note>> allNotes;
    private static NoteDao noteDao;

    public NoteRepository(Application application){
        NoteRoomDatabase database = NoteRoomDatabase.getDatabase(application);
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
    }

    public static void insert(Note note){
        NoteRoomDatabase.dataWriterExecutor.execute(() ->{
            noteDao.insert(note);
        });
    }

    public void update(Note note){
        NoteRoomDatabase.dataWriterExecutor.execute(() ->{
            noteDao.update(note);
        });
    }

    public void delete(Note note){
        NoteRoomDatabase.dataWriterExecutor.execute(()->{
            noteDao.delete(note);
        });
    }

    public void deleteAllNotes(){
        NoteRoomDatabase.dataWriterExecutor.execute(()->{
            noteDao.deleteAll();
        });
    }

    public LiveData<Note> getNote(int id){
        return noteDao.get(id);
    }

    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }


}
