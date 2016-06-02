package com.fengwo.reading.task;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;

import com.fengwo.reading.application.MyApplication;
import com.fengwo.reading.main.comment.BpInfoBean;
import com.fengwo.reading.main.comment.BpInfoJson;
import com.fengwo.reading.main.read.IndexBean;
import com.fengwo.reading.main.read.Json_BookInfoWithPacks;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by timeloveboy on 16/5/18.
 */
public class Task_download_all_bookpack {
    private Context context = null;

    public Task_download_all_bookpack(String pb_id) {
        this.pb_id = pb_id;
        new Thread(new Runnable() {
            @Override
            public void run() {
                getData_book_pack();
            }
        }).start();
    }
    public Task_download_all_bookpack(String pb_id,Context context) {
        this.pb_id = pb_id;
        this.context = context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                getData_book_pack();
            }
        }).start();
    }

    private String pb_id = "";  //共读id
    private List<IndexBean> list;
    private static int now_index;

    /**
     * 请求网络 - 查看拆书包
     */
    private void getData_book_pack() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("pb_id", pb_id);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.book_pack, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {

                    }


                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String jsonString = responseInfo.result;
                        MLog.v("reading",jsonString);
                        try {
                            Json_BookInfoWithPacks json = new Gson().fromJson(jsonString, Json_BookInfoWithPacks.class);
                            if ("1".equals(json.code)) {
                                list=json.data;
                                for(int i=0;i<list.size();i++){
                                   
                                    IndexBean bean=json.data.get(i);
                                    getData_pack_info(bean.id);
                                }
                            } else {

                            }

                        } catch (Exception e) {

                        }
                        /*if (context !=null){
                            CustomToast.showToast(context, "下载完成");
                        }*/
                    }
                }, true, null);
    }
    // 请求网络 - 拆书包详情
    private void getData_pack_info(String id) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", id);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.pack_info, new RequestCallBack<String>() {
                    //设置 302不可见，设置
                    @Override
                    public void onFailure(HttpException arg0, String error) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String jsonString = responseInfo.result;
                        MLog.v("reading", jsonString);

                        try {
                            BpInfoJson json = new Gson().fromJson(jsonString, BpInfoJson.class);
                            if ("1".equals(json.code)) {
                                BpInfoBean bpInfoBean = json.data;
                                httpdownload(bpInfoBean);//下载音频
                                NOsqlUtil.set_BpInfoBean(bpInfoBean);//存储json

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, true, null);
    }
    void httpdownload(BpInfoBean bpInfoBean) {
        if(TextUtils.isEmpty(bpInfoBean.media)){
            now_index++;

            Intent intent = new Intent();
            intent.setAction("DownloadAllPack");
            intent.putExtra("pb_id", list.get(0).pb_id);
            intent.putExtra("PackCount", list.size());
            intent.putExtra("now_index", now_index);
            MyApplication.getContext().sendOrderedBroadcast(intent,null);
            MLog.v("reading","PackCount"+list.size()+"\tnow_index:"+now_index);
            return;
        }
        final String mediaFolder = GlobalParams.FolderPath_Media + bpInfoBean.book_title;
        final String mediaName = bpInfoBean.title + ".mp3";
        HttpUtils http = new HttpUtils();
       http.download(bpInfoBean.media, Environment.getExternalStorageDirectory().getPath() + mediaFolder + "/" + mediaName,
                false, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        now_index++;

                        Intent intent = new Intent();
                        intent.setAction("DownloadAllPack");
                        intent.putExtra("pb_id",list.get(0).pb_id);
                        intent.putExtra("PackCount", list.size());
                        intent.putExtra("now_index",now_index);

                        MyApplication.getContext().sendBroadcast(intent);
                        MLog.v("reading","PackCount:"+list.size()+"\tnow_index:"+now_index);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {

                    }
                });
    }
}
