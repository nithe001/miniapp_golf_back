package com.golf.golf.db;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 每组开球球洞表
 */
@Entity
@Table(name = "match_score_start_hole", catalog = "miniapp_golf")
public class MatchScoreStartHole {

	/**
	 * 主键
	 */
    private Long shId;

	/**
	 * 比赛id
	 */
    private Long shMatchId;

    /**
     * 分组id
     */
    private Long shGroupId;

	/**
	 * 前后场（0：前  1：后）
	 */
    private Integer shBeforeAfter;

    /**
     * 球洞名称A or B or C 之类
     */
    private String shHoleName;

	/**
	 * 球洞号
	 */
    private Integer shHoleNum;


	/**
	 * 创建时间
	 */
    private Long shCreateTime;


    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "sh_id", unique = true, nullable = false)
    public Long getShId() {
        return shId;
    }

    public void setShId(Long shId) {
        this.shId = shId;
    }


    @Column(name = "sh_match_id")
    public Long getShMatchId() {
        return shMatchId;
    }

    public void setShMatchId(Long shMatchId) {
        this.shMatchId = shMatchId;
    }


    @Column(name = "sh_group_id")
    public Long getShGroupId() {
        return shGroupId;
    }

    public void setShGroupId(Long shGroupId) {
        this.shGroupId = shGroupId;
    }

    @Column(name = "sh_before_after")
    public Integer getShBeforeAfter() {
        return shBeforeAfter;
    }

    public void setShBeforeAfter(Integer shBeforeAfter) {
        this.shBeforeAfter = shBeforeAfter;
    }

    @Column(name = "sh_hole_name")
    public String getShHoleName() {
        return shHoleName;
    }

    public void setShHoleName(String shHoleName) {
        this.shHoleName = shHoleName;
    }

    @Column(name = "sh_hole_num")
    public Integer getShHoleNum() {
        return shHoleNum;
    }

    public void setShHoleNum(Integer shHoleNum) {
        this.shHoleNum = shHoleNum;
    }

    @Column(name = "sh_create_time")
    public Long getShCreateTime() {
        return shCreateTime;
    }

    public void setShCreateTime(Long shCreateTime) {
        this.shCreateTime = shCreateTime;
    }


}
