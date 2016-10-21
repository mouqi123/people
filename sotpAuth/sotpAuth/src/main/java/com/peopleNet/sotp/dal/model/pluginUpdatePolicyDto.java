package com.peopleNet.sotp.dal.model;

import java.util.Date;

public class pluginUpdatePolicyDto {
	private Integer id;

	private String policyName;

	private Integer updateCycle;

	private Integer totalUsecnt;

	private Integer totalErrcnt;

	private Byte policyStatus;

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

	public Integer getUpdateCycle() {
		return updateCycle;
	}

	public void setUpdateCycle(Integer updateCycle) {
		this.updateCycle = updateCycle;
	}

	public Integer getTotalUsecnt() {
		return totalUsecnt;
	}

	public void setTotalUsecnt(Integer totalUsecnt) {
		this.totalUsecnt = totalUsecnt;
	}

	public Integer getTotalErrcnt() {
		return totalErrcnt;
	}

	public void setTotalErrcnt(Integer totalErrcnt) {
		this.totalErrcnt = totalErrcnt;
	}

	public Byte getPolicyStatus() {
		return policyStatus;
	}

	public void setPolicyStatus(Byte policyStatus) {
		this.policyStatus = policyStatus;
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