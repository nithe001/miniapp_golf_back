package com.golf.golf.db;

import com.golf.common.util.TimeUtil;

import javax.persistence.*;

@Entity
@Table(name = "match_info")
public class MatchInfo {
    private Long miId;
    private Integer miType;
    private Integer miPeopleNum;
    private String miTitle;
    private Integer miParkId;
    private String miParkName;
    private String miZoneBeforeNine;
    private String miZoneAfterNine;
    private String miDigest;
    private Long miMatchTime;
    private String miContent;
    private Integer miMatchOpenType;
    private Integer miJoinOpenType;
    private String miReportScoreType;
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
    @Column(name = "mi_type")
    public Integer getMiType() {
        return miType;
    }

    public void setMiType(Integer miType) {
        this.miType = miType;
    }

    @Basic
    @Column(name = "mi_people_num")
    public Integer getMiPeopleNum() {
        return miPeopleNum;
    }

    public void setMiPeopleNum(Integer miPeopleNum) {
        this.miPeopleNum = miPeopleNum;
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
    @Column(name = "mi_zone_before_nine")
    public String getMiZoneBeforeNine() {
        return miZoneBeforeNine;
    }

    public void setMiZoneBeforeNine(String miZoneBeforeNine) {
        this.miZoneBeforeNine = miZoneBeforeNine;
    }

    @Basic
    @Column(name = "mi_zone_after_nine")
    public String getMiZoneAfterNine() {
        return miZoneAfterNine;
    }

    public void setMiZoneAfterNine(String miZoneAfterNine) {
        this.miZoneAfterNine = miZoneAfterNine;
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
    public String getMiReportScoreType() {
        return miReportScoreType;
    }

    public void setMiReportScoreType(String miReportScoreType) {
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


    private String matchTimeStr;
    @Transient
    public String getMatchTimeStr() {
        createTimeStr = TimeUtil.longToString(this.getMiMatchTime(),TimeUtil.FORMAT_DATETIME_HH_MM);
        return matchTimeStr;
    }

    public void setMatchTimeStr(Long matchTime) {
        this.matchTimeStr = TimeUtil.longToString(matchTime,TimeUtil.FORMAT_DATETIME_HH_MM);
    }


    private String createTimeStr;
    @Transient
    public String getCreateTimeStr() {
        createTimeStr = TimeUtil.longToString(this.getMiCreateTime(),TimeUtil.FORMAT_DATETIME_HH_MM);
        return createTimeStr;
    }

    public void setCreateTimeStr(Long createTime) {
        this.createTimeStr = TimeUtil.longToString(createTime,TimeUtil.FORMAT_DATETIME_HH_MM);
    }

    private String stateStr;
    @Transient

    public String getStateStr() {
        return stateStr;
    }

    public void setStateStr() {
        Long nowTime = System.currentTimeMillis();
        //比赛当天的开始时间
        Long matchStartTime = TimeUtil.stringToLong(TimeUtil.longToString(this.getMiMatchTime(),TimeUtil.FORMAT_DATE),TimeUtil.FORMAT_DATE);
        Long matchEndTime = matchStartTime +(24 * 60 * 60 * 1000);
        if(this.getMiMatchTime() == null && nowTime < matchStartTime - (24 * 60 * 60 * 1000)){
            this.stateStr = "报名中";
        }else{
            if(nowTime < this.getMiMatchTime()){
                this.stateStr = "报名中";
            }else if(nowTime > this.getMiMatchTime() && nowTime < matchEndTime){
                this.stateStr = "进行中";
            }else if(nowTime > matchEndTime){
                this.stateStr = "已结束";
            }
        }
    }
}
