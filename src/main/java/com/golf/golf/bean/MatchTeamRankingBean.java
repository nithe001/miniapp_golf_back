package com.golf.golf.bean;

/**
 * 比赛——分队统计 bean
 * Created by dev on 17-2-10
 */
public class MatchTeamRankingBean {
	//球队id
	private Long teamId;
	//球队名称
	private String teamName;
	//球队简称
	private String teamAbbrev;
	//参赛人数（实际）
	private Integer userCount;
	//平均杆数
	private Double avgRodNum;
	//总杆数
	private Integer sumRodNum;
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

	public String getTeamAbbrev() {
		return teamAbbrev;
	}

	public void setTeamAbbrev(String teamAbbrev) {
		this.teamAbbrev = teamAbbrev;
	}

	public Integer getUserCount() {
		return userCount;
	}

	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}

	public Double getAvgRodNum() {
		if(this.avgRodNum == null){
			return 0.0;
		}
		return avgRodNum;
	}

	public void setAvgRodNum(Double avgRodNum) {
		this.avgRodNum = avgRodNum;
	}

	public Integer getSumRodNum() {
		if(this.sumRodNum == null){
			return 0;
		}
		return sumRodNum;
	}

	public void setSumRodNum(Integer sumRodNum) {
		this.sumRodNum = sumRodNum;
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
