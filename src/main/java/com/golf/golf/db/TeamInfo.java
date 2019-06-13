package com.golf.golf.db;

import com.golf.common.util.TimeUtil;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "team_info")
public class TeamInfo implements java.io.Serializable {

	// Fields

	private Long tiId;
	private String tiLogo;
	private String tiName;
	private String tiAbbrev;
	private String tiSignature;
	private String tiDigest;
	private String tiAddress;
	private Integer tiJoinOpenType;
	private Integer tiInfoOpenType;
	private Integer tiUserInfoType;
	private Integer tiMatchResultAuditType;
	private Integer tiIsValid;
	private Long tiCreateTime;
	private Long tiCreateUserId;
	private String tiCreateUserName;
	private Long tiUpdateTime;
	private Long tiUpdateUserId;
	private String tiUpdateUserName;

	// Constructors

	/** default constructor */
	public TeamInfo() {
	}

	/** full constructor */
	public TeamInfo(String tiLogo, String tiName, String tiAbbrev, String tiSignature,
					String tiDigest, String tiAddress,
					Integer tiJoinOpenType, Integer tiInfoOpenType, Integer tiUserInfoType,
					Integer tiMatchResultAuditType, Integer tiIsValid,Long tiCreateTime,
					Long tiCreateUserId, String tiCreateUserName, Long tiUpdateTime,
					Long tiUpdateUserId, String tiUpdateUserName) {
		this.tiLogo = tiLogo;
		this.tiName = tiName;
		this.tiAbbrev = tiAbbrev;
		this.tiSignature = tiSignature;
		this.tiDigest = tiDigest;
		this.tiAddress = tiAddress;
		this.tiJoinOpenType = tiJoinOpenType;
		this.tiInfoOpenType = tiInfoOpenType;
		this.tiUserInfoType = tiUserInfoType;
		this.tiMatchResultAuditType = tiMatchResultAuditType;
		this.tiIsValid = tiIsValid;
		this.tiCreateTime = tiCreateTime;
		this.tiCreateUserId = tiCreateUserId;
		this.tiCreateUserName = tiCreateUserName;
		this.tiUpdateTime = tiUpdateTime;
		this.tiUpdateUserId = tiUpdateUserId;
		this.tiUpdateUserName = tiUpdateUserName;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ti_id", unique = true, nullable = false)
	public Long getTiId() {
		return this.tiId;
	}

	public void setTiId(Long tiId) {
		this.tiId = tiId;
	}

	@Column(name = "ti_logo")
	public String getTiLogo() {
		return this.tiLogo;
	}

	public void setTiLogo(String tiLogo) {
		this.tiLogo = tiLogo;
	}

	@Column(name = "ti_name")
	public String getTiName() {
		return this.tiName;
	}

	public void setTiName(String tiName) {
		this.tiName = tiName;
	}

	@Column(name = "ti_abbrev")
	public String getTiAbbrev() {
		return tiAbbrev;
	}

	public void setTiAbbrev(String tiAbbrev) {
		this.tiAbbrev = tiAbbrev;
	}

	@Column(name = "ti_signature", length = 512)
	public String getTiSignature() {
		return this.tiSignature;
	}

	public void setTiSignature(String tiSignature) {
		this.tiSignature = tiSignature;
	}

	@Column(name = "ti_digest", length = 512)
	public String getTiDigest() {
		return this.tiDigest;
	}

	public void setTiDigest(String tiDigest) {
		this.tiDigest = tiDigest;
	}

	@Column(name = "ti_address")
	public String getTiAddress() {
		return this.tiAddress;
	}

	public void setTiAddress(String tiAddress) {
		this.tiAddress = tiAddress;
	}

	@Column(name = "ti_join_open_type")
	public Integer getTiJoinOpenType() {
		return this.tiJoinOpenType;
	}

	public void setTiJoinOpenType(Integer tiJoinOpenType) {
		this.tiJoinOpenType = tiJoinOpenType;
	}

	@Column(name = "ti_info_open_type")
	public Integer getTiInfoOpenType() {
		return this.tiInfoOpenType;
	}

	public void setTiInfoOpenType(Integer tiInfoOpenType) {
		this.tiInfoOpenType = tiInfoOpenType;
	}

	@Column(name = "ti_user_info_type")
	public Integer getTiUserInfoType() {
		return tiUserInfoType;
	}

	public void setTiUserInfoType(Integer tiUserInfoType) {
		this.tiUserInfoType = tiUserInfoType;
	}

	@Column(name = "ti_match_result_audit_type")
	public Integer getTiMatchResultAuditType() {
		return this.tiMatchResultAuditType;
	}

	public void setTiMatchResultAuditType(Integer tiMatchResultAuditType) {
		this.tiMatchResultAuditType = tiMatchResultAuditType;
	}

	@Column(name = "ti_is_valid")
	public Integer getTiIsValid() {
		return tiIsValid;
	}

	public void setTiIsValid(Integer tiIsValid) {
		this.tiIsValid = tiIsValid;
	}

	@Column(name = "ti_create_time")
	public Long getTiCreateTime() {
		return this.tiCreateTime;
	}

	public void setTiCreateTime(Long tiCreateTime) {
		this.tiCreateTime = tiCreateTime;
	}

	@Column(name = "ti_create_user_id")
	public Long getTiCreateUserId() {
		return this.tiCreateUserId;
	}

	public void setTiCreateUserId(Long tiCreateUserId) {
		this.tiCreateUserId = tiCreateUserId;
	}

	@Column(name = "ti_create_user_name", length = 128)
	public String getTiCreateUserName() {
		return this.tiCreateUserName;
	}

	public void setTiCreateUserName(String tiCreateUserName) {
		this.tiCreateUserName = tiCreateUserName;
	}

	@Column(name = "ti_update_time")
	public Long getTiUpdateTime() {
		return this.tiUpdateTime;
	}

	public void setTiUpdateTime(Long tiUpdateTime) {
		this.tiUpdateTime = tiUpdateTime;
	}

	@Column(name = "ti_update_user_id")
	public Long getTiUpdateUserId() {
		return this.tiUpdateUserId;
	}

	public void setTiUpdateUserId(Long tiUpdateUserId) {
		this.tiUpdateUserId = tiUpdateUserId;
	}

	@Column(name = "ti_update_user_name", length = 128)
	public String getTiUpdateUserName() {
		return this.tiUpdateUserName;
	}

	public void setTiUpdateUserName(String tiUpdateUserName) {
		this.tiUpdateUserName = tiUpdateUserName;
	}

	private String createTimeStr;
	@Transient
	public String getCreateTimeStr() {
		createTimeStr = TimeUtil.longToString(this.getTiCreateTime(),TimeUtil.FORMAT_DATE);
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}


	//创建球队——滑动删除时需要的
	private Integer shows;
	@Transient
	public Integer getShows() {
		return shows;
	}

	public void setShows(Integer shows) {
		this.shows = shows;
	}


    private String tiCreateTimeStr;
    @Transient
    public String getTiCreateTimeStr() {
        return tiCreateTimeStr;
    }

    public void setTiCreateTimeStr(String tiCreateTimeStr) {
        this.tiCreateTimeStr = tiCreateTimeStr;
    }
}
