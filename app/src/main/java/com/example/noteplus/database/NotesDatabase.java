package com.example.noteplus.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.noteplus.dao.NoteDao;
import com.example.noteplus.entities.Note;


@Database(entities = Note.class, version = 2, exportSchema = false) // 数据库版本号提升到 2
public abstract class NotesDatabase extends RoomDatabase {

    private static NotesDatabase notesDatabase;

    // 数据库迁移逻辑，从版本 1 升级到 2
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 为 notes 表添加 userId 字段
            database.execSQL("ALTER TABLE notes ADD COLUMN userId TEXT");
        }
    };

    // 单例模式获取数据库实例
    public static synchronized NotesDatabase getNotesDatabase(Context context) {
        if (notesDatabase == null) {
            notesDatabase = Room.databaseBuilder(
                            context,
                            NotesDatabase.class,
                            "notes_db"
                    )
                    .addMigrations(MIGRATION_1_2) // 添加迁移逻辑
                    .build();
        }
        return notesDatabase;
    }

    public abstract NoteDao noteDao();
}