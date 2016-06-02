package com.fengwo.reading.common;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.view.ScrollerNumberPicker;
import com.fengwo.reading.view.ScrollerNumberPicker.OnSelectListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 滚轮
 * 
 * @author lxq
 * 
 */
public class SelectLocationPopupWindow extends PopupWindow {

	private TextView tv_location_cancel, tv_location_submit,tv_location_title;
	private ScrollerNumberPicker snp_popupwindow_province,
			snp_popupwindow_city;

	/**
	 * context 上下文
	 * 
	 * itemsOnClick 点击事件
	 */
	public SelectLocationPopupWindow(Context context,
			OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mView = inflater.inflate(R.layout.layout_popupwindow_location,
				null);

		tv_location_cancel = (TextView) mView.findViewById(R.id.tv_location_cancel);
		tv_location_submit =  (TextView) mView.findViewById(R.id.tv_location_submit);
		tv_location_title = (TextView) mView
				.findViewById(R.id.tv_location_title);
		snp_popupwindow_province = (ScrollerNumberPicker) mView
				.findViewById(R.id.snp_popupwindow_province);
		snp_popupwindow_city = (ScrollerNumberPicker) mView
				.findViewById(R.id.snp_popupwindow_city);
		// 取消按钮
		tv_location_cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 销毁弹出框
				dismiss();
			}
		});
		RelativeLayout relativeLayout = (RelativeLayout) mView
				.findViewById(R.id.rl_popupwindow_layout);
		relativeLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 销毁弹出框
				dismiss();
			}
		});

		// 设置按钮监听
		tv_location_submit.setOnClickListener(itemsOnClick);

		initDataByJson();

		// 设置SelectPicPopupWindow的View
		this.setContentView(mView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottom);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
	}

	public void setTitle(String string) {
		tv_location_title.setText(string);
	}

	private void initData() {

		mCurrentProvinceIndex = 0;
		mCurrentCityIndex = 0;
		mCurrentProvinceName = "";
		mCurrentCityName = "";
		mCurrentProvinceId = "";
		mCurrentCityId = "";

		mProvinceList.clear();
		for (int i = 0; i < mProvinceDatas.length; i++) {
			mProvinceList.add(mProvinceDatas[i]);
		}

		// 放数据
		snp_popupwindow_province.setData(mProvinceList);
		// 默认选择的位置
		snp_popupwindow_province.setDefault(0);

		// 选择事件
		snp_popupwindow_province.setOnSelectListener(new OnSelectListener() {

			@Override
			public void endSelect(int id, String text) {
				mCurrentProvinceIndex = id;
				mCurrentProvinceName = text;
				updateProvince();
			}

			@Override
			public void selecting(int id, String text) {
			}

		});

		snp_popupwindow_city.setOnSelectListener(new OnSelectListener() {

			@Override
			public void endSelect(int id, String text) {
				mCurrentCityIndex = id;
				mCurrentCityName = text;
				updateCitys();
			}

			@Override
			public void selecting(int id, String text) {
			}

		});

		updateProvince();// 刷新省
	}

	private void updateCitys() {// 更新市
		mCurrentCityName = mCitisDatasMap.get(mCurrentProvinceName)[mCurrentCityIndex]; // 根据位置获取当前选择的市的名字
		mCurrentCityId = mCitis_area_id_Map.get(mCurrentCityName);// 根据名字获得市对应的area_id,并且保存到全局变量中
	}

	/**
	 * 根据当前的省，更新市的信息
	 */
	private void updateProvince() {
		mCurrentProvinceName = mProvinceDatas[mCurrentProvinceIndex];// 根据位置ID拿到省份的名称
		mCurrentProvinceId = mProvince_area_id_Map.get(mCurrentProvinceName);// 根据名称拿到省份对应的area_id,并且保存到全局变量中
		String[] cities = mCitisDatasMap.get(mCurrentProvinceName);// 拿到对应省份所有的市区
		// TODO
		mCityList.clear();
		if (cities == null) {
			cities = new String[] { "" };
			// TODO
			mCityList.add("");
		}
		for (int i = 0; i < cities.length; i++) {
			mCityList.add(cities[i]);
		}
		// TODO
		snp_popupwindow_city.setData(mCityList);
		snp_popupwindow_city.setDefault(0);
		mCurrentCityIndex = 0;
		mCurrentCityName = mCitisDatasMap.get(mCurrentProvinceName)[mCurrentCityIndex];
		mCurrentCityId = mCitis_area_id_Map.get(mCurrentCityName);// 将城市ID保存到全局变量
	}

	private void initDataByJson() {
		try {
			JSONObject jsonObject = new JSONObject(GlobalConstant.LOCATION);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			mProvinceDatas = new String[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象
				String province = jsonP.getString("title");// 省名字
				String province_area_id = jsonP.getString("area_id");// 省ID
				mProvinceDatas[i] = province;
				mProvince_area_id_Map.put(province, province_area_id);
				JSONArray jsonCs = null;
				try {
					jsonCs = jsonP.getJSONArray("city");
				} catch (Exception e1) {
					continue;
				}
				String[] mCitiesDatas = new String[jsonCs.length()];
				for (int j = 0; j < jsonCs.length(); j++) {
					JSONObject jsonCity = jsonCs.getJSONObject(j);
					String city = jsonCity.getString("title");// 市名字
					String city_area_id = jsonCity.getString("area_id");// 省ID
					mCitiesDatas[j] = city;
					mCitis_area_id_Map.put(city, city_area_id);
				}
				mCitisDatasMap.put(province, mCitiesDatas);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		initData();
	}

	/**
	 * 所有省
	 */
	private String[] mProvinceDatas;
	/**
	 * key - 省名称 value - 省id
	 */
	private Map<String, String> mProvince_area_id_Map = new HashMap<String, String>();
	/**
	 * key - 市名称 value - 市id
	 */
	private Map<String, String> mCitis_area_id_Map = new HashMap<String, String>();

	/**
	 * key - 省 value - 市s
	 */
	private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();

	private ArrayList<String> mProvinceList = new ArrayList<String>(),
			mCityList = new ArrayList<String>();
	private int mCurrentProvinceIndex = 0, mCurrentCityIndex = 0;
	private String mCurrentProvinceName = "", mCurrentCityName = "";
	private String mCurrentProvinceId = "", mCurrentCityId = "";

	public String getProvinceName() {
		return mCurrentProvinceName;
	}

	public String getCityName() {
		return mCurrentCityName;
	}

	public String getProvinceId() {
		return mCurrentProvinceId;
	}

	public String getCityId() {
		return mCurrentCityId;
	}
}
