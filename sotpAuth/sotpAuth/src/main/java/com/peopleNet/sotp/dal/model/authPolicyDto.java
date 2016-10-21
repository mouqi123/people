package com.peopleNet.sotp.dal.model;

import java.io.Serializable;

public class authPolicyDto implements Serializable {
	/**
	 * 认证策略类
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;

	private String serviceCode;

	private String serviceName;

	private Integer status;

	private String serviceDesc;

	private Integer busiId;

	private Integer authFactorNum;

	private Integer dynamicPolicy;

	private Integer serviceType;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getServiceDesc() {
		return serviceDesc;
	}

	public void setServiceDesc(String serviceDesc) {
		this.serviceDesc = serviceDesc;
	}

	public Integer getBusiId() {
		return busiId;
	}

	public void setBusiId(Integer busiId) {
		this.busiId = busiId;
	}

	public Integer getAuthFactorNum() {
		return authFactorNum;
	}

	public void setAuthFactorNum(Integer authFactorNum) {
		this.authFactorNum = authFactorNum;
	}

	public Integer getDynamicPolicy() {
		return dynamicPolicy;
	}

	public void setDynamicPolicy(Integer dynamicPolicy) {
		this.dynamicPolicy = dynamicPolicy;
	}

	public Integer getServiceType() {
		return serviceType;
	}

	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}

}
