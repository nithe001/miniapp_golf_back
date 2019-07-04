package com.golf.golf.db;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "match_hole_result")
public class MatchHoleResult implements java.io.Serializable {

	// Fields

	private Long mhrId;
	private Long mhrMatchId;
	private Long mhrGroupId;
	private Long mhrTeamId;
	private Integer mhrResult;
	private Integer mhrIsSubmit;


	// Constructors

	/**
	 * default constructor
	 */
	public MatchHoleResult() {
	}

	/**
	 * full constructor
	 */
	public MatchHoleResult(Long mhrId, Long mhrMatchId, Long mhrGroupId, Long mhrTeamId,
						   Integer mhrResult,Integer mhrIsSubmit) {
		this.mhrId = mhrId;
		this.mhrMatchId = mhrMatchId;
		this.mhrGroupId = mhrGroupId;
		this.mhrTeamId = mhrTeamId;
		this.mhrResult = mhrResult;
		this.mhrIsSubmit = mhrIsSubmit;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "mhr_id", unique = true, nullable = false)
	public Long getMhrId() {
		return this.mhrId;
	}

	public void setMhrId(Long mhrId) {
		this.mhrId = mhrId;
	}

	@Column(name = "mhr_match_id")
	public Long getMhrMatchId() {
		return mhrMatchId;
	}

	public void setMhrMatchId(Long mhrMatchId) {
		this.mhrMatchId = mhrMatchId;
	}

	@Column(name = "mhr_group_id")
	public Long getMhrGroupId() {
		return mhrGroupId;
	}

	public void setMhrGroupId(Long mhrGroupId) {
		this.mhrGroupId = mhrGroupId;
	}

	@Column(name = "mhr_team_id")
	public Long getMhrTeamId() {
		return mhrTeamId;
	}

	public void setMhrTeamId(Long mhrTeamId) {
		this.mhrTeamId = mhrTeamId;
	}

	@Column(name = "mhr_result")
	public Integer getMhrResult() {
		return mhrResult;
	}

	public void setMhrResult(Integer mhrResult) {
		this.mhrResult = mhrResult;
	}

	@Column(name = "mhr_is_submit")
	public Integer getMhrIsSubmit() {
		return mhrIsSubmit;
	}

	public void setMhrIsSubmit(Integer mhrIsSubmit) {
		this.mhrIsSubmit = mhrIsSubmit;
	}
}
