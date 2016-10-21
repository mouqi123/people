package com.peopleNet.sotp.dal.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.peopleNet.sotp.context.Contextable;

public class invokeInfoDto {
	private Integer id;

	private String userId;

	private String userPhone;

	private String pluginId;

	private String interfaceName;

	private Date invoketime;

	private String location;

	private String ip;

	private Integer status;

	private String errorcode;

	private String remark;

	private Integer risklevel;

	private String requestMsg;

	private String responseMsg;

	private String appCode;

	private String busiCode;

	private String tableName;

	public invokeInfoDto() {
		this.userId = "nil";
		this.userPhone = "nil";
		this.pluginId = "nil";
		this.interfaceName = "nil";
		this.invoketime = new Date(System.currentTimeMillis());
		this.location = "nil";
		this.ip = "nil";
		this.status = 0;
		this.errorcode = "-1";
		this.remark = "nil";

		this.risklevel = 0;
		this.requestMsg = "nil";
		this.responseMsg = "nil";
		this.appCode = "nil";
		this.busiCode = "nil";

		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
		this.tableName = formatter.format(currentTime);
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getBusiCode() {
		return busiCode;
	}

	public void setBusiCode(String busiCode) {
		this.busiCode = busiCode;
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
		this.userId = userId == null ? null : userId.trim();
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone == null ? null : userPhone.trim();
	}

	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId == null ? null : pluginId.trim();
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public Date getInvoketime() {
		return invoketime;
	}

	public void setInvoketime(Date invoketime) {
		this.invoketime = invoketime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location == null ? null : location.trim();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip == null ? null : ip.trim();
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode == null ? null : errorcode.trim();
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}

	public Integer getRisklevel() {
		return risklevel;
	}

	public void setRisklevel(Integer risklevel) {
		this.risklevel = risklevel;
	}

	public String getRequestMsg() {
		return requestMsg;
	}

	public void setRequestMsg(String requestMsg) {
		this.requestMsg = requestMsg == null ? null : requestMsg.trim();
	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg == null ? null : responseMsg.trim();
	}

	public String getTableName() {
		if (this.tableName == null || "".equals(this.tableName)) {
			Date currentTime = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
			tableName = formatter.format(currentTime);
		}
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("user_id:" + ((null == userId) ? "" : userId));
		sb.append(" user_phone:" + ((null == userPhone) ? "" : userPhone));
		sb.append(" plugin_id:" + ((null == pluginId) ? "" : pluginId));
		sb.append(" interface_name:" + ((null == interfaceName) ? "" : interfaceName));
		sb.append(" invoke_time:" + ((null == invoketime) ? "" : invoketime));
		sb.append(" location:" + ((null == location) ? "" : location));
		sb.append(" ip:" + ((null == ip) ? "" : ip));
		sb.append(" status:" + ((null == status) ? "" : status));
		sb.append(" errorcode:" + ((null == errorcode) ? "" : errorcode));
		sb.append(" remark:" + ((null == remark) ? "" : remark));
		sb.append(" risklevel:" + ((null == risklevel) ? "" : risklevel));
		sb.append(" requestMsg:" + ((null == requestMsg) ? "" : requestMsg));
		sb.append(" responseMsg:" + ((null == responseMsg) ? "" : responseMsg));
		sb.append(" appCode:" + ((null == appCode) ? "" : appCode));
		sb.append(" busiCode:" + ((null == busiCode) ? "" : busiCode));
		return sb.toString();
	}

	public void setValueByUserContext(Contextable context) {
		this.userId = (null != context.getUserId() ? context.getUserId() : "nil");
		this.userPhone = (null != context.getUserPhone() ? context.getUserPhone() : "nil");
		this.pluginId = (null != context.getPluginId() ? context.getPluginId() : "nil");
		this.interfaceName = (null != context.getInterfaceName() ? context.getInterfaceName() : "nil");
		this.location = (null != context.getLocation() ? context.getLocation() : "nil");
		this.ip = (null != context.getUserIp() ? context.getUserIp() : "nil");
		this.status = context.getStatus();
		this.appCode = (null != context.getAppId() ? context.getAppId() : "nil");
	}
}