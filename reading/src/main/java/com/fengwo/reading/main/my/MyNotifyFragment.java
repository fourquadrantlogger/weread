package com.fengwo.reading.main.my;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.MainActivity;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.MySQLiteOpenHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lxq - 消息通知
 */
public class MyNotifyFragment extends Fragment implements OnClickListener {

    private CustomProgressDialog progressDialog;
    private SQLiteDatabase database;
    private ImageView iv_title_left;
    private TextView tv_title_mid;
    private RelativeLayout rl_mynotify_comment;
    private TextView tv_mynotify_comment;
    private RelativeLayout rl_mynotify_zan;
    private TextView tv_mynotify_zan;

    private ListView listView;
    private List<MyNotifyBean> list;
    private MyNotifyAdapter adapter;

    private View saveView = null;
    public boolean needSaveView = false;

    private static MyNotifyFragment fragment = new MyNotifyFragment();

    public static MyNotifyFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;
        View view = inflater.inflate(R.layout.fragment_mynotify, container,
                false);

        findViewById(view);

        tv_title_mid.setText("消息");
        tv_title_mid.setVisibility(View.VISIBLE);

        progressDialog = CustomProgressDialog.createDialog(fragment
                .getActivity());

        View v = LayoutInflater.from(fragment.getActivity()).inflate(
                R.layout.layout_mynotify_header, null);
        listView.addHeaderView(v);
        findViewById_header(v);

        list = new ArrayList<>();
        adapter = new MyNotifyAdapter(fragment, list);
        listView.setAdapter(adapter);

        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(
                fragment.getActivity());
        database = helper.getReadableDatabase();
        Cursor cursor = database
                .rawQuery(
                        "select * from tb_notify where uid = ? and source = ? order by _id desc",
                        new String[]{GlobalParams.uid, "system"});
        getMyNotifyBeans(cursor);
        cursor.close();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //从数据库取 评论,点赞 数量
        Cursor cursor1 = database
                .rawQuery(
                        "select * from tb_notify where uid = ? and source = ? and is_read = ? order by _id desc",
                        new String[]{GlobalParams.uid, "comment", "0"});
        int num1 = cursor1.getCount();
        cursor1.close();
        tv_mynotify_comment.setText(num1 > 99 ? " 99+ " : " " + num1 + " ");
        if (num1 == 0) {
            tv_mynotify_comment.setVisibility(View.GONE);
        } else {
            tv_mynotify_comment.setVisibility(View.VISIBLE);
        }
        Cursor cursor2 = database
                .rawQuery(
                        "select * from tb_notify where uid = ? and source = ? and is_read = ? order by _id desc",
                        new String[]{GlobalParams.uid, "digg", "0"});
        int num2 = cursor2.getCount();
        cursor1.close();
        tv_mynotify_zan.setText(num2 > 99 ? " 99+ " : " " + num2 + " ");
        if (num2 == 0) {
            tv_mynotify_zan.setVisibility(View.GONE);
        } else {
            tv_mynotify_zan.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 消息参数
     *
     * @param cursor
     */
    private void getMyNotifyBeans(Cursor cursor) {
        list.clear();
        if (cursor.getCount() == 0) {
            adapter.notifyDataSetChanged();
            return;
        }
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String notify_user_id = cursor.getString(cursor.getColumnIndex("notify_user_id"));
            String source = cursor.getString(cursor.getColumnIndex("source"));
            //修改type
            String type = null;
            if (!(MainActivity.version.substring(0, 3).equals("1.0") && !MainActivity.version.equals("1.0.7"))) {
                type = cursor.getString(cursor.getColumnIndex("type"));
            }
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String avatar = cursor.getString(cursor.getColumnIndex("avatar"));
            String sex = cursor.getString(cursor.getColumnIndex("sex"));
            String right = cursor.getString(cursor.getColumnIndex("right"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String create_time = cursor.getString(cursor.getColumnIndex("create_time"));
            MyNotifyBean bean = new MyNotifyBean(_id + "", id, name, avatar,
                    sex, notify_user_id, source, right, content, create_time, type);
            list.add(bean);
        }
        adapter.notifyDataSetChanged();
        updateLocationData();
    }

    private void updateLocationData() {
        ContentValues values = new ContentValues();
        values.put("is_read", "1");
        int i = database.update("tb_notify", values, "source = ?",
                new String[]{"system"});
        if (i > 0) {
            System.out.println("updateLocationData==========ok");
        } else {
            System.out.println("updateLocationData==========no");
        }
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);
        listView = (ListView) view.findViewById(R.id.lv_mynotify_show);
    }

    private void findViewById_header(View view) {
        rl_mynotify_comment = (RelativeLayout) view
                .findViewById(R.id.rl_mynotify_comment);
        tv_mynotify_comment = (TextView) view
                .findViewById(R.id.tv_mynotify_comment);

        rl_mynotify_zan = (RelativeLayout) view
                .findViewById(R.id.rl_mynotify_zan);
        tv_mynotify_zan = (TextView) view.findViewById(R.id.tv_mynotify_zan);

        rl_mynotify_comment.setOnClickListener(this);
        rl_mynotify_zan.setOnClickListener(this);
        iv_title_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Context context = fragment.getActivity();
        if (context == null) {
            return;
        }
        FragmentTransaction transaction = fragment.getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right,
                R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        switch (v.getId()) {
            case R.id.iv_return:
                fragment.getActivity().finish();
                fragment.getActivity().overridePendingTransition(
                        R.anim.in_from_left, R.anim.out_to_right);
                // fragment.getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.rl_mynotify_comment:
                //跳转评论
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next,
                        MyNotifyCommentFragment.getInstance());
                transaction.commit();
                MyNotifyCommentFragment.getInstance().needSaveView = false;
                MyNotifyCommentFragment.getInstance().type = 0;
                break;
            case R.id.rl_mynotify_zan:
                //跳转点赞
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next,
                        MyNotifyCommentFragment.getInstance());
                transaction.commit();
                MyNotifyCommentFragment.getInstance().needSaveView = false;
                MyNotifyCommentFragment.getInstance().type = 1;
                break;

            default:
                break;
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 2:
                    Context context = fragment.getActivity();
                    if (context != null) {
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
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("MyNotifyFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MyNotifyFragment");
    }

}
