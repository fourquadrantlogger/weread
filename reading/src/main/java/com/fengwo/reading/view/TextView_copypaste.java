package com.fengwo.reading.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.text.ClipboardManager;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by timeloveboy on 16/3/29.
 */
public class TextView_copypaste extends EditText{
    private int off; // 字符串的偏移值

    public TextView_copypaste(Context context) {
        super(context);
        setGravity(Gravity.TOP);
        setBackgroundColor(Color.WHITE);
    }

    public TextView_copypaste(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.TOP);
        setBackgroundColor(Color.WHITE);
    }

    public TextView_copypaste(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setGravity(Gravity.TOP);
        setBackgroundColor(Color.WHITE);
    }
    @Override
    public boolean getDefaultEditable() {
            return false;
    }
    String text;
    /**
     * 判断是否有长按动作发生
     * @param lastX 按下时X坐标
     * @param lastY 按下时Y坐标
     * @param thisX 移动时X坐标
     * @param thisY 移动时Y坐标
     * @param lastDownTime 按下时间
     * @param thisEventTime 移动时间
     * @param longPressTime 判断长按时间的阀值
     */
    long longPressTime=500;
    boolean mIsLongPressedOK=false;
    float lastX,lastY;
    long lastDownTime;
    private boolean isLongPressed(float lastX,float lastY, float thisX,float thisY ,long thisEventTime){
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
        if(offsetX <=10 && offsetY<=10 && intervalTime >= longPressTime){
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int  action = event.getAction();
//        this.requestFocus();

        Layout layout = getLayout();
        int  line = 0;
        switch (action)
        {
            case  MotionEvent.ACTION_DOWN:

                ///
                lastX = event.getX();
                lastY = event.getY();
                lastDownTime = new Date().getTime();
                if(mIsLongPressedOK) {
                    //
                    SpannableStringBuilder BASEstyle=new SpannableStringBuilder(this.getText());
                    BASEstyle.setSpan(new BackgroundColorSpan(Color.WHITE),0,getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    this.setText(BASEstyle);
                    getParent().requestDisallowInterceptTouchEvent(true);

                    line = layout.getLineForVertical(getScrollY() + (int) event.getY());
                    off = layout.getOffsetForHorizontal(line, (int) event.getX());

                }
                break;
            case  MotionEvent.ACTION_MOVE:
                if(mIsLongPressedOK) {
                    try{
                    line = layout.getLineForVertical(getScrollY() + (int) event.getY());
                    int curOff = layout.getOffsetForHorizontal(line, (int) event.getX());
                    if(curOff<off){
                        int temp=off;
                        off=curOff;
                        curOff=temp;
                    }

                    Selection.setSelection(getEditableText(), off, curOff);
                    SpannableStringBuilder style=new SpannableStringBuilder(this.getText());
                    style.setSpan(new BackgroundColorSpan(Color.rgb(225, 225, 225)), off, curOff, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    this.setText(style);
                    text = getText().subSequence(off, curOff).toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break ;
            case  MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                Log.v("moe", mIsLongPressedOK + "");
                if( mIsLongPressedOK ){
                    //长按模式所做的事
                    showDialog();

                    mIsLongPressedOK=false;
                }else{
                    //移动模式所做的事
                }
                if(mIsLongPressedOK==false){
                    mIsLongPressedOK = isLongPressed(lastX, lastY,event.getX(),event.getY(),new Date().getTime());
                    if(mIsLongPressedOK==true) {
                        setEditableFactory(android.text.Editable.Factory.getInstance());
                        Vibrator vibrator = (Vibrator) getContext().getSystemService(getContext().VIBRATOR_SERVICE);
                        vibrator.vibrate(50);
                        Selection.setSelection(getEditableText(), 0, getEditableText().length());
                        SpannableStringBuilder style=new SpannableStringBuilder( getText());
                        style.setSpan(new BackgroundColorSpan(Color.rgb(225, 225, 225)), 0,getEditableText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        setText(style);
                        text = getText().subSequence( 0, getEditableText().length()).toString();
                        showDialog();
                    }
                    else {
                        Selection.setSelection(getEditableText(), off);
                    }
                }else {
                    Selection.setSelection(getEditableText(), off);
                }
                break ;
        }
        return   true ;
    }
    private PopupWindow_copypaste mCommentPopup;
    public void showDialog(){

        mCommentPopup = new PopupWindow_copypaste(getContext());
        mCommentPopup.setOnCommentPopupClickListener(new PopupWindow_copypaste.OnCommentPopupClickListener() {
            @Override
            public void onLikeClick(View v, TextView likeText) {

                ClipboardManager cmb = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(text);
                Log.v("moe", "复制文本:" + text);
                Toast.makeText(getContext(), "复制成功", Toast.LENGTH_SHORT).show();
                mCommentPopup.dismiss();
            }

            @Override
            public void onCommentClick(View v) {
                Selection.setSelection(getEditableText(), 0, getEditableText().length());
                SpannableStringBuilder style=new SpannableStringBuilder( getText());
                style.setSpan(new BackgroundColorSpan(Color.rgb(225, 225, 225)), 0,getEditableText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                setText(style);
                text = getText().subSequence( 0, getEditableText().length()).toString();

            }
        });
        mCommentPopup.showPopupWindow(this);
    }
}

