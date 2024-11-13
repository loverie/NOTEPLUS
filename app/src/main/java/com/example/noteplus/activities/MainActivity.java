package com.example.noteplus.activities; // 修改为你的包名

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.noteplus.R;
import com.example.noteplus.adapter.NoteAdapter;
import com.example.noteplus.database.NotesDatabase;
import com.example.noteplus.entities.Note;
import com.example.noteplus.listener.NotesListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {

    private static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE=2;
    public static final int REQUEST_CODE_SHOW_NOTES=3;
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
    private int noteClickedPosition=-1;
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
        notesAdapter = new NoteAdapter(noteList,this);
        notesRecyclerView.setAdapter(notesAdapter);
        getNotes(REQUEST_CODE_SHOW_NOTES,false);
        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(noteList.size()!=0){
                    notesAdapter.searchNotes(s.toString());
                }

            }
        });
    }

    @Override
    public void onNoteClicked(Note note, int position) {
            noteClickedPosition=position;
            Intent intent=new Intent(getApplicationContext(),CreateNoteActivity.class);
            intent.putExtra("isViewOrUpdate",true);
            intent.putExtra("note",note);
            startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);
    }

    private void getNotes(final int requestCode,final boolean isNoteDeleted) {

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
                if (requestCode == REQUEST_CODE_SHOW_NOTES) {
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    noteList.remove(noteClickedPosition);
                    if(isNoteDeleted) {
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    }else{
                        noteList.add(noteClickedPosition,notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }
            }
        }

        new GetNoteTask().execute();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==REQUEST_CODE_ADD_NOTE&&resultCode==RESULT_OK){
            getNotes(REQUEST_CODE_ADD_NOTE,false);
        }else if(requestCode==REQUEST_CODE_UPDATE_NOTE&&resultCode==RESULT_OK){
            if(data!=null){
                getNotes(REQUEST_CODE_UPDATE_NOTE,data.getBooleanExtra("isNoteDeleted",false));
            }
        }
    }
}
