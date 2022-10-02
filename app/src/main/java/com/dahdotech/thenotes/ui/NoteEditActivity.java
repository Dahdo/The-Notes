package com.dahdotech.thenotes.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dahdotech.thenotes.R;
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

    private String noteTitle;
    private String noteContent;
    boolean deletedOrUpdated = false; //to avoid errors observing already deleted note object


    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon); // display homeAsUp button or back arrow




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
                if(!notes.isEmpty() && !deletedOrUpdated){
                    currentNote = notes.get(noteId);
                    titleEditText.setText(currentNote.getTitle());
                    contentEditText.setText(currentNote.getContent());
                }
            });
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
        deletedOrUpdated = true;
        noteViewModel.deleteNote(currentNote);
        this.finish();
    }

    private boolean saveEventListener(){
        boolean success = false;
        saveButtonClicked = true;
        noteTitle = titleEditText.getText().toString().trim();
        noteContent = contentEditText.getText().toString().trim();
        if(!TextUtils.isEmpty(noteTitle) || !TextUtils.isEmpty(noteContent)){
            if(!currentNote.getContent().equals(noteContent) || !currentNote.getTitle().equals(noteTitle)){
                createUpdateNote(noteTitle, noteContent);
                success = true;
            }
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

        return success;
    }

    //titleEditText.

    private void createUpdateNote(String noteTitle, String noteContent) {
        if(noteAlreadyExists){
            currentNote.setTitle(noteTitle);
            currentNote.setContent(noteContent);
            currentNote.setTime(calendar.getTime().getTime());
            deletedOrUpdated = true;
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
            public void onTextChanged(CharSequence s, int start, int lengthBefore, int lengthAfter) {
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
                if(lengthAfter > lengthBefore && contentEditText.getText().toString().endsWith("\n")){
                    boolean appendRestricted = false; //to avoid undoing what the 1st if statement did; for the 2nd if statement
                                       String lastLine = getSplitLastLine(contentEditText.getText().toString(), "\n");

                    if(lastLine.trim().equals("-") || lastLine.trim().equals("+")
                            || lastLine.trim().equals("•")){
                        contentEditText.setText(contentEditText.getText().toString().trim()
                                .substring(0, contentEditText.getText().toString().trim().length() - 1));
                        contentEditText.setSelection(contentEditText.getText().length());
                        lastLine = getSplitLastLine(contentEditText.getText().toString(), "\n");
                        appendRestricted = true;
                    }

                    if((lastLine.startsWith("- ") || lastLine.startsWith("+ ")
                    || lastLine.startsWith("• ")) && !appendRestricted &&
                            !contentEditText.getText().toString().endsWith("\n\n")){
                        contentEditText.setText(contentEditText.getText().
                                append(lastLine.substring(0, 2)));
                        contentEditText.setSelection(contentEditText.getText().length());
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
            case android.R.id.home: //when homeAsUp button clicked.
                if(saveEventListener())
                    Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                finish();
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

    @Override
    protected void onStop() {
        super.onStop();
        if(saveEventListener())
            Toast.makeText(this, "Current note saved!", Toast.LENGTH_SHORT).show();
    }
    private String getSplitLastLine(String text, String delimeter){
        String [] textLines = text.split(delimeter);
        return textLines[textLines.length - 1];
    }
}