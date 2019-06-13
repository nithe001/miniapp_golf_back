package com.golf.golf.web.admin;

import com.golf.common.Const;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.util.TimeUtil;
import com.golf.golf.db.AdminUser;
import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;
import com.golf.golf.service.admin.AdminUserService;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 后台微信用户管理
 * 
 * @author nmy
 * 2016年10月31日
 */
@Controller
@RequestMapping(value = "/admin/wechatUser")
public class AdminWechatUserController {
	private final static Logger logger = LoggerFactory
			.getLogger(AdminWechatUserController.class);

    @Autowired
    private AdminUserService logic;

	/**
	 * 微信用户列表
	 * @param mm
	 * @param keyword
	 * @param page
	 * @param rowsPerPage
	 * @return
	 */
	@RequestMapping("wechatUserList")
	public String wechatUserList(ModelMap mm, String keyword, String startDate, String endDate, Integer state, Integer page, Integer rowsPerPage){
		if (page == null || page.intValue() == 0) {
			page = 1;
		}
		if (rowsPerPage == null || rowsPerPage.intValue() == 0) {
			rowsPerPage = Const.ROWSPERPAGE;
		}
		POJOPageInfo pageInfo = new POJOPageInfo<Map<String,Object>>(rowsPerPage , page);
		try {
			SearchBean searchBean = new SearchBean();
			if (StringUtils.isNotEmpty(keyword)) {
				searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
			}
			searchBean.addParpField("startDate", TimeUtil.stringToLong(startDate, TimeUtil.FORMAT_DATE));
			searchBean.addParpField("endDate", TimeUtil.stringToLong(endDate, TimeUtil.FORMAT_DATE));
			searchBean.addParpField("state", state);
			pageInfo = logic.getWechatUserList(searchBean, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("后台管理-获取微信用户列表信息出错。"+ e );
			return "error";
		}
		mm.addAttribute("pageInfo",pageInfo);
		mm.addAttribute("keyword",keyword);
		mm.addAttribute("startDate", startDate);
		mm.addAttribute("endDate", endDate);
		mm.addAttribute("page",page);
		mm.addAttribute("state", state);
		mm.addAttribute("rowsPerPage",rowsPerPage);
		return "admin/user/wechatuser_list";
	}

	/**
	 * 编辑——更新微信用户详细信息
	 * @param user
	 * @return
	 */
	@RequestMapping("wechatUserUpdate")
	public String wechatUserUpdate(WechatUserInfo wechatUserInfo, UserInfo user){
		try{
			logic.updateUserInfo(wechatUserInfo, user);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("后台管理——编辑——更新微信用户详细信息时出错。"+ e );
			return "error";
		}
		return "redirect:wechatUserList";
	}

	/**
	 * 编辑前台用户init
	 * @param mm
	 * @param wechatId
	 * @return
	 */
	@RequestMapping("wechatUserEditUI")
	public String wechatUserEditUI(ModelMap mm, Long wechatId){
		try{
			Map<String, Object> parp = logic.getWechatUserById(wechatId);
			mm.addAttribute("wechatUser",parp.get("wechatUser"));
			mm.addAttribute("userInfo",parp.get("userInfo"));
		}catch(Exception e){
			e.printStackTrace();
			logger.error("后台管理——获取微信用户信息时出错。"+ e );
			return "admin/error";
		}
		return "admin/user/wechatuser_edit";
	}

	/**
	 * 设置微信用户为赛事管理员
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = { "setWechatUserToAdmin" })
	public JsonElement setWechatUserToAdmin(Long wechatUserId){
		try {
			logic.setAdmin(wechatUserId);
		} catch (Exception e) {
			e.printStackTrace();
			String errMsg = "后台管理——设置微信用户为赛事管理员时出错。wechatUserId=" + wechatUserId;
			logger.error(errMsg+ e );
			return JsonWrapper.newErrorInstance(errMsg);
		}
		return JsonWrapper.newSuccessInstance();
	}

	/**
	 * 注销微信用户
	 * @param wechatUserId
	 * @return
	 */
	@RequestMapping("updateWechatUserState")
	public String updateWechatUserState(Long wechatUserId){
		try{
			WechatUserInfo wechatUser = logic.getWechatUser(wechatUserId);
			if(wechatUser.getWuiIsValid() == 0){
				wechatUser.setWuiIsValid(1);
			}else{
				wechatUser.setWuiIsValid(0);
			}
			logic.update(wechatUser);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("后台管理——注销/恢复微信用户时出错。"+ e );
			return "admin/error";
		}
		return "redirect:wechatUserList";
	}

}
