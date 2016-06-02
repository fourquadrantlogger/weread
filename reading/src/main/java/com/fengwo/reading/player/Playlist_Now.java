package com.fengwo.reading.player;

import android.os.Environment;

import com.fengwo.reading.main.comment.BpInfoBean;
import com.fengwo.reading.main.read.IndexBean;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.MLog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 当前播放列表
 * Created by timeloveboy on 16/4/8.
 */
public class Playlist_Now {

    public static Integer musicID = null;//那首歌

    public static boolean setmusicID(BpInfoBean bpInfoBean){
        for(int i=0;i< musicFiles().size();i++){
            if(bpInfoBean.media.equals(musicFiles().get(i))){
               musicID=i;
                return true;
            }
        }
        return false;
    }
    //周一到周五的顺序
    public static List<IndexBean> Medialist() {
        //todo
        List<IndexBean> tsil = new ArrayList<>();
        //todo
        List<IndexBean> mylist= Playlist_Cache.list_allhavemedia(Playlist_Cache.list());

        if(Playlist_Cache.isreadfragment) {// 首页 需要逆转列表
            for (int i = 0; i < mylist.size(); i++) {
                tsil.add(mylist.get(mylist.size() - 1 - i));
            }
        }else {
            tsil=mylist;
        }
        return tsil;
    }
    //音频地址集合
    public static ArrayList<String> musicFiles() {
        ArrayList<String> list = new ArrayList<>();
        if (Playlist_Now.Medialist() != null) {
            for (int i = 0; i < Playlist_Now.Medialist().size(); i++) {
                list.add(Playlist_Now.Medialist().get(i).media);
            }
        }
        return list;
    }


    //当前所播放的mediabookpack json
    public static IndexBean media_now_IndexBean;
    // 如果存在缓存文件,则播放缓存
    public static String getLocalMediaPath(boolean donotsavecache) {

        media_now_IndexBean=Medialist().get(musicID);
        String url = musicFiles().get(musicID);

        MLog.v("music", musicID + "");
        if(media_now_IndexBean.Exist()){
            return media_now_IndexBean.media_localpath();
        } else {
            //todo
            if(donotsavecache)
                return url;
            else {
                Playlist_Now musicFile = new Playlist_Now();
                return musicFile.httpdownload(url,media_now_IndexBean.media_localpath());
            }
            //
        }
    }
    boolean downloadok=false;
    public String httpdownload(String url,String localpath){
        HttpUtils http = new HttpUtils();

        HttpHandler myhandler = http.download(url,//下载地址
                localpath,//保存地址
                true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {

                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        downloadok=true;
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {

                    }
                });

        while (true) {
            if(downloadok)
                //如果下载成功则返回下载内容的sd卡存储地址
                return localpath;
        }
    }
}

