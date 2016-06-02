package com.fengwo.reading.main.group.qun;

import com.fengwo.reading.bean.BaseJson;

import java.util.List;

/**
 * Created by timeloveboy on 16/4/1.
 */
public class groupRankInfoJson extends BaseJson {
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
        public int journal;
    }
    public class Group{
        public String score;
        public String group_name;
        public String group_id;
        public String rank;
    }
    public List< Group> groupList;

    public QunDetailJson.QunDetailBean.GroupInfo groupInfo;
}
