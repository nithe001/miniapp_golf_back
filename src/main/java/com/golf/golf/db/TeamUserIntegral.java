package com.golf.golf.db;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * TeamUserIntegral entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "team_user_integral")
public class TeamUserIntegral implements java.io.Serializable {

	// Fields

	private Long tuiId;
	private Long tuiTeamId;
	private Long tuiUserId;

	// Constructors

	/** default constructor */
	public TeamUserIntegral() {
	}

	/** minimal constructor */
	public TeamUserIntegral(Long tuiId) {
		this.tuiId = tuiId;
	}

	/** full constructor */
	public TeamUserIntegral(Long tuiId, Long tuiTeamId, Long tuiUserId) {
		this.tuiId = tuiId;
		this.tuiTeamId = tuiTeamId;
		this.tuiUserId = tuiUserId;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "tui_id", unique = true, nullable = false)
	public Long getTuiId() {
		return this.tuiId;
	}

	public void setTuiId(Long tuiId) {
		this.tuiId = tuiId;
	}

	@Column(name = "tui_team_id")
	public Long getTuiTeamId() {
		return this.tuiTeamId;
	}

	public void setTuiTeamId(Long tuiTeamId) {
		this.tuiTeamId = tuiTeamId;
	}

	@Column(name = "tui_user_id")
	public Long getTuiUserId() {
		return this.tuiUserId;
	}

	public void setTuiUserId(Long tuiUserId) {
		this.tuiUserId = tuiUserId;
	}

}