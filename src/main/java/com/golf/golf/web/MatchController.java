package com.golf.golf.web;

import com.google.gson.JsonElement;
import com.golf.common.Const;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.service.MatchService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 赛事活动Controller
 * @author nmy
 * 2016年12月27日
 */
@Controller
@RequestMapping(value = "/match")
public class MatchController {
	private final static Logger logger = LoggerFactory.getLogger(MatchController.class);

	@Autowired
	private MatchService matchService;

	/**
	 * 获取赛事活动列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMatchList")
	public JsonElement getMatchList(String page) {
		Integer nowPage = 1;
		if (StringUtils.isNotEmpty(page) && Integer.parseInt(page) > 0) {
			nowPage = Integer.parseInt(page);
		}
		POJOPageInfo pageInfo = new POJOPageInfo<MatchInfo>(Const.ROWSPERPAGE , nowPage);
		try {
			SearchBean searchBean = new SearchBean();
			pageInfo = matchService.getMatchList(searchBean, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("前台-获取赛事活动管理列表出错。"+ e );
			return JsonWrapper.newErrorInstance("前台-获取赛事活动管理列表出错。");
		}
		return JsonWrapper.newDataInstance(pageInfo);
	}

	/**
	 * 获取赛事活动详情
	 * @return
	 */
	@RequestMapping("getMatchDetail")
	public String getMatchDetail(ModelMap mm, Long matchId) {
		try {
			MatchInfo matchInfo = matchService.getMatchInfoById(matchId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("前台-获取赛事活动管理详情出错。" + e);
			return "admin/error";
		}
		return "activities/detail";
	}

}
