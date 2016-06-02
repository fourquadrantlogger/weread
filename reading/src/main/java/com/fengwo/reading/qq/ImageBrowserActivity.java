package com.fengwo.reading.qq;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengwo.lib.PhotoView;
import com.fengwo.reading.R;
import com.fengwo.reading.activity.BaseActivity;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.CustomToast;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *    图片浏览
 */
public class ImageBrowserActivity extends BaseActivity implements View.OnClickListener {
    public static List<String> mList;
    public static int position = 0;

    private ViewPager mPager;

    private TextView textView_ImageBrowser, textView_ImageBrowser_download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_imagebrowser);

        textView_ImageBrowser = (TextView) findViewById(R.id.textView_ImageBrowser);
        textView_ImageBrowser.setText((position % mList.size()) + 1 + "/" + mList.size());

        textView_ImageBrowser_download = (TextView) findViewById(R.id.textView_ImageBrowser_download);

        textView_ImageBrowser_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //图片下载
                File sdCardDir = Environment.getExternalStorageDirectory();//获取SDCard目录

                final String mediaUrl = mList.get(position);
                final String mediaFolder = GlobalParams.FolderPath_Photo;
                SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
                final String mediaName =dateFormat.format(new Date())+ ".jpg";
                final String path = sdCardDir  + "/YOUSHU/PHOTO";
//                sdCardDir + "/" + mediaFolder

                HttpUtils http = new HttpUtils();
                HttpHandler handler = http.download(mediaUrl, path +"/"+ mediaName,
                        true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                        true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                        new RequestCallBack<File>() {

                            @Override
                            public void onStart() {
                                CustomToast.showToast(ImageBrowserActivity.this, "开始下载");
                            }

                            @Override
                            public void onLoading(long total, long current, boolean isUploading) {

                            }

                            @Override
                            public void onSuccess(ResponseInfo<File> responseInfo) {
                                CustomToast.showToast(ImageBrowserActivity.this, "下载成功");
                                // 其次把文件插入到系统图库
                                try {
                                    MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),
                                            path +"/"+ mediaName, mediaName, null);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                // 最后通知图库更新
                                getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));

                            }


                            @Override
                            public void onFailure(HttpException error, String msg) {
                                CustomToast.showToast(ImageBrowserActivity.this, "下载失败");
                            }
                        });

            }
        });
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ImageBrowserActivity.position = position;
                textView_ImageBrowser.setText((position % mList.size()) + 1 + "/" + mList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        mPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                final PhotoView photoView = new PhotoView(ImageBrowserActivity.this);
                photoView.enable();
                BitmapUtils bitmapUtils = new BitmapUtils(ImageBrowserActivity.this);

                bitmapUtils.display(photoView, mList.get(position),
                        new BitmapLoadCallBack<ImageView>() {

                            @Override
                            public void onLoadCompleted(ImageView imageView, String s,
                                                        Bitmap bitmap,
                                                        BitmapDisplayConfig bitmapDisplayConfig,
                                                        BitmapLoadFrom bitmapLoadFrom) {

                                photoView.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onLoadFailed(ImageView imageView, String s,
                                                     Drawable drawable) {
                            }

                            @Override
                            public void onLoading(ImageView container, String uri,
                                                  BitmapDisplayConfig config, long total, long current) {
                                super.onLoading(container, uri, config, total, current);

                            }

                            @Override
                            public void onLoadStarted(ImageView container, String uri,
                                                      BitmapDisplayConfig config) {
                                super.onLoadStarted(container, uri, config);
                            }

                            public void onPreLoad(ImageView container, String uri,
                                                  BitmapDisplayConfig config) {
                            }

                            ;

                        });

                container.addView(photoView);
                return photoView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        mPager.setCurrentItem(position);
    }


    @Override
    public void onClick(View v) {
        Log.v("reading", "" + v.getId());
    }


}
