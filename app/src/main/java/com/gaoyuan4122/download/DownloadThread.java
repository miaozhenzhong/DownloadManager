package com.gaoyuan4122.download;

import com.gaoyuan4122.download.db.DownloadDbManager;
import com.gaoyuan4122.download.util.HttpUtil;
import com.gaoyuan4122.download.util.SdcardUtil;

import org.apache.http.Header;
import org.apache.http.HttpException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * Created by GAOYUAN on 2015/5/26.
 */
public class DownloadThread extends Thread {
    public final static String ACCEPT = "image/gif, image/jpeg, image/pjpeg, image/pjpeg," +
            " application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument," +
            " application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel," +
            " application/vnd.ms-powerpoint, application/msword, */*";
    private final DownloadTask mTask;
    private final DownloadInfo mInfo;
    public boolean mIsRunning;
    protected long mStartPosition;
    protected long mEndPosition;

    /**
     * 对当前下载任务, 创建一个下载线程
     *
     * @param task
     */
    public DownloadThread(DownloadTask task) {
        mTask = task;
        mInfo = task.mDownloadInfo;
        // 调用下载任务的 onWait 回调
        mTask.onWait();
    }

    @Override
    public void run() {
        // 调用下载任务的 onWait 回调
        mTask.onStart();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", ACCEPT);
        headers.put("Accept-Language", "zh-CN");
        headers.put("Charset", "UTF-8");
        if (mInfo.mTotalSize != 0) {
            if (mInfo.mDownloadedSize == mInfo.mTotalSize) {
                mInfo.mDownloadedSize = mInfo.mTotalSize - 10;
            }
            mStartPosition = mInfo.mDownloadedSize;// 开始位置
            mEndPosition = mInfo.mTotalSize;// 结束位置
            headers.put("Range", "bytes=" + mStartPosition + "-" + mEndPosition);
        }
        headers.put("Connection", "Keep-Alive");
        HttpUtil httpUtil = new HttpUtil();
        try {
            httpUtil.downFile(mInfo.mUrl, headers, new HttpUtil.DownloadProcessor() {
                @Override
                public void processStream(InputStream stream, long totalSize, Header encoding, String newURL) {
                    try {
                        initDownloadDir(mInfo.mLocalPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mTask.onFail(e.getMessage());
                    }
                    if (stream != null) {
                        // 如果 info 的总大小字段为0, 说明是第一次进行下载, 此时将 info 大小写入到数据库
                        if (mInfo.mTotalSize == 0) {
                            DownloadDbManager.getInstance().updateTotalSizeToDb(mInfo.mId, totalSize);
                            mInfo.mTotalSize = totalSize;
                        }
                        // 创建缓冲区
                        byte[] buffer = new byte[1024 * 8];
                        int readLength = 0;
                        RandomAccessFile randomFile = null;
                        ByteArrayOutputStream bos = null;
                        try {
                            randomFile = new RandomAccessFile(mInfo.mLocalPath + ".tmp", "rwd");
                            randomFile.seek(mStartPosition);
                            bos = new ByteArrayOutputStream();
                            int tempLength = 0;
                            while ((readLength = stream.read(buffer)) > 0 && mIsRunning) {
                                bos.write(buffer, 0, readLength);
                                mInfo.mDownloadedSize += readLength;
                                // 每 100 kb 更新一下界面, 往文件里写一次, 然后清空字节流, 写数据库
                                tempLength += readLength;
                                if (tempLength >= 1024 * 100) {
                                    randomFile.write(bos.toByteArray());
                                    DownloadDbManager.getInstance().updateDownloadedSizeToDb(mInfo.mId, mInfo
                                            .mDownloadedSize, DownloadTask.STATE_DOWNLOADING);
                                    mTask.onUpdate((int) (100 * mInfo.mDownloadedSize / mInfo.mTotalSize));
                                    bos.reset();
                                    tempLength = 0;
                                }
                            }
                            // 最后还要再写一次文件
                            randomFile.write(bos.toByteArray());
                            if (mInfo.mDownloadedSize >= mInfo.mTotalSize) {
                                // 下载完成
                                File file = new File(mInfo.mLocalPath + ".tmp");
                                file.renameTo(new File(mInfo.mLocalPath));
                                mTask.onFinish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mTask.onFail(e.getMessage());
                        } finally {
                            if (bos != null) {
                                try {
                                    bos.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            if (randomFile != null) {
                                try {
                                    randomFile.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (stream != null) {
                                try {
                                    stream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }, true);
        } catch (HttpException e) {
            e.printStackTrace();
            mTask.onFail(e.getMessage());
        }
    }

    protected void initDownloadDir(String localPath) throws Exception {
        boolean SDCardIsExist = SdcardUtil.checkSDCard();
        if (!SDCardIsExist) {
            throw new Exception("SDcard not exists");
        }
        localPath = localPath.substring(0, localPath.lastIndexOf("/"));
        File downloadDir = new File(localPath);
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
    }
}
