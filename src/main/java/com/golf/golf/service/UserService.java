package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.HttpUtil;
import com.golf.common.util.PropertyConst;
import com.golf.golf.common.security.UserModel;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.common.security.WechatUserUtil;
import com.golf.golf.dao.UserDao;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.TeamInfo;
import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;
import com.golf.golf.enums.MatchGroupUserMappingTypeEnum;
import com.golf.golf.enums.UserTypeEnum;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

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
    public void updateUser(UserInfo user) throws Exception {
    	UserInfo db = dao.get(UserInfo.class,WebUtil.getUserIdBySessionId());
    	db.setUiRealName(user.getUiRealName());
    	db.setUiAge(user.getUiAge());
    	db.setUiTelNo(user.getUiTelNo());
		db.setUiEmail(user.getUiEmail());
		db.setUiCraduateSchool(user.getUiCraduateSchool());
		db.setUiCraduateDepartment(user.getUiCraduateDepartment());
		db.setUiMajor(user.getUiMajor());
		db.setUiCraduateTime(user.getUiCraduateTime());
		db.setUiStudentId(user.getUiStudentId());
		db.setUiWorkUnit(user.getUiWorkUnit());
		db.setUiPost(user.getUiPost());
		db.setUiAddress(user.getUiAddress());
		db.setUiHomeCourt(user.getUiHomeCourt());
		db.setUiUpdateTime(System.currentTimeMillis());
		db.setUiUpdateUserId(WebUtil.getUserIdBySessionId());
		db.setUiUpdateUserName(WebUtil.getUserNameBySessionId());
        dao.update(db);
    }

	/**
	 * 更新签名
	 * @return
	 */
	public void updateUserSignature(String signature) {
		UserInfo db = dao.get(UserInfo.class,WebUtil.getUserIdBySessionId());
		db.setUiPersonalizedSignature(signature);
		db.setUiUpdateTime(System.currentTimeMillis());
		db.setUiUpdateUserId(WebUtil.getUserIdBySessionId());
		db.setUiUpdateUserName(WebUtil.getUserNameBySessionId());
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

	/**
	 * 获取我的活动
	 * @return
	 */
	public List<MatchInfo> getCalendarListByUserId(Long cuClub) {
		return dao.getCalendarListByUserId(cuClub);
	}



    /**
     * 详细资料 只有是队友且该球队要求 详细资料时才可见
     * @return
     */
    public boolean userInfoIsOpen(Long userId) {
        //是否是我的队友
        Long teamId = dao.getIsMyTeammate(WebUtil.getUserIdBySessionId(),userId);
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
//		Session登录
		HttpSession session = WebUtil.getSessionById();
		UserModel userModel = WebUtil.getUserModelBySessionId();
		if(userModel == null){
			userModel = new UserModel();
		}
		userModel.setOpenId(openid);

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

			//下载头像
			/*String headImgPath = PropertyConst.HEADIMG_PATH + openid + ".png";
			String path = WebUtil.getRealPath(PropertyConst.HEADIMG_PATH);
			if(StringUtils.isNotEmpty(wechatUserInfo.getWuiHeadimgurl())){
				HttpUtil.downloadPicture(wechatUserInfo.getWuiHeadimgurl(), path,openid + ".png");
			}*/
//			wechatUserInfo.setWuiHeadimg(headImgPath);
			wechatUserInfo.setWuiIsValid(1);
			wechatUserInfo.setCreateTime(System.currentTimeMillis());

			//创建一条用户信息
			saveOrUpdateUserInfo(userInfo, openid, wechatUserInfo);
			userModel.setUser(userInfo);
			wechatUserInfo.setWuiUId(userInfo.getUiId());
			dao.update(wechatUserInfo);
			userModel.setWechatUser(wechatUserInfo);
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
			userModel.setUser(userInfo);
			wechatUserInfo.setWuiUId(userInfo.getUiId());
			dao.save(wechatUserInfo);

			userModel.setWechatUser(wechatUserInfo);
		}
		//登录
		session.setAttribute(WechatUserUtil.USER_SESSION_NAME, userModel);
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
			userInfo.setUiCreateUserId(WebUtil.getUserIdBySessionId());
			userInfo.setUiCreateUserName(WebUtil.getUserNameBySessionId());
			dao.save(userInfo);
		}else{
			userInfo.setUiUpdateTime(System.currentTimeMillis());
			userInfo.setUiUpdateUserId(WebUtil.getUserIdBySessionId());
			userInfo.setUiUpdateUserName(WebUtil.getUserNameBySessionId());
			dao.update(userInfo);
		}
		return userInfo;
	}

	/**
	 * 更新用户经纬度信息
	 * @return
	 */
	public void updateUserLocation(String latitude, String longitude) {
		UserInfo db = dao.get(UserInfo.class,WebUtil.getUserIdBySessionId());
		db.setUiLatitude(latitude);
		db.setUiLongitude(longitude);
		db.setUiUpdateTime(System.currentTimeMillis());
		db.setUiUpdateUserId(WebUtil.getUserIdBySessionId());
		db.setUiUpdateUserName(WebUtil.getUserNameBySessionId());
		dao.update(db);
	}
}
