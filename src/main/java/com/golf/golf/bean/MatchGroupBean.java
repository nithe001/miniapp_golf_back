package com.golf.golf.bean;

import com.golf.golf.db.MatchGroup;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.TeamInfo;
import com.golf.golf.db.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * 球队比赛 bean
 * Created by dev on 17-2-10
 */
public class MatchGroupBean {

    private MatchGroup matchGroup;
    private List<Map<String, Object>>  userInfoList;

	public MatchGroup getMatchGroup() {
		return matchGroup;
	}

	public void setMatchGroup(MatchGroup matchGroup) {
		this.matchGroup = matchGroup;
	}

	public List<Map<String, Object>> getUserInfoList() {
		return userInfoList;
	}

	public void setUserInfoList(List<Map<String, Object>> userInfoList) {
		this.userInfoList = userInfoList;
	}
}
