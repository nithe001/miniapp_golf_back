package com.golf.golf.bean;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 球队比赛——分组——用户记分 bean
 * Created by dev on 17-2-10
 */
public class ChooseUserBean {
	private String userName;
	private List<JSONObject> list;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<JSONObject> getList() {
		return list;
	}

	public void setList(List<JSONObject> list) {
		this.list = list;
	}
}
