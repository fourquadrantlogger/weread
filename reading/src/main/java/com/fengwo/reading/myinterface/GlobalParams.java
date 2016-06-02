package com.fengwo.reading.myinterface;

import com.fengwo.reading.bean.UserInfoBean;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;


public class GlobalParams {

    public static String uid = "1";
    public static String weiboaccess_token = "";
    public static UserInfoBean userInfoBean = null;
    public static String cid = "";

    public static String wx = "";
    public static String qq = "";
    public static String weibo = "";
    public static String type = "";

    public static boolean isOne = true; //首页是否传id带有书圈
    public static long time = 0; //间隔时间

    public static List<NameValuePair> NAMEVALUEPAIRS = new ArrayList<>();// 封装具体参数的集合

    public static String FolderPath = "/YOUSHU/";
    public static String FolderPath_Photo = "/YOUSHU/PHOTO/";  //图片保存
    public static String FolderPath_Media = "/YOUSHU/MEDIA/";  //音频
    public static String FolderPath_Shoot = "/YOUSHU/SHOOT/";  //拍照


}
