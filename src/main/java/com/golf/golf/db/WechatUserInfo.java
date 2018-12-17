package com.golf.golf.db;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * WechatUserInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "wechat_user_info")
public class WechatUserInfo extends AbstractWechatUserInfo implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public WechatUserInfo() {
	}

	/** full constructor */
	public WechatUserInfo(Long UId, Integer subscribe, String openid,
			String nickName, String sex, String city, String country,
			String province, String language, String headimgurl,
			Long subscribeTime, String unionid, String remark, String headimg,
			Integer isValid, Long createTime, Long updateTime, String watermarkAppid, String watermarkTimestamp) {
		super(UId, subscribe, openid, nickName, sex, city, country, province,
				language, headimgurl, subscribeTime, unionid, remark, headimg,
				isValid, createTime, updateTime, watermarkAppid, watermarkTimestamp);
	}

}
