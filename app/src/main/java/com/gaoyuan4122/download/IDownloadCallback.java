package com.gaoyuan4122.download;

/**
 * 下载回调接口
 * Created by GAOYUAN on 2015/5/26.
 */
public interface IDownloadCallback {
    void onWait();
    void onStart();
    void onUpdate(int progress);
    void onFinish();
    void onFail(String error);
    void onStop();
}
