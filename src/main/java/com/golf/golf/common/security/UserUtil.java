package com.golf.golf.common.security;

import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.CookieUtil;
import com.golf.common.util.RC4Util;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.CoreConnectionPNames;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;


/**
 * 用户工具类
 *
 * @author 裴宏
 *
 */
public class UserUtil {
	//存储用户扫带参数二维码的场景值
	public static final String USER_SCENE_SESSION_NAME = "USER_SCENE_SESSION";
	public static String USER_SESSION_NAME = "USER_LOGIN_SESSION";
	public static String COOKIE_AUTOLOGIN = "USER_LOGIN_COOKIE";
	public static int COOKIE_TIME = 365 * 24 * 60 * 60;

	/** httpclient超时时间10秒 */
	private static int HTTPCLIENT_TIMEOUT = 10000;

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
     * 取得用户的微信id
     *
     * @return
     */
    public static Long getUserId() {
        if (hasLogin()) {
//            return getLoginUser().getWechatUser().getCwuId();
			return getLoginUser().getUser().getUiId();
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
			return getLoginUser().getWechatUser().getWuiOpenid();
		}
		return null;
	}


    public static String getShowName(){
        return getLoginUser().getWechatUser().getWuiNickName();
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
	}

	/**
	 * 清除session
	 * @param sessionName
	 */
	public static void clearSession(String sessionName) {
		getSession().removeAttribute(sessionName);
	}


	public static HttpClient getHttpClient() throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		// 设置超时
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, HTTPCLIENT_TIMEOUT);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, HTTPCLIENT_TIMEOUT);
		// 为避免时间过长，不retry
		DefaultHttpRequestRetryHandler retryhandler = new DefaultHttpRequestRetryHandler(0, false);
		httpClient.setHttpRequestRetryHandler(retryhandler);

		HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);

		return httpClient;
	}
}
