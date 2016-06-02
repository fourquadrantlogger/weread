package com.fengwo.reading.player;

import android.text.TextUtils;

import com.fengwo.reading.main.read.IndexBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放列表的缓存
 * Created by timeloveboy on 16/4/17.
 */
public class Playlist_Cache {
    //
    public static List<IndexBean> 首页_list = new ArrayList<>(), 往期某书_list = new ArrayList<>();
    //todo
    public static boolean isreadfragment = true;

    public static List<IndexBean> list() {
        if (isreadfragment)
            return 首页_list;
        else
            return 往期某书_list;
    }


    //所返回 有音频的list,去掉没有音频的
    public static List<IndexBean> list_allhavemedia(List<IndexBean> mylist) {
        List<IndexBean> tsil = new ArrayList<>();
        for (int i = 0; i < mylist.size(); i++) {
            if (!TextUtils.isEmpty(mylist.get(i).media)) {
                tsil.add(mylist.get(i));
            }
        }
        return tsil;
    }

}
