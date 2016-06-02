package com.fengwo.reading.utils.localdata;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.fengwo.reading.application.MyApplication;
import com.fengwo.reading.bean.UserInfoBean;

import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.task.config.Bean_act;
import com.fengwo.reading.umeng.UMengBean;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lxq 配置信息存储
 */
public class SPUtils {
    public static void clearData(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("info_uid", GlobalConstant.ISLOGINUID);
        editor.putString("info_avatar", "");
        editor.putString("info_name", "");
        editor.putString("info_sex", "");
        editor.putString("info_job", "");
        editor.putString("info_province", "");
        editor.putString("info_city", "");
        editor.putString("info_intro", "");
        editor.putString("info_create_time", "");
        editor.commit();
        editor = null;
        sp = null;
    }

    //region 获取第一次
    public static boolean getAppFirst(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        boolean b = sp.getBoolean("app_first", true);
        sp = null;
        return b;
    }

    public static void setAppFirst(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean("app_first", false);
        editor.commit();
        editor = null;
        sp = null;
    }


    public static boolean getAppFirst1(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        boolean b = sp.getBoolean("app_first1", true);
        sp = null;
        return b;
    }

    public static void setAppFirst1(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean("app_first1", false);
        editor.commit();
        editor = null;
        sp = null;
    }


    public static boolean getAppFirst2(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        boolean b = sp.getBoolean("app_first2", true);
        sp = null;
        return b;
    }

    public static void setAppFirst2(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean("app_first2", false);
        editor.commit();
        editor = null;
        sp = null;
    }

    //endregion
    //region userinfo
    public static void setUserId(Context context, String uid) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("info_uid", uid);
        editor.commit();
        editor = null;
        sp = null;
    }

    public static String getUserId(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        String uid = sp.getString("info_uid", GlobalConstant.ISLOGINUID);
        sp = null;
//        GlobalParams.uid = uid;
        return uid;
    }


    public static void setUserCid(Context context, String cid) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("info_cid", cid);
        editor.commit();
        editor = null;
        sp = null;
        GlobalParams.cid = cid;
    }

    public static String getUserCid(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        String cid = sp.getString("info_cid", "");
        sp = null;
        return cid;
    }

    //endregion
    //region WeiboAccess_Token
    public static void getUserWeiboAccess_Token(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        String weiboaccess_token = sp.getString("info_weiboaccess_token" + GlobalParams.uid, "");
        sp = null;
        GlobalParams.weiboaccess_token = weiboaccess_token;
    }

    public static void setUserWeiboAccess_Token(Context context, String weiboaccess_token) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("info_weiboaccess_token" + GlobalParams.uid, weiboaccess_token);
        editor.commit();
        editor = null;
        sp = null;
    }

    //首页引导条数据
    public static void setAppConfig_act(String string) {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("app_config_act", string);
        editor.commit();
        editor = null;
        sp = null;
    }

    public static Bean_act getAppConfig_act() {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        String string = sp.getString("app_config_act", "");
        sp = null;
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        try {
            Bean_act json = new Gson().fromJson(string, Bean_act.class);
            return json;
        } catch (Exception e) {
            return null;
        }
    }

    // TODO
    public static boolean getAppTime() {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("config",
                Context.MODE_PRIVATE);
        String time = sp.getString("app_time", "");
        sp = null;
        if (TextUtils.isEmpty(time)) {
            return false;
        }
        return time.equals(getToday());
    }

    public static void setAppTime(String time) {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("app_time", time);
        editor.commit();
        editor = null;
        sp = null;
    }

    //endregion
    public static String getToday() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(System
                .currentTimeMillis()));
    }

    // TODO
    public static boolean getAppTimeFirst() {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("config",
                Context.MODE_PRIVATE);
        boolean b = sp.getBoolean("app_time_first", false);
        sp = null;
        return b;
    }

    public static void setAppTimeFirst(boolean b) {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean("app_time_first", b);
        editor.commit();
        editor = null;
        sp = null;
    }

    public static String getAppTimeName() {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("config",
                Context.MODE_PRIVATE);
        String name = sp.getString("app_time_name", "ad");
        sp = null;
        return name;
    }

    public static void setAppTimeName(String name) {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("app_time_name", name);
        editor.commit();
        editor = null;
        sp = null;
    }

    //region 保存随笔(话题)内容
    public static void setContent(Context context, String content) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("info_content" + GlobalParams.uid, content);
        editor.commit();
        editor = null;
        sp = null;
    }


    public static String getContent(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        String content = sp.getString("info_content" + GlobalParams.uid, "");
        sp = null;
        return content;
    }

    //region 设置友盟绑定状态
    public static void setUMeng(Context context, UMengBean bean) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("umeng_wx", bean.wx);
        editor.putString("umeng_qq", bean.qq);
        editor.putString("umeng_weibo", bean.weibo);
        editor.putString("umeng_type", bean.type);
        editor.commit();
        editor = null;
        sp = null;
    }

    //获取友盟绑定状态
    public static void getUMeng(Context context) {
        UserInfoBean bean = new UserInfoBean();
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        GlobalParams.wx = sp.getString("umeng_wx", "");
        GlobalParams.qq = sp.getString("umeng_qq", "");
        GlobalParams.weibo = sp.getString("umeng_weibo", "");
        GlobalParams.type = sp.getString("umeng_type", "");
        sp = null;
    }

    //region 搜索记录
    public static void setSouSuo(Context context, List<String> list, boolean isClear) {
        SharedPreferences sp = context.getSharedPreferences("sousuo",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        if (isClear) {
            editor.clear().commit();
        } else {
            editor.putInt("SouSuo_size", list.size());
            for (int i = 0; i < list.size(); i++) {
                editor.remove("SouSuo_" + i);
                editor.putString("SouSuo_" + i, list.get(i));
            }
            editor.commit();
        }
    }

    public static List getSouSuo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("sousuo",
                Context.MODE_PRIVATE);
        int size = sp.getInt("SouSuo_size", 0);
        List<String> list = new ArrayList<>();
        if (size > 0) {
            for (int j = 0; j < size; j++) {
                String s = sp.getString("SouSuo_" + j, "");
                list.add(s);
            }
        }
        return list;
    }

    //region 勋章版本
    public static void set_xunzhang(Context context, String xunzhangbanben) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("xunzhangbanben", xunzhangbanben);
        editor.commit();
        editor = null;
        sp = null;
    }

    public static String get_xunzhang(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        String name = sp.getString("xunzhangbanben", "0");
        sp = null;
        return name;
    }
    //endregion

    //引导评分5分钟计时
    public static void setTime(Context context, long time) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putLong("Time", time);
        editor.commit();
        editor = null;
        sp = null;
    }

    public static long getTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        long time = sp.getLong("Time", 0);
        sp = null;
        return time;
    }

    //region 是否使用网络播放
    public static void setBoFang(boolean isBoFang) {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean("BoFang", isBoFang);
        editor.commit();
        editor = null;
        sp = null;
    }

    public static boolean getBoFang() {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("config",
                Context.MODE_PRIVATE);
        boolean time = sp.getBoolean("BoFang", false);
        sp = null;
        return time;
    }
    //endregion

    //region 是否使用网络下载
    public static void setXiaZai(boolean isXiaZai) {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean("XiaZai", isXiaZai);
        editor.commit();
        editor = null;
        sp = null;
    }

    public static boolean getXiaZai() {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("config",
                Context.MODE_PRIVATE);
        boolean time = sp.getBoolean("XiaZai", false);
        sp = null;
        return time;
    }
    //endregion

    /**
     * 清除本地推送数据库(一个月之后删除)
     */
    public static void setcom(Context context, boolean isXiaZai) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean("com", isXiaZai);
        editor.commit();
        editor = null;
        sp = null;
    }

    public static boolean getcom(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        boolean time = sp.getBoolean("com", true);
        sp = null;
        return time;
    }

    /**
     * 找回数据 , 1.1.0版本 ,两个版本后删除
     */
    public static void setThirdId(Context context, String third_id) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("third_id", third_id);
        editor.commit();
        editor = null;
        sp = null;
    }

    public static String getThirdId(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        String third_id = sp.getString("third_id", "");
        sp = null;
        return third_id;
    }

}
