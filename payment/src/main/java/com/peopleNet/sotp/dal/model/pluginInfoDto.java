package com.peopleNet.sotp.dal.model;

import java.io.Serializable;
import java.util.Date;

public class pluginInfoDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;

	private String pluginId;

	private String pluginKey;

	private Integer pluginType;

	private Integer status;

	private String deviceInfo;

	private Date startTime;

	private Date genTime;

	private String holdInfo;

	private String protectCode;

	private String bindUserid;

	private String bindUserphone;

	private Integer verifyErrcnt;

	private Integer totalUsecnt;

	private Integer totalErrcnt;

	private Date errDay;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId == null ? null : pluginId.trim();
	}

	public String getPluginKey() {
		return pluginKey;
	}

	public void setPluginKey(String pluginKey) {
		this.pluginKey = pluginKey == null ? null : pluginKey.trim();
	}

	public Integer getPluginType() {
		return pluginType;
	}

	public void setPluginType(Integer pluginType) {
		this.pluginType = pluginType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo == null ? null : deviceInfo.trim();
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getGenTime() {
		return genTime;
	}

	public void setGenTime(Date genTime) {
		this.genTime = genTime;
	}

	public String getHoldInfo() {
		return holdInfo;
	}

	public void setHoldInfo(String holdInfo) {
		this.holdInfo = holdInfo == null ? null : holdInfo.trim();
	}

	public String getProtectCode() {
		return protectCode;
	}

	public void setProtectCode(String protectCode) {
		this.protectCode = protectCode == null ? null : protectCode.trim();
	}

	public String getBindUserid() {
		return bindUserid;
	}

	public void setBindUserid(String bindUserid) {
		this.bindUserid = bindUserid == null ? null : bindUserid.trim();
	}

	public String getBindUserphone() {
		return bindUserphone;
	}

	public void setBindUserphone(String bindUserphone) {
		this.bindUserphone = bindUserphone == null ? null : bindUserphone.trim();
	}

	public Integer getVerifyErrcnt() {
		return verifyErrcnt;
	}

	public void setVerifyErrcnt(Integer verifyErrcnt) {
		this.verifyErrcnt = verifyErrcnt;
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

	public Date getErrDay() {
		return errDay;
	}

	public void setErrDay(Date errDay) {
		this.errDay = errDay;
	}
}