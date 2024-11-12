package com.example.noteplus; // 修改为你的包名

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

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

        // 你可以在这里设置事件监听器或初始化数据
    }
}
