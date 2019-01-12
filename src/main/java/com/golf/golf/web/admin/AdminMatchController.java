package com.golf.golf.web.admin;

import com.golf.common.Const;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.service.admin.AdminMatchService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 赛事活动
 * @author nmy
 * 2016年12月27日
 */
@Controller
@RequestMapping(value = "/admin/match")
public class AdminMatchController {
	private final static Logger logger = LoggerFactory.getLogger(AdminMatchController.class);

    @Autowired
    private AdminMatchService adminMatchService;

	/**
	 * 赛事活动列表
	 * @return
	 */
	@RequestMapping("list")
	public String list(ModelMap mm, String keyword, String startDate, String endDate, Integer page,Integer rowsPerPage,
					   Integer type,Integer isOpen,Integer isDel,Integer state){
		if (page == null || page.intValue() == 0) {
			page = 1;
		}
		if (rowsPerPage == null || rowsPerPage.intValue() == 0) {
			rowsPerPage = Const.ROWSPERPAGE;
		}
		POJOPageInfo pageInfo = new POJOPageInfo<MatchInfo>(rowsPerPage , page);
		try {
			SearchBean searchBean = new SearchBean();
			if (StringUtils.isNotEmpty(keyword)) {
				searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
			}
			if (type != null) {
				searchBean.addParpField("type", type);
			}
			if (isOpen != null) {
				searchBean.addParpField("isOpen", isOpen);
			}
			if (isDel != null) {
				searchBean.addParpField("isDel", isDel);
			}
			pageInfo = adminMatchService.matchList(searchBean, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("后台管理-获取赛事活动列表时出错。"+ e );
			return "admin/error";
		}
		mm.addAttribute("pageInfo",pageInfo);
		mm.addAttribute("type",type);
		mm.addAttribute("keyword",keyword);
		mm.addAttribute("page",page);
		mm.addAttribute("rowsPerPage",rowsPerPage);
		mm.addAttribute("isOpen",isOpen);
		mm.addAttribute("isDel",isDel);
		mm.addAttribute("state",state);
		mm.addAttribute("startDate", startDate);
		mm.addAttribute("endDate", endDate);
		return "admin/activities/list";
	}


    /**
     * 新增赛事活动init
     * @return
     */
    @RequestMapping("addMatchIdUI")
    public String addMatchIdUI(){
        return "admin/match/add";
    }

	/**
	 * 新增赛事活动
	 * @return
	 */
	@RequestMapping("addMatch")
	public String addMatch(MatchInfo calendar){
		try{
			String a = calendar.getMiContent().replace("\"", "\'");
			calendar.setMiContent(a);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("新增赛事活动时出错。"+ e );
			return "admin/error";
		}
		return "redirect:list";
	}

    /**
     * 编辑赛事活动init
     * @param mm
     * @param matchId 活动id
     * @return
     */
    @RequestMapping("editMatchUI")
    public String editMatchUI(ModelMap mm, Long matchId){
		try{
			MatchInfo matchInfo = adminMatchService.getMatchById(matchId);
			mm.addAttribute("matchInfo",matchInfo);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("编辑赛事活动init出错。"+ e );
			return "admin/error";
		}
        return "admin/activities/edit";
    }

    /**
     * 编辑赛事活动-保存
     * @param actiivity
     * @return
     */
    @RequestMapping("calendarEdit")
    public String calendarEdit(MatchInfo actiivity){
        try{
			String a = actiivity.getMiContent().replace("\"", "\'");
			actiivity.setMiContent(a);
            adminMatchService.editMatch(actiivity);
        }catch(Exception e){
            e.printStackTrace();
			logger.error("编辑赛事活动时出错。"+ e );
            return "admin/error";
        }
        return "redirect:calendarList";
    }
}
