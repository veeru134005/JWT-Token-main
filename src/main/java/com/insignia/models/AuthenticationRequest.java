package com.insignia.models;

import java.io.Serializable;

public class AuthenticationRequest implements Serializable {

	private String userId;
	private String applicationId;
	private String tenantId;
	private String password;
	private Integer expirationTime;
	private boolean isRememberMeSelected;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Integer expirationTime) {
		this.expirationTime = expirationTime;
	}

	public boolean isRememberMeSelected() {
		return isRememberMeSelected;
	}

	public void setRememberMeSelected(boolean isRememberMeSelected) {
		this.isRememberMeSelected = isRememberMeSelected;
	}

	// need default constructor for JSON Parsing
	public AuthenticationRequest() {

	}

	public String getCustomeUserName() {

		return new StringBuilder().append(this.userId + this.applicationId + this.tenantId).toString();
	}
}
