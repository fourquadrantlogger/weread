package com.fengwo.reading.myinterface;

import org.apache.http.NameValuePair;

/**
 * httputils中发送的参数实现类,以键值对形式出现
 * 
 * @author jackiechan
 * 
 */
public class BaseNameValuePairs implements NameValuePair {
	private String name;
	private String value;

	public BaseNameValuePairs(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

}
