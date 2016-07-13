package com.people.sotp.dataobject;

import java.sql.Timestamp;

import com.people.sotp.commons.base.BaseDO;

/**
 * 账户表
 * 
 * @author liuxx
 */
public class AccountManageDO extends BaseDO {

	private long id;
	private String memberId;
	private String accountName;
	private String accountType;
	private Timestamp createTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

}
