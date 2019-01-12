package com.golf.golf.handler;

import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;
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
 * 用户扫描带参数二维码事件：用户已关注时的事件推送Handler
 * <p>
 */
@Component
public class ScanSubscribeHandler extends AbstractHandler {

    @Autowired
    protected WxMpConfigStorage configStorage;
    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    protected WechatService coreService;
    @Autowired
    protected UserService userService;



    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        WxMpXmlOutTextMessage message = null;
        WxMpUser wxMpUser = coreService.getUserInfo(wxMessage.getFromUser(), "zh_CN");
		// 扫描带参数二维码事件：用户未关注时，进行关注后的事件推送
		String qrscene = wxMessage.getEventKey();

		// 是否已经是医生
		WechatUserInfo wechatUser = userService.getWechatUserByOpenid(wxMpUser.getOpenId());
		if (wechatUser != null && wechatUser.getWuiUId() != null) {
			UserInfo user = userService.getUserById(wechatUser.getWuiUId());
		}else{
			message = WxMpXmlOutMessage.TEXT()
					.content("您已经关联医生!")
					.fromUser(wxMessage.getToUser())
					.toUser(wxMessage.getFromUser())
					.build();
		}
		return message;
    }

};
