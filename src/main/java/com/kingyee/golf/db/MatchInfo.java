package com.kingyee.golf.db;

import javax.persistence.Entity;
import javax.persistence.Table;

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
	public MatchInfo(Long matchTime, String title, String abstract_, String thumb,
					 String content, String address, Integer isDel, Integer isOpen,
					 Integer state, Integer hit, String createUserName,
					 Long createUserId, Long createTime, Long applyStartTime,
					 Long applyEndTime) {
		super(matchTime, title, abstract_, thumb, content, address, isDel,
				isOpen, state, hit, createUserName, createUserId, createTime,
				applyStartTime, applyEndTime);
	}

}
