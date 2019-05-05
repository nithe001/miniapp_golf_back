package com.golf.golf.bean;

/**
 * 球队比赛总比分显示Bean
 * Created by dev on 17-2-10
 */
public class MatchScoreBean implements Comparable<MatchScoreBean> {

    private Long userId;
    private String userName;
	private String headImg;
	private String holeName;
	private Integer holeNum;
	private Integer standardRod;
    private Integer rodNum;
	private Integer pushRodNum;
	private Integer sumRodNum;

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

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

	public String getHoleName() {
		return holeName;
	}

	public void setHoleName(String holeName) {
		this.holeName = holeName;
	}

	public Integer getHoleNum() {
		return holeNum;
	}

	public void setHoleNum(Integer holeNum) {
		this.holeNum = holeNum;
	}

	public Integer getStandardRod() {
		return standardRod;
	}

	public void setStandardRod(Integer standardRod) {
		this.standardRod = standardRod;
	}

	public Integer getRodNum() {
		return rodNum;
	}

	public void setRodNum(Integer rodNum) {
		this.rodNum = rodNum;
	}

	public Integer getPushRodNum() {
		return pushRodNum;
	}

	public void setPushRodNum(Integer pushRodNum) {
		this.pushRodNum = pushRodNum;
	}

	public Integer getSumRodNum() {
		return sumRodNum;
	}

	public void setSumRodNum(Integer sumRodNum) {
		this.sumRodNum = sumRodNum;
	}

	@Override
	public int compareTo(MatchScoreBean bean) {
		return Integer.parseInt(this.userId.toString()) - Integer.parseInt(bean.userId.toString());
	}
}
