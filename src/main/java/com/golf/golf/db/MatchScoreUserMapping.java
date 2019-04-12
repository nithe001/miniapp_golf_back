package com.golf.golf.db;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "match_score_user_mapping")
public class MatchScoreUserMapping {
	// Fields

	private Long msumId;
	private Long msumMatchId;
	private Long msumGroupId;
	private Long msumCreateUserId;
	private Long msumScorerId;
	private Long msumCreateTime;

	// Constructors

	/** default constructor */
	public MatchScoreUserMapping() {
	}

	/** full constructor */
	public MatchScoreUserMapping(Long msumMatchId, Long msumGroupId,
								 Long msumCreateUserId, Long msumScorerId, Long msumCreateTime) {
		this.msumMatchId = msumMatchId;
		this.msumGroupId = msumGroupId;
		this.msumCreateUserId = msumCreateUserId;
		this.msumScorerId = msumScorerId;
		this.msumCreateTime = msumCreateTime;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "msum_id", unique = true, nullable = false)
	public Long getMsumId() {
		return this.msumId;
	}

	public void setMsumId(Long msumId) {
		this.msumId = msumId;
	}

	@Column(name = "msum_match_id")
	public Long getMsumMatchId() {
		return this.msumMatchId;
	}

	public void setMsumMatchId(Long msumMatchId) {
		this.msumMatchId = msumMatchId;
	}

	@Column(name = "msum_group_id")
	public Long getMsumGroupId() {
		return this.msumGroupId;
	}

	public void setMsumGroupId(Long msumGroupId) {
		this.msumGroupId = msumGroupId;
	}

	@Column(name = "msum_create_user_id")
	public Long getMsumCreateUserId() {
		return this.msumCreateUserId;
	}

	public void setMsumCreateUserId(Long msumCreateUserId) {
		this.msumCreateUserId = msumCreateUserId;
	}

	@Column(name = "msum_scorer_id")
	public Long getMsumScorerId() {
		return this.msumScorerId;
	}

	public void setMsumScorerId(Long msumScorerId) {
		this.msumScorerId = msumScorerId;
	}

	@Column(name = "msum_create_time")
	public Long getMsumCreateTime() {
		return this.msumCreateTime;
	}

	public void setMsumCreateTime(Long msumCreateTime) {
		this.msumCreateTime = msumCreateTime;
	}

}
