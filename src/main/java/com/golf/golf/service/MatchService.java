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
import com.golf.golf.dao.MatchScoreDao;
import com.golf.golf.dao.TeamDao;
import com.golf.golf.dao.UserDao;
import com.golf.golf.db.*;
import com.golf.golf.enums.MatchResultEnum;
import com.golf.golf.service.admin.AdminImportService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.chanjar.weixin.common.error.WxErrorException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.*;
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
	private MatchScoreDao matchScoreDao;
	@Autowired
	private TeamDao teamDao;
    @Autowired
    private UserDao userDao;
	@Autowired
	private TeamService teamService;
	@Autowired
	protected WxMaService wxMaService;
	@Autowired
	protected UserService userService;
    @Autowired
    protected AdminImportService adminImportService;
    @Autowired
    protected MatchScoreService matchScoreService;

	/**
	 * 查询球场列表——区域
	 * @return
	 */
	public POJOPageInfo getParkListByCity(SearchBean searchBean, POJOPageInfo pageInfo){
		UserInfo userInfo = matchDao.get(UserInfo.class, (Long)searchBean.getParps().get("userId"));
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
	public POJOPageInfo getParkListNearby(SearchBean searchBean, POJOPageInfo pageInfo){
		UserInfo userInfo = matchDao.get(UserInfo.class, (Long)searchBean.getParps().get("userId"));
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
     * //改了个名子（原来叫getMatchList，因为get打头的函数会设为只读数据库，但pullMatchList里需修改比赛结束状态
	 * @return
	 */
	public POJOPageInfo pullMatchList(SearchBean searchBean, POJOPageInfo pageInfo) throws ParseException {
		UserInfo userInfo = matchDao.get(UserInfo.class, (Long)searchBean.getParps().get("userId"));
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
			/*if(pageInfo.getItems() != null && pageInfo.getItems().size()>0){
				//就判断这个人在不在参赛球队的名单中，在就符合要求，不在的不要。如果没参赛球队的也要。可报名的比赛应该不多，可以先不管地理位置远近了。
				checkIsInJoinTeam(pageInfo,(Long)searchBean.getParps().get("userId"));
			}*/
		}else if(type == 3){
			//已报名的比赛（包括我参加的和我创建的报名中的比赛，不管是否加入分组）
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

                //处理过期未关闭的比赛，超过比赛日9天自动关闭
                String  matchTime=getName(result, "mi_match_time");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date mTime = formatter.parse(matchTime);
                Calendar cTime1 = Calendar.getInstance();   //当前日期
                Calendar cTime2 = Calendar.getInstance();
                cTime2.setTime(mTime);
                int today = cTime1.get(Calendar.DAY_OF_YEAR);
                int matchday = cTime2.get(Calendar.DAY_OF_YEAR);

                Integer matchIsEnd = getIntegerValue(result, "mi_is_end");
                if ((today-matchday)>9 && matchIsEnd ==1 ){
                    matchIsEnd = 2;
                   matchDao.updateMatchIsEnd( matchInfo.getMiId());
                }


               //处理LOGO
				String miLogo = getName(result, "mi_logo");
				if ("".equals(miLogo)) {miLogo = null;}
               if (matchInfo.getMiType()>0 ) {
                  matchInfo.setMiLogo(PropertyConst.DOMAIN + miLogo);
                 } else{
                  matchInfo.setMiLogo(miLogo);
                }

               matchInfo.setMiTitle(getName(result, "mi_title"));
                matchInfo.setMiPriority(getIntegerValue(result, "mi_priority"));
				matchInfo.setMiParkName(getName(result, "mi_park_name"));
				matchInfo.setMiMatchTime(getName(result, "mi_match_time"));
				matchInfo.setMiMatchFormat1(getIntegerValue(result, "mi_match_format_1"));
				matchInfo.setMiMatchFormat2(getIntegerValue(result, "mi_match_format_2"));
				matchInfo.setMiHit(getIntegerValue(result, "userWatchCount"));
				matchInfo.setUserCount(getIntegerValue(result, "userCount"));
                matchInfo.setMiIsEnd(matchIsEnd);
				matchInfo.setStateStr(matchInfo.getStateStr());
				//是否是赛长（显示创建比赛列表时用）
				matchInfoList.add(matchInfo);
			}
			pageInfo.setItems(matchInfoList);
			pageInfo.setCount(pageInfo.getCount());
		}
		return pageInfo;
	}

    /**
     * 比赛——获取关联比赛列表
     * @return
     */
    public POJOPageInfo getChildMatchList(SearchBean searchBean, POJOPageInfo pageInfo, String openid) throws UnsupportedEncodingException {
        pageInfo = matchDao.getChildMatchList(searchBean,pageInfo);
        if (pageInfo.getCount() > 0 && pageInfo.getItems().size() > 0) {
            List<MatchInfo> matchInfoList = new ArrayList<>();
            for (Map<String, Object> result : (List<Map<String, Object>>) pageInfo.getItems()) {
                MatchInfo matchInfo = new MatchInfo();

                matchInfo.setMiType(getIntegerValue(result, "mi_type"));
                matchInfo.setMiId(getLongValue(result, "mi_id"));

                //处理LOGO
                String miLogo = getName(result, "mi_logo");
                if ("".equals(miLogo)) {miLogo = null;}
                if (matchInfo.getMiType() == 1) {
                    matchInfo.setMiLogo(PropertyConst.DOMAIN + miLogo);
                } else{
                    matchInfo.setMiLogo(miLogo);
                }

                matchInfo.setMiTitle(getName(result, "mi_title"));
                matchInfo.setMiParkName(getName(result, "mi_park_name"));
                matchInfo.setMiMatchTime(getName(result, "mi_match_time"));
                matchInfo.setMiMatchFormat1(getIntegerValue(result, "mi_match_format_1"));
                matchInfo.setMiMatchFormat2(getIntegerValue(result, "mi_match_format_2"));
                matchInfo.setMiHit(getIntegerValue(result, "userWatchCount"));
                matchInfo.setUserCount(getIntegerValue(result, "userCount"));
                matchInfo.setMiIsEnd(getIntegerValue(result, "mi_is_end"));
                matchInfo.setStateStr(matchInfo.getStateStr());
                //是否是赛长（显示创建比赛列表时用）
                matchInfoList.add(matchInfo);
            }
            pageInfo.setItems(matchInfoList);
            pageInfo.setCount(pageInfo.getCount());
        }
        /*
        if(pageInfo.getCount() >0 && pageInfo.getItems() != null && pageInfo.getItems().size() >0){
            getCaptain(pageInfo.getItems(), openid);
        }
        */
        return pageInfo;
    }

	/**
	 * 获取可报名比赛列表后——如果是队际赛，判断我是否在参赛球队中  公开赛不用管
	 * 如果没参赛球队的也要
	 * @return
	 */
	private void checkIsInJoinTeam(POJOPageInfo pageInfo, Long userId) {
		for(Iterator<Map<String,Object>> teamIterator = (Iterator<Map<String, Object>>) pageInfo.getItems().iterator(); teamIterator.hasNext();){
			Map<String,Object> map = teamIterator.next();
			Integer matchType = getIntegerValue(map,"mi_type");
			String joinTeamIds = getName(map,"mi_join_team_ids");
            Long createUserId = getLongValue(map,"mi_create_user_id");
			List<Long> joinTeamIdList = getLongTeamIdList(joinTeamIds);
			//我是否在参赛球队中
			if(joinTeamIdList != null && joinTeamIdList.size() >0){
                List<Long> joinTeamList = matchDao.getIsJoinTeam(userId,joinTeamIdList);
				if(!createUserId.equals(userId) && (joinTeamList == null || joinTeamList.size() == 0)){
				    //不在
                    teamIterator.remove();
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
	public Map<String, Object> getMatchInfo(MatchInfo matchInfo, Long matchId, Integer count, String openid) throws UnsupportedEncodingException {
		Map<String, Object> result = new HashMap<String, Object>();
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		//参赛球队
        List<Long> joinTeamIds = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
        if(joinTeamIds != null && joinTeamIds.size() >0){
            List<TeamInfo> joinTeamList = matchDao.getTeamListByIds(joinTeamIds);
            if(joinTeamList != null && joinTeamList.size() >0){
                for(TeamInfo teamInfo:joinTeamList){
                    teamInfo.setTiLogo(PropertyConst.DOMAIN+teamInfo.getTiLogo());
                }
            }
            result.put("joinTeamList",joinTeamList);
        }
		//围观
        Long  watchNum =0l;
		List<Map<String, Object>> watchList = matchDao.getWatchUserListByMatchId(matchId);
       for (Map<String,Object> map : watchList) {
            watchNum =watchNum+getLongValue(map,"watchNum");
        }
		//赛长
		List<Map<String, Object>> captainList = matchDao.getCaptainListByMatchId(matchId);
		//用户昵称解码
		decodeUserNickName(captainList);
		//取用户名
		setUserName(captainList);

		//分组
		List<Map<String,Object>> groupList_ = matchDao.getMatchGroupList(matchId,matchInfo.getMiIsEnd());
		List<MatchGroupBean> groupList = new ArrayList<>();
		if (groupList_ != null && groupList_.size() > 0) {
			for (Map<String,Object> map : groupList_) {
				MatchGroup matchGroup = new MatchGroup();
				matchGroup.setMgMatchId(getLongValue(map,"matchId"));
				matchGroup.setMgId(getLongValue(map,"groupId"));
				matchGroup.setMgGroupName(getIntegerValue(map,"groupName").toString());
                matchGroup.setMgGroupNotice(getName(map,"groupNotice"));
                matchGroup.setMgIsGuest(getIntegerValueWithNull(map,"groupIsGuest"));
				MatchGroupBean matchGroupBean = new MatchGroupBean();
				matchGroupBean.setMatchGroup(matchGroup);
				List<Map<String, Object>> groupUserList = matchDao.getMatchGroupListByGroupId(matchId, matchGroup.getMgId());
				if(groupUserList != null && groupUserList.size()>0){
					//用户昵称解码
					decodeUserNickName(groupUserList);
					//取用户名
					setUserName(groupUserList);
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
		//获取已分组的人数
		Long userCountAlreadyGroup = matchDao.getMatchUserCountAlreadyGroup(matchId);
		//已报名人数（出去自动设置的赛长以外全部的）
		Long allApplyUserCount = matchDao.getAllMatchApplyUserCount(matchId);

		//我是否是参赛者
		Long isJoinMatchUser = matchDao.getIsJoinMatchUser(matchId,userInfo.getUiId());

		result.put("matchInfo", matchInfo);
		result.put("watchList", watchList);
		result.put("captainList", captainList);
		result.put("groupList", groupList);
		result.put("isMatchCaptain", isMatchCaptain);
		result.put("userCountAlreadyGroup", userCountAlreadyGroup);
		result.put("allApplyUserCount", allApplyUserCount);
		result.put("isJoinMatchUser", isJoinMatchUser);
        result.put("watchNum", watchNum);
		return result;
	}

	/**
	 * 当前登录用户是否是赛长
	 */
	public boolean getIsCaptain(Long matchId, String openid){
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		if(userInfo == null){
			return false;
		}
		Long captainCount = matchDao.getIsMatchCaptain(matchId, userInfo.getUiId());
        return captainCount > 0;
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
	public List<Map<String, Object>> getMoreWatchUserList(Long matchId) throws UnsupportedEncodingException {
		List<Map<String, Object>> allWatchUserList = matchDao.getWatchUserListByMatchId(matchId);
		//用户昵称解码
		decodeUserNickName(allWatchUserList);
		//取用户名
		setUserName(allWatchUserList);
		return allWatchUserList;
	}

	/**
	 * 点击进入比赛详情——获取参赛球队信息和比赛详情
	 *
	 * @return
	 */
	public Map<String, Object> getMatchDetailInfo(Long matchId, String openid) {
		Map<String, Object> result = new HashMap<>();
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		//数据库中没有logo 用 ""表示，而小程序前端则用 null表示
		if ( !matchInfo.getMiLogo().equals("") ) {
            matchInfo.setMiLogo(PropertyConst.DOMAIN + matchInfo.getMiLogo());
        } else { matchInfo.setMiLogo(null);}
		result.put("matchInfo", matchInfo);
		if(matchInfo.getMiParkName().length()>11){
			result.put("parkNameShow", matchInfo.getMiParkName().substring(0,9)+"...");
		}else{
			result.put("parkNameShow", matchInfo.getMiParkName());
		}
		//球场信息
		ParkInfo parkInfo = matchDao.get(ParkInfo.class, matchInfo.getMiParkId());
		result.put("parkInfo", parkInfo);
		if (StringUtils.isNotEmpty(userInfo.getUiLongitude()) &&  StringUtils.isNotEmpty(userInfo.getUiLatitude())) {
            if (StringUtils.isNotEmpty(parkInfo.getPiLat()) && StringUtils.isNotEmpty(parkInfo.getPiLng())) {
                String distance = MapUtil.getDistance(userInfo.getUiLatitude(), userInfo.getUiLongitude(), parkInfo.getPiLat(), parkInfo.getPiLng());
                result.put("toMyDistance", Integer.parseInt(distance));
            }
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
		//获取我的参赛代表球队（创建比赛时用到）
		MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId, null, userInfo.getUiId());
		result.put("myChooseTeamId", matchUserGroupMapping != null?matchUserGroupMapping.getMugmTeamId():null);

        //获取我是不是赛长
        Long captainCount = matchDao.getIsMatchCaptain(matchId, userInfo.getUiId());
        Boolean isMatchCaptain = false;
        if (captainCount > 0) {
            isMatchCaptain = true;
        }
        result.put("isMatchCaptain",  isMatchCaptain);
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
        return count > 0;
    }
	/**
	 * 更新比赛详情
	 * 报名比赛期间都可以改，但有些改完数据会发生变化。
	 * @return
	 */
	public MatchInfo updateMatchInfo(MatchInfo matchInfo, String parkName, String chooseTeamId, String openid) {
		UserInfo myUserInfo = userService.getUserInfoByOpenId(openid);
		ParkInfo parkInfo = matchDao.getParkIdByName(parkName);
		Long matchId =  matchInfo.getMiId();
		//更新
		MatchInfo matchInfoDb = matchDao.get(MatchInfo.class, matchInfo.getMiId());
        Long parkIdDb = matchInfoDb.getMiParkId();
		//更新比赛用户配置
		String oldJoinTeamIds = matchInfoDb.getMiJoinTeamIds();
		String newJoinTeamIds = matchInfo.getMiJoinTeamIds();

		//更新赛制
		Integer oldMatchFormat1 = matchInfoDb.getMiMatchFormat1();
		Integer oldMatchFormat2 = matchInfoDb.getMiMatchFormat2();
		Integer newMatchFormat1 = matchInfo.getMiMatchFormat1();
		Integer newMatchFormat2 = matchInfo.getMiMatchFormat2();

		//报名期间都可以改，但比赛开始后，就只能改 观战范围，成绩上报 和 比赛说明。
        List<Long> oldChildMatchIds = getLongIdListReplace(matchInfoDb.getMiChildMatchIds());
        List<Long> childMatchIds = getLongIdListReplace(matchInfo.getMiChildMatchIds());
		matchInfoDb.setMiMatchOpenType(matchInfo.getMiMatchOpenType());
		matchInfoDb.setMiReportScoreTeamId(matchInfo.getMiReportScoreTeamId());
        matchInfoDb.setMiChildMatchIds(matchInfo.getMiChildMatchIds());
		matchInfoDb.setMiContent(matchInfo.getMiContent());
		matchInfoDb.setMiTitle(matchInfo.getMiTitle());
    	if (!matchInfo.getMiLogo().equals(matchInfoDb.getMiLogo())) {
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
	    matchInfoDb.setMiMatchFormat3(matchInfo.getMiMatchFormat3());
		matchInfoDb.setMiUpdateTime(System.currentTimeMillis());
		matchInfoDb.setMiUpdateUserId(myUserInfo.getUiId());
		matchInfoDb.setMiUpdateUserName(myUserInfo.getUserName());
		matchDao.update(matchInfoDb);

		//如果参赛球队有改变

        //旧参赛球队
        List<Long> oldJoinTeamIdList = getLongTeamIdList(oldJoinTeamIds);
        Collections.sort(oldJoinTeamIdList);
        //新参赛球队
        List<Long> newJoinTeamIdList = getLongTeamIdList(newJoinTeamIds);
        Collections.sort(newJoinTeamIdList);

        //如果参赛球队有改变，删除已经删除球队的报名球员
        oldJoinTeamIdList.removeAll(newJoinTeamIdList);
        if (oldJoinTeamIdList.size() > 0 || "".equals(oldJoinTeamIds)) {
            matchDao.delMatchUserMappingByMatchId(matchInfoDb.getMiId(), oldJoinTeamIdList);
        }

        //如果球队有增加，则把新球队的队长加进来
        oldJoinTeamIdList = getLongTeamIdList(oldJoinTeamIds);
        Collections.sort(oldJoinTeamIdList);
        newJoinTeamIdList.removeAll(oldJoinTeamIdList);
        List<Map<String, Object>> matchCaptainList ;
        if (newJoinTeamIdList != null && newJoinTeamIdList.size() > 0) {
            //队际赛 获取新的参赛球队的队长
            List<Map<String, Object>> teamCaptainList = matchDao.getTeamCaptailByTeamIds(newJoinTeamIdList);

            //将这些球队的队长加入比赛mapping表 我默认放第一组。其他赛长不分组，设置isdel=1
            if (teamCaptainList != null && teamCaptainList.size() > 0) {
                boolean addCaptain;
                Long captainUserId;
                Long captainUserId1 ;
                for (Map<String, Object> map : teamCaptainList) {
                    matchCaptainList =  matchDao.getAllMatchCaptainList(matchInfo.getMiId());
                    addCaptain = true;
                    captainUserId = (Long) map.get("userId");
                    for (Map<String, Object> map1 : matchCaptainList) {
                        captainUserId1 = (Long) map1.get("uiId");
                        if (captainUserId.equals(captainUserId1)) {
                            addCaptain = false;
                            break;
                        }
                    }
                    if (addCaptain) {
                        MatchUserGroupMapping mugm = new MatchUserGroupMapping();
                        mugm.setMugmMatchId(matchInfo.getMiId());
                        mugm.setMugmUserType(0);
                        //其他赛长为自动设置的赛长
                        mugm.setMugmIsAutoCap(1);
                        mugm.setMugmTeamId((Long) map.get("teamId"));
                        mugm.setMugmIsDel(1);
                        /*
                        mugm.setMugmGroupId(matchGroup.getMgId());
                        mugm.setMugmGroupName(matchGroup.getMgGroupName());
                        */
                        mugm.setMugmUserId(captainUserId);
                        UserInfo otherCaptainUserInfo = matchDao.get(UserInfo.class, captainUserId);
                        mugm.setMugmUserName(otherCaptainUserInfo.getUserName());
                        mugm.setMugmCreateUserId(myUserInfo.getUiId());
                        mugm.setMugmCreateUserName(myUserInfo.getUserName());
                        mugm.setMugmCreateTime(System.currentTimeMillis());
                        matchDao.save(mugm);
                    }
                }
            }
        }

       //如果我不是比赛赛长，就把我加成
        Long isCaptain = matchDao.getIsMatchCaptain(matchInfo.getMiId(), myUserInfo.getUiId());
        MatchUserGroupMapping myMugm = new MatchUserGroupMapping();
        myMugm.setMugmMatchId(matchInfo.getMiId());

        if (isCaptain == 0) {

            myMugm.setMugmUserType(0);
            //标记我 自动设置的赛长为否
            myMugm.setMugmIsAutoCap(0);
            myMugm.setMugmIsDel(0);
            /*
            myMugm.setMugmGroupId(matchGroup.getMgId());
            myMugm.setMugmGroupName(matchGroup.getMgGroupName());
            */
            if (!"".equals(chooseTeamId)) {
                Long tId = Long.parseLong(chooseTeamId);
                myMugm.setMugmTeamId(tId);
            }
            myMugm.setMugmUserId(myUserInfo.getUiId());
            myMugm.setMugmUserName(myUserInfo.getUserName());
            myMugm.setMugmCreateUserId(myUserInfo.getUiId());
            myMugm.setMugmCreateUserName(myUserInfo.getUserName());
            myMugm.setMugmCreateTime(System.currentTimeMillis());
            matchDao.save(myMugm);
        }

        //如果球场有变化，如果已经记分的话，需要更新match_score中的记分记录
        if( parkInfo !=null && parkIdDb != parkInfo.getPiId()){
            updatePark(matchInfo,parkInfo.getPiId());
        }
        //如果球队没变化新老有一个是个人比洞，并且新老值和以前不一样，就将已经参赛的用户重新放入报名列表
        if (((oldMatchFormat1 == 1 && oldMatchFormat2 == 0) || (newMatchFormat1 == 1 && newMatchFormat2 == 0))
                && (!oldMatchFormat1.equals(newMatchFormat1) || !oldMatchFormat2.equals(newMatchFormat2))) {
            //更新参赛球友的isdel，除了我
            matchDao.updateUserMappingIsDel(matchInfoDb.getMiId(), myUserInfo.getUiId());
        }
        //把新的父比赛ID更新到子比赛中
        if (childMatchIds !=null && childMatchIds.size()!=0 ) {
            matchInfoDb.setMiType(2);
            matchInfoDb.setMiIsEnd(1);
            MatchInfo childMatchInfo;
            List<Long> fatherMatchIds;
            for (int i=0;i<childMatchIds.size(); i++){
                childMatchInfo = getMatchById(childMatchIds.get(i));
                fatherMatchIds = getLongIdListReplace(childMatchInfo.getMiFatherMatchIds());
                fatherMatchIds.remove(matchId);
                fatherMatchIds.add(matchId);
                childMatchInfo.setMiFatherMatchIds(org.apache.commons.lang.StringUtils.join( fatherMatchIds.toArray(),","));
                matchDao.save(childMatchInfo);
            }
        } else {
            matchInfoDb.setMiType(1);
            if (oldChildMatchIds !=null && oldChildMatchIds.size()!=0 ) {
                matchInfoDb.setMiIsEnd(0);
            }
        }
        //对于被本比赛删掉的子比赛，把父比赛ID从这些子比赛删除
        oldChildMatchIds.removeAll( childMatchIds);
        if (oldChildMatchIds !=null &&oldChildMatchIds.size()!=0 ) {
            MatchInfo childMatchInfo;
            List<Long> fatherMatchIds;
            for (int i=0;i<oldChildMatchIds.size(); i++){
                childMatchInfo = getMatchById(oldChildMatchIds.get(i));
                fatherMatchIds = getLongIdListReplace(childMatchInfo.getMiFatherMatchIds());
                fatherMatchIds.remove(matchId);
                childMatchInfo.setMiFatherMatchIds(org.apache.commons.lang.StringUtils.join( fatherMatchIds.toArray(),","));
                matchDao.save(childMatchInfo);

                //没父比赛了，就把Teammatchpoint 表中该比赛的记录删除
                if ( fatherMatchIds !=null && fatherMatchIds.size()!=0 ) {
                    matchDao.delTeamUserPoint(oldChildMatchIds.get(i),null,0l);
                }
            }
        }

        return matchInfoDb;
	}


    /**
     * 如果比赛更改了球场，如果已经记过分数的话，改变match_score 中的球场数据
     * 主要是改每个洞的标准杆，并根据新的标准杆加上杆差，求出新的每洞杆数
     */

	public void updatePark(MatchInfo matchInfo,Long parkId){
	    Long matchId = matchInfo.getMiId();
        String ppName =  matchInfo.getMiZoneBeforeNine();
	    //获得前后场的球洞信息
        List<Map<String, Object>> beforeParkHoleList = matchDao.getParkPartitionListUpdate(parkId, ppName);
        ppName =  matchInfo.getMiZoneAfterNine();
        List<Map<String, Object>> afterParkHoleList = matchDao.getParkPartitionListUpdate(parkId,ppName);
        Integer holeNum;
        Integer holeStandardRod;
        String holeName;
        //更改前九洞的信息
        for ( Map<String, Object> hole: beforeParkHoleList){
            holeNum =  getIntegerValue(hole,"holeNum");
            holeName = getName(hole,"ppName");
            holeStandardRod =  getIntegerValue(hole,"holeStandardRod");
            matchDao.updateMatchScorePark(matchId,0,holeName, holeNum, holeStandardRod);
        }
        //更改后九洞的信息
        for ( Map<String, Object> hole: afterParkHoleList){
            holeNum =  getIntegerValue(hole,"holeNum");
            holeName = getName(hole,"ppName");
            holeStandardRod =  getIntegerValue(hole,"holeStandardRod");
            matchDao.updateMatchScorePark(matchId,1,holeName, holeNum, holeStandardRod);
        }
    }
	/**
	 * 创建比赛-保存-自动成为赛长
	 * 报名期间都可以改，但比赛开始后，就只能改 观战范围，成绩上报 和 比赛说明。
	 * @return
	 */
	public MatchInfo saveMatchInfo(MatchInfo matchInfo, String parkName, String chooseTeamId, String openid) {
		UserInfo myUserInfo = userService.getUserInfoByOpenId(openid);
		ParkInfo parkInfo = matchDao.getParkIdByName(parkName);

		//新增
		if (parkInfo != null) {
			matchInfo.setMiParkId(parkInfo.getPiId());
		}
        matchInfo.setMiParkName(parkName);
		if(StringUtils.isEmpty(matchInfo.getMiJoinTeamIds())){
			//公开
			matchInfo.setMiJoinOpenType(1);
		}else{
			//队内
			matchInfo.setMiJoinOpenType(2);
		}
		//处理关联比赛信息和比赛类型
        List<Long> childMatchIds = getLongIdListReplace(matchInfo.getMiChildMatchIds());
		if (childMatchIds == null || childMatchIds.size() ==0 ) {
            matchInfo.setMiType(1);
            //0：报名中  1进行中  2结束
            matchInfo.setMiIsEnd(0);
        } else {
            matchInfo.setMiType(2);
            //0：报名中  1进行中  2结束
            matchInfo.setMiIsEnd(1);
            //把本比赛的id 存到各个子比赛的fatherMathids字段

        }

		matchInfo.setMiCreateTime(System.currentTimeMillis());
		matchInfo.setMiCreateUserId(myUserInfo.getUiId());
		matchInfo.setMiCreateUserName(myUserInfo.getUserName());
		matchInfo.setMiIsValid(1);

		matchDao.save(matchInfo);

		//把本比赛ID保存到子比赛中
        Long matchId = matchInfo.getMiId();
        if (childMatchIds != null && childMatchIds.size() != 0 && matchId !=null ) {
            MatchInfo childMatchInfo;
            List<Long> fatherMatchIds;
            for (int i = 0; i < childMatchIds.size(); i++) {
                childMatchInfo = getMatchById(childMatchIds.get(i));
                fatherMatchIds = getLongIdListReplace(matchInfo.getMiFatherMatchIds());
                fatherMatchIds.remove(matchId);
                fatherMatchIds.add(matchId);
                childMatchInfo.setMiFatherMatchIds(org.apache.commons.lang.StringUtils.join(fatherMatchIds.toArray(), ","));
                matchDao.save(childMatchInfo);
            }
        }

		//创建比赛分组
		MatchGroup matchGroup = new MatchGroup();
		matchGroup.setMgMatchId(matchInfo.getMiId());
		matchGroup.setMgGroupName("1");
		matchGroup.setMgCreateTime(System.currentTimeMillis());
		matchGroup.setMgCreateUserId(myUserInfo.getUiId());
		matchGroup.setMgCreateUserName(myUserInfo.getUserName());
		matchDao.save(matchGroup);

		//创建用户分组mapping 创建人自动成为赛长
		MatchUserGroupMapping matchUserGroupMapping = new MatchUserGroupMapping();
		matchUserGroupMapping.setMugmMatchId(matchInfo.getMiId());
		if(StringUtils.isNotEmpty(chooseTeamId) && !chooseTeamId.equals("0") && !chooseTeamId.equals("null") && !chooseTeamId.equals("undefined")){
			matchUserGroupMapping.setMugmTeamId(Long.parseLong(chooseTeamId));
			Long tid = Long.parseLong(chooseTeamId);
			//我是不是这个球队的 申请中也算
			Long count = teamDao.getMeIsInTeam(myUserInfo.getUiId(),tid);
			if(count <= 0){
				//发送一条入队申请
				TeamInfo teamInfo = teamDao.get(TeamInfo.class,tid);
				TeamUserMapping teamUserMapping = new TeamUserMapping();
				teamUserMapping.setTumTeamId(tid);
				teamUserMapping.setTumUserId(myUserInfo.getUiId());
				//是否有加入审核(1、是（队长审批）  0、否)
				if(teamInfo.getTiJoinOpenType() == 1){
					teamUserMapping.setTumUserType(2);
				}else{
					teamUserMapping.setTumUserType(1);
				}
				teamUserMapping.setTumCreateUserId(myUserInfo.getUiId());
				teamUserMapping.setTumCreateTime(System.currentTimeMillis());
				teamUserMapping.setTumCreateUserName(myUserInfo.getUserName());
				matchDao.save(teamUserMapping);
			}
		}
		matchUserGroupMapping.setMugmUserType(0);
		matchUserGroupMapping.setMugmIsAutoCap(0);
		matchUserGroupMapping.setMugmIsDel(1);//创建人也先不放入第一组
		matchUserGroupMapping.setMugmGroupId(matchGroup.getMgId());
		matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
		matchUserGroupMapping.setMugmUserId(myUserInfo.getUiId());
		matchUserGroupMapping.setMugmUserName(myUserInfo.getUserName());
		matchUserGroupMapping.setMugmCreateUserId(myUserInfo.getUiId());
		matchUserGroupMapping.setMugmCreateUserName(myUserInfo.getUserName());
		matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
        matchUserGroupMapping.setMugmUpdateTime(System.currentTimeMillis());
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
					//自动设置的赛长
					mugm.setMugmIsAutoCap(1);
					//创建人默认在第一组，其他赛长先不放入待选球友中
					mugm.setMugmIsDel(1);
					mugm.setMugmGroupId(matchGroup.getMgId());
					mugm.setMugmGroupName(matchGroup.getMgGroupName());
					mugm.setMugmUserId(captainUserId);
					UserInfo userInfo_ = matchDao.get(UserInfo.class, captainUserId);
					mugm.setMugmUserName(userInfo_.getUserName());
					mugm.setMugmCreateUserId(myUserInfo.getUiId());
					mugm.setMugmCreateUserName(myUserInfo.getUserName());
					mugm.setMugmCreateTime(System.currentTimeMillis());
					matchDao.save(mugm);
				}
			}
		}

        //如果有上报队 每个上报队的队长自动成为赛长
        if (StringUtils.isNotEmpty(matchInfo.getMiReportScoreTeamId())) {
            List<Long> teamIdList = getLongTeamIdList(matchInfo.getMiReportScoreTeamId());
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
                    //自动设置的赛长
                    mugm.setMugmIsAutoCap(1);
                    //创建人默认在第一组，其他赛长先不放入待选球友中
                    mugm.setMugmIsDel(1);
                    mugm.setMugmGroupId(matchGroup.getMgId());
                    mugm.setMugmGroupName(matchGroup.getMgGroupName());
                    mugm.setMugmUserId(captainUserId);
                    UserInfo userInfo_ = matchDao.get(UserInfo.class, captainUserId);
                    mugm.setMugmUserName(userInfo_.getUserName());
                    mugm.setMugmCreateUserId(myUserInfo.getUiId());
                    mugm.setMugmCreateUserName(myUserInfo.getUserName());
                    mugm.setMugmCreateTime(System.currentTimeMillis());
                    matchDao.save(mugm);
                }
            }
        }
		return matchInfo;
	}

	/**
	 * 单练——开始记分——保存数据
	 *
	 * @return
	 */
	public Map<String, Object> saveSinglePlay(String matchTitle, Long parkId, String parkName, String playTime, Integer peopleNum, Integer openType, String digest,
											  String beforeZoneName, String afterZoneName, String openid) {
		Map<String, Object> result = new HashMap<>();

		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
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
        matchInfo.setMiMatchOpenType(openType);
		matchInfo.setMiDigest(digest);
		matchInfo.setMiJoinOpenType(3);
//		赛制1( 0:比杆 、1:比洞)
		matchInfo.setMiMatchFormat1(0);
//		赛制2( 0:个人 、1:双人)
		matchInfo.setMiMatchFormat2(0);
		matchInfo.setMiIsEnd(1);
		matchInfo.setMiCreateTime(System.currentTimeMillis());
		matchInfo.setMiCreateUserId(userInfo.getUiId());
		matchInfo.setMiCreateUserName(userInfo.getUserName());
		matchInfo.setMiIsValid(1);
		matchDao.save(matchInfo);

		//创建分组
		MatchGroup matchGroup = new MatchGroup();
		matchGroup.setMgMatchId(matchInfo.getMiId());
		matchGroup.setMgGroupName("1");
		matchGroup.setMgCreateUserId(userInfo.getUiId());
		matchGroup.setMgCreateUserName(userInfo.getUserName());
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
		matchUserGroupMapping.setMugmUserName(userInfo.getUserName());
		matchUserGroupMapping.setMugmCreateUserId(userInfo.getUiId());
		matchUserGroupMapping.setMugmCreateUserName(userInfo.getUserName());
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
			otherPeople.setMugmIsLinshi(1);
			otherPeople.setMugmIsDel(0);
			otherPeople.setMugmGroupId(matchGroup.getMgId());
			otherPeople.setMugmGroupName(matchGroup.getMgGroupName());
			otherPeople.setMugmUserName("球友" + i);
			otherPeople.setMugmCreateUserId(userInfo.getUiId());
			otherPeople.setMugmCreateUserName(userInfo.getUserName());
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
	public Map<String, Object> getMySinglePlay(String openid){
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> parp = TimeUtil.getThisDayTime();
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		parp.put("userId", userInfo.getUiId());
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
	public MatchGroup addGroupByTeamId(Long matchId, String openid){
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		//获取最大组
		Integer maxGroupName = matchDao.getMaxGroupByMatchId(matchId);
		if(maxGroupName !=null){
            maxGroupName++;
		}else{
            maxGroupName = 1;
        }
		MatchGroup group = new MatchGroup();
		group.setMgMatchId(matchId);
		group.setMgGroupName(maxGroupName.toString());
		group.setMgCreateUserId(userInfo.getUiId());
		group.setMgCreateUserName(userInfo.getUserName());
		group.setMgCreateTime(System.currentTimeMillis());
		matchDao.save(group);
		return group;
	}

	/**
	 * 比赛详情——赛长获取待分组人员不进行任何筛选，直接取所有待分组的
	 *
	 * @return
	 */
	public Map<String, Object> getWaitGroupUserList(Long matchId,String keyword) throws UnsupportedEncodingException {
		Map<String, Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class,matchId);
		//获取参赛队
		List<Long> joinTeamIds = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
		List<TeamUserBean> teamUserList = new ArrayList<>();
		if(joinTeamIds != null && joinTeamIds.size()>0){
			for(Long teamId:joinTeamIds){
				TeamUserBean bean = new TeamUserBean();
				//获取本参赛队的报名用户
				List<Map<String, Object>> userList = matchDao.getWaitGroupUserList(matchId,teamId, keyword);
				//用户昵称解码
				decodeUserNickName(userList);
				TeamInfo teamInfo = matchDao.get(TeamInfo.class,teamId);
				bean.setUserList(userList);
				if(bean.getTeamInfo() == null){
					bean.setTeamInfo(teamInfo);
				}
				teamUserList.add(bean);
			}
			result.put("teamUserList",teamUserList);
		}else{
			//公开赛 获取所有报名用户
			List<Map<String, Object>> userList = matchDao.getWaitGroupUserList(matchId, null, keyword);
			result.put("userList",userList);
		}
		result.put("matchInfo",matchInfo);
		return result;
	}

    /**
     * 比赛详情——赛长获取已报名人员——按球队分组
     *不包括自动分配的赛长
     * @return
     */
    public Map<String, Object> getApplyUserByMatchId(Long matchId,String keyword) throws UnsupportedEncodingException {
		Map<String, Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class,matchId);
		//获取参赛队
		List<Long> joinTeamIds = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
		List<TeamUserBean> teamUserList = new ArrayList<>();
		if(joinTeamIds != null && joinTeamIds.size()>0){
			for(Long teamId:joinTeamIds){
				TeamUserBean bean = new TeamUserBean();
				//获取本参赛队的报名用户 不包括自动分配的赛长
				List<Map<String, Object>> userList = matchDao.getUserListByMatchTeamId(matchId,teamId, keyword);
				//用户昵称解码
				decodeUserNickName(userList);
				TeamInfo teamInfo = matchDao.get(TeamInfo.class,teamId);
				bean.setUserList(userList);
				if(bean.getTeamInfo() == null){
					bean.setTeamInfo(teamInfo);
				}
				teamUserList.add(bean);
			}
			result.put("teamUserList",teamUserList);
		}else{
			//公开赛 获取所有报名用户
			List<Map<String, Object>> userList = matchDao.getApplyUserIdList(matchId, keyword);
			result.put("userList",userList);
		}
		result.put("matchInfo",matchInfo);
		return result;
    }

	/**
	 * 比赛详情——赛长删除本组用户——获取本组用户 不包括已报名的
	 *
	 * @return
	 */
	public Map<String, Object> getUserListByGroupId(Long matchId, Long groupId) throws UnsupportedEncodingException {
		Map<String, Object> result = new HashMap<>();
		//本组人数
		Long count = matchDao.getGroupUserCountById(matchId, groupId);
		result.put("userCount", count);
		List<Map<String, Object>> userList = matchDao.getUserListByGroupId(matchId,null,groupId,0);
		//解码用户昵称
		decodeUserNickName(userList);
		result.put("userList", userList);
		return result;
	}

	/**
	 * 比赛详情——赛长 添加用户至分组
	 * @param checkIds:选中的id（mappingid或者用户id）
	 * @return
	 */
	public void updateGroupUserByMatchIdGroupId(Long matchId, Long groupId, JSONArray checkIds, String openid){
        MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		UserInfo captainUserInfo = userService.getUserInfoByOpenId(openid);
        MatchGroup matchGroup = matchDao.get(MatchGroup.class,groupId);
//	    JSONArray checkIdsArray = JSONArray.fromObject(checkIds);
        if(checkIds != null && checkIds.size() > 0) {
            for (int i = 0; i < checkIds.size(); i++) {
                JSONObject jsonObject = (JSONObject) checkIds.get(i);
                Long userId = null;
                Long teamId = null;
                if (jsonObject.get("userId") != null) {
                    userId = Long.parseLong(jsonObject.get("userId").toString());
                }
                if (jsonObject.get("teamId") != null) {
                    teamId = Long.parseLong(jsonObject.get("teamId").toString());
                }
                //查询用户是否存在
                MatchUserGroupMapping matchUserGroupMapping = matchDao.getIsInMatchUserMapping(matchId, null, userId);
                if (matchUserGroupMapping == null) {
                    //新增
                    matchUserGroupMapping = new MatchUserGroupMapping();
                    matchUserGroupMapping.setMugmMatchId(matchId);
                    if (teamId != null) {
                        matchUserGroupMapping.setMugmTeamId(teamId);
                    }
                    matchUserGroupMapping.setMugmUserType(1);
                    matchUserGroupMapping.setMugmGroupId(groupId);
                    matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
                    matchUserGroupMapping.setMugmIsDel(0);
                    matchUserGroupMapping.setMugmUserId(userId);
                    UserInfo userInfo = matchDao.get(UserInfo.class, userId);
                    matchUserGroupMapping.setMugmUserName(userInfo.getUserName());
                    matchUserGroupMapping.setMugmCreateUserId(captainUserInfo.getUiId());
                    matchUserGroupMapping.setMugmCreateUserName(captainUserInfo.getUserName());
                    matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
                    matchUserGroupMapping.setMugmUpdateTime(System.currentTimeMillis());
                    matchDao.save(matchUserGroupMapping);
                } else {
                    //更新
                    if (teamId != null) {
                        matchUserGroupMapping.setMugmTeamId(teamId);
                    }
                    //如果是自动分配的赛长，就取消自动分配
                    if (matchUserGroupMapping.getMugmUserType() == 0 &&
                            matchUserGroupMapping.getMugmIsAutoCap() != null && matchUserGroupMapping.getMugmIsAutoCap() == 1) {
                        matchUserGroupMapping.setMugmIsAutoCap(0);
                    }
                    matchUserGroupMapping.setMugmGroupId(groupId);
                    matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
                    matchUserGroupMapping.setMugmIsDel(0);
                    matchUserGroupMapping.setMugmUpdateUserId(captainUserInfo.getUiId());
                    matchUserGroupMapping.setMugmUpdateUserName(captainUserInfo.getUserName());
                    matchUserGroupMapping.setMugmUpdateTime(System.currentTimeMillis());
                    matchDao.update(matchUserGroupMapping);
                }
                //查询是否在比分表有数据（对于从后台导入的比赛信息，有得分记录，如果调整了分组，match_score表要同步调整分组）
                Long userMatchScoreCount = matchDao.getUserMatchScoreCountById(userId, matchId);
                if (userMatchScoreCount > 0) {
                    matchDao.updateGroupIdWithMatchScore(userId, matchId, groupId);
                }
            }
        //修改比赛信息钟的比赛人数
        Long userCount = matchDao.getAllMatchApplyUserCount(matchId);
        matchInfo.setMiPeopleNum(userCount.intValue());
        matchDao.update(matchInfo);
        }
	}


    /**
     * 报名——赛长 添加用户至报名表
     * @param
     * @return
     */
    public void addUserToApply(Long matchId,Long teamId,  String userName,  String openid){
        MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
        List<UserInfo> userInfo = userDao.getUserIdByRealName(userName);
        UserInfo myUserInfo = userService.getUserInfoByOpenId(openid);
        TeamUserMapping teamUserMapping = null;
        UserInfo user=null;
        Long userId = null;

        //如果没有这个名字的用户，先建一个
        if (userInfo.size() <1){
            user = new UserInfo();
            user.setUiType(2);
            user.setUiIsValid(1);
            user.setUiRealName(userName);
            user.setUiCreateTime(System.currentTimeMillis());
            user.setUiCreateUserId(myUserInfo.getUiId());
            user.setUiCreateUserName(myUserInfo.getUserName());
            userDao.save(user);
            userInfo.add(user);
            userId = user.getUiId();
        } else {
            userId = userInfo.get(0).getUiId();//如果没有球队信息，就找授权用户或第一个叫这个名字的人
            for (int i=0;i< userInfo.size(); i++) {
                if (userInfo.get(i).getUiOpenId() != null) {
                    userId = userInfo.get(i).getUiId();
                    break;
                }
            }
        }
      //找到这个参赛队的这个人
       for (int i=0;i< userInfo.size(); i++){
         teamUserMapping=  teamDao.getTeamUserMapping(teamId, userInfo.get(i).getUiId());
         if (teamUserMapping != null){
             userId = userInfo.get(i).getUiId();
            break;
         }
        }
       if (teamUserMapping == null && teamId !=null ) {
            //如果没找到，把第一个该名字的用户加入球队，发送一条入队申请
            userId = userInfo.get(0).getUiId();
            TeamInfo teamInfo = teamDao.get(TeamInfo.class, teamId);
            teamUserMapping = new TeamUserMapping();
            teamUserMapping.setTumTeamId(teamId);
            teamUserMapping.setTumUserId(userInfo.get(0).getUiId());
            //是否有加入审核(1、是（队长审批）  0、否)
            if (teamInfo.getTiJoinOpenType() == 1) {
                teamUserMapping.setTumUserType(2);
            } else {
                teamUserMapping.setTumUserType(1);
            }
            teamUserMapping.setTumCreateUserId(myUserInfo.getUiId());
            teamUserMapping.setTumCreateTime(System.currentTimeMillis());
            teamUserMapping.setTumCreateUserName(myUserInfo.getUserName());
            matchDao.save(teamUserMapping);
        }
        //让该用户加入比赛
        //如果是队长，设为设为赛长
        Long isCaptain = teamDao.isCaptainIdByTeamId(teamId, userId);
         Integer userType;
       if (isCaptain >0) {
           userType = 0;
       } else {
           userType = 1;
       }

       //如果是“我”,仍然保留是赛长
       if (userId == myUserInfo.getUiId()){
           userType = 0;
        }
        adminImportService.importMatchUserMap(matchId, teamId, "", userId, userName,userType,myUserInfo.getUiId(),myUserInfo.getUserName());

        //如果已有比赛成绩，跟新成绩信息
        matchDao.updateMatchScoreTeamId(matchId,userId,teamId);
       //修改比赛信息中的比赛人数
        Long userCount = matchDao.getAllMatchApplyUserCount(matchId);
        matchInfo.setMiPeopleNum(userCount.intValue());
        matchDao.update(matchInfo);

    }

    /**
     * 普通球友 换组 这个方法好像被applymatch替代了
     * @return
     */
    public void updateMatchGroupByUserId(Long matchId, Long groupId, String openid){
        UserInfo userInfo = userService.getUserInfoByOpenId(openid);
        MatchGroup matchGroup = matchDao.get(MatchGroup.class,groupId);
        Long time = System.currentTimeMillis();
        MatchUserGroupMapping matchUserGroupMapping = matchDao.getIsInMatchUserMapping(matchId, null, userInfo.getUiId());
        if(matchUserGroupMapping == null){
            //新增
            matchUserGroupMapping = new MatchUserGroupMapping();
            matchUserGroupMapping.setMugmMatchId(matchId);
            matchUserGroupMapping.setMugmUserType(1);
            matchUserGroupMapping.setMugmGroupId(groupId);
            matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
            matchUserGroupMapping.setMugmIsDel(0);
            matchUserGroupMapping.setMugmUserId(userInfo.getUiId());
            matchUserGroupMapping.setMugmUserName(userInfo.getUserName());
            matchUserGroupMapping.setMugmCreateUserId(userInfo.getUiId());
            matchUserGroupMapping.setMugmCreateUserName(userInfo.getUserName());
            matchUserGroupMapping.setMugmCreateTime(time);
            matchUserGroupMapping.setMugmUpdateTime(time);
            matchDao.save(matchUserGroupMapping);
        }else{
            //更新
            matchUserGroupMapping.setMugmGroupId(groupId);
            matchUserGroupMapping.setMugmGroupName(matchGroup.getMgGroupName());
            matchUserGroupMapping.setMugmIsDel(0);
            matchUserGroupMapping.setMugmUpdateUserId(userInfo.getUiId());
            matchUserGroupMapping.setMugmUpdateUserName(userInfo.getUserName());
            matchUserGroupMapping.setMugmUpdateTime(time);
            matchDao.update(matchUserGroupMapping);
        }
    }

        /**
         * 获取单人比杆赛记分卡
         *
         * @return
         */
	public Map<String, Object> getSingleRodScoreCardInfoByGroupId(Long matchId, Long groupId) throws UnsupportedEncodingException {
		Map<String, Object> result = new HashMap<>();
		List<MatchGroupUserScoreBean> list = new ArrayList<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		result.put("matchInfo", matchInfo);
		//本组用户
		List<Map<String, Object>> userList = null;
		if(matchInfo.getMiType() == 1){
			userList = matchDao.getUserListByScoreCard(matchId, groupId);
		}else{
			//单练
			userList = matchDao.getSingleUserListById(matchId, groupId);
		}
		//解码用户昵称
		decodeUserNickName(userList);
		//设置用户姓名
		setUserName(userList);
		result.put("userList", userList);

		//半场球洞
		List<Map<String, Object>> parkHoleList = matchDao.getParkPartitionList(matchId);

		//第一条记录（半场分区信息）
		MatchGroupUserScoreBean thBean = new MatchGroupUserScoreBean();
		thBean.setUserId(0L);
		thBean.setUserName("球洞球杆");
		thBean.setUserScoreList(parkHoleList);
		list.add(thBean);

        List<Map<String, Object>> totalScoreList = new ArrayList<>();
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
                List<Map<String, Object>> totalScore=matchDao.getTotalScoreWithUser(matchId, uiId);
                for (Map<String, Object>score : totalScore) {
                    totalScoreList.add(score);
                }
			}
		}
		result.put("parkHoleList", list);
        result.put("totalScoreList", totalScoreList);
		//总杆数
		Long totalRod = matchDao.getTotalRod(matchInfo);
		result.put("totalRod", totalRod);

		return result;
	}

	/**
	 * 保存或更新计分数据
	 * 对于两球的比杆或者比洞，由于同组同队只记一个成绩，所以需要队友复制成绩，除此之外各种赛制的记分都用这个方法进行记分
	 * 对两球和四球比杆同组最好成绩，在获取记分卡时计算并放入match_rod_result中
     * 对两球和四球比洞的逐洞结果，在获取记分卡时计算并放入match_hole_result中
	 * 不给上报球队单独记分,所有球队公用一套记分 nhq)
	 * @return
	 */
	synchronized public void saveOrUpdateScore(Long userId, Long matchId, Long groupId, Long holeId,
                                  String isUp, Integer rod, String rodCha,
								  Integer pushRod, Integer beforeAfter, String openid, String userIds) {
        UserInfo myUserInfo = userService.getUserInfoByOpenId(openid);
        MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
        MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId, groupId, userId);
        ParkPartition parkPartition = matchDao.get(ParkPartition.class, holeId);
        MatchGroup matchGroup = matchDao.get(MatchGroup.class, groupId);

        //该用户参赛代表队
        Long teamId = matchUserGroupMapping.getMugmTeamId();
        //参赛球队
        List<Long> joinTeamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());

        //如果是四人两球赛，需要把同队两个人的记分更新成一样的
        if (matchInfo.getMiMatchFormat2() == 1 ) {
            if ( matchGroup.getMgIsGuest()==null || matchGroup.getMgIsGuest()==0) {

                //获取本组本球队的用户list
                List<Map<String, Object>> userListByTeam = matchDao.getUserListByTeamId(matchId, teamId, groupId);
                if (userListByTeam != null && userListByTeam.size() > 0) {
                    for (Map<String, Object> user : userListByTeam) {
                        Long userId_ = getLongValue(user, "uiId");
                        UserInfo u = matchDao.get(UserInfo.class, userId_);
                        //查询是否有本洞的记分
                        MatchScore scoreDb = matchDao.getMatchScoreByIds(userId_, matchId, groupId, teamId, beforeAfter,
                                parkPartition.getppName(), parkPartition.getPpHoleNum());
                        if (scoreDb != null) {
                            //更新记分
                            updateScoreByUserTeam(scoreDb, myUserInfo, parkPartition, isUp, rod, rodCha, pushRod);

                        } else {
                            //新增记分
                            MatchScore score = saveScore(matchInfo, teamId, groupId, matchGroup.getMgGroupName(),
                                    userId_, u.getUserName(), myUserInfo, parkPartition,
                                    beforeAfter, isUp, rod, rodCha, pushRod);
                        }
                    }
                }
            } else { //如果是助威组的情况
                //获取本组用户
                List<Map<String, Object>> userList = matchDao.getUserListByScoreCard(matchId, groupId);

                //看看本用户是第几个
                Integer partner=0;
                Long userId_=0L;
                for (int i=0;i< userList.size();i++){
                    userId_ = getLongValue(userList.get(i),"uiId");
                    if (userId.equals(userId_) ) {
                        partner=i;
                        break;
                    }
                }
                //计算搭档的序号
                 if ((partner+1)%2 ==0){
                     partner = partner-1;
                 } else {
                     partner = partner+1;
                 }

                 //给本用户记分

                UserInfo u = matchDao.get(UserInfo.class, userId);
                //查询是否有本洞的记分
                MatchScore scoreDb = matchDao.getMatchScoreByIds(userId, matchId, groupId, teamId, beforeAfter,
                        parkPartition.getppName(), parkPartition.getPpHoleNum());
                if (scoreDb != null) {
                    //更新记分
                    updateScoreByUserTeam(scoreDb, myUserInfo, parkPartition, isUp, rod, rodCha, pushRod);

                } else {
                    //新增记分
                    MatchScore score = saveScore(matchInfo, teamId, groupId, matchGroup.getMgGroupName(),
                            userId, u.getUserName(), myUserInfo, parkPartition,
                            beforeAfter, isUp, rod, rodCha, pushRod);
                }

                //给搭档记分
                if(partner <userList.size() && userList.size() >2) {
                    Long partnerUserId = getLongValue(userList.get(partner),"uiId");
                    u = matchDao.get(UserInfo.class, partnerUserId);
                    //查询是否有本洞的记分
                    scoreDb = matchDao.getMatchScoreByIds(partnerUserId, matchId, groupId, teamId, beforeAfter,
                            parkPartition.getppName(), parkPartition.getPpHoleNum());
                    if (scoreDb != null) {
                        //更新记分
                        updateScoreByUserTeam(scoreDb, myUserInfo, parkPartition, isUp, rod, rodCha, pushRod);

                    } else {
                        //新增记分
                        MatchScore score = saveScore(matchInfo, teamId, groupId, matchGroup.getMgGroupName(),
                                partnerUserId, u.getUserName(), myUserInfo, parkPartition,
                                beforeAfter, isUp, rod, rodCha, pushRod);
                    }
                }

            }
        }else{
            //查询是否有本洞的记分
            MatchScore scoreDb = matchDao.getMatchScoreByIds(userId, matchId, groupId, teamId, beforeAfter,
                    parkPartition.getppName(), parkPartition.getPpHoleNum());
            if (scoreDb != null) {
                updateScoreByUserTeam(scoreDb, myUserInfo, parkPartition, isUp, rod, rodCha, pushRod);
            } else {
                MatchScore score = saveScore(matchInfo, teamId, groupId, matchGroup.getMgGroupName(),
                        userId, matchUserGroupMapping.getMugmUserName(), myUserInfo, parkPartition,
                        beforeAfter, isUp, rod, rodCha, pushRod);

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
		UserInfo myUserInfo = userService.getUserInfoByOpenId(openid);
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		ParkPartition parkPartition = matchDao.get(ParkPartition.class,holeId);
		if(matchInfo.getMiMatchFormat2() == 1 && matchInfo.getMiMatchFormat1() == 0){
			if(StringUtils.isNotEmpty(userIds) && !"null".equals(userIds) && !"undefined".equals(userIds)){
				List<Long> userList = getLongTeamIdList(userIds);
				for(Long uId:userList) {
					UserInfo u = matchDao.get(UserInfo.class, uId);
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
								uId, u.getUserName(), myUserInfo, parkPartition,
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
	/* nhq
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
*/
	/**
	 * 将比分记录到上报球队中
	 */
/*	nhq
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
 	*/

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
        scoreDb.setMsUpdateUserName(myUserInfo.getUserName());
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
    	//查询是否有本组得分-为了设置开球洞
    	Long count = matchScoreDao.getStartHoleCountByGroupId(matchInfo.getMiId(), groupId);
		if(count == 0){
			//设置本组开球球洞
			MatchScoreStartHole scoreStartHole = new MatchScoreStartHole();
			scoreStartHole.setShMatchId(matchInfo.getMiId());
			scoreStartHole.setShGroupId(groupId);
			scoreStartHole.setShBeforeAfter(beforeAfter);
			scoreStartHole.setShHoleName(parkPartition.getppName());
			scoreStartHole.setShHoleNum(parkPartition.getPpHoleNum());
			scoreStartHole.setShCreateTime(System.currentTimeMillis());
			matchScoreDao.save(scoreStartHole);
		}
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

    public void getScore(MatchScore score, Integer holeStandardRod) {
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

		if(score.getMsPushRodNum() != null){
			if(score.getMsRodNum() - score.getMsPushRodNum() == holeStandardRod - 2){
				//标ON是计算出来的，如果某洞：杆数-推杆数=该洞标准杆数-2，则该洞为 标ON 3-1=4-2
				score.setMsIsOn(1);
			}
		}
	}

	/**
	 * 开始比赛 前检查分组是否合理
	 * state   0：报名中  1进行中  2结束
	 * 1、如果是多队双人比赛，不管比杆比洞，每组，每个队不超过两人，也可以是一人，每组最多两个队。生成记分卡时，只有一个队的两个人才能放入一行。
	 * 2、对于单人比洞，每组只能两个人，如果又是队式的，则一组的两个人要是两个队的
	 * 3、单人比杆赛分组不用有任何限制。
	 * 4、一个队队内，及多个队之间没法进行比洞赛
	 * 比洞赛的输赢组已经在获取比洞赛记分卡的时候进行了更新
	 * @return
	 */
	public String checkBeforeUpdateMatchState(Long matchId, Integer state) {

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
        //1. 两个队以上的比洞赛即视为队式比洞赛
		//2、对于单人比洞，每组只能两个人，如果又是队式的，则一组的两个人要是两个队的
		// 3、没队及一个队的比洞赛分组没有限制，比杆赛分组不用有任何限制。
		if(joinTeamIdList.size() >1){
			//单人比洞
			if(matchInfo.getMiMatchFormat1() == 1 && matchInfo.getMiMatchFormat2() == 0){
				//每组只能两个人
				//获取每组人数
				List<Map<String,Object>> userCountByEveGroup = matchDao.getUserCountWithEveGroupByMatchId(matchId);
				for(Map<String,Object> userCount:userCountByEveGroup){
					Long groupId = getLongValue(userCount,"groupId");
                    if (matchDao.groupIsGuest(groupId)>0) {continue;} //嘉宾组不检查
					String groupName = getName(userCount,"groupName");
					Integer countEveGroup = getIntegerValue(userCount,"count");
					if(countEveGroup != 2){
						return "第"+groupName+"组 人数不符合比赛要求，调整或者设为助威组。";
					}
					if(joinTeamIdList.size()>1){
						//队际赛，一组的两个人要是两个球队的 就判断每一组的球队个数，如果为1 说明这俩人是一队，不能开始比赛
						//获取本组的球队个数
						List<Map<String,Object>> teamCountByGroup = matchDao.getTeamCountByMatchId(matchId,groupId);
						if(getIntegerValue(teamCountByGroup.get(0),"count") !=2){
							return "第"+groupName+"组 球队数不符合比赛要求，调整或者设为助威组。";
						}
					}
					//该组重复的用户个数
					List<Object[]> count = matchDao.getHavingCountUserId(matchId,groupId);
					if(count != null &&  count.size() > 0){
						return "第"+groupName+"组 有重复球友，无法开始比赛。";
					}
				}
			}else if(matchInfo.getMiMatchFormat2() == 1 || matchInfo.getMiMatchFormat2() == 2 ){

				//1、如果是多队双人比赛，不管比杆比洞，每组最多两个队，每个队不超过两人，也可以是一人。生成记分卡时，只有一个队的两个人才能放入一行。
				if(joinTeamIdList.size() >= 2){
					//所有组球队个数
					for(Map<String,Object> teamCount :teamCountList){
						Long groupId = getLongValue(teamCount,"groupId");
						if (matchDao.groupIsGuest(groupId)>0) {continue;} //嘉宾组不检查

						String groupName = getName(teamCount,"groupName");
						Integer tCount = getIntegerValue(teamCount,"count");
						if(tCount >2 || ((matchInfo.getMiMatchFormat1() == 1 ||matchInfo.getMiMatchFormat1() == 2) && tCount == 1)){
							//双人比洞赛，每组2个队（一个队或者多个队打不了比洞赛）
							return "第"+groupName+"组 球队数不符合比赛要求，调整或者设为助威组。";
						}
						//获取本组每个球队的人数
						userCountListByTeam = matchDao.getUserCountByMatchId(matchId,groupId);
						for(Map<String,Object> userCount:userCountListByTeam){
							Integer c = getIntegerValue(userCount,"count");
							if(c >2 || (tCount == 1 && c<2)){
								return "第"+groupName+"组 人数不符合比赛要求，调整或者设为助威组。。";
							}
						}

						//该组重复的用户个数
						List<Object[]> count = matchDao.getHavingCountUserId(matchId,groupId);
						if(count != null &&  count.size() > 0){
							return "第"+groupName+"组 有重复球友，无法开始比赛。";
						}
					}
				}
			}
		} else {
		    //没有或者只有一个参赛队的比赛，就把每个组都自动设置为助威组，不然不知道怎么生成记分卡
            List<Map<String,Object>> userCountByEveGroup = matchDao.getUserCountWithEveGroupByMatchId(matchId);
            for(Map<String,Object> userCount:userCountByEveGroup) {
                Long groupId = getLongValue(userCount, "groupId");
                updateGroupIsGuest(groupId,1);
            }
        }

		return "true";
	}

	/**
	 * 开始比赛
	 * state   0：报名中  1进行中  2结束
	 *  更改比赛状态时，如果把比赛从结束改为比赛中为了改成绩，也要改变每组的状态，这个没有实现，因为现在不允许按组结束比赛了。nhq20210319
     * * 比洞赛的输赢组已经在获取比洞赛记分卡的时候进行了更新
     * 如果本比赛是某个比赛的子比赛，则在结束比赛state=2时把子比赛的比赛成绩从matchscore中复制到matchfatherscore 中
	 * @return
	 */
	public String updateMatchState(Long matchId, Integer state, String openid) {
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		Long userCount = matchDao.getMatchUserCountAlreadyGroup(matchId);
        matchInfo.setMiPeopleNum(userCount.intValue());
		matchInfo.setMiIsEnd(state);
		matchInfo.setMiUpdateUserId(userInfo.getUiId());
		matchInfo.setMiUpdateUserName(userInfo.getUserName());
		matchInfo.setMiUpdateTime(System.currentTimeMillis());
		matchDao.update(matchInfo);



		//记分截止时提交数据
        if(state ==2) {
            // 如果本比赛是某个比赛的子比赛，则在结束比赛时把本比赛的比赛成绩上报到teamuserpoint
            List<Long> fatherMatchIds = getLongIdListReplace(matchInfo.getMiFatherMatchIds());
            List<Long> childMatchIds = getLongIdListReplace(matchInfo.getMiChildMatchIds());
            List<MatchGroupUserScoreBean> list = new ArrayList<>();
            if (fatherMatchIds !=null && fatherMatchIds.size() !=0 && matchInfo.getMiType() !=2){
               // matchDao.scoreCopy(matchId);

                // 如果本比赛是某个比赛的子比赛 ，则在结束比赛把本比赛的个人总杆都保存到teamuserpoint 中
                List<Map<String, Object>>  userScoreList = matchDao.getUserListById(matchId,matchInfo.getMiType(),childMatchIds,null, null,0);
                updateScoreByRodSum(userInfo.getUiId(), matchId,  userScoreList);

                //  如果本比赛是某个比赛的子比赛 ，则在结束比赛把本比赛的个人净杆都保存到teamuserpoint 中
                matchScoreService.createNewUserNetScoreList(userScoreList,list,matchInfo);
                updatePointByRodSum(userInfo.getUiId(), matchId,  list);
            }
        }
		return "true";
	}

	/**
	 * 成绩上报 杆数越少 代表成绩越好 （注意球友加入球队是否成功）
	 * 我作为一个上报球队队长，可能给不同的参赛球队分配不同的积分
     * scoreType：0：球员积分，1：挑战赛球队积分，2:积分赛球队积分  nhq
     * 球员积分是拿每个参赛队和每个上报球队算交集，如果没有就忽略，如果有，就把交集的队员成绩交给上报球队并积分
         * @param baseScore 基础分,
         * @param rodScore  杆差倍数,
         * @param winScore  赢球奖分
     *  挑战赛球队积分是根据两队输赢计算两队积分
         * @param baseScore 输赢关系，1：主队赢，2：主队输,
         * @param rodScore  主队排名,
         * @param winScore  客队排名
         * @param baseScore 输赢关系，3：打平
         * @param rodScore  主队得分,
         * @param winScore  客队得分
     *  积分赛球队积分是根据各队排名计算各队积分
         * @param baseScore  第一名得分
         * @param rodScore   第二名得分,
         * @param winScore   递减分差
	 * @return
	 */
	public void submitScoreByTeamId(Long matchId, Long teamId,Long guestTeamId,Long reportteamId,  Double baseScore,
									   Integer rodScore, Integer winScore,Integer scoreType, Integer mingci,String openid) {
	    if (guestTeamId == null ) { guestTeamId =reportteamId;}
        Double teamPoint=0.0;
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		Long captainUserId = userInfo.getUiId();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
        List<Long> childMatchIds = getLongIdListReplace(matchInfo.getMiChildMatchIds());
		/*//我是否是选中的球队队长，页面上其实已经做过判断，为了保险，此处再校验一次
		Long isTeamCaptain = teamService.getIsCaptain(captainUserId, teamId);
		//参赛球队
		List<Long> joinTeamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
		//上报球队
		List<Long> reportTeamIdList = getLongTeamIdList(matchInfo.getMiReportScoreTeamId());

		if(isTeamCaptain >0){
		以上这段代码没用了
		*/

			//是否是参赛球队还是上报球队，创建比赛时，参赛球队和上报球队不可能重复
			//if(joinTeamIdList.contains(teamId)){  nhq
			if (teamId == reportteamId) {
				//参赛球队和上报队一样，给球员向本队积分
				//计算球友积分（一场比赛对应一次积分）
				//calculateScore(captainUserId, matchInfo,teamId, ,null, scoreType, baseScore, rodScore, winScore, 0);   nhq
				calculateScore(captainUserId, matchInfo,teamId, reportteamId,null, baseScore, rodScore, winScore, 0);
				// else if(reportTeamIdList.contains(teamId)){  nhq
                saveOrUpdateConfig(matchId, teamId,guestTeamId, reportteamId,baseScore, rodScore, winScore, teamPoint,userInfo,scoreType);
			}else {
			    Integer format3=matchInfo.getMiMatchFormat3();
			    if (format3==null){ format3=0;}
                if (scoreType == 0) {
                    //是参赛队向其他上报球队给球员积分
                    //筛选参赛队员中即属于该参赛球队又属于该上报球队的队员 nhq
                    //List<Long> userIdList = matchDao.getScoreUserList(matchId,teamId);
                    List<Long> userIdList = matchDao.getScoreUserList(matchInfo.getMiType(), matchId, childMatchIds,teamId);
                    List<Long> reportuserIdList = matchDao.getTeamUserIdList(reportteamId);

                    //以前的上报逻辑是只有已经加入过上报球队的人才能上报成绩，现改为如果未加入上报球队，自动加入上报球队。
                     //userIdList.retainAll(reportuserIdList);
                    //计算差集，也就是找到本球队还没有加入上报球队的人员列表
                    List<Long> userIdListToJoin =  new ArrayList<>();
                    userIdListToJoin.addAll(userIdList);
                    userIdListToJoin.removeAll(reportuserIdList);
                    //把上述这些人的真实用户自动加入上报球队,但没处理同名的情况
                    for ( Long userId: userIdListToJoin ) {
                        UserInfo uInfo = userDao.getUserInfoByUserId(userId);
                        String uopenId = uInfo.getUiOpenId();
                        if (uopenId !=null){
                            teamService.joinOrQuitTeamById(reportteamId, 0, uopenId);
                        } else {
                            userIdList.remove(userId); //虚拟用户不上报积分
                        }
                    }


                    //计算球友积分（一场比赛对应一次积分）
                    //calculateScore(captainUserId, matchInfo,teamId,null, scoreType, baseScore, rodScore, winScore, 0);   nhq
                    calculateScore(captainUserId, matchInfo, teamId, reportteamId, userIdList,  baseScore, rodScore, winScore, 1);
                    saveOrUpdateConfig(matchId, teamId, guestTeamId,reportteamId,baseScore, rodScore, winScore, teamPoint,userInfo,scoreType);
                } else if (scoreType == 2 ) {
                    //积分赛球队积分,包括单人和双人比杆

                    Map<String, Object> result = new HashMap<>();
                    List<Long> joinTeamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
                    //获得比杆赛参赛球队成绩排名
                    rodMatch(joinTeamIdList, mingci, matchId, matchInfo, result);
                    if (result != null) {
                        List<MatchTeamRankingBean> scoreList = (List<MatchTeamRankingBean>) result.get("scoreList");
                        //第一名
                        teamId = scoreList.get(0).getTeamId();
                        teamPoint = baseScore;
                        saveOrUpdateConfig(matchId, teamId, guestTeamId, reportteamId, baseScore, rodScore, winScore, teamPoint, userInfo, scoreType);
                        //第二名
                        if (scoreList.size() > 1) {
                            teamId = scoreList.get(1).getTeamId();
                            teamPoint = rodScore + 0d;
                            saveOrUpdateConfig(matchId, teamId, guestTeamId, reportteamId, baseScore, rodScore, winScore, teamPoint, userInfo, scoreType);
                        }
                        for (int i = 2; i < scoreList.size(); i++) {
                            teamId = scoreList.get(i).getTeamId();
                            teamPoint = rodScore + 0d - (i - 1) * winScore;
                            saveOrUpdateConfig(matchId, teamId, guestTeamId, reportteamId, baseScore, rodScore, winScore, teamPoint, userInfo, scoreType);

                        }
                    }
               } else {
			        //挑战赛球队积分
                    //获取两球队实际参赛人数
                    List<Long> joinTeamIdList = new  ArrayList<>();
                    joinTeamIdList.add(teamId);
                    joinTeamIdList.add(guestTeamId);
                    List<Map<String,Object>> joinMatchUserList = matchDao.getJoinMatchUserList(matchId,joinTeamIdList,matchInfo.getMiType(),childMatchIds);
                   //只计算前两个队的挑战赛积分
                    Integer userCount1;
                    Integer userCount2;
                    Double userPoint;
                    if (joinTeamIdList.get(0) ==teamId) {
                        userCount1 = getIntegerValue(joinMatchUserList.get(0), "userCount");
                        userCount2 = getIntegerValue(joinMatchUserList.get(1), "userCount");
                    }else {
                        userCount2= getIntegerValue(joinMatchUserList.get(0), "userCount");
                        userCount1 = getIntegerValue(joinMatchUserList.get(1), "userCount");
                    }
                    if(baseScore ==1) { //主队赢
                        //主队得分
                        if ((userCount1 - userCount2)>0) {
                            userPoint = (userCount1 - userCount2) * 0.5;
                        } else  userPoint = 0d;
                         teamPoint = 40 + (rodScore - winScore) * 3 + userPoint;
                         saveOrUpdateConfig(matchId, teamId, guestTeamId,reportteamId,baseScore, rodScore, winScore, teamPoint,userInfo,scoreType);
                         //客队得分
                         teamPoint = 10d ;
                          saveOrUpdateConfig(matchId, guestTeamId,teamId,reportteamId,2d, winScore,rodScore,  teamPoint,userInfo,scoreType);
                        } else if(baseScore ==2){ //主队输
                        //主队得分
                        teamPoint = 10d ;
                        saveOrUpdateConfig(matchId,teamId, guestTeamId,reportteamId,baseScore, rodScore, winScore, teamPoint,userInfo,scoreType);
                        //客队得分
                        if ((userCount2 - userCount1)>0) {
                            userPoint = (userCount2 - userCount1) * 0.5;
                        } else  userPoint = 0d;
                        teamPoint = 40 + (winScore-rodScore) * 3 + userPoint;
                        saveOrUpdateConfig(matchId, guestTeamId,teamId,reportteamId,1d, winScore,rodScore, teamPoint,userInfo,scoreType);
                        } else { //指定两队比分
                        teamPoint = rodScore+0d;
                        saveOrUpdateConfig(matchId,teamId, guestTeamId,reportteamId,baseScore, rodScore, winScore, teamPoint,userInfo,scoreType);
                        teamPoint = winScore+0d;
                        saveOrUpdateConfig(matchId,guestTeamId,teamId, reportteamId,baseScore, rodScore, winScore, teamPoint,userInfo,scoreType);
                    }

             }
        }

	}

	/**
	 * 成绩上报——计算球友积分 teamId 参赛球队id,上报球队id
	 * （一场比赛对应一次积分）
	 * teamType：0：参赛队与上报队是一个，1：参赛队和上报队不是一个
	 * teamType：0：向本队积分，1：向上报队积分  nhq
	 * fotmat:0 比杆赛，1：比洞赛
	 * 增加reportteamId 参数 nhq
	 */
	private void calculateScore(Long captainUserId, MatchInfo matchInfo, Long teamId,Long reportteamId, List<Long> userIdList,  Double baseScore,
								Integer rodScore, Integer winScore, Integer teamType) {
		Integer format = matchInfo.getMiMatchFormat1();
		boolean flag = false;
		/*
		//以下方法是比杆和比洞赛积分方法不同的算法
		if (format == 0) {
			//比杆赛
			//积分“杆差倍数”和“赢球奖分”只能二选  球友积分=基础积分+（110-球友比分）*杆差倍数
			//或者 球友积分=基础积分+赢球奖分/比赛排名
			flag = updatePointByRodScore(captainUserId, matchInfo.getMiId(), teamId, reportteamId,userIdList,  baseScore, rodScore, winScore, teamType);
		} else if (format == 1) {
			//比洞赛
			// 比洞赛积分，只计算“基础积分”和“赢球奖分”两项，其中输球的组赢球奖分为0，打平的为一半。
			// 计算公式为：球友积分=基础积分+赢球奖分
			flag = updatePointByHoleScore(captainUserId, matchInfo, teamId, reportteamId,userIdList,baseScore, winScore, teamType);
		}
		*/
        flag = updatePointByRodScore(captainUserId, matchInfo, teamId, reportteamId,userIdList,  baseScore, rodScore, winScore, teamType);

		if(flag){
			//将该组的得分成绩标为已确认 nhq
			if (teamType==0){
			      matchDao.updateMatchScoreById(matchInfo.getMiId(),teamId,userIdList);}
			else{ matchDao.updateMatchScoreById(matchInfo.getMiId(),reportteamId,userIdList);}
		}
	}

	/**
	 * 成绩上报——保存参赛球队 或 上报球队 的成绩提交的积分计算配置 允许重复上报
	 * 即integral.config中保存的 球队比赛榜的内容，增加参数reportteamId  nhq
	 * 我作为一个上报球队队长，可能给不同的参赛球队分配不同的积分
	 * scoreType:0 球员积分  1：球队积分
	 */
	private void saveOrUpdateConfig(Long matchId,Long teamId,Long guestTeamId,Long reportteamId,Double baseScore, Integer rodScore,
										Integer winScore, Double teamPoint,UserInfo userInfo,Integer scoreType) {
		String userName = userInfo.getUserName();
		IntegralConfig config = matchDao.getSubmitScoreConfig(matchId,teamId,guestTeamId,reportteamId,scoreType);
		if(config == null){
			config = new IntegralConfig();
			config.setIcMatchId(matchId);
			config.setIcTeamId(teamId);
            config.setIcGuestTeamId(guestTeamId);
			config.setIcReportTeamId(reportteamId);
			config.setIcBaseScore(baseScore);
			config.setIcRodCha(rodScore);
			config.setIcWinScore(winScore);
            config.setIcTeamPoint(teamPoint);
			config.setIcCreateTime(System.currentTimeMillis());
			config.setIcCreateUserId(userInfo.getUiId());
			config.setIcCreateUserName(userName);
            config.setIcScoreType(scoreType);
			matchDao.save(config);
		}else{
			config.setIcBaseScore(baseScore);
			config.setIcRodCha(rodScore);
			config.setIcWinScore(winScore);
            config.setIcTeamPoint(teamPoint);
			config.setIcUpdateTime(System.currentTimeMillis());
			config.setIcUpdateUserId(userInfo.getUiId());
			config.setIcUpdateUserName(userName);
			matchDao.update(config);
		}
	}


	/**
	 * 成绩上报 比杆赛   （一场比赛对应一次积分）
	 * 积分“杆差倍数”和“赢球奖分”只能二选，scoreType 1,杆差倍数，其他，赢球奖分
	 * @param teamId 上报球队id或者本球队id
	 * @param teamType 0：参赛队，1：上报队
	 * 杆差倍数 ：球友积分=基础积分+（110-球友比分）*杆差倍数
	 * 赢球奖分 ：球友积分=基础积分+赢球奖分/比赛排名
	 * 增加参数 上报球队ID:Long reportteamId,   nhq
	 */
	private boolean updatePointByRodScore(Long captainUserId,MatchInfo matchInfo, Long teamId, Long reportteamId,List<Long> userIdList, Double baseScore,
									   Integer rodScore, Integer winScore, Integer teamType) {
	    Long matchId = matchInfo.getMiId();
        List<Long> childMatchIds = getLongIdListReplace(matchInfo.getMiChildMatchIds());
        Integer matchType = matchInfo.getMiType();
		if (rodScore > 0) {
			//杆差倍数 球友积分=基础积分+（110-球友比分）*杆差倍数
			//获取该队伍的得分情况
			List<Map<String, Object>> matchScoreList = matchDao.getSumScoreListByMatchIdTeamId(matchType,matchId, childMatchIds,teamId,userIdList, teamType);
			if (matchScoreList != null && matchScoreList.size() > 0) {
				for (Map<String, Object> scoreMap : matchScoreList) {
                    Integer scoreHoleCount = getIntegerValue(scoreMap, "holeCount");
                    if ( scoreHoleCount ==18) { //只有18洞成绩完整的才上报积分
                        //杆数
                        Integer score = getIntegerValue(scoreMap, "sumRodNum");
                        Long userId = getLongValue(scoreMap, "userId");
                        Double point = baseScore + (Const.DEFAULT_ROD_NUM - score) * rodScore;
                        //更新该球友原先的积分情况  （一场比赛对应一次积分）
                        //updatePointByIds(matchId, teamId, userId, captainUserId, point, 0);
                        //根据teamType的值，是0 向本队积分，是1 向上报球队积分   nhq
                        if (teamType == 0) {
                            updatePointByIds(matchId, teamId, reportteamId,userId, captainUserId, point, score, 0);
                        } else {
                            updatePointByIds(matchId,teamId, reportteamId, userId, captainUserId, point, score, 0);
                        }
                    }
					}
				return true;
			}
		} else {
			//赢球奖分 球友积分=基础积分+赢球奖分/比赛排名
			//计算杆数的总排名,待改
			List<Map<String, Object>> matchScoreList = matchDao.getRankingListByMatchId(matchId, teamId,userIdList, teamType);
			if (matchScoreList != null && matchScoreList.size() > 0) {
				for (int i=0;i<matchScoreList.size();i++) {
                    Map<String, Object> scoreMap = matchScoreList.get(i);
					Long teamIdByScore = getLongValue(scoreMap, "teamId");
					Long userId = getLongValue(scoreMap, "userId");
					Integer score = getIntegerValue(scoreMap, "sumRodNum");
					//排名
					Integer rank = i+1;
					//计算得分
					Double point = baseScore + winScore/rank;
					//计算得分(有小数)
					/*BigDecimal a = new BigDecimal(baseScore);
					BigDecimal b = new BigDecimal((float)winScore/rank);
					BigDecimal c = a.add(b);
					Double point = c.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();*/
					//更新该球友原先的积分情况
					updatePointByIds(matchId, teamIdByScore,reportteamId, userId, captainUserId, point, score,0);
				}
				return true;
			}
		}
		return false;
	}


	/**
	 * 撤销成绩上报
	 * @param matchId 比赛id,
	 * @param teamId 参赛球队id, nhq
	 * @param reportteamId 上报球队id,
	 *  增加参数       reportteamId
	 *
	 * @return
	 */
	public boolean cancelScoreByTeamId(Long matchId, Long teamId,Long guestTeamId,Long reportteamId, Integer scoreType, String openid) {
		if(guestTeamId==null) {guestTeamId =reportteamId ;}
	    Long captainUserId = userService.getUserIdByOpenid(openid);
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		//我是否是选中的球队队长，页面上其实已经做过判断，为了保险，此处再校验一次 nhq
		//Long isTeamCaptain = teamService.getIsCaptain(captainUserId, teamId);
		//if(isTeamCaptain >0){

	      if (scoreType == 0 || scoreType == 1 ) {
              //球员积分以及球队挑战赛积分
               IntegralConfig config = matchDao.getSubmitScoreConfig(matchId,teamId,guestTeamId,reportteamId,scoreType);
               if(config == null){
                   return false;
               }
               //删除积分计算配置
               matchDao.del(config);

               //球员积分，删除球队队员的积分
               matchDao.delTeamUserPoint(matchInfo.getMiId(),teamId, reportteamId);

           } else {
	          //球队排位赛积分
              //获取参赛球队
              List<Long> joinTeamIds = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
              IntegralConfig config;
              if (joinTeamIds != null && joinTeamIds.size()>0 ) {
                  for (Long tId : joinTeamIds) {
                      config = matchDao.getSubmitScoreConfig(matchId, tId, guestTeamId, reportteamId, scoreType);
                      if (config!= null) {
                          //删除积分计算配置
                          matchDao.del(config);
                      }

                  }
              }
          }
			return true;
			}


	/**
	 * 更新该球友原先的比赛积分情况  （一场比赛对应一次积分）
	 * 如果是上报球队队长，作为一个上报球队队长，可能给不同的参赛球队分配不同的积分
	 * type: 0:加积分   1：减积分
	 */
	private void updatePointByIds(Long matchId, Long teamId, Long reportTeamId, Long userId, Long captainUserId,Double point, Integer score,Integer type) {
		UserInfo captainUserInfo = matchDao.get(UserInfo.class,captainUserId);
		TeamUserPoint teamUserPoint = matchDao.getTeamUserPoint(matchId, teamId,reportTeamId, userId, captainUserId);
		if(type == 0){
			//加积分 或者 修改积分
			if(teamUserPoint == null){
				teamUserPoint = new TeamUserPoint();
				teamUserPoint.setTupMatchId(matchId);
				teamUserPoint.setTupTeamId(teamId);
                teamUserPoint.setTupReportTeamId(reportTeamId);
				teamUserPoint.setTupUserId(userId);
				if(point>=0){teamUserPoint.setTupMatchPoint(point);}
				if(score>=0){teamUserPoint.setTupMatchScore(score);}
				teamUserPoint.setTupCreateUserId(captainUserId);
				teamUserPoint.setTupCreateUserName(captainUserInfo.getUserName());
				teamUserPoint.setTupCreateTime(System.currentTimeMillis());
				matchDao.save(teamUserPoint);
			}else{
				if(point>=0){teamUserPoint.setTupMatchPoint(point);}
				if(score>=0){teamUserPoint.setTupMatchScore(score);}
				teamUserPoint.setTupUpdateUserId(captainUserId);
				teamUserPoint.setTupUpdateUserName(captainUserInfo.getUserName());
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
     * 比赛结束时，  把用户的总杆和净杆保存到积分表teamuserpoint中
     *
     */
    private void updateScorePointByIds(Long matchId, String matchName,Long teamId,String teamAbbrev, Long userId,String userName,String userHeadimg,Long groupId,Integer holeCount, Long captainUserId,Double point, Integer score) {
        UserInfo captainUserInfo = matchDao.get(UserInfo.class,captainUserId);
        TeamUserPoint teamUserPoint = matchDao.getTeamUserPoint(matchId, teamId,0l, userId, captainUserId);

            //加积分 或者 修改积分
            if(teamUserPoint == null){
                teamUserPoint = new TeamUserPoint();
                teamUserPoint.setTupMatchId(matchId);
                teamUserPoint.setTupMatchName(matchName);
                teamUserPoint.setTupTeamId(teamId);
                teamUserPoint.setTupTeamAbbrev(teamAbbrev);
                teamUserPoint.setTupUserId(userId);
                teamUserPoint.setTupUserName(userName);
                teamUserPoint.setTupUserHeadimg(userHeadimg);
                teamUserPoint.setTupGroupId(groupId);
                teamUserPoint.setTupHoleCount(holeCount);
                teamUserPoint.setTupReportTeamId(0l);
                if(point>-100){teamUserPoint.setTupMatchPoint(point);}
                if(score>-100){teamUserPoint.setTupMatchScore(score);}
                teamUserPoint.setTupCreateUserId(captainUserId);
                teamUserPoint.setTupCreateUserName(captainUserInfo.getUserName());
                teamUserPoint.setTupCreateTime(System.currentTimeMillis());
                matchDao.save(teamUserPoint);
            }else{
                teamUserPoint.setTupMatchId(matchId);
                teamUserPoint.setTupMatchName(matchName);
                teamUserPoint.setTupTeamId(teamId);
                teamUserPoint.setTupTeamAbbrev(teamAbbrev);
                teamUserPoint.setTupUserId(userId);
                teamUserPoint.setTupUserName(userName);
                teamUserPoint.setTupUserHeadimg(userHeadimg);
                teamUserPoint.setTupGroupId(groupId);
                teamUserPoint.setTupHoleCount(holeCount);
                teamUserPoint.setTupReportTeamId(0l);
                if(point>-100){teamUserPoint.setTupMatchPoint(point);}
                if(score>-100){teamUserPoint.setTupMatchScore(score);}
                teamUserPoint.setTupUpdateUserId(captainUserId);
                teamUserPoint.setTupUpdateUserName(captainUserInfo.getUserName());
                teamUserPoint.setTupUpdateTime(System.currentTimeMillis());
                matchDao.update(teamUserPoint);
            }
     }


    /**
	 * 球员成绩上报 比洞赛
	 * 积分只计算“基础积分”和“赢球奖分”两项，其中输球的组赢球奖分为0，打平的为一半。
	 * 计算公式为：球友积分=基础积分+赢球奖分
	 * @param teamType 0：参赛队，1：上报队
	 *  增加参数   reportteamId     nhq
     *   需要考虑多队比洞赛的情况，这个还没有改，参照holematch 的改法，增加了个matchchildid   nhq
	 */
	private boolean updatePointByHoleScore(Long captainUserId,MatchInfo matchInfo, Long teamId,Long reportteamId, List<Long> userIdList, Double baseScore,
										   Integer winScore, Integer teamType) {
		//上报球友比赛成绩 nhq
        Long matchId = matchInfo.getMiId();
        List<Long> childMatchIds = getLongIdListReplace(matchInfo.getMiChildMatchIds());
        Integer matchType = matchInfo.getMiType();
		List<Map<String, Object>> matchScoreList = matchDao.getSumScoreListByMatchIdTeamId(matchType,matchId, childMatchIds,teamId,userIdList, teamType);
		if (matchScoreList != null && matchScoreList.size() > 0) {
			for (Map<String, Object> scoreMap : matchScoreList) {
				//杆数
				Integer score = getIntegerValue(scoreMap, "sumRodNum");
				Long userId = getLongValue(scoreMap, "userId");
				if (teamType == 0) {
					updatePointByIds(matchId, teamId,reportteamId, userId, captainUserId, -1d, score, 0);
				} else {
					updatePointByIds(matchId, teamId, reportteamId, userId, captainUserId, -1d, score, 0);
				}
			}
		}

	//以下计算和上报球友积分 nhq
		List<MatchHoleResult> matchHoleResultList = null;
		if(teamType == 0){
			//选的是参赛队
			//获取本队的输球组和赢球组
			matchHoleResultList = matchDao.getMatchHoleWinOrLoseList(matchId,teamId);
			if(matchHoleResultList != null && matchHoleResultList.size()>0){
				//是参赛球队
				Double point = 0d;
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
							updatePointByIds(matchId, teamId, reportteamId,userId, captainUserId, point, -1,0);
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
					List<Map<String,Object>> groupUserIdList = matchDao.getUserListByGroupIdInMatchScoreForReport(matchHoleResult.getMhrMatchId(),
							matchHoleResult.getMhrGroupId(),matchHoleResult.getMhrTeamId());
					if(groupUserIdList != null && groupUserIdList.size() >0){
						//如果存在，复制一份输赢数据给上报球队
						MatchHoleResult matchHoleResultForReport = new MatchHoleResult();
						BeanUtils.copyProperties(matchHoleResult,matchHoleResultForReport);
						//type=1 上报球队记分
						//matchHoleResultForReport.setMhrTeamId(teamId); nhq
						matchHoleResultForReport.setMhrTeamId(reportteamId);
						//查看是否有上报队输赢结果
						Long count = matchDao.getReportMatchHoleReslutCount(matchHoleResultForReport);
						if(count == 0){
							matchDao.save(matchHoleResultForReport);
						}

						Double point = 0d;
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
						for(Map<String,Object> map:groupUserIdList){
							Long userId = getLongValue(map,"userId");
							//更新该球友的积分情况
							//updatePointByIds(matchId, teamId, userId, captainUserId, point, 0); nhq
							 updatePointByIds(matchId, teamId,reportteamId, userId, captainUserId, point, -1,0);
						}
					}
				}
				return true;
			}
		}
        return false;
	}

	//把子比赛的球员总成绩存放到team_usr_point 中。这个在比赛点击“记分截止”时触发

    private boolean updateScoreByRodSum(Long captainUserId, Long matchId,  List<Map<String, Object>>matchScoreList) {

        //杆差倍数 球友积分=基础积分+（110-球友比分）*杆差倍数
        //获取该队伍的得分情况
        List<Map<String, Object>> newUserTeamIds = new ArrayList<>();
        if (matchScoreList != null && matchScoreList.size() > 0) {
            for (Map<String, Object> scoreMap : matchScoreList) {
                    //杆数
                    Long userId = getLongValue(scoreMap, "uiId");
                    Integer holeCount = getIntegerValue(scoreMap, "holeCount");
                    String userName = getName(scoreMap, "uiRealName");
                    String userHeadimg = getName(scoreMap, "uiHeadimg");
                    Long teamId = getLongValue(scoreMap, "team_id");
                    Long groupId = getLongValue(scoreMap, "group_id");
                    String teamAbbrev =getName(scoreMap, "teamAbbrev");
                    String matchName =getName(scoreMap, "matchName");
                    Integer score = getIntegerValue(scoreMap, "sumRodCha");
                    updateScorePointByIds(matchId, matchName,teamId,teamAbbrev, userId, userName,userHeadimg,groupId,holeCount,captainUserId, -100d, score);
                    Map<String, Object> userIdTeamId = new HashMap<>();
                    userIdTeamId.put("userId",userId);
                    userIdTeamId.put("teamId",teamId);
                    newUserTeamIds.add(userIdTeamId);
                }
            List<Map<String, Object>> oldUserTeamIds = matchDao.getMatchPointUserTeamIds(matchId);
            oldUserTeamIds.removeAll(newUserTeamIds);
            if (oldUserTeamIds!=null && oldUserTeamIds.size()>0){
                for (Map<String, Object> userTeam : oldUserTeamIds) {
                    Long userId = getLongValue(userTeam, "userId");
                    Long teamId = getLongValue(userTeam, "teamId");
                    matchDao.delUserPoint(matchId, userId,teamId);
                }
            }
       }
        return true;
    }

    private boolean updatePointByRodSum(Long captainUserId, Long matchId,   List<MatchGroupUserScoreBean> list) {

        //杆差倍数 球友积分=基础积分+（110-球友比分）*杆差倍数
        //获取该队伍的得分情况

        if (list != null && list.size() > 0) {
            for ( int i=0;i< list.size(); i++ ) {

                Long userId = list.get(i).getUserId();
                Integer holeCount = list.get(i).getHoleCount();
                String userName = list.get(i).getUserName();
                String userHeadimg = list.get(i).getUserHeadimg();
                Long teamId =  list.get(i).getTeamId();
                String teamAbbrev =  list.get(i).getTeamAbbrev();
                Long groupId =  list.get(i).getGroupId();
                String matchName =  list.get(i).getMatchName();

                Double point =  list.get(i).getTotalNetRodScore();


                updateScorePointByIds(matchId, matchName,teamId,teamAbbrev, userId, userName,userHeadimg,groupId,holeCount, captainUserId, point, -100);


            }
        }
        return true;
    }
	/**
	 * 结束单练
	 * @return
	 */
	public void endSingleMatchById(Long matchId, String openid) {
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		matchInfo.setMiIsEnd(2);
		matchInfo.setMiUpdateTime(System.currentTimeMillis());
		matchInfo.setMiUpdateUserId(userInfo.getUiId());
		matchInfo.setMiUpdateUserName(userInfo.getUserName());
		matchDao.update(matchInfo);
	}

	/**
	 * 如果不是参赛人员，则加入围观用户
     * 增加了每个人的观看次数 nhq
	 * @return
	 */
	public boolean saveOrUpdateWatch(MatchInfo matchInfo, String openid) {
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		Long userId = userInfo.getUiId();
		Long matchId = matchInfo.getMiId();
		//观战范围：3、封闭：参赛队员可见 并且不是参赛人员
		Long isJoinMatch = matchDao.getIsContestants(userId, matchId);

		if(matchInfo.getMiMatchOpenType() == 3 && isJoinMatch <=0){
			return false;
		}
        //是参赛人员
        if(isJoinMatch > 0){
            return true;
        }
		//不是参赛人员 是否已经围观
        Long watchCount = matchDao.getIsWatch(userId,matchId);
        if(watchCount > 0){
            //已经围观
            Long watchNum = matchDao.getWatchNum(userId,matchId);
            watchNum +=1;
            matchDao.updateWatchNum(userId,matchId,watchNum);
            return true;
        }
        //没有围观
        MatchJoinWatchInfo matchJoinWatchInfo = new MatchJoinWatchInfo();
        if(matchInfo.getMiMatchOpenType() == 1){
            //1、公开 球友均可见； 直接加入围观用户
            matchJoinWatchInfo.setMjwiUserId(userId);
            matchJoinWatchInfo.setMjwiMatchId(matchId);
            matchJoinWatchInfo.setMjwiType(0);
            matchJoinWatchInfo.setMjwiWatchNum(1l);
            matchJoinWatchInfo.setMjwiCreateTime(System.currentTimeMillis());
            matchDao.save(matchJoinWatchInfo);
            return true;
        }else if(matchInfo.getMiMatchOpenType() == 2) {
            //2、队内公开：参赛者的队友可见 查询是否是参赛球队的队员
            List<Long> teamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
            if (teamIdList != null && teamIdList.size() > 0) {
                Long joinCount = matchDao.getIsJoinTeamsUser(userId, teamIdList);
                if (joinCount > 0) {
                    //是参赛队的队员，加入围观用户
                    matchJoinWatchInfo.setMjwiUserId(userId);
                    matchJoinWatchInfo.setMjwiMatchId(matchId);
                    matchJoinWatchInfo.setMjwiType(0);
                    matchJoinWatchInfo.setMjwiWatchNum(1l);
                    matchJoinWatchInfo.setMjwiCreateTime(System.currentTimeMillis());
                    matchDao.save(matchJoinWatchInfo);
                    return true;
                }
            }
        }
        return false;
	}

	/**
	 * 比赛——group——总比分
	 * 罗列每个参赛球友的记分卡。其中的数字“蓝色是Par,红色是小鸟，灰色是高于标准杆的。黑色是老鹰”
	 * @param matchId 比赛id
	 * @return
	 */
	public Map<String, Object> getTotalScoreByMatchId(Long matchId, Integer page,Integer orderType) throws UnsupportedEncodingException {
		Map<String, Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		List<Long> childMatchIds = getLongIdListReplace(matchInfo.getMiChildMatchIds());
		result.put("matchInfo", matchInfo);
		List<MatchGroupUserScoreBean> list = new ArrayList<>();

		//所有球洞
		List<MatchTotalUserScoreBean> parkHoleList = new ArrayList<>();
		//每洞杆数
		List<MatchTotalUserScoreBean> parkRodList = new ArrayList<>();
		//获取球洞信息，如果是关联比赛的父比赛，就随便取第一个子比赛的场地
        Long parkMatchId;
        if ( matchInfo.getMiType() ==2 ){
            parkMatchId=childMatchIds.get(0);
        } else {
            parkMatchId = matchId;
        }
		List<Map<String, Object>> beforeParkHoleList = matchDao.getBeforeAfterParkPartitionList(parkMatchId,0);
		getNewParkHoleList(parkHoleList,parkRodList,beforeParkHoleList);
		//获取后半场球洞
		List<Map<String, Object>> afterParkHoleList = matchDao.getBeforeAfterParkPartitionList(parkMatchId,1);
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
/*
		//区分比赛是普通比赛还是个关联比赛的父比赛
        List<Map<String, Object>> userList =  new ArrayList<>();

        if ( matchInfo.getMiType()==2){
            //比赛的所有用户和其总杆数，为0的排后面(首列显示)
            List<Map<String, Object>> userList1 = null;
            MatchInfo matchInfo1;
            for(int i=0;i< childMatchIds.size();i++) {
                userList1 = matchDao.getUserListById(childMatchIds.get(i),null, null);//顺便按成绩排序,没记完整成绩排后面

                //用户昵称解码
                decodeUserNickName(userList1);
                //用户名
                setUserName(userList1);
                //本比赛用户每个洞得分情况
                matchInfo1=matchDao.get(MatchInfo.class, childMatchIds.get(i));
                createNewUserScoreList(userList1, list, matchInfo1);
                userList.addAll(userList1);
            }
        } else{
            //比赛的所有用户和其总杆数，为0的排后面(首列显示)

            //参赛球队
            //String joinTeamIds = matchInfo.getMiJoinTeamIds();
*/
        List<Map<String, Object>> userList;
        if (matchInfo.getMiType() ==2) {
        	int rowsPerPage = 50;
			if(page == 0){
				rowsPerPage = 0;
			}
			page = page != null && page > 0 ? page : 1;
			POJOPageInfo pageInfo = new POJOPageInfo<Map<String, Object>>(rowsPerPage , page);
            userList = matchDao.getUserScoreByMatchId(matchId,null,matchInfo.getMiType(), childMatchIds, pageInfo,orderType);
            //用户昵称解码
            decodeUserNickName(userList);
            //用户名
            setUserName(userList);
        } else {
            userList = matchDao.getUserListById(matchId, matchInfo.getMiType(), childMatchIds, null, null,orderType);//顺便按成绩排序,没记完整成绩排后面
            //用户昵称解码
            decodeUserNickName(userList);
            //用户名
            setUserName(userList);
            //本比赛用户每个洞得分情况
            createNewUserScoreList(userList, list, matchInfo);

        }

        result.put("userList", userList);
        result.put("list", list);
		return result;
	}

	//格式化用户半场得分
	public void createNewUserScore(List<MatchTotalUserScoreBean> userScoreList, List<Map<String, Object>> uScoreList) {
		Integer totalRod = 0;
		//杆差
		Integer totalRodCha = 0;
		for(Map<String, Object> map:uScoreList){
			MatchTotalUserScoreBean bean = new MatchTotalUserScoreBean();
			Integer rodNum = getIntegerValue(map,"rod_num");
			totalRod += rodNum;
			//杆数
			bean.setRodNum(rodNum);
			bean.setHoleStandardRod(getIntegerValue(map,"hole_standard_rod"));
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
		//筛选我是队长的上报球队
		if(reportTeamIdList != null && reportTeamIdList.size()>0){
			reportTeamIdList = matchDao.getMyTeamInfoList(reportTeamIdList,userId);
		}

		List<TeamInfo> teamList = new ArrayList<>();
		//1：我只是选中的参赛球队的队长，只给该参赛球队分配积分,teamList中只有选中参赛队
		if(isTeamCaptain > 0 && (reportTeamIdList == null || reportTeamIdList.size() <= 0)){
			TeamInfo teamInfo = matchDao.get(TeamInfo.class,teamId);
			teamList.add(teamInfo);
		}else if(isTeamCaptain <= 0 && reportTeamIdList != null && reportTeamIdList.size() > 0){
			//2：我只是某个上报球队队长,则计算两队交集并把交集队员提交上报球队并积分
			//如果我同时是多个上报球队的队长，让用户选择给哪个队积分，teamList中只有我是队长的上报队
			teamList = matchDao.getTeamListByIds(reportTeamIdList);
		}else if(isTeamCaptain > 0 && reportTeamIdList != null && reportTeamIdList.size() > 0){
			//3：我既是选中的参赛队队长，又是上报队队长，则让他选择向哪个球队积分，选上报球队执行2，选参赛球队执行1。
			//teamList中有我是队长的上报队及选中的参赛队
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
	public Map<String, Object> getTeamScoreByMatchId(Long matchId, Long teamId, String openid) throws UnsupportedEncodingException {
		Map<String, Object> result = new HashMap<>();
		List<MatchGroupUserScoreBean> list = new ArrayList<>();
		//比赛信息
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
        List<Long> childMatchIds = getLongIdListReplace(matchInfo.getMiChildMatchIds());
		result.put("matchInfo", matchInfo);

        Long  userId = userService.getUserIdByOpenid(openid);
        //所有球洞
		List<MatchTotalUserScoreBean> parkHoleList = new ArrayList<>();
		//每洞杆数
		List<MatchTotalUserScoreBean> parkRodList = new ArrayList<>();
		//获取前半场球洞
        Long parkMatchId;
        if ( matchInfo.getMiType() ==2 ){
            parkMatchId=childMatchIds.get(0);
        } else {
            parkMatchId = matchId;
        }
		List<Map<String, Object>> beforeParkHoleList = matchDao.getBeforeAfterParkPartitionList(parkMatchId,0);
		getNewParkHoleList(parkHoleList,parkRodList,beforeParkHoleList);
		//获取后半场球洞
		List<Map<String, Object>> afterParkHoleList = matchDao.getBeforeAfterParkPartitionList(parkMatchId,1);
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
        //区分比赛是普通比赛还是个关联比赛的父比赛
        /*
        List<Map<String, Object>> userList =  new ArrayList<>();

        if ( matchInfo.getMiType()==2){
            //比赛的所有用户和其总杆数，为0的排后面(首列显示)
            List<Map<String, Object>> userList1 = null;
            MatchInfo matchInfo1;
            for(int i=0;i< childMatchIds.size();i++) {
                userList1 = matchDao.getUserListById(childMatchIds.get(i),null, teamId);//顺便按成绩排序,没记完整成绩排后面

                //用户昵称解码
                decodeUserNickName(userList1);
                //用户名
                setUserName(userList1);
                //本比赛用户每个洞得分情况
                matchInfo1=matchDao.get(MatchInfo.class, childMatchIds.get(i));
                createNewUserScoreList(userList1, list, matchInfo1);
                userList.addAll(userList1);
            }
        } else{
            //比赛的所有用户和其总杆数，为0的排后面(首列显示)

            //参赛球队
            //String joinTeamIds = matchInfo.getMiJoinTeamIds();
*/
        List<Map<String, Object>> userList;
        if (matchInfo.getMiType() ==2) {
            userList =matchDao.getUserScoreByMatchId(matchId,teamId,matchInfo.getMiType(), childMatchIds, null,0);
            //用户昵称解码
            decodeUserNickName(userList);
            //用户名
            setUserName(userList);
            //本比赛用户每个洞得分情况
        } else {
            userList = matchDao.getUserListById(matchId, matchInfo.getMiType(), childMatchIds, null, teamId,0);//顺便按成绩排序,没记完整成绩排后面
           //用户昵称解码
            decodeUserNickName(userList);
            //用户名
            setUserName(userList);
            //本比赛用户每个洞得分情况
            createNewUserScoreList(userList, list, matchInfo);
       }
        result.put("userList", userList);
        result.put("list", list);
/*
		//比赛的本球队所有用户 和总杆数(首列显示)
		List<Map<String, Object>> userList = matchDao.getUserListById(matchId, null,teamId);
		//用户昵称解码
		decodeUserNickName(userList);
		//设置用户名
		setUserName(userList);
		result.put("userList", userList);

		//本组用户每个洞得分情况
		createNewUserScoreList(userList,list,matchInfo);
		result.put("list", list);
*/
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
	 * 用户名
	 */
	public void setUserName(List<Map<String, Object>> userList) {
		if(userList!= null && userList.size()>0){
			for(Map<String, Object> user:userList){
				String realName = getName(user,"uiRealName");
				if(StringUtils.isNotEmpty(realName)){
					user.put("uiRealName",realName);
				}else{
					String nickName = getName(user,"uiNickName");
					if(StringUtils.isNotEmpty(nickName)){
						user.put("uiRealName",nickName);
					}
				}
			}
		}
	}

	/**
	 * 获取用户在每个球洞的得分情况
	 */
	public void createNewUserScoreList(List<Map<String, Object>> userList, List<MatchGroupUserScoreBean> list,MatchInfo matchInfo) {
	    Integer matchType = matchInfo.getMiType();
	    if (userList != null && userList.size() > 0) {
			for (Map<String, Object> user : userList) {
				Long uiId = getLongValue(user, "uiId");
				Long teamId = getLongValue(user, "team_id");
                Long childMatchId = getLongValue(user, "match_id");
                MatchGroupUserScoreBean bean = new MatchGroupUserScoreBean();
				bean.setUserId(uiId);
				bean.setUserName(getName(user, "uiRealName"));
                bean.setGroupName(getName(user, "groupName"));
                bean.setTeamAbbrev(getName(user, "teamAbbrev"));
				//本用户的前后半场总得分情况

				List<MatchTotalUserScoreBean> userScoreList = new ArrayList<>();
				//本用户前半场得分情况
				List<Map<String, Object>> uScoreBeforeList = matchDao.getBeforeAfterScoreByUserId(uiId, childMatchId,0,teamId);
				createNewUserScore(userScoreList, uScoreBeforeList);
				//本用户后半场得分情况
				List<Map<String, Object>> uScoreAfterList = matchDao.getBeforeAfterScoreByUserId(uiId, childMatchId,1,teamId);
				createNewUserScore(userScoreList, uScoreAfterList);
				bean.setUserScoreTotalList(userScoreList);
				list.add(bean);
			}
		}
	}

	/**
	 * 比赛——group——分队统计（队际成绩）
	 * 如果是两个队比洞的话，把每队各组的分相加，其中赢的组得1分，输的组得0分，平的组各得0.5分。一个队队内，及多个队之间没法进行比洞赛
	 * 分队统计的前n名，就是指每个队排前n名的人的杆数和排名
	 * 比杆赛：如果是比杆赛，名次就取每队成绩最好（杆数最少）的前n人（如果是双人，就是前5组）计算
	 * 					按创建比赛时“参赛范围”选择的球队统计成绩并按平均杆数排名，（球队、参赛人数、平均杆数、总杆数、排名）
	 * 	如果是比洞赛，n就按按分组顺序取前n组，还按原来的表头计算
	 * 比洞赛：用不同的表。（球队、获胜组、打平组、得分、排名）
	 * 计算时各队的参赛人数都是一样的，如果有球队的参赛人数小于n,少的那几个人，每人杆数按110计算
     * 助威组成绩不做统计 nhq
	 * @return
	 */
	public Map<String, Object> getTeamTotalScoreByMatchId(Long matchId,Integer childId, Integer mingci,String openid) {
        Long  userId = userService.getUserIdByOpenid(openid);
		Map<String, Object> result = new HashMap<>();

		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		result.put("matchInfo",matchInfo);
		List<Long> joinTeamIdList = null;
		//参赛球队
		if(StringUtils.isNotEmpty(matchInfo.getMiJoinTeamIds())){
			joinTeamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
		}

//		赛制(：多队排位赛（积分赛) 走rodMatch取数据，其他都走holeMatch
        Integer format3 = matchInfo.getMiMatchFormat3();
		if ( format3 ==null){format3 = 0;}
		if (matchInfo.getMiMatchFormat1() == 0  ) {
			//比杆赛
			rodMatch(joinTeamIdList,mingci,matchId,matchInfo,result);
		} else {
			//比洞赛 n就按按分组顺序取前n组，还按原来的表头计算
			//如果是两个队比洞的话，把每队各组的分相加，其中赢的组得1分，输的组得0分，平的组各得0.5分。一个队队内，及多个队之间没法进行比洞赛
            //如果是两个队比杆的话
			holeMatch(matchId,childId,result,mingci,matchInfo,joinTeamIdList);
		}
        //是否是上报球队队长 显示“球队积分确认按钮”
        List<Long> reportTeamIdList = getLongTeamIdList(matchInfo.getMiReportScoreTeamId());
        if(reportTeamIdList != null && reportTeamIdList.size() >0){
            Long isReportTeamCaptain = teamService.getIsReportTeamCaptain(userId, reportTeamIdList);
            result.put("isReportTeamCaptain", isReportTeamCaptain);
        }

		return result;
	}



	/**
	 * 比杆赛——分队统计（队际成绩）
	 * 如果是比杆赛，名次就取每队成绩最好（杆数最少）的前n人（如果是双人，就是前5组）计算
	 * 				 按创建比赛时“参赛范围”选择的球队统计成绩并按平均杆数排名，（球队、参赛人数、平均杆数、总杆数、排名）
	 * 计算时各队的参赛人数都是一样的，如果有球队的参赛人数小于n,少的那几个人，每人杆数按110(Const.DEFAULT_ROD_NUM)计算
	 */
	private void rodMatch(List<Long> joinTeamIdList, Integer mingci, Long matchId,
						  MatchInfo matchInfo, Map<String,Object> result) {
		//取前n名
        Integer matchType = matchInfo.getMiType();
        List<Long> childMatchIds = getLongIdListReplace(matchInfo.getMiChildMatchIds());
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
                List<Map<String, Object>> scoreList = new ArrayList<>();

                scoreList = matchDao.getMatchRodTotalScoreByMingci(matchId, joinTeamIdList.get(0), mingci);

  				TeamInfo teamInfo = matchDao.get(TeamInfo.class,joinTeamIdList.get(0));
				MatchTeamRankingBean matchTeamRankingBean = new MatchTeamRankingBean();
				matchTeamRankingBean.setTeamName(teamInfo.getTiName());
				matchTeamRankingBean.setTeamAbbrev(teamInfo.getTiAbbrev());//nhq
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

                //取各参赛队的实际参赛人数，关联比赛需要取所有子比赛各队人数

                List <Map<String,Object>> joinMatchUserList = matchDao.getJoinMatchUserList(matchId, joinTeamIdList,matchInfo.getMiType(),childMatchIds);

				if(joinMatchUserList != null && joinMatchUserList.size() >0){
					for(Map<String,Object> team:joinMatchUserList){
						Long teamId = getLongValue(team,"teamId");
						Integer userCount = getIntegerValue(team,"userCount");
						MatchTeamRankingBean matchTeamRankingBean = new MatchTeamRankingBean();
						matchTeamRankingBean.setTeamId(teamId);
						matchTeamRankingBean.setTeamName(getName(team,"teamName"));
						matchTeamRankingBean.setTeamAbbrev(getName(team,"teamAbbrev"));
						matchTeamRankingBean.setUserCount(userCount);
						//获取本队参赛人数
//						List<Long> teamIdList = new ArrayList<>();
//						teamIdList.add(teamId);
//						List<Map<String,Object>> mapList = matchDao.getUserCountByMatchUserMappingTeamId(matchInfo.getMiId(),teamIdList);
						Integer sumRodNumByN = 0;
						//本球队参赛人数小于名次的N，并且小于最大参赛人数
						if(userCount != 0){

							//获取每个队前n名的成绩
                            List<Map<String, Object>> scoreList = new ArrayList<>();

                            if (matchInfo.getMiMatchFormat2()>0) {
                                //两人或四人比杆，结果从matchHoleResult中取
                                scoreList=matchDao.getMatchDoubleRodScoreByMingci(matchId,teamId,mingci);
                            } else {
                                //个人比杆赛，结果从matchScore中取
                                Integer mingci1;
                                if (matchType == 2 ) {

                                    Long userCount1 = matchDao.getUserCountByChildMatchIds(teamId, childMatchIds);
                                    matchTeamRankingBean.setUserCount(userCount1.intValue());
                                    if (matchInfo.getMiMatchFormat3()!=null && matchInfo.getMiMatchFormat3() ==3) {
                                        //球队均值排位，取各队前mingci*10 % 算平均值
                                       Double  mingci2 =  Math.ceil(userCount1.doubleValue() * mingci.doubleValue() / 10);
                                       mingci1 = mingci2.intValue();
                                    }
                                    else {
                                        mingci1 = mingci;
                                    }
                                    scoreList = matchDao.getFatherMatchRodTotalScoreByMingci(matchId, childMatchIds,teamId, mingci1);
                                }else {
                                   if ( matchInfo.getMiMatchFormat3()!= null && matchInfo.getMiMatchFormat3() ==3) {
                                        //球队均值排位，取各队前mingci*10 % 算平均值
                                        mingci1 = (int) Math.ceil(userCount * mingci / 10);
                                    }
                                    else {
                                        mingci1 = mingci;
                                    }
                                    scoreList = matchDao.getMatchRodTotalScoreByMingci(matchId, teamId, mingci1);
                                }
                            }
							if(scoreList != null && scoreList.size()>0){
                                Integer userCount1 = getIntegerValue(scoreList.get(0),"userCount");
                                if(userCount1 < mingci && userCount1 < maxCount){
                                    // 如果有球队的参赛人数小于n,少的那几个人，每人杆数按110(Const.DEFAULT_ROD_NUM)计算
                                    Long n = mingci.longValue() - userCount1;
                                    sumRodNumByN = n.intValue()*110;
                                }
								Integer sumRodNum = getIntegerValue(scoreList.get(0),"sumRodNum");
                                Double avgRodNum = getDoubleValue(scoreList.get(0),"avgRodNum");
								if(sumRodNum != 0 ){
								    if (matchInfo.getMiMatchFormat3()!=null && matchInfo.getMiMatchFormat3() == 3 ) {
								        //均杆按比例模式，直接用查询出来的结果
                                        matchTeamRankingBean.setAvgRodNum(avgRodNum);
                                        matchTeamRankingBean.setSumRodNum(sumRodNum);
                                    } else {
                                        if (matchInfo.getMiMatchFormat2() == 0) {
                                            Integer sum = sumRodNum + sumRodNumByN;
                                            BigDecimal b = BigDecimal.valueOf(sum.doubleValue() / mingci);
                                            Double avg = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                            matchTeamRankingBean.setAvgRodNum(avg);
                                            matchTeamRankingBean.setSumRodNum(sum);
                                        } else {
                                            Integer sum = sumRodNum;
                                            Integer groupNum = getIntegerValue(scoreList.get(0), "groupNum");
                                            BigDecimal b = new BigDecimal((float) (sum / groupNum));
                                            Double avg = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                            matchTeamRankingBean.setAvgRodNum(avg);
                                            matchTeamRankingBean.setSumRodNum(sum);
                                        }
                                    }
								}else{
									matchTeamRankingBean.setAvgRodNum(199.0);
									matchTeamRankingBean.setSumRodNum(9999);
								}
							}else{
								matchTeamRankingBean.setAvgRodNum(199.0);
								matchTeamRankingBean.setSumRodNum(9999);
							}
						}else{
							matchTeamRankingBean.setAvgRodNum(199.0);
							matchTeamRankingBean.setSumRodNum(9999);
						}
						matchTeamRankingList.add(matchTeamRankingBean);
					}
					//排序
                    if (matchInfo.getMiMatchFormat3()!=null && matchInfo.getMiMatchFormat3() == 3) {
                        Collections.sort(matchTeamRankingList, new Comparator<MatchTeamRankingBean>() {
                            @Override
                            public int compare(MatchTeamRankingBean bean1, MatchTeamRankingBean bean2) {
                                if (bean1.getAvgRodNum() == 0) {
                                    return -1;
                                }

                                return new Double(bean1.getAvgRodNum()).compareTo(new Double(bean2.getAvgRodNum()));
                            }
                        });
                    }  else {
                        Collections.sort(matchTeamRankingList, new Comparator<MatchTeamRankingBean>() {
                            @Override
                            public int compare(MatchTeamRankingBean bean1, MatchTeamRankingBean bean2) {
                                if (bean1.getSumRodNum() == 0) {
                                    return -1;
                                }

                                return new Double(bean1.getSumRodNum()).compareTo(new Double(bean2.getSumRodNum()));
                            }
                        });
                    }
				}
			}
		}
		result.put("scoreList",matchTeamRankingList);
		result.put("mingciArray",nList);
	}

	/**
	 * 这个方法本来是处理捉对按组比洞的，现在用它计算两人和四人的比洞或比杆
	 * 如果是比洞赛，n就按按分组顺序取前n组，还按原来的表头计算
	 * 比洞赛：用不同的表。（球队、获胜组、打平组、得分、排名）
	 * 如果是两个队比洞的话，把每队各组的分相加，其中赢的组得1分，输的组得0分，平的组各得0.5分。一个队队内，及多个队之间没法进行比洞赛
     * 助威组不做统计 实际显示记分卡的时候并未往比赛结果中放，nhq
	 */
	private void holeMatch(Long matchId,Integer childId, Map<String, Object> result, Integer mingci,MatchInfo matchInfo, List<Long> joinTeamIdList) {

	  /*
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
        result.put("mingciArray",nList);

		//获取参赛队
        List<Map<String,Object>> teamInfoList = matchDao.getJoinTeamMappingList(matchId);
        //计算队的得分情况
        List<MatchTotalTeamBean> beanList = new ArrayList<>();
        if(teamInfoList != null && teamInfoList.size()>0){
            for(Map<String,Object> teamMap:teamInfoList){
				Long teamId = getLongValue(teamMap,"teamId");
                //每个球队的平、赢组个数
                MatchTotalTeamBean matchTotalTeamBean = new MatchTotalTeamBean();
                matchTotalTeamBean.setTeamId(teamId);
                TeamInfo teamInfo = matchDao.get(TeamInfo.class,teamId);
                matchTotalTeamBean.setTeamName(teamInfo.getTiName());
				matchTotalTeamBean.setTeamAbbrev(teamInfo.getTiAbbrev());
                List<Map<String, Object>> numList = matchDao.getPingWinNumList(matchId,teamId);
                if(numList != null && numList.size() >0){
                    Map<String, Object> map = numList.get(0);
                    Integer winNum = getIntegerValue(map,"winNum");
					Integer pingNum = getIntegerValue(map,"pingNum");
                    matchTotalTeamBean.setWinNum(winNum);
                    matchTotalTeamBean.setPingNum(pingNum);
					BigDecimal score = new BigDecimal(winNum.floatValue() + pingNum.floatValue() * 0.5).setScale(1, BigDecimal.ROUND_HALF_UP);
					matchTotalTeamBean.setScore(score.doubleValue());
                }
                //取每队各组的情况
                List <MatchHoleResult> teamGroupResult=matchDao.getTeamGroupResult(matchId,teamId);
                //为前台显示格式化数据
                matchTotalTeamBean.setTeamGroupResult(teamGroupResult);
                beanList.add(matchTotalTeamBean);
            }
            Collections.sort(beanList);
        }
		result.put("scoreList",beanList);
		result.put("mingciArray",nList);
*/


          //获取子比赛列表
          List<Map<String,Object>> childList = matchDao.getChildMappingList(matchInfo.getMiId());
          result.put("childList",childList);


            //获取参赛队
          List<Map<String,Object>> teamInfoList = matchDao.getJoinTeamMappingList(matchId, childId );
        //计算队的得分情况

        if(teamInfoList != null && teamInfoList.size()>1) {
            //第一个队
            Long teamId = getLongValue(teamInfoList.get(0), "teamId");
            //每个球队的平、赢组个数
            MatchTotalTeamBean teamGroup1 = new MatchTotalTeamBean();
            teamGroup1.setTeamId(teamId);
            TeamInfo teamInfo = matchDao.get(TeamInfo.class, teamId);
            teamGroup1.setTeamName(teamInfo.getTiName());
            teamGroup1.setTeamAbbrev(teamInfo.getTiAbbrev());
            List<Map<String, Object>> numList = matchDao.getHoleMatchPingWinNumList(matchId, childId, teamId);
            if (numList != null && numList.size() > 0) {
                Map<String, Object> map = numList.get(0);
                Integer winNum = getIntegerValue(map, "winNum");
                Integer pingNum = getIntegerValue(map, "pingNum");
                teamGroup1.setWinNum(winNum);
                teamGroup1.setPingNum(pingNum);
                BigDecimal score = new BigDecimal(winNum.floatValue() + pingNum.floatValue() * 0.5).setScale(1, BigDecimal.ROUND_HALF_UP);
                teamGroup1.setScore(score.doubleValue());
            }
            //取第一队组的情况
            List<Map<String, Object>> teamGroupResult1 = matchDao.getTeamGroupResult(matchId, childId,teamId);
            //计算总成绩
            Integer tScore=0;
            for ( Map<String, Object> group: teamGroupResult1){
               if (matchInfo.getMiMatchFormat1()==1){
                  tScore +=  getIntegerValue(group,"result");
                } else {
                   tScore +=  getIntegerValue(group,"rodResult");
               }
            }
            teamGroup1.setTotalScore(tScore);
            teamGroup1.setTeamGroupResult(teamGroupResult1);
            result.put("teamGroup1", teamGroup1);

            //第二个队
            teamId = getLongValue(teamInfoList.get(1), "teamId");
            //每个球队的平、赢组个数
            MatchTotalTeamBean teamGroup2 = new MatchTotalTeamBean();
            teamGroup2.setTeamId(teamId);
            teamInfo = matchDao.get(TeamInfo.class, teamId);
            teamGroup2.setTeamName(teamInfo.getTiName());
            teamGroup2.setTeamAbbrev(teamInfo.getTiAbbrev());
            numList = matchDao.getHoleMatchPingWinNumList(matchId, childId, teamId);
            if (numList != null && numList.size() > 0) {
                Map<String, Object> map = numList.get(0);
                Integer winNum = getIntegerValue(map, "winNum");
                Integer pingNum = getIntegerValue(map, "pingNum");
                teamGroup2.setWinNum(winNum);
                teamGroup2.setPingNum(pingNum);
                BigDecimal score = new BigDecimal(winNum.floatValue() + pingNum.floatValue() * 0.5).setScale(1, BigDecimal.ROUND_HALF_UP);
                teamGroup2.setScore(score.doubleValue());
            }
            //取第二队组的情况
            List<Map<String, Object>> teamGroupResult2 = matchDao.getTeamGroupResult(matchId, childId,teamId);
            //计算总成绩
            tScore=0;
            for ( Map<String, Object> group: teamGroupResult2){
                if (matchInfo.getMiMatchFormat1()==1){
                    tScore +=  getIntegerValue(group,"result");
                } else {
                    tScore +=  getIntegerValue(group,"rodResult");
                }
            }
            teamGroup2.setTotalScore(tScore);
            teamGroup2.setTeamGroupResult(teamGroupResult2);
            result.put("teamGroup2", teamGroup2);

            //计算两队比赛的成绩
            //用第一队数据形成成绩列表和剩洞列表
            List<HoleMatchScoreResultBean> chengjiList = new ArrayList<>();
            List<Integer> holeLeftList = new ArrayList<>();
            int res = 0;
            for (int i=0; i<teamGroupResult1.size();i++) {
                Map<String, Object> teamResult1=teamGroupResult1.get(i);
                Map<String, Object> teamResult2=teamGroupResult2.get(i);
                HoleMatchScoreResultBean bean = new HoleMatchScoreResultBean();
                res = getIntegerValue(teamResult1, "result");
                if (res == 0) {
                    bean.setNum(null);
                    bean.setUpDnAs(MatchResultEnum.AS.text());
                } else if (res > 0) {
                    bean.setNum(res);
                    bean.setUpDnAs(MatchResultEnum.UP.text());
                } else if (res < 0) {
                    bean.setNum(Math.abs(res));
                    bean.setUpDnAs(MatchResultEnum.DN.text());
                }
                //把双人比杆赛的最好杆数放进去
                bean.setRodNum1(getIntegerValue(teamResult1, "rodResult"));
                bean.setRodNum2(getIntegerValue(teamResult2, "rodResult"));
                chengjiList.add(bean);
                holeLeftList.add(getIntegerValue(teamResult1, "holeLeft"));
            }
            result.put("chengjiList", chengjiList);
            result.put("holeLeftList", holeLeftList);
        }

	}

	/**
	 * 取前N名 N的list
	 */
	private void getNList(List<Map<String, Object>> nList, MatchInfo matchInfo,
											   List<Long> joinTeamIdList,Map<String,Object> nMap) {
		Long minCount = 0L;
		Long maxCount = 0L;
		List<Map<String,Object>> mapList = new ArrayList<>();
        List<Map<String,Object>> mapList1 ;
		Integer matchType=matchInfo.getMiType();
        List<Long> childMatchIds = getLongIdListReplace(matchInfo.getMiChildMatchIds());
		if(matchInfo.getMiMatchFormat2() == 0){
			//单人赛 人数较少的队伍的人数
            if(matchType ==2 ) {
                mapList = matchDao.getUserCountByFatherMatchUserMappingTeamId(childMatchIds, joinTeamIdList);
            }else {
                mapList = matchDao.getUserCountByMatchUserMappingTeamId(matchInfo.getMiId(), joinTeamIdList);
            }
            if (mapList != null && mapList.size()>0 ) {
                minCount = getLongValue(mapList.get(0), "userCount");
                maxCount = getLongValue(mapList.get(mapList.size() - 1), "userCount");
            }
		}else{
			//如果是双人赛,默认显示最少的组数，最大值为人数最多的数
			mapList = matchDao.getUserCountByMatchGroupTeamId(matchInfo.getMiId(),joinTeamIdList);
			if (mapList != null && mapList.size()>0) {
                minCount = getLongValue(mapList.get(0), "groupCount");
                maxCount = getLongValue(mapList.get(mapList.size() - 1), "groupCount");
            }
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
	public boolean applyMatch(Long matchId, Long groupId, String groupName, String chooseTeamId, String openid){
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		//参赛球队
		List<Long> teamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		Long userId = userInfo.getUiId();

		Long chooseTeam = null;
		//代表球队
		if(StringUtils.isNotEmpty(chooseTeamId) && !"null".equals(chooseTeamId) && !"undefined".equals(chooseTeamId)){
			chooseTeam = Long.parseLong(chooseTeamId);
			//我是否在本球队中，没有就加一条入队申请
			Long count = teamDao.getMeIsInTeam(userId,chooseTeam);
			if(count <= 0){
			    TeamInfo teamInfo = teamDao.get(TeamInfo.class,chooseTeam);
				TeamUserMapping teamUserMapping = new TeamUserMapping();
				teamUserMapping.setTumTeamId(chooseTeam);
				teamUserMapping.setTumUserId(userId);
				//是否有加入审核(1、是（队长审批）  0、否)
				if(teamInfo.getTiJoinOpenType() == 1){
                    teamUserMapping.setTumUserType(2);
                }else{
                    teamUserMapping.setTumUserType(1);
                }
				teamUserMapping.setTumCreateUserId(userId);
				teamUserMapping.setTumCreateTime(System.currentTimeMillis());
				teamUserMapping.setTumCreateUserName(userInfo.getUserName());
				matchDao.save(teamUserMapping);
			}
		}

		//查询是否已经报名，待分组的也算
		MatchUserGroupMapping matchUserGroupMapping = matchDao.getIsInMatch(matchId,userId);
		if(matchUserGroupMapping == null){
			matchUserGroupMapping = new MatchUserGroupMapping();
			matchUserGroupMapping.setMugmMatchId(matchId);
			matchUserGroupMapping.setMugmGroupId(groupId);
			matchUserGroupMapping.setMugmGroupName(groupName);
			matchUserGroupMapping.setMugmUserId(userId);
			matchUserGroupMapping.setMugmUserName(userInfo.getUserName());
			matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
            matchUserGroupMapping.setMugmUpdateTime(System.currentTimeMillis());//同时改这个更新时间，以便给组员排序
			matchUserGroupMapping.setMugmCreateUserId(userId);
			matchUserGroupMapping.setMugmCreateUserName(userInfo.getUserName());
			matchUserGroupMapping.setMugmUserType(1);
			matchUserGroupMapping.setMugmIsDel(0);
			if(teamIdList != null && teamIdList.size()>0 && chooseTeam != null){
				//是队际赛 并且选择了参赛球队  如果是公开赛，不用保存参赛球队
				matchUserGroupMapping.setMugmTeamId(chooseTeam);
			}
			matchDao.save(matchUserGroupMapping);

		}else{
			//在临时分组，修改其状态，从临时分组改为加入到报名组
			matchUserGroupMapping.setMugmGroupId(groupId);
			matchUserGroupMapping.setMugmGroupName(groupName);
			matchUserGroupMapping.setMugmIsDel(0);
			matchUserGroupMapping.setMugmUpdateTime(System.currentTimeMillis());
			matchUserGroupMapping.setMugmUpdateUserId(userId);
			matchUserGroupMapping.setMugmUpdateUserName(userInfo.getUserName());
			matchDao.update(matchUserGroupMapping);

		}
		//修改比赛信息钟的比赛人数
		Long userCount = matchDao.getAllMatchApplyUserCount(matchId);
		matchInfo.setMiPeopleNum(userCount.intValue());
		matchDao.update(matchInfo);
		return true;
	}

	/**
	 * 比赛——普通用户从一个组退出比赛  nhq
	 *退出比赛只是退出分组，已有比赛成绩保留在库中 nhq
	 * @return
	 */
	public void quitMatch(Long matchId, String openid) {
        UserInfo userInfo = userService.getUserInfoByOpenId(openid);
        Long userId = userInfo.getUiId();
        Long isCaptain = matchDao.getIsMatchCaptain(matchId, userId);
/*
        //从比赛分组中删除
        List<MatchUserGroupMapping> grouplist = matchDao.getIsInMatchGroupMappingByUserId(matchId, userId);
        if (grouplist != null && grouplist.size() > 0) {
            for (MatchUserGroupMapping bean : grouplist) {
                if (isCaptain>0 ){
                    //赛长退出比赛只退到报名名单中。
                   delUserByMatchIdGroupId(bean.getMugmId().toString());
                } else {
                    matchDao.del(bean);
                }
            }
        }

		//删除比赛成绩
		List<MatchScore> scorelist = matchDao.getIsInMatchByUserId(matchId,userId);
		if(scorelist != null && scorelist.size()>0){
			for(MatchScore bean :scorelist){
				matchDao.del(bean);
			}
		}
		*/

        List<MatchUserGroupMapping> grouplist = matchDao.getIsInMatchGroupMappingByUserId(matchId, userId);
        if (grouplist != null && grouplist.size() > 0) {
            for (MatchUserGroupMapping bean : grouplist) {
                //退出比赛只退到报名名单中。nhq
                delUserByMatchIdGroupId(bean.getMugmId().toString());

            }
        }
	}

    /**
     * 比赛——普通用户从报名名单退出比赛  nhq
     *退出比赛，删除比赛成绩 nhq
     * @return
     */
    public void cancelApply(Long matchId, String openid) {
        UserInfo userInfo = userService.getUserInfoByOpenId(openid);
        Long userId = userInfo.getUiId();

		//删除比赛成绩
		List<MatchScore> scorelist = matchDao.getIsInMatchByUserId(matchId,userId);
		if(scorelist != null && scorelist.size()>0){
			for(MatchScore bean :scorelist){
				matchDao.del(bean);
			}
		}
		//退出比赛
        matchDao.delMatchUserMappingByUserId(matchId, null, userId);

    }



    /**
	 * 比赛——报名页面——底部按钮——报名待分组
	 * @return
	 */
	public void saveUserApplyWaitGroup(Long matchId, String chooseTeamId, String openid) {
        MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		Long userId = userInfo.getUiId();

		//我是否报名
		MatchUserGroupMapping myMugm = matchDao.getIsInMatch(matchId,userId);
		if(myMugm == null){
			myMugm = new MatchUserGroupMapping();
			myMugm.setMugmMatchId(matchId);
			if(StringUtils.isNotEmpty(chooseTeamId) && !chooseTeamId.equals("null")){
				Long tid = Long.parseLong(chooseTeamId);
				//我是不是这个球队的 申请中也算
				Long count = teamDao.getMeIsInTeam(userId,tid);
				if(count <= 0){
					//发送一条入队申请
					TeamInfo teamInfo = teamDao.get(TeamInfo.class,tid);
					TeamUserMapping teamUserMapping = new TeamUserMapping();
					teamUserMapping.setTumTeamId(tid);
					teamUserMapping.setTumUserId(userInfo.getUiId());
					//是否有加入审核(1、是（队长审批）  0、否)
					if(teamInfo.getTiJoinOpenType() == 1){
						teamUserMapping.setTumUserType(2);
					}else{
						teamUserMapping.setTumUserType(1);
					}
					teamUserMapping.setTumCreateUserId(userInfo.getUiId());
					teamUserMapping.setTumCreateTime(System.currentTimeMillis());
					teamUserMapping.setTumCreateUserName(userInfo.getUserName());
					matchDao.save(teamUserMapping);
				}
				myMugm.setMugmTeamId(tid);
			}
			//普通球友
			myMugm.setMugmUserType(1);
			//待分组
			myMugm.setMugmIsDel(1);
			myMugm.setMugmUserId(userId);
			myMugm.setMugmUserName(userInfo.getUserName());
			myMugm.setMugmCreateUserId(userId);
			myMugm.setMugmCreateUserName(userInfo.getUserName());
			myMugm.setMugmCreateTime(System.currentTimeMillis());
			matchDao.save(myMugm);
		}
        //修改比赛信息钟的比赛人数
        Long userCount = matchDao.getAllMatchApplyUserCount(matchId);
        matchInfo.setMiPeopleNum(userCount.intValue());
        matchDao.update(matchInfo);

	}
/**
 * 更新组公告 nhq
 */
public void updateGroupNotice( Long groupId, String groupNotice) {
    MatchGroup matchGroup = matchDao.get(MatchGroup.class, groupId);
    matchGroup.setMgGroupNotice(groupNotice);
    matchGroup.setMgUpdateTime(System.currentTimeMillis());
    matchDao.update(matchGroup);

}

    /**
     * 更新助威组 nhq
     */
    public void updateGroupIsGuest( Long groupId, Integer groupIsGuest) {
        MatchGroup matchGroup = matchDao.get(MatchGroup.class, groupId);
        matchGroup.setMgIsGuest(groupIsGuest);
        matchGroup.setMgUpdateTime(System.currentTimeMillis());
        matchDao.update(matchGroup);
    }

	/**
	 * 要设置disableHtmlEscaping，否则会在转换成json的时候自作多情地转义一些特殊字符，如"="
	 */
	private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	/**
	 * 比赛——邀请记分——生成二维码
	 * 接口B: 获取小程序码（永久有效、数量暂无限制）.
	 * https://developers.weixin.qq.com/miniprogram/dev/api/getWXACodeUnlimit.html
	 * 必须是已经发布的小程序存在的页面（否则报错），例如 pages/index/index,
	 * 根路径前不要填加 /,不能携带参数（参数请放在scene字段里），如果不填写这个字段，默认跳主页面
	 *
	 * @param type 0:邀请记分 1：邀请加入
	 * @return
	 */
	public Map<String,Object> createQRCode(Long matchId, Long groupId, String openid, Integer type) throws WxErrorException, IOException {
		Map<String,Object> result = new HashMap<>();
		String path = null;
		Long myUserId = userService.getUserIdByOpenid(openid);
		MatchInfo matchInfo = matchDao.get(MatchInfo.class,matchId);
		result.put("matchInfo",matchInfo);
		if(type == 1 && matchInfo.getMiType() == 0){
			//单练——最多4个人
			Long userCount = matchDao.getGroupUserCountById(matchId,groupId);
			if(userCount == 4){
				result.put("userCount",userCount);
				return result;
			}
		}

		//查询是否有我生成的二维码
		MatchUserQrcode matchUserQrcode = matchDao.getHasMyQRCode(matchId, groupId, myUserId, type);
		if(matchUserQrcode != null){
			path = PropertyConst.DOMAIN + matchUserQrcode.getMuqQrcodePath();
			result.put("qrCodePath",path);
			return result;
		}

		//没有生成过  文件名称 = 比赛id_本组id_邀请人id_类型（0:邀请记分 1：邀请加入）
		String fileName = matchId+"_"+groupId+"_"+myUserId+"_"+type+".png";
		String QRCodePath = WebUtil.getPath()+PropertyConst.QRCODE_PATH;
		File file = new File(QRCodePath);
		if(!file.exists()){
			file.mkdirs();
		}
		//String parp = "?mId="+matchId+"&gId="+groupId+"&uId="+myUserId+"&type="+type;//参数
        String parp = "?m="+matchId+"&g="+groupId+"&u="+myUserId+"&t="+type; //参数 为了使调用字符串不超过腾讯规定的32长度
//		String scene = gson.toJson(parp);
//		scene = URLEncoder.encode(scene,"utf-8");
		String page ="pages/index/index";//要跳转的页面，先跳转到index，再通过首页判断进行路由，根路径不能加/  不填默认跳转主页
//		File file_ = wxMaService.getQrcodeService().createWxaCodeUnlimit(parp,page); //此方法好像是有包冲突，会报错
//		System.out.println(file_);
		byte[] qrcodeResult = wxMaService.getQrcodeService().createWxaCodeUnlimitBytes(parp,page,300,false,null,false);
		InputStream inputStream = null;
		OutputStream outputStream = null;
		inputStream = new ByteArrayInputStream(qrcodeResult);
		outputStream = new FileOutputStream(QRCodePath+fileName);
		int len = 0;
		byte[] buf = new byte[1024];
		while ((len = inputStream.read(buf, 0, 1024)) != -1) {
			outputStream.write(buf, 0, len);
		}
		outputStream.flush();

		//新增一条二维码记录
		matchUserQrcode = new MatchUserQrcode();
		matchUserQrcode.setMuqMatchId(matchId);
		matchUserQrcode.setMuqGroupId(groupId);
		matchUserQrcode.setMuqMatchUserId(myUserId);
		matchUserQrcode.setMuqType(type);
		matchUserQrcode.setMuqQrcodePath(PropertyConst.QRCODE_PATH + fileName);
		matchUserQrcode.setMuqCreateTime(System.currentTimeMillis());
		matchDao.save(matchUserQrcode);
		path = PropertyConst.DOMAIN + PropertyConst.QRCODE_PATH + fileName;
		result.put("qrCodePath",path);
		return result;
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
	public boolean setMatchCaptainByUserId(Long matchId, Long userId, String openid) {
		UserInfo myUserInfo = userService.getUserInfoByOpenId(openid);
        UserInfo userInfo =  matchDao.get(UserInfo.class, userId);
		//该用户是否参加了比赛，不管是否报名待分组
		MatchUserGroupMapping matchUserGroupMapping = matchDao.getIsInMatch(matchId,userId);
		if(matchUserGroupMapping != null){
			matchUserGroupMapping.setMugmUserType(0);
			//不是自动设置的赛长
			matchUserGroupMapping.setMugmIsAutoCap(0);
			matchUserGroupMapping.setMugmIsDel(0);
			matchUserGroupMapping.setMugmUpdateTime(System.currentTimeMillis());
			matchUserGroupMapping.setMugmUpdateUserId(myUserInfo.getUiId());
			matchUserGroupMapping.setMugmUpdateUserName(myUserInfo.getUserName());
			matchDao.update(matchUserGroupMapping);

		} else {
            matchUserGroupMapping = new MatchUserGroupMapping();
            matchUserGroupMapping.setMugmMatchId(matchId);
            matchUserGroupMapping.setMugmUserId(userId);
            matchUserGroupMapping.setMugmUserName( userInfo.getUserName());
            matchUserGroupMapping.setMugmUserType(0);
            matchUserGroupMapping.setMugmIsAutoCap(0);
            matchUserGroupMapping.setMugmIsDel(1);
            matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
            matchUserGroupMapping.setMugmCreateUserId(myUserInfo.getUiId());
            matchUserGroupMapping.setMugmCreateUserName(myUserInfo.getUserName());
            matchUserGroupMapping.setMugmUpdateTime(System.currentTimeMillis());
            matchDao.save(matchUserGroupMapping);

        }
		return true;
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
        return matchScoreUserMapping != null;
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
		MatchGroup group = matchDao.get(MatchGroup.class, groupId);
		//删除当前分组
		matchDao.del(group);
		//更新其他分组
		matchDao.updateGroupNames(group.getMgMatchId(),group.getMgGroupName());
	}

	/**
	 * 比赛详情——赛长——获取备选球友(除去已经报名、参赛的)，赛长所在队的球友或者其搜索的结果
     * 备选列表中，把我所在的参赛球队的队友都列出来（我可能同时在几个参赛队中，只是代表一个队参赛），
     * 当把该名单中某几人添加到分组后，备选名单里这些人要还在，
     * 也就是不维护备选名单的人员，这样即使一个人已加入一组，
     * 就还能从备选名单中选他加入新组以实现自动换组
	 * @return
	 */
	public Map<String,Object> getMyTeamUserList(Long matchId, String keyword, String openid) throws UnsupportedEncodingException {
		Map<String,Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class,matchId);
        Long captainUserId = userService.getUserIdByOpenid(openid);
		List<Long> joinTeamIdList = getLongTeamIdList(matchInfo.getMiJoinTeamIds());

		if(joinTeamIdList != null && joinTeamIdList.size()>0){
		    //队际赛，我是否在参赛球队中（不管是不是申请入队的）
            joinTeamIdList = matchDao.getMyJoinTeamInfoList(joinTeamIdList,captainUserId);
        }else{
            //公开赛，我所在的球队
            joinTeamIdList =  matchDao.getMyJoinTeamList(captainUserId);
        }

		List<TeamUserBean> teamUserList = new ArrayList<>();
		if(joinTeamIdList != null && joinTeamIdList.size()>0){
			for(Long teamId:joinTeamIdList){
				TeamUserBean bean = new TeamUserBean();
				//获取本参赛队的所有用户
				List<Map<String, Object>> userList = matchDao.getUserListByTeamId(teamId, keyword);
				if(userList != null && userList.size() >0){
					//用户昵称解码
					decodeUserNickName(userList);
					//如果有多条我的记录，去掉我的那个不是我代表队的那条
					for(Iterator<Map<String,Object>> userIterator = userList.iterator(); userIterator.hasNext();){
						Map<String,Object> map = userIterator.next();
						Long userId = getLongValue(map,"uiId");
						Long teamId_ = getLongValue(map,"tiId");
						if(userId.equals(captainUserId) && !teamId.equals(teamId_)){
							userIterator.remove();
						}
					}
				}
				TeamInfo teamInfo = matchDao.get(TeamInfo.class,teamId);
				bean.setUserList(userList);
				if(bean.getTeamInfo() == null){
					bean.setTeamInfo(teamInfo);
				}
				teamUserList.add(bean);
			}
			result.put("teamUserList",teamUserList);
			result.put("matchInfo",matchInfo);

           /* userList = matchDao.getUserListByJoinTeamId(keyword,joinTeamIdList);
            //如果有多条我的记录，去掉不是我代表队的那条
            MatchUserGroupMapping matchUserGroupMapping = matchDao.getIsInMatchUserMapping(matchId,null,captainUserId);
            Long chooseTeamId = matchUserGroupMapping.getMugmTeamId();
            if(userList != null && userList.size() >0){
                for(Iterator<Map<String,Object>> userIterator = (Iterator<Map<String, Object>>) userList.iterator(); userIterator.hasNext();){
                    Map<String,Object> map = userIterator.next();
                    Long userId = getLongValue(map,"uiId");
                    Long teamId = getLongValue(map,"tiId");
                    if(userId.equals(captainUserId) && !teamId.equals(chooseTeamId)){
                        userIterator.remove();
                    }
                }
            }*/
		}
		return result;
	}

	/**
	 * 创建比赛——获取赛长用户所在球队，是否同时是参赛球队的队长 如果是让用户选择一个做代表队
	 * 如果用户都不在这些球队中，就返回这些球队，让用户选一个加入
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
			//我不在参赛队中，就返回这些球队，让用户选一个加入
			return matchDao.getTeamListByIds(joinTeamIdList);
		}
		return matchDao.getTeamListByIds(myJoinTeamIdList);
	}


	/**
	 * 比赛报名——将用户从该分组删除
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
     * 比赛报名——将用户从比赛中删除
     *
     * @return
     */
    public void delUserByMatchIdUserId(Long matchId,JSONArray checkIds, String openid) {
        Long myUserId = userService.getUserIdByOpenid(openid);
        if (checkIds != null && checkIds.size() > 0) {
            for (int i = 0; i < checkIds.size(); i++) {
                JSONObject jsonObject = (JSONObject) checkIds.get(i);
                Long userId = null;
                Long teamId = null;
                if (jsonObject.get("userId") != null) {
                    userId = Long.parseLong(jsonObject.get("userId").toString());
                }
                if (jsonObject.get("teamId") != null) {
                    teamId = Long.parseLong(jsonObject.get("teamId").toString());
                }
                //从比赛中删除用户
                if (userId != myUserId) {
                    matchDao.delMatchUserMappingByUserId(matchId, teamId, userId);
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
			MatchUserGroupMapping matchUserGroupMapping = matchDao.getIsInMatchUserMapping(matchId,null,userId);
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
			matchScoreUserMapping.setMsumScoreUserId(otherUserId);
			matchScoreUserMapping.setMsumType(0);
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


    //我是否是本组参赛人员(显示邀请记分按钮)
    public Long getIsJoinMatchGroup(Long matchId, Long groupId, String openid) {
	    Long userId = userService.getUserIdByOpenid(openid);
	    MatchInfo matchInfo = matchDao.get(MatchInfo.class,matchId);
		return matchDao.getIsJoinMatchGroup(matchId,groupId,userId);
    }

	/**
	 * 比赛详情——获取全部已报名人员，按球队分组显示（去掉除我之外的参赛队的队长）
	 * @return
	 */
	public Map<String, Object> getAllApplyUserByMatchId(Long matchId, String openid) throws UnsupportedEncodingException {
		Map<String, Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class,matchId);
		Long myUserId = userService.getUserIdByOpenid(openid);
		//获取参赛队
		List<Long> joinTeamIds = getLongTeamIdList(matchInfo.getMiJoinTeamIds());
		List<TeamUserBean> teamUserList = new ArrayList<>();
		if(joinTeamIds != null && joinTeamIds.size()>0){
			for(Long teamId:joinTeamIds){
				TeamUserBean bean = new TeamUserBean();
				//获取本参赛队的报名用户 除去队长（自动设置的赛长）
				List<Map<String, Object>> userList = matchDao.getUserListByMatchTeamIdWithOutTeamCap(matchId,teamId);
				TeamInfo teamInfo = matchDao.get(TeamInfo.class,teamId);
				if(bean.getTeamInfo() == null){
					bean.setTeamInfo(teamInfo);
				}
				//用户昵称解码
				decodeUserNickName(userList);

				bean.setUserList(userList);
				teamUserList.add(bean);
			}
			result.put("teamUserList",teamUserList);
		}else{
			//公开赛 获取所有报名用户
			List<Map<String, Object>> userList = matchDao.getApplyUserIdList(matchId, null);
			//用户昵称解码
			decodeUserNickName(userList);
			result.put("userList",userList);
		}
		result.put("matchInfo",matchInfo);

		//我是否报名
		MatchUserGroupMapping matchUserGroupMapping = matchDao.getIsInMatchUserMapping(matchId,null,myUserId);
		result.put("isInMatch", matchUserGroupMapping != null);
		return result;
	}

	/**
	 * 用户昵称解码
	 * @return
	 */
	public void decodeUserNickName(List<Map<String, Object>> userList) throws UnsupportedEncodingException {
		if(userList != null && userList.size()>0){
			for(Map<String, Object> user : userList){
				String realName = getName(user,"uiRealName");
				String nickName = getName(user,"uiNickName");
				if(StringUtils.isNotEmpty(realName)){
					String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
					Boolean isLegal = realName.matches(base64Pattern);
					if (isLegal) {
						realName = new String(Base64.decodeBase64(realName.getBytes()), StandardCharsets.UTF_8);
						if(StringUtils.isNotEmpty(realName)){
							user.put("uiRealName",realName);
						}
					}
				}else if(StringUtils.isNotEmpty(nickName)){
					String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
					Boolean isLegal = nickName.matches(base64Pattern);
					if (isLegal) {
						nickName = new String(Base64.decodeBase64(nickName.getBytes()), StandardCharsets.UTF_8);
						if(StringUtils.isNotEmpty(nickName)){
							user.put("uiNickName",nickName);
						}
					}
				}
			}
		}
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
//		[{"q_no":1,"has":"是","keep":"症状来了又走了"},{"q_no":2,"has":"否"},{"q_no":3,"has":"是","keep":"症状大部分时间都持续在那里"}]


		String inputStr="8J+NlPCfjZ/wn42V";
		String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
		Boolean isLegal = inputStr.matches(base64Pattern);
		if (!isLegal) {
			System.out.println("输入的不是Base64编码的字符串。");
		}else{
			String nickName = new String(Base64.decodeBase64(inputStr.getBytes()), StandardCharsets.UTF_8);
			System.out.println(nickName);
		}
	}

	/**
	 * 比赛——报名页面——获取更多赛长
	 * @return
	 */
	public List<Map<String, Object>> getAllMatchCaptainList(Long matchId) throws UnsupportedEncodingException {
		List<Map<String, Object>> captainList = matchDao.getAllMatchCaptainList(matchId);
		//用户昵称解码
		decodeUserNickName(captainList);
		//取用户名
		setUserName(captainList);
		return captainList;
	}

	/**
	 * 比赛——扫码加入其他球友的单练
	 * @param matchId 比赛id
	 * @param groupId 本组id
	 * @param openid 我的openid
	 * @return
	 */
	public void joinOtherPractice(Long matchId, Long groupId, String openid) throws Exception {
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		MatchGroup group = matchDao.get(MatchGroup.class,groupId);
		//我是否加入了
		MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId,groupId,userInfo.getUiId());
		if(matchUserGroupMapping == null){
			matchUserGroupMapping = new MatchUserGroupMapping();
			matchUserGroupMapping.setMugmMatchId(matchId);
			matchUserGroupMapping.setMugmUserType(1);
			matchUserGroupMapping.setMugmGroupId(groupId);
			matchUserGroupMapping.setMugmGroupName(group.getMgGroupName());
			matchUserGroupMapping.setMugmUserId(userInfo.getUiId());
			matchUserGroupMapping.setMugmUserName(userInfo.getUserName());
			matchUserGroupMapping.setMugmIsDel(0);
			matchUserGroupMapping.setMugmCreateUserId(userInfo.getUiId());
			matchUserGroupMapping.setMugmCreateUserName(userInfo.getUserName());
			matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
			matchDao.save(matchUserGroupMapping);
			//删除一个临时球友 获取临时球友，按照id升序排列，取第一个临时球友
			MatchUserGroupMapping linshi = matchDao.getLinshiUserByMatchId(matchId);
			if(linshi != null){
				matchDao.del(linshi);
			}
		}
	}

	/**
	 * 比赛——更新我的扫码记录
	 * @param matchId 比赛id
	 * @param groupId 本组id
	 * @param openid 我的openid
	 * @return
	 */
	public void updateMyScanQRCode(Long matchId, Long groupId, String openid, Integer type) {
		Long myUserId = userService.getUserIdByOpenid(openid);
		//查询是否有我扫描的这个二维码
		MatchScoreUserMapping matchScoreUserMapping = matchDao.getHasMyScanQRCode(matchId, groupId, myUserId,type);
		if(matchScoreUserMapping == null){
			matchScoreUserMapping = new MatchScoreUserMapping();
			matchScoreUserMapping.setMsumMatchId(matchId);
			matchScoreUserMapping.setMsumGroupId(groupId);
			matchScoreUserMapping.setMsumScoreUserId(myUserId);
			matchScoreUserMapping.setMsumType(type);
			matchScoreUserMapping.setMsumCreateTime(System.currentTimeMillis());
			matchDao.save(matchScoreUserMapping);
		}
	}

	public MatchGroup getMatchGroupById(Long groupId) {
		return matchDao.get(MatchGroup.class,groupId);
	}


	/**
	 * 比赛——某一组——记分卡——结束该组比赛
	 * 防止一场比赛比多天的时候，前一天结束后改成绩
	 */
	public void updateMatchGroupStateByGroupId(Long groupId) {
	    /*
	    //本功能暂时不用 nhq20201012
		MatchGroup group = matchDao.get(MatchGroup.class,groupId);
		group.setMgIsEnd(2);
		matchDao.update(group);
		*/
	}


	/**
	 * 获取本组开球球洞
	 */
	public MatchScoreStartHole getStartHole(Long matchId, Long groupId) {
		return matchScoreDao.getStartHole(matchId,groupId);
	}
    /**
     * 删除比赛的重复记分记录
     */
    public void delDupMatchScore(Long matchId) {
        //删除比赛用户的重复记分记录
        List<Long> dupMatchScoreIds = matchDao.getDupMatchScore(matchId);
        if (dupMatchScoreIds.size() > 0) {
            matchDao.delDupMatchScore(dupMatchScoreIds);
        }
    }
}
