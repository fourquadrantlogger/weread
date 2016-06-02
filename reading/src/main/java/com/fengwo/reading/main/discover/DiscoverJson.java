package com.fengwo.reading.main.discover;

import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.main.discover.hottopics.HotListBean;
import com.fengwo.reading.main.group.GroupBean;

import java.util.List;

public class DiscoverJson extends BaseJson {

    public List<HotListBean> topic;            //轮播 - 话题
    public List<GroupBean> note;               //精选随笔
    public List<ACEBean> export;     //达人信息
    public List<ACEBean> weekNewer;  //新人榜
    public List<ACEBean> data;  //新人榜(换)

    public String action; //精选随笔顶部图片
    public BookInfo book;

    class BookInfo {
        public String pb_id;
        public String book_id;
        public String book_title;
        public String book_cover;
        public String chosen_img;
    }
}
