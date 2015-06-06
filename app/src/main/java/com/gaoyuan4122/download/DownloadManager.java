package com.gaoyuan4122.download;

import com.gaoyuan4122.download.db.DownloadDbManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by GAOYUAN on 2015/5/26.
 */
public class DownloadManager {
    private static DownloadManager ourInstance = new DownloadManager();
    /**
     * 执行队列
     */
    private LinkedList<DownloadTask> mDownloadingTaskList;
    /**
     * 最大同时下载数
     */
    private int mMaxDownloadingTaskSize = 3;
    /**
     * 等待队列
     */
    private LinkedList<DownloadTask> mWaitTaskList;
    /**
     * 暂停队列
     */
    private LinkedList<DownloadTask> mPauseTaskList;
    /**
     * 下载任务map
     */
    private HashMap<String, DownloadTask> mTaskMap;
    /**
     * 下载线程池
     */
    private ThreadPoolExecutor mThreadPool;
    private int mMaxQueueSize = 20; // 最大线程数, 我感觉应该和最大下载数量一样
    private int mCorePoolSize = 5;
    private int mMaxPoolSize = 5;
    private long mKeepAliveTime = 1L;

    private DownloadManager() {
        mDownloadingTaskList = new LinkedList<DownloadTask>();
        mWaitTaskList = new LinkedList<DownloadTask>();
        mPauseTaskList = new LinkedList<>();
        mTaskMap = new HashMap<String, DownloadTask>();
        // TODO 既然有成员变量, 成员变量可修改, 不应再这里初始化线程池.
        mThreadPool = new ThreadPoolExecutor(mCorePoolSize, mMaxPoolSize, mKeepAliveTime, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(mMaxQueueSize));
    }

    public static DownloadManager getInstance() {
        return ourInstance;
    }

    /**
     * 添加一个下载任务
     *
     * @param info
     */
    public void addTask(DownloadInfo info) {
        // 1. 使用 downloadInfo new 一个 downloadTask
        // 2. 判断 任务map 中有没有这个任务, 如果没有, 才进行处理
        // 3. 将 task 的状态改为 STATE_WAIT
        // 4. 添加到 任务map 中
        // 5. 添加到等待队列中
        // 6. 将 downloadInfo 信息写入到数据库中, 获取返回值 id
        // 7. 将 id 赋给 downloadInfo
        // 8. 执行下一个下载任务
        DownloadTask task = new DownloadTask(info);
        if(!mTaskMap.containsKey(task.mId)) {
            task.mState = DownloadTask.STATE_WAIT;
            task.onWait();
            mWaitTaskList.add(task);
            mTaskMap.put(task.mId, task);
            long id = DownloadDbManager.getInstance().addInfoToDb(info);
            info.mId = id;
        }
        executeNextTask();
    }

    public void stopTask(DownloadTask task) {
        // 1. 判断下载任务的状态
        //     1.1. STATE_WAIT, 将状态改为 STATE_STOP, 调用任务的 stop 方法, 并将其从等待队列中移除
        //     1.2. STATE_DOWNLOADING, 将状态改为STATE_STOP, 调用任务的 stop 方法, 从线程池中移除, 从执行队列中移除
        // 2. 将任务加到暂停队列的队尾
        // 3. 执行下一个任务
        // TODO 4. 更改数据库信息
        switch (task.mState) {
            case DownloadTask.STATE_WAIT:
                mWaitTaskList.remove(task);
                break;
            case DownloadTask.STATE_DOWNLOADING:
                mDownloadingTaskList.remove(task);
                mThreadPool.remove(task.mDownloadThread);
                break;
            default:
                return;
        }
        task.mState = DownloadTask.STATE_STOP;
        task.stop();
        // 注意 addLast
        mPauseTaskList.addLast(task);
        executeNextTask();
    }

    public void removeTask(DownloadTask task) {
        // 1. 判断下载任务状态
        //     1.1. STATE_WAIT, 从等待队列中移除即可
        //     1.2. STATE_DOWNLOADING, 先将任务停下来(改状态, 调stop), 再从线程池中移除, 再从执行队列中移除
        //     1.3. STATE_STOP, 直接从暂停队列中移除.
        // 2. 从任务map里移除
        // 3. 从数据库里移除
        // 4. 执行下一个任务
        switch (task.mState) {
            case DownloadTask.STATE_WAIT:
                mWaitTaskList.remove(task);
                break;
            case DownloadTask.STATE_DOWNLOADING:
                // TODO 到底应将状态改为什么
                task.mState = DownloadTask.STATE_STOP;
                task.stop();
                mThreadPool.remove(task.mDownloadThread);
                mDownloadingTaskList.remove(task);
                break;
            case DownloadTask.STATE_STOP:
                mWaitTaskList.remove(task);
                break;
            default:
                return;
        }
        mTaskMap.remove(task.mId);
        DownloadDbManager.getInstance().removeInfoFromDb(task.mDownloadInfo);
        executeNextTask();
    }

    public void restartTask(DownloadTask task) {
        // 1. 判断下载任务状态
        //     1.1. 如果为 STATE_STOP, 改状态为 STATE_DOWNLOADING, 调用 start 方法, 从暂停队列中移除.
        //     1.2. 其余状态无需做任何事情
        // 2. 将任务加入到等待队列中
        // 3. 执行下一个任务
        switch (task.mState) {
            case DownloadTask.STATE_STOP:
                task.mState = DownloadTask.STATE_DOWNLOADING;
                task.restart();
                mWaitTaskList.add(task);
                break;
            default:
                return;
        }
        executeNextTask();
    }

    /**
     * 完成一个下载任务, 不需要框架使用者去调用
     * @param task
     */
    public void finishTask(DownloadTask task) {
        // 1. 从任务map中移除
        // 2. 从执行队列中移除
        // 3. 从数据库中移除
        // 4. 执行下一个任务
        if(task != null) {
            mDownloadingTaskList.remove(task);
            mTaskMap.remove(task.mId);
            DownloadDbManager.getInstance().removeInfoFromDb(task.mDownloadInfo);
        }
        executeNextTask();
    }

    /**
     * 执行下一个任务
     */
    private void executeNextTask() {
        // 1. 获取等待队列中的第一个任务
        // 2. 如果执行队列的大小小于最大执行数量, 并且执行队列中不包含当前任务, 才进行处理
        // 3. 将任务加入到执行队列中
        // 4. 将任务状态改为 STATE_DOWNLOADING, 并调用任务的开始方法
        // 5. 将任务放进下载线程池中执行
        // 6. 从等待队列中移除任务
        if(mWaitTaskList.size() > 0) {
            DownloadTask task = mWaitTaskList.getFirst();
            if(mDownloadingTaskList.size() < mMaxDownloadingTaskSize && !mDownloadingTaskList.contains(task)) {
                mDownloadingTaskList.add(task);
                task.mState = DownloadTask.STATE_DOWNLOADING;
                task.start();
                mThreadPool.execute(task.mDownloadThread);
                mWaitTaskList.remove(task);
            }
        }
    }
}
