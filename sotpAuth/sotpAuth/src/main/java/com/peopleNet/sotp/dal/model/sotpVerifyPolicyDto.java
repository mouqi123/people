package com.peopleNet.sotp.dal.model;

import java.io.Serializable;
import java.util.Date;

public class sotpVerifyPolicyDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;

	private String policyName;

	private Byte motpTypeId;

	private Byte passwordType;

	private Integer authWindowSize;

	private Integer passwordLength;

	private Integer errorTimes;

	private Byte isUnlock;

	private Integer autoUnlockTime;

	private Byte policyStatus;

	private Integer smsTimeout;

	private Byte deviceCnt;

	private Date createTime;

	private String createUser;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName == null ? null : policyName.trim();
	}

	public Byte getMotpTypeId() {
		return motpTypeId;
	}

	public void setMotpTypeId(Byte motpTypeId) {
		this.motpTypeId = motpTypeId;
	}

	public Byte getPasswordType() {
		return passwordType;
	}

	public void setPasswordType(Byte passwordType) {
		this.passwordType = passwordType;
	}

	public Integer getAuthWindowSize() {
		return authWindowSize;
	}

	public void setAuthWindowSize(Integer authWindowSize) {
		this.authWindowSize = authWindowSize;
	}

	public Integer getPasswordLength() {
		return passwordLength;
	}

	public void setPasswordLength(Integer passwordLength) {
		this.passwordLength = passwordLength;
	}

	public Integer getErrorTimes() {
		return errorTimes;
	}

	public void setErrorTimes(Integer errorTimes) {
		this.errorTimes = errorTimes;
	}

	public Byte getIsUnlock() {
		return isUnlock;
	}

	public void setIsUnlock(Byte isUnlock) {
		this.isUnlock = isUnlock;
	}

	public Integer getAutoUnlockTime() {
		return autoUnlockTime;
	}

	public void setAutoUnlockTime(Integer autoUnlockTime) {
		this.autoUnlockTime = autoUnlockTime;
	}

	public Byte getPolicyStatus() {
		return policyStatus;
	}

	public void setPolicyStatus(Byte policyStatus) {
		this.policyStatus = policyStatus;
	}

	public Integer getSmsTimeout() {
		return smsTimeout;
	}

	public void setSmsTimeout(Integer smsTimeout) {
		this.smsTimeout = smsTimeout;
	}

	public Byte getDeviceCnt() {
		return deviceCnt;
	}

	public void setDeviceCnt(Byte deviceCnt) {
		this.deviceCnt = deviceCnt;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser == null ? null : createUser.trim();
	}
}