package com.fengwo.reading.main.my.achieve;

import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.main.my.RankBean;

import java.util.List;

public class Json_Rank extends BaseJson{
	public RankData data;
	public class RankData {
		public String paihang;
		public List<RankBean> list;
	}
}