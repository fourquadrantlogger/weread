package com.fengwo.reading.main.read;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.common.CommonHandler;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.player.Fragment_MediaPlayer;
import com.fengwo.reading.player.Play_Anmi;
import com.fengwo.reading.player.PlayerService;
import com.fengwo.reading.player.Playlist_Cache;
import com.fengwo.reading.player.Playlist_Now;
import com.fengwo.reading.task.Task_download_all_bookpack;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.ImageUtils;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.utils.MyDialog;
import com.fengwo.reading.utils.NetUtil;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.fengwo.reading.view.RoundProgressBar;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * LS 领读合集
 */
public class Fragment_BookInfoWithPacks extends Fragment implements OnClickListener {
    private CommonHandler handler;
    //region 顶栏
    private ImageView iv_return;
    private ImageView iv_goto_fragment_mediaplayer;

    //region bookinfo
    private ImageView iv_readbooklist_bookinfo_fengmian;
    private TextView tv_readbooklist_bookinfo_bookname, tv_readbooklist_bookinfo_qi, tv_readbooklist_bookinfo_author, tv_readbooklist_bookinfo_wereadtime;
    private RelativeLayout relativeLayout_bookinfowithpacks_play;
    private TextView textView_savetolocal;
    private ImageView ImageView_bookinfowithpacks_savetolocal;
    private RoundProgressBar progress_bookinfowithpacks;



    private ListView listView;
    private Adapter_BookInfoWithPacks adapter;
    public String pb_id;
    private Json_BookInfoWithPacks jsonData;

    public Fragment_BookInfoWithPacks() {
    }

    public static Fragment_BookInfoWithPacks fragment = new Fragment_BookInfoWithPacks();

    public static Fragment_BookInfoWithPacks getInstance() {
        return fragment;
    }

    private View saveView = null;
    public boolean needSaveView = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ((saveView != null) && needSaveView)
            return saveView;

        View view = inflater.inflate(R.layout.fragment_readbooklist, container, false);
        handler = new CommonHandler(getActivity(), null);

        findViewById(view);
        // 添加头部
        addHeaderView();

        adapter = new Adapter_BookInfoWithPacks(fragment, Playlist_Cache.往期某书_list);
        listView.setAdapter(adapter);

        // 延时请求网络
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                jsonData = NOsqlUtil.get_BookInfoWithPacks(pb_id);
                if (jsonData == null) {
                    getData();
                } else {
                    setData();
                }
            }
        }, 300);


        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= 1) {
                    Playlist_Cache.isreadfragment = false;

                    Fragment_Bookpack.getInstance().needSaveView = false;
                    Fragment_Bookpack.getInstance().pb_id = jsonData.book_data.id;
                    Fragment_Bookpack.getInstance().id = Playlist_Cache.往期某书_list.get(i - 1).id;
                    Fragment_Bookpack.getInstance().bookpackindex = i - 1;
                    Fragment_Bookpack.getInstance().source = 2;

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left,
                            R.anim.out_to_right);
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.ll_activity_next, Fragment_Bookpack.getInstance());
                    transaction.commit();
                }
            }
        });

        return view;
    }

    private void findViewById(View view) {

        iv_return = (ImageView) view.findViewById(R.id.iv_return);
        iv_goto_fragment_mediaplayer = (ImageView) view.findViewById(R.id.iv_goto_fragment_mediaplayer);
        listView = (ListView) view.findViewById(R.id.lv_readbooklist);

        iv_return.setOnClickListener(this);
        iv_goto_fragment_mediaplayer.setOnClickListener(this);
    }


    private void addHeaderView() {
        View readbooklist_header = LayoutInflater.from(getActivity()).inflate(R.layout.part_readbooklist_header, null);
        iv_readbooklist_bookinfo_fengmian = (ImageView) readbooklist_header.findViewById(R.id.iv_readbooklist_bookinfo_fengmian);
        tv_readbooklist_bookinfo_bookname = (TextView) readbooklist_header.findViewById(R.id.tv_readbooklist_bookinfo_bookname);
        tv_readbooklist_bookinfo_qi = (TextView) readbooklist_header.findViewById(R.id.tv_readbooklist_bookinfo_qi);
        tv_readbooklist_bookinfo_author = (TextView) readbooklist_header.findViewById(R.id.tv_readbooklist_bookinfo_author);
        tv_readbooklist_bookinfo_wereadtime = (TextView) readbooklist_header.findViewById(R.id.tv_readbooklist_bookinfo_wereadtime);
        // 添加头部
        textView_savetolocal = (TextView) readbooklist_header.findViewById(R.id.textView_savetolocal);

        ImageView_bookinfowithpacks_savetolocal = (ImageView) readbooklist_header.findViewById(R.id.ImageView_bookinfowithpacks_savetolocal);
        progress_bookinfowithpacks = (RoundProgressBar) readbooklist_header.findViewById(R.id.progress_bookinfowithpacks);
        relativeLayout_bookinfowithpacks_play = (RelativeLayout) readbooklist_header.findViewById(R.id.relativeLayout_bookinfowithpacks_play);

        relativeLayout_bookinfowithpacks_play.setOnClickListener(this);
        ImageView_bookinfowithpacks_savetolocal.setOnClickListener(this);
        textView_savetolocal.setOnClickListener(this);
        iv_goto_fragment_mediaplayer.setOnClickListener(this);
        listView.addHeaderView(readbooklist_header);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.relativeLayout_bookinfowithpacks_play:
                //播放领读
                UMengUtils.onCountListener(getActivity(), "GD_02_04_03_01");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (PlayerService.player == null || Playlist_Now.musicID != 0) {
                            Playlist_Cache.isreadfragment = false;
                            Playlist_Now.musicID = 0;
                            Intent intent = new Intent(getActivity(), PlayerService.class);
                            intent.putExtra("MusicID", Playlist_Now.musicID);
                            intent.putExtra("MUSIC", PlayerService.PLAY);
                            getActivity().startService(intent);
                        }
                    }
                }).start();

                if (true) {
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.ll_activity_next, Fragment_MediaPlayer.getInstance());
                    transaction.commit();
                }
                break;
            case R.id.iv_goto_fragment_mediaplayer:
                //右上  音频播放器
                UMengUtils.onCountListener(getActivity(), "GD_02_04_03_03");
                Playlist_Cache.isreadfragment = false;
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next, Fragment_MediaPlayer.getInstance());
                transaction.commit();
                break;
            case R.id.textView_savetolocal:
                //region 下载所有拆书包
                UMengUtils.onCountListener(getActivity(), "GD_02_04_03_02");
                CustomToast.showToast(getActivity(), "开始下载" + jsonData.book_data.book_title + "的所有领读包");
                ImageView_bookinfowithpacks_savetolocal.setImageResource(R.drawable.bookinfowithpacks_ing);
                new Task_download_all_bookpack(pb_id,getActivity());
                //下载时有无网络进行提示
                if (!NetUtil.checkNet(getActivity())) {
                    Toast.makeText(getActivity(), "当前没有网络，请检查网络连接！", Toast.LENGTH_SHORT).show();
                } else {
                    if (NetUtil.isWifiConnection(getActivity()) || SPUtils.getXiaZai()) {
                        //region 下载所有拆书包
                        CustomToast.showToast(getActivity(), "开始下载" + jsonData.book_data.book_title + "的所有领读包");
                        ImageView_bookinfowithpacks_savetolocal.setImageResource(R.drawable.bookinfowithpacks_ing);
                        new Task_download_all_bookpack(pb_id,getActivity());
                        //endregion
                    } else {
                        MyDialog dialog = new MyDialog(getActivity(), R.style.loading_dialog, "下载开关才能下载", "开启2G/3G/4G网络下载开关", new MyDialog.MyDialogInterfaceListener() {
                            @Override
                            public void callBack() {
                                SPUtils.setXiaZai(true);
                                //region 下载所有拆书包
                                CustomToast.showToast(getActivity(), "开始下载" + jsonData.book_data.book_title + "的所有领读包");
                                ImageView_bookinfowithpacks_savetolocal.setImageResource(R.drawable.bookinfowithpacks_ing);
                                new Task_download_all_bookpack(pb_id,getActivity());
                                //endregion
                            }
                        });
                        dialog.show();
                    }

                }
                break;
            default:
                break;
        }
    }

    /**
     * 请求网络 - 查看拆书包
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("pb_id", pb_id);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.book_pack, new RequestCallBack<String>() {

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
                    public void onFailure(HttpException arg0, String arg1) {
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
                            Json_BookInfoWithPacks js = new Gson().fromJson(jsonString, Json_BookInfoWithPacks.class);
                            if ("1".equals(js.code)) {
                                jsonData = js;
                                NOsqlUtil.set_BookInfoWithPacks(js); //直接存储bpinfobean到本地
                                setData();
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
    }

    private void setData() {

        if (!TextUtils.isEmpty(jsonData.book_data.qi) && !TextUtils.isEmpty(jsonData.book_data.author)) {
            tv_readbooklist_bookinfo_qi.setText(jsonData.book_data.qi);
            tv_readbooklist_bookinfo_author.setText(jsonData.book_data.author + "\t著");
        }
        if (jsonData.mediaalreadycount() == jsonData.data_allhavemedia().size()) {
            ImageView_bookinfowithpacks_savetolocal.setImageResource(R.drawable.bookinfowithpacks_finish);

        } else {
            ImageView_bookinfowithpacks_savetolocal.setImageResource(R.drawable.bookinfowithpacks_download);

        }
        if (!jsonData.book_data.book_cover_exist()) {
            DisplayImageUtils.displayImage(jsonData.book_data.book_cover, iv_readbooklist_bookinfo_fengmian, 0, R.drawable.zanwufengmian);
            HttpUtils http = new HttpUtils();
            http.download(jsonData.book_data.book_cover, jsonData.book_data.book_cover_local(), new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    MLog.v("reading","book cover:"+jsonData.book_data.book_cover_local());
                }

                @Override
                public void onFailure(HttpException e, String s) {

                }
            });
        }
        else
            iv_readbooklist_bookinfo_fengmian.setImageBitmap(ImageUtils.getLoacalBitmap(jsonData.book_data.book_cover_local()));

        tv_readbooklist_bookinfo_bookname.setText(jsonData.book_data.book_title);
        tv_readbooklist_bookinfo_wereadtime.setText("共读时间:" + jsonData.book_data.start_time);
        Playlist_Cache.往期某书_list.clear();
        if (jsonData.data == null || jsonData.data.size() == 0) {
            // 没有数据

        } else {
            // 如果在 2个 拆书包详情 之间互相跳转，
            Playlist_Cache.往期某书_list.addAll(jsonData.data);
            if (jsonData.check != null && jsonData.check.size() != 0) {
                for (int i = 0; i < jsonData.data.size(); i++) {
                    for (int j = 0; j < jsonData.check.size(); j++) {
                        if (jsonData.data.get(i).id.equals(jsonData.check.get(j))) {
                            jsonData.data.get(i).readornot = "1";
                        }
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    //拆书包详情签到成功后刷新方法
    public void refresh(int position) {
        Playlist_Cache.往期某书_list.get(position).readornot = "1";
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("Fragment_BookInfoWithPacks");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Fragment_BookInfoWithPacks");
    }

    private CurrentReceiver currentReceiver;
    private LixianReceiver lixianReceiver;

    @Override
    public void onStart() {
        super.onStart();
        currentReceiver = new CurrentReceiver();
        IntentFilter currentFilter = new IntentFilter();
        currentFilter.addAction("CURRENT_ACTION");
        getActivity().registerReceiver(currentReceiver, currentFilter);

        lixianReceiver = new LixianReceiver();
        IntentFilter lixianFilter = new IntentFilter();
        lixianFilter.addAction("DownloadAllPack");
        getActivity().registerReceiver(lixianReceiver, lixianFilter);

    }

    public class CurrentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (PlayerService.player != null && PlayerService.player.isPlaying() == true) {
                if (iv_goto_fragment_mediaplayer.getTag() == null || !(boolean) iv_goto_fragment_mediaplayer.getTag()) {
                    iv_goto_fragment_mediaplayer.startAnimation(Play_Anmi.getAnimation_gotofragment(getActivity()));
                    iv_goto_fragment_mediaplayer.setTag(true);
                }
            } else {
                iv_goto_fragment_mediaplayer.clearAnimation();
                iv_goto_fragment_mediaplayer.setTag(false);
            }
        }
    }

    public class LixianReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String pb_id = intent.getStringExtra("pb_id");
            int count = intent.getIntExtra("PackCount", -1);
            int now_index = intent.getIntExtra("now_index", -1);

            if (pb_id == null || pb_id.equals("") || count == -1 || now_index == -1)
                return;

            progress_bookinfowithpacks.setMax(count);
            progress_bookinfowithpacks.setProgress(now_index);
            if (count != now_index) {
                ImageView_bookinfowithpacks_savetolocal.setImageResource(R.drawable.bookinfowithpacks_ing);
            } else {

                ImageView_bookinfowithpacks_savetolocal.setImageResource(R.drawable.bookinfowithpacks_finish);
                progress_bookinfowithpacks.setVisibility(View.GONE);
                getActivity().unregisterReceiver(lixianReceiver);
            }
        }
    }
}