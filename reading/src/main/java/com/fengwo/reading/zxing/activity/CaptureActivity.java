package com.fengwo.reading.zxing.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.fengwo.reading.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.activity.BaseActivity;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.common.MyProgressDialog;
import com.fengwo.reading.main.discover.DiscoverJson;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.ActivityUtil;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.VersionUtils;
import com.fengwo.reading.zxing.camera.CameraManager;
import com.fengwo.reading.zxing.decoding.CaptureActivityHandler;
import com.fengwo.reading.zxing.decoding.InactivityTimer;
import com.fengwo.reading.zxing.decoding.RGBLuminanceSource;
import com.fengwo.reading.zxing.view.ViewfinderView;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * 扫描
 */
public class CaptureActivity extends BaseActivity implements Callback {

    public String type;//pack:特权 web_login:PC端登录

    private TextView title;
    private Button bt_album;

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    private String Text;

    private MyProgressDialog progressDialog;
    private TextView textView;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zxing_capture);

        ActivityUtil.captureActivity = this;

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type", "");
        }

        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

        progressDialog = new MyProgressDialog(this);

        textView = new TextView(this);
        textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        int left = (int) (screenWidth * 0.15625);
        int right = (int) (screenWidth * 0.84375);
        int top = (int) (screenHeight * 0.175);
        int bottom = top + right - left;
        int margin = (int) (screenHeight * 0.06);
        textView.setPadding(0, bottom + margin, 0, 0);
        textView.setTextColor(getResources().getColor(R.color.text_99));
        textView.setTextSize(15);

        ImageView imageView = (ImageView) findViewById(R.id.zxing_back);
        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title = (TextView) findViewById(R.id.zxing_title);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        relativeLayout.addView(textView);
        bt_album = (Button) findViewById(R.id.bt_album);
        bt_album.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 100);
            }
        });

        title.setText("扫一扫");
        textView.setText("将需扫描部份放入框内，即可自动扫描\n（本版本扫一扫只用于解锁“往期共读”）");
        bt_album.setVisibility(View.GONE);

        // TODO
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    // @Override
    // public void onBackPressed() {
    // super.onBackPressed();
    // finish();
    // overridePendingTransition(R.anim.in_from_left,
    // R.anim.out_to_right);
    // }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            Uri originalUri = data.getData(); // 获得图片的uri
            String[] proj = {MediaStore.Images.Media.DATA};
            // 好像是android多媒体数据库的封装接口，具体的看Android文档
            @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(originalUri, proj, null, null, null);
            // 按我个人理解 这个是获得用户选择的图片的索引值
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            // 将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            // 最后根据索引值获取图片路径
            final String path = cursor.getString(column_index);

            Result result = scanningImage(path);
            if (result != null) {
                dealwithmessage(result);
            } else {
                handler2.sendEmptyMessage(0);
            }
        }
    }

    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        MultiFormatReader multiFormatReader = new MultiFormatReader();

        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); // 设置二维码内容的编码

        multiFormatReader.setHints(hints);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        try {
            RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
            BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
            return multiFormatReader.decodeWithState(bitmap1);
        } catch (ReaderException re) {
        } finally {
            multiFormatReader.reset();
        }
        return null;
    }

    private void dealwithmessage(Result obj) {
//        String Barcode = obj.getBarcodeFormat().toString();
        Text = obj.getText().toString();
        restartDecode();
        if ("".equals(Text)) {
            Toast.makeText(CaptureActivity.this, "Scan failed!",
                    Toast.LENGTH_SHORT).show();
        } else {
            switch (type) {
                case "pack":
                    long l = 0;
                    //二维码扫描成功(往期书单特权)
                    try {
                        String str1 = Text.substring(Text.indexOf("|") + 1, Text.length());
                        l = Long.valueOf(str1.substring(str1.indexOf("|") + 1, str1.length())).longValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if ((System.currentTimeMillis() / 1000) - l >= 300) {
                        Toast.makeText(CaptureActivity.this, "此二维码已过期,请刷新后再试", Toast.LENGTH_SHORT).show();
                    } else {
                        getData();
                    }
                    break;
                case "web_login":
                    break;
            }

        }
    }

    /**
     * 是否解锁
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("type", type);
        String str1 = Text.substring(Text.indexOf("|") + 1, Text.length());
        map.put("user_lock_id", str1.substring(0, str1.indexOf("|")));

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.user_unlock, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        if (progressDialog != null
                                && !progressDialog.isShowing()) {
                            progressDialog.show();
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if (progressDialog != null
                                && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        String jsonString = responseInfo.result;
                        try {
                            BaseJson json = new Gson().fromJson(jsonString,
                                    BaseJson.class);
                            if ("1".equals(json.code)) {
                                Toast.makeText(CaptureActivity.this, json.msg,
                                        Toast.LENGTH_SHORT).show();
                                CaptureActivity.this.finish();
                            } else {
                                Toast.makeText(CaptureActivity.this, json.msg,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            if (this != null) {
                                e.printStackTrace();
                                Toast.makeText(CaptureActivity.this,
                                        CaptureActivity.this.getString(R.string.json_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        if (progressDialog != null
                                && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(CaptureActivity.this, CaptureActivity.this.getString(R.string.json_error),
                                Toast.LENGTH_SHORT).show();
                        restartDecode();
                    }
                }, true, null);
    }

    private Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            Toast.makeText(CaptureActivity.this, "二维码无法识别", Toast.LENGTH_LONG)
                    .show();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // SurfaceView surfaceView = (SurfaceView)
        // findViewById(R.id.preview_view);
        // SurfaceHolder surfaceHolder = surfaceView.getHolder();
        // if (hasSurface) {
        // initCamera(surfaceHolder);
        // } else {
        // surfaceHolder.addCallback(this);
        // surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // }
        // decodeFormats = null;
        // characterSet = null;
        //
        // playBeep = true;
        // AudioManager audioService = (AudioManager)
        // getSystemService(AUDIO_SERVICE);
        // if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
        // {
        // playBeep = false;
        // }
        // initBeepSound();
        // vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        dealwithmessage(result);
    }

    private void restartDecode() {
        // 实现连续扫描
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, 2000);
        }
        if (TextUtils.isEmpty(Text)) {
            textView.setText("扫描失效，请重新扫描");
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            textView.setText("摄像头调取失败，请从设置权限中开启。");
            // return;
        } catch (RuntimeException e) {
            textView.setText("摄像头调取失败，请从设置权限中开启。");
            // return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(CaptureActivity.this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

}