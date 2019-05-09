package com.golf.golf.web;

import com.golf.common.Const;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.MapUtil;
import com.golf.common.util.PropertyConst;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.ParkInfo;
import com.golf.golf.db.TeamInfo;
import com.golf.golf.db.UserInfo;
import com.golf.golf.service.MatchService;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
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


	/**
	 * 比赛列表
	 * @param page 翻页
	 * @param type 0：全部比赛  1：我参加的比赛  2：我可以报名的比赛(包括我创建的比赛，用于审核其他球友的报名)  3:我创建的比赛
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
			if(StringUtils.isNotEmpty(keyword) && !"undefined".equals(keyword)){
				searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
			}
			searchBean.addParpField("type", type);
			searchBean.addParpField("userId", WebUtil.getUserIdBySessionId());
			pageInfo = matchService.getMatchList(searchBean, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
			String errmsg = "前台-获取比赛列表出错。";
			logger.error(errmsg+ e );
			return JsonWrapper.newErrorInstance(errmsg);
		}

		return JsonWrapper.newDataInstance(pageInfo);
	}

	/**
	 * 获取参赛球队列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getJoinTeamList")
	public JsonElement getJoinTeamList(Long matchId) {
		try {
			List<Map<String, Object>> teamList = matchService.getTeamListByMatchId(matchId);
			return JsonWrapper.newDataInstance(teamList);
		} catch (Exception e) {
			e.printStackTrace();
			String errmsg = "前台-获取参赛球队列表出错。";
			logger.error(errmsg+ e );
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}


	/**
	 * 创建比赛——获取选中的参赛球队列表的详情
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getTeamListByIds")
	public JsonElement getTeamListByIds(String joinTeamIds) {
		try {
			List<Map<String,Object>> list = matchService.getTeamListByIds(joinTeamIds);
			return JsonWrapper.newDataInstance(list);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取选中的参赛球队的详情时出错。球队id="+joinTeamIds + e);
			return JsonWrapper.newErrorInstance("获取选中的参赛球队的详情时出错");
		}
	}

	/**
	 * 创建比赛
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "saveMatchInfo")
	public JsonElement saveMatchInfo(String matchInfo, String logoPath, String joinTeamIds, String parkName, String beforeZoneName,
									 String afterZoneName,String reportTeamIds) {
		try {
			if(StringUtils.isNotEmpty(matchInfo) && StringUtils.isNotEmpty(logoPath)){
				net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(matchInfo);
				MatchInfo matchInfoBean = (MatchInfo) net.sf.json.JSONObject.toBean(jsonObject, MatchInfo.class);
				matchInfoBean.setMiLogo(logoPath);
				matchInfoBean.setMiJoinTeamIds(joinTeamIds);
				matchInfoBean.setMiZoneBeforeNine(beforeZoneName);
				matchInfoBean.setMiZoneAfterNine(afterZoneName);
				matchInfoBean.setMiReportScoreTeamId(reportTeamIds);
				matchService.saveMatchInfo(matchInfoBean,parkName);
			}
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("创建比赛时出错。" + e);
			return JsonWrapper.newErrorInstance("创建比赛时出错");
		}
	}



	/**
	 * 删除比赛
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "delMatchById")
	public JsonElement delMatchById(Long matchId) {
		try {
			boolean flag = matchService.delMatchById(matchId);
			if(flag){
				return JsonWrapper.newSuccessInstance();
			}else{
				return JsonWrapper.newErrorInstance("您没有权限删除该比赛");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除比赛时出错。" + e);
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
	public JsonElement getMatchDetail(Long matchId, Integer count) {
		try {
			//如果不是参赛人员，则加入围观用户
			matchService.saveOrUpdateWatch(matchId);
			Map<String, Object> matchMap = matchService.getMatchInfo(matchId, count);
			return JsonWrapper.newDataInstance(matchMap);
		} catch (Exception e) {
			String errmsg = "前台-点击进入比赛详情-获取围观用户列表和比赛分组时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 点击进入比赛详情——获取是否是赛长
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getIsCaptain")
	public JsonElement getIsCaptain(Long matchId) {
		try {
			boolean isCaptain = matchService.getIsCaptain(matchId);
			return JsonWrapper.newDataInstance(isCaptain);
		} catch (Exception e) {
			String errmsg = "前台-点击进入比赛详情-获取是否是赛长时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
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
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }

	/**
	 * 点击进入比赛详情——获取参赛球队信息和比赛详情
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMatchDetailInfo")
	public JsonElement getMatchDetailInfo(Long matchId) {
		try {
			Map<String, Object> matchDetailInfo = matchService.getMatchDetailInfo(matchId);
			return JsonWrapper.newDataInstance(matchDetailInfo);
		} catch (Exception e) {
			String errmsg = "前台-点击进入比赛详情-获取参赛球队信息和比赛详情时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛详情——添加组
	 * @return
	 */
	@ResponseBody
	@RequestMapping("addGroupByTeamId")
	public JsonElement addGroupByTeamId(Long matchId) {
		try {
			matchService.addGroupByTeamId(matchId);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "前台-点击进入比赛详情-获取参赛球队信息和比赛详情时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛详情——赛长获取已经报名的用户
	 * @param type 0:添加组员（获取已经报名的用户）  1 删除组员（获取本组用户）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getApplyUserByMatchId")
	public JsonElement getApplyUserByMatchId(Long matchId, Long groupId, Integer type) {
		try {
			Map<String, Object> result = null;
			if(type == 0){
				result = matchService.getApplyUserByMatchId(matchId, groupId);
			}else{
				result = matchService.getUserListByMatchIdGroupId(matchId, groupId);
			}
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			String errmsg = "前台-比赛详情——赛长获取已经报名的用户时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}


	/**
	 * 比赛详情——保存——将用户加入该分组
	 * @return
	 */
	@ResponseBody
	@RequestMapping("addUserToGroupByMatchId")
	public JsonElement addUserToGroupByMatchId(Long matchId, Long groupId, String userIds) {
		try {
			matchService.addUserToGroupByMatchId(matchId,groupId,userIds);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "前台-比赛详情——赛长将用户加入该分组时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛详情——保存——将用户从该分组删除
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delUserByMatchIdGroupId")
	public JsonElement delUserByMatchIdGroupId(Long matchId, Long groupId, String userIds) {
		try {
			matchService.delUserByMatchIdGroupId(matchId,groupId,userIds);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "前台-比赛详情—将用户删除该分组时出错。";
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
			String errmsg = "前台-根据邀请用户记分时出错。记分人id="+scorerId;
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
            searchBean.addParpField("matchId", matchId);
            searchBean.addParpField("groupId", groupId);
            searchBean.addParpField("matchUserId", matchUserId);
            searchBean.addParpField("userId", UserUtil.getUserId());
            return JsonWrapper.newDataInstance(matchService.getScoreType(searchBean));
        } catch (Exception e) {
			String errmsg = "前台-跳转记分卡页面时出错。userId="+UserUtil.getUserId();
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
			String errmsg = "前台-获取本组比赛结果详情出错。";
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
			String errmsg = "前台-获取整组比赛结果详情出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}









	/**
	 * 单练——查询是否有我正在进行的单练 今天
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMySinglePlay")
	public JsonElement getMySinglePlay() {
		try {
			Map<String,Object> result = matchService.getMySinglePlay();
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			String errmsg = "前台-单练——查询是否有我正在进行的单练时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 单练——创建单练  默认单人比杆
	 * @return
	 */
	@ResponseBody
	@RequestMapping("saveSinglePlay")
	public JsonElement saveSinglePlay(String parkName, String playTime, Integer peopleNum, String digest,
									  String beforeZoneName, String afterZoneName) {
		try {
			Map<String,Object> result = matchService.saveSinglePlay(parkName, playTime, peopleNum, digest, beforeZoneName, afterZoneName);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			String errmsg = "前台-单练——开始记分——保存数据时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
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
    public JsonElement getParkList(Integer page, String keyword, String city) {
        if (page == null || page == 0) {
            page = 1;
        }
        try {
            SearchBean searchBean = new SearchBean();
            if(StringUtils.isNotEmpty(keyword)){
                searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
            }
			if(StringUtils.isNotEmpty(city)){
				searchBean.addParpField("city", city);
			}
            POJOPageInfo pageInfo = new POJOPageInfo<ParkInfo>( Const.ROWSPERPAGE, page);
			//附近
			pageInfo = matchService.getParkListNearby(searchBean, pageInfo);
            return JsonWrapper.newDataInstance(pageInfo);
        } catch (Exception e) {
            String errmsg = "前台-创建比赛—选择球场—查询球场列表时出错。";
            e.printStackTrace();
            logger.error(errmsg + e);
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
			logger.error(errmsg + e);
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
            logger.error(errmsg + e);
            return JsonWrapper.newErrorInstance(errmsg);
        }
    }


	/**
	 * 创建球队——上传logo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "uploadMatchLogo")
	public JsonElement uploadMatchLogo(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
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
						return JsonWrapper.newErrorInstance("文件类型不正确");
					}
				}else {
					return JsonWrapper.newErrorInstance("文件不存在");
				}
			}else {
				return JsonWrapper.newErrorInstance("文件不存在");
			}
			return JsonWrapper.newDataInstance(logoPath);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("上传球队logo时出错。" + e);
			return JsonWrapper.newErrorInstance("上传球队logo时出错");
		}
	}

	/**
	 * 通过matchid和groupid查询本组记分卡信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getScoreCardInfoByGroupId")
	public JsonElement getScoreCardInfoByGroupId(Long matchId, Long groupId) {
		try {
			Map<String,Object> groupInfoList = matchService.getScoreCardInfoByGroupId(matchId,groupId);
			return JsonWrapper.newDataInstance(groupInfoList);
		} catch (Exception e) {
			String errmsg = "前台-比赛—通过matchid和groupid查询本组记分卡信息时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}


	/**
	 * 保存或更新计分数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping("saveOrUpdateScore")
	public JsonElement saveOrUpdateScore(Long userId, Long matchId, Long groupId, Long scoreId, String holeName,
										 Integer holeNum, Integer holeStandardRod, String isUp, Integer rod,
										 Integer pushRod, Integer beforeAfter) {
		try {
			matchService.saveOrUpdateScore(userId, matchId, groupId,scoreId, holeName, holeNum,
														holeStandardRod, isUp, rod, pushRod, beforeAfter);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "前台-比赛—保存或更新计分数据时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}


	/**
	 * 保存或更新比赛状态
	 * state   0：报名中  1进行中  2结束
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updateMatchState")
	public JsonElement updateMatchState(Long matchId, Integer state) {
		try {
			matchService.updateMatchState(matchId, state);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "前台-比赛—保存或更新比赛状态时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 根据比赛id获取参赛球队
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getTeamByMatchId")
	public JsonElement getTeamByMatchId(Long matchId) {
		try {
			Map<String,Object> result = new HashMap<>();
			List<TeamInfo> teamInfoList = matchService.getTeamByMatchId(matchId);
			result.put("teamInfoList",teamInfoList);
			MatchInfo matchInfo = matchService.getMatchById(matchId);
			result.put("matchInfo",matchInfo);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			String errmsg = "前台-比赛—保存或更新比赛状态时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}


	/**
	 * 成绩上报 计算积分
	 * @param scoreType 积分规则 1：杆差倍数  2：赢球奖分,
	 * @param baseScore 基础分,
	 * @param rodScore 杆差倍数,
	 * @param winScore 赢球奖分
	 * @return
	 */
	@ResponseBody
	@RequestMapping("submitScoreByTeamId")
	public JsonElement submitScoreByTeamId(Long matchId, Integer scoreType, Integer baseScore, Integer rodScore, Integer winScore) {
		try {
			matchService.submitScoreByTeamId(matchId, scoreType, baseScore, rodScore, winScore);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "前台-比赛—成绩上报时出错。matchId="+matchId;
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 结束单练
	 * @return
	 */
	@ResponseBody
	@RequestMapping("endSingleMatchById")
	public JsonElement endSingleMatchById(Long matchId) {
		try {
			matchService.endSingleMatchById(matchId);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			String errmsg = "前台-比赛—结束单练时出错。matchId="+matchId;
			e.printStackTrace();
			logger.error(errmsg + e);
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
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛——group——分队比分
	 * 显示创建比赛时“参赛范围”所选择球队的第一个，也可以选其他参赛球队
	 * 如果是该队队长，就显示“球队确认”按钮
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getTeamScoreByMatchId")
	public JsonElement getTeamScoreByMatchId(Long matchId, Long teamId) {
		try {
			Map<String,Object> teamScore = matchService.getTeamScoreByMatchId(matchId, teamId);
			return JsonWrapper.newDataInstance(teamScore);
		} catch (Exception e) {
			String errmsg = "比赛——group——获取分队比分时出错。matchId="+matchId;
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛——group——分队统计
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getTeamTotalScoreByMatchId")
	public JsonElement getTeamTotalScoreByMatchId(Long matchId) {
		try {
			List<Map<String, Object>> scoreList = matchService.getTeamTotalScoreByMatchId(matchId);
			return JsonWrapper.newDataInstance(scoreList);
		} catch (Exception e) {
			String errmsg = "比赛——group——获取分队统计时出错。matchId="+matchId;
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}




}
