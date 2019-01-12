package com.golf.golf.db;

import javax.persistence.*;

@Entity
@Table(name = "match_score")
public class MatchScore {
    private Long msId;
    private Long msMatchId;
    private String msMatchTitle;
    private Long msGroupId;
    private String msGroupName;
    private Long msUserId;
    private String msUserName;
    private Integer msScore;
    private Integer msHoleNum;
    private Integer msHoleTotalRodNum;
    private Integer msPushRodNum;
    private Integer msIsUp;
    private Integer msMatchTotalRodNum;
    private Long msCreateUserId;
    private Long msCreateTime;
    private Long msUpdateUserId;
    private Long msUpdateTime;

    @Id
    @Column(name = "ms_id")
    public Long getMsId() {
        return msId;
    }

    public void setMsId(Long msId) {
        this.msId = msId;
    }

    @Basic
    @Column(name = "ms_match_id")
    public Long getMsMatchId() {
        return msMatchId;
    }

    public void setMsMatchId(Long msMatchId) {
        this.msMatchId = msMatchId;
    }

    @Basic
    @Column(name = "ms_match_title")
    public String getMsMatchTitle() {
        return msMatchTitle;
    }

    public void setMsMatchTitle(String msMatchTitle) {
        this.msMatchTitle = msMatchTitle;
    }

    @Basic
    @Column(name = "ms_group_id")
    public Long getMsGroupId() {
        return msGroupId;
    }

    public void setMsGroupId(Long msGroupId) {
        this.msGroupId = msGroupId;
    }

    @Basic
    @Column(name = "ms_group_name")
    public String getMsGroupName() {
        return msGroupName;
    }

    public void setMsGroupName(String msGroupName) {
        this.msGroupName = msGroupName;
    }

    @Basic
    @Column(name = "ms_user_id")
    public Long getMsUserId() {
        return msUserId;
    }

    public void setMsUserId(Long msUserId) {
        this.msUserId = msUserId;
    }

    @Basic
    @Column(name = "ms_user_name")
    public String getMsUserName() {
        return msUserName;
    }

    public void setMsUserName(String msUserName) {
        this.msUserName = msUserName;
    }

    @Basic
    @Column(name = "ms_score")
    public Integer getMsScore() {
        return msScore;
    }

    public void setMsScore(Integer msScore) {
        this.msScore = msScore;
    }

    @Basic
    @Column(name = "ms_hole_num")
    public Integer getMsHoleNum() {
        return msHoleNum;
    }

    public void setMsHoleNum(Integer msHoleNum) {
        this.msHoleNum = msHoleNum;
    }

    @Basic
    @Column(name = "ms_hole_total_rod_num")
    public Integer getMsHoleTotalRodNum() {
        return msHoleTotalRodNum;
    }

    public void setMsHoleTotalRodNum(Integer msHoleTotalRodNum) {
        this.msHoleTotalRodNum = msHoleTotalRodNum;
    }

    @Basic
    @Column(name = "ms_push_rod_num")
    public Integer getMsPushRodNum() {
        return msPushRodNum;
    }

    public void setMsPushRodNum(Integer msPushRodNum) {
        this.msPushRodNum = msPushRodNum;
    }

    @Basic
    @Column(name = "ms_is_up")
    public Integer getMsIsUp() {
        return msIsUp;
    }

    public void setMsIsUp(Integer msIsUp) {
        this.msIsUp = msIsUp;
    }

    @Basic
    @Column(name = "ms_match_total_rod_num")
    public Integer getMsMatchTotalRodNum() {
        return msMatchTotalRodNum;
    }

    public void setMsMatchTotalRodNum(Integer msMatchTotalRodNum) {
        this.msMatchTotalRodNum = msMatchTotalRodNum;
    }

    @Basic
    @Column(name = "ms_create_user_id")
    public Long getMsCreateUserId() {
        return msCreateUserId;
    }

    public void setMsCreateUserId(Long msCreateUserId) {
        this.msCreateUserId = msCreateUserId;
    }

    @Basic
    @Column(name = "ms_create_time")
    public Long getMsCreateTime() {
        return msCreateTime;
    }

    public void setMsCreateTime(Long msCreateTime) {
        this.msCreateTime = msCreateTime;
    }

    @Basic
    @Column(name = "ms_update_user_id")
    public Long getMsUpdateUserId() {
        return msUpdateUserId;
    }

    public void setMsUpdateUserId(Long msUpdateUserId) {
        this.msUpdateUserId = msUpdateUserId;
    }

    @Basic
    @Column(name = "ms_update_time")
    public Long getMsUpdateTime() {
        return msUpdateTime;
    }

    public void setMsUpdateTime(Long msUpdateTime) {
        this.msUpdateTime = msUpdateTime;
    }

}
