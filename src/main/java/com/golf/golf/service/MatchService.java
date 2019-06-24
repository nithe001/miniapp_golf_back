package com.golf.golf.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.MapUtil;
import com.golf.common.util.PropertyConst;
import com.golf.common.util.TimeUtil;
import com.golf.golf.bean.MatchGroupBean;
import com.golf.golf.bean.MatchGroupUserScoreBean;
import com.golf.golf.bean.MatchTotalUserScoreBean;
import com.golf.golf.dao.MatchDao;
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
			//可报名的比赛 先获取正在报名中的并且我不在比赛用户配置中的比赛 按距离排序
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
			if(StringUtils.isNotEmpty(joinTeamIds)){
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
				matchGroupBean.setUserInfoList(groupUserList);
				groupList.add(matchGroupBean);
			}
		}

		//是否是赛长
		Long captainCount = matchDao.getIsCaptain(matchId, userInfo.getUiId());
		Boolean isMatchCaptain = false;
		if (captainCount > 0) {
			isMatchCaptain = true;
		}

		result.put("matchInfo", matchInfo);
		result.put("watchList", watchList);
		result.put("captainList", captainList);
		result.put("groupList", groupList);
		result.put("isMatchCaptain", isMatchCaptain);
		return result;
	}

	/**
	 * 当前登录用户是否是赛长
	 */
	public boolean getIsCaptain(Long matchId, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		Long captainCount = matchDao.getIsCaptain(matchId, userInfo.getUiId());
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
	public Integer getIntegerDoubleValue(Map<String, Object> map, String key) {
		if (map == null || map.get(key) == null) {
			return 0;
		} else {
			Double d = (Double)map.get(key);
			return (int)Math.floor(d);
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
		String teamIds = matchInfo.getMiJoinTeamIds();
		if (StringUtils.isNotEmpty(teamIds)) {
			List<Long> teamIdList = getLongTeamIdList(teamIds);
			List<TeamInfo> teamList = matchDao.getTeamListByIds(teamIdList);
			String joinTeamNames = "";
			if (teamList != null && teamList.size() > 0) {
				for (TeamInfo teamInfo : teamList) {
					teamInfo.setTiLogo(PropertyConst.DOMAIN + teamInfo.getTiLogo());
					joinTeamNames += ","+teamInfo.getTiName();
				}
			}
			result.put("teamList", teamList);
			result.put("joinTeamNames", joinTeamNames);
			result.put("joinTeamIds", teamIds);
		}
		//获取成绩上报球队信息
		if (StringUtils.isNotEmpty(matchInfo.getMiReportScoreTeamId()) && !matchInfo.getMiReportScoreTeamId().equals("undefined")) {
			List<Long> reportScoreTeamIdList = getLongTeamIdList(matchInfo.getMiReportScoreTeamId());
			List<TeamInfo> reportScoreTeamList = matchDao.getTeamListByIds(reportScoreTeamIdList);
			/*if(reportScoreTeamIdList != null && reportScoreTeamIdList.size()>0){
				for(TeamInfo teamInfo:reportScoreTeamList){
					teamInfo.setTiLogo(PropertyConst.DOMAIN + teamInfo.getTiLogo());
				}
			}*/
			result.put("reportScoreTeamList", reportScoreTeamList);
		}
		//获取我的参赛代表球队
		MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId, null, userInfo.getUiId());
		result.put("myChooseTeamId", matchUserGroupMapping.getMugmTeamId());
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
	 * 创建比赛 更新比赛-保存-自动成为赛长
	 * 报名期间都可以改，但比赛开始后，就只能改 观战范围，成绩上报 和 比赛说明。
	 * @return
	 */
	public void saveMatchInfo(MatchInfo matchInfo, String parkName, Long chooseTeamId, String openid) {
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

			if(matchInfoDb.getMiIsEnd() == 0){
				matchInfoDb.setMiTitle(matchInfo.getMiTitle());
				if (!matchInfo.getMiLogo().contains(matchInfoDb.getMiLogo())) {
					matchInfoDb.setMiLogo(matchInfo.getMiLogo());
				}
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
			if (!oldJoinTeamIds.equals(newJoinTeamIds)) {
				//找出新老参赛队不同的teamId
				List<Long> differentTeamIdList = getDifferentTeamIds(oldJoinTeamIds,newJoinTeamIds);
				if(differentTeamIdList != null && differentTeamIdList.size() >0 ){
					//删除比赛mapping表该球队的信息 已报名的球友和已参赛的球友 除了自己
					matchDao.delMatchUserMappingByTeamId(matchInfoDb.getMiId(),differentTeamIdList,userInfo.getUiId());
				}
			}
			//更新我的比赛用户配置
			MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchInfoDb.getMiId(),null,userInfo.getUiId());
			if(!matchUserGroupMapping.getMugmTeamId().equals(chooseTeamId)){
				matchUserGroupMapping.setMugmTeamId(chooseTeamId);
				matchUserGroupMapping.setMugmUpdateTime(System.currentTimeMillis());
				matchUserGroupMapping.setMugmUpdateUserName(userInfo.getUiRealName());
				matchUserGroupMapping.setMugmUpdateUserId(userInfo.getUiId());
				matchDao.update(matchUserGroupMapping);
			}
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
			matchUserGroupMapping.setMugmMatchId(matchInfo.getMiId());
			if(chooseTeamId != null && chooseTeamId != 0L){
				matchUserGroupMapping.setMugmTeamId(chooseTeamId);
			}
			matchUserGroupMapping.setMugmUserType(0);
			matchUserGroupMapping.setMugmGroupId(matchGroup.getMgId());
			matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
			matchUserGroupMapping.setMugmUserId(userInfo.getUiId());
			matchUserGroupMapping.setMugmUserName(userInfo.getUiRealName());
			matchUserGroupMapping.setMugmCreateUserId(userInfo.getUiId());
			matchUserGroupMapping.setMugmCreateUserName(userInfo.getUiRealName());
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
						mugm.setMugmGroupId(matchGroup.getMgId());
						mugm.setMugmGroupName(matchGroup.getMgGroupName());
						mugm.setMugmUserId(captainUserId);
						UserInfo userInfo_ = matchDao.get(UserInfo.class, captainUserId);
						mugm.setMugmUserName(userInfo_.getUiRealName());
						mugm.setMugmCreateUserId(userInfo_.getUiId());
						mugm.setMugmCreateUserName(userInfo_.getUiRealName());
						mugm.setMugmCreateTime(System.currentTimeMillis());
						matchDao.save(mugm);
					}
				}
			}
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
		matchUserGroupMapping.setMugmUserName(userInfo.getUiRealName());
		matchUserGroupMapping.setMugmCreateUserId(userInfo.getUiId());
		matchUserGroupMapping.setMugmCreateUserName(userInfo.getUiRealName());
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
			otherPeople.setMugmCreateUserName(userInfo.getUiRealName());
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
	public void addGroupByTeamId(Long matchId, String openid) {
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
		group.setMgCreateUserName(userInfo.getUiRealName());
		group.setMgCreateTime(System.currentTimeMillis());
		matchDao.save(group);
	}

	/**
	 * 比赛详情——赛长获取已经报名的用户
	 *
	 * @return
	 */
	public Map<String, Object> getApplyUserByMatchId(Long matchId,String keyword, Long groupId) {
		Map<String, Object> result = new HashMap<>();
		//本组人数
		Long count = matchDao.getGroupUserCountById(matchId, groupId);
		result.put("userCount", count);
		List<Map<String, Object>> applyUserList = matchDao.getApplyUserByMatchId(matchId, keyword,groupId);
		result.put("applyUserList", applyUserList);
		return result;
	}

	/**
	 * 比赛详情——赛长获取本组用户
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
		//获取用户所在球队的简称
		if(userList != null && userList.size()>0){
			for(Map<String, Object> parp :userList){
				Long userId = (Long)parp.get("uiId");
				String teamAbbrev = matchDao.getTeamAbbrevByUserId(userId);
				if(StringUtils.isNotEmpty(teamAbbrev)){
					parp.put("teamAbbrev",teamAbbrev);
				}else{
					parp.put("teamAbbrev","暂无球队");
				}
			}
		}
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
					MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId, groupId, Long.parseLong(uid));
					matchUserGroupMapping.setMugmUserType(1);
					matchUserGroupMapping.setMugmUpdateUserId(captainUserId);
					matchUserGroupMapping.setMugmUpdateUserName(StringUtils.isNotEmpty(userInfo.getUiRealName())?userInfo.getUiRealName():userInfo.getUiNickName());
					matchUserGroupMapping.setMugmUpdateTime(updateTime);
					matchDao.update(matchUserGroupMapping);
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
//			userList = matchDao.getUserListById(matchId, groupId,null,0);
			userList = matchDao.getUserListByScoreCard(matchId, groupId,null);
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
				Long teamId = getLongValue(user, "team_id");
				MatchGroupUserScoreBean bean = new MatchGroupUserScoreBean();
				bean.setUserId(uiId);
				bean.setUserName(getName(user, "uiRealName"));
				//本用户得分情况
				if (uiId != null) {
					List<Map<String, Object>> uscoreList = matchDao.getScoreByUserId(groupId, uiId, matchInfo, teamId);
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
	 * 与标准杆一样 叫平标准杆
	 * 比标准杆少一杆叫小鸟
	 * 比标准杆多一杆或者标准杆完成该洞叫Par
	 * 低于标准杆2杆完成该洞叫老鹰
	 * @return
	 */
	public void saveOrUpdateScore(Long userId, String userName, Long matchId, Long groupId, Long scoreId, String holeName,
								  Integer holeNum, Integer holeStandardRod, String isUp, Integer rod, String rodCha,
								  Integer pushRod, Integer beforeAfter, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId,groupId,userId);
		//用户参赛代表队
		Long teamId = matchUserGroupMapping.getMugmTeamId();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		//如果有上报球队，同时将比分记录到上报球队中
		String reportTeamIds = matchInfo.getMiReportScoreTeamId();
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
			//计算得分结果
			getScore(scoreDb,holeStandardRod);
			scoreDb.setMsUpdateTime(System.currentTimeMillis());
			scoreDb.setMsUpdateUserId(userInfo.getUiId());
			scoreDb.setMsUpdateUserName(userInfo.getUiRealName());
			matchDao.update(scoreDb);
			//如果有上报球队，同时将比分记录到上报球队中
			if(StringUtils.isNotEmpty(reportTeamIds)){
				List<Long> reportTeamIdList = getLongTeamIdList(reportTeamIds);
				for(Long reportTeamId :reportTeamIdList){
					//获取上报球队记分详情
					MatchScore scoreByReportTeam = matchDao.getScoreByReportTeam(reportTeamId,matchId,groupId,userId,
							scoreDb.getMsBeforeAfter(),scoreDb.getMsHoleName(),scoreDb.getMsHoleNum(),scoreDb.getMsHoleStandardRod());
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
		} else {
			MatchGroup matchGroup = matchDao.get(MatchGroup.class, groupId);
			MatchScore score = new MatchScore();
			//type=0 比赛球队记分
			score.setMsType(0);
			score.setMsTeamId(teamId);
			score.setMsMatchId(matchId);
			score.setMsMatchTitle(matchInfo.getMiTitle());
			score.setMsMatchType(matchInfo.getMiType());
			score.setMsGroupId(groupId);
			score.setMsGroupName(matchGroup.getMgGroupName());
			score.setMsUserId(userId);
			score.setMsHoleStandardRod(holeStandardRod);
            if(matchInfo.getMiType() == 1){
                score.setMsUserName(matchUserGroupMapping.getMugmUserName());
            }else{
                score.setMsUserName(userName);
            }
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

			//计算得分结果
			getScore(score,holeStandardRod);

			score.setMsCreateUserId(userInfo.getUiId());
			score.setMsCreateUserName(userInfo.getUiRealName());
			score.setMsCreateTime(System.currentTimeMillis());
			matchDao.save(score);
			//如果有上报球队，同时将比分记录到上报球队中
			if(StringUtils.isNotEmpty(reportTeamIds)){
				List<Long> reportTeamIdList = getLongTeamIdList(reportTeamIds);
				for(Long reportTeamId :reportTeamIdList){
					//如果上报球队和参赛球队一样，就不计了
					if(!reportTeamId.equals(teamId)){
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
	 *
	 * @return
	 */
	public String updateMatchState(Long matchId, Integer state, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		List<Map<String,Object>> countList = matchDao.getCountUserByMatchId(matchId);
		if(countList == null || countList.size() ==0){
            return "没有参赛队员无法开始比赛。";
        }
        if(matchInfo.getMiMatchFormat1() ==1 && matchInfo.getMiMatchFormat2() == 0){
            //单人比洞 必须每组2人
            for(Map<String,Object> map: countList){
                Long count = getLongValue(map,"count");
                if(count != 2){
                    String groupName = getName(map,"groupName");
                    return groupName+" 参赛人数不符合要求，无法开始比赛。";
                }
            }
        }else if(matchInfo.getMiMatchFormat1() == 0 && matchInfo.getMiMatchFormat2() == 1){
		    //双人比杆 每组4人
            for(Map<String,Object> map: countList){
                Long count = getLongValue(map,"count");
                if(count != 4 && count != 2){
                    String groupName = getName(map,"groupName");
                    return groupName+" 参赛人数不符合要求，无法开始比赛。";
                }
            }
        } if(matchInfo.getMiMatchFormat1() == 1 && matchInfo.getMiMatchFormat2() == 1){
            //双人比洞 每组4人
            for(Map<String,Object> map: countList){
                Long count = getLongValue(map,"count");
                if(count != 4 && count != 2){
                    String groupName = getName(map,"groupName");
                    return groupName+" 参赛人数不符合要求，无法开始比赛。";
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
	 * 成绩上报 杆数越少 代表成绩越好
	 * @param scoreType 积分规则 1：杆差倍数  2：赢球奖分,
	 * @param baseScore 基础分,
	 * @param rodScore  杆差倍数,
	 * @param winScore  赢球奖分
	 * @return
	 */
	public boolean submitScoreByTeamId(Long matchId, Long teamId, Integer scoreType, Integer baseScore,
									   Integer rodScore, Integer winScore, String openid) {
		//更新配置
		boolean flag = saveOrUpdateConfig(matchId, teamId, baseScore, rodScore, winScore, openid);
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
			//将该组的得分成绩标为已确认
			matchDao.updateMatchScoreById(matchId,teamId);
			return true;
		}
		return false;
	}

	//保存成绩提交的积分计算配置
	private boolean saveOrUpdateConfig(Long matchId, Long teamId,Integer baseScore, Integer rodScore, Integer winScore, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		IntegralConfig config = matchDao.getSubmitScoreConfig(matchId,teamId);
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
		if (scoreType == 1) {
			//杆差倍数 球友积分=基础积分+（144-球友比分）*杆差倍数
			//获取该队伍的得分情况
			List<Map<String, Object>> matchScoreList = matchDao.getSumScoreListByMatchIdTeamId(matchId, teamId);
			if (matchScoreList != null && matchScoreList.size() > 0) {
				for (Map<String, Object> scoreMap : matchScoreList) {
					//杆数
					Integer score = getIntegerValue(scoreMap, "sumRodNum");
					Long userId = getLongValue(scoreMap, "userId");
					Integer point = baseScore + (144 - score) * rodScore;
					//更新该球友原先的积分情况
					updatePointByIds(teamId, userId, point, 0);
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
						Integer rank = getIntegerDoubleValue(scoreMap,"rank");
						//计算得分
						Integer point = baseScore + winScore/rank;
						//本队球友
						//更新该球友原先的积分情况
						updatePointByIds(teamIdByScore, userId, point, 0);

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
	public boolean cancelScoreByTeamId(Long matchId, Long teamId) {
		//获取配置
		IntegralConfig config = matchDao.getSubmitScoreConfig(matchId,teamId);
		if(config == null){
			return false;
		}
		//基础积分
		Integer baseScore = config.getIcBaseScore();
		//杆差倍数
		Integer rodCha = config.getIcRodCha();
		//赢球奖分
		Integer winScore = config.getIcWinScore();
		//计算得分
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		Integer format = matchInfo.getMiMatchFormat1();
		if (format == 0) {
			//比杆赛 积分“杆差倍数”和“赢球奖分”只能二选  球友积分=基础积分+（144-球友比分）*杆差倍数 或者 球友积分=基础积分+赢球奖分/比赛排名
			if(rodCha != null){
				//获取该队伍的得分情况
				List<Map<String, Object>> teamScoreList = matchDao.getSumScoreListByMatchIdTeamId(matchId, teamId);
				if (teamScoreList != null && teamScoreList.size() > 0) {
					for (Map<String, Object> scoreMap : teamScoreList) {
						//杆数
						Integer score = getIntegerValue(scoreMap, "sumRodNum");
						Long userId = getLongValue(scoreMap, "userId");
						Integer point = baseScore + (144 - score) * rodCha;
						//更新该球友的积分情况
						updatePointByIds(teamId, userId, point, 1);
					}
				}
			}else if(winScore != null){
				//选择的赢球奖分 赢球奖分 球友积分=基础积分+赢球奖分/比赛排名
				//计算杆数的总排名
				List<Map<String, Object>> matchScoreList = matchDao.getRankingListByMatchId(matchId);
				if (matchScoreList != null && matchScoreList.size() > 0) {
					for (Map<String, Object> scoreMap : matchScoreList) {
						Long teamIdByScore = getLongValue(scoreMap, "teamId");
						if(teamId.equals(teamIdByScore)){
							Long userId = getLongValue(scoreMap, "userId");
							//排名
							Integer rank = getIntegerDoubleValue(scoreMap,"rank");
							//计算得分
							Integer point = baseScore + winScore/rank;
							//更新该球友原先的积分情况
							updatePointByIds(teamIdByScore, userId, point, 1);
						}
					}
				}
			}
		} else if (format == 1) {
			//比洞赛
			// 比洞赛积分，只计算“基础积分”和“赢球奖分”两项，其中输球的组赢球奖分为0，打平的为一半。
			// 计算公式为：球友积分=基础积分+赢球奖分
			//获取本次比赛中的分组和每组用户的总分
			List<Map<String,Object>> holeScoreList = matchDao.getMatchHoleScoreList(matchId);
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
					updatePointByIds(teamId, userForTeam, point, 1);
				}
			}
		}
		//删除积分计算配置
		matchDao.del(config);
		//将该组的得分成绩标为撤销
		matchDao.cancelMatchScoreById(matchId,teamId);
		return true;
	}


	/**
	 * 更新该球友原先的积分情况
	 * type: 0:加积分   1：减积分
	 */
	private void updatePointByIds(Long teamId, Long userId, Integer point, Integer type) {
		UserInfo userInfo = userService.getUserById(userId);
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
	}


	/**
	 * 成绩上报 比洞赛
	 * 积分只计算“基础积分”和“赢球奖分”两项，其中输球的组赢球奖分为0，打平的为一半。
	 * 计算公式为：球友积分=基础积分+赢球奖分
	 */
	private void updatePointByHoleScore(Long matchId, Long teamId, Integer baseScore, Integer winScore) {
		//获取本次比赛中的分组和每组用户的总分
		List<Map<String,Object>> holeScoreList = matchDao.getMatchHoleScoreList(matchId);
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
				updatePointByIds(teamId, userForTeam, point, 0);
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
		//观战范围：3、封闭：参赛队员可见）
		if(matchInfo.getMiMatchOpenType() == 3){
			return false;
		}
		Long userId = userInfo.getUiId();
		Long matchId = matchInfo.getMiId();
		//是否已经围观
		Long watchCount = matchDao.getIsWatch(userId,matchId);
		if(watchCount <=0){
			//是否是参赛人员
			Long count = matchDao.getIsContestants(userId, matchId);
			//没有参加比赛的
			if (count <= 0) {
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
		List<Map<String, Object>> userList = matchDao.getUserListById(matchId, null,null,0);
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
		thBean2.setUserName("总杆");
		thBean2.setUserScoreTotalList(parkHoleList);
		list.add(thBean2);

		//参赛球队
		String joinTeamIds = matchInfo.getMiJoinTeamIds();
		//成绩上报球队
		String reportTeamIds = matchInfo.getMiReportScoreTeamId();
		List<Long> reportTeamIdList = null;
		if(StringUtils.isNotEmpty(matchInfo.getMiJoinTeamIds())){
			List<Long> teamIds = getLongTeamIdList(joinTeamIds);
			if(StringUtils.isNotEmpty(reportTeamIds)){
				reportTeamIdList = getLongTeamIdList(reportTeamIds);
				teamIds.addAll(reportTeamIdList);
				//去重
				HashSet tids = new HashSet(teamIds);
				teamIds.clear();
				teamIds.addAll(tids);
			}
			//参赛球队+上报球队
			List<TeamInfo> teamList = matchDao.getTeamListByIds(teamIds);
			result.put("teamList", teamList);
			if (teamId == null) {
				//如果没选球队，默认显示第一个球队
				teamId = teamList.get(0).getTiId();
			}
		}


		//比赛的本球队所有用户(首列显示)
//		List<Map<String, Object>> userList = matchDao.getUserListByTeamId(matchId, teamId);
		Integer scoreType = 0;//默认查比赛记分
		if(reportTeamIdList != null && reportTeamIdList.size()>0 && reportTeamIdList.contains(teamId)){
			//选中的球队是上报球队,查上报给该球队的记分
			scoreType = 1;
		}
		List<Map<String, Object>> userList = matchDao.getUserListById(matchId, null,teamId,scoreType);
		result.put("userList", userList);

		//本组用户每个洞得分情况
		createNewUserScoreList(userList,list,matchInfo,scoreType);
		result.put("list", list);

		//是否是该球队队长
		Long isTeamCaptain = teamService.getIsCaptain(userService.getUserIdByOpenid(openid), teamId);
		result.put("isTeamCaptain", isTeamCaptain);
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
	 * 比杆赛：按创建比赛时“参赛范围”选择的球队统计成绩并按平均杆数排名，（球队、参赛人数、平均杆数、总杆数、排名）
	 * 比洞赛：用不同的表。（球队、获胜组、打平组、得分、排名）
	 *
	 * @return
	 */
	public Map<String, Object> getTeamTotalScoreByMatchId(Long matchId, Integer mingci) {
		Map<String, Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
//		赛制1( 0:比杆 、1:比洞)
		if (matchInfo.getMiMatchFormat1() == 0) {
			List<Map<String, Object>> scoreList = matchDao.getMatchRodTotalScore(matchId);
			result.put("scoreList",scoreList);
		} else {
			List<Map<String, Object>> list = new ArrayList<>();
			List<Long> teamIds = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
			//获取本次比赛中的分组和每组用户的总分
			List<Map<String,Object>> holeScoreList = matchDao.getMatchHoleScoreList(matchId);
			if(holeScoreList != null && holeScoreList.size()>0){
				for(Long teamId :teamIds){
					TeamInfo teamInfo = matchDao.get(TeamInfo.class,teamId);
					Map<String, Object> bean = new HashMap<>();
					bean.put("teamId",teamId);
					bean.put("teamName",teamInfo.getTiName());
					for(int i = 0;i<holeScoreList.size();i++){
						Map<String,Object> team1 = holeScoreList.get(i);
						Map<String,Object> team2 = holeScoreList.get(i+1);
						i += 1;
						Long team1Id = getLongValue(team1,"teamId");
						Long team2Id = getLongValue(team2,"teamId");
						Integer team1SumRod = getIntegerValue(team1,"sumRod");
						Integer team2SumRod = getIntegerValue(team2,"sumRod");
						if(teamId.equals(team1Id)){
							if(team1SumRod < team2SumRod){
								//第一队赢球
								bean.put("winGroupName",getName(team1,"groupName"));
							}else if(team1SumRod.equals(team2SumRod)){
								//打平
								bean.put("drewGroupName",getName(team1,"groupName"));
							}
							bean.put("score",team1SumRod);
						}else if(teamId.equals(team2Id)){
							if(team2SumRod < team1SumRod){
								//第二队赢球
								bean.put("winGroupName",getName(team2,"groupName"));
							}else if(team2SumRod.equals(team1SumRod)){
								//打平
								bean.put("drewGroupName",getName(team2,"groupName"));
							}
							bean.put("score",team2SumRod);
						}
					}
					list.add(bean);
				}
			}
			result.put("scoreList",list);
		}
		//获取前N名成绩统计 每个队排前n名的人的杆数和排名
//		List<Map<String, Object>> mingciList = matchDao.getMatchRodScoreByMingci(matchId,mingci);
		List<Map<String, Object>> mingciList = matchDao.getMatchRodScoreByMingci(matchId,mingci);
		result.put("mingciList",mingciList);
		return result;
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

		MatchUserGroupMapping matchUserGroupMapping = new MatchUserGroupMapping();
		matchUserGroupMapping.setMugmMatchId(matchId);
		matchUserGroupMapping.setMugmGroupId(groupId);
		matchUserGroupMapping.setMugmGroupName(groupName);
		matchUserGroupMapping.setMugmUserId(userId);
		matchUserGroupMapping.setMugmUserName(userInfo.getUiRealName());
		matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
		matchUserGroupMapping.setMugmCreateUserId(userId);
		matchUserGroupMapping.setMugmCreateUserName(userInfo.getUiRealName());
//        参赛范围(1、公开 球友均可报名；2、队内：某几个球队队员可报名；3:不公开)
		if(matchInfo.getMiJoinOpenType() == 1){
			//type = 2 待审核
			matchUserGroupMapping.setMugmUserType(2);
			matchDao.save(matchUserGroupMapping);
            flag = 0;
        }else if(matchInfo.getMiJoinOpenType() == 2){
			matchUserGroupMapping.setMugmTeamId(chooseTeamId);
            //是否参赛球队的队员
            Long count = matchDao.getIsJoinTeamsUser(userId,teamIdList);
            if(count >0){
				//type = 2 待审核
                matchUserGroupMapping.setMugmUserType(2);
				matchDao.save(matchUserGroupMapping);
                flag = 0;
            }
        }
		return flag;
	}

	/**
	 * 比赛——普通用户从一个组退出比赛
	 * @return
	 */
	public void quitMatch(Long matchId, Long groupId, String openid) {
		matchDao.delFromMatch(matchId, groupId, userService.getUserIdByOpenid(openid));
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
	public Integer getUserChaPoint(Long userId) {
		//获取最近参加的10场比赛的id
		List<Object> list = matchDao.getLessFiveMatchByUserId(userId);
		if(list != null && list.size()>0){
			BigDecimal avg = (BigDecimal)list.get(0);
			if(avg != null){
				BigDecimal processClll = new BigDecimal((avg.floatValue() - 72) * 0.8).setScale(0, BigDecimal.ROUND_HALF_UP);
				return processClll.intValue();
			}
		}
		return 0;
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
	public Long getMeCanScore(Long matchId, Long groupId, String openid) {
		return matchDao.getMeCanScore(matchId, groupId, userService.getUserIdByOpenid(openid));
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
	 * 比赛详情——赛长——获取备选球友(除去已经报名的)，赛长所在队的球友或者其搜索的结果
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
			//查询和赛长代表参赛队的同队的队友，不包括自己和已经参赛的，和已经报名的球友
			List<Long> userIdList = matchDao.getUserListByIds(matchId);
			//已经报名的球友
			List<Map<String, Object>> applyUserList = matchDao.getApplyUserByMatchId(matchId,null, groupId);
			if(applyUserList != null && applyUserList.size()>0){
				for(Map<String, Object> map:applyUserList){
					userIdList.add((Long)map.get("uiId"));
				}
			}
			userIdList.add(captainUserId);
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
					//将该球友的状态再改为已报名
					matchUserGroupMapping.setMugmUserType(2);
					matchDao.update(matchUserGroupMapping);
				}
			}
		}
	}

	/**
	 * 创建比赛——选择上报的上级球队——获取参赛用户所在的上级球队
	 * @return
	 */
	public List<Map<String,Object>> getJoinTeamListByMatchId(String joinTeamIds) {
		String[] ids = joinTeamIds.split(",");
		List<Long> idList = new ArrayList<>();
		for(String id:ids){
			if(StringUtils.isNotEmpty(id)){
				idList.add(Long.parseLong(id));
			}
		}
		List<Map<String,Object>> list = matchDao.getJoinTeamListByMatchId(idList);
		if(list != null && list.size()>0){
			for(Map<String,Object> map :list){
				//logo
				String logo = (String)map.get("teamLogo");
				map.put("teamLogo", PropertyConst.DOMAIN+logo);
			}
		}
		return list;
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
}
