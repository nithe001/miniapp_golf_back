package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.TimeUtil;
import com.golf.golf.bean.MatchGroupUserScoreBean;
import com.golf.golf.bean.MatchTotalUserScoreBean;
import com.golf.golf.common.security.UserModel;
import com.golf.golf.common.security.WechatUserUtil;
import com.golf.golf.dao.MatchDao;
import com.golf.golf.dao.UserDao;
import com.golf.golf.db.*;
import com.golf.golf.enums.UserTypeEnum;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理
 * 
 * @author fanyongqian
 * 2016年10月31日
 */
@Service
public class UserService implements IBaseService {
	
    @Autowired
    private UserDao dao;
    @Autowired
    protected WxMpService wxMpService;
    @Autowired
	private MatchDao matchDao;
    @Autowired
	private MatchService matchService;

	/**
	 * 获取“我的”
	 * @return
	 */
	public Map<String,Object> getMyDetail(String openid) {
		Map<String,Object> result = new HashMap<>();
		UserInfo userInfo = dao.getUserByOpenid(openid);
		result.put("userInfo",userInfo);
		if(userInfo != null){

		}
		//差点
		Double chaPoint = matchService.getUserChaPoint(userInfo.getUiId());
		result.put("chaPoint",chaPoint);
		return result;
	}


	/**
	 * 获取其他用户的详细资料
	 * @return
	 */
	public Map<String, Object> getOtherDetail(Long userId) {
		Map<String,Object> result = new HashMap<>();
		UserInfo userInfo = dao.get(UserInfo.class, userId);
		result.put("userInfo",userInfo);
		//差点
		Double chaPoint = matchService.getUserChaPoint(userInfo.getUiId());
		result.put("chaPoint",chaPoint);
		return result;
	}

    /**
     * 根据用户id取得用户信息
     * @param userId 用户id
     * @return
     */
    public UserInfo getUserById(Long userId){
		UserInfo userInfo = dao.get(UserInfo.class, userId);
		return userInfo;
    }

    /**
     * 根据用户openid取得微信的用户信息
     * @param openid openid
     * @return
     */
    public WechatUserInfo getWechatUserByOpenid(String openid){
        return dao.getWechatUserByOpenid(openid);
    }


    /**
     * 保存用户信息
     * @param user
     */
    public void updateUser(UserInfo user, String openid) throws Exception {
    	UserInfo db = getUserByOpenId(openid);
    	db.setUiRealName(user.getUiRealName());
    	db.setUiAge(user.getUiAge());
    	db.setUiTelNo(user.getUiTelNo());
		db.setUiEmail(user.getUiEmail());
		db.setUiGraduateSchool(user.getUiGraduateSchool());
		db.setUiGraduateDepartment(user.getUiGraduateDepartment());
		db.setUiMajor(user.getUiMajor());
		db.setUiGraduateTime(user.getUiGraduateTime());
		db.setUiStudentId(user.getUiStudentId());
		db.setUiWorkUnit(user.getUiWorkUnit());
		db.setUiPost(user.getUiPost());
		db.setUiAddress(user.getUiAddress());
		db.setUiHomeCourt(user.getUiHomeCourt());
		db.setUiUpdateTime(System.currentTimeMillis());
		db.setUiUpdateUserId(db.getUiId());
		db.setUiUpdateUserName(db.getUiRealName());
        dao.update(db);
        //更新其他表有用到真实姓名的地方
		//比赛表
		dao.updateMatchInfo(db.getUiId(),user.getUiRealName());
		//比赛成绩表
		dao.updateMatchScore(db.getUiId(),user.getUiRealName());
		//比赛用户分组表
		dao.updateMatchUserGroupMapping(db.getUiId(),user.getUiRealName());
		//球队表
		dao.updateTeamInfo(db.getUiId(),user.getUiRealName());
    }

	/**
	 * 更新签名
	 * @return
	 */
	public void updateUserSignature(String signature, String openid) {
		UserInfo db = dao.getUserByOpenid(openid);
		db.setUiPersonalizedSignature(signature);
		db.setUiUpdateTime(System.currentTimeMillis());
		db.setUiUpdateUserId(db.getUiId());
		db.setUiUpdateUserName(db.getUiRealName());
		dao.update(db);
	}

	/**
	 * 注册
	 */
	public void registerUser(UserInfo user) {
		dao.save(user);
	}

	/**
	 * 更新用户绑定状态
	 */
	public void updateSubscribeType(WxMpUser wxMpUser) {
		String openId = wxMpUser.getOpenId();
		dao.updateSubscribeTypeByOpenId(openId);
	}

	//通过openId获取用户信息
	public UserModel getUserInfoByOpenId(String openId) {
		UserModel userModel = null;
		WechatUserInfo dbUser = dao.getUserInfoByOpenId(openId);
		if(dbUser != null){
			userModel = new UserModel();
			userModel.setWechatUser(dbUser);
			if(dbUser.getWuiId() != null){
				UserInfo UserInfo = dao.get(UserInfo.class,dbUser.getWuiId());
				userModel.setUser(UserInfo);
			}
		}
		userModel.setOpenId(openId);
		return userModel;
	}

	//通过openId获取用户信息
	public UserInfo getUserByOpenId(String openId) {
		WechatUserInfo wechatUserInfo = dao.getUserInfoByOpenId(openId);
		return dao.get(UserInfo.class,wechatUserInfo.getWuiUId());
	}

    /**
     * 详细资料 只有是队友且该球队要求 详细资料时才可见
     * @return
     */
    public boolean userInfoIsOpen(Long userId, String openid) {
        //是否是我的队友
        Long teamId = dao.getIsMyTeammate(getUserIdByOpenid(openid),userId);
        if(teamId != null){
            TeamInfo teamInfo = dao.get(TeamInfo.class, teamId);
            if(teamInfo != null && teamInfo.getTiUserInfoType() == 1){
                return true;
            }
        }
        return false;
    }

	/**
	 * 保存/更新 微信用户信息
	 * @return
	 */
	public void saveOrUpdateWechatUserInfo(String openid, String userDataStr) throws IOException {
		WechatUserInfo wechatUserInfo = dao.getUserInfoByOpenId(openid);
		UserInfo userInfo = null;
		if(wechatUserInfo != null && wechatUserInfo.getWuiUId() != null){
			userInfo = dao.get(UserInfo.class, wechatUserInfo.getWuiUId());
		}
		if(wechatUserInfo != null){
			net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(userDataStr);
			wechatUserInfo.setWuiOpenid(openid);
			wechatUserInfo.setWuiNickName(jsonObject.get("nickName").toString());
			String gender = jsonObject.get("gender").toString();
			if("1".equals(gender)){
				wechatUserInfo.setWuiSex("男");
			}else if("0".equals(gender)){
				wechatUserInfo.setWuiSex("女");
			}else{
				wechatUserInfo.setWuiSex("未知");
			}
			wechatUserInfo.setWuiLanguage(jsonObject.get("language").toString());
			wechatUserInfo.setWuiCity(jsonObject.get("city").toString());
			wechatUserInfo.setWuiProvince(jsonObject.get("province").toString());
			wechatUserInfo.setWuiHeadimgurl(jsonObject.get("avatarUrl").toString());

			wechatUserInfo.setWuiIsValid(1);
			wechatUserInfo.setCreateTime(System.currentTimeMillis());

			//创建一条用户信息
			saveOrUpdateUserInfo(userInfo, openid, wechatUserInfo);
			wechatUserInfo.setWuiUId(userInfo.getUiId());
			dao.update(wechatUserInfo);
		}else{
			net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(userDataStr);
			wechatUserInfo = new WechatUserInfo();
			wechatUserInfo.setWuiOpenid(openid);
			wechatUserInfo.setWuiNickName(jsonObject.get("nickName").toString());
			String gender = jsonObject.get("gender").toString();
			if("1".equals(gender)){
				wechatUserInfo.setWuiSex("男");
			}else if("0".equals(gender)){
				wechatUserInfo.setWuiSex("女");
			}else{
				wechatUserInfo.setWuiSex("未知");
			}
			wechatUserInfo.setWuiLanguage(jsonObject.get("language").toString());
			wechatUserInfo.setWuiCity(jsonObject.get("city").toString());
			wechatUserInfo.setWuiProvince(jsonObject.get("province").toString());
			wechatUserInfo.setWuiHeadimgurl(jsonObject.get("avatarUrl").toString());

			//下载头像
			/*String headImgPath = PropertyConst.HEADIMG_PATH + openid + ".png";
			String path = WebUtil.getRealPath(PropertyConst.HEADIMG_PATH);
			if(StringUtils.isNotEmpty(wechatUserInfo.getWuiHeadimgurl())){
				HttpUtil.downloadPicture(wechatUserInfo.getWuiHeadimgurl(), path,openid + ".png");
			}
			wechatUserInfo.setWuiHeadimg(headImgPath);*/
			wechatUserInfo.setWuiIsValid(1);
			wechatUserInfo.setCreateTime(System.currentTimeMillis());

			//创建一条用户信息
			userInfo = saveOrUpdateUserInfo(userInfo, openid, wechatUserInfo);
			wechatUserInfo.setWuiUId(userInfo.getUiId());
			dao.save(wechatUserInfo);
		}
	}

	private UserInfo saveOrUpdateUserInfo(UserInfo userInfo, String openid, WechatUserInfo wechatUserInfo) {
		if(wechatUserInfo.getWuiUId() == null){
			userInfo = new UserInfo();
		}
		userInfo.setUiOpenId(openid);
		userInfo.setUiNickName(wechatUserInfo.getWuiNickName());
		userInfo.setUiHeadimg(wechatUserInfo.getWuiHeadimgurl());
		userInfo.setUiSex(wechatUserInfo.getWuiSex());
		if(wechatUserInfo.getWuiUId() == null){
			//普通用户
			userInfo.setUiType(UserTypeEnum.PT.ordinal());
			userInfo.setUiCreateTime(System.currentTimeMillis());
			dao.save(userInfo);
		}else{
			userInfo.setUiUpdateTime(System.currentTimeMillis());
			dao.update(userInfo);
		}
		return userInfo;
	}

	/**
	 * 更新用户经纬度信息
	 * @return
	 */
	public void updateUserLocation(String latitude, String longitude, String openid) {
		UserInfo db = getUserByOpenId(openid);
		db.setUiLatitude(latitude);
		db.setUiLongitude(longitude);
		db.setUiUpdateTime(System.currentTimeMillis());
		db.setUiUpdateUserId(db.getUiId());
		db.setUiUpdateUserName(db.getUiRealName());
		dao.update(db);
	}

	/**
	 * 获取我的历史成绩
	 * 表格同 比赛总比分 ，把人名换成球场名 增加一列时间 第一行下面的杆数不要
	 * 可以横向布局
	 * @return
	 */
	public Map<String, Object> getMyHistoryScoreByUserId(String openid) {
		Long userId = getUserIdByOpenid(openid);
		Map<String, Object> result = new HashMap<>();
		List<MatchGroupUserScoreBean> list = new ArrayList<>();

		//所有球洞 18
		List<String> parkHoleList = new ArrayList<>();
		for(int i=1;i<=9;i++){
			parkHoleList.add(i+"");
		}
        parkHoleList.add("A场");
        for(int i=10;i<=18;i++){
            parkHoleList.add(i+"");
        }
        parkHoleList.add("B场");
        parkHoleList.add("总杆");

		//第一条记录 所有球洞
		MatchGroupUserScoreBean thBean = new MatchGroupUserScoreBean();
		thBean.setUserId(0L);
		thBean.setUserName("Hole");
		thBean.setParkHoleList(parkHoleList);
		list.add(thBean);


		//获取我参加的所有比赛所在的球场 和总杆差 时间 (比赛id，球场id，球场名称,前半场名称，后半场名称,时间)
		List<Map<String, Object>> matchList = matchDao.getTotalChaListByUserId(userId);
		result.put("matchList", matchList);

//		List<Map<String, Object>> matchList = matchDao.getParkListByUserId(userId);
		//通过球场获取比分
		if(matchList != null && matchList.size()>0){
			for(Map<String, Object> match:matchList){
				Long matchId = matchService.getLongValue(match,"miId");
				MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
				//本用户每个洞得分情况
				createNewUserScoreList(userId,list,matchInfo);
			}
		}
        result.put("list", list);
		return result;
	}

	//通过openid获取userid
	public Long getUserIdByOpenid(String openid) {
		UserInfo userInfo = dao.getUserByOpenid(openid);
		if(userInfo != null){
			return userInfo.getUiId();
		}
		return null;
	}

	//获取用户在每个球洞的得分情况
	private void createNewUserScoreList(Long userId, List<MatchGroupUserScoreBean> list,MatchInfo matchInfo) {
		MatchGroupUserScoreBean bean = new MatchGroupUserScoreBean();
		//这场比赛我的代表球队
		MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchInfo.getMiId(), null, userId);
		bean.setUserId(userId);
		//本用户的前后半场总得分情况
		List<MatchTotalUserScoreBean> userScoreList = new ArrayList<>();
		//本用户前半场得分情况
		List<Map<String, Object>> uScoreBeforeList = matchDao.getBeforeAfterScoreByUserId(userId, matchInfo,0,matchUserGroupMapping.getMugmTeamId(),0);
		createNewUserScore(userScoreList, uScoreBeforeList);
		Integer beforeTotalScore = userScoreList.get(userScoreList.size()-1).getRodNum();
		//本用户后半场得分情况
		List<Map<String, Object>> uScoreAfterList = matchDao.getBeforeAfterScoreByUserId(userId, matchInfo,1,matchUserGroupMapping.getMugmTeamId(),0);
		createNewUserScore(userScoreList, uScoreAfterList);
        Integer afterTotalScore = userScoreList.get(userScoreList.size()-1).getRodNum();

		bean.setUserScoreTotalList(userScoreList);
		bean.setTotalRodScore(beforeTotalScore + afterTotalScore);
		list.add(bean);
	}


	//格式化用户半场得分
	private void createNewUserScore(List<MatchTotalUserScoreBean> userScoreList, List<Map<String, Object>> uScoreList) {
		Integer totalRod = 0;
		//杆差
		for(Map<String, Object> map:uScoreList){
			MatchTotalUserScoreBean bean = new MatchTotalUserScoreBean();
			Integer rodNum = matchService.getIntegerValue(map,"rod_num");
			Integer rodCha = matchService.getIntegerValue(map,"rod_cha");
			totalRod += rodNum;
			//杆数
			bean.setRodNum(rodNum);
			bean.setHoleStandardRod(matchService.getIntegerValue(map,"pp_hole_standard_rod"));
			userScoreList.add(bean);
		}
		//每个半场的总杆数
		MatchTotalUserScoreBean bean = new MatchTotalUserScoreBean();
		bean.setRodNum(totalRod);
		userScoreList.add(bean);
	}


	/**
	 * 年度成绩分析 包括单练
	 * 计算一年内平均每18洞分项的数量
	 * “暴洞”是指+3及以上的洞数总和
	 * 开球情况对应记分卡 球道滚轮的箭头
	 * 标ON是计算出来的，如果某洞：杆数-推杆数=该洞标准杆数-2，则该洞为 标ON
	 * @return
	 */
	public Map<String, Object> getMyHistoryScoreByYear(String date, String openid) {
		Map<String, Object> result = new HashMap<>();
		Map<String,Object> parp = new HashMap<>();
		Long userId = getUserIdByOpenid(openid);
		parp.put("userId",userId);
		parp.put("startTime", TimeUtil.getYearFirst(Integer.parseInt(date)));
		parp.put("endTime", TimeUtil.getYearLast(Integer.parseInt(date)));
		//今年参加比赛的场数
		Long matchCount = dao.getMatchCountByYear(parp);
		matchCount = matchCount==null?0:matchCount;
		result.put("matchCount",matchCount);
		//总杆数
		Long sumRod = dao.getSumRod(parp);
		sumRod = sumRod==null?0:sumRod;
		result.put("sumRod",sumRod);
		Double avgRod = 0.0;
		if(sumRod > 0){
			BigDecimal b = new BigDecimal((float)(sumRod / matchCount));
			avgRod = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		result.put("avgRod",avgRod);
		//所有杆数
		List<Map<String, Object>> scoreList = dao.getScoreByYear(parp);
		result.put("scoreList",scoreList);
		return result;
	}

	/**
	 * 查看用户基本信息
	 * 详细资料 只有是队友且该球队要求 详细资料时才可见
	 * @return
	 */
	public Map<String, Object> getUserDetaliInfoById(String teamIdStr, String matchIdStr, Long userId, String openid) {
		Map<String, Object> result = new HashMap<>();
		//被查看用户资料
		UserInfo otherUserInfo = getUserById(userId);
		result.put("userInfo",otherUserInfo);
		//信息是否公开
		boolean isOpen = userInfoIsOpen(userId, openid);
		result.put("isOpen",isOpen);
		//差点
		Double chaPoint = matchService.getUserChaPoint(userId);
		result.put("chaPoint",chaPoint);
		Long myUserId = getUserIdByOpenid(openid);
		if(StringUtils.isNotEmpty(teamIdStr)){
			Long teamId = Long.parseLong(teamIdStr);
			TeamInfo teamInfo = matchDao.get(TeamInfo.class,teamId);
			result.put("teamInfo",teamInfo);
			//被查看用户是否是本队队长
			Long elseIsTeamCaptain = matchDao.getIsTeamCaptain(teamId,userId);
			//我是否是本队队长
			Long meIsTeamCaptain = matchDao.getIsTeamCaptain(teamId,myUserId);
			result.put("elseIsTeamCaptain",elseIsTeamCaptain >0 ?true:false);
			result.put("meIsTeamCaptain",meIsTeamCaptain >0 ?true:false);
		}else if(StringUtils.isNotEmpty(matchIdStr)){
			Long matchId = Long.parseLong(matchIdStr);
			MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
			result.put("matchInfo",matchInfo);

			//被查看用户是否是本比赛的参赛人员
			Long otherIsJoinMatchUser = matchDao.getIsMatchCaptain(matchId,otherUserInfo.getUiId());
			Long otherIsMatchCaptain = 0l;
			Long otherIsMatchWatch = 0l;
			if(otherIsJoinMatchUser>0){
				//被查看用户是否是本比赛的赛长
				otherIsMatchCaptain = matchDao.getIsMatchCaptain(matchId,otherUserInfo.getUiId());
			}else{
				//被查看用户是否是本比赛的围观人员
				otherIsMatchWatch = matchDao.getIsWatch(otherUserInfo.getUiId(),matchId);
				if(otherIsMatchWatch>0){
					//查询我所在的比赛分组
					MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId,null,myUserId);
					//被查看用户是否已经被邀请记分
					MatchScoreUserMapping matchScoreUserMapping = matchDao.getMatchScoreUserMapping(matchId, matchUserGroupMapping.getMugmGroupId(), otherUserInfo.getUiId());
					if(matchScoreUserMapping == null){
						result.put("otherIsInvitate",false);
					}else{
						result.put("otherIsInvitate",true);
					}
				}
			}

			result.put("otherIsJoinMatchUser",otherIsJoinMatchUser >0 ?true:false);
			result.put("otherIsMatchCaptain",otherIsMatchCaptain >0 ?true:false);
			result.put("otherIsMatchWatch",otherIsMatchWatch >0 ?true:false);

			//我是否是参赛者
			Long meIsJoinMatchUser = matchDao.getIsJoinMatchUser(matchId,myUserId);
			Long meIsMatchCaptain = 0L;
			if(meIsJoinMatchUser>0){
				//我是否是本比赛的赛长
				meIsMatchCaptain = matchDao.getIsMatchCaptain(matchId,myUserId);
			}
			result.put("meIsJoinMatchUser",meIsJoinMatchUser >0 ?true:false);
			result.put("meIsMatchCaptain",meIsMatchCaptain >0 ?true:false);
		}
		return result;
	}

	/**
	 * 高球规则
	 * @return
	 */
	public List<MatchRule> getMatchRuleList() {
		return dao.getMatchRuleList();
	}

}
