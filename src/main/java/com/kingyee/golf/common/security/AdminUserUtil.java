package com.kingyee.golf.common.security;

import javax.servlet.http.HttpSession;

import com.kingyee.common.spring.mvc.WebUtil;

/**
 * 用户工具类
 * 
 * 
 * @author 李旭光
 * @version 2013-7-13下午1:11:59
 */
public class AdminUserUtil {

	public static final String USER_SESSION_NAME = "ADMIN_USER_LOGIN_SESSION";

	
	/** 取得session */
	private static HttpSession getSession() {
		return WebUtil.getOrCreateSession();
	}

	/**
	 * 判断用户是否登录
	 * 
	 * @return
	 */
	public static boolean hasLogin() {
		return getSession().getAttribute(USER_SESSION_NAME) != null;
	}


	/**
	 * 取得登录用户信息
	 * 
	 * @return
	 */
	public static AdminUserModel getLoginUser() {
		if (hasLogin()) {
			return (AdminUserModel) getSession().getAttribute(USER_SESSION_NAME);
		} else {
			return null;
		}
	}
	
	public static String getShowName(){
		return getLoginUser().getName();
	}
	
	public static Long getUserId(){
		return getLoginUser().getId();
	}
	
	public static long getRole(){
		return getLoginUser().getRole();
	}

}
