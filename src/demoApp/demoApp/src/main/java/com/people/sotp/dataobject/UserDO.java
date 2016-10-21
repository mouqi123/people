package com.people.sotp.dataobject;

import java.sql.Timestamp;

import com.people.sotp.commons.base.BaseDO;

public class UserDO extends BaseDO{
	
	private long id;
	private String userName;
	private String pwd;
	private String phoneNum;
	private String identityNum;
	private String cardNum;
	private String balance;
	private String holdInfo;
	

	private Timestamp  createTime;
	
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getIdentityNum() {
		return identityNum;
	}
	public void setIdentityNum(String identityNum) {
		this.identityNum = identityNum;
	}
	public String getCardNum() {
		return cardNum;
	}
	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	
	public String getHoldInfo() {
		return holdInfo;
	}
	public void setHoldInfo(String holdInfo) {
		this.holdInfo = holdInfo;
	}
	

}
