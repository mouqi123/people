package com.peopleNet.sotp.dal.model;

import java.io.Serializable;

public class appVersionInfoDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;

	private String version;

	private Integer app_type;

	private String app_key;

	private String hash_value;

	private String signature;

	private Integer status;

	private String version_desc;

	private String app_code;

	private Integer app_info_id;

	private String sdk_secret;

	public appVersionInfoDto() {

	}

	public appVersionInfoDto(String app_code) {
		this.app_code = app_code;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Integer getApp_type() {
		return app_type;
	}

	public void setApp_type(Integer app_type) {
		this.app_type = app_type;
	}

	public String getHash_value() {
		return hash_value;
	}

	public void setHash_value(String hash_value) {
		this.hash_value = hash_value;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getVersion_desc() {
		return version_desc;
	}

	public void setVersion_desc(String version_desc) {
		this.version_desc = version_desc;
	}

	public String getApp_code() {
		return app_code;
	}

	public void setApp_code(String app_code) {
		this.app_code = app_code;
	}

	public Integer getApp_info_id() {
		return app_info_id;
	}

	public void setApp_info_id(Integer app_info_id) {
		this.app_info_id = app_info_id;
	}

	public String getApp_key() {
		return app_key;
	}

	public void setApp_key(String app_key) {
		this.app_key = app_key;
	}

	public String getSdk_secret() {
		return sdk_secret;
	}

	public void setSdk_secret(String sdk_secret) {
		this.sdk_secret = sdk_secret;
	}

}
