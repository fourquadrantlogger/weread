package com.fengwo.reading.main.read;

import android.os.Environment;

import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.player.Playlist_Cache;
import com.fengwo.reading.utils.MediaFileFilter;
import com.fengwo.reading.utils.localdata.FileUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

public class Json_BookInfoWithPacks extends BaseJson {
    public List<IndexBean> data;

    public List<IndexBean> data_allhavemedia() {
        return Playlist_Cache.list_allhavemedia(data);
    }

    public List<String> check;//check里的数据和data里的匹配，check存在的数据即是已签的拆书包
    public Bean_Book book_data;

    public String bookfolder() {
        String mediaFolder = Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Media + book_data.book_title;
        return mediaFolder;
    }

    public String localsize() {
        File file = new File(bookfolder());
        if (file.exists()) {
            return FileUtil.getFolderSize(file) / (1042 * 1024) + "MB";
        } else {
            return "0 MB";
        }
    }

    public int mediaalreadycount() {
        File file = new File(bookfolder());
        if (file.exists()) {
            return file.listFiles(new MediaFileFilter(".mp3")).length;
        } else return 0;
    }
}
