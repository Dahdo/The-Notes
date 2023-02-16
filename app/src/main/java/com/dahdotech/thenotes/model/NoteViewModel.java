package com.dahdotech.thenotes.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dahdotech.thenotes.data.NoteRepository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private static NoteRepository repo;
    //private final LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repo = new NoteRepository(application);
        //allNotes = repo.getAll();
    }
    public static void insertNote(Note note){
        repo.insert(note);
    }

    public void updateNote(Note note){
        repo.update(note);
    }

    public void deleteNote(Note note){
        repo.delete(note);
    }

    public void deleteAllNotes(){
        repo.deleteAll();
    }

    public LiveData<Note> getNote(int id){
        return repo.get(id);
    }

    public LiveData<List<Note>> getAllNotes(){
        return repo.getAll();
    }

}
