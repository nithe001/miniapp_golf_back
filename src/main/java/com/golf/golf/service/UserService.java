package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.common.util.EmojiFilterUtil;
import com.golf.common.util.TimeUtil;
import com.golf.golf.bean.MatchGroupUserScoreBean;
import com.golf.golf.bean.MatchTotalUserScoreBean;
import com.golf.golf.common.security.UserModel;
import com.golf.golf.dao.MatchDao;
import com.golf.golf.dao.TeamDao;
import com.golf.golf.dao.UserDao;
import com.golf.golf.db.*;
import com.golf.golf.enums.UserTypeEnum;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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
	@Autowired
	private TeamDao teamDao;

	/**
	 * 获取“我的”
	 * @return
	 */
	public Map<String,Object> getMyDetail(String openid) throws UnsupportedEncodingException {
		Map<String,Object> result = new HashMap<>();
		//用户信息——昵称已解码
		UserInfo userInfo = getUserInfoByOpenIdDecodeNickName(openid);
		result.put("userInfo",userInfo);
		//差点
		Double chaPoint = matchService.getUserChaPoint(userInfo.getUiId());
		result.put("chaPoint",chaPoint);
		return result;
	}


	/**
	 * 获取其他用户的详细资料
	 * @return
	 */
	public Map<String, Object> getOtherDetail(Long userId) throws UnsupportedEncodingException {
		Map<String,Object> result = new HashMap<>();
		UserInfo userInfo = getUserInfoByIdDecodeNickName(userId);
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
    public UserInfo getUserInfoByIdDecodeNickName(Long userId) throws UnsupportedEncodingException {
		UserInfo userInfo = dao.get(UserInfo.class, userId);
		//解码用户昵称
		decodeUserNickName(userInfo);
		return userInfo;
    }

    /**
     * 根据用户openid取得微信的用户信息
     * @param openid openid
     * @return
     */
    public WechatUserInfo getWechatUserByOpenidDecodeNickName(String openid) throws UnsupportedEncodingException {
		WechatUserInfo wechatUserInfo = dao.getWechatUserByOpenid(openid);
		//解码用户昵称
		decodeWechatUserNickName(wechatUserInfo);
		return  wechatUserInfo;
    }


    /**
     * 保存用户信息
	 * 判断是否存在和我同一个球队的人有相同的真实姓名，如果有且是真实用户就拒绝改真名，有但是导入用户，就认领 nhq
     * @param user
     */
    public String updateUser(UserInfo user, String openid) {
    	UserInfo db = dao.getUserInfoByOpenid(openid);
    	//判断是否有同队队友重名
    	Long userId = db.getUiId();
    	Long teamId= null;
    	if (user.getUiRealName()!= null) {
			List<UserInfo> chooseuserList = dao.getUserIdByRealName(user.getUiRealName());
			for (UserInfo chooseuser : chooseuserList) {
				Long otherUserId = chooseuser.getUiId();
				teamId = dao.getIsMyTeammate(userId,otherUserId);
				if (teamId !=null ){
					if(chooseuser.getUiOpenId() !=null){
						break; //有同名真实用户，不让改
					}else {
						//同一队中有同名导入用户，认领他
						updateClaimUserScore(openid, String.valueOf(otherUserId));
						teamId = null;
					}
				}
			}
		}
    	if (teamId == null ) {
			db.setUiRealName(user.getUiRealName());
		}
    	db.setUiAge(user.getUiAge());
    	db.setUiTelNo(user.getUiTelNo());
		db.setUiEmail(user.getUiEmail());
		db.setUiGraduateSchool(user.getUiGraduateSchool());
		db.setUiGraduateDepartment(user.getUiGraduateDepartment());
		db.setUiMajor(user.getUiMajor());
		db.setUiGraduateTime(user.getUiGraduateTime());
		db.setUiStudentId(user.getUiStudentId());
		db.setUiWorkUnit(user.getUiWorkUnit());
		db.setUiAddress(user.getUiAddress());
		db.setUiAddress(user.getUiAddress());
		db.setUiHomeCourse(user.getUiHomeCourse());
		db.setUiUpdateTime(System.currentTimeMillis());
		db.setUiUpdateUserId(db.getUiId());
		db.setUiUpdateUserName(db.getUiRealName());
        dao.update(db);
        //更新其他表有用到真实姓名的地方
		//比赛积分计算配置表
		if (teamId == null) {
			dao.updateMatchIntegralConfigInfo(db.getUiId(), user.getUiRealName());
			//比赛分组表
			dao.updateMatchGroupInfo(db.getUiId(), user.getUiRealName());
			//比赛表
			dao.updateMatchInfo(db.getUiId(), user.getUiRealName());
			//比赛成绩表
			dao.updateMatchScore(db.getUiId(), user.getUiRealName());
			//比赛用户分组表
			dao.updateMatchUserGroupMapping(db.getUiId(), user.getUiRealName());
			//球队表
			dao.updateTeamInfo(db.getUiId(), user.getUiRealName());
			//球队用户mapping表
			dao.updateTeamUserMappingInfo(db.getUiId(), user.getUiRealName());
			//球队用户积分表
			dao.updateTeamUserPointInfo(db.getUiId(), user.getUiRealName());
		}
		String teamName = null;
		if (teamId !=null){
		TeamInfo teamInfo = dao.get(TeamInfo.class, teamId);
		if(teamInfo != null ){
			teamName = teamInfo.getTiName();
		}
		}
		return teamName;
    }

	/**
	 * 更新签名
	 * @return
	 */
	public void updateUserSignature(String signature, String openid){
		UserInfo db = dao.getUserInfoByOpenid(openid);
		db.setUiPersonalizedSignature(signature);
		db.setUiUpdateTime(System.currentTimeMillis());
		db.setUiUpdateUserId(db.getUiId());
		db.setUiUpdateUserName(db.getUiRealName());
		dao.update(db);
	}

	/**
	 * 更新用户绑定状态
	 */
	public void updateSubscribeType(WxMpUser wxMpUser) {
		String openId = wxMpUser.getOpenId();
		dao.updateSubscribeTypeByOpenId(openId);
	}

	/**
	 * 通过openId获取用户信息——昵称已解码
	 * @return
	 */
	public UserInfo getUserInfoByOpenIdDecodeNickName(String openId) throws UnsupportedEncodingException {
		UserInfo userInfo = dao.getUserInfoByOpenid(openId);
		if(userInfo != null){
			decodeUserNickName(userInfo);
		}
		return userInfo;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String name = "8J+NlPCfjZ/wn42V";
		String nickName = new String(Base64.decodeBase64(name.getBytes()), StandardCharsets.UTF_8);
		System.out.println(nickName);
	}

	/**
	 * 解码用户昵称
	 */
	public void decodeUserNickName(UserInfo userInfo) throws UnsupportedEncodingException {
		if(userInfo != null){
			String nickName = userInfo.getUiNickName();
			if(StringUtils.isNotEmpty(nickName)){
				nickName = new String(Base64.decodeBase64(nickName.getBytes()), StandardCharsets.UTF_8);
				if(StringUtils.isNotEmpty(nickName)){
					userInfo.setUiNickName(nickName);
				}
			}
		}
	}

	/**
	 * 解码用户昵称
	 */
	public void decodeWechatUserNickName(WechatUserInfo wechatUserInfo) throws UnsupportedEncodingException {
		if(wechatUserInfo != null){
			String nickName = wechatUserInfo.getWuiNickName();
			if(StringUtils.isNotEmpty(nickName)){
				nickName = new String(Base64.decodeBase64(nickName.getBytes()), StandardCharsets.UTF_8);
				if(StringUtils.isNotEmpty(nickName)){
					wechatUserInfo.setWuiNickName(nickName);
				}
			}
		}
	}

    /**
     * 详细资料 只有是队友且该球队要求 详细资料时才可见
     * @return
     */
    public boolean userInfoIsOpen(Long userId, String openid) {
        //是否是我的队友（入队审核通过的）
        Long teamId = dao.getIsMyTeammate(getUserIdByOpenid(openid),userId);
        if(teamId != null){
            TeamInfo teamInfo = dao.get(TeamInfo.class, teamId);
            return teamInfo != null && teamInfo.getTiUserInfoType() == 1;
        }
        return false;
    }

	/**
	 * 保存/更新 微信用户信息
	 * @return
	 */
	public void saveOrUpdateWechatUserInfo(String openid, String userDataStr) throws IOException {
		WechatUserInfo wechatUserInfo = dao.getWechatUserInfoByOpenId(openid);
		if(wechatUserInfo != null){
			updateWechatUserInfo(wechatUserInfo, openid, userDataStr);
			if(wechatUserInfo.getWuiUId() != null){
				updateUserInfo(wechatUserInfo);
			}
		}else{
			wechatUserInfo = saveWechatUserInfo(openid, userDataStr);
			saveUserInfo(wechatUserInfo);
		}
	}

	/**
	 * 保存用户微信信息
	 * @return
	 */
	private WechatUserInfo saveWechatUserInfo(String openid, String userDataStr) throws UnsupportedEncodingException {
		WechatUserInfo wechatUserInfo = new WechatUserInfo();
		net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(userDataStr);
		String nickName = null;
		if(jsonObject.get("nickName") != null){
			nickName = jsonObject.get("nickName").toString();
			boolean hasEmoji = EmojiFilterUtil.containsEmoji(nickName);
			if(hasEmoji){
				nickName = Base64.encodeBase64String(nickName.getBytes(StandardCharsets.UTF_8));
			}
		}
		wechatUserInfo.setWuiNickName(nickName);
		wechatUserInfo.setWuiOpenid(openid);
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
		dao.save(wechatUserInfo);
		return wechatUserInfo;
	}

	/**
	 * 更新用户微信信息
	 * @return
	 */
	private void updateWechatUserInfo(WechatUserInfo wechatUserInfo, String openid, String userDataStr) throws UnsupportedEncodingException {
		net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(userDataStr);
		String nickName = null;
		if(jsonObject.get("nickName") != null){
			nickName = jsonObject.get("nickName").toString();
			boolean hasEmoji = EmojiFilterUtil.containsEmoji(nickName);
			if(hasEmoji){
				nickName = Base64.encodeBase64String(nickName.getBytes(StandardCharsets.UTF_8));
			}
		}
		wechatUserInfo.setWuiOpenid(openid);
		wechatUserInfo.setWuiNickName(nickName);
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
		dao.update(wechatUserInfo);
	}

	/**
	 * 保存一条用户注册信息
	 * @return
	 */
	private void saveUserInfo(WechatUserInfo wechatUserInfo) {
		UserInfo userInfo = new UserInfo();
		userInfo.setUiOpenId(wechatUserInfo.getWuiOpenid());
		userInfo.setUiNickName(wechatUserInfo.getWuiNickName());
		//初始时把微信昵称设置为用户真实姓名  nhq
		userInfo.setUiRealName(wechatUserInfo.getWuiNickName());
		userInfo.setUiHeadimg(wechatUserInfo.getWuiHeadimgurl());
		userInfo.setUiSex(wechatUserInfo.getWuiSex());
		userInfo.setUiType(UserTypeEnum.PT.ordinal());
		userInfo.setUiCreateTime(System.currentTimeMillis());
		dao.save(userInfo);
		wechatUserInfo.setWuiUId(userInfo.getUiId());
		dao.update(wechatUserInfo);
	}

	/**
	 * 更新用户注册信息
	 * @return
	 */
	private void updateUserInfo(WechatUserInfo wechatUserInfo) {
		UserInfo userInfo = dao.get(UserInfo.class,wechatUserInfo.getWuiUId());
		userInfo.setUiOpenId(wechatUserInfo.getWuiOpenid());
		userInfo.setUiNickName(wechatUserInfo.getWuiNickName());
		userInfo.setUiHeadimg(wechatUserInfo.getWuiHeadimgurl());
		userInfo.setUiSex(wechatUserInfo.getWuiSex());
		userInfo.setUiUpdateTime(System.currentTimeMillis());
		dao.update(userInfo);
	}

	/**
	 * 更新用户经纬度信息
	 * @return
	 */
	public void updateUserLocation(String latitude, String longitude, String openid) {
		UserInfo db = dao.getUserInfoByOpenid(openid);
		if(db != null){
			db.setUiLatitude(latitude);
			db.setUiLongitude(longitude);
			db.setUiUpdateTime(System.currentTimeMillis());
			db.setUiUpdateUserId(db.getUiId());
			db.setUiUpdateUserName(db.getUiRealName());
			dao.update(db);
		}
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

	/**
	 * 通过openid获取userid
	 * @return
	 */
	public Long getUserIdByOpenid(String openid) {
		UserInfo userInfo = dao.getUserInfoByOpenid(openid);
		if(userInfo != null){
			return userInfo.getUiId();
		}
		return null;
	}

	/**
	 * 获取用户在每个球洞的得分情况
	 * @return
	 */
	private void createNewUserScoreList(Long userId, List<MatchGroupUserScoreBean> list,MatchInfo matchInfo) {
        Integer matchType = matchInfo.getMiType();
        //个人不展示关联比赛的数据
        if ( matchType!=2) {
            MatchGroupUserScoreBean bean = new MatchGroupUserScoreBean();
            //这场比赛我的代表球队
            MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchInfo.getMiId(), null, userId);
            bean.setUserId(userId);
            //本用户的前后半场总得分情况
            List<MatchTotalUserScoreBean> userScoreList = new ArrayList<>();
            //本用户前半场得分情况
            List<Map<String, Object>> uScoreBeforeList = matchDao.getBeforeAfterScoreByUserId(userId,  matchInfo.getMiId(), matchType,0, matchUserGroupMapping.getMugmTeamId());
            createNewUserScore(userScoreList, uScoreBeforeList);
            Integer beforeTotalScore = userScoreList.get(userScoreList.size() - 1).getRodNum();
            //本用户后半场得分情况
            List<Map<String, Object>> uScoreAfterList = matchDao.getBeforeAfterScoreByUserId(userId,  matchInfo.getMiId(), matchType,1, matchUserGroupMapping.getMugmTeamId());
            createNewUserScore(userScoreList, uScoreAfterList);
            Integer afterTotalScore = userScoreList.get(userScoreList.size() - 1).getRodNum();

            bean.setUserScoreTotalList(userScoreList);
            bean.setTotalRodScore(beforeTotalScore + afterTotalScore);
            list.add(bean);
        }
	}


	/**
	 * 格式化用户半场得分
	 */
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
		//平均杆数
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
	public Map<String, Object> getUserDetaliInfoById(String teamIdStr, String matchIdStr, Long userId, String openid) throws UnsupportedEncodingException {
		Map<String, Object> result = new HashMap<>();
		//被查看用户资料
		UserInfo otherUserInfo = getUserInfoByIdDecodeNickName(userId);
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
			result.put("elseIsTeamCaptain", elseIsTeamCaptain > 0);
			result.put("meIsTeamCaptain", meIsTeamCaptain > 0);
		}else if(StringUtils.isNotEmpty(matchIdStr)){
			Long matchId = Long.parseLong(matchIdStr);
			MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
			result.put("matchInfo",matchInfo);

			//我是否是本比赛的围观人员
			Long meIsMatchWatch = matchDao.getIsWatch(myUserId,matchId);
			Long meIsJoinMatchUser = 0L;
			Long meIsMatchCaptain = 0L;
			if(meIsMatchWatch <= 0){
				//我不是围观人 再判断我是否是参赛者
				meIsJoinMatchUser = matchDao.getIsJoinMatchUser(matchId,myUserId);
				if(meIsJoinMatchUser>0){
					//我是否是本比赛的赛长
					meIsMatchCaptain = matchDao.getIsMatchCaptain(matchId,myUserId);
				}
			}
			result.put("meIsMatchWatch", meIsMatchWatch > 0);
			result.put("meIsJoinMatchUser", meIsJoinMatchUser > 0);
			result.put("meIsMatchCaptain", meIsMatchCaptain > 0);

			//被查看用户是否是本比赛的参赛人员
			Long otherIsJoinMatchUser = matchDao.getIsJoinMatchUser(matchId,userId);
			Long otherIsMatchCaptain = 0l;
			Long otherIsMatchWatch = 0l;
			if(otherIsJoinMatchUser>0){
				//被查看用户是否是本比赛的赛长
				otherIsMatchCaptain = matchDao.getIsMatchCaptain(matchId,userId);
			}else{
				//被查看用户是否是本比赛的围观人员
				otherIsMatchWatch = matchDao.getIsWatch(userId,matchId);
				//被查看用户是围观人员，我是参赛者
				if(otherIsMatchWatch>0 && meIsJoinMatchUser >0){
					//查询我所在的比赛分组
					MatchUserGroupMapping matchUserGroupMapping = matchDao.getMatchGroupMappingByUserId(matchId,null,myUserId);
					//被查看用户是否已经被邀请记分
					MatchScoreUserMapping matchScoreUserMapping = matchDao.getMatchScoreUserMapping(matchId, matchUserGroupMapping.getMugmGroupId(), userId);
					if(matchScoreUserMapping == null){
						result.put("otherIsInvitate",false);
					}else{
						result.put("otherIsInvitate",true);
					}
				}
			}
			result.put("otherIsJoinMatchUser", otherIsJoinMatchUser > 0);
			result.put("otherIsMatchCaptain", otherIsMatchCaptain > 0);
			result.put("otherIsMatchWatch", otherIsMatchWatch > 0);
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

	/**
	 * 我的微信信息和注册信息
	 * @return
	 */
	public UserModel getUserModelByOpenid(String openid) throws UnsupportedEncodingException {
		UserModel userModel = new UserModel();
		UserInfo userInfo = getUserInfoByOpenIdDecodeNickName(openid);
		WechatUserInfo wechatUserInfo = getWechatUserByOpenidDecodeNickName(openid);
		userModel.setUser(userInfo);
		userModel.setWechatUser(wechatUserInfo);
		return userModel;
	}

	/**
	 * 通过openid获取用户注册信息
	 * @return
	 */
	public UserInfo getUserInfoByOpenId(String openid) {
		return dao.getUserInfoByOpenid(openid);
	}

	/**
	 * 通过openid获取用户微信信息
	 * @return
	 */
	public WechatUserInfo getWechatUserByOpenid(String openid) {
		return dao.getWechatUserByOpenid(openid);
	}

	/**
	 * 比赛哪里点击用户头像来认领该用户 nhq
	 * @return
	 */
	public String  claimUserScore(String openid,String importUserId,String importUserRealName,String matchId) {
		//把自己的真实姓名改为被认领用户的真实姓名
		UserInfo userInfo = getUserInfoByOpenId(openid);
		Long myUserId = userInfo.getUiId();
		userInfo.setUiRealName(importUserRealName);
		//删除自己的作为观赛者的记录
		dao.deleteImportMatchWatchUserId(openid, matchId);
		//把被认领用户的ID替换为自己的，并把认领用户删除
		updateClaimUserScore(openid, importUserId);
		return String.valueOf(myUserId);
	}
	/**
	 * 查询是否有待认领的成绩
	 * 查询除了我之外，是否存在这个真实姓名 有未导入的数据
	 * @return
	 */
	public List<Map<String, Object>> getHasScoreByClaim(String openid) {
		UserInfo userInfo = getUserInfoByOpenId(openid);
		return dao.getUserCountByRealName(userInfo.getUiRealName());
	}
	/**
	 * 匹配导入的成绩
	 * openid：认领者的openId，chooseIds：被认领者的userId串，由于导入时，同名但不同球队的人会分配不同的ID，所以
	 * 不用再用TeamID ,来判断导入的人了  nhq
	 * @return
	 */
	public void updateClaimUserScore(String openid,String chooseIds) {
		UserInfo userInfo = getUserInfoByOpenId(openid);
		Long myUserId = userInfo.getUiId();
		Long chooseUserId = null;
		//Long chooseTeamId = null;
		//Long chooseMatchId = null;
		if (StringUtils.isNotEmpty(chooseIds)) {
			String[] chooseIdStr = chooseIds.split(",");
			for(int i=0;i<chooseIdStr.length; i++) {
				chooseUserId = Long.parseLong(chooseIdStr[i]);
				//chooseTeamId = Long.parseLong(chooseIdStr[1]);
				//chooseMatchId = Long.parseLong(chooseIdStr[2]);

				//如果存在我和导入的人存在同一个球队，则先删掉自己在该球队的信息
				Long teamId = dao.getIsMyTeammate(myUserId,chooseUserId);
				while (teamId !=null){
					teamDao.deleteFromTeamUserMapping(teamId,myUserId);
					teamId = dao.getIsMyTeammate(myUserId,chooseUserId);
				}

				//如果存在我和导入的人存在同一个比赛，则先删掉自己在该比赛中的信息
				Long matchId = matchDao.getIsMyMatchmate(myUserId,chooseUserId);
				while (matchId !=null){
					matchService.quitMatch(matchId, openid);
					matchId = matchDao.getIsMyMatchmate(myUserId,chooseUserId);
				}


				//更新这个导入用户的 比赛分组mapping 为真实用户的id
				dao.updateImportMatchMappingUserId(chooseUserId, myUserId);
				//更新这个导入用户的 比赛成绩 的用户id为真实用户的id 并设置是否认领为1
				dao.updateImportMatchScoreUserId(chooseUserId, myUserId);
				//更新这个导入用户的 球队及 成绩与积分teamuserpoint的记录 为真实用户的id
				dao.updateImportTeamMappingUserId(chooseUserId, myUserId);
				//删除这个导入用户
				UserInfo chooseUser = dao.get(UserInfo.class, chooseUserId);
				dao.del(chooseUser);
			}
		}
    }
}
