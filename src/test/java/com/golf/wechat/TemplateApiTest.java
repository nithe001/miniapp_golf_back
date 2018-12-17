package com.golf.wechat;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;

/**
 * 模板消息接口生成
 * Created by peihong.
 *
 *
 */
public class TemplateApiTest {


    /**
     * 运行此main函数即可生成公众号自定义菜单
     *
     * @param args
     */
    public static void main(String[] args) throws WxErrorException {
        ClassPathXmlApplicationContext context
                = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml", "mvc-core.xml"});

        WxMpService wxMpService = context.getBean(WxMpService.class);

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SSS");
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser("olmZ8uJxPElPvL2nnu6qj04n8HbU")
                .templateId("_wZ_ioJM_2P61Qv-VZgNZd1AsHF3R1-oXlyDmUyHgGA").build();

        /*templateMessage.addWxMpTemplateData(
                new WxMpTemplateData("first", "大势，您的服药时间到喽，请您按时服药。", "#FF00FF"));
        templateMessage.addWxMpTemplateData(
                new WxMpTemplateData("keyword1", "络活喜", "#FF00FF"));
        templateMessage.addWxMpTemplateData(
                new WxMpTemplateData("keyword2", "5mg", "#FF00FF"));
        templateMessage.addWxMpTemplateData(
                new WxMpTemplateData("keyword3", "请您参考说明书【注意事项】", "#FF00FF"));
        templateMessage.addWxMpTemplateData(
                new WxMpTemplateData("remark", "谢谢您的使用哦！", "#FF00FF"));*/
        templateMessage.setUrl("http://test9.sciemr.com/durgs/");
        String msgId = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        System.out.println(msgId);

    }

}
