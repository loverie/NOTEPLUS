package com.example.noteplus.listener;
import com.example.noteplus.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note,int position);
}
