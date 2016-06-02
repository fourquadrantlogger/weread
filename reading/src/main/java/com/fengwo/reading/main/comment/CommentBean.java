package com.fengwo.reading.main.comment;

import com.fengwo.reading.bean.UserInfoBean;

public class CommentBean {

    public UserInfoBean user_data;
    public UserInfoBean reply_user_data;//被回复的用户

    public String id;//评论id
    public String comment_type;//0评论1回复
    public String content;
    public String create_time;

    public String digg_count;    // 点赞数
    public String is_digg;        // 是否点赞  1为已点
    public String re_content;    // 被回复者的内容

}
