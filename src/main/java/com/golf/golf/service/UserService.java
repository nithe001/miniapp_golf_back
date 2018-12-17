package com.golf.golf.service;

import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.golf.common.IBaseService;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.HttpUtil;
import com.golf.common.util.PropertyConst;
import com.golf.golf.common.security.UserModel;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.dao.UserDao;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;
import com.golf.golf.enums.UserTypeEnum;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * 根据用户id取得微信的用户信息
     * @param userId 用户id
     * @return
     */
    public WechatUserInfo getWechatUserById(Long userId){
        return dao.get(WechatUserInfo.class, userId);
    }

    /**
     * 根据用户id取得用户信息
     * @param userId 用户id
     * @return
     */
    public UserInfo getUserById(Long userId){
        return dao.get(UserInfo.class, userId);
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
     * 根据用户手机号取得用户信息
     * @param telNo 手机号码
     * @return
     */
    public UserInfo getUserByTelNo(String telNo){
        return dao.getUserByTelNo(telNo);
    }

	/**
	 * 新增用户微信信息
	 * @param wxMaUserInfo
	 */
	public WechatUserInfo saveWechatUser(WxMaUserInfo wxMaUserInfo) throws IOException {
		String openid = wxMaUserInfo.getOpenId();
		String headImgPath = PropertyConst.HEADIMG_PATH + File.separator +openid + ".png";
		String path = WebUtil.getRealPath(PropertyConst.HEADIMG_PATH);
		if(StringUtils.isNotEmpty(wxMaUserInfo.getAvatarUrl())){
			HttpUtil.downloadPicture(wxMaUserInfo.getAvatarUrl(), path,openid + ".png");
		}
		WechatUserInfo wechatUserInfo = new WechatUserInfo();
		wechatUserInfo.setOpenid(wxMaUserInfo.getOpenId());
		wechatUserInfo.setNickName(wxMaUserInfo.getNickName());
		wechatUserInfo.setSex(wxMaUserInfo.getGender());
		wechatUserInfo.setLanguage(wxMaUserInfo.getLanguage());
		wechatUserInfo.setCity(wxMaUserInfo.getCity());
		wechatUserInfo.setProvince(wxMaUserInfo.getProvince());
		wechatUserInfo.setCountry(wxMaUserInfo.getCountry());
		wechatUserInfo.setHeadimgurl(wxMaUserInfo.getAvatarUrl());
		wechatUserInfo.setHeadimg(headImgPath);
		wechatUserInfo.setUnionid(wxMaUserInfo.getUnionId());
		wechatUserInfo.setWatermarkAppid(wxMaUserInfo.getWatermark().getAppid());
		wechatUserInfo.setWatermarkTimestamp(wxMaUserInfo.getWatermark().getTimestamp());
		wechatUserInfo.setIsValid(1);
		wechatUserInfo.setCreateTime(System.currentTimeMillis());
		dao.save(wechatUserInfo);
		return wechatUserInfo;
	}

    /**
     * 更新微信用户信息
     */
    public void updateWechatUser(WxMaUserInfo wxMaUserInfo, WechatUserInfo dbWechatUserInfo) throws IOException {
        String openid = wxMaUserInfo.getOpenId();
		String headImgPath = PropertyConst.HEADIMG_PATH + File.separator +openid + ".png";
		String path = WebUtil.getRealPath(PropertyConst.HEADIMG_PATH);
		if(StringUtils.isNotEmpty(wxMaUserInfo.getAvatarUrl())){
			HttpUtil.downloadPicture(wxMaUserInfo.getAvatarUrl(), path,openid + ".png");
		}
		dbWechatUserInfo.setOpenid(wxMaUserInfo.getOpenId());
		dbWechatUserInfo.setNickName(wxMaUserInfo.getNickName());
		dbWechatUserInfo.setSex(wxMaUserInfo.getGender());
		dbWechatUserInfo.setLanguage(wxMaUserInfo.getLanguage());
		dbWechatUserInfo.setCity(wxMaUserInfo.getCity());
		dbWechatUserInfo.setProvince(wxMaUserInfo.getProvince());
		dbWechatUserInfo.setCountry(wxMaUserInfo.getCountry());
		dbWechatUserInfo.setHeadimgurl(wxMaUserInfo.getAvatarUrl());
		dbWechatUserInfo.setHeadimg(headImgPath);
		dbWechatUserInfo.setUnionid(wxMaUserInfo.getUnionId());
		dbWechatUserInfo.setWatermarkAppid(wxMaUserInfo.getWatermark().getAppid());
		dbWechatUserInfo.setWatermarkTimestamp(wxMaUserInfo.getWatermark().getTimestamp());
		dbWechatUserInfo.setIsValid(1);
		dbWechatUserInfo.setCreateTime(System.currentTimeMillis());
        dao.update(dbWechatUserInfo);
    }


    /**
     * 保存用户注册信息
     * @param user
     */
    public Long saveUser(UserInfo user,String captcha) throws UnsupportedEncodingException, WxErrorException {
        user.setType(Integer.parseInt(UserTypeEnum.DOMESTIC.text()));//普通用户
        user.setIsValid(1);
        user.setCreateTime(System.currentTimeMillis());
        Long uid = dao.save(user);
		UserModel userModel = new UserModel();
		userModel.setUser(user);
		UserUtil.login(userModel);
		return uid;
    }


    /**
     * 保存用户信息
     * @param user
     */
    public void updateUser(UserInfo user) throws Exception {
    	UserInfo db = dao.get(UserInfo.class,user.getId());
		db.setEmail(user.getEmail());
		db.setUpdateTime(System.currentTimeMillis());
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
		UserModel user = null;
		WechatUserInfo dbUser = dao.getUserInfoByOpenId(openId);
		if(dbUser != null){
			user = new UserModel();
			user.setWechatUser(dbUser);
			if(dbUser.getId() != null){
				UserInfo UserInfo = dao.get(UserInfo.class,dbUser.getId());
				user.setUser(UserInfo);
			}
		}
		return user;
	}

	/**
	 * 获取我的活动
	 * @return
	 */
	public List<MatchInfo> getCalendarListByUserId(Long cuClub) {
		return dao.getCalendarListByUserId(cuClub);
	}

	public void updateWUser(WechatUserInfo userInfo, String nickName, String avatarUrl, String gender, String province, String city, String country) throws IOException {
		userInfo.setNickName(nickName);
		String openid = userInfo.getOpenid();
		String headImgPath = PropertyConst.HEADIMG_PATH + File.separator +openid + ".png";
		String path = WebUtil.getRealPath(PropertyConst.HEADIMG_PATH);
		if(StringUtils.isNotEmpty(avatarUrl)){
			HttpUtil.downloadPicture(avatarUrl, path,openid + ".png");
		}
		userInfo.setSex(gender);
		userInfo.setHeadimg(headImgPath);
		userInfo.setProvince(province);
		userInfo.setCity(city);
		userInfo.setCountry(country);
		dao.update(userInfo);
	}
}
