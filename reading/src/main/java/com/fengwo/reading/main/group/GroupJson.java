package com.fengwo.reading.main.group;

import com.fengwo.reading.bean.BaseJson;

import java.util.List;

public class GroupJson extends BaseJson {
    public List<GroupBean> data;        //有书圈随笔
    public List<groupData> group_data;    //群组筛选
    public List<GroupUserBean> group_user;    //群组排名

    public int user_score;    // 当前排名，数字类型
    public String is_push_score;  //是否推送给软件打分，1为推送，0为不推送
    public String page;            // 当前页

    public class groupData {
        public String group_id;
        public String group_name;
    }

}


