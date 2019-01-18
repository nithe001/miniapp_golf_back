package com.golf.golf.db;

import com.golf.common.util.TimeUtil;

import javax.persistence.*;

@Entity
@Table(name = "match_info")
public class MatchInfo {
    private Long miId;
    private String miTitle;
    private Integer miParkId;
    private String miParkName;
    private String miDigest;
    private Long miMatchTime;
    private String miContent;
    private Integer miMatchOpenType;
    private Integer miJoinOpenType;
    private Integer miReportScoreType;
    private Integer miHit;
    private Long miApplyEndTime;
    private String miCreateUserName;
    private Long miCreateUserId;
    private Long miCreateTime;
    private String miUpdateUserName;
    private Long miUpdateUserId;
    private Long miUpdateTime;

    @Id
    @Column(name = "mi_id")
    public Long getMiId() {
        return miId;
    }

    public void setMiId(Long miId) {
        this.miId = miId;
    }

    @Basic
    @Column(name = "mi_title")
    public String getMiTitle() {
        return miTitle;
    }

    public void setMiTitle(String miTitle) {
        this.miTitle = miTitle;
    }

    @Basic
    @Column(name = "mi_park_id")
    public Integer getMiParkId() {
        return miParkId;
    }

    public void setMiParkId(Integer miParkId) {
        this.miParkId = miParkId;
    }

    @Basic
    @Column(name = "mi_park_name")
    public String getMiParkName() {
        return miParkName;
    }

    public void setMiParkName(String miParkName) {
        this.miParkName = miParkName;
    }

    @Basic
    @Column(name = "mi_digest")
    public String getMiDigest() {
        return miDigest;
    }

    public void setMiDigest(String miDigest) {
        this.miDigest = miDigest;
    }

    @Basic
    @Column(name = "mi_match_time")
    public Long getMiMatchTime() {
        return miMatchTime;
    }

    public void setMiMatchTime(Long miMatchTime) {
        this.miMatchTime = miMatchTime;
    }

    @Basic
    @Column(name = "mi_content")
    public String getMiContent() {
        return miContent;
    }

    public void setMiContent(String miContent) {
        this.miContent = miContent;
    }

    @Basic
    @Column(name = "mi_match_open_type")
    public Integer getMiMatchOpenType() {
        return miMatchOpenType;
    }

    public void setMiMatchOpenType(Integer miMatchOpenType) {
        this.miMatchOpenType = miMatchOpenType;
    }

    @Basic
    @Column(name = "mi_join_open_type")
    public Integer getMiJoinOpenType() {
        return miJoinOpenType;
    }

    public void setMiJoinOpenType(Integer miJoinOpenType) {
        this.miJoinOpenType = miJoinOpenType;
    }

	@Basic
	@Column(name = "mi_report_score_type")
	public Integer getMiReportScoreType() {
		return miReportScoreType;
	}

	public void setMiReportScoreType(Integer miReportScoreType) {
		this.miReportScoreType = miReportScoreType;
	}

	@Basic
    @Column(name = "mi_hit")
    public Integer getMiHit() {
        return miHit;
    }

    public void setMiHit(Integer miHit) {
        this.miHit = miHit;
    }

    @Basic
    @Column(name = "mi_apply_end_time")
    public Long getMiApplyEndTime() {
        return miApplyEndTime;
    }

    public void setMiApplyEndTime(Long miApplyEndTime) {
        this.miApplyEndTime = miApplyEndTime;
    }

    @Basic
    @Column(name = "mi_create_user_name")
    public String getMiCreateUserName() {
        return miCreateUserName;
    }

    public void setMiCreateUserName(String miCreateUserName) {
        this.miCreateUserName = miCreateUserName;
    }

    @Basic
    @Column(name = "mi_create_user_id")
    public Long getMiCreateUserId() {
        return miCreateUserId;
    }

    public void setMiCreateUserId(Long miCreateUserId) {
        this.miCreateUserId = miCreateUserId;
    }

    @Basic
    @Column(name = "mi_create_time")
    public Long getMiCreateTime() {
        return miCreateTime;
    }

    public void setMiCreateTime(Long miCreateTime) {
        this.miCreateTime = miCreateTime;
    }

    @Basic
    @Column(name = "mi_update_user_name")
    public String getMiUpdateUserName() {
        return miUpdateUserName;
    }

    public void setMiUpdateUserName(String miUpdateUserName) {
        this.miUpdateUserName = miUpdateUserName;
    }

    @Basic
    @Column(name = "mi_update_user_id")
    public Long getMiUpdateUserId() {
        return miUpdateUserId;
    }

    public void setMiUpdateUserId(Long miUpdateUserId) {
        this.miUpdateUserId = miUpdateUserId;
    }

    @Basic
    @Column(name = "mi_update_time")
    public Long getMiUpdateTime() {
        return miUpdateTime;
    }

    public void setMiUpdateTime(Long miUpdateTime) {
        this.miUpdateTime = miUpdateTime;
    }


    private String createTimeStr;
    private String updateTimeStr;
    private String applyTimeStr;
    private String matchTimeStr;

    @Transient
    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(Long createTimeStr) {
        this.createTimeStr = TimeUtil.longToString(createTimeStr,TimeUtil.FORMAT_DATETIME_HH_MM);
    }
    @Transient
    public String getUpdateTimeStr() {
        return updateTimeStr;
    }

    public void setUpdateTimeStr(String updateTimeStr) {
        this.updateTimeStr = updateTimeStr;
    }
    @Transient
    public String getApplyTimeStr() {
        return applyTimeStr;
    }

    public void setApplyTimeStr(String applyTimeStr) {
        this.applyTimeStr = applyTimeStr;
    }
    @Transient
    public String getMatchTimeStr() {
        return matchTimeStr;
    }

    public void setMatchTimeStr(Long matchTimeStr) {
        this.matchTimeStr = TimeUtil.longToString(matchTimeStr,TimeUtil.FORMAT_DATETIME_HH_MM);
    }
}
