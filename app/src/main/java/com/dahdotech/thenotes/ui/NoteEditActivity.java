package com.dahdotech.thenotes.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
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
import java.util.Objects;

public class NoteEditActivity extends AppCompatActivity {
    private boolean noteAlreadyExists;
    private boolean saveButtonClicked;
    private Note currentNote;

    private TextView timeTextView;
    private EditText titleEditText;
    private EditText contentEditText;
    private Menu mainMenu;

    NoteViewModel noteViewModel;

    private String noteTitle;
    private String noteContent;
    boolean deletedOrUpdated = false; //to avoid errors observing already deleted note object
    private boolean noteDeleted = false;


    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        getSupportActionBar().setTitle("Note");
        getSupportActionBar().setElevation(0);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.back_arrow_icon); // display homeAsUp button or back arrow




        timeTextView = findViewById(R.id.edit_note_time_text_view);
        titleEditText = findViewById(R.id.edit_note_title_edit_text);
        contentEditText = findViewById(R.id.edit_note_content_edit_text);


        //creating modelView instance
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);


        //Creating a new note
        if(getIntent().hasExtra(MainActivity.EXTRA_MESSAGE_NEW_NOTE)){
            noteAlreadyExists = false;
            timeTextView.setText(new Utils().shortDateFormat(calendar.getTime()));
            titleEditText.requestFocus();
        }

        //updating an existing note
        if(getIntent().hasExtra(MainActivity.EXTRA_MESSAGE_EXISTING_NOTE)){
            noteAlreadyExists = true;
            new Utils().collapseKeyboard(this);
            contentEditText.setFocusable(false);
            titleEditText.setFocusable(false);
            int noteId = getIntent().
                    getIntExtra(MainActivity.EXTRA_MESSAGE_EXISTING_NOTE, 0);
            noteViewModel.getAllNotes().observe(this, notes -> {
                if(!notes.isEmpty() && !deletedOrUpdated){
                    currentNote = notes.get(noteId);
                    titleEditText.setText(currentNote.getTitle());
                    contentEditText.setText(currentNote.getContent());
                }
            });
            timeTextView.setText(new Utils().longDateFormat(Calendar.getInstance().getTime()));
        }
        contentEditText.setOnClickListener(view -> {
            onClickContentEdit(view);
        });
        titleEditText.setOnClickListener(view -> {
            onClickTitleEdit(view);
        });
    }

    private void shareEventListener(){
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String subject = currentNote.getTitle();
        String content = currentNote.getContent();
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void deleteEventListener(){
        AlertDialog.Builder alert = new AlertDialog.Builder(contentEditText.getContext(), R.style.AlertDialogCustom);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete this note?");
        alert.setPositiveButton("Sure", (dialog, which) -> {
            deletedOrUpdated = true;
            noteViewModel.deleteNote(currentNote);
            currentNote = null;
            noteDeleted = true;
            this.finish();
        });

        alert.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        alert.show().getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
//
    }

    private boolean saveEventListener(){
        boolean success = false;
        saveButtonClicked = true;
        noteTitle = titleEditText.getText().toString().trim();
        noteContent = contentEditText.getText().toString().trim();
        if(!TextUtils.isEmpty(noteTitle) || !TextUtils.isEmpty(noteContent)){
            if(noteAlreadyExists && !noteDeleted){
                if(!currentNote.getContent().equals(noteContent) || !currentNote.getTitle().equals(noteTitle)){
                    createUpdateNote(noteTitle, noteContent);
                    success = true;
                }
            }
            else if(!noteDeleted) {
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

        if(!noteAlreadyExists){
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
                if(s.length() >= 100)
                    Toast.makeText(NoteEditActivity.this, "Character limit reached!", Toast.LENGTH_SHORT).show();

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
                if(s.length() >= 10000)
                    Toast.makeText(NoteEditActivity.this, "Character limit reached!", Toast.LENGTH_SHORT).show();

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
                if(lengthAfter > lengthBefore && getSelection(contentEditText, contentEditText.getSelectionEnd()).endsWith("\n")){
                    boolean appendRestricted = false; //to avoid undoing what the 1st if statement did
                    String wholeText;
                    int selectionLength = contentEditText.getSelectionEnd();
                    String selectedText = getSelection(contentEditText, selectionLength);
                    String afterSelectedText = getAfterSelection(contentEditText, selectionLength);
                    String lastLine = getSplitLastLine(selectedText, "\n");

                    if(lastLine.trim().equals("-") || lastLine.trim().equals("+")
                            || lastLine.trim().equals("•")){
                        wholeText = selectedText.substring(0, selectedText.length() - (lastLine.length())); // wholeText = selectedText.substring(0, selectedText.length() - (lastLine.length()));
                                                                                                            //can also work in case you want to stay in the inner section of bulleted list
                        wholeText = wholeText .concat(afterSelectedText);
                        contentEditText.setText(wholeText);
                        if(selectionLength < 0)
                            contentEditText.setSelection(0);
                        contentEditText.setSelection(selectionLength - (lastLine.length()));
                        appendRestricted = true;
                    }
                    else if(conformsToNumberFormat(lastLine.trim() + " ")){
                        if(trimFront(lastLine).equals(getNumberPrefix(lastLine.trim()) + ". \n")){ // if(lastLine.equals(getNumberPrefix(lastLine.trim()) + ". \n")) true unless there are whitespaces before lastLine
                                                                                                   // if(lastLine.trim().equals(getNumberPrefix(lastLine.trim()) + ".")) would be true for every number and a dot after
                            wholeText = selectedText.substring(0, selectedText.length() - (lastLine.length()));
                            wholeText = wholeText .concat(afterSelectedText);
                            contentEditText.setText(wholeText);
                            if(selectionLength < 0)
                                contentEditText.setSelection(0);
                            contentEditText.setSelection(selectionLength - (lastLine.length()));
                        }
                    }

                    if((lastLine.trim().startsWith("- ") || lastLine.trim().startsWith("+ ")
                    || lastLine.trim().startsWith("• ")) && !appendRestricted &&
                            endsWithOneNewLineChar(lastLine)){
                        wholeText = selectedText.concat(lastLine.substring(0, prefixWhiteSpaces(lastLine) + 2)); // ex: plus 2 for '-' and ' '
                        wholeText = wholeText.concat(afterSelectedText);
                        contentEditText.setText(wholeText);
                        contentEditText.setSelection(selectionLength + prefixWhiteSpaces(lastLine) + 2);
                    }
                    else if(conformsToNumberFormat(lastLine.trim()) && endsWithOneNewLineChar(lastLine)){
                        int numberIncremented = (getNumberPrefix(lastLine.trim()) + 1);
                        wholeText = selectedText.concat(lastLine.substring(0, prefixWhiteSpaces(lastLine)) + numberIncremented + ". "); // incrementing as well
                        wholeText = wholeText.concat(afterSelectedText);
                        contentEditText.setText(wholeText);
                        contentEditText.setSelection(selectionLength + prefixWhiteSpaces(lastLine) + numberPrefixLength(numberIncremented + "") + 2);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return super.onCreateOptionsMenu(menu);
    }



    @SuppressLint("NonConstantResourceId")
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
            Toast.makeText(this, "Progress saved!", Toast.LENGTH_SHORT).show();
    }
    private String getSplitLastLine(String text, String delimiter){
        text = ".".concat(text); // prevent trim used below, to trim white line before text
        String whiteSpaces = text.substring(text.trim().length());
        String [] textLines = text.trim().split(delimiter);
        if(textLines.length == 1)
            return textLines[textLines.length - 1].substring(1) + whiteSpaces; // to remove the "."
        return textLines[textLines.length - 1] + whiteSpaces;
    }

    private String getSelection(EditText editText, int selectionLength) {
        return editText.getText().toString().substring(0, selectionLength);
    }

    private String getAfterSelection(EditText editText, int selectionLength) {
        return editText.getText().toString().substring(selectionLength);
    }
    private int postFixWhiteSpaces(String text) {
        text = ".".concat(text);
        return text.length() - text.trim().length();
    }

    private int prefixWhiteSpaces(String text) {
        text = text.concat(".");
        return text.length() - text.trim().length();
    }

    private boolean endsWithOneNewLineChar(String text) {
        int whiteSpaces = postFixWhiteSpaces(text);
        if(whiteSpaces > 0)
            // check if the last char is new line char
            if(!text.substring(text.length() - whiteSpaces, text.length() - 1).contains("\n")) //leaving the last char
                return text.charAt(text.length() - 1) == '\n';

        return false;
    }

    private boolean conformsToNumberFormat(String text) {
        if(!(numberPrefixLength(text) > 0))
            return false;
        return dotAndSpaceAfterDigit(text);
    }

    private int getNumberPrefix(String text) {
        StringBuilder aNumber = new StringBuilder("0");
        for(char c : text.toCharArray()){
            if(Character.isDigit(c))
                aNumber.append(c);
            else
                break;
        }
        return Integer.parseInt(aNumber.toString());
    }
    private int numberPrefixLength(String text){
        int numberOfDigits = 0;
        for(char c : text.toCharArray()){
            if(Character.isDigit(c))
                ++numberOfDigits;
            else
                break;
        }
        return numberOfDigits;
    }

    private boolean dotAndSpaceAfterDigit(String text){
        return text.substring(numberPrefixLength(text)).startsWith(". ");
    }
    private String trimFront(String text){
        return text.substring(prefixWhiteSpaces(text));
    }

}