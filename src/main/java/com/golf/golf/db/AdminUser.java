package com.golf.golf.db;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * AdminUser entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "admin_user")
public class AdminUser extends AbstractAdminUser implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public AdminUser() {
	}

	/** full constructor */
	public AdminUser(String auUserName, String auPassword, String auShowName,
			Integer auSex, Integer auAge, String auTel, String auEmail,
			Long auRole, Integer auIsValid, Long auCreateDate,
			Long auCreateUserId, String auCreateUserName, Long auUpdateUserId,
			String auUpdateUserName, Long auUpdateDate) {
		super(auUserName, auPassword, auShowName, auSex, auAge, auTel, auEmail,
				auRole, auIsValid, auCreateDate, auCreateUserId,
				auCreateUserName, auUpdateUserId, auUpdateUserName,
				auUpdateDate);
	}

}
