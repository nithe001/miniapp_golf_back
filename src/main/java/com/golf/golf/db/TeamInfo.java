package com.golf.golf.db;

import com.golf.common.util.TimeUtil;

import javax.persistence.*;

@Entity
@Table(name = "team_info")
public class TeamInfo {
    private long tiId;
    private String tiLogo;
    private String tiName;
    private String tiSignature;
    private String tiDigest;
    private String tiAddress;
    private String tiSlogan;
    private Integer tiJoinOpenType;
    private Integer tiInfoOpenType;
    private Integer tiMatchResultAuditType;
    private Integer tiUserInfoType;
    private Long tiCreateTime;
    private Long tiCreateUserId;
    private String tiCreateUserName;
    private Long tiUpdateTime;
    private Long tiUpdateUserId;
    private String tiUpdateUserName;

    @Id
    @Column(name = "ti_id")
    public long getTiId() {
        return tiId;
    }

    public void setTiId(long tiId) {
        this.tiId = tiId;
    }

    @Basic
    @Column(name = "ti_logo")
    public String getTiLogo() {
        return tiLogo;
    }

    public void setTiLogo(String tiLogo) {
        this.tiLogo = tiLogo;
    }

    @Basic
    @Column(name = "ti_name")
    public String getTiName() {
        return tiName;
    }

    public void setTiName(String tiName) {
        this.tiName = tiName;
    }

    @Basic
    @Column(name = "ti_signature")
    public String getTiSignature() {
        return tiSignature;
    }

    public void setTiSignature(String tiSignature) {
        this.tiSignature = tiSignature;
    }

    @Basic
    @Column(name = "ti_digest")
    public String getTiDigest() {
        return tiDigest;
    }

    public void setTiDigest(String tiDigest) {
        this.tiDigest = tiDigest;
    }

    @Basic
    @Column(name = "ti_address")
    public String getTiAddress() {
        return tiAddress;
    }

    public void setTiAddress(String tiAddress) {
        this.tiAddress = tiAddress;
    }

    @Basic
    @Column(name = "ti_slogan")
    public String getTiSlogan() {
        return tiSlogan;
    }

    public void setTiSlogan(String tiSlogan) {
        this.tiSlogan = tiSlogan;
    }

    @Basic
    @Column(name = "ti_join_open_type")
    public Integer getTiJoinOpenType() {
        return tiJoinOpenType;
    }

    public void setTiJoinOpenType(Integer tiJoinOpenType) {
        this.tiJoinOpenType = tiJoinOpenType;
    }

    @Basic
    @Column(name = "ti_info_open_type")
    public Integer getTiInfoOpenType() {
        return tiInfoOpenType;
    }

    public void setTiInfoOpenType(Integer tiInfoOpenType) {
        this.tiInfoOpenType = tiInfoOpenType;
    }

    @Basic
    @Column(name = "ti_match_result_audit_type")
    public Integer getTiMatchResultAuditType() {
        return tiMatchResultAuditType;
    }

    public void setTiMatchResultAuditType(Integer tiMatchResultAuditType) {
        this.tiMatchResultAuditType = tiMatchResultAuditType;
    }

    @Basic
    @Column(name = "ti_user_info_type")
    public Integer getTiUserInfoType() {
        return tiUserInfoType;
    }

    public void setTiUserInfoType(Integer tiUserInfoType) {
        this.tiUserInfoType = tiUserInfoType;
    }

    @Basic
    @Column(name = "ti_create_time")
    public Long getTiCreateTime() {
        return tiCreateTime;
    }

    public void setTiCreateTime(Long tiCreateTime) {
        this.tiCreateTime = tiCreateTime;
    }

    @Basic
    @Column(name = "ti_create_user_id")
    public Long getTiCreateUserId() {
        return tiCreateUserId;
    }

    public void setTiCreateUserId(Long tiCreateUserId) {
        this.tiCreateUserId = tiCreateUserId;
    }

    @Basic
    @Column(name = "ti_create_user_name")
    public String getTiCreateUserName() {
        return tiCreateUserName;
    }

    public void setTiCreateUserName(String tiCreateUserName) {
        this.tiCreateUserName = tiCreateUserName;
    }

    @Basic
    @Column(name = "ti_update_time")
    public Long getTiUpdateTime() {
        return tiUpdateTime;
    }

    public void setTiUpdateTime(Long tiUpdateTime) {
        this.tiUpdateTime = tiUpdateTime;
    }

    @Basic
    @Column(name = "ti_update_user_id")
    public Long getTiUpdateUserId() {
        return tiUpdateUserId;
    }

    public void setTiUpdateUserId(Long tiUpdateUserId) {
        this.tiUpdateUserId = tiUpdateUserId;
    }

    @Basic
    @Column(name = "ti_update_user_name")
    public String getTiUpdateUserName() {
        return tiUpdateUserName;
    }

    public void setTiUpdateUserName(String tiUpdateUserName) {
        this.tiUpdateUserName = tiUpdateUserName;
    }

    private String createTimeStr;
    @Transient
    public String getCreateTimeStr() {
        createTimeStr = TimeUtil.longToString(this.getTiCreateTime(),TimeUtil.FORMAT_DATETIME_HH_MM);
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }
}
