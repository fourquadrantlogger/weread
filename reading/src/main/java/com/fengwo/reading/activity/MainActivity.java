package com.fengwo.reading.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.main.discover.DiscoverFragment;
import com.fengwo.reading.main.group.GroupFragment;
import com.fengwo.reading.main.my.Fragment_My;
import com.fengwo.reading.main.read.Fragment_WeRead;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.player.PlayerService;
import com.fengwo.reading.task.Task_updateconfig;
import com.fengwo.reading.task.Task_updateuserinfo;
import com.fengwo.reading.task.Task_updatexunzhang;
import com.fengwo.reading.utils.ActivityUtil;
import com.fengwo.reading.utils.MySQLiteOpenHelper;
import com.fengwo.reading.utils.NetUtil;
import com.fengwo.reading.utils.NetworkUtils;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;


public class MainActivity extends BaseActivity implements OnClickListener {

    public static MainActivity Activity;

    private LinearLayout ll_main_dh1, ll_main_dh2, ll_main_dh3, ll_main_dh4;
    private RadioButton rb_main_dh1, rb_main_dh2, rb_main_dh3, rb_main_dh4;
    private TextView tv_main_dh1, tv_main_dh2, tv_main_dh3, tv_main_dh4;

    private RelativeLayout rl_main_tuceng1, rl_main_tuceng2, rl_main_tuceng3;

    private Fragment[] fragments;
    private int index = 0;
    private RadioButton[] radioButtons;
    private TextView[] textViews;
    private int[] colors;

    private boolean first;
    public String third_id;

    public static boolean isRead = false;

    public static String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        version = getPackageInfo(this).versionName;
        ActivityUtil.mainActivity = this;
        Activity = this;

        makeCom();

        //恢复默认设置
        UmengUpdateAgent.setDefault();
        // 设置更新模式，默认true为增量更新，设为false则为全量更新。
        UmengUpdateAgent.setDeltaUpdate(false);
        // 任意网络环境下都进行更新自动提醒
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        // 开启了集成检测功能(Toast问题出在那里),功能正常则false禁止掉
        UmengUpdateAgent.setUpdateCheckConfig(false);
        // 是否有网络
        if (NetworkUtils.isNetworkConnected(this)) {
            switch (NetUtil.getNetworkType(this)) {
                case "wifi":
                case "3G":
                case "4G":
                    // 对应网络,友盟进行自动更新检测
                    UmengUpdateAgent.update(this);
                    break;
                default:
                    break;
            }
        }

        // SPUtils.getUserId(this);
        // SPUtils.getUserInfo(this);

        // 设置下一个有效的闹铃
        // List<Alarm> list = AlarmHandle.getAlarms(this);
        // if (list != null && list.size() != 0) {
        // AlarmClockManager.setNextAlarm(this);
        // }

        findViewById(null);
        onClickListener();

        fragments = new Fragment[]{Fragment_WeRead.getInstance(), GroupFragment.getInstance()
                , DiscoverFragment.getInstance(), Fragment_My.getInstance()};
        radioButtons = new RadioButton[]{rb_main_dh1, rb_main_dh2,
                rb_main_dh3, rb_main_dh4};
        textViews = new TextView[]{tv_main_dh1, tv_main_dh2, tv_main_dh3, tv_main_dh4};

        colors = new int[]{getResources().getColor(R.color.green_17),
                getResources().getColor(R.color.text_98)};

        index = 0;
        radioButtons[index].setChecked(true);
        textViews[index].setTextColor(colors[0]);

        first = true;
        isEnabled = true;
        handler.sendEmptyMessageDelayed(0, 1000);

        if (SPUtils.getAppFirst1(MainActivity.this)) {
//            rl_main_tuceng1.setVisibility(View.VISIBLE);
        }
    }

    private void makeCom() {
        if (SPUtils.getcom(this)) {
            MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();
            db.execSQL("drop table tb_notify");
            db.execSQL("CREATE TABLE tb_notify(_id INTEGER PRIMARY KEY AUTOINCREMENT ,uid text not null ,id text not null ,notify_user_id text not null ,source text not null ,type text not null ,name text not null ,avatar text not null ,sex text not null ,right text not null ,content text not null ,create_time text not null ,is_read text not null)");
            db.close();
            SPUtils.setcom(this, false);
        }
    }

    boolean app_todayfirststart_afteruserlogin = true;

    @Override
    protected void onStart() {
        super.onStart();
        if (TextUtils.isEmpty(GlobalParams.uid)||GlobalParams.uid.equals("1")||GlobalParams.userInfoBean == null){
                GlobalParams.uid = SPUtils.getUserId(MainActivity.this);
                GlobalParams.userInfoBean = NOsqlUtil.get_userInfoBean();
                SPUtils.getUMeng(MainActivity.this);
                third_id = SPUtils.getThirdId(MainActivity.this);
            }
        //config 接口存储数据
        if (app_todayfirststart_afteruserlogin) {
            new Task_updatexunzhang();
            new Task_updateuserinfo();
            new Task_updateconfig();
            app_todayfirststart_afteruserlogin = false;
        }
        if (first) {
            first = false;
            getSupportFragmentManager().beginTransaction().add(R.id.ll_main_layout, fragments[index]).show(fragments[index]).commit();
        }
    }

    private void findViewById(View view) {
        rb_main_dh1 = (RadioButton) findViewById(R.id.rb_main_dh1);
        rb_main_dh2 = (RadioButton) findViewById(R.id.rb_main_dh2);
        rb_main_dh3 = (RadioButton) findViewById(R.id.rb_main_dh3);
        rb_main_dh4 = (RadioButton) findViewById(R.id.rb_main_dh4);

        ll_main_dh1 = (LinearLayout) findViewById(R.id.ll_main_dh1);
        ll_main_dh2 = (LinearLayout) findViewById(R.id.ll_main_dh2);
        ll_main_dh3 = (LinearLayout) findViewById(R.id.ll_main_dh3);
        ll_main_dh4 = (LinearLayout) findViewById(R.id.ll_main_dh4);

        tv_main_dh1 = (TextView) findViewById(R.id.tv_main_dh1);
        tv_main_dh2 = (TextView) findViewById(R.id.tv_main_dh2);
        tv_main_dh3 = (TextView) findViewById(R.id.tv_main_dh3);
        tv_main_dh4 = (TextView) findViewById(R.id.tv_main_dh4);

        rl_main_tuceng1 = (RelativeLayout) findViewById(R.id.rl_main_tuceng1);
        rl_main_tuceng2 = (RelativeLayout) findViewById(R.id.rl_main_tuceng2);
        rl_main_tuceng3 = (RelativeLayout) findViewById(R.id.rl_main_tuceng3);
    }

    private void onClickListener() {
        ll_main_dh1.setOnClickListener(this);
        ll_main_dh2.setOnClickListener(this);
        ll_main_dh3.setOnClickListener(this);
        ll_main_dh4.setOnClickListener(this);
        rb_main_dh1.setOnClickListener(this);
        rb_main_dh2.setOnClickListener(this);
        rb_main_dh3.setOnClickListener(this);
        rb_main_dh4.setOnClickListener(this);
        rb_main_dh1.setOnClickListener(this);
        rb_main_dh2.setOnClickListener(this);
        rb_main_dh3.setOnClickListener(this);
        rl_main_tuceng1.setOnClickListener(this);
        rl_main_tuceng2.setOnClickListener(this);
        rl_main_tuceng3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_main_dh1:
            case R.id.ll_main_dh1:
                //共读(首页)
                UMengUtils.onCountListener(this, "GD_02");
                isRead = true;
                try {
                    if (index != 0) {
                        FragmentTransaction transaction = getSupportFragmentManager()
                                .beginTransaction();
                        transaction.hide(fragments[index]);
                        radioButtons[index].setChecked(false);
                        textViews[index].setTextColor(colors[1]);
                        if (!fragments[0].isAdded()) {
                            transaction.add(R.id.ll_main_layout, fragments[0]);
                        }
                        transaction.show(fragments[0]).commit();
                        index = 0;
                        radioButtons[index].setChecked(true);
                        textViews[index].setTextColor(colors[0]);
                    }
                } catch (Exception e) {
                }
                Fragment_My.getInstance().is_notify = false;
                break;
            case R.id.rb_main_dh2:
            case R.id.ll_main_dh2:
                //有书圈
                UMengUtils.onCountListener(this, "GD_03");
                isRead = false;
                if (index != 1) {
                    FragmentTransaction transaction = getSupportFragmentManager()
                            .beginTransaction();
                    transaction.hide(fragments[index]);
                    radioButtons[index].setChecked(false);
                    textViews[index].setTextColor(colors[1]);
                    if (!fragments[1].isAdded()) {
                        transaction.add(R.id.ll_main_layout, fragments[1]);
                    }
                    transaction.show(fragments[1]).commit();
                    index = 1;
                    radioButtons[index].setChecked(true);
                    textViews[index].setTextColor(colors[0]);
                }
                if (SPUtils.getAppFirst2(MainActivity.this)) {
                    rl_main_tuceng2.setVisibility(View.VISIBLE);
                }
                Fragment_My.getInstance().is_notify = false;
                break;
            case R.id.rb_main_dh3:
            case R.id.ll_main_dh3:
                //发现
                UMengUtils.onCountListener(this, "GD_04");
                isRead = false;
                if (index != 2) {
                    FragmentTransaction transaction = getSupportFragmentManager()
                            .beginTransaction();
                    transaction.hide(fragments[index]);
                    radioButtons[index].setChecked(false);
                    textViews[index].setTextColor(colors[1]);
                    if (!fragments[2].isAdded()) {
                        transaction.add(R.id.ll_main_layout, fragments[2]);
                    }
                    transaction.show(fragments[2]).commit();
                    index = 2;
                    radioButtons[index].setChecked(true);
                    textViews[index].setTextColor(colors[0]);
                }
                Fragment_My.getInstance().is_notify = true;
                Fragment_My.getInstance().refresh();
                break;
            case R.id.rb_main_dh4:
            case R.id.ll_main_dh4:
                //我的
                UMengUtils.onCountListener(this, "GD_05");
                isRead = false;
                if (index != 3) {
                    FragmentTransaction transaction = getSupportFragmentManager()
                            .beginTransaction();
                    transaction.hide(fragments[index]);
                    radioButtons[index].setChecked(false);
                    textViews[index].setTextColor(colors[1]);
                    if (!fragments[3].isAdded()) {
                        transaction.add(R.id.ll_main_layout, fragments[3]);
                    }
                    transaction.show(fragments[3]).commit();
                    index = 3;
                    radioButtons[index].setChecked(true);
                    textViews[index].setTextColor(colors[0]);
                }
                Fragment_My.getInstance().is_notify = false;
                break;
            case R.id.rl_main_tuceng1:
                rl_main_tuceng1.setVisibility(View.GONE);
                SPUtils.setAppFirst1(MainActivity.this);
                break;
            case R.id.rl_main_tuceng2:
                rl_main_tuceng2.setVisibility(View.GONE);
                rl_main_tuceng3.setVisibility(View.VISIBLE);
//                SPUtils.setAppFirst2(MainActivity.this);
                break;
            case R.id.rl_main_tuceng3:
                rl_main_tuceng2.setVisibility(View.GONE);
                rl_main_tuceng3.setVisibility(View.GONE);
                SPUtils.setAppFirst2(MainActivity.this);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isEnabled) {
                isEnabled = true;
                Toast.makeText(MainActivity.this, "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessageDelayed(0, 2500);
            } else {
                onDestroy();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isEnabled = false;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            isEnabled = false;
        }

        ;
    };


    /**
     * 跳转有书圈
     */
    public void refresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isRead = false;
                if (index != 1) {
                    FragmentTransaction transaction = getSupportFragmentManager()
                            .beginTransaction();
                    transaction.hide(fragments[index]);
                    radioButtons[index].setChecked(false);
                    textViews[index].setTextColor(colors[1]);
                    if (!fragments[1].isAdded()) {
                        transaction.add(R.id.ll_main_layout, fragments[1]);
                    }
                    transaction.show(fragments[1]).commit();
                    index = 1;
                    radioButtons[index].setChecked(true);
                    textViews[index].setTextColor(colors[0]);
                }
                if (SPUtils.getAppFirst2(MainActivity.this)) {
                    rl_main_tuceng2.setVisibility(View.VISIBLE);
                }
                Fragment_My.getInstance().is_notify = false;
            }
        }, 400);
    }

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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, PlayerService.class);
        stopService(intent);
        stopService(Fragment_WeRead.getInstance().serviceIntent);
        ActivityUtil.exit();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);

    }

    //获取当前apk包信息
    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }

}