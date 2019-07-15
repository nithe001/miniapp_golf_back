package com.golf.golf.bean;

import com.golf.golf.db.TeamInfo;

import java.util.List;
import java.util.Map;

/**
 * 球队-用户对应列表bena
 * Created by dev on 17-2-10
 */
public class TeamUserBean {
	private TeamInfo teamInfo;
	private List<Map<String,Object>> userList;

	public TeamInfo getTeamInfo() {
		return teamInfo;
	}

	public void setTeamInfo(TeamInfo teamInfo) {
		this.teamInfo = teamInfo;
	}

	public List<Map<String, Object>> getUserList() {
		return userList;
	}

	public void setUserList(List<Map<String, Object>> userList) {
		this.userList = userList;
	}
}
