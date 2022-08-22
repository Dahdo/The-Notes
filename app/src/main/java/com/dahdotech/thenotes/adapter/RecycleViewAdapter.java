package com.dahdotech.thenotes.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dahdotech.thenotes.R;
import com.dahdotech.thenotes.model.Note;
import com.dahdotech.thenotes.util.Utils;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    private List<Note> noteList;

    public RecycleViewAdapter(List<Note> noteList){
        this.noteList = noteList;
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

        String formattedTime = new Utils().formatDate(new Date(note.getTime()));
        holder.timeTextView.setText(formattedTime);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView titleTextView;
        protected TextView contentGlimpseTextView;
        protected TextView timeTextView;
        protected TextView numberOfCompletedTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.task_title_text_view);
            contentGlimpseTextView = itemView.findViewById(R.id.task_first_line);
            timeTextView = itemView.findViewById(R.id.task_time_created_text_view);
            numberOfCompletedTextView = itemView.findViewById(R.id.task_time_created_text_view);
        }
    }
}
