package com.golf.golf.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.golf.common.Const;
import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.MapUtil;
import com.golf.common.util.PropertyConst;
import com.golf.common.util.TimeUtil;
import com.golf.golf.bean.*;
import com.golf.golf.dao.MatchDao;
import com.golf.golf.dao.TeamDao;
import com.golf.golf.db.*;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

/**
 * 赛事活动
 * Created by nmy on 2017/7/1.
 */
@Service
public class MatchService implements IBaseService {

	@Autowired
	private MatchDao matchDao;
    @Autowired
    private TeamDao teamDao;
	@Autowired
	private TeamService teamService;
	@Autowired
	protected WxMaService wxMaService;
	@Autowired
	protected UserService userService;

	/**
	 * 查询球场列表——区域
	 * @return
	 */
	public POJOPageInfo getParkListByCity(SearchBean searchBean, POJOPageInfo pageInfo) {
		UserInfo userInfo = userService.getUserById((Long)searchBean.getParps().get("userId"));
		pageInfo = matchDao.getParkListByRegionName(searchBean, pageInfo);
		//计算离我的距离
		if (pageInfo.getCount() > 0 && pageInfo.getItems() != null && pageInfo.getItems().size() > 0) {
			getToMyDistance(pageInfo,userInfo);
		}
		return pageInfo;
	}

	/**
	 * 查询球场列表——附近
	 * @return
	 */
	public POJOPageInfo getParkListNearby(SearchBean searchBean, POJOPageInfo pageInfo) {
		UserInfo userInfo = userService.getUserById((Long)searchBean.getParps().get("userId"));
		if(userInfo.getUiLatitude() != null && userInfo.getUiLatitude() != null){
			searchBean.addParpField("myLat", userInfo.getUiLatitude());
			searchBean.addParpField("myLng", userInfo.getUiLongitude());
		}
		pageInfo = matchDao.getParkListNearby(searchBean, pageInfo);
		//计算离我的距离
		if (pageInfo.getCount() > 0 && pageInfo.getItems() != null && pageInfo.getItems().size() > 0) {
			getToMyDistance(pageInfo,userInfo);
		}
		return pageInfo;
	}

	//计算离我的距离
	private void getToMyDistance(POJOPageInfo pageInfo, UserInfo userInfo ) {
		if (StringUtils.isNotEmpty(userInfo.getUiLatitude()) && StringUtils.isNotEmpty(userInfo.getUiLongitude())) {
			for (Map<String, Object> parkInfo : (List<Map<String, Object>>) pageInfo.getItems()) {
				if (parkInfo.get("pi_lat") != null && parkInfo.get("pi_lng") != null) {
					String distance = MapUtil.getDistance(userInfo.getUiLatitude(), userInfo.getUiLongitude(),
													parkInfo.get("pi_lat").toString(), parkInfo.get("pi_lng").toString());
					parkInfo.put("toMyDistance", Integer.parseInt(distance));
				}
			}
//			Collections.sort(pageInfo.getItems());
			Collections.sort(pageInfo.getItems(), new Comparator<Map<String, Object>>() {
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					if(o1.get("toMyDistance") != null && o2.get("toMyDistance") != null){
						int map1value = (Integer) o1.get("toMyDistance");
						int map2value = (Integer) o2.get("toMyDistance");
						return map1value - map2value;
					}
					return 0;
				}
			});
		}
	}

	/**
	 * 获取比赛列表
	 * 0：全部比赛 1：我参加的比赛  2：可报名的比赛 3:已报名的比赛  4：我创建的比赛
	 * @return
	 */
	public POJOPageInfo getMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		UserInfo userInfo = userService.getUserById((Long)searchBean.getParps().get("userId"));
		if(userInfo.getUiLatitude() != null && userInfo.getUiLatitude() != null){
			searchBean.getParps().put("myLat",userInfo.getUiLatitude());
			searchBean.getParps().put("myLng",userInfo.getUiLongitude());
		}
		Integer type = (Integer)searchBean.getParps().get("type");
		if(type <= 1){
			pageInfo = matchDao.getMatchList(searchBean, pageInfo);
		}else if(type ==2){
			//正在报名的比赛（所有正在报名的比赛，包括我创建的正在报名的比赛）
			// 先获取正在报名中的并且我不在比赛用户配置中的比赛 按距离排序
			pageInfo = matchDao.getCanJoinMatchList(searchBean, pageInfo);
			//判断我是否在参赛球队中
			if(pageInfo.getItems() != null && pageInfo.getItems().size()>0){
				checkIsInJoinTeam(pageInfo,(Long)searchBean.getParps().get("userId"));
			}
		}else if(type == 3){
			//已报名的比赛（包括我参加的报名中的比赛）
			pageInfo = matchDao.getMyJoinMatchList(searchBean, pageInfo);
		}else{
			pageInfo = matchDao.getMyMatchList(searchBean, pageInfo);
		}


		if (pageInfo.getCount() > 0 && pageInfo.getItems().size() > 0) {
			List<MatchInfo> matchInfoList = new ArrayList<>();
			for (Map<String, Object> result : (List<Map<String, Object>>) pageInfo.getItems()) {
				MatchInfo matchInfo = new MatchInfo();
				matchInfo.setMiType(getIntegerValue(result, "mi_type"));
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
				matchInfo.setMiHit(getIntegerValue(result, "userWatchCount"));
				matchInfo.setUserCount(getIntegerValue(result, "userCount"));
				matchInfo.setMiIsEnd(getIntegerValue(result, "mi_is_end"));
				if(matchInfo.getMiType() == 1){
					matchInfo.setMiLogo(PropertyConst.DOMAIN + matchInfo.getMiLogo());
				}
				//是否是赛长（显示创建比赛列表时用）
				matchInfoList.add(matchInfo);
			}
			pageInfo.setItems(matchInfoList);
			pageInfo.setCount(pageInfo.getCount());
		}
		return pageInfo;
	}


	/**
	 * 获取可报名比赛列表后——判断我是否在参赛球队中
	 * 如果没参赛球队的也要
	 * @return
	 */
	private void checkIsInJoinTeam(POJOPageInfo pageInfo, Long userId) {
		for(Iterator<Map<String,Object>> teamIterator = (Iterator<Map<String, Object>>) pageInfo.getItems().iterator(); teamIterator.hasNext();){
//		for(Map<String,Object> map:(List<Map<String,Object>>)pageInfo.getItems()){
			Map<String,Object> map = teamIterator.next();
			String joinTeamIds = getName(map,"mi_join_team_ids");
			List<Long> joinTeamIdList = getLongTeamIdList(joinTeamIds);
			if(joinTeamIdList != null && joinTeamIdList.size() >0){
				//获取我的参赛球队
				List<Long> myTeamIdList = matchDao.getMyJoinTeamList(userId);
				if(myTeamIdList != null && myTeamIdList.size()>0){
					Integer count = 0;
					for(Long joinTeam:joinTeamIdList){
						for(Long myTeam:myTeamIdList){
							if(joinTeam.equals(myTeam)){
								count ++;
								break;
							}
						}
					}
					//如果不一样的球队个数等于0，说明我不能报名这个比赛，就从列表中删除
					if(count == 0){
//						pageInfo.getItems().remove(map);
						teamIterator.remove();
					}
				}
			}
		}
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
	public Map<String, Object> getMatchInfo(MatchInfo matchInfo, Long matchId, Integer count, String openid) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (count == null) {
			count = 0;
		}
		POJOPageInfo pageInfo = new POJOPageInfo(count, 1);
		UserInfo userInfo = userService.getUserByOpenId(openid);
		//围观
		List<Map<String, Object>> watchList = matchDao.getWatchUserListByMatchId(matchId, pageInfo);

		//赛长
		List<Map<String, Object>> captainList = matchDao.getCaptainListByMatchId(matchId, pageInfo);

		//分组
		List<Map<String,Object>> groupList_ = matchDao.getMatchGroupList_(matchId,matchInfo.getMiIsEnd());
		List<MatchGroupBean> groupList = new ArrayList<>();
		if (groupList_ != null && groupList_.size() > 0) {
			for (Map<String,Object> map : groupList_) {
				MatchGroup matchGroup = new MatchGroup();
				matchGroup.setMgMatchId((Long)map.get("matchId"));
				matchGroup.setMgId((Long)map.get("groupId"));
				matchGroup.setMgGroupName((String)map.get("groupName"));

				MatchGroupBean matchGroupBean = new MatchGroupBean();
				matchGroupBean.setMatchGroup(matchGroup);
				List<Map<String, Object>> groupUserList = matchDao.getMatchGroupListByGroupId(matchId, matchGroup.getMgId());
				if(groupUserList != null && groupUserList.size()>0){
					for(Map<String, Object> user:groupUserList){
						String realName = getName(user,"uiRealName");
						if(StringUtils.isNotEmpty(realName) && realName.length() >3){
							user.put("uiRealName",realName.substring(0,3)+"...");
						}
						String nickName = getName(user,"uiNickName");
						if(StringUtils.isNotEmpty(nickName) && nickName.length() >3){
							user.put("uiNickName",nickName.substring(0,3)+"...");
						}
					}
				}
				matchGroupBean.setUserInfoList(groupUserList);
				groupList.add(matchGroupBean);
			}
		}

		//是否是赛长
		Long captainCount = matchDao.getIsMatchCaptain(matchId, userInfo.getUiId());
		Boolean isMatchCaptain = false;
		if (captainCount > 0) {
			isMatchCaptain = true;
		}
		//报名人数
		Long userCount = matchDao.getMatchUserCount(matchId);

		//我是否是参赛者
		Long isJoinMatchUser = matchDao.getIsJoinMatchUser(matchId,userInfo.getUiId());

		result.put("matchInfo", matchInfo);
		result.put("watchList", watchList);
		result.put("captainList", captainList);
		result.put("groupList", groupList);
		result.put("isMatchCaptain", isMatchCaptain);
		result.put("userCount", userCount);
		result.put("isJoinMatchUser", isJoinMatchUser);
		return result;
	}

	/**
	 * 当前登录用户是否是赛长
	 */
	public boolean getIsCaptain(Long matchId, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		Long captainCount = matchDao.getIsMatchCaptain(matchId, userInfo.getUiId());
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
	 * Integer
	 *
	 * @param map
	 * @param key
	 */
	public Integer getIntegerValueWithNull(Map<String, Object> map, String key) {
		if (map == null || map.get(key) == null) {
			return null;
		} else {
			return Integer.parseInt(map.get(key).toString());
		}
	}

	/**
	 * Integer
	 *
	 * @param map
	 * @param key
	 */
	public Integer getIntegerDoubleValue(Map<String, Object> map, String key) {
		if (map == null || map.get(key) == null) {
			return 0;
		} else {
			BigDecimal b = (BigDecimal)map.get(key);
			Double d = b.doubleValue();
			return (int)Math.floor(d);
		}
	}

	/**
	 * Double
	 *
	 * @param map
	 * @param key
	 */
	public Double getDoubleValue(Map<String, Object> map, String key) {
		if (map == null || map.get(key) == null) {
			return 0.00;
		} else {
			BigDecimal b = (BigDecimal)map.get(key);
			Double d = b.doubleValue();
			return d;
		}
	}


	/**
	 * 获取全部围观列表
	 *
	 * @return
	 */
	public List<Map<String, Object>> getMoreWatchUserList(Long matchId) {
		POJOPageInfo pageInfo = new POJOPageInfo(0, 1);
		List<Map<String, Object>> watchList = matchDao.getWatchUserListByMatchId(matchId, pageInfo);
		return watchList;
	}

	/**
	 * 点击进入比赛详情——获取参赛球队信息和比赛详情
	 *
	 * @return
	 */
	public Map<String, Object> getMatchDetailInfo(Long matchId, String openid) {
		Map<String, Object> result = new HashMap<>();
		UserInfo userInfo = userService.getUserByOpenId(openid);
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		matchInfo.setMiLogo(PropertyConst.DOMAIN + matchInfo.getMiLogo());
		result.put("matchInfo", matchInfo);
		if(matchInfo.getMiParkName().length()>11){
			result.put("parkNameShow", matchInfo.getMiParkName().substring(0,9)+"...");
		}else{
			result.put("parkNameShow", matchInfo.getMiParkName());
		}
		//球场信息
		ParkInfo parkInfo = matchDao.get(ParkInfo.class, matchInfo.getMiParkId());
		result.put("parkInfo", parkInfo);
		if (StringUtils.isNotEmpty(parkInfo.getPiLat()) && StringUtils.isNotEmpty(parkInfo.getPiLng())) {
			String distance = MapUtil.getDistance(userInfo.getUiLatitude(), userInfo.getUiLongitude(), parkInfo.getPiLat(), parkInfo.getPiLng());
			result.put("toMyDistance", Integer.parseInt(distance));
		}
		//获取比赛球队信息
		String joinTeamIds = matchInfo.getMiJoinTeamIds();
        List<Long> teamIdList = getLongTeamIdList(joinTeamIds);
		if (teamIdList != null && teamIdList.size()>0) {
			List<TeamInfo> joinTeamList = matchDao.getTeamListByIds(teamIdList);
			String joinTeamNames = "";
			if (joinTeamList != null && joinTeamList.size() > 0) {
				for (TeamInfo teamInfo : joinTeamList) {
					teamInfo.setTiLogo(PropertyConst.DOMAIN + teamInfo.getTiLogo());
					joinTeamNames += ","+teamInfo.getTiName();
				}
			}
			result.put("joinTeamList", joinTeamList);
			result.put("joinTeamIds", joinTeamIds);
			result.put("joinTeamNames", joinTeamNames);
		}
		//获取成绩上报球队信息
		if (StringUtils.isNotEmpty(matchInfo.getMiReportScoreTeamId()) && !matchInfo.getMiReportScoreTeamId().equals("undefined")) {
			List<Long> reportScoreTeamIdList = getLongTeamIdList(matchInfo.getMiReportScoreTeamId());
			List<TeamInfo> reportScoreTeamList = matchDao.getTeamListByIds(reportScoreTeamIdList);
			//在比赛详情页显示上报球队
			if (reportScoreTeamList != null && reportScoreTeamList.size() > 0) {
				for (TeamInfo teamInfo : reportScoreTeamList) {
					teamInfo.setTiLogo(PropertyConst.DOMAIN + teamInfo.getTiLogo());
				}
			}
			result.put("reportScoreTeamList", reportScoreTeamList);
			String checkReportTeamNames = "";
			if(reportScoreTeamIdList != null && reportScoreTeamIdList.size()>0){
				for(TeamInfo teamInfo:reportScoreTeamList){
					checkReportTeamNames += ","+teamInfo.getTiName();
				}
			}
			//在创建比赛页显示上报球队
			result.put("checkReportTeamNames", checkReportTeamNames);
		}
		//获取我的参赛代表球队
		MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId, null, userInfo.getUiId());
		result.put("myChooseTeamId", matchUserGroupMapping != null?matchUserGroupMapping.getMugmTeamId():null);
		return result;
	}

	public List<Long> getLongTeamIdList(String teamIds) {
		List<Long> teamIdList = new ArrayList<>();
		if(StringUtils.isNotEmpty(teamIds) && StringUtils.isNotEmpty(teamIds.trim())){
			String[] ids = teamIds.split(",");
			for (String id : ids) {
				if (StringUtils.isNotEmpty(id)) {
					teamIdList.add(Long.parseLong(id));
				}
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
	 * 保存一条用户记分对应关系
	 *
	 * @return
	 */
	public void saveUserScoreMapping(Long matchId, Long groupId, Long scorerId) {
		MatchScoreUserMapping mapping = new MatchScoreUserMapping();
		mapping.setMsumMatchId(matchId);
		mapping.setMsumGroupId(groupId);
		mapping.setMsumScoreUserId(scorerId);
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
		Long isCaptain = matchDao.getIsMatchCaptain((Long) parp.get("matchId"), (Long) parp.get("userId"));
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
	 * 创建比赛 更新比赛-保存-自动成为赛长
	 * 报名期间都可以改，但比赛开始后，就只能改 观战范围，成绩上报 和 比赛说明。
	 * @return
	 */
	public MatchInfo saveMatchInfo(MatchInfo matchInfo, String parkName, String chooseTeamId, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		ParkInfo parkInfo = matchDao.getParkIdByName(parkName);
		if (matchInfo.getMiId() != null) {
			//更新
			MatchInfo matchInfoDb = matchDao.get(MatchInfo.class, matchInfo.getMiId());
			//更新比赛用户配置
			String oldJoinTeamIds = matchInfoDb.getMiJoinTeamIds();
			String newJoinTeamIds = matchInfo.getMiJoinTeamIds();
			//更新赛制
			Integer oldMatchFormat1 = matchInfoDb.getMiMatchFormat1();
			Integer oldMatchFormat2 = matchInfoDb.getMiMatchFormat2();
			Integer newMatchFormat1 = matchInfo.getMiMatchFormat1();
			Integer newMatchFormat2 = matchInfo.getMiMatchFormat2();

			//报名期间都可以改，但比赛开始后，就只能改 观战范围，成绩上报 和 比赛说明。
			matchInfoDb.setMiMatchOpenType(matchInfo.getMiMatchOpenType());
			matchInfoDb.setMiReportScoreTeamId(matchInfo.getMiReportScoreTeamId());
			matchInfoDb.setMiContent(matchInfo.getMiContent());
			matchInfoDb.setMiTitle(matchInfo.getMiTitle());
			if (!matchInfo.getMiLogo().contains(matchInfoDb.getMiLogo())) {
				matchInfoDb.setMiLogo(matchInfo.getMiLogo());
			}

			if(matchInfoDb.getMiIsEnd() == 0){
				matchInfoDb.setMiParkName(parkName);
				if (parkInfo != null) {
					matchInfoDb.setMiParkId(parkInfo.getPiId());
				}
				matchInfoDb.setMiZoneBeforeNine(matchInfo.getMiZoneBeforeNine());
				matchInfoDb.setMiZoneAfterNine(matchInfo.getMiZoneAfterNine());
				matchInfoDb.setMiMatchTime(matchInfo.getMiMatchTime());
				matchInfoDb.setMiMatchFormat1(matchInfo.getMiMatchFormat1());
				matchInfoDb.setMiMatchFormat2(matchInfo.getMiMatchFormat2());
				matchInfoDb.setMiJoinTeamIds(matchInfo.getMiJoinTeamIds());
				if(StringUtils.isEmpty(matchInfo.getMiJoinTeamIds())){
					//公开
					matchInfoDb.setMiJoinOpenType(1);
				}else{
					//队内
					matchInfoDb.setMiJoinOpenType(2);
				}
			}

			matchInfoDb.setMiUpdateTime(System.currentTimeMillis());
			matchInfoDb.setMiUpdateUserId(userInfo.getUiId());
			matchInfoDb.setMiUpdateUserName(userInfo.getUiRealName());
			matchDao.update(matchInfoDb);

			//顺序：如果赛制有修改，先将参赛用户退回报名表，再去判断参赛球队是否有变化，再删除报名表中对应的参赛球队的用户
			//赛制不一样 更新 ，让赛长重新调整分组 个人比洞是每组两个人，其他赛制每组可以四个人
			//只要新老有一个是个人比洞，并且新老值和以前不一样，就将已经参赛的用户重新放入报名列表
			if(((oldMatchFormat1 == 1 && oldMatchFormat2 ==0) || (newMatchFormat1 == 1 && newMatchFormat2 ==0))
					&& (!oldMatchFormat1.equals(newMatchFormat1) || !oldMatchFormat2.equals(newMatchFormat2))){
				//获取参赛球友列表
				List<MatchUserGroupMapping> matchUserGroupMappingList = matchDao.getMatchUserGroupMappingListByMatchId(matchInfoDb.getMiId());
				//将球友退回报名表 除了赛长
				Long updateTime = System.currentTimeMillis();
				if(matchUserGroupMappingList != null && matchUserGroupMappingList.size()>0){
					for(MatchUserGroupMapping bean:matchUserGroupMappingList){
						if(bean.getMugmUserType() != 0){
							bean.setMugmUserType(2);
							bean.setMugmUpdateUserId(userInfo.getUiId());
							bean.setMugmUpdateUserName(userInfo.getUiRealName());
							bean.setMugmUpdateTime(updateTime);
							matchDao.save(bean);
						}
					}
				}
			}
			//如果参赛球队有改变，更新比赛用户配置
			if (matchInfoDb.getMiIsEnd() == 0) {
				List<Long> oldJoinTeamIdList = null;
				List<Long> newJoinTeamIdList = null;
				//获取我在本比赛的分组配置
				MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchInfoDb.getMiId(),null,userInfo.getUiId());
				if(StringUtils.isNotEmpty(oldJoinTeamIds)){
					//删除比赛mapping表的本比赛所有参赛报名用户 除了自己
					oldJoinTeamIdList = getLongTeamIdList(oldJoinTeamIds);
					matchDao.delMatchUserMappingByTeamId(matchInfoDb.getMiId(), oldJoinTeamIdList, userInfo.getUiId());
					//删除比赛mapping的无效的group
					matchDao.delMatchGroup(matchInfoDb.getMiId(),matchUserGroupMapping.getMugmGroupId());
				}
				if(StringUtils.isNotEmpty(newJoinTeamIds)){
					//将这些球队的队长加入比赛mapping表
					newJoinTeamIdList = getLongTeamIdList(newJoinTeamIds);
					//获取参赛球队的队长
					List<Map<String,Object>> teamCaptainList = matchDao.getTeamCaptailByTeamIds(newJoinTeamIdList);
					//将这些球队的队长加入比赛mapping表 将其他队长放入跟我一个组
					if (teamCaptainList != null && teamCaptainList.size() > 0) {
						for (Map<String, Object> map : teamCaptainList) {
							Long captainUserId = (Long) map.get("userId");
							//是否存在
							if (captainUserId.equals(userInfo.getUiId())) {
								continue;
							}
							Long groupId = matchUserGroupMapping.getMugmGroupId();
							String groupName = matchUserGroupMapping.getMugmGroupName();
							//获取我所在组的人数
							Long count = matchDao.getGroupUserCountById(matchInfo.getMiId(),matchUserGroupMapping.getMugmGroupId());
							if(matchInfo.getMiMatchFormat2() == 0 && matchInfo.getMiMatchFormat1() == 1){
								//个人比洞 每组2人
								if(count >= 2){
									//超过了2个人，新增一个分组
									MatchGroup matchGroup = addGroupByTeamId(matchInfo.getMiId(), openid);
									groupId = matchGroup.getMgId();
									groupName = matchGroup.getMgGroupName();
								}
							}else{
								//其余的每组4人
								if(count >= 3){
									//超过了4个人，新增一个分组
									MatchGroup matchGroup = addGroupByTeamId(matchInfo.getMiId(), openid);
									groupId = matchGroup.getMgId();
									groupName = matchGroup.getMgGroupName();
								}
							}


							MatchUserGroupMapping mugm = new MatchUserGroupMapping();
							mugm.setMugmMatchId(matchInfo.getMiId());
							mugm.setMugmTeamId((Long) map.get("teamId"));
							mugm.setMugmUserType(0);
							mugm.setMugmIsDel(0);
							mugm.setMugmGroupId(groupId);
							mugm.setMugmGroupName(groupName);
							mugm.setMugmUserId(captainUserId);
							UserInfo userInfo_ = matchDao.get(UserInfo.class, captainUserId);
							mugm.setMugmUserName(StringUtils.isNotEmpty(userInfo_.getUiRealName())?userInfo_.getUiRealName():userInfo_.getUiNickName());
							mugm.setMugmCreateUserId(userInfo.getUiId());
							mugm.setMugmCreateUserName(StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName());
							mugm.setMugmCreateTime(System.currentTimeMillis());
							matchDao.save(mugm);
						}
					}
				}
			}
			//更新我的比赛用户配置
			MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchInfoDb.getMiId(),null,userInfo.getUiId());
			if(StringUtils.isNotEmpty(chooseTeamId) && !chooseTeamId.equals("0") && !chooseTeamId.equals("null")
					&& !chooseTeamId.equals("undefined") && !chooseTeamId.equals(matchUserGroupMapping.getMugmTeamId())){
				matchUserGroupMapping.setMugmTeamId(Long.parseLong(chooseTeamId));
				matchUserGroupMapping.setMugmUpdateTime(System.currentTimeMillis());
				matchUserGroupMapping.setMugmUpdateUserName(StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName());
				matchUserGroupMapping.setMugmUpdateUserId(userInfo.getUiId());
				matchDao.update(matchUserGroupMapping);
			}
			return matchInfoDb;
		} else {
			//新增
			if (parkInfo != null) {
				matchInfo.setMiParkId(parkInfo.getPiId());
			}
			if(StringUtils.isEmpty(matchInfo.getMiJoinTeamIds())){
				//公开
				matchInfo.setMiJoinOpenType(1);
			}else{
				//队内
				matchInfo.setMiJoinOpenType(2);
			}
			matchInfo.setMiParkName(parkName);
			matchInfo.setMiType(1);
			matchInfo.setMiCreateTime(System.currentTimeMillis());
			matchInfo.setMiCreateUserId(userInfo.getUiId());
			matchInfo.setMiCreateUserName(userInfo.getUiRealName());
			matchInfo.setMiIsValid(1);
			//0：报名中  1进行中  2结束
			matchInfo.setMiIsEnd(0);
			matchDao.save(matchInfo);

			//创建比赛分组
			MatchGroup matchGroup = new MatchGroup();
			matchGroup.setMgMatchId(matchInfo.getMiId());
			matchGroup.setMgGroupName("第1组");
			matchGroup.setMgCreateTime(System.currentTimeMillis());
			matchGroup.setMgCreateUserId(userInfo.getUiId());
			matchGroup.setMgCreateUserName(userInfo.getUiRealName());
			matchDao.save(matchGroup);

			//查询用户所在球队
		/*if(chooseTeamId != null){
			//获取赛长所在的球队
			TeamUserMapping teamUserMapping = matchDao.getTeamUserMappingByUserId(userInfo.getUiId());
			chooseTeamId = teamUserMapping.getTumTeamId();
		}*/

			//创建用户分组mapping 创建人自动成为赛长
			MatchUserGroupMapping matchUserGroupMapping = new MatchUserGroupMapping();
			String userName = StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName();
			matchUserGroupMapping.setMugmMatchId(matchInfo.getMiId());
			if(StringUtils.isNotEmpty(chooseTeamId) && !chooseTeamId.equals("0") && !chooseTeamId.equals("null") && !chooseTeamId.equals("undefined")){
				matchUserGroupMapping.setMugmTeamId(Long.parseLong(chooseTeamId));
			}
			matchUserGroupMapping.setMugmUserType(0);
			matchUserGroupMapping.setMugmIsDel(0);
			matchUserGroupMapping.setMugmGroupId(matchGroup.getMgId());
			matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
			matchUserGroupMapping.setMugmUserId(userInfo.getUiId());
			matchUserGroupMapping.setMugmUserName(userName);
			matchUserGroupMapping.setMugmCreateUserId(userInfo.getUiId());
			matchUserGroupMapping.setMugmCreateUserName(userName);
			matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
			matchDao.save(matchUserGroupMapping);

			//如果是队际赛 每个队的队长自动成为赛长
			if (StringUtils.isNotEmpty(matchInfo.getMiJoinTeamIds())) {
				List<Long> teamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
				List<Map<String, Object>> teamCaptainList = matchDao.getTeamCaptailByTeamIds(teamIdList);
				if (teamCaptainList != null && teamCaptainList.size() > 0) {
					for (Map<String, Object> map : teamCaptainList) {
						Long captainUserId = (Long) map.get("userId");
						//是否存在
						if (captainUserId.equals(userInfo.getUiId())) {
							continue;
						}
						MatchUserGroupMapping mugm = new MatchUserGroupMapping();
						mugm.setMugmMatchId(matchInfo.getMiId());
						mugm.setMugmTeamId((Long) map.get("teamId"));
						mugm.setMugmUserType(0);
						mugm.setMugmIsDel(0);
						mugm.setMugmGroupId(matchGroup.getMgId());
						mugm.setMugmGroupName(matchGroup.getMgGroupName());
						mugm.setMugmUserId(captainUserId);
						UserInfo userInfo_ = matchDao.get(UserInfo.class, captainUserId);
						mugm.setMugmUserName(StringUtils.isNotEmpty(userInfo_.getUiRealName())?userInfo_.getUiRealName():userInfo_.getUiNickName());
						mugm.setMugmCreateUserId(userInfo_.getUiId());
						mugm.setMugmCreateUserName(StringUtils.isNotEmpty(userInfo_.getUiRealName())?userInfo_.getUiRealName():userInfo_.getUiNickName());
						mugm.setMugmCreateTime(System.currentTimeMillis());
						matchDao.save(mugm);
					}
				}
			}
			return matchInfo;
		}
	}

	//获取新老参赛队中不同的球队id，如果老参赛队id为空，则不管
	private List<Long> getDifferentTeamIds(String oldJoinTeamIds,String newJoinTeamIds){
		if(StringUtils.isEmpty(oldJoinTeamIds)){
			return null;
		}
		if(StringUtils.isEmpty(newJoinTeamIds)){
			return getLongTeamIdList(oldJoinTeamIds);
		}
		String[] oldTeamIds = oldJoinTeamIds.split(",");
		String[] newTeamIds = newJoinTeamIds.split(",");
		List<Long> diffTeamIdList = new ArrayList<>();
		for (int i = 0; i < oldTeamIds.length; i++) {
			boolean flag = true;
			String temp = oldTeamIds[i];
			for (int j = 0; j < newTeamIds.length; j++) {
				if (temp.equals(newTeamIds[j])) {
					flag = false;
					break;
				}
			}
			if (flag) {
				diffTeamIdList.add(Long.parseLong(temp));
			}
		}
		System.out.println(diffTeamIdList.toString());
		return diffTeamIdList;
	}


	/**
	 * 赛长——删除用户——本组用户列表
	 * 不包括自己
	 * @return
	 */
	public Map<String, Object> getUserListByMatchIdGroupId(Long matchId, Long groupId, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> list = matchDao.getUserListByMatchIdGroupId(matchId, groupId, userInfo.getUiId());
		result.put("applyUserList", list);
		result.put("userCount", list.size());
		return result;
	}

	/**
	 * 单练——开始记分——保存数据
	 *
	 * @return
	 */
	public Map<String, Object> saveSinglePlay(String matchTitle, Long parkId, String parkName, String playTime, Integer peopleNum, String digest,
											  String beforeZoneName, String afterZoneName, String openid) {
		Map<String, Object> result = new HashMap<>();

		UserInfo userInfo = userService.getUserByOpenId(openid);
		//获取随机用户最大的用户id
		Long maxOtherUserId = matchDao.getMaxOtherUserId();

		MatchInfo matchInfo = new MatchInfo();
		ParkInfo parkInfo = matchDao.getParkIdByName(parkName);
		if (parkInfo != null) {
			matchInfo.setMiParkId(parkInfo.getPiId());
		}
		matchInfo.setMiTitle(matchTitle);
		matchInfo.setMiParkId(parkId);
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
		matchInfo.setMiCreateUserId(userInfo.getUiId());
		matchInfo.setMiCreateUserName(userInfo.getUiRealName());
		matchInfo.setMiIsValid(1);
		matchDao.save(matchInfo);

		//创建分组
		MatchGroup matchGroup = new MatchGroup();
		matchGroup.setMgMatchId(matchInfo.getMiId());
		matchGroup.setMgGroupName("第1组");
		matchGroup.setMgCreateUserId(userInfo.getUiId());
		matchGroup.setMgCreateUserName(userInfo.getUiRealName());
		matchGroup.setMgCreateTime(System.currentTimeMillis());
		matchDao.save(matchGroup);

		//保存我的用户分组信息
		MatchUserGroupMapping matchUserGroupMapping = new MatchUserGroupMapping();
		matchUserGroupMapping.setMugmMatchId(matchInfo.getMiId());
		matchUserGroupMapping.setMugmUserType(0);
		matchUserGroupMapping.setMugmGroupId(matchGroup.getMgId());
		matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
		matchUserGroupMapping.setMugmUserId(userInfo.getUiId());
		matchUserGroupMapping.setMugmUserName(StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName());
		matchUserGroupMapping.setMugmCreateUserId(userInfo.getUiId());
		matchUserGroupMapping.setMugmCreateUserName(StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName());
		matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
		matchDao.save(matchUserGroupMapping);


		if(maxOtherUserId == null){
			maxOtherUserId = 1000001L;
		}

		//随机生成几个用户队友
		for (int i = 1; i < peopleNum; i++) {
			MatchUserGroupMapping otherPeople = new MatchUserGroupMapping();
			otherPeople.setMugmMatchId(matchInfo.getMiId());
			Long i_ = Long.valueOf(i);
			maxOtherUserId += i_;
			otherPeople.setMugmUserId(maxOtherUserId);
			otherPeople.setMugmUserType(1);
			otherPeople.setMugmGroupId(matchGroup.getMgId());
			otherPeople.setMugmGroupName(matchGroup.getMgGroupName());
			otherPeople.setMugmUserName("球友" + i);
			otherPeople.setMugmCreateUserId(userInfo.getUiId());
			otherPeople.setMugmCreateUserName(StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName());
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
	public Map<String, Object> getMySinglePlay(String openid) {
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> parp = TimeUtil.getThisDayTime();
		UserInfo userInfo = userService.getUserByOpenId(openid);
		Long userId = userInfo.getUiId();
		parp.put("userId", userId);
		MatchInfo matchInfo = matchDao.getMySinglePlay(parp);
		result.put("matchInfo", matchInfo);
		if (matchInfo != null) {
			//我所在的组id
			MatchGroup matchGroup = matchDao.getMyGroupById(matchInfo.getMiId());
			result.put("groupId", matchGroup.getMgId());
		}
		return result;
	}

	/**
	 * 比赛详情——添加组
	 *
	 * @return
	 */
	public MatchGroup addGroupByTeamId(Long matchId, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		//获取最大组
		MatchGroup maxGroup = matchDao.getMaxGroupByMatchId(matchId);
		Integer max = 1;
		String groupName = "";
		if(maxGroup != null){
			groupName = maxGroup.getMgGroupName();
			max = Integer.parseInt(groupName.substring(1, groupName.length() - 1));
			max++;
		}

		MatchGroup group = new MatchGroup();
		group.setMgMatchId(matchId);
		group.setMgGroupName("第" + max + "组");
		group.setMgCreateUserId(userInfo.getUiId());
		group.setMgCreateUserName(StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName());
		group.setMgCreateTime(System.currentTimeMillis());
		matchDao.save(group);
		return group;
	}

	/**
	 * 比赛详情——赛长获取待分组人员
	 *
	 * @return
	 */
	public Map<String, Object> getApplyUserByMatchId(Long matchId,String keyword, Long groupId) {
		Map<String, Object> result = new HashMap<>();
		//本组人数(不包括已报名的)
		Long count = matchDao.getGroupUserCountById(matchId, groupId);
		result.put("userCount", count);
		List<Map<String, Object>> applyUserList = matchDao.getApplyUserByMatchId(matchId, keyword,groupId);
		result.put("applyUserList", applyUserList);
		return result;
	}

	/**
	 * 比赛详情——赛长删除本组用户——获取本组用户 不包括已报名的
	 *
	 * @return
	 */
	public Map<String, Object> getUserListByGroupId(Long matchId, Long groupId,String openid) {
		Map<String, Object> result = new HashMap<>();
		UserInfo userInfo = userService.getUserByOpenId(openid);
		//本组人数
		Long count = matchDao.getGroupUserCountById(matchId, groupId);
		result.put("userCount", count);
		List<Map<String, Object>> userList = matchDao.getUserListByGroupId(matchId,groupId,userInfo.getUiId());
		result.put("userList", userList);
		return result;
	}

	/**
	 * 比赛详情——赛长 添加用户至分组
	 * @param myTeamId 赛长所在球队id，也就是这些用户的代表球队id
	 * @return
	 */
	public void updateGroupUserByMatchIdGroupId(Long matchId, Long myTeamId, Long groupId, String userIds, String openid) {
		//赛长
		UserInfo userInfo = userService.getUserByOpenId(openid);
		MatchGroup matchGroup = matchDao.get(MatchGroup.class,groupId);

		Long captainUserId = userInfo.getUiId();
		//将用户加入比赛分组
		if(StringUtils.isNotEmpty(userIds)){
			userIds = userIds.replace("[", "");
			userIds = userIds.replace("]", "");
			userIds = userIds.replace("\"", "");
			String[] uids = userIds.split(",");
			Long updateTime = System.currentTimeMillis();
			for(String uid : uids){
				if(StringUtils.isNotEmpty(uid)){
					Long userId = Long.parseLong(uid);
					MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId, null, userId);
					if(matchUserGroupMapping == null){
						//如果为null，则表示从备选球友中选进来的
						TeamUserMapping teamUserMapping = matchDao.getTeamUserMappingByUserId(userId);
						UserInfo ui = matchDao.get(UserInfo.class,userId);
						matchUserGroupMapping = new MatchUserGroupMapping();
						matchUserGroupMapping.setMugmMatchId(matchId);
						if(teamUserMapping != null){
							matchUserGroupMapping.setMugmTeamId(teamUserMapping.getTumTeamId());
						}
						matchUserGroupMapping.setMugmUserType(1);
						matchUserGroupMapping.setMugmIsDel(0);
						matchUserGroupMapping.setMugmGroupId(groupId);
						matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
						matchUserGroupMapping.setMugmUserId(userId);
						matchUserGroupMapping.setMugmUserName(StringUtils.isNotEmpty(ui.getUiRealName())?ui.getUiRealName():ui.getUiNickName());
						matchUserGroupMapping.setMugmCreateUserId(userInfo.getUiId());
						matchUserGroupMapping.setMugmCreateUserName(StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName());
						matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
						matchDao.save(matchUserGroupMapping);
					}else{
						matchUserGroupMapping.setMugmGroupId(groupId);
						matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
						matchUserGroupMapping.setMugmIsDel(0);
						matchUserGroupMapping.setMugmUpdateUserId(captainUserId);
						matchUserGroupMapping.setMugmUpdateUserName(StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName());
						matchUserGroupMapping.setMugmUpdateTime(updateTime);
						matchDao.update(matchUserGroupMapping);
					}
				}
			}
		}
	}

	/**
	 * 获取单人比杆赛记分卡
	 * @return
	 */
	public Map<String, Object> getSingleRodScoreCardInfoByGroupId(Long matchId, Long groupId) {
		Map<String, Object> result = new HashMap<>();
		List<MatchGroupUserScoreBean> list = new ArrayList<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		result.put("matchInfo", matchInfo);
		//本组用户
		List<Map<String, Object>> userList = null;
		if(matchInfo.getMiType() == 1){
			userList = matchDao.getUserListByScoreCard(matchId, groupId,null);
		}else{
			//单练
			userList = matchDao.getSingleUserListById(matchId, groupId);
		}
		if(userList != null && userList.size()>0){
			for(Map<String, Object> user:userList){
				String realName = getName(user,"uiRealName");
				if(realName != null && realName.length() >5){
					user.put("uiRealName",realName.substring(0,5)+"...");
				}
				String nickName = getName(user,"uiNickName");
				if(nickName != null && nickName.length() >5){
					user.put("uiNickName",nickName.substring(0,5)+"...");
				}
			}
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
				Long teamId = getLongValue(user, "team_id");
				MatchGroupUserScoreBean bean = new MatchGroupUserScoreBean();
				bean.setUserId(uiId);
				String userName = getName(user, "uiRealName");
				if(StringUtils.isEmpty(userName)){
					userName = getName(user, "uiNickName");
				}
				bean.setUserName(userName);
				//本用户得分情况
                List<Map<String, Object>> uscoreList = matchDao.getScoreByUserId(groupId, uiId, matchInfo, teamId);
                if (uscoreList != null && uscoreList.size() > 0) {
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
		List<Map<String, Object>> totalScoreList = null;
		if(matchInfo.getMiType() == 1){
			totalScoreList = matchDao.getTotalScoreWithUser(matchId, groupId);
		}else{
			//单练的总分
			totalScoreList = matchDao.getSingleTotalScoreWithUser(matchId, groupId);
		}
		result.put("totalScoreList", totalScoreList);
		return result;
	}

	/**
	 * 保存或更新计分数据
	 * 与标准杆一样 叫平标准杆
	 * 比标准杆少一杆叫小鸟
	 * 比标准杆多一杆或者标准杆完成该洞叫Par
	 * 低于标准杆2杆完成该洞叫老鹰
	 * @return
	 */
	public void saveOrUpdateScore(Long userId, Long matchId, Long groupId, Long holeId,
                                  String isUp, Integer rod, String rodCha,
								  Integer pushRod, Integer beforeAfter, String openid, String userIds) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId,groupId,userId);
		//用户参赛代表队
		Long teamId = matchUserGroupMapping.getMugmTeamId();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		//如果有上报球队，同时将比分记录到上报球队中
		String reportTeamIds = matchInfo.getMiReportScoreTeamId();
		ParkPartition parkPartition = matchDao.get(ParkPartition.class,holeId);
		//查询是否有本洞的记分
		MatchScore scoreDb = matchDao.getMatchScoreByIds(userId,matchId,groupId,teamId,beforeAfter,parkPartition.getppName(),parkPartition.getPpHoleNum());
		if (scoreDb != null) {
            if(matchInfo.getMiMatchFormat2() == 1){
                //如果是双人赛，同时更新本队的球友
                //获取本组本球队的用户list
                List<Map<String,Object>> userListByTeam = matchDao.getUserListByTeamId(matchId,teamId,groupId);
                if(userListByTeam != null && userListByTeam.size()>0){
                    for(Map<String,Object> user:userListByTeam){
                        Long userId_ = getLongValue(user,"uiId");
                        //更新记分
                        updateScoreByUserTeam(scoreDb,userInfo,parkPartition,isUp,rod,rodCha,pushRod);
                        //更新在上报球队中的比分
                        updateReportTeamScore(reportTeamIds,scoreDb,userInfo,matchId,groupId,userId_);
                    }
                }
            }else{
                //更新记分
                updateScoreByUserTeam(scoreDb,userInfo,parkPartition,isUp,rod,rodCha,pushRod);
                //更新在上报球队中的比分
                updateReportTeamScore(reportTeamIds,scoreDb,userInfo,matchId,groupId,userId);
            }
		} else {
			//新增一条记分记录
			MatchGroup matchGroup = matchDao.get(MatchGroup.class, groupId);
			MatchScore score = null;
			if(matchInfo.getMiMatchFormat2() == 1){
			    //如果是双人赛，将比分同时记给本组本球队的两个人
                //获取本组本球队的用户list
                List<Map<String,Object>> userListByTeam = matchDao.getUserListByTeamId(matchId,teamId,groupId);
                if(userListByTeam != null && userListByTeam.size()>0){
                	if(StringUtils.isNotEmpty(matchInfo.getMiJoinTeamIds()) && StringUtils.isNotEmpty(matchInfo.getMiJoinTeamIds().trim())){
                		//队际赛
						for(Map<String,Object> user:userListByTeam){
							Long userId_ = getLongValue(user,"uiId");
							String userName_ = getName(user,"uiRealName");
							if(StringUtils.isEmpty(userName_)){
								userName_ = getName(user,"uiNickName");
							}
							//保存记分
							score = saveScore(matchInfo,teamId,groupId, matchGroup.getMgGroupName(),
									userId_,userName_,userInfo,parkPartition,
									beforeAfter,isUp,rod,rodCha,pushRod);
							//如果有上报球队，同时将比分记录到上报球队中
							saveReportTeamScore(reportTeamIds,userId_,teamId, score);
						}
					}else{
						//公开赛
						if(StringUtils.isNotEmpty(userIds) && !"null".equals(userIds) && !"undefined".equals(userIds)){
							List<Long> userList = getLongTeamIdList(userIds);
							for(Long uId:userList){
								UserInfo u = matchDao.get(UserInfo.class,uId);
								String userName_ = u.getUiRealName();
								if(StringUtils.isEmpty(userName_)){
									userName_ = u.getUiNickName();
								}
								//保存记分
								saveScore(matchInfo,teamId,groupId, matchGroup.getMgGroupName(),
										uId,userName_,userInfo,parkPartition,
										beforeAfter,isUp,rod,rodCha,pushRod);
							}
						}
					}
                }
            }else{
                //保存记分
                score = saveScore(matchInfo,teamId,groupId,matchGroup.getMgGroupName(),
                        userId,matchUserGroupMapping.getMugmUserName(),userInfo,parkPartition,
                        beforeAfter,isUp,rod,rodCha,pushRod);
                //如果有上报球队，同时将比分记录到上报球队中
                saveReportTeamScore(reportTeamIds,userId,teamId, score);
            }
		}
	}

	//更新双人比杆公开赛的成绩
	public void saveOrUpdateScoreDoubleRod(Long matchId, Long groupId, Long holeId, String isUp, Integer rod, String rodCha, Integer pushRod, Integer beforeAfter, String openid, String userIds) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		ParkPartition parkPartition = matchDao.get(ParkPartition.class,holeId);
		if(matchInfo.getMiMatchFormat2() == 1 && matchInfo.getMiMatchFormat1() == 0){
			if(StringUtils.isNotEmpty(userIds) && !"null".equals(userIds) && !"undefined".equals(userIds)){
				List<Long> userList = getLongTeamIdList(userIds);
				for(Long uId:userList) {
					UserInfo u = matchDao.get(UserInfo.class, uId);
					String userName_ = u.getUiRealName();
					if (StringUtils.isEmpty(userName_)) {
						userName_ = u.getUiNickName();
					}
					//查询是否有本洞的记分
					MatchScore scoreDb = matchDao.getMatchScoreByIds(uId, matchId, groupId, null, beforeAfter, parkPartition.getppName(), parkPartition.getPpHoleNum());
					if (scoreDb != null) {
						//更新记分
						updateScoreByUserTeam(scoreDb,userInfo,parkPartition,isUp,rod,rodCha,pushRod);
					}else{
						//新增一条记分记录
						MatchGroup matchGroup = matchDao.get(MatchGroup.class, groupId);
						//保存记分
						saveScore(matchInfo, null, groupId, matchGroup.getMgGroupName(),
								uId, userName_, userInfo, parkPartition,
								beforeAfter, isUp, rod, rodCha, pushRod);
					}
				}
			}
		}
	}

    //更新在上报球队中的比分
    private void updateReportTeamScore(String reportTeamIds, MatchScore scoreDb, UserInfo userInfo,
                                       Long matchId,Long groupId,Long userId) {
        if(StringUtils.isNotEmpty(reportTeamIds)){
            List<Long> reportTeamIdList = getLongTeamIdList(reportTeamIds);
            for(Long reportTeamId :reportTeamIdList){
                //获取上报球队记分详情
                MatchScore scoreByReportTeam = matchDao.getScoreByReportTeam(reportTeamId,matchId,groupId,userId,
                        scoreDb.getMsBeforeAfter(),scoreDb.getMsHoleName(),scoreDb.getMsHoleNum(),scoreDb.getMsHoleStandardRod());
                if(scoreByReportTeam != null){
                    scoreByReportTeam.setMsIsUp(scoreDb.getMsIsUp());
                    scoreByReportTeam.setMsRodNum(scoreDb.getMsRodNum());
                    scoreByReportTeam.setMsRodCha(scoreDb.getMsRodCha());
                    scoreByReportTeam.setMsPushRodNum(scoreDb.getMsPushRodNum());
                    scoreByReportTeam.setMsIsPar(scoreDb.getMsIsPar());
                    scoreByReportTeam.setMsIsBird(scoreDb.getMsIsBird());
                    scoreByReportTeam.setMsIsEagle(scoreDb.getMsIsEagle());
                    scoreByReportTeam.setMsIsOn(scoreDb.getMsIsOn());
                    scoreByReportTeam.setMsIsBomb(scoreDb.getMsIsBomb());
                    scoreByReportTeam.setMsIsBogey(scoreDb.getMsIsBogey());
                    scoreByReportTeam.setMsUpdateTime(System.currentTimeMillis());
                    scoreByReportTeam.setMsUpdateUserId(userInfo.getUiId());
                    scoreByReportTeam.setMsUpdateUserName(userInfo.getUiRealName());
                    matchDao.update(scoreByReportTeam);
                }
            }
        }
    }

    //将比分记录到上报球队中
    private void saveReportTeamScore(String reportTeamIds, Long userId, Long teamId, MatchScore score) {
        //如果有上报球队，同时将比分记录到上报球队中
        if(StringUtils.isNotEmpty(reportTeamIds)){
            List<Long> reportTeamIdList = getLongTeamIdList(reportTeamIds);
            for(Long reportTeamId :reportTeamIdList){
                //如果上报球队和参赛球队一样，就不计了
                //获取本用户是否在上报球队中
                Long count = matchDao.getIsInTeam(userId,reportTeamId);
                if(!reportTeamId.equals(teamId) && count >0){
                    MatchScore scoreByReportTeam = new MatchScore();
                    BeanUtils.copyProperties(score,scoreByReportTeam);
                    //type=1 上报球队记分
                    scoreByReportTeam.setMsType(1);
                    scoreByReportTeam.setMsTeamId(reportTeamId);
                    matchDao.save(scoreByReportTeam);
                }
            }
        }
    }

    //更新球友记分卡的得分
    private MatchScore updateScoreByUserTeam(MatchScore scoreDb,UserInfo userInfo, ParkPartition parkPartition,
                                       String isUp, Integer rod,
                                       String rodCha, Integer pushRod) {
        scoreDb.setMsIsUp(isUp);
        //杆差=杆数-本洞标准杆数
        if(rod != null){
            scoreDb.setMsRodNum(rod);
            scoreDb.setMsRodCha(rod - parkPartition.getPpHoleStandardRod());
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
            scoreDb.setMsRodNum(parkPartition.getPpHoleStandardRod()+gc);
        }
        scoreDb.setMsPushRodNum(pushRod);
        //计算得分结果
        getScore(scoreDb,parkPartition.getPpHoleStandardRod());
        scoreDb.setMsUpdateTime(System.currentTimeMillis());
        scoreDb.setMsUpdateUserId(userInfo.getUiId());
        scoreDb.setMsUpdateUserName(userInfo.getUiRealName());
        matchDao.update(scoreDb);
        return scoreDb;
    }

    //新增球友记分卡的得分
    private MatchScore saveScore(MatchInfo matchInfo, Long teamId, Long groupId, String groupName,
                                 Long userId, String userName, UserInfo userInfo,
                                 ParkPartition parkPartition,
                                 Integer beforeAfter, String isUp, Integer rod, String rodCha, Integer pushRod) {
        MatchScore score = new MatchScore();
        //type=0 比赛球队记分
        score.setMsType(0);
        score.setMsTeamId(teamId);
        score.setMsMatchId(matchInfo.getMiId());
        score.setMsMatchTitle(matchInfo.getMiTitle());
        score.setMsMatchType(matchInfo.getMiType());
        score.setMsGroupId(groupId);
        score.setMsGroupName(groupName);
        score.setMsUserId(userId);
        score.setMsHoleStandardRod(parkPartition.getPpHoleStandardRod());
        score.setMsUserName(userName);
        score.setMsBeforeAfter(beforeAfter);
        score.setMsHoleName(parkPartition.getppName());
        score.setMsHoleNum(parkPartition.getPpHoleNum());
        score.setMsIsUp(isUp);
        if(rod != null){
            score.setMsRodNum(rod);
            score.setMsRodCha(rod - parkPartition.getPpHoleStandardRod());
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
            score.setMsRodNum(parkPartition.getPpHoleStandardRod()+gc);
        }
        score.setMsPushRodNum(pushRod);
        //计算得分结果
        getScore(score,parkPartition.getPpHoleStandardRod());
        score.setMsCreateUserId(userInfo.getUiId());
        score.setMsCreateUserName(StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName());
        score.setMsCreateTime(System.currentTimeMillis());
        matchDao.save(score);
        return score;
    }

    private void getScore(MatchScore score, Integer holeStandardRod) {
		if(score.getMsRodNum().equals(holeStandardRod)){
			//标准杆一样 par
			score.setMsIsPar(1);
			score.setMsIsBird(null);
			score.setMsIsBogey(null);
			score.setMsIsEagle(null);
		}else if(holeStandardRod - score.getMsRodNum() == 1){
			//比标准杆少一杆叫小鸟
			score.setMsIsPar(null);
			score.setMsIsBird(1);
			score.setMsIsBogey(null);
			score.setMsIsEagle(null);
		}else if(score.getMsRodNum() - holeStandardRod == 1){
			//比标准杆多一杆叫bogey
			score.setMsIsPar(null);
			score.setMsIsBird(null);
			score.setMsIsBogey(1);
			score.setMsIsEagle(null);
		}else if(holeStandardRod - score.getMsRodNum() == 2){
			//比标准杆少2杆 叫老鹰
			score.setMsIsPar(null);
			score.setMsIsBird(null);
			score.setMsIsBogey(null);
			score.setMsIsEagle(1);
		}

		if(score.getMsRodNum() - holeStandardRod >= 3){
			//“暴洞”是指+3及以上
			score.setMsIsBomb(1);
			score.setMsIsPar(null);
			score.setMsIsBird(null);
			score.setMsIsBogey(null);
			score.setMsIsEagle(null);
		}

		if(score.getMsRodNum() - score.getMsPushRodNum() == holeStandardRod - 2){
			//标ON是计算出来的，如果某洞：杆数-推杆数=该洞标准杆数-2，则该洞为 标ON 3-1=4-2
			score.setMsIsOn(1);
		}
	}

	/**
	 * 保存或更新比赛状态
	 * state   0：报名中  1进行中  2结束
	 * 1、如果是多队双人比赛，不管比杆比洞，每组，每个队不超过两人，也可以是一人，每组最多两个队。生成记分卡时，只有一个队的两个人才能放入一行。
	 * 2、对于单人比洞，每组只能两个人，如果又是队式的，则一组的两个人要是两个队的
	 * 3、单人比杆赛分组不用有任何限制。
	 * 4、一个队队内，及多个队之间没法进行比洞赛
	 * @return
	 */
	public String updateMatchState(Long matchId, Integer state, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		String joinTeamIds = matchInfo.getMiJoinTeamIds();
		List<Long> joinTeamIdList = getLongTeamIdList(joinTeamIds);
		//如果是队内或者队际赛，获取每个组的队伍个数情况
		List<Map<String,Object>> teamCountList = null;
		if(joinTeamIdList != null && joinTeamIdList.size()>0){
			teamCountList = matchDao.getTeamCountByMatchId(matchId,null);
			if(teamCountList == null || teamCountList.size() == 0){
				return "没有参赛队无法开始比赛。";
			}
		}
		//获取每个组的每队人数情况
		List<Map<String,Object>> userCountListByTeam = matchDao.getUserCountByMatchId(matchId,null);
		if(userCountListByTeam == null || userCountListByTeam.size() ==0){
            return "没有参赛队员无法开始比赛。";
        }

		//2、对于单人比洞，每组只能两个人，如果又是队式的，则一组的两个人要是两个队的
		// 3、单人比杆赛分组不用有任何限制。
		if(joinTeamIdList != null){
			//单人比洞
			if(matchInfo.getMiMatchFormat1() == 1 && matchInfo.getMiMatchFormat2() == 0){
				//每组只能两个人
				//获取每组人数
				List<Map<String,Object>> userCountByEveGroup = matchDao.getUserCountWithEveGroupByMatchId(matchId);
				for(Map<String,Object> userCount:userCountByEveGroup){
					Long groupId = getLongValue(userCount,"groupId");
					String groupName = getName(userCount,"groupName");
					Integer countEveGroup = getIntegerValue(userCount,"count");
					if(countEveGroup != 2){
						return groupName+" 参赛人数不符合比赛要求，无法开始比赛。";
					}
					if(joinTeamIdList.size()>1){
						//队际赛，一组的两个人要是两个球队的 就判断每一组的球队个数，如果为1 说明这俩人是一队，不能开始比赛
						//获取本组的球队个数
						List<Map<String,Object>> teamCountByGroup = matchDao.getTeamCountByMatchId(matchId,groupId);
						if(getIntegerValue(teamCountByGroup.get(0),"count") !=2){
							return groupName+" 球队数不符合比赛要求，无法开始比赛。";
						}
					}
				}
			}else if(matchInfo.getMiMatchFormat2() == 1){
				//1、如果是多队双人比赛，不管比杆比洞，每组最多两个队，每个队不超过两人，也可以是一人。生成记分卡时，只有一个队的两个人才能放入一行。
				if(joinTeamIdList.size() == 2){
					//所有组球队个数
					for(Map<String,Object> teamCount :teamCountList){
						Long groupId = getLongValue(teamCount,"groupId");
						String groupName = getName(teamCount,"groupName");
						Integer tCount = getIntegerValue(teamCount,"count");
						if(tCount >2 || tCount == 1){
							return groupName+" 球队数不符合比赛要求，无法开始比赛。";
						}
						//获取本组每个球队的人数
						userCountListByTeam = matchDao.getUserCountByMatchId(matchId,groupId);
						for(Map<String,Object> userCount:userCountListByTeam){
							Integer c = getIntegerValue(userCount,"count");
							if(c >2){
								return groupName+" 参赛人数不符合比赛要求，无法开始比赛。";
							}
						}
					}
				}
			}
		}
		matchInfo.setMiIsEnd(state);
		matchInfo.setMiUpdateUserId(userInfo.getUiId());
		matchInfo.setMiUpdateUserName(userInfo.getUiRealName());
		matchInfo.setMiUpdateTime(System.currentTimeMillis());
		matchDao.update(matchInfo);
		return "true";
	}

	/**
	 * 成绩上报 杆数越少 代表成绩越好 （注意球友加入球队是否成功）
	 * ***************拿每个参赛队和每个上报球队算交集，如果没有就忽略，如果有，就把交集的队员成绩交给上报球队并积分
	 * @param scoreType 积分规则 1：杆差倍数  2：赢球奖分,
	 * @param baseScore 基础分,
	 * @param rodScore  杆差倍数,
	 * @param winScore  赢球奖分
	 * @return
	 */
	public void submitScoreByTeamId(Long matchId, Long teamId, Integer scoreType, Integer baseScore,
									   Integer rodScore, Integer winScore, String openid) {
		Long userId = userService.getUserIdByOpenid(openid);
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		String reportTeamIds = matchInfo.getMiReportScoreTeamId();
		List<Long> reportTeamIdList = null;
		Long isReportTeamCaptain = 0L;
		if(StringUtils.isNotEmpty(reportTeamIds)){
			//有上报球队
			reportTeamIdList = getLongTeamIdList(reportTeamIds);
			//我是否是上报球队的队长
			for(Long reportTeamId:reportTeamIdList){
				isReportTeamCaptain = teamService.getIsCaptain(userId, reportTeamId);
				if(isReportTeamCaptain >0){
					//是上报球队队长
					//新增、更新 上报球队——积分配置 允许重复上报（修改）
					saveOrUpdateReportConfig(matchId, reportTeamId, teamId, baseScore, rodScore, winScore, openid);
					//获取本球队上报的球友
					List<Long> userIdList = matchDao.getScoreUserList(matchId,teamId);
					//计算球友积分（一场比赛对应一次积分）
					calculateScore(matchInfo,reportTeamId, userIdList, scoreType, baseScore, rodScore, winScore);
				}
			}
		}
		//我是否是本球队队长，页面上其实已经做过判断，为了保险，此处再校验一次
		Long isTeamCaptain = teamService.getIsCaptain(userId, teamId);
		if(isTeamCaptain >0){
			//更新配置 允许重复上报（修改）
			saveOrUpdateConfig(matchId, teamId, baseScore, rodScore, winScore, openid);
			//计算球友积分（一场比赛对应一次积分）
			calculateScore(matchInfo,teamId, null, scoreType, baseScore, rodScore, winScore);
		}
	}

	//计算球友积分 teamId 球队id,上报球队id
	//（一场比赛对应一次积分）
	private void calculateScore(MatchInfo matchInfo, Long teamId, List<Long> userIdList, Integer scoreType, Integer baseScore,
								Integer rodScore, Integer winScore) {
		Integer format = matchInfo.getMiMatchFormat1();
		if (format == 0) {
			//比杆赛
			//积分“杆差倍数”和“赢球奖分”只能二选  球友积分=基础积分+（110-球友比分）*杆差倍数
			//或者 球友积分=基础积分+赢球奖分/比赛排名
			updatePointByRodScore(matchInfo.getMiId(), teamId, userIdList, scoreType, baseScore, rodScore, winScore);
		} else if (format == 1) {
			//比洞赛
			// 比洞赛积分，只计算“基础积分”和“赢球奖分”两项，其中输球的组赢球奖分为0，打平的为一半。
			// 计算公式为：球友积分=基础积分+赢球奖分
			updatePointByHoleScore(matchInfo.getMiId(), teamId, baseScore, winScore);
		}
		//将该组的得分成绩标为已确认
		matchDao.updateMatchScoreById(matchInfo.getMiId(),teamId,userIdList);
	}

	//保存成绩提交的积分计算配置 允许重复上报（修改）
	private void saveOrUpdateConfig(Long matchId, Long teamId,Integer baseScore, Integer rodScore, Integer winScore, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		String userName = StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName();
		IntegralConfig config = matchDao.getSubmitScoreConfig(matchId,null,teamId,0);
		if(config == null){
			config = new IntegralConfig();
			config.setIcMatchId(matchId);
			config.setIcTeamId(teamId);
			config.setIcBaseScore(baseScore);
			config.setIcRodCha(rodScore);
			config.setIcWinScore(winScore);
			config.setIcCreateTime(System.currentTimeMillis());
			config.setIcCreateUserId(userInfo.getUiId());
			config.setIcCreateUserName(userInfo.getUiRealName());
			matchDao.save(config);
		}else{
			config.setIcBaseScore(baseScore);
			config.setIcRodCha(rodScore);
			config.setIcWinScore(winScore);
			config.setIcUpdateTime(System.currentTimeMillis());
			config.setIcUpdateUserId(userInfo.getUiId());
			config.setIcUpdateUserName(userName);
			matchDao.update(config);
		}
	}
	//保存成绩提交的积分计算配置 允许重复上报（修改）
	private void saveOrUpdateReportConfig(Long matchId, Long reportTeamId, Long teamId, Integer baseScore, Integer rodScore, Integer winScore, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		String userName = StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName();
		IntegralConfig config = matchDao.getSubmitScoreConfig(matchId,reportTeamId,teamId,1);
		if(config == null){
			config = new IntegralConfig();
			config.setIcMatchId(matchId);
			//上报球队id
			config.setIcReportTeamId(reportTeamId);
			//被上报球队id
			config.setIcTeamId(teamId);
			config.setIcBaseScore(baseScore);
			config.setIcRodCha(rodScore);
			config.setIcWinScore(winScore);
			config.setIcCreateTime(System.currentTimeMillis());
			config.setIcCreateUserId(userInfo.getUiId());
			config.setIcCreateUserName(userName);
			matchDao.save(config);
		}else{
			config.setIcBaseScore(baseScore);
			config.setIcRodCha(rodScore);
			config.setIcWinScore(winScore);
			config.setIcUpdateTime(System.currentTimeMillis());
			config.setIcUpdateUserId(userInfo.getUiId());
			config.setIcUpdateUserName(userName);
			matchDao.update(config);
		}
	}

	/**
	 * 成绩上报 比杆赛   （一场比赛对应一次积分）
	 * 积分“杆差倍数”和“赢球奖分”只能二选
	 * @param scoreType 积分规则 1：杆差倍数  2：赢球奖分,
	 * @param teamId 上报球队id或者本球队id
	 * 杆差倍数 ：球友积分=基础积分+（110-球友比分）*杆差倍数
	 * 赢球奖分 ：球友积分=基础积分+赢球奖分/比赛排名
	 *
	 */
	private void updatePointByRodScore(Long matchId, Long teamId, List<Long> userIdList,Integer scoreType, Integer baseScore,
									   Integer rodScore, Integer winScore) {
		if (scoreType == 1) {
			//杆差倍数 球友积分=基础积分+（110-球友比分）*杆差倍数
			//获取该队伍的得分情况
			List<Map<String, Object>> matchScoreList = matchDao.getSumScoreListByMatchIdTeamId(matchId, teamId,userIdList);
			if (matchScoreList != null && matchScoreList.size() > 0) {
				for (Map<String, Object> scoreMap : matchScoreList) {
					//杆数
					Integer score = getIntegerValue(scoreMap, "sumRodNum");
					Long userId = getLongValue(scoreMap, "userId");
					Integer point = baseScore + (Const.DEFAULT_ROD_NUM - score) * rodScore;
					//更新该球友原先的积分情况  （一场比赛对应一次积分）
					updatePointByIds(matchId, teamId, userId, point, 0);
				}
			}
		} else {
			//赢球奖分 球友积分=基础积分+赢球奖分/比赛排名
			//计算杆数的总排名
			List<Map<String, Object>> matchScoreList = matchDao.getRankingListByMatchId(matchId, teamId,userIdList);
			if (matchScoreList != null && matchScoreList.size() > 0) {
				for (int i=0;i<matchScoreList.size();i++) {
                    Map<String, Object> scoreMap = matchScoreList.get(i);
					Long teamIdByScore = getLongValue(scoreMap, "teamId");
					if(teamId.equals(teamIdByScore)){
						Long userId = getLongValue(scoreMap, "userId");
						//排名
						Integer rank = i+1;
						//计算得分
						Integer point = baseScore + winScore/rank;
						//计算得分(有小数)
						/*BigDecimal a = new BigDecimal(baseScore);
						BigDecimal b = new BigDecimal((float)winScore/rank);
						BigDecimal c = a.add(b);
						Double point = c.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();*/
						//更新该球友原先的积分情况
						updatePointByIds(matchId, teamIdByScore, userId, point, 0);

					}
				}
			}
		}
	}


	/**
	 * 撤销成绩上报
	 * @param matchId 比赛id,
	 * @param teamId 上报球队id,
	 * @return
	 */
	public boolean cancelScoreByTeamId(Long matchId, Long teamId, String openid) {
		Long userId = userService.getUserIdByOpenid(openid);
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		String reportTeamIds = matchInfo.getMiReportScoreTeamId();
		List<Long> reportTeamIdList = null;
		Long isReportTeamCaptain = 0L;
		if(StringUtils.isNotEmpty(reportTeamIds)){
			//有上报球队
			reportTeamIdList = getLongTeamIdList(reportTeamIds);
			//我是否是上报球队的队长
			for(Long reportTeamId:reportTeamIdList){
				isReportTeamCaptain = teamService.getIsCaptain(userId, reportTeamId);
				if(isReportTeamCaptain >0){
					//是上报球队队长
					//获取该球队的上报的球友
					List<Long> userIdList = matchDao.getScoreUserList(matchId,teamId);
					//获取配置
					IntegralConfig config = matchDao.getSubmitScoreConfig(matchId,reportTeamId,teamId,1);
					if(config == null){
						return false;
					}
					//计算得分
					updateScore(matchInfo,reportTeamId,config,userIdList);
				}
			}
		}
		//我是否是本球队队长，页面上其实已经做过判断，为了保险，此处再校验一次
		Long isTeamCaptain = teamService.getIsCaptain(userId, teamId);
		if(isTeamCaptain >0){
			IntegralConfig config = matchDao.getSubmitScoreConfig(matchId,null,teamId,0);
			if(config == null){
				return false;
			}
			//计算得分
			updateScore(matchInfo,teamId,config,null);
		}
		return true;
	}

	//撤销上报——计算得分 teamId:球队id 或者上报球队id
	private void updateScore(MatchInfo matchInfo,Long teamId,IntegralConfig config,List<Long> userIdList) {
		//基础积分
		Integer baseScore = config.getIcBaseScore();
		//杆差倍数
		Integer rodCha = config.getIcRodCha();
		//赢球奖分
		Integer winScore = config.getIcWinScore();
		if (matchInfo.getMiMatchFormat1() == 0) {
			//比杆赛 积分“杆差倍数”和“赢球奖分”只能二选  球友积分=基础积分+（110-球友比分）*杆差倍数 或者 球友积分=基础积分+赢球奖分/比赛排名
			if(rodCha != null){
				//获取该队伍的得分情况
				List<Map<String, Object>> teamScoreList = matchDao.getSumScoreListByMatchIdTeamId(matchInfo.getMiId(), teamId, userIdList);
				if (teamScoreList != null && teamScoreList.size() > 0) {
					for (Map<String, Object> scoreMap : teamScoreList) {
						//杆数
						Integer score = getIntegerValue(scoreMap, "sumRodNum");
						Long userId = getLongValue(scoreMap, "userId");
						Integer point = baseScore + (Const.DEFAULT_ROD_NUM - score) * rodCha;
						//更新该球友的积分情况
						updatePointByIds(matchInfo.getMiId(), teamId, userId, point, 1);
					}
				}
			}else if(winScore != null){
				//选择的赢球奖分 赢球奖分 球友积分=基础积分+赢球奖分/比赛排名
				//计算杆数的总排名
				List<Map<String, Object>> matchScoreList = matchDao.getRankingListByMatchId(matchInfo.getMiId(),teamId,userIdList);
				if (matchScoreList != null && matchScoreList.size() > 0) {
                    for (int i=0;i<matchScoreList.size();i++) {
                        Map<String, Object> scoreMap = matchScoreList.get(i);
						Long teamIdByScore = getLongValue(scoreMap, "teamId");
						if(teamId.equals(teamIdByScore)){
							Long userId = getLongValue(scoreMap, "userId");
							//排名
							Integer rank = i+1;
							//计算得分
							Integer point = baseScore + winScore/rank;
							//更新该球友原先的积分情况
							updatePointByIds(matchInfo.getMiId(), teamIdByScore, userId, point, 1);
						}
					}
				}
			}
		} else if (matchInfo.getMiMatchFormat1() == 1) {
			//比洞赛
			// 比洞赛积分，只计算“基础积分”和“赢球奖分”两项，其中输球的组赢球奖分为0，打平的为一半。
			// 计算公式为：球友积分=基础积分+赢球奖分
			//获取本次比赛中的分组和每组用户的总分
			List<Map<String,Object>> holeScoreList = matchDao.getMatchHoleScoreList(matchInfo.getMiId());
			if(holeScoreList != null && holeScoreList.size()>0){
				for(int i = 0;i<holeScoreList.size();i++){
					Map<String,Object> team1 = holeScoreList.get(i);
					Map<String,Object> team2 = holeScoreList.get(i+1);
					i += 1;
					Long team1Id = getLongValue(team1,"teamId");
					Long team2Id = getLongValue(team2,"teamId");
					Integer team1SumRod = getIntegerValue(team1,"sumRod");
					Integer team2SumRod = getIntegerValue(team2,"sumRod");
					Long userForTeam = null;
					Integer point = 0;
					if(teamId.equals(team1Id)){
						userForTeam = getLongValue(team1,"userId");
						if(team1SumRod < team2SumRod){
							//第一队赢球 球友积分=基础积分+赢球奖分
							point = baseScore + winScore;
						}else if(team1SumRod > team2SumRod){
							//第一队输球 球友积分=基础积分+赢球奖分0
							point = baseScore;
						}else if(team1SumRod.equals(team2SumRod)){
							//打平 球友积分=基础积分+赢球奖分的一半
							point = baseScore + winScore/2;
						}
					}else if(teamId.equals(team2Id)){
						userForTeam = getLongValue(team2,"userId");
						if(team2SumRod < team1SumRod){
							//第二队赢球 球友积分=基础积分+赢球奖分
							point = baseScore + winScore;
						}else if(team2SumRod > team1SumRod){
							//第二队输球 球友积分=基础积分+赢球奖分0
							point = baseScore;
						}else if(team2SumRod.equals(team1SumRod)){
							//打平 球友积分=基础积分+赢球奖分的一半
							point = baseScore + winScore/2;
						}
					}
					//更新该球友的积分情况
					updatePointByIds(matchInfo.getMiId(), teamId, userForTeam, point, 1);
				}
			}
		}
		//删除积分计算配置
		matchDao.del(config);
		//将该组的得分成绩标为撤销
		matchDao.cancelMatchScoreById(matchInfo.getMiId(),teamId,userIdList);
	}


	/**
	 * 更新该球友原先的积分情况  （一场比赛对应一次积分）
	 * type: 0:加积分   1：减积分
	 */
	private void updatePointByIds(Long matchId, Long teamId, Long userId, Integer point, Integer type) {
		UserInfo userInfo = userService.getUserById(userId);
		//总积分
		TeamUserMapping teamUserMapping = matchDao.getTeamUserMappingByIds(teamId, userId);
		if (teamUserMapping.getTumPoint() == null || teamUserMapping.getTumPoint() == 0) {
			teamUserMapping.setTumPoint(point);
		} else {
			if(type == 0){
				teamUserMapping.setTumPoint(teamUserMapping.getTumPoint() + point);
			}else{
				teamUserMapping.setTumPoint(teamUserMapping.getTumPoint() - point);
			}
		}
		 teamUserMapping.setTumUpdateTime(System.currentTimeMillis());
		teamUserMapping.setTumUpdateUserId(userInfo.getUiId());
		teamUserMapping.setTumUpdateUserName(userInfo.getUiRealName());
		matchDao.update(teamUserMapping);

		//比赛积分
		TeamUserPoint teamUserPoint = matchDao.getTeamUserPoint(matchId, teamId, userId);
		if(type == 0){
			//加积分 或者 修改积分
			if(teamUserPoint == null){
				teamUserPoint = new TeamUserPoint();
				teamUserPoint.setTupMatchId(matchId);
				teamUserPoint.setTupTeamId(teamId);
				teamUserPoint.setTupUserId(userId);
				teamUserPoint.setTupMatchPoint(point);
				teamUserPoint.setTupCreateUserId(userId);
				teamUserPoint.setTupCreateUserName(StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName());
				teamUserPoint.setTupCreateTime(System.currentTimeMillis());
				matchDao.save(teamUserPoint);
			}else{
				teamUserPoint.setTupMatchPoint(point);
				teamUserPoint.setTupUpdateUserId(userId);
				teamUserPoint.setTupUpdateUserName(StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName());
				teamUserPoint.setTupUpdateTime(System.currentTimeMillis());
				matchDao.update(teamUserPoint);
			}
		}else{
			//撤销上报，删除球友比赛积分对应关系
			if(teamUserPoint != null){
				matchDao.del(teamUserPoint);
			}
		}
	}


	/**
	 * 成绩上报 比洞赛
	 * 积分只计算“基础积分”和“赢球奖分”两项，其中输球的组赢球奖分为0，打平的为一半。
	 * 计算公式为：球友积分=基础积分+赢球奖分
	 */
	private void updatePointByHoleScore(Long matchId, Long teamId, Integer baseScore, Integer winScore) {
		//获取本队的输球组和赢球组
        List<MatchHoleResult> matchHoleResultList = matchDao.getMatchHoleWinOrLoseList(matchId,teamId);
        if(matchHoleResultList != null && matchHoleResultList.size()>0){
            Integer point = 0;
            for(MatchHoleResult matchHoleResult:matchHoleResultList){
                if(matchHoleResult.getMhrResult() == 1){
                    //赢球组 球友积分=基础积分+赢球奖分
                    point = baseScore + winScore;
                }else if(matchHoleResult.getMhrResult() == 2){
                    //输球组 奖分为0 球友积分=基础积分+0
                    point = baseScore;
                }else if(matchHoleResult.getMhrResult() == 0){
                    //打平 球友积分=基础积分+赢球奖分的一半
                    point = baseScore + winScore/2;
                }
                //获取本组本球队的用户列表
                List<Map<String,Object>> userList = matchDao.getUserListByTeamId(matchId,teamId,matchHoleResult.getMhrGroupId());
                if(userList != null && userList.size() >0){
                    for(Map<String,Object> user:userList){
                        Long userId = getLongValue(user,"uiId");
                        //更新该球友的积分情况
                        updatePointByIds(matchId, teamId, userId, point, 0);
                    }
                }
            }
        }
	}

	/**
	 * 结束单练
	 * @return
	 */
	public void endSingleMatchById(Long matchId, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		matchInfo.setMiIsEnd(2);
		matchInfo.setMiUpdateTime(System.currentTimeMillis());
		matchInfo.setMiUpdateUserId(userInfo.getUiId());
		matchInfo.setMiUpdateUserName(userInfo.getUiRealName());
		matchDao.update(matchInfo);
	}

	/**
	 * 如果不是参赛人员，则加入围观用户
	 * @return
	 */
	public boolean saveOrUpdateWatch(MatchInfo matchInfo, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		Long userId = userInfo.getUiId();
		Long matchId = matchInfo.getMiId();
		//观战范围：3、封闭：参赛队员可见 并且不是参赛人员
		Long isJoinMatch = matchDao.getIsContestants(userId, matchId);
		if(matchInfo.getMiMatchOpenType() == 3 && isJoinMatch <=0){
			return false;
		}
		//是否已经围观
		Long watchCount = matchDao.getIsWatch(userId,matchId);
		if(watchCount <=0){
			//没有参加比赛的
			if (isJoinMatch <= 0) {
				if(matchInfo.getMiMatchOpenType() == 1){
					//1、公开 球友均可见； 直接加入围观用户
					MatchJoinWatchInfo matchJoinWatchInfo = new MatchJoinWatchInfo();
					matchJoinWatchInfo.setMjwiUserId(userId);
					matchJoinWatchInfo.setMjwiMatchId(matchId);
					matchJoinWatchInfo.setMjwiType(0);
					matchJoinWatchInfo.setMjwiCreateTime(System.currentTimeMillis());
					matchDao.save(matchJoinWatchInfo);
					return true;
				}else if(matchInfo.getMiMatchOpenType() == 2){
					//2、队内公开：参赛者的队友可见 查询是否是参赛球队的队员
					List<Long> teamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
					if(teamIdList != null && teamIdList.size()>0){
                        Long joinCount = matchDao.getIsJoinTeamsUser(userId,teamIdList);
                        if(joinCount > 0){
                            //是参赛队的队员，加入围观用户
                            MatchJoinWatchInfo matchJoinWatchInfo = new MatchJoinWatchInfo();
                            matchJoinWatchInfo.setMjwiUserId(userId);
                            matchJoinWatchInfo.setMjwiMatchId(matchId);
                            matchJoinWatchInfo.setMjwiType(0);
                            matchJoinWatchInfo.setMjwiCreateTime(System.currentTimeMillis());
                            matchDao.save(matchJoinWatchInfo);
                            return true;
                        }
                    }
				}
			}else{
				return true;
			}
		}else{
			return true;
		}
		return false;
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

		//第二条记录（总杆）
		MatchGroupUserScoreBean thBean2 = new MatchGroupUserScoreBean();
		thBean2.setUserId(0L);
		thBean2.setUserName("杆差");
		thBean2.setUserScoreTotalList(parkHoleList);
		list.add(thBean2);


		//比赛的所有用户(首列显示)
		List<Map<String, Object>> userList = matchDao.getUserListById(matchId, null,null);
		if(userList!= null && userList.size()>0){
			for(Map<String, Object> user:userList){
				String realName = getName(user,"uiRealName");
				if(StringUtils.isNotEmpty(realName) && realName.length() >7){
					user.put("uiRealName",realName.substring(0,7)+"...");
				}
				String nickName = getName(user,"uiNickName");
				if(StringUtils.isNotEmpty(nickName) && nickName.length() >7){
					user.put("uiNickName",nickName.substring(0,7)+"...");
				}
			}
		}
		result.put("userList", userList);

		//本组用户每个洞得分情况
		createNewUserScoreList(userList,list,matchInfo,0);

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
	public void getNewParkHoleList(List<MatchTotalUserScoreBean> parkHoleList, List<MatchTotalUserScoreBean> parkRodList,
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
	public Map<String, Object> getTeamScoreByMatchId(Long matchId, Long teamId, String openid) {
		Map<String, Object> result = new HashMap<>();
		List<MatchGroupUserScoreBean> list = new ArrayList<>();
		//比赛信息
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		result.put("matchInfo", matchInfo);
		Long userId = userService.getUserIdByOpenid(openid);

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
		thBean2.setUserName("总杆");
		thBean2.setUserScoreTotalList(parkHoleList);
		list.add(thBean2);

		//参赛球队
		String joinTeamIds = matchInfo.getMiJoinTeamIds();
		//成绩上报球队
		String reportTeamIds = matchInfo.getMiReportScoreTeamId();
		List<Long> reportTeamIdList = null;
		if(StringUtils.isNotEmpty(reportTeamIds)){
			reportTeamIdList = getLongTeamIdList(reportTeamIds);
		}
		if(StringUtils.isNotEmpty(joinTeamIds)){
			List<Long> joinTeamIdList = getLongTeamIdList(joinTeamIds);
			//参赛球队
			List<TeamInfo> teamList = matchDao.getTeamListByIds(joinTeamIdList);
			result.put("teamList", teamList);
			if (teamId == null) {
				//如果没选球队，默认显示第一个球队
				teamId = teamList.get(0).getTiId();
			}
		}


		//比赛的本球队所有用户(首列显示)
		List<Map<String, Object>> userList = null;
		Integer scoreType = 0;//默认查比赛记分
		if(reportTeamIdList != null && reportTeamIdList.size()>0 && reportTeamIdList.contains(teamId)){
			//选中的球队是上报球队,查上报给该球队的记分
			scoreType = 1;
			userList = matchDao.getReportUserListById(matchId, teamId);
		}else{
			userList = matchDao.getUserListById(matchId, null,teamId);
		}
		if(userList!= null && userList.size()>0){
			for(Map<String, Object> user:userList){
				String realName = getName(user,"uiRealName");
				if(StringUtils.isNotEmpty(realName) && realName.length() >7){
					user.put("uiRealName",realName.substring(0,7)+"...");
				}
				String nickName = getName(user,"uiNickName");
				if(StringUtils.isNotEmpty(nickName) && nickName.length() >7){
					user.put("uiNickName",nickName.substring(0,7)+"...");
				}
			}
		}
		result.put("userList", userList);

		//本组用户每个洞得分情况
		createNewUserScoreList(userList,list,matchInfo,scoreType);
		result.put("list", list);

		//是否是该球队队长
		Long isTeamCaptain = teamService.getIsCaptain(userId, teamId);
		result.put("isTeamCaptain", isTeamCaptain);
		//是否是上报球队队长
		if(reportTeamIdList != null && reportTeamIdList.size() >0){
			Long isReportTeamCaptain = teamService.getIsReportTeamCaptain(userId, reportTeamIdList);
			result.put("isReportTeamCaptain", isReportTeamCaptain);
		}

		return result;
	}

	//获取用户在每个球洞的得分情况
	private void createNewUserScoreList(List<Map<String, Object>> userList, List<MatchGroupUserScoreBean> list,MatchInfo matchInfo,Integer scoreType) {
		if (userList != null && userList.size() > 0) {
			for (Map<String, Object> user : userList) {
				Long uiId = getLongValue(user, "uiId");
				Long teamId = getLongValue(user, "team_id");
				MatchGroupUserScoreBean bean = new MatchGroupUserScoreBean();
				bean.setUserId(uiId);
				bean.setUserName(getName(user, "uiRealName"));
				//本用户的前后半场总得分情况
				List<MatchTotalUserScoreBean> userScoreList = new ArrayList<>();
				//本用户前半场得分情况
				List<Map<String, Object>> uScoreBeforeList = matchDao.getBeforeAfterScoreByUserId(uiId, matchInfo,0,teamId,scoreType);
				createNewUserScore(userScoreList, uScoreBeforeList);
				//本用户后半场得分情况
				List<Map<String, Object>> uScoreAfterList = matchDao.getBeforeAfterScoreByUserId(uiId, matchInfo,1,teamId,scoreType);
				createNewUserScore(userScoreList, uScoreAfterList);
				bean.setUserScoreTotalList(userScoreList);

				list.add(bean);
			}
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
	 * 计算时各队的参赛人数都是一样的，如果有球队的参赛人数小于n,少的那几个人，每人杆数按110计算
	 * @return
	 */
	public Map<String, Object> getTeamTotalScoreByMatchId(Long matchId, Integer mingci) {
		Map<String, Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		List<Long> joinTeamIdList = null;
		//参赛球队
		if(StringUtils.isNotEmpty(matchInfo.getMiJoinTeamIds())){
			joinTeamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
		}

//		赛制1( 0:比杆 、1:比洞)
		if (matchInfo.getMiMatchFormat1() == 0) {
			//比杆赛
			rodMatch(joinTeamIdList,mingci,matchId,matchInfo,result);
		} else {
			//比洞赛 n就按按分组顺序取前n组，还按原来的表头计算
			//如果是两个队比洞的话，把每队各组的分相加，其中赢的组得1分，输的组得0分，平的组各得0.5分。一个队队内，及多个队之间没法进行比洞赛
			holeMatch(matchId,result,mingci,matchInfo,joinTeamIdList);
		}
		return result;
	}



	/**
	 * 比杆赛——分队统计
	 * 如果是比杆赛，名次就取每队成绩最好（杆数最少）的前n人（如果是双人，就是前5组）计算
	 * 				 按创建比赛时“参赛范围”选择的球队统计成绩并按平均杆数排名，（球队、参赛人数、平均杆数、总杆数、排名）
	 * 计算时各队的参赛人数都是一样的，如果有球队的参赛人数小于n,少的那几个人，每人杆数按110(Const.DEFAULT_ROD_NUM)计算
	 */
	private void rodMatch(List<Long> joinTeamIdList, Integer mingci, Long matchId,
						  MatchInfo matchInfo, Map<String,Object> result) {
		//取前n名
		List<Map<String,Object>> nList = new ArrayList<>();
		Map<String,Object> nMap = new HashMap<>();
		//要显示的列表
		List<MatchTeamRankingBean> matchTeamRankingList = new ArrayList<>();
		//有参赛队
		if(joinTeamIdList != null && joinTeamIdList.size()>0){
			if(joinTeamIdList.size() == 1){
				//队内赛，直接取前n名的 平均成绩和总杆数
				if(mingci == null || mingci == 0){
					//N的list
					getNList(nList,matchInfo,joinTeamIdList,nMap);
					mingci = getIntegerValue(nMap,"minCount");
				}else{
					getNList(nList,matchInfo,joinTeamIdList,nMap);
				}
				//要显示的列表
				List<Map<String, Object>> scoreList = matchDao.getMatchRodTotalScoreByMingci(matchId,joinTeamIdList.get(0),mingci);
				TeamInfo teamInfo = matchDao.get(TeamInfo.class,joinTeamIdList.get(0));
				MatchTeamRankingBean matchTeamRankingBean = new MatchTeamRankingBean();
				matchTeamRankingBean.setTeamName(teamInfo.getTiName());
				matchTeamRankingBean.setUserCount(nList.size());
				matchTeamRankingBean.setAvgRodNum(getDoubleValue(scoreList.get(0),"avgRodNum"));
				matchTeamRankingBean.setSumRodNum(getIntegerValue(scoreList.get(0),"sumRodNum"));
				matchTeamRankingList.add(matchTeamRankingBean);
			}else if(joinTeamIdList.size() > 1){
				//多队比杆：一般要求各队参赛人数是一样的，但实际情况可能会不一样，
				// 因此缺省的设置数字，可以按人数最少的那个队的显示（如果是双人赛就显示最少的组数），
				// 然后n最大值为人数最多的那个队
				// 如果有球队的参赛人数小于n,少的那几个人，每人杆数按110(Const.DEFAULT_ROD_NUM)计算
				if(mingci == null || mingci == 0){
					//N的list
					getNList(nList,matchInfo,joinTeamIdList,nMap);
					mingci = getIntegerValue(nMap,"minCount");
				}else{
					getNList(nList,matchInfo,joinTeamIdList,nMap);
				}
				//人数多的球队人数
				Long maxCount = getLongValue(nMap,"maxCount");
				//然后按照这个数字假如是5，计算各队前5人（双人是前5组）的总杆数及平均杆数并排名 ，
				// 获取本比赛每个队的参赛人数 显示的是各队实际的参赛人数
				List<Map<String,Object>> joinMatchUserList = matchDao.getJoinMatchUserList(matchId);
				if(joinMatchUserList != null && joinMatchUserList.size() >0){
					for(Map<String,Object> team:joinMatchUserList){
						Long teamId = getLongValue(team,"teamId");
						MatchTeamRankingBean matchTeamRankingBean = new MatchTeamRankingBean();
						matchTeamRankingBean.setTeamId(teamId);
						matchTeamRankingBean.setTeamName(getName(team,"teamName"));
						matchTeamRankingBean.setUserCount(getIntegerValue(team,"userCount"));
						//获取本队参赛人数
						List<Long> teamIdList = new ArrayList<>();
						teamIdList.add(teamId);
						List<Map<String,Object>> mapList = matchDao.getUserCountByMatchUserMappingTeamId(matchInfo.getMiId(),teamIdList);
						Integer userCount = getIntegerValue(mapList.get(0),"userCount");
						Integer sumRodNumByN = 0;
						//本球队参赛人数小于名次的N，并且小于最大参赛人数
						if(userCount < mingci && userCount < maxCount){
							// 如果有球队的参赛人数小于n,少的那几个人，每人杆数按110(Const.DEFAULT_ROD_NUM)计算
							Long n = maxCount - userCount;
							sumRodNumByN = n.intValue()*110;
						}
						//获取每个队前n名的成绩
						List<Map<String, Object>> scoreList = matchDao.getMatchRodTotalScoreByMingci(matchId,teamId,mingci);
						if(scoreList != null && scoreList.size()>0){
							if(sumRodNumByN > 0){
								Integer sum = getIntegerValue(scoreList.get(0),"sumRodNum")+sumRodNumByN;
								BigDecimal b = new BigDecimal((float)(sum/mingci));
								Double avg = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
								matchTeamRankingBean.setAvgRodNum(avg);
								matchTeamRankingBean.setSumRodNum(sum);
							}else{
								matchTeamRankingBean.setAvgRodNum(getDoubleValue(scoreList.get(0),"avgRodNum"));
								matchTeamRankingBean.setSumRodNum(getIntegerValue(scoreList.get(0),"sumRodNum"));
							}
						}else{
							matchTeamRankingBean.setAvgRodNum(0.0);
							matchTeamRankingBean.setSumRodNum(0);
						}
						matchTeamRankingList.add(matchTeamRankingBean);
					}
					//排序
					Collections.sort(matchTeamRankingList,new Comparator<MatchTeamRankingBean>(){
						public int compare(MatchTeamRankingBean bean1,MatchTeamRankingBean bean2){
							return new Double(bean1.getAvgRodNum()).compareTo(new Double(bean2.getAvgRodNum()));}
					});
				}
			}
		}
		result.put("scoreList",matchTeamRankingList);
		result.put("mingciArray",nList);
	}

	/**
	 * 比洞赛——分队统计 跟总分没关系，是逐洞比，那组赢的洞多那组赢
	 * 如果是比洞赛，n就按按分组顺序取前n组，还按原来的表头计算
	 * 比洞赛：用不同的表。（球队、获胜组、打平组、得分、排名）
	 * 如果是两个队比洞的话，把每队各组的分相加，其中赢的组得1分，输的组得0分，平的组各得0.5分。一个队队内，及多个队之间没法进行比洞赛
	 */
	private void holeMatch(Long matchId,Map<String, Object> result, Integer mingci,MatchInfo matchInfo, List<Long> joinTeamIdList) {
		//取前n组
		List<Map<String,Object>> nList = new ArrayList<>();
		Map<String,Object> nMap = new HashMap<>();
		if(mingci == null || mingci == 0){
			//N的list
			getNList(nList,matchInfo,joinTeamIdList,nMap);
			mingci = getIntegerValue(nMap,"minCount");
		}else{
			getNList(nList,matchInfo,joinTeamIdList,nMap);
		}
		//获取前n组的比赛情况
		List<Long> groupList = matchDao.getGroupIdByMingci(matchId,mingci);
		//获取参赛队(有可能是公开赛，所以要从matchuserMapping表中获取参赛队)
        List<Map<String,Object>> teamInfoList = matchDao.getJoinTeamMappingList(matchId,groupList);
        //要显示的列表
        List<MatchTotalTeamBean> beanList = new ArrayList<>();
        if(teamInfoList != null && teamInfoList.size()>0){
            for(Map<String,Object> teamMap:teamInfoList){
				Long teamId = getLongValue(teamMap,"teamId");
                //每个球队的平、赢组个数
                MatchTotalTeamBean matchTotalTeamBean = new MatchTotalTeamBean();
                matchTotalTeamBean.setTeamId(teamId);
                TeamInfo teamInfo = matchDao.get(TeamInfo.class,teamId);
                matchTotalTeamBean.setTeamName(teamInfo.getTiName());
                List<Map<String, Object>> numList = matchDao.getPingWinNumList(matchId,teamId);
                if(numList != null && numList.size() >0){
                    Map<String, Object> map = numList.get(0);
                    Integer winNum = getIntegerValue(map,"winNum");
					Integer pingNum = getIntegerValue(map,"pingNum");
                    matchTotalTeamBean.setWinNum(winNum);
                    matchTotalTeamBean.setPingNum(pingNum);
					BigDecimal score = new BigDecimal(winNum.floatValue() + pingNum.floatValue() * 0.5).setScale(1, BigDecimal.ROUND_HALF_UP);
					matchTotalTeamBean.setScore(score.intValue());
                }
                beanList.add(matchTotalTeamBean);
            }
            Collections.sort(beanList);
        }
		result.put("scoreList",beanList);
		result.put("mingciArray",nList);
	}

	//取前N名 N的list
	private void getNList(List<Map<String, Object>> nList, MatchInfo matchInfo,
											   List<Long> joinTeamIdList,Map<String,Object> nMap) {
		Long minCount = 0L;
		Long maxCount = 0L;
		List<Map<String,Object>> mapList = null;
		Map<String,Object> map = null;
		if(matchInfo.getMiMatchFormat2() == 0){
			//单人赛 人数较少的队伍的人数
			mapList = matchDao.getUserCountByMatchUserMappingTeamId(matchInfo.getMiId(),joinTeamIdList);
			minCount = getLongValue(mapList.get(0),"userCount");
			maxCount = getLongValue(mapList.get(mapList.size()-1),"userCount");
		}else{
			//如果是双人赛,默认显示最少的组数，最大值为人数最多的数
			mapList = matchDao.getUserCountByMatchGroupTeamId(matchInfo.getMiId(),joinTeamIdList);
			minCount = getLongValue(mapList.get(0),"groupCount");
			maxCount = getLongValue(mapList.get(mapList.size()-1),"groupCount");
		}
		for(int i=0;i<maxCount;i++){
			Map<String,Object> n = new HashMap<>();
			n.put("id",i);
			n.put("name",i+1);
			nList.add(n);
		}
		nMap.put("minCount",minCount);
		nMap.put("maxCount",maxCount);
	}

	/**
	 * 删除比赛
	 *
	 * @return
	 */
	public boolean delMatchById(Long matchId, String openid) {
		//判断是否是我创建的
		Long count = matchDao.getIsMyCreatMatch(matchId, userService.getUserIdByOpenid(openid));
		if (count != null && count > 0) {
			//删除比赛 逻辑删除 置为不可用
			matchDao.updateMatchState(matchId);
			return true;
		}
		return false;
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
	public Integer applyMatch(Long matchId, Long groupId, String groupName, Long chooseTeamId, String openid) {
		Integer flag = -1;
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		UserInfo userInfo = userService.getUserByOpenId(openid);
		Long userId = userInfo.getUiId();
		List<Long> teamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
		//是否已经报名
		Long countApply = matchDao.getIsApply(userId,matchId);
		if(countApply >0){
			return -2;
		}
		//查看是否是该球队的队员，如果不是，自动生成一条入队申请
		if(chooseTeamId != null){
			TeamUserMapping teamUserMapping = matchDao.getTeamUserMappingByIds(chooseTeamId,userId);
			if(teamUserMapping == null){
				teamUserMapping = new TeamUserMapping();
				teamUserMapping.setTumTeamId(chooseTeamId);
				teamUserMapping.setTumUserId(userId);
				teamUserMapping.setTumUserType(2);
				teamUserMapping.setTumCreateTime(System.currentTimeMillis());
				teamUserMapping.setTumCreateUserId(userId);
				teamUserMapping.setTumCreateUserName(userInfo.getUiRealName());
				matchDao.save(teamUserMapping);
			}
		}
		//查询是否在临时分组中
		MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId,null,userId);
		if(matchUserGroupMapping == null){
			matchUserGroupMapping = new MatchUserGroupMapping();
			matchUserGroupMapping.setMugmMatchId(matchId);
			matchUserGroupMapping.setMugmGroupId(groupId);
			matchUserGroupMapping.setMugmGroupName(groupName);
			matchUserGroupMapping.setMugmUserId(userId);
			matchUserGroupMapping.setMugmUserName(StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName());
			matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
			matchUserGroupMapping.setMugmCreateUserId(userId);
			matchUserGroupMapping.setMugmCreateUserName(StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName());
			matchUserGroupMapping.setMugmUserType(1);
			matchUserGroupMapping.setMugmIsDel(0);
			//参赛范围 赛长可能修改比赛的赛制(1、公开 球友均可报名；2、队内：某几个球队队员可报名；3:不公开)
			if(matchInfo.getMiJoinOpenType() == 1){
				matchDao.save(matchUserGroupMapping);
				flag = 0;
			}else if(matchInfo.getMiJoinOpenType() == 2){
				matchUserGroupMapping.setMugmTeamId(chooseTeamId);
				//是否参赛球队的队员
				Long count = matchDao.getIsJoinTeamsUser(userId,teamIdList);
				if(count >0){
					matchDao.save(matchUserGroupMapping);
					flag = 0;
				}
			}
		}else{
			matchUserGroupMapping.setMugmGroupId(groupId);
			matchUserGroupMapping.setMugmGroupName(groupName);
			matchUserGroupMapping.setMugmIsDel(0);
			matchUserGroupMapping.setMugmUpdateTime(System.currentTimeMillis());
			matchUserGroupMapping.setMugmUpdateUserId(userId);
			matchUserGroupMapping.setMugmUpdateUserName(userInfo.getUiRealName());
			if(matchInfo.getMiJoinOpenType() == 1){
				matchDao.update(matchUserGroupMapping);
				flag = 0;
			}else if(matchInfo.getMiJoinOpenType() == 2){
				matchUserGroupMapping.setMugmTeamId(chooseTeamId);
				//是否参赛球队的队员
				Long count = matchDao.getIsJoinTeamsUser(userId,teamIdList);
				if(count >0){
					matchDao.update(matchUserGroupMapping);
					flag = 0;
				}
			}
		}

		return flag;
	}

	/**
	 * 比赛——普通用户从一个组退出比赛
	 * @return
	 */
	public void quitMatch(Long matchId, Long groupId, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		Long userId = userInfo.getUiId();
		MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId,null,userId);
		if(matchUserGroupMapping != null){
			matchUserGroupMapping.setMugmIsDel(1);
			matchUserGroupMapping.setMugmUpdateUserId(userId);
			matchUserGroupMapping.setMugmUpdateUserName(StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName());
			matchUserGroupMapping.setMugmUpdateTime(System.currentTimeMillis());
			matchDao.update(matchUserGroupMapping);
		}
//		matchDao.delFromMatch(matchId, groupId, userId);
	}


	/**
	 * 比赛——邀请记分——生成二维码
	 * 接口B: 获取小程序码（永久有效、数量暂无限制）.
	 * https://developers.weixin.qq.com/miniprogram/dev/api/getWXACodeUnlimit.html
	 * 必须是已经发布的小程序存在的页面（否则报错），例如 pages/index/index,
	 * 根路径前不要填加 /,不能携带参数（参数请放在scene字段里），如果不填写这个字段，默认跳主页面
	 * @return
	 */
	public String invitationScore(Long matchId, Long groupId, String openid) throws WxErrorException, IOException {
		String path = null;
		Long myUserId = userService.getUserIdByOpenid(openid);
		//查询是否有我生成的邀请记分二维码
		MatchScoreUserMapping matchScoreUserMapping = matchDao.getMatchScoreUserMapping(matchId, groupId, myUserId);
		if(matchScoreUserMapping != null){
			path = PropertyConst.DOMAIN + PropertyConst.QRCODE_PATH + matchId+"_"+groupId+"_"+myUserId+".png";
			return path;
		}
		//没有生成过
		String fileName = matchId+"_"+groupId+"_"+myUserId+".png";//文件名称 比赛id_本组id_邀请人id
		String QRCodePath = WebUtil.getPath()+PropertyConst.QRCODE_PATH;
		File file = new File(QRCodePath);
		if(!file.exists()){
			file.mkdirs();
		}
		String parp = "matchId="+matchId+"&groupId="+groupId;//参数
		String scene = URLEncoder.encode(parp,"utf-8");
		String page ="pages/score_card/score_card";//要跳转的页面，根路径不能加/  不填默认跳转主页
//		file = wxMaService.getQrcodeService().createWxaCodeUnlimit(scene,page); 此方法好像是有包冲突，会报错
		byte[] result = wxMaService.getQrcodeService().createWxaCodeUnlimitBytes(scene,page,300,false,null,false);
		InputStream inputStream = null;
		OutputStream outputStream = null;
		inputStream = new ByteArrayInputStream(result);
		outputStream = new FileOutputStream(file);
		int len = 0;
		byte[] buf = new byte[1024];
		while ((len = inputStream.read(buf, 0, 1024)) != -1) {
			outputStream.write(buf, 0, len);
		}
		outputStream.flush();

		//新增一条记录
		matchScoreUserMapping = new MatchScoreUserMapping();
		matchScoreUserMapping.setMsumMatchId(matchId);
		matchScoreUserMapping.setMsumGroupId(groupId);
		matchScoreUserMapping.setMsumMatchUserId(myUserId);
		matchScoreUserMapping.setMsumCreateTime(System.currentTimeMillis());
		matchDao.save(matchScoreUserMapping);
		path = PropertyConst.DOMAIN + PropertyConst.QRCODE_PATH + fileName;
		return path;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
//		String scene = "matchId="+1+"&groupId="+2;//参数
//		String a = URLEncoder.encode(scene,"utf-8");
//		System.out.println(a);
//		System.out.println(URLDecoder.decode(a,"utf-8"));

		int InitProducts[] = { 1, 2, 3, 4, 5 };
		int deleteProducts[] = { 4, 2 };
		int newProducts[] = new int[InitProducts.length
				- deleteProducts.length];
		int count = 0;
		for (int i = 0; i < InitProducts.length; i++) {
			boolean flag = true;
			int temp = InitProducts[i];
			for (int j = 0; j < deleteProducts.length; j++) {
				if (temp==deleteProducts[j]) {
					flag = false;
					break;
				}
			}
			if (flag) {
				newProducts[count++] = temp;
			}
		}
		System.out.println(Arrays.toString(newProducts));
	}

	/**
	 * 单练——更新临时用户姓名
	 * @return
	 */
	public void updateTemporaryUserNameById(Long userId, String userName, Long matchId, Long groupId) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("userId",userId);
		parp.put("userName",userName);
		parp.put("matchId",matchId);
		parp.put("groupId",groupId);
		//更新比赛用户mapping中的临时用户姓名
		matchDao.updateMatchUserMapping(parp);
		//更新记分表中的临时用户姓名
		matchDao.updateMatchScoreUserInfo(parp);
	}

	/**
	 * 根据用户id获取用户差点 不包括单练
	 * 取最近十场比赛的成绩平均（不够十场按实际场数），减去72然后再乘0.8
	 * @return
	 */
	public Double getUserChaPoint(Long userId) {
		//获取最近参加的10场比赛的id
		List<Object> list = matchDao.getLessFiveMatchByUserId(userId);
		if(list != null && list.size()>0){
			BigDecimal avg = (BigDecimal)list.get(0);
			if(avg != null){
				BigDecimal processClll = new BigDecimal((avg.floatValue() - 72) * 0.8).setScale(1, BigDecimal.ROUND_HALF_UP);
				return processClll.doubleValue();
			}
		}
		return 0.0;
	}

	/**
	 * 赛长指定该用户成为赛长
	 * @param matchId:比赛id
	 * @param userId:被指定人id
	 * @return
	 */
	public void setMatchCaptainByUserId(Long matchId, Long userId, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId,null,userId);
		matchUserGroupMapping.setMugmUserType(0);
		matchUserGroupMapping.setMugmUpdateTime(System.currentTimeMillis());
		matchUserGroupMapping.setMugmUpdateUserId(userInfo.getUiId());
		matchUserGroupMapping.setMugmUpdateUserName(userInfo.getUiRealName());
		matchDao.update(matchUserGroupMapping);
	}

	/**
	 * 记分卡 初始化 查询我是否可以记分
	 * @return
	 */
	public boolean getMeCanScore(Long matchId, Long groupId, String openid) {
		Long userId = userService.getUserIdByOpenid(openid);
		//是否是赛长
		Long count = matchDao.getIsMatchCaptain(matchId, userId);
		if(count >0){
			return true;
		}
		//如果不是赛长，是否是本组参赛球友
		count = matchDao.getMeCanScore(matchId, groupId, userId);
		if(count >0){
			return true;
		}
		//如果不是本组参赛球友，查询是否是被邀请给该组记分
		MatchScoreUserMapping matchScoreUserMapping = matchDao.getMatchScoreUserMapping(matchId, groupId, userId);
		if(matchScoreUserMapping != null){
			return true;
		}
		return false;
	}

	/**
	 * 获取单练的groupId
	 * @return
	 */
	public Long getSingleMatchGroupIdByMatchId(Long matchId) {
		return matchDao.getSingleMatchGroupIdByMatchId(matchId);
	}

	/**
	 * 删除比赛分组
	 * @return
	 */
	public void delMatchGroupByGroupId(Long groupId) {
		matchDao.del(MatchGroup.class, groupId);
	}

	/**
	 * 比赛详情——赛长——获取备选球友(除去已经报名、参赛的)，赛长所在队的球友或者其搜索的结果
	 * @return
	 */
	public Map<String,Object> getMyTeamUserList(Long matchId,Long groupId, String keyword, String openid) {
		Map<String,Object> result = new HashMap<>();
		Long captainUserId = userService.getUserIdByOpenid(openid);
		//查询赛长代表哪个球队比赛
		Long teamId = matchDao.getTeamIdByMatchIdAndUserId(matchId,captainUserId);
		result.put("myTeamId",teamId);
		List<Map<String,Object>> userList = null;
		if(teamId != null){
			//赛长所在球队 已经报名/参赛的球友
			List<Long> userIdList = matchDao.getApplyUserIdList(matchId,teamId);
			//获取与赛长同队的球友，去除已经报名、参赛的同队球友
			userList = matchDao.getUserListByTeamId(teamId, keyword, userIdList);
		}
		result.put("userList",userList);
		return result;
	}

	/**
	 * 创建比赛——获取赛长用户所在球队，是否同时是参赛球队的队长 如果是让用户选择一个做代表队
	 * 报名比赛 也调用次方法——获取报名的球友所在球队
	 * @param joinTeamIds:创建比赛时选择的参赛球队
	 * 选了参赛球队，才会进此方法
	 * @return
	 */
	public List<TeamInfo> getCaptainTeamIdList(String joinTeamIds,String openid) {
		Long userId = userService.getUserIdByOpenid(openid);
		//参赛球队
		List<Long> teamIds = getLongTeamIdList(joinTeamIds);
		//我加入的球队
		List<Long> myTeamIdList = matchDao.getMyJoinTeamList(userId);
		List<Long> idList = new ArrayList<>();
		for(Long joinTid:teamIds){
			for(Long myJoinTid:myTeamIdList){
				if(joinTid.equals(myJoinTid)){
					idList.add(myJoinTid);
				}
			}
		}
		if(idList.size() >0){
			//获取这些球队的id和球队名称
			return matchDao.getTeamListByIds(idList);
		}
		return null;
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
					Long uid = Long.parseLong(userId);
					MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId, groupId, uid);
					if(matchUserGroupMapping != null){
						//将该球友的状态改为已删除
						matchUserGroupMapping.setMugmIsDel(1);
						matchDao.update(matchUserGroupMapping);
					}
				}
			}
		}
	}


	/**
	 * 比赛—球友报名—普通用户选一个组报名——获取球友所在球队
	 *  获取用户是否在参赛球队中，如果是多队比赛，并且同时都在参赛队中，让用户选择一个做代表队
	 *  如果是队式比赛，一个人报名的时候如果还不是任何一个参赛队成员，就让他先选一个，但只是在参赛队里选。
	 *        （这种情况只会有人把比赛报名链接分享到微信群后，通过微信群进入才会有，在小程序里，一个人应该看不到他不在任何一个参赛队中的比赛报名）
	 * @return
	 */
	public List<TeamInfo> getMyJoinTeamList(Long matchId, String openid) {
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		//比赛参赛球队
		String joinTeamIds = matchInfo.getMiJoinTeamIds();
		return getCaptainTeamIdList(joinTeamIds,openid);
	}

	/**
	 * 比赛——报名——查询是否已经报名
	 * @return
	 */
	public boolean checkIsApplyMatch(Long matchId, String openid) {
		Long userId = userService.getUserIdByOpenid(openid);
		//是否已经报名
		Long countApply = matchDao.getIsApply(userId,matchId);
		if(countApply >0){
			return true;
		}
		return false;
	}

    /**
     * 比赛——退出观战
     * @return
     */
    public void delWatchMatch(Long matchId, String openid) {
        MatchJoinWatchInfo matchJoinWatchInfo = matchDao.getMatchWatchInfo(matchId,userService.getUserIdByOpenid(openid));
        if(matchJoinWatchInfo != null){
            matchDao.del(matchJoinWatchInfo);
        }
    }

	/**
	 * 点击围观用户并邀请其记分
	 * @return
	 */
	public void saveInvitationScore(Long matchId, Long otherUserId, String openid) {
		Long myUserId = userService.getUserIdByOpenid(openid);
		//查询我所在的比赛分组
		MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId,null,myUserId);
		//查询该用户是否已经被邀请记分
		MatchScoreUserMapping matchScoreUserMapping = matchDao.getMatchScoreUserMapping(matchUserGroupMapping.getMugmMatchId()
																	, matchUserGroupMapping.getMugmGroupId(), otherUserId);
		if(matchScoreUserMapping == null){
			matchScoreUserMapping = new MatchScoreUserMapping();
			matchScoreUserMapping.setMsumMatchId(matchId);
			matchScoreUserMapping.setMsumGroupId(matchUserGroupMapping.getMugmGroupId());
			matchScoreUserMapping.setMsumMatchUserId(myUserId);
			matchScoreUserMapping.setMsumScoreUserId(otherUserId);
			matchScoreUserMapping.setMsumCreateTime(System.currentTimeMillis());
			matchDao.save(matchScoreUserMapping);
		}
	}


	public List<Long> getLongIdListReplace(String idStr) {
		if(StringUtils.isNotEmpty(idStr) && !"undefined".equals(idStr) && !"[]".equals(idStr)){
			idStr = idStr.replace("[","");
			idStr = idStr.replace("]","");
			idStr = idStr.replace("\"","");
			List<Long> idList = getLongTeamIdList(idStr);
			return idList;
		}
		return new ArrayList<>();
	}
}
