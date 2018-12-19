package com.golf.golf.db;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * AbstractUserMatchGroup entity provides the base persistence definition of the
 * UserMatchGroup entity. @author MyEclipse Persistence Tools
 */
@MappedSuperclass
public abstract class AbstractMatchGroupUser implements java.io.Serializable {

	// Fields

	private Long mguId;
	private Long mguMatchId;
	private Long mguGroupId;
	private String mguGroupName;
	private Long mguUserId;
	private Long mguCreateUserId;
	private Long mguCreateTime;

	// Constructors

	/** default constructor */
	public AbstractMatchGroupUser() {
	}

	/** minimal constructor */
	public AbstractMatchGroupUser(Long mguId) {
		this.mguId = mguId;
	}

	/** full constructor */
	public AbstractMatchGroupUser(Long mguId, Long mguMatchId, Long mguGroupId,
								  String mguGroupName, Long mguUserId, Long mguCreateUserId, Long mguCreateTime) {
		this.mguId = mguId;
		this.mguMatchId = mguMatchId;
		this.mguGroupId = mguGroupId;
		this.mguGroupName = mguGroupName;
		this.mguUserId = mguUserId;
		this.mguCreateUserId = mguCreateUserId;
		this.mguCreateTime = mguCreateTime;
	}

	// Property accessors
	@Id
	@Column(name = "mgu_id", unique = true, nullable = false)
	public Long getMguId() {
		return this.mguId;
	}

	public void setMguId(Long mguId) {
		this.mguId = mguId;
	}

	@Column(name = "mgu_match_id")
	public Long getMguMatchId() {
		return this.mguMatchId;
	}

	public void setMguMatchId(Long mguMatchId) {
		this.mguMatchId = mguMatchId;
	}

	@Column(name = "mgu_group_id")
	public Long getMguGroupId() {
		return this.mguGroupId;
	}

	public void setMguGroupId(Long mguGroupId) {
		this.mguGroupId = mguGroupId;
	}

	@Column(name = "mgu_group_name", length = 128)
	public String getMguGroupName() {
		return this.mguGroupName;
	}

	public void setMguGroupName(String mguGroupName) {
		this.mguGroupName = mguGroupName;
	}

	@Column(name = "mgu_user_id")
	public Long getMguUserId() {
		return this.mguUserId;
	}

	public void setMguUserId(Long mguUserId) {
		this.mguUserId = mguUserId;
	}

	@Column(name = "mgu_create_user_id")
	public Long getMguCreateUserId() {
		return mguCreateUserId;
	}

	public void setMguCreateUserId(Long mguCreateUserId) {
		this.mguCreateUserId = mguCreateUserId;
	}

	@Column(name = "mgu_create_time")
	public Long getMguCreateTime() {
		return mguCreateTime;
	}

	public void setMguCreateTime(Long mguCreateTime) {
		this.mguCreateTime = mguCreateTime;
	}
	
}