package com.fengwo.reading.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.main.discover.PrivilegeService;
import com.fengwo.reading.main.discover.PrivilegeUtil;

/**
 * 特权
 */
public class PrivilegeDialog extends Dialog {

    private ImageView iv_privilege_img;
    private TextView tv_privilege_title, tv_privilege_content;
    private View.OnClickListener onClickListener;
    private int type;

    public PrivilegeDialog(Context context,
                           View.OnClickListener onClickListener, int type) {
        super(context, R.style.dialog);
        this.onClickListener = onClickListener;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_privilege);

        ImageView cancel = (ImageView) findViewById(R.id.iv_privilege_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        iv_privilege_img = (ImageView) findViewById(R.id.iv_privilege_img);
        tv_privilege_title = (TextView) findViewById(R.id.tv_privilege_title);
        tv_privilege_content = (TextView) findViewById(R.id.tv_privilege_content);

        switch (type) {
            case 1:
                iv_privilege_img.setImageResource(R.drawable.privilege_failed);
                tv_privilege_title.setText("二维码已过期");
                tv_privilege_content.setText("刷新二维码");
                break;
            case 2:
                iv_privilege_img.setImageResource(R.drawable.privilege_succeed);
                tv_privilege_title.setText("解锁成功");
                tv_privilege_content.setText("查看往期领读合辑");
                break;
        }

        tv_privilege_content.setOnClickListener(onClickListener);
        setCanceledOnTouchOutside(false);
    }

}
