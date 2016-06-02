package com.fengwo.reading.main.group;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.common.SelectPicPopupWindow;
import com.fengwo.reading.main.comment.CommentListJson;
import com.fengwo.reading.main.discover.hottopics.LabelFragment;
import com.fengwo.reading.main.discover.hottopics.TopicsActivity;
import com.fengwo.reading.main.my.achieve.ExplainFragment;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.qq.QQAlbumActivity;
import com.fengwo.reading.qq.QQAlbumAdapter;
import com.fengwo.reading.umeng.PlatformBind;
import com.fengwo.reading.umeng.UMShare;
import com.fengwo.reading.utils.EditTextUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 有书圈 - 发表随笔(发表话题)
 *
 * @author Luo Sheng
 * @date 2016-1-27
 */
public class PublishFeelingsFragment extends Fragment implements
        OnClickListener {

    private SelectPicPopupWindow selectPicPopupWindow;

    private int width = 65;
    private List<ImageBean> list;
    private MyReadNoteAdapter adapter;
    private List<File> lisFiles;

    private ImageView iv_title_left, iv_publishfeelings_wb, iv_publishfeelings_qq;
    private TextView tv_title_left, tv_title_mid, tv_title_right1,
            tv_publishfeelings_bookname, tv_publishfeelings_state,
            tv_publishfeelings_zishu, tv_publishfeelings_biaoqian;
    private EditText et_publishfeelings_content, et_publishfeelings_title;
    private GridView gridView;
    private RelativeLayout rl_publishfeelings_bookname, rl_publishfeelings_state, rl_publishfeelings_tianjia;
    private LinearLayout ll_publishfeelings_yincang;

    public boolean isQQZone = false;

    public CustomProgressDialog progressDialog;
    private View saveView = null;
    public boolean needSaveView = false;

    public static boolean isRefresh = false;// 用于相册的数据刷新
    public static boolean isRefresh2 = false;// 用于ReadNoteList的数据刷新

    public int source = 0;// 来源 1:有书圈添加 2:编辑 3:话题添加 4:任务模块
    public String title = "";// 话题标题

    public GroupBean bean = null; // 编辑传过来
    public String id = "";// 笔记id
    public String book_id = "";// 相关书籍id
    public int is_pub = 0;// 谁可以看,0公开，1私有

    private int i = 0;
    private int content = 800; // 内容字数限制

    private boolean sendto_weibo;

    public PublishFeelingsFragment() {
    }

    public static PublishFeelingsFragment fragment = new PublishFeelingsFragment();

    public static PublishFeelingsFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_publishfeelings,
                container, false);
        progressDialog = CustomProgressDialog.createDialog(getActivity());

        findViewById(view);
        setTitle();
        // 获取之前未提交的内容
        et_publishfeelings_content.setText(SPUtils.getContent(getActivity()));

        isQQZone = false;
        book_id = "";
        is_pub = 0;

        list = new ArrayList<ImageBean>();
        adapter = new MyReadNoteAdapter(getActivity(), list);
        gridView.setAdapter(adapter);

        // GridView的尺寸
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = (metric.widthPixels / 5);
        rl_publishfeelings_tianjia.setVisibility(View.VISIBLE);

        lisFiles = new ArrayList<>();

        QQAlbumActivity.mSelectedImage.clear();
        QQAlbumAdapter.mSelectedImage.clear();

        switch (source) {
            case 1:
            case 4:
                // 添加 - 清空数据
                break;
            case 2:
                // 编辑 - 设置数据
                setInfo();
                break;
            case 3:
                // 话题
                et_publishfeelings_content.setText(title);
                break;
            default:
                break;
        }

        // 弹出窗体
        selectPicPopupWindow = new SelectPicPopupWindow(fragment.getActivity(), new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_popupwindow_takephoto:// 放弃编辑
                        SPUtils.setContent(getActivity(), "");
                        EditTextUtils.hideSoftInput(et_publishfeelings_content, getActivity());
                        EditTextUtils.hideSoftInput(et_publishfeelings_title, getActivity());
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                        break;
                    case R.id.btn_popupwindow_pickphoto:// 保存私密
                        handler.sendEmptyMessageDelayed(3, 200);
                        break;
                    default:
                        break;
                }
                selectPicPopupWindow.dismiss();
            }
        });
        selectPicPopupWindow.setBtnText("放弃编辑", "保存私密");
        selectPicPopupWindow.setColor(getActivity().getResources().getColor(R.color.red), getActivity().getResources().getColor(R.color.text_32));
        selectPicPopupWindow.setFinish("关闭");

        return view;
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        tv_title_left = (TextView) view.findViewById(R.id.tv_title_left);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);
        tv_title_right1 = (TextView) view.findViewById(R.id.tv_title_right1);
        tv_publishfeelings_bookname = (TextView) view
                .findViewById(R.id.tv_publishfeelings_bookname);
        tv_publishfeelings_state = (TextView) view
                .findViewById(R.id.tv_publishfeelings_state);
        tv_publishfeelings_zishu = (TextView) view
                .findViewById(R.id.tv_publishfeelings_zishu);
        rl_publishfeelings_tianjia = (RelativeLayout) view
                .findViewById(R.id.rl_publishfeelings_tianjia);
        et_publishfeelings_content = (EditText) view
                .findViewById(R.id.et_publishfeelings_content);
        et_publishfeelings_title = (EditText) view
                .findViewById(R.id.et_publishfeelings_title);
        gridView = (GridView) view
                .findViewById(R.id.gv_publishfeelings_tianjia);
        rl_publishfeelings_bookname = (RelativeLayout) view
                .findViewById(R.id.rl_publishfeelings_bookname);
        rl_publishfeelings_state = (RelativeLayout) view
                .findViewById(R.id.rl_publishfeelings_state);

        iv_publishfeelings_wb = (ImageView) view.findViewById(R.id.iv_publishfeelings_wb);
        iv_publishfeelings_qq = (ImageView) view.findViewById(R.id.iv_publishfeelings_qq);

        tv_publishfeelings_biaoqian = (TextView) view
                .findViewById(R.id.tv_publishfeelings_biaoqian);

        // 字数的限制
        try {
            content = Integer.valueOf(NOsqlUtil.get_wordlimit().note_limit);
            int title = Integer.valueOf(NOsqlUtil.get_wordlimit().note_title_limit);
            tv_publishfeelings_zishu.setText("还可输入" + content + "个字");
            et_publishfeelings_content
                    .setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                            content)});
            et_publishfeelings_title
                    .setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                            title)});

        } catch (Exception e) {
        }

        tv_title_left.setOnClickListener(this);
        tv_title_right1.setOnClickListener(this);
        tv_publishfeelings_biaoqian.setOnClickListener(this);
        rl_publishfeelings_tianjia.setOnClickListener(this);
        rl_publishfeelings_bookname.setOnClickListener(this);
        rl_publishfeelings_state.setOnClickListener(this);
        iv_publishfeelings_wb.setOnClickListener(this);
        iv_publishfeelings_qq.setOnClickListener(this);

        // 右侧按钮灰色,不可点击
        tv_title_right1.setTextColor(fragment.getActivity().getResources()
                .getColor(R.color.fasong));
//        tv_title_right1.setBackgroundResource(R.drawable.publishmessage_white);
        tv_title_right1.setEnabled(false);

        // 输入框的监听
        et_publishfeelings_content.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_publishfeelings_zishu.setText("最多输入" + (content - et_publishfeelings_content.getText().toString().length()) + "个字");
                SPUtils.setContent(getActivity(), et_publishfeelings_content
                        .getText().toString());
                setIsEnabled();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setTitle() {
        iv_title_left.setVisibility(View.GONE);
        tv_title_left.setVisibility(View.VISIBLE);
        tv_title_right1.setVisibility(View.VISIBLE);
        tv_title_mid.setVisibility(View.VISIBLE);
        tv_title_left.setText("取消");
        tv_title_right1.setText("发布");
        tv_title_mid.setText("随笔");
    }


    private UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.login");

    /**
     * 授权。如果授权成功，则获取用户信息
     *
     * @param platform 平台标签
     */
    private void login(final SHARE_MEDIA platform) {
        mController.doOauthVerify(getActivity(), platform,
                new SocializeListeners.UMAuthListener() {

                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                    }

                    @Override
                    public void onError(SocializeException e, SHARE_MEDIA platform) {
                        Toast.makeText(getActivity(), "授权错误", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete(Bundle value, SHARE_MEDIA platform) {
                        if (value != null
                                && !TextUtils.isEmpty(value.getString("uid"))) {
                            // uid不为空，获取相关授权信息
                            //getUserInfo(platform);
                        } else {
                            Toast.makeText(getActivity(), "授权失败...",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        // Log.e("TAG", "-----授权取消");
                    }
                });
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        switch (v.getId()) {
            case R.id.tv_title_left:
                switch (source) {
                    case 1:
                    case 3:
                    case 4:
                        // 弹框
                        EditTextUtils.hideSoftInput(et_publishfeelings_content, getActivity());
                        EditTextUtils.hideSoftInput(et_publishfeelings_title, getActivity());
                        if (et_publishfeelings_title.getText().length() > 0 || et_publishfeelings_content.getText().length() > 0) {
                            selectPicPopupWindow.showAtLocation(fragment.getActivity()
                                    .findViewById(R.id.ll_activity_next), Gravity.BOTTOM
                                    | Gravity.CENTER_HORIZONTAL, 0, 0);
                        } else {
                            getActivity().finish();
                            getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                        }
                        break;
                    case 2:
                        // 编辑 - 直接返回
                        EditTextUtils.hideSoftInput(et_publishfeelings_content, getActivity());
                        EditTextUtils.hideSoftInput(et_publishfeelings_title, getActivity());
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                        break;
                    default:
                        break;
                }
                break;
            case R.id.tv_title_right1:
                // 确认发布
                MLog.v("reading", "确认发送");
                progressDialog.show();
                handler.sendEmptyMessageDelayed(3, 200);
                break;
            case R.id.rl_publishfeelings_tianjia:
                // TODO
                // 添加图片
                if (list.size() >= 9) {
                    Toast.makeText(getActivity(), "最多选择9张图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                EditTextUtils.hideSoftInput(et_publishfeelings_content, getActivity());
                EditTextUtils.hideSoftInput(et_publishfeelings_title, getActivity());

                QQAlbumAdapter.mSelectedImage.clear();
                QQAlbumAdapter.mSelectedImage
                        .addAll(QQAlbumActivity.mSelectedImage);

                picMenuWindow = new SelectPicPopupWindow(getActivity(),
                        itemsOnClick);
                picMenuWindow.setBtnText("拍照", "相册");
                picMenuWindow.showAtLocation(
                        getActivity().findViewById(R.id.ll_activity_next),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.rl_publishfeelings_bookname:
                // 跳转 相关书籍
                transaction.replace(R.id.ll_activity_next,
                        BooksFragment.getInstance());
                transaction.commit();

                BooksFragment.getInstance().book_id = book_id;
                BooksFragment.getInstance().needSaveView = false;
                break;
            case R.id.rl_publishfeelings_state:
                // 跳转 谁可以看
                transaction.replace(R.id.ll_activity_next,
                        StateFragment.getInstance());
                transaction.commit();
                StateFragment.getInstance().needSaveView = false;
                StateFragment.getInstance().is_pub = is_pub;
                break;
            case R.id.tv_publishfeelings_biaoqian:
                // 软键盘弹出 - 选择标签
                EditTextUtils.hideSoftInput(et_publishfeelings_content,
                        getActivity());
                EditTextUtils
                        .hideSoftInput(et_publishfeelings_title, getActivity());
                transaction.replace(R.id.ll_activity_next,
                        LabelFragment.getInstance());
                transaction.commit();
                LabelFragment.getInstance().needSaveView = false;
                break;
            case R.id.iv_publishfeelings_wb:
                SPUtils.getUserWeiboAccess_Token(fragment.getActivity());
                if (GlobalParams.weiboaccess_token.equals("")) {
                    // 跳转到 绑定界面
                    PlatformBind platformBind = new PlatformBind(fragment.getActivity());
                    platformBind.login(SHARE_MEDIA.SINA);
                    return;
                } else {
                    //同步发布到微博
                    if (sendto_weibo = !sendto_weibo) {
                        iv_publishfeelings_wb.setImageDrawable(getResources().getDrawable(R.drawable.setting_tb_wb_yes));
                    } else {
                        iv_publishfeelings_wb.setImageDrawable(getResources().getDrawable(R.drawable.setting_tb_wb_no));
                    }
                }
                break;
            case R.id.iv_publishfeelings_qq:
                //同步发布到QQ空间
                if (isQQZone) {
                    iv_publishfeelings_qq.setImageDrawable(getResources().getDrawable(R.drawable.setting_tb_qq_no));
                    isQQZone = false;
                } else {
                    iv_publishfeelings_qq.setImageDrawable(getResources().getDrawable(R.drawable.setting_tb_qq_yes));
                    isQQZone = true;
                }

                break;

            default:
                break;
        }
    }

    /**
     * 发送 是否可点击
     */
    private void setIsEnabled() {
        if (!TextUtils.isEmpty(et_publishfeelings_content.getText().toString()
                .trim())) {
            tv_title_right1.setTextColor(fragment.getActivity().getResources()
                    .getColor(R.color.white));
//            tv_title_right1
//                    .setBackgroundResource(R.drawable.publishmessage_green);
            tv_title_right1.setEnabled(true);
        } else {
            tv_title_right1.setTextColor(fragment.getActivity().getResources()
                    .getColor(R.color.fasong));
//            tv_title_right1
//                    .setBackgroundResource(R.drawable.publishmessage_white);
            tv_title_right1.setEnabled(false);
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 3:
                    setFile();
                    break;
                case 2:
                    Context context = fragment.getActivity();
                    if (context != null) {
                        Toast.makeText(context,
                                context.getString(R.string.network_check),
                                Toast.LENGTH_SHORT).show();
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
    };

    /**
     * 扫描、刷新相册
     */
    private void scanPhotos(String filePath, Context context) {// public static
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     * Gridview适配器
     */
    private class MyReadNoteAdapter extends BaseAdapter {

        private Context context;
        private List<ImageBean> list;

        public MyReadNoteAdapter(Context context, List<ImageBean> list) {
            super();
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.item_readnote, parent, false);
                holder.iv_item_readnote_img = (ImageView) convertView
                        .findViewById(R.id.iv_item_readnote_img);
                holder.iv_item_readnote_delete = (ImageView) convertView
                        .findViewById(R.id.iv_item_readnote_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    width, width);
            holder.iv_item_readnote_img.setLayoutParams(params);

            // 删除是否显示
            if (list.get(position).isShow) {
                holder.iv_item_readnote_delete.setVisibility(View.VISIBLE);
            } else {
                holder.iv_item_readnote_delete.setVisibility(View.GONE);
            }

            // 加载本地图片
            BitmapUtils bitmapUtils = new BitmapUtils(context);
            bitmapUtils.display(holder.iv_item_readnote_img,
                    list.get(position).url);

            holder.iv_item_readnote_img
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // 点击图片

                        }
                    });

            holder.iv_item_readnote_delete
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // 删除图片
                            list.remove(position);
                            QQAlbumActivity.mSelectedImage.remove(position);
                            // QQAlbumAdapter.mSelectedImage.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    });

            holder.iv_item_readnote_img
                    .setOnLongClickListener(new OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {
                            // 长按图片显示删除图标 或 隐藏图标
                            if (list.get(position).isShow) {
                                isShow(false);
                            } else {
                                isShow(true);
                            }
                            return false;
                        }
                    });

            return convertView;
        }

        private class ViewHolder {
            private ImageView iv_item_readnote_img, iv_item_readnote_delete;
        }
    }

    public class ImageBean {
        public String url; // 本地图片地址path
        public boolean isShow = false;// 删除按钮是否显示
    }

    private void isShow(boolean isShow) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).isShow = isShow;
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置 相关书籍
     */
    public void setBookName(String bookname) {
        tv_publishfeelings_bookname.setText("《" + bookname + "》");
    }

    /**
     * 设置 谁可以看
     */
    public void setWho(String state) {
        tv_publishfeelings_state.setText(state);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isRefresh) {
            isRefresh = false;
            list.clear();
            ImageBean bean = new ImageBean();
            for (int i = 0; i < QQAlbumAdapter.mSelectedImage.size(); i++) {
                bean = new ImageBean();
                bean.url = QQAlbumAdapter.mSelectedImage.get(i);
                bean.isShow = false;
                list.add(bean);
            }
            adapter.notifyDataSetChanged();
            isShow(false);
        }
        if (isRefresh2) {
            isRefresh2 = false;
            // tv_readnote_name.setText(book_name);
            setIsEnabled();
        }
    }

    /**
     * 编辑 - 设置参数
     */
    private void setInfo() {
        et_publishfeelings_title.setText(bean.title);
        et_publishfeelings_content.setText(bean.content);
        if (!TextUtils.isEmpty(bean.book_title)) {
            setBookName(bean.book_title);
        }
        if (!TextUtils.isEmpty(bean.book_id)) {
            book_id = bean.book_id;
        }
        if ("1".equals(bean.is_pub)) {
            is_pub = 1;
            tv_publishfeelings_state.setText("秘密");
        } else {
            is_pub = 0;
            tv_publishfeelings_state.setText("公开");
        }
        // 设置图片,然后把图片保存到本地
        // TODO
        // 下载的图片放在fengwo_note_reading2这个文件夹里面
        File file = new File(Environment.getExternalStorageDirectory()
                + "/fengwo_note_reading2");
        if (!file.exists()) {
            // 为空
            if (file.mkdir()) {
                saveImage();
            }
        } else {
            // 不为空,全部清除
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
            saveImage();
        }
    }

    private void saveImage() {
        if (bean != null && bean.img_str != null && bean.img_str.length != 0) {
            list.clear();
            adapter.notifyDataSetChanged();
            for (i = 0; i < bean.img_str.length; i++) {
                final File f = new File(
                        Environment.getExternalStorageDirectory()
                                + "/fengwo_note_reading2",
                        (String.valueOf(bean.img_str[i].hashCode()) + ".jpg"));
                // 加载图片，然后存在本地
                ImageLoader.getInstance().loadImage(bean.img_str[i],
                        new ImageLoadingListener() {

                            @Override
                            public void onLoadingStarted(String imageUri,
                                                         View view) {
                            }

                            @Override
                            public void onLoadingFailed(String imageUri,
                                                        View view, FailReason failReason) {
                                try {
                                    Bitmap bitmap = BitmapFactory
                                            .decodeResource(fragment
                                                            .getActivity()
                                                            .getResources(),
                                                    R.drawable.cover);
                                    FileOutputStream out = new FileOutputStream(
                                            f);
                                    boolean b = bitmap
                                            .compress(
                                                    Bitmap.CompressFormat.PNG,
                                                    100, out);
                                    out.flush();
                                    out.close();
                                    if (b) {
                                        ImageBean bean = new ImageBean();
                                        bean.url = f.getPath();
                                        bean.isShow = false;
                                        list.add(bean);
                                        adapter.notifyDataSetChanged();
                                        QQAlbumActivity.mSelectedImage
                                                .add(bean.url);
                                        QQAlbumAdapter.mSelectedImage
                                                .add(bean.url);
                                    } else {

                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                // TODO
                                try {
                                    FileOutputStream out = new FileOutputStream(
                                            f);
                                    boolean b = loadedImage
                                            .compress(
                                                    Bitmap.CompressFormat.PNG,
                                                    100, out);
                                    out.flush();
                                    out.close();
                                    if (b) {
                                        // 刷新集合
                                        ImageBean bean = new ImageBean();
                                        bean.url = f.getPath();
                                        bean.isShow = false;
                                        list.add(bean);
                                        adapter.notifyDataSetChanged();
                                        QQAlbumActivity.mSelectedImage
                                                .add(bean.url);
                                        QQAlbumAdapter.mSelectedImage
                                                .add(bean.url);
                                    } else {

                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri,
                                                           View view) {

                            }
                        });
            }
        }
    }

//    public boolean isShouldHideInput(View v, MotionEvent event) {
//        if (v != null && (v instanceof EditText)) {
//            int[] leftTop = {0, 0};
//            v.getLocationInWindow(leftTop);
//            int left = leftTop[0];
//            int top = leftTop[1];
//            int bottom = top + v.getHeight();
//            int right = left + v.getWidth();
//            if (event.getX() > left && event.getX() < right
//                    && event.getY() > top && event.getY() < bottom) {
//                return false;
//            } else {
//                return true;
//            }
//        }
//        return false;
//    }

//    private static void put(Map<String, String> map) {
//        Collection<String> keyset = map.keySet();
//        List<String> list = new ArrayList<String>(keyset);
//        for (int i = 0; i < list.size(); i++) {
//            GlobalParams.NAMEVALUEPAIRS.add(new BaseNameValuePairs(list.get(i),
//                    map.get(list.get(i))));
//        }
//    }

    /**
     * 建立文件夹,用于存储图片
     */
    private void setFile() {
        lisFiles.clear();
        File file = new File(Environment.getExternalStorageDirectory()
                + "/fengwo_note_reading");
        if (!file.exists()) {
            // 为空
            if (file.mkdir()) {
                saveFile();
            }
        } else {
            // 不为空,全部清除
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
            saveFile();
        }
    }

    /**
     * 删除
     */
    private void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }

    /**
     * 存储图片
     */
    private void saveFile() {
        // TODO
        for (int i = 0; i < QQAlbumActivity.mSelectedImage.size(); i++) {
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/fengwo_note_reading", (i + ".jpg"));
            if (copyFile(QQAlbumActivity.mSelectedImage.get(i), file.getPath())) {
                lisFiles.add(file);
                try {
                    // 图片处理
                    Bitmap bm = getSmallBitmap(file.getPath());
                    FileOutputStream fos = new FileOutputStream(file);
                    bm.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        getData();
    }

    private boolean copyFile(String oldPath, String newPath) {
        boolean isok = true;
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();
                fs.close();
                inStream.close();
            } else {
                isok = false;
            }
        } catch (Exception e) {
            isok = false;
        }
        return isok;
    }

    private Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 发布 - 网络请求
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("title", et_publishfeelings_title.getText().toString());
        map.put("content", et_publishfeelings_content.getText().toString());
        map.put("is_pub", is_pub + "");
        if (!TextUtils.isEmpty(book_id)) {
            map.put("book_id", book_id);
        }
        if (!TextUtils.isEmpty(id) && source == 2) {
            // 笔记id (编辑时)
            map.put("id", id);
        }
        // 图片上传
        Map<String, File> filemap = null;
        if (lisFiles.size() != 0) {
            filemap = new HashMap<>();
            for (int i = 0; i < lisFiles.size(); i++) {
                filemap.put("img[" + i + "]", lisFiles.get(i));
            }
        }

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.note_save, new RequestCallBack<String>() {

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
                        MLog.v("reading", "note_save:" + '\r' + jsonString);
                        try {
//                            System.out.println("------jsonString:" + jsonString);
                            PublishFeelingsJson json = new Gson().fromJson(
                                    jsonString, PublishFeelingsJson.class);
                            if ("1".equals(json.code)) {
                                Toast.makeText(getActivity(), "发布成功", Toast.LENGTH_SHORT).show();
                                if (isQQZone) {
                                    UMShare.setUMeng(getActivity(), 3, et_publishfeelings_title.getText().toString(), et_publishfeelings_content.getText().toString(), json.img, GlobalConstant.SERVERURL
                                            .equals("http://api.fengwo.com/m/") ? "http://api.fengwo.com/"
                                            : "http://gongdu.youshu.cc/" + "share/note?note_id="
                                            + json.id, "", "");
                                }
                                EditTextUtils.hideSoftInput(et_publishfeelings_content, getActivity());
                                EditTextUtils.hideSoftInput(et_publishfeelings_title, getActivity());
                                // 微博同步
                                if (sendto_weibo) {
                                    SPUtils.getUserWeiboAccess_Token(fragment.getActivity());
                                    getDataWeibo(json.img, json.id);
                                }
                                SPUtils.setContent(getActivity(), "");
                                switch (source) {
                                    case 1:
                                        // 添加后的更新
                                        GroupFragment.getInstance().is_refresh = true;
                                        if (json.level_is_up != null) {
                                            GroupFragment.getInstance().level_is_up = json.level_is_up;
                                        }
                                        break;
                                    case 2:
                                        break;
                                    case 3:
                                        // 话题后的更新
                                        TopicsActivity.Activity.is_refresh = true;
                                        if (json.level_is_up != null) {
                                            TopicsActivity.Activity.level_is_up = json.level_is_up;
                                        }
                                        break;
                                    case 4:
                                        //任务模块
                                        ExplainFragment.getInstance().refresh(json.level_is_up);
//                                        ExplainFragment.getInstance().is_refresh = true;
//                                        ExplainFragment.getInstance().level_is_up = json.level_is_up;
                                        break;

                                    default:
                                        break;
                                }
                                getActivity().finish();
                                getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                            } else {
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    Toast.makeText(context, json.msg,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Context context = fragment.getActivity();
                            if (context != null) {
                                Toast.makeText(context,
                                        context.getString(R.string.json_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, true, filemap);
    }

    // 同步微博
    private void getDataWeibo(String img, String id) {
        Map<String, String> map = new HashMap<>();
        String str = "";
        String h5Url = GlobalConstant.SERVERURL
                .equals("http://api.fengwo.com/m/") ? "http://api.fengwo.com/"
                : "http://gongdu.youshu.cc/" + "share/note?note_id="
                + id;
        if (et_publishfeelings_content.getText().toString().length() > 100) {
            str = et_publishfeelings_content.getText().toString().substring(0, 100) + "...#有书共读#来自@有书共读" + h5Url;
        } else {
            str = et_publishfeelings_content.getText().toString() + "...#有书共读#来自@有书共读" + h5Url;
        }

        map.put("status", str);
        String weibo_url;

        if (img == null) {
            weibo_url = "https://api.weibo.com/2/statuses/update.json";
        } else if (img.equals("")) {
            weibo_url = "https://api.weibo.com/2/statuses/update.json";
        } else {
            map.put("url", img);
            weibo_url = "https://api.weibo.com/2/statuses/upload_url_text.json";

        }
        map.put("access_token", GlobalParams.weiboaccess_token);
        HttpParamsUtil.getWeiBoPost(map,
                weibo_url, new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String jsonString = responseInfo.result;
                        try {

                            CommentListJson json = new Gson().fromJson(
                                    jsonString, CommentListJson.class);
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(getActivity(), "共享微博失败", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // TODO
    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;
    /* 用来标识请求gallery的activity */
    private static final int PHOTO_REQUEST_GALLERY = 3021;// 从相册中选择
    /* 拍照的照片存储位置 */
    private static final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory() + GlobalParams.FolderPath_Shoot);
    private File mCurrentPhotoFile;// 照相机拍照得到的图片

    private SelectPicPopupWindow picMenuWindow;
    private OnClickListener itemsOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentActivity activity = fragment.getActivity();
            if (activity == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.btn_popupwindow_takephoto:// 拍照
                    try {
                        takePhone();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    break;
                case R.id.btn_popupwindow_pickphoto:// 相册
                    seelectPhoto();
                    break;
                default:
                    break;
            }
            picMenuWindow.dismiss();
        }
    };

    /**
     * 选择相册
     */
    private void seelectPhoto() {
        Intent intent = new Intent(fragment.getActivity(),
                QQAlbumActivity.class);
        startActivity(intent);
    }

    /**
     * 选择拍照
     */
    private void takePhone() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
            doTakePhoto();// 执行从照相机获取
        } else {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "没有SD卡", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * 拍照获取图片
     */
    protected void doTakePhoto() {// protected
        try {
            if (!PHOTO_DIR.exists()) {
                // 创建照片的存储目录
                if (PHOTO_DIR.mkdirs()) {
                    mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
                    Intent getImageByCamera = new Intent(
                            "android.media.action.IMAGE_CAPTURE");
                    Uri u = Uri.fromFile(mCurrentPhotoFile);
                    getImageByCamera.putExtra(
                            MediaStore.Images.Media.ORIENTATION, 0);
                    getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, u);
                    startActivityForResult(getImageByCamera, CAMERA_WITH_DATA);
                }
            } else {
                mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
                Intent getImageByCamera = new Intent(
                        "android.media.action.IMAGE_CAPTURE");
                Uri u = Uri.fromFile(mCurrentPhotoFile);
                getImageByCamera.putExtra(MediaStore.Images.Media.ORIENTATION,
                        0);
                getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, u);
                startActivityForResult(getImageByCamera, CAMERA_WITH_DATA);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "选择的照片未发现", Toast.LENGTH_SHORT)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用当前时间给取得的图片命名
     */
    @SuppressLint("SimpleDateFormat")
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    private Intent getTakePickIntent(File f) {// public static
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        try {
            switch (requestCode) {
                case CAMERA_WITH_DATA:// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                    // // System.out.println("=================="
                    // // + mCurrentPhotoFile.getAbsolutePath());
                    // QQAlbumActivity.mSelectedImage.add(mCurrentPhotoFile
                    // .getAbsolutePath());
                    // list.clear();
                    // ImageBean bean = new ImageBean();
                    // for (int i = 0; i < QQAlbumAdapter.mSelectedImage.size();
                    // i++) {
                    // bean = new ImageBean();
                    // bean.url = QQAlbumAdapter.mSelectedImage.get(i);
                    // bean.isShow = false;
                    // list.add(bean);
                    // }
                    // bean = new ImageBean();
                    // bean.url = mCurrentPhotoFile.getAbsolutePath();
                    // mCurrentPhotoFile = null;
                    // bean.isShow = false;
                    // list.add(bean);
                    // // bean = new ImageBean();
                    // // bean.url = null;
                    // // bean.isShow = false;
                    // // list.add(bean);
                    // adapter.notifyDataSetChanged();
                    // isShow(false);

                    Bitmap bitmap = null;
                    try {
                        // 对图片进行压缩
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        // 获取这个图片的宽和高
                        bitmap = BitmapFactory.decodeFile(
                                mCurrentPhotoFile.getPath(), options);// 此时返回bm为空
                        options.inJustDecodeBounds = false;
                        // 计算缩放比
                        int be = (int) (options.outHeight / (float) 800);// 配置图片分辨率
                        if (be <= 0) {
                            be = 1;
                        }
                        options.inSampleSize = be;
                        // 重新读入图片，注意这次要把options.inJustDecodeBounds设为false哦
                        bitmap = BitmapFactory.decodeFile(
                                mCurrentPhotoFile.getPath(), options);

                        // 保存入sdCard
                        File file2 = new File(mCurrentPhotoFile.getPath() + "");
                        // bitmap.recycle();
                        try {
                            FileOutputStream out = new FileOutputStream(file2);
                            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                                    out)) {
                                out.flush();
                                out.close();
                                QQAlbumActivity.mSelectedImage
                                        .add(mCurrentPhotoFile.getAbsolutePath());
                                list.clear();
                                ImageBean bean = new ImageBean();
                                for (int i = 0; i < QQAlbumAdapter.mSelectedImage
                                        .size(); i++) {
                                    bean = new ImageBean();
                                    bean.url = QQAlbumAdapter.mSelectedImage.get(i);
                                    bean.isShow = false;
                                    list.add(bean);
                                }
                                bean = new ImageBean();
                                bean.url = mCurrentPhotoFile.getAbsolutePath();
                                mCurrentPhotoFile = null;
                                bean.isShow = false;
                                list.add(bean);
                                adapter.notifyDataSetChanged();
                                isShow(false);
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }
                    } catch (OutOfMemoryError e) {
                        // TODO: handle exception
                        bitmap.recycle();
                        e.printStackTrace();
                    }

                    break;

                default:
                    break;
            }
        } catch (Exception e) {
        }
    }

    /**
     * 返回键
     */
    public void finish() {
        EditTextUtils.hideSoftInput(et_publishfeelings_content, getActivity());
        EditTextUtils.hideSoftInput(et_publishfeelings_title, getActivity());
        if (et_publishfeelings_title.getText().length() > 0 || et_publishfeelings_content.getText().length() > 0) {
            selectPicPopupWindow.showAtLocation(fragment.getActivity()
                    .findViewById(R.id.ll_activity_next), Gravity.BOTTOM
                    | Gravity.CENTER_HORIZONTAL, 0, 0);
        } else {
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    }

    /**
     * 添加标签后刷新方法,(末尾 或 光标位置??)
     */
    public void refresh(String str) {
        String s = et_publishfeelings_content.getText().toString() + str;
        et_publishfeelings_content.setText(s);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("PublishFeelingsFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("PublishFeelingsFragment");
    }

}