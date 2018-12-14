package com.kingyee.golf.web;

import com.google.gson.JsonElement;
import com.kingyee.common.gson.JsonWrapper;
import com.kingyee.common.model.POJOPageInfo;
import com.kingyee.common.model.SearchBean;
import com.kingyee.golf.db.MatchInfo;
import com.kingyee.golf.service.MatchService;
import com.kingyee.golf.service.admin.AdminMatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 球队管理
 * @author nmy
 * 2016年12月27日
 */
@Controller
@RequestMapping(value = "/teamManage")
public class TeamManageController {
	private final static Logger logger = LoggerFactory.getLogger(TeamManageController.class);

	@Autowired
	private MatchService matchService;
	@Autowired
	private AdminMatchService adminActivitiesService;

	/**
	 * 获取我加入的球队
	 * @return
	 */
	@RequestMapping("list")
	public String list(ModelMap mm) {
		POJOPageInfo pageInfo = new POJOPageInfo<MatchInfo>(0 , 1);
		try {
			SearchBean searchBean = new SearchBean();
			pageInfo = adminActivitiesService.matchList(searchBean, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("前台-获取球队列表出错。"+ e );
			return "admin/error";
		}
		mm.addAttribute("pageInfo",pageInfo);
		return "activities/list";
	}

	/**
	 * 获取球队详情
	 * @return
	 */
	@RequestMapping("getMatchDetail")
	public String getMatchDetail(ModelMap mm, Long matchId) {
		try {
			MatchInfo matchInfo = matchService.getMatchInfoById(matchId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("前台-获取球队详情出错。" + e);
			return "admin/error";
		}
		return "activities/detail";
	}


	/**
	 * 创建球队
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "createTeam")
	public JsonElement createTeam(ModelMap mm, Long matchId) {
		try {
			MatchInfo matchInfo = matchService.getMatchInfoById(matchId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("创建球队时出错。" + e);
			return JsonWrapper.newErrorInstance("创建球队时出错");
		}
		return JsonWrapper.newSuccessInstance();
	}
}
