package com.kingyee.golf.handler;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaMessage;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 对所有接收到的消息输出日志，也可进行持久化处理
 *
 * Created by FirenzesEagle on 2016/7/27 0027.
 * Email:liumingbo2008@gmail.com
 */
@Component
public class MiniAppLogHandler extends MiniAppAbstractHandler {

    @Override
	public void handle(WxMaMessage message, Map<String, Object> context,
				WxMaService service, WxSessionManager sessionManager) {
		this.logger.info("\n接收到请求消息，内容：【{}】", message.toString());
	}
}