package com.golf.golf.web.admin;

import com.golf.common.Const;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.util.TimeUtil;
import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;
import com.golf.golf.service.admin.AdminMiniappUserService;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 后台微信用户管理
 * 
 * @author nmy
 * 2016年10月31日
 */
@Controller
@RequestMapping(value = "/admin/miniappUser")
public class AdminMiniappUserController {
	private final static Logger logger = LoggerFactory
			.getLogger(AdminMiniappUserController.class);

    @Autowired
    private AdminMiniappUserService logic;

	/**
	 * 微信用户列表
	 * @param mm
	 * @param keyword
	 * @param page
	 * @param rowsPerPage
	 * @return
	 */
	@RequestMapping("miniappUserList")
	public String miniappUserList(ModelMap mm, String keyword, String startDate, String endDate, Integer state, Integer page, Integer rowsPerPage){
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
			pageInfo = logic.getMiniappUserList(searchBean, pageInfo);
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
		return "admin/user/miniappuser_list";
	}

	/**
	 * 编辑——更新微信用户详细信息
	 * @param user
	 * @return
	 */
	@RequestMapping("miniappUserUpdate")
	public String miniappUserUpdate(UserInfo user){
		try{
			logic.updateUserInfo(user);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("后台管理——编辑——更新微信用户详细信息时出错。"+ e );
			return "error";
		}
		return "redirect:miniappUserList";
	}

	/**
	 * 编辑前台用户init
	 * @param mm
	 * @param userid
	 * @return
	 */
	@RequestMapping("miniappUserEditUI")
	public String miniappUserEditUI(ModelMap mm, Long userId){
		try{
			Map<String, Object> parp = logic.getMiniappUserById(userId);
			mm.addAttribute("userInfo",parp.get("userInfo"));
		}catch(Exception e){
			e.printStackTrace();
			logger.error("后台管理——获取微信用户信息时出错。"+ e );
			return "admin/error";
		}
		return "admin/user/miniappuser_edit";
	}

	/**
	 * 设置微信用户为赛事管理员
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = { "setMiniappUserToAdmin" })
	public JsonElement setMiniappUserToAdmin(Long userId){
		try {
			logic.setAdmin(userId);
		} catch (Exception e) {
			e.printStackTrace();
			String errMsg = "后台管理——设置微信用户为赛事管理员时出错。miniappUserId=" + userId;
			logger.error(errMsg+ e );
			return JsonWrapper.newErrorInstance(errMsg);
		}
		return JsonWrapper.newSuccessInstance();
	}

	/**
	 * 注销用户
	 * @param userId
	 * @return
	 */
	@RequestMapping("updateMiniappUserState")
	public String updateMiniappUserState(Long userId){
		try{
			UserInfo user = logic.getMiniappUser(userId);
			if(user.getUiIsValid() == 0){
				user.setUiIsValid(1);
			}else{
				user.setUiIsValid(0);
			}
			logic.update(user);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("后台管理——认领用户时出错。"+ e );
			return "admin/error";
		}
		return "redirect:miniappUserList";
	}

    /**
     * 认领用户
     * @param userId
     * @return
     */
    @RequestMapping("claimUser")
    public String claimUser(String ownerUserName,Long userId){
        try{
            logic.claimUser(ownerUserName,userId);

        }catch(Exception e){
            e.printStackTrace();
            logger.error("后台管理——注销/恢复微信用户时出错。"+ e );
            return "admin/error";
        }
        return "redirect:miniappUserList";
    }

}
