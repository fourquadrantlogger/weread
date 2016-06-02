package com.fengwo.reading.main.discover.hottopics;

import com.fengwo.reading.bean.UserInfoBean;

public class TopicsBean {
	
	public UserInfoBean user_data;// 用户信息
	public String id;
	public String user_id;
	public String book_id;
	public String title;
	public String content;
	public String img_str[];
	public String digg_count;
	public String comment_count;
	public String read_count;
	public String is_delete;
	public String is_pub;
	public String create_time;
	public String is_top;
	public String is_fav;
	public String is_digg;

	public String[] img_str_orignsize() {    //大图地址
		String[] imgs = new String[img_str.length];
		for (int i = 0; i < img_str.length; i++) {
			if(img_str[i].contains("@")) {
				imgs[i] = img_str[i].substring(0, img_str[i].indexOf("@"));
			}else {
				imgs[i]=img_str[i];
			}
		}
		return imgs;
	}
	public static String[] img_str_orignsize(String[] img_str) {    //大图地址
		String[] imgs = new String[img_str.length];
		for (int i = 0; i < img_str.length; i++) {
			imgs[i] = img_str[i].substring(0, img_str[i].indexOf("@"));
		}
		return imgs;
	}
}
