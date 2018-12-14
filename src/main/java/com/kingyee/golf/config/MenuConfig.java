package com.kingyee.golf.config;

import com.kingyee.common.util.PropertyConst;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.bean.menu.WxMenuRule;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.tag.WxUserTag;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * 个性化菜单生成
 * Created by peihong.
 *
 *
 */
public class MenuConfig {

    /**
     * 定义菜单结构
     * @param type 0:游客;1:理事会;2:委员会;3:普通用户
     *
     * @return
     */
    protected static WxMenu getMenu(WxMpService wxMpService, int type, Long tagid) {
        WxMenu menu = new WxMenu();


		String domain = PropertyConst.DOMAIN;
//		"http://ph.sciemr.com";

        // ============== 心信速递 ===============
        WxMenuButton button1 = new WxMenuButton();
        button1.setName("心信速递");
		/*WxMenuButton button2 = new WxMenuButton();
		button2.setName("学术活动");
		WxMenuButton button3 = new WxMenuButton();
		button3.setName("协会官网");*/
        WxMenuButton button11 = new WxMenuButton();
//        button11.setType(WxConsts.BUTTON_VIEW);
        button11.setName("文献解读");
        button11.setUrl(wxMpService.oauth2buildAuthorizationUrl(domain + "/news/bannerUnscramble", "snsapi_base", ""));

        WxMenuButton button12 = new WxMenuButton();
//        button12.setType(WxConsts.BUTTON_VIEW);
        button12.setName("文献推荐");
        button12.setUrl(wxMpService.oauth2buildAuthorizationUrl(domain + "/news/recommendList", "snsapi_base", ""));

        WxMenuButton button13 = new WxMenuButton();
//        button13.setType(WxConsts.BUTTON_VIEW);
        button13.setName("学术咨询");
        button13.setUrl(wxMpService.oauth2buildAuthorizationUrl(domain + "/consult/bannerUnscramble", "snsapi_base", ""));

        button1.getSubButtons().add(button11);
        button1.getSubButtons().add(button12);
        button1.getSubButtons().add(button13);

        // ============== 学术活动 ===============
        WxMenuButton button2 = new WxMenuButton();
        button2.setName("学术活动");

        WxMenuButton button21 = new WxMenuButton();
//        button21.setType(WxConsts.BUTTON_VIEW);
        button21.setName("益心论道");
//        button21.setKey("drugsCode");
		button21.setUrl(wxMpService.oauth2buildAuthorizationUrl(domain + "/activities/list?type=1", "snsapi_base", ""));

        WxMenuButton button22 = new WxMenuButton();
//        button22.setType(WxConsts.BUTTON_VIEW);
        button22.setName("心内外科沙龙");
        button22.setUrl(wxMpService.oauth2buildAuthorizationUrl(domain + "/activities/list?type=2", "snsapi_base", ""));

        WxMenuButton button23 = new WxMenuButton();
//        button23.setType(WxConsts.BUTTON_VIEW);
        button23.setName("北青CTO俱乐部");
        button23.setUrl(wxMpService.oauth2buildAuthorizationUrl(domain + "/activities/list?type=3", "snsapi_base", ""));

		WxMenuButton button24 = new WxMenuButton();
//		button23.setType(WxConsts.BUTTON_VIEW);
		button23.setName("女医师俱乐部");
		button23.setUrl(wxMpService.oauth2buildAuthorizationUrl(domain + "/activities/list?type=4", "snsapi_base", ""));

        button2.getSubButtons().add(button21);
        button2.getSubButtons().add(button22);
		button2.getSubButtons().add(button23);
        button2.getSubButtons().add(button24);

        // ============== 协会官网 ===============
		WxMenuButton button3 = new WxMenuButton();
		button3.setName("协会官网");

		WxMenuButton button31 = new WxMenuButton();
//		button31.setType(WxConsts.BUTTON_VIEW);
		button31.setName("协会官网");
		button31.setUrl(wxMpService.oauth2buildAuthorizationUrl(domain + "/", "snsapi_base", ""));

		WxMenuButton button32 = new WxMenuButton();
//		button32.setType(WxConsts.BUTTON_VIEW);
		button32.setName("会员中心");
		button32.setUrl(wxMpService.oauth2buildAuthorizationUrl(domain + "/user/userCenter", "snsapi_base", ""));

		button3.getSubButtons().add(button31);
		button3.getSubButtons().add(button32);


        if(type == 0){
            // 游客
            menu.getButtons().add(button1);
            menu.getButtons().add(button2);
            menu.getButtons().add(button3);
        }else if(type == 1){
            // 患者
            menu.getButtons().add(button2);
            menu.getButtons().add(button3);
        }else if(type == 2){
            // 医生
            menu.getButtons().add(button1);
            menu.getButtons().add(button3);
        }

        if(type == 1 || type == 2){
            WxMenuRule rule = new WxMenuRule();
            rule.setTagId(tagid.toString());
            menu.setMatchRule(rule);
        }


        return menu;
    }

    /**
     * 运行此main函数即可生成公众号自定义菜单
     *
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context
                = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml", "mvc-core.xml"});


        WxMpService wxMpService = context.getBean(WxMpService.class);
        try {

            List<WxUserTag> tagList =  wxMpService.getUserTagService().tagGet();
            boolean hasTagPatient = false;
            boolean hasTagDoctor = false;
            Long tagIdPatient = null;
            Long tagIdDoctor = null;
            for (WxUserTag wxUserTag: tagList) {
                if(wxUserTag.getName().equals("patient")){
                    hasTagPatient = true;
                    tagIdPatient = wxUserTag.getId();
                }
                if(wxUserTag.getName().equals("doctor")){
                    hasTagDoctor = true;
                    tagIdDoctor = wxUserTag.getId();
                }
            }

            // ==== 生成用户标签tag =====
            if(!hasTagPatient){
                WxUserTag wxUserTag = wxMpService.getUserTagService().tagCreate("patient");
                tagIdPatient = wxUserTag.getId();
            }
            if(!hasTagDoctor){
                WxUserTag wxUserTag = wxMpService.getUserTagService().tagCreate("doctor");
                tagIdDoctor = wxUserTag.getId();
            }

            // 用户标签
            System.out.println("医生的标签" + tagIdDoctor);
            System.out.println("患者的标签" + tagIdPatient);




            // ==== 给个人设置tag =====
//            wxMpService.getUserTagService().batchUntagging(100L, new String[]{"olmZ8uJxPElPvL2nnu6qj04n8HbU"});
//            wxMpService.getUserTagService().batchTagging(tagIdPatient, new String[]{"olmZ8uJxPElPvL2nnu6qj04n8HbU"});
//            List<Long> tagIds = wxMpService.getUserTagService().userTagList("olmZ8uJxPElPvL2nnu6qj04n8HbU");
//            for (Long tagid: tagIds) {
//                System.out.println(tagid);
//            }

            // ==== 生成个性菜单 =====
          wxMpService.getMenuService().menuDelete();
            if(wxMpService.getMenuService().menuGet() == null){
                wxMpService.getMenuService().menuCreate(getMenu(wxMpService, 0, 0L));
                wxMpService.getMenuService().menuCreate(getMenu(wxMpService, 1, tagIdPatient));
                wxMpService.getMenuService().menuCreate(getMenu(wxMpService, 2, tagIdDoctor));
            }

            System.out.println(wxMpService.getMenuService().menuGet().toJson());

            // ==== 测试个性菜单 =====
            System.out.println(wxMpService.getMenuService().menuTryMatch("olmZ8uJxPElPvL2nnu6qj04n8HbU").toJson());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
