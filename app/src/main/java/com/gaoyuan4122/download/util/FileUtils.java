package com.gaoyuan4122.download.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * @author tiny <a href="mailto:tiny.ma@xinmei365.com">tiny</a> 12-3-29 下午4:56
 * @since version 1.0
 */
public class FileUtils {
    private static int BUFFER = 1024 * 4; // 这里缓冲区我们使用4KB

    /**
     * 递归删除文件
     *
     * @param file 要删除的文件
     */
    public static void deleteFile(File file) {
        if (file.exists()) {
            try {

                if (file.isFile()) { // 是文件直接删除
                    file.delete();
                } else if (file.isDirectory()) { // 是文件夹的话递归删除文件夹下的文件
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFile(files[i]);
                    }
                }
                // file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.i("bug", "文件不存在");
        }
    }

    // 删除文件，包括文件夹
    public static void deleteFile2(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                deleteFile2(childFiles[i]);
            }
            file.delete();
        }
    }

    /**
     * 文件copy
     *
     * @param srcFile  源文件
     * @param desFile  新建文件
     * @param isEnough 内存状态
     */
    public static void copyFile(String srcFile, String desFile, boolean isEnough) {

        if (srcFile != null && srcFile.trim().length() > 0) {
            File src = new File(srcFile);
            if (!src.exists()) {
                return;
            }
            FileInputStream fis = null;
            FileOutputStream fos = null;
            // if(MemoryStatus.getAvailableExternalMemorySize() > src.length())
            // {
            if (isEnough) { // 内存足够
                try {
                    fis = new FileInputStream(srcFile);
                    fos = new FileOutputStream(desFile);

                    byte[] buffer = new byte[1024 * 10];
                    int c = -1;
                    while ((c = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, c);
                    }
                    fos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                Log.i("bug", "内存不足");
            }
        }
    }

    /**
     * 文件copy
     *
     * @param srcFile  源文件
     * @param desFile  新建文件
     * @param isEnough 内存状态
     * @throws Exception
     */
    public static void copyFile(String srcFile, String desFile) throws Exception {

        if (srcFile != null && srcFile.trim().length() > 0) {
            File src = new File(srcFile);
            if (!src.exists()) {
                throw new Exception("src file is not exists");
            }
            FileInputStream fis = null;
            FileOutputStream fos = null;
            boolean isEnough = MemoryStatus.getAvailableExternalMemorySize() > src.length();
            if (isEnough) { // 内存足够
                try {
                    File des = new File(desFile);
                    if (!des.exists()) {
                        des.createNewFile();
                    }
                    fis = new FileInputStream(srcFile);
                    fos = new FileOutputStream(desFile);

                    byte[] buffer = new byte[1024 * 100];
                    int c = -1;
                    while ((c = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, c);
                    }
                    fos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    File file = new File(desFile);
                    if (file.exists()) {
                        file.deleteOnExit();
                    }
                    throw new Exception("IO Exception");
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                throw new Exception("memory not enough");
            }
        }
    }

    public static void copyFile1(String srcFile, String desFile, boolean isEnough) {

        Log.e("srcFile------->>", srcFile);
        Log.e("desFile------->>", desFile);

        Process process = null;
        if (srcFile != null && srcFile.trim().length() > 0) {
            File src = new File(srcFile);
            File des = new File(desFile);

            if (!src.exists()) {
                return;
            }
            if (des.exists()) {
                des.delete();
                try {
                    des.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String cmdStr = "chmod 777 " + desFile + " \n";
            try {
                process = Runtime.getRuntime().exec(cmdStr, null, null);

                int retValue = process.waitFor();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                process.destroy();
            }
            FileInputStream fis = null;
            FileOutputStream fos = null;
            if (isEnough) { // 内存足够
                try {

                    fis = new FileInputStream(srcFile);
                    fos = new FileOutputStream(desFile);
                    byte[] buffer = new byte[1024];
                    int c = -1;
                    while ((c = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, c);
                    }
                    fos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                Log.i("bug", "内存不足");
            }
        }
        String cmdStr = "chmod 777 " + desFile + " \n";
        try {
            process = Runtime.getRuntime().exec(cmdStr, null, null);
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
            errorGobbler.start();
            outputGobbler.start();
            int retValue = process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            process.destroy();
        }
    }

    /**
     * 解压字体apk文件
     *
     * @param apkFile     apk文件名
     * @param saveNamePre 解压到指定的文件夹 （可以是当前目录）
     */
    public static void unzipApk(String apkFile, String saveNamePre, String fileName) {
        // int BUFFER = 4096; // 这里缓冲区我们使用4KB
        try {
            BufferedOutputStream dest = null; // 缓冲输出流
            FileInputStream fis = new FileInputStream(apkFile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry; // 每个zip条目的实例
            while ((entry = zis.getNextEntry()) != null) {

                try {
                    if (entry.getName().contains("font")) {
                        boolean isEn = false;
                        if (entry.getName().contains("font-en")) {
                            isEn = true;
                        }
                        int count;
                        byte data[] = new byte[BUFFER];
                        File entryFile = new File(saveNamePre + fileName + "-zh.ttf");
                        if (isEn) {
                            entryFile = new File(saveNamePre + fileName + "-en.ttf");
                        }

                        File entryDir = new File(entryFile.getParent());
                        if (!entryDir.exists()) {
                            entryDir.mkdirs();
                        }

                        FileOutputStream fos = new FileOutputStream(entryFile);
                        dest = new BufferedOutputStream(fos, BUFFER);
                        while ((count = zis.read(data, 0, BUFFER)) != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.flush();
                        dest.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            zis.close();
        } catch (Exception cwj) {
            cwj.printStackTrace();
        }
    }

    /**
     * 压缩文件,文件夹 ,为打MIUI的包
     *
     * @param srcFilePath 要压缩的文件/文件夹名字
     * @param zipFilePath 指定压缩成的文件名
     * @throws Exception
     */
    public static void zipFolder(String srcFilePath, String zipFilePath) throws Exception {
        // 创建Zip包
        java.util.zip.ZipOutputStream outZip = new java.util.zip.ZipOutputStream(new FileOutputStream(zipFilePath));

        // 打开要输出的文件
        File file = new File(srcFilePath);
        // 压缩
        zipFiles(file.getParent() + File.separator, file.getName(), outZip);
        // 完成,关闭
        outZip.finish();
        outZip.close();
    }

    /**
     * 压缩文件
     *
     * @param folderPath
     * @param filePath
     * @param zipOut
     * @throws Exception
     */
    public static void zipFiles(String folderPath, String filePath, java.util.zip.ZipOutputStream zipOut) throws Exception {

        if (zipOut == null) {
            return;
        }
        File file = new File(folderPath + filePath);

        // 判断是不是文件
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(filePath.replaceAll("miui/", ""));

            FileInputStream inputStream = new FileInputStream(file);
            zipOut.putNextEntry(zipEntry);

            int len;
            byte[] buffer = new byte[BUFFER];

            while ((len = inputStream.read(buffer)) != -1) {
                zipOut.write(buffer, 0, len);
            }
            zipOut.closeEntry();
            inputStream.close();
        } else {
            // 文件夹的方式,获取文件夹下的子文件
            String fileList[] = file.list();

            // 如果没有子文件, 则添加进去即可
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(filePath + File.separator);
                zipOut.putNextEntry(zipEntry);
                zipOut.closeEntry();
            }
            // 如果有子文件, 遍历子文件
            for (String aFileList : fileList) {
                zipFiles(folderPath, filePath + File.separator + aFileList, zipOut);
            }
        }
    }

    /**
     * 解压缩略图ttf文件
     *
     * @param zipFile
     * @param saveName
     */
    public static void unzipTmpTTf(String zipFile, String saveName) {

        // Log.e("saveName------>",saveName);
        File saveFile = new File(saveName);
        if (!saveFile.exists()) {
            int BUFFER = 4096; // 这里缓冲区我们使用4KB，
            try {
                BufferedOutputStream dest = null; // 缓冲输出流
                FileInputStream fis = new FileInputStream(zipFile);
                ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
                while (zis.getNextEntry() != null) {
                    try {
                        int count;
                        byte data[] = new byte[BUFFER];
                        File entryFile = new File(saveName);
                        File entryDir = new File(entryFile.getParent());
                        if (!entryDir.exists()) {
                            entryDir.mkdirs();
                        }
                        FileOutputStream fos = new FileOutputStream(entryFile);
                        dest = new BufferedOutputStream(fos, BUFFER);
                        while ((count = zis.read(data, 0, BUFFER)) != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.flush();
                        dest.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                zis.close();

            } catch (Exception cwj) {
                cwj.printStackTrace();
            }
        }
        File file = new File(zipFile);
        if (file.exists()) {
            file.delete();
        }

    }

    public static String getCustomFontPath() {
        String ret = "";
        File file = new File("/data/data/com.android.settings/app_fonts/sans.loc");
        if (file != null && file.exists()) {
            InputStream is;
            OutputStream os;
            try {
                is = new FileInputStream(file);
                os = new ByteArrayOutputStream();
                int b = -1;
                while ((b = is.read()) != -1) {
                    os.write(b);
                }
                os.flush();
                os.close();
                is.close();
                ret = os.toString();
                ret = ret.substring(0, ret.lastIndexOf("#"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return ret;
    }

    /**
     * 这个是手机内存的可用空间大小
     *
     * @return
     */
    static public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 递归获得缓存文件大小
     *
     * @param f
     * @return
     */
    public static long getFileSize(File f) {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    /**
     * 将递归获得的文件夹大小转成字符串
     *
     * @param fileS
     * @return 如果为.00B 返回0B
     */
    public static String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";

        // 最小单位是Mb，最大单位也是Mb
        // if (fileS < 1024) {
        // fileSizeString = df.format((double) fileS) + "B";
        // } else if (fileS < 1048576) {
        // fileSizeString = df.format((double) fileS / 1024) + "K";
        // } else if (fileS < 1073741824) {
        // fileSizeString = df.format((double) fileS / 1048576) + "Mb";
        // } else {
        // fileSizeString = df.format((double) fileS / 1073741824) + "G";
        // }
        fileSizeString = df.format((double) fileS / 1048576) + "Mb";
        if (fileSizeString.equals(".00Mb")) {
            fileSizeString = new String("0Mb");
        }
        if (fileSizeString.startsWith(".")) {
            fileSizeString = "0" + fileSizeString;
        }
        return fileSizeString;
    }

    /**
     * 获得一定格式的缓存文件夹大小
     *
     * @param f
     * @return
     */
    public static String getFileCacheSize(File f) {
        return formetFileSize(getFileSize(f));
    }

    /**
     * 保存字符串到文件
     *
     * @param str
     */
    public static void saveStringFile(String str, Context context, String fileName) {
        try {
            // FileOutputStream outStream = context.openFileOutput(fileName,
            // Context.MODE_WORLD_READABLE);
            File file1 = context.getCacheDir();
            File file2 = new File(file1, fileName);
            if (!file2.exists()) {
                file2.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file2);
            outStream.write(str.getBytes());
            outStream.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    /**
     * 获得文件中的字符串
     *
     * @return
     */
    public static String getStringFile(Context context, String fileName) {
        byte[] buffer = new byte[1024];
        int length = -1;
        String str = null;
        try {
            // FileInputStream inStream = context.openFileInput(fileName);
            File file1 = context.getCacheDir();
            File file2 = new File(file1, fileName);
            if (!file2.exists()) {
                return null;
            }
            FileInputStream inStream = new FileInputStream(file2);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            while ((length = inStream.read(buffer)) != -1) {
                stream.write(buffer, 0, length);
            }
            stream.close();
            inStream.close();
            str = stream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;

    }

    /**
     * 因为java删除文件内容只有一种实现方法，就是把整个文件重写，只是把须要删除的那一条记录去除掉
     */
    public static void cleanFile(Context context, String fileName) {
        // 如果只须要删除文件中的一部分内容则须要在这里对字符串做一些操作
        String cleanStr = "";
        try {
            // FileOutputStream outStream = context.openFileOutput(fileName,
            // Context.MODE_WORLD_READABLE);
            File file1 = context.getCacheDir();
            File file2 = new File(file1, fileName);
            if (!file2.exists()) {
                file2.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file2);
            outStream.write(cleanStr.getBytes());
            outStream.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

    }
}
