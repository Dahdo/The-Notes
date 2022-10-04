package com.dahdotech.thenotes.adapter;

import android.graphics.Color;
import android.media.Image;
import android.provider.ContactsContract;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dahdotech.thenotes.R;
import com.dahdotech.thenotes.model.Note;
import com.dahdotech.thenotes.ui.MainActivity;
import com.dahdotech.thenotes.util.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Date;
import java.util.List;
import java.util.Vector;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<Note> noteList;
    private OnNoteClickListener onNoteClickListener;

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
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        protected TextView titleTextView;
        protected TextView contentGlimpseTextView;
        protected TextView timeTextView;
        private ImageButton unCheckedCheckBox;
        private ImageButton checkedCheckBox;

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

            //toggling the checking in action mode
            if(unCheckedCheckBox.getVisibility() == View.VISIBLE){
                unCheckedCheckBox.setVisibility(View.GONE);
                checkedCheckBox.setVisibility(View.VISIBLE);
            }
            else {
                unCheckedCheckBox.setVisibility(View.VISIBLE);
                checkedCheckBox.setVisibility(View.GONE);
            }

        }

        @Override
        public boolean onLongClick(View view) {

            if(actionMode != null)
                return false;
            actionMode = view.startActionMode(actionModeCallback);
            view.setSelected(true);
            return true;
        }



    }


    // ActionMode related section

    private ActionMode actionMode = null;

    //from MainActivity
    private BottomNavigationView bottomNavigationView = MainActivity.bottomNavigationView;
    private RecyclerView recyclerView = MainActivity.notesRecyclerView;
    private LinearLayout frontPageHead = MainActivity.frontPageHead;
    private FloatingActionButton fab = MainActivity.fab;



    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater menuInflater = actionMode.getMenuInflater();
            menuInflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {

            bottomNavigationView.findViewById(R.id.delete_forever).setVisibility(View.VISIBLE);
            bottomNavigationView.findViewById(R.id.notes).setVisibility(View.GONE);
            bottomNavigationView.findViewById(R.id.todo).setVisibility(View.GONE);

            for (int i = 0; i < getItemCount(); i++) {
                recyclerView.getChildAt(i).findViewById(R.id.item_row_check_box)
                        .setVisibility(View.VISIBLE);
            }

            frontPageHead.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);

            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if(menuItem.getItemId() == R.id.checkbox_action_mode){ //select all
                menuItem.setVisible(false);
                actionMode.getMenu().findItem(R.id.checkbox_action_mode_checked)
                        .setVisible(true);

                for (int i = 0; i < getItemCount(); i++) {
                    recyclerView.getChildAt(i).findViewById(R.id.item_row_check_box)
                            .setVisibility(View.GONE);
                    recyclerView.getChildAt(i).findViewById(R.id.item_row_check_box_checked)
                            .setVisibility(View.VISIBLE);
                }
            }
            else if(menuItem.getItemId() == R.id.checkbox_action_mode_checked){ //deselect all
                menuItem.setVisible(false);
                actionMode.getMenu().findItem(R.id.checkbox_action_mode)
                        .setVisible(true);

                for (int i = 0; i < getItemCount(); i++) {
                    recyclerView.getChildAt(i).findViewById(R.id.item_row_check_box)
                            .setVisibility(View.VISIBLE);
                    recyclerView.getChildAt(i).findViewById(R.id.item_row_check_box_checked)
                            .setVisibility(View.GONE);
                }
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;

            bottomNavigationView.findViewById(R.id.delete_forever).setVisibility(View.GONE);
            bottomNavigationView.findViewById(R.id.notes).setVisibility(View.VISIBLE);
            bottomNavigationView.findViewById(R.id.todo).setVisibility(View.VISIBLE);

            for (int i = 0; i < getItemCount(); i++) {
                recyclerView.getChildAt(i).findViewById(R.id.item_row_check_box)
                        .setVisibility(View.GONE);
                recyclerView.getChildAt(i).findViewById(R.id.item_row_check_box_checked)
                        .setVisibility(View.GONE);
            }

            frontPageHead.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        }
    };


//            bottomNavigationView.setOnNavigationItemSelectedListener(item ->{
//        actionMode.invalidate();
//
//        return true;
//    });

}
