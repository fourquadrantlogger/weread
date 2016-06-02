package com.fengwo.reading.main.read.bookpackdetails;

import com.fengwo.reading.bean.UserInfoBean;

import java.util.List;

public class CommentDetailsBean {
    public UserInfoBean user;  //评论主人的信息
    public comminfoBean comm_info; //评论的信息
    public List<UserInfoBean> digg_users; //头10个分享人信息

    public class comminfoBean {
        public String content;//内容
        public String create_time;//时间
        public String comment_count;//评论数量
        public String digg_count;//点赞数量
        public String read_count;//阅读数量
        public String is_digg;//点赞
    }

    //评论list的信息
    public String id;//
    public String bpc_id;//
    public String receive_user;//
    public String send_user;//
    public String content;//
    public String create_time;
    public String cid;
    public String comment_type;//1是回复，0是评论
    public UserInfoBean user_data;//发送者的相关信息
    public UserInfoBean reply_user_data;//接收者的相关信息

}
