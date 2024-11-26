package com.example.noteplus.adapter;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.noteplus.R;
import com.example.noteplus.entities.Note;
import com.example.noteplus.listener.NotesListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>{
    private List<Note> notes;
    private NotesListener notesListener;
    private Timer timer;
    private List<Note> notesSource;
    public NoteAdapter(List<Note> notes,NotesListener notesListener) {
        this.notes = notes;
        this.notesListener=notesListener;
        notesSource=notes;
    }

    @NonNull
    @Override
    public NoteAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_note,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesListener.onNoteClicked(notes.get(position),position);
            }
        });
    }
    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static  class  NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutNote;
        RoundedImageView imageNote;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutNote=itemView.findViewById(R.id.layoutNote);
            imageNote=itemView.findViewById(R.id.imageNote);
        }

        void setNote(Note note) {
            textTitle.setText(note.getTitle());
            if (note.getSubtitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            } else {
                textSubtitle.setText(note.getSubtitle());
            }
            textDateTime.setText(note.getDateTime());
            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if(note.getColor()!=null){
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            }else {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }
            if (note.getImagePath() != null) {
                // 使用 Glide 异步加载图片
                Glide.with(itemView.getContext())
                        .load(note.getImagePath())  // 加载图片路径
                        .into(imageNote);  // 加载到 imageNote 中
                imageNote.setVisibility(View.VISIBLE);
            } else{
                imageNote.setVisibility(View.GONE);
            }
        }
    }
    public void searchNotes(final String searchKeyword) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<Note> filteredNotes;
                if (searchKeyword.trim().isEmpty()) {
                    filteredNotes = notesSource;
                } else {
                    filteredNotes = new ArrayList<>();
                    for (Note note : notesSource) {
                        if (note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase()) ||
                                note.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase()) ||
                                note.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            filteredNotes.add(note);
                        }
                    }
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    notes = filteredNotes;
                    notifyDataSetChanged();
                });
            }
        }, 500);
    }
    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
