package com.golf.golf.web;

import com.golf.common.Const;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.bean.MatchUserGroupMappingBean;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.UserInfo;
import com.golf.golf.service.MatchService;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 比赛报名Controller
 * @author nmy
 * 2016年12月27日
 */
@Controller
@RequestMapping(value = "/match/join")
public class MatchJoinController {
    private final static Logger logger = LoggerFactory.getLogger(MatchJoinController.class);
    private String errmsg;

    @Autowired
    private MatchService matchService;


    /**
     * 比赛列表 我能报名的
     * @param page 翻页
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
            searchBean.addParpField("joinEndTime", System.currentTimeMillis());
            pageInfo = matchService.getMatchList(searchBean, pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            errmsg = "前台-获取可报名的比赛列表出错。";
            logger.error(errmsg+ e );
            return JsonWrapper.newErrorInstance(errmsg);
        }
        return JsonWrapper.newDataInstance(pageInfo);
    }

    /**
     * 报名——获取比赛赛长和分组
     * @return
     */
    @ResponseBody
    @RequestMapping("getMatchGroupList")
    public JsonElement getMatchGroupList(Long matchId) {
        try {
            List<MatchUserGroupMappingBean> mappingList = matchService.getMatchGroupMappingList(matchId);
            return JsonWrapper.newDataInstance(mappingList);
        } catch (Exception e) {
            errmsg = "前台-获取报名的比赛活动分组详情出错。";
            e.printStackTrace();
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }

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
            errmsg = "前台-保存分组时出错。";
            e.printStackTrace();
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }

    /**
     * 球友报名 -加入本组
     * @return
     */
    @ResponseBody
    @RequestMapping("joinMatchGroup")
    public JsonElement joinMatchGroup(Long matchId, Long groupId) {
        try {
			matchService.updateMatchGroup(matchId, groupId, UserUtil.getUserId());
            return JsonWrapper.newSuccessInstance();
        } catch (Exception e) {
            errmsg = "前台-报名-加入本组时出错。";
            e.printStackTrace();
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }

	/**
	 * 球友取消报名-退出本组
	 * @return
	 */
	@ResponseBody
	@RequestMapping("quitMatchGroup")
	public JsonElement quitMatchGroup(Long matchId, Long groupId) {
		try {
			matchService.quitMatchGroup(matchId, groupId, UserUtil.getUserId(), null);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			errmsg = "前台-报名-球友退出本组时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

    /**
     * 赛长——审核报名用户，将用户加入该组
     * @return
     */
    @ResponseBody
    @RequestMapping("auditUser")
    public JsonElement auditUser(Long matchId, Long groupId) {
        try {
            matchService.quitMatchGroup(matchId, groupId, UserUtil.getUserId(), null);
            return JsonWrapper.newSuccessInstance();
        } catch (Exception e) {
            errmsg = "前台-报名-赛长审核报名用户，将用户加入该组时出错。";
            e.printStackTrace();
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }


    /**
     * 报名——获取参赛球队 和 比赛详情
     * @return
     */
    @ResponseBody
    @RequestMapping("getMatchDetail")
    public JsonElement getMatchDetail(Long matchId) {
        try {
            Map<String, Object> matchInfo = matchService.getMatchInfoById(matchId);
            return JsonWrapper.newDataInstance(matchInfo);
        } catch (Exception e) {
            errmsg = "前台-获取比赛活动详情出错。";
            e.printStackTrace();
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }


}
