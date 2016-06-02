package com.fengwo.reading.utils.localdata;

import android.os.Environment;

import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.MLog;
import java.io.File;


/**
 * Created by timeloveboy on 16/3/29.
 */
public class FileUtil {
    public static boolean clearMediaFile(){
        File sdCardDir = Environment.getExternalStorageDirectory();//获取SDCard目录
        File saveFile = new File(sdCardDir, GlobalParams.FolderPath_Media);
        MLog.v("file", saveFile.getPath());
        try {
            return delAllFile(saveFile.getPath());
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static boolean delAllFile(String path)
    {
         boolean flag = false;
         File file = new File(path);
         if (!file.exists()) {
         return flag;
         }
         if (!file.isDirectory()) {
         return flag;
          }
          String[] tempList = file.list();
         File temp = null;
         for (int i = 0; i < tempList.length; i++) {
          if (path.endsWith(File.separator)) {
         temp = new File(path + tempList[i]);
         } else {
         temp = new File(path + File.separator + tempList[i]);
         }
         if (temp.isFile()) {
          temp.delete();
         }
         if (temp.isDirectory()) {
         delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
         delFolder(path + "/" + tempList[i]);//再删除空文件夹
         flag = true;
         }
         }
         return flag;
     }
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean ExistMedia(String book_title,String title) {
        String mediaFolder = Environment.getExternalStorageDirectory() + GlobalParams.FolderPath_Media +
                book_title;
        String mediaName = title + ".mp3";
        File file = new File(mediaFolder, mediaName);
        return file.exists();
    }
    /**
     * 获取文件夹大小
     * @param   File实例
     * @return long
     */
    public static String getFolderSize(){
        File sdCardDir = Environment.getExternalStorageDirectory();//获取SDCard目录
        File saveFile = new File(sdCardDir, GlobalParams.FolderPath_Media);

        return getFolderSize(saveFile)/(1024*1024)+"MB";
    }
    public static long getFolderSize(java.io.File file){
        long size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++)
            {
                if (fileList[i].isDirectory())
                {
                    size = size + getFolderSize(fileList[i]);

                }else{
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return size;
    }
}
