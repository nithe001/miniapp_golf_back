package com.golf.golf.handler;

import com.golf.golf.service.WechatService;
import com.golf.golf.service.UserService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用户关注公众号Handler/用户扫描带参数的二维码关注公众号
 * <p>
 */
@Component
public class SubscribeHandler extends AbstractHandler {

    @Autowired
    protected WxMpConfigStorage configStorage;
    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    protected WechatService wechatService;
    @Autowired
    protected UserService userService;



    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        WxMpUser wxMpUser = wechatService.getUserInfo(wxMessage.getFromUser(), "zh_CN");
        // 保存用户
		WxMpXmlOutTextMessage message = null;
		// 关注事件
		if(StringUtils.isEmpty(wxMessage.getEventKey())){
			message = WxMpXmlOutMessage.TEXT()
					.content("欢迎您关注心信速递微信公众平台。\n\n" +
							"此平台旨在为宣传心血管疾病防治的相关信息，联系医生与传递学术的纽带。\n\n" +
							"学术咨询：随时为您解答心血管疾病防治的医学问题。\n\n" +
							"学术活动：为您提供权威的学术课件，帮助您更好的成长。\n\n" +
							"如果您感兴趣，请进行官网注册，我们将为您提供最新的信息服务。\n")
					.fromUser(wxMessage.getToUser())
					.toUser(wxMessage.getFromUser())
					.build();
		}else{
			// 扫描带参数二维码事件：用户未关注时，进行关注后的事件推送
			String eventKey = wxMessage.getEventKey();
			// qrscene_123123
			String qrscene = eventKey.substring(8);
			message = WxMpXmlOutMessage.TEXT()
					.content("欢迎关注HP根治，您已经关联医生!")
					.fromUser(wxMessage.getToUser())
					.toUser(wxMessage.getFromUser())
					.build();
		}

		return message;
    }
};
