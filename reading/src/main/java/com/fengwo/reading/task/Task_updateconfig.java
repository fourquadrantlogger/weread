package com.fengwo.reading.task;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.fengwo.reading.application.MyApplication;
import com.fengwo.reading.common.CustomPopupWindowDialog;
import com.fengwo.reading.main.read.Fragment_WeRead;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.task.config.Json_Config;
import com.fengwo.reading.utils.ActivityUtil;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 更新config
 */
public class Task_updateconfig {

    public Task_updateconfig() {
        getData();
    }

    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.config,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        SPUtils.setAppTimeFirst(false);
                    }

                    @Override
                    public void onFailure(HttpException arg0, String error) {
                        arg0.printStackTrace();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String jsonString = responseInfo.result;
                        MLog.v("reading", "config:\n" + jsonString);
                        try {
                            final Json_Config json = new Gson().fromJson(jsonString, Json_Config.class);
                            if ("1".equals(json.code)) {
                                SPUtils.setAppTime(SPUtils.getToday());
                                //闹铃
                                if (json.nao_ling != null && "1".equals(json.nao_ling.is_config)) {
                                    NOsqlUtil.set_naoling(json.nao_ling);
                                }
                                //文字限制
                                if (json.word_limit != null) {
                                    NOsqlUtil.set_wordlimit(json.word_limit);
                                }
                                //首页引导条
                                if (json.act == null) {
                                    SPUtils.setAppConfig_act("");
                                } else {
                                    SPUtils.setAppConfig_act(new Gson().toJson(json.act));
                                    if (json.act.action_type.equals("alert")) {
                                        File file = new File(Environment.getExternalStorageDirectory() + "/fengwo_note_reading3");
                                        if (!file.exists()) {
                                            // 为空
                                            if (file.mkdir()) {
                                                saveImage(json.act.img);
                                            }
                                        } else {
                                            // 不为空,全部清除
                                            File files[] = file.listFiles();
                                            for (int i = 0; i < files.length; i++) {
                                                deleteFile(files[i]);
                                            }
                                            saveImage(json.act.img);
                                        }
                                    }
                                }
                                //开屏广告
                                if (json.ad != null) {
                                    NOsqlUtil.setConfig_ad(json.ad);
                                }
                                //六月书单
                                if (json.shudan!=null){
                                    NOsqlUtil.setConfig_shudan(json.shudan);
                                    Log.i("shudan",json.shudan.title);
                                }

                                Fragment_WeRead.getInstance().refreshConfig(
                                        ActivityUtil.mainActivity);
                            } else {
                                SPUtils.setAppConfig_act("");

                                Fragment_WeRead.getInstance().refreshConfig(
                                        ActivityUtil.mainActivity);
                            }
                        } catch (Exception e) {
                            SPUtils.setAppConfig_act("");
                            Fragment_WeRead.getInstance().refreshConfig(
                                    ActivityUtil.mainActivity);
                        }
                    }
                }, true, null);
    }

    private void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }

    private void saveImage(String url) {
        String name = String.valueOf(url.hashCode());
        final File f = new File(Environment.getExternalStorageDirectory()
                + "/fengwo_note_reading3", (name + ".jpg"));
        SPUtils.setAppTimeName(name);
        // 加载图片，然后存在本地
        ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                                        FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    boolean b = loadedImage.compress(Bitmap.CompressFormat.PNG,
                            100, out);
                    out.flush();
                    out.close();
                    if (b) {
                        SPUtils.setAppTimeFirst(true);
                        if (SPUtils.getAppTimeFirst()) {
                            // SPUtils.setAppTimeFirst(context, false);
                            // TODO
                            new CustomPopupWindowDialog(MyApplication.getContext(), SPUtils.getAppConfig_act().img, SPUtils.getAppConfig_act().href).show();
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
    }

}
