package com.example.keepnotes.database;

import android.provider.BaseColumns;

public class NoteDBContract {

    public static final class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "ALL_NOTES_TABLE";
        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_TITLE = "TITLE";
        public static final String COLUMN_TIMESTAMP = "TIMESTAMP";
        public static final String COLUMN_DATE_TIME = "DATE_TIME";
        public static final String COLUMN_NOTE_TEXT = "NOTE_TEXT";
        public static final String COLUMN_IMAGE_PATH = "IMAGE_PATH";
        public static final String COLUMN_WEB_LINK = "WEB_LINK";
        public static final String COLUMN_COLOR = "COLOR";
    }
}
