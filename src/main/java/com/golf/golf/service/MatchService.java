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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
			Collections.sort(pageInfo.getItems(), new Comparator<Map<String, Object>>() {
				@Override
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
			//正在报名的比赛(全部正在报名的比赛)
			pageInfo = matchDao.getCanJoinMatchList(searchBean, pageInfo);
			if(pageInfo.getItems() != null && pageInfo.getItems().size()>0){
				//就判断这个人在不在参赛球队的名单中，在就符合要求，不在的不要。如果没参赛球队的也要。可报名的比赛应该不多，可以先不管地理位置远近了。
				checkIsInJoinTeam(pageInfo,(Long)searchBean.getParps().get("userId"));
			}
		}else if(type == 3){
			//已报名的比赛（包括我参加的和我创建的报名中的比赛）
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
		List<Map<String,Object>> groupList_ = matchDao.getMatchGroupList(matchId,matchInfo.getMiIsEnd());
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
		//报名人数（报名页的报名人数去掉待分组的）
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
		UserInfo myUserInfo = userService.getUserByOpenId(openid);
		String myUserName = StringUtils.isNotEmpty(myUserInfo.getUiRealName())?myUserInfo.getUiRealName():myUserInfo.getUiNickName();
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
			matchInfoDb.setMiUpdateUserId(myUserInfo.getUiId());
			matchInfoDb.setMiUpdateUserName(myUserName);
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
							bean.setMugmIsDel(1);
							bean.setMugmUpdateUserId(myUserInfo.getUiId());
							bean.setMugmUpdateUserName(myUserName);
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
				MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchInfoDb.getMiId(),null,myUserInfo.getUiId());
				if(StringUtils.isNotEmpty(oldJoinTeamIds)){
					//删除比赛mapping表的本比赛所有参赛报名用户 除了自己
					oldJoinTeamIdList = getLongTeamIdList(oldJoinTeamIds);
					matchDao.delMatchUserMappingByTeamId(matchInfoDb.getMiId(), oldJoinTeamIdList, myUserInfo.getUiId());
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
							if (captainUserId.equals(myUserInfo.getUiId())) {
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
							mugm.setMugmCreateUserId(myUserInfo.getUiId());
							mugm.setMugmCreateUserName(myUserName);
							mugm.setMugmCreateTime(System.currentTimeMillis());
							matchDao.save(mugm);
						}
					}
				}
			}
			//更新我的比赛用户配置
			MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchInfoDb.getMiId(),null,myUserInfo.getUiId());
			if(StringUtils.isNotEmpty(chooseTeamId) && !chooseTeamId.equals("0") && !chooseTeamId.equals("null")
					&& !chooseTeamId.equals("undefined") && !chooseTeamId.equals(matchUserGroupMapping.getMugmTeamId())){
				matchUserGroupMapping.setMugmTeamId(Long.parseLong(chooseTeamId));
				matchUserGroupMapping.setMugmUpdateTime(System.currentTimeMillis());
				matchUserGroupMapping.setMugmUpdateUserName(myUserName);
				matchUserGroupMapping.setMugmUpdateUserId(myUserInfo.getUiId());
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
			matchInfo.setMiCreateUserId(myUserInfo.getUiId());
			matchInfo.setMiCreateUserName(myUserInfo.getUiRealName());
			matchInfo.setMiIsValid(1);
			//0：报名中  1进行中  2结束
			matchInfo.setMiIsEnd(0);
			matchDao.save(matchInfo);

			//创建比赛分组
			MatchGroup matchGroup = new MatchGroup();
			matchGroup.setMgMatchId(matchInfo.getMiId());
			matchGroup.setMgGroupName("第1组");
			matchGroup.setMgCreateTime(System.currentTimeMillis());
			matchGroup.setMgCreateUserId(myUserInfo.getUiId());
			matchGroup.setMgCreateUserName(myUserInfo.getUiRealName());
			matchDao.save(matchGroup);

			//创建用户分组mapping 创建人自动成为赛长
			MatchUserGroupMapping matchUserGroupMapping = new MatchUserGroupMapping();

			matchUserGroupMapping.setMugmMatchId(matchInfo.getMiId());
			if(StringUtils.isNotEmpty(chooseTeamId) && !chooseTeamId.equals("0") && !chooseTeamId.equals("null") && !chooseTeamId.equals("undefined")){
				matchUserGroupMapping.setMugmTeamId(Long.parseLong(chooseTeamId));
			}
			matchUserGroupMapping.setMugmUserType(0);
			matchUserGroupMapping.setMugmIsDel(0);
			matchUserGroupMapping.setMugmGroupId(matchGroup.getMgId());
			matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
			matchUserGroupMapping.setMugmUserId(myUserInfo.getUiId());
			matchUserGroupMapping.setMugmUserName(myUserName);
			matchUserGroupMapping.setMugmCreateUserId(myUserInfo.getUiId());
			matchUserGroupMapping.setMugmCreateUserName(myUserName);
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
						if (captainUserId.equals(myUserInfo.getUiId())) {
							continue;
						}
						MatchUserGroupMapping mugm = new MatchUserGroupMapping();
						mugm.setMugmMatchId(matchInfo.getMiId());
						mugm.setMugmTeamId((Long) map.get("teamId"));
						mugm.setMugmUserType(0);
                        //创建人默认在第一组，其他赛长先不放入待选球友中
                        mugm.setMugmIsDel(1);
						mugm.setMugmGroupId(matchGroup.getMgId());
						mugm.setMugmGroupName(matchGroup.getMgGroupName());
						mugm.setMugmUserId(captainUserId);
						UserInfo userInfo_ = matchDao.get(UserInfo.class, captainUserId);
						mugm.setMugmUserName(StringUtils.isNotEmpty(userInfo_.getUiRealName())?userInfo_.getUiRealName():userInfo_.getUiNickName());
						mugm.setMugmCreateUserId(myUserInfo.getUiId());
						mugm.setMugmCreateUserName(myUserName);
						mugm.setMugmCreateTime(System.currentTimeMillis());
						matchDao.save(mugm);
					}
				}
			}
			return matchInfo;
		}
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
		matchUserGroupMapping.setMugmIsDel(0);
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
			otherPeople.setMugmIsDel(0);
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
		//获取待分组人员
		List<Map<String, Object>> applyUserList = matchDao.getUserListByGroupId(matchId, keyword,null,1);
		result.put("applyUserList", applyUserList);
		return result;
	}

	/**
	 * 比赛详情——赛长删除本组用户——获取本组用户 不包括已报名的
	 *
	 * @return
	 */
	public Map<String, Object> getUserListByGroupId(Long matchId, Long groupId) {
		Map<String, Object> result = new HashMap<>();
		//本组人数
		Long count = matchDao.getGroupUserCountById(matchId, groupId);
		result.put("userCount", count);
		List<Map<String, Object>> userList = matchDao.getUserListByGroupId(matchId,null,groupId,0);
		result.put("userList", userList);
		return result;
	}

	/**
	 * 比赛详情——赛长 添加用户至分组
	 * @param checkIds:选中的id（mappingid或者用户id）
	 * @param type：0 添加待分组球友mappingid  1：添加备选球友 用户id
	 * @return
	 */
	public void updateGroupUserByMatchIdGroupId(Long matchId, Long groupId, String checkIds, Integer type, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		MatchGroup matchGroup = matchDao.get(MatchGroup.class,groupId);
		Long time = System.currentTimeMillis();
		if(type == 0){
			//添加待分组球友 将该球友的状态改为加入
			if (StringUtils.isNotEmpty(checkIds)) {
				List<Long> mappingIdList = getLongIdListReplace(checkIds);
				for (Long mappingId : mappingIdList) {
					if (mappingId != null) {
						MatchUserGroupMapping matchUserGroupMapping = matchDao.get(MatchUserGroupMapping.class, mappingId);
						if(matchUserGroupMapping != null){
							matchUserGroupMapping.setMugmGroupId(groupId);
							matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
							matchUserGroupMapping.setMugmIsDel(0);
							matchUserGroupMapping.setMugmUpdateUserId(userInfo.getUiId());
							matchUserGroupMapping.setMugmUpdateUserName(StringUtils.isNotEmpty(userInfo.getUiRealName()) ? userInfo.getUiRealName() : userInfo.getUiNickName());
							matchUserGroupMapping.setMugmUpdateTime(time);
							matchDao.update(matchUserGroupMapping);
						}
					}
				}
			}
		}else{
			//添加备选球友 将用户加入比赛分组
			if(StringUtils.isNotEmpty(checkIds)){
				List<Long> userIdList = getLongIdListReplace(checkIds);
				for(Long userId : userIdList){
					if(userId != null){
						UserInfo ui = matchDao.get(UserInfo.class, userId);
						//赛长参赛代表队
						TeamUserMapping teamUserMapping = matchDao.getTeamUserMappingByUserId(userId);
						MatchUserGroupMapping matchUserGroupMapping = new MatchUserGroupMapping();
						matchUserGroupMapping.setMugmMatchId(matchId);
						if (teamUserMapping != null) {
							matchUserGroupMapping.setMugmTeamId(teamUserMapping.getTumTeamId());
						}
						matchUserGroupMapping.setMugmUserType(1);
						matchUserGroupMapping.setMugmIsDel(0);
						matchUserGroupMapping.setMugmGroupId(groupId);
						matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
						matchUserGroupMapping.setMugmUserId(userId);
						matchUserGroupMapping.setMugmUserName(StringUtils.isNotEmpty(ui.getUiRealName()) ? ui.getUiRealName() : ui.getUiNickName());
						matchUserGroupMapping.setMugmCreateUserId(userInfo.getUiId());
						matchUserGroupMapping.setMugmCreateUserName(StringUtils.isNotEmpty(userInfo.getUiRealName()) ? userInfo.getUiRealName() : userInfo.getUiNickName());
						matchUserGroupMapping.setMugmCreateTime(time);
						matchDao.save(matchUserGroupMapping);
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
		UserInfo myUserInfo = userService.getUserByOpenId(openid);
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId,groupId,userId);
		ParkPartition parkPartition = matchDao.get(ParkPartition.class,holeId);
		MatchGroup matchGroup = matchDao.get(MatchGroup.class, groupId);

		//该用户参赛代表队
		Long teamId = matchUserGroupMapping.getMugmTeamId();
		//参赛球队
		List<Long> joinTeamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
		//如果有上报球队，同时将比分记录到上报球队中
		List<Long> reportTeamIdList = getLongTeamIdList(matchInfo.getMiReportScoreTeamId());

		//单人赛
		if(matchInfo.getMiMatchFormat2() == 0){
			//查询是否有本洞的记分
			MatchScore scoreDb = matchDao.getMatchScoreByIds(userId,matchId,groupId,teamId,beforeAfter,
																parkPartition.getppName(),parkPartition.getPpHoleNum());
			if (scoreDb != null) {
				//更新记分 直接更新这个用户在本比赛本组本球洞的所有得分（数据库不发生变化）
//				updateAllScoreByUser(scoreDb,myUserInfo,beforeAfter,parkPartition);

				updateScoreByUserTeam(scoreDb,myUserInfo,parkPartition,isUp,rod,rodCha,pushRod);
				//更新在上报球队中的比分
				updateReportTeamScore(reportTeamIdList,scoreDb,myUserInfo,beforeAfter,parkPartition);
			}else{
				MatchScore score = saveScore(matchInfo,teamId,groupId,matchGroup.getMgGroupName(),
						userId,matchUserGroupMapping.getMugmUserName(),myUserInfo,parkPartition,
						beforeAfter,isUp,rod,rodCha,pushRod);
				//如果有上报球队，同时将比分记录到上报球队中
				saveReportTeamScore(reportTeamIdList,userId, score);
			}
		}else{
			//双人赛 队际赛 更新本组本球队的所有用户的记分
			if(joinTeamIdList != null && joinTeamIdList.size() >0){
				//获取本组本球队的用户list
				List<Map<String,Object>> userListByTeam = matchDao.getUserListByTeamId(matchId,teamId,groupId);
				if(userListByTeam != null && userListByTeam.size()>0){
					for(Map<String,Object> user:userListByTeam){
						Long userId_ = getLongValue(user,"uiId");
						UserInfo u = matchDao.get(UserInfo.class,userId_);
						String userName_ = u.getUiRealName();
						if(StringUtils.isEmpty(userName_)){
							userName_ = u.getUiNickName();
						}
						//查询是否有本洞的记分
						MatchScore scoreDb = matchDao.getMatchScoreByIds(userId_,matchId,groupId,teamId,beforeAfter,
															parkPartition.getppName(),parkPartition.getPpHoleNum());
						if(scoreDb != null){
							//更新记分
							updateScoreByUserTeam(scoreDb,myUserInfo,parkPartition,isUp,rod,rodCha,pushRod);
							//更新在上报球队中的比分
							updateReportTeamScore(reportTeamIdList,scoreDb,myUserInfo,beforeAfter,parkPartition);
						}else{
							//新增记分
							MatchScore score = saveScore(matchInfo,teamId,groupId, matchGroup.getMgGroupName(),
									userId_,userName_,myUserInfo,parkPartition,
									beforeAfter,isUp,rod,rodCha,pushRod);
							//如果有上报球队，同时将比分记录到上报球队中
							saveReportTeamScore(reportTeamIdList,userId_, score);
						}
					}
				}
			}else{
				//公开赛 没有上报球队
				if(StringUtils.isNotEmpty(userIds) && !"null".equals(userIds) && !"undefined".equals(userIds)){
					List<Long> userList = getLongTeamIdList(userIds);
					for(Long uId:userList){
						UserInfo u = matchDao.get(UserInfo.class,uId);
						String userName_ = u.getUiRealName();
						if(StringUtils.isEmpty(userName_)){
							userName_ = u.getUiNickName();
						}
						//查询是否有本洞的记分
						MatchScore scoreDb = matchDao.getMatchScoreByIds(uId,matchId,groupId,teamId,beforeAfter,
								parkPartition.getppName(),parkPartition.getPpHoleNum());
						if(scoreDb != null){
							//更新记分
							updateScoreByUserTeam(scoreDb,myUserInfo,parkPartition,isUp,rod,rodCha,pushRod);
						}else{
							//保存记分
							saveScore(matchInfo,teamId,groupId, matchGroup.getMgGroupName(),
									uId,userName_,myUserInfo,parkPartition,
									beforeAfter,isUp,rod,rodCha,pushRod);
						}
					}
				}
			}
		}
	}

	/**
	 * 单人赛——更新记分 直接更新这个用户在本比赛本组本球洞的所有得分
	 */
	private void updateAllScoreByUser(MatchScore scoreDb, UserInfo myUserInfo, Integer beforeAfter, ParkPartition parkPartition) {
		matchDao.updateScoreAndReportScore(scoreDb.getMsUserId(),myUserInfo,scoreDb,beforeAfter,parkPartition,null);
	}

	/**
	 * 更新双人比杆公开赛的成绩
	 */
	public void saveOrUpdateScoreDoubleRod(Long matchId, Long groupId, Long holeId, String isUp, Integer rod, String rodCha, Integer pushRod, Integer beforeAfter, String openid, String userIds) {
		UserInfo myUserInfo = userService.getUserByOpenId(openid);
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
						updateScoreByUserTeam(scoreDb,myUserInfo,parkPartition,isUp,rod,rodCha,pushRod);
					}else{
						//新增一条记分记录
						MatchGroup matchGroup = matchDao.get(MatchGroup.class, groupId);
						//保存记分
						saveScore(matchInfo, null, groupId, matchGroup.getMgGroupName(),
								uId, userName_, myUserInfo, parkPartition,
								beforeAfter, isUp, rod, rodCha, pushRod);
					}
				}
			}
		}
	}

	/**
	 * 更新在上报球队中的比分
	 * myUserInfo：操作者用户信息
	 */
    private void updateReportTeamScore(List<Long> reportTeamIdList, MatchScore scoreDb, UserInfo myUserInfo,Integer beforeAfter,
									   ParkPartition parkPartition) {
        if(reportTeamIdList != null && reportTeamIdList.size()>0){
            //进一步筛选用是否在这些上报球队中，在就查询球队的id
			reportTeamIdList = matchDao.getReportTeamIdListByUserId(scoreDb.getMsUserId(),reportTeamIdList);
			if(reportTeamIdList != null && reportTeamIdList.size() >0){
				matchDao.updateScoreAndReportScore(scoreDb.getMsUserId(),myUserInfo,scoreDb,beforeAfter,parkPartition,reportTeamIdList);
			}
        }
    }

	/**
	 * 将比分记录到上报球队中
	 */
    private void saveReportTeamScore(List<Long> reportTeamIdList, Long userId, MatchScore score) {
        //如果有上报球队，同时将比分记录到上报球队中
        if(reportTeamIdList != null && reportTeamIdList.size() >0){
			//进一步筛选用是否在这些上报球队中，在就查询球队的id
			reportTeamIdList = matchDao.getReportTeamIdListByUserId(userId,reportTeamIdList);
			if(reportTeamIdList != null && reportTeamIdList.size()>0){
				for(Long reportTeamId :reportTeamIdList){
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

	/**
	 * 同时更新记分卡的比赛得分和上报球队得分
	 * 更新球友记分卡的得分
	 */
    private MatchScore updateScoreByUserTeam(MatchScore scoreDb,UserInfo myUserInfo, ParkPartition parkPartition,
                                       String isUp, Integer rod,
                                       String rodCha, Integer pushRod) {
        scoreDb.setMsIsUp(isUp);
        Integer rodChaInt = 0;
        //杆差=杆数-本洞标准杆数
        if(rod != null){
            scoreDb.setMsRodNum(rod);
            scoreDb.setMsRodCha(rod - parkPartition.getPpHoleStandardRod());
        }else{
            if(rodCha.contains("+")){
				rodChaInt = Integer.parseInt(rodCha.substring(1));
                scoreDb.setMsRodCha(rodChaInt);
            }else{
				rodChaInt = Integer.parseInt(rodCha);
                scoreDb.setMsRodCha(rodChaInt);
            }
            //杆数=标准杆+杆差
            scoreDb.setMsRodNum(parkPartition.getPpHoleStandardRod()+rodChaInt);
        }
        scoreDb.setMsPushRodNum(pushRod);
        //计算得分结果
        getScore(scoreDb,parkPartition.getPpHoleStandardRod());
        scoreDb.setMsUpdateTime(System.currentTimeMillis());
        scoreDb.setMsUpdateUserId(myUserInfo.getUiId());
        scoreDb.setMsUpdateUserName(myUserInfo.getUiRealName());
        matchDao.update(scoreDb);
        return scoreDb;
    }

	/**
	 * 新增球友记分卡的得分
	 */
    private MatchScore saveScore(MatchInfo matchInfo, Long teamId, Long groupId, String groupName,
                                 Long userId, String userName, UserInfo myUserInfo,
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
        score.setMsCreateUserId(myUserInfo.getUiId());
        score.setMsCreateUserName(StringUtils.isNotEmpty(myUserInfo.getUiRealName())?myUserInfo.getUiRealName():myUserInfo.getUiNickName());
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
	 * 开始比赛 / 结束比赛——保存或更新比赛状态
	 * state   0：报名中  1进行中  2结束
	 * 1、如果是多队双人比赛，不管比杆比洞，每组，每个队不超过两人，也可以是一人，每组最多两个队。生成记分卡时，只有一个队的两个人才能放入一行。
	 * 2、对于单人比洞，每组只能两个人，如果又是队式的，则一组的两个人要是两个队的
	 * 3、单人比杆赛分组不用有任何限制。
	 * 4、一个队队内，及多个队之间没法进行比洞赛
	 * 比洞赛的输赢组已经在获取比洞赛记分卡的时候进行了更新
	 * @return
	 */
	public String updateMatchState(Long matchId, Integer state, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		String joinTeamIds = matchInfo.getMiJoinTeamIds();
		List<Long> joinTeamIdList = getLongTeamIdList(joinTeamIds);

		if(state == 1){
			//开始比赛
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
						//该组重复的用户个数
						List<Object[]> count = matchDao.getHavingCountUserId(matchId,groupId);
						if(count != null &&  count.size() > 0){
							return groupName+" 有重复球友，无法开始比赛。";
						}
					}
				}else if(matchInfo.getMiMatchFormat2() == 1){
					//1、如果是多队双人比赛，不管比杆比洞，每组最多两个队，每个队不超过两人，也可以是一人。生成记分卡时，只有一个队的两个人才能放入一行。
					if(joinTeamIdList.size() >= 2){
						//所有组球队个数
						for(Map<String,Object> teamCount :teamCountList){
							Long groupId = getLongValue(teamCount,"groupId");
							String groupName = getName(teamCount,"groupName");
							Integer tCount = getIntegerValue(teamCount,"count");
							if(tCount >2 || (matchInfo.getMiMatchFormat1() == 1 && tCount == 1)){
								//双人比洞赛，每组2个队（一个队或者多个队打不了比洞赛）
								return groupName+" 球队数不符合比赛要求，无法开始比赛。";
							}
							//获取本组每个球队的人数
							userCountListByTeam = matchDao.getUserCountByMatchId(matchId,groupId);
							for(Map<String,Object> userCount:userCountListByTeam){
								Integer c = getIntegerValue(userCount,"count");
								if(c >2 || (tCount == 1 && c<2)){
									return groupName+" 参赛人数不符合比赛要求，无法开始比赛。";
								}
							}

							//该组重复的用户个数
							List<Object[]> count = matchDao.getHavingCountUserId(matchId,groupId);
							if(count != null &&  count.size() > 0){
								return groupName+" 有重复球友，无法开始比赛。";
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
	 * 我作为一个上报球队队长，可能给不同的参赛球队分配不同的积分
	 * ***************拿每个参赛队和每个上报球队算交集，如果没有就忽略，如果有，就把交集的队员成绩交给上报球队并积分
	 * @param scoreType 积分规则 1：杆差倍数  2：赢球奖分,
	 * @param baseScore 基础分,
	 * @param rodScore  杆差倍数,
	 * @param winScore  赢球奖分
	 * @return
	 */
	public void submitScoreByTeamId(Long matchId, Long teamId, Integer scoreType, Integer baseScore,
									   Integer rodScore, Integer winScore, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		Long captainUserId = userInfo.getUiId();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		//我是否是选中的球队队长，页面上其实已经做过判断，为了保险，此处再校验一次
		Long isTeamCaptain = teamService.getIsCaptain(captainUserId, teamId);
		//参赛球队
		List<Long> joinTeamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
		//上报球队
		List<Long> reportTeamIdList = getLongTeamIdList(matchInfo.getMiReportScoreTeamId());

		if(isTeamCaptain >0){
			//更新配置 允许重复上报（update）
			saveOrUpdateConfig(matchId, teamId, baseScore, rodScore, winScore, userInfo);

			//是否是参赛球队还是上报球队，创建比赛时，参赛球队和上报球队不可能重复
			if(joinTeamIdList.contains(teamId)){
				//是参赛球队
				//计算球友积分（一场比赛对应一次积分）
				calculateScore(captainUserId, matchInfo,teamId, null, scoreType, baseScore, rodScore, winScore, 0);
			}else if(reportTeamIdList.contains(teamId)){
				//是上报球队
				//获取本球队上报的球友
				List<Long> userIdList = matchDao.getScoreUserList(matchId,teamId);
				//计算球友积分（一场比赛对应一次积分）
				calculateScore(captainUserId, matchInfo,teamId, userIdList, scoreType, baseScore, rodScore, winScore, 1);
			}
		}
	}

	/**
	 * 成绩上报——计算球友积分 teamId 参赛球队id,上报球队id
	 * （一场比赛对应一次积分）
	 * teamType：0：参赛队，1：上报队
	 */
	private void calculateScore(Long captainUserId, MatchInfo matchInfo, Long teamId, List<Long> userIdList, Integer scoreType, Integer baseScore,
								Integer rodScore, Integer winScore, Integer teamType) {
		Integer format = matchInfo.getMiMatchFormat1();
		boolean flag = false;
		if (format == 0) {
			//比杆赛
			//积分“杆差倍数”和“赢球奖分”只能二选  球友积分=基础积分+（110-球友比分）*杆差倍数
			//或者 球友积分=基础积分+赢球奖分/比赛排名
			flag = updatePointByRodScore(captainUserId, matchInfo.getMiId(), teamId, userIdList, scoreType, baseScore, rodScore, winScore, teamType);
		} else if (format == 1) {
			//比洞赛
			// 比洞赛积分，只计算“基础积分”和“赢球奖分”两项，其中输球的组赢球奖分为0，打平的为一半。
			// 计算公式为：球友积分=基础积分+赢球奖分
			flag = updatePointByHoleScore(captainUserId, matchInfo.getMiId(), teamId, baseScore, winScore, teamType);
		}
		if(flag){
			//将该组的得分成绩标为已确认
			matchDao.updateMatchScoreById(matchInfo.getMiId(),teamId,userIdList);
		}
	}

	/**
	 * 成绩上报——保存参赛球队 或 上报球队 的成绩提交的积分计算配置 允许重复上报（修改）
	 * 我作为一个上报球队队长，可能给不同的参赛球队分配不同的积分
	 * type:0 参赛队  1：上报队
	 */
	private void saveOrUpdateConfig(Long matchId,Long teamId,Integer baseScore, Integer rodScore,
										Integer winScore, UserInfo userInfo) {
		String userName = StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName();
		IntegralConfig config = matchDao.getSubmitScoreConfig(matchId,teamId,userInfo.getUiId());
		if(config == null){
			config = new IntegralConfig();
			config.setIcMatchId(matchId);
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
	 * @param teamType 0：参赛队，1：上报队
	 * 杆差倍数 ：球友积分=基础积分+（110-球友比分）*杆差倍数
	 * 赢球奖分 ：球友积分=基础积分+赢球奖分/比赛排名
	 *
	 */
	private boolean updatePointByRodScore(Long captainUserId, Long matchId, Long teamId, List<Long> userIdList,Integer scoreType, Integer baseScore,
									   Integer rodScore, Integer winScore, Integer teamType) {
		if (scoreType == 1) {
			//杆差倍数 球友积分=基础积分+（110-球友比分）*杆差倍数
			//获取该队伍的得分情况
			List<Map<String, Object>> matchScoreList = matchDao.getSumScoreListByMatchIdTeamId(matchId, teamId,userIdList, teamType);
			if (matchScoreList != null && matchScoreList.size() > 0) {
				for (Map<String, Object> scoreMap : matchScoreList) {
					//杆数
					Integer score = getIntegerValue(scoreMap, "sumRodNum");
					Long userId = getLongValue(scoreMap, "userId");
					Integer point = baseScore + (Const.DEFAULT_ROD_NUM - score) * rodScore;
					//更新该球友原先的积分情况  （一场比赛对应一次积分）
					updatePointByIds(matchId, teamId, userId, captainUserId, point, 0);
				}
				return true;
			}
		} else {
			//赢球奖分 球友积分=基础积分+赢球奖分/比赛排名
			//计算杆数的总排名
			List<Map<String, Object>> matchScoreList = matchDao.getRankingListByMatchId(matchId, teamId,userIdList, teamType);
			if (matchScoreList != null && matchScoreList.size() > 0) {
				for (int i=0;i<matchScoreList.size();i++) {
                    Map<String, Object> scoreMap = matchScoreList.get(i);
					Long teamIdByScore = getLongValue(scoreMap, "teamId");
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
					updatePointByIds(matchId, teamIdByScore, userId, captainUserId, point, 0);
				}
				return true;
			}
		}
		return false;
	}


	/**
	 * 撤销成绩上报
	 * @param matchId 比赛id,
	 * @param teamId 上报球队id,
	 * @return
	 */
	public boolean cancelScoreByTeamId(Long matchId, Long teamId, String openid) {
		Long captainUserId = userService.getUserIdByOpenid(openid);
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		//我是否是选中的球队队长，页面上其实已经做过判断，为了保险，此处再校验一次
		Long isTeamCaptain = teamService.getIsCaptain(captainUserId, teamId);
		if(isTeamCaptain >0){
			//查询是否有对该球队的积分配置
			IntegralConfig config = matchDao.getSubmitScoreConfig(matchId,teamId,captainUserId);
			if(config == null){
				return false;
			}
			//删除球队用户积分
			matchDao.delTeamUserPoint(matchInfo.getMiId(),teamId,captainUserId);
			//删除积分计算配置
			matchDao.del(config);
			List<Long> userIdList = matchDao.getScoreUserList(matchId,teamId);
			matchDao.cancelMatchScoreById(matchInfo.getMiId(),teamId,userIdList);
			return true;
		}
		return false;
	}


	/**
	 * 更新该球友原先的比赛积分情况  （一场比赛对应一次积分）
	 * 如果是上报球队队长，作为一个上报球队队长，可能给不同的参赛球队分配不同的积分
	 * type: 0:加积分   1：减积分
	 */
	private void updatePointByIds(Long matchId, Long teamId, Long userId, Long captainUserId, Integer point, Integer type) {
		UserInfo captainUserInfo = userService.getUserById(captainUserId);
		String captainName = StringUtils.isNotEmpty(captainUserInfo.getUiRealName())?captainUserInfo.getUiRealName():captainUserInfo.getUiNickName();
		TeamUserPoint teamUserPoint = matchDao.getTeamUserPoint(matchId, teamId, userId, captainUserId);
		if(type == 0){
			//加积分 或者 修改积分
			if(teamUserPoint == null){
				teamUserPoint = new TeamUserPoint();
				teamUserPoint.setTupMatchId(matchId);
				teamUserPoint.setTupTeamId(teamId);
				teamUserPoint.setTupUserId(userId);
				teamUserPoint.setTupMatchPoint(point);
				teamUserPoint.setTupCreateUserId(captainUserId);
				teamUserPoint.setTupCreateUserName(captainName);
				teamUserPoint.setTupCreateTime(System.currentTimeMillis());
				matchDao.save(teamUserPoint);
			}else{
				teamUserPoint.setTupMatchPoint(point);
				teamUserPoint.setTupUpdateUserId(captainUserId);
				teamUserPoint.setTupUpdateUserName(captainName);
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
	 * @param teamType 0：参赛队，1：上报队
	 */
	private boolean updatePointByHoleScore(Long captainUserId, Long matchId, Long teamId, Integer baseScore,
										   Integer winScore, Integer teamType) {
		List<MatchHoleResult> matchHoleResultList = null;
		if(teamType == 0){
			//选的是参赛队
			//获取本队的输球组和赢球组
			matchHoleResultList = matchDao.getMatchHoleWinOrLoseList(matchId,teamId);
			if(matchHoleResultList != null && matchHoleResultList.size()>0){
				//是参赛球队
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
							updatePointByIds(matchId, teamId, userId, captainUserId, point, 0);
						}
					}
				}
				return true;
			}
        }else{
			//选的是上报球队
			//获取本队的输球组和赢球组
			matchHoleResultList = matchDao.getMatchHoleWinOrLoseList(matchId,null);
			if(matchHoleResultList != null && matchHoleResultList.size()>0){
				for(MatchHoleResult matchHoleResult:matchHoleResultList){
					//获取该输赢组该球队的参赛人员 是否在上报球队中
					List<Map<String,Object>> userIdList = matchDao.getUserListByGroupIdInMatchScoreForReport(matchHoleResult.getMhrMatchId(),
							matchHoleResult.getMhrGroupId(),matchHoleResult.getMhrTeamId());
					if(userIdList != null && userIdList.size() >0){
						//如果存在，复制一份输赢数据给上报球队
						MatchHoleResult matchHoleResultForReport = new MatchHoleResult();
						BeanUtils.copyProperties(matchHoleResult,matchHoleResultForReport);
						//type=1 上报球队记分
						matchHoleResultForReport.setMhrTeamId(teamId);
						//查看是否有上报队输赢结果
						Long count = matchDao.getReportMatchHoleReslutCount(matchHoleResultForReport);
						if(count == 0){
							matchDao.save(matchHoleResultForReport);
						}

						Integer point = 0;
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
						//更新上报球队参赛用户的积分
						for(Map<String,Object> map:userIdList){
							Long userId = getLongValue(map,"userId");
							//更新该球友的积分情况
							updatePointByIds(matchId, teamId, userId, captainUserId, point, 0);
						}
					}
				}
				return true;
			}
		}
        return false;
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
		//用户名截串（太长显示不下的）
		subStringUserName(userList);
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
	 * 比赛——group——分队比分（不包括上报球队）  获取我在参赛队和上报队都是队长的球队
	 * @return
	 */
	public Map<String, Object> getMyTeamListByKeepScore(Long matchId, Long teamId, String openid) {
		Map<String, Object> result = new HashMap<>();
		Long userId = userService.getUserIdByOpenid(openid);
		//比赛信息
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		//成绩上报球队
		List<Long> reportTeamIdList = getLongTeamIdList(matchInfo.getMiReportScoreTeamId());

		//我是否是选中的参赛球队队长
		Long isTeamCaptain = teamService.getIsCaptain(userId, teamId);
		//筛选我是否是某个上报球队的队长
		if(reportTeamIdList != null && reportTeamIdList.size()>0){
			reportTeamIdList = matchDao.getMyTeamInfoList(reportTeamIdList,userId);
		}

		List<TeamInfo> teamList = new ArrayList<>();
		//1：我只是选中的参赛球队的队长，只给该参赛球队分配积分
		if(isTeamCaptain > 0 && (reportTeamIdList == null || reportTeamIdList.size() == 0)){
			TeamInfo teamInfo = matchDao.get(TeamInfo.class,teamId);
			teamList.add(teamInfo);
		}else if(isTeamCaptain <= 0 && reportTeamIdList != null && reportTeamIdList.size() > 0){
			//2：我只是某个上报球队队长,则计算两队交集并把交集队员提交上报球队并积分
			//如果我同时是多个上报球队的队长，让用户选择给哪个队积分
			teamList = matchDao.getTeamListByIds(reportTeamIdList);
		}else if(isTeamCaptain > 0 && reportTeamIdList != null && reportTeamIdList.size() > 0){
			//3：我既是选中的参赛队队长，又是上报队队长，则让他选择向哪个球队积分，选上报球队执行2，选参赛球队执行1。
			TeamInfo teamInfo = matchDao.get(TeamInfo.class,teamId);
			teamList = matchDao.getTeamListByIds(reportTeamIdList);
			teamList.add(teamInfo);
		}
		result.put("teamList", teamList);
		return result;
	}

	/**
	 * 比赛——group——分队比分（不包括上报球队）
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

		//显示的球队滚轮
		List<TeamInfo> teamList = new ArrayList<>();
		//参赛球队
		String joinTeamIds = matchInfo.getMiJoinTeamIds();
		List<Long> joinTeamIdList = getLongTeamIdList(joinTeamIds);
		if(joinTeamIdList != null && joinTeamIdList.size() >0){
			teamList = matchDao.getTeamListByIds(joinTeamIdList);
		}

		if (teamId == null) {
			//如果没选球队，默认显示第一个球队
			teamId = teamList.get(0).getTiId();
		}
		result.put("teamList", teamList);

		//比赛的本球队所有用户 和总杆数(首列显示)
		List<Map<String, Object>> userList = matchDao.getUserListById(matchId, null,teamId);

		//用户名截串（太长显示不下的）
		subStringUserName(userList);
		result.put("userList", userList);

		//本组用户每个洞得分情况
		createNewUserScoreList(userList,list,matchInfo);
		result.put("list", list);

		//是否是该球队队长
		Long isTeamCaptain = teamService.getIsCaptain(userId, teamId);
		result.put("isTeamCaptain", isTeamCaptain);

		//是否是上报球队队长 显示“球队积分确认按钮”
		List<Long> reportTeamIdList = getLongTeamIdList(matchInfo.getMiReportScoreTeamId());
		if(reportTeamIdList != null && reportTeamIdList.size() >0){
			Long isReportTeamCaptain = teamService.getIsReportTeamCaptain(userId, reportTeamIdList);
			result.put("isReportTeamCaptain", isReportTeamCaptain);
		}

		return result;
	}

	/**
	 * 用户名截串（太长显示不下的）
	 */
	private void subStringUserName(List<Map<String, Object>> userList) {
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
	}

	/**
	 * 获取用户在每个球洞的得分情况
	 */
	private void createNewUserScoreList(List<Map<String, Object>> userList, List<MatchGroupUserScoreBean> list,MatchInfo matchInfo) {
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
				List<Map<String, Object>> uScoreBeforeList = matchDao.getBeforeAfterScoreByUserId(uiId, matchInfo,0,teamId);
				createNewUserScore(userScoreList, uScoreBeforeList);
				//本用户后半场得分情况
				List<Map<String, Object>> uScoreAfterList = matchDao.getBeforeAfterScoreByUserId(uiId, matchInfo,1,teamId);
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
						@Override
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

	/**
	 * 取前N名 N的list
	 */
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
	 * 比赛——普通用户报名——可以换组
	 * @return
	 */
	public Integer applyMatch(Long matchId, Long groupId, String groupName, String chooseTeamId, String openid) {
		Integer flag = -1;
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		//参赛球队
		List<Long> teamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
		UserInfo userInfo = userService.getUserByOpenId(openid);
		String userName = StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName();
		Long userId = userInfo.getUiId();
		Long chooseTeam = null;
		//代表球队
		if(StringUtils.isNotEmpty(chooseTeamId) && !"null".equals(chooseTeamId) && !"undefined".equals(chooseTeamId)){
			chooseTeam = Long.parseLong(chooseTeamId);
			//我是否在本球队中，没有就加一条入队申请
			Long count = teamDao.getMeIsInTeam(userId,chooseTeam);
			if(count <= 0){
				TeamUserMapping teamUserMapping = new TeamUserMapping();
				teamUserMapping.setTumTeamId(chooseTeam);
				teamUserMapping.setTumUserId(userId);
				teamUserMapping.setTumUserType(2);
				teamUserMapping.setTumCreateUserId(userId);
				teamUserMapping.setTumCreateTime(System.currentTimeMillis());
				teamUserMapping.setTumCreateUserName(userName);
				matchDao.save(teamUserMapping);
			}
		}

		//查询是否已经报名 isDel=0
		MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId,null,userId);
		if(matchUserGroupMapping == null){
			matchUserGroupMapping = new MatchUserGroupMapping();
			matchUserGroupMapping.setMugmMatchId(matchId);
			matchUserGroupMapping.setMugmGroupId(groupId);
			matchUserGroupMapping.setMugmGroupName(groupName);
			matchUserGroupMapping.setMugmUserId(userId);
			matchUserGroupMapping.setMugmUserName(userName);
			matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
			matchUserGroupMapping.setMugmCreateUserId(userId);
			matchUserGroupMapping.setMugmCreateUserName(userName);
			matchUserGroupMapping.setMugmUserType(1);
			matchUserGroupMapping.setMugmIsDel(0);
			if(teamIdList != null && teamIdList.size()>0 && chooseTeam != null){
				//是队际赛 并且选择了参赛球队  如果是公开赛，不用保存参赛球队
				matchUserGroupMapping.setMugmTeamId(chooseTeam);
			}
			matchDao.save(matchUserGroupMapping);
			flag = 0;
		}else{
			//在临时分组，修改其状态，从临时分组改为加入到报名组
			matchUserGroupMapping.setMugmGroupId(groupId);
			matchUserGroupMapping.setMugmGroupName(groupName);
			matchUserGroupMapping.setMugmIsDel(0);
			matchUserGroupMapping.setMugmUpdateTime(System.currentTimeMillis());
			matchUserGroupMapping.setMugmUpdateUserId(userId);
			matchUserGroupMapping.setMugmUpdateUserName(userName);
			matchDao.update(matchUserGroupMapping);
			flag = 0;
		}
		return flag;
	}

	/**
	 * 比赛——普通用户从一个组退出比赛
	 * 直接删除
	 * @return
	 */
	public void quitMatch(Long matchId, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		Long userId = userInfo.getUiId();
		List<MatchUserGroupMapping> list = matchDao.getIsInMatchGroupMappingByUserId(matchId,userId);
		if(list != null && list.size()>0){
			for(MatchUserGroupMapping bean :list){
				matchDao.del(bean);
			}
		}
	}

	private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
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
		String jsonParp = gson.toJson(parp);
		String scene = URLEncoder.encode(jsonParp,"utf-8");
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
		matchUserGroupMapping.setMugmIsDel(0);
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
	public Map<String,Object> getMyTeamUserList(Long matchId,String keyword, String openid) {
		Map<String,Object> result = new HashMap<>();
		Long captainUserId = userService.getUserIdByOpenid(openid);
		//查询赛长代表哪个球队比赛
		Long teamId = matchDao.getTeamIdByMatchIdAndUserId(matchId,captainUserId);
		result.put("myTeamId",teamId);
		List<Map<String,Object>> userList = null;
		if(teamId != null){
			//赛长所在球队 已经报名/参赛的球友 包括待分组的
			List<Long> userIdList = matchDao.getApplyUserIdList(matchId,teamId);
			if(userIdList != null && userIdList.size()>0){
				//获取与赛长同队的球友，去除已经报名/参赛的球友
				userList = matchDao.getUserListByTeamId(teamId, keyword, userIdList);
			}
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
		List<Long> joinTeamIdList = getLongTeamIdList(joinTeamIds);
		if(joinTeamIdList == null || joinTeamIdList.size() == 0){
			return new ArrayList<>();
		}

		//我是否在参赛队中(申请入队的也算)
		List<Long> myJoinTeamIdList = matchDao.getIsJoinTeam(userId,joinTeamIdList);
		if(myJoinTeamIdList == null || myJoinTeamIdList.size() == 0){
			//我不在参赛队中，
			return matchDao.getTeamListByIds(joinTeamIdList);
		}
		return matchDao.getTeamListByIds(myJoinTeamIdList);
	}


	/**
	 * 比赛详情——保存——将用户从该分组删除
	 *
	 * @return
	 */
	public void delUserByMatchIdGroupId(String mappingIds) {
		if (StringUtils.isNotEmpty(mappingIds)) {
			List<Long> mappingIdList = getLongIdListReplace(mappingIds);
			for (Long mappingId : mappingIdList) {
				if (mappingId != null) {
					MatchUserGroupMapping matchUserGroupMapping = matchDao.get(MatchUserGroupMapping.class, mappingId);
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
		Long userId = userService.getUserIdByOpenid(openid);
		//用户换组 是队际赛，先判断是否已经选过代表球队
		List<Long> joinTeamIdList = getLongTeamIdList(joinTeamIds);
		if(joinTeamIdList != null && joinTeamIdList.size() > 0){
			MatchUserGroupMapping matchUserGroupMapping = matchDao.getIsInMatchUserMapping(matchId,userId);
			if(matchUserGroupMapping != null && matchUserGroupMapping.getMugmTeamId() != null){
				List<TeamInfo> list = new ArrayList<>();
				TeamInfo t = matchDao.get(TeamInfo.class, matchUserGroupMapping.getMugmTeamId());
				list.add(t);
				return  list;
			}
		}
		return getCaptainTeamIdList(joinTeamIds,openid);
	}

	/**
	 * 比赛——报名——普通用户——查询自己是否已经报名
	 * @return
	 */
	public boolean checkIsApplyMatch(Long matchId,Long groupId, String openid) {
		Long userId = userService.getUserIdByOpenid(openid);
		Long countApply = matchDao.getIsApplyNormalUser(userId,matchId,groupId);
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
