package com.golf.golf.db;

import javax.persistence.*;

/**
 * 比赛计算净杆球洞号表（每个比赛创建时随机生成，生成后不可修改，是固定的）
 */
@Entity
@Table(name = "match_score_net_hole", catalog = "miniapp_golf")
public class MatchScoreNetHole {

	/**
	 * 主键
	 */
    private Long msntId;

	/**
	 * 比赛id
	 */
    private Long msntMatchId;

	/**
	 * 前半场球洞名称A or B or C 之类
	 */
    private String msntHoleBeforeName;

	/**
	 * 前半随机场球洞号1
	 */
    private Integer msntHoleBeforeNum1;

	/**
	 * 前半随机场球洞号2
	 */
    private Integer msntHoleBeforeNum2;

	/**
	 * 前半随机场球洞号3
	 */
    private Integer msntHoleBeforeNum3;

	/**
	 * 后半场球洞名称A or B or C 之类
	 */
    private String msntHoleAfterName;

	/**
	 * 后半随机场球洞号1
	 */
    private Integer msntHoleAfterNum1;

	/**
	 * 后半随机场球洞号2
	 */
    private Integer msntHoleAfterNum2;

	/**
	 * 后半随机场球洞号3
	 */
    private Integer msntHoleAfterNum3;

	/**
	 * 创建人id
	 */
    private Long msnrCreateUserId;

	/**
	 * null
	 */
    private String msCreateUserName;

	/**
	 * 创建时间
	 */
    private Long msCreateTime;


    @Id
    @GeneratedValue()
    @Column(name = "msnt_id")
    public Long getMsntId() {
        return msntId;
    }

    public void setMsntId(Long msntId) {
        this.msntId = msntId;
    }


    @Column(name = "msnt_match_id")
    public Long getMsntMatchId() {
        return msntMatchId;
    }

    public void setMsntMatchId(Long msntMatchId) {
        this.msntMatchId = msntMatchId;
    }


    @Column(name = "msnt_hole_before_name")
    public String getMsntHoleBeforeName() {
        return msntHoleBeforeName;
    }

    public void setMsntHoleBeforeName(String msntHoleBeforeName) {
        this.msntHoleBeforeName = msntHoleBeforeName;
    }


    @Column(name = "msnt_hole_before_num_1")
    public Integer getMsntHoleBeforeNum1() {
        return msntHoleBeforeNum1;
    }

    public void setMsntHoleBeforeNum1(Integer msntHoleBeforeNum1) {
        this.msntHoleBeforeNum1 = msntHoleBeforeNum1;
    }


    @Column(name = "msnt_hole_before_num_2")
    public Integer getMsntHoleBeforeNum2() {
        return msntHoleBeforeNum2;
    }

    public void setMsntHoleBeforeNum2(Integer msntHoleBeforeNum2) {
        this.msntHoleBeforeNum2 = msntHoleBeforeNum2;
    }


    @Column(name = "msnt_hole_before_num_3")
    public Integer getMsntHoleBeforeNum3() {
        return msntHoleBeforeNum3;
    }

    public void setMsntHoleBeforeNum3(Integer msntHoleBeforeNum3) {
        this.msntHoleBeforeNum3 = msntHoleBeforeNum3;
    }


    @Column(name = "msnt_hole_after_name")
    public String getMsntHoleAfterName() {
        return msntHoleAfterName;
    }

    public void setMsntHoleAfterName(String msntHoleAfterName) {
        this.msntHoleAfterName = msntHoleAfterName;
    }


    @Column(name = "msnt_hole_after_num_1")
    public Integer getMsntHoleAfterNum1() {
        return msntHoleAfterNum1;
    }

    public void setMsntHoleAfterNum1(Integer msntHoleAfterNum1) {
        this.msntHoleAfterNum1 = msntHoleAfterNum1;
    }


    @Column(name = "msnt_hole_after_num_2")
    public Integer getMsntHoleAfterNum2() {
        return msntHoleAfterNum2;
    }

    public void setMsntHoleAfterNum2(Integer msntHoleAfterNum2) {
        this.msntHoleAfterNum2 = msntHoleAfterNum2;
    }


    @Column(name = "msnt_hole_after_num_3")
    public Integer getMsntHoleAfterNum3() {
        return msntHoleAfterNum3;
    }

    public void setMsntHoleAfterNum3(Integer msntHoleAfterNum3) {
        this.msntHoleAfterNum3 = msntHoleAfterNum3;
    }


    @Column(name = "msnr_create_user_id")
    public Long getMsnrCreateUserId() {
        return msnrCreateUserId;
    }

    public void setMsnrCreateUserId(Long msnrCreateUserId) {
        this.msnrCreateUserId = msnrCreateUserId;
    }


    @Column(name = "ms_create_user_name")
    public String getMsCreateUserName() {
        return msCreateUserName;
    }

    public void setMsCreateUserName(String msCreateUserName) {
        this.msCreateUserName = msCreateUserName;
    }


    @Column(name = "ms_create_time")
    public Long getMsCreateTime() {
        return msCreateTime;
    }

    public void setMsCreateTime(Long msCreateTime) {
        this.msCreateTime = msCreateTime;
    }

}
