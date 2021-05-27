package com.golf.golf.common.security;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 后台用户拦截器
 * 
 * @author cky
 * @version 2014-4-14 下午2:03:58
 * 
 */
public class AdminRoleInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		if (!AdminUserUtil.hasLogin()) {
			response.sendRedirect(request.getContextPath() + "/admin/login");
			return false;
		}
		if (AdminUserUtil.getRole() != null && AdminUserUtil.getRole() != 0) {
			response.sendRedirect(request.getContextPath() + "/403");
			return false;
		}
        return super.preHandle(request, response, handler);
	}

}
