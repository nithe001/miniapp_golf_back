package com.golf.golf.db;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user_info")
public class UserInfo {

	// Fields

	private Long uiId;
	private String uiOpenId;
	private Integer uiType;
	private String uiHeadimg;
	private String uiPersonalizedSignature;
	private String uiLongitude;
	private String uiLatitude;
	private String uiRealName;
	private String uiNickName;
	private String uiSex;
	private Integer uiAge;
	private String uiTelNo;
	private String uiEmail;
	private String uiCraduateSchool;
	private String uiCraduateDepartment;
	private String uiCraduateTime;
	private String uiMajor;
	private String uiStudentId;
	private String uiWorkUnit;
	private String uiPost;
	private String uiAddress;
	private String uiHomeCourt;
	private Integer uiIsValid;
	private Long uiCreateTime;
	private String uiCreateUserName;
	private Long uiCreateUserId;
	private Long uiUpdateTime;
	private String uiUpdateUserName;
	private Long uiUpdateUserId;

	// Constructors

	/** default constructor */
	public UserInfo() {
	}

	/** full constructor */
	public UserInfo(String uiOpenId, Integer uiType, String uiHeadimg,
					String uiPersonalizedSignature, String uiLongitude,
					String uiLatitude, String uiRealName, String uiNickName, String uiSex, Integer uiAge,
					String uiTelNo, String uiEmail, String uiCraduateSchool,
					String uiCraduateDepartment, String uiCraduateTime, String uiMajor,String uiStudentId,
					String uiWorkUnit, String uiPost, String uiAddress, String uiHomeCourt, Integer uiIsValid,
					Long uiCreateTime, String uiCreateUserName, Long uiCreateUserId,
					Long uiUpdateTime, String uiUpdateUserName, Long uiUpdateUserId) {
		this.uiOpenId = uiOpenId;
		this.uiType = uiType;
		this.uiHeadimg = uiHeadimg;
		this.uiPersonalizedSignature = uiPersonalizedSignature;
		this.uiLongitude = uiLongitude;
		this.uiLatitude = uiLatitude;
		this.uiRealName = uiRealName;
		this.uiNickName = uiNickName;
		this.uiSex = uiSex;
		this.uiAge = uiAge;
		this.uiTelNo = uiTelNo;
		this.uiEmail = uiEmail;
		this.uiCraduateSchool = uiCraduateSchool;
		this.uiCraduateDepartment = uiCraduateDepartment;
		this.uiCraduateTime = uiCraduateTime;
		this.uiMajor = uiMajor;
		this.uiStudentId = uiStudentId;
		this.uiWorkUnit = uiWorkUnit;
		this.uiPost = uiPost;
		this.uiAddress = uiAddress;
		this.uiHomeCourt = uiHomeCourt;
		this.uiIsValid = uiIsValid;
		this.uiCreateTime = uiCreateTime;
		this.uiCreateUserName = uiCreateUserName;
		this.uiCreateUserId = uiCreateUserId;
		this.uiUpdateTime = uiUpdateTime;
		this.uiUpdateUserName = uiUpdateUserName;
		this.uiUpdateUserId = uiUpdateUserId;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ui_id", unique = true, nullable = false)
	public Long getUiId() {
		return this.uiId;
	}

	public void setUiId(Long uiId) {
		this.uiId = uiId;
	}

	@Column(name = "ui_open_id", length = 128)
	public String getUiOpenId() {
		return this.uiOpenId;
	}

	public void setUiOpenId(String uiOpenId) {
		this.uiOpenId = uiOpenId;
	}

	@Column(name = "ui_type")
	public Integer getUiType() {
		return this.uiType;
	}

	public void setUiType(Integer uiType) {
		this.uiType = uiType;
	}

	@Column(name = "ui_headimg")
	public String getUiHeadimg() {
		return this.uiHeadimg;
	}

	public void setUiHeadimg(String uiHeadimg) {
		this.uiHeadimg = uiHeadimg;
	}

	@Column(name = "ui_personalized_signature", length = 512)
	public String getUiPersonalizedSignature() {
		return this.uiPersonalizedSignature;
	}

	public void setUiPersonalizedSignature(String uiPersonalizedSignature) {
		this.uiPersonalizedSignature = uiPersonalizedSignature;
	}

	@Column(name = "ui_longitude", length = 128)
	public String getUiLongitude() {
		return this.uiLongitude;
	}

	public void setUiLongitude(String uiLongitude) {
		this.uiLongitude = uiLongitude;
	}

	@Column(name = "ui_latitude", length = 128)
	public String getUiLatitude() {
		return this.uiLatitude;
	}

	public void setUiLatitude(String uiLatitude) {
		this.uiLatitude = uiLatitude;
	}

	@Column(name = "ui_real_name", length = 128)
	public String getUiRealName() {
		return this.uiRealName;
	}

	public void setUiRealName(String uiRealName) {
		this.uiRealName = uiRealName;
	}

	@Column(name = "ui_nick_name", length = 128)
	public String getUiNickName() {
		return uiNickName;
	}

	public void setUiNickName(String uiNickName) {
		this.uiNickName = uiNickName;
	}

	@Column(name = "ui_sex", length = 64)
	public String getUiSex() {
		return uiSex;
	}

	public void setUiSex(String uiSex) {
		this.uiSex = uiSex;
	}

	@Column(name = "ui_age")
	public Integer getUiAge() {
		return this.uiAge;
	}

	public void setUiAge(Integer uiAge) {
		this.uiAge = uiAge;
	}

	@Column(name = "ui_tel_no", length = 128)
	public String getUiTelNo() {
		return this.uiTelNo;
	}

	public void setUiTelNo(String uiTelNo) {
		this.uiTelNo = uiTelNo;
	}

	@Column(name = "ui_email", length = 128)
	public String getUiEmail() {
		return this.uiEmail;
	}

	public void setUiEmail(String uiEmail) {
		this.uiEmail = uiEmail;
	}

	@Column(name = "ui_craduate_school")
	public String getUiCraduateSchool() {
		return this.uiCraduateSchool;
	}

	public void setUiCraduateSchool(String uiCraduateSchool) {
		this.uiCraduateSchool = uiCraduateSchool;
	}

	@Column(name = "ui_craduate_department")
	public String getUiCraduateDepartment() {
		return this.uiCraduateDepartment;
	}

	public void setUiCraduateDepartment(String uiCraduateDepartment) {
		this.uiCraduateDepartment = uiCraduateDepartment;
	}

	@Column(name = "ui_craduate_time", length = 128)
	public String getUiCraduateTime() {
		return this.uiCraduateTime;
	}

	public void setUiCraduateTime(String uiCraduateTime) {
		this.uiCraduateTime = uiCraduateTime;
	}

	@Column(name = "ui_major")
	public String getUiMajor() {
		return this.uiMajor;
	}

	public void setUiMajor(String uiMajor) {
		this.uiMajor = uiMajor;
	}

	@Column(name = "ui_work_unit")
	public String getUiWorkUnit() {
		return this.uiWorkUnit;
	}

	public void setUiWorkUnit(String uiWorkUnit) {
		this.uiWorkUnit = uiWorkUnit;
	}

	@Column(name = "ui_address")
	public String getUiAddress() {
		return this.uiAddress;
	}

	public void setUiAddress(String uiAddress) {
		this.uiAddress = uiAddress;
	}

	@Column(name = "ui_is_valid")
	public Integer getUiIsValid() {
		return this.uiIsValid;
	}

	public void setUiIsValid(Integer uiIsValid) {
		this.uiIsValid = uiIsValid;
	}

	@Column(name = "ui_create_time")
	public Long getUiCreateTime() {
		return this.uiCreateTime;
	}

	public void setUiCreateTime(Long uiCreateTime) {
		this.uiCreateTime = uiCreateTime;
	}

	@Column(name = "ui_create_user_name", length = 128)
	public String getUiCreateUserName() {
		return this.uiCreateUserName;
	}

	public void setUiCreateUserName(String uiCreateUserName) {
		this.uiCreateUserName = uiCreateUserName;
	}

	@Column(name = "ui_create_user_id")
	public Long getUiCreateUserId() {
		return this.uiCreateUserId;
	}

	public void setUiCreateUserId(Long uiCreateUserId) {
		this.uiCreateUserId = uiCreateUserId;
	}

	@Column(name = "ui_update_time")
	public Long getUiUpdateTime() {
		return this.uiUpdateTime;
	}

	public void setUiUpdateTime(Long uiUpdateTime) {
		this.uiUpdateTime = uiUpdateTime;
	}

	@Column(name = "ui_update_user_name", length = 128)
	public String getUiUpdateUserName() {
		return this.uiUpdateUserName;
	}

	public void setUiUpdateUserName(String uiUpdateUserName) {
		this.uiUpdateUserName = uiUpdateUserName;
	}

	@Column(name = "ui_update_user_id")
	public Long getUiUpdateUserId() {
		return this.uiUpdateUserId;
	}

	public void setUiUpdateUserId(Long uiUpdateUserId) {
		this.uiUpdateUserId = uiUpdateUserId;
	}

	@Column(name = "ui_student_id", length = 255)
	public String getUiStudentId() {
		return uiStudentId;
	}

	public void setUiStudentId(String uiStudentId) {
		this.uiStudentId = uiStudentId;
	}

	@Column(name = "ui_post", length = 255)
	public String getUiPost() {
		return uiPost;
	}

	public void setUiPost(String uiPost) {
		this.uiPost = uiPost;
	}

	@Column(name = "ui_home_court", length = 255)
	public String getUiHomeCourt() {
		return uiHomeCourt;
	}

	public void setUiHomeCourt(String uiHomeCourt) {
		this.uiHomeCourt = uiHomeCourt;
	}
}
