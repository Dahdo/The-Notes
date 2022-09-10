package com.dahdotech.thenotes.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.dahdotech.thenotes.R;
import com.dahdotech.thenotes.adapter.RecyclerViewAdapter;
import com.dahdotech.thenotes.model.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE_NEW_NOTE = "fab";
    private RecyclerView notesRecyclerView;
    private RecyclerViewAdapter notesRecyclerViewAdapter;
    private FloatingActionButton fab;

    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.note_fab);

        NoteViewModel noteViewModel = new ViewModelProvider.AndroidViewModelFactory(
                this.getApplication()).create(NoteViewModel.class);

        notesRecyclerView = findViewById(R.id.recyclerView);
        notesRecyclerView.setHasFixedSize(true);
        notesRecyclerView.setLayoutManager((new LinearLayoutManager(this)));

//        Note firstNote = new Note();
//        firstNote.setId(0);
//        firstNote.setTitle("Testing1");
//        firstNote.setContent("I am testing to see how it is working... but now I am so hopful that is indeed working!");
//        firstNote.setTime(calendar.getTime().getTime());
//        noteViewModel.insertNote(firstNote);
//
//        Note second = new Note();
//        second.setContent("hahahah");
//        second.setTitle("Second");
//        second.setTime(calendar.getTime().getTime());
//        noteViewModel.insertNote(second);


        noteViewModel.getAllNotes().observe(this, notes -> {
            notesRecyclerViewAdapter = new RecyclerViewAdapter(notes);
            notesRecyclerView.setAdapter(notesRecyclerViewAdapter);
        });

        //Note fab eventListener

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
            intent.putExtra(EXTRA_MESSAGE_NEW_NOTE, true);
            startActivity(intent);
        });

    }
}