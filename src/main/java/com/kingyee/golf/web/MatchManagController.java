package com.kingyee.golf.web;

import com.google.gson.JsonElement;
import com.kingyee.common.gson.JsonWrapper;
import com.kingyee.common.util.TimeUtil;
import com.kingyee.golf.bean.TeamMatchInfoBean;
import com.kingyee.golf.common.security.UserUtil;
import com.kingyee.golf.db.MatchInfo;
import com.kingyee.golf.db.TeamInfo;
import com.kingyee.golf.db.UserInfo;
import com.kingyee.golf.service.MatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 赛事活动Controller
 * @author nmy
 * 2016年12月27日
 */
@Controller
@RequestMapping(value = "/matchManage")
public class MatchManagController {
	private final static Logger logger = LoggerFactory.getLogger(MatchManagController.class);

	@Autowired
	private MatchService matchService;
	/**
	 * 创建比赛
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "createMatch")
	public JsonElement createMatch(String teamId, String matchTitle, String address,
								   String matchTime, String applyEndTime, String digest, String isOpen) {
		try {
			TeamMatchInfoBean teamMatchInfoBean = new TeamMatchInfoBean();
			TeamInfo teamInfo = matchService.getTeamInfoById(teamId);
			teamMatchInfoBean.setTeamInfo(teamInfo);
			MatchInfo matchInfo = new MatchInfo();
			matchInfo.setTitle(matchTitle);
			matchInfo.setAddress(address);
			matchInfo.setIsOpen(Integer.parseInt(isOpen));
			matchInfo.setMatchTime(TimeUtil.stringToLong(TimeUtil.FORMAT_DATETIME,matchTime));
			matchInfo.setApplyEndTime(TimeUtil.stringToLong(TimeUtil.FORMAT_DATETIME,applyEndTime));
			matchInfo.setDigest(digest);
			matchInfo.setCreateTime(System.currentTimeMillis());
			UserInfo userInfo = UserUtil.getLoginUser().getUser();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("创建比赛时出错。" + e);
			return JsonWrapper.newErrorInstance("创建比赛时出错");
		}
		return JsonWrapper.newSuccessInstance();
	}

}
