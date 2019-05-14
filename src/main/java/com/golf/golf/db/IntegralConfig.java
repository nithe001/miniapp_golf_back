package com.golf.golf.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * IntegralConfig entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "integral_config")
public class IntegralConfig implements java.io.Serializable {

	// Fields

	private Long icId;
	private Long icMatchId;
	private Long icTeamId;
	private Integer icBaseScore;
	private Integer icRodCha;
	private Integer icWinScore;
	private Long icCreateTime;
	private Long icCreateUserId;
	private String icCreateUserName;
	private Long icUpdateTime;
	private Long icUpdateUserId;
	private String icUpdateUserName;

	// Constructors

	/** default constructor */
	public IntegralConfig() {
	}

	/** full constructor */
	public IntegralConfig(Long icMatchId, Long icTeamId, Integer icBaseScore,
			Integer icRodCha, Integer icWinScore, Long icCreateTime,
			Long icCreateUserId, String icCreateUserName, Long icUpdateTime,
			Long icUpdateUserId, String icUpdateUserName) {
		this.icMatchId = icMatchId;
		this.icTeamId = icTeamId;
		this.icBaseScore = icBaseScore;
		this.icRodCha = icRodCha;
		this.icWinScore = icWinScore;
		this.icCreateTime = icCreateTime;
		this.icCreateUserId = icCreateUserId;
		this.icCreateUserName = icCreateUserName;
		this.icUpdateTime = icUpdateTime;
		this.icUpdateUserId = icUpdateUserId;
		this.icUpdateUserName = icUpdateUserName;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ic_id", unique = true, nullable = false)
	public Long getIcId() {
		return this.icId;
	}

	public void setIcId(Long icId) {
		this.icId = icId;
	}

	@Column(name = "ic_match_id")
	public Long getIcMatchId() {
		return this.icMatchId;
	}

	public void setIcMatchId(Long icMatchId) {
		this.icMatchId = icMatchId;
	}

	@Column(name = "ic_team_id")
	public Long getIcTeamId() {
		return icTeamId;
	}

	public void setIcTeamId(Long icTeamId) {
		this.icTeamId = icTeamId;
	}

	@Column(name = "ic_base_score")
	public Integer getIcBaseScore() {
		return this.icBaseScore;
	}

	public void setIcBaseScore(Integer icBaseScore) {
		this.icBaseScore = icBaseScore;
	}

	@Column(name = "ic_rod_cha")
	public Integer getIcRodCha() {
		return this.icRodCha;
	}

	public void setIcRodCha(Integer icRodCha) {
		this.icRodCha = icRodCha;
	}

	@Column(name = "ic_win_score")
	public Integer getIcWinScore() {
		return this.icWinScore;
	}

	public void setIcWinScore(Integer icWinScore) {
		this.icWinScore = icWinScore;
	}

	@Column(name = "ic_create_time")
	public Long getIcCreateTime() {
		return this.icCreateTime;
	}

	public void setIcCreateTime(Long icCreateTime) {
		this.icCreateTime = icCreateTime;
	}

	@Column(name = "ic_create_user_id")
	public Long getIcCreateUserId() {
		return this.icCreateUserId;
	}

	public void setIcCreateUserId(Long icCreateUserId) {
		this.icCreateUserId = icCreateUserId;
	}

	@Column(name = "ic_create_user_name", length = 128)
	public String getIcCreateUserName() {
		return this.icCreateUserName;
	}

	public void setIcCreateUserName(String icCreateUserName) {
		this.icCreateUserName = icCreateUserName;
	}

	@Column(name = "ic_update_time")
	public Long getIcUpdateTime() {
		return this.icUpdateTime;
	}

	public void setIcUpdateTime(Long icUpdateTime) {
		this.icUpdateTime = icUpdateTime;
	}

	@Column(name = "ic_update_user_id")
	public Long getIcUpdateUserId() {
		return this.icUpdateUserId;
	}

	public void setIcUpdateUserId(Long icUpdateUserId) {
		this.icUpdateUserId = icUpdateUserId;
	}

	@Column(name = "ic_update_user_name", length = 128)
	public String getIcUpdateUserName() {
		return this.icUpdateUserName;
	}

	public void setIcUpdateUserName(String icUpdateUserName) {
		this.icUpdateUserName = icUpdateUserName;
	}

}