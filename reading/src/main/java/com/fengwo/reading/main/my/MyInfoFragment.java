package com.fengwo.reading.main.my;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.bean.UserInfoBean;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.common.SelectLocationPopupWindow;
import com.fengwo.reading.common.SelectPicPopupWindow;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.PicUtil;
import com.fengwo.reading.utils.VersionUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lxq - 个人信息
 */
public class MyInfoFragment extends Fragment implements OnClickListener {

    private ImageView iv_title_left;
    private TextView tv_title_mid;

    private ImageView iv_myinfo_avatar;
    private TextView tv_myinfo_name, tv_myinfo_sex, tv_myinfo_job,
            tv_myinfo_location, tv_myinfo_intro;
    private RelativeLayout rl_myinfo_name, rl_myinfo_sex, rl_myinfo_job,
            rl_myinfo_location, rl_myinfo_intro, rl_myinfo_avatar;

    private CustomProgressDialog progressDialog;

    public int source;  //0:Activity 1:Fragment

    private View saveView = null;
    public boolean needSaveView = false;

    private MyInfoFragment() {

    }

    private static MyInfoFragment fragment = new MyInfoFragment();

    public static MyInfoFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater
                .inflate(R.layout.fragment_myinfo, container, false);

        findViewById(view);
        onClickListener();

        tv_title_mid.setText("个人信息");
        tv_title_mid.setVisibility(View.VISIBLE);

        progressDialog = CustomProgressDialog.createDialog(fragment
                .getActivity());

        if (GlobalParams.userInfoBean == null) {
            GlobalParams.userInfoBean = new UserInfoBean();
            GlobalParams.userInfoBean.user_id=GlobalParams.uid;
        }
        setUserInfo();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("MyInfoFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MyInfoFragment");
    }

    private void setUserInfo() {
        if (TextUtils.isEmpty(GlobalParams.userInfoBean.avatar)) {
            iv_myinfo_avatar.setImageResource(R.drawable.avatar);
        } else {
            DisplayImageUtils.displayImage(GlobalParams.userInfoBean.avatar,
                    iv_myinfo_avatar, 100, R.drawable.avatar);
        }
        tv_myinfo_name.setText(GlobalParams.userInfoBean.name);
//        tv_myinfo_name.setCompoundDrawables(null, null, GlobalParams.userInfoBean.badge_Drawable(0.12f, 0.12f), null);
        if ("2".equals(GlobalParams.userInfoBean.sex)) {
            tv_myinfo_sex.setText("女");
        } else if ("1".equals(GlobalParams.userInfoBean.sex)) {
            tv_myinfo_sex.setText("男");
        } else {
            tv_myinfo_sex.setText("");
        }
        tv_myinfo_job.setText(GlobalParams.userInfoBean.job);
        String location = GlobalParams.userInfoBean.province + " "
                + GlobalParams.userInfoBean.city;
        if (location.length() == 1) {
            location = "";
        }
        tv_myinfo_location.setText(location);
        tv_myinfo_intro.setText(GlobalParams.userInfoBean.intro);
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);

        rl_myinfo_avatar = (RelativeLayout) view.findViewById(R.id.rl_myinfo_avatar);
        iv_myinfo_avatar = (ImageView) view.findViewById(R.id.iv_myinfo_avatar);

        tv_myinfo_name = (TextView) view.findViewById(R.id.tv_myinfo_name);
        tv_myinfo_sex = (TextView) view.findViewById(R.id.tv_myinfo_sex);
        tv_myinfo_job = (TextView) view.findViewById(R.id.tv_myinfo_job);
        tv_myinfo_location = (TextView) view
                .findViewById(R.id.tv_myinfo_location);
        tv_myinfo_intro = (TextView) view.findViewById(R.id.tv_myinfo_intro);

        rl_myinfo_name = (RelativeLayout) view
                .findViewById(R.id.rl_myinfo_name);
        rl_myinfo_sex = (RelativeLayout) view.findViewById(R.id.rl_myinfo_sex);
        rl_myinfo_job = (RelativeLayout) view.findViewById(R.id.rl_myinfo_job);
        rl_myinfo_location = (RelativeLayout) view
                .findViewById(R.id.rl_myinfo_location);
        rl_myinfo_intro = (RelativeLayout) view
                .findViewById(R.id.rl_myinfo_intro);

    }

    private void onClickListener() {
        iv_title_left.setOnClickListener(this);

        rl_myinfo_avatar.setOnClickListener(this);
        iv_myinfo_avatar.setOnClickListener(this);

        rl_myinfo_name.setOnClickListener(this);
        rl_myinfo_sex.setOnClickListener(this);
        rl_myinfo_job.setOnClickListener(this);
        rl_myinfo_location.setOnClickListener(this);
        rl_myinfo_intro.setOnClickListener(this);
    }

    /**
     * 所有点击事件
     */
    @Override
    public void onClick(View v) {
        Context context = fragment.getActivity();
        if (context == null) {
            return;
        }
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom,
                R.anim.out_to_top, R.anim.in_from_top, R.anim.out_to_bottom);
        switch (v.getId()) {
            case R.id.iv_return:
                switch (source) {
                    case 0:
                        fragment.getActivity().finish();
                        fragment.getActivity().overridePendingTransition(
                                R.anim.in_from_left, R.anim.out_to_right);
                        break;
                    case 1:
                        getActivity().getSupportFragmentManager().popBackStack();
                        break;
                }
                break;
            case R.id.rl_myinfo_avatar:
            case R.id.iv_myinfo_avatar:
                picPopupWindow = new SelectPicPopupWindow(getActivity(),
                        avatarListener);
                picPopupWindow.setBtnText("拍照", "相册");
                picPopupWindow.showAtLocation(
                        getActivity().findViewById(R.id.ll_myinfo), Gravity.BOTTOM
                                | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.rl_myinfo_name:
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next,
                        MyInfoNameFragment.getInstance());
                transaction.commit();
                MyInfoNameFragment.getInstance().needSaveView = false;
                break;
            case R.id.rl_myinfo_sex:
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next,
                        MyInfoSexFragment.getInstance());
                transaction.commit();
                MyInfoSexFragment.getInstance().needSaveView = false;
                break;
            case R.id.rl_myinfo_job:
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next,
                        MyInfoJobFragment.getInstance());
                transaction.commit();
                MyInfoJobFragment.getInstance().needSaveView = false;
                break;
            case R.id.rl_myinfo_location:
                locationPopupWindow = new SelectLocationPopupWindow(context,
                        itemsOnClickLocation);
                locationPopupWindow.showAtLocation(fragment.getActivity()
                        .findViewById(R.id.ll_myinfo), Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.rl_myinfo_intro:
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next,
                        MyInfoIntroFragment.getInstance());
                transaction.commit();
                MyInfoIntroFragment.getInstance().needSaveView = false;
                break;

            default:
                break;
        }
    }

    private SelectPicPopupWindow picPopupWindow;

    private OnClickListener avatarListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_popupwindow_takephoto:// 拍照
                    picPopupWindow.dismiss();
                    takePhone();
                    break;
                case R.id.btn_popupwindow_pickphoto:// 照片
                    picPopupWindow.dismiss();
                    seelectPhoto();
                    break;
                default:
                    break;
            }
        }
    };

    // TODO
    private SelectLocationPopupWindow locationPopupWindow;

    private OnClickListener itemsOnClickLocation = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_location_submit:
                    locationPopupWindow.dismiss();
                    tv_myinfo_location.setText(locationPopupWindow
                            .getProvinceName()
                            + " "
                            + locationPopupWindow.getCityName());
                    getDataLocation(locationPopupWindow.getProvinceName(),
                            locationPopupWindow.getCityName());
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取数据(个人信息保存)
     */
    private void getDataAvatar() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("soft", VersionUtils.getVersion(getActivity()));
        Map<String, File> map2 = new HashMap<String, File>();
        File file = new File(Environment.getExternalStorageDirectory(),
                "reading.png");
        map2.put("img", file);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.userinfo_save, new RequestCallBack<String>() {

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
                        MLog.v("reading", jsonString);
                        try {
                            UserinfoSaveJson json = new Gson().fromJson(
                                    jsonString, UserinfoSaveJson.class);
                            if ("1".equals(json.code)) {
                                if (json.user_data != null) {
                                    GlobalParams.userInfoBean = json.user_data;
                                    NOsqlUtil.set_userInfoBean(GlobalParams.userInfoBean);
                                }
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    CustomToast.showToast(context, "头像修改成功");
                                }
                            } else {
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    CustomToast.showToast(context, json.msg);
                                }
                            }
                        } catch (Exception e) {
                            Context context = fragment.getActivity();
                            if (context != null) {
                                CustomToast.showToast(context,
                                        context.getString(R.string.json_error));
                            }
                        }
                    }
                }, true, map2);
    }

    /**
     * 获取数据(个人信息保存)
     */
    private void getDataLocation(String province, String city) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", GlobalParams.uid);
        map.put("province", province);
        map.put("city", city);
        map.put("soft", VersionUtils.getVersion(getActivity()));
        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.userinfo_save, new RequestCallBack<String>() {

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
                        MLog.v("reading",jsonString);
                        try {
                            UserinfoSaveJson json = new Gson().fromJson(
                                    jsonString, UserinfoSaveJson.class);
                            if ("1".equals(json.code)) {
                                if (json.user_data != null) {
                                    GlobalParams.userInfoBean = json.user_data;
                                    NOsqlUtil.set_userInfoBean(GlobalParams.userInfoBean);
                                }
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    CustomToast.showToast(context, "修改成功");
                                }
                            } else {
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    CustomToast.showToast(context, json.msg);
                                }
                            }
                        } catch (Exception e) {
                            Context context = fragment.getActivity();
                            if (context != null) {
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

    // TODO
    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;
    /* 用来标识请求照片剪辑功能的activity */
    private static final int CROP_PHOTO = 3024;
    /* 用来标识请求gallery的activity */
    private static final int PHOTO_REQUEST_GALLERY = 3021;// 从相册中选择
    /* 拍照的照片存储位置 */
    private static final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Shoot);
    private File mCurrentPhotoFile;// 照相机拍照得到的图片

    private void takePhone() {// 点击拍照按钮选择头像的事件
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
            PHOTO_DIR.mkdirs();// 创建照片的存储目录
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //调用系统相机
            mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
            Uri imageUri = Uri.fromFile(mCurrentPhotoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//指定照片保存路径
            startActivityForResult(intent, CAMERA_WITH_DATA);  //用户点击了从相机获取
        } catch (ActivityNotFoundException e) {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "选择的照片未发现", Toast.LENGTH_SHORT)
                        .show();
            }
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
        try {
            if (resultCode != Activity.RESULT_OK)
                return;
            switch (requestCode) {
                case CAMERA_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                    scanPhotos(mCurrentPhotoFile.getAbsolutePath(), getActivity());// 获得新拍图片路径,然后刷新图库,让其出现在图库中
                    doCropPhoto(mCurrentPhotoFile);
                    break;
                }
                case CROP_PHOTO:// 剪辑完成后返回调用
                    if (data == null) {
                        return;
                    }
                    final Bitmap photo = data.getParcelableExtra("data");
                    Bitmap bitmap = PicUtil.getRoundedCornerBitmap(photo, 2);
                    iv_myinfo_avatar.setImageBitmap(bitmap);
                    try {
                        boolean flag = photo.compress(
                                Bitmap.CompressFormat.PNG,
                                100,
                                new FileOutputStream(new File(Environment
                                        .getExternalStorageDirectory(),
                                        "reading.png")));
                        if (flag) {
                            getDataAvatar();
                        } else {
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;

                case PHOTO_REQUEST_GALLERY:
                    if (data != null) {
                        startPhotoZoom(data.getData(), 150);
                    }
                    break;

            }
        } catch (Exception e) {
        }

    }

    /**
     * 扫描、刷新相册
     */
    private void scanPhotos(String filePath, Context context) {// public static
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    private void doCropPhoto(File f) {// protected
        try {
            // 启动gallery去剪辑这个照片
            final Intent intent = getCropImageIntent(Uri.fromFile(f));
            startActivityForResult(intent, CROP_PHOTO);
        } catch (Exception e) {
        }
    }

    /**
     * 调用图片剪辑程序
     */
    private Intent getCropImageIntent(Uri photoUri) {// public static
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 80);
        intent.putExtra("outputY", 80);
        intent.putExtra("return-data", true);

        return intent;
    }

    private void seelectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CROP_PHOTO);
    }

    // TODO
    public void refresh() {
        setUserInfo();
    }

}
