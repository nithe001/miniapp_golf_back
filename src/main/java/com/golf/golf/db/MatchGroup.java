package com.golf.golf.db;

import javax.persistence.*;

@Entity
@Table(name = "match_group")
public class MatchGroup {
    private Long mgId;
    private Long mgMatchId;
    private String mgGroupName;
    private Long mgCreateUserId;
    private String mgCreateUserName;
    private Long mgCreateTime;
    private Long mgUpdateUserId;
    private String mgUpdateUserName;
    private Long mgUpdateTime;

    @Id
    @Column(name = "mg_id")
    public Long getMgId() {
        return mgId;
    }

    public void setMgId(Long mgId) {
        this.mgId = mgId;
    }

    @Basic
    @Column(name = "mg_match_id")
    public Long getMgMatchId() {
        return mgMatchId;
    }

    public void setMgMatchId(Long mgMatchId) {
        this.mgMatchId = mgMatchId;
    }

    @Basic
    @Column(name = "mg_group_name")
    public String getMgGroupName() {
        return mgGroupName;
    }

    public void setMgGroupName(String mgGroupName) {
        this.mgGroupName = mgGroupName;
    }

    @Basic
    @Column(name = "mg_create_user_id")
    public Long getMgCreateUserId() {
        return mgCreateUserId;
    }

    public void setMgCreateUserId(Long mgCreateUserId) {
        this.mgCreateUserId = mgCreateUserId;
    }

    @Basic
    @Column(name = "mg_create_user_name")
    public String getMgCreateUserName() {
        return mgCreateUserName;
    }

    public void setMgCreateUserName(String mgCreateUserName) {
        this.mgCreateUserName = mgCreateUserName;
    }

    @Basic
    @Column(name = "mg_create_time")
    public Long getMgCreateTime() {
        return mgCreateTime;
    }

    public void setMgCreateTime(Long mgCreateTime) {
        this.mgCreateTime = mgCreateTime;
    }

    @Basic
    @Column(name = "mg_update_user_id")
    public Long getMgUpdateUserId() {
        return mgUpdateUserId;
    }

    public void setMgUpdateUserId(Long mgUpdateUserId) {
        this.mgUpdateUserId = mgUpdateUserId;
    }

    @Basic
    @Column(name = "mg_update_user_name")
    public String getMgUpdateUserName() {
        return mgUpdateUserName;
    }

    public void setMgUpdateUserName(String mgUpdateUserName) {
        this.mgUpdateUserName = mgUpdateUserName;
    }

    @Basic
    @Column(name = "mg_update_time")
    public Long getMgUpdateTime() {
        return mgUpdateTime;
    }

    public void setMgUpdateTime(Long mgUpdateTime) {
        this.mgUpdateTime = mgUpdateTime;
    }
}
