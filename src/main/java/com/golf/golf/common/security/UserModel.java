package com.golf.golf.common.security;

import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;

import java.io.Serializable;

public class UserModel implements Serializable{

	private WechatUserInfo wechatUser;
	private UserInfo user;
	private String sessionId;

    public WechatUserInfo getWechatUser() {
        return wechatUser;
    }

    public void setWechatUser(WechatUserInfo wechatUser) {
        this.wechatUser = wechatUser;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
