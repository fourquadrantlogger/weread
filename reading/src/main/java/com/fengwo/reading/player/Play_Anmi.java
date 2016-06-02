package com.fengwo.reading.player;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.fengwo.reading.R;

/**
 * Created by timeloveboy on 16/5/20.
 */
//todo
public class Play_Anmi {
    private static Animation animation_gotofragment;
    public static Animation getAnimation_gotofragment(Context context){
        animation_gotofragment = AnimationUtils.loadAnimation(context, R.anim.music_anim);
        animation_gotofragment.setInterpolator(new LinearInterpolator());

        return animation_gotofragment;
    }

}
