package com.golf.golf.db;

import javax.persistence.*;

@Entity
@Table(name = "match_user_group_mapping")
public class MatchUserGroupMapping {
    private Long mugmId;
    private Long mugmMatchId;
    private Integer mugmIsCaptain;
    private String mugmGroupName;
    private Long mugmUserId;
    private String mugmUserName;
    private Long mugmCreateUserId;
    private String mugmCreateUserName;
    private Long mugmCreateTime;
    private Long mugmUpdateUserId;
    private String mugmUpdateUserName;
    private Long mugmUpdateTime;

    @Id
    @Column(name = "mugm_id")
    public Long getMugmId() {
        return mugmId;
    }

    public void setMugmId(Long mgiId) {
        this.mugmId = mgiId;
    }

    @Basic
    @Column(name = "mugm_match_id")
    public Long getMugmMatchId() {
        return mugmMatchId;
    }

    public void setMugmMatchId(Long mgiMatchId) {
        this.mugmMatchId = mgiMatchId;
    }

    @Basic
    @Column(name = "mugm_is_captain")
    public Integer getMugmIsCaptain() {
        return mugmIsCaptain;
    }

    public void setMugmIsCaptain(Integer mugmIsCaptain) {
        this.mugmIsCaptain = mugmIsCaptain;
    }

    @Basic
    @Column(name = "mugm_group_name")
    public String getMugmGroupName() {
        return mugmGroupName;
    }

    public void setMugmGroupName(String mgiGroupName) {
        this.mugmGroupName = mgiGroupName;
    }

    @Basic
    @Column(name = "mugm_user_id")
    public Long getMugmUserId() {
        return mugmUserId;
    }

    public void setMugmUserId(Long mgiUserId) {
        this.mugmUserId = mgiUserId;
    }

    @Basic
    @Column(name = "mugm_user_name")
    public String getMugmUserName() {
        return mugmUserName;
    }

    public void setMugmUserName(String mgiUserName) {
        this.mugmUserName = mgiUserName;
    }

    @Basic
    @Column(name = "mugm_create_user_id")
    public Long getMugmCreateUserId() {
        return mugmCreateUserId;
    }

    public void setMugmCreateUserId(Long mgiCreateUserId) {
        this.mugmCreateUserId = mgiCreateUserId;
    }

    @Basic
    @Column(name = "mugm_create_user_name")
    public String getMugmCreateUserName() {
        return mugmCreateUserName;
    }

    public void setMugmCreateUserName(String mgiCreateUserName) {
        this.mugmCreateUserName = mgiCreateUserName;
    }

    @Basic
    @Column(name = "mugm_create_time")
    public Long getMugmCreateTime() {
        return mugmCreateTime;
    }

    public void setMugmCreateTime(Long mgiCreateTime) {
        this.mugmCreateTime = mgiCreateTime;
    }

    @Basic
    @Column(name = "mugm_update_user_id")
    public Long getMugmUpdateUserId() {
        return mugmUpdateUserId;
    }

    public void setMugmUpdateUserId(Long mgiUpdateUserId) {
        this.mugmUpdateUserId = mgiUpdateUserId;
    }

    @Basic
    @Column(name = "mugm_update_user_name")
    public String getMugmUpdateUserName() {
        return mugmUpdateUserName;
    }

    public void setMugmUpdateUserName(String mgiUpdateUserName) {
        this.mugmUpdateUserName = mgiUpdateUserName;
    }

    @Basic
    @Column(name = "mugm_update_time")
    public Long getMugmUpdateTime() {
        return mugmUpdateTime;
    }

    public void setMugmUpdateTime(Long mgiUpdateTime) {
        this.mugmUpdateTime = mgiUpdateTime;
    }

}