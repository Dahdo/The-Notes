package com.dahdotech.thenotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.dahdotech.thenotes.adapter.RecycleViewAdapter;
import com.dahdotech.thenotes.model.Note;
import com.dahdotech.thenotes.model.NoteViewModel;

import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private RecyclerView notesRecyclerView;
    private RecycleViewAdapter notesRecyclerViewAdapter;
    private Calendar calendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NoteViewModel noteViewModel = new ViewModelProvider.AndroidViewModelFactory(
                this.getApplication()).create(NoteViewModel.class);

        notesRecyclerView = findViewById(R.id.recyclerView);
        notesRecyclerView.setHasFixedSize(true);
        notesRecyclerView.setLayoutManager((new LinearLayoutManager(this)));

        Note firstNote = new Note();
        firstNote.setId(0);
        firstNote.setTitle("Testing1");
        firstNote.setContent("I am testing to see how it is working... but now I am so hopful that is indeed working!");
        firstNote.setTime(calendar.getTime().getTime());
        noteViewModel.insertNote(firstNote);

        Note second = new Note();
        second.setContent("hahahah");
        second.setTitle("Second");
        second.setTime(calendar.getTime().getTime());
        noteViewModel.insertNote(second);


        noteViewModel.getAllNotes().observe(this, notes -> {
            notesRecyclerViewAdapter = new RecycleViewAdapter(notes);
            notesRecyclerView.setAdapter(notesRecyclerViewAdapter);
        });


    }
}