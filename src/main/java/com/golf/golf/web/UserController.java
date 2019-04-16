package com.golf.golf.web;

import com.golf.common.gson.JsonWrapper;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.golf.common.security.UserModel;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.UserInfo;
import com.golf.golf.service.UserService;
import com.google.gson.JsonElement;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户
 * @author nmy
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {
	private final static Logger logger = LoggerFactory
			.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	@Autowired
	protected WxMpService wxMpService;



	/**
	 * 保存/更新 微信用户信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "saveUserInfo")
	public JsonElement saveUserInfo(String openid, String userDataStr) {
		try {
			if(StringUtils.isNotEmpty(openid) && StringUtils.isNotEmpty(userDataStr)){
				userService.saveOrUpdateWechatUserInfo(openid, userDataStr);
			}
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存微信用户信息时出错。" + e);
			return JsonWrapper.newErrorInstance("保存微信用户信息时出错");
		}
	}

	/**
	 * 获取我的信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getUserInfo")
	public JsonElement getUserInfo() {
		try {
			UserInfo userInfo = userService.getUserById(WebUtil.getUserIdBySessionId());
			return JsonWrapper.newDataInstance(userInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取用户信息失败。", e);
			return JsonWrapper.newErrorInstance("获取用户信息失败");
		}
	}

	/**
	 * 修改个人信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updateUserInfo")
	public JsonElement updateUserInfo(String userInfo) {
		try {
			if(StringUtils.isNotEmpty(userInfo)){
				net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(userInfo);
				UserInfo userInfoBean = (UserInfo) net.sf.json.JSONObject.toBean(jsonObject, UserInfo.class);
				userService.updateUser(userInfoBean);
			}
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("更新用户信息时失败。", e);
			return JsonWrapper.newErrorInstance("更新用户信息时失败");
		}
	}

	/**
	 * 更新签名
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updateUserSignature")
	public JsonElement updateUserSignature(String signature) {
		try {
			if(StringUtils.isNotEmpty(signature)){
				userService.updateUserSignature(signature);
			}
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("更新用户签名时失败。", e);
			return JsonWrapper.newErrorInstance("更新用户签名时失败");
		}
	}


	/**
	 * 基本信息
	 * 详细资料 只有是队友且该球队要求 详细资料时才可见
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getUserInfoById")
	public JsonElement getUserInfoById(Long userId) {
		try {
			Map<String, Object> result = new HashMap<>();
			UserInfo userInfo = userService.getUserById(userId);
			result.put("userInfo",userInfo);
			boolean isOpen = userService.userInfoIsOpen(userId);
			result.put("isOpen",isOpen);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("前台-根据用户id获取用户信息失败。" + e);
			return JsonWrapper.newErrorInstance("根据用户id获取用户信息失败。");
		}
	}

	/**
	 * 我的日历
	 *
	 * @return
	 */
	@RequestMapping("myClub")
	public String myClub(ModelMap mm) {
		try {
			UserModel userModel = UserUtil.getLoginUser();
			UserInfo user = userService.getUserById(userModel.getUser().getUiId());
			List<MatchInfo> calendarList = userService.getCalendarListByUserId(null);
			mm.addAttribute("calendarList", calendarList);
			mm.addAttribute("user", user);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取用户信息时出错。", e);
			return "error";
		}
		return "user/myClub";
	}

	/**
	 * 找回密码
	 * @return
	 */
	@RequestMapping("forgetPwdUI")
	public String forgetPwdUI() {
		return "user/forgetPwd";
	}

	/**
	 * 重置密码页面
	 * @return
	 */
	@RequestMapping("forgetPwdResetUI")
	public String resetPwdUI(ModelMap mm ,String telNo,String code) {
		mm.addAttribute("telNo",telNo);
		mm.addAttribute("code",code);
		return "user/resetPwd";
	}

}
