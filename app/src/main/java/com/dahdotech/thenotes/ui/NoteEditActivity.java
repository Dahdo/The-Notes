package com.dahdotech.thenotes.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.opengl.Visibility;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.dahdotech.thenotes.R;
import com.dahdotech.thenotes.model.Note;
import com.dahdotech.thenotes.model.NoteViewModel;

import java.util.Calendar;
import java.util.Objects;

public class NoteEditActivity extends AppCompatActivity {
    private boolean isCreateNote = false;
    TextView timeTextView;
    EditText titleEditText;
    EditText contentEditText;
    MenuItem saveMenuItem;
    MenuItem shareMenuItem;

    NoteViewModel noteViewModel;

    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        timeTextView = findViewById(R.id.edit_note_time_text_view);
        titleEditText = findViewById(R.id.edit_note_title_edit_text);
        contentEditText = findViewById(R.id.edit_note_content_edit_text);

        saveMenuItem = findViewById(R.id.save);
        shareMenuItem = findViewById(R.id.share);

        //creating modelView instance
        noteViewModel = new ViewModelProvider.AndroidViewModelFactory(
                this.getApplication()).create(NoteViewModel.class);

        //Creating a new note
        if(getIntent().hasExtra(MainActivity.EXTRA_MESSAGE_NEW_NOTE)){
            isCreateNote = getIntent().getBooleanExtra(MainActivity.EXTRA_MESSAGE_NEW_NOTE, false);

//            String noteTitle = titleEditText.getText().toString().trim();
//            String noteContent = contentEditText.getText().toString().trim();
//
//            if(!TextUtils.isEmpty(noteTitle) || !TextUtils.isEmpty(noteContent)){
//                createNote(noteTitle, noteContent);
//            }
            //saveMenuItem.setVisible(false);
            //shareMenuItem.setVisible(false);

        }
    }

    private void createNote(String noteTitle, String noteContent) {
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        noteViewModel.insertNote(note);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.note_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}