package com.fengwo.reading.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.common.SelectSharePopupWindow;
import com.fengwo.reading.main.read.IndexBean;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.umeng.UMShare;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.ImageUtils;
import com.fengwo.reading.utils.MyDialog;
import com.fengwo.reading.utils.NetUtil;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.utils.localdata.SPUtils;
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
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author lxq 音乐播放器
 */
public class Fragment_MediaPlayer extends Fragment implements OnClickListener {

    private SelectSharePopupWindow sharePopupWindow;
    //region 顶栏
    private LinearLayout topimg_fragment_mediaplayer;
    private ImageView iv_music_back;
    private TextView tv_music_nowindex;
    public  static boolean isConNet = true;

    //    AlertDialog.Builder dialog;
//    TextView tv_3,tv_2,tv_1;
//    View view1;
    LinearLayout ll_music_layout;
    //region 下部分
    private TextView tv_music_book, tv_music_title, tv_nextmusic_title;
    private TextView tv_music_progress, tv_music_duration;
    private SeekBar sb_music_show;
    private ImageView iv_music_prev, iv_music_play, iv_music_next;

    private ImageView iv_music_bg;
    private ImageView iv_music_list, imageView_fragment_media_media_download, imageView_fragment_media_media_share, imageView_fragment_media_media_clock;
    private PopupWindow_SelectMusic popupWindow;
    private PopupWindow_MediaClock mediaClockPopupWindow;

    private Intent serviceIntent;
    private CurrentReceiver currentReceiver;
    private NextReceiver nextReceiver;
    private PercentReceiver percentReceiver;

    public static Fragment_MediaPlayer fragment = new Fragment_MediaPlayer();

    public static Fragment_MediaPlayer getInstance() {
        return fragment;
    }

    private OnClickListener tv_3_onclick = new OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    };

    //显示，当前播放的bplist
    public void showMusicTextInfo(List<IndexBean> now_Medialist, int now_index) {
        if (now_Medialist == null || now_index > now_Medialist.size()) {
            return;
        }
        tv_music_book.setText("《" + now_Medialist.get(now_index).book_title + "》");

        tv_music_title.setText("周" + now_Medialist.get(now_index).getPub_time_week() + now_Medialist.get(now_index).timetype_tostring()
                + ":"
                + now_Medialist.get(now_index).title);

        if (now_index + 1 >= now_Medialist.size()) {
            //如果当前是最后一个，则显示下一篇为第一篇
            tv_nextmusic_title.setText("下一篇：" + now_Medialist.get(0).title);
        } else {
            tv_nextmusic_title.setText("下一篇：" + now_Medialist.get(now_index + 1).title);
        }

        tv_music_duration.setText(now_Medialist.get(now_index).media_time);

        tv_music_nowindex.setText((now_index + 1) + "/" + now_Medialist.size());
    }

    //todo
    Drawable bg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 音乐全凭播放页面fragment
        View view = inflater.inflate(R.layout.fragment_media, container, false);

        findViewById(view);
        onClickListener();
        if (!NetUtil.checkNet(getActivity())){
            isConNet = false;
        }else{
            isConNet = true;
        }
        if (Playlist_Now.Medialist().size() == 0) {
            CustomToast.showToast(getActivity(), "音频列表是空的哦");
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().finish();
            return null;
        }

        if (Playlist_Now.musicID == null) {
            Playlist_Now.musicID = Playlist_Now.Medialist().size() - 1;
            //当前播放音频的实体类
            Playlist_Now.media_now_IndexBean = Playlist_Now.Medialist().get(Playlist_Now.musicID);
        }

        //todo
        setbackgroud();
        //region 分享
        // TODO
        sharePopupWindow = new SelectSharePopupWindow(getActivity(),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        FragmentActivity activity = getActivity();
                        if (activity == null) {
                            return;
                        }
                        sharePopupWindow.imageUrl = Playlist_Now.media_now_IndexBean.top_img;
                        sharePopupWindow.h5Url = GlobalConstant.ServerDomain + "share/pack/" + Playlist_Now.media_now_IndexBean.id;
                        int num = 0;
                        switch (v.getId()) {
                            case R.id.ll_popupwindow_wx:
                                num = 1;
                                sharePopupWindow.title = "《" + Playlist_Now.media_now_IndexBean.book_title + "》" + tv_music_title.getText().toString();
                                sharePopupWindow.content = Playlist_Now.media_now_IndexBean.title;
                                break;
                            case R.id.ll_popupwindow_pyq:
                                num = 2;
                                sharePopupWindow.title = Playlist_Now.media_now_IndexBean.title;
                                sharePopupWindow.content = Playlist_Now.media_now_IndexBean.title;
                                break;
                            case R.id.ll_popupwindow_qq:
                                num = 3;
                                sharePopupWindow.title = "《" + Playlist_Now.media_now_IndexBean.book_title + "》" + tv_music_title.getText().toString();
                                sharePopupWindow.content = Playlist_Now.media_now_IndexBean.title;
                                break;
                            case R.id.ll_popupwindow_wb:
                                num = 4;
                                sharePopupWindow.title = "《" + Playlist_Now.media_now_IndexBean.book_title + "》" + tv_music_title.getText().toString();
                                sharePopupWindow.content = Playlist_Now.media_now_IndexBean.title;
                                break;
                            default:
                                break;
                        }
                        UMengUtils.onCountListener(getActivity(), "shouye_CSB_FX");
                        UMShare.setUMeng(activity, num, sharePopupWindow.title, sharePopupWindow.content, sharePopupWindow.imageUrl, sharePopupWindow.h5Url, "", "");
                        sharePopupWindow.dismiss();
                    }
                });

        serviceIntent = new Intent(getActivity(), PlayerService.class);

        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        //todo 设置文字进度
        tv_music_progress.setText(format.format(PlayerService.current_progress));
        //todo 设置seekbar进度
        sb_music_show.setProgress(PlayerService.current_progress);

        //显示当前音频信息
        if (Playlist_Now.Medialist() != null && Playlist_Now.Medialist().size() > 0) {
            if (Playlist_Now.musicID != null && Playlist_Now.musicID <= Playlist_Now.Medialist().size() - 1)
                showMusicTextInfo(Playlist_Now.Medialist(), Playlist_Now.musicID);
            else
                showMusicTextInfo(Playlist_Now.Medialist(), Playlist_Now.Medialist().size() - 1);
            //设置seekbar max进度
            sb_music_show.setMax(100);
        }
        //设置播放按钮状态
        if (PlayerService.player == null) {
            iv_music_play.setImageResource(R.drawable.music_play);
        } else {
            if (PlayerService.player.isPlaying()) {
                iv_music_play.setImageResource(R.drawable.music_stop);
            } else {
                iv_music_play.setImageResource(R.drawable.music_play);
            }
        }
        //设置seekbar监听
        sb_music_show.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

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
                    Intent intent = new Intent();
                    //"com.fengwo.reading.PROGRESS_ACTION"是PlayerService中注册的关于播放进度的广播
                    intent.setAction("com.fengwo.reading.PROGRESS_ACTION");
                    if (PlayerService.player == null) return;
                    int musicMax = PlayerService.player.getDuration();
                    int seekBarMax = seekBar.getMax();
                    //musicMax * progress / seekBarMax 传送调整后的播放进度
                    intent.putExtra("PROGRESS", musicMax * progress / seekBarMax);
                    //发送广播
                    getActivity().sendBroadcast(intent);
                    // 并且改变当前seekbar进度信息和tv_music_progress的显示信息
                    SimpleDateFormat format = new SimpleDateFormat("mm:ss");
                    tv_music_progress.setText(format.format(progress));
                    sb_music_show.setProgress(progress);

                    Log.v("reading", "" + progress);
                }
            }
        });

        if (new File(Environment.getExternalStorageDirectory().getPath() +  GlobalParams.FolderPath_Media+Playlist_Now.media_now_IndexBean.book_title+"\n"+Playlist_Now.media_now_IndexBean.title+".mp3").exists()){
            imageView_fragment_media_media_download.setImageResource(R.drawable.media_downloadfinish);
        }else {
            imageView_fragment_media_media_download.setImageResource(R.drawable.media_download);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //注册当前播放进度的广播
        currentReceiver = new CurrentReceiver();
        IntentFilter currentFilter = new IntentFilter();
        currentFilter.addAction("CURRENT_ACTION");
        getActivity().registerReceiver(currentReceiver, currentFilter);
        //注册自动播放下一首的广播
        nextReceiver = new NextReceiver();
        IntentFilter nextFilter = new IntentFilter();
        nextFilter.addAction("NEXT_ACTION");
        getActivity().registerReceiver(nextReceiver, nextFilter);
        //todo 缓冲进度的广播接收
        percentReceiver = new PercentReceiver();
        IntentFilter percentFilter = new IntentFilter();
        percentFilter.addAction("PERCENT_ACTION");
        getActivity().registerReceiver(percentReceiver, percentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        //销毁广播
        getActivity().unregisterReceiver(currentReceiver);
        getActivity().unregisterReceiver(nextReceiver);
        getActivity().unregisterReceiver(percentReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void findViewById(View view) {
        //region 顶栏
        topimg_fragment_mediaplayer = (LinearLayout) view.findViewById(R.id.topimg_fragment_mediaplayer);
        iv_music_back = (ImageView) view.findViewById(R.id.iv_music_back);
        tv_music_nowindex = (TextView) view.findViewById(R.id.tv_music_nowindex);

        ll_music_layout = (LinearLayout) view.findViewById(R.id.ll_music_layout);
        tv_music_book = (TextView) view.findViewById(R.id.tv_music_book);

        tv_nextmusic_title = (TextView) view.findViewById(R.id.tv_music_nextmusic_title);
        tv_music_title = (TextView) view.findViewById(R.id.tv_music_title);
        tv_music_progress = (TextView) view.findViewById(R.id.tv_music_progress);
        tv_music_duration = (TextView) view.findViewById(R.id.tv_music_duration);
        //seekbar
        sb_music_show = (SeekBar) view.findViewById(R.id.sb_music_show);
        //上一首
        iv_music_prev = (ImageView) view.findViewById(R.id.iv_music_prev);
        //播放or暂停
        iv_music_play = (ImageView) view.findViewById(R.id.iv_music_play);
        //下一首
        iv_music_next = (ImageView) view.findViewById(R.id.iv_music_next);

        iv_music_bg = (ImageView) view.findViewById(R.id.iv_music_bg);
        //region 底部
        iv_music_list = (ImageView) view.findViewById(R.id.iv_music_list);
        imageView_fragment_media_media_download = (ImageView) view.findViewById(R.id.imageView_fragment_media_media_download);
        imageView_fragment_media_media_share = (ImageView) view.findViewById(R.id.imageView_fragment_media_media_share);
        imageView_fragment_media_media_clock = (ImageView) view.findViewById(R.id.imageView_fragment_media_media_clock);

//        if (FileUtil.ExistMedia(Playlist_Now.media_now_IndexBean.book_title, Playlist_Now.media_now_IndexBean.title)) {
//            imageView_fragment_media_media_download.setImageResource(R.drawable.media_downloadfinish);
//        }
        //endregion
    }

    private void onClickListener() {
        iv_music_back.setOnClickListener(this);
        iv_music_prev.setOnClickListener(this);
        iv_music_play.setOnClickListener(this);
        iv_music_next.setOnClickListener(this);
        iv_music_list.setOnClickListener(this);

        imageView_fragment_media_media_share.setOnClickListener(this);
        imageView_fragment_media_media_download.setOnClickListener(this);
        imageView_fragment_media_media_clock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Context context = getActivity();
        if (context == null) {
            return;
        }
        if (Playlist_Now.musicFiles().size() == 0) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_music_back:
                // 返回键
                UMengUtils.onCountListener(getActivity(), "GD_02_07_01");
                getActivity().finish();
                getActivity().overridePendingTransition(
                        R.anim.in_from_left, R.anim.out_to_right);
                break;
            case R.id.iv_music_prev:
                // 前一首 音乐
                UMengUtils.onCountListener(getActivity(), "GD_02_07_05");
                //播放时有无网络进行提示
                if (!NetUtil.checkNet(getActivity())) {
                    Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
                } else {
                    if (NetUtil.isWifiConnection(getActivity()) || SPUtils.getBoFang()) {
                        //region
                        if (Playlist_Now.musicID != 0) {
                            Playlist_Now.musicID = Playlist_Now.musicID - 1;
                        } else {
                            //播放最后一首
                            Playlist_Now.musicID = Playlist_Now.Medialist().size() - 1;
                        }
                        //初始化各个控件
                        tv_music_progress.setText("00:00");
                        tv_music_duration.setText("00:00");
                        sb_music_show.setProgress(0);
                        sb_music_show.setMax(100);
                        sb_music_show.setSecondaryProgress(0);
                        //显示当前播放信息
                        showMusicTextInfo(Playlist_Now.Medialist(), Playlist_Now.musicID);
                        try {
                            sb_music_show.setMax(Playlist_Now.Medialist().get(Playlist_Now.musicID).getMaxlength());
                        } catch (Exception e) {
                            sb_music_show.setMax(100);
                        }

                        serviceIntent.putExtra("MUSIC", PlayerService.BACK);

                        getActivity().startService(serviceIntent);
                        iv_music_play.setImageResource(R.drawable.music_stop);
                    } else {
                        MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "播放开关才能播放", "开启2G/3G/4G网络播放开关", new MyDialog.MyDialogInterfaceListener() {
                            @Override
                            public void callBack() {
                                SPUtils.setBoFang(true);
                                //region
                                if (Playlist_Now.musicID != 0) {
                                    Playlist_Now.musicID = Playlist_Now.musicID - 1;
                                } else {
                                    //播放最后一首
                                    Playlist_Now.musicID = Playlist_Now.Medialist().size() - 1;
                                }
                                //初始化各个控件
                                tv_music_progress.setText("00:00");
                                tv_music_duration.setText("00:00");
                                sb_music_show.setProgress(0);
                                sb_music_show.setMax(100);
                                sb_music_show.setSecondaryProgress(0);
                                //显示当前播放信息
                                showMusicTextInfo(Playlist_Now.Medialist(), Playlist_Now.musicID);

                                try {
                                    sb_music_show.setMax(Playlist_Now.Medialist().get(Playlist_Now.musicID).getMaxlength());
                                } catch (Exception e) {
                                    sb_music_show.setMax(100);
                                }

                                serviceIntent.putExtra("MUSIC", PlayerService.BACK);

                                getActivity().startService(serviceIntent);
                                iv_music_play.setImageResource(R.drawable.music_stop);
                                //endregion
                            }
                        });
                        dialog.show();
                    }
                }
                break;
            case R.id.iv_music_next:
                // 后一首 音乐
                UMengUtils.onCountListener(getActivity(), "GD_02_07_06");
                //播放时有无网络进行提示
                if (!NetUtil.checkNet(getActivity())) {
                        Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
                } else {
                    if (NetUtil.isWifiConnection(getActivity()) || SPUtils.getBoFang()) {
                        //region
                        if (Playlist_Now.musicID + 1 >= Playlist_Now.musicFiles().size())
                            Playlist_Now.musicID = 0;
                        else
                            Playlist_Now.musicID = Playlist_Now.musicID + 1;

                        tv_music_progress.setText("00:00");
                        tv_music_duration.setText("00:00");
                        sb_music_show.setProgress(0);
                        sb_music_show.setMax(100);
                        sb_music_show.setSecondaryProgress(0);
                        showMusicTextInfo(Playlist_Now.Medialist(), Playlist_Now.musicID);
                        try {
                            sb_music_show.setMax(Playlist_Now.Medialist().get(Playlist_Now.musicID).getMaxlength());
                        } catch (Exception e) {
                            sb_music_show.setMax(100);
                        }
                        //
                        serviceIntent.putExtra("MUSIC", PlayerService.NEXT);
                        //启动服务
                        getActivity().startService(serviceIntent);
                        iv_music_play.setImageResource(R.drawable.music_stop);
                        //endregion
                    } else {
                        MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "播放开关才能播放", "开启2G/3G/4G网络播放开关", new MyDialog.MyDialogInterfaceListener() {
                            @Override
                            public void callBack() {
                                SPUtils.setBoFang(true);
                                //region
                                if (Playlist_Now.musicID + 1 >= Playlist_Now.musicFiles().size())
                                    Playlist_Now.musicID = 0;
                                else
                                    Playlist_Now.musicID = Playlist_Now.musicID + 1;
                                tv_music_progress.setText("00:00");
                                tv_music_duration.setText("00:00");
                                sb_music_show.setProgress(0);
                                sb_music_show.setMax(100);
                                sb_music_show.setSecondaryProgress(0);
                                showMusicTextInfo(Playlist_Now.Medialist(), Playlist_Now.musicID);
                                try {
                                    sb_music_show.setMax(Playlist_Now.Medialist().get(Playlist_Now.musicID).getMaxlength());
                                } catch (Exception e) {
                                    sb_music_show.setMax(100);
                                }
                                //
                                serviceIntent.putExtra("MUSIC", PlayerService.NEXT);
                                //启动服务
                                getActivity().startService(serviceIntent);
                                iv_music_play.setImageResource(R.drawable.music_stop);
                                //endregion
                            }
                        });
                        dialog.show();
                    }
                }
//                getActivity().startService(serviceIntent);
//                iv_music_play.setImageResource(R.drawable.music_stop);
                break;
            // 播放 当前 音乐：
            case R.id.iv_music_play:
                //region
                if (PlayerService.player == null) {
                    if (Playlist_Now.musicID == null)
                        if (Playlist_Now.Medialist().size() - 1 < 0) return;
                    //播放时有无网络进行提示
                    if (!NetUtil.checkNet(getActivity())) {
                        //已下载的就播放本地音频
                        if (new File(Environment.getExternalStorageDirectory().getPath() +  GlobalParams.FolderPath_Media+Playlist_Now.media_now_IndexBean.book_title+"/"+Playlist_Now.media_now_IndexBean.title+".mp3").exists()){
                            Playlist_Now.musicID = Playlist_Now.Medialist().size() - 1;
                            serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
                            serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                            //启动服务
                            getActivity().startService(serviceIntent);
                            iv_music_play.setImageResource(R.drawable.music_stop);
                        }else {
                            Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (NetUtil.isWifiConnection(getActivity()) || SPUtils.getBoFang()) {
                            Playlist_Now.musicID = Playlist_Now.Medialist().size() - 1;
                            serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
                            serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                            //启动服务
                            getActivity().startService(serviceIntent);
                            iv_music_play.setImageResource(R.drawable.music_stop);
                        } else {
                            MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "播放开关才能播放", "开启2G/3G/4G网络播放开关", new MyDialog.MyDialogInterfaceListener() {
                                @Override
                                public void callBack() {
                                    SPUtils.setBoFang(true);
                                    Playlist_Now.musicID = Playlist_Now.Medialist().size() - 1;
                                    serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
                                    serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                    //启动服务
                                    getActivity().startService(serviceIntent);
                                    iv_music_play.setImageResource(R.drawable.music_stop);
                                }
                            });
                            dialog.show();
                        }
                    }
                    return;
                }
                if (PlayerService.player.isPlaying()) {
                    UMengUtils.onCountListener(getActivity(), "GD_02_07_04");
                    iv_music_play.setImageResource(R.drawable.music_play);
                } else {
                    UMengUtils.onCountListener(getActivity(), "GD_02_07_03");
                    iv_music_play.setImageResource(R.drawable.music_stop);
                }
                //播放时有无网络进行提示
                if (!NetUtil.checkNet(getActivity())) {
                    //已下载的就播放本地音频
                    if (new File(Environment.getExternalStorageDirectory().getPath() +
                            GlobalParams.FolderPath_Media+
                            Playlist_Now.media_now_IndexBean.book_title+"/"+
                            Playlist_Now.media_now_IndexBean.title+".mp3").exists()){
                        serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
                        //启动服务
                        getActivity().startService(serviceIntent);
                    }else {
                        Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
                } else {
                    if (NetUtil.isWifiConnection(getActivity()) || SPUtils.getBoFang()) {
                        serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
                        //启动服务
                        getActivity().startService(serviceIntent);
                    } else {
                        MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "播放开关才能播放", "开启2G/3G/4G网络播放开关", new MyDialog.MyDialogInterfaceListener() {
                            @Override
                            public void callBack() {
                                SPUtils.setBoFang(true);
                                Playlist_Now.musicID = Playlist_Now.Medialist().size() - 1;
                                serviceIntent.putExtra("MusicID", Playlist_Now.musicID);
                                serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                //启动服务
                                getActivity().startService(serviceIntent);
                                iv_music_play.setImageResource(R.drawable.music_stop);
                            }
                        });
                        dialog.show();

                    }

                }
                break;
            case R.id.iv_music_list:
                // 音乐 列表：
                UMengUtils.onCountListener(getActivity(), "GD_02_07_07");
                popupWindow = new PopupWindow_SelectMusic(getActivity(),
                        Playlist_Now.Medialist(), Playlist_Now.musicID,
                        new OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                tv_music_progress.setText("00:00");
                                tv_music_duration.setText("00:00");
                                sb_music_show.setProgress(0);
                                sb_music_show.setMax(100);
                                sb_music_show.setSecondaryProgress(0);

                                if (position < 0 && position > Playlist_Now.Medialist().size() - 1)
                                    return;
                                Playlist_Now.musicID = position;
                                try {
                                    sb_music_show.setMax(Playlist_Now.Medialist().get(Playlist_Now.musicID).getMaxlength());
                                } catch (Exception e) {
                                    sb_music_show.setMax(100);
                                }
                                serviceIntent.putExtra("MusicID", position);
                                serviceIntent.putExtra("MUSIC", PlayerService.PLAY);
                                getActivity().startService(serviceIntent);
                                iv_music_play.setImageResource(R.drawable.music_stop);

                                showMusicTextInfo(Playlist_Now.Medialist(), Playlist_Now.musicID);
                                popupWindow.dismiss();
                            }
                        });
                popupWindow.setOutsideTouchable(true);
                popupWindow.showAsDropDown(topimg_fragment_mediaplayer);
                break;
            case R.id.imageView_fragment_media_media_download:
                //下载
                UMengUtils.onCountListener(getActivity(), "GD_02_07_08");
                if (Playlist_Now.media_now_IndexBean.Exist()) {
                    CustomToast.showToast(getActivity(), R.string.音频文件已经下载);
                    imageView_fragment_media_media_download.setImageResource(R.drawable.media_downloadfinish);
                } else {
                    //播放时有无网络进行提示
                    if (!NetUtil.checkNet(getActivity())) {
                        Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
                    } else {
                        if (NetUtil.isWifiConnection(getActivity()) || SPUtils.getXiaZai()) {

                        } else {
                            MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "下载开关才能下载", "开启2G/3G/4G网络下载开关", new MyDialog.MyDialogInterfaceListener() {
                                @Override
                                public void callBack() {
                                    SPUtils.setXiaZai(true);
                                    httpdownload(Playlist_Now.media_now_IndexBean);
                                }
                            });
                            dialog.show();
                        }
                    }
                }
                break;
            case R.id.imageView_fragment_media_media_share:
                //分享
                UMengUtils.onCountListener(getActivity(), "GD_02_07_09");
                sharePopupWindow.showAtLocation(getActivity()
                        .findViewById(R.id.fragment_mediaplayer), Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.imageView_fragment_media_media_clock:
                //定时关闭

                UMengUtils.onCountListener(getActivity(), "GD_02_07_10");
                mediaClockPopupWindow = new PopupWindow_MediaClock(getActivity());
                mediaClockPopupWindow.setOutsideTouchable(true);
                mediaClockPopupWindow.showAsDropDown(topimg_fragment_mediaplayer);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        PlayerService.autoplaynext = true;
        MobclickAgent.onPageStart("Fragment_MediaPlayer");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Fragment_MediaPlayer");
    }

    //当前音频进度的广播接收
    private class CurrentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int current = intent.getIntExtra("CURRENT", -1);
            SimpleDateFormat format = new SimpleDateFormat("mm:ss");
            String time = format.format(current);
            //广播接收信息后及时更新tv_music_progress的显示信息和sb_music_show的进度
            tv_music_progress.setText(time);
            imageView_fragment_media_media_download.setImageResource(R.drawable.media_downloadfinish);
            sb_music_show.setProgress(current / 1000);
            try {
                sb_music_show.setMax(Playlist_Now.Medialist().get(Playlist_Now.musicID).getMaxlength());
            } catch (Exception e) {
                sb_music_show.setMax(100);
                e.printStackTrace();
            }
            if (PlayerService.player.isPlaying()) {
                iv_music_play.setImageResource(R.drawable.music_stop);
            }
            setbackgroud();
        }
    }

    void setbackgroud() {
        if ((ll_music_layout.getTag() == null) || !ll_music_layout.getTag().toString().equals(Playlist_Now.media_now_IndexBean.top_img)) {
            if (Playlist_Now.media_now_IndexBean == null) return;
            new BitmapUtils(getActivity()).display(ll_music_layout, Playlist_Now.media_now_IndexBean.top_img, new BitmapLoadCallBack<LinearLayout>() {
                @Override
                public void onLoadCompleted(LinearLayout linearLayout, String s, Bitmap srcBmp, BitmapDisplayConfig bitmapDisplayConfig, BitmapLoadFrom bitmapLoadFrom) {
                    Bitmap endbmp = srcBmp.copy(srcBmp.getConfig(), true);
                    endbmp = ImageUtils.gaosiFilter(endbmp, 50, 50);
                    endbmp = ImageUtils.maskFilter(endbmp, 102, 0, 0, 0);
                    float w_h = 0.914634146f;
                    endbmp = ImageUtils.CropImg(endbmp, w_h);
                    Drawable drawable = new BitmapDrawable(getResources(), endbmp);
                    if (Build.VERSION.SDK_INT >= 16) {
                        topimg_fragment_mediaplayer.setBackground(new BitmapDrawable(srcBmp));
                        iv_music_bg.setImageDrawable(drawable);
                        linearLayout.setBackground(drawable);
                    }
                    ll_music_layout.setTag(Playlist_Now.media_now_IndexBean.top_img);
                }

                @Override
                public void onLoadFailed(LinearLayout linearLayout, String s, Drawable drawable) {

                }
            });
        }
    }

    //自动播放下一首音频的广播接收
    private class NextReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收到下一首的广播时，初始化各个控件
            tv_music_progress.setText("00:00");
            sb_music_show.setProgress(0);
            iv_music_play.setImageResource(R.drawable.music_play);
            try {
                sb_music_show.setMax(100);
            } catch (Exception e) {
                sb_music_show.setMax(100);
            }
        }
    }

    //缓冲进度的广播接收
    private class PercentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            sb_music_show.setSecondaryProgress(intent.getIntExtra("PERCENT", 1) * sb_music_show.getMax() / 100);
        }
    }

    HttpHandler myhandler;

    void httpdownload(IndexBean bpInfoBean) {
        HttpUtils http = new HttpUtils();
        myhandler = http.download(Playlist_Now.media_now_IndexBean.media, Playlist_Now.media_now_IndexBean.media_localpath(),
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
                        imageView_fragment_media_media_download.setImageResource(R.drawable.media_downloadfinish);
                        CustomToast.showToast(getActivity(), "下载完成");
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {

                        CustomToast.showToast(getActivity(), "下载失败");
                    }
                });
    }

}
