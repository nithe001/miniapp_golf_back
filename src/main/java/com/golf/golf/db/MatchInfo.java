package com.golf.golf.db;

import com.golf.common.util.TimeUtil;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "match_info")
public class MatchInfo {

	// Fields

	private Long miId;
	private Integer miType;
	private Integer miPeopleNum;
	private String miTitle;
	private String miLogo;
	private Long miParkId;
	private String miParkName;
	private String miZoneBeforeNine;
	private String miZoneAfterNine;
	private String miDigest;
	private String miMatchTime;
	private String miContent;
	private Integer miMatchOpenType;
	private Integer miJoinOpenType;
	private Integer miMatchFormat1;
	private Integer miMatchFormat2;
	private String miJoinTeamIds;
	private String miReportScoreTeamId;
	private Integer miHit;
	private Long miApplyEndTime;
	private Integer miIsEnd;
	private Integer miIsValid;
	private String miCreateUserName;
	private Long miCreateUserId;
	private Long miCreateTime;
	private String miUpdateUserName;
	private Long miUpdateUserId;
	private Long miUpdateTime;

	// Constructors

	/** default constructor */
	public MatchInfo() {
	}

	/** full constructor */
	public MatchInfo(Integer miType, Integer miPeopleNum, String miTitle, String miLogo,
					 Long miParkId, String miParkName, String miZoneBeforeNine,
					 String miZoneAfterNine, String miDigest, String miMatchTime,
					 String miContent, Integer miMatchOpenType, Integer miJoinOpenType,
					 Integer miMatchFormat1, Integer miMatchFormat2, String miJoinTeamIds,
					 String miReportScoreTeamId, Integer miHit, Long miApplyEndTime, Integer miIsEnd, Integer miIsValid,
					 String miCreateUserName, Long miCreateUserId, Long miCreateTime,
					 String miUpdateUserName, Long miUpdateUserId, Long miUpdateTime) {
		this.miType = miType;
		this.miPeopleNum = miPeopleNum;
		this.miTitle = miTitle;
		this.miLogo = miLogo;
		this.miParkId = miParkId;
		this.miParkName = miParkName;
		this.miZoneBeforeNine = miZoneBeforeNine;
		this.miZoneAfterNine = miZoneAfterNine;
		this.miDigest = miDigest;
		this.miMatchTime = miMatchTime;
		this.miContent = miContent;
		this.miMatchOpenType = miMatchOpenType;
		this.miJoinOpenType = miJoinOpenType;
		this.miMatchFormat1 = miMatchFormat1;
		this.miMatchFormat2 = miMatchFormat2;
		this.miJoinTeamIds = miJoinTeamIds;
		this.miReportScoreTeamId = miReportScoreTeamId;
		this.miHit = miHit;
		this.miApplyEndTime = miApplyEndTime;
		this.miIsEnd = miIsEnd;
		this.miIsValid = miIsValid;
		this.miCreateUserName = miCreateUserName;
		this.miCreateUserId = miCreateUserId;
		this.miCreateTime = miCreateTime;
		this.miUpdateUserName = miUpdateUserName;
		this.miUpdateUserId = miUpdateUserId;
		this.miUpdateTime = miUpdateTime;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "mi_id", unique = true, nullable = false)
	public Long getMiId() {
		return this.miId;
	}

	public void setMiId(Long miId) {
		this.miId = miId;
	}

	@Column(name = "mi_type")
	public Integer getMiType() {
		return this.miType;
	}

	public void setMiType(Integer miType) {
		this.miType = miType;
	}

	@Column(name = "mi_people_num")
	public Integer getMiPeopleNum() {
		return this.miPeopleNum;
	}

	public void setMiPeopleNum(Integer miPeopleNum) {
		this.miPeopleNum = miPeopleNum;
	}

	@Column(name = "mi_title", length = 128)
	public String getMiTitle() {
		return this.miTitle;
	}

	public void setMiTitle(String miTitle) {
		this.miTitle = miTitle;
	}

	@Column(name = "mi_logo", length = 255)
	public String getMiLogo() {
		return miLogo;
	}

	public void setMiLogo(String miLogo) {
		this.miLogo = miLogo;
	}

	@Column(name = "mi_park_id")
	public Long getMiParkId() {
		return this.miParkId;
	}

	public void setMiParkId(Long miParkId) {
		this.miParkId = miParkId;
	}

	@Column(name = "mi_park_name", length = 128)
	public String getMiParkName() {
		return this.miParkName;
	}

	public void setMiParkName(String miParkName) {
		this.miParkName = miParkName;
	}

	@Column(name = "mi_zone_before_nine")
	public String getMiZoneBeforeNine() {
		return this.miZoneBeforeNine;
	}

	public void setMiZoneBeforeNine(String miZoneBeforeNine) {
		this.miZoneBeforeNine = miZoneBeforeNine;
	}

	@Column(name = "mi_zone_after_nine")
	public String getMiZoneAfterNine() {
		return this.miZoneAfterNine;
	}

	public void setMiZoneAfterNine(String miZoneAfterNine) {
		this.miZoneAfterNine = miZoneAfterNine;
	}

	@Column(name = "mi_digest", length = 512)
	public String getMiDigest() {
		return this.miDigest;
	}

	public void setMiDigest(String miDigest) {
		this.miDigest = miDigest;
	}

	@Column(name = "mi_match_time", length = 128)
	public String getMiMatchTime() {
		return this.miMatchTime;
	}

	public void setMiMatchTime(String miMatchTime) {
		this.miMatchTime = miMatchTime;
	}

	@Column(name = "mi_content", length = 65535)
	public String getMiContent() {
		return this.miContent;
	}

	public void setMiContent(String miContent) {
		this.miContent = miContent;
	}

	@Column(name = "mi_match_open_type")
	public Integer getMiMatchOpenType() {
		return this.miMatchOpenType;
	}

	public void setMiMatchOpenType(Integer miMatchOpenType) {
		this.miMatchOpenType = miMatchOpenType;
	}

	@Column(name = "mi_join_open_type")
	public Integer getMiJoinOpenType() {
		return this.miJoinOpenType;
	}

	public void setMiJoinOpenType(Integer miJoinOpenType) {
		this.miJoinOpenType = miJoinOpenType;
	}

	@Column(name = "mi_match_format_1")
	public Integer getMiMatchFormat1() {
		return miMatchFormat1;
	}

	public void setMiMatchFormat1(Integer miMatchFormat1) {
		this.miMatchFormat1 = miMatchFormat1;
	}

	@Column(name = "mi_match_format_2")
	public Integer getMiMatchFormat2() {
		return miMatchFormat2;
	}

	public void setMiMatchFormat2(Integer miMatchFormat2) {
		this.miMatchFormat2 = miMatchFormat2;
	}

	@Column(name = "mi_join_team_ids", length = 255)
	public String getMiJoinTeamIds() {
		return miJoinTeamIds;
	}

	public void setMiJoinTeamIds(String miJoinTeamIds) {
		this.miJoinTeamIds = miJoinTeamIds;
	}

	@Column(name = "mi_report_score_team_id")
	public String getMiReportScoreTeamId() {
		return this.miReportScoreTeamId;
	}

	public void setMiReportScoreTeamId(String miReportScoreTeamId) {
		this.miReportScoreTeamId = miReportScoreTeamId;
	}

	@Column(name = "mi_hit")
	public Integer getMiHit() {
		return this.miHit;
	}

	public void setMiHit(Integer miHit) {
		this.miHit = miHit;
	}

	@Column(name = "mi_apply_end_time")
	public Long getMiApplyEndTime() {
		return this.miApplyEndTime;
	}

	public void setMiApplyEndTime(Long miApplyEndTime) {
		this.miApplyEndTime = miApplyEndTime;
	}


	@Column(name = "mi_is_end")
	public Integer getMiIsEnd() {
		return miIsEnd;
	}

	public void setMiIsEnd(Integer miIsEnd) {
		this.miIsEnd = miIsEnd;
	}

	@Column(name = "mi_is_valid")
	public Integer getMiIsValid() {
		return miIsValid;
	}

	public void setMiIsValid(Integer miIsValid) {
		this.miIsValid = miIsValid;
	}

	@Column(name = "mi_create_user_name", length = 128)
	public String getMiCreateUserName() {
		return this.miCreateUserName;
	}

	public void setMiCreateUserName(String miCreateUserName) {
		this.miCreateUserName = miCreateUserName;
	}

	@Column(name = "mi_create_user_id")
	public Long getMiCreateUserId() {
		return this.miCreateUserId;
	}

	public void setMiCreateUserId(Long miCreateUserId) {
		this.miCreateUserId = miCreateUserId;
	}

	@Column(name = "mi_create_time")
	public Long getMiCreateTime() {
		return this.miCreateTime;
	}

	public void setMiCreateTime(Long miCreateTime) {
		this.miCreateTime = miCreateTime;
	}

	@Column(name = "mi_update_user_name", length = 128)
	public String getMiUpdateUserName() {
		return this.miUpdateUserName;
	}

	public void setMiUpdateUserName(String miUpdateUserName) {
		this.miUpdateUserName = miUpdateUserName;
	}

	@Column(name = "mi_update_user_id")
	public Long getMiUpdateUserId() {
		return this.miUpdateUserId;
	}

	public void setMiUpdateUserId(Long miUpdateUserId) {
		this.miUpdateUserId = miUpdateUserId;
	}

	@Column(name = "mi_update_time")
	public Long getMiUpdateTime() {
		return this.miUpdateTime;
	}

	public void setMiUpdateTime(Long miUpdateTime) {
		this.miUpdateTime = miUpdateTime;
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
	private Long isCaptain;
	@Transient
	public String getStateStr() {
		return stateStr;
	}
	public void setStateStr(String stateStr) {
		this.stateStr = stateStr;
	}


	@Transient
	public Long getIsCaptain() {
		return isCaptain;
	}

	public void setIsCaptain(Long isCaptain) {
		this.isCaptain = isCaptain;
	}
}
