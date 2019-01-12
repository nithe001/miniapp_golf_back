package com.golf.golf.db;

import com.golf.common.util.TimeUtil;

import javax.persistence.*;

@Entity
@Table(name = "admin_user")
public class AdminUser {
    private Long auId;
    private String auUserName;
    private String auPassword;
    private String auShowName;
    private Integer auSex;
    private Integer auAge;
    private String auTel;
    private String auEmail;
    private Long auRole;
    private Integer auIsValid;
    private Long auCreateDate;
    private Long auCreateUserId;
    private String auCreateUserName;
    private Long auUpdateUserId;
    private String auUpdateUserName;
    private Long auUpdateDate;

    @Id
    @Column(name = "au_id")
    public Long getAuId() {
        return auId;
    }

    public void setAuId(Long auId) {
        this.auId = auId;
    }

    @Basic
    @Column(name = "au_user_name")
    public String getAuUserName() {
        return auUserName;
    }

    public void setAuUserName(String auUserName) {
        this.auUserName = auUserName;
    }

    @Basic
    @Column(name = "au_password")
    public String getAuPassword() {
        return auPassword;
    }

    public void setAuPassword(String auPassword) {
        this.auPassword = auPassword;
    }

    @Basic
    @Column(name = "au_show_name")
    public String getAuShowName() {
        return auShowName;
    }

    public void setAuShowName(String auShowName) {
        this.auShowName = auShowName;
    }

    @Basic
    @Column(name = "au_sex")
    public Integer getAuSex() {
        return auSex;
    }

    public void setAuSex(Integer auSex) {
        this.auSex = auSex;
    }

    @Basic
    @Column(name = "au_age")
    public Integer getAuAge() {
        return auAge;
    }

    public void setAuAge(Integer auAge) {
        this.auAge = auAge;
    }

    @Basic
    @Column(name = "au_tel")
    public String getAuTel() {
        return auTel;
    }

    public void setAuTel(String auTel) {
        this.auTel = auTel;
    }

    @Basic
    @Column(name = "au_email")
    public String getAuEmail() {
        return auEmail;
    }

    public void setAuEmail(String auEmail) {
        this.auEmail = auEmail;
    }

    @Basic
    @Column(name = "au_role")
    public Long getAuRole() {
        return auRole;
    }

    public void setAuRole(Long auRole) {
        this.auRole = auRole;
    }

    @Basic
    @Column(name = "au_is_valid")
    public Integer getAuIsValid() {
        return auIsValid;
    }

    public void setAuIsValid(Integer auIsValid) {
        this.auIsValid = auIsValid;
    }

    @Basic
    @Column(name = "au_create_date")
    public Long getAuCreateDate() {
        return auCreateDate;
    }

    public void setAuCreateDate(Long auCreateDate) {
        this.auCreateDate = auCreateDate;
    }

    @Basic
    @Column(name = "au_create_user_id")
    public Long getAuCreateUserId() {
        return auCreateUserId;
    }

    public void setAuCreateUserId(Long auCreateUserId) {
        this.auCreateUserId = auCreateUserId;
    }

    @Basic
    @Column(name = "au_create_user_name")
    public String getAuCreateUserName() {
        return auCreateUserName;
    }

    public void setAuCreateUserName(String auCreateUserName) {
        this.auCreateUserName = auCreateUserName;
    }

    @Basic
    @Column(name = "au_update_user_id")
    public Long getAuUpdateUserId() {
        return auUpdateUserId;
    }

    public void setAuUpdateUserId(Long auUpdateUserId) {
        this.auUpdateUserId = auUpdateUserId;
    }

    @Basic
    @Column(name = "au_update_user_name")
    public String getAuUpdateUserName() {
        return auUpdateUserName;
    }

    public void setAuUpdateUserName(String auUpdateUserName) {
        this.auUpdateUserName = auUpdateUserName;
    }

    @Basic
    @Column(name = "au_update_date")
    public Long getAuUpdateDate() {
        return auUpdateDate;
    }

    public void setAuUpdateDate(Long auUpdateDate) {
        this.auUpdateDate = auUpdateDate;
    }

    private String createTimeStr;
    private String updateTimeStr;


    @Transient
    public String getCreateTimeStr() {
        return TimeUtil.longToString(this.getAuCreateDate(),TimeUtil.FORMAT_DATETIME_HH_MM);
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    @Transient
    public String getUpdateTimeStr() {
        return TimeUtil.longToString(this.getAuUpdateDate(),TimeUtil.FORMAT_DATETIME_HH_MM);
    }

    public void setUpdateTimeStr(String updateTimeStr) {
        this.updateTimeStr = updateTimeStr;
    }

}
