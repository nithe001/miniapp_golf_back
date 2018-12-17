package com.golf.golf.bean;

/**
 * 用户权限 bean
 * Created by dev on 17-2-10
 */
public class UserAuthorityBean {

    private Long userId;
    private String userName;
    private String userShowName;
    private Integer userLevel;//用户级别（对服务器或者项目）

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserShowName() {
        return userShowName;
    }

    public void setUserShowName(String userShowName) {
        this.userShowName = userShowName;
    }

    public Integer getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }
}
