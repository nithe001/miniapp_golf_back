package com.golf.golf.web;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.alibaba.fastjson.JSON;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.util.AesUtil;
import com.golf.golf.common.GenericController;
import com.golf.golf.service.UserService;
import com.golf.golf.service.WechatService;
import com.google.gson.JsonElement;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by FirenzesEagle on 2016/5/30 0030.
 * Email:liumingbo2008@gmail.com
 */
@Controller
@RequestMapping(value = "/")
public class WechatMiniAppController extends GenericController {

    @Autowired
    protected WxMpConfigStorage configStorage;
	@Autowired
	protected WxMaService wxMaService;
    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    protected WechatService wechatService;
    @Autowired
    protected UserService userService;


    /**
     * 登录——获取openid
     * https://blog.csdn.net/fanfan4569/article/details/80903450
     * @throws Exception
     */
	@ResponseBody
    @RequestMapping(value = "onLogin")
    public JsonElement wechatCore(@RequestAttribute("code") String code){
		try{
			WxMaJscode2SessionResult jsCode2SessionInfo = this.wxMaService.jsCode2SessionInfo(code);
			String sessionkey = jsCode2SessionInfo.getSessionKey();
			String openid = jsCode2SessionInfo.getOpenid();
			if (StringUtils.isNotEmpty(openid) && StringUtils.isNotEmpty(sessionkey)) {
				Map<String, Object> result = new HashMap<>();
				result.put("openid",openid);
				//对返回数据进行AES加密
				String jsonString = JSON.toJSONString(result);
				jsonString = AesUtil.encrypt(jsonString, AesUtil.key);
				logger.info("=== 用户登录 end 将openid={}加密并返回给小程序，加密结果：{} ============", openid, jsonString);
				return JsonWrapper.newDataInstance(jsonString);
			}
        }catch (Exception e) {
        	e.printStackTrace();
            logger.error("响应微信请求失败。code="+code, e);
        }
		return JsonWrapper.newErrorInstance("获取用户openid失败");
    }
}
