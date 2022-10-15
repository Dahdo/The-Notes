package com.dahdotech.thenotes.adapter;

import android.app.AlertDialog;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dahdotech.thenotes.R;
import com.dahdotech.thenotes.model.Note;
import com.dahdotech.thenotes.model.NoteViewModel;
import com.dahdotech.thenotes.ui.MainActivity;
import com.dahdotech.thenotes.util.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final List<Note> noteList;
    private final OnNoteClickListener onNoteClickListener;

    public RecyclerViewAdapter(List<Note> noteList, OnNoteClickListener onNoteClickListener){
        this.noteList = noteList;
        this.onNoteClickListener = onNoteClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.timeTextView.setText(note.getTitle());
        holder.titleTextView.setText(note.getTitle());
        holder.contentGlimpseTextView.setText(note.getContent());

        String formattedTime = new Utils().longDateFormat(new Date(note.getTime()));
        holder.timeTextView.setText(formattedTime);

        /*action mode related implementations*/
        if(actionMode == null){ //to be called upon action mode destroying
            holder.unCheckedCheckBox.setVisibility(View.GONE);
            holder.checkedCheckBox.setVisibility(View.GONE);
        }
        else {
            if(checkTheBox)
                holder.unCheckedCheckBox.setVisibility(View.VISIBLE);

            if(toggleTheCheckBoxes){
                if(checkAll){
                    holder.unCheckedCheckBox.setVisibility(View.GONE);
                    holder.checkedCheckBox.setVisibility(View.VISIBLE);
                    notesToDelete.clear();
                    for (int i = 0; i < noteList.size(); i++)
                        notesToDelete.add(i); //add all existing notes
                }
                else if(unCheckAll){
                    notesToDelete.clear();
                    holder.checkedCheckBox.setVisibility(View.GONE);
                    holder.unCheckedCheckBox.setVisibility(View.VISIBLE);
                }
            }
            if(checkIssuesInClickMode && holder.checkedCheckBox.getVisibility() == View.GONE
            && holder.unCheckedCheckBox.getVisibility() == View.GONE) // any time without checkbox, have unchecked one. only in single item click mode
                holder.unCheckedCheckBox.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        protected TextView titleTextView;
        protected TextView contentGlimpseTextView;
        protected TextView timeTextView;
        private final ImageButton unCheckedCheckBox;
        private final ImageButton checkedCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.task_title_text_view);
            contentGlimpseTextView = itemView.findViewById(R.id.task_first_line);
            timeTextView = itemView.findViewById(R.id.task_time_created_text_view);
            unCheckedCheckBox = itemView.findViewById(R.id.item_row_check_box);
            checkedCheckBox = itemView.findViewById(R.id.item_row_check_box_checked);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this); // for action mode activation

            //in active mode when individual checkBox is clicked
            unCheckedCheckBox.setOnClickListener(this);
            checkedCheckBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(actionMode == null){ //for viewing the note
                onNoteClickListener.onNoteClick(getAdapterPosition());
                return;
            }

            /*toggling the checking in action mode upon single item click**/

            //disabling the notifyDataSetChanged started by context menu toggle checkbox
            //and the initial checkbox revealing
            toggleTheCheckBoxes = false;
            checkTheBox = false;

            checkIssuesInClickMode = true; // in case some items have no any checkboxes due to
                                            //previous notifyDataSetChanged

            if(unCheckedCheckBox.getVisibility() == View.VISIBLE){
                this.unCheckedCheckBox.setVisibility(View.GONE);
                this.checkedCheckBox.setVisibility(View.VISIBLE);
                notesToDelete.add(getAdapterPosition());
            }
            else {
                this.unCheckedCheckBox.setVisibility(View.VISIBLE);
                this.checkedCheckBox.setVisibility(View.GONE);
                notesToDelete.remove(Integer.valueOf(getAdapterPosition()));
            }

        }

        @Override
        public boolean onLongClick(View view) {

            if(actionMode != null)
                return false;
            actionMode = view.startActionMode(actionModeCallback); //start action mode
            view.setSelected(true);
            return true;
        }

    }


    /* action mode related part**/

    private ActionMode actionMode = null;
    boolean checkTheBox = false;
    boolean toggleTheCheckBoxes = false;
    boolean checkAll = false;
    boolean unCheckAll = false;
    boolean checkIssuesInClickMode = false;

    private ArrayList<Integer> notesToDelete;

    //from MainActivity
    private CardView deleteCardView;
    private ImageView deleteImageView;
    private LinearLayout frontPageHead;
    private FloatingActionButton fab;
    private RecyclerViewAdapter adapter;
    private NoteViewModel noteViewModel;



    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater menuInflater = actionMode.getMenuInflater();
            menuInflater.inflate(R.menu.context_menu, menu);
            adapter = MainActivity.notesRecyclerViewAdapter;
            fab = MainActivity.fab;
            frontPageHead = MainActivity.frontPageHead;
            deleteCardView = MainActivity.deleteCardView;
            deleteImageView = MainActivity.deleteImageView;
            noteViewModel = MainActivity.noteViewModel;
            notesToDelete = new ArrayList<>();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            deleteCardView.setVisibility(View.VISIBLE);

            //at start. to show all checkboxes
            checkTheBox = true;
            checkIssuesInClickMode = false; // to disable single item click mode. not harmful, though
            if(adapter != null)
                adapter.notifyDataSetChanged();

            frontPageHead.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);

            // delete button.
            deleteImageView.setOnClickListener(view -> {
                if(notesToDelete.size() > 0){
                    AlertDialog.Builder alert = new AlertDialog.Builder(deleteImageView.getContext());
                    alert.setTitle("Delete");
                    alert.setMessage("Are you sure you want to delete " + notesToDelete.size() + " note(s)?");
                    alert.setPositiveButton("Sure", (dialog, which) -> {
                        for(int i : notesToDelete)
                            noteViewModel.deleteNote(noteList.get(i));
                        dialog.dismiss();
                        actionMode.finish();
                    });

                    alert.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

                    alert.show();
                }
                else{
                    Snackbar.make(deleteImageView, "Select at least one note!", Snackbar.LENGTH_SHORT).show();
                }
            });
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if(menuItem.getItemId() == R.id.checkbox_action_mode){ //select all
                menuItem.setVisible(false);
                actionMode.getMenu().findItem(R.id.checkbox_action_mode_checked)
                        .setVisible(true);

                //below three statements to disable other notifyDataSetChanged
                unCheckAll = false;
                checkTheBox = false;
                checkIssuesInClickMode = false;
                toggleTheCheckBoxes = true;
                checkAll = true;
                if(adapter != null)
                    adapter.notifyDataSetChanged();
            }
            else if(menuItem.getItemId() == R.id.checkbox_action_mode_checked){ //deselect all
                menuItem.setVisible(false);
                actionMode.getMenu().findItem(R.id.checkbox_action_mode)
                        .setVisible(true);
                //below three statements to disable other notifyDataSetChanged
                checkAll = false;
                checkTheBox = false;
                checkIssuesInClickMode = false;
                toggleTheCheckBoxes = true;
                unCheckAll = true;
                if(adapter != null)
                    adapter.notifyDataSetChanged();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null; // disable action mode

            //hide delete icon in bottomNavigation bar show the rest two
            deleteCardView.setVisibility(View.GONE);

            adapter.notifyDataSetChanged(); // restore everything since action mode is first nullified
            checkTheBox = false;
            toggleTheCheckBoxes = false;
            checkAll = false;
            unCheckAll = false;
            checkIssuesInClickMode = false;
            notesToDelete.clear();

            //restore front pages and fab
            frontPageHead.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        }
    };
}
