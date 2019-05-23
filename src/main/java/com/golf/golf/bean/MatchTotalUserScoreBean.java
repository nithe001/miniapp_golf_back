package com.golf.golf.bean;

/**
 * 球队比赛——分组——用户记分 bean
 * Created by dev on 17-2-10
 */
public class MatchTotalUserScoreBean implements Comparable<MatchTotalUserScoreBean> {

    private Long userId;
    private String userName;
	private String holeName;
	private String holeNum;
	private Integer rodNum;
	private Integer rodCha;
	private Integer holeStandardRod;

    private Integer totalRodScore;
	private Integer totalPushRodScore;

	public Integer getRodNum() {
		return rodNum;
	}

	public void setRodNum(Integer rodNum) {
		this.rodNum = rodNum;
	}

	public Integer getRodCha() {
		return rodCha;
	}

	public void setRodCha(Integer rodCha) {
		this.rodCha = rodCha;
	}

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

	public String getHoleName() {
		return holeName;
	}

	public void setHoleName(String holeName) {
		this.holeName = holeName;
	}

	public String getHoleNum() {
		return holeNum;
	}

	public void setHoleNum(String holeNum) {
		this.holeNum = holeNum;
	}

	public Integer getHoleStandardRod() {
		return holeStandardRod;
	}

	public void setHoleStandardRod(Integer holeStandardRod) {
		this.holeStandardRod = holeStandardRod;
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

	@Override
	public int compareTo(MatchTotalUserScoreBean bean) {
		return Integer.parseInt(this.userId.toString()) - Integer.parseInt(bean.userId.toString());
	}
}
