package com.fengwo.reading.main.group;

import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.bean.UserInfoBean;

import java.util.List;

public class OtherUserJson extends BaseJson {
	public UserInfoBean user_data;// 用户信息

	public List<GroupBean> data;

	public String page;// 当前页

}
