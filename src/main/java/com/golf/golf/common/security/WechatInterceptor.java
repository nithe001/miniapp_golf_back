package com.golf.golf.common.security;

import com.golf.common.util.CommonUtil;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 微信关注拦截器
 * 
 * @author peihong
 * @version 2014-4-14 下午2:03:58
 * 
 */
public class WechatInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private WxMpService wxMpService;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(true);
        UserModel userInfo = session != null ? (UserModel) session.getAttribute(UserUtil.USER_SESSION_NAME) : null;

        if(userInfo == null){
            // 没有身份信息。通过微信的oatuh2接口取得用户的openid
            String basePath = CommonUtil.getBasePath(request);
            String callBackUrl = CommonUtil.getFullUrl(request, true);
            String getOpenidUrl = basePath + "getOpenid?url=" + callBackUrl;
            String wechatOauthUrl = wxMpService.oauth2buildAuthorizationUrl(getOpenidUrl, "snsapi_base", "");
            response.sendRedirect(wechatOauthUrl);
            return false;
        }

        return super.preHandle(request, response, handler);
	}

}
