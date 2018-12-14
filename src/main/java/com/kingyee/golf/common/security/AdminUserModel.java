package com.kingyee.golf.common.security;

import java.io.Serializable;

public class AdminUserModel implements Serializable{
	private Long id;
	private String name;
	private String showName;
	private Long role;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShowName() { return showName; }
	public void setShowName(String showName) { this.showName = showName; }
	public Long getRole() { return role; }
	public void setRole(Long role) { this.role = role; }
}
