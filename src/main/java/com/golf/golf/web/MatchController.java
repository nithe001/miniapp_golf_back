package com.golf.golf.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.golf.common.Const;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.PropertyConst;
import com.golf.golf.bean.ChooseUserBean;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.db.MatchGroup;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.TeamInfo;
import com.golf.golf.service.MatchDoubleRodService;
import com.golf.golf.service.MatchHoleService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

	@Autowired
	private MatchService matchService;
	@Autowired
	private UserService userService;

	//双人比杆记分卡service 每组4人  同一个球队的放一行
	@Autowired
	protected MatchDoubleRodService matchDoubleRodService;

	//比洞赛记分卡service 每组4人 同一个球队的放一行
	@Autowired
	protected MatchHoleService matchHoleService;



	/**
	 * 比赛列表
	 * @param page 翻页
	 * @param type 0：全部比赛 1：我参加的比赛（包括我参加的正在报名的比赛）2：可报名的比赛 3:已报名的比赛  4：我创建的比赛
	 * @param keyword 搜索内容
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMatchList")
	public JsonElement getMatchList(String page, Integer type, String keyword,String openid) {
		Integer nowPage = 1;
		if (StringUtils.isNotEmpty(page) && Integer.parseInt(page) > 0) {
			nowPage = Integer.parseInt(page);
		}
		POJOPageInfo pageInfo = new POJOPageInfo<MatchInfo>(Const.ROWSPERPAGE , nowPage);
		try {
			SearchBean searchBean = new SearchBean();
			if(StringUtils.isNotEmpty(keyword) && !"undefined".equals(keyword)){
				searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
			}
			searchBean.addParpField("type", type);
			Long userId = userService.getUserIdByOpenid(openid);
			searchBean.addParpField("userId", userId);
			pageInfo = matchService.getMatchList(searchBean, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
			String errmsg = "前台-获取比赛列表出错。openid="+openid;
			logger.error(errmsg,e );
			return JsonWrapper.newErrorInstance(errmsg);
		}

		return JsonWrapper.newDataInstance(pageInfo);
	}

	/**
	 * 创建比赛——获取赛长用户所在球队，是否同时是参赛球队的队长 如果是让用户选择一个做代表队
	 * @param joinTeamIds:创建比赛时选择的参赛球队
	 * 选了参赛球队，才会进此方法
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getCaptainTeamIdList")
	public JsonElement getCaptainTeamIdList(String joinTeamIds,String openid) {
		try {
			List<TeamInfo> list = matchService.getCaptainTeamIdList(joinTeamIds,openid);
			return JsonWrapper.newDataInstance(list);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("创建比赛——获取赛长所在球队时出错。" ,e);
			return JsonWrapper.newErrorInstance("创建比赛——获取赛长所在球队时出错。");
		}
	}

	/**
	 * 创建比赛 更新比赛
	 * 报名期间都可以改，但比赛开始后，就只能改 观战范围，成绩上报 和 比赛说明。
	 * @param chooseTeamId 如果赛长加入了两个以上的球队，选择一个球队为代表
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "saveMatchInfo")
	public JsonElement saveMatchInfo(String matchInfo, String logoPath, String joinTeamIds, String parkName, String beforeZoneName,
									 String afterZoneName, String reportTeamIds, String chooseTeamId, String openid) {
		try {
			MatchInfo m = null;
			//if(StringUtils.isNotEmpty(matchInfo) && StringUtils.isNotEmpty(logoPath)){
            if(StringUtils.isNotEmpty(matchInfo) ){
				net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(matchInfo);
				MatchInfo matchInfoBean = (MatchInfo) net.sf.json.JSONObject.toBean(jsonObject, MatchInfo.class);
				matchInfoBean.setMiLogo(logoPath);
				matchInfoBean.setMiJoinTeamIds(joinTeamIds);
				if(StringUtils.isNotEmpty(joinTeamIds)){
					matchInfoBean.setMiJoinOpenType(2);
				}
				matchInfoBean.setMiZoneBeforeNine(beforeZoneName);
				matchInfoBean.setMiZoneAfterNine(afterZoneName);
				if(StringUtils.isNotEmpty(reportTeamIds) && !reportTeamIds.equals("undefined") && !reportTeamIds.equals("null")){
					matchInfoBean.setMiReportScoreTeamId(reportTeamIds);
				}
				if(matchInfoBean.getMiId() != null){
					m = matchService.updateMatchInfo(matchInfoBean, parkName, chooseTeamId, openid);
				}else{
					m = matchService.saveMatchInfo(matchInfoBean, parkName, chooseTeamId, openid);
				}
			}
			return JsonWrapper.newDataInstance(m);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("创建比赛时出错。" ,e);
			return JsonWrapper.newErrorInstance("创建比赛时出错");
		}
	}


	/**
	 * 赛长点击本比赛球友信息页面，有指定该用户成为赛长按钮
	 * @param matchId:比赛id
	 * @param userId:被指定人id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "setMatchCaptainByUserId")
	public JsonElement setMatchCaptainByUserId(Long matchId, Long userId, String openid) {
		try {
			boolean flag = matchService.setMatchCaptainByUserId(matchId, userId, openid);
			return JsonWrapper.newDataInstance(flag);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("赛长指定该用户成为赛长时出错。比赛id="+matchId +" 用户id="+userId,e);
			return JsonWrapper.newErrorInstance("赛长指定该用户成为赛长时出错");
		}
	}


	/**
	 * 删除比赛
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "delMatchById")
	public JsonElement delMatchById(Long matchId, String openid) {
		try {
			boolean flag = matchService.delMatchById(matchId, openid);
			if(flag){
				return JsonWrapper.newSuccessInstance();
			}else{
				return JsonWrapper.newErrorInstance("您没有权限删除该比赛");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除比赛时出错。" ,e);
			return JsonWrapper.newErrorInstance("删除比赛时出错");
		}
	}

	/**
	 * 点击进入比赛详情——获取围观用户列表和比赛分组 如果不是参赛人员，则加入围观用户
	 * @param count 获取围观显示的个数
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMatchDetail")
	public JsonElement getMatchDetail(Long matchId, Integer count, String openid) {
		try {
			//比赛详情
			MatchInfo matchInfo = matchService.getMatchById(matchId);

			//如果比赛状态是进行中，如果不是参赛人员，则加入围观用户
            boolean isWatch = true;
			if(matchInfo.getMiIsEnd() != 0){
				isWatch = matchService.saveOrUpdateWatch(matchInfo, openid);
			}
            Map<String, Object> matchMap = matchService.getMatchInfo(matchInfo, matchId, count, openid);
            matchMap.put("isWatch",isWatch);
			return JsonWrapper.newDataInstance(matchMap);
		} catch (Exception e) {
			String errmsg = "前台-点击进入比赛详情-获取围观用户列表和比赛分组时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 删除比赛分组 并重新排列组
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delMatchGroupByGroupId")
	public JsonElement delMatchGroupByGroupId(Long groupId) {
		try {
			matchService.delMatchGroupByGroupId(groupId);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "前台-删除比赛分组时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}



	/**
	 * 获取比赛详情
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMatchInfoById")
	public JsonElement getMatchInfoById(Long matchId,Long groupId,String openid) {
		try {
			//比赛详情
			MatchInfo matchInfo = matchService.getMatchById(matchId);
			//我是否是本组参赛人员(显示邀请记分按钮)
            Long meIsInGroup = matchService.getIsJoinMatchGroup(matchId,groupId,openid);
			//我是否是赛长
			boolean meIsCap = matchService.getIsCaptain(matchId,openid);

			//20200427-nmy-判断本组比赛是否已经结束（对于一场比赛比多天，防止前一天比完的组偷摸改成绩）
			if(groupId != null){
				MatchGroup matchgroup = matchService.getMatchGroupById(groupId);
				if(matchgroup != null && matchgroup.getMgIsEnd() != null && matchgroup.getMgIsEnd() == 2){
					matchInfo.setMiIsEnd(2);
				}
			}
            Map<String,Object> map = new HashMap<>();
            map.put("matchInfo",matchInfo);
            map.put("meIsInGroup",meIsInGroup);
			map.put("meIsCap",meIsCap);
			return JsonWrapper.newDataInstance(map);
		} catch (Exception e) {
			String errmsg = "前台-获取比赛详情时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}
	/**
	 * 获取单练的groupId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getSingleMatchGroupIdByMatchId")
	public JsonElement getSingleMatchGroupIdByMatchId(Long matchId) {
		try {
			Long groupId = matchService.getSingleMatchGroupIdByMatchId(matchId);
			return JsonWrapper.newDataInstance(groupId);
		} catch (Exception e) {
			String errmsg = "前台-获取单练的groupId时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}


	/**
	 * 点击进入比赛详情——获取是否是赛长
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getIsCaptain")
	public JsonElement getIsCaptain(Long matchId, String openid) {
		try {
			boolean isCaptain = matchService.getIsCaptain(matchId, openid);
			return JsonWrapper.newDataInstance(isCaptain);
		} catch (Exception e) {
			String errmsg = "前台-点击进入比赛详情-获取是否是赛长时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

    /**
     * 获取更多围观用户
     * @return
     */
    @ResponseBody
    @RequestMapping("getMoreWatchUserList")
    public JsonElement getMoreWatchUserList(Long matchId) {
        try {
			List<Map<String, Object>> watchList = matchService.getMoreWatchUserList(matchId);
			return JsonWrapper.newDataInstance(watchList);
        } catch (Exception e) {
			String errmsg = "前台-获取更多围观用户时出错。matchId="+matchId;
            e.printStackTrace();
            logger.error(errmsg ,e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }

	/**
	 * 点击进入比赛详情——获取参赛球队信息和比赛详情
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMatchDetailInfo")
	public JsonElement getMatchDetailInfo(Long matchId, String openid) {
		try {
			Map<String, Object> matchDetailInfo = matchService.getMatchDetailInfo(matchId, openid);
			return JsonWrapper.newDataInstance(matchDetailInfo);
		} catch (Exception e) {
			String errmsg = "前台-点击进入比赛详情-获取参赛球队信息和比赛详情时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛详情——添加分组 添加组
	 * @return
	 */
	@ResponseBody
	@RequestMapping("addGroupByTeamId")
	public JsonElement addGroupByTeamId(Long matchId, String openid) {
		try {
			matchService.addGroupByTeamId(matchId, openid);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "前台-添加分组 添加组时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛详情——赛长获取本组用户，包括自己
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getUserListByGroupId")
	public JsonElement getUserListByGroupId(Long matchId, Long groupId) {
		try {
			Map<String, Object> result = matchService.getUserListByGroupId(matchId, groupId);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			String errmsg = "前台-比赛详情——赛长获取已经报名的用户时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛详情——获取全部已报名人员，按球队分组显示（去掉除我之外的参赛队的队长）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getAllApplyUserByMatchId")
	public JsonElement getAllApplyUserByMatchId(Long matchId, String openid) {
		try {
			Map<String, Object> result = matchService.getAllApplyUserByMatchId(matchId, openid);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			String errmsg = "前台-比赛详情——赛长获取已报名人员时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

    /**
     * 比赛详情——赛长获取已报名人员——按球队分组
	 *  不包括自动分配的赛长
     * @return
     */
    @ResponseBody
    @RequestMapping("getApplyUserByMatchId")
    public JsonElement getApplyUserByMatchId(Long matchId, String keyword) {
        try {
			Map<String, Object> result = matchService.getApplyUserByMatchId(matchId, keyword);
            return JsonWrapper.newDataInstance(result);
        } catch (Exception e) {
            String errmsg = "前台-比赛详情——赛长获取已报名人员时出错。";
            e.printStackTrace();
            logger.error(errmsg ,e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }


    /**
	 * 比赛详情——赛长获取待分组人员不进行任何筛选，直接取所有待分组的
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getWaitGroupUserList")
	public JsonElement getWaitGroupUserList(Long matchId, String keyword) {
		try {
            Map<String, Object> result = matchService.getWaitGroupUserList(matchId, keyword);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			String errmsg = "前台-比赛详情——赛长获取待分组人员时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}


	/**
	 * 比赛详情——赛长——获取备选球友(除去已经报名的)，赛长所在队的球友或者其搜索的结果
     * 备选列表中，把我所在的参赛球队的队友都列出来（我可能同时在几个参赛队中，只是代表一个队参赛），
     * 当把该名单中某几人添加到分组后，备选名单里这些人要还在，
     * 也就是不维护备选名单的人员，这样即使一个人已加入一组，
     * 就还能从备选名单中选他加入新组以实现自动换组
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMyTeamUserList")
	public JsonElement getMyTeamUserList(Long matchId, String keyword, String openid) {
		try {
			Map<String,Object> result = matchService.getMyTeamUserList(matchId, keyword,openid);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			String errmsg = "前台-比赛详情——赛长获取备选球友时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛详情——保存——将用户从该分组删除，用户再次进入临时分组
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delUserByMatchIdGroupId")
	public JsonElement delUserByMatchIdGroupId(String mappingIds) {
		try {
			matchService.delUserByMatchIdGroupId(mappingIds);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "前台-比赛详情—将用户删除该分组时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

    /**
     * 普通球友 换组
     * @return
     */
    @ResponseBody
    @RequestMapping("updateMatchGroupByUserId")
    public JsonElement updateMatchGroupByUserId(Long matchId, Long groupId, String openid) {
        try {
            matchService.updateMatchGroupByUserId(matchId, groupId, openid);
            return JsonWrapper.newSuccessInstance();
        } catch (Exception e) {
            String errmsg = "前台-比赛详情—普通球友换组时出错。";
            e.printStackTrace();
            logger.error(errmsg ,e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }

	/**
	 * 比赛详情——赛长 添加用户至分组——筛选选中的用户是否有重复的
	 * @param checkIds:选中的id（mappingid或者用户id）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("checkChooseUserMutl")
	public JsonElement checkChooseUserMutl(String checkIds, String mutlUserId) {
		try {
			JSONArray allArray = JSON.parseArray(checkIds);
			JSONArray mutlUserIdArray = JSON.parseArray(mutlUserId);
			List<ChooseUserBean> list = new ArrayList<>();
			for(Object mutlObject:mutlUserIdArray){
				ChooseUserBean bean = new ChooseUserBean();
				List<JSONObject> objectList = new ArrayList<>();
				for(int j =0;j<allArray.size();j++){
					JSONObject allObj = allArray.getJSONObject(j);
					if(mutlObject.equals(allObj.get("userId"))){
						bean.setUserName(allObj.get("userName").toString());
						objectList.add(allObj);
					}
				}
				bean.setList(objectList);
				list.add(bean);
			}
			return JsonWrapper.newDataInstance(list);
		} catch (Exception e) {
			String errmsg = "前台-筛选选中的用户是否有重复时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	public static void main(String[] args) {
		String checkIds = "[{\"userId\":\"8\",\"userName\":\"宋建宁\",\"teamId\":\"5\",\"teamName\":\"北大队\"}," +
				"{\"userId\":\"8\",\"userName\":\"宋建宁\",\"teamId\":\"10\",\"teamName\":\"光华队\"}," +
				"{\"userId\":\"3\",\"userName\":\"张三\",\"teamId\":\"10\",\"teamName\":\"1队\"}," +
				"{\"userId\":\"3\",\"userName\":\"张三\",\"teamId\":\"10\",\"teamName\":\"2队\"}" +
				"]";
		String mutlUserId = "[\"3\",\"8\"]";
		JSONArray allArray = JSON.parseArray(checkIds);
		System.out.println(allArray);
		JSONArray mutlUserIdArray = JSON.parseArray(mutlUserId);
		System.out.println(mutlUserIdArray);

		List<ChooseUserBean> list = new ArrayList<>();

		for(Object mutlObject:mutlUserIdArray){
			ChooseUserBean bean = new ChooseUserBean();
			List<JSONObject> objectList = new ArrayList<>();
			for(int j =0;j<allArray.size();j++){
				JSONObject allObj = allArray.getJSONObject(j);
				if(mutlObject.equals(allObj.get("userId"))){
					bean.setUserName(allObj.get("userName").toString());
					objectList.add(allObj);
				}
			}
			bean.setList(objectList);
			list.add(bean);
		}
		System.out.println(111);
	}

	/**
	 * 比赛详情——赛长 添加用户至分组
	 * @param checkIds:选中的id（mappingid或者用户id）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updateGroupUserByMatchIdGroupId")
	public JsonElement updateGroupUserByMatchIdGroupId(Long matchId, Long groupId, String checkIds, String openid) {
		try {
			matchService.updateGroupUserByMatchIdGroupId(matchId, groupId,checkIds,openid);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "前台-比赛详情—更新用户分组时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
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
			String errmsg = "前台-获取本组比赛结果详情出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
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
			String errmsg = "前台-获取整组比赛结果详情出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}









	/**
	 * 单练——查询是否有我正在进行的单练 今天
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMySinglePlay")
	public JsonElement getMySinglePlay(String openid) {
		try {
			Map<String,Object> result = matchService.getMySinglePlay(openid);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			String errmsg = "前台-单练——查询是否有我正在进行的单练时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 单练——创建单练  默认单人比杆
	 * @return
	 */
	@ResponseBody
	@RequestMapping("saveSinglePlay")
	public JsonElement saveSinglePlay(String matchTitle, Long parkId, String parkName, String playTime, Integer peopleNum, String digest,
									  String beforeZoneName, String afterZoneName, String openid) {
		try {
			Map<String,Object> result = matchService.saveSinglePlay(matchTitle, parkId, parkName, playTime, peopleNum, digest,
										beforeZoneName, afterZoneName, openid);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			String errmsg = "前台-单练——开始记分——保存数据时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 记分卡 初始化 查询我是否可以记分
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMeCanScore")
	public JsonElement getMeCanScore(Long matchId, Long groupId, String openid) {
		try {
			boolean flag = matchService.getMeCanScore(matchId, groupId, openid);
			return JsonWrapper.newDataInstance(flag);
		} catch (Exception e) {
			String errmsg = "前台-单练——开始记分——保存数据时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 点击围观用户并邀请其记分
	 * @return
	 */
	@ResponseBody
	@RequestMapping("invitationScore")
	public JsonElement invitationScore(Long matchId, Long userId, String openid) {
		try {
			matchService.saveInvitationScore(matchId, userId, openid);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "前台-点击围观用户并邀请其记分时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}



    /**
     * 创建比赛—获取球场列表——离我最近的排前面
     * @param page 翻页
     * @param keyword 搜索关键字
	 * @param city 城市
     * @return
     */
    @ResponseBody
    @RequestMapping("getParkList")
    public JsonElement getParkList(Integer page, String keyword, String city, String openid) {
        if (page == null || page == 0) {
            page = 1;
        }
        try {
			POJOPageInfo pageInfo = new POJOPageInfo<Map<String,Object>>( Const.ROWSPERPAGE, page);
            SearchBean searchBean = new SearchBean();
            if(StringUtils.isNotEmpty(keyword)){
                searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
            }
			searchBean.addParpField("userId", userService.getUserIdByOpenid(openid));
			if(StringUtils.isNotEmpty(city)){
				searchBean.addParpField("city", city);
				//区域
				pageInfo = matchService.getParkListByCity(searchBean, pageInfo);
			}else{
				//附近
				pageInfo = matchService.getParkListNearby(searchBean, pageInfo);
			}
            return JsonWrapper.newDataInstance(pageInfo);
        } catch (Exception e) {
            String errmsg = "前台-创建比赛—选择球场—查询球场列表时出错。";
            e.printStackTrace();
            logger.error(errmsg ,e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }
	/**
	 * 创建比赛—获取球场城市列表
	 * @param keyword 搜索关键字
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getParkCityList")
	public JsonElement getParkCityList(String keyword) {
		try {
			List<String> list = matchService.getParkCityList(keyword);
			return JsonWrapper.newDataInstance(list);
		} catch (Exception e) {
			String errmsg = "前台-创建比赛—获取球场城市列表时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}


    /**
     * 创建比赛—点击球场-获取分区（前九洞 后九洞）
     * @return
     */
    @ResponseBody
    @RequestMapping("getZoneByParkId")
    public JsonElement getZoneByParkId(Long parkId) {
        try {
			List<Object[]> parkPartitionList = matchService.getParkZoneAndHole(parkId);
            return JsonWrapper.newDataInstance(parkPartitionList);
        } catch (Exception e) {
            String errmsg = "前台-创建比赛—获取球场分区时出错。球场ID="+parkId;
            e.printStackTrace();
            logger.error(errmsg ,e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }


	/**
	 * 创建比赛——上传logo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "uploadMatchLogo")
	public String uploadMatchLogo(HttpServletRequest request) throws IOException {
		MultipartHttpServletRequest req =(MultipartHttpServletRequest)request;
		MultipartFile file =  req.getFile("file");
		logger.error("进入上传比赛logo请求。");
		try {
			String logoPath = null;
			System.out.println("执行upload");
			request.setCharacterEncoding("UTF-8");
			logger.info("执行图片上传");
			if(!file.isEmpty()) {
				logger.info("成功获取照片");
				String fileName = file.getOriginalFilename();
				String type = null;
				type = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()) : null;
				logger.info("图片初始名称为：" + fileName + " 类型为：" + type);
				if (type != null) {
					if ("GIF".equals(type.toUpperCase())||"PNG".equals(type.toUpperCase())||"JPG".equals(type.toUpperCase())) {
						// 项目在容器中实际发布运行的根路径
						String realPath = WebUtil.getRealPath(PropertyConst.MATHC_LOGO_PATH);
						// 自定义的文件名称
						String trueFileName = String.valueOf(System.currentTimeMillis()) + "."+type;
						File targetFile = new File(realPath, trueFileName);
						if(!targetFile.exists()){
							targetFile.mkdirs();
						}
						file.transferTo(targetFile);
						logoPath = PropertyConst.MATHC_LOGO_PATH+trueFileName;
						logger.info("文件成功上传到指定目录下");
					}else {
						logger.info("文件类型不正确");
						return "error";
					}
				}else {
					logger.info("文件不存在");
					return "error";
				}
			}else {
				logger.info("文件不存在");
				return "error";
			}
			return logoPath;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("上传比赛logo时出错。" ,e);
			return "error";
		}
	}

	/**
	 * 通过matchid和groupid查询本组记分卡信息——个人比杆 每组4个人
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getSingleRodScoreCard")
	public JsonElement getSingleRodScoreCardInfoByGroupId(Long matchId, Long groupId) {
		try {
			Map<String,Object> groupInfoList = matchService.getSingleRodScoreCardInfoByGroupId(matchId,groupId);
			return JsonWrapper.newDataInstance(groupInfoList);
		} catch (Exception e) {
			String errmsg = "前台-比赛—通过matchid和groupid查询本组单人比杆记分卡信息时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 通过matchid和groupid查询本组记分卡信息——双人比杆 每组4个人 每2个人一组
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getDoubleRodScoreCard")
	public JsonElement getDoubleRodScoreCardByGroupId(Long matchId, Long groupId) {
		try {
			Map<String,Object> groupInfoList = matchDoubleRodService.getDoubleRodScoreCardByGroupId(matchId,groupId);
			return JsonWrapper.newDataInstance(groupInfoList);
		} catch (Exception e) {
			String errmsg = "前台-比赛—通过matchid和groupid查询本组双人比杆记分卡信息时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 通过matchid和groupid查询本组记分卡信息——单人比洞 每组2个人
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getHoleScoreCard")
	public JsonElement getHoleScoreCard(Long matchId, Long groupId) {
		try {
			Map<String,Object> groupInfoList = matchHoleService.updateOrGetHoleScoreCardByGroupId(matchId,groupId);
			return JsonWrapper.newDataInstance(groupInfoList);
		} catch (Exception e) {
			String errmsg = "前台-比赛—通过matchid和groupid查询本组比洞赛记分卡信息时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 保存或更新计分数据   如果有上报球队，同时向上报球队记分 从上报球队中再筛选是否符合条件的上报球队
	 * 与标准杆一样 叫平标准杆
	 * 比标准杆少一杆叫小鸟
	 * 比标准杆多一杆或者标准杆完成该洞叫Par
	 * 低于标准杆2杆完成该洞叫老鹰
	 * userIds: 双人比杆公开赛 大分组中小组的所有用户id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("saveOrUpdateScore")
	public JsonElement saveOrUpdateScore(Long userId, Long matchId, Long groupId, Long holeId,
                                         String isUp, Integer rod, String rodCha,
										 Integer pushRod, Integer beforeAfter, String openid, String userIds) {
		try {
			//判断本组比赛是否结束
			MatchGroup group = matchService.getMatchGroupById(groupId);
			if(group != null && group.getMgIsEnd() ==2){
				//结束
				return JsonWrapper.newErrorInstance("本组比赛已经结束");
			}

			if(userId != 0L){
				matchService.saveOrUpdateScore(userId, matchId, groupId,
						holeId,
						isUp, rod, rodCha, pushRod, beforeAfter, openid,userIds);
			}else{
				//双人比杆公开赛
				matchService.saveOrUpdateScoreDoubleRod(matchId, groupId,
						holeId,
						isUp, rod, rodCha, pushRod, beforeAfter, openid,userIds);
			}
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "前台-比赛—保存或更新计分数据时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 开始比赛 前检查分组是否合理
	 * @return
	 */
	@ResponseBody
	@RequestMapping("checkBeforeUpdateMatchState")
	public JsonElement checkBeforeUpdateMatchState(Long matchId, Integer state) {
		try {
			String msg = matchService.checkBeforeUpdateMatchState(matchId, state);
			return JsonWrapper.newDataInstance(msg);
		} catch (Exception e) {
			String errmsg = "前台-比赛—开始比赛前检查分组是否合理时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 开始比赛 / 结束比赛 ——保存或更新比赛状态
	 * state   0：报名中  1进行中  2结束
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updateMatchState")
	public JsonElement updateMatchState(Long matchId, Integer state, String openid) {
		try {
            String msg = matchService.updateMatchState(matchId, state, openid);
			return JsonWrapper.newDataInstance(msg);
		} catch (Exception e) {
			String errmsg = "前台-比赛—保存或更新比赛状态时出错。";
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}


	/**
	 * 成绩上报 计算积分（注意球友加入球队是否成功） 允许重复上报
	 * 我作为一个上报球队队长，可能给不同的参赛球队分配不同的积分
	 * 拿每个参赛队和每个上报球队算交集，如果没有就忽略，如果有，就把交集的队员成绩交给上报球队并积分
     * **********注意比洞赛的积分公式和比杆赛不一样
	 * @param matchId 比赛id,
	 * @param teamId 上报球队id,
	 * @param teamId 参赛球队id,      nhq
	 * @param reportteamId 上报球队id,      nhq
	 * @param scoreType 积分规则 1：杆差倍数  2：赢球奖分,
	 * @param baseScore 基础分,
	 * @param rodScore 杆差倍数,
	 * @param winScore 赢球奖分
	 * @return
	 */
	@ResponseBody
	@RequestMapping("submitScoreByTeamId")
	public JsonElement submitScoreByTeamId(Long matchId, Long teamId, Long reportteamId,Integer scoreType, Integer baseScore,
										   Integer rodScore, Integer winScore, String openid) {
		try {
			matchService.submitScoreByTeamId(matchId, teamId, reportteamId,scoreType, baseScore, rodScore, winScore, openid);
		} catch (Exception e) {
			String errmsg = "前台-比赛—成绩上报时出错。matchId="+matchId;
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
		return JsonWrapper.newSuccessInstance();
	}

	/**
	 * 撤销成绩上报
	 * @param matchId 比赛id,
	 * @param teamId 球队id,
	 * @return
	 */
	@ResponseBody
	@RequestMapping("cancelScoreByTeamId")
	public JsonElement cancelScoreByTeamId(Long matchId, Long teamId,  Long reportteamId, String openid) {
		try {
			boolean flag = matchService.cancelScoreByTeamId(matchId, teamId,reportteamId,openid);
			if(flag){
				return JsonWrapper.newDataInstance(1);
			}else{
				return JsonWrapper.newDataInstance(0);
			}
		} catch (Exception e) {
			String errmsg = "前台-比赛—撤销成绩上报时出错。matchId="+matchId;
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 结束单练
	 * @return
	 */
	@ResponseBody
	@RequestMapping("endSingleMatchById")
	public JsonElement endSingleMatchById(Long matchId, String openid) {
		try {
			matchService.endSingleMatchById(matchId, openid);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "前台-比赛—结束单练时出错。matchId="+matchId;
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 单练——更新临时用户姓名
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updateTemporaryUserNameById")
	public JsonElement updateTemporaryUserNameById(Long userId, String userName, Long matchId, Long groupId) {
		try {
			matchService.updateTemporaryUserNameById(userId, userName, matchId, groupId);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "前台-单练——更新临时用户姓名时出错。matchId="+matchId;
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛——group——总比分
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getTotalScoreByMatchId")
	public JsonElement getTotalScoreByMatchId(Long matchId) {
		try {
			Map<String,Object> scoreList = matchService.getTotalScoreByMatchId(matchId);
			return JsonWrapper.newDataInstance(scoreList);
		} catch (Exception e) {
			String errmsg = "比赛——group——获取总比分时出错。matchId="+matchId;
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛——group——分队比分（不包括上报球队）
	 * 显示创建比赛时“参赛范围”所选择球队的第一个，也可以选其他参赛球队
	 * 如果是该队队长，就显示“球队确认”按钮
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getTeamScoreByMatchId")
	public JsonElement getTeamScoreByMatchId(Long matchId, Long teamId, String openid) {
		try {
			Map<String,Object> teamScore = matchService.getTeamScoreByMatchId(matchId, teamId, openid);
			return JsonWrapper.newDataInstance(teamScore);
		} catch (Exception e) {
			String errmsg = "比赛——group——获取分队比分时出错。matchId="+matchId;
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛——group——分队比分（不包括上报球队）  获取我在参赛队和上报队都是队长的球队
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMyTeamListByKeepScore")
	public JsonElement getMyTeamListByKeepScore(Long matchId, Long teamId, String openid) {
		try {
			Map<String,Object> myTeamList = matchService.getMyTeamListByKeepScore(matchId, teamId, openid);
			return JsonWrapper.newDataInstance(myTeamList);
		} catch (Exception e) {
			String errmsg = "比赛—分队比分—初始化球队积分确认-获取我在参赛队和上报队都是队长的球队时出错。matchId="+matchId;
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}


	/**
	 * 比赛——group——分队统计
	 * 如果是两个队比洞的话，把每队各组的分相加，其中赢的组得1分，输的组得0分，平的组各得0.5分。一个队队内，及多个队之间没法进行比洞赛
	 * 分队统计的前n名，就是指每个队排前n名的人的杆数和排名
	 * 比杆赛：如果是比杆赛，名次就取每队成绩最好（杆数最少）的前n人（如果是双人，就是前5组）计算
	 * 					按创建比赛时“参赛范围”选择的球队统计成绩并按平均杆数排名，（球队、参赛人数、平均杆数、总杆数、排名）
	 * 	如果是比洞赛，n就按按分组顺序取前n组，还按原来的表头计算
	 * 比洞赛：用不同的表。（球队、获胜组、打平组、得分、排名）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getTeamTotalScoreByMatchId")
	public JsonElement getTeamTotalScoreByMatchId(Long matchId, Integer mingci) {
		try {
			Map<String, Object> result = matchService.getTeamTotalScoreByMatchId(matchId, mingci);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			String errmsg = "比赛——group——获取分队统计时出错。matchId="+matchId;
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛—球友报名—普通用户选一个组报名——获取球友所在球队
	 *  获取用户是否在参赛球队中，如果是多队比赛，并且同时都在参赛队中，让用户选择一个做代表队
	 *  如果是队式比赛，一个人报名的时候如果还不是任何一个参赛队成员，就让他先选一个，但只是在参赛队里选。
	 *        （这种情况只会有人把比赛报名链接分享到微信群后，通过微信群进入才会有，在小程序里，一个人应该看不到他不在任何一个参赛队中的比赛报名）
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getMyJoinTeamList")
	public JsonElement getMyJoinTeamList(Long matchId, String openid) {
		try {
			List<TeamInfo> list = matchService.getMyJoinTeamList(matchId,openid);
			return JsonWrapper.newDataInstance(list);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("球友报名——获取球友所在球队时出错。" ,e);
			return JsonWrapper.newErrorInstance("球友报名——获取球友所在球队时出错。");
		}
	}

	/**
	 * 比赛——普通用户选一个组报名——可以换组
	 * @param chooseTeamId:代表球队id（在加入了多个球队的情况下）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("applyMatch")
	public JsonElement applyMatch(Long matchId, Long groupId, String groupName, String chooseTeamId, String openid) {
		try {
			boolean flag = matchService.applyMatch(matchId, groupId, groupName, chooseTeamId, openid);
			return JsonWrapper.newDataInstance(flag);
		} catch (Exception e) {
			String errmsg = "比赛——报名时出错。matchId="+matchId;
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛——普通用户从一个组退出比赛
	 * @return
	 */
	@ResponseBody
	@RequestMapping("quitMatch")
	public JsonElement quitMatch(Long matchId, String openid) {
		try {
			matchService.quitMatch(matchId, openid);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "比赛——退出比赛时出错。matchId="+matchId;
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛——报名页面——底部按钮——报名待分组
	 * @return
	 */
	@ResponseBody
	@RequestMapping("saveUserApplyWaitGroup")
	public JsonElement saveUserApplyWaitGroup(Long matchId, String chooseTeamId, String openid) {
		try {
			matchService.saveUserApplyWaitGroup(matchId, chooseTeamId, openid);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "比赛——报名待分组时出错。matchId="+matchId;
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}


	/**
	 * 比赛——生成二维码：邀请记分、邀请加入
	 * @param matchId 比赛id
	 * @param groupId 本组id
	 * @param openid 我的openid
	 * @param type 0:邀请记分 1：邀请加入
	 * @return
	 */
	@ResponseBody
	@RequestMapping("createQRCode")
	public JsonElement createQRCode(Long matchId, Long groupId, String openid, Integer type) {
		try {
			Map<String,Object> result = matchService.createQRCode(matchId, groupId, openid, type);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			String errmsg = "";
			if(type == 0){
				errmsg = "比赛——生成'邀请记分'二维码时出错。matchId="+matchId+" groupId="+groupId+" openid="+openid;
			}else{
				errmsg = "比赛——生成'邀请加入'二维码时出错。matchId="+matchId+" groupId="+groupId+" openid="+openid;
			}
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛——扫码加入其他球友的单练
	 * @param matchId 比赛id
	 * @param groupId 本组id
	 * @param openid 我的openid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("joinOtherPractice")
	public JsonElement joinOtherPractice(Long matchId, Long groupId, String openid) {
		try {
			matchService.joinOtherPractice(matchId, groupId, openid);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "比赛——扫码加入其他球友的单练时出错。matchId="+matchId+" groupId="+groupId+" openid="+openid;
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛——更新我的扫码记录
	 * @param matchId 比赛id
	 * @param groupId 本组id
	 * @param openid 我的openid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updateMyScanQRCode")
	public JsonElement updateMyScanQRCode(Long matchId, Long groupId, Integer type, String openid) {
		try {
			matchService.updateMyScanQRCode(matchId, groupId, openid,type);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "比赛——更新我的扫码记录时出错。matchId="+matchId+" groupId="+groupId+" openid="+openid+" type="+type;
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}




    /**
     * 比赛——退出观战——此功能不用，改为不退出
     * @return
     */
    @ResponseBody
    @RequestMapping("delWatchMatch_notuse")
    public JsonElement delWatchMatch(Long matchId, String openid) {
        try {
            matchService.delWatchMatch(matchId, openid);
            return JsonWrapper.newSuccessInstance();
        } catch (Exception e) {
            String errmsg = "比赛——退出观战时出错。matchId="+matchId+"  openid="+openid;
            e.printStackTrace();
            logger.error(errmsg ,e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }


	/**
	 * 比赛——报名页面——获取更多赛长
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getAllMatchCaptainList")
	public JsonElement getAllMatchCaptainList(Long matchId) {
		try {
			List<Map<String, Object>> watchList = matchService.getAllMatchCaptainList(matchId);
			return JsonWrapper.newDataInstance(watchList);
		} catch (Exception e) {
			String errmsg = "前台-获取更多赛长时出错。matchId="+matchId;
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}


	/**
	 * 比赛——某一组——记分卡——赛长结束该组比赛
	 * 防止一场比赛比多天的时候，前一天结束后改成绩
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updateMatchGroupStateByGroupId")
	public JsonElement updateMatchGroupStateByGroupId(Long matchId, Long groupId, String openid) {
		try {
			//我是否是赛长
			boolean meIsCap = matchService.getIsCaptain(matchId,openid);
			if(meIsCap){
				matchService.updateMatchGroupStateByGroupId(groupId);
				return JsonWrapper.newSuccessInstance();
			}
		} catch (Exception e) {
			String errmsg = "前台-结束该组比赛时出错。groupId="+groupId+" 操作用户openid="+openid;
			e.printStackTrace();
			logger.error(errmsg ,e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
		return JsonWrapper.newErrorInstance("抱歉您不是赛长，没有权限操作。");
	}
}
