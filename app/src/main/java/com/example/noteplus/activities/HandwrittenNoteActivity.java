package com.example.noteplus.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noteplus.R;
import com.example.noteplus.views.DrawingView;

public class HandwrittenNoteActivity extends AppCompatActivity {

    private DrawingView drawingView;
    private Button buttonPen, buttonEraser, buttonSave;
    private LinearLayout colorPalette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handwritten_note);

        drawingView = findViewById(R.id.drawingView);
        buttonPen = findViewById(R.id.buttonPen);
        buttonEraser = findViewById(R.id.buttonEraser);
        buttonSave = findViewById(R.id.buttonSave);
        colorPalette = findViewById(R.id.colorPalette);

        buttonPen.setOnClickListener(v -> drawingView.setPenMode());
        buttonEraser.setOnClickListener(v -> drawingView.setEraserMode());
        buttonSave.setOnClickListener(v -> saveHandwrittenNote());

        setupColorPalette();
    }

    private void setupColorPalette() {
        int[] colors = {Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        for (int color : colors) {
            View colorView = new View(this);
            colorView.setBackgroundColor(color);
            colorView.setOnClickListener(v -> drawingView.setPenColor(color));
            colorPalette.addView(colorView, new LinearLayout.LayoutParams(100, 100));
        }
    }

    private void saveHandwrittenNote() {
        // 保存手写笔记到SQLite数据库
        // 你需要实现将Bitmap转换为文件并存储路径到数据库的逻辑
    }
} 