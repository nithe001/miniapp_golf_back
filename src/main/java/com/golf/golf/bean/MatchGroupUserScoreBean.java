package com.golf.golf.bean;

import java.util.List;
import java.util.Map;

/**
 * 球队比赛——分组——用户记分 bean
 * Created by dev on 17-2-10
 */
public class MatchGroupUserScoreBean implements Comparable<MatchGroupUserScoreBean> {

    private Long userId;
    private String userName;
    private String userHeadimg;
    private Long groupId;
    private Integer holeCount;
    private String groupName;
    private Long teamId;
    private String teamAbbrev;
    private Long matchId;
    private String matchName;
    private List<Map<String, Object>>  userScoreList;
	private List<MatchTotalUserScoreBean>  userScoreTotalList;
	private List<String> parkHoleList;
    private Integer totalRodScore;
	private Integer totalPushRodScore;
	private double totalNetRodScore;//净杆

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

    public String getUserHeadimg() {
        return userHeadimg;
    }

    public void setUserHeadimg(String userHeadimg) {
        this.userHeadimg = userHeadimg;
    }


    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }


    public Integer getHoleCount() {
        return holeCount;
    }

    public void setHoleCount(Integer holeCount) {
        this.holeCount = holeCount;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamAbbrev() {
        return teamAbbrev;
    }

    public void setTeamAbbrev(String teamAbbrev) {
        this.teamAbbrev = teamAbbrev;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }


    public List<Map<String, Object>> getUserScoreList() {
		return userScoreList;
	}

	public void setUserScoreList(List<Map<String, Object>> userScoreList) {
		this.userScoreList = userScoreList;
	}

	public Integer getTotalRodScore() {
		return totalRodScore;
	}

	public void setTotalRodScore(Integer totalRodScore) {
		this.totalRodScore = totalRodScore;
	}

	public Integer getTotalPushRodScore() {
		return totalPushRodScore;
	}

	public void setTotalPushRodScore(Integer totalPushRodScore) {
		this.totalPushRodScore = totalPushRodScore;
	}

	public List<MatchTotalUserScoreBean> getUserScoreTotalList() {
		return userScoreTotalList;
	}

	public void setUserScoreTotalList(List<MatchTotalUserScoreBean> userScoreTotalList) {
		this.userScoreTotalList = userScoreTotalList;
	}

	public List<String> getParkHoleList() {
		return parkHoleList;
	}

	public void setParkHoleList(List<String> parkHoleList) {
		this.parkHoleList = parkHoleList;
	}

	public double getTotalNetRodScore() {
		return totalNetRodScore;
	}

	public void setTotalNetRodScore(double totalNetRodScore) {
		this.totalNetRodScore = totalNetRodScore;
	}

	@Override
	public int compareTo(MatchGroupUserScoreBean bean) {
		return Integer.parseInt(this.userId.toString()) - Integer.parseInt(bean.userId.toString());
	}
}
