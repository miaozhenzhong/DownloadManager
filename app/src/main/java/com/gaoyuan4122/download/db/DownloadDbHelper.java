package com.gaoyuan4122.download.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by GAOYUAN on 2015/5/26.
 */
public class DownloadDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "download.db";
    private static final int DATABASE_VERSION = 1;
    public static final String DOWNLOAD_TABLE_NAME = "download_info";

    public DownloadDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DOWNLOAD_TABLE_NAME + " ( "
                + DownloadColumns._ID + " INTEGER PRIMARY KEY,"
                + DownloadColumns.NAME + " TEXT,"
                + DownloadColumns.URL + " TEXT,"
                + DownloadColumns.SAVE_PATH + " TEXT,"
                + DownloadColumns.DOWNLOADED_SIZE + " LONG,"
                + DownloadColumns.TOTAL_SIZE + " LONG,"
                + DownloadColumns.DOWNLOAD_STATUS + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
