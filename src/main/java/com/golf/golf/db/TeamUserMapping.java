package com.golf.golf.db;

import javax.persistence.*;

@Entity
@Table(name = "team_user_mapping")
public class TeamUserMapping {
    private long tumId;
    private Long tumTeamId;
    private Long tumUserId;
    private Integer tumUserType;
    private Integer tumType;
    private Long tumCreateTime;
    private Long tumCreateUserId;
    private String tumCreateUserName;
    private Long tumUpdateTime;
    private Long tumUpdateUserId;
    private String tumUpdateUserName;

    @Id
    @Column(name = "tum_id")
    public long getTumId() {
        return tumId;
    }

    public void setTumId(long tumId) {
        this.tumId = tumId;
    }

    @Basic
    @Column(name = "tum_team_id")
    public Long getTumTeamId() {
        return tumTeamId;
    }

    public void setTumTeamId(Long tumTeamId) {
        this.tumTeamId = tumTeamId;
    }

    @Basic
    @Column(name = "tum_user_id")
    public Long getTumUserId() {
        return tumUserId;
    }

    public void setTumUserId(Long tumUserId) {
        this.tumUserId = tumUserId;
    }

    @Basic
    @Column(name = "tum_user_type")
    public Integer getTumUserType() {
        return tumUserType;
    }

    public void setTumUserType(Integer tumUserType) {
        this.tumUserType = tumUserType;
    }

    @Basic
    @Column(name = "tum_type")
    public Integer getTumType() {
        return tumType;
    }

    public void setTumType(Integer tumType) {
        this.tumType = tumType;
    }

    @Basic
    @Column(name = "tum_create_time")
    public Long getTumCreateTime() {
        return tumCreateTime;
    }

    public void setTumCreateTime(Long tumCreateTime) {
        this.tumCreateTime = tumCreateTime;
    }

    @Basic
    @Column(name = "tum_create_user_id")
    public Long getTumCreateUserId() {
        return tumCreateUserId;
    }

    public void setTumCreateUserId(Long tumCreateUserId) {
        this.tumCreateUserId = tumCreateUserId;
    }

    @Basic
    @Column(name = "tum_create_user_name")
    public String getTumCreateUserName() {
        return tumCreateUserName;
    }

    public void setTumCreateUserName(String tumCreateUserName) {
        this.tumCreateUserName = tumCreateUserName;
    }

    @Basic
    @Column(name = "tum_update_time")
    public Long getTumUpdateTime() {
        return tumUpdateTime;
    }

    public void setTumUpdateTime(Long tumUpdateTime) {
        this.tumUpdateTime = tumUpdateTime;
    }

    @Basic
    @Column(name = "tum_update_user_id")
    public Long getTumUpdateUserId() {
        return tumUpdateUserId;
    }

    public void setTumUpdateUserId(Long tumUpdateUserId) {
        this.tumUpdateUserId = tumUpdateUserId;
    }

    @Basic
    @Column(name = "tum_update_user_name")
    public String getTumUpdateUserName() {
        return tumUpdateUserName;
    }

    public void setTumUpdateUserName(String tumUpdateUserName) {
        this.tumUpdateUserName = tumUpdateUserName;
    }

}
