package com.dahdotech.thenotes.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.dahdotech.thenotes.data.NoteDao;
import com.dahdotech.thenotes.model.Note;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NoteRoomDatabase extends RoomDatabase {
    public static final int NUMBER_OF_THREADS = 3;
    public static volatile NoteRoomDatabase INSTANCE;
    public static final String DATABASE_NAME = "note_database";
    public static final ExecutorService dataWriterExecutor
            = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static final RoomDatabase.Callback roomDatabaseCallBack =
            new RoomDatabase.Callback(){
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    dataWriterExecutor.execute(() -> {
                            NoteDao noteDao = INSTANCE.noteDao();
                            noteDao.deleteAll();
                        });
                }
            };

    public static NoteRoomDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (NoteRoomDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NoteRoomDatabase.class, DATABASE_NAME).addCallback(roomDatabaseCallBack)
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    protected abstract NoteDao noteDao();
}
