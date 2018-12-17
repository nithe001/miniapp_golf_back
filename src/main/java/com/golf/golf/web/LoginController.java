package com.golf.golf.web;

import com.golf.golf.common.security.UserUtil;
import com.golf.golf.db.UserInfo;
import com.golf.golf.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户
 * 
 * @author peihong
 * 2017年05月10日
 */
@Controller
@RequestMapping(value = "")
public class LoginController {
	private final static Logger logger = LoggerFactory
			.getLogger(LoginController.class);
	@Autowired
	private UserService logic;

	/**
	 * 注册
	 */
	@RequestMapping("register")
	public String register(UserInfo user){
		try{
			logic.registerUser(user);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("用户注册时出错。"+ e );
			return "error";
		}
		return "redirect:user/userCenter";
	}

	/**
	 * 用户登录
	 */
	@RequestMapping("login")
	public String login(UserInfo user){
		try{
//			UserUtil.login(user);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("用户登录时出错。"+ e );
			return "error";
		}
		return "user/userCenter";
	}

    /**
     * 用户登出操作
     */
    @RequestMapping("logout")
    public String logout(){
        try{
            UserUtil.logout();
        }catch(Exception e){
            e.printStackTrace();
            logger.error("用户登出时出错。"+ e );
            return "error";
        }
        return "redirect:news/bannerUnscramble";
    }

	/**
	 * 个人中心
	 */
	@RequestMapping("userCenter")
	public String userCenter(ModelMap mm,Long userId){
		UserInfo user = logic.getUserById(userId);
		mm.addAttribute("user",user);
		return "user/userCenter";
	}

	/**
	 * 编辑用户信息-init
	 */
	@RequestMapping("editUserInfoUI")
	public String editUserInfoInit(ModelMap mm,Long userId){
		UserInfo user = logic.getUserById(userId);
		mm.addAttribute("user",user);
		return "user/userCenter";
	}


}
