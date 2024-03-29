package com.golf.golf.web.admin;

import com.golf.common.Const;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.AdminUser;
import com.golf.golf.service.admin.AdminWechatUserService;
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

/**
 * 管理员用户管理
 * @author nmy
 * 2016年10月31日
 */
@Controller
@RequestMapping(value = "/admin/user")
public class AdminUserController {
	private final static Logger logger = LoggerFactory
			.getLogger(AdminUserController.class);

    @Autowired
    private AdminWechatUserService logic;

    /**
     * 管理员用户列表
     * @param mm
     * @param keyword
     * @param page
     * @param rowsPerPage
     * @return
     */
    @RequestMapping("adminUserList")
    public String list(ModelMap mm, String keyword, Integer State, Integer page, Integer rowsPerPage){
		if (page == null || page.intValue() == 0) {
            page = 1;
        }
        if (rowsPerPage == null || rowsPerPage.intValue() == 0) {
            rowsPerPage = Const.ROWSPERPAGE;
        }
        POJOPageInfo pageInfo = new POJOPageInfo<AdminUser>(rowsPerPage , page);
        try {
            SearchBean searchBean = new SearchBean();
            if (StringUtils.isNotEmpty(keyword)) {
                searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
            }
            searchBean.addParpField("State", State);
            pageInfo = logic.getAdminUserList(searchBean, pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("后台管理——获取管理员用户列表信息出错。"+ e );
            return "error";
        }
        mm.addAttribute("pageInfo",pageInfo);
        mm.addAttribute("keyword",keyword);
        mm.addAttribute("page",page);
        mm.addAttribute("State", State);
        mm.addAttribute("rowsPerPage",rowsPerPage);
        return "admin/user/adminuser_list";
    }

    /**
     * 新增init
     * @return
     */
    @RequestMapping("userAddUI")
    public String addUI(){
        return "admin/user/adminuser_add";
    }

    /**
     * 新增用户
     * @param user
     * @return
     */
    @RequestMapping("userAdd")
    public String add(AdminUser user){
        try{
            logic.save(user);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("后台管理——保存管理员用户时出错。"+ e );
            return "error";
        }
        return "redirect:adminUserList";
    }

    /**
     * 编辑init
     * @param mm
     * @param id
     * @return
     */
    @RequestMapping("userEditUI")
    public String editUI(ModelMap mm, Long id){
        try{
            AdminUser user = logic.getById(id);
            user.setAuPassword(null);
            mm.addAttribute("user",user);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("后台管理——获取管理员用户信息时出错。"+ e );
            return "admin/error";
        }
        return "admin/user/adminuser_edit";
    }

    /**
     * 编辑保存
     * @param user
     * @return
     */
    @RequestMapping("userEdit")
    public String edit(AdminUser user){
        try{
            logic.edit(user);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("后台管理——编辑管理员用户信息时出错。"+ e );
            return "admin/error";
        }
        return "redirect:adminUserList";
    }

    /**
     * 查看用户名是否已经存在
     * @return
     */
    @ResponseBody
    @RequestMapping(value = { "exsitUser" }, method = RequestMethod.GET)
    public JsonElement checkName(String fieldValue, String fieldId){
        Object[] array = new Object[2];
        try {
            Boolean flag =logic.checkName(fieldValue);
            array[0] = fieldId;
            array[1] = !flag;
        } catch (Exception e) {
            e.printStackTrace();
            String errMsg = "检查用户是否存在时出错。usCode=" + fieldValue;
            logger.error(errMsg+ e );
            // 返回json格式必须是[字段ID,布尔类型,显示信息]这种格式
            Object[] array_ = new Object[] { fieldId, false, errMsg };
            return JsonWrapper.newJson(array_);
        }
        return JsonWrapper.newJson(array);
    }

    /**
     * 注销
     * @param id
     * @return
     */
    @RequestMapping("userReset")
    public String reset(Long id){
        try{
            AdminUser user = logic.getById(id);
            if(user.getAuIsValid() == 0){
                user.setAuIsValid(1);
            }else{
                user.setAuIsValid(0);
            }
            logic.update(user);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("后台管理——注销/恢复管理员用户时出错。"+ e );
            return "admin/error";
        }
        return "redirect:adminUserList";
    }

}
