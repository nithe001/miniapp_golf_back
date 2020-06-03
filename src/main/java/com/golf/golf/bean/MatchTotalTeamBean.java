package com.golf.golf.bean;
import java.util.List;
import java.util.Map;

import com.golf.golf.db.MatchHoleResult;

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
	private Double score;
    private  List<Map<String, Object>>  teamGroupResult;

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

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
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

    public  List<Map<String, Object>> getTeamGroupResult() {
        return teamGroupResult;
    }

    public void setTeamGroupResult( List<Map<String, Object>>  teamGroupResult) {
        this.teamGroupResult = teamGroupResult;}

    @Override
    public int compareTo(MatchTotalTeamBean bean) {
//    return bean.getScore().intValue() - this.score.intValue();
        Double d1 = bean.getScore();
        Double d2 = this.score;
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
