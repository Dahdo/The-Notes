package com.dahdotech.thenotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.dahdotech.thenotes.model.Note;
import com.dahdotech.thenotes.model.NoteViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Note> allNotes = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NoteViewModel noteViewModel = new ViewModelProvider.AndroidViewModelFactory(
                this.getApplication()).create(NoteViewModel.class);

        Note firstNote = new Note();
        firstNote.setId(0);
        firstNote.setTitle("Testing1");
        firstNote.setContent("I am testing to see how it is working!");
        firstNote.setTime(new Date().getTime());
        firstNote.setContent("I changed my mind");
//        noteViewModel.insertNote(firstNote);
//        noteViewModel.insertNote(firstNote);
//        noteViewModel.insertNote(firstNote);
//        noteViewModel.insertNote(firstNote);

        noteViewModel.getAllNotes().observe(this, notes -> {

        });


    }
}