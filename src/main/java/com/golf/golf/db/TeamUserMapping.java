package com.golf.golf.db;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "team_user_mapping")
public class TeamUserMapping {
	// Fields

	private Long tumId;
	private Long tumTeamId;
	private Long tumUserId;
	private Integer tumUserType;
	private Integer tumPoint;
	private Long tumCreateTime;
	private Long tumCreateUserId;
	private String tumCreateUserName;
	private Long tumUpdateTime;
	private Long tumUpdateUserId;
	private String tumUpdateUserName;

	// Constructors

	/** default constructor */
	public TeamUserMapping() {
	}

	/** minimal constructor */
	public TeamUserMapping(Long tumId) {
		this.tumId = tumId;
	}

	/** full constructor */
	public TeamUserMapping(Long tumId, Long tumTeamId, Long tumUserId,
						   Integer tumUserType, Integer tumPoint, Long tumCreateTime,
						   Long tumCreateUserId, String tumCreateUserName, Long tumUpdateTime,
						   Long tumUpdateUserId, String tumUpdateUserName) {
		this.tumId = tumId;
		this.tumTeamId = tumTeamId;
		this.tumUserId = tumUserId;
		this.tumUserType = tumUserType;
		this.tumPoint = tumPoint;
		this.tumCreateTime = tumCreateTime;
		this.tumCreateUserId = tumCreateUserId;
		this.tumCreateUserName = tumCreateUserName;
		this.tumUpdateTime = tumUpdateTime;
		this.tumUpdateUserId = tumUpdateUserId;
		this.tumUpdateUserName = tumUpdateUserName;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "tum_id", unique = true, nullable = false)
	public Long getTumId() {
		return this.tumId;
	}

	public void setTumId(Long tumId) {
		this.tumId = tumId;
	}

	@Column(name = "tum_team_id")
	public Long getTumTeamId() {
		return this.tumTeamId;
	}

	public void setTumTeamId(Long tumTeamId) {
		this.tumTeamId = tumTeamId;
	}

	@Column(name = "tum_user_id")
	public Long getTumUserId() {
		return this.tumUserId;
	}

	public void setTumUserId(Long tumUserId) {
		this.tumUserId = tumUserId;
	}

	@Column(name = "tum_user_type")
	public Integer getTumUserType() {
		return this.tumUserType;
	}

	public void setTumUserType(Integer tumUserType) {
		this.tumUserType = tumUserType;
	}

	@Column(name = "tum_point")
	public Integer getTumPoint() {
		return tumPoint;
	}

	public void setTumPoint(Integer tumPoint) {
		this.tumPoint = tumPoint;
	}

	@Column(name = "tum_create_time")
	public Long getTumCreateTime() {
		return this.tumCreateTime;
	}

	public void setTumCreateTime(Long tumCreateTime) {
		this.tumCreateTime = tumCreateTime;
	}

	@Column(name = "tum_create_user_id")
	public Long getTumCreateUserId() {
		return this.tumCreateUserId;
	}

	public void setTumCreateUserId(Long tumCreateUserId) {
		this.tumCreateUserId = tumCreateUserId;
	}

	@Column(name = "tum_create_user_name", length = 128)
	public String getTumCreateUserName() {
		return this.tumCreateUserName;
	}

	public void setTumCreateUserName(String tumCreateUserName) {
		this.tumCreateUserName = tumCreateUserName;
	}

	@Column(name = "tum_update_time")
	public Long getTumUpdateTime() {
		return this.tumUpdateTime;
	}

	public void setTumUpdateTime(Long tumUpdateTime) {
		this.tumUpdateTime = tumUpdateTime;
	}

	@Column(name = "tum_update_user_id")
	public Long getTumUpdateUserId() {
		return this.tumUpdateUserId;
	}

	public void setTumUpdateUserId(Long tumUpdateUserId) {
		this.tumUpdateUserId = tumUpdateUserId;
	}

	@Column(name = "tum_update_user_name", length = 128)
	public String getTumUpdateUserName() {
		return this.tumUpdateUserName;
	}

	public void setTumUpdateUserName(String tumUpdateUserName) {
		this.tumUpdateUserName = tumUpdateUserName;
	}

}
