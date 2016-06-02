package com.fengwo.reading.main.read.bookpackdetails;

import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.bean.UserInfoBean;
import com.fengwo.reading.main.read.IndexBean;

import java.util.List;


public class CommentDetailsJson extends BaseJson {
    public String page;

    public CommentDetailsBean data; //评论的信息

    public List<CommentDetailsBean> comment_list; //详情的评论list
}
