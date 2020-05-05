package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.util.PropertyConst;
import com.golf.common.util.TimeUtil;
import com.golf.golf.bean.TeamPointBean;
import com.golf.golf.dao.TeamDao;
import com.golf.golf.dao.UserDao;
import com.golf.golf.db.TeamInfo;
import com.golf.golf.db.TeamUserMapping;
import com.golf.golf.db.UserInfo;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
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

	@Autowired
	private UserDao userDao;

	/**
	 * 获取球队列表
	 * type 0：所有球队 1：已加入球队 2：可加入球队  3：我创建的球队(显示待审核人数)
	 * @return
	 */
	public POJOPageInfo getTeamList(SearchBean searchBean, POJOPageInfo pageInfo, String openid) throws UnsupportedEncodingException {
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
	public POJOPageInfo getChooseTeamList(SearchBean searchBean, POJOPageInfo pageInfo, String openid) throws UnsupportedEncodingException {
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
	public POJOPageInfo getReportTeamList(SearchBean searchBean, POJOPageInfo pageInfo, String openid) throws UnsupportedEncodingException {
		pageInfo = teamDao.getReportTeamList(searchBean,pageInfo);
		if(pageInfo.getCount() >0 && pageInfo.getItems() != null && pageInfo.getItems().size() >0){
			getCaptain(pageInfo.getItems(), openid);
		}
		return pageInfo;
	}


    //队长
	public void getCaptain(List<Map<String,Object>> mapList, String openid) throws UnsupportedEncodingException {
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		for(Map<String,Object> result : mapList){
			Integer count = matchService.getIntegerValue(result, "userCount");
			if(count == 0){
				result.put("userCount", 0);
			}
			//队长名字
			Long teamId = matchService.getLongValue(result, "tiId");
			if(teamId != null){
				List<Map<String,Object>> captainList = teamDao.getCaptainByTeamId(teamId);
				//用户昵称解码
				matchService.decodeUserNickName(captainList);
				if(captainList != null && captainList.size()>0){
					Map<String,Object> cap = captainList.get(0);
					String realName = matchService.getName(cap,"uiRealName");
					String nickName = matchService.getName(cap,"uiNickName");
					if(StringUtils.isNotEmpty(realName)){
						result.put("captain", realName);
					}else{
						result.put("captain", nickName);
					}
				}else{
					result.put("captain", "未知");
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
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		if(teamInfoBean.getTiId() == null){
			teamInfoBean.setTiCreateTime(TimeUtil.stringToLong(teamInfoBean.getTiCreateTimeStr(),TimeUtil.FORMAT_DATE));
			teamInfoBean.setTiCreateUserId(userInfo.getUiId());
			teamInfoBean.setTiCreateUserName(userInfo.getUserName());
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
			teamUserMapping.setTumCreateUserName(userInfo.getUserName());
			teamDao.save(teamUserMapping);
		}else{
			TeamInfo db = teamDao.get(TeamInfo.class,teamInfoBean.getTiId());
			db.setTiLogo(teamInfoBean.getTiLogo());
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
			db.setTiUpdateUserName(userInfo.getUserName());
			teamDao.update(db);
		}

	}

	/**
	 * 获取球队详情
	 * @param teamId:球队id count 显示的队员人数
	 * @return
	 */
	public Map<String, Object> getTeamInfoById(Long teamId, String openid) throws UnsupportedEncodingException {
		Map<String, Object> result = new HashMap<>();
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		TeamInfo teamInfo = teamDao.get(TeamInfo.class, teamId);
		teamInfo.getCreateTimeStr();
		//Logo没有时后台为 "",前台为null
		if (!teamInfo.getTiLogo().equals("")) {
            teamInfo.setTiLogo(PropertyConst.DOMAIN + teamInfo.getTiLogo());
        } else{teamInfo.setTiLogo(null);}
		result.put("teamInfo",teamInfo);
		List<Map<String, Object>> userList = teamDao.getTeamUserListByTeamId(teamId);
		//解码用户昵称
		matchService.decodeUserNickName(userList);
		//取用户名
		matchService.setUserName(userList);
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
	public Map<String, Object> getTeamPointByYear(Integer type, String date, Long teamId, Integer changCi) throws UnsupportedEncodingException {
		Map<String,Object> result = new HashMap<>();
		Map<String,Object> parp = new HashMap<>();
		parp.put("startYear", TimeUtil.longToString(TimeUtil.getYearFirst(Integer.parseInt(date)),TimeUtil.FORMAT_DATE));
		parp.put("endYear", TimeUtil.longToString(TimeUtil.getYearLast(Integer.parseInt(date)),TimeUtil.FORMAT_DATE));
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

					String nickName = userInfo.getUiNickName();
					//用户昵称解码
					if(StringUtils.isNotEmpty(nickName)){
						String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
						Boolean isLegal = nickName.matches(base64Pattern);
						if (isLegal) {
							nickName = new String(Base64.decodeBase64(nickName.getBytes()),"utf-8");
							if(StringUtils.isNotEmpty(nickName)){
								teamPointBean.setNickName(nickName);
							}
						}else{
							teamPointBean.setNickName(userInfo.getUiNickName());
						}
					}

					//获取本球友代表此球队的参赛场次（有效的比赛）
					parp.put("userId",userId);
					List<Map<String, Object>> joinList = teamDao.getJoinMatchChangCiByYear(parp);
                    Integer changCi1 =  matchService.getIntegerValue(joinList.get(0),"count");

                    if (joinList!=null&& changCi1>changCi){
                        teamPointBean.setTotalMatchNum(changCi);
                    } else {
                        if (joinList != null &&  changCi1> 0) {
                            teamPointBean.setTotalMatchNum( changCi1);
                        } else {
                            teamPointBean.setTotalMatchNum(0);
                        }
                    }
					//查每个用户的得分 和 场次下的总积分 不用从nhq
					//List<Map<String, Object>> sumList = teamDao.getUserSumRodScore(parp);

                    if(type == 1){
						//积分榜 获取本用户的前n场总积分 按照积分排名
                        List<Map<String, Object>> point = teamDao.getUserPointByChangci(parp);
                        Integer sumpoint =matchService.getIntegerValue(point.get(0),"sumPoint");
                        teamPointBean.setPoint(sumpoint);

					}
                    //获取本用户的前n场总成绩 按照成绩排名
                    List<Map<String, Object>> score = teamDao.getUserScoreByChangci(parp);
                    Integer sumrodnum=matchService.getIntegerValue(score.get(0),"sumRodNum");
                    teamPointBean.setSumRodNum(sumrodnum);

                    if (teamPointBean.getTotalMatchNum()>0 && sumrodnum >0){
                        Integer avgrodnum = Math.round(sumrodnum/teamPointBean.getTotalMatchNum());
                        teamPointBean.setAvgRodInteger(avgrodnum);
                    } else  teamPointBean.setAvgRodInteger(99999);

                    /*   nhq
				    Map<String, Object> sum = sumList.get(0);
					teamPointBean.setAvgRodNum(matchService.getDoubleValue(sum,"avgRodNum"));
					teamPointBean.setAvgRodInteger(matchService.getIntegerDoubleValue(sum,"avgRodNum"));
					teamPointBean.setSumRodNum(matchService.getIntegerValue(sum,"sumRodNum"));
                   */
					list.add(teamPointBean);
				}
				//排序

				if(type == 0){
					//比分榜 球队比分排名杆数少的排前面，积分榜是积分多的排前面
					Collections.sort(list,new Comparator<TeamPointBean>(){
						@Override
						public int compare(TeamPointBean teamPointBean1,TeamPointBean teamPointBean2){
							//if(teamPointBean1.getSumRodNum() != 0 || teamPointBean2.getSumRodNum() != 0){
								//return new Double(teamPointBean1.getSumRodNum()).compareTo(new Double(teamPointBean2.getSumRodNum()));
                           return  teamPointBean1.getAvgRodInteger().compareTo(teamPointBean2.getAvgRodInteger());
							//}
							//return 0;
						}
					});
				}else {
                    //积分榜是积分多的排前面
                    Collections.sort(list, new Comparator<TeamPointBean>() {
                        @Override
                        public int compare(TeamPointBean teamPointBean1, TeamPointBean teamPointBean2) {
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
	public List<Map<String, Object>> getUserListByTeamId(Long teamId, Integer type) throws UnsupportedEncodingException {
		List<Map<String, Object>> userList = teamDao.getUserListByTeamId(teamId, type);
		//解码用户昵称
		matchService.decodeUserNickName(userList);
		return userList;
	}

	/**
	 * 更新球队用户
	 * @param teamId:球队id
	 * @param userIds:用户
	 * @param type:0添加已报名的 1删除这些用户
	 * @return
	 */
	public void updateTeamUserByTeamId(Long teamId, String userIds, Integer type, String openid) {
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
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
						teamUserMapping.setTumCreateUserName(userInfo.getUserName());
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
	public List<Map<String, Object>> getAllUserListByTeamId(Long teamId) throws UnsupportedEncodingException {
		List<Map<String, Object>> userList = teamDao.getUserListByTeamId(teamId, 2);
		//解码用户昵称
		matchService.decodeUserNickName(userList);
		//取用户名
		matchService.setUserName(userList);
		return userList;
	}

    /* 以下这个程序用UserService 里的同一个方法会异常，只好在这里搞一个类似副本 nhq
    // 由于这个方法是用户在加入球队时调用，所以不存在和这个导入用户同时在同一球队或同一场比赛的情况。
    */
	public void updateClaimUserScoreById(String openid,Long importuserid) {
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		Long myUesrId = userInfo.getUiId();
		//Long chooseUserId = null;
		//Long chooseTeamId = null;
		//Long chooseMatchId = null;
		//if (StringUtils.isNotEmpty(importuserid)) {
		if (importuserid !=null) {
				//chooseUserId = Long.parseLong(importuserid);
				//chooseTeamId = Long.parseLong(chooseIdStr[1]);
				//chooseMatchId = Long.parseLong(chooseIdStr[2]);

				//更新这个导入用户的 比赛分组mapping 为db的id
				userDao.updateImportMatchMappingUserId(importuserid, myUesrId);
				//更新这个导入用户的 比赛成绩 的用户id为db的id 并设置是否认领为1
				userDao.updateImportMatchScoreUserId(importuserid, myUesrId);
				//更新这个导入用户的 球队分组mapping 为db的id
				userDao.updateImportTeamMappingUserId(importuserid, myUesrId);
				//删除这个导入用户
				UserInfo chooseUser = userDao.get(UserInfo.class, importuserid);
				userDao.del(chooseUser);
		}
	}

	/**
	 * 申请加入或退出该球队
	 * @param teamId:球队id
	 * @param type:0加入 1退出
	 * @return
	 */
	public Long joinOrQuitTeamById(Long teamId, Integer type, String openid) {
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		Long userId = userInfo.getUiId();
		String myRealName = userInfo.getUiRealName();

		if (type == 0) {
			List<UserInfo> chooseuserList = userDao.getUserIdByRealName(myRealName);
			//发现队内有同名用户，如果是导入用户，认领；如果不是导入用户，不能加入球队，直接退出 nhq
			for (UserInfo chooseuser: chooseuserList) {
				Long chooseuserid = chooseuser.getUiId();
				String chooseuseropenid = chooseuser.getUiOpenId();
				Long count = teamDao.isInTeamById(teamId, chooseuserid);
				if (count >0 ) {
					if (chooseuseropenid == null) {
						updateClaimUserScoreById(openid, chooseuserid);
						return null;
					}if(chooseuseropenid !=openid) {return chooseuserid;}
				}
			}
			TeamInfo teamInfo = teamDao.get(TeamInfo.class, teamId);
			TeamUserMapping teamUserMapping = new TeamUserMapping();
			teamUserMapping.setTumTeamId(teamId);
			teamUserMapping.setTumUserId(userId);
			//是否开启入队审核
			if (teamInfo.getTiJoinOpenType() == 1) {
				//申请入队
				teamUserMapping.setTumUserType(2);
			} else {
				//直接入队
				teamUserMapping.setTumUserType(1);
			}
			teamUserMapping.setTumCreateTime(System.currentTimeMillis());
			teamUserMapping.setTumCreateUserName(userInfo.getUserName());
			teamUserMapping.setTumCreateUserId(userId);
			teamDao.save(teamUserMapping);
		} else {
			teamDao.deleteFromTeamUserMapping(teamId, userId);
		}
		return null;
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
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
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
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
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
