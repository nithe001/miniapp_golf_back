package com.golf.golf.db;

import javax.persistence.*;

@Entity
@Table(name = "user_info")
public class UserInfo {
    private Long uiId;
	private String uiOpenId;
    private Integer uiType;
    private String uiHeadimg;
    private String uiPersonalizedSignature;
    private String uiLongitude;
    private String uiLatitude;
    private String uiRealName;
    private Integer uiAge;
    private String uiTelNo;
    private String uiEmail;
    private String uiCraduateSchool;
    private String uiCraduateDepartment;
    private Long uiCraduateTime;
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

    @Id
    @Column(name = "ui_id")
    public Long getUiId() {
        return uiId;
    }

    public void setUiId(Long uiId) {
        this.uiId = uiId;
    }

	@Basic
	@Column(name = "ui_open_id")
	public String getUiOpenId() {
		return uiOpenId;
	}

	public void setUiOpenId(String uiOpenId) {
		this.uiOpenId = uiOpenId;
	}

	@Basic
    @Column(name = "ui_longitude")
    public String getUiLongitude() {
        return uiLongitude;
    }

    public void setUiLongitude(String uiLongitude) {
        this.uiLongitude = uiLongitude;
    }

    @Basic
    @Column(name = "ui_latitude")
    public String getUiLatitude() {
        return uiLatitude;
    }

    public void setUiLatitude(String uiLatitude) {
        this.uiLatitude = uiLatitude;
    }

    @Basic
    @Column(name = "ui_type")
    public Integer getUiType() {
        return uiType;
    }

    public void setUiType(Integer uiType) {
        this.uiType = uiType;
    }

    @Basic
    @Column(name = "ui_personalized_signature")
    public String getUiPersonalizedSignature() {
        return uiPersonalizedSignature;
    }

    public void setUiPersonalizedSignature(String uiPersonalizedSignature) {
        this.uiPersonalizedSignature = uiPersonalizedSignature;
    }

    @Basic
    @Column(name = "ui_headimg")
    public String getUiHeadimg() {
        return uiHeadimg;
    }

    public void setUiHeadimg(String uiHeadimg) {
        this.uiHeadimg = uiHeadimg;
    }

    @Basic
    @Column(name = "ui_real_name")
    public String getUiRealName() {
        return uiRealName;
    }

    public void setUiRealName(String uiRealName) {
        this.uiRealName = uiRealName;
    }

    @Basic
    @Column(name = "ui_age")
    public Integer getUiAge() {
        return uiAge;
    }

    public void setUiAge(Integer uiAge) {
        this.uiAge = uiAge;
    }

    @Basic
    @Column(name = "ui_tel_no")
    public String getUiTelNo() {
        return uiTelNo;
    }

    public void setUiTelNo(String uiTelNo) {
        this.uiTelNo = uiTelNo;
    }

    @Basic
    @Column(name = "ui_email")
    public String getUiEmail() {
        return uiEmail;
    }

    public void setUiEmail(String uiEmail) {
        this.uiEmail = uiEmail;
    }

    @Basic
    @Column(name = "ui_craduate_school")
    public String getUiCraduateSchool() {
        return uiCraduateSchool;
    }

    public void setUiCraduateSchool(String uiCraduateSchool) {
        this.uiCraduateSchool = uiCraduateSchool;
    }

    @Basic
    @Column(name = "ui_craduate_department")
    public String getUiCraduateDepartment() {
        return uiCraduateDepartment;
    }

    public void setUiCraduateDepartment(String uiCraduateDepartment) {
        this.uiCraduateDepartment = uiCraduateDepartment;
    }

    @Basic
    @Column(name = "ui_craduate_time")
    public Long getUiCraduateTime() {
        return uiCraduateTime;
    }

    public void setUiCraduateTime(Long uiCraduateTime) {
        this.uiCraduateTime = uiCraduateTime;
    }

    @Basic
    @Column(name = "ui_major")
    public String getUiMajor() {
        return uiMajor;
    }

    public void setUiMajor(String uiMajor) {
        this.uiMajor = uiMajor;
    }

    @Basic
    @Column(name = "ui_student_id")
    public String getUiStudentId() {
        return uiStudentId;
    }

    public void setUiStudentId(String uiStudentId) {
        this.uiStudentId = uiStudentId;
    }

    @Basic
    @Column(name = "ui_work_unit")
    public String getUiWorkUnit() {
        return uiWorkUnit;
    }

    public void setUiWorkUnit(String uiWorkUnit) {
        this.uiWorkUnit = uiWorkUnit;
    }


    @Basic
    @Column(name = "ui_post")
    public String getUiPost() {
        return uiPost;
    }

    public void setUiPost(String uiPost) {
        this.uiPost = uiPost;
    }

    @Basic
    @Column(name = "ui_address")
    public String getUiAddress() {
        return uiAddress;
    }

    public void setUiAddress(String uiAddress) {
        this.uiAddress = uiAddress;
    }

    @Basic
    @Column(name = "ui_home_court")
    public String getUiHomeCourt() {
        return uiHomeCourt;
    }

    public void setUiHomeCourt(String uiHomeCourt) {
        this.uiHomeCourt = uiHomeCourt;
    }

    @Basic
    @Column(name = "ui_is_valid")
    public Integer getUiIsValid() {
        return uiIsValid;
    }

    public void setUiIsValid(Integer uiIsValid) {
        this.uiIsValid = uiIsValid;
    }

    @Basic
    @Column(name = "ui_create_time")
    public Long getUiCreateTime() {
        return uiCreateTime;
    }

    public void setUiCreateTime(Long uiCreateTime) {
        this.uiCreateTime = uiCreateTime;
    }

    @Basic
    @Column(name = "ui_create_user_name")
    public String getUiCreateUserName() {
        return uiCreateUserName;
    }

    public void setUiCreateUserName(String uiCreateUserName) {
        this.uiCreateUserName = uiCreateUserName;
    }

    @Basic
    @Column(name = "ui_create_user_id")
    public Long getUiCreateUserId() {
        return uiCreateUserId;
    }

    public void setUiCreateUserId(Long uiCreateUserId) {
        this.uiCreateUserId = uiCreateUserId;
    }

    @Basic
    @Column(name = "ui_update_time")
    public Long getUiUpdateTime() {
        return uiUpdateTime;
    }

    public void setUiUpdateTime(Long uiUpdateTime) {
        this.uiUpdateTime = uiUpdateTime;
    }

    @Basic
    @Column(name = "ui_update_user_name")
    public String getUiUpdateUserName() {
        return uiUpdateUserName;
    }

    public void setUiUpdateUserName(String uiUpdateUserName) {
        this.uiUpdateUserName = uiUpdateUserName;
    }

    @Basic
    @Column(name = "ui_update_user_id")
    public Long getUiUpdateUserId() {
        return uiUpdateUserId;
    }

    public void setUiUpdateUserId(Long uiUpdateUserId) {
        this.uiUpdateUserId = uiUpdateUserId;
    }

}
