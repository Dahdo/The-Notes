package com.dahdotech.thenotes.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.dahdotech.thenotes.R;
import com.dahdotech.thenotes.adapter.OnNoteClickListener;
import com.dahdotech.thenotes.adapter.RecyclerViewAdapter;
import com.dahdotech.thenotes.model.Note;
import com.dahdotech.thenotes.model.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnNoteClickListener {
    public static final String EXTRA_MESSAGE_NEW_NOTE = "newNote";
    public static final String EXTRA_MESSAGE_EXISTING_NOTE = "existingNote";
    public static RecyclerView notesRecyclerView;
    public static LinearLayout frontPageHead;
    public static CardView deleteCardView;
    public static ImageView deleteImageView;
    public static FloatingActionButton fab;
    public static RecyclerViewAdapter notesRecyclerViewAdapter;
    public static NoteViewModel noteViewModel;


    private SearchView searchView;
    public static CardView searchCardView;

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);

        rootView = findViewById(R.id.front_page_head);
        searchCardView = findViewById(R.id.searchCardView);

        fab = findViewById(R.id.note_fab);
        frontPageHead = findViewById(R.id.front_page_head);

        deleteCardView = findViewById(R.id.delete_card_view);
        deleteImageView = findViewById(R.id.delete_forever_image);

        noteViewModel = new ViewModelProvider.AndroidViewModelFactory(
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
        searchView = findViewById(R.id.search);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                noteViewModel.getAllNotes().observe(MainActivity.this, notes -> {
                    List<Note> matchingNotes = new ArrayList<>();
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
    public void onNoteClick(int position) {
        Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
        intent.putExtra(EXTRA_MESSAGE_EXISTING_NOTE, position);
        startActivity(intent);
    }

    public boolean isNightMode() {
        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

}