package com.fengwo.reading.main.my;

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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.common.SelectPicPopupWindow;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.qq.QQAlbumActivity;
import com.fengwo.reading.qq.QQAlbumAdapter;
import com.fengwo.reading.utils.EditTextUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.NetUtil;
import com.fengwo.reading.utils.VersionUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 反馈
 */
public class SuggestFragment extends Fragment implements OnClickListener {

    private LinearLayout ll_suggest;
    private RelativeLayout rl_suggest_tianjia;
    private GridView gridView;
    private ImageView iv_title_left;
    private TextView tv_title_right, tv_title_mid;
    private EditText et_suggest_content, et_suggest_contact;
    private CustomProgressDialog progressDialog;

    private int width = 65;
    private List<ImageBean> list;
    private MyReadNoteAdapter adapter;
    private List<File> lisFiles;
    public static boolean isRefresh = false;// 用于相册的数据刷新

    private String phone = "";
    private View saveView = null;
    public boolean needSaveView = false;

    private static SuggestFragment fragment = new SuggestFragment();

    public static SuggestFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (needSaveView && saveView != null) {
            return saveView;
        }
//        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_suggest, container,
                false);

        findViewById(view);
        onClickListener();

        progressDialog = CustomProgressDialog.createDialog(fragment.getActivity());

        tv_title_right.setVisibility(View.VISIBLE);
        tv_title_right.setText("提交");
        tv_title_mid.setVisibility(View.VISIBLE);
        tv_title_mid.setText("意见反馈");

        list = new ArrayList<ImageBean>();
        adapter = new MyReadNoteAdapter(getActivity(), list);
        gridView.setAdapter(adapter);

        // GridView的尺寸
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = (metric.widthPixels / 5);
        rl_suggest_tianjia.setVisibility(View.VISIBLE);

        lisFiles = new ArrayList<>();

        QQAlbumActivity.mSelectedImage.clear();
        QQAlbumAdapter.mSelectedImage.clear();

        return view;
    }

    private void findViewById(View view) {
        ll_suggest = (LinearLayout) view.findViewById(R.id.ll_suggest);
        rl_suggest_tianjia = (RelativeLayout) view.findViewById(R.id.rl_suggest_tianjia);
        gridView = (GridView) view.findViewById(R.id.gv_suggest_phone);
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        tv_title_right = (TextView) view.findViewById(R.id.tv_title_right);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);
        et_suggest_content = (EditText) view
                .findViewById(R.id.et_suggest_content);
        et_suggest_contact = (EditText) view
                .findViewById(R.id.et_suggest_contact);
    }

    private void onClickListener() {
        ll_suggest.setOnClickListener(this);
        rl_suggest_tianjia.setOnClickListener(this);
        iv_title_left.setOnClickListener(this);
        tv_title_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Context context = fragment.getActivity();
        if (context == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.ll_suggest:
                EditTextUtils.hideSoftInput(et_suggest_content, context);
                EditTextUtils.hideSoftInput(et_suggest_contact, context);
                break;
            case R.id.rl_suggest_tianjia:
                //添加图片
                if (list.size() >= 9) {
                    Toast.makeText(getActivity(), "最多选择9张图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                EditTextUtils.hideSoftInput(et_suggest_content, getActivity());
                EditTextUtils.hideSoftInput(et_suggest_contact, getActivity());

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
            case R.id.iv_return:
                fragment.getActivity().finish();
                fragment.getActivity().overridePendingTransition(R.anim.in_from_left,
                        R.anim.out_to_right);
                EditTextUtils.hideSoftInput(et_suggest_content, context);
                EditTextUtils.hideSoftInput(et_suggest_contact, context);
                break;
            case R.id.tv_title_right:
                //提交反馈
                if (TextUtils.isEmpty(et_suggest_content.getText().toString())) {
                    Toast.makeText(context, "内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isEmail(et_suggest_contact.getText().toString())) {
                    Toast.makeText(context, "抱歉,邮箱格式不正确", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                // 机型，系统 ，网络类型，版本
                StringBuilder builder = new StringBuilder();
                builder.append("机型：" + android.os.Build.MODEL);
                builder.append("系统：" + android.os.Build.VERSION.RELEASE);
                builder.append("网络："
                        + NetUtil.getNetworkType(fragment.getActivity()));
                builder.append("版本：" + VersionUtils.getVersion(getActivity()));
                phone = builder.toString();
                getData();
                break;

            default:
                break;
        }
    }

    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("msg", et_suggest_content.getText().toString());
        map.put("lianxi", et_suggest_contact.getText().toString());
        map.put("soft", VersionUtils.getVersion(getActivity()));
        // 判断手机信息是否获取到
        if (!TextUtils.isEmpty(phone)) {
            map.put("phone", phone);
        } else {
            map.put("phone", "未获取到手机信息");
        }
        // 图片上传
        Map<String, File> filemap = null;
        if (lisFiles.size() != 0) {
            filemap = new HashMap<>();
            for (int i = 0; i < lisFiles.size(); i++) {
                filemap.put("img[" + i + "]", lisFiles.get(i));
            }
        }

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.msg_save,
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
                            BaseJson json = new Gson().fromJson(jsonString,
                                    BaseJson.class);
                            if ("1".equals(json.code)) {
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    Toast.makeText(context, "反馈成功",
                                            Toast.LENGTH_SHORT).show();
                                }
                                fragment.getActivity().finish();
                                fragment.getActivity().overridePendingTransition(
                                        R.anim.in_from_left,
                                        R.anim.out_to_right);
                                EditTextUtils.hideSoftInput(et_suggest_content,
                                        getActivity());
                                EditTextUtils.hideSoftInput(et_suggest_content,
                                        getActivity());
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

    /**
     * 判断邮箱格式的正则表达式
     */
    private boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
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
                    .setOnLongClickListener(new View.OnLongClickListener() {

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
                    Intent getImageByCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri u = Uri.fromFile(mCurrentPhotoFile);
                    getImageByCamera.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                    getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, u);
                    startActivityForResult(getImageByCamera, CAMERA_WITH_DATA);
                }
            } else {
                mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
                Intent getImageByCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri u = Uri.fromFile(mCurrentPhotoFile);
                getImageByCamera.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        try {
            switch (requestCode) {
                case CAMERA_WITH_DATA:// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
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
                        // 重新读入图片，注意这次要把options.inJustDecodeBounds设为false
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

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
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

        ;
    };

    @Override
    public void onStart() {
        super.onStart();
        if (!needSaveView) {
            needSaveView = true;
            et_suggest_content.setText("");
            et_suggest_contact.setText("");
        }
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
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("SuggestFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SuggestFragment");
    }

}
