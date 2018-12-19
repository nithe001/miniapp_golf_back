package com.golf.golf.db;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * UserInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_info")
public class UserInfo extends AbstractUserInfo implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public UserInfo() {
	}

	/** full constructor */
	public UserInfo(Integer type, String realName, Integer age, String telNo,
			String email, Long club, Integer isValid, Long createTime,
			Long updateTime) {
		super(type, realName, age, telNo, email, club, isValid, createTime,
				updateTime);
	}

}
