package com.fengwo.reading.bean;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.fengwo.reading.main.my.achieve.Json_wodexunzhang;
import com.fengwo.reading.utils.ImageUtils;

public class UserInfoBean {
    public String user_id = "";// id
    public String avatar; // 头像
    public String name;// 姓名
    public String sex;// 性别,2女1男0没选
    public String job;// 职业
    public String province;// 省
    public String city;// 市
    public String intro;// 签名
    public String create_time;//
    public String user_img;//我的随笔背景大图


    //region 1.0.7
    public String level;//": "5",//等级
    public String exp;//: "50",//总经验
    public String badge_id;//": "5",//使用的勋章id

    public Drawable badge_Drawable(float sx, float sy) {
        if (badge_id == null || badge_id.equals("0")) {
            Drawable drawable = new BitmapDrawable();
            drawable.setBounds(0, 0, 40, 50);
            return drawable;
        } else {
            Bitmap source = ImageUtils.getLoacalBitmap(Json_wodexunzhang.xunzhangFolder() + badge_id + ".png");
            Matrix matrix = new Matrix();
            // 缩放原图
            matrix.postScale(sx, sy);
            Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
            Drawable drawable = new BitmapDrawable(bitmap);
//            drawable.setBounds(0, 0, 40, 50);
            return drawable;
        }
    }

}
