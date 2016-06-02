package com.fengwo.reading.main.my;

import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.bean.UserInfoBean;
import com.fengwo.reading.main.my.achieve.AchieveInfoBean;
import com.fengwo.reading.umeng.UMengBean;

import java.util.List;

public class ProgressJson extends BaseJson {

    public String TotalDigg;           //总共的点赞数
    public UserReadBean nowReading;    //现在在读的一本书
    public AchieveInfoBean achieve;    //阅历信息
    public String avatar;              //
    public String name;                //
    public List<ProgressBean2> BookData;    //

}
