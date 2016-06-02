package com.fengwo.reading.main.read;

import com.fengwo.reading.bean.BaseJson;

import java.util.List;


public class Json_Index extends BaseJson {
	public String target;// 一句话描述建议
	public Bean_Book book_data;
	public List<BannerBean> banner;
	public List<IndexBean> bp_list;
	public List<String> check;//是否拆包的书
	public List<IndexBean> today_recom_book;
}
class BannerBean {
	public String title;//广告title
	public String href;//h5链接
	public String img;//图片地址
}