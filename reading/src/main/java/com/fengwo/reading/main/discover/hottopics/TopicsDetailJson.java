package com.fengwo.reading.main.discover.hottopics;

import com.fengwo.reading.bean.BaseJson;

import java.util.List;

public class TopicsDetailJson extends BaseJson {

	public String id;
	public String topic_title;
	public String user_id;
	public String join_nums;
	public String topic_content;
	public String img;
	public String topic_rule;
	public String create_time;

	public List<TopicsBean> list;
}
