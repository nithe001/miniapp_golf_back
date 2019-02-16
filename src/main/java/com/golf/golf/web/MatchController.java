package com.golf.golf.web;

import com.golf.common.Const;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.ParkInfo;
import com.golf.golf.db.ParkPartition;
import com.golf.golf.db.UserInfo;
import com.golf.golf.service.MatchService;
import com.golf.golf.service.TeamService;
import com.golf.golf.service.UserService;
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
	@Autowired
    private TeamService teamService;


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
			}else{
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
	 * 点击进入比赛详情——获取围观用户列表和比赛分组
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMatchInfo")
	public JsonElement getMatchInfo(Long matchId) {
		try {
			Map<String, Object> matchMap = matchService.getMatchInfo(matchId);
			return JsonWrapper.newDataInstance(matchMap);
		} catch (Exception e) {
			errmsg = "前台-点击进入比赛详情-获取围观用户列表和比赛分组时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}


    /**
     * 点击围观用户头像 获取详细信息
     * 详细资料 只有是队友且该球队要求 详细资料时才可见
     * @return
     */
    @ResponseBody
    @RequestMapping("getUserInfoById")
    public JsonElement getUserInfoById(Long userId) {
        try {
            //是否可见
            if(userService.userInfoIsOpen(userId)){
                UserInfo userInfo = userService.getUserInfoById(userId);
                return JsonWrapper.newDataInstance(userInfo);
            }
            return JsonWrapper.newErrorInstance("抱歉，您无权查看该用户信息。");
        } catch (Exception e) {
            errmsg = "前台-根据用户id获取用户信息时出错。userId="+userId;
            e.printStackTrace();
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }

    /**
     * 点击“邀请记分”——生成二维码，并显示，对方扫码进入记分页面
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
	 * 获取本组比赛结果详情
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMatchScoreByGroupId")
	public JsonElement getMatchScoreByGroupId(Long groupId) {
		try {
            Map<String, Object> matchInfo = matchService.getMatchInfoById(groupId);
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
            Map<String, Object> matchInfo = matchService.getMatchInfoById(matchId);
			return JsonWrapper.newDataInstance(matchInfo);
		} catch (Exception e) {
			errmsg = "前台-获取整组比赛结果详情出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}













    /**
     * 创建比赛—选择参赛范围——查询球队列表
     * 参赛范围处不选球队为公开赛
     * 选多个球队为队际赛
     * 弹出页面从所有球队中选择，可以搜索
     * @return
     */
    @ResponseBody
    @RequestMapping("getTeamList")
    public JsonElement getTeamList(Integer page, String keyword) {
        Integer nowPage = 1;
        if (page > 0) {
            nowPage = page;
        }
        try {
            SearchBean searchBean = new SearchBean();
            if(StringUtils.isNotEmpty(keyword)){
                searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
            }
            POJOPageInfo pageInfo = new POJOPageInfo<Object[]>(Const.ROWSPERPAGE , nowPage);
            pageInfo = teamService.getTeamList(searchBean, pageInfo);
            return JsonWrapper.newDataInstance(pageInfo);
        } catch (Exception e) {
            errmsg = "前台-创建比赛—选择参赛范围——查询球队列表时出错。";
            e.printStackTrace();
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }

    /**
     * 创建比赛—获取球场列表
     * @param page 翻页
     * @param keyword 搜索关键字
     * @param type 0:附近 1：区域
     * @param regionName 区域名称
     * @return
     */
    @ResponseBody
    @RequestMapping("getParkList")
    public JsonElement getParkList(Integer page, String keyword, Integer type, String regionName) {
        Integer nowPage = 1;
        if (page > 0) {
            nowPage = page;
        }
        try {
            SearchBean searchBean = new SearchBean();
            if(StringUtils.isNotEmpty(keyword)){
                searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
            }
            POJOPageInfo pageInfo = new POJOPageInfo<ParkInfo>(Const.ROWSPERPAGE , nowPage);
            if(type == null){
                //全部
                pageInfo = teamService.getParkList(searchBean, pageInfo);
            }else if(type == 0){
                //附近
                pageInfo = teamService.getParkListNearby(searchBean, pageInfo);
            }else if(type == 1){
                //区域
                pageInfo.setRowsPerPage(0);
                pageInfo.setNowPage(1);
                pageInfo = teamService.getParkListByRegion(searchBean, pageInfo);
            }else if(StringUtils.isNotEmpty(regionName)){
                //该区域下的所有球场
                searchBean.addParpField("regionName", regionName);
                pageInfo = teamService.getParkListByRegionName(searchBean, pageInfo);
            }
            return JsonWrapper.newDataInstance(pageInfo);
        } catch (Exception e) {
            errmsg = "前台-创建比赛—选择球场—查询球场列表时出错。";
            e.printStackTrace();
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }

    /**
     * 创建比赛—点击球场-获取分区（前九洞 后九洞）
     * @return
     */
    @ResponseBody
    @RequestMapping("getParkZoneAndHole")
    public JsonElement getParkZoneAndHole(Long parkId) {
        try {
            List<ParkPartition> parkPartitionList = teamService.getParkZoneAndHole(parkId);
            return JsonWrapper.newDataInstance(parkPartitionList);
        } catch (Exception e) {
            errmsg = "前台-创建比赛—获取球场分区时出错。球场ID="+parkId;
            e.printStackTrace();
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }

    /**
     * 创建比赛—确定
     * @return
     */
    @ResponseBody
    @RequestMapping("createMatch")
    public JsonElement createMatch(Long parkId) {
        try {
            List<ParkPartition> parkPartitionList = teamService.getParkZoneAndHole(parkId);
            return JsonWrapper.newDataInstance(parkPartitionList);
        } catch (Exception e) {
            errmsg = "前台-创建比赛—获取球场分区时出错。球场ID="+parkId;
            e.printStackTrace();
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }






    /**
     * 创建比赛—单练
     * @param parkId 所在球场Id
     * @param parkName 球场名称
     * @param zoneBeforeNine 前9洞区域
     * @param zoneAfterNine 后9洞区域
     * @param parkName 球场名称
     * @param playTime 打球时间
     * @param groupPeopleNum 同组人数
     * @param digest 备注
     * @return
     */
    @ResponseBody
    @RequestMapping("saveMyOnlyMatch")
    public JsonElement saveMyOnlyMatch(Long parkId, String parkName, String zoneBeforeNine,
                                       String zoneAfterNine,
                                       String playTime, Integer groupPeopleNum, String digest) {
        try {
            matchService.saveMyOnlyMatch(parkId, parkName, zoneBeforeNine, zoneAfterNine, playTime, groupPeopleNum, digest);
            return JsonWrapper.newDataInstance(null);
        } catch (Exception e) {
            errmsg = "前台-创建比赛—创建单练时出错。";
            e.printStackTrace();
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }

}
