package com.golf.golf.bean;

import java.util.List;
import java.util.Map;

/**
 * 球队比赛——分组——用户记分 bean
 * Created by dev on 17-2-10
 */
public class DoubleRodUserScoreBean{
	//用户列表
	private List<Map<String, Object>[]>  userList;
	//用户成绩 文字
	private String chengji;
	//用户每个洞的得分
    private List<Map<String, Object>[]>  userScoreList;
    //用户成绩
	private List<String> totalScoreList;

	public List<Map<String, Object>[]> getUserList() {
		return userList;
	}

	public void setUserList(List<Map<String, Object>[]> userList) {
		this.userList = userList;
	}

	public String getChengji() {
		return "成绩";
	}

	public void setChengji(String chengji) {
		this.chengji = chengji;
	}

	public List<Map<String, Object>[]> getUserScoreList() {
		return userScoreList;
	}

	public void setUserScoreList(List<Map<String, Object>[]> userScoreList) {
		this.userScoreList = userScoreList;
	}

	public List<String> getTotalScoreList() {
		return totalScoreList;
	}

	public void setTotalScoreList(List<String> totalScoreList) {
		this.totalScoreList = totalScoreList;
	}
}
