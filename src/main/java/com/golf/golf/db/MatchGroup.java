package com.golf.golf.db;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "match_group")
public class MatchGroup {

	// Fields

	private Long mgId;
	private Long mgMatchId;
	private String mgGroupName;
	private Long mgCreateUserId;
	private String mgCreateUserName;
	private Long mgCreateTime;
	private Long mgUpdateUserId;
	private String mgUpdateUserName;
	private Long mgUpdateTime;

	// Constructors

	/** default constructor */
	public MatchGroup() {
	}

	/** full constructor */
	public MatchGroup(Long mgMatchId, String mgGroupName, Long mgCreateUserId,
					  String mgCreateUserName, Long mgCreateTime, Long mgUpdateUserId,
					  String mgUpdateUserName, Long mgUpdateTime) {
		this.mgMatchId = mgMatchId;
		this.mgGroupName = mgGroupName;
		this.mgCreateUserId = mgCreateUserId;
		this.mgCreateUserName = mgCreateUserName;
		this.mgCreateTime = mgCreateTime;
		this.mgUpdateUserId = mgUpdateUserId;
		this.mgUpdateUserName = mgUpdateUserName;
		this.mgUpdateTime = mgUpdateTime;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "mg_id", unique = true, nullable = false)
	public Long getMgId() {
		return this.mgId;
	}

	public void setMgId(Long mgId) {
		this.mgId = mgId;
	}

	@Column(name = "mg_match_id")
	public Long getMgMatchId() {
		return this.mgMatchId;
	}

	public void setMgMatchId(Long mgMatchId) {
		this.mgMatchId = mgMatchId;
	}

	@Column(name = "mg_group_name", length = 128)
	public String getMgGroupName() {
		return this.mgGroupName;
	}

	public void setMgGroupName(String mgGroupName) {
		this.mgGroupName = mgGroupName;
	}

	@Column(name = "mg_create_user_id")
	public Long getMgCreateUserId() {
		return this.mgCreateUserId;
	}

	public void setMgCreateUserId(Long mgCreateUserId) {
		this.mgCreateUserId = mgCreateUserId;
	}

	@Column(name = "mg_create_user_name", length = 128)
	public String getMgCreateUserName() {
		return this.mgCreateUserName;
	}

	public void setMgCreateUserName(String mgCreateUserName) {
		this.mgCreateUserName = mgCreateUserName;
	}

	@Column(name = "mg_create_time")
	public Long getMgCreateTime() {
		return this.mgCreateTime;
	}

	public void setMgCreateTime(Long mgCreateTime) {
		this.mgCreateTime = mgCreateTime;
	}

	@Column(name = "mg_update_user_id")
	public Long getMgUpdateUserId() {
		return this.mgUpdateUserId;
	}

	public void setMgUpdateUserId(Long mgUpdateUserId) {
		this.mgUpdateUserId = mgUpdateUserId;
	}

	@Column(name = "mg_update_user_name", length = 128)
	public String getMgUpdateUserName() {
		return this.mgUpdateUserName;
	}

	public void setMgUpdateUserName(String mgUpdateUserName) {
		this.mgUpdateUserName = mgUpdateUserName;
	}

	@Column(name = "mg_update_time")
	public Long getMgUpdateTime() {
		return this.mgUpdateTime;
	}

	public void setMgUpdateTime(Long mgUpdateTime) {
		this.mgUpdateTime = mgUpdateTime;
	}
}
