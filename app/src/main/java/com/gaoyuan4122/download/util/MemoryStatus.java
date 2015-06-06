package com.gaoyuan4122.download.util;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * @author tiny <a href="mailto:tiny.ma@dpaopao.com">tiny</a>
 *         11-12-9 下午2:08
 * @since version 1.0
 */
public class MemoryStatus {

    private static final int ERROR = -1;

    /**
     * sdcard 是否可用
     *
     * @return flag
     */
    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                && Environment.getExternalStorageDirectory().canWrite();
    }

    /**
     * 返回内部存储可用大小，单位为字节
     *
     * @return long
     */
    public static long getAvailableInternalMemorySize() {
        File path = new File("/system");
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 返回内部存储可用大小，单位为字节
     *
     * @return ｌｏｎｇ
     */
    public static long getFontDriMemorySize() {
        File path = new File("/system/fonts");
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }


    /**
     * 返回内部存储总大小 ，单位为字节
     *
     * @return long
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 返回 sdcard 可用大小  ，单位为字节
     *
     * @return ｌｏｎｇ
     */
    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return ERROR;
        }
    }

    public static long getSDFreeSize() {
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
    }

    /**
     * 返回 sdcard 总容量  ，单位为字节
     *
     * @return long
     */
    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return ERROR;
        }
    }

    /**
     * 返回传入大小的格式化单位字符串；KB或MB
     *
     * @param size 字节大小
     */
    public static String formatSize(long size) {
        String suffix = null;
        if (size < 1024) {
            suffix = "B";
        }

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }
}
