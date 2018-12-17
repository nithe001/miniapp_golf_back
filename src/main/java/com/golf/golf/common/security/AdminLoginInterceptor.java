package com.golf.golf.common.security;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 后台拦截器
 * 
 * @author cky
 * @version 2014-4-14 下午2:03:58
 * 
 */
public class AdminLoginInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String requestUri = request.getRequestURI();
		String path = request.getContextPath();
		requestUri = requestUri.replaceFirst(path, "");
		if (!AdminUserUtil.hasLogin()) {
			response.sendRedirect(request.getContextPath() + "/admin/login");
			return false;
		}
//		if (AdminUserUtil.isAdmin()) {
//			return super.preHandle(request, response, handler);
//		} else {
//			response.sendRedirect(request.getContextPath() + "");
//			return false;
//		}
        return super.preHandle(request, response, handler);
	}

}
