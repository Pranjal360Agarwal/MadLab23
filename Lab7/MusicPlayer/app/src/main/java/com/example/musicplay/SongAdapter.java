package com.example.musicplay;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {

    ArrayList<SongInfo> songs;
    Context context;

    OnitemClickListener onitemClickListener;

    SongAdapter(Context context, ArrayList<SongInfo>songs){
        this.context = context;
        this.songs = songs;
    }

    public interface OnitemClickListener{
        void onItemClick(Button b, View v, SongInfo obj, int position);
    }
    public void setOnitemClickListener(OnitemClickListener onitemClickListener){

    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(context).inflate(R.layout.row_song,parent,false);
        return new SongHolder(myView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SongHolder holder, final int position) {
        final SongInfo c = songs.get(position);
        holder.songName.setText(c.songName);
        holder.artistName.setText(c.artistName);
        holder.btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onitemClickListener !=null){
                    onitemClickListener.onItemClick(holder.btnAction,v,c,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class SongHolder extends RecyclerView.ViewHolder {

        TextView songName,artistName;
        Button btnAction;

        public SongHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.tvSongName);
            artistName = itemView.findViewById(R.id.tvArtistName);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}
