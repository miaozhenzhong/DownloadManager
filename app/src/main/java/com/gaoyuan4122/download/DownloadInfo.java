package com.gaoyuan4122.download;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by GAOYUAN on 2015/5/26.
 * 封装一条下载记录, 可存进数据库的实体类
 */
public class DownloadInfo implements Serializable {
    public long mId;
    public long mTotalSize;
    public long mDownloadedSize;
    public String mName;
    public String mUrl;
    public String mLocalPath;
    private ArrayList<IDownloadCallback> mCallbackList = new ArrayList<IDownloadCallback>();

    /**
     * 新创建一个下载任务的时候可以使用这个构造方法, 因为不知道文件总大小, 当前下载了多少, 以及 id
     *
     * @param name
     * @param url
     * @param localPath
     */
    public DownloadInfo(String name, String url, String localPath) {
        this.mName = name;
        this.mUrl = url;
        this.mLocalPath = localPath;
    }

    /**
     * 从数据库中恢复的时候, 可使用这个构造方法
     *
     * @param id
     * @param totalSize
     * @param downloadedSize
     * @param name
     * @param url
     * @param localPath
     */
    public DownloadInfo(long id,  String name, String url,  String localPath, long downloadedSize, long totalSize) {
        mId = id;
        mName = name;
        mUrl = url;
        mLocalPath = localPath;
        mDownloadedSize = downloadedSize;
        mTotalSize = totalSize;
    }

    public void addCallback(IDownloadCallback callback) {
        mCallbackList.add(callback);
    }

    public void removeCallback(IDownloadCallback callback) {
        mCallbackList.remove(callback);
    }

    public ArrayList<IDownloadCallback> getCallbackList() {
        return mCallbackList;
    }
}
