package com.fengwo.reading.application;

import android.app.Application;
import android.content.Context;

import com.igexin.sdk.PushManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MyApplication extends Application {

    private static ImageLoader imageLoader;

    public static Context getContext() {
        return context;
    }

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        PushManager.getInstance().initialize(this.getApplicationContext());

        // 创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this.getApplicationContext());
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(configuration);
    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }

    public static void clearMemoryCache() {
        // 清除内存缓存
        imageLoader.clearMemoryCache();
    }

}
