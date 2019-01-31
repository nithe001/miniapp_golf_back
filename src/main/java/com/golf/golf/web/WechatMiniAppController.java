package com.golf.golf.web;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.RandomUtil;
import com.golf.golf.common.GenericController;
import com.golf.golf.common.security.UserModel;
import com.golf.golf.common.security.WechatUserUtil;
import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;
import com.golf.golf.service.UserService;
import com.golf.golf.service.WechatService;
import com.google.gson.JsonElement;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
     * 登录
     * https://blog.csdn.net/fanfan4569/article/details/80903450
     * @throws Exception
     */
	@ResponseBody
    @RequestMapping(value = "onLogin")
    public JsonElement wechatCore(String code, String userInfo){
        String errMsg = "";
		try{
        	if(StringUtils.isNotEmpty(code)){
				WxMaJscode2SessionResult jsCode2SessionInfo = this.wxMaService.jsCode2SessionInfo(code);
				String sessionkey = jsCode2SessionInfo.getSessionKey();
				String openid = jsCode2SessionInfo.getOpenid();
				if (StringUtils.isNotEmpty(openid) && StringUtils.isNotEmpty(sessionkey)) {
					// 成功 自定义生成3rd_session与openid&session_key绑定并返回3rd_session
					String loginSessionKey = RandomUtil.generateIntString(128);
					HttpSession session = WebUtil.getOrCreateSession();
					if (session.getAttribute(loginSessionKey) != null) {
						session.removeAttribute(loginSessionKey);
					}
					//以3rd_session为key,session_key+openid为value写入session存储
					session.setAttribute(loginSessionKey, sessionkey+","+openid);
					//回传loginSessionKey
					return JsonWrapper.newDataInstance(loginSessionKey);
				}
			}else{
				// 错误 未获取到用户凭证code
				logger.error("用户凭证code为空。code="+code);
			}
        }catch (Exception e) {
        	e.printStackTrace();
            logger.error("响应微信请求失败。code="+code, e);
        }
		return JsonWrapper.newErrorInstance("获取用户微信信息失败-"+errMsg);
    }



	@ResponseBody
	@RequestMapping(value = "onLogind")
	public JsonElement wechatCored(String code){
		String errMsg = "";
		try{
			if(StringUtils.isNotEmpty(code)){
				WxMaJscode2SessionResult jsCode2SessionInfo = this.wxMaService.jsCode2SessionInfo(code);
				if (StringUtils.isNotEmpty(jsCode2SessionInfo.getOpenid()) && StringUtils.isNotEmpty(jsCode2SessionInfo.getSessionKey())) {
					// 成功 自定义生成3rd_session与openid&session_key绑定并返回3rd_session
//					WxMaUserInfo wechatUserInfo = wxMaService.getUserService().getUserInfo(jsCode2SessionInfo.getSessionKey(), encryptedData, iv);
					WechatUserInfo wechatUserLocal = userService.getWechatUserByOpenid(jsCode2SessionInfo.getOpenid());
					/*if(wechatUserLocal != null){
						userService.updateWechatUser(wechatUserInfo,wechatUserLocal);
					}else{
						wechatUserLocal = userService.saveWechatUser(wechatUserInfo);
					}*/
					UserModel userModel = new UserModel();
					userModel.setWechatUser(wechatUserLocal);
					if(wechatUserLocal.getWuiUId()!= null){
						UserInfo u = userService.getUserById(wechatUserLocal.getWuiUId());
						userModel.setUser(u);
					}
				}else{
					// 错误 未获取到用户openid 或 session
					errMsg = "获取用户登录后的信息失败，";
				}
			}else{
				// 错误 未获取到用户凭证code
				logger.error("用户凭证code为空。code="+code);
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("响应微信请求失败。code="+code, e);
		}
		return JsonWrapper.newErrorInstance("获取用户微信信息失败-"+errMsg);
	}

	/**
	 * 获取并更新用户信息(微信、个人信息)
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "updateWui", method = RequestMethod.POST)
//	String sessionId, String nickName, String avatarUrl, String gender, String province, String city, String country
	public JsonElement updateWui(String sessionId, String encryptedData,
								 String iv){
		try{
			if(StringUtils.isNotEmpty(sessionId)){
				String openId = WechatUserUtil.getOpenIdBySessionId(sessionId);
				WechatUserInfo userInfo = userService.getWechatUserByOpenid(openId);
				if(userInfo != null){
//					userService.updateWUser(userInfo, nickName, avatarUrl, gender, province, city, country);
				}
				UserModel model = WechatUserUtil.getLoginUserBySessionId(sessionId);
				model.setWechatUser(userInfo);
				//更新session
				WechatUserUtil.saveUserInfoBySessionId(model, sessionId);
				return JsonWrapper.newDataInstance(model);
			}else{
				return JsonWrapper.newErrorInstance("操作失败，未获取到用户的openid");
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息失败", e);
		}
		return JsonWrapper.newErrorInstance("保存用户信息失败");
	}

}
