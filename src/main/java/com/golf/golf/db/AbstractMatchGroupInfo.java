package com.golf.golf.db;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * AbstractUserMatchGroup entity provides the base persistence definition of the
 * UserMatchGroup entity. @author MyEclipse Persistence Tools
 */
@MappedSuperclass
public abstract class AbstractMatchGroupInfo implements java.io.Serializable {

	// Fields
	private Long mgiId;
	private Long mgiMatchId;
	private String mgiGroupName;
	private Long mgiCreateUserId;
	private Long mgiCreateTime;

	// Constructors

	/** default constructor */
	public AbstractMatchGroupInfo() {
	}

	/** minimal constructor */
	public AbstractMatchGroupInfo(Long mgiId) {
		this.mgiId = mgiId;
	}

	/** full constructor */
	public AbstractMatchGroupInfo(Long mgiId, Long mgiMatchId, String mgiGroupName,
								  Long mgiCreateUserId, Long mgiCreateTime) {
		this.mgiId = mgiId;
		this.mgiMatchId = mgiMatchId;
		this.mgiGroupName = mgiGroupName;
		this.mgiCreateUserId = mgiCreateUserId;
		this.mgiCreateTime = mgiCreateTime;
	}

	// Property accessors
	@Id
	@Column(name = "mgi_id", unique = true, nullable = false)
	public Long getMgiId() {
		return this.mgiId;
	}

	public void setMgiId(Long mgiId) {
		this.mgiId = mgiId;
	}

	@Column(name = "mgi_match_id")
	public Long getMgiMatchId() {
		return this.mgiMatchId;
	}

	public void setMgiMatchId(Long mgiMatchId) {
		this.mgiMatchId = mgiMatchId;
	}

	@Column(name = "mgi_group_name", length = 128)
	public String getMgiGroupName() {
		return this.mgiGroupName;
	}

	public void setMgiGroupName(String mgiGroupName) {
		this.mgiGroupName = mgiGroupName;
	}

	@Column(name = "mgi_create_user_id")
	public Long getMgiCreateUserId() {
		return this.mgiCreateUserId;
	}

	public void setMgiCreateUserId(Long mgiCreateUserId) {
		this.mgiCreateUserId = mgiCreateUserId;
	}

	@Column(name = "mgi_create_time")
	public Long getMgiCreateTime() {
		return mgiCreateTime;
	}

	public void setMgiCreateTime(Long mgiCreateTime) {
		this.mgiCreateTime = mgiCreateTime;
	}
}