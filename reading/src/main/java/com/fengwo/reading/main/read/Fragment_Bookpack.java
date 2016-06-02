package com.fengwo.reading.main.read;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.UpgradeActivity;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.comment.EmojiActivity;
import com.fengwo.reading.common.CommonHandler;
import com.fengwo.reading.common.SelectSharePopupWindow;
import com.fengwo.reading.main.comment.BpInfoBean;
import com.fengwo.reading.main.comment.BpInfoJson;
import com.fengwo.reading.main.group.GroupBean;
import com.fengwo.reading.main.group.GroupJson;
import com.fengwo.reading.main.my.achieve.ExplainFragment;
import com.fengwo.reading.main.read.bookpackdetails.CommentDetailsFragment;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.player.Fragment_MediaPlayer;
import com.fengwo.reading.player.Play_Anmi;
import com.fengwo.reading.player.PlayerService;
import com.fengwo.reading.player.Playlist_Now;
import com.fengwo.reading.umeng.UMShare;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.MyDialog;
import com.fengwo.reading.utils.NetUtil;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lxq 拆书包详情
 */
public class Fragment_Bookpack extends Fragment implements OnClickListener {

    private LinearLayout title_layout;
    private ImageView iv_title_left, iv_readbook_left, iv_title_right;
    private TextView tv_title_mid, tv_comment_header_2;

    private PullToRefreshListView pullToRefreshListView;
    private ListView newListView;

    //拆书包 顶部图片 及标题
    private TextView tv_bookpack_title;
    private ImageView iv_bookpack_image;
    private WebView wv_readbook_content;

    private boolean playFlag = false;

//    Handler handler1 = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            UMengUtils.onCountListener(getActivity(), "GD_02_02_02");
//            if (jsonData == null) {
//                MLog.v("reading", "出现错误,没有找到对应的音频文件");
//                return;
//            }
//            Fragment_WeRead.getInstance().refresh_playeranmi();
//            if (source != 4) {
//                Playlist_Now.setmusicID(jsonData);
//                MLog.v("reading", "播放列表");
//                //region 播放列表
//                if (Playlist_Now.media_now_IndexBean == null || !jsonData.media.equals(Playlist_Now.media_now_IndexBean.media)) {
//                    //播放时有无网络进行提示
//                    if (!NetUtil.checkNet(getActivity())) {
//                        //已下载的就播放本地音频
//                        if (new File(Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Media + Playlist_Now.media_now_IndexBean.book_title + "\n" + Playlist_Now.media_now_IndexBean.title + ".mp3").exists()) {
//                            serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
//                            serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
//                            // serviceIntent.setAction("MUSIC");
//                            //启动服务
//                            getActivity().startService(serviceIntent);
//                            Animation_MediaPlaying(true);
//                        } else {
//                            Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        if (NetUtil.isMobileConnection(getActivity())) {
//                            if (SPUtils.getBoFang()) {
//                                // 播放新的media
//                                // TODO: 16/4/18
//                                serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
//                                serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
//                                // serviceIntent.setAction("MUSIC");
//                                //启动服务
//                                getActivity().startService(serviceIntent);
//                                Animation_MediaPlaying(true);
//                            } else {
//                                MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "播放开关才能播放", "开启2G/3G/4G网络播放开关", new MyDialog.MyDialogInterfaceListener() {
//                                    @Override
//                                    public void callBack() {
//                                        SPUtils.setBoFang(true);
//                                        // 播放新的media
//                                        // TODO: 16/4/18
//                                        serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
//                                        serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
//                                        // serviceIntent.setAction("MUSIC");
//                                        //启动服务
//                                        getActivity().startService(serviceIntent);
//                                        Animation_MediaPlaying(true);
//
//                                    }
//                                });
//                                dialog.show();
//                            }
//
//                        } else if (NetUtil.isWifiConnection(getActivity())) {
//                            // 播放新的media
//                            // TODO: 16/4/18
//                            serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
//                            serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
//                            // serviceIntent.setAction("MUSIC");
//                            //启动服务
//                            getActivity().startService(serviceIntent);
//                            Animation_MediaPlaying(true);
//                        }
//                    }
//
//
//                } else {
//                    if (PlayerService.player == null) {
//                        //播放时有无网络进行提示
//                        if (!NetUtil.checkNet(getActivity())) {
//                            //已下载的就播放本地音频
//                            if (new File(Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Media + Playlist_Now.media_now_IndexBean.book_title + "\n" + Playlist_Now.media_now_IndexBean.title + ".mp3").exists()) {
//                                serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
//                                serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
//                                // serviceIntent.setAction("MUSIC");
//                                //启动服务
//                                getActivity().startService(serviceIntent);
//                                Animation_MediaPlaying(true);
//                            } else {
//                                Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            if (NetUtil.isMobileConnection(getActivity())) {
//                                if (SPUtils.getBoFang()) {
//                                    serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
//                                    serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
//                                    // serviceIntent.setAction("MUSIC");
//                                    //启动服务
//                                    getActivity().startService(serviceIntent);
//                                    Animation_MediaPlaying(true);
//                                } else {
//                                    MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "播放开关才能播放", "开启2G/3G/4G网络播放开关", new MyDialog.MyDialogInterfaceListener() {
//                                        @Override
//                                        public void callBack() {
//                                            SPUtils.setBoFang(true);
//                                            // 播放新的media
//                                            // TODO: 16/4/18
//                                            serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
//                                            serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
//                                            // serviceIntent.setAction("MUSIC");
//                                            //启动服务
//                                            getActivity().startService(serviceIntent);
//                                            Animation_MediaPlaying(true);
//
//                                        }
//                                    });
//                                    dialog.show();
//                                }
//
//                            } else if (NetUtil.isWifiConnection(getActivity())) {
//                                serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
//                                serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
//                                // serviceIntent.setAction("MUSIC");
//                                //启动服务
//                                getActivity().startService(serviceIntent);
//                                Animation_MediaPlaying(true);
//                            }
//                        }
//
//
//                    } else {
//                        if (PlayerService.player.isPlaying()) {
//                            Animation_MediaPlaying(true);
//                        } else {
//                            Animation_MediaPlaying(false);
//                            // media 暂停
//                        }
//                        //播放时有无网络进行提示
//                        if (!NetUtil.checkNet(getActivity())) {
//                            //已下载的就播放本地音频
//                            if (new File(Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Media + Playlist_Now.media_now_IndexBean.book_title + "\n" + Playlist_Now.media_now_IndexBean.title + ".mp3").exists()) {
//                                serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
//                                //启动服务
//                                getActivity().startService(serviceIntent);
//                            } else {
//                                Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            if (NetUtil.isMobileConnection(getActivity())) {
//                                if (SPUtils.getBoFang()) {
//                                    // media 继续播放
//                                    // media 暂停/继续播放
//                                    //都用 PlayerService.PAUSE
//                                    serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
//                                    //启动服务
//                                    getActivity().startService(serviceIntent);
//                                } else {
//                                    MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "播放开关才能播放", "开启2G/3G/4G网络播放开关", new MyDialog.MyDialogInterfaceListener() {
//                                        @Override
//                                        public void callBack() {
//                                            SPUtils.setBoFang(true);
//                                            // 播放新的media
//                                            // TODO: 16/4/18
//                                            serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
//                                            //启动服务
//                                            getActivity().startService(serviceIntent);
//
//                                        }
//                                    });
//                                    dialog.show();
//                                }
//
//                            } else if (NetUtil.isWifiConnection(getActivity())) {
//                                // media 继续播放
//                                // media 暂停/继续播放
//                                //都用 PlayerService.PAUSE
//                                serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
//                                //启动服务
//                                getActivity().startService(serviceIntent);
//                            }
//                        }
//
//                    }
//                }
//                //endregion
//            } else {
//                //region 播放单曲
//                if (PlayerService.media_now_IndexBean == null || PlayerService.player == null) {
//                    //播放时有无网络进行提示
//                    if (!NetUtil.checkNet(getActivity())) {
//                        //已下载的就播放本地音频
//                        if (new File(Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Media + Playlist_Now.media_now_IndexBean.book_title + "\n" + Playlist_Now.media_now_IndexBean.title + ".mp3").exists()) {
//                            serviceIntent.putExtra("singleBpInfoBean", jsonData);
//                            serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
//                            //启动服务
//                            getActivity().startService(serviceIntent);
//                            Animation_MediaPlaying(true);
//                        } else {
//                            Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        if (NetUtil.isMobileConnection(getActivity())) {
//                            if (SPUtils.getBoFang()) {
//                                // TODO: 16/4/18
//                                serviceIntent.putExtra("singleBpInfoBean", jsonData);
//                                serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
//                                //启动服务
//                                getActivity().startService(serviceIntent);
//                                Animation_MediaPlaying(true);
//                            } else {
//                                MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "播放开关才能播放", "开启2G/3G/4G网络播放开关", new MyDialog.MyDialogInterfaceListener() {
//                                    @Override
//                                    public void callBack() {
//                                        SPUtils.setBoFang(true);
//                                        // 播放新的media
//                                        // TODO: 16/4/18
//                                        serviceIntent.putExtra("singleBpInfoBean", jsonData);
//                                        serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
//                                        //启动服务
//                                        getActivity().startService(serviceIntent);
//                                        Animation_MediaPlaying(true);
//
//                                    }
//                                });
//                                dialog.show();
//                            }
//
//                        } else if (NetUtil.isWifiConnection(getActivity())) {
//                            // TODO: 16/4/18
//                            serviceIntent.putExtra("singleBpInfoBean", jsonData);
//                            serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
//                            //启动服务
//                            getActivity().startService(serviceIntent);
//                            Animation_MediaPlaying(true);
//                        }
//                    }
//
//
//                    return;
//                }
//
//                if (!jsonData.media.equals(PlayerService.media_now_IndexBean.media)) {
//                    //播放时有无网络进行提示
//                    if (!NetUtil.checkNet(getActivity())) {
//                        //已下载的就播放本地音频
//                        if (new File(Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Media + Playlist_Now.media_now_IndexBean.book_title + "\n" + Playlist_Now.media_now_IndexBean.title + ".mp3").exists()) {
//                            serviceIntent.putExtra("singleBpInfoBean", jsonData);
//                            serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
//                            //启动服务
//                            getActivity().startService(serviceIntent);
//                            Animation_MediaPlaying(true);
//                        } else {
//                            Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        if (NetUtil.isMobileConnection(getActivity())) {
//                            if (SPUtils.getBoFang()) {
//                                // 播放新的media
//                                // TODO: 16/4/18
//                                serviceIntent.putExtra("singleBpInfoBean", jsonData);
//                                serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
//                                //启动服务
//                                getActivity().startService(serviceIntent);
//                                Animation_MediaPlaying(true);
//                            } else {
//                                MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "播放开关才能播放", "开启2G/3G/4G网络播放开关", new MyDialog.MyDialogInterfaceListener() {
//                                    @Override
//                                    public void callBack() {
//                                        SPUtils.setBoFang(true);
//                                        // 播放新的media
//                                        // TODO: 16/4/18
//                                        serviceIntent.putExtra("singleBpInfoBean", jsonData);
//                                        serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
//                                        //启动服务
//                                        getActivity().startService(serviceIntent);
//                                        Animation_MediaPlaying(true);
//
//                                    }
//                                });
//                                dialog.show();
//                            }
//
//                        } else if (NetUtil.isWifiConnection(getActivity())) {
//                            // 播放新的media
//                            // TODO: 16/4/18
//                            serviceIntent.putExtra("singleBpInfoBean", jsonData);
//                            serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
//                            //启动服务
//                            getActivity().startService(serviceIntent);
//                            Animation_MediaPlaying(true);
//                        }
//                    }
//
//
//                } else {
//
//                    if (PlayerService.player.isPlaying()) {
//                        Animation_MediaPlaying(false);
//                    } else {
//                        Animation_MediaPlaying(true);
//                        // media 暂停
//                    }
//                    //播放时有无网络进行提示
//                    if (!NetUtil.checkNet(getActivity())) {
//                        if (new File(Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Media + Playlist_Now.media_now_IndexBean.book_title + "\n" + Playlist_Now.media_now_IndexBean.title + ".mp3").exists()) {
//                            serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
//                            //启动服务
//                            getActivity().startService(serviceIntent);
//                        } else {
//                            Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        if (NetUtil.isMobileConnection(getActivity())) {
//                            if (SPUtils.getBoFang()) {
//                                // media 继续播放
//                                // media 暂停/继续播放
//                                //都用 PlayerService.PAUSE
//                                serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
//                                //启动服务
//                                getActivity().startService(serviceIntent);
//                            } else {
//                                MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "播放开关才能播放", "开启2G/3G/4G网络播放开关", new MyDialog.MyDialogInterfaceListener() {
//                                    @Override
//                                    public void callBack() {
//                                        SPUtils.setBoFang(true);
//                                        // 播放新的media
//                                        // TODO: 16/4/18
//                                        serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
//                                        //启动服务
//                                        getActivity().startService(serviceIntent);
//
//                                    }
//                                });
//                                dialog.show();
//                            }
//
//                        } else if (NetUtil.isWifiConnection(getActivity())) {
//                            // media 继续播放
//                            // media 暂停/继续播放
//                            //都用 PlayerService.PAUSE
//                            serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
//                            //启动服务
//                            getActivity().startService(serviceIntent);
//                        }
//                    }
//
//
//                }
//                //endregion
//            }
//        }
//    };

    //拆书 签到
    private LinearLayout bookpack_qiandao_notify;
    private TextView tv_readbook_qiandao;
    private ImageView iv_readbook_qiandao, iv_bookpart_wx, iv_bookpart_pyq;
    //写评论
    private TextView tv_readbook_pl;
    private RelativeLayout rl_readbook_fx, rl_readbook_pl, rl_readbook_shoucang, rl_readbook_bottom, rl_readbook, rl_title_layout;
    private TextView tv_readbook_liuyan_num;
    private ImageView iv_readbook_shoucang;

    private List<GroupBean> newList;
    private BookPackCommentAdapter newAdapter;
    private LinearLayout ll_readbook_1, ll_readbook_2;
    private WebView wv_readbook_302;
    private FrameLayout ll_top;
    private CommonHandler handler;
    private SelectSharePopupWindow sharePopupWindow;

    public String id;// 拆书包id
    public String pb_id;// 共读id
    public int bookpackindex;//拆书包 index

    private int newLocation;
    private boolean is_loading;
    private String type = "";//hot热门，new最新
    private int newPage;
    private BpInfoBean jsonData;
    public String bookname = "";// 书名
    public boolean isDown = true; //滑动的第一个坐标

    public int source = 0;// 来源 1:ReadFragment 2:Fragment_BookInfoWithPacks 3:任务,4:今日推荐
    private View saveView = null;
    public boolean needSaveView = false;
    //region 音乐
    private FrameLayout rl_music_voice;
    private LinearLayout ll_readbook_all;
    private ImageView iv_music_voice, iv_music_xiazai, imageView_goto_fragment_music;
    private TextView tv_music_title;
    private TextView tv_music_from;
    private SeekBar sb_music_show;
    private ProgressBar pb_title_music_show;
    private TextView tv_music_media_time, tv_music_media_progress;

    private CurrentReceiver currentReceiver;

    private Intent serviceIntent;

    boolean Animationisplaying = false;

    void Animation_MediaPlaying(boolean playing) {
        if (playing) {
            iv_title_right.setImageResource(R.drawable.readbook_zanting);
            iv_music_voice.setImageResource(R.drawable.zaowandu_stop);
//            playFlag = true;
            if (Animationisplaying == false) {
                imageView_goto_fragment_music.startAnimation(Play_Anmi.getAnimation_gotofragment(getActivity()));
                Animationisplaying = true;
            } else {
            }
        } else {
            imageView_goto_fragment_music.clearAnimation();
            Animationisplaying = false;
            iv_music_voice.setImageResource(R.drawable.zaowandu_play);
//            playFlag = false;
            iv_title_right.setImageResource(R.drawable.readbook_bofang);
        }
    }

    public static Fragment_Bookpack fragment = new Fragment_Bookpack();

    public static Fragment_Bookpack getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_bookpack, container, false);
        handler = new CommonHandler(getActivity(), null);
        findViewById(view);
        // 上拉、下拉设定
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        newListView = pullToRefreshListView.getRefreshableView();
        // 添加头部
        addHeaderView();
        onClickListener();

        newList = new ArrayList<>();
        newAdapter = new BookPackCommentAdapter(fragment, newList, 2);
        newListView.setAdapter(newAdapter);

        newPage = 1;

        wv_readbook_content.setVisibility(View.GONE);
        ll_readbook_1.setVisibility(View.GONE);
        ll_readbook_2.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getDataComment();
            }
        }, 200);
        is_loading = false;
        jsonData = null;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                jsonData = NOsqlUtil.get_BpInfoBean(id);
                if (jsonData == null) {
                    getData_BpInfoBean();
                } else {
                    setData_BpInfoBean();
                }
            }
        }, 400);

        // 上拉监听函数
        pullToRefreshListView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // 执行刷新函数
                        if (is_loading) {
                            return;
                        }
                        is_loading = true;
                        newPage++;
                        getDataComment();
                    }
                });

        // 滑动事件监听
        newListView.setOnTouchListener(new View.OnTouchListener() {
            private float startY, offsetY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    //按住事件发生后执行代码的区域(无法监听,跟onClick事件冲突)
                    case MotionEvent.ACTION_DOWN: {
                        break;
                    }
                    //移动事件发生后执行代码的区域
                    case MotionEvent.ACTION_MOVE: {
                        if (isDown) {
                            //手指按下后的 Y 的坐标
                            startY = motionEvent.getY();
                            isDown = false;
                        }
                        //移动中 Y 的坐标
                        offsetY = motionEvent.getY();

                        Rect scrollBounds = new Rect();
                        newListView.getHitRect(scrollBounds);
                        if (ll_readbook_all.getLocalVisibleRect(scrollBounds)) {
                            //子控件至少有一个像素在可视范围内(ListView)
                            iv_readbook_left.setVisibility(View.GONE);
                            rl_readbook_bottom.setVisibility(View.VISIBLE);
                            title_layout.setVisibility(View.VISIBLE);
                            rl_title_layout.setVisibility(View.VISIBLE);
                        } else {//子控件完全不在可视范围内(ListView)
                            if (ll_top.getLocalVisibleRect(scrollBounds)) {
                                //子控件至少有一个像素在可视范围内(头部)
                                if (startY > offsetY) {
                                    //往上
                                    iv_readbook_left.setVisibility(View.VISIBLE);
                                    rl_readbook_bottom.setVisibility(View.GONE);
                                    title_layout.setVisibility(View.GONE);
//                            title_layout.getBackground().setAlpha(100);
                                } else if (startY < offsetY) {
                                    //向下
                                    iv_readbook_left.setVisibility(View.VISIBLE);
                                    rl_readbook_bottom.setVisibility(View.VISIBLE);
                                    title_layout.setVisibility(View.GONE);
//                            title_layout.getBackground().setAlpha(0);//透明度0~255透明度值 ，值越小越透明
                                }
                            } else {//子控件完全不在可视范围内(头部)
                                if (startY > offsetY) {
                                    //往上
                                    iv_readbook_left.setVisibility(View.GONE);
                                    rl_readbook_bottom.setVisibility(View.GONE);
                                    title_layout.setVisibility(View.VISIBLE);
                                    rl_title_layout.setVisibility(View.GONE);
                                    pb_title_music_show.setVisibility(View.VISIBLE);
//                            title_layout.getBackground().setAlpha(100);
                                } else if (startY < offsetY) {
                                    //向下
                                    iv_readbook_left.setVisibility(View.VISIBLE);
                                    rl_readbook_bottom.setVisibility(View.VISIBLE);
                                    title_layout.setVisibility(View.VISIBLE);
                                    rl_title_layout.setVisibility(View.VISIBLE);
//                            title_layout.getBackground().setAlpha(0);//透明度0~255透明度值 ，值越小越透明
                                }
                            }
                        }
                        break;
                    }
                    //松开事件发生后执行代码的区域
                    case MotionEvent.ACTION_UP: {
                        isDown = true;
                        break;
                    }

                    default:
                        break;
                }
                return false;
            }
        });

        newListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    // 当状态发生改变时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 屏幕停止滚动时
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滚动时
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:// 惯性滑动时
                        break;
                }
            }

            /**
             * firstVisibleItem：当前能看见的第一个列表项ID（从0开始）
             * visibleItemCount：当前能看见的列表项个数（小半个也算） totalItemCount：列表项共数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    // 滚动到顶部
                } else {
                }
            }
        });

        sharePopupWindow = new SelectSharePopupWindow(getActivity(),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        FragmentActivity activity = getActivity();
                        if (activity == null) {
                            return;
                        }

                        sharePopupWindow.imageUrl = jsonData.top_img;
                        sharePopupWindow.h5Url = GlobalConstant.ServerDomain + "share/pack/" + jsonData.id;
                        sharePopupWindow.title = "《" + jsonData.book_title + "》" + tv_music_title.getText().toString();
                        int num = 0;
                        switch (v.getId()) {
                            case R.id.ll_popupwindow_wx:
                                num = 1;
                                sharePopupWindow.content = jsonData.title;
                                break;
                            case R.id.ll_popupwindow_pyq:
                                num = 2;
                                sharePopupWindow.content = jsonData.title;
                                break;
                            case R.id.ll_popupwindow_qq:
                                num = 3;
                                sharePopupWindow.content = jsonData.title;
                                break;
                            case R.id.ll_popupwindow_wb:
                                num = 4;
                                sharePopupWindow.content = "推荐有书共读" + tv_music_title.getText().toString() + "——《" + jsonData.title + "》，来自@有书共读" + sharePopupWindow.h5Url;
                                break;
                            default:
                                break;
                        }
                        UMengUtils.onCountListener(getActivity(), "GD_02_02_06");
                        UMShare.setUMeng(activity, num, sharePopupWindow.title, sharePopupWindow.content, sharePopupWindow.imageUrl, sharePopupWindow.h5Url, jsonData.id, "bp");
                        if ("1".equals(UMShare.getLevel())) {
                            startActivity(new Intent(getActivity(), UpgradeActivity.class));
                        }
                        sharePopupWindow.dismiss();
                    }
                });

        newListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                newLocation = position - 2;
                FragmentTransaction transaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right,
                        R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next,
                        CommentDetailsFragment.getInstance());
                transaction.commit();
                CommentDetailsFragment.getInstance().source = 2;
                CommentDetailsFragment.getInstance().groupPosition = newLocation;
                CommentDetailsFragment.getInstance().bpc_id = newList.get(newLocation).id;
                CommentDetailsFragment.getInstance().bookname = bookname;
                CommentDetailsFragment.getInstance().needSaveView = false;
            }
        });

        serviceIntent = new Intent(getActivity(), PlayerService.class);
        sb_music_show.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            // 手动调整播放进度
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (jsonData == null) return;
                    switch (source) {
                        case 1:
                        case 2:
                        case 3:
                            if (Playlist_Now.media_now_IndexBean != null && jsonData.media.equals(Playlist_Now.media_now_IndexBean.media))
                                break;
                            else
                                return;
                        case 4:
                            if (PlayerService.media_now_IndexBean != null && jsonData.media.equals(PlayerService.media_now_IndexBean.media))
                                break;
                            else
                                return;
                    }
                    Intent intent = new Intent();
                    intent.setAction("com.fengwo.reading.PROGRESS_ACTION");
                    if (PlayerService.player == null) return;
                    int musicMax = PlayerService.player.getDuration();
                    int seekBarMax = seekBar.getMax();
                    intent.putExtra("PROGRESS", musicMax * progress / seekBarMax);

                    getActivity().sendBroadcast(intent);

                    Log.v("reading", "goto progress:" + progress);
                }
            }
        });
        Animation_MediaPlaying(false);
        if (jsonData != null && Playlist_Now.media_now_IndexBean != null && jsonData.media.equals(Playlist_Now.media_now_IndexBean.media) && PlayerService.player != null && PlayerService.player.isPlaying()) {
            Animation_MediaPlaying(true);
        }

        return view;
    }

    private void findViewById(View view) {
        rl_readbook = (RelativeLayout) view.findViewById(R.id.rl_readbook);

        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_bookpack_listview);

        ll_readbook_1 = (LinearLayout) view.findViewById(R.id.ll_readbook_1);
        ll_readbook_2 = (LinearLayout) view.findViewById(R.id.ll_readbook_2);
        wv_readbook_302 = (WebView) view.findViewById(R.id.wv_readbook_302);

        rl_readbook_bottom = (RelativeLayout) view.findViewById(R.id.rl_readbook_bottom);
        tv_readbook_pl = (TextView) view.findViewById(R.id.tv_readbook_pl);
        rl_readbook_pl = (RelativeLayout) view.findViewById(R.id.rl_readbook_pl);
        tv_readbook_liuyan_num = (TextView) view.findViewById(R.id.tv_readbook_liuyan_num);
        rl_readbook_shoucang = (RelativeLayout) view.findViewById(R.id.rl_readbook_shoucang);
        iv_readbook_shoucang = (ImageView) view.findViewById(R.id.iv_readbook_shoucang);
        rl_readbook_fx = (RelativeLayout) view.findViewById(R.id.rl_readbook_fx);

        title_layout = (LinearLayout) view.findViewById(R.id.title_layout);
        rl_title_layout = (RelativeLayout) view.findViewById(R.id.rl_title_layout);
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);
        iv_title_right = (ImageView) view.findViewById(R.id.iv_title_right);
        pb_title_music_show = (ProgressBar) view.findViewById(R.id.pb_title_music_show);
        iv_readbook_left = (ImageView) view.findViewById(R.id.iv_readbook_left);
    }

    private void addHeaderView() {
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.part_bookpack_maincontent, null);

        ll_top = (FrameLayout) view.findViewById(R.id.ll_top);

        iv_bookpack_image = (ImageView) view.findViewById(R.id.iv_bookpart_image);
        tv_bookpack_title = (TextView) view.findViewById(R.id.tv_bookpart_title);

        tv_comment_header_2 = (TextView) view.findViewById(R.id.tv_comment_header_2);
        tv_comment_header_2.setVisibility(View.GONE);

        wv_readbook_content = (WebView) view.findViewById(R.id.wv_readbook_content);

        iv_bookpart_wx = (ImageView) view.findViewById(R.id.iv_bookpart_wx);
        iv_bookpart_pyq = (ImageView) view.findViewById(R.id.iv_bookpart_pyq);

        bookpack_qiandao_notify = (LinearLayout) view.findViewById(R.id.bookpack_qiandao_notify);

        tv_readbook_qiandao = (TextView) view.findViewById(R.id.tv_readbook_qiandao);
        iv_readbook_qiandao = (ImageView) view.findViewById(R.id.iv_readbook_qiandao);

        ll_readbook_all = (LinearLayout) view.findViewById(R.id.ll_readbook_all);

        rl_music_voice = (FrameLayout) view.findViewById(R.id.rl_music_voice);
        iv_music_voice = (ImageView) view.findViewById(R.id.iv_music_voice);
        imageView_goto_fragment_music = (ImageView) view.findViewById(R.id.imageView_goto_fragment_music);
        iv_music_xiazai = (ImageView) view.findViewById(R.id.iv_music_xiazai);

        tv_music_title = (TextView) view.findViewById(R.id.tv_music_title);
        tv_music_from = (TextView) view.findViewById(R.id.tv_music_from);
        sb_music_show = (SeekBar) view.findViewById(R.id.sb_music_show);
        tv_music_media_time = (TextView) view.findViewById(R.id.tv_music_media_time);
        tv_music_media_progress = (TextView) view.findViewById(R.id.tv_music_media_progress);

        // 添加头部
        newListView.addHeaderView(view);
    }

    private void onClickListener() {
        tv_title_mid.setVisibility(View.VISIBLE);
        title_layout.setVisibility(View.GONE);
        iv_title_right.setVisibility(View.VISIBLE);
        pb_title_music_show.setVisibility(View.VISIBLE);

        imageView_goto_fragment_music.setOnClickListener(this);
        iv_title_left.setOnClickListener(this);
        iv_title_right.setOnClickListener(this);

        iv_readbook_left.setOnClickListener(this);
        tv_readbook_qiandao.setOnClickListener(this);
        tv_readbook_pl.setOnClickListener(this);

        rl_readbook_fx.setOnClickListener(this);
        rl_readbook_pl.setOnClickListener(this);
        rl_readbook_shoucang.setOnClickListener(this);

        iv_bookpart_wx.setOnClickListener(this);
        iv_bookpart_pyq.setOnClickListener(this);

        // 音乐播放部分
        rl_music_voice.setOnClickListener(this);
        iv_music_voice.setOnClickListener(this);
        tv_music_title.setOnClickListener(this);
        tv_music_from.setOnClickListener(this);
        iv_music_xiazai.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Context context = getActivity();
        MLog.v("reading", "Id:" + v.getId());
        if (context == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.imageView_goto_fragment_music:
                //region 挑转音频fragment
                FragmentTransaction transaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right,
                        R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                UMengUtils.onCountListener(getActivity(), "GD_02_02_05");
                if (Playlist_Now.Medialist().size() == 0) {
                    return;
                }
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next, Fragment_MediaPlayer.getInstance());
                transaction.commit();
                break;
            case R.id.iv_readbook_left:
            case R.id.iv_return:
                UMengUtils.onCountListener(getActivity(), "GD_02_02_01");
                switch (source) {
                    case 4:
                    case 1:
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                        break;
                    case 2:
                    case 3:
                        getActivity().getSupportFragmentManager().popBackStack();
                        break;
                    default:
                        break;
                }
                break;
            case R.id.tv_readbook_qiandao:
                getDataCheckIn(true);
                //签到成功,签到动画
                if (!jsonData.is_qian.equals("1")) {
                    MLog.v("reading", " //签到成功,签到动画");
                }
                UMengUtils.onCountListener(getActivity(), "GD_02_02_07");
                break;
            case R.id.rl_readbook_fx:
                sharePopupWindow.showAtLocation(getActivity()
                        .findViewById(R.id.rl_readbook), Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.tv_readbook_pl:
                //写评论
                Intent intent2 = new Intent(getActivity(), EmojiActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putInt("source", 1);
                bundle2.putInt("comment_type", 0);
                bundle2.putString("id", id);
                bundle2.putString("name", "");
                intent2.putExtras(bundle2);
                getActivity().startActivity(intent2);
                getActivity().overridePendingTransition(R.anim.push_bottom_in,
                        R.anim.push_bottom_out);
                UMengUtils.onCountListener(getActivity(), "GD_02_02_08");
                break;
            case R.id.rl_readbook_pl:
                //跳转 评论区
                newListView.setSelected(true);
                if (android.os.Build.VERSION.SDK_INT >= 8) {
                    newListView.smoothScrollToPosition(2);
                } else {
                    newListView.setSelection(2);
                }
                UMengUtils.onCountListener(getActivity(), "GD_02_02_09");
                break;
            case R.id.rl_readbook_shoucang:
                getData_shoucang();
                UMengUtils.onCountListener(getActivity(), "GD_02_02_10");
                break;
            case R.id.iv_music_xiazai:
                //音频下载
                UMengUtils.onCountListener(getActivity(), "GD_02_02_03");
                if (jsonData.Exist()) {
                    CustomToast.showToast(getActivity(), R.string.音频文件已经下载);
                } else {
                    if (NetUtil.isWifiConnection(getActivity()) || SPUtils.getXiaZai()) {
                        httpdownload(jsonData);
                        CustomToast.showToast(getActivity(), R.string.开始下载);
                    } else {
                        MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "下载开关才能下载", "开启2G/3G/4G网络下载开关", new MyDialog.MyDialogInterfaceListener() {
                            @Override
                            public void callBack() {
                                SPUtils.setXiaZai(true);
                                httpdownload(jsonData);

                            }
                        });
                        dialog.show();
                        /*AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                        dialogBuilder.setTitle("流量");
                        dialogBuilder.setMessage("当前不是wifi环境,是否下载?");
                        dialogBuilder.setCancelable(true);
                        dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                myhandler.cancel();
                            }
                        });
                        dialogBuilder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                            //确认按钮的点击事件
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                httpdownload(jsonData);
                            }
                        });
                        dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog dialog = dialogBuilder.create();

                        dialog.show();*/
                    }
                }
                break;
            // 标题栏播放按钮
            case R.id.iv_title_right:
                // 播放按钮
            case R.id.tv_music_title:
            case R.id.tv_music_from:
            case R.id.iv_music_voice:
               /* new Thread(new Runnable() {
                    @Override
                    public void run() {
                        playFlag = !playFlag;
                        if (playFlag){
//                            iv_music_voice.setImageResource(R.drawable.zaowandu_stop);
                            source = 1;
                        }else{
                            source = 4;
//                            iv_music_voice.setImageResource(R.drawable.zaowandu_play);
                        }
                        handler1.removeCallbacks(this);
                       handler1.sendEmptyMessage(0);
                    }
                }).start();*/
                UMengUtils.onCountListener(getActivity(), "GD_02_02_02");
                if (jsonData == null) {
                    MLog.v("reading", "出现错误,没有找到对应的音频文件");
                    return;
                }
                Fragment_WeRead.getInstance().refresh_playeranmi();
                if (source != 4) {
                    Playlist_Now.setmusicID(jsonData);
                    MLog.v("reading", "播放列表");
                    //region 播放列表
                    if (Playlist_Now.media_now_IndexBean == null || !jsonData.media.equals(Playlist_Now.media_now_IndexBean.media)) {
                        //播放时有无网络进行提示
                        if (!NetUtil.checkNet(getActivity())) {
                            //已下载的就播放本地音频
                            if (new File(Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Media + Playlist_Now.media_now_IndexBean.book_title + "\n" + Playlist_Now.media_now_IndexBean.title + ".mp3").exists()) {
                                serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
                                serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                // serviceIntent.setAction("MUSIC");
                                //启动服务
                                getActivity().startService(serviceIntent);
                                Animation_MediaPlaying(true);
                            } else {
                                Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (NetUtil.isMobileConnection(getActivity())) {
                                if (SPUtils.getBoFang()) {
                                    // 播放新的media
                                    // TODO: 16/4/18
                                    serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
                                    serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                    // serviceIntent.setAction("MUSIC");
                                    //启动服务
                                    getActivity().startService(serviceIntent);
                                    Animation_MediaPlaying(true);
                                } else {
                                    MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "播放开关才能播放", "开启2G/3G/4G网络播放开关", new MyDialog.MyDialogInterfaceListener() {
                                        @Override
                                        public void callBack() {
                                            SPUtils.setBoFang(true);
                                            // 播放新的media
                                            // TODO: 16/4/18
                                            serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
                                            serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                            // serviceIntent.setAction("MUSIC");
                                            //启动服务
                                            getActivity().startService(serviceIntent);
                                            Animation_MediaPlaying(true);
                                        }
                                    });
                                    dialog.show();
                                }
                            } else if (NetUtil.isWifiConnection(getActivity())) {
                                // 播放新的media
                                // TODO: 16/4/18
                                serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
                                serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                // serviceIntent.setAction("MUSIC");
                                //启动服务
                                getActivity().startService(serviceIntent);
                                Animation_MediaPlaying(true);
                            }
                        }
                    } else {
                        if (PlayerService.player == null) {
                            //播放时有无网络进行提示
                            if (!NetUtil.checkNet(getActivity())) {
                                //已下载的就播放本地音频
                                if (new File(Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Media + Playlist_Now.media_now_IndexBean.book_title + "\n" + Playlist_Now.media_now_IndexBean.title + ".mp3").exists()) {
                                    serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
                                    serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                    // serviceIntent.setAction("MUSIC");
                                    //启动服务
                                    getActivity().startService(serviceIntent);
                                    Animation_MediaPlaying(true);
                                } else {
                                    Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (NetUtil.isMobileConnection(getActivity())) {
                                    if (SPUtils.getBoFang()) {
                                        serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
                                        serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                        // serviceIntent.setAction("MUSIC");
                                        //启动服务
                                        getActivity().startService(serviceIntent);
                                        Animation_MediaPlaying(true);
                                    } else {
                                        MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "播放开关才能播放", "开启2G/3G/4G网络播放开关", new MyDialog.MyDialogInterfaceListener() {
                                            @Override
                                            public void callBack() {
                                                SPUtils.setBoFang(true);
                                                // 播放新的media
                                                // TODO: 16/4/18
                                                serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
                                                serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                                // serviceIntent.setAction("MUSIC");
                                                //启动服务
                                                getActivity().startService(serviceIntent);
                                                Animation_MediaPlaying(true);

                                            }
                                        });
                                        dialog.show();
                                    }
                                } else if (NetUtil.isWifiConnection(getActivity())) {
                                    serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
                                    serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                    // serviceIntent.setAction("MUSIC");
                                    //启动服务
                                    getActivity().startService(serviceIntent);
                                    Animation_MediaPlaying(true);
                                }
                            }
                        } else {
                            if (PlayerService.player.isPlaying()) {
                                Animation_MediaPlaying(true);
                            } else {
                                Animation_MediaPlaying(false);
                                // media 暂停
                            }
                            //播放时有无网络进行提示
                            if (!NetUtil.checkNet(getActivity())) {
                                //已下载的就播放本地音频
                                if (new File(Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Media + Playlist_Now.media_now_IndexBean.book_title + "\n" + Playlist_Now.media_now_IndexBean.title + ".mp3").exists()) {
                                    serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
                                    //启动服务
                                    getActivity().startService(serviceIntent);
                                } else {
                                    Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (NetUtil.isMobileConnection(getActivity())) {
                                    if (SPUtils.getBoFang()) {
                                        // media 继续播放
                                        // media 暂停/继续播放
                                        //都用 PlayerService.PAUSE
                                        serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
                                        //启动服务
                                        getActivity().startService(serviceIntent);
                                    } else {
                                        MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "播放开关才能播放", "开启2G/3G/4G网络播放开关", new MyDialog.MyDialogInterfaceListener() {
                                            @Override
                                            public void callBack() {
                                                SPUtils.setBoFang(true);
                                                // 播放新的media
                                                // TODO: 16/4/18
                                                serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
                                                //启动服务
                                                getActivity().startService(serviceIntent);
                                            }
                                        });
                                        dialog.show();
                                    }
                                } else if (NetUtil.isWifiConnection(getActivity())) {
                                    // media 继续播放
                                    // media 暂停/继续播放
                                    //都用 PlayerService.PAUSE
                                    serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
                                    //启动服务
                                    getActivity().startService(serviceIntent);
                                }
                            }
                        }
                    }
                } else {
                    //region 播放单曲
                    if (PlayerService.media_now_IndexBean == null || PlayerService.player == null) {
                        //播放时有无网络进行提示
                        if (!NetUtil.checkNet(getActivity())) {
                            //已下载的就播放本地音频
                            if (new File(Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Media + Playlist_Now.media_now_IndexBean.book_title + "\n" + Playlist_Now.media_now_IndexBean.title + ".mp3").exists()) {
                                serviceIntent.putExtra("singleBpInfoBean", jsonData);
                                serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                //启动服务
                                getActivity().startService(serviceIntent);
                                Animation_MediaPlaying(true);
                            } else {
                                Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (NetUtil.isMobileConnection(getActivity())) {
                                if (SPUtils.getBoFang()) {
                                    // TODO: 16/4/18
                                    serviceIntent.putExtra("singleBpInfoBean", jsonData);
                                    serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                    //启动服务
                                    getActivity().startService(serviceIntent);
                                    Animation_MediaPlaying(true);
                                } else {
                                    MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "播放开关才能播放", "开启2G/3G/4G网络播放开关", new MyDialog.MyDialogInterfaceListener() {
                                        @Override
                                        public void callBack() {
                                            SPUtils.setBoFang(true);
                                            // 播放新的media
                                            // TODO: 16/4/18
                                            serviceIntent.putExtra("singleBpInfoBean", jsonData);
                                            serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                            //启动服务
                                            getActivity().startService(serviceIntent);
                                            Animation_MediaPlaying(true);
                                        }
                                    });
                                    dialog.show();
                                }
                            } else if (NetUtil.isWifiConnection(getActivity())) {
                                // TODO: 16/4/18
                                serviceIntent.putExtra("singleBpInfoBean", jsonData);
                                serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                //启动服务
                                getActivity().startService(serviceIntent);
                                Animation_MediaPlaying(true);
                            }
                        }
                        return;
                    }
                    if (!jsonData.media.equals(PlayerService.media_now_IndexBean.media)) {
                        //播放时有无网络进行提示
                        if (!NetUtil.checkNet(getActivity())) {
                            //已下载的就播放本地音频
                            if (new File(Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Media + Playlist_Now.media_now_IndexBean.book_title + "\n" + Playlist_Now.media_now_IndexBean.title + ".mp3").exists()) {
                                serviceIntent.putExtra("singleBpInfoBean", jsonData);
                                serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                //启动服务
                                getActivity().startService(serviceIntent);
                                Animation_MediaPlaying(true);
                            } else {
                                Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (NetUtil.isMobileConnection(getActivity())) {
                                if (SPUtils.getBoFang()) {
                                    // 播放新的media
                                    // TODO: 16/4/18
                                    serviceIntent.putExtra("singleBpInfoBean", jsonData);
                                    serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                    //启动服务
                                    getActivity().startService(serviceIntent);
                                    Animation_MediaPlaying(true);
                                } else {
                                    MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "播放开关才能播放", "开启2G/3G/4G网络播放开关", new MyDialog.MyDialogInterfaceListener() {
                                        @Override
                                        public void callBack() {
                                            SPUtils.setBoFang(true);
                                            // 播放新的media
                                            // TODO: 16/4/18
                                            serviceIntent.putExtra("singleBpInfoBean", jsonData);
                                            serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                            //启动服务
                                            getActivity().startService(serviceIntent);
                                            Animation_MediaPlaying(true);

                                        }
                                    });
                                    dialog.show();
                                }
                            } else if (NetUtil.isWifiConnection(getActivity())) {
                                // 播放新的media
                                // TODO: 16/4/18
                                serviceIntent.putExtra("singleBpInfoBean", jsonData);
                                serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                //启动服务
                                getActivity().startService(serviceIntent);
                                Animation_MediaPlaying(true);
                            }
                        }
                    } else {

                        if (PlayerService.player.isPlaying()) {
                            Animation_MediaPlaying(false);
                        } else {
                            Animation_MediaPlaying(true);
                            // media 暂停
                        }
                        //播放时有无网络进行提示
                        if (!NetUtil.checkNet(getActivity())) {
                            if (new File(Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Media + Playlist_Now.media_now_IndexBean.book_title + "\n" + Playlist_Now.media_now_IndexBean.title + ".mp3").exists()) {
                                serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
                                //启动服务
                                getActivity().startService(serviceIntent);
                            } else {
                                Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (NetUtil.isMobileConnection(getActivity())) {
                                if (SPUtils.getBoFang()) {
                                    // media 继续播放
                                    // media 暂停/继续播放
                                    //都用 PlayerService.PAUSE
                                    serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
                                    //启动服务
                                    getActivity().startService(serviceIntent);
                                } else {
                                    MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "播放开关才能播放", "开启2G/3G/4G网络播放开关", new MyDialog.MyDialogInterfaceListener() {
                                        @Override
                                        public void callBack() {
                                            SPUtils.setBoFang(true);
                                            // 播放新的media
                                            // TODO: 16/4/18
                                            serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
                                            //启动服务
                                            getActivity().startService(serviceIntent);
                                        }
                                    });
                                    dialog.show();
                                }

                            } else if (NetUtil.isWifiConnection(getActivity())) {
                                // media 继续播放
                                // media 暂停/继续播放
                                //都用 PlayerService.PAUSE
                                serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
                                //启动服务
                                getActivity().startService(serviceIntent);
                            }
                        }
                    }
                }

                break;
            case R.id.iv_bookpart_wx:
                //分享 - 微信
                sharePopupWindow.imageUrl = jsonData.top_img;
                sharePopupWindow.h5Url = GlobalConstant.ServerDomain + "share/pack/" + jsonData.id;
                sharePopupWindow.title = jsonData.title;
                sharePopupWindow.content = "《" + jsonData.book_title + "》" + tv_music_title.getText().toString();
                UMengUtils.onCountListener(getActivity(), "GD_02_02_06");
                UMShare.setUMeng(getActivity(), 1, sharePopupWindow.title, sharePopupWindow.content, sharePopupWindow.imageUrl, sharePopupWindow.h5Url, jsonData.id, "bp");
                if ("1".equals(UMShare.getLevel())) {
                    startActivity(new Intent(getActivity(), UpgradeActivity.class));
                }
                break;
            case R.id.iv_bookpart_pyq:
                //分享 - 朋友圈
                sharePopupWindow.imageUrl = jsonData.top_img;
                sharePopupWindow.h5Url = GlobalConstant.ServerDomain + "share/pack/" + jsonData.id;
                sharePopupWindow.title = "";
                sharePopupWindow.content = jsonData.title;
                UMengUtils.onCountListener(getActivity(), "GD_02_02_06");
                UMShare.setUMeng(getActivity(), 2, sharePopupWindow.title, sharePopupWindow.content, sharePopupWindow.imageUrl, sharePopupWindow.h5Url, jsonData.id, "bp");
                if ("1".equals(UMShare.getLevel())) {
                    startActivity(new Intent(getActivity(), UpgradeActivity.class));
                }
                break;
            default:
                break;
        }
    }


    HttpHandler myhandler;

    void httpdownload(BpInfoBean bpInfoBean) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("pb_id", pb_id);
        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.book_pack, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();

                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {

                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {

                        String jsonString = responseInfo.result;
                        try {
                            Json_BookInfoWithPacks js = new Gson().fromJson(jsonString, Json_BookInfoWithPacks.class);
                            if ("1".equals(js.code)) {
                                NOsqlUtil.set_BookInfoWithPacks(js); //直接存储bpinfobean到本地
                            } else {
                                Context context = getActivity();
                                if (context != null) {
                                    Toast.makeText(context, js.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Context context = getActivity();
                            if (context != null) {
                                Toast.makeText(context, context.getString(R.string.json_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, true, null);
        HttpUtils http = new HttpUtils();
        myhandler = http.download(bpInfoBean.media, bpInfoBean.media_localpath(),
                true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {

                        iv_music_xiazai.setImageResource(R.drawable.downloadfinish_bookpack);
                        CustomToast.showToast(getActivity(), "缓存完成");
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {

                        CustomToast.showToast(getActivity(), "缓存失败");
                    }
                });
    }

    /**
     * 签到动画
     */
    public void qiandao_anmiation() {
        iv_readbook_qiandao.setImageResource(R.drawable.qiandaochenggong);
        MLog.v("reading", "tv_readbook_qiandao");
        AnimationSet set = (AnimationSet) AnimationUtils.loadAnimation(getActivity(), R.anim.qiandao);
        set.setRepeatCount(Animation.INFINITE);
        iv_readbook_qiandao.startAnimation(set);
    }

    // 网络请求 - 评论列表
    private void getDataComment() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", id);
        map.put("type", "new");
        map.put("page", newPage + "");

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.pack_comment, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String error) {
                        is_loading = false;
                        pullToRefreshListView.onRefreshComplete();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        is_loading = false;
                        pullToRefreshListView.onRefreshComplete();
                        String jsonString = responseInfo.result;
                        try {
//                            System.out.println("----------333:" + jsonString);
                            GroupJson json = new Gson().fromJson(
                                    jsonString, GroupJson.class);
                            if ("1".equals(json.code)) {
                                if (newPage == 1) {
                                    newList.clear();
                                    if (json.data == null
                                            || json.data.size() == 0) {
                                        // 没有数据
                                    } else {
                                        newList.addAll(json.data);
                                    }
                                } else {
                                    if (json.data == null
                                            || json.data.size() == 0) {
                                        newPage--;
                                    } else {
                                        newList.addAll(json.data);
                                    }
                                }
                                newAdapter.notifyDataSetChanged();
                            } else {
                                Context context = getActivity();
                                if (context != null
                                        && !((Activity) context).isFinishing()) {
                                    CustomToast.showToast(context, json.msg);
                                }
                            }
                        } catch (Exception e) {
                            Context context = getActivity();
                            if (context != null
                                    && !((Activity) context).isFinishing()) {
                                CustomToast.showToast(context,
                                        context.getString(R.string.json_error));
                                MLog.v("reading", "getDataComment:\n" + e.toString());
                            }
                        }
                    }
                }, true, null);
    }


    //region 请求网络 有网情况
    private void getData_BpInfoBean() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", id);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.pack_info, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(0);
                            }
                        }.start();

                        if (ll_readbook_1.getVisibility() == View.GONE) {
                        }
                    }

                    //设置 302不可见，设置
                    @Override
                    public void onFailure(HttpException arg0, String error) {
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                                handler.sendEmptyMessage(2);
                            }
                        }.start();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                            }
                        }.start();
                        String jsonString = responseInfo.result;
                        MLog.v("reading", jsonString);

                        try {
                            BpInfoJson js = new Gson().fromJson(jsonString, BpInfoJson.class);
                            if ("1".equals(js.code)) {

                                jsonData = js.data;
                                NOsqlUtil.set_BpInfoBean(jsonData);//直接存储bpinfobean到本地
                                setData_BpInfoBean();

                            } else if ("302".equals(js.code)) {
                                ll_readbook_1.setVisibility(View.GONE);
                                ll_readbook_2.setVisibility(View.VISIBLE);
                                wv_readbook_302.setWebViewClient(new WebViewClient());
                                wv_readbook_302.getSettings().setJavaScriptEnabled(true);
                            } else {
                                Context context = getActivity();
                                if (context != null
                                        && !((Activity) context).isFinishing()) {
                                    CustomToast.showToast(context, js.msg);
                                }
                            }
                        } catch (Exception e) {
                            Context context = getActivity();
                            if (context != null && !((Activity) context).isFinishing()) {
                                CustomToast.showToast(context,
                                        context.getString(R.string.json_error));
                                MLog.v("reading", "getData_BpInfoBean:\n" + e.toString());
                            }
                        }
                    }
                }, true, null);
    }

    private void setData_BpInfoBean() {

        ll_readbook_1.setVisibility(View.VISIBLE);
        ll_readbook_2.setVisibility(View.GONE);

        tv_music_from.setText("来自有书共读主播" + jsonData.name);
        if (jsonData == null) return;

        if (jsonData.Exist()) {
            iv_music_xiazai.setImageResource(R.drawable.downloadfinish_bookpack);
        }

        MLog.v("reading", "bp week" + jsonData.pub_time);

        tv_music_title.setText(jsonData.Getweek_zh() + jsonData.timetype_tostring());
        tv_title_mid.setText(jsonData.Getweek_zh() + jsonData.timetype_tostring());
        tv_music_media_time.setText(jsonData.media_time);
        bookname = jsonData.book_title;
        // 顶部图片
        if (TextUtils.isEmpty(jsonData.top_img)) {
            iv_bookpack_image.setBackgroundColor(getResources().getColor(R.color.realgrey));
        } else {
            new BitmapUtils(getActivity())
                    .display(
                            iv_bookpack_image,
                            jsonData.top_img,
                            new BitmapLoadCallBack<ImageView>() {
                                @Override
                                public void onLoadCompleted(
                                        ImageView view,
                                        String string,
                                        Bitmap bitmap,
                                        BitmapDisplayConfig bitmapDisplayConfig,
                                        BitmapLoadFrom bitmapLoadFrom) {
                                    iv_bookpack_image.setImageBitmap(bitmap);
//															}
                                }

                                @Override
                                public void onLoadFailed(
                                        ImageView view,
                                        String string,
                                        Drawable drawable) {
                                }
                            });
        }

        //检查 音频是否存在
        if (TextUtils.isEmpty(jsonData.media)) {
            rl_music_voice.setVisibility(View.GONE);

        }

        if (TextUtils.isEmpty(jsonData.content)) {
            // 设置网页不显示内容
            wv_readbook_content.setVisibility(View.GONE);
        } else {
            wv_readbook_content.setVisibility(View.VISIBLE);

            WebSettings ws = wv_readbook_content.getSettings();
            ws.setJavaScriptEnabled(false);
            ws.setAllowFileAccess(true);
            ws.setBuiltInZoomControls(false);
            ws.setSupportZoom(false);
            ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
            ws.setDefaultTextEncodingName("utf-8");
            ws.setAppCacheEnabled(true);
            ws.setCacheMode(WebSettings.LOAD_DEFAULT);
            wv_readbook_content.loadDataWithBaseURL(null, jsonData.content, "text/html", "utf-8", null);
        }

        // 评论 赞 的数量
        tv_readbook_liuyan_num.setText(jsonData.comment_count);

        if ("0".equals(jsonData.is_fav)) {
            iv_readbook_shoucang.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.collect_no));
        } else if ("1".equals(jsonData.is_fav)) {
            iv_readbook_shoucang.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.collect));
        }

        //签到
        if (jsonData.is_qian.equals("1")) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) (iv_readbook_qiandao.getLayoutParams());
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            iv_readbook_qiandao.setLayoutParams(params);
            iv_readbook_qiandao.setImageResource(R.drawable.qiandaochenggong);
            tv_readbook_qiandao.setVisibility(View.GONE);
        } else {
            tv_readbook_qiandao.setBackgroundResource(R.drawable.yuan_green);
            if ("0".equals(jsonData.show_check)) {
                bookpack_qiandao_notify.setVisibility(View.VISIBLE);
                tv_readbook_qiandao.setVisibility(View.GONE);
                iv_readbook_qiandao.setVisibility(View.GONE);
            }
        }
        // 顶部 标题
        if (jsonData.title != null) {
            tv_bookpack_title.setText(jsonData.title);
        }
    }

    //网络请求 - 签到
    private void getDataCheckIn(final boolean click) {
        if ("1".equals(jsonData.is_qian)) {
            CustomToast.showToast(getActivity(), "已经签到过了哦,不用重复签到");
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", id);
        map.put("pb_id", pb_id);

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.check_in,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(0);
                            }
                        }.start();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String error) {
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                                handler.sendEmptyMessage(2);
                            }
                        }.start();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                            }
                        }.start();
                        String jsonString = responseInfo.result;

                        try {
                            final BaseJson json = new Gson().fromJson(jsonString, BaseJson.class);

                            //签到成功
                            if ("1".equals(json.code) && !("1".equals(Fragment_Bookpack.this.jsonData.is_qian))) {
                                Fragment_Bookpack.this.jsonData.is_qian = "1";
                                tv_readbook_qiandao.setBackgroundResource(R.drawable.yuan_grey);
                                if (click) {
                                    qiandao_anmiation();
                                }
                                //根据来源更新
                                switch (source) {
                                    case 1:
                                        Fragment_WeRead.getInstance().qiandaoOK_refresh(bookpackindex);
                                        break;
                                    case 2:
                                        Fragment_BookInfoWithPacks.getInstance().refresh(bookpackindex);
                                        break;
                                    case 3:
//                                        ExplainFragment.getInstance().refresh("");
                                        break;
                                }
                                //是否升级
                                if ("1".equals(json.level_is_up)) {
                                    startActivity(new Intent(getActivity(), UpgradeActivity.class));
                                }
                            } else {
                                Context context = getActivity();
                                if (context != null && !((Activity) context).isFinishing()) {
                                    CustomToast.showToast(context, json.msg);
                                }
                            }
                        } catch (Exception e) {
                            Context context = getActivity();
                            if (context != null
                                    && !((Activity) context).isFinishing()) {
                                CustomToast.showToast(context,
                                        context.getString(R.string.json_error));
                                MLog.v("reading", "getDataCheckIn:\n" + e.toString());
                            }
                        }
                    }
                }, true, null);
    }

    //收藏
    private void getData_shoucang() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", GlobalParams.uid);
        map.put("pack_id", jsonData.id);

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.SERVERURL + "pack/fav",
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(0);
                            }
                        }.start();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String error) {
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                                handler.sendEmptyMessage(2);
                            }
                        }.start();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                            }
                        }.start();
                        String jsonString = responseInfo.result;
                        MLog.v("reading", jsonString);
                        try {
                            final BaseJson json = new Gson().fromJson(jsonString, BaseJson.class);
                            // TODO
                            Context context = getActivity();
                            if (context != null && !((Activity) context).isFinishing()) {
                                if ("1".equals(json.code)) {
                                    CustomToast.showToast(context, "已收藏");
                                    jsonData.is_fav = "1";
                                    iv_readbook_shoucang.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.collect));
                                } else if ("2".equals(json.code)) {
                                    CustomToast.showToast(context, "取消收藏");
                                    jsonData.is_fav = "0";
                                    iv_readbook_shoucang.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.collect_no));
                                }
                            }


                        } catch (Exception e) {
                            Context context = getActivity();
                            if (context != null
                                    && !((Activity) context).isFinishing()) {
                                CustomToast.showToast(context,
                                        context.getString(R.string.json_error));
                            }
                        }
                    }
                }, true, null);
    }

    //

    /**
     * 点赞的点击
     */
    public void dianzan(final int position, final int source) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("bpc_id", newList.get(position).id);

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.comment_comdigg,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String jsonString = responseInfo.result;
                        try {
                            BaseJson json = new Gson().fromJson(jsonString,
                                    BaseJson.class);
                            if ("1".equals(json.code)) {
                                refresh(position, "dianzan" + position, source);
                            } else if ("2".equals(json.code)) {
                                refresh1(position, "dianzan" + position, source);
                            }
                        } catch (Exception e) {

                        }
                    }
                }, true, null);
    }

    /**
     * 点赞
     */
    public void refresh(int position, String tag, int source) {
        if (tag.startsWith("dianzan")) {
            TextView textView = (TextView) newListView
                    .findViewWithTag("dianzan_tv" + position);
            // 设置为赞过
            newList.get(position).is_digg = "1";
            Drawable drawable = getResources().getDrawable(
                    R.drawable.comment_zan_hou);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            textView
                    .setCompoundDrawables(drawable, null, null, null);
            textView.setTextColor(getActivity().getResources()
                    .getColor(R.color.zan_text_color));
            try {
                int count = Integer
                        .valueOf(newList.get(position).digg_count);
                textView.setText((count + 1) + "");
                newList.get(position).digg_count = (count + 1) + "";
            } catch (Exception e) {
            }
        }
    }

    /**
     * 取消点赞
     */
    public void refresh1(int position, String tag, int source) {
        if (tag.startsWith("dianzan")) {
            TextView textView = (TextView) newListView
                    .findViewWithTag("dianzan_tv" + position);
            // 取消赞
            newList.get(position).is_digg = "0";
            Drawable drawable = getResources().getDrawable(
                    R.drawable.comment_zan);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            textView
                    .setCompoundDrawables(drawable, null, null, null);
            textView.setTextColor(getActivity().getResources()
                    .getColor(R.color.text_98));
            try {
                int count = Integer
                        .valueOf(newList.get(position).digg_count);
                textView.setText((count - 1) + "");
                newList.get(position).digg_count = (count - 1) + "";
            } catch (Exception e) {
            }
        }
    }

    /**
     * 笔记删除后刷新
     */
    public void refresh(int source, int position) {
        newList.remove(position);
        newAdapter.notifyDataSetChanged();
    }

    /**
     * 评论刷新
     */
    public void refresh(final String level_is_up) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                newPage = 1;
                getDataComment();
                try {
                    int count = Integer.valueOf(jsonData.comment_count);
                    jsonData.comment_count = (count + 1) + "";
                    tv_readbook_liuyan_num.setText("" + jsonData.comment_count);
                } catch (Exception e) {
                }
                //是否升级
                if ("1".equals(level_is_up)) {
                    startActivity(new Intent(getActivity(), UpgradeActivity.class));
                }
            }
        }, 300);
    }

    private class CurrentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int current = intent.getIntExtra("CURRENT", -1);
            if (jsonData == null) return;

            MLog.v("reading", "source:" + source);

            switch (source) {
                case 1:
                case 2:
                case 3:
                    //region Playlist_Now.media_now_IndexBean
                    if (Playlist_Now.media_now_IndexBean != null && PlayerService.media_now_IndexBean == null && jsonData.media.equals(Playlist_Now.media_now_IndexBean.media)) {
                        iv_music_xiazai.setImageResource(R.drawable.downloadfinish_bookpack);

                        //进度条 设置
                        sb_music_show.setMax(Playlist_Now.media_now_IndexBean.getMaxlength());
                        pb_title_music_show.setMax(Playlist_Now.media_now_IndexBean.getMaxlength());

                        Animation_MediaPlaying(PlayerService.player.isPlaying());

                        sb_music_show.setProgress(current / 1000);
                        pb_title_music_show.setProgress(current / 1000);

                        current = current / 1000;
                        int minite = (current / 60);
                        int second = (current - minite * 60);

                        String now = (minite > 9 ? minite : "0" + minite) + ":" + (second > 9 ? second : "0" + second);
                        tv_music_media_progress.setText(now);
                    } else {
                        sb_music_show.setProgress(0 / 1000);
                        pb_title_music_show.setProgress(0 / 1000);
                        tv_music_media_progress.setText("00:00");
                    }
                    //endregion
                    break;
                case 4:
                    //region Playlist_Now.media_now_IndexBean
                    if (PlayerService.media_now_IndexBean != null && jsonData.media.equals(PlayerService.media_now_IndexBean.media)) {

                        iv_music_xiazai.setImageResource(R.drawable.downloadfinish_bookpack);
                        //进度条 设置
                        sb_music_show.setMax(PlayerService.media_now_IndexBean.getMaxlength());
                        pb_title_music_show.setMax(PlayerService.media_now_IndexBean.getMaxlength());

                        Animation_MediaPlaying(PlayerService.player.isPlaying());

                        sb_music_show.setProgress(current / 1000);
                        pb_title_music_show.setProgress(current / 1000);

                        current = current / 1000;
                        int minite = (current / 60);
                        int second = (current - minite * 60);

                        String now = (minite > 9 ? minite : "0" + minite) + ":" + (second > 9 ? second : "0" + second);
                        tv_music_media_progress.setText(now);
                    } else {
                        sb_music_show.setProgress(0 / 1000);
                        pb_title_music_show.setProgress(0 / 1000);
                        tv_music_media_progress.setText("00:00");
                    }
                    //endregion
                    break;
            }

        }
    }


    @Override
    public void onStart() {
        super.onStart();
        currentReceiver = new CurrentReceiver();
        IntentFilter currentFilter = new IntentFilter();
        currentFilter.addAction("CURRENT_ACTION");
        getActivity().registerReceiver(currentReceiver, currentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(currentReceiver);
    }

    public void onResume() {
        super.onResume();
        if (source == 4) {
            imageView_goto_fragment_music.setVisibility(View.GONE);
        } else {
            imageView_goto_fragment_music.setVisibility(View.VISIBLE);
        }
        saveView = getView();
        PlayerService.autoplaynext = false;
        MobclickAgent.onPageStart("Fragment_Bookpack");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Fragment_Bookpack");
    }

}
