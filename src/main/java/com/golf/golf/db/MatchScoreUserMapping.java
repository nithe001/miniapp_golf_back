package com.golf.golf.db;

import javax.persistence.*;

@Entity
@Table(name = "match_score_user_mapping")
public class MatchScoreUserMapping {
    private long msumId;
    private Long msumMatchId;
    private Long msumGroupId;
    private Long msumCreateUserId;
    private Long msumScorerId;
    private Long msumCreateTime;

    @Id
    @Column(name = "msum_id")
    public long getMsumId() {
        return msumId;
    }

    public void setMsumId(long msumId) {
        this.msumId = msumId;
    }

    @Basic
    @Column(name = "msum_match_id")
    public Long getMsumMatchId() {
        return msumMatchId;
    }

    public void setMsumMatchId(Long msumMatchId) {
        this.msumMatchId = msumMatchId;
    }

    @Basic
    @Column(name = "msum_group_id")
    public Long getMsumGroupId() {
        return msumGroupId;
    }

    public void setMsumGroupId(Long msumGroupId) {
        this.msumGroupId = msumGroupId;
    }

    @Basic
    @Column(name = "msum_create_user_id")
    public Long getMsumCreateUserId() {
        return msumCreateUserId;
    }

    public void setMsumCreateUserId(Long msumCreateUserId) {
        this.msumCreateUserId = msumCreateUserId;
    }

    @Basic
    @Column(name = "msum_scorer_id")
    public Long getMsumScorerId() {
        return msumScorerId;
    }

    public void setMsumScorerId(Long msumScorerId) {
        this.msumScorerId = msumScorerId;
    }

    @Basic
    @Column(name = "msum_create_time")
    public Long getMsumCreateTime() {
        return msumCreateTime;
    }

    public void setMsumCreateTime(Long msumCreateTime) {
        this.msumCreateTime = msumCreateTime;
    }

}
