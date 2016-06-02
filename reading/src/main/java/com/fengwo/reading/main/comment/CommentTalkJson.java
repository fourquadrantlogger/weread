package com.fengwo.reading.main.comment;

import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.bean.UserInfoBean;

import java.util.List;

public class CommentTalkJson extends BaseJson{
	public UserInfoBean meuser_data;
	public UserInfoBean tauser_data;
	public List<CommentTalkBean> talk_list;
}
