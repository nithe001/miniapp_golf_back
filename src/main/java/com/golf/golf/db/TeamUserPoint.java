package com.golf.golf.db;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * TeamUserIntegral entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "team_user_point")
public class TeamUserPoint implements java.io.Serializable {

	// Fields

	private Long tupId;
	private Long tupTeamId;
	private Long tupMatchId;
	private Integer tupMatchPoint;
	private Long tupUserId;
	private Long tupCreateUserId;
	private String tupCreateUserName;
	private Long tupCreateTime;
	private Long tupUpdateUserId;
	private String tupUpdateUserName;
	private Long tupUpdateTime;

	// Constructors

	/** default constructor */
	public TeamUserPoint() {
	}

	/** full constructor */
	public TeamUserPoint(Long tupTeamId, Long tupMatchId,
						 Integer tupMatchPoint, Long tupUserId, Long tupCreateUserId,
						 String tupCreateUserName, Long tupCreateTime, Long tupUpdateUserId,
						 String tupUpdateUserName, Long tupUpdateTime) {
		this.tupTeamId = tupTeamId;
		this.tupMatchId = tupMatchId;
		this.tupMatchPoint = tupMatchPoint;
		this.tupUserId = tupUserId;
		this.tupCreateUserId = tupCreateUserId;
		this.tupCreateUserName = tupCreateUserName;
		this.tupCreateTime = tupCreateTime;
		this.tupUpdateUserId = tupUpdateUserId;
		this.tupUpdateUserName = tupUpdateUserName;
		this.tupUpdateTime = tupUpdateTime;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "tup_id", unique = true, nullable = false)
	public Long getTupId() {
		return this.tupId;
	}

	public void setTupId(Long tupId) {
		this.tupId = tupId;
	}

	@Column(name = "tup_team_id")
	public Long getTupTeamId() {
		return this.tupTeamId;
	}

	public void setTupTeamId(Long tupTeamId) {
		this.tupTeamId = tupTeamId;
	}

	@Column(name = "tup_match_id")
	public Long getTupMatchId() {
		return this.tupMatchId;
	}

	public void setTupMatchId(Long tupMatchId) {
		this.tupMatchId = tupMatchId;
	}

	@Column(name = "tup_match_point")
	public Integer getTupMatchPoint() {
		return this.tupMatchPoint;
	}

	public void setTupMatchPoint(Integer tupMatchPoint) {
		this.tupMatchPoint = tupMatchPoint;
	}

	@Column(name = "tup_user_id")
	public Long getTupUserId() {
		return this.tupUserId;
	}

	public void setTupUserId(Long tupUserId) {
		this.tupUserId = tupUserId;
	}

	@Column(name = "tup_create_user_id")
	public Long getTupCreateUserId() {
		return this.tupCreateUserId;
	}

	public void setTupCreateUserId(Long tupCreateUserId) {
		this.tupCreateUserId = tupCreateUserId;
	}

	@Column(name = "tup_create_user_name", length = 128)
	public String getTupCreateUserName() {
		return this.tupCreateUserName;
	}

	public void setTupCreateUserName(String tupCreateUserName) {
		this.tupCreateUserName = tupCreateUserName;
	}

	@Column(name = "tup_create_time")
	public Long getTupCreateTime() {
		return this.tupCreateTime;
	}

	public void setTupCreateTime(Long tupCreateTime) {
		this.tupCreateTime = tupCreateTime;
	}

	@Column(name = "tup_update_user_id")
	public Long getTupUpdateUserId() {
		return this.tupUpdateUserId;
	}

	public void setTupUpdateUserId(Long tupUpdateUserId) {
		this.tupUpdateUserId = tupUpdateUserId;
	}

	@Column(name = "tup_update_user_name", length = 128)
	public String getTupUpdateUserName() {
		return this.tupUpdateUserName;
	}

	public void setTupUpdateUserName(String tupUpdateUserName) {
		this.tupUpdateUserName = tupUpdateUserName;
	}

	@Column(name = "tup_update_time")
	public Long getTupUpdateTime() {
		return this.tupUpdateTime;
	}

	public void setTupUpdateTime(Long tupUpdateTime) {
		this.tupUpdateTime = tupUpdateTime;
	}

}