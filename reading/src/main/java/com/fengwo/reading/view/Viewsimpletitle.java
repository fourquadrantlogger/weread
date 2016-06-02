package com.fengwo.reading.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;

/**
 * Created by timeloveboy on 16/3/23.
 */
public class Viewsimpletitle extends RelativeLayout {
    public ImageView getImageView_back() {
        return imageView_back;
    }

    ImageView imageView_back;

    public ImageView getImageView_history() {
        return imageView_history;
    }

    ImageView imageView_history;

    public TextView getTextView_title() {
        return textView_title;
    }

    TextView textView_title;

    public Viewsimpletitle(final Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_simple_title, this);

        imageView_back = (ImageView) findViewById(R.id.iv_return);
        imageView_history = (ImageView) findViewById(R.id.iv_title_right);
        textView_title = (TextView) findViewById(R.id.textView_title);
    }
}
