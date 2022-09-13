package com.dahdotech.thenotes.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dahdotech.thenotes.R;
import com.dahdotech.thenotes.model.Note;
import com.dahdotech.thenotes.model.NoteViewModel;
import com.dahdotech.thenotes.util.Utils;

import java.util.Calendar;

public class NoteEditActivity extends AppCompatActivity {
    private boolean noteAlreadyExists;
    private Note currentNote;

    private TextView timeTextView;
    private EditText titleEditText;
    private EditText contentEditText;
    private MenuItem saveMenuItem;
    private MenuItem shareMenuItem;

    NoteViewModel noteViewModel;

    String noteTitle;
    String noteContent;


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
            timeTextView.setText(new Utils().shortDateFormat(calendar.getTime()));
            noteAlreadyExists = false;
        }
    }

    private void saveEventListener(){
        noteTitle = titleEditText.getText().toString().trim();
        noteContent = contentEditText.getText().toString().trim();
        if(!TextUtils.isEmpty(noteTitle) || !TextUtils.isEmpty(noteContent)){
            createUpdateNote(noteTitle, noteContent);
        }

        new Utils().collapseKeyboard(this);
        contentEditText.setFocusable(false);
        titleEditText.setFocusable(false);
    }

    //titleEditText.

    private void createUpdateNote(String noteTitle, String noteContent) {
        if(noteAlreadyExists){
            currentNote.setTitle(noteTitle);
            currentNote.setContent(noteContent);
            currentNote.setTime(calendar.getTime().getTime());
            noteViewModel.updateNote(currentNote);
        }
        else {
            currentNote = new Note();
            currentNote.setTitle(noteTitle);
            currentNote.setContent(noteContent);
            currentNote.setTime(calendar.getTime().getTime());
            NoteViewModel.insertNote(currentNote);
            noteAlreadyExists = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.note_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                saveEventListener();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickTitleEdit(View view) {
        titleEditText.setFocusableInTouchMode(true);
        //new Utils().undoCollapseKeyboard(this);
    }

    public void onClickContentEdit(View view) {
        contentEditText.setFocusableInTouchMode(true);
        //new Utils().undoCollapseKeyboard(this);
    }
}