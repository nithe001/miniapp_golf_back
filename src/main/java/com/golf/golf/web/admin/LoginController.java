package com.golf.golf.web.admin;

import com.golf.common.util.EncryptUtil;
import com.golf.golf.common.security.AdminUserModel;
import com.golf.golf.common.security.AdminUserUtil;
import com.golf.golf.db.AdminUser;
import com.golf.golf.service.admin.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * 后台登录
 * @author nmy
 * 2016年9月7日
 */
@Controller
@RequestMapping(value = "/admin")
public class LoginController {
	
	private final static Logger logger = LoggerFactory
			.getLogger(LoginController.class);

    @Autowired
    private LoginService logic;

	@RequestMapping(value = {"login","/"})
	public String index(ModelMap mm, AdminUser user, HttpServletRequest request) {
		if(StringUtils.isEmpty(user.getAuUserName())){
			mm.addAttribute("msg", "用户名不能为空！");			
			return "admin/login";
		}
		if(StringUtils.isEmpty(user.getAuPassword())){
			mm.addAttribute("msg", "密码不能为空！");			
			return "admin/login";
		}
		try{
			AdminUser db = logic.getUser(user.getAuUserName());
			if(db != null){
				if(EncryptUtil.getSHA256Value(user.getAuPassword()).equals(db.getAuPassword())){//登录成功
					AdminUserModel um = new AdminUserModel();
					um.setId(db.getAuId());
					um.setName(db.getAuUserName());
					um.setShowName(db.getAuShowName());
					um.setRole(db.getAuRole());

					HttpSession session = request.getSession(true);
					if(session.getAttribute(AdminUserUtil.USER_SESSION_NAME) != null){
						session.removeAttribute(AdminUserUtil.USER_SESSION_NAME);
					}
					session.setAttribute(AdminUserUtil.USER_SESSION_NAME, um);
					
					mm.addAttribute("user", user);
					return "redirect:index";
				}else{
					mm.addAttribute("msg", "用户名或密码错误！");			
					return "admin/login";
				}				
			}else{
				mm.addAttribute("msg", "用户名或密码错误！");			
				return "admin/login";
			}
			 
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);				
			mm.addAttribute("msg", "登录过程出错，请重试！");
				return "admin/login";
		}
	}	
	
	@RequestMapping(value = {"logout"})
	public String exit(HttpServletRequest request){
		HttpSession session = request.getSession(true);
		session.removeAttribute(AdminUserUtil.USER_SESSION_NAME);
		
		return "admin/login";
	}
	
	
	@RequestMapping(value = {"index"})
	public String index(HttpServletRequest request){
		
		return "admin/index";
	}
}
