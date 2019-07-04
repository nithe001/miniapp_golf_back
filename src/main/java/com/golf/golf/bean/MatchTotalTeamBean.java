package com.golf.golf.bean;

/**
 * 比赛——分队统计bean
 * Created by dev on 17-2-10
 */
public class MatchTotalTeamBean implements Comparable<MatchTotalTeamBean> {
	private Long teamId;
	private String teamName;
	//0打平  1打赢
	private Integer type;
	private String winGroupName;
	private String pingGroupName;
	private Integer score;

	public Long getTeamId() {
		return teamId;
	}

	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getWinGroupName() {
		return winGroupName;
	}

	public void setWinGroupName(String winGroupName) {
		this.winGroupName = winGroupName;
	}

	public String getPingGroupName() {
		return pingGroupName;
	}

	public void setPingGroupName(String pingGroupName) {
		this.pingGroupName = pingGroupName;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public int compareTo(MatchTotalTeamBean bean) {
		return bean.getScore() - this.score;
	}
}
