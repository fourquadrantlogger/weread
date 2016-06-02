package com.fengwo.reading.main.my;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.fengwo.reading.R;
import com.fengwo.reading.activity.MainActivity;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.main.group.GroupDetailsFragment;
import com.fengwo.reading.main.read.bookpackdetails.CommentDetailsFragment;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.MySQLiteOpenHelper;
import com.fengwo.reading.utils.ScreenUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lxq - 消息详情(评论,点赞)
 */
public class MyNotifyCommentFragment extends Fragment implements
        OnClickListener {

    private CustomProgressDialog progressDialog;
    private SQLiteDatabase database;

    private ImageView iv_title_left;
    private TextView tv_title_mid;
    private ImageView iv_mynotify_null;

    private SwipeMenuListView listView;
    private List<MyNotifyBean> list;
    private List<MyNotifyBean> mList;
    private MyNotifyCommentAdapter adapter;

    public int type; //0:评论  1:赞

    private View saveView = null;
    public boolean needSaveView = false;

    private static MyNotifyCommentFragment fragment = new MyNotifyCommentFragment();

    public static MyNotifyCommentFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_mynotify_comment,
                container, false);

        findViewById(view);
        onClickListener();

        tv_title_mid.setVisibility(View.VISIBLE);
        switch (type) {
            case 0:
                tv_title_mid.setText("评论");
                iv_mynotify_null
                        .setImageResource(R.drawable.myinfo_notify_null_comment);
                break;
            case 1:
                tv_title_mid.setText("赞");
                iv_mynotify_null
                        .setImageResource(R.drawable.myinfo_notify_null_zan);
                break;

            default:
                break;
        }

        progressDialog = CustomProgressDialog.createDialog(fragment
                .getActivity());

        list = new ArrayList<>();
        mList = new ArrayList<>();
        adapter = new MyNotifyCommentAdapter(fragment, list);
        // listView.setAdapter(adapter);

        listView.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(fragment
                        .getActivity().getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(getResources()
                        .getColor(R.color.red)));
                deleteItem.setWidth(ScreenUtil.dip2px(fragment.getActivity(),
                        70));
                // deleteItem.setTitle("删除");
                // deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setIcon(R.drawable.myinfo_notify_delete);
                menu.addMenuItem(deleteItem);
            }

        });

        listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                deleteLocationData(position);
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Context context = fragment.getActivity();
                if (context == null) {
                    return;
                }
                FragmentTransaction transaction = fragment.getActivity()
                        .getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right,
                        R.anim.out_to_left, R.anim.in_from_left,
                        R.anim.out_to_right);

                //note随笔、bpcomment拆书包评论详情页
                if (list.get(position).type != null) {//添加对于type的判断
                    switch (list.get(position).type) {
                        case "note":
                            //随笔
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.ll_activity_next,
                                    GroupDetailsFragment.getInstance());
                            transaction.commit();
                            GroupDetailsFragment.getInstance().needSaveView = false;
                            GroupDetailsFragment.getInstance().id = list.get(position).id;
                            GroupDetailsFragment.getInstance().source = 5;
                            break;
                        case "bpcomment":
                            //拆书包评论详情页
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.ll_activity_next,
                                    CommentDetailsFragment.getInstance());
                            transaction.commit();
                            CommentDetailsFragment.getInstance().source = 3;
                            CommentDetailsFragment.getInstance().groupPosition = position;
                            CommentDetailsFragment.getInstance().bpc_id = list.get(position).id;
                            CommentDetailsFragment.getInstance().needSaveView = false;
                            break;
                    }
                }else{
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.ll_activity_next,
                            GroupDetailsFragment.getInstance());
                    transaction.commit();
                    GroupDetailsFragment.getInstance().needSaveView = false;
                    GroupDetailsFragment.getInstance().id = list.get(position).id;
                    GroupDetailsFragment.getInstance().source = 5;
                }
            }
        });

        View v = LayoutInflater.from(fragment.getActivity()).inflate(
                R.layout.layout_mynotify_footer, null);

        //本地数据库
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(
                fragment.getActivity());
        database = helper.getReadableDatabase();

        String s = "";
        switch (type) {
            case 0:
                s = "comment";
                break;
            case 1:
                s = "digg";
                break;
        }

        Cursor cursor2 = database
                .rawQuery(
                        "select * from tb_notify where uid = ? and source = ? order by _id desc",
                        new String[]{GlobalParams.uid, s});
        int num2 = cursor2.getCount();
        if (num2 == 0) {
            iv_mynotify_null.setVisibility(View.VISIBLE);
        } else {
            iv_mynotify_null.setVisibility(View.GONE);
            Cursor cursor1 = database
                    .rawQuery(
                            "select * from tb_notify where uid = ? and source = ? and is_read = ? order by _id desc",
                            new String[]{GlobalParams.uid, s, "0"});
            int num1 = cursor1.getCount();
            if (num1 == 0) {
                listView.setAdapter(adapter);
                getMyNotifyBeans(cursor2, false, true);
            } else {
                if (num1 < num2) {
                    listView.addFooterView(v);
                }
                listView.setAdapter(adapter);
                getMyNotifyBeans(cursor1, true, false);
                getMyNotifyBeans(cursor2, false, false);
            }
        }

        v.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                listView.removeFooterView(v);
                adapter.notifyDataSetChanged();
                Cursor cursor = database
                        .rawQuery(
                                "select * from tb_notify where uid = ? and source = ? order by _id desc",
                                new String[]{GlobalParams.uid,
                                        type == 0 ? "comment" : "digg"});
                getMyNotifyBeans(cursor, false, true);
            }
        });

        return view;
    }

    /**
     * @param cursor
     * @param read
     * @param add
     */
    private void getMyNotifyBeans(Cursor cursor, boolean read, boolean add) {
        if (add) {
            list.clear();
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
            if (read) {
                list.add(bean);
                adapter.notifyDataSetChanged();
            } else {
                if (add) {
                    list.add(bean);
                    adapter.notifyDataSetChanged();
                } else {
                    mList.add(bean);
                }
            }
        }
        cursor.close();
        if (read) {
            updateLocationData();
        }
    }

    private void updateLocationData() {
        ContentValues values = new ContentValues();
        values.put("is_read", "1");
        int i = database.update("tb_notify", values, "source = ?",
                new String[]{type == 0 ? "comment" : "digg"});
        if (i > 0) {
            System.out.println("updateLocationData==========ok");
        } else {
            System.out.println("updateLocationData==========no");
        }
    }

    private void deleteLocationData(int position) {
        int i = database.delete("tb_notify", "_id = ?",
                new String[]{list.get(position)._id});
        if (i > 0) {
            System.out.println("deleteLocationData==========ok");
            list.remove(position);
            adapter.notifyDataSetChanged();
            if (list.size() == 0) {
                iv_mynotify_null.setVisibility(View.VISIBLE);
            }
        } else {
            System.out.println("deleteLocationData==========no");
        }
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);
        iv_mynotify_null = (ImageView) view.findViewById(R.id.iv_mynotify_null);
        listView = (SwipeMenuListView) view.findViewById(R.id.lv_mynotify_show);
    }

    private void onClickListener() {
        iv_title_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Context context = fragment.getActivity();
        if (context == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_return:
                fragment.getActivity().getSupportFragmentManager().popBackStack();
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
        MobclickAgent.onPageEnd("MyNotifyCommentFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MyNotifyCommentFragment");
    }

}
