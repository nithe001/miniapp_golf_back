package com.golf.golf.bean;

/**
 * 比洞赛——成绩bean
 * Created by dev on 17-2-10
 */
public class HoleMatchScoreResultBean {
	//数字 打平为0 输赢为1,2,3
	private Integer num;
	//输赢或者打平“UP”"DN" "A/S"
	private String upDnAs;

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getUpDnAs() {
		return upDnAs;
	}

	public void setUpDnAs(String upDnAs) {
		this.upDnAs = upDnAs;
	}
}
