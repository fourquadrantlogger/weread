package com.fengwo.reading.common;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;

import com.fengwo.reading.R;
import com.fengwo.reading.utils.CustomToast;

/**
 * 通用的旋转进度条
 */
public class CommonHandler {

    public CommonHandler(Context context, DialogInterface.OnCancelListener cancelListener) {
        this.context = context;
        progressDialog = CustomProgressDialog.createDialog(context);
        progressDialog.setCancelable(true);
        if (cancelListener != null) {
            progressDialog.setOnCancelListener(cancelListener);
        } else {
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    progressDialog.dismiss();
                }
            });
        }
    }

    public void sendEmptyMessage(int msg) {
        handler.sendEmptyMessage(msg);
    }

    private Context context;
    private CustomProgressDialog progressDialog;
    public static int MSG_START = 0, MSG_OK = 1, MSG_ERROR = 2;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 2:
                    if (context != null) {
                        CustomToast.showToast(context, context.getString(R.string.network_check));
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
}
