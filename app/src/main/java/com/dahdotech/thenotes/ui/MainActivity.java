package com.dahdotech.thenotes.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.util.StringUtil;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.dahdotech.thenotes.R;
import com.dahdotech.thenotes.adapter.OnNoteClickListener;
import com.dahdotech.thenotes.adapter.RecyclerViewAdapter;
import com.dahdotech.thenotes.model.Note;
import com.dahdotech.thenotes.model.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnNoteClickListener {
    public static final String EXTRA_MESSAGE_NEW_NOTE = "newNote";
    public static final String EXTRA_MESSAGE_EXISTING_NOTE = "existingNote";
    private RecyclerView notesRecyclerView;
    private RecyclerViewAdapter notesRecyclerViewAdapter;
    private FloatingActionButton fab;
    private SearchView searchView;

    private View rootView;

    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = findViewById(R.id.root_layout);

        fab = findViewById(R.id.note_fab);

        NoteViewModel noteViewModel = new ViewModelProvider.AndroidViewModelFactory(
                this.getApplication()).create(NoteViewModel.class);

        notesRecyclerView = findViewById(R.id.recyclerView);
        notesRecyclerView.setHasFixedSize(true);
        notesRecyclerView.setLayoutManager((new LinearLayoutManager(this)));


        noteViewModel.getAllNotes().observe(this, notes -> {
            notesRecyclerViewAdapter = new RecyclerViewAdapter(notes, this);
            notesRecyclerView.setAdapter(notesRecyclerViewAdapter);
        });

        //Note fab eventListener

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
            intent.putExtra(EXTRA_MESSAGE_NEW_NOTE, true);
            startActivity(intent);
        });

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView)findViewById(R.id.search);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            noteViewModel.getAllNotes().observe(MainActivity.this, notes -> {
                List<Note> matchingNotes = new ArrayList<Note>();
                for(Note note : notes){
                    if(note.getTitle().toLowerCase().contains(s.toLowerCase()) || note.getContent().toLowerCase().contains(s.toLowerCase())){
                        matchingNotes.add(note);
                    }
                }
                notesRecyclerViewAdapter = new RecyclerViewAdapter(matchingNotes, MainActivity.this);
                notesRecyclerView.setAdapter(notesRecyclerViewAdapter);
            });

            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            onQueryTextSubmit(s);
            return true;
        }
    });

    }

    @Override
    protected void onResume() {
        super.onResume();
        rootView.requestFocus();
    }

    @Override
    public void onNoteClick(int postion) {
        Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
        intent.putExtra(EXTRA_MESSAGE_EXISTING_NOTE, postion);
        startActivity(intent);
    }
}