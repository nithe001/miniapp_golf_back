package com.kingyee.golf.bean;

import com.kingyee.golf.db.MatchInfo;
import com.kingyee.golf.db.TeamInfo;

/**
 * 球队比赛 bean
 * Created by dev on 17-2-10
 */
public class TeamMatchInfoBean {

    private TeamInfo teamInfo;
    private MatchInfo matchInfo;

	public TeamInfo getTeamInfo() {
		return teamInfo;
	}

	public void setTeamInfo(TeamInfo teamInfo) {
		this.teamInfo = teamInfo;
	}

	public MatchInfo getMatchInfo() {
		return matchInfo;
	}

	public void setMatchInfo(MatchInfo matchInfo) {
		this.matchInfo = matchInfo;
	}
}
