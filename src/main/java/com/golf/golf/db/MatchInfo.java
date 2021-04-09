package com.golf.golf.db;

import com.golf.common.util.TimeUtil;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "match_info")
public class MatchInfo implements java.io.Serializable {

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
    private Integer miMatchFormat3;
	private String miJoinTeamIds;
	private String miReportScoreTeamId;
    private String miChildMatchIds;
    private String miFatherMatchIds;
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
					 Integer miMatchFormat1, Integer miMatchFormat2,Integer miMatchFormat3, String miJoinTeamIds,
					 String miReportScoreTeamId, String miChildMatchIds, String  miFatherMatchIds, Integer miHit, Long miApplyEndTime, Integer miIsEnd, Integer miIsValid,
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
        this.miMatchFormat3 = miMatchFormat3;
		this.miJoinTeamIds = miJoinTeamIds;
		this.miReportScoreTeamId = miReportScoreTeamId;
        this.miChildMatchIds = miChildMatchIds;
        this.miFatherMatchIds = miFatherMatchIds;
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

    @Column(name = "mi_match_format_3")
    public Integer getMiMatchFormat3() {
        return miMatchFormat3;
    }

    public void setMiMatchFormat3(Integer miMatchFormat3) {
        this.miMatchFormat3 = miMatchFormat3;
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

    @Column(name = "mi_child_match_ids")
    public String getMiChildMatchIds() {
        return this.miChildMatchIds;
    }

    public void setMiChildMatchIds(String miChildMatchIds) {
        this.miChildMatchIds = miChildMatchIds;
    }

    @Column(name = "mi_father_match_ids")
    public String getMiFatherMatchIds() {
        return this.miFatherMatchIds;
    }

    public void setMiFatherMatchIds(String miFatherMatchIds) {
        this.miFatherMatchIds = miFatherMatchIds;
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
	private String updateTimeStr;
	@Transient
	public String getCreateTimeStr() {
		createTimeStr = TimeUtil.longToString(this.getMiCreateTime(),TimeUtil.FORMAT_DATETIME_HH_MM);
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}
	public void setCreateTimeStr(Long createTime) {
		this.createTimeStr = TimeUtil.longToString(createTime,TimeUtil.FORMAT_DATETIME_HH_MM);
	}

	@Transient
	public String getUpdateTimeStr() {
		updateTimeStr = TimeUtil.longToString(this.getMiUpdateTime(),TimeUtil.FORMAT_DATETIME_HH_MM);
		return updateTimeStr;
	}

	public void setUpdateTimeStr(String updateTimeStr) {
		this.updateTimeStr = updateTimeStr;
	}

	private String stateStr;
	private Long isCaptain;
	@Transient
	public String getStateStr() {
		if(this.getMiIsEnd() == 0){
			stateStr = "报名中";
		}else if(this.getMiIsEnd() == 1){
			stateStr = "进行中";
		}else if(this.getMiIsEnd() == 2){
			stateStr = "已结束";
		}
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


	private String watchTypeStr;
	private String joinTypeStr;
	private String matchTypeStr;
	@Transient
	public String getWatchTypeStr() {
		if(this.getMiType() == 0 || this.getMiMatchOpenType() == null){
			return "不公开";
		}
//		观战范围：（1、公开 球友均可见；2、队内公开：参赛者的队友可见；3、封闭：参赛队员可见）
		if(this.getMiMatchOpenType() == 1){
			watchTypeStr = "公开";
		}else if(this.getMiMatchOpenType() == 2){
			watchTypeStr = "队内公开";
		}else if(this.getMiMatchOpenType() == 3){
			watchTypeStr = "封闭";
		}
		return watchTypeStr;
	}
	public void setWatchTypeStr(String watchTypeStr) {
		this.watchTypeStr = watchTypeStr;
	}

	@Transient
	public String getJoinTypeStr() {
//		参赛范围(1、公开 球友均可报名；2、队内：某几个球队队员可报名；3:不公开)
		if(this.getMiType() == 0 || this.getMiJoinOpenType() == null){
			return "不公开";
		}
		if(this.getMiJoinOpenType() == 1){
			joinTypeStr = "公开";
		}else if(this.getMiJoinOpenType() == 2){
			joinTypeStr = "队内公开";
		}else if(this.getMiJoinOpenType() == 3){
			joinTypeStr = "不公开";
		}
		return joinTypeStr;
	}
	public void setJoinTypeStr(String joinTypeStr) {
		this.joinTypeStr = joinTypeStr;
	}

	@Transient
	public String getMatchTypeStr() {
		if(this.getMiType() == 0){
			return "个人 | 比杆";
		}
//		赛制2( 0:个人 、1:双人)
		if(this.getMiMatchFormat2() == 0){
			matchTypeStr = "个人";
		}else{
			matchTypeStr = "双人";
		}
//		赛制1( 0:比杆 、1:比洞)
		if(this.getMiMatchFormat1() == 0){
			matchTypeStr += " | 比杆";
		}else{
			matchTypeStr += " | 比洞";
		}
		return matchTypeStr;
	}
	public void setMatchTypeStr(String matchTypeStr) {
		this.matchTypeStr = matchTypeStr;
	}



	//创建比赛——滑动删除时需要的
	private Integer shows;
	@Transient
	public Integer getShows() {
		return shows;
	}

	public void setShows(Integer shows) {
		this.shows = shows;
	}

	//报名——参赛人数
	private Integer userCount;
	@Transient

	public Integer getUserCount() {
		return userCount;
	}

	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}
}
