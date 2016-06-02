package com.fengwo.reading.main.group.qun;

import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.main.group.GroupUserBean;

import java.util.List;

/**
 * Created by timeloveboy on 16/4/1.
 */

public class QunDetailJson extends BaseJson {
    public QunDetailBean data;

    public class QunDetailBean{
        UserInfo userInfo;
        public class UserInfo{
            public String user_id;
            public String avatar;
            public String name;//sheldon",
            public String rank;//: 0,//排名
            public String score;//: 0//积分
        }
        GroupInfo groupInfo;
        public class GroupInfo{
            public String id;// "1",
            public String group_name;//"有书共读1群",
            public String join_nums;// "9",//群组成员数
            public String operator;//"张志涵",//负责人
            public String rate;//": 22%,//群组排行率
            public String score;//": 2,//群组积分
            public String rank;//": 2//群组排名
        }
        List<GroupUserBean> groupUser;
    }

}


