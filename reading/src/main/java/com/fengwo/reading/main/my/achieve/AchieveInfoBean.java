package com.fengwo.reading.main.my.achieve;

import java.util.List;

public class AchieveInfoBean {
    public String sum_date;
    public String book_sum;        //读完
    public String qd_sum;         //签到数
    public String note_sum;        //随笔数
    public String shuyou;         //书友数量
    public String bfb;            //百分百
    public String exp;            //总经验
    public String day_exp;        //今日经验
    public String chao;            //超越
    public String badge_num;      //勋章数
    public List<GrowPrizeBean> grow_prize;    //成长奖励

    public class GrowPrizeBean {
        public String img;           //图片
        public String href;          //链接地址
        public String need_level;    //需要的等级
    }
}