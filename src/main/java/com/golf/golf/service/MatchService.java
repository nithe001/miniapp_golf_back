package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.MapUtil;
import com.golf.common.util.PropertyConst;
import com.golf.common.util.TimeUtil;
import com.golf.golf.bean.*;
import com.golf.golf.common.security.UserModel;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.dao.MatchDao;
import com.golf.golf.db.*;
import com.golf.golf.enums.MatchGroupUserMappingTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 学术活动
 * Created by nmy on 2017/7/1.
 */
@Service
public class MatchService implements IBaseService {

	@Autowired
	private MatchDao matchDao;
	@Autowired
	private TeamService teamService;

	/**
	 * 查询球场列表——附近的球场
	 *
	 * @return
	 */
	public POJOPageInfo getParkListNearby(SearchBean searchBean, POJOPageInfo pageInfo) {
		UserInfo userInfo = matchDao.get(UserInfo.class,WebUtil.getUserIdBySessionId());
		//用户经纬度存在, 计算我附近10千米的经纬度
		if (StringUtils.isNotEmpty(userInfo.getUiLatitude()) && StringUtils.isNotEmpty(userInfo.getUiLongitude())) {
			searchBean = MapUtil.findNeighPosition(searchBean, Double.parseDouble(userInfo.getUiLongitude()),
					Double.parseDouble(userInfo.getUiLatitude()), 10);
		}
		pageInfo = matchDao.getParkListNearby(searchBean, pageInfo);
		//计算离我的距离
		if (pageInfo.getCount() > 0 && pageInfo.getItems() != null && pageInfo.getItems().size() > 0) {
			getToMyDistance(pageInfo);
		}
		return pageInfo;
	}

	//计算离我的距离
	private void getToMyDistance(POJOPageInfo pageInfo) {
		UserInfo userInfo = matchDao.get(UserInfo.class, WebUtil.getUserIdBySessionId());
		if (StringUtils.isNotEmpty(userInfo.getUiLatitude()) && StringUtils.isNotEmpty(userInfo.getUiLongitude())) {
			for (ParkInfo parkInfo : (List<ParkInfo>) pageInfo.getItems()) {
				String distance = MapUtil.getDistance(userInfo.getUiLatitude(), userInfo.getUiLongitude(), parkInfo.getPiLat(), parkInfo.getPiLng());
				parkInfo.setToMyDistance(distance);
			}
		}
	}

	/**
	 * 获取比赛列表 0：全部比赛  1：我参加的比赛  2：我可以报名的比赛 3:我创建的比赛
	 *
	 * @return
	 */
	public POJOPageInfo getMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		UserInfo userInfo = WebUtil.getUserInfoBySessionId();
		//用户经纬度存在, 计算我附近10千米的经纬度
		if (userInfo != null && StringUtils.isNotEmpty(userInfo.getUiLatitude()) && StringUtils.isNotEmpty(userInfo.getUiLongitude())) {
			searchBean = MapUtil.findNeighPosition(searchBean, Double.parseDouble(userInfo.getUiLongitude()),
					Double.parseDouble(userInfo.getUiLatitude()), 10);
		}
		POJOPageInfo pageInfo_ = matchDao.getMatchList(searchBean, pageInfo);
		if (pageInfo_.getCount() > 0 && pageInfo_.getItems().size() > 0) {
			List<MatchInfo> matchInfoList = new ArrayList<>();
			for (Map<String, Object> result : (List<Map<String, Object>>) pageInfo_.getItems()) {
				MatchInfo matchInfo = new MatchInfo();
				matchInfo.setMiId(getLongValue(result, "mi_id"));
				matchInfo.setMiLogo(getName(result, "mi_logo"));
				matchInfo.setMiTitle(getName(result, "mi_title"));
				matchInfo.setMiParkName(getName(result, "mi_park_name"));
				matchInfo.setMiMatchTime(getName(result, "mi_match_time"));
				matchInfo.setMiMatchFormat1(getIntegerValue(result, "mi_match_format_1"));
				matchInfo.setMiMatchFormat2(getIntegerValue(result, "mi_match_format_2"));
				Integer state = getIntegerValue(result, "mi_is_end");
				//0：报名中  1进行中  2结束
				if (state == null || state == 0) {
					matchInfo.setStateStr("报名中");
				} else if (state == 1) {
					matchInfo.setStateStr("进行中");
				} else {
					matchInfo.setStateStr("已结束");
				}
				matchInfo.setMiHit(getIntegerValue(result, "userCount"));
				matchInfo.setMiIsEnd(getIntegerValue(result, "mi_is_end"));
				matchInfo.setMiLogo(PropertyConst.DOMAIN + matchInfo.getMiLogo());
				//是否是赛长（显示创建比赛列表时用）
//				matchInfo.setIsCaptain(matchDao.getIsCaptain(matchInfo.getMiId(),WebUtil.getUserIdBySessionId()));
				matchInfoList.add(matchInfo);
			}
			pageInfo.setItems(matchInfoList);
			pageInfo.setCount(pageInfo_.getCount());
		}
		return pageInfo;
	}


	/**
	 * 加点击量
	 *
	 * @return
	 */
	public MatchInfo addHit(Long matchId) {
		MatchInfo db = matchDao.get(MatchInfo.class, matchId);
		if (db.getMiHit() == null || db.getMiHit() == 0) {
			db.setMiHit(1);
		} else {
			db.setMiHit(db.getMiHit() + 1);
		}
		matchDao.update(db);
		return db;
	}

	/**
	 * 获取本比赛的围观用户列表和比赛分组
	 *
	 * @param count 获取围观显示的个数
	 * @return
	 */
	public Map<String, Object> getMatchInfo(Long matchId, Integer count) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (count == null) {
			count = 0;
		}
		POJOPageInfo pageInfo = new POJOPageInfo(count, 1);
		//比赛详情
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		//围观
		List<Map<String, Object>> watchList = matchDao.getUserListByMatchId(matchId, 0, pageInfo);

		//赛长
		List<Map<String, Object>> captainList = matchDao.getCaptainListByMatchId(matchId, pageInfo);

		//分组
		List<MatchGroup> groupList_ = matchDao.getMatchGroupList_(matchId);
		List<MatchGroupBean> groupList = new ArrayList<>();
		if (groupList_ != null && groupList_.size() > 0) {
			for (MatchGroup matchGroup : groupList_) {
				MatchGroupBean matchGroupBean = new MatchGroupBean();
				matchGroupBean.setMatchGroup(matchGroup);
				List<Map<String, Object>> groupUserList = matchDao.getMatchGroupListByGroupId(matchGroup.getMgId());
				matchGroupBean.setUserInfoList(groupUserList);
				groupList.add(matchGroupBean);
			}
		}

		//是否是赛长
		Long captainCount = matchDao.getIsCaptain(matchId, WebUtil.getUserIdBySessionId());
		Boolean isCaptain = false;
		if (captainCount > 0) {
			isCaptain = true;
		}

		result.put("matchInfo", matchInfo);
		result.put("watchList", watchList);
		result.put("captainList", captainList);
		result.put("groupList", groupList);
		result.put("isCaptain", isCaptain);
		return result;
	}

	/**
	 * 当前登录用户是否是赛长
	 */
	public boolean getIsCaptain(Long matchId) {
		Long captainCount = matchDao.getIsCaptain(matchId, WebUtil.getUserIdBySessionId());
		Boolean isCaptain = false;
		if (captainCount > 0) {
			isCaptain = true;
		}
		return isCaptain;
	}

	/**
	 * 获取值
	 *
	 * @param map
	 * @param key
	 */
	public String getName(Map<String, Object> map, String key) {
		if (map == null || map.get(key) == null) {
			return null;
		} else {
			return map.get(key).toString();
		}
	}

	/**
	 * 获取long
	 *
	 * @param map
	 * @param key
	 */
	public Long getLongValue(Map<String, Object> map, String key) {
		if (map == null || map.get(key) == null) {
			return null;
		} else {
			return Long.parseLong(map.get(key).toString());
		}
	}

	/**
	 * Integer
	 *
	 * @param map
	 * @param key
	 */
	public Integer getIntegerValue(Map<String, Object> map, String key) {
		if (map == null || map.get(key) == null) {
			return 0;
		} else {
			return Integer.parseInt(map.get(key).toString());
		}
	}

	/**
	 * 获取全部围观列表
	 *
	 * @return
	 */
	public List<Map<String, Object>> getMoreWatchUserList(Long matchId) {
		POJOPageInfo pageInfo = new POJOPageInfo(0, 1);
		List<Map<String, Object>> watchList = matchDao.getUserListByMatchId(matchId, 0, pageInfo);
		return watchList;
	}

	/**
	 * 点击进入比赛详情——获取参赛球队信息和比赛详情
	 *
	 * @return
	 */
	public Map<String, Object> getMatchDetailInfo(Long matchId) {
		Map<String, Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		result.put("matchInfo", matchInfo);
		//获取比赛球队信息
		String teamIds = matchInfo.getMiJoinTeamIds();
		if (StringUtils.isNotEmpty(teamIds)) {
			List<Long> teamIdList = getLongTeamIdList(teamIds);
			List<TeamInfo> teamList = matchDao.getTeamListByIds(teamIdList);
			if (teamList != null && teamList.size() > 0) {
				for (TeamInfo teamInfo : teamList) {
					teamInfo.setTiLogo(PropertyConst.DOMAIN + teamInfo.getTiLogo());
				}
			}
			result.put("teamList", teamList);
		}
		//获取成绩上报球队信息
		if (StringUtils.isNotEmpty(matchInfo.getMiReportScoreTeamId())) {
			List<Long> reportScoreTeamIdList = getLongTeamIdList(matchInfo.getMiReportScoreTeamId());
			List<TeamInfo> reportScoreTeamList = matchDao.getTeamListByIds(reportScoreTeamIdList);
			result.put("reportScoreTeamList", reportScoreTeamList);
		}
		return result;
	}

	public List<Long> getLongTeamIdList(String teamIds) {
		List<Long> teamIdList = new ArrayList<>();
		String[] ids = teamIds.split(",");
		for (String id : ids) {
			if (StringUtils.isNotEmpty(id)) {
				teamIdList.add(Long.parseLong(id));
			}
		}
		return teamIdList;
	}


	/**
	 * 报名-获取本场比赛详情 参赛球队、比赛信息、上报球队
	 *
	 * @return
	 */
	public Map<String, Object> getMatchInfoById(Long wcId) {
		Map<String, Object> result = new HashMap<>();
		//比赛信息
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, wcId);
		matchInfo.setMiHit(matchInfo.getMiHit() == null ? 1 : matchInfo.getMiHit() + 1);
		matchDao.update(matchInfo);
		result.put("matchInfo", matchInfo);
		//参赛球队
		List<Long> teamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
		List<TeamInfo> teamInfoList = matchDao.getTeamListByIds(teamIdList);
		result.put("teamInfoList", teamInfoList);
		//成绩上报球队
		List<Long> reportScoreTeamIdList = getLongTeamIdList(matchInfo.getMiReportScoreTeamId());
		List<TeamInfo> scoreReportTeamInfoList = matchDao.getTeamListByIds(reportScoreTeamIdList);
		result.put("scoreReportTeamInfoList", scoreReportTeamInfoList);
		return result;
	}

	/**
	 * 创建比赛
	 *
	 * @return
	 */
	public TeamInfo getTeamInfoById(String teamId) {
		return matchDao.get(TeamInfo.class, Long.parseLong(teamId));
	}


	/**
	 * 创建比赛—点击球场-获取分区和洞
	 *
	 * @return
	 */
	public List<Object[]> getParkZoneAndHole(Long parkId) {
		return matchDao.getParkZoneAndHole(parkId);
	}

	/**
	 * 查询球场区域
	 *
	 * @return
	 */
	public POJOPageInfo getParkListByRegion(SearchBean searchBean, POJOPageInfo pageInfo) {
		return matchDao.getParkListByRegion(searchBean, pageInfo);
	}

	/**
	 * 查询该区域下的球场
	 *
	 * @return
	 */
	public POJOPageInfo getParkListByRegionName(SearchBean searchBean, POJOPageInfo pageInfo) {
		return matchDao.getParkListByRegionName(searchBean, pageInfo);
	}


	/**
	 * 保存一条用户记分对应关系
	 *
	 * @return
	 */
	public void saveUserScoreMapping(Long matchId, Long groupId, Long scorerId) {
		MatchScoreUserMapping mapping = new MatchScoreUserMapping();
		mapping.setMsumMatchId(matchId);
		mapping.setMsumGroupId(groupId);
		mapping.setMsumScorerId(scorerId);
		mapping.setMsumCreateTime(System.currentTimeMillis());
		matchDao.save(mapping);
	}

	/**
	 * 点击组内用户头像，判断是否能给该用户记分 跳转记分卡页面
	 *
	 * @return
	 */
	public boolean getScoreType(SearchBean searchBean) {
		Map<String, Object> parp = searchBean.getParps();
		//判断是否赛长
		Long isCaptain = matchDao.getIsCaptain((Long) parp.get("matchId"), (Long) parp.get("userId"));
		if (isCaptain > 0) {
			return true;
		}
		//判断是否可记分
		Long count = matchDao.getScoreTypeCount(searchBean);
		if (count > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 保存分组
	 *
	 * @return
	 */
	public void addMatchGroup(Long matchId, String groupName) {
		Long userId = UserUtil.getUserId();
		String userName = UserUtil.getShowName();
		MatchUserGroupMapping groupMapping = new MatchUserGroupMapping();
		groupMapping.setMugmMatchId(matchId);
		groupMapping.setMugmGroupName(groupName);
		groupMapping.setMugmUserId(userId);
		groupMapping.setMugmUserName(userName);
		groupMapping.setMugmCreateTime(System.currentTimeMillis());
		groupMapping.setMugmCreateUserId(userId);
		groupMapping.setMugmCreateUserName(userName);
		matchDao.save(groupMapping);
	}

	/**
	 * 报名-加入本组本人加入本组
	 *
	 * @return
	 */
	public void updateMatchGroup(Long matchId, Long groupId, Long userId) {
		String userName = UserUtil.getShowName();
		MatchGroup matchGroup = matchDao.get(MatchGroup.class, groupId);
		MatchUserGroupMapping groupMapping = new MatchUserGroupMapping();
		groupMapping.setMugmMatchId(matchId);
		groupMapping.setMugmGroupId(groupId);
		groupMapping.setMugmGroupName(matchGroup.getMgGroupName());
		groupMapping.setMugmUserId(userId);
		groupMapping.setMugmUserName(userName);
		groupMapping.setCreate();
		matchDao.save(groupMapping);
	}

	/**
	 * 报名-加入本组-赛长选中一些球友加入本组
	 *
	 * @return
	 */
	public void updateMatchGroupByCaption(Long matchId, Long groupId, String userIds) {
		if (StringUtils.isEmpty(userIds)) {
			String[] ids = userIds.split(",");
			for (String userId : ids) {
				String userName = UserUtil.getShowName();
				MatchGroup matchGroup = matchDao.get(MatchGroup.class, groupId);
				MatchUserGroupMapping groupMapping = new MatchUserGroupMapping();
				groupMapping.setMugmMatchId(matchId);
				groupMapping.setMugmGroupId(groupId);
				//普通球友
				groupMapping.setMugmUserType(MatchGroupUserMappingTypeEnum.ORDINARY.ordinal());
				groupMapping.setMugmGroupName(matchGroup.getMgGroupName());
				groupMapping.setMugmUserId(Long.parseLong(userId));
				groupMapping.setMugmUserName(userName);
				groupMapping.setCreate();
				matchDao.save(groupMapping);
			}
		}
	}


	/**
	 * 报名——获取比赛赛长和分组
	 *
	 * @return
	 */
	public List<MatchUserGroupMappingBean> getMatchGroupMappingList(Long matchId) {
		List<MatchUserGroupMapping> mappingList = matchDao.getMatchGroupMappingList(matchId);
		List<MatchUserGroupMappingBean> list = new ArrayList<MatchUserGroupMappingBean>();
		if (mappingList != null && mappingList.size() > 0) {
			MatchUserGroupMappingBean bean = new MatchUserGroupMappingBean();
			for (MatchUserGroupMapping mapping : mappingList) {
				bean.setGroupId(mapping.getMugmGroupId());
				bean.setGroupName(mapping.getMugmGroupName());
				bean.setUserType(mapping.getMugmUserType());

				UserInfo userInfo = matchDao.get(UserInfo.class, mapping.getMugmUserId());
				userInfo.setUiId(userInfo.getUiId());
				userInfo.setUiRealName(userInfo.getUiRealName());
				userInfo.setUiHeadimg(userInfo.getUiHeadimg());
				bean.getUserList().add(userInfo);

				//是否队长不一样，分组名称不一样
				if (!bean.getUserType().equals(mapping.getMugmUserType()) ||
						!bean.getGroupId().equals(mapping.getMugmGroupId())) {
					list.add(bean);
				}
			}
		}
		return list;
	}


	/**
	 * 取消报名，退出分组 到临时分组    赛长将多个球友退出分组
	 *
	 * @return
	 */
	public void quitMatchGroup(Long matchId, Long groupId, Long userId, String userIds) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId", matchId);
		parp.put("groupId", groupId);
		if (StringUtils.isNotEmpty(userIds)) {
			String[] ids = userIds.split(",");
			List<Long> userIdList = new ArrayList<Long>();
			for (String id : ids) {
				if (StringUtils.isNotEmpty(id)) {
					userIdList.add(Long.parseLong(id));
				}

			}
			parp.put("userIdList", userIdList);
		} else if (userId != null) {
			parp.put("userId", userId);
		}
		matchDao.updateMyMatchGroupMapping(parp);
	}

	/**
	 * 获取临时分组中的球友
	 *
	 * @param matchId 比赛id
	 * @param groupId 比赛分组id
	 * @return
	 */
	public List<MatchUserGroupMapping> getUserByTemporary(Long matchId, Long groupId) {
		return matchDao.getUserByTemporary(matchId, groupId);
	}

	/**
	 * 创建比赛-保存-自动成为赛长
	 *
	 * @return
	 */
	public void saveMatchInfo(MatchInfo matchInfo, String parkName) {
		ParkInfo parkInfo = matchDao.getParkIdByName(parkName);
		if (parkInfo != null) {
			matchInfo.setMiParkId(parkInfo.getPiId());
		}
		matchInfo.setMiParkName(parkName);
		matchInfo.setMiType(1);
		matchInfo.setMiCreateTime(System.currentTimeMillis());
		matchInfo.setMiCreateUserId(WebUtil.getUserIdBySessionId());
		matchInfo.setMiCreateUserName(WebUtil.getUserNameBySessionId());
		matchInfo.setMiIsValid(1);
		//0：报名中  1进行中  2结束
		matchInfo.setMiIsEnd(0);
		matchDao.save(matchInfo);

		//创建比赛分组
		MatchGroup matchGroup = new MatchGroup();
		matchGroup.setMgMatchId(matchInfo.getMiId());
		matchGroup.setMgGroupName("第1组");
		matchGroup.setMgCreateTime(System.currentTimeMillis());
		matchGroup.setMgCreateUserId(WebUtil.getUserIdBySessionId());
		matchGroup.setMgCreateUserName(WebUtil.getUserNameBySessionId());
		matchDao.save(matchGroup);

		//创建用户分组mapping 创建人自动成为赛长
		MatchUserGroupMapping matchUserGroupMapping = new MatchUserGroupMapping();
		matchUserGroupMapping.setMugmMatchId(matchInfo.getMiId());
		matchUserGroupMapping.setMugmUserType(1);
		matchUserGroupMapping.setMugmGroupId(matchGroup.getMgId());
		matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
		matchUserGroupMapping.setMugmUserId(WebUtil.getUserIdBySessionId());
		matchUserGroupMapping.setMugmUserName(WebUtil.getUserNameBySessionId());
		matchUserGroupMapping.setMugmCreateUserId(WebUtil.getUserIdBySessionId());
		matchUserGroupMapping.setMugmCreateUserName(WebUtil.getUserNameBySessionId());
		matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
		matchDao.save(matchUserGroupMapping);

		//如果是队际赛 每个队的队长自动成为赛长
		if (StringUtils.isNotEmpty(matchInfo.getMiJoinTeamIds())) {
			List<Long> teamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
			List<Long> teamCaptainList = matchDao.getTeamCaptailByTeamIds(teamIdList);
			if (teamCaptainList != null && teamCaptainList.size() > 0) {
				for (Long captainUserId : teamCaptainList) {
					//是否存在
					if (captainUserId.equals(WebUtil.getUserIdBySessionId())) {
						continue;
					}
					MatchUserGroupMapping mugm = new MatchUserGroupMapping();
					mugm.setMugmMatchId(matchInfo.getMiId());
					mugm.setMugmUserType(1);
					mugm.setMugmGroupId(matchGroup.getMgId());
					mugm.setMugmGroupName(matchGroup.getMgGroupName());
					mugm.setMugmUserId(captainUserId);
					UserInfo userInfo = matchDao.get(UserInfo.class, captainUserId);
					mugm.setMugmUserName(userInfo.getUiRealName());
					mugm.setMugmCreateUserId(WebUtil.getUserIdBySessionId());
					mugm.setMugmCreateUserName(WebUtil.getUserNameBySessionId());
					mugm.setMugmCreateTime(System.currentTimeMillis());
					matchDao.save(mugm);
				}
			}
		}
	}

	/**
	 * 赛长——本组用户列表
	 *
	 * @return
	 */
	public Map<String, Object> getUserListByMatchIdGroupId(Long matchId, Long groupId) {
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> list = matchDao.getUserListByMatchIdGroupId(matchId, groupId);
		result.put("applyUserList", list);
		result.put("userCount", list.size());
		return result;
	}

	/**
	 * 单练——开始记分——保存数据
	 *
	 * @return
	 */
	public Map<String, Object> saveSinglePlay(String parkName, String playTime, Integer peopleNum, String digest,
											  String beforeZoneName, String afterZoneName) {
		Map<String, Object> result = new HashMap<>();
		//获取随机用户最大的用户id
		Long maxOtherUserId = matchDao.getMaxOtherUserId();


		MatchInfo matchInfo = new MatchInfo();
		ParkInfo parkInfo = matchDao.getParkIdByName(parkName);
		if (parkInfo != null) {
			matchInfo.setMiParkId(parkInfo.getPiId());
		}
		matchInfo.setMiTitle(WebUtil.getUserNameBySessionId() + "的单练");
		matchInfo.setMiParkName(parkName);
		matchInfo.setMiZoneBeforeNine(beforeZoneName);
		matchInfo.setMiZoneAfterNine(afterZoneName);
		matchInfo.setMiType(0);
		matchInfo.setMiMatchTime(playTime);
		matchInfo.setMiPeopleNum(peopleNum);
		matchInfo.setMiDigest(digest);
		matchInfo.setMiJoinOpenType(3);
//		赛制1( 0:比杆 、1:比洞)
		matchInfo.setMiMatchFormat1(0);
//		赛制2( 0:个人 、1:双人)
		matchInfo.setMiMatchFormat2(0);
		matchInfo.setMiIsEnd(1);
		matchInfo.setMiCreateTime(System.currentTimeMillis());
		matchInfo.setMiCreateUserId(WebUtil.getUserIdBySessionId());
		matchInfo.setMiCreateUserName(WebUtil.getUserNameBySessionId());
		matchDao.save(matchInfo);

		//创建分组
		MatchGroup matchGroup = new MatchGroup();
		matchGroup.setMgMatchId(matchInfo.getMiId());
		matchGroup.setMgGroupName("第1组");
		matchGroup.setMgCreateUserId(WebUtil.getUserIdBySessionId());
		matchGroup.setMgCreateUserName(WebUtil.getUserNameBySessionId());
		matchGroup.setMgCreateTime(System.currentTimeMillis());
		matchDao.save(matchGroup);

		//保存我的用户分组信息
		MatchUserGroupMapping matchUserGroupMapping = new MatchUserGroupMapping();
		matchUserGroupMapping.setMugmMatchId(matchInfo.getMiId());
		matchUserGroupMapping.setMugmUserType(1);
		matchUserGroupMapping.setMugmGroupId(matchGroup.getMgId());
		matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
		matchUserGroupMapping.setMugmUserId(WebUtil.getUserIdBySessionId());
		matchUserGroupMapping.setMugmUserName(WebUtil.getUserNameBySessionId());
		matchUserGroupMapping.setMugmCreateUserId(WebUtil.getUserIdBySessionId());
		matchUserGroupMapping.setMugmCreateUserName(WebUtil.getUserNameBySessionId());
		matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
		matchDao.save(matchUserGroupMapping);


		if(maxOtherUserId == null){
			maxOtherUserId = 1000001L;
		}

		//随机生成几个用户队友
		for (int i = 1; i <= peopleNum; i++) {
			MatchUserGroupMapping otherPeople = new MatchUserGroupMapping();
			otherPeople.setMugmMatchId(matchInfo.getMiId());
			Long i_ = Long.valueOf(i);
			maxOtherUserId += i_;
			otherPeople.setMugmUserId(maxOtherUserId);
			otherPeople.setMugmUserType(0);
			otherPeople.setMugmGroupId(matchGroup.getMgId());
			otherPeople.setMugmGroupName(matchGroup.getMgGroupName());
			otherPeople.setMugmUserName("球友" + i);
			otherPeople.setMugmCreateUserId(WebUtil.getUserIdBySessionId());
			otherPeople.setMugmCreateUserName(WebUtil.getUserNameBySessionId());
			otherPeople.setMugmCreateTime(System.currentTimeMillis());
			matchDao.save(otherPeople);
		}
		result.put("matchId", matchInfo.getMiId());
		result.put("groupId", matchGroup.getMgId());
		return result;
	}

	/**
	 * 单练——查询是否有我正在进行的单练
	 *
	 * @return
	 */
	public Map<String, Object> getMySinglePlay() {
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> parp = TimeUtil.getThisDayTime();
		Long userId = WebUtil.getUserIdBySessionId();
		parp.put("userId", userId);
		MatchInfo matchInfo = matchDao.getMySinglePlay(parp);
		result.put("matchInfo", matchInfo);
		if (matchInfo != null) {
			//我所在的组id
			MatchGroup matchGroup = matchDao.getMyGroupById(matchInfo.getMiId());
			result.put("groupId", matchGroup.getMgId());
		}
		result.put("matchType", 0);
		return result;
	}

	/**
	 * 比赛详情——添加组
	 *
	 * @return
	 */
	public void addGroupByTeamId(Long matchId) {
		//获取最大组
		MatchGroup maxGroup = matchDao.getMaxGroupByMatchId(matchId);
		String groupName = maxGroup.getMgGroupName();
		Integer max = Integer.parseInt(groupName.substring(1, groupName.length() - 1));
		max++;
		MatchGroup group = new MatchGroup();
		group.setMgMatchId(matchId);
		group.setMgGroupName("第" + max + "组");
		group.setMgCreateUserId(WebUtil.getUserIdBySessionId());
		group.setMgCreateUserName(WebUtil.getUserNameBySessionId());
		group.setMgCreateTime(System.currentTimeMillis());
		matchDao.save(group);
	}

	/**
	 * 比赛详情——赛长获取已经报名的用户
	 *
	 * @return
	 */
	public Map<String, Object> getApplyUserByMatchId(Long matchId, Long groupId) {
//		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
//		if(StringUtils.isEmpty(matchInfo.getMiJoinTeamIds())){
		//也从临时报名的用户中选
//			return matchDao.getApplyUserByMatchId(matchId);
//		}
		//从球队中选 去除已经参赛的用户
		//参赛球队
//		List<Long> teamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
//		List<Map<String, Object>> userList = matchDao.getApplyUserListByMatchId(matchId,teamIdList);
		//也从临时报名的用户中选
//		List<Map<String, Object>> linshiUserList =
		Map<String, Object> result = new HashMap<>();
		//本组人数
		Long count = matchDao.getGroupUserCountById(matchId, groupId);
		result.put("userCount", count);
		List<Map<String, Object>> applyUserList = matchDao.getApplyUserByMatchId(matchId);
		result.put("applyUserList", applyUserList);
		return result;
	}

	/**
	 * 比赛详情——保存——将用户加入该分组
	 *
	 * @return
	 */
	public void addUserToGroupByMatchId(Long matchId, Long groupId, String userIds) {
		if (StringUtils.isNotEmpty(userIds)) {
			userIds = userIds.replace("[", "");
			userIds = userIds.replace("]", "");
			userIds = userIds.replace("\"", "");
			String[] uIds = userIds.split(",");
			MatchGroup matchGroup = matchDao.get(MatchGroup.class, groupId);
			for (String userId : uIds) {
				if (StringUtils.isNotEmpty(userId)) {
					Long uId = Long.parseLong(userId);
					TeamUserMapping teamUserMapping = matchDao.getTeamUserMappingByUserId(uId);
					MatchUserGroupMapping matchUserGroupMapping = new MatchUserGroupMapping();
					matchUserGroupMapping.setMugmMatchId(matchId);
					matchUserGroupMapping.setMugmTeamId(teamUserMapping.getTumTeamId());
					matchUserGroupMapping.setMugmGroupId(groupId);
					matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
					matchUserGroupMapping.setMugmUserType(0);
					matchUserGroupMapping.setMugmUserId(uId);
					matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
					matchUserGroupMapping.setMugmCreateUserId(WebUtil.getUserIdBySessionId());
					matchUserGroupMapping.setMugmCreateUserName(WebUtil.getUserNameBySessionId());
					UserInfo userInfo = matchDao.get(UserInfo.class, uId);
					matchUserGroupMapping.setMugmUserName(userInfo.getUiRealName());
					matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
					matchUserGroupMapping.setMugmCreateUserId(WebUtil.getUserIdBySessionId());
					matchUserGroupMapping.setMugmCreateUserName(WebUtil.getUserNameBySessionId());
					matchDao.save(matchUserGroupMapping);
					//将该用户从报名表删除
					matchDao.delUserFromApply(matchId, uId);
				}
			}
		}
	}

	/**
	 * 比赛详情——保存——将用户从该分组删除
	 *
	 * @return
	 */
	public void delUserByMatchIdGroupId(Long matchId, Long groupId, String userIds) {
		if (StringUtils.isNotEmpty(userIds)) {
			userIds = userIds.replace("[", "");
			userIds = userIds.replace("]", "");
			userIds = userIds.replace("\"", "");
			String[] uIds = userIds.split(",");
			for (String userId : uIds) {
				if (StringUtils.isNotEmpty(userId)) {
					matchDao.delUserByMatchIdGroupId(matchId, groupId, Long.parseLong(userId));
				}
			}
		}
	}

	/**
	 * 获取参赛球队列表
	 *
	 * @return
	 */
	public List<Map<String, Object>> getTeamListByMatchId(Long matchId) {
		if (matchId != null) {
			MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
			List<Long> teamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
			return matchDao.getApplyUserListByMatchId(matchId, teamIdList);
		} else {
			return null;
		}
	}

	/**
	 * 通过matchid和groupid查询本组记分卡信息
	 *
	 * @return
	 */
	public Map<String, Object> getScoreCardInfoByGroupId(Long matchId, Long groupId) {
		Map<String, Object> result = new HashMap<>();
		List<MatchGroupUserScoreBean> list = new ArrayList<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		//本组用户
		List<Map<String, Object>> userList = null;
		if(matchInfo.getMiType() == 1){
			userList = matchDao.getUserListById(matchId, groupId);
		}else{
			//单练
			userList = matchDao.getSingleUserListById(matchId, groupId);
		}
		result.put("userList", userList);

		//半场球洞
		List<Map<String, Object>> parkHoleList = matchDao.getParkPartitionList(matchId);

		//第一条记录（半场分区信息）
		MatchGroupUserScoreBean thBean = new MatchGroupUserScoreBean();
		thBean.setUserId(0L);
		thBean.setUserName("球洞球杆");
		thBean.setUserScoreList(parkHoleList);
		list.add(thBean);

		//本组用户每个洞得分情况
		if (userList != null && userList.size() > 0) {
			for (Map<String, Object> user : userList) {
				Long uiId = getLongValue(user, "uiId");
				MatchGroupUserScoreBean bean = new MatchGroupUserScoreBean();
				bean.setUserId(uiId);
				bean.setUserName(getName(user, "uiRealName"));
				//本用户得分情况
				if (uiId != null) {
					List<Map<String, Object>> uscoreList = matchDao.getScoreByUserId(groupId, uiId, matchInfo);
					if (uscoreList != null && uscoreList.size() > 0) {
						bean.setUserScoreList(uscoreList);
						list.add(bean);
					}
				} else {
					List<Map<String, Object>> uscoreList = new ArrayList<>();
					for (int i = 0; i < parkHoleList.size(); i++) {
						Map<String, Object> m = new HashMap<>();
						uscoreList.add(m);
					}
					bean.setUserScoreList(uscoreList);
					list.add(bean);
				}
			}
		}
		result.put("parkHoleList", list);

		//总杆数
		Long totalRod = matchDao.getTotalRod(matchInfo);
		result.put("totalRod", totalRod);
		//用户总分
		List<Map<String, Object>> totalScoreList = matchDao.getTotalScoreWithUser(matchId, groupId);
		result.put("totalScoreList", totalScoreList);
		return result;
	}

	/**
	 * 保存或更新计分数据
	 *
	 * @return
	 */
	public void saveOrUpdateScore(Long userId, Long matchId, Long groupId, Long scoreId, String holeName,
								  Integer holeNum, Integer holeStandardRod, String isUp, Integer rod, String rodCha,
								  Integer pushRod, Integer beforeAfter) {
		if (scoreId != null) {
			MatchScore scoreDb = matchDao.get(MatchScore.class, scoreId);
			scoreDb.setMsIsUp(isUp);
			//杆差=杆数-本洞标准杆数
			if(rod != null){
				scoreDb.setMsRodNum(rod);
				scoreDb.setMsRodCha(rod - holeStandardRod);
			}else{
				Integer gc = 0;
				if(rodCha.contains("+")){
					gc = Integer.parseInt(rodCha.substring(1));
					scoreDb.setMsRodCha(gc);
				}else{
					gc = Integer.parseInt(rodCha);
					scoreDb.setMsRodCha(gc);
				}
				//杆数=标准杆+杆差
				scoreDb.setMsRodNum(holeStandardRod+gc);
			}
			scoreDb.setMsPushRodNum(pushRod);
			scoreDb.setMsUpdateTime(System.currentTimeMillis());
			scoreDb.setMsUpdateUserId(WebUtil.getUserIdBySessionId());
			scoreDb.setMsUpdateUserName(WebUtil.getUserNameBySessionId());
			matchDao.update(scoreDb);
		} else {
			MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
			MatchGroup matchGroup = matchDao.get(MatchGroup.class, groupId);
			Long teamId = null;
			if (StringUtils.isNotEmpty(matchInfo.getMiJoinTeamIds())) {
				List<Long> teamIds = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
				List<Long> teamIdList = matchDao.getTeamIds(teamIds, userId);
				if (teamIdList != null && teamIdList.size() > 0) {
					teamId = teamIdList.get(0);
				}
			}

			MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId, groupId, userId);
			MatchScore score = new MatchScore();
			score.setMsTeamId(teamId);
			score.setMsMatchId(matchId);
			score.setMsMatchTitle(matchInfo.getMiTitle());
			score.setMsGroupId(groupId);
			score.setMsGroupName(matchGroup.getMgGroupName());
			score.setMsUserId(userId);
			score.setMsUserName(matchUserGroupMapping.getMugmUserName());
			score.setMsBeforeAfter(beforeAfter);
			score.setMsHoleName(holeName);
			score.setMsHoleNum(holeNum);
			score.setMsIsUp(isUp);
			if(rod != null){
				score.setMsRodNum(rod);
				score.setMsRodCha(rod - holeStandardRod);
			}else{
				Integer gc = 0;
				if(rodCha.contains("+")){
					gc = Integer.parseInt(rodCha.substring(1));
					score.setMsRodCha(gc);
				}else{
					gc = Integer.parseInt(rodCha);
					score.setMsRodCha(gc);
				}
				//杆数=标准杆+杆差
				score.setMsRodNum(holeStandardRod+gc);
			}
			score.setMsPushRodNum(pushRod);
			score.setMsCreateUserId(WebUtil.getUserIdBySessionId());
			score.setMsCreateUserName(WebUtil.getUserNameBySessionId());
			score.setMsCreateTime(System.currentTimeMillis());
			matchDao.save(score);
		}
	}

	/**
	 * 保存或更新比赛状态
	 * state   0：报名中  1进行中  2结束
	 *
	 * @return
	 */
	public void updateMatchState(Long matchId, Integer state) {
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		matchInfo.setMiIsEnd(state);
		matchInfo.setMiUpdateUserId(WebUtil.getUserIdBySessionId());
		matchInfo.setMiUpdateUserName(WebUtil.getUserNameBySessionId());
		matchInfo.setMiUpdateTime(System.currentTimeMillis());
		matchDao.update(matchInfo);
	}

	/**
	 * 成绩上报 杆数越少 代表成绩越好
	 * @param scoreType 积分规则 1：杆差倍数  2：赢球奖分,
	 * @param baseScore 基础分,
	 * @param rodScore  杆差倍数,
	 * @param winScore  赢球奖分
	 * @return
	 */
	public boolean submitScoreByTeamId(Long matchId, Long teamId, Integer scoreType, Integer baseScore, Integer rodScore, Integer winScore) {
		//更新配置
		boolean flag = saveOrUpdateConfig(matchId, teamId, baseScore, rodScore, winScore);
		if(flag){
			//计算得分
			MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
			Integer format = matchInfo.getMiMatchFormat1();
			if (format == 0) {
				//比杆赛
				//积分“杆差倍数”和“赢球奖分”只能二选  球友积分=基础积分+（144-球友比分）*杆差倍数
				//或者 球友积分=基础积分+赢球奖分/比赛排名
				updatePointByRodScore(matchId, teamId, scoreType, baseScore, rodScore, winScore);
			} else if (format == 1) {
				//比洞赛
				// 比洞赛积分，只计算“基础积分”和“赢球奖分”两项，其中输球的组赢球奖分为0，打平的为一半。
				// 计算公式为：球友积分=基础积分+赢球奖分
				updatePointByHoleScore(matchId, teamId, baseScore, winScore);
			}
			return true;
		}
		return false;
	}

	private boolean saveOrUpdateConfig(Long matchId, Long teamId,Integer baseScore, Integer rodScore, Integer winScore) {
		IntegralConfig config = matchDao.getSubmitScoreConfig(matchId,teamId);
		if(config == null){
			config = new IntegralConfig();
			config.setIcMatchId(matchId);
			config.setIcTeamId(teamId);
			config.setIcBaseScore(baseScore);
			config.setIcRodCha(rodScore);
			config.setIcWinScore(winScore);
			config.setIcCreateTime(System.currentTimeMillis());
			config.setIcCreateUserId(WebUtil.getUserIdBySessionId());
			config.setIcCreateUserName(WebUtil.getUserNameBySessionId());
			matchDao.save(config);
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 成绩上报 比杆赛
	 * 积分“杆差倍数”和“赢球奖分”只能二选
	 * @param scoreType 积分规则 1：杆差倍数  2：赢球奖分,
	 * 杆差倍数 ：球友积分=基础积分+（144-球友比分）*杆差倍数
	 * 赢球奖分 ：球友积分=基础积分+赢球奖分/比赛排名
	 */
	private void updatePointByRodScore(Long matchId, Long teamId, Integer scoreType, Integer baseScore, Integer rodScore, Integer winScore) {
		Integer point = 0;
		if (scoreType == 1) {
			//杆差倍数 球友积分=基础积分+（144-球友比分）*杆差倍数
			//获取该队伍的得分情况
			List<Map<String, Object>> matchScoreList = matchDao.getSumScoreListByMatchIdTeamId(matchId, teamId);
			if (matchScoreList != null && matchScoreList.size() > 0) {
				for (Map<String, Object> scoreMap : matchScoreList) {
					//杆数
					Integer score = getIntegerValue(scoreMap, "sumRodNum");
					Long userId = getLongValue(scoreMap, "userId");
					point = baseScore + (144 - score) * rodScore;
					//更新该球友原先的积分情况
					updatePointByIds(teamId, userId, point);
				}
			}
		} else {
			//赢球奖分 球友积分=基础积分+赢球奖分/比赛排名
			//计算杆数的总排名
			List<Map<String, Object>> matchScoreList = matchDao.getRankingListByMatchId(matchId);
			if (matchScoreList != null && matchScoreList.size() > 0) {
				for (Map<String, Object> scoreMap : matchScoreList) {
					Long teamIdByScore = getLongValue(scoreMap, "teamId");
					if(teamId.equals(teamIdByScore)){
						Long userId = getLongValue(scoreMap, "userId");
						//排名
						Integer rank = getIntegerValue(scoreMap,"rank");
						//计算得分
						point = baseScore + winScore/rank;
						//本队球友
						//更新该球友原先的积分情况
						updatePointByIds(teamIdByScore, userId, point);

					}
				}
			}
		}
	}


	/**
	 * 更新该球友原先的积分情况
	 */
	private void updatePointByIds(Long teamId, Long userId, Integer point) {
		TeamUserMapping teamUserMapping = matchDao.getTeamUserMappingByIds(teamId, userId);
		Integer oldPoint = teamUserMapping.getTumPoint();
		if (oldPoint == null || oldPoint == 0) {
			teamUserMapping.setTumPoint(point);
		} else {
			teamUserMapping.setTumPoint(oldPoint + point);
		}
		teamUserMapping.setTumUpdateTime(System.currentTimeMillis());
		teamUserMapping.setTumUpdateUserId(WebUtil.getUserIdBySessionId());
		teamUserMapping.setTumUpdateUserName(WebUtil.getUserNameBySessionId());
		matchDao.update(teamUserMapping);
	}


	/**
	 * 成绩上报 比洞赛
	 * 积分只计算“基础积分”和“赢球奖分”两项，其中输球的组赢球奖分为0，打平的为一半。
	 * 计算公式为：球友积分=基础积分+赢球奖分
	 */
	private void updatePointByHoleScore(Long matchId, Long teamId, Integer baseScore, Integer winScore) {
	}

	/**
	 * 结束单练
	 *
	 * @return
	 */
	public void endSingleMatchById(Long matchId) {
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		matchInfo.setMiIsEnd(2);
		matchInfo.setMiUpdateTime(System.currentTimeMillis());
		matchInfo.setMiUpdateUserId(WebUtil.getUserIdBySessionId());
		matchInfo.setMiUpdateUserName(WebUtil.getUserNameBySessionId());
		matchDao.update(matchInfo);
	}

	/**
	 * 如果不是参赛人员，则加入围观用户
	 *
	 * @return
	 */
	public void saveOrUpdateWatch(Long matchId) {
		Long userId = WebUtil.getUserIdBySessionId();
		//是否已经围观
		Long watchCount = matchDao.getisApplyOrWatch(userId,matchId,0);
		if(watchCount <=0){
			Long count = matchDao.getIsContestants(userId, matchId);
			if (count == 0) {
				//加入围观用户
				MatchJoinWatchInfo matchJoinWatchInfo = new MatchJoinWatchInfo();
				matchJoinWatchInfo.setMjwiUserId(userId);
				matchJoinWatchInfo.setMjwiMatchId(matchId);
				matchJoinWatchInfo.setMjwiType(0);
				matchJoinWatchInfo.setMjwiCreateTime(System.currentTimeMillis());
				matchDao.save(matchJoinWatchInfo);
			}
		}
	}

	/**
	 * 比赛——group——总比分
	 * 罗列每个参赛球友的记分卡。其中的数字“蓝色是Par,红色是小鸟，灰色是高于标准杆的。黑色是老鹰”
	 *
	 * @return
	 */
	public Map<String, Object> getTotalScoreByMatchId(Long matchId) {
		Map<String, Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		result.put("matchInfo", matchInfo);
		List<MatchGroupUserScoreBean> list = new ArrayList<>();

		//所有球洞
		List<MatchTotalUserScoreBean> parkHoleList = new ArrayList<>();
		//每洞杆数
		List<MatchTotalUserScoreBean> parkRodList = new ArrayList<>();
		//获取前半场球洞
		List<Map<String, Object>> beforeParkHoleList = matchDao.getBeforeAfterParkPartitionList(matchId,0);
		getNewParkHoleList(parkHoleList,parkRodList,beforeParkHoleList);
		//获取后半场球洞
		List<Map<String, Object>> afterParkHoleList = matchDao.getBeforeAfterParkPartitionList(matchId,1);
		getNewParkHoleList(parkHoleList,parkRodList,afterParkHoleList);

		//第一条记录（半场分区信息）
		MatchGroupUserScoreBean thBean = new MatchGroupUserScoreBean();
		thBean.setUserId(0L);
		thBean.setUserName("Hole");
		thBean.setUserScoreTotalList(parkHoleList);
		list.add(thBean);

		//第二条记录（杆数）
		MatchGroupUserScoreBean thBean2 = new MatchGroupUserScoreBean();
		thBean2.setUserId(0L);
		thBean2.setUserName("杆差");
		thBean2.setUserScoreTotalList(parkHoleList);
		list.add(thBean2);


		//比赛的所有用户(首列显示)
		List<Map<String, Object>> userList = matchDao.getUserListById(matchId, null);
		result.put("userList", userList);

		//本组用户每个洞得分情况
		createNewUserScoreList(userList,list,matchInfo);

		result.put("list", list);
		return result;
	}

	//格式化用户半场得分
	private void createNewUserScore(List<MatchTotalUserScoreBean> userScoreList, List<Map<String, Object>> uScoreList) {
		Integer totalRod = 0;
		//杆差
		Integer totalRodCha = 0;
		for(Map<String, Object> map:uScoreList){
			MatchTotalUserScoreBean bean = new MatchTotalUserScoreBean();
			Integer rodNum = getIntegerValue(map,"rod_num");
			totalRod += rodNum;
			//杆数
			bean.setRodNum(rodNum);
			bean.setHoleStandardRod(getIntegerValue(map,"pp_hole_standard_rod"));
			userScoreList.add(bean);
		}
		//每个半场的总杆数
		MatchTotalUserScoreBean bean = new MatchTotalUserScoreBean();
		bean.setRodNum(totalRod);
		userScoreList.add(bean);
	}

	//格式化半场球洞
	private void getNewParkHoleList(List<MatchTotalUserScoreBean> parkHoleList, List<MatchTotalUserScoreBean> parkRodList,
									List<Map<String, Object>> beforeAfterParkHoleList) {
		Integer totalStandardRod = 0;
		String name = "";
		for(Map<String, Object> map:beforeAfterParkHoleList){
			MatchTotalUserScoreBean beanHole = new MatchTotalUserScoreBean();
			Integer holeStandardRod = getIntegerValue(map,"holeStandardRod");
			totalStandardRod += holeStandardRod;
			//球洞号
			beanHole.setHoleNum(getIntegerValue(map,"holeNum").toString());
			//标准杆
			beanHole.setHoleStandardRod(holeStandardRod);
			name = getName(map,"ppName");
			parkHoleList.add(beanHole);

			MatchTotalUserScoreBean beanRod = new MatchTotalUserScoreBean();
			//标准杆
			beanHole.setHoleStandardRod(holeStandardRod);
			parkRodList.add(beanRod);
		}
		//每个半场
		MatchTotalUserScoreBean bean = new MatchTotalUserScoreBean();
		bean.setHoleNum(name+"场");
		parkHoleList.add(bean);
		//每个半场的总标准杆
		MatchTotalUserScoreBean beanRod = new MatchTotalUserScoreBean();
		bean.setHoleStandardRod(totalStandardRod);
		parkRodList.add(bean);
	}

	/**
	 * 比赛——group——分队比分
	 * 显示创建比赛时“参赛范围”所选择球队的第一个，也可以选其他参赛球队
	 * 如果是该队队长，就显示“球队确认”按钮
	 *
	 * @return
	 */
	public Map<String, Object> getTeamScoreByMatchId(Long matchId, Long teamId) {
		Map<String, Object> result = new HashMap<>();
		List<MatchGroupUserScoreBean> list = new ArrayList<>();
		//获取参赛球队
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		result.put("matchInfo", matchInfo);

		//所有球洞
		List<MatchTotalUserScoreBean> parkHoleList = new ArrayList<>();
		//每洞杆数
		List<MatchTotalUserScoreBean> parkRodList = new ArrayList<>();
		//获取前半场球洞
		List<Map<String, Object>> beforeParkHoleList = matchDao.getBeforeAfterParkPartitionList(matchId,0);
		getNewParkHoleList(parkHoleList,parkRodList,beforeParkHoleList);
		//获取后半场球洞
		List<Map<String, Object>> afterParkHoleList = matchDao.getBeforeAfterParkPartitionList(matchId,1);
		getNewParkHoleList(parkHoleList,parkRodList,afterParkHoleList);

		//第一条记录（半场分区信息）
		MatchGroupUserScoreBean thBean = new MatchGroupUserScoreBean();
		thBean.setUserId(0L);
		thBean.setUserName("Hole");
		thBean.setUserScoreTotalList(parkHoleList);
		list.add(thBean);

		//第二条记录（杆数）
		MatchGroupUserScoreBean thBean2 = new MatchGroupUserScoreBean();
		thBean2.setUserId(0L);
		thBean2.setUserName("杆差");
		thBean2.setUserScoreTotalList(parkHoleList);
		list.add(thBean2);

		List<Long> teamIds = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
		List<TeamInfo> teamList = matchDao.getTeamListByIds(teamIds);
		result.put("teamList", teamList);
		if (teamId == null) {
			//如果没选球队，默认显示第一个球队
			teamId = teamList.get(0).getTiId();
		}

		//比赛的本球队所有用户(首列显示)
		List<Map<String, Object>> userList = matchDao.getUserListByTeamId(matchId, teamId);
		result.put("userList", userList);

		//本组用户每个洞得分情况
		createNewUserScoreList(userList,list,matchInfo);
		result.put("list", list);

		//是否是该球队队长
		Long isCaptain = teamService.getIsCaptain(WebUtil.getUserIdBySessionId(), teamId);
		result.put("isCaptain", isCaptain);
		return result;
	}

	//获取用户在每个球洞的得分情况
	private void createNewUserScoreList(List<Map<String, Object>> userList, List<MatchGroupUserScoreBean> list,MatchInfo matchInfo) {
		if (userList != null && userList.size() > 0) {
			for (Map<String, Object> user : userList) {
				Long uiId = getLongValue(user, "uiId");
				MatchGroupUserScoreBean bean = new MatchGroupUserScoreBean();
				bean.setUserId(uiId);
				bean.setUserName(getName(user, "uiRealName"));
				//本用户的前后半场总得分情况
				List<MatchTotalUserScoreBean> userScoreList = new ArrayList<>();
				//本用户前半场得分情况
				List<Map<String, Object>> uScoreBeforeList = matchDao.getBeforeAfterScoreByUserId(uiId, matchInfo,0);
				createNewUserScore(userScoreList, uScoreBeforeList);
				//本用户后半场得分情况
				List<Map<String, Object>> uScoreAfterList = matchDao.getBeforeAfterScoreByUserId(uiId, matchInfo,1);
				createNewUserScore(userScoreList, uScoreAfterList);
				bean.setUserScoreTotalList(userScoreList);

				list.add(bean);
			}
		}
	}

	/**
	 * 比赛——group——分队统计
	 * 比杆赛：按创建比赛时“参赛范围”选择的球队统计成绩并按平均杆数排名，（球队、参赛人数、平均杆数、总杆数、排名）
	 * 比洞赛：用不同的表。（球队、获胜组、打平组、得分、排名）
	 *
	 * @return
	 */
	public List<Map<String, Object>> getTeamTotalScoreByMatchId(Long matchId) {
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
//		赛制1( 0:比杆 、1:比洞)
		if (matchInfo.getMiMatchFormat1() == 0) {
			return matchDao.getMatchRodTotalScore(matchId);
		} else {
			List<Long> teamIds = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
			List<TeamInfo> teamInfoList = matchDao.getTeamListByIds(teamIds);
			for (TeamInfo teamInfo : teamInfoList) {
				//获取每个队伍的
			}
			return matchDao.getMatchHoleTotalScore(matchId);
		}
	}

	/**
	 * 删除比赛
	 *
	 * @return
	 */
	public boolean delMatchById(Long matchId) {
		//判断是否是我创建的
		Long count = matchDao.getIsMyCreatMatch(matchId, WebUtil.getUserIdBySessionId());
		if (count != null && count > 0) {
			matchDao.del(MatchInfo.class, matchId);
			return true;
		}
		return false;
	}

	/**
	 * 创建比赛——获取选中的参赛球队的详情
	 *
	 * @return
	 */
	public List<Map<String, Object>> getTeamListByIds(String teamIds) {
		List<Long> ids = getLongTeamIdList(teamIds);
		List<Map<String, Object>> list = matchDao.getTeamListByTeamIds(ids);
		teamService.getCaptain(list);
		return list;
	}

	/**
	 * 创建比赛—获取球场城市列表
	 * @param keyword 搜索关键字
	 * @return
	 */
	public List<String> getParkCityList(String keyword) {
		return matchDao.getParkCityList(keyword);
	}

	public MatchInfo getMatchById(Long matchId) {
		return matchDao.get(MatchInfo.class, matchId);
	}

	/**
	 * 比赛——普通用户报名
	 * @return
	 */
	public Integer applyMatch(Long matchId, Long groupId, String groupName) {
		Integer flag = -1;
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		Long userId = WebUtil.getUserIdBySessionId();
		List<Long> teamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
		//是否已经报名
		Long countApply = matchDao.getisApplyOrWatch(userId,matchId,1);
		if(countApply >0){
			return -2;
		}
		//是否参赛球队的队员
		Long count = matchDao.getIsJoinTeamsUser(userId,teamIdList);
		if(count >0){
			//获取用户所在球队id
			TeamUserMapping teamUserMapping = matchDao.getTeamUserMappingByUserId(userId);
			//是 直接加入比赛 不用审核
			MatchUserGroupMapping matchUserGroupMapping = new MatchUserGroupMapping();
			matchUserGroupMapping.setMugmMatchId(matchId);
			matchUserGroupMapping.setMugmTeamId(teamUserMapping.getTumTeamId());
			matchUserGroupMapping.setMugmUserType(0);
			matchUserGroupMapping.setMugmGroupId(groupId);
			matchUserGroupMapping.setMugmGroupName(groupName);
			matchUserGroupMapping.setMugmUserId(userId);
			UserInfo userInfo = matchDao.get(UserInfo.class,userId);
			matchUserGroupMapping.setMugmUserName(userInfo.getUiRealName());
			matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
			matchUserGroupMapping.setMugmCreateUserId(userId);
			matchUserGroupMapping.setMugmCreateUserName(userInfo.getUiRealName());
			matchDao.save(matchUserGroupMapping);
			flag = 1;
		}
//		}
		return flag;
	}

	/**
	 * 比赛——普通用户从一个组退出比赛
	 * @return
	 */
	public void quitMatch(Long matchId, Long groupId) {
		matchDao.delFromMatch(matchId,groupId,WebUtil.getUserIdBySessionId());
	}

	/**
	 * 撤销成绩上报
	 * @param matchId 比赛id,
	 * @param teamId 上报球队id,
	 * @return
	 */
	public boolean cancelScoreByTeamId(Long matchId, Long teamId) {
		//获取配置
		IntegralConfig config = matchDao.getSubmitScoreConfig(matchId,teamId);
		if(config == null){
			return false;
		}
		//计算得分
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		Integer format = matchInfo.getMiMatchFormat1();
		if (format == 0) {
			//比杆赛
			//积分“杆差倍数”和“赢球奖分”只能二选  球友积分=基础积分+（144-球友比分）*杆差倍数
			//或者 球友积分=基础积分+赢球奖分/比赛排名
		} else if (format == 1) {
			//比洞赛
			// 比洞赛积分，只计算“基础积分”和“赢球奖分”两项，其中输球的组赢球奖分为0，打平的为一半。
			// 计算公式为：球友积分=基础积分+赢球奖分
		}
		matchDao.del(config);
		return true;
	}
}
