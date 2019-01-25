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

import javax.servlet.http.HttpSession;
import java.util.List;

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
	public JsonElement userManage(String loginSessionKey) {
		try {
			if(StringUtils.isNotEmpty(loginSessionKey)){
				HttpSession session = WebUtil.getSession();
				if (session.getAttribute(loginSessionKey) != null) {
					String value = session.getAttribute(loginSessionKey).toString();
					String openId = value.split(",")[0];
					UserInfo user = service.getUserByOpenid(openId);
					return JsonWrapper.newDataInstance(user);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取用户微信信息失败。", e);
			return JsonWrapper.newErrorInstance("获取用户微信信息失败");
		}
		return JsonWrapper.newErrorInstance("用户未登录");
	}

	/**
	 * 修改个人信息init
	 * @return
	 */
	@RequestMapping("editUserInfoUI")
	public String editUserInfoUI(ModelMap mm) {
		try {
			UserInfo user = service.getUserById(UserUtil.getUserId());
			mm.addAttribute("user", user);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取个人信息时出错。", e);
			return "error";
		}
		return "user/userDetailEdit";
	}

	/**
	 * 修改个人信息
	 * @return
	 */
	@RequestMapping("updateUser")
	public String updateUser(UserInfo user) {
		try {
			service.updateUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("修改个人信息-保存时出错。", e);
			return "error";
		}
		return "redirect:userManage?edit=1";
	}

	/**
	 * 基本信息
	 *
	 * @return
	 */
	@RequestMapping("getUserInfoById")
	public String getUserInfoById(Long wechatId) {
		try {
			UserModel userModel = UserUtil.getLoginUser();
			UserInfo user = service.getUserById(userModel.getUser().getUiId());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取基本信息时出错。", e);
			return "error";
		}
		return "user/userDetail";
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
