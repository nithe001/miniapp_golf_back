package com.golf.golf.handler;

import com.golf.golf.service.WechatService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用户点击自定义菜单：scancode_push：扫码推事件的事件推送Handler
 * <p>
 */
@Component
public class ScanCodePushHandler extends AbstractHandler {

    @Autowired
    protected WxMpConfigStorage configStorage;
    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    protected WechatService coreService;



    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
		String key = wxMessage.getEventKey();
            /*if(key.equals("drugsCode")){
                String code = wxMessage.getScanCodeInfo().getScanResult();
                // 根据条形码code，取得药品信息
                WhDrugs drugs = service.getDrugByCode(code);
                if(drugs != null){
                    WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
                    item.setTitle(drugs.getWdTradeMarkName() + "_" + drugs.getWdGeneralName() + "_" + drugs.getWdSpecification());
                    item.setPicUrl(CommonUtil.getBasePath(WebUtil.getRequest()) + drugs.getWdThumbnailPath());
                    item.setDescription(drugs.getWdDrugInfo());
                    item.setUrl(CommonUtil.getBasePath(WebUtil.getRequest()) + "drugs/detail/" + drugs.getWdId());
                    WxMpXmlOutNewsMessage message = WxMpXmlOutMessage.NEWS()
                            .addArticle(item)
                            .fromUser(wxMessage.getToUser())
                            .toUser(wxMessage.getFromUser())
                            .build();
                    return message;
                }else{
                    WxMpXmlOutTextMessage message = WxMpXmlOutMessage.TEXT()
                        .content("您扫码的产品信息不存在。")
                        .fromUser(wxMessage.getToUser())
                        .toUser(wxMessage.getFromUser())
                        .build();
                    return message;
                }
            }*/
		return null;
    }
}
