package com.people.sotp.dataobject;

import com.people.sotp.commons.base.BaseDO;

public class DealTacticsDO extends BaseDO {
	/**
	 * liuxx 交易策略
	 */

	private long id;
	private long memberId;
	private String lastMoney; // 支付策略的金额
	private String lastTime; // 支付策略的时间间隔
	private String payMoney; // 定义支付金额是否需要认证
	private String limitMoney; // 消费金额
	private String createTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMemberId() {
		return memberId;
	}

	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}

	public String getLastMoney() {
		return lastMoney;
	}

	public void setLastMoney(String lastMoney) {
		this.lastMoney = lastMoney;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}

	public String getLimitMoney() {
		return limitMoney;
	}

	public void setLimitMoney(String limitMoney) {
		this.limitMoney = limitMoney;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}
