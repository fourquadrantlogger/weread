package com.fengwo.reading.main.group;

import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.main.discover.hottopics.HotListBean;

import java.util.List;

public class SearchDetailsJson extends BaseJson {

    public HotListBean topic;      //发现 - 话题
    public List<GroupBean> note;         //精选随笔
    public List<GroupBean> allNote;      //所有随笔

}
