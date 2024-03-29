package com.golf.golf.bean;

import com.golf.golf.db.UserInfo;

import java.util.List;

/**
 * 用户比赛分组mapping bean
 * Created by dev on 17-2-10
 */
public class MatchUserGroupMappingBean {
    private Long groupId;
    private String groupName;
    private Integer userType;
    private List<UserInfo> userList;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public List<UserInfo> getUserList() {
        return userList;
    }

    public void setUserList(List<UserInfo> userList) {
        this.userList = userList;
    }
}