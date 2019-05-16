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
    private List<Map<String, Object>>  userScoreList;
	private List<MatchTotalUserScoreBean>  userScoreTotalList;
	private List<String> parkHoleList;
    private Integer totalRodScore;
	private Integer totalPushRodScore;

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

	@Override
	public int compareTo(MatchGroupUserScoreBean bean) {
		return Integer.parseInt(this.userId.toString()) - Integer.parseInt(bean.userId.toString());
	}
}
