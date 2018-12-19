package com.golf.golf.db;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * UserMatchGroup entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "match_group_info")
public class MatchGroupInfo extends AbstractMatchGroupInfo implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public MatchGroupInfo() {
	}

	/** minimal constructor */
	public MatchGroupInfo(Long mgiId) {
		super(mgiId);
	}

	/** full constructor */
	public MatchGroupInfo(Long mgiId, Long mgiMatchId, String mgiGroupName, Long mgiCreateUserId, Long mgiCreateTime) {
		super(mgiId, mgiMatchId, mgiGroupName, mgiCreateUserId, mgiCreateTime);
	}

}
