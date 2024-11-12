package com.example.noteplus.activities; // 修改为你的包名

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.noteplus.R;
import com.example.noteplus.database.NotesDatabase;
import com.example.noteplus.entities.Note;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_NOTE = 1;
    private TextView textMyNotes;
    private LinearLayout layoutSearch;
    private EditText inputSearch;
    private RecyclerView notesRecyclerView;
    private LinearLayout layoutQuickActions;
    private ImageView imageAddNote;
    private ImageView imageAddImage;
    private ImageView imageAddWebLink;
    private ImageView imageAddNoteMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        textMyNotes = findViewById(R.id.textMyNotes);
        layoutSearch = findViewById(R.id.layoutSearch);
        inputSearch = findViewById(R.id.inputSearch);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        layoutQuickActions = findViewById(R.id.layoutQuickActions);
        imageAddNote = findViewById(R.id.imageAddNote);
        imageAddImage = findViewById(R.id.imageAddImage);
        imageAddWebLink = findViewById(R.id.imageAddWebLink);
        imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(v -> startActivityForResult(
                new Intent(getApplicationContext(), CreateNoteActivity.class), REQUEST_CODE_ADD_NOTE)
        );
        // 你可以在这里设置事件监听器或初始化数据
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
                Log.d("MY_NOTES",notes.toString());
            }
        }

        new GetNoteTask().execute();
    }
}
