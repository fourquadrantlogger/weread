package com.fengwo.reading.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.fengwo.reading.application.MyApplication;
import com.fengwo.reading.myinterface.BaseNameValuePairs;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import org.apache.http.NameValuePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 直接将http请求的参数加到参数集合中
 */
public class HttpParamsUtil {

    /**
     * http工具
     */
    public static HttpUtils http = new HttpUtils();

    public static RequestParams PARAMS = new RequestParams();// 联网请求参数对象
    public static List<NameValuePair> NAMEVALUEPAIRS = new ArrayList<NameValuePair>();// 封装具体参数的集合

    /**
     * 加密添加排序后的参数
     */
    private static void put(Map<String, String> map) {
        Collection<String> keyset = map.keySet();
        List<String> list = new ArrayList<String>(keyset);
        for (int i = 0; i < list.size(); i++) {
            NAMEVALUEPAIRS.add(new BaseNameValuePairs(list.get(i), map.get(list
                    .get(i))));
        }
        PARAMS = new RequestParams();
        PARAMS.addBodyParameter(NAMEVALUEPAIRS);// 将参数添加到集合
    }

    public static String encoding(String data) {
        String result = "";
        try {
            result = URLEncoder.encode(data, "UTF-8");
        } catch (Exception e) {
            Log.e("reading", "decode error!");
        }
        return result;
    }

    /**
     * 加密 x-www-form encode
     */
    private static void putEncodeUrl(Map<String, String> map) {
        Collection<String> keyset = map.keySet();
        List<String> list = new ArrayList<String>(keyset);
        for (int i = 0; i < list.size(); i++) {
            NAMEVALUEPAIRS.add(
                    new BaseNameValuePairs(
                            list.get(i), encoding(map.get(list.get(i))
                    )
                    )
            );
        }
        PARAMS = new RequestParams();
        PARAMS.addBodyParameter(NAMEVALUEPAIRS);// 将参数添加到集合
    }
    // /**
    // * 封装用于请求其他加密请求的方法,使用前清空
    // *
    // * @param map
    // * //装有参数的map集合
    // * @param uid
    // * //uid 参数对应的值 无可以为null
    // * @param Url
    // * //请求的地址
    // * @param requestCallBack
    // * 回调
    // * @param MD5
    // * 是否需要加密请求
    // */
    // public static void sendata(Map<String, String> map, String uid, String
    // Url,
    // RequestCallBack<String> requestCallBack, boolean MD5) {
    // NAMEVALUEPAIRS.clear();
    // if (map == null) {
    // return;
    // }
    // map.put("version", GlobalConstant.SERVERVERSION);
    // if (MD5) {
    // put(MD5Util.getNewMap(uid, Url, map));
    // } else {
    // put(map);
    // }
    // http.configCurrentHttpCacheExpiry(1000 * 60);// 设置超时时间
    // http.send(HttpRequest.HttpMethod.POST, Url, PARAMS, requestCallBack);
    // }
    //
    // /**
    // * 封装用于请求其他加密请求的方法,使用前清空
    // *
    // * @param map
    // * //装有参数的map集合
    // * @param uid
    // * //uid 参数对应的值,无可以为null
    // * @param Url
    // * //请求的地址
    // * @param requestCallBack
    // * 回调
    // * @param MD5
    // * 是否需要加密请求
    // * @param param
    // * 是否有文件上传,有则输入文件对应的参数名,无则null
    // * @param file
    // * 要上传的文件,无则null
    // * @param MIMEtype
    // * 上传文件类型
    // */
    // public static void sendata(Map<String, String> map, String uid, String
    // Url,
    // RequestCallBack<String> requestCallBack, boolean MD5, String param,
    // File file, String MIMEtype) {
    // NAMEVALUEPAIRS.clear();
    // if (map == null || requestCallBack == null) {
    // return;
    // }
    // map.put("version", GlobalConstant.SERVERVERSION);
    // if (MD5) {
    // put(MD5Util.getNewMap(uid, Url, map));
    // } else {
    // put(map);
    // }
    // if (!TextUtils.isEmpty(param)) {
    // if (TextUtils.isEmpty(MIMEtype)) {
    // PARAMS.addBodyParameter(param, file);
    // } else {
    // PARAMS.addBodyParameter(param, file, MIMEtype);
    // }
    // }
    // http.send(HttpRequest.HttpMethod.POST, Url, PARAMS, requestCallBack);
    // }

    /**
     * 加载图片有缓存
     *
     * @param imageView  放图片的view
     * @param picUrl     图片地址
     * @param context    上下文
     * @param needchange 是否转换成圆形
     * @param id         加载失败后显示的默认图
     */
    public static void loadingPic(ImageView imageView, final String picUrl,
                                  final Context context, final boolean needchange, final int id) {
        if (imageView == null || context == null) {
            return;
        }
        BitmapUtils bitmapUtils = new BitmapUtils(context);
        bitmapUtils.display(imageView, picUrl,
                new BitmapLoadCallBack<ImageView>() {
                    @Override
                    public void onLoadCompleted(ImageView imageView, String s,
                                                Bitmap bitmap,
                                                BitmapDisplayConfig bitmapDisplayConfig,
                                                BitmapLoadFrom bitmapLoadFrom) {
                        if (needchange) {
                            imageView.setImageBitmap(PicUtil
                                    .getRoundedCornerBitmap(bitmap, 2));
                        } else {
                            imageView.setImageBitmap(bitmap);
                        }
                        try {
                            File file = new File(Environment
                                    .getExternalStorageDirectory() + "/Reading");
                            if (!file.exists()) {
                                file.mkdir();
                            }
                            bitmap.compress(
                                    Bitmap.CompressFormat.PNG,
                                    100,
                                    new FileOutputStream(new File(file, MD5Util
                                            .encodeBy32BitMD5(picUrl) + ".png")));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadFailed(ImageView imageView, String s, Drawable drawable) {

                    }
                });
    }



    /**
     * get请求
     */
    public static void sendDataByGet(String url,
                                     RequestCallBack<String> requestCallBack) {
        http.configCurrentHttpCacheExpiry(1000 * 60);
        http.send(HttpRequest.HttpMethod.GET, url, requestCallBack);
    }

    /**
     * post请求
     */
    public static void getWeiBoPost(Map<String, String> map, String Url,
                                      RequestCallBack<String> requestCallBack) {
        put(map);
        http.configCurrentHttpCacheExpiry(1000 * 60);
        http.send(HttpRequest.HttpMethod.POST, Url, PARAMS, requestCallBack);
    }



    // TODO
    /**
     * 封装用于普通的数据请求，上传图片，上传多张图片（待测）
     *
     * @param map             ————装有参数的map集合
     * @param uid             ————uid 参数对应的值,无可以为null
     * @param Url             ————请求的地址
     * @param requestCallBack ————回调
     * @param MD5             ————是否需要加密请求
     * @param filemap         ————存放文件的Map集合 key为参数名,值为文件,无则null
     */
    public static void sendData(Map<String, String> map, String uid,
                                String Url, RequestCallBack<String> requestCallBack, boolean MD5,
                                Map<String, File> filemap) {
        NAMEVALUEPAIRS.clear();

        if (map == null || requestCallBack == null) {
            LogUtils.e("sendData=====请求时传的参数有误");
            return;
        }
        map.put("soft", VersionUtils.getVersion(MyApplication.getContext()));
        if (MD5) {
            put(MD5Util.getNewMap(uid, Url, map));
        } else {
            put(map);
        }
        if (filemap != null) {
            putFile(filemap);
            http.configCurrentHttpCacheExpiry(1000 * 120);
            http.configTimeout(1000 * 120);
        } else {
            http.configCurrentHttpCacheExpiry(1000 * 60);
            http.configTimeout(1000 * 60);
        }
        Gson gson = new GsonBuilder().create();
        MLog.v("reading", Url+"(post):" + gson.toJson(map));

        http.send(HttpRequest.HttpMethod.POST, Url, PARAMS, requestCallBack);
    }

    /**
     * 添加文件类型的参数
     */
    private static void putFile(Map<String, File> map) {
        if (map == null || map.size() == 0) {
            return;
        }
        Collection<String> keyset = map.keySet();
        List<String> list = new ArrayList<String>(keyset);
        for (int i = 0; i < list.size(); i++) {
            PARAMS.addBodyParameter(list.get(i), map.get(list.get(i)));
            // PARAMS.addBodyParameter("img[" + i + "]", lisFiles.get(i))
        }
    }


}
