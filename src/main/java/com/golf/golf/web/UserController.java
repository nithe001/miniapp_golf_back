package com.golf.golf.web;

import com.golf.common.gson.JsonWrapper;
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
	private UserService service;
	@Autowired
	protected WxMpService wxMpService;

	/**
	 * 登录Init
	 *
	 * @return
	 */
	@RequestMapping("loginInit")
	public String loginInit() {
		return "user/login";
	}


	/**
	 * 注册Init
	 *
	 * @return
	 */
	@RequestMapping("registerInit")
	public String register() {
		return "user/register";
	}

	/**
	 * 注册
	 *
	 * @return
	 */
	@RequestMapping("register")
	public String register(UserInfo user, String captcha) {
		try {
			service.saveUser(user,captcha);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("注册时出错。", e);
			return "error";
		}
		return "redirect:userManage";
	}

	/**
	 * 获取我的信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getUserInfo")
	public JsonElement getUserInfo() {
		try {
			UserInfo userInfo = service.getUserById(4L);
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
				service.updateUser(userInfoBean);
			}
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("更新用户信息时失败。", e);
			return JsonWrapper.newErrorInstance("更新用户信息时失败");
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
			UserInfo userInfo = service.getUserById(userId);
			result.put("userInfo",userInfo);
			boolean isOpen = service.userInfoIsOpen(userId);
			result.put("isOpen",true);
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
			UserInfo user = service.getUserById(userModel.getUser().getUiId());
			List<MatchInfo> calendarList = service.getCalendarListByUserId(null);
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
