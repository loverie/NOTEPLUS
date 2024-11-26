package com.example.noteplus.activities; // 修改为你的包名

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.noteplus.Model.Users;
import com.example.noteplus.R;
import com.example.noteplus.adapter.NoteAdapter;
import com.example.noteplus.database.NotesDatabase;
import com.example.noteplus.entities.Note;
import com.example.noteplus.listener.NotesListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {

    private static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE=2;
    public static final int REQUEST_CODE_SHOW_NOTES=3;
    private static final int REQUEST_CODE_SELECT_IMAGES = 4;
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
    private ImageView chatImage;
    private int noteClickedPosition=-1;
    private ImageView imageProfile;
    FirebaseUser firebaseUser;
    DatabaseReference myRef;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();;
        myRef= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users=snapshot.getValue(Users.class);
//                Toast.makeText(MainActivity.this,"User Login:"+users.getUsername(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // 初始化视图
        textMyNotes = findViewById(R.id.textMyNotes);
        layoutSearch = findViewById(R.id.layoutSearch);
        inputSearch = findViewById(R.id.inputSearch);
        layoutQuickActions = findViewById(R.id.layoutQuickActions);
        imageAddNote = findViewById(R.id.imageAddNote);
        imageAddImage = findViewById(R.id.imageAddImage);
        imageAddImage.setOnClickListener(v -> openImagePicker());
        imageProfile=findViewById(R.id.imageProfile);
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });
        chatImage=findViewById(R.id.chatImage);
        chatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(MainActivity.this, ChatActivity.class);
                startActivity(i);
            }
        });
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
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }

    private void getNotes(final int requestCode, final boolean isNoteDeleted) {
        @SuppressLint("StaticFieldLeak")
        class GetNoteTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                // 获取当前用户ID
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // 查询当前用户的笔记
                return NotesDatabase.getNotesDatabase(getApplicationContext())
                        .noteDao().getNotesByUserId(userId);
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if (requestCode == REQUEST_CODE_SHOW_NOTES) {
                    noteList.clear(); // 清空旧数据
                    noteList.addAll(notes); // 添加新的用户笔记
                    notesAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    noteList.remove(noteClickedPosition);
                    if (isNoteDeleted) {
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    } else {
                        noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
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
        if (requestCode == REQUEST_CODE_SELECT_IMAGES && resultCode == RESULT_OK) {
            if (data != null) {
                List<String> imagePaths = new ArrayList<>();

                if (data.getClipData() != null) {
                    // 多选
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imagePaths.add(getPathFromUri(imageUri));
                    }
                } else if (data.getData() != null) {
                    // 单选
                    Uri imageUri = data.getData();
                    imagePaths.add(getPathFromUri(imageUri));
                }

                // 创建笔记
                createImageNotes(imagePaths);
            }
        }
    }
    private String getPathFromUri(Uri uri) {
        String filePath;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            filePath = uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 允许多选
        startActivityForResult(Intent.createChooser(intent, "Select Images"), REQUEST_CODE_SELECT_IMAGES);
    }
    @SuppressLint("StaticFieldLeak")
    private void createImageNotes(List<String> imagePaths) {
        class SaveNotesTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                for (String imagePath : imagePaths) {
                    Note note = new Note();
                    note.setTitle("IMG");
                    note.setSubtitle("IMG");
                    note.setNoteText("IMG");
                    note.setImagePath(imagePath);

                    // 关联当前用户
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    note.setUserId(userId);

                    // 插入数据库
                    NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().insertNote(note);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                getNotes(REQUEST_CODE_SHOW_NOTES, false); // 刷新界面
            }
        }

        new SaveNotesTask().execute();
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item){
//        switch (item.getItemId()){
//            case R.id.logout:
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                finish();
//                return true;
//
//        }
//        return  false;
//    }
}
