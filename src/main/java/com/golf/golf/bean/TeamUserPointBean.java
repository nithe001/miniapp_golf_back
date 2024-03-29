package com.golf.golf.bean;

/**
 * 用于计算球队成员的积分——比分榜、积分榜 bean
 * Created by dev on 17-2-10
 */
public class TeamUserPointBean implements Comparable<TeamUserPointBean> {
	//用户id
	private Long userId;
	//真实姓名
	private String realName;
	//昵称
	private String nickName;
	//参赛场次
	private Integer totalMatchNum;
	//平均杆数
	private Double avgRodNum;
	//平均杆数
	private Integer avgRodInteger;
	//总杆数
	private Integer sumRodNum;
	//积分
	private Double point;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Integer getTotalMatchNum() {
		return totalMatchNum;
	}

	public void setTotalMatchNum(Integer totalMatchNum) {
		this.totalMatchNum = totalMatchNum;
	}

	public Double getAvgRodNum() {
		return avgRodNum;
	}

	public void setAvgRodNum(Double avgRodNum) {
		this.avgRodNum = avgRodNum;
	}

	public Integer getSumRodNum() {
		return sumRodNum;
	}

	public void setSumRodNum(Integer sumRodNum) {
		this.sumRodNum = sumRodNum;
	}

	public Double getPoint() {
		return point;
	}

	public void setPoint(Double point) {
		this.point = point;
	}

	public Integer getAvgRodInteger() {
		return avgRodInteger;
	}

	public void setAvgRodInteger(Integer avgRodInteger) {
		this.avgRodInteger = avgRodInteger;
	}

	@Override
	public int compareTo(TeamUserPointBean teamUserPointBean) {
		return this.avgRodInteger - teamUserPointBean.avgRodInteger;
	}
}
