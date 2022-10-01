package com.dahdotech.thenotes.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.dahdotech.thenotes.R;
import com.dahdotech.thenotes.adapter.RecyclerViewAdapter;
import com.dahdotech.thenotes.model.Note;
import com.dahdotech.thenotes.model.NoteViewModel;
import com.dahdotech.thenotes.util.Utils;

import java.util.Calendar;

public class NoteEditActivity extends AppCompatActivity {
    private boolean noteAlreadyExists;
    private boolean saveButtonClicked;
    private Note currentNote;

    private TextView timeTextView;
    private EditText titleEditText;
    private EditText contentEditText;
    private Menu mainMenu;
    private MenuItem saveMenuItem;
    private MenuItem shareMenuItem;
    private MenuItem deleteMenuItem;

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
        deleteMenuItem = findViewById(R.id.action_delete);

        //creating modelView instance
        noteViewModel = new ViewModelProvider.AndroidViewModelFactory(
                this.getApplication()).create(NoteViewModel.class);

        //Creating a new note
        if(getIntent().hasExtra(MainActivity.EXTRA_MESSAGE_NEW_NOTE)){
            noteAlreadyExists = false;
            timeTextView.setText(new Utils().shortDateFormat(calendar.getTime()));
            titleEditText.requestFocus();
        }

        //updating an existing note
        if(getIntent().hasExtra(MainActivity.EXTRA_MESSAGE_EXISTING_NOTE)){
            noteAlreadyExists = true;
            int noteId = getIntent().
                    getIntExtra(MainActivity.EXTRA_MESSAGE_EXISTING_NOTE, 0);
            noteViewModel.getAllNotes().observe(this, notes -> {
                if(!notes.isEmpty()){
                    currentNote = notes.get(noteId);
                    titleEditText.setText(currentNote.getTitle());
                    contentEditText.setText(currentNote.getContent());
                }
            });
            Log.d("WHYAREYOUSTUPID", "current: " + currentNote);// idk why here the currentNote is null
            timeTextView.setText(new Utils().shortDateFormat(Calendar.getInstance().getTime()));
        }

    }

    private void shareEventListener(){
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String subject = currentNote.getTitle() + "\n----------------------------------------\n";
        String content = subject + currentNote.getContent();
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void deleteEventListener(){
        noteViewModel.deleteNote(currentNote);
        this.finish();
    }

    private void saveEventListener(){
        saveButtonClicked = true;
        noteTitle = titleEditText.getText().toString().trim();
        noteContent = contentEditText.getText().toString().trim();
        if(!TextUtils.isEmpty(noteTitle) || !TextUtils.isEmpty(noteContent)){
            createUpdateNote(noteTitle, noteContent);
        }
        new Utils().collapseKeyboard(this);
        contentEditText.setFocusable(false);
        titleEditText.setFocusable(false);

        //showing all menuItems except save
        //at this point mainMenu should not be null
        if(mainMenu != null){
            invalidateOptionsMenu();
            mainMenu.getItem(0).setVisible(true);
            mainMenu.getItem(1).setVisible(false);
            mainMenu.getItem(2).setVisible(true);
        }
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
        mainMenu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.note_edit_menu, menu);

        if(noteAlreadyExists == false){
            //When it's a new note
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
        }
        else{
            //When the note exists or viewed
            menu.getItem(1).setVisible(false);
        }

        //titleEditText TextWatcher
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0 && contentEditText.getText().toString().isEmpty()){
                    //hide all menuItems
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    menu.getItem(2).setVisible(false);
                }
                else{
                    //show only save menuItem
                    //additional check if save button was previously clicked. variable saveButtonClicked
                    //is updated when title & content editTexts are clicked.
                    //This is to prevent TextWatcher actions after save button is clicked.
                    if(!saveButtonClicked){
                        menu.getItem(0).setVisible(false);
                        menu.getItem(1).setVisible(true);
                        menu.getItem(2).setVisible(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // contentEditText TextWatcher
        contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0 && titleEditText.getText().toString().isEmpty()){
                    //hide all menuItems
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    menu.getItem(2).setVisible(false);
                }
                else{
                    //show only save menuItem
                    //additional check if save button was previously clicked. variable saveButtonClicked
                    //is updated when title & content editTexts are clicked.
                    //This is to prevent TextWatcher actions after save button is clicked.
                    if(!saveButtonClicked){
                        menu.getItem(0).setVisible(false);
                        menu.getItem(1).setVisible(true);
                        menu.getItem(2).setVisible(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                saveEventListener();
                return true;
            case R.id.action_delete:
                deleteEventListener();
                return true;
            case R.id.share:
                shareEventListener();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickTitleEdit(View view) {
        titleEditText.setFocusableInTouchMode(true);
        saveButtonClicked = false;
        //new Utils().undoCollapseKeyboard(this);
    }

    public void onClickContentEdit(View view) {
        contentEditText.setFocusableInTouchMode(true);
        saveButtonClicked = false;
        //new Utils().undoCollapseKeyboard(this);
    }


}