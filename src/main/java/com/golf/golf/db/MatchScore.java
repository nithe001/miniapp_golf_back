package com.golf.golf.db;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "match_score")
public class MatchScore {
	// Fields

	private Long msId;
	private Long msTeamId;
	private Long msMatchId;
	private String msMatchTitle;
	private Long msGroupId;
	private String msGroupName;
	private Long msUserId;
	private String msUserName;
	private Integer msScore;
	private Integer msHoleNum;
	private Integer msHoleTotalRodNum;
	private Integer msPushRodNum;
	private Integer msIsUp;
	private Integer msMatchTotalRodNum;
	private Long msCreateUserId;
	private Long msCreateTime;
	private Long msUpdateUserId;
	private Long msUpdateTime;

	// Constructors

	/** default constructor */
	public MatchScore() {
	}

	/** full constructor */
	public MatchScore(Long msTeamId, Long msMatchId, String msMatchTitle,
					  Long msGroupId, String msGroupName, Long msUserId,
					  String msUserName, Integer msScore, Integer msHoleNum,
					  Integer msHoleTotalRodNum, Integer msPushRodNum, Integer msIsUp,
					  Integer msMatchTotalRodNum, Long msCreateUserId, Long msCreateTime,
					  Long msUpdateUserId, Long msUpdateTime) {
		this.msTeamId = msTeamId;
		this.msMatchId = msMatchId;
		this.msMatchTitle = msMatchTitle;
		this.msGroupId = msGroupId;
		this.msGroupName = msGroupName;
		this.msUserId = msUserId;
		this.msUserName = msUserName;
		this.msScore = msScore;
		this.msHoleNum = msHoleNum;
		this.msHoleTotalRodNum = msHoleTotalRodNum;
		this.msPushRodNum = msPushRodNum;
		this.msIsUp = msIsUp;
		this.msMatchTotalRodNum = msMatchTotalRodNum;
		this.msCreateUserId = msCreateUserId;
		this.msCreateTime = msCreateTime;
		this.msUpdateUserId = msUpdateUserId;
		this.msUpdateTime = msUpdateTime;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ms_id", unique = true, nullable = false)
	public Long getMsId() {
		return this.msId;
	}

	public void setMsId(Long msId) {
		this.msId = msId;
	}

	@Column(name = "ms_team_id")
	public Long getMsTeamId() {
		return this.msTeamId;
	}

	public void setMsTeamId(Long msTeamId) {
		this.msTeamId = msTeamId;
	}

	@Column(name = "ms_match_id")
	public Long getMsMatchId() {
		return this.msMatchId;
	}

	public void setMsMatchId(Long msMatchId) {
		this.msMatchId = msMatchId;
	}

	@Column(name = "ms_match_title")
	public String getMsMatchTitle() {
		return this.msMatchTitle;
	}

	public void setMsMatchTitle(String msMatchTitle) {
		this.msMatchTitle = msMatchTitle;
	}

	@Column(name = "ms_group_id")
	public Long getMsGroupId() {
		return this.msGroupId;
	}

	public void setMsGroupId(Long msGroupId) {
		this.msGroupId = msGroupId;
	}

	@Column(name = "ms_group_name")
	public String getMsGroupName() {
		return this.msGroupName;
	}

	public void setMsGroupName(String msGroupName) {
		this.msGroupName = msGroupName;
	}

	@Column(name = "ms_user_id")
	public Long getMsUserId() {
		return this.msUserId;
	}

	public void setMsUserId(Long msUserId) {
		this.msUserId = msUserId;
	}

	@Column(name = "ms_user_name", length = 128)
	public String getMsUserName() {
		return this.msUserName;
	}

	public void setMsUserName(String msUserName) {
		this.msUserName = msUserName;
	}

	@Column(name = "ms_score")
	public Integer getMsScore() {
		return this.msScore;
	}

	public void setMsScore(Integer msScore) {
		this.msScore = msScore;
	}

	@Column(name = "ms_hole_num")
	public Integer getMsHoleNum() {
		return this.msHoleNum;
	}

	public void setMsHoleNum(Integer msHoleNum) {
		this.msHoleNum = msHoleNum;
	}

	@Column(name = "ms_hole_total_rod_num")
	public Integer getMsHoleTotalRodNum() {
		return this.msHoleTotalRodNum;
	}

	public void setMsHoleTotalRodNum(Integer msHoleTotalRodNum) {
		this.msHoleTotalRodNum = msHoleTotalRodNum;
	}

	@Column(name = "ms_push_rod_num")
	public Integer getMsPushRodNum() {
		return this.msPushRodNum;
	}

	public void setMsPushRodNum(Integer msPushRodNum) {
		this.msPushRodNum = msPushRodNum;
	}

	@Column(name = "ms_is_up")
	public Integer getMsIsUp() {
		return this.msIsUp;
	}

	public void setMsIsUp(Integer msIsUp) {
		this.msIsUp = msIsUp;
	}

	@Column(name = "ms_match_total_rod_num")
	public Integer getMsMatchTotalRodNum() {
		return this.msMatchTotalRodNum;
	}

	public void setMsMatchTotalRodNum(Integer msMatchTotalRodNum) {
		this.msMatchTotalRodNum = msMatchTotalRodNum;
	}

	@Column(name = "ms_create_user_id")
	public Long getMsCreateUserId() {
		return this.msCreateUserId;
	}

	public void setMsCreateUserId(Long msCreateUserId) {
		this.msCreateUserId = msCreateUserId;
	}

	@Column(name = "ms_create_time")
	public Long getMsCreateTime() {
		return this.msCreateTime;
	}

	public void setMsCreateTime(Long msCreateTime) {
		this.msCreateTime = msCreateTime;
	}

	@Column(name = "ms_update_user_id")
	public Long getMsUpdateUserId() {
		return this.msUpdateUserId;
	}

	public void setMsUpdateUserId(Long msUpdateUserId) {
		this.msUpdateUserId = msUpdateUserId;
	}

	@Column(name = "ms_update_time")
	public Long getMsUpdateTime() {
		return this.msUpdateTime;
	}

	public void setMsUpdateTime(Long msUpdateTime) {
		this.msUpdateTime = msUpdateTime;
	}
}
