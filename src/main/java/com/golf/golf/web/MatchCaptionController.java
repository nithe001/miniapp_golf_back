package com.golf.golf.web;

import com.golf.common.gson.JsonWrapper;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.db.MatchUserGroupMapping;
import com.golf.golf.service.MatchService;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 赛长Controller
 * @author nmy
 * 2016年12月27日
 */
@Controller
@RequestMapping(value = "/match/caption")
public class MatchCaptionController {
    private final static Logger logger = LoggerFactory.getLogger(MatchCaptionController.class);
    private String errmsg;

    @Autowired
    private MatchService matchService;

    /**
     * 添加比赛分组初始化  弹框？新页面
     * @return
     */
    @ResponseBody
    @RequestMapping("addMatchGroupInit")
    public JsonElement addMatchGroupInit(Long matchId) {
        return JsonWrapper.newDataInstance(matchId);
    }

    /**
     * 保存分组
     * @return
     */
    @ResponseBody
    @RequestMapping("addMatchGroup")
    public JsonElement addMatchGroup(Long matchId, String groupName) {
        try {
            matchService.addMatchGroup(matchId, groupName);
            return JsonWrapper.newSuccessInstance();
        } catch (Exception e) {
            errmsg = "前台-获取比赛活动分组详情出错。";
            e.printStackTrace();
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }


    /**
     * 获取临时分组中的球友
	 * @param matchId 比赛id
	 * @param groupId 比赛分组id
     * @return
     */
    @ResponseBody
    @RequestMapping("getUserByTemporary")
    public JsonElement getUserByTemporary(Long matchId, Long groupId) {
        try {
            List<MatchUserGroupMapping> userInfo = matchService.getUserByTemporary(matchId, groupId);
            return JsonWrapper.newDataInstance(userInfo);
        } catch (Exception e) {
            errmsg = "前台-赛长获取临时分组中的球友时出错。";
            e.printStackTrace();
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }

	/**
	 * 赛长添加多个球友 -加入本组
	 * @return
	 */
	@ResponseBody
	@RequestMapping("joinMatchGroup")
	public JsonElement joinMatchGroup(Long matchId, Long groupId, String userIds) {
		try {
			//TODO 权限校验
			matchService.updateMatchGroupByCaption(matchId, groupId, userIds);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			errmsg = "前台-赛长添加多个球友加入本组时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 赛长删除某组的多个球友到临时组 - 退出本组
	 * @return
	 */
	@ResponseBody
	@RequestMapping("quitMatchGroup")
	public JsonElement quitMatchGroup(Long matchId, Long groupId, String userIds) {
		try {
			//TODO 赛长权限校验
			matchService.quitMatchGroup(matchId, groupId, UserUtil.getUserId(), userIds);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			errmsg = "前台-报名-赛长删除某组的多个球友到临时组时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

}
