package com.golf.golf.db;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * UserMatchGroup entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "match_group_user")
public class MatchGroupUser extends AbstractMatchGroupUser implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public MatchGroupUser() {
	}

	/** minimal constructor */
	public MatchGroupUser(Long mguId) {
		super(mguId);
	}

	/** full constructor */
	public MatchGroupUser(Long mguId, Long mguMatchId, Long mguGroupId,
						  String mguGroupName, Long mguUserId, Long mguCreateUserId, Long mguCreateTime) {
		super(mguId, mguMatchId, mguGroupId, mguGroupName, mguUserId, mguCreateUserId, mguCreateTime);
	}

}
