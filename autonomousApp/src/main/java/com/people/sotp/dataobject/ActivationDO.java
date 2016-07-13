package com.people.sotp.dataobject;

import com.people.sotp.commons.base.BaseDO;

public class ActivationDO extends BaseDO{
	
	private String modelName;
	private String modelType;
	private String time;
	private String sotpId;
	private String status;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getSotpId() {
		return sotpId;
	}
	public void setSotpId(String sotpId) {
		this.sotpId = sotpId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getModelType() {
		return modelType;
	}
	public void setModelType(String modelType) {
		this.modelType = modelType;
	}
	

}
