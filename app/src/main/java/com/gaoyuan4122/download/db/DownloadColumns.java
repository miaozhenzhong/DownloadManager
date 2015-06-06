package com.gaoyuan4122.download.db;

import android.provider.BaseColumns;

/**
 * Created by GAOYUAN on 2015/5/26.
 */
public class DownloadColumns implements BaseColumns{
    /**
     * 名称
     */
    public static final String NAME = "name";
    /**
     * url
     */
    public static final String URL = "url";
    /**
     * 保存路径
     */
    public static final String SAVE_PATH = "save_path";
    /**
     * 总大小
     */
    public static final String TOTAL_SIZE = "total_size";
    /**
     * 已下载大小
     */
    public static final String DOWNLOADED_SIZE = "downloaded_size";
    /**
     * 下载状态
     */
    public static final String DOWNLOAD_STATUS = "download_status";
}
