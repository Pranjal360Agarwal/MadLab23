package com.example.keepnotes.database;

import  android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.keepnotes.database.NoteDBContract.NoteEntry;
import com.example.keepnotes.model.NoteModel;

public class NoteDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MY_DATABASE_2";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase database;
    private Context context;

    public NoteDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +
                NoteEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NoteEntry.COLUMN_TITLE + " TEXT, " +
                NoteEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                NoteEntry.COLUMN_DATE_TIME + " TEXT, " +
                NoteEntry.COLUMN_NOTE_TEXT + " TEXT, " +
                NoteEntry.COLUMN_IMAGE_PATH + " TEXT, " +
                NoteEntry.COLUMN_WEB_LINK + " TEXT, " +
                NoteEntry.COLUMN_COLOR + " TEXT" + ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME);
        onCreate(db);
    }

    public long addNewNoteData(String title, String note_text, String date_time, String web_link, String image_path, String color) {
        database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NoteEntry.COLUMN_TITLE, title);
        cv.put(NoteEntry.COLUMN_NOTE_TEXT, note_text);
        cv.put(NoteEntry.COLUMN_DATE_TIME, date_time);
        cv.put(NoteEntry.COLUMN_WEB_LINK, web_link);
        cv.put(NoteEntry.COLUMN_IMAGE_PATH, image_path);
        cv.put(NoteEntry.COLUMN_COLOR, color);
        return database.insert(NoteEntry.TABLE_NAME, null, cv);
    }

    public void updateNoteData(NoteModel note) {
        database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NoteEntry.COLUMN_TITLE, note.getTitle());
        cv.put(NoteEntry.COLUMN_NOTE_TEXT, note.getNote_text());
        cv.put(NoteEntry.COLUMN_DATE_TIME, note.getDate_time());
        cv.put(NoteEntry.COLUMN_WEB_LINK, note.getWeb_link());
        cv.put(NoteEntry.COLUMN_IMAGE_PATH, note.getImage_path());
        cv.put(NoteEntry.COLUMN_COLOR, note.getColor());
        database.update(NoteEntry.TABLE_NAME, cv, NoteEntry.COLUMN_ID + "=?" ,new String[]{note.getId().toString()} );
    }

    public void removeNote(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NoteEntry.TABLE_NAME, NoteEntry._ID + "=" + id,null);
    }
}
