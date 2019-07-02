package com.golf.golf.bean;

/**
 * 球队——比赛榜——比洞赛表格 bean
 * Created by dev on 17-2-10
 */
public class TeamPointHoleBean {
	//球队id
	private Long teamId;
	//球队名称
	private String teamName;
	//获胜组
	private String winGroupName;
	//打平组
	private String drewGroupName;
	//得分
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

	public String getDrewGroupName() {
		return drewGroupName;
	}

	public void setDrewGroupName(String drewGroupName) {
		this.drewGroupName = drewGroupName;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
}
