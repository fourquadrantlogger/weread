package com.fengwo.reading.qq;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.BaseActivity;
import com.fengwo.reading.main.group.PublishFeelingsFragment;
import com.fengwo.reading.main.group.widget.FlowLayout;
import com.fengwo.reading.main.my.SuggestFragment;
import com.fengwo.reading.utils.MLog;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * 相册
 *
 * @author Luo Sheng
 * @date 2016-5-17
 */
public class QQAlbumActivity extends BaseActivity implements OnClickListener, ListImageDirPopupWindow.OnImageDirSelected {

    private ImageView iv_title_left;
    private TextView tv_title_mid;
    private static TextView tv_title_right;
    private ProgressDialog mProgressDialog;

    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;
    /**
     * 所有的图片
     */
    private List<String> mImgs;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<>();
    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFloder> mImageFloders = new ArrayList<>();

    private GridView girdView;
    private QQAlbumAdapter adapter;

    private ListImageDirPopupWindow mListImageDirPopupWindow;
    private RelativeLayout mBottomLy;
    private TextView mChooseDir;
    private TextView mImageCount;
    private int mScreenHeight;
    private int totalCount = 0;

    public static List<String> mSelectedImage = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qqalbum);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;

        findViewById();
        try {
            setNum();
            getImages();
            initEvent();
        } catch (Exception e) {
            // TODO: handle exception
        }

        handler2.sendEmptyMessageDelayed(0, 300);
    }

    private void findViewById() {
        iv_title_left = (ImageView) findViewById(R.id.iv_return);
        tv_title_mid = (TextView) findViewById(R.id.tv_title_mid);
        tv_title_right = (TextView) findViewById(R.id.tv_title_right);

        girdView = (GridView) findViewById(R.id.gridView);
        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
        mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
        mImageCount = (TextView) findViewById(R.id.id_total_count);

        iv_title_left.setVisibility(View.VISIBLE);
        iv_title_left.setImageResource(R.drawable.title_jiantou_left_white);
        tv_title_mid.setText("相册");
        tv_title_right.setVisibility(View.VISIBLE);
        tv_title_right.setText("确定");

        iv_title_left.setOnClickListener(this);
        tv_title_right.setOnClickListener(this);
        iv_title_left.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                finish();
                break;
            case R.id.tv_title_right:
                if (QQAlbumAdapter.mSelectedImage.size() == 0) {
                    Toast.makeText(QQAlbumActivity.this, "至少选择一张图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                mSelectedImage.clear();
                for (int i = 0; i < QQAlbumAdapter.mSelectedImage.size(); i++) {
                    mSelectedImage.add(QQAlbumAdapter.mSelectedImage.get(i));
                }
                finish();
                PublishFeelingsFragment.isRefresh = true;
                SuggestFragment.isRefresh = true;
                break;

            default:
                break;
        }
    }

    public static void setNum() {
        if (QQAlbumAdapter.mSelectedImage.size() == 0) {
            tv_title_right.setText("确定");
        } else {
            tv_title_right.setText("确定(" + QQAlbumAdapter.mSelectedImage.size() + ")");
        }
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示进度条
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
        mProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                String firstImage = null;
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = QQAlbumActivity.this
                        .getContentResolver();

                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);
                MLog.v("TAG", mCursor.getCount() + "");
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    // 拿到第一张图片的路径
                    if (firstImage == null)
                        firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    if (parentFile.list() == null) {
                        continue;
                    }
                    String dirPath = parentFile.getAbsolutePath();
                    ImageFloder imageFloder = null;
                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        imageFloder = new ImageFloder();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }

                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    }).length;
                    totalCount += picSize;

                    imageFloder.setCount(picSize);
                    mImageFloders.add(imageFloder);

                    if (picSize > mPicsSize) {
                        mPicsSize = picSize;
                        mImgDir = parentFile;
                    }
                }
                mCursor.close();
                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;
                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110);
            }
        }).start();
    }

    private void initEvent() {
        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        mBottomLy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListImageDirPopupWindow
                        .setAnimationStyle(R.style.anim_popup_dir);
//                mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);
                mListImageDirPopupWindow.showAsDropDown(mBottomLy);
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = .7f;
                getWindow().setAttributes(lp);
            }
        });
    }

    @Override
    public void selected(ImageFloder floder) {
        mImgDir = new File(floder.getDir());
        mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        }));
//        mImgs = Arrays.asList(mImgDir.list());
//把排序翻转，按时间最新的排序
        Collections.reverse(mImgs);
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        adapter = new QQAlbumAdapter(QQAlbumActivity.this, mImgs,
                R.layout.item_qqalbum, mImgDir.getAbsolutePath());
        girdView.setAdapter(adapter);
        // mAdapter.notifyDataSetChanged();
        mImageCount.setText(floder.getCount() + "张");
        mChooseDir.setText(floder.getName());
        mListImageDirPopupWindow.dismiss();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mProgressDialog.dismiss();
            // 为View绑定数据
            if (mImgDir == null) {
                Toast.makeText(getApplicationContext(), "擦，一张图片没扫描到",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            mImgs = Arrays.asList(mImgDir.list());
            Collections.reverse(mImgs);
            /**
             * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
             */
            adapter = new QQAlbumAdapter(QQAlbumActivity.this, mImgs,
                    R.layout.item_qqalbum, mImgDir.getAbsolutePath());
            girdView.setAdapter(adapter);
            mImageCount.setText(totalCount + "张");
            //TODO
            //QQAlbumDetailActivity.count = totalCount;

            // 初始化展示文件夹的popupWindw
            initListDirPopupWindw();
        }
    };

    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw() {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                FlowLayout.LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.list_qqalbum_list, null));

        mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    private Handler handler2 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            iv_title_left.setEnabled(true);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
