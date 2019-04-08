package com.golf.golf.web;

import com.golf.common.Const;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.service.TeamService;
import com.google.gson.JsonElement;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping(value = "/team")
public class TeamController {
	private final static Logger logger = LoggerFactory.getLogger(TeamController.class);
    private String errmsg;

    @Autowired
    private TeamService teamService;


    /**
     * 获取球队列表
     * @param page 分页
     * @param type 1：我加入的球队  0：所有球队
     * @param keyword 球队名称
     * @return
     */
    @ResponseBody
    @RequestMapping("getTeamList")
    public JsonElement getTeamList(Integer page, Integer type, String keyword) {
        Integer nowPage = 1;
        if (page > 0) {
            nowPage = page;
        }
        POJOPageInfo pageInfo = new POJOPageInfo<Object[]>(Const.ROWSPERPAGE , nowPage);
        try {
            SearchBean searchBean = new SearchBean();
            if(StringUtils.isNotEmpty(keyword)){
                searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
            }
            if(type == null){
                pageInfo = teamService.getTeamList(searchBean, pageInfo);
            }else if(type == 0){
                searchBean.addParpField("userId", UserUtil.getUserId());
                pageInfo = teamService.getMyTeamList(searchBean, pageInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errmsg = "前台-获取球队列表出错。";
            logger.error(errmsg+ e );
            return JsonWrapper.newErrorInstance(errmsg);
        }
        return JsonWrapper.newDataInstance(pageInfo);
    }

	/**
	 * 获取球队详情
	 * @return
	 */
	@RequestMapping("getMatchDetail")
	public String getMatchDetail(Long teamId) {
		try {
//			MatchInfo matchInfo = t.getMatchInfoById(teamId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("前台-获取球队详情出错。" + e);
			return "admin/error";
		}
		return "activities/detail";
	}

    /**
     * 球队队长审核入队申请
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "auditJoinUser")
    public JsonElement auditJoinUser(Long teamId, String userIds) {
        try {
            teamService.auditJoinUser(teamId, userIds);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("球队队长审核入队申请时出错。" + e);
            return JsonWrapper.newErrorInstance("球队队长审核入队申请时出错。");
        }
        return JsonWrapper.newSuccessInstance();
    }

    /**
     * 球队队长删除队员
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "delUserFromTeam")
    public JsonElement delUserFromTeam(Long teamId, String userIds) {
        try {
            teamService.delUserFromTeam(teamId, userIds);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("球队队长删除队员时出错。" + e);
            return JsonWrapper.newErrorInstance("球队队长删除队员时出错。");
        }
        return JsonWrapper.newSuccessInstance();
    }

    /**
     * 球队队长点击队员头像将其指定为队长（和邀请记分设计一样）
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "setTeamCaption")
    public JsonElement setTeamCaption(Long teamId, Long userId) {
        try {
            teamService.setTeamCaption(teamId, userId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("球队队长指定其他人为队长时出错。" + e);
            return JsonWrapper.newErrorInstance("球队队长指定其他人为队长时出错。");
        }
        return JsonWrapper.newSuccessInstance();
    }


    /**
     * 创建球队
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "createTeam")
    public JsonElement createTeam( ) {
        try {
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("创建球队时出错。" + e);
            return JsonWrapper.newErrorInstance("创建球队时出错。");
        }
        return JsonWrapper.newSuccessInstance();
    }






















    /**
     * 申请加入球队
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "joinTeam")
    public JsonElement joinTeam(Long teamId) {
        try {
            teamService.joinTeam(teamId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("申请加入球队时出错。" + e);
            return JsonWrapper.newErrorInstance("申请加入球队时出错。");
        }
        return JsonWrapper.newSuccessInstance();
    }

    /**
     * 退出球队
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "quitTeam")
    public JsonElement quitTeam(Long teamId) {
        try {
            teamService.quitTeam(teamId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("退出球队时出错。" + e);
            return JsonWrapper.newErrorInstance("退出球队时出错。");
        }
        return JsonWrapper.newSuccessInstance();
    }
}
