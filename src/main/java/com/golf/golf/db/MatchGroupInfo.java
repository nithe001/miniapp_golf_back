package com.golf.golf.db;

import javax.persistence.*;

@Entity
@Table(name = "match_group_info")
public class MatchGroupInfo {
    private Long mgiId;
    private Long mgiMatchId;
    private String mgiGroupName;
    private Long mgiUserId;
    private String mgiUserName;
    private Long mgiCreateUserId;
    private String mgiCreateUserName;
    private Long mgiCreateTime;
    private Long mgiUpdateUserId;
    private String mgiUpdateUserName;
    private Long mgiUpdateTime;

    @Id
    @Column(name = "mgi_id")
    public Long getMgiId() {
        return mgiId;
    }

    public void setMgiId(Long mgiId) {
        this.mgiId = mgiId;
    }

    @Basic
    @Column(name = "mgi_match_id")
    public Long getMgiMatchId() {
        return mgiMatchId;
    }

    public void setMgiMatchId(Long mgiMatchId) {
        this.mgiMatchId = mgiMatchId;
    }

    @Basic
    @Column(name = "mgi_group_name")
    public String getMgiGroupName() {
        return mgiGroupName;
    }

    public void setMgiGroupName(String mgiGroupName) {
        this.mgiGroupName = mgiGroupName;
    }

    @Basic
    @Column(name = "mgi_user_id")
    public Long getMgiUserId() {
        return mgiUserId;
    }

    public void setMgiUserId(Long mgiUserId) {
        this.mgiUserId = mgiUserId;
    }

    @Basic
    @Column(name = "mgi_user_name")
    public String getMgiUserName() {
        return mgiUserName;
    }

    public void setMgiUserName(String mgiUserName) {
        this.mgiUserName = mgiUserName;
    }

    @Basic
    @Column(name = "mgi_create_user_id")
    public Long getMgiCreateUserId() {
        return mgiCreateUserId;
    }

    public void setMgiCreateUserId(Long mgiCreateUserId) {
        this.mgiCreateUserId = mgiCreateUserId;
    }

    @Basic
    @Column(name = "mgi_create_user_name")
    public String getMgiCreateUserName() {
        return mgiCreateUserName;
    }

    public void setMgiCreateUserName(String mgiCreateUserName) {
        this.mgiCreateUserName = mgiCreateUserName;
    }

    @Basic
    @Column(name = "mgi_create_time")
    public Long getMgiCreateTime() {
        return mgiCreateTime;
    }

    public void setMgiCreateTime(Long mgiCreateTime) {
        this.mgiCreateTime = mgiCreateTime;
    }

    @Basic
    @Column(name = "mgi_update_user_id")
    public Long getMgiUpdateUserId() {
        return mgiUpdateUserId;
    }

    public void setMgiUpdateUserId(Long mgiUpdateUserId) {
        this.mgiUpdateUserId = mgiUpdateUserId;
    }

    @Basic
    @Column(name = "mgi_update_user_name")
    public String getMgiUpdateUserName() {
        return mgiUpdateUserName;
    }

    public void setMgiUpdateUserName(String mgiUpdateUserName) {
        this.mgiUpdateUserName = mgiUpdateUserName;
    }

    @Basic
    @Column(name = "mgi_update_time")
    public Long getMgiUpdateTime() {
        return mgiUpdateTime;
    }

    public void setMgiUpdateTime(Long mgiUpdateTime) {
        this.mgiUpdateTime = mgiUpdateTime;
    }

}
