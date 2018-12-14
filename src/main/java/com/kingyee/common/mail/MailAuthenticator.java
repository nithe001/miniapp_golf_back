/*
 * Created on 2005-11-14
 */
package com.kingyee.common.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 验证信息bean
 * 
 * @author 马劼
 */
public class MailAuthenticator extends Authenticator {

	/** User name */
	private String un = null;

	/** Password */
	private String pwd = null;

	/**
	 * Constructor
	 * 
	 * @param un
	 * @param pwd
	 */
	public MailAuthenticator(String un, String pwd) {
		this.un = un;
		this.pwd = pwd;
	}

	/**
	 * Set UserName
	 * 
	 * @param un
	 */
	public void setUserName(String un) {
		this.un = un;
	}

	/**
	 * Set Password
	 * 
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		this.pwd = pwd;
	}

	/**
	 * @see Authenticator
	 */
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(un, pwd);
	}

}