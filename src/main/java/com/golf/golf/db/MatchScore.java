package com.golf.golf.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * MatchScore entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "match_score", catalog = "miniapp_golf")
public class MatchScore implements java.io.Serializable {

	// Fields

	private Long msId;
	private Long msTeamId;
	private Long msMatchId;
	private String msMatchTitle;
	private Integer msMatchType;
	private Long msGroupId;
	private String msGroupName;
	private Long msUserId;
	private String msUserName;
	private Integer msType;
	private Integer msScore;
	private Integer msBeforeAfter;
	private String msHoleName;
	private Integer msHoleNum;
	private Integer msHoleStandardRod;
	private String msIsUp;
	private Integer msRodNum;
	private Integer msRodCha;
	private Integer msPushRodNum;
	private Integer msIsPar;
	private Integer msIsBird;
	private Integer msIsEagle;
	private Integer msIsOn;
	private Integer msIsBomb;
	private Integer msIsBogey;
    private Integer msIsTeamSubmit;
	private Long msCreateUserId;
	private String msCreateUserName;
	private Long msCreateTime;
	private Long msUpdateUserId;
	private String msUpdateUserName;
	private Long msUpdateTime;

	// Constructors

	/** default constructor */
	public MatchScore() {
	}

	/** full constructor */
	public MatchScore(Long msTeamId, Long msMatchId, String msMatchTitle, Integer msMatchType,
					  Long msGroupId, String msGroupName, Long msUserId,
					  String msUserName, Integer msType, Integer msScore, Integer msBeforeAfter, String msHoleName,
					  Integer msHoleNum, Integer msHoleStandardRod, Integer msRodCha, String msIsUp, Integer msRodNum,
					  Integer msPushRodNum, Integer msIsPar, Integer msIsBird,Integer msIsEagle,Integer msIsOn, Integer msIsBomb,
					  Integer msIsBogey, Integer msIsTeamSubmit, Long msCreateUserId, String msCreateUserName, Long msCreateTime,
					  Long msUpdateUserId, String msUpdateUserName, Long msUpdateTime) {
		this.msTeamId = msTeamId;
		this.msMatchId = msMatchId;
		this.msMatchTitle = msMatchTitle;
		this.msMatchType = msMatchType;
		this.msGroupId = msGroupId;
		this.msGroupName = msGroupName;
		this.msUserId = msUserId;
		this.msUserName = msUserName;
		this.msType = msType;
		this.msScore = msScore;
		this.msBeforeAfter = msBeforeAfter;
		this.msHoleName = msHoleName;
		this.msHoleNum = msHoleNum;
		this.msHoleStandardRod = msHoleStandardRod;
		this.msIsUp = msIsUp;
		this.msRodNum = msRodNum;
		this.msRodCha = msRodCha;
		this.msPushRodNum = msPushRodNum;
		this.msIsPar = msIsPar;
		this.msIsBird = msIsBird;
		this.msIsEagle = msIsEagle;
		this.msIsOn = msIsOn;
		this.msIsBomb = msIsBomb;
		this.msIsBogey = msIsBogey;
		this.msIsTeamSubmit = msIsTeamSubmit;
		this.msCreateUserId = msCreateUserId;
		this.msCreateUserName = msCreateUserName;
		this.msCreateTime = msCreateTime;
		this.msUpdateUserId = msUpdateUserId;
		this.msUpdateUserName = msUpdateUserName;
		this.msUpdateTime = msUpdateTime;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ms_id", unique = true, nullable = false)
	public Long getMsId() {
		return this.msId;
	}

	public void setMsId(Long msId) {
		this.msId = msId;
	}

	@Column(name = "ms_team_id")
	public Long getMsTeamId() {
		return this.msTeamId;
	}

	public void setMsTeamId(Long msTeamId) {
		this.msTeamId = msTeamId;
	}

	@Column(name = "ms_match_id")
	public Long getMsMatchId() {
		return this.msMatchId;
	}

	public void setMsMatchId(Long msMatchId) {
		this.msMatchId = msMatchId;
	}

	@Column(name = "ms_match_title")
	public String getMsMatchTitle() {
		return this.msMatchTitle;
	}

	public void setMsMatchTitle(String msMatchTitle) {
		this.msMatchTitle = msMatchTitle;
	}

	@Column(name = "ms_match_type")
	public Integer getMsMatchType() {
		return msMatchType;
	}

	public void setMsMatchType(Integer msMatchType) {
		this.msMatchType = msMatchType;
	}

	@Column(name = "ms_group_id")
	public Long getMsGroupId() {
		return this.msGroupId;
	}

	public void setMsGroupId(Long msGroupId) {
		this.msGroupId = msGroupId;
	}

	@Column(name = "ms_group_name")
	public String getMsGroupName() {
		return this.msGroupName;
	}

	public void setMsGroupName(String msGroupName) {
		this.msGroupName = msGroupName;
	}

	@Column(name = "ms_user_id")
	public Long getMsUserId() {
		return this.msUserId;
	}

	public void setMsUserId(Long msUserId) {
		this.msUserId = msUserId;
	}

	@Column(name = "ms_user_name", length = 128)
	public String getMsUserName() {
		return this.msUserName;
	}

	public void setMsUserName(String msUserName) {
		this.msUserName = msUserName;
	}

	@Column(name = "ms_type")
	public Integer getMsType() {
		return msType;
	}

	public void setMsType(Integer msType) {
		this.msType = msType;
	}

	@Column(name = "ms_score")
	public Integer getMsScore() {
		return this.msScore;
	}

	public void setMsScore(Integer msScore) {
		this.msScore = msScore;
	}

	@Column(name = "ms_before_after")
	public Integer getMsBeforeAfter() {
		return msBeforeAfter;
	}

	public void setMsBeforeAfter(Integer msBeforeAfter) {
		this.msBeforeAfter = msBeforeAfter;
	}

	@Column(name = "ms_hole_name", length = 128)
	public String getMsHoleName() {
		return this.msHoleName;
	}

	public void setMsHoleName(String msHoleName) {
		this.msHoleName = msHoleName;
	}

	@Column(name = "ms_hole_num")
	public Integer getMsHoleNum() {
		return this.msHoleNum;
	}

	public void setMsHoleNum(Integer msHoleNum) {
		this.msHoleNum = msHoleNum;
	}

	@Column(name = "ms_hole_standard_rod")
	public Integer getMsHoleStandardRod() {
		return msHoleStandardRod;
	}

	public void setMsHoleStandardRod(Integer msHoleStandardRod) {
		this.msHoleStandardRod = msHoleStandardRod;
	}

	@Column(name = "ms_is_up",length = 128)
	public String getMsIsUp() {
		return this.msIsUp;
	}

	public void setMsIsUp(String msIsUp) {
		this.msIsUp = msIsUp;
	}

	@Column(name = "ms_rod_num")
	public Integer getMsRodNum() {
		return this.msRodNum;
	}

	public void setMsRodNum(Integer msRodNum) {
		this.msRodNum = msRodNum;
	}

	@Column(name = "ms_rod_cha")
	public Integer getMsRodCha() {
		return msRodCha;
	}

	public void setMsRodCha(Integer msRodCha) {
		this.msRodCha = msRodCha;
	}

	@Column(name = "ms_push_rod_num")
	public Integer getMsPushRodNum() {
		return this.msPushRodNum;
	}

	public void setMsPushRodNum(Integer msPushRodNum) {
		this.msPushRodNum = msPushRodNum;
	}

	@Column(name = "ms_is_par")
	public Integer getMsIsPar() {
		return msIsPar;
	}

	public void setMsIsPar(Integer msIsPar) {
		this.msIsPar = msIsPar;
	}

	@Column(name = "ms_is_bird")
	public Integer getMsIsBird() {
		return msIsBird;
	}

	public void setMsIsBird(Integer msIsBird) {
		this.msIsBird = msIsBird;
	}

	@Column(name = "ms_is_eagle")
	public Integer getMsIsEagle() {
		return msIsEagle;
	}

	public void setMsIsEagle(Integer msIsEagle) {
		this.msIsEagle = msIsEagle;
	}

	@Column(name = "ms_is_on")
	public Integer getMsIsOn() {
		return msIsOn;
	}

	public void setMsIsOn(Integer msIsOn) {
		this.msIsOn = msIsOn;
	}

	@Column(name = "ms_is_bomb")
	public Integer getMsIsBomb() {
		return msIsBomb;
	}

	public void setMsIsBomb(Integer msIsBomb) {
		this.msIsBomb = msIsBomb;
	}

	@Column(name = "ms_is_bogey")
	public Integer getMsIsBogey() {
		return msIsBogey;
	}

	public void setMsIsBogey(Integer msIsBogey) {
		this.msIsBogey = msIsBogey;
	}

	@Column(name = "ms_is_team_submit")
    public Integer getMsIsTeamSubmit() {
        return msIsTeamSubmit;
    }

    public void setMsIsTeamSubmit(Integer msIsTeamSubmit) {
        this.msIsTeamSubmit = msIsTeamSubmit;
    }

    @Column(name = "ms_create_user_id")
	public Long getMsCreateUserId() {
		return this.msCreateUserId;
	}

	public void setMsCreateUserId(Long msCreateUserId) {
		this.msCreateUserId = msCreateUserId;
	}

	@Column(name = "ms_create_time")
	public Long getMsCreateTime() {
		return this.msCreateTime;
	}

	public void setMsCreateTime(Long msCreateTime) {
		this.msCreateTime = msCreateTime;
	}

	@Column(name = "ms_update_user_id")
	public Long getMsUpdateUserId() {
		return this.msUpdateUserId;
	}

	public void setMsUpdateUserId(Long msUpdateUserId) {
		this.msUpdateUserId = msUpdateUserId;
	}

	@Column(name = "ms_update_time")
	public Long getMsUpdateTime() {
		return this.msUpdateTime;
	}

	public void setMsUpdateTime(Long msUpdateTime) {
		this.msUpdateTime = msUpdateTime;
	}

	@Column(name = "ms_create_user_name",length = 128)
	public String getMsCreateUserName() {
		return msCreateUserName;
	}

	public void setMsCreateUserName(String msCreateUserName) {
		this.msCreateUserName = msCreateUserName;
	}

	@Column(name = "ms_update_user_name",length = 128)
	public String getMsUpdateUserName() {
		return msUpdateUserName;
	}

	public void setMsUpdateUserName(String msUpdateUserName) {
		this.msUpdateUserName = msUpdateUserName;
	}
}