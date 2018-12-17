//****************************************************
//系统名					p2p
//子系统名				业务共通
//class名				CookieUtils
//主要功能				操作cookie的工具类
//
//===============================================
//version          变更日          变更者         变更内容
//-------------------------------------------------------------------------------------
//1.00             2006.09.30     	刘领娣        作成
//*****************************************************
package com.golf.common.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

    public CookieUtil() {
    }

    /**
     * 根据cookie的名称取得cookie
     * @param request			http请求
     * @param name				cookie的名称
     * @return cookie			Cookie类
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie cookies[] = request.getCookies();
        if (cookies == null || name == null || name.length() == 0) {
            return null;
        }
        Cookie cookie = null;
        for (int i = 0; i < cookies.length; i++) {
            if (!cookies[i].getName().equals(name)) {
                continue;
            }else {
                cookie = cookies[i];
                break;
            }
            
//            if (request.getServerName().equals(cookie.getDomain())) {
//                break;
//            }
        }
        return cookie;
    }

    /**
     * 删除cookie
     * @param request			http请求
     * @param response		http响应
     * @param cookie			Cookie类
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, Cookie cookie) {
        if (cookie != null) {
//            String path = request.getContextPath() != null ? request.getContextPath() : "/";
//            if ("".equals(path)) {
//                path = "/";
//            }
            /*liuld update start for 兼容 jive论坛的cookie*/
            String path = "/";
            cookie.setPath(path);
            cookie.setValue("");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
    
    /**
     * 设定cookie(有效期为30天)
     * @param request
     * @param response
     * @param name
     * @param value
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value) {
        setCookie(request, response, name, value, 0x278d00);
    }

    /**
     * 设定cookie
     * @param request
     * @param response
     * @param name
     * @param value
     * @param maxAge		cookie有效期(单位为秒)
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value,
            int maxAge) {
        if (value == null) {
            value = "";
        }
        String path = request.getContextPath() != null ? request.getContextPath() : "/";
        if ("".equals(path)) {
            path = "/";
        }
        /*for test*/
        path = "/";
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        response.addCookie(cookie);
    }
    
    /**
     * 设定cookie
     * @param request
     * @param response
     * @param name
     * @param value
     * @param maxAge		cookie有效期(单位为秒)
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value,
    		int maxAge, String domain) {
    	if (value == null) {
    		value = "";
    	}
    	String path = request.getContextPath() != null ? request.getContextPath() : "/";
    	if ("".equals(path)) {
    		path = "/";
    	}
    	/*for test*/
    	path = "/";
    	Cookie cookie = new Cookie(name, value);
    	cookie.setMaxAge(maxAge);
    	cookie.setPath(path);
    	cookie.setDomain(domain);
    	response.addCookie(cookie);
    }
    
}