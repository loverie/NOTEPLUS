package com.example.noteplus.activities; // 修改为你的包名

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.noteplus.R;
import com.example.noteplus.adapter.NoteAdapter;
import com.example.noteplus.database.NotesDatabase;
import com.example.noteplus.entities.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_NOTE = 1;
    private TextView textMyNotes;
    private LinearLayout layoutSearch;
    private EditText inputSearch;
    private LinearLayout layoutQuickActions;
    private ImageView imageAddNote;
    private ImageView imageAddImage;
    private ImageView imageAddWebLink;
    private ImageView imageAddNoteMain;
    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NoteAdapter notesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        textMyNotes = findViewById(R.id.textMyNotes);
        layoutSearch = findViewById(R.id.layoutSearch);
        inputSearch = findViewById(R.id.inputSearch);
        layoutQuickActions = findViewById(R.id.layoutQuickActions);
        imageAddNote = findViewById(R.id.imageAddNote);
        imageAddImage = findViewById(R.id.imageAddImage);
        imageAddWebLink = findViewById(R.id.imageAddWebLink);
        imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(v -> startActivityForResult(
                new Intent(getApplicationContext(), CreateNoteActivity.class), REQUEST_CODE_ADD_NOTE)
        );
        // 你可以在这里设置事件监听器或初始化数据
        notesRecyclerView=findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        );
        noteList=new ArrayList<>();
        notesAdapter = new NoteAdapter(noteList);
        notesRecyclerView.setAdapter(notesAdapter);
        getNotes();
    }
    private void getNotes() {

        @SuppressLint("StaticFieldLeak")
        class GetNoteTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase.getNotesDatabase(getApplicationContext())
                        .noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if(noteList.size()==0){
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                }else{
                    noteList.add(0,notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                }
                notesRecyclerView.smoothScrollToPosition(0);
            }
        }

        new GetNoteTask().execute();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==REQUEST_CODE_ADD_NOTE&&resultCode==RESULT_OK){
            getNotes();
        }
    }
}
