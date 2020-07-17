package com.golf.golf.db;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user_info")
public class UserInfo implements java.io.Serializable {

	// Fields

	private Long uiId;
	private String uiOpenId;
    private String uiWechat;
	private Integer uiType;
	private String uiHeadimg;
	private String uiPersonalizedSignature;
	private String uiLongitude;
	private String uiLatitude;
	private String uiRealName;
	private String uiNickName;
	private String uiSex;
	private Integer uiAge;
    private String uiBirthday;
	private String uiTelNo;
	private String uiEmail;
	private String uiGraduateSchool;
	private String uiGraduateDepartment;
	private String uiGraduateTime;
	private String uiMajor;
    private String uiDegree;
	private String uiStudentId;
    private String uiAlumniCard;
	private String uiWorkUnit;
    private String uiOccupation;
	private String uiAddress;
	private String uiHomeCourse;
    private String uiHandicap;
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
	public UserInfo(String uiOpenId, String uiWechat, Integer uiType, String uiHeadimg,
					String uiPersonalizedSignature, String uiLongitude,
					String uiLatitude, String uiRealName, String uiNickName, String uiSex, Integer uiAge,String uiBirthday,
					String uiTelNo, String uiEmail, String uiGraduateSchool,
					String uiGraduateDepartment, String uiGraduateTime, String uiMajor,String uiDegree,String uiStudentId,String uiAlumniCard,
					String uiWorkUnit,String uiOccupation, String uiAddress, String uiHomeCourse,String uiHandicap, Integer uiIsValid,
					Long uiCreateTime, String uiCreateUserName, Long uiCreateUserId,
					Long uiUpdateTime, String uiUpdateUserName, Long uiUpdateUserId) {

	    this.uiOpenId = uiOpenId;
        this.uiWechat = uiWechat;
		this.uiType = uiType;
		this.uiHeadimg = uiHeadimg;
		this.uiPersonalizedSignature = uiPersonalizedSignature;
		this.uiLongitude = uiLongitude;
		this.uiLatitude = uiLatitude;
		this.uiRealName = uiRealName;
		this.uiNickName = uiNickName;
		this.uiSex = uiSex;
		this.uiAge = uiAge;
        this.uiBirthday = uiBirthday;
		this.uiTelNo = uiTelNo;
		this.uiEmail = uiEmail;
		this.uiGraduateSchool = uiGraduateSchool;
		this.uiGraduateDepartment = uiGraduateDepartment;
		this.uiGraduateTime = uiGraduateTime;
		this.uiMajor = uiMajor;
        this.uiDegree = uiDegree;
		this.uiStudentId = uiStudentId;
        this.uiAlumniCard = uiAlumniCard;
		this.uiWorkUnit = uiWorkUnit;
        this.uiOccupation = uiOccupation;
		this.uiAddress = uiAddress;
		this.uiHomeCourse = uiHomeCourse;
        this.uiHandicap = uiHandicap;
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

	@Column(name = "ui_open_id")
	public String getUiOpenId() {
		return this.uiOpenId;
	}

	public void setUiOpenId(String uiOpenId) {
		this.uiOpenId = uiOpenId;
	}

    @Column(name = "ui_wechat")
    public String getUiWechat() {
        return this.uiWechat;
    }

    public void setUiWechat(String Wechat) {
        this.uiWechat = uiWechat;
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

	@Column(name = "ui_personalized_signature")
	public String getUiPersonalizedSignature() {
		return this.uiPersonalizedSignature;
	}

	public void setUiPersonalizedSignature(String uiPersonalizedSignature) {
		this.uiPersonalizedSignature = uiPersonalizedSignature;
	}

	@Column(name = "ui_longitude")
	public String getUiLongitude() {
		return this.uiLongitude;
	}

	public void setUiLongitude(String uiLongitude) {
		this.uiLongitude = uiLongitude;
	}

	@Column(name = "ui_latitude")
	public String getUiLatitude() {
		return this.uiLatitude;
	}

	public void setUiLatitude(String uiLatitude) {
		this.uiLatitude = uiLatitude;
	}

	@Column(name = "ui_real_name")
	public String getUiRealName() {
		return this.uiRealName;
	}

	public void setUiRealName(String uiRealName) {
		this.uiRealName = uiRealName;
	}

	@Column(name = "ui_nick_name")
	public String getUiNickName() {
		return uiNickName;
	}

	public void setUiNickName(String uiNickName) {
		this.uiNickName = uiNickName;
	}

	@Column(name = "ui_sex")
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

    @Column(name = "ui_birthday")
    public String getUiBirthday() {
        return this.uiBirthday;
    }

    public void setUiBirthday(String uiBirthday) {
        this.uiBirthday = uiBirthday;
    }

	@Column(name = "ui_tel_no")
	public String getUiTelNo() {
		return this.uiTelNo;
	}

	public void setUiTelNo(String uiTelNo) {
		this.uiTelNo = uiTelNo;
	}

	@Column(name = "ui_email")
	public String getUiEmail() {
		return this.uiEmail;
	}

	public void setUiEmail(String uiEmail) {
		this.uiEmail = uiEmail;
	}

	@Column(name = "ui_graduate_school")
	public String getUiGraduateSchool() {
		return this.uiGraduateSchool;
	}

	public void setUiGraduateSchool(String uiGraduateSchool) {
		this.uiGraduateSchool = uiGraduateSchool;
	}

	@Column(name = "ui_graduate_department")
	public String getUiGraduateDepartment() {
		return this.uiGraduateDepartment;
	}

	public void setUiGraduateDepartment(String uiGraduateDepartment) {
		this.uiGraduateDepartment = uiGraduateDepartment;
	}

	@Column(name = "ui_graduate_time")
	public String getUiGraduateTime() {
		return this.uiGraduateTime;
	}

	public void setUiGraduateTime(String uiGraduateTime) {
		this.uiGraduateTime = uiGraduateTime;
	}

    @Column(name = "ui_major")
    public String getUiMajor() {
        return this.uiMajor;
    }

    public void setUiMajor(String uiMajor) {
        this.uiMajor = uiMajor;
    }

	@Column(name = "ui_degree")
	public String getUiDegree() {
		return this.uiDegree;
	}

	public void setUiDegree(String uiDegree) {
		this.uiDegree = uiDegree;
	}

    @Column(name = "ui_student_id")
    public String getUiStudentId() {
        return uiStudentId;
    }

    public void setUiStudentId(String uiStudentId) {
        this.uiStudentId = uiStudentId;
    }

    @Column(name = "ui_alumni_card")
    public String getUiAlumniCard() {
        return uiAlumniCard;
    }

    public void setUiAlumniCard(String uiAlumniCard) {
        this.uiAlumniCard = uiAlumniCard;
    }

    @Column(name = "ui_work_unit")
    public String getUiWorkUnit() {
        return this.uiWorkUnit;
    }

    public void setUiWorkUnit(String uiWorkUnit) {
        this.uiWorkUnit = uiWorkUnit;
    }

    @Column(name = "ui_occupation")
    public String getUiOccupation() {
        return this.uiOccupation;
    }

    public void setUiOccupation(String uiOccupation) {
        this.uiOccupation = uiOccupation;
    }

    @Column(name = "ui_address")
    public String getUiAddress() {
        return uiAddress;
    }

    public void setUiAddress(String uiAddress) {
        this.uiAddress = uiAddress;
    }

    @Column(name = "ui_home_course")
    public String getUiHomeCourse() {
        return uiHomeCourse;
    }

    public void setUiHomeCourse(String uiHomeCourse) {
        this.uiHomeCourse = uiHomeCourse;
    }

    @Column(name = "ui_handicap")
    public String getUiHandicap() {
        return uiHandicap;
    }

    public void setUiHandicap(String uiHandicap) {
        this.uiHandicap = uiHandicap;
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

	@Column(name = "ui_update_user_name")
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

	private String userName;
	@Transient

	public String getUserName() {
		userName = StringUtils.isNotEmpty(this.getUiRealName())?this.getUiRealName():this.getUiNickName();
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
