package com.people.sotp.dataobject;

import java.sql.Timestamp;

import com.people.sotp.commons.base.BaseDO;

public class DealLogDO extends BaseDO{
	
	private Long id;
	private Timestamp dealDate;
	private String phone;
	private String dealInfo;
	private String pcInfo;
	private int status;
	private String data;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Timestamp getDealDate() {
		return dealDate;
	}
	public void setDealDate(Timestamp dealDate) {
		this.dealDate = dealDate;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getDealInfo() {
		return dealInfo;
	}
	public void setDealInfo(String dealInfo) {
		this.dealInfo = dealInfo;
	}
	public String getPcInfo() {
		return pcInfo;
	}
	public void setPcInfo(String pcInfo) {
		this.pcInfo = pcInfo;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	

}
