package com.fengwo.reading.main.my;

import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.bean.UserInfoBean;
import com.fengwo.reading.umeng.UMengBean;

public class UserinfoSaveJson extends BaseJson {
    public UserInfoBean user_data;
    public UMengBean bind;
    public String token;// 返回个人信息时,使用MD5验证,正确才保存
}
