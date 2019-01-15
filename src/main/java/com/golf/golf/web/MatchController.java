package com.golf.golf.web;

import com.golf.common.Const;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.UserInfo;
import com.golf.golf.service.MatchService;
import com.golf.golf.service.UserService;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 比赛活动Controller
 * @author nmy
 * 2016年12月27日
 */
@Controller
@RequestMapping(value = "/match")
public class MatchController {
	private final static Logger logger = LoggerFactory.getLogger(MatchController.class);
	private String errmsg;

	@Autowired
	private MatchService matchService;
	@Autowired
    private UserService userService;


	/**
	 * 比赛列表
	 * @param page 翻页
	 * @param type null：全部比赛  其他：我参加的比赛
	 * @param keyword 搜索内容
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMatchList")
	public JsonElement getMatchList(String page, Integer type, String keyword) {
		Integer nowPage = 1;
		if (StringUtils.isNotEmpty(page) && Integer.parseInt(page) > 0) {
			nowPage = Integer.parseInt(page);
		}
		POJOPageInfo pageInfo = new POJOPageInfo<MatchInfo>(Const.ROWSPERPAGE , nowPage);
		try {
			SearchBean searchBean = new SearchBean();
			if(StringUtils.isNotEmpty(keyword)){
				searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
			}
			if(type == null){
                pageInfo = matchService.getMatchList(searchBean, pageInfo);
			}else if(type == 0){
                searchBean.addParpField("userId", UserUtil.getUserId());
                pageInfo = matchService.getMyMatchList(searchBean, pageInfo);
            }
		} catch (Exception e) {
			e.printStackTrace();
			errmsg = "前台-获取比赛列表出错。";
			logger.error(errmsg+ e );
			return JsonWrapper.newErrorInstance(errmsg);
		}
		return JsonWrapper.newDataInstance(pageInfo);
	}

	/**
	 * 获取比赛分组
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMatchGroupList")
	public JsonElement getMatchGroupList(Long matchId) {
		try {
			Map<String, Object> matchMap = matchService.getMatchGroupList(matchId);
			return JsonWrapper.newDataInstance(matchMap);
		} catch (Exception e) {
			errmsg = "前台-获取比赛活动分组详情出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}


    /**
     * 点击围观用户头像 获取详细信息
     * @return
     */
    @ResponseBody
    @RequestMapping("getUserInfoById")
    public JsonElement getUserInfoById(Long userId) {
        try {
            UserInfo userInfo = userService.getUserInfoById(userId);
            return JsonWrapper.newDataInstance(userInfo);
        } catch (Exception e) {
            errmsg = "前台-根据用户id获取用户信息时出错。userId="+userId;
            e.printStackTrace();
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }

    /**
     * 点击“邀请记分”
     * @return
     */
    @ResponseBody
    @RequestMapping("saveUserScoreMapping")
    public JsonElement saveUserScoreMapping(Long matchId, Long groupId, Long scorerId) {
        try {
            matchService.saveUserScoreMapping(matchId, groupId, scorerId);
            return JsonWrapper.newSuccessInstance();
        } catch (Exception e) {
            errmsg = "前台-根据邀请用户记分时出错。记分人id="+scorerId;
            e.printStackTrace();
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }

    /**
     * 点击组内用户头像，判断是否能给该用户记分 跳转记分卡页面
     * @param matchId 比赛id
     * @param groupId 本赛分组id
     * @param matchUserId 被记分人id
     * @return
     */
    @ResponseBody
    @RequestMapping("addScoreInit")
    public JsonElement addScoreInit(Long matchId, Long groupId, Long matchUserId) {
        try {
            SearchBean searchBean = new SearchBean();
            searchBean.addParpField("keyword", matchId);
            searchBean.addParpField("groupId", groupId);
            searchBean.addParpField("matchUserId", matchUserId);
            searchBean.addParpField("scorerId", UserUtil.getUserId());
            return JsonWrapper.newDataInstance(matchService.getScoreType(searchBean));
        } catch (Exception e) {
            errmsg = "前台-跳转记分卡页面时出错。userId="+UserUtil.getUserId();
            e.printStackTrace();
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }

	/**
	 * 获取比赛详情
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMatchDetail")
	public JsonElement getMatchDetail(Long matchId) {
		try {
			MatchInfo matchInfo = matchService.getMatchInfoById(matchId);
			return JsonWrapper.newDataInstance(matchInfo);
		} catch (Exception e) {
			errmsg = "前台-获取比赛活动详情出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 获取本组比赛结果详情
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMatchScoreByGroupId")
	public JsonElement getMatchScoreByGroupId(Long groupId) {
		try {
			MatchInfo matchInfo = matchService.getMatchInfoById(groupId);
			return JsonWrapper.newDataInstance(matchInfo);
		} catch (Exception e) {
			errmsg = "前台-获取本组比赛结果详情出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 获取整组比赛结果详情
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMatchScoreByMatchId")
	public JsonElement getMatchScoreByMatchId(Long matchId) {
		try {
			MatchInfo matchInfo = matchService.getMatchInfoById(matchId);
			return JsonWrapper.newDataInstance(matchInfo);
		} catch (Exception e) {
			errmsg = "前台-获取整组比赛结果详情出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}


}
