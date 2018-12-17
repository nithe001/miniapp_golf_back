package com.golf.golf.common.security;

import cn.binarywang.wx.miniapp.api.WxMaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 前台用户行为拦截器
 * 
 * @author nmy
 * @version 2014-4-14 下午2:03:58
 * 
 */
public class UserActionInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	protected WxMaService wxMaService;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);

		String signature = request.getParameter("signature");
		String nonce = request.getParameter("nonce");
		String timestamp = request.getParameter("timestamp");

		if (!this.wxMaService.checkSignature(timestamp, nonce, signature)) {
			// 消息签名不正确，说明不是公众平台发过来的消息
			response.getWriter().println("非法请求");
			return false;
		}

		String echoStr = request.getParameter("echostr");
		if (StringUtils.isNotBlank(echoStr)) {
			// 说明是一个仅仅用来验证的请求，回显echostr
			String echoStrOut = String.copyValueOf(echoStr.toCharArray());
			response.getWriter().println(echoStrOut);
			return false;
		}





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
