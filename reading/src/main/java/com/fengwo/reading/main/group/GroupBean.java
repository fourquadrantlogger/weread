package com.fengwo.reading.main.group;

import com.fengwo.reading.bean.UserInfoBean;

public class GroupBean {

    public UserInfoBean user_data;// 用户信息

    public String id;                // 笔记id (评论id)
    public String user_id;           // id
    public String book_id;           // 书id
    public String book_title;        // 书名
    public String title;             // 讨论标题
    public String content;           // 内容
    public String[] img_str;         // 图片地址
    public String is_digg;           // 是否点赞  1为已点
    public String is_fav;            // 是否收藏  1为已收
    public String read_count;        // 阅读量
    public String word_count;        // 字数
    public String digg_count;        // 点赞数
    public String comment_count;     // 评论数
    public String create_time;       // 发布时间
    public String is_pub;            //0公开,1秘密

    private int type = GroupAdapter.ALL;//指定是哪种类型

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String[] img_str_orignsize() {    //大图地址
        String[] imgs = new String[img_str.length];
        for (int i = 0; i < img_str.length; i++) {
            if (img_str[i].contains("@")) {
                imgs[i] = img_str[i].substring(0, img_str[i].indexOf("@"));
            } else {
                imgs[i] = img_str[i];
            }
        }
        return imgs;
    }

    public static String[] img_str_orignsize(String[] img_str) {    //大图地址
        String[] imgs = new String[img_str.length];
        for (int i = 0; i < img_str.length; i++) {
            imgs[i] = img_str[i].substring(0, img_str[i].indexOf("@"));
        }
        return imgs;
    }
}
