// ======================================
// Project Name:ifc
// Package Name:com.kingyee.ifc.common
// File Name:AdminLoginInterceptor.java
// Create Date:2014-4-14
// ======================================
package com.kingyee.golf.common.security;

import com.kingyee.common.spring.mvc.WebUtil;
import com.kingyee.common.util.CommonUtil;
import com.kingyee.common.util.CookieUtil;
import com.kingyee.golf.db.UserInfo;
import com.kingyee.golf.service.UserService;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 前台用户信息取得拦截器
 * 
 * @author peihong
 * @version 2014-4-14 下午2:03:58
 * 
 */
public class UserInfoInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UserService userService;
	@Autowired
	protected WxMpService wxMpService;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// 如果是爬虫，则跳过此拦截器
		if(CommonUtil.isRequestFromSpider(request)){
			return super.preHandle(request, response, handler);
		}
		String sessionId = request.getParameter("sessionId");
		if(StringUtils.isNotEmpty(sessionId)){
			String openId = WechatUserUtil.getOpenIdBySessionId(sessionId);
			CookieUtil.setCookie(request,response,"openId",openId);
			CookieUtil.setCookie(request,response,"sessionId",sessionId);
		}else{
//			response.sendRedirect(CommonUtil.getBasePath(request) + "user/loginInit");
			return false;
		}
		return super.preHandle(request, response, handler);
	}

}

