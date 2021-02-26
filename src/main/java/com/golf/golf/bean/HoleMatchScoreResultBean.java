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
    //两球或者四球比杆第一队的最好杆数
    private Integer rodNum1;
    //两球或者四球比杆第二队的最好杆数
    private Integer rodNum2;
    //是否开球洞
    private Integer isStartHole;
    //半场
    private Integer beforeAfter;
    //球洞名称
    private String holeName;
    //球洞序号
    private Integer holeNum;

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

    public Integer getRodNum1() {
        return rodNum1;
    }

    public void setRodNum1(Integer rodNum1) {
        this.rodNum1 = rodNum1;
    }

    public Integer getRodNum2() {
        return  rodNum2;
    }

    public void setRodNum2(Integer rodNum2) {
        this.rodNum2 =  rodNum2;
    }

	public String getUpDnAs() {
		return upDnAs;
	}

	public void setUpDnAs(String upDnAs) {
		this.upDnAs = upDnAs;
	}

    public Integer getIsStartHole() {
        return isStartHole;
    }

    public void setIsStartHole(Integer isStartHole) {
        this.isStartHole = isStartHole;
    }

    public Integer getBeforeAfter() {
        return beforeAfter;
    }

    public void setBeforeAfter(Integer beforeAfter) {
        this.beforeAfter = beforeAfter;
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
}
