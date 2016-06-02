package com.fengwo.reading.main.group.qun;

import com.fengwo.reading.bean.BaseJson;

import java.util.List;

/**
 * Created by timeloveboy on 16/4/1.
 */
public class groupMemRankJson extends BaseJson {
    public Book book;
    public class Book{
        public String id;
        public String book_id;
        public String book_title;
        public String book_cover;
        public String create_time;
        public String start_time;
        public String end_time;
        public String status;
        public String comment;
        public String journal;
    }
    public class GroupUser{
        public String score;//"0",
        public String avatar;//"http://avatarimg.fengwo.com/readwith/20160325/56f4eb1aace07.jpg@170w_170h.jpg",
        public String name;//"有书任博",
        public String user_id;// "42",
        public int num;// 2
    }
    public List<GroupUser> groupUser;
    public QunDetailJson.QunDetailBean.UserInfo userInfo;
}
