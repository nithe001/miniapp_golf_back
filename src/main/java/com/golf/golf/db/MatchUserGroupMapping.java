package com.golf.golf.db;

import com.golf.golf.common.security.UserUtil;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "match_user_group_mapping")
public class MatchUserGroupMapping implements java.io.Serializable {
	// Fields

	private Long mugmId;
	private Long mugmMatchId;
	private Long mugmTeamId;
	private Integer mugmUserType;
	private Long mugmGroupId;
	private String mugmGroupName;
	private Long mugmUserId;
	private String mugmUserName;
	private Long mugmCreateUserId;
	private String mugmCreateUserName;
	private Long mugmCreateTime;
	private Long mugmUpdateUserId;
	private String mugmUpdateUserName;
	private Long mugmUpdateTime;

	// Constructors

	/** default constructor */
	public MatchUserGroupMapping() {
	}

	/** full constructor */
	public MatchUserGroupMapping(Long mugmMatchId, Long mugmTeamId, Integer mugmUserType,
								 Long mugmGroupId, String mugmGroupName, Long mugmUserId,
								 String mugmUserName, Long mugmCreateUserId,
								 String mugmCreateUserName, Long mugmCreateTime,Long mugmUpdateUserId,
								 String mugmUpdateUserName,Long mugmUpdateTime) {
		this.mugmMatchId = mugmMatchId;
		this.mugmTeamId = mugmTeamId;
		this.mugmUserType = mugmUserType;
		this.mugmGroupId = mugmGroupId;
		this.mugmGroupName = mugmGroupName;
		this.mugmUserId = mugmUserId;
		this.mugmUserName = mugmUserName;
		this.mugmCreateUserId = mugmCreateUserId;
		this.mugmCreateUserName = mugmCreateUserName;
		this.mugmCreateTime = mugmCreateTime;
		this.mugmUpdateUserId = mugmUpdateUserId;
		this.mugmUpdateUserName = mugmUpdateUserName;
		this.mugmUpdateTime = mugmUpdateTime;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "mugm_id", unique = true, nullable = false)
	public Long getMugmId() {
		return this.mugmId;
	}

	public void setMugmId(Long mugmId) {
		this.mugmId = mugmId;
	}

	@Column(name = "mugm_match_id")
	public Long getMugmMatchId() {
		return this.mugmMatchId;
	}

	public void setMugmMatchId(Long mugmMatchId) {
		this.mugmMatchId = mugmMatchId;
	}

	@Column(name = "mugm_team_id")
	public Long getMugmTeamId() {
		return mugmTeamId;
	}

	public void setMugmTeamId(Long mugmTeamId) {
		this.mugmTeamId = mugmTeamId;
	}

	@Column(name = "mugm_user_type")
	public Integer getMugmUserType() {
		return this.mugmUserType;
	}

	public void setMugmUserType(Integer mugmUserType) {
		this.mugmUserType = mugmUserType;
	}

	@Column(name = "mugm_group_id")
	public Long getMugmGroupId() {
		return this.mugmGroupId;
	}

	public void setMugmGroupId(Long mugmGroupId) {
		this.mugmGroupId = mugmGroupId;
	}

	@Column(name = "mugm_group_name", length = 128)
	public String getMugmGroupName() {
		return this.mugmGroupName;
	}

	public void setMugmGroupName(String mugmGroupName) {
		this.mugmGroupName = mugmGroupName;
	}

	@Column(name = "mugm_user_id")
	public Long getMugmUserId() {
		return this.mugmUserId;
	}

	public void setMugmUserId(Long mugmUserId) {
		this.mugmUserId = mugmUserId;
	}

	@Column(name = "mugm_user_name", length = 128)
	public String getMugmUserName() {
		return this.mugmUserName;
	}

	public void setMugmUserName(String mugmUserName) {
		this.mugmUserName = mugmUserName;
	}

	@Column(name = "mugm_create_user_id")
	public Long getMugmCreateUserId() {
		return this.mugmCreateUserId;
	}

	public void setMugmCreateUserId(Long mugmCreateUserId) {
		this.mugmCreateUserId = mugmCreateUserId;
	}

	@Column(name = "mugm_create_user_name", length = 128)
	public String getMugmCreateUserName() {
		return this.mugmCreateUserName;
	}

	public void setMugmCreateUserName(String mugmCreateUserName) {
		this.mugmCreateUserName = mugmCreateUserName;
	}

	@Column(name = "mugm_create_time")
	public Long getMugmCreateTime() {
		return this.mugmCreateTime;
	}

	public void setMugmCreateTime(Long mugmCreateTime) {
		this.mugmCreateTime = mugmCreateTime;
	}

	@Column(name = "mugm_update_user_id")
	public Long getMugmUpdateUserId() {
		return mugmUpdateUserId;
	}

	public void setMugmUpdateUserId(Long mugmUpdateUserId) {
		this.mugmUpdateUserId = mugmUpdateUserId;
	}

	@Column(name = "mugm_update_user_name", length = 128)
	public String getMugmUpdateUserName() {
		return mugmUpdateUserName;
	}

	public void setMugmUpdateUserName(String mugmUpdateUserName) {
		this.mugmUpdateUserName = mugmUpdateUserName;
	}

	@Column(name = "mugm_update_time")
	public Long getMugmUpdateTime() {
		return mugmUpdateTime;
	}

	public void setMugmUpdateTime(Long mugmUpdateTime) {
		this.mugmUpdateTime = mugmUpdateTime;
	}

	@Transient
	public void setCreate() {
		this.setMugmCreateTime(System.currentTimeMillis());
		this.setMugmCreateUserId(UserUtil.getUserId());
		this.setMugmCreateUserName(UserUtil.getShowName());
	}
}
