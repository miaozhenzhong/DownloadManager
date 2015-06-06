package com.gaoyuan4122.download.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gaoyuan4122.download.DownloadInfo;
import com.gaoyuan4122.download.app.Constants;
import com.gaoyuan4122.download.app.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GAOYUAN on 2015/5/26.
 */
public class DownloadDbManager {
    private static DownloadDbManager sDownloadDbManager = new DownloadDbManager();
    private final DownloadDbHelper mDbHelper;

    private DownloadDbManager() {
        mDbHelper = new DownloadDbHelper(MyApplication.getContext());
    }

    public static DownloadDbManager getInstance() {
        return sDownloadDbManager;
    }

    /**
     * 增
     *
     * @param info
     * @return
     */
    public long addInfoToDb(DownloadInfo info) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DownloadColumns.URL, info.mUrl);
        contentValues.put(DownloadColumns.SAVE_PATH, info.mLocalPath);
        contentValues.put(DownloadColumns.DOWNLOADED_SIZE, 0);
        contentValues.put(DownloadColumns.TOTAL_SIZE, -1);
        contentValues.put(DownloadColumns.DOWNLOAD_STATUS, Constants.DOWNLOAD_UNFINISHED);
        SQLiteDatabase writableDatabase = mDbHelper.getWritableDatabase();
        long id = writableDatabase.insert(DownloadDbHelper.DOWNLOAD_TABLE_NAME, null, contentValues);
        return id;
    }

    /**
     * 删
     *
     * @param info
     */
    public void removeInfoFromDb(DownloadInfo info) {
        mDbHelper.getWritableDatabase().delete(DownloadDbHelper.DOWNLOAD_TABLE_NAME,
                DownloadColumns._ID + "=" + info.mId, null);
    }

    /**
     * 改
     *
     * @param infoId
     * @param downloadedSize
     */
    public void updateDownloadedSizeToDb(long infoId, long downloadedSize, int state) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DownloadColumns.TOTAL_SIZE, downloadedSize);
        contentValues.put(DownloadColumns.DOWNLOAD_STATUS, state);
        mDbHelper.getWritableDatabase().update(DownloadDbHelper.DOWNLOAD_TABLE_NAME, contentValues,
                DownloadColumns._ID + "=" + infoId, null);
    }

    public void updateTotalSizeToDb(long infoId, long totalSize) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DownloadColumns.TOTAL_SIZE, totalSize);
        mDbHelper.getWritableDatabase().update(DownloadDbHelper.DOWNLOAD_TABLE_NAME, contentValues,
                DownloadColumns._ID + "=" + infoId, null);
    }

    /**
     * 查
     *
     * @return
     */
    public List<DownloadInfo> queryAllInfosFromDb() {
        Cursor cursor = mDbHelper.getReadableDatabase().query(DownloadDbHelper.DOWNLOAD_TABLE_NAME, new String[]{},
                null, null, null, null, null);
        List<DownloadInfo> infos = new ArrayList<DownloadInfo>();
        DownloadInfo info = null;
        while (cursor.moveToNext()) {
            info = new DownloadInfo(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getLong(4), cursor.getLong(5));
            infos.add(info);
        }
        return infos;
    }
}
