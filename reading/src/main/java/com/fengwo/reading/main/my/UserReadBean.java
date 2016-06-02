package com.fengwo.reading.main.my;

import android.os.Environment;

import com.fengwo.reading.myinterface.GlobalParams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserReadBean {
	public String id;			//共读id即pb_id
	public String book_id;
	public String book_title;
	public String create_time;
	public String start_time;
	public String end_time;
	public String wereadtime_data_to_date(){
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");// HH:mm:ss

			Date date1 = simpleDateFormat
					.parse( start_time);
			Date date2 = simpleDateFormat
					.parse(end_time);

			SimpleDateFormat format = new SimpleDateFormat("MM月dd日");

			String create = format.format(date1);
			String end = format.format(date2);

			return create  + " 至" + end;

		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}
	public String book_cover;
	public String book_cover_local(){
		;//  本地图片
		return bookfolder()+"/book_cover.jpg";
	}

	public String bookfolder(){
		String mediaFolder = Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Media +book_title;
		return mediaFolder;
	}
	public String check_sum;	//签到次数
	public String note_sum;		//随笔条数

	public String pb_id;
	public String chosen_img;

	public String title;
	public String author;
	public String intro;
	public String mouth;
	public String qi;

}
