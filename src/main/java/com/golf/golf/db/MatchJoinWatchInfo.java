package com.golf.golf.db;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "match_join_watch_info")
public class MatchJoinWatchInfo implements java.io.Serializable{
	// Fields

	private Long mjwiId;
	private Integer mjwiType;
	private Long mjwiMatchId;
	private Long mjwiUserId;
	private Long mjwiCreateTime;

    private Long mjwiWatchNum;

	// Constructors

	/** default constructor */
	public MatchJoinWatchInfo() {
	}

	/** full constructor */
	public MatchJoinWatchInfo(Integer mjwiType, Long mjwiMatchId,
							  Long mjwiUserId, Long mjwiCreateTime, Long mjwiWatchNum) {
		this.mjwiType = mjwiType;
		this.mjwiMatchId = mjwiMatchId;
		this.mjwiUserId = mjwiUserId;
		this.mjwiCreateTime = mjwiCreateTime;
        this.mjwiWatchNum = mjwiWatchNum;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "mjwi_id", unique = true, nullable = false)
	public Long getMjwiId() {
		return this.mjwiId;
	}

	public void setMjwiId(Long mjwiId) {
		this.mjwiId = mjwiId;
	}

	@Column(name = "mjwi_type")
	public Integer getMjwiType() {
		return this.mjwiType;
	}

	public void setMjwiType(Integer mjwiType) {
		this.mjwiType = mjwiType;
	}

	@Column(name = "mjwi_match_id")
	public Long getMjwiMatchId() {
		return this.mjwiMatchId;
	}

	public void setMjwiMatchId(Long mjwiMatchId) {
		this.mjwiMatchId = mjwiMatchId;
	}

	@Column(name = "mjwi_user_id")
	public Long getMjwiUserId() {
		return this.mjwiUserId;
	}

	public void setMjwiUserId(Long mjwiUserId) {
		this.mjwiUserId = mjwiUserId;
	}

	@Column(name = "mjwi_create_time")
	public Long getMjwiCreateTime() {
		return this.mjwiCreateTime;
	}

	public void setMjwiCreateTime(Long mjwiCreateTime) {
		this.mjwiCreateTime = mjwiCreateTime;
	}

	//增加观赛次数 nhq
    @Column(name = "mjwi_watch_num")
    public Long getMjwiWatchNum() {
        return this.mjwiWatchNum;
    }

    public void setMjwiWatchNum(Long mjwiWatchNum) {
        this.mjwiWatchNum = mjwiWatchNum;
    }
}
