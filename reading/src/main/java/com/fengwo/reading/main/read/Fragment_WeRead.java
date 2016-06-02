package com.fengwo.reading.main.read;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecloud.pulltozoomview.PullToZoomBase;
import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.felipecsl.gifimageview.library.GifImageView;
import com.fengwo.reading.R;
import com.fengwo.reading.activity.EditTextActivity;
import com.fengwo.reading.activity.MainActivity;
import com.fengwo.reading.activity.NextActivity;
import com.fengwo.reading.activity.UpdateActivity;
import com.fengwo.reading.common.CommonHandler;
import com.fengwo.reading.common.CustomPopupWindowDialog;
import com.fengwo.reading.main.discover.hottopics.TopicsActivity;
import com.fengwo.reading.main.group.GroupFragment;
import com.fengwo.reading.main.group.PublishFeelingsFragment;
import com.fengwo.reading.main.my.RemindFragment;
import com.fengwo.reading.main.my.WebFragment;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.player.Fragment_MediaPlayer;
import com.fengwo.reading.player.Play_Anmi;
import com.fengwo.reading.player.PlayerService;
import com.fengwo.reading.player.Playlist_Cache;
import com.fengwo.reading.player.Playlist_Now;
import com.fengwo.reading.task.config.Bean_shudan;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.ImageUtils;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.fengwo.reading.view.MyListView;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author lxq 首页
 */
public class Fragment_WeRead extends Fragment implements OnClickListener {
    private PullToZoomScrollViewEx swipeRefreshLayout;
    CommonHandler handler;

    //region 音频播放区域
    private ImageView imageView_bg;//背景图
    private ImageView imageView_goto_fragment_music;//跳转音频fragment
    private TextView textView_weread_bookname, textView_weread_target, textView_weread_book_period;
    private CurrentReceiver currentReceiver;

    private LinearLayout linearLayout_weread_playcontrol,ll_shudan;
    private ImageView imageView_weread_playerstart, imageView_weread_closecontrol;
    private GifImageView imageView_weread_playeranmi;
    private TextView textView_weread_playertext,tv_shudan;

    public Bean_shudan bean_shudan;

    void Animation_init() {

    }

    boolean Animationisplaying = false;

    void Animation_MediaPlaying(boolean playing) {
        if (playing) {
            if (Animationisplaying == false) {
                if (PlayerService.media_now_IndexBean == null) {
                    imageView_goto_fragment_music.startAnimation(Play_Anmi.getAnimation_gotofragment(getActivity()));
                    imageView_goto_fragment_music.setVisibility(View.VISIBLE);
                } else {
                    imageView_goto_fragment_music.setVisibility(View.INVISIBLE);
                }
                Animationisplaying = true;
            } else {
            }
        } else {
            imageView_goto_fragment_music.setVisibility(View.VISIBLE);
            imageView_goto_fragment_music.clearAnimation();
            Animationisplaying = false;
        }
    }

    private class CurrentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Animation_MediaPlaying(PlayerService.player.isPlaying());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        currentReceiver = new CurrentReceiver();
        IntentFilter currentFilter = new IntentFilter();
        currentFilter.addAction("CURRENT_ACTION");
        getActivity().registerReceiver(currentReceiver, currentFilter);

        refreshConfig(getActivity());
    }

    //region 通知或设置
    private RelativeLayout rl_read_setting;
    private TextView tv_read_btn;
    private TextView tv_read_content;

    //region 本周拆书包列表
    private TextView textView_weread_nowweek_book;

    private MyListView listView_nowweek_bookpacklist;
    private Adapter_weread_nowweekbookpacklist adapter_nowweekbookpacklist;

    private RelativeLayout rl_weread_more;
    private TextView textView_weread_more;

    //region 往期拆书包
    private LinearLayout linearLayout_weread_historyweek_book;

    //region 今日推荐
    private MyListView listView_todayrecommend;
    private Adapter_weread_todayrecommend adapter_todayrecommend;
    private List<IndexBean> list_todayrecommend;

    private boolean is_loading = false;

    public Intent serviceIntent;

    //region Fragment_WeRead()
    public Fragment_WeRead() {
    }

    public static Fragment_WeRead fragment = new Fragment_WeRead();

    public static Fragment_WeRead getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //region views
        handler = new CommonHandler(getActivity(), null);

        View view = inflater.inflate(R.layout.fragment_weread, container, false);
        findViewById_ScrollView(view);

        View headView = inflater.inflate(R.layout.part_weread_head, null, false);
        findViewById_Header(headView);

        View zoomView = inflater.inflate(R.layout.part_weread_zoom, null, false);
        findViewById_Zoom(zoomView);

        View contentView = inflater.inflate(R.layout.part_weread_content, null, false);
        findViewById_Content(contentView);

        bean_shudan = NOsqlUtil.getConfig_shudan();

        tv_shudan.setText(bean_shudan.title);

        swipeRefreshLayout.setHeaderView(headView);
        swipeRefreshLayout.setZoomView(zoomView);
        swipeRefreshLayout.setScrollContentView(contentView);
        onClickListener();

        swipeRefreshLayout.setOnPullZoomListener(new PullToZoomBase.OnPullZoomListener() {
            @Override
            public void onPullZooming(int newScrollValue) {
            }

            @Override
            public void onPullZoomEnd() {
                if (is_loading) {
                    return;
                }
                getData();
                Playlist_Cache.isreadfragment = true;
            }
        });
        swipeRefreshLayout.setZoomEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                jsonData = NOsqlUtil.get_Json_Index();
                if (jsonData == null) {
                    getData();
                } else {
                    setData();
                }
            }
        }, 300);

        listView_nowweek_bookpacklist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //region
                Context context = getActivity();
                if (context == null) {
                    return;
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                intent.setClass(context, NextActivity.class);
                bundle.putString("fragmentname", Fragment_Bookpack.class.getSimpleName());
                intent.putExtras(bundle);

                Playlist_Cache.isreadfragment = true;
                Fragment_Bookpack.getInstance().needSaveView = false;
                Fragment_Bookpack.getInstance().pb_id = Playlist_Cache.首页_list.get(position).pb_id;
                Fragment_Bookpack.getInstance().id = Playlist_Cache.首页_list.get(position).id;
                Fragment_Bookpack.getInstance().source = 1;
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                UMengUtils.onCountListener(getActivity(), "GD_02_02");
            }
        });
        listView_todayrecommend.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //region
                Context context = getActivity();
                if (context == null) {
                    return;
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                intent.setClass(context, NextActivity.class);
                bundle.putString("fragmentname", Fragment_Bookpack.class.getSimpleName());
                intent.putExtras(bundle);

                Playlist_Cache.isreadfragment = true;
                Fragment_Bookpack.getInstance().needSaveView = false;
                Fragment_Bookpack.getInstance().pb_id = list_todayrecommend.get(position).pb_id;
                Fragment_Bookpack.getInstance().id = list_todayrecommend.get(position).id;
                Fragment_Bookpack.getInstance().bookpackindex = position;
                Fragment_Bookpack.getInstance().source = 4;
                getActivity().startActivity(intent);

                getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                UMengUtils.onCountListener(getActivity(), "GD_02_05");
                UMengUtils.onCountListener(getActivity(), "GD_02_06");
            }
        });

        rl_read_setting.setVisibility(View.GONE);

        tv_read_btn.setTag(0);

        serviceIntent = new Intent(getActivity(), PlayerService.class);

        return view;
    }

    //region findView
    private void findViewById_ScrollView(View view) {
        swipeRefreshLayout = (PullToZoomScrollViewEx) view.findViewById(R.id.srl_read_refresh);
    }

    private void findViewById_Header(View view) {
        //region 音频播放
        imageView_goto_fragment_music = (ImageView) view.findViewById(R.id.imageView_goto_fragment_music);
        textView_weread_bookname = (TextView) view.findViewById(R.id.textView_weread_bookname);
        textView_weread_target = (TextView) view.findViewById(R.id.textView_weread_target);
        textView_weread_book_period = (TextView) view.findViewById(R.id.textView_weread_book_period);

        linearLayout_weread_playcontrol = (LinearLayout) view.findViewById(R.id.linearLayout_weread_playcontrol);
        textView_weread_playertext = (TextView) view.findViewById(R.id.textView_weread_playertext);
        imageView_weread_playerstart = (ImageView) view.findViewById(R.id.imageView_weread_playerstart);
        imageView_weread_playeranmi = (GifImageView) view.findViewById(R.id.imageView_weread_playeranmi);
        imageView_weread_playeranmi.setGifFile(R.raw.weread_playing);
        imageView_weread_closecontrol = (ImageView) view.findViewById(R.id.imageView_weread_closecontrol);
    }

    private void findViewById_Zoom(View view) {
        //region 音频播放
        imageView_bg = (ImageView) view.findViewById(R.id.rl_music_read_bg);
    }

    private void findViewById_Content(View view) {
        rl_read_setting = (RelativeLayout) view.findViewById(R.id.rl_read_setting);
        tv_read_btn = (TextView) view.findViewById(R.id.tv_read_btn);
        ll_shudan = (LinearLayout) view.findViewById(R.id.ll_shudan);
        tv_read_content = (TextView) view.findViewById(R.id.tv_read_content);
        tv_shudan = (TextView) view.findViewById(R.id.tv_shudan);
        textView_weread_nowweek_book = (TextView) view.findViewById(R.id.textView_weread_nowweek_book);
        listView_nowweek_bookpacklist = (MyListView) view.findViewById(R.id.listView_nowweek_bookpacklist);
        textView_weread_more = (TextView) view.findViewById(R.id.textView_weread_more);
        rl_weread_more = (RelativeLayout) view.findViewById(R.id.rl_weread_more);

        linearLayout_weread_historyweek_book = (LinearLayout) view.findViewById(R.id.linearLayout_weread_historyweek_book);

        listView_todayrecommend = (MyListView) view.findViewById(R.id.listView_todayrecommend);
    }

    //region 显示 最新拆书包的背景图
    boolean first_showed = false;


    private Drawable drawable_bg;

    private void firstshow() {
        if (first_showed) return;
        BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
        if (Playlist_Cache.首页_list.size() > 0)

            bitmapUtils.display(imageView_bg, Playlist_Cache.首页_list.get(0).top_img, new BitmapLoadCallBack<ImageView>() {
                @Override
                public void onLoadCompleted(ImageView relativeLayout, String s, Bitmap bitmap, BitmapDisplayConfig bitmapDisplayConfig, BitmapLoadFrom bitmapLoadFrom) {
                    //蒙板 背景
                    Bitmap mbitmap = ImageUtils.maskFilter(bitmap, 127, 0, 0, 0);
                    drawable_bg = new BitmapDrawable(getResources(), mbitmap);
                    if (Build.VERSION.SDK_INT >= 16) {
                        relativeLayout.setImageDrawable(drawable_bg);
                    }

                    first_showed = true;
                }

                @Override
                public void onLoadFailed(ImageView relativeLayout, String s, Drawable drawable) {

                }
            });
    }

    public void refreshConfig(Context context) {
        rl_read_setting.setVisibility(View.GONE);
        if (context == null || ((Activity) context).isFinishing()) {
            return;
        }
        if (TextUtils.isEmpty(NOsqlUtil.get_naoling().is_config)) {
            return;
        }
        if ("".equals(NOsqlUtil.get_naoling().is_config) || "0".equals(NOsqlUtil.get_naoling().is_config)) {
            //闹铃未设置
            rl_read_setting.setVisibility(View.VISIBLE);
            // TODO
            tv_read_btn.setText("立即设置");
            tv_read_content.setText("请先去设置闹铃");
            tv_read_btn.setTag(1);
        } else {
            //闹铃已设置
            if (SPUtils.getAppConfig_act() == null) {
                rl_read_setting.setVisibility(View.GONE);
            } else {
                rl_read_setting.setVisibility(View.VISIBLE);
                tv_read_btn.setText(SPUtils.getAppConfig_act().button);
                tv_read_content
                        .setText(SPUtils.getAppConfig_act().title);
                //topic 话题
                if ("topic"
                        .equals(SPUtils.getAppConfig_act().action_type)) {
                    tv_read_btn.setTag(2);
                    return;
                }
                //alert 弹窗
                if ("alert"
                        .equals(SPUtils.getAppConfig_act().action_type)) {
                    tv_read_btn.setTag(3);
                    return;
                }
                //act_note 提示写笔记
                if ("act_note"
                        .equals(SPUtils.getAppConfig_act().action_type)) {
                    tv_read_btn.setTag(4);
                    return;
                }
            }
        }
    }

    //region click
    private void onClickListener() {
        imageView_goto_fragment_music.setOnClickListener(this);
        linearLayout_weread_playcontrol.setOnClickListener(this);
        textView_weread_playertext.setOnClickListener(this);
        imageView_weread_closecontrol.setOnClickListener(this);
        imageView_weread_playerstart.setOnClickListener(this);

        rl_read_setting.setOnClickListener(this);
        tv_read_btn.setOnClickListener(this);
        rl_weread_more.setOnClickListener(this);

        ll_shudan.setOnClickListener(this);

        linearLayout_weread_historyweek_book.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Context context = getActivity();
        if (context == null) {
            return;
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.textView_weread_playertext:
            case R.id.linearLayout_weread_playcontrol:
            case R.id.imageView_goto_fragment_music:
                //region 跳转音频播放器
                UMengUtils.onCountListener(getActivity(), "GD_02_07");
                if (Playlist_Now.Medialist().size() == 0) {
                    return;
                }
                if (PlayerService.media_now_IndexBean != null) {
                    return;
                }
                intent.setClass(context, NextActivity.class);
                bundle.putString("fragmentname", Fragment_MediaPlayer.class.getSimpleName());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                //endregion
                break;
            case R.id.imageView_weread_closecontrol:
                if (PlayerService.player.isPlaying()) {
                    serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
                    getActivity().startService(serviceIntent);
                }
                linearLayout_weread_playcontrol.setVisibility(View.INVISIBLE);
                break;
            case R.id.imageView_weread_playerstart:
                //region

                if (PlayerService.player.isPlaying()) {
                    imageView_weread_playerstart.setImageResource(R.drawable.play);
                    imageView_weread_playeranmi.stopAnimation();
                } else {
                    imageView_weread_playerstart.setImageResource(R.drawable.stop);
                    imageView_weread_playeranmi.startAnimation();
                }
                serviceIntent.putExtra("MUSIC", PlayerService.PAUSE);
                getActivity().startService(serviceIntent);

                break;
            case R.id.rl_read_setting:
            case R.id.tv_read_btn:
                //region 设置
                switch ((Integer) tv_read_btn.getTag()) {
                    case 0:
                        break;
                    case 1:
                        //闹铃
                        intent.setClass(context, NextActivity.class);
                        bundle.putString("fragmentname", RemindFragment.class.getSimpleName());
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.in_from_bottom,
                                R.anim.out_to_top);
                        RemindFragment.getInstance().type = 1;
                        RemindFragment.getInstance().needSaveView = false;
                        break;
                    case 2:
                        UMengUtils.onCountListener(getActivity(), "shouye_huati");
                        //话题
                        intent.setClass(getActivity(), TopicsActivity.class);
                        bundle.putString("name", "#" + SPUtils
                                .getAppConfig_act().title + "#");
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.in_from_right,
                                R.anim.out_to_left);
                        break;
                    case 3:
                        //弹窗
                        new CustomPopupWindowDialog(context,
                                SPUtils.getAppConfig_act().img,
                                SPUtils.getAppConfig_act().href).show();
                        break;
                    case 4:
                        UMengUtils.onCountListener(getActivity(), "shouye_suibi");
                        //笔记
                        intent.setClass(context, EditTextActivity.class);
                        bundle.putInt("key", 1);
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);

                        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        PublishFeelingsFragment.getInstance().source = 1;
                        PublishFeelingsFragment.getInstance().needSaveView = false;
                        break;

                    default:
                        break;
                }
                break;
            case R.id.rl_weread_more:
                //region 查看更多
                UMengUtils.onCountListener(getActivity(), "GD_02_03");
                if (Playlist_Cache.首页_list == null || Playlist_Cache.首页_list.size() == 0) {
                    return;
                }
                if (!"".equals(textView_weread_more.getText())) {
                    adapter_nowweekbookpacklist = new Adapter_weread_nowweekbookpacklist(fragment, Playlist_Cache.首页_list);
                    listView_nowweek_bookpacklist.setAdapter(adapter_nowweekbookpacklist);
                    textView_weread_more.setText("");
                    Drawable drawable = getResources().getDrawable(R.drawable.jiantou_shang);
                    drawable.setBounds(0, 0, 40, 40);
                    textView_weread_more.setCompoundDrawables(null, null, drawable, null);
                } else {
                    adapter_nowweekbookpacklist = new Adapter_weread_nowweekbookpacklist(fragment, Playlist_Cache.首页_list.subList(0, 3));
                    listView_nowweek_bookpacklist.setAdapter(adapter_nowweekbookpacklist);
                    textView_weread_more.setText("查看更多");
                    Drawable drawable = getResources().getDrawable(R.drawable.jiantou_bottom);
                    drawable.setBounds(0, 0, 40, 40);
                    textView_weread_more.setCompoundDrawables(null, null, drawable, null);
                }
                break;
            case R.id.linearLayout_weread_historyweek_book:
                //region 挑转 往期拆书包fragment
                UMengUtils.onCountListener(getActivity(), "GD_02_04");
                if (Playlist_Now.Medialist().size() == 0) {
                    return;
                }
                intent.setClass(context, NextActivity.class);
                bundle.putString("fragmentname", Fragment_BookList.class.getSimpleName());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case R.id.ll_shudan:
                //跳转H5页面
                /*Intent intent1 = new Intent(getActivity(), NextActivity.class);
                Bundle bundle1 = new Bundle();
                bundle.putString("fragmentname", WebFragment.class.getSimpleName());
                WebFragment.getInstance().needSaveView = false;
                WebFragment.getInstance().source = 1;
                WebFragment.getInstance().url = bean_shudan.href;
                intent1.putExtras(bundle1);
                getActivity().startActivity(intent1);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);*/
                intent.setClass(getActivity(), NextActivity.class);
                bundle.putString("fragmentname", WebFragment.class.getSimpleName());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                WebFragment.getInstance().needSaveView = false;
                WebFragment.getInstance().url = bean_shudan.href;
                WebFragment.getInstance().source = 1;

                break;
            default:
                break;
        }
    }

    private void getData() {
        is_loading = false;

        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.index,
                new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        handler.sendEmptyMessage(CommonHandler.MSG_START);
                    }

                    @Override
                    public void onFailure(HttpException arg0, String error) {
                        Context context = getActivity();
                        if (context != null && !((Activity) context).isFinishing()) {
                            CustomToast.showToast(context, context.getString(R.string.network_check));
                        }
                        is_loading = false;
                        handler.sendEmptyMessage(CommonHandler.MSG_ERROR);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {

                        String jsonString = responseInfo.result;
                        MLog.v("reading", "首页:\n" + jsonString);
                        try {
                            Json_Index js = new Gson().fromJson(jsonString, Json_Index.class);
                            if ("1".equals(js.code)) {
                                jsonData = js;
                                NOsqlUtil.set_Json_Index(js);//缓存本地
                                setData();
                            } else if ("999".equals(js.code)) {
                                //强制更新
                                startActivity(new Intent(getActivity(), UpdateActivity.class));
                            } else {
                                Context context = getActivity();
                                if (context != null && !((Activity) context).isFinishing()) {
                                    CustomToast.showToast(context, js.msg);
                                }
                            }
                        } catch (Exception e) {
                            Context context = getActivity();
                            if (context != null && !((Activity) context).isFinishing()) {
                                CustomToast.showToast(context, context.getString(R.string.json_error));
                            }
                        }
                        handler.sendEmptyMessage(CommonHandler.MSG_OK);
                    }
                }, true, null);
    }

    Json_Index jsonData;

    private void setData() {
        //region 计算 已签到的bookpack
        if (jsonData.check != null) {
            MLog.v("reading", " 签到数据;" + jsonData.check.size());
            for (int i = 0; i < jsonData.check.size(); i++) {
                for (int j = 0; j < jsonData.bp_list.size(); j++) {
                    if (jsonData.check.get(i).equals(jsonData.bp_list.get(j).id)) {
                        jsonData.bp_list.get(j).readornot = "1";
                        MLog.v("reading", "已签到" + j);
                    }
                }
            }
        }

        textView_weread_more.setText("查看更多");

        //region 书的信息
        if (jsonData.book_data != null) {
            textView_weread_nowweek_book.setText("本周共读《" + jsonData.book_data.book_title + "》");

            if (jsonData.target.contains("#")) {
                String str1 = jsonData.target.substring(0, jsonData.target.indexOf("#"));
                String str2 = jsonData.target.substring(jsonData.target.indexOf("#") + 1, jsonData.target.length());
                textView_weread_target.setText(str1 + "\n" + str2);
            } else {
                textView_weread_target.setText(jsonData.target);
            }


            textView_weread_bookname.setText("《" + jsonData.book_data.book_title + "》");
            textView_weread_book_period.setText("一\t有书共读\t" + jsonData.book_data.qi + "\t一");
            if (GlobalParams.isOne) {
                GroupFragment.getInstance().book_id = jsonData.book_data.book_id;
                GroupFragment.getInstance().book_title = jsonData.book_data.book_title;
                GlobalParams.isOne = false;
            }
        } else {
            if (GlobalParams.isOne) {
                GroupFragment.getInstance().book_id = "0";
                GroupFragment.getInstance().book_title = "所有图书";
                GlobalParams.isOne = false;
            }
        }
        //endregion

        //region 本周拆书包list
        Playlist_Cache.首页_list = new ArrayList<>();
        Playlist_Cache.首页_list.addAll(jsonData.bp_list);
        if (Playlist_Cache.首页_list.size() > 3) {
            adapter_nowweekbookpacklist = new Adapter_weread_nowweekbookpacklist(fragment, Playlist_Cache.首页_list.subList(0, 3));
        } else {
            textView_weread_more.setVisibility(View.GONE);
            adapter_nowweekbookpacklist = new Adapter_weread_nowweekbookpacklist(fragment, Playlist_Cache.首页_list);
        }

        listView_nowweek_bookpacklist.setAdapter(adapter_nowweekbookpacklist);
        Drawable drawable = getResources().getDrawable(R.drawable.jiantou_bottom);
        drawable.setBounds(0, 0, 40, 40);
        textView_weread_more.setCompoundDrawables(null, null, drawable, null);
        //endregion

        //region 今日推荐
        list_todayrecommend = new ArrayList<>();
        list_todayrecommend.addAll(jsonData.today_recom_book);
        adapter_todayrecommend = new Adapter_weread_todayrecommend(fragment, list_todayrecommend);
        listView_todayrecommend.setAdapter(adapter_todayrecommend);
        //endregion
        firstshow();
        swipeRefreshLayout.setZoomEnabled(true);

        is_loading = false;
    }

    //拆书包详情签到成功后刷新方法
    public void qiandaoOK_refresh(int position) {
        Playlist_Cache.首页_list.get(position).readornot = "1";
        adapter_nowweekbookpacklist.notifyDataSetChanged();
    }


    //拆书包详情签到成功后刷新方法
    public void refresh_playeranmi() {
        imageView_weread_playeranmi.startAnimation();
        try {
            if (PlayerService.media_now_IndexBean != null) {
                IndexBean indexBean = new IndexBean();
                indexBean.title = PlayerService.media_now_IndexBean.title;
                indexBean.time_type = PlayerService.media_now_IndexBean.time_type;
                indexBean.pub_time = PlayerService.media_now_IndexBean.pub_time;
                setTime(textView_weread_playertext, indexBean);
            } else {
                IndexBean indexBean = new IndexBean();
                indexBean.title = Playlist_Now.media_now_IndexBean.title;
                indexBean.time_type = Playlist_Now.media_now_IndexBean.time_type;
                indexBean.pub_time = Playlist_Now.media_now_IndexBean.pub_time;
                setTime(textView_weread_playertext, indexBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        super.onResume();

        Animation_init();
        if (PlayerService.player != null && PlayerService.player.isPlaying() == true) {
            linearLayout_weread_playcontrol.setVisibility(View.VISIBLE);
            if (PlayerService.player.isPlaying()) {
                imageView_weread_playerstart.setImageResource(R.drawable.stop);
                refresh_playeranmi();

            } else {
                imageView_weread_playerstart.setImageResource(R.drawable.play);
                imageView_weread_playeranmi.stopAnimation();
            }
        } else {
            linearLayout_weread_playcontrol.setVisibility(View.INVISIBLE);
        }
        MobclickAgent.onPageStart("ReadFragment");
        MainActivity.isRead = true;
        Context context = getActivity();
        if (context != null) {
            if (SPUtils.getAppTimeFirst()) {

                new CustomPopupWindowDialog(context,
                        SPUtils.getAppConfig_act().img,
                        SPUtils.getAppConfig_act().href).show();
            }
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ReadFragment");
        MainActivity.isRead = false;
    }

    private void setTime(TextView textView_week, IndexBean indexBean) {

        textView_week.setText("周" + indexBean.getPub_time_week() + indexBean.timetype_tostring() + " " + indexBean.title);

    }
}
