package com.example.keepnotes.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepnotes.R;
import com.example.keepnotes.activity.NewNote;
import com.example.keepnotes.database.NoteDBHelper;
import com.example.keepnotes.model.NoteModel;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<NoteModel> noteList;

//    int getAdapterPosition() {
//        return noteList
//    }

    public NoteAdapter(Context context, ArrayList<NoteModel> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView note_title;
        public TextView note_text;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            note_title = itemView.findViewById(R.id.display_note_title);
            note_text = itemView.findViewById(R.id.display_note_text);


        }

    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        final NoteModel note = noteList.get(position);
        Long id = note.getId();

        holder.itemView.setTag(id);
        holder.note_title.setText(note.getTitle());
        holder.note_text.setText(note.getNote_text());
        setNoteBackground(holder, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpItemViewClickFunction(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void updateFilteredList(ArrayList<NoteModel> newList) {
        noteList = newList;
        notifyDataSetChanged();
    }

    private void setNoteBackground(CustomViewHolder holder, int position) {
        NoteModel note = noteList.get(position);
        switch (note.getColor()) {
            case "#ffffff" :
                holder.itemView.setBackgroundResource(R.drawable.bg0);
                break;
            case "#262626" :
                holder.itemView.setBackgroundResource(R.drawable.bg1);
                break;
            case "#993333" :
                holder.itemView.setBackgroundResource(R.drawable.bg2);
                break;
            case "#cc9900" :
                holder.itemView.setBackgroundResource(R.drawable.bg3);
                break;
            case "#cccc00" :
                holder.itemView.setBackgroundResource(R.drawable.bg4);
                break;
            case "#339933" :
                holder.itemView.setBackgroundResource(R.drawable.bg5);
                break;
            case "#009999" :
                holder.itemView.setBackgroundResource(R.drawable.bg6);
                break;
            case "#006666" :
                holder.itemView.setBackgroundResource(R.drawable.bg7);
                break;
            case "#003366" :
                holder.itemView.setBackgroundResource(R.drawable.bg8);
                break;
            case "#6600cc" :
                holder.itemView.setBackgroundResource(R.drawable.bg9);
                break;
            case "#993366" :
                holder.itemView.setBackgroundResource(R.drawable.bg10);
                break;
            case "#663300" :
                holder.itemView.setBackgroundResource(R.drawable.bg11);
                break;
            case "#666699" :
                holder.itemView.setBackgroundResource(R.drawable.bg12);
                break;
        }
    }

    public void updateNoteList(ArrayList<NoteModel> newNoteList) {
        noteList = newNoteList;
        if(newNoteList != null) {
            notifyDataSetChanged();
        }
    }

    private void setUpItemViewClickFunction(NoteModel note) {
        Bundle bundle = new Bundle();
        bundle.putLong("Id", note.getId());
        bundle.putString("Note_Title", note.getTitle());
        bundle.putString("Note_Text", note.getNote_text());
        bundle.putString("Date_Time", note.getDate_time());
        bundle.putString("Web_Link", note.getWeb_link());
        bundle.putString("Image_Path", note.getImage_path());
        bundle.putString("Color", note.getColor());

        Intent intent = new Intent(context, NewNote.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
