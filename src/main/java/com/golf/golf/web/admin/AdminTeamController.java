package com.golf.golf.web.admin;

import com.golf.common.Const;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.TeamInfo;
import com.golf.golf.db.TeamUserMapping;
import com.golf.golf.service.admin.AdminMatchService;
import com.golf.golf.service.admin.AdminTeamService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 球队管理
 * @author nmy
 * 2016年12月27日
 */
@Controller
@RequestMapping(value = "/admin/team")
public class AdminTeamController {
	private final static Logger logger = LoggerFactory.getLogger(AdminTeamController.class);

    @Autowired
    private AdminTeamService adminTeamService;

	/**
	 * 球队列表
	 * @return
	 */
	@RequestMapping("list")
	public String list(ModelMap mm, String keyword, String startDate, String endDate, Integer page,Integer rowsPerPage,Integer state){
		if (page == null || page.intValue() == 0) {
			page = 1;
		}
		if (rowsPerPage == null || rowsPerPage.intValue() == 0) {
			rowsPerPage = Const.ROWSPERPAGE;
		}
		POJOPageInfo pageInfo = new POJOPageInfo<Map<String, Object>>(rowsPerPage , page);
		try {
			SearchBean searchBean = new SearchBean();
			if (StringUtils.isNotEmpty(keyword)) {
				searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
			}
			if (state != null) {
				searchBean.addParpField("state", state);
			}
			pageInfo = adminTeamService.teamList(searchBean, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("后台管理-获取球队列表时出错。"+ e );
			return "admin/error";
		}
		mm.addAttribute("pageInfo",pageInfo);
		mm.addAttribute("keyword",keyword);
		mm.addAttribute("page",page);
		mm.addAttribute("rowsPerPage",rowsPerPage);
		mm.addAttribute("state",state);
		mm.addAttribute("startDate", startDate);
		mm.addAttribute("endDate", endDate);
		return "admin/team/list";
	}


    /**
     * 新增球队init
     * @return
     */
    @RequestMapping("addTeamIdUI")
    public String addTeamIdUI(){
        return "admin/team/add";
    }

	/**
	 * 新增球队
	 * @return
	 */
	@RequestMapping("addTeam")
	public String addTeam(TeamInfo teamInfo){
		try{
//			String a = calendar.getMiContent().replace("\"", "\'");
//			calendar.setMiContent(a);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("新增球队时出错。"+ e );
			return "admin/error";
		}
		return "redirect:list";
	}

    /**
	 * 编辑球队init
	 * @param mm
	 * @param teamId 球队id
	 * @return
	 */
	@RequestMapping("editTeamUI")
	public String editTeamUI(ModelMap mm, Long teamId){
		try{
			Map<String,Object> teamInfo = adminTeamService.getMatchInfoById(teamId);
			mm.addAttribute("teamInfo",teamInfo);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("编辑球队init出错。"+ e );
			return "admin/error";
		}
		return "admin/team/edit";
	}

	/**
	 * 编辑球队-保存
	 * @return
	 */
	@RequestMapping("teamEdit")
	public String teamEdit(TeamInfo teamInfo){
		try{
			adminTeamService.teamEdit(teamInfo);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("后台管理——编辑球队时出错。"+ e );
			return "admin/error";
		}
		return "redirect:list";
	}

	/**
	 * 修改状态
	 * @param teamId 球队id
	 * @return
	 */
	@RequestMapping("updateTeamState")
	public String updateTeamState(Long teamId){
		try{
			adminTeamService.updateTeamState(teamId);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("修改球队状态出错。"+ e );
			return "admin/error";
		}
		return "redirect:list";
	}

    /**
     * 删除
     * @param teamId 球队id
     * @return
     */
    @RequestMapping("delTeam")
    public String delTeam(Long teamId){
        try{
            adminTeamService.delTeam(teamId);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("删除球队出错。"+ e );
            return "admin/error";
        }
        return "redirect:list";
    }
}
