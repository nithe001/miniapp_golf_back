package com.golf.golf.db;

import javax.persistence.*;

@Entity
@Table(name = "match_join_watch_info")
public class MatchJoinWatchInfo {
    private long mjwiId;
    private Integer mjwiType;
    private Long mjwiMatchId;
    private Long mjwiUserId;
    private Long mjwiCreateTime;

    @Id
    @Column(name = "mjwi_id")
    public long getMjwiId() {
        return mjwiId;
    }

    public void setMjwiId(long mjwiId) {
        this.mjwiId = mjwiId;
    }

    @Basic
    @Column(name = "mjwi_type")
    public Integer getMjwiType() {
        return mjwiType;
    }

    public void setMjwiType(Integer mjwiType) {
        this.mjwiType = mjwiType;
    }

    @Basic
    @Column(name = "mjwi_match_id")
    public Long getMjwiMatchId() {
        return mjwiMatchId;
    }

    public void setMjwiMatchId(Long mjwiMatchId) {
        this.mjwiMatchId = mjwiMatchId;
    }

    @Basic
    @Column(name = "mjwi_user_id")
    public Long getMjwiUserId() {
        return mjwiUserId;
    }

    public void setMjwiUserId(Long mjwiUserId) {
        this.mjwiUserId = mjwiUserId;
    }

    @Basic
    @Column(name = "mjwi_create_time")
    public Long getMjwiCreateTime() {
        return mjwiCreateTime;
    }

    public void setMjwiCreateTime(Long mjwiCreateTime) {
        this.mjwiCreateTime = mjwiCreateTime;
    }
}
