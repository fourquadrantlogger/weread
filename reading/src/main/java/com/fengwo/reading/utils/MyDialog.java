package com.fengwo.reading.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fengwo.reading.R;

/**
 * 自定义dialog
 * author Song
 * Created by Administrator on 2016/5/24.
 */
public class MyDialog extends Dialog {
    TextView tv_dialog_1,tv_dialog_2,tv_dialog_3;
    String name_1,name_2;
    private MyDialogInterfaceListener myDialogInterfaceListener;
    public interface MyDialogInterfaceListener{
        public void callBack();
    }
    public MyDialog(Context context,int theme,String name1,String name2,MyDialogInterfaceListener myDialogInterfaceListener) {
        super(context,theme);
        this.name_1 = name1;
        this.name_2 = name2;
        this.myDialogInterfaceListener = myDialogInterfaceListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.my_dialog);

        tv_dialog_1 = (TextView) findViewById(R.id.tv_dialog_1);
        tv_dialog_2 = (TextView) findViewById(R.id.tv_dialog_2);
        tv_dialog_3 = (TextView) findViewById(R.id.tv_dialog_3);
        tv_dialog_1.setText(name_1);
        tv_dialog_2.setText(name_2);
        tv_dialog_2.setOnClickListener(clickListener);
        tv_dialog_3.setOnClickListener(clickListener);
    }
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_dialog_2:
                    MyDialog.this.myDialogInterfaceListener.callBack();
                    MyDialog.this.dismiss();
                    break;
                case R.id.tv_dialog_3:
                    MyDialog.this.dismiss();
                    break;
            }
        }
    };
}
