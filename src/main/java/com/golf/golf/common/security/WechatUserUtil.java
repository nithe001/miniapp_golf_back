package com.golf.golf.common.security;

import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.CookieUtil;
import com.golf.common.util.RC4Util;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;


/**
 * 用户工具类
 *
 * @author 裴宏
 *
 */
public class WechatUserUtil {
	private static HashMap<String, Object> sessionIdMap = new HashMap<String, Object>();

	//存储用户扫带参数二维码的场景值
	public static final String USER_SCENE_SESSION_NAME = "USER_SCENE_SESSION";
	public static String USER_SESSION_NAME = "USER_LOGIN_SESSION";
	public static String COOKIE_AUTOLOGIN = "USER_LOGIN_COOKIE";
	public static int COOKIE_TIME = 365 * 24 * 60 * 60;

	/** 用户权限分隔符 */
	public static final String USER_RIGHT_SPLITER = ",";

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
	 * 判断用户是否登录
	 *
	 * @return
	 */
	public static boolean hasLoginBySessionId(String sessionId) {
		return getSessionById(sessionId) != null;
	}



    /**
     * 做登录时，需要的一些操作
     */
	public static void login(UserModel userModel) throws UnsupportedEncodingException {
        getSession().setAttribute(USER_SESSION_NAME, userModel);
        setCookie(WebUtil.getRequest(), WebUtil.getResponse(), COOKIE_AUTOLOGIN, getUserId().toString(), COOKIE_TIME);
    }

    /**
     * 做logout时，需要的一些操作
     */
    public static void logout() throws UnsupportedEncodingException {
        getSession().setAttribute(USER_SESSION_NAME, null);
        setCookie(WebUtil.getRequest(), WebUtil.getResponse(), COOKIE_AUTOLOGIN, "", -1);
    }

	/**
	 * 取得登录用户信息
	 *
	 * @return
	 */
	public static UserModel getLoginUser() {
		if (hasLogin()) {
			return (UserModel) getSession().getAttribute(USER_SESSION_NAME);
		} else {
			return null;
		}
	}

	/**
	 * 取得登录用户信息
	 *
	 * @return
	 */
	public static UserModel getLoginUserBySessionId(String sessionId) {
		if (hasLoginBySessionId(sessionId)) {
			return (UserModel) getSessionById(sessionId).getAttribute(USER_SESSION_NAME);
		} else {
			return null;
		}
	}

    /**
     * 取得用户的微信id
     *
     * @return
     */
    public static Long getUserId() {
        if (hasLogin()) {
//            return getLoginUser().getWechatUser().getCwuId();
			return getLoginUser().getUser().getId();
        }
        return null;
    }

	/**
	 * 取得用户的openid
	 *
	 * @return
	 */
	public static String getOpenId() {
		if (hasLogin()) {
			return getLoginUser().getWechatUser().getOpenid();
		}
		return null;
	}

	/**
	 * 取得用户的openid
	 *
	 * @return
	 */
	public static String getOpenIdBySessionId(String sessionId) {
		if (hasLoginBySessionId(sessionId)) {
			return getLoginUserBySessionId(sessionId).getWechatUser().getOpenid();
		}
		return null;
	}


    public static String getShowName(){
        return getLoginUser().getWechatUser().getNickName();
    }

	/**
	 * 创造cookie.
	 * @throws UnsupportedEncodingException 
	 *
	 */
	public static void setCookie(HttpServletRequest request, HttpServletResponse response,
		String cookieName, String userName, int maxAge) throws UnsupportedEncodingException{
		/* 自动登录 cookie设定 */
		String autoLogin = RC4Util.encode(userName);
		CookieUtil.setCookie(request, response, cookieName, autoLogin, maxAge);
	}
	
	/**
	 * 创造cookie.
	 * @throws UnsupportedEncodingException 
	 *
	 */
	public static void setCookie(HttpServletRequest request, HttpServletResponse response,
			String cookieName, String userName, int maxAge, String domain) throws UnsupportedEncodingException{
		/* 自动登录 cookie设定 */
		String autoLogin = RC4Util.encode(userName);
		CookieUtil.setCookie(request, response, cookieName, autoLogin, maxAge, domain);
	}
	
	/**
	 * 获得cookie对应的内容
	 *
	 * @param request
	 * @param key
	 * @return Cookie
	 */
	public static String getCookie(HttpServletRequest request, String key) {
		Cookie cookie = CookieUtil.getCookie(request, key);
		if(cookie != null){
			String autoLogin = cookie.getValue();
			autoLogin = RC4Util.decode(autoLogin);
			return autoLogin;
		}else{
			return null;
		}
	}

	/**
	 * 判断是否有用户信息
	 *
	 * @return
	 */
	public static boolean hasUserInfo() {
		return getSession().getAttribute(USER_SESSION_NAME) != null;
	}

	/**
	 * 绑定
	 * @author songqian
	 * @date 2017/5/4 15:47
	 */
	public static void saveUserInfo(UserModel user) {
		HttpSession session = getSession();
		Object o = session.getAttribute(USER_SESSION_NAME);
		if (o != null) {
			session.removeAttribute(USER_SESSION_NAME);
		}
		session.setAttribute(USER_SESSION_NAME, user);
		addSession(session);
	}

	/**
	 * 绑定
	 * @author nmy
	 * @date 2017/5/4 15:47
	 */
	public static void saveUserInfoBySessionId(UserModel user,String sessionId) {
		HttpSession session = StringUtils.isNotEmpty(sessionId)?getSessionById(sessionId):getSession();
		Object o = session.getAttribute(USER_SESSION_NAME);
		if (o != null) {
			session.removeAttribute(USER_SESSION_NAME);
		}
		session.setAttribute(USER_SESSION_NAME, user);
		addSession(session);
	}

	/**
	 * 清除session
	 * @param sessionName
	 */
	public static void clearSession(String sessionName) {
		delSession(getSession());
		getSession().removeAttribute(sessionName);
	}

	/**
	 * 添加根据sessionid取session -begin
	 */
	public static synchronized void addSession(HttpSession session) {
		if (session != null) {
			sessionIdMap.put(session.getId(), session);
		}
	}
	public static synchronized void delSession(HttpSession session) {
		if (session != null) {
			sessionIdMap.remove(session.getId());
		}
	}
	public static synchronized HttpSession getSessionById(String session_id) {
		if (sessionIdMap.containsKey(session_id)) {
			return (HttpSession) sessionIdMap.get(session_id);
		} else {
			return null;
		}
	}
	/**
	 * 添加根据sessionid取session -end
	 */

}
