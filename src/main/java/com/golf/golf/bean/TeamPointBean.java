package com.golf.golf.bean;

/**
 * 用于计算球队的积分——比分榜、积分榜,这个bean目前没有用 nhq 2020.5.16
 * Created by dev on 17-2-10
 */
public class TeamPointBean implements Comparable<TeamPointBean> {
	//用户id
	private Long teamId;
	//真实姓名
	private String teamName;
	//昵称
	private String teamAbbrev;
	//参赛场次
	private Integer totalMatchNum;
	//积分
	private Double teamPoint;


	public Long getTeamId() {
		return teamId;
	}

	public void setTeamId(Long teamId) {
		this.teamId =teamId;
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

	public Integer getTotalMatchNum() {
		return totalMatchNum;
	}

	public void setTotalMatchNum(Integer totalMatchNum) {
		this.totalMatchNum = totalMatchNum;
	}

	public Double getTeamPoint() {
		return teamPoint;
	}

	public void setTeamPoint(Double teamPoint) {
		this.teamPoint = teamPoint;
	}



	@Override
	public int compareTo(TeamPointBean teamPointBean) {
		//return this.teamPoint - teamPointBean.teamPoint;
        Double d1 = this.teamPoint;
        Double d2 =  teamPointBean.teamPoint;
        if((d1 == null || d1 == 0) && (d2 == null || d2 == 0)){
            return 0;
        }
        if(d1 == null || d1 == 0){
            return 1;
        }
        if(d2 == null || d2 == 0){
            return -1;
        }
        return new Double(d1).compareTo(new Double(d2));
	}

}
