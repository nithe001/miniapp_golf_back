package com.golf.common.spring.mvc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.golf.common.MySessionContext;
import com.golf.golf.common.security.UserModel;
import com.golf.golf.common.security.WechatUserUtil;
import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

/**
 * 保存 Http 信息
 * 
 * @version 1.0 20140208
 * @version 1.1 20140213 改为基于 spring webutils实现
 * 
 */
public class WebUtil {
	/**
	 * 取得 request
	 * 
	 * @return request
	 */
	public static HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.currentRequestAttributes()).getRequest();
		return request;
	}

    /**
     * 取得 response
     *
     * @return response
     */
    public static HttpServletResponse getResponse() {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getResponse();
        return response;
    }

	/**
	 * 从 request 中取值
	 * 
	 * @param key
	 *            key
	 * @return request 中的值
	 */
	public static Object getRequestAttribute(String key) {
		return getRequest().getAttribute(key);
	}

	/**
	 * 设置 request attribute
	 * 
	 * @param key
	 * @param value
	 */
	public static void setRequestAttribute(String key, Object value) {
		if (value != null) {
			getRequest().setAttribute(key, value);
		} else {
			removeRequestAttribute(key);
		}
	}

	/**
	 * 删除 request attribute
	 * 
	 * @param key
	 */
	public static void removeRequestAttribute(String key) {
		getRequest().removeAttribute(key);
	}

	/**
	 * 取 cookie
	 * 
	 * @param key
	 * @return cookie
	 */
	public static Cookie getCookie(String key) {
		return WebUtils.getCookie(getRequest(), key);
	}

	/**
	 * 检查 request param是否存在
	 * 
	 * @param key
	 */
	public static boolean hasParam(String key) {
		return WebUtils.hasSubmitParameter(getRequest(), key);
	}

	/**
	 * 从parameter中取值
	 * 
	 * @param key
	 *            key
	 * @return Param 中的值
	 */
	public static String getParam(String key) {
		return WebUtils.findParameterValue(getRequest(), key);
	}

	/**
	 * 取以 prefix 开头的 param
	 * 
	 * @param prefix
	 */
	public static Map<String, Object> getParamStartingWith(String prefix) {
		return WebUtils.getParametersStartingWith(getRequest(), prefix);
	}

	/**
	 * 取临时目录
	 * 
	 * @return
	 */
	public static File getTempDir() {
		return WebUtils.getTempDir(getRequest().getServletContext());
	}

	/**
	 * 取文件绝对路径
	 * 
	 * @param path
	 *            相对路径
	 * @return 绝对路径
	 */
	public static String getRealPath(String path) {
		String p = null;
		try {
			p = WebUtils.getRealPath(getRequest().getServletContext(), path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return p;
	}

	/**
	 * 取得 session
	 * 
	 * @return session
	 */
	public static HttpSession getSession() {
		return getRequest().getSession(false);
	}

	/**
	 * 取得 session
	 * 
	 * @return session
	 */
	public static HttpSession getOrCreateSession() {
		return getRequest().getSession(true);
	}

	/**
	 * 取 session id
	 * 
	 * @return session id
	 */
	public static String getSessionId() {
		HttpSession session = getSession();
		return (session != null ? session.getId() : null);
	}

	/**
	 * 从 session 中取值
	 * 
	 * @param key
	 *            key
	 * @return session 中的值
	 */
	public static Object getSessionAttribute(String key) {
		HttpSession session = getSession();
		return (session != null ? session.getAttribute(key) : null);
	}

	/**
	 * 设置 session attribute
	 * 
	 * @param key
	 * @param value
	 */
	public static void setSessionAttribute(String key, Object value) {
		if (value != null) {
			getSession().setAttribute(key, value);
		} else {
			removeSessionAttribute(key);
		}
	}

	/**
	 * 删除 session attribute
	 * 
	 * @param key
	 */
	public static void removeSessionAttribute(String key) {
		HttpSession session = getSession();
		if (session != null) {
			session.removeAttribute(key);
		}
	}

	/**
	 * 获取header中的sessionid
	 */
	public static String getSessionIdByCookie() {
		return getRequest().getSession().getId();
	}

	/**
	 * 通过sessionid获取session
	 */
	public static HttpSession getSessionById(String sessionId) {
		MySessionContext myc= MySessionContext.getInstance();
		HttpSession sess = myc.getSession(sessionId);
		return sess;
	}

	/**
	 * 通过sessionid获取session
	 */
	public static HttpSession getSessionById() {
		String sessionId = WebUtil.getSessionIdByCookie();
		HttpSession sess = getSessionById(sessionId);
		return sess;
	}

	/**
	 * 通过sessionid获取UserModel
	 */
	public static UserModel getUserModelBySessionId(){
		String sessionId = WebUtil.getSessionIdByCookie();
		HttpSession session = WebUtil.getSessionById(sessionId);
		UserModel model = (UserModel)session.getAttribute(WechatUserUtil.USER_SESSION_NAME);
		return model;
	}

	/**
	 * 通过sessionid获取UserInfo
	 */
	public static UserInfo getUserInfoBySessionId(){
		UserModel model = getUserModelBySessionId();
		return model.getUser();
	}

	/**
	 * 通过sessionid获取WechatUserInfo
	 */
	public static WechatUserInfo getWechatUserInfoBySessionId(){
		UserModel model = getUserModelBySessionId();
		return model.getWechatUser();
	}

	/**
	 * 通过sessionid获取UserId
	 */
	public static Long getUserIdBySessionId(){
		String sessionId = WebUtil.getSessionIdByCookie();
		HttpSession session = WebUtil.getSessionById(sessionId);
		UserModel model = (UserModel)session.getAttribute(WechatUserUtil.USER_SESSION_NAME);
		if(model != null && model.getUser() != null){
			return model.getUser().getUiId();
		}
		return null;
	}

	/**
	 * 通过sessionid获取UserWechatId
	 */
	public static Long getUserWechatIdBySessionId(){
		String sessionId = WebUtil.getSessionIdByCookie();
		HttpSession session = WebUtil.getSessionById(sessionId);
		UserModel model = (UserModel)session.getAttribute(WechatUserUtil.USER_SESSION_NAME);
		if(model != null && model.getWechatUser() != null) {
			return model.getWechatUser().getWuiId();
		}
		return null;
	}

	/**
	 * 通过sessionid获取UserRealName
	 */
	public static String getUserNameBySessionId(){
		String sessionId = WebUtil.getSessionIdByCookie();
		HttpSession session = WebUtil.getSessionById(sessionId);
		UserModel model = (UserModel)session.getAttribute(WechatUserUtil.USER_SESSION_NAME);
		if(model != null){
			if(model.getUser() == null){
				return model.getWechatUser().getWuiNickName();
			}
			return model.getUser().getUiRealName();
		}
		return null;
	}

}
