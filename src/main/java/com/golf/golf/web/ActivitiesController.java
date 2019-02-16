package com.golf.golf.web;

import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.service.MatchService;
import com.golf.golf.service.admin.AdminMatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 赛事活动
 * @author nmy
 * 2016年12月27日
 */
@Controller
@RequestMapping(value = "/activities")
public class ActivitiesController {
	private final static Logger logger = LoggerFactory.getLogger(ActivitiesController.class);

	@Autowired
	private MatchService matchService;
	@Autowired
	private AdminMatchService adminActivitiesService;

	/**
	 * 获取赛事活动列表
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
			logger.error("前台-获取赛事活动列表出错。"+ e );
			return "admin/error";
		}
		mm.addAttribute("pageInfo",pageInfo);
		return "activities/list";
	}

	/**
	 * 获取赛事活动详情
	 * @return
	 */
	@RequestMapping("getMatchDetail")
	public String getMatchDetail(ModelMap mm, Long matchId) {
		try {
            Map<String, Object> match = matchService.getMatchInfoById(matchId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("前台-获取赛事活动详情出错。" + e);
			return "admin/error";
		}
		return "activities/detail";
	}

}
