package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.util.PropertyConst;
import com.golf.common.util.TimeUtil;
import com.golf.golf.bean.TeamPointBean;
import com.golf.golf.dao.TeamDao;
import com.golf.golf.db.TeamInfo;
import com.golf.golf.db.TeamUserMapping;
import com.golf.golf.db.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 球队管理
 * Created by nmy on 2017/7/1.
 */
@Service
public class TeamService implements IBaseService {

    @Autowired
    private TeamDao teamDao;
	@Autowired
	private MatchService matchService;
	@Autowired
	private UserService userService;

	/**
	 * 获取球队列表
	 * type 0：所有球队 1：已加入球队 2：可加入球队  3：我创建的球队(显示待审核人数)
	 * @return
	 */
	public POJOPageInfo getTeamList(SearchBean searchBean, POJOPageInfo pageInfo, String openid) {
		if((Integer)searchBean.getParps().get("type") <3){
			pageInfo = teamDao.getTeamList(searchBean,pageInfo);
		}else if((Integer)searchBean.getParps().get("type") ==3){
			//我创建的球队，包括待审核人数
			pageInfo = teamDao.getMyCreateTeamList(searchBean,pageInfo);
		}
		if(pageInfo.getCount() >0 && pageInfo.getItems() != null && pageInfo.getItems().size() >0){
			getCaptain(pageInfo.getItems(), openid);
		}
		return pageInfo;
	}

	/**
	 * 比赛——获取已经选中的球队列表
	 * @return
	 */
	public POJOPageInfo getChooseTeamList(SearchBean searchBean, POJOPageInfo pageInfo, String openid) {
		pageInfo = teamDao.getChooseTeamList(searchBean,pageInfo);
		if(pageInfo.getCount() >0 && pageInfo.getItems() != null && pageInfo.getItems().size() >0){
			getCaptain(pageInfo.getItems(), openid);
		}
		return pageInfo;
	}

	/**
	 * 比赛——获取上报球队列表
	 * @return
	 */
	public POJOPageInfo getReportTeamList(SearchBean searchBean, POJOPageInfo pageInfo, String openid) {
		pageInfo = teamDao.getReportTeamList(searchBean,pageInfo);
		if(pageInfo.getCount() >0 && pageInfo.getItems() != null && pageInfo.getItems().size() >0){
			getCaptain(pageInfo.getItems(), openid);
		}
		return pageInfo;
	}


    //队长
	public void getCaptain(List<Map<String,Object>> mapList, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		for(Map<String,Object> result : mapList){
			Integer count = matchService.getIntegerValue(result, "userCount");
			if(count == 0){
				result.put("userCount", 0);
			}
			//队长名字
			Long teamId = matchService.getLongValue(result, "tiId");
			if(teamId != null){
				List<String> captainList = teamDao.getCaptainByTeamId(teamId);
				if(captainList == null || captainList.size() ==0){
					result.put("captain", "未知");
				}else{
					result.put("captain", captainList.get(0));
				}
			}
			//logo
			String logo = matchService.getName(result,"logo");
			result.put("logo", PropertyConst.DOMAIN+logo);
			//我是否是队长
			Long isCaptain = teamDao.isCaptainIdByTeamId(teamId, userInfo.getUiId());
			result.put("isCaptain", isCaptain);
		}
    }

	/**
	 * 创建更新球队
	 * @return
	 */
	public void saveOrUpdateTeamInfo(TeamInfo teamInfoBean, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		if(teamInfoBean.getTiId() == null){
			teamInfoBean.setTiCreateTime(TimeUtil.stringToLong(teamInfoBean.getTiCreateTimeStr(),TimeUtil.FORMAT_DATE));
			teamInfoBean.setTiCreateUserId(userInfo.getUiId());
			teamInfoBean.setTiCreateUserName(userInfo.getUiRealName());
			teamInfoBean.setTiIsValid(1);
			Long teamId = teamDao.save(teamInfoBean);
			//向球队用户表新增一条记录
			TeamUserMapping teamUserMapping = new TeamUserMapping();
			teamUserMapping.setTumTeamId(teamId);
			teamUserMapping.setTumUserId(userInfo.getUiId());
			//设置队长
			teamUserMapping.setTumUserType(0);
			teamUserMapping.setTumCreateTime(System.currentTimeMillis());
			teamUserMapping.setTumCreateUserId(userInfo.getUiId());
			teamUserMapping.setTumCreateUserName(userInfo.getUiRealName());
			teamDao.save(teamUserMapping);
		}else{
			TeamInfo db = teamDao.get(TeamInfo.class,teamInfoBean.getTiId());
			if(!teamInfoBean.getTiLogo().contains(db.getTiLogo())){
				db.setTiLogo(teamInfoBean.getTiLogo());
			}
			db.setTiName(teamInfoBean.getTiName());
			db.setTiAbbrev(teamInfoBean.getTiAbbrev());
			db.setTiCreateTime(TimeUtil.stringToLong(teamInfoBean.getTiCreateTimeStr(),TimeUtil.FORMAT_DATE));
			db.setTiAddress(teamInfoBean.getTiAddress());
			db.setTiDigest(teamInfoBean.getTiDigest());
			db.setTiSignature(teamInfoBean.getTiSignature());
			db.setTiJoinOpenType(teamInfoBean.getTiJoinOpenType());
			db.setTiInfoOpenType(teamInfoBean.getTiInfoOpenType());
			db.setTiUserInfoType(teamInfoBean.getTiUserInfoType());
			db.setTiMatchResultAuditType(teamInfoBean.getTiMatchResultAuditType());
			db.setTiUpdateTime(System.currentTimeMillis());
			db.setTiUpdateUserId(userInfo.getUiId());
			db.setTiUpdateUserName(userInfo.getUiRealName());
			teamDao.update(db);
		}

	}

	/**
	 * 获取球队详情
	 * @param teamId:球队id count 显示的队员人数
	 * @return
	 */
	public Map<String, Object> getTeamInfoById(Long teamId, String openid) {
		Map<String, Object> result = new HashMap<>();
		UserInfo userInfo = userService.getUserByOpenId(openid);
		TeamInfo teamInfo = teamDao.get(TeamInfo.class, teamId);
		teamInfo.getCreateTimeStr();
		teamInfo.setTiLogo(PropertyConst.DOMAIN+teamInfo.getTiLogo());
		result.put("teamInfo",teamInfo);
		List<Map<String, Object>> userList = teamDao.getTeamUserListByTeamId(teamId);
		if(userList!= null && userList.size()>0){
			for(Map<String, Object> user:userList){
				String realName = matchService.getName(user,"uiRealName");
				if(StringUtils.isNotEmpty(realName) && realName.length() >5){
					user.put("uiRealName",realName.substring(0,5)+"...");
				}
				String nickName = matchService.getName(user,"uiNickName");
				if(StringUtils.isNotEmpty(nickName) && nickName.length() >5){
					user.put("uiNickName",nickName.substring(0,5)+"...");
				}
			}
		}
		result.put("userList",userList);
		Long isCaptain = teamDao.isCaptainIdByTeamId(teamId, userInfo.getUiId());
		result.put("isCaptain",isCaptain);
		Long isInTeam = teamDao.isInTeamById(teamId, userInfo.getUiId());
		result.put("isInTeam",isInTeam);
		return result;
	}

	/**
	 * 删除球队
	 * @return
	 */
	public boolean delTeamById(Long teamId, String openid) {
		Long userId = userService.getUserIdByOpenid(openid);
		//是否是该球队的队长
		Long count = teamDao.isCaptainIdByTeamId(teamId,userId);
		if(count >0){
			//删除球队、用户mapping 逻辑删除
			teamDao.delTeam(teamId);
			return true;
		}
		return false;
	}

	/**
	 * 获取球队记分详情 只计算比赛结束并且球队确认过的场次成绩
	 * 积分榜那里的平均杆数是指每场（18洞）的平均杆数，不是每洞的。
	 * 球队比分排名杆数少的排前面，积分榜是积分多的排前面
	 * @param teamId:球队id
	 * @param type:0比分榜 按平均杆排名 1积分榜:按北大发明的积分方法积分，方法另附 2获取比赛榜 列出当年所有本球队相关的比赛情况统计
	 * @return
	 */
	public Map<String, Object> getTeamPointByYear(Integer type, String date, Long teamId, Integer changCi) {
		Map<String,Object> result = new HashMap<>();
		Map<String,Object> parp = new HashMap<>();
		parp.put("startYear", TimeUtil.getYearFirst(Integer.parseInt(date)));
		parp.put("endYear", TimeUtil.getYearLast(Integer.parseInt(date)));
		parp.put("teamId", teamId);
		parp.put("changCi", changCi);
		TeamInfo teamInfo = teamDao.get(TeamInfo.class,teamId);
		result.put("teamInfo",teamInfo);

		if(type <= 1){
			//比分榜 or 积分榜 场次
			List<TeamPointBean> list = new ArrayList<>();
			//获取本球队所有的球友
			List<TeamUserMapping> userList = teamDao.getTeamUserList(parp);
			if(userList != null && userList.size()>0){
				for(TeamUserMapping user:userList){
					TeamPointBean teamPointBean = new TeamPointBean();
					Long userId = user.getTumUserId();
					UserInfo userInfo = teamDao.get(UserInfo.class,userId);
					teamPointBean.setUserId(userId);
					teamPointBean.setRealName(userInfo.getUiRealName());
					teamPointBean.setNickName(userInfo.getUiNickName());

					//获取本球友代表此球队的参赛场次（有效的比赛）
					parp.put("userId",userId);
					List<Map<String, Object>> joinList = teamDao.getJoinMatchChangCiByYear(parp);
					if(joinList!=null&& joinList.size()>0){
						teamPointBean.setTotalMatchNum(matchService.getIntegerValue(joinList.get(0),"count"));
					}else{
						teamPointBean.setTotalMatchNum(joinList.size());
					}
					//查每个用户的得分 和 场次下的总积分
					List<Map<String, Object>> sumList = teamDao.getUserSumRodScore(parp);
                    if(type == 1){
						//积分榜 获取本用户的前n场总积分 按照积分排名
                        List<Map<String, Object>> point = teamDao.getUserPointByChangci(parp);
                        teamPointBean.setPoint(matchService.getIntegerValue(point.get(0),"sumPoint"));
					}
					Map<String, Object> sum = sumList.get(0);
					teamPointBean.setAvgRodNum(matchService.getDoubleValue(sum,"avgRodNum"));
					teamPointBean.setAvgRodInteger(matchService.getIntegerDoubleValue(sum,"avgRodNum"));
					teamPointBean.setSumRodNum(matchService.getIntegerValue(sum,"sumRodNum"));
					list.add(teamPointBean);
				}
				//排序
				if(type == 0){
					//比分榜 球队比分排名杆数少的排前面，积分榜是积分多的排前面
					Collections.sort(list,new Comparator<TeamPointBean>(){
						@Override
						public int compare(TeamPointBean teamPointBean1,TeamPointBean teamPointBean2){
							if(teamPointBean1.getAvgRodNum() != 0 && teamPointBean2.getAvgRodNum() != 0){
								return new Double(teamPointBean1.getAvgRodNum()).compareTo(new Double(teamPointBean2.getAvgRodNum()));
							}
							return 0;
						}
					});
				}else{
					//积分榜是积分多的排前面
					Collections.sort(list,new Comparator<TeamPointBean>(){
						@Override
						public int compare(TeamPointBean teamPointBean1,TeamPointBean teamPointBean2){
							return teamPointBean2.getPoint().compareTo(teamPointBean1.getPoint());
						}
					});
				}
			}
			result.put("yearList",list);
		}else{
			//比赛榜 列出当年所有本球队相关的比赛情况统计  只列比赛结束且球队确认过的比赛 排序 按照离今天近的排序
			parp.put("startYear", TimeUtil.longToString(TimeUtil.getYearFirst(Integer.parseInt(date)),TimeUtil.FORMAT_DATE));
			parp.put("endYear", TimeUtil.longToString(TimeUtil.getYearLast(Integer.parseInt(date)),TimeUtil.FORMAT_DATE));
			List<Map<String, Object>> yearList = teamDao.getTeamMatchByYear(parp);
			result.put("yearList",yearList);
		}
		return result;
	}

	/**
	 * 获取球队已报名的用户或者球队用户列表
	 * @param teamId:球队id
	 * @param type:0已报名的 1本队用户 2本队所有用户
	 * @return
	 */
	public List<Map<String, Object>> getUserListByTeamId(Long teamId, Integer type) {
		return teamDao.getUserListByTeamId(teamId, type);
	}

	/**
	 * 更新球队用户
	 * @param teamId:球队id
	 * @param userIds:用户
	 * @param type:0添加已报名的 1删除这些用户
	 * @return
	 */
	public void updateTeamUserByTeamId(Long teamId, String userIds, Integer type, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		if(StringUtils.isNotEmpty(userIds)){
			userIds = userIds.replace("[","");
			userIds = userIds.replace("]","");
			userIds = userIds.replace("\"","");
			String[] uIds = userIds.split(",");
			for(String userId :uIds){
				if(StringUtils.isNotEmpty(userId)){
					TeamUserMapping teamUserMapping = teamDao.getTeamUserMapping(teamId,Long.parseLong(userId));
					if(type == 0){
						//设置为 普通队员
						teamUserMapping.setTumUserType(1);
						teamUserMapping.setTumCreateTime(System.currentTimeMillis());
						teamUserMapping.setTumCreateUserId(userInfo.getUiId());
						teamUserMapping.setTumCreateUserName(userInfo.getUiRealName());
						teamDao.update(teamUserMapping);
					}else{
						teamDao.del(teamUserMapping);
					}
				}
			}
		}
	}


	/**
	 * 获取本球队所有用户
	 * @param teamId:球队id
	 * type:0已报名的 1本队用户 2本队所有用户
	 * @return
	 */
	public List<Map<String, Object>> getAllUserListByTeamId(Long teamId) {
		List<Map<String, Object>> userList = teamDao.getUserListByTeamId(teamId, 2);
		if(userList!= null && userList.size()>0){
			for(Map<String, Object> user:userList){
				String realName = matchService.getName(user,"uiRealName");
				if(StringUtils.isNotEmpty(realName) && realName.length() >5){
					user.put("uiRealName",realName.substring(0,5)+"...");
				}
				String nickName = matchService.getName(user,"uiNickName");
				if(StringUtils.isNotEmpty(nickName) && nickName.length() >5){
					user.put("uiNickName",nickName.substring(0,5)+"...");
				}
			}
		}
		return userList;
	}

	/**
	 * 申请加入或退出该球队
	 * @param teamId:球队id
	 * @param type:0加入 1退出
	 * @return
	 */
	public void joinOrQuitTeamById(Long teamId, Integer type, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		Long userId = userInfo.getUiId();
		if(type == 0){
			TeamInfo teamInfo = teamDao.get(TeamInfo.class, teamId);
			TeamUserMapping teamUserMapping = new TeamUserMapping();
			teamUserMapping.setTumTeamId(teamId);
			teamUserMapping.setTumUserId(userId);
			//是否开启入队审核
			if(teamInfo.getTiJoinOpenType() == 1){
				//申请入队
				teamUserMapping.setTumUserType(2);
			}else{
				//直接入队
				teamUserMapping.setTumUserType(1);
			}
			teamUserMapping.setTumCreateTime(System.currentTimeMillis());
			teamUserMapping.setTumCreateUserName(userInfo.getUiRealName());
			teamUserMapping.setTumCreateUserId(userId);
			teamDao.save(teamUserMapping);
		}else{
			teamDao.deleteFromTeamUserMapping(teamId, userId);
		}
	}

	/**
	 * 是否是该球队队长
	 * @param userId:用户id
	 * @param teamId:球队id
	 * @return
	 */
	public Long getIsCaptain(Long userId, Long teamId) {
		return teamDao.getIsCaptain(userId, teamId);
	}

	/**
	 * 是否是上报球队队长
	 * @param userId:用户id
	 * @param reportTeamIdList:球队id
	 * @return
	 */
	public Long getIsReportTeamCaptain(Long userId, List<Long> reportTeamIdList) {
		return teamDao.getIsReportTeamCaptain(userId, reportTeamIdList);
	}

	/**
	 * 队长指定该用户成为队长
	 * @param teamId:球队id
	 * @param userId:被指定人id
	 * @return
	 */
	public void setTeamCaptainByUserId(Long teamId, Long userId, String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		TeamUserMapping teamUserMapping = teamDao.getTeamUserMapping(teamId,userId);
		teamUserMapping.setTumUserType(0);
		teamUserMapping.setTumUpdateTime(System.currentTimeMillis());
		teamUserMapping.setTumUpdateUserId(userInfo.getUiId());
		teamUserMapping.setTumUpdateUserName(userInfo.getUiRealName());
		teamDao.update(teamUserMapping);
	}

	/**
	 * 查询我是否填写了详细资料
	 * @return
	 */
	public boolean getHasDetail(String openid) {
		UserInfo userInfo = userService.getUserByOpenId(openid);
		if(StringUtils.isNotEmpty(userInfo.getUiRealName()) && userInfo.getUiAge() != null && StringUtils.isNotEmpty(userInfo.getUiTelNo())
			&& StringUtils.isNotEmpty(userInfo.getUiEmail()) && StringUtils.isNotEmpty(userInfo.getUiGraduateSchool())
				&& StringUtils.isNotEmpty(userInfo.getUiGraduateDepartment())
				&& StringUtils.isNotEmpty(userInfo.getUiGraduateTime())
				&& StringUtils.isNotEmpty(userInfo.getUiMajor())
				&& StringUtils.isNotEmpty(userInfo.getUiWorkUnit())
				&& StringUtils.isNotEmpty(userInfo.getUiPost()) && StringUtils.isNotEmpty(userInfo.getUiAddress())){
			return true;
		}
		return false;
	}



}
