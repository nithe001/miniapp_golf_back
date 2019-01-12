package com.golf.golf.web;

import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.TeamInfo;
import com.golf.golf.db.UserInfo;
import com.google.gson.JsonElement;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.util.TimeUtil;
import com.golf.golf.bean.TeamMatchInfoBean;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.service.MatchService;
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
			matchInfo.setMiTitle(matchTitle);
			matchInfo.setMiParkId(null);
			matchInfo.setMiParkName(address);
			matchInfo.setMiMatchOpenType(Integer.parseInt(isOpen));
			matchInfo.setMiJoinOpenType(Integer.parseInt(isOpen));
			matchInfo.setMiMatchTime(TimeUtil.stringToLong(TimeUtil.FORMAT_DATETIME,matchTime));
			matchInfo.setMiApplyEndTime(TimeUtil.stringToLong(TimeUtil.FORMAT_DATETIME,applyEndTime));
			matchInfo.setMiDigest(digest);
			matchInfo.setMiCreateTime(System.currentTimeMillis());
			matchInfo.setMiCreateUserId(UserUtil.getUserId());
			matchInfo.setMiCreateUserName(UserUtil.getShowName());
			UserInfo userInfo = UserUtil.getLoginUser().getUser();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("创建比赛时出错。" + e);
			return JsonWrapper.newErrorInstance("创建比赛时出错");
		}
		return JsonWrapper.newSuccessInstance();
	}

}
