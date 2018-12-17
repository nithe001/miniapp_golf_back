package com.golf.golf.db;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * AbstractUserInfo entity provides the base persistence definition of the
 * UserInfo entity. @author MyEclipse Persistence Tools
 */
@MappedSuperclass
public abstract class AbstractUserInfo implements java.io.Serializable {

	// Fields

	private Long id;
	private Integer type;
	private String realName;
	private Integer age;
	private String telNo;
	private String email;
	private Long club;
	private Integer isValid;
	private Long createTime;
	private Long updateTime;

	// Constructors

	/** default constructor */
	public AbstractUserInfo() {
	}

	/** full constructor */
	public AbstractUserInfo(Integer type, String realName, Integer age, String telNo, String email,
							Long club, Integer isValid, Long createTime,
			Long updateTime) {
		this.type = type;
		this.realName = realName;
		this.age = age;
		this.telNo = telNo;
		this.email = email;
		this.club = club;
		this.isValid = isValid;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "real_name", length = 128)
	public String getRealName() {
		return this.realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	@Column(name = "age")
	public Integer getAge() {
		return this.age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Column(name = "tel_no", length = 128)
	public String getTelNo() {
		return this.telNo;
	}

	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}

	@Column(name = "email", length = 128)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "club")
	public Long getClub() {
		return this.club;
	}

	public void setClub(Long club) {
		this.club = club;
	}

	@Column(name = "is_valid")
	public Integer getIsValid() {
		return this.isValid;
	}

	public void setIsValid(Integer isValid) {
		this.isValid = isValid;
	}

	@Column(name = "create_time")
	public Long getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	@Column(name = "update_time")
	public Long getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
}