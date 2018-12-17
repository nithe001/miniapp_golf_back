package com.golf.golf.common;

import com.golf.common.util.CommonUtil;
import com.golf.golf.common.security.UserModel;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;
import com.golf.golf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 前台用户信息取得拦截器
 * 
 * @author peihong
 * @version 2014-4-14 下午2:03:58
 * 
 */
public class ActiveInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UserService service;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(true);
        UserModel userInfo = session != null ? (UserModel) session.getAttribute(UserUtil.USER_SESSION_NAME) : null;

        if(userInfo != null){
            UserInfo user = userInfo.getUser();
            WechatUserInfo wecahtUser = userInfo.getWechatUser();

            if(user == null){
                // 登录
                response.sendRedirect(CommonUtil.getBasePath(request) + "user/loginInit");
                return false;
            }
            /*else{
                if(user.getCuType().equals(UserTypeEnum.DOMESTIC.text())){
                    if(StringUtils.isBlank(user.getCuRealName())){
                        // 患者认证
                        response.sendRedirect(CommonUtil.getBasePath(request) + "user/patientAuthInit");
                        return false;
                    }
                }
            }*/
        }

        return super.preHandle(request, response, handler);
    }

}
