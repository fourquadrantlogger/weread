package com.fengwo.reading.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengwo.reading.R;

/**
 * Created by timeloveboy on 16/5/9.
 */
public class fav_select extends LinearLayout {

    public TextView getTextView_right() {
        return textView_right;
    }

    public TextView getTextView_left() {
        return textView_left;
    }
    public boolean getselect(){
        return selectleft;
    }
    boolean selectleft=true;
    public void selectLeft(boolean left){
        selectleft=left;
        if(left){
            textView_left.setBackgroundDrawable(getResources().getDrawable(R.drawable.fav_left_pressed));
            textView_right.setBackgroundDrawable(getResources().getDrawable(R.drawable.fav_right));
            textView_left.setTextColor(getResources().getColor(R.color.green_17));
            textView_right.setTextColor(getResources().getColor(R.color.white));
        }else {
            textView_left.setTextColor(getResources().getColor(R.color.white));
            textView_right.setTextColor(getResources().getColor(R.color.green_17));
            textView_left.setBackgroundDrawable(getResources().getDrawable(R.drawable.fav_left));
            textView_right.setBackgroundDrawable(getResources().getDrawable(R.drawable.fav_right_pressed));
        }
    }
    TextView textView_left,textView_right;

    public fav_select(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_fav_select, this);
        findViewById_init(getRootView());
    }

    public fav_select(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_fav_select, this);
        findViewById_init(getRootView());
    }

    public fav_select(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_fav_select, this);
        findViewById_init(getRootView());
    }
    void findViewById_init(View view){
        textView_left=(TextView)view.findViewById(R.id.textView_left);
        textView_right=(TextView)view.findViewById(R.id.textView_right);

        selectLeft(selectleft);
    }
}
