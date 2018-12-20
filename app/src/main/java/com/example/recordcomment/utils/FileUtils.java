package com.example.recordcomment.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by zhwp on 2018/4/9.
 */

public class FileUtils {
	/*
	 * 文件删除
	 */
	public static boolean deleteFile(String filePath) {
		if (StringUtil.isNull(filePath)) {
			return true;
		}
		File file = new File(filePath);
		if (file.exists() == false) {
			return false;
		}
		return file.delete();
	}

    /*
     * 判断文件是否存在
     */
    public static boolean fileIsExists(String filePath) {
        if(StringUtil.isNull(filePath)){
            return false;
        }
        
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static long getFolderSize(File file) {

        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);

                } else {
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return size;
    }

    /*
     * 删除文件并删除路径
     */
	public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
		if (StringUtil.isNull(filePath)) {
			return;
		}
		try {
			File file = new File(filePath);
			if (file.isDirectory()) {// 处理目录
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFolderFile(files[i].getAbsolutePath(), true);
				}
			}
			if (deleteThisPath) {
				if (!file.isDirectory()) {// 如果是文件，删除
					file.delete();
				} else {// 目录
					if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
						file.delete();
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public static long getFileSizeToM(double size) {
        long fileSize = (long) Math.floor(size * 1.0f);
        fileSize = fileSize / (1024 * 1024);
        return fileSize;
    }

    public static long readSDCardAvailableMemorySize() {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();
            long availCount = sf.getAvailableBlocks();
//			Log.d("", "block大小:"+ blockSize+",block数目:"+ blockCount+",总大小:"+blockSize*blockCount/1024+"KB");
//			Log.d("", "可用的block数目：:"+ availCount+",剩余空间:"+ availCount*blockSize/1024+"KB");
            long availableSize = availCount*blockSize;
            return getFileSizeToM(availableSize);

        }

        return 0;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }
    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                if(fis != null) {
                    size = fis.available();
                }
            }

        } catch (Exception e) {
            size = 0;

        }

        return size;
    }

    public static void creatFoledr(String path){
        if(StringUtil.isNull(path)){
            return;
        }

        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }

    }

    /**
     *调用此方法判断文件是否存在并获取文件大小
     *
     * @param filePath 文件绝对路径
     *
     * @return 0：文件不存在 ;   ！=0 ：文件存在并返回其大小
     */
    public static long judgeIsExistAndGetSize(String filePath){
        File file = new File(filePath);
        if(!file.exists()){
            return 0;
        }
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            blockSize = 0;
        }
        return blockSize;
    }

    public static String getRecordCacheDirPath(Context context){
        String filePath = context.getExternalCacheDir().getAbsolutePath();
        if(!TextUtils.isEmpty(filePath)) {
            filePath += File.separator + TimeUtil.getCurFormatTime() + ".wav";
            return filePath;
        }
        return "";
    }
}
