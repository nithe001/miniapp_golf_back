package com.golf.golf.db;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "match_score_user_mapping")
public class MatchScoreUserMapping implements java.io.Serializable {
	// Fields

	private Long msumId;
	private Long msumMatchId;
	private Long msumGroupId;
	private Long msumMatchUserId;
	private Long msumScoreUserId;
	private Integer msumType;
	private String msumQrcodePath;
	private Long msumCreateTime;

	// Constructors

	/** default constructor */
	public MatchScoreUserMapping() {
	}

	/** full constructor */
	public MatchScoreUserMapping(Long msumMatchId, Long msumGroupId, Long msumMatchUserId, Long msumScoreUserId,
								 Integer msumType, String msumQrcodePath, Long msumCreateTime) {
		this.msumMatchId = msumMatchId;
		this.msumGroupId = msumGroupId;
		this.msumMatchUserId = msumMatchUserId;
		this.msumScoreUserId = msumScoreUserId;
		this.msumType = msumType;
		this.msumQrcodePath = msumQrcodePath;
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

	@Column(name = "msum_match_user_id")
	public Long getMsumMatchUserId() {
		return this.msumMatchUserId;
	}

	public void setMsumMatchUserId(Long msumMatchUserId) {
		this.msumMatchUserId = msumMatchUserId;
	}

	@Column(name = "msum_score_user_id")
	public Long getMsumScoreUserId() {
		return this.msumScoreUserId;
	}

	public void setMsumScoreUserId(Long msumScoreUserId) {
		this.msumScoreUserId = msumScoreUserId;
	}

	@Column(name = "msum_type")
	public Integer getMsumType() {
		return msumType;
	}

	public void setMsumType(Integer msumType) {
		this.msumType = msumType;
	}

	@Column(name = "msum_qrcode_path",length = 128)
	public String getMsumQrcodePath() {
		return msumQrcodePath;
	}

	public void setMsumQrcodePath(String msumQrcodePath) {
		this.msumQrcodePath = msumQrcodePath;
	}

	@Column(name = "msum_create_time")
	public Long getMsumCreateTime() {
		return this.msumCreateTime;
	}

	public void setMsumCreateTime(Long msumCreateTime) {
		this.msumCreateTime = msumCreateTime;
	}

}
