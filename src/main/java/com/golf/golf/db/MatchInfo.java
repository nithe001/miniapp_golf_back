package com.golf.golf.db;

import com.golf.common.util.TimeUtil;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Time;

/**
 * Match entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "match_info")
public class MatchInfo extends AbstractMatchInfo implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public MatchInfo() {
	}

	/** full constructor */
	public MatchInfo(Long matchTime, String title, String digest, String thumb,
					 String content, String address, Integer isOpen, Integer hit,
					 Long applyEndTime, String createUserName, Long createUserId,
					 Long createTime, String updateUserName, Long updateUserId,
					 Long updateTime) {
		super(matchTime, title, digest, thumb, content, address, isOpen, hit,
				applyEndTime, createUserName, createUserId, createTime,
				updateUserName, updateUserId, updateTime);
	}

	private String matchTimeStr;
	@Transient
	public String getMatchTimeStr() {
		createTimeStr = TimeUtil.longToString(this.getMatchTime(),TimeUtil.FORMAT_DATETIME_HH_MM);
		return matchTimeStr;
	}

	public void setMatchTimeStr(Long matchTime) {
		this.matchTimeStr = TimeUtil.longToString(matchTime,TimeUtil.FORMAT_DATETIME_HH_MM);
	}


	private String createTimeStr;
	@Transient
	public String getCreateTimeStr() {
		createTimeStr = TimeUtil.longToString(this.getCreateTime(),TimeUtil.FORMAT_DATETIME_HH_MM);
		return createTimeStr;
	}

	public void setCreateTimeStr(Long createTime) {
		this.createTimeStr = TimeUtil.longToString(createTime,TimeUtil.FORMAT_DATETIME_HH_MM);
	}

	private String stateStr;
	@Transient

	public String getStateStr() {
		return stateStr;
	}

	public void setStateStr() {
		Long nowTime = System.currentTimeMillis();
		//比赛当天的开始时间
		Long matchStartTime = TimeUtil.stringToLong(TimeUtil.longToString(this.getMatchTime(),TimeUtil.FORMAT_DATE),TimeUtil.FORMAT_DATE);
		Long matchEndTime = matchStartTime +(24 * 60 * 60 * 1000);
		if(this.getApplyEndTime() == null && nowTime < matchStartTime - (24 * 60 * 60 * 1000)){
			this.stateStr = "报名中";
		}else{
			if(nowTime < this.getApplyEndTime()){
				this.stateStr = "报名中";
			}else if(nowTime > this.getMatchTime() && nowTime < matchEndTime){
				this.stateStr = "进行中";
			}else if(nowTime > matchEndTime){
				this.stateStr = "已结束";
			}
		}
	}
}
