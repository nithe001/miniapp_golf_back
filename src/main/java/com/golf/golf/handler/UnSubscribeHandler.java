package com.golf.golf.handler;

import com.golf.golf.common.security.UserModel;
import com.golf.golf.service.UserService;
import com.golf.golf.service.WechatService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用户关注公众号Handler/用户扫描带参数的二维码关注公众号
 * <p>
 */
@Component
public class UnSubscribeHandler extends AbstractHandler {

    @Autowired
    protected WxMpConfigStorage configStorage;
    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    protected WechatService wechatService;
    @Autowired
    protected UserService userService;


	// 取消关注事件
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
		//更新用户信息
		try {
			WxMpUser wxMpUser = wechatService.getUserInfo(wxMessage.getFromUser(), "zh_CN");
			WxMpXmlOutTextMessage message = null;
			UserModel user = userService.getUserInfoByOpenId(wxMpUser.getOpenId());
			if(user != null){
				//更新用户绑定状态
				userService.updateSubscribeType(wxMpUser);
			}
			return message;
		} catch (Exception e) {
				logger.error("取消关注时，更新用户信息失败。", e);
			}
			return null;
		}
}
