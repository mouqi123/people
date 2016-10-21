package com.people.sotp.dataobject;


import java.sql.Timestamp;

import com.people.sotp.commons.base.BaseDO;

public class OrderDO extends BaseDO{

	private long id;
	private long memberId;
	private String orderNumber;
	private String orderMoney;
	private Timestamp orderStart; 
	private Timestamp orderEnd;
	private Integer orderStatus;
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
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getOrderMoney() {
		return orderMoney;
	}
	public void setOrderMoney(String orderMoney) {
		this.orderMoney = orderMoney;
	}
	public Timestamp getOrderStart() {
		return orderStart;
	}
	public void setOrderStart(Timestamp orderStart) {
		this.orderStart = orderStart;
	}
	public Timestamp getOrderEnd() {
		return orderEnd;
	}
	public void setOrderEnd(Timestamp orderEnd) {
		this.orderEnd = orderEnd;
	}
	public Integer getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	
}
