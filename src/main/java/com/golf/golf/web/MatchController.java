package com.golf.golf.web;

import com.golf.common.Const;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.HttpUtil;
import com.golf.common.util.PropertyConst;
import com.golf.golf.common.security.UserModel;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.common.security.WechatUserUtil;
import com.golf.golf.db.*;
import com.golf.golf.service.MatchService;
import com.golf.golf.service.TeamService;
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
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
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
    private TeamService teamService;


	/**
	 * 比赛列表
	 * @param page 翻页
	 * @param type 0：全部比赛  1：我参加的比赛  2：我可以报名的比赛  3:我创建的比赛
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
			searchBean.addParpField("type", type);
			searchBean.addParpField("userId", 4L);
			pageInfo = matchService.getMatchList(searchBean, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
			errmsg = "前台-获取比赛列表出错。";
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
	public JsonElement getMatchDetail(Long matchId) {
		try {
			List<Map<String, Object>> teamList = matchService.getteamListByMatchId(matchId);
			return JsonWrapper.newDataInstance(teamList);
		} catch (Exception e) {
			e.printStackTrace();
			errmsg = "前台-获取参赛球队列表出错。";
			logger.error(errmsg+ e );
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}


	/**
	 * 创建比赛
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "saveMatchInfo")
	public JsonElement saveMatchInfo(String matchInfo, String logoPath) {
		try {
			if(StringUtils.isNotEmpty(matchInfo) && StringUtils.isNotEmpty(logoPath)){
				net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(matchInfo);
				MatchInfo matchInfoBean = (MatchInfo) net.sf.json.JSONObject.toBean(jsonObject, MatchInfo.class);
				matchInfoBean.setMiLogo(PropertyConst.DOMAIN + logoPath);
				matchService.saveMatchInfo(matchInfoBean);
			}
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("创建比赛时出错。" + e);
			return JsonWrapper.newErrorInstance("创建比赛时出错");
		}
	}


	/**
	 * 点击进入比赛详情——获取围观用户列表和比赛分组
	 * @param count 获取围观显示的个数
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMatchDetail")
	public JsonElement getMatchDetail(Long matchId, Integer count) {
		try {
			Map<String, Object> matchMap = matchService.getMatchInfo(matchId, count);
			return JsonWrapper.newDataInstance(matchMap);
		} catch (Exception e) {
			errmsg = "前台-点击进入比赛详情-获取围观用户列表和比赛分组时出错。";
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
            errmsg = "前台-获取更多围观用户时出错。matchId="+matchId;
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
			errmsg = "前台-点击进入比赛详情-获取参赛球队信息和比赛详情时出错。";
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
			errmsg = "前台-点击进入比赛详情-获取参赛球队信息和比赛详情时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 比赛详情——赛长获取已经报名的用户
	 * @param type 2:添加组员（获取已经报名的用户）  1 删除组员（获取本组用户）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getApplyUserByMatchId")
	public JsonElement getApplyUserByMatchId(Long matchId, Long groupId, Integer type) {
		try {
			List<Map<String, Object>> applyUserList = null;
			if(type == 0){
				applyUserList = matchService.getApplyUserByMatchId(matchId);
			}else{
				applyUserList = matchService.getUserListByMatchIdGroupId(matchId, groupId);
			}
			return JsonWrapper.newDataInstance(applyUserList);
		} catch (Exception e) {
			errmsg = "前台-比赛详情——赛长获取已经报名的用户时出错。";
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
			errmsg = "前台-比赛详情——赛长将用户加入该分组时出错。";
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
			errmsg = "前台-比赛详情—将用户删除该分组时出错。";
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
            searchBean.addParpField("matchId", matchId);
            searchBean.addParpField("groupId", groupId);
            searchBean.addParpField("matchUserId", matchUserId);
            searchBean.addParpField("userId", UserUtil.getUserId());
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
	 * 单练——查询是否有我正在进行的单练
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMySinglePlay")
	public JsonElement getMySinglePlay() {
		try {
			MatchInfo matchInfo = matchService.getMySinglePlay();
			return JsonWrapper.newDataInstance(matchInfo);
		} catch (Exception e) {
			errmsg = "前台-单练——查询是否有我正在进行的单练时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}



	/**
	 * 单练——选择器——获取球场
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getParkInfo")
	public JsonElement getParkInfo(String city) {
		try {
			Map<String, Object> parkInfoList = matchService.getParkInfoList(city);
			return JsonWrapper.newDataInstance(parkInfoList);
		} catch (Exception e) {
			errmsg = "前台-单练——选择器——获取球场时出错。";
			e.printStackTrace();
			logger.error(errmsg + e);
			return JsonWrapper.newErrorInstance(errmsg);
		}
	}

	/**
	 * 单练——开始记分——保存数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping("saveSinglePlay")
	public JsonElement saveSinglePlay(String parkName, String playTime, Integer peopleNum, String digest) {
		try {
			Long singleMatchId = matchService.saveSinglePlay(parkName, playTime, peopleNum, digest);
			return JsonWrapper.newDataInstance(singleMatchId);
		} catch (Exception e) {
			errmsg = "前台-单练——开始记分——保存数据时出错。";
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
						String realPath = WebUtil.getRealPath("up/teamLogo");
						// 自定义的文件名称
						String trueFileName = String.valueOf(System.currentTimeMillis()) + "."+type;
						file.transferTo(new File(realPath,trueFileName));
						logoPath = "up/matchLogo/"+trueFileName;
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

}
