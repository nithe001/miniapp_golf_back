package com.golf.golf.web;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.google.gson.JsonElement;
import com.golf.common.gson.JsonWrapper;
import com.golf.golf.common.GenericController;
import com.golf.golf.common.security.UserModel;
import com.golf.golf.common.security.WechatUserUtil;
import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;
import com.golf.golf.service.UserService;
import com.golf.golf.service.WechatService;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by FirenzesEagle on 2016/5/30 0030.
 * Email:liumingbo2008@gmail.com
 */
@Controller
@RequestMapping(value = "/user/")
public class WechatMiniAppController extends GenericController {

    @Autowired
    protected WxMpConfigStorage configStorage;
	@Autowired
	protected WxMaService wxMaService;
    @Autowired
    protected WechatService wechatService;
    @Autowired
    protected UserService userService;

    /**
     * 获取用户微信信息
     * @throws Exception
     */
	@ResponseBody
    @RequestMapping(value = "miniappLogin", method = RequestMethod.POST)
    public JsonElement wechatCore(HttpServletRequest request, String sessionId, String encryptedData, String iv, String code){
        String errMsg = "";
		try{
        	if(StringUtils.isNotEmpty(code)){
				//获取登录后的session信息.
				WxMaJscode2SessionResult jsCode2SessionInfo = this.wxMaService.jsCode2SessionInfo(code);
				if (StringUtils.isNotEmpty(jsCode2SessionInfo.getOpenid()) && StringUtils.isNotEmpty(jsCode2SessionInfo.getSessionKey())) {
					// 成功 自定义生成3rd_session与openid&session_key绑定并返回3rd_session
					WxMaUserInfo wechatUserInfo = wxMaService.getUserService().getUserInfo(jsCode2SessionInfo.getSessionKey(), encryptedData, iv);
					WechatUserInfo wechatUserLocal = userService.getWechatUserByOpenid(jsCode2SessionInfo.getOpenid());
					if(wechatUserLocal != null){
						userService.updateWechatUser(wechatUserInfo,wechatUserLocal);
					}else{
						wechatUserLocal = userService.saveWechatUser(wechatUserInfo);
					}
					UserModel model = new UserModel();
					model.setWechatUser(wechatUserLocal);
					if(wechatUserLocal.getUId()!= null){
						UserInfo u = userService.getUserById(wechatUserLocal.getUId());
						model.setUser(u);
					}
					//存session
					WechatUserUtil.saveUserInfoBySessionId(model, sessionId);
					//回传sessionid
					sessionId = request.getSession().getId();
					model.setSessionId(sessionId);
					return JsonWrapper.newDataInstance(model);
				}else{
					// 错误 未获取到用户openid 或 session
					errMsg = "获取用户登录后的信息失败，"+" encryptedData="+encryptedData+" iv="+iv+" code="+code;
				}
			}else{
				// 错误 未获取到用户凭证code
				errMsg = "用户凭证code为空，"+" encryptedData="+encryptedData+" iv="+iv+" code="+code;
			}
        }catch (Exception e) {
        	e.printStackTrace();
            logger.error("响应微信请求失败。encryptedData="+encryptedData+" iv="+iv+" code="+code, e);
        }
		return JsonWrapper.newErrorInstance("获取用户微信信息失败-"+errMsg);
    }

	/**
	 * 更新用户信息(微信、个人信息)
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "updateWui", method = RequestMethod.POST)
	public JsonElement updateWui(String sessionId, String nickName, String avatarUrl, String gender, String province, String city, String country){
		try{
			if(StringUtils.isNotEmpty(sessionId)){
				String openId = WechatUserUtil.getOpenIdBySessionId(sessionId);
				WechatUserInfo userInfo = userService.getWechatUserByOpenid(openId);
				if(userInfo != null){
					userService.updateWUser(userInfo, nickName, avatarUrl, gender, province, city, country);
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
