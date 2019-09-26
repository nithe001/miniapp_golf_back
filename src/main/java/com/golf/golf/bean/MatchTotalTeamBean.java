package com.golf.golf.bean;

/**
 * 比赛——分队统计bean
 * Created by dev on 17-2-10
 */
public class MatchTotalTeamBean implements Comparable<MatchTotalTeamBean> {
	private Long teamId;
	private String teamName;
	private String teamAbbrev;
	//0打平  1打赢
	private Integer winNum;
	private Integer pingNum;
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

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

    public Integer getWinNum() {
        return winNum;
    }

    public void setWinNum(Integer winNum) {
        this.winNum = winNum;
    }

    public Integer getPingNum() {
        return pingNum;
    }

    public void setPingNum(Integer pingNum) {
        this.pingNum = pingNum;
    }

    @Override
	public int compareTo(MatchTotalTeamBean bean) {
		return bean.getScore() - this.score;
	}
}
