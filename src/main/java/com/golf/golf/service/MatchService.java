package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.golf.bean.MatchGroupBean;
import com.golf.golf.bean.MatchUserGroupMappingBean;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.dao.MatchDao;
import com.golf.golf.db.*;
import com.golf.golf.enums.MatchGroupUserMappingTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * 获取全部比赛列表 或 获取我参加的比赛列表
	 * @return
	 */
	public POJOPageInfo getMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		POJOPageInfo pageInfo_ = matchDao.getMatchList(searchBean,pageInfo);
		if(pageInfo_.getCount() >0 && pageInfo_.getItems().size()>0){
			List<MatchInfo> matchInfoList = new ArrayList<>();
			for(Map<String, Object> result: (List<Map<String, Object>>)pageInfo_.getItems()){
				MatchInfo matchInfo = new MatchInfo();
//				String select = "m.mi_id AS mi_id," +
//				"m.mi_title AS mi_title," +
//				"m.mi_park_name AS mi_park_name," +
//				"m.mi_match_time AS mi_match_time," +
//				"m.mi_apply_end_time as mi_apply_end_time," +
//				"m.mi_is_end AS mi_is_end,count(mj.mjwi_user_id) as userCount ";
				matchInfo.setMiId(getLongValue(result,"mi_id"));
				matchInfo.setMiTitle(getName(result, "mi_title"));
				matchInfo.setMiParkName(getName(result, "mi_park_name"));
				matchInfo.setMiMatchTime(getName(result,"mi_match_time"));
				matchInfo.setStateStr();
				matchInfo.setMiHit(getIntegerValue(result,"userCount"));
				matchInfoList.add(matchInfo);
			}
			pageInfo.setItems(matchInfoList);
			pageInfo.setCount(pageInfo_.getCount());
		}
		return pageInfo;
	}


	/**
	 * 加点击量
	 * @return
	 */
	public MatchInfo addHit(Long matchId){
		MatchInfo db = matchDao.get(MatchInfo.class,matchId);
		if(db.getMiHit() == null || db.getMiHit() == 0){
			db.setMiHit(1);
		}else{
			db.setMiHit(db.getMiHit() + 1);
		}
		matchDao.update(db);
		return db;
	}

	/**
	 * 获取本比赛的围观用户列表和比赛分组
	 * @param count 获取围观显示的个数
	 * @return
	 */
	public Map<String, Object> getMatchInfo(Long matchId, Integer count) {
        Map<String, Object> result = new HashMap<String, Object>();
        if(count == null){
        	count = 0;
		}
        POJOPageInfo pageInfo = new POJOPageInfo(count, 1);
	    //围观
		List<Map<String, Object>> watchList = matchDao.getUserListByMatchId(matchId, 0, pageInfo);

		//赛长
		List<Map<String, Object>> captainList = matchDao.getCaptainListByMatchId(matchId, pageInfo);

		//分组
		List<MatchGroup> groupList_ = matchDao.getMatchGroupList_(matchId);
		List<MatchGroupBean> groupList = new ArrayList<>();
		if(groupList_ != null && groupList_.size() >0){
			for(MatchGroup matchGroup : groupList_){
				MatchGroupBean matchGroupBean = new MatchGroupBean();
				matchGroupBean.setMatchGroup(matchGroup);
				List<Map<String, Object>> groupUserList = matchDao.getMatchGroupListByGroupId(matchGroup.getMgId());
				matchGroupBean.setUserInfoList(groupUserList);
				groupList.add(matchGroupBean);
			}
		}

		//是否是赛长
		SearchBean searchBean = new SearchBean();
		searchBean.addParpField("matchId", matchId);
		searchBean.addParpField("userId", WebUtil.getUserIdBySessionId());
		Long captainCount = matchDao.getIsCaptain(searchBean);
		Boolean isCaptain = false;
		if(captainCount > 0){
			isCaptain = true;
		}

        result.put("watchList", watchList);
		result.put("captainList", captainList);
        result.put("groupList", groupList);
		result.put("isCaptain", isCaptain);
		return result;
	}

	/**
	 * 获取值
	 * @param map
	 * @param key
	 */
	public String getName(Map<String, Object> map, String key){
		if(map == null || map.get(key) == null){
			return null;
		}else{
			return map.get(key).toString();
		}
	}

	/**
	 * 获取long
	 * @param map
	 * @param key
	 */
	public Long getLongValue(Map<String, Object> map, String key){
		if(map == null || map.get(key) == null){
			return null;
		}else{
			return Long.parseLong(map.get(key).toString());
		}
	}

	/**
	 * Integer
	 * @param map
	 * @param key
	 */
	public Integer getIntegerValue(Map<String, Object> map, String key){
		if(map == null || map.get(key) == null){
			return 0;
		}else{
			return Integer.parseInt(map.get(key).toString());
		}
	}

	/**
	 * 获取全部围观列表
	 * @return
	 */
	public List<Map<String, Object>> getMoreWatchUserList(Long matchId) {
		POJOPageInfo pageInfo = new POJOPageInfo(0, 1);
		List<Map<String, Object>> watchList = matchDao.getUserListByMatchId(matchId, 0, pageInfo);
		return watchList;
	}

	/**
	 * 点击进入比赛详情——获取参赛球队信息和比赛详情
	 * @return
	 */
	public Map<String, Object> getMatchDetailInfo(Long matchId) {
		Map<String, Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		result.put("matchInfo", matchInfo);
		//获取比赛球队信息
		String teamIds = matchInfo.getMiJoinTeamIds();
		if(StringUtils.isNotEmpty(teamIds)){
			List<Long> teamIdList = getLongTeamIdList(teamIds);
			List<TeamInfo> teamList = matchDao.getTeamListByIds(teamIdList);
			result.put("teamList", teamList);
		}
		//获取成绩上报球队信息
		if(StringUtils.isNotEmpty(matchInfo.getMiReportScoreTeamId())){
			List<Long> reportScoreTeamIdList = getLongTeamIdList(matchInfo.getMiReportScoreTeamId());
			List<TeamInfo> reportScoreTeamList = matchDao.getTeamListByIds(reportScoreTeamIdList);
			result.put("reportScoreTeamList", reportScoreTeamList);
		}
		return result;
	}

	private List<Long> getLongTeamIdList(String teamIds) {
		List<Long> teamIdList = new ArrayList<>();
		String[] ids = teamIds.split(",");
		for(String id:ids){
			if(StringUtils.isNotEmpty(id)){
				teamIdList.add(Long.parseLong(id));
			}
		}
		return teamIdList;
	}


	/**
     * 报名-获取本场比赛详情 参赛球队、比赛信息、上报球队
     * @return
     */
    public Map<String, Object> getMatchInfoById(Long wcId) {
        Map<String, Object> result = new HashMap<>();
        //比赛信息
        MatchInfo matchInfo = matchDao.get(MatchInfo.class,wcId);
        matchInfo.setMiHit(matchInfo.getMiHit()==null?1:matchInfo.getMiHit()+1);
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
	 * @return
	 */
	public TeamInfo getTeamInfoById(String teamId) {
		return matchDao.get(TeamInfo.class,Long.parseLong(teamId));
	}


	/**
	 * 创建比赛—点击球场-获取分区和洞
	 * @return
	 */
	public List<ParkPartition> getParkZoneAndHole(Long parkId) {
		return matchDao.getParkZoneAndHole(parkId);
	}

	/**
	 * 查询球场区域
	 * @return
	 */
	public POJOPageInfo getParkListByRegion(SearchBean searchBean, POJOPageInfo pageInfo) {
		return matchDao.getParkListByRegion(searchBean,pageInfo);
	}

	/**
	 * 查询该区域下的球场
	 * @return
	 */
	public POJOPageInfo getParkListByRegionName(SearchBean searchBean, POJOPageInfo pageInfo) {
		return matchDao.getParkListByRegionName(searchBean,pageInfo);
	}

	/**
	 * 查询球场列表——所有球场
	 * @return
	 */
	public POJOPageInfo getParkList(SearchBean searchBean, POJOPageInfo pageInfo) {
		return matchDao.getParkList(searchBean,pageInfo);
	}

	/**
	 * 查询球场列表——附近的球场
	 * @return
	 */
	public POJOPageInfo getParkListNearby(SearchBean searchBean, POJOPageInfo pageInfo) {
		UserInfo userInfo = matchDao.get(UserInfo.class, UserUtil.getUserId());
		if(userInfo != null && StringUtils.isNotEmpty(userInfo.getUiLongitude())
				&& StringUtils.isNotEmpty(userInfo.getUiLatitude())){
			//用户经纬度存在, 计算我附近5千米的经纬度
			searchBean = teamService.findNeighPosition(searchBean, Double.parseDouble(userInfo.getUiLongitude()),
					Double.parseDouble(userInfo.getUiLatitude()));
		}
		return matchDao.getParkListNearby(searchBean,pageInfo);
	}


    /**
     * 保存一条用户记分对应关系
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
     * @return
     */
    public boolean getScoreType(SearchBean searchBean) {
        //判断是否赛长
        Long isCaptain = matchDao.getIsCaptain(searchBean);
        if(isCaptain > 0){
            return true;
        }
        //判断是否可记分
        Long count = matchDao.getScoreTypeCount(searchBean);
        if(count > 0){
            return true;
        }
        return false;
    }

    /**
     * 保存分组
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
	 * @return
	 */
	public void updateMatchGroupByCaption(Long matchId, Long groupId, String userIds) {
		if(StringUtils.isEmpty(userIds)){
			String[] ids = userIds.split(",");
			for(String userId : ids){
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
     * @return
     */
    public List<MatchUserGroupMappingBean> getMatchGroupMappingList(Long matchId) {
        List<MatchUserGroupMapping> mappingList = matchDao.getMatchGroupMappingList(matchId);
        List<MatchUserGroupMappingBean> list = new ArrayList<MatchUserGroupMappingBean>();
        if(mappingList != null && mappingList.size() > 0){
            MatchUserGroupMappingBean bean = new MatchUserGroupMappingBean();
            for(MatchUserGroupMapping mapping:mappingList){
                bean.setGroupId(mapping.getMugmGroupId());
                bean.setGroupName(mapping.getMugmGroupName());
                bean.setUserType(mapping.getMugmUserType());

                UserInfo userInfo = matchDao.get(UserInfo.class,mapping.getMugmUserId());
                userInfo.setUiId(userInfo.getUiId());
                userInfo.setUiRealName(userInfo.getUiRealName());
                userInfo.setUiHeadimg(userInfo.getUiHeadimg());
                bean.getUserList().add(userInfo);

                //是否队长不一样，分组名称不一样
                if(!bean.getUserType().equals(mapping.getMugmUserType()) ||
						!bean.getGroupId().equals(mapping.getMugmGroupId())){
                    list.add(bean);
                }
            }
        }
        return list;
    }


	/**
	 * 取消报名，退出分组 到临时分组    赛长将多个球友退出分组
	 * @return
	 */
	public void quitMatchGroup(Long matchId, Long groupId, Long userId, String userIds) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId", matchId);
		parp.put("groupId", groupId);
		if(StringUtils.isNotEmpty(userIds)){
			String[] ids = userIds.split(",");
			List<Long> userIdList = new ArrayList<Long>();
			for(String id :ids){
				if(StringUtils.isNotEmpty(id)){
					userIdList.add(Long.parseLong(id));
				}

			}
			parp.put("userIdList", userIdList);
		}else if(userId != null){
			parp.put("userId", userId);
		}
		matchDao.updateMyMatchGroupMapping(parp);
	}

	/**
	 * 获取临时分组中的球友
	 * @param matchId 比赛id
	 * @param groupId 比赛分组id
	 * @return
	 */
	public List<MatchUserGroupMapping> getUserByTemporary(Long matchId, Long groupId) {
		return matchDao.getUserByTemporary(matchId, groupId);
	}

	/**
	 * 创建比赛-保存-自动成为赛长
	 * @return
	 */
	public void saveMatchInfo(MatchInfo matchInfo) {
		ParkInfo parkInfo = matchDao.getParkIdByName(matchInfo.getMiParkName());
		if(parkInfo != null){
			matchInfo.setMiParkId(parkInfo.getPiId());
		}
		matchInfo.setMiParkName(matchInfo.getMiParkName());
		matchInfo.setMiType(1);
		matchInfo.setMiCreateTime(System.currentTimeMillis());
		matchInfo.setMiCreateUserId(UserUtil.getUserId());
		matchInfo.setMiCreateUserName(UserUtil.getShowName());
		matchInfo.setMiIsValid(1);
		matchDao.save(matchInfo);
	}

    /**
     * 赛长——本组用户列表
     * @return
     */
    public List<Map<String, Object>> getUserListByMatchIdGroupId(Long matchId, Long groupId) {
		return matchDao.getUserListByMatchIdGroupId(matchId, groupId);
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
    public void saveMyOnlyMatch(Long parkId, String parkName, String zoneBeforeNine, String zoneAfterNine, String playTime, Integer groupPeopleNum, String digest) {
        MatchInfo matchInfo = new MatchInfo();
        matchInfo.setMiType(0);
        matchInfo.setMiPeopleNum(groupPeopleNum);
        matchInfo.setMiParkId(parkId);
        matchInfo.setMiParkName(parkName);
        matchInfo.setMiZoneBeforeNine(zoneBeforeNine);
        matchInfo.setMiZoneAfterNine(zoneAfterNine);
        matchInfo.setMiDigest(digest);
        matchInfo.setMiMatchTime(playTime);
        matchInfo.setMiMatchOpenType(3);
        matchInfo.setMiCreateTime(System.currentTimeMillis());
        matchInfo.setMiCreateUserId(UserUtil.getUserId());
        matchInfo.setMiCreateUserName(UserUtil.getShowName());
        matchDao.save(matchInfo);
    }


	/**
	 * 单练——选择器——获取球场
	 * @return
	 */
	public Map<String, Object> getParkInfoList(String city) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<String> cityList = matchDao.getParkInfoCityList();
		if(StringUtils.isEmpty(city)){
			city = cityList.get(0);
		}
		List<String> parkList = matchDao.getParkInfoList(city);
		result.put("cityList", cityList);
		result.put("parkList", parkList);
		return result;
	}


	/**
	 * 单练——开始记分——保存数据
	 * @return
	 */
	public Long saveSinglePlay(String parkName, String playTime, Integer peopleNum, String digest) {
		MatchInfo matchInfo = new MatchInfo();
		ParkInfo parkInfo = matchDao.getParkIdByName(parkName);
		if(parkInfo != null){
			matchInfo.setMiParkId(parkInfo.getPiId());
		}
		matchInfo.setMiTitle(WebUtil.getUserNameBySessionId()+"的单练");
		matchInfo.setMiParkName(parkName);
		matchInfo.setMiType(0);
		matchInfo.setMiMatchTime(playTime);
		matchInfo.setMiPeopleNum(peopleNum);
		matchInfo.setMiDigest(digest);
		matchInfo.setMiJoinOpenType(3);
		matchInfo.setMiIsEnd(0);
		matchInfo.setCreateTimeStr(System.currentTimeMillis());
		matchInfo.setMiCreateUserId(WebUtil.getUserIdBySessionId());
		matchInfo.setMiCreateUserName(WebUtil.getUserNameBySessionId());
		matchDao.save(matchInfo);
		return matchInfo.getMiId();
	}

	/**
	 * 单练——查询是否有我正在进行的单练
	 * @return
	 */
	public MatchInfo getMySinglePlay() {
		Long userId = WebUtil.getUserIdBySessionId();
		return matchDao.getMySinglePlay(userId);
	}

	/**
	 * 比赛详情——添加组
	 * @return
	 */
	public void addGroupByTeamId(Long matchId) {
		//获取最大组
		MatchGroup maxGroup = matchDao.getMaxGroupByMatchId(matchId);
		String groupName = maxGroup.getMgGroupName();
		Integer max = Integer.parseInt(groupName.substring(1,groupName.length()-1));
		max++;
		MatchGroup group = new MatchGroup();
		group.setMgMatchId(matchId);
		group.setMgGroupName("第"+max+"组");
		group.setMgCreateUserId(WebUtil.getUserIdBySessionId());
		group.setMgCreateUserName(WebUtil.getUserNameBySessionId());
		group.setMgCreateTime(System.currentTimeMillis());
		matchDao.save(group);
	}

	/**
	 * 比赛详情——赛长获取已经报名的用户
	 * @return
	 */
	public List<Map<String, Object>> getApplyUserByMatchId(Long matchId) {
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		if(StringUtils.isEmpty(matchInfo.getMiJoinTeamIds())){
			//比赛没选球队，就从临时报名的用户中选
			return matchDao.getApplyUserByMatchId(matchId);
		}
		//比赛选了球队，从球队中选 去除已经参赛的用户
		//参赛球队
		List<Long> teamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
		return matchDao.getApplyUserListByMatchId(matchId,teamIdList);
	}

	/**
	 * 比赛详情——保存——将用户加入该分组
	 * @return
	 */
	public void addUserToGroupByMatchId(Long matchId, Long groupId, String userIds) {
		if(StringUtils.isNotEmpty(userIds)){
			userIds = userIds.replace("[","");
			userIds = userIds.replace("]","");
			userIds = userIds.replace("\"","");
			String[] uIds = userIds.split(",");
			MatchGroup matchGroup = matchDao.get(MatchGroup.class,groupId);
			for(String userId :uIds){
				if(StringUtils.isNotEmpty(userId)){
					MatchUserGroupMapping matchUserGroupMapping = new MatchUserGroupMapping();
					matchUserGroupMapping.setMugmMatchId(matchId);
					matchUserGroupMapping.setMugmGroupId(groupId);
					matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
					matchUserGroupMapping.setMugmUserType(0);
					matchUserGroupMapping.setMugmUserId(Long.parseLong(userId));
					matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
					matchUserGroupMapping.setMugmCreateUserId(WebUtil.getUserIdBySessionId());
					matchUserGroupMapping.setMugmCreateUserName(WebUtil.getUserNameBySessionId());
					matchDao.save(matchUserGroupMapping);
				}
			}
		}
	}

	/**
	 * 比赛详情——保存——将用户从该分组删除
	 * @return
	 */
	public void delUserByMatchIdGroupId(Long matchId, Long groupId, String userIds) {
		if(StringUtils.isNotEmpty(userIds)){
			userIds = userIds.replace("[","");
			userIds = userIds.replace("]","");
			userIds = userIds.replace("\"","");
			String[] uIds = userIds.split(",");
			for(String userId :uIds){
				if(StringUtils.isNotEmpty(userId)){
					matchDao.delUserByMatchIdGroupId(matchId, groupId, Long.parseLong(userId));
				}
			}
		}
	}

	/**
	 * 获取参赛球队列表
	 * @return
	 */
	public List<Map<String, Object>> getTeamListByMatchId(Long matchId) {
		if(matchId != null){
			MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
			List<Long> teamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
			return matchDao.getApplyUserListByMatchId(matchId,teamIdList);
		}else{
			return null;
		}
	}
}
