package com.golf.golf.web.admin;

import com.golf.common.Const;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.util.TimeUtil;
import com.golf.golf.db.AdminUser;
import com.golf.golf.db.ParkInfo;
import com.golf.golf.db.UserInfo;
import com.golf.golf.service.admin.AdminParkService;
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
 * 球场管理
 * @author nmy
 * 2016年10月31日
 */
@Controller
@RequestMapping(value = "/admin/park")
public class AdminParkController {
	private final static Logger logger = LoggerFactory
			.getLogger(AdminParkController.class);

    @Autowired
    private AdminParkService adminParkService;

    /**
     * 球场列表
     * @param mm
     * @param keyword
     * @param page
     * @param rowsPerPage
     * @return
     */
    @RequestMapping("list")
    public String list(ModelMap mm, String keyword, Integer state, Integer page, Integer rowsPerPage){
		if (page == null || page.intValue() == 0) {
            page = 1;
        }
        if (rowsPerPage == null || rowsPerPage.intValue() == 0) {
            rowsPerPage = Const.ROWSPERPAGE;
        }
        POJOPageInfo pageInfo = new POJOPageInfo<ParkInfo>(rowsPerPage , page);
        try {
            SearchBean searchBean = new SearchBean();
            if (StringUtils.isNotEmpty(keyword)) {
                searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
            }
            searchBean.addParpField("state", state);
            pageInfo = adminParkService.getParkList(searchBean, pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("后台管理-获取球场列表信息出错。"+ e );
            return "error";
        }
        mm.addAttribute("pageInfo",pageInfo);
        mm.addAttribute("keyword",keyword);
        mm.addAttribute("page",page);
        mm.addAttribute("state", state);
        mm.addAttribute("rowsPerPage",rowsPerPage);
        return "admin/park/park_list";
    }


    /**
     * 新增init
     * @return
     */
    @RequestMapping("parkAddUI")
    public String parkAddUI(){
        return "admin/park/park_add";
    }

    /**
     * 新增球场
     * @param parkInfo
     * @return
     */
    @RequestMapping("parkAdd")
    public String parkAdd(ParkInfo parkInfo){
        try{
            adminParkService.save(parkInfo);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("保存球场时出错。"+ e );
            return "error";
        }
        return "redirect:list";
    }

    /**
     * 编辑init
     * @param mm
     * @param parkId
     * @return
     */
    @RequestMapping("parkEditUI")
    public String editUI(ModelMap mm, Long parkId){
        try{
            ParkInfo parkInfo = adminParkService.getById(parkId);
            mm.addAttribute("parkInfo",parkInfo);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("获取球场信息时出错。"+ e );
            return "admin/error";
        }
        return "admin/park/park_edit";
    }

    /**
     * 编辑保存
     * @param parkInfo
     * @return
     */
    @RequestMapping("parkEdit")
    public String edit(ParkInfo parkInfo){
        try{
            adminParkService.edit(parkInfo);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("编辑球场信息时出错。"+ e );
            return "admin/error";
        }
        return "redirect:list";
    }

    /**
     * 查看球场名是否已经存在
     * @return
     */
    @ResponseBody
    @RequestMapping(value = { "exsitName" }, method = RequestMethod.GET)
    public JsonElement checkName(String fieldValue, String fieldId){
        Object[] array = new Object[2];
        try {
            Boolean flag =adminParkService.checkName(fieldValue);
            array[0] = fieldId;
            array[1] = !flag;
        } catch (Exception e) {
            e.printStackTrace();
            String errMsg = "检查球场是否存在时出错。usCode=" + fieldValue;
            logger.error(errMsg+ e );
            // 返回json格式必须是[字段ID,布尔类型,显示信息]这种格式
            Object[] array_ = new Object[] { fieldId, false, errMsg };
            return JsonWrapper.newJson(array_);
        }
        return JsonWrapper.newJson(array);
    }

    /**
     * 注销
     * @param parkId
     * @return
     */
    @RequestMapping("parkReset")
    public String reset(Long parkId){
        try{
            adminParkService.update(parkId);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("注销/恢复球场时出错。"+ e );
            return "admin/error";
        }
        return "redirect:list";
    }
}
