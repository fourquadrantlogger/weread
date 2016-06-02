package com.fengwo.reading.comment;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.BaseActivity;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.main.comment.CommentAddJson;
import com.fengwo.reading.main.group.GroupDetailsFragment;
import com.fengwo.reading.main.my.Fragment_Suibi;
import com.fengwo.reading.main.read.Fragment_Bookpack;
import com.fengwo.reading.main.read.bookpackdetails.CommentDetailsFragment;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.EditTextUtils;
import com.fengwo.reading.utils.EmojiUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 评论(带表情)
 * Author：ShengLuo
 * Date: 2016-03-10
 * Time: 18:49
 */
public class EmojiActivity extends BaseActivity implements OnClickListener {

    private CustomProgressDialog progressDialog;

    private RelativeLayout rl_emoji_layout;
    private LinearLayout ll_emoji_bq, ll_emoji_no;
    private ViewPager viewPager;
    private EditText et_emoji_zhengwen;
    private ImageView iv_emoji_img;
    private TextView tv_emoji_suibi, tv_emoji_fasong, tv_emoji_num;

    public int comment_type;// 评论0回复1
    public String name;// 姓名
    public String id;// 拆书包id、笔记id
    public String cid;// 评论id
    public String receive_user;// 被回复人id
    public int source = 0;// 来源界面 1:拆书包详情 2:有书圈详情 3:我的随笔我的收藏 4:话题 5:评论详情
    private int count = 10000;//限制字数
    private boolean flag = false; //
    private List<String> resList;

    private boolean isSuiBi = false; //是否同步到随笔

    private View saveView = null;
    public boolean needSaveView = false;

    public EmojiActivity() {
    }

    public static EmojiActivity Activity = new EmojiActivity();

    public static EmojiActivity getInstance() {
        return Activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji);
        Activity = this;
        findViewById();
        progressDialog = CustomProgressDialog.createDialog(this);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            source = bundle.getInt("source", 0);
            comment_type = bundle.getInt("comment_type", 0);
            id = bundle.getString("id", "");
            receive_user = bundle.getString("receive_user", "");
            name = bundle.getString("name", "");
            cid = bundle.getString("cid", "");
            if (source == 1) {
                tv_emoji_suibi.setVisibility(View.VISIBLE);
            } else {
                tv_emoji_suibi.setVisibility(View.GONE);
            }
        }

        if (et_emoji_zhengwen != null) {
            et_emoji_zhengwen.setFocusable(true);
            et_emoji_zhengwen.setFocusableInTouchMode(true);
            et_emoji_zhengwen.requestFocus();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() { //让软键盘延时弹出，以更好的加载Activity
                public void run() {
                    InputMethodManager inputManager =
                            (InputMethodManager) et_emoji_zhengwen.getContext().
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(et_emoji_zhengwen, 0);
                }
            }, 250);
        }

        switch (comment_type) {
            case 0:
                et_emoji_zhengwen.setHint("优秀评论将被优先展示");
                break;
            case 1:
                et_emoji_zhengwen.setHint("@" + name);
                break;
            default:
                break;
        }

        try {
            count = Integer.valueOf(NOsqlUtil.get_wordlimit().comment_limit);
            tv_emoji_num.setText("0/" + count);
            //限制字数
            et_emoji_zhengwen.setFilters(new InputFilter[]{new InputFilter.LengthFilter(count)});
        } catch (Exception e) {
        }
        et_emoji_zhengwen.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                tv_emoji_num
                        .setText(et_emoji_zhengwen
                                .getText().toString().length() + "/" + EmojiActivity.this.count);
                if (et_emoji_zhengwen.getText().length() < 30) {
                    isSuiBi = false;
                    Drawable drawable = getResources().getDrawable(
                            R.drawable.btn_emoji_ok);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                            drawable.getMinimumHeight());
                    tv_emoji_suibi
                            .setCompoundDrawables(drawable, null, null, null);
                } else if (et_emoji_zhengwen.getText().length() >= 30) {
//                    isSuiBi = true;
//                    Drawable drawable = getResources().getDrawable(
//                            R.drawable.emoji_choose);
//                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
//                            drawable.getMinimumHeight());
//                    tv_emoji_suibi
//                            .setCompoundDrawables(drawable, null, null, null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        initData();

    }

    private void findViewById() {
        rl_emoji_layout = (RelativeLayout) findViewById(R.id.rl_emoji_layout);
        et_emoji_zhengwen = (EditText) findViewById(R.id.et_emoji_zhengwen);
        iv_emoji_img = (ImageView) findViewById(R.id.iv_emoji_img);
        tv_emoji_suibi = (TextView) findViewById(R.id.tv_emoji_suibi);
        tv_emoji_fasong = (TextView) findViewById(R.id.tv_emoji_fasong);
        tv_emoji_num = (TextView) findViewById(R.id.tv_emoji_num);
        ll_emoji_bq = (LinearLayout) findViewById(R.id.ll_emoji_bq);
        ll_emoji_no = (LinearLayout) findViewById(R.id.ll_emoji_no);
        viewPager = (ViewPager) findViewById(R.id.vp_emoji_bq);

        ll_emoji_bq.setVisibility(View.GONE);

        rl_emoji_layout.setOnClickListener(this);
        ll_emoji_no.setOnClickListener(this);
        et_emoji_zhengwen.setOnClickListener(this);
        iv_emoji_img.setOnClickListener(this);
        tv_emoji_fasong.setOnClickListener(this);
        tv_emoji_suibi.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_emoji_layout:
                finish();
                EditTextUtils.hideSoftInput(et_emoji_zhengwen, this);
                overridePendingTransition(
                        R.anim.push_bottom_in, R.anim.push_bottom_out);
                break;
            case R.id.tv_emoji_fasong:
                if (TextUtils.isEmpty(et_emoji_zhengwen.getText().toString().trim())) {
                    CustomToast.showToast(EmojiActivity.this, "内容不能为空");
                } else {
                    if (source == 5) {
                        //评论详情
                        getData1();
                    } else {
                        //拆书包 随笔
                        getData();
                    }
                }
                break;
            case R.id.tv_emoji_suibi:
                //是否同步到随笔
                if (source == 1 && et_emoji_zhengwen.getText().length() < 30) {
                    Toast.makeText(EmojiActivity.this, "评论大于30个字,才能同步到有书圈", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isSuiBi) {
                    isSuiBi = false;
                    Drawable drawable = getResources().getDrawable(
                            R.drawable.btn_emoji_ok);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                            drawable.getMinimumHeight());
                    tv_emoji_suibi
                            .setCompoundDrawables(drawable, null, null, null);
                } else {
                    isSuiBi = true;
                    Drawable drawable = getResources().getDrawable(
                            R.drawable.emoji_choose);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                            drawable.getMinimumHeight());
                    tv_emoji_suibi
                            .setCompoundDrawables(drawable, null, null, null);
//                    tv_emoji_suibi.setTextColor(getResources().getColor(R.color.green));
                }
                break;
        }
    }

    /**
     * 初始化表情数据
     */
    private void initData() {
        flag = false;

        et_emoji_zhengwen.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                flag = false;
                ll_emoji_bq.setVisibility(View.GONE);
            }
        });

        iv_emoji_img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (flag) {
                    ll_emoji_bq.setVisibility(View.GONE);
                    flag = false;
                } else {
                    ll_emoji_bq.setVisibility(View.VISIBLE);
                    flag = true;
                }
                EditTextUtils.hideSoftInput(et_emoji_zhengwen, EmojiActivity.this);
            }
        });

        // 表情list
        resList = getExpressionRes(141);
        // 初始化表情viewpager
        List<View> views = new ArrayList<View>();
        View gv1 = getGridChildView(1);
        View gv2 = getGridChildView(2);
        View gv3 = getGridChildView(3);
        View gv4 = getGridChildView(4);
        View gv5 = getGridChildView(5);
        View gv6 = getGridChildView(6);
        View gv7 = getGridChildView(7);
        views.add(gv1);
        views.add(gv2);
        views.add(gv3);
        views.add(gv4);
        views.add(gv5);
        views.add(gv6);
        views.add(gv7);
        viewPager.setAdapter(new ExpressionPagerAdapter(views));
        et_emoji_zhengwen.requestFocus();
    }

    /**
     * 获取表情的gridview的子view
     */
    private View getGridChildView(int i) {
        View view = View.inflate(EmojiActivity.this, R.layout.expression_gridview,
                null);
        GridView gv = (GridView) view.findViewById(R.id.gridView1);
        List<String> list = new ArrayList<String>();
        if (i == 7) {
            list.addAll(resList.subList(20 * 7, resList.size()));
        } else {
            list.addAll(resList.subList((i - 1) * 20, i * 20));
        }
        list.add("delete_expression");
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(
                EmojiActivity.this, 1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String filename = expressionAdapter.getItem(position);
                try {
                    // 文字输入框可见时，才可输入表情
                    // 按住说话可见，不让输入表情
                    // 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
                    if (filename != "delete_expression") { // 不是删除键，显示表情
                        Class<?> clz = Class
                                .forName("com.fengwo.reading.utils.EmojiUtils");
                        Field field = clz.getField(filename);
                        et_emoji_zhengwen.append(EmojiUtils.getSmiledText(EmojiActivity.this,
                                (String) field.get(null)));
                    } else {// 删除文字或者表情
                        if (!TextUtils.isEmpty(et_emoji_zhengwen.getText().toString())) {
                            int selectionStart = et_emoji_zhengwen.getSelectionStart();// 获取光标的位置
                            if (selectionStart > 0) {
                                String body = et_emoji_zhengwen.getText().toString();
                                String tempStr = body.substring(0,
                                        selectionStart);
                                if (tempStr.length() >= 5 && tempStr.endsWith("]") && tempStr.substring(0, tempStr.length() - 4).endsWith("[")) {
                                    int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
                                    if (i != -1) {
                                        CharSequence cs = tempStr.substring(i,
                                                selectionStart);
                                        if (EmojiUtils.containsKey(cs.toString())) {
                                            et_emoji_zhengwen.getEditableText().delete(i,
                                                    selectionStart);
                                        } else {
                                            et_emoji_zhengwen.getEditableText().delete(
                                                    selectionStart - 1,
                                                    selectionStart);
                                        }
                                    } else {
                                        et_emoji_zhengwen.getEditableText().delete(
                                                selectionStart - 1, selectionStart);
                                    }
                                } else {
                                    et_emoji_zhengwen.getEditableText().delete(
                                            selectionStart - 1, selectionStart);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
        return view;
    }

    private List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "ee_" + x;
            reslist.add(filename);
        }
        return reslist;
    }

    /**
     * 发布
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", id);
        map.put("type", source == 1 ? "bp" : "note");
        map.put("content", et_emoji_zhengwen.getText().toString());
        if (comment_type == 1) {
            map.put("cid", cid);
        }
        if (isSuiBi) {
            map.put("is_note", "1");
        } else {
            map.put("is_note", "0");
        }

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.comment_add, new RequestCallBack<String>() {

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
//                        System.out.println("-------455:" + id + " , " + source + " , " + jsonString);
                        try {
                            final CommentAddJson json = new Gson().fromJson(
                                    jsonString, CommentAddJson.class);
                            if ("1".equals(json.code)) {
                                switch (source) {
                                    case 1:
                                        Fragment_Bookpack.getInstance().refresh(json.level_is_up);
                                        break;
                                    case 2:
                                        GroupDetailsFragment.getInstance()
                                                .refresh1(
                                                        json.cid,
                                                        et_emoji_zhengwen
                                                                .getText()
                                                                .toString(), json.level_is_up);
                                        break;
                                    case 3:
                                        Fragment_Suibi.getInstance().refresh(json.level_is_up);
                                        break;

                                    default:
                                        break;
                                }
                                EditTextUtils.hideSoftInput(et_emoji_zhengwen, EmojiActivity.this);
                                finish();
                                overridePendingTransition(
                                        R.anim.push_bottom_in, R.anim.push_bottom_out);
                            } else {
                                Context context = EmojiActivity.this;
                                if (context != null
                                        && !((Activity) context).isFinishing()) {
                                    CustomToast.showToast(context, json.msg);
                                }
                            }
                        } catch (Exception e) {
                            Context context = EmojiActivity.this;
                            if (context != null
                                    && !((Activity) context).isFinishing()) {
                                CustomToast.showToast(context,
                                        context.getString(R.string.json_error));
                            }
                        }
                    }
                }, true, null);
    }

    /**
     * 评论详情的评论
     */
    private void getData1() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("bpc_id", id);
        map.put("receive_user", receive_user);
        map.put("content", et_emoji_zhengwen.getText().toString());
        if (comment_type == 1) {
            map.put("id", cid);
        }
        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.comment_comadd, new RequestCallBack<String>() {

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
//                        System.out.println("========" + jsonString);
                        try {
                            final CommentAddJson json = new Gson().fromJson(
                                    jsonString, CommentAddJson.class);
                            if ("1".equals(json.code)) {
                                CommentDetailsFragment.getInstance().refresh(json.level_is_up);
                                EditTextUtils.hideSoftInput(et_emoji_zhengwen, EmojiActivity.this);
                                finish();
                                overridePendingTransition(
                                        R.anim.push_bottom_in, R.anim.push_bottom_out);
                            } else {
                                Context context = EmojiActivity.this;
                                if (context != null
                                        && !((Activity) context).isFinishing()) {
                                    CustomToast.showToast(context, json.msg);
                                }
                            }
                        } catch (Exception e) {
                            Context context = EmojiActivity.this;
                            if (context != null
                                    && !((Activity) context).isFinishing()) {
                                CustomToast.showToast(context,
                                        context.getString(R.string.json_error));
                            }
                        }
                    }
                }, true, null);
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 2:
                    Context context = EmojiActivity.this;
                    if (context != null && !((Activity) context).isFinishing()) {
                        CustomToast.showToast(context,
                                context.getString(R.string.network_check));
                    }
                    break;
                case 1:
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    break;
                case 0:
                    if (progressDialog != null && !progressDialog.isShowing()) {
                        progressDialog.show();
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!needSaveView) {
            needSaveView = true;
//            et_emoji_zhengwen.setText("");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EditTextUtils.hideSoftInput(et_emoji_zhengwen, this);
        overridePendingTransition(
                R.anim.push_bottom_in, R.anim.push_bottom_out);
        MobclickAgent.onPause(this);
    }

}