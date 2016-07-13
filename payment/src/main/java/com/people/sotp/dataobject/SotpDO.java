package com.people.sotp.dataobject;

public class SotpDO {
	// 业务类型
	private Integer type;
	// 手机号
	private String phoneNum;
	// 登录密码
	private String pwd;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

}
