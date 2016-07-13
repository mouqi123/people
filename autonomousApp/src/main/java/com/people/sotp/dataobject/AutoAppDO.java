package com.people.sotp.dataobject;

import java.util.HashMap;
import java.util.Map;

public class AutoAppDO {
	
	private String service;
	private String phoneNum; //手机号
	private String verCode; //短信验证码
	private String loginType; //登陆方式
	private String devinfo; //登陆信息
	private String password;
	private String userInfo;
	private String sotpInfo;
	private String businessName;
	private String feedbackInfo;
	private String grade;
	private String oldPasswrd;
	private String newPasswrd;
	
	
	
	
	
	public String getOldPasswrd() {
		return oldPasswrd;
	}
	public void setOldPasswrd(String oldPasswrd) {
		this.oldPasswrd = oldPasswrd;
	}
	public String getNewPasswrd() {
		return newPasswrd;
	}
	public void setNewPasswrd(String newPasswrd) {
		this.newPasswrd = newPasswrd;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getFeedbackInfo() {
		return feedbackInfo;
	}
	public void setFeedbackInfo(String feedbackInfo) {
		this.feedbackInfo = feedbackInfo;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public String getSotpInfo() {
		return sotpInfo;
	}
	public void setSotpInfo(String sotpInfo) {
		this.sotpInfo = sotpInfo;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getVerCode() {
		return verCode;
	}
	public void setVerCode(String verCode) {
		this.verCode = verCode;
	}
	public String getLoginType() {
		return loginType;
	}
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
	public String getDevinfo() {
		return devinfo;
	}
	public void setDevinfo(String devinfo) {
		this.devinfo = devinfo;
	}
	
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	
	
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}
	public Map<String, Object>getSignParMap(){
		Map<String, Object>paramMap =new HashMap<String, Object>();
		paramMap.put("phoneNum", phoneNum);
		paramMap.put("verCode", verCode);
		paramMap.put("loginType", loginType);
		paramMap.put("devinfo", devinfo);
		paramMap.put("service", service);
		return paramMap;
		
	}
}
