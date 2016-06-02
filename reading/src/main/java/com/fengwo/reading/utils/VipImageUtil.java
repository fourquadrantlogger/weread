package com.fengwo.reading.utils;


import android.content.Context;
import android.widget.ImageView;

/**
 * VIP等级对应图片
 */
public class VipImageUtil {

    private static int grade = 0;  //等级 ( 1 - 20 )
    private static int expAll = 0; //当前等级经验上限
    private static int expStart = 0; //当前等级起始经验

    /**
     * 设置等级图标
     *
     * @param grade 等级 ( 1 - 20 )
     * @param type  1:白字黄底  2:黄字透明  3:灰字透明(下一等级) 4:红字透明
     */
    public static void getVipGrade(Context context, ImageView iv, int grade, int type) {
        String imgName = "";
        switch (type) {
            case 1:
                imgName = "grade_v" + grade;
                break;
            case 2:
                imgName = "grade_" + grade;
                break;
            case 3:
                imgName = "grade_" + grade + "hou";
                break;
            case 4:
                imgName = "upgrade_" + grade;
                break;
        }
        int img = context.getResources().getIdentifier(imgName, "drawable", "com.fengwo.reading");
        iv.setBackgroundResource(img);
    }

    public static void getExp(String experience) {
        int exp1 = 0;
        try {
            exp1 = Integer.valueOf(experience).intValue();
        } catch (Exception e) {
            MLog.v("VipImageUtil", e + "");
        }
        if (exp1 < 100) {
            grade = 1;
            expStart = 0;
            expAll = 100;
        } else if (exp1 < 150) {
            grade = 2;
            expStart = 100;
            expAll = 150;
        } else if (exp1 < 200) {
            grade = 3;
            expStart = 150;
            expAll = 200;
        } else if (exp1 < 300) {
            grade = 4;
            expStart = 200;
            expAll = 300;
        } else if (exp1 < 450) {
            grade = 5;
            expStart = 300;
            expAll = 450;
        } else if (exp1 < 700) {
            grade = 6;
            expStart = 450;
            expAll = 700;
        } else if (exp1 < 1000) {
            grade = 7;
            expStart = 700;
            expAll = 1000;
        } else if (exp1 < 1500) {
            grade = 8;
            expStart = 1000;
            expAll = 1500;
        } else if (exp1 < 2200) {
            grade = 9;
            expStart = 1500;
            expAll = 2200;
        } else if (exp1 < 3200) {
            grade = 10;
            expStart = 2200;
            expAll = 3200;
        } else if (exp1 < 4700) {
            grade = 11;
            expStart = 3200;
            expAll = 4700;
        } else if (exp1 < 6900) {
            grade = 12;
            expStart = 4700;
            expAll = 6900;
        } else if (exp1 < 10000) {
            grade = 13;
            expStart = 6900;
            expAll = 10000;
        } else if (exp1 < 15000) {
            grade = 14;
            expStart = 10000;
            expAll = 15000;
        } else if (exp1 < 22000) {
            grade = 15;
            expStart = 15000;
            expAll = 22000;
        } else if (exp1 < 32000) {
            grade = 16;
            expStart = 22000;
            expAll = 32000;
        } else if (exp1 < 47000) {
            grade = 17;
            expStart = 32000;
            expAll = 47000;
        } else if (exp1 < 69000) {
            grade = 18;
            expStart = 47000;
            expAll = 69000;
        } else if (exp1 < 100000) {
            grade = 19;
            expStart = 69000;
            expAll = 100000;
        } else if (exp1 > 100000) {
            grade = 20;
            expStart = 100000;
            expAll = 100000;
        }
    }

    public static int getGrade() {
        return grade;
    }

    public static int getExpAll() {
        return expAll;
    }

    public static int getExpStart() {
        return expStart;
    }

}