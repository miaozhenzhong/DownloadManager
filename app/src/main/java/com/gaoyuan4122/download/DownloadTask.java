package com.gaoyuan4122.download;


import android.os.Handler;
import android.os.Message;

import com.gaoyuan4122.download.util.MD5Util;

/**
 * Created by GAOYUAN on 2015/5/26.
 */
public class DownloadTask {
    public static final String TAG = DownloadTask.class.getSimpleName();

    // 状态是没有 START 的
    public static final int STATE_WAIT = 1;
    public final static int STATE_DOWNLOADING = 2;
    public final static int STATE_STOP = 3;
    public final static int STATE_FAIL = 4;
    public final static int STATE_FINISH = 5;

    public final static int CALLBACK_STATE_WAIT = 11;
    public final static int CALLBACK_STATE_START = 12;
    public final static int CALLBACK_STATE_STOP = 13;
    public final static int CALLBACK_STATE_FAIL = 14;
    public final static int CALLBACK_STATE_FINISH = 15;
    public final static int CALLBACK_STATE_DOWNLOADING = 16;

    public int mState = 0;
    public String mId;
    public DownloadInfo mDownloadInfo;
    public DownloadThread mDownloadThread;
    private DownloadManager mDownloadManager;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CALLBACK_STATE_WAIT:
                    for(IDownloadCallback callback : mDownloadInfo.getCallbackList()) {
                        callback.onWait();
                    }
                    break;
                case CALLBACK_STATE_START:
                    for(IDownloadCallback callback : mDownloadInfo.getCallbackList()) {
                        callback.onWait();
                    }
                    break;
                case CALLBACK_STATE_DOWNLOADING:
                    break;
                case CALLBACK_STATE_STOP:
                    break;
                case CALLBACK_STATE_FINISH:
                    break;
                case CALLBACK_STATE_FAIL:
                    break;
            }
        }
    };

    public DownloadTask(DownloadInfo downloadInfo) {
        mDownloadInfo = downloadInfo;
        mId = MD5Util.getMD5Pass(downloadInfo.mName);
        mDownloadManager = DownloadManager.getInstance();
        mDownloadThread = new DownloadThread(this);
    }

    /**
     * 任务处于等待状态时的回调
     */
    public void onWait() {
        mHandler.sendEmptyMessage(CALLBACK_STATE_WAIT);
    }


    /**
     * 开始一个任务
     */
    public void start() {
        mDownloadThread.mIsRunning = true;
    }

    /**
     * 任务开始时的回调
     */
    public void onStart() {
        mHandler.sendEmptyMessage(CALLBACK_STATE_START);
    }

    public void stop() {
    }

    public void onStop() {

    }

    public void restart() {
        start();
    }

    public void onFail(String message) {

    }

    public void onUpdate(int i) {
    }

    public void onFinish() {
    }
}
