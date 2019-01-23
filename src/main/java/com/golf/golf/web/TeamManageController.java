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
@RequestMapping(value = "/teamManage")
public class TeamManageController {
	private final static Logger logger = LoggerFactory.getLogger(TeamManageController.class);
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
	public String getMatchDetail(ModelMap mm, Long matchId) {
		try {
//			MatchInfo matchInfo = t.getMatchInfoById(matchId);
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
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("创建球队时出错。" + e);
			return JsonWrapper.newErrorInstance("创建球队时出错");
		}
		return JsonWrapper.newSuccessInstance();
	}
}
