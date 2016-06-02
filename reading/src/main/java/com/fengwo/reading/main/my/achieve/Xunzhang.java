package com.fengwo.reading.main.my.achieve;

import android.graphics.Bitmap;

import com.fengwo.reading.utils.ImageUtils;

/**
 * Created by timeloveboy on 16/5/4.
 */
public class Xunzhang {

    public String id="";
    public String limg_url="";

    public String localPath(){
        return Json_wodexunzhang.xunzhangFolder()+id+".png";
    }
    public Bitmap bitmap(){
        return ImageUtils.getLoacalBitmap(localPath());
    }
    public String badge="";
    public String get_rule;
    public String ask_sum;
    public String simg_url;
    public String create_time="";//获取时间

    public boolean got=false;//是否获得
}