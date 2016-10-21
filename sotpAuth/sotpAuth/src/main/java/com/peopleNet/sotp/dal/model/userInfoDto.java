package com.peopleNet.sotp.dal.model;

import java.io.Serializable;

public class userInfoDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;

	private String userId;

	private String userName;

	private String userPwd;

	private String userIdentitytype;

	private String userIdentitynum;

	private String userPhone;

	private String userAddress;

	private String userMail;

	private String realName;

	private String gender;

	private String appId;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName == null ? null : userName.trim();
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd == null ? null : userPwd.trim();
	}

	public String getUserIdentitytype() {
		return userIdentitytype;
	}

	public void setUserIdentitytype(String userIdentitytype) {
		this.userIdentitytype = userIdentitytype == null ? null : userIdentitytype.trim();
	}

	public String getUserIdentitynum() {
		return userIdentitynum;
	}

	public void setUserIdentitynum(String userIdentitynum) {
		this.userIdentitynum = userIdentitynum == null ? null : userIdentitynum.trim();
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone == null ? null : userPhone.trim();
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress == null ? null : userAddress.trim();
	}

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail == null ? null : userMail.trim();
	}
}