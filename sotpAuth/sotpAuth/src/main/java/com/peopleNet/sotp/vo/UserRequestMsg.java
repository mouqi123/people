package com.peopleNet.sotp.vo;

import java.util.HashMap;
import java.util.Map;

public class UserRequestMsg {

	private int type;

	private String userId;

	private String phoneNum;

	private String sms;

	private String sotpPayInfo;

	private String devInfo;

	private String appId;

	private String appKey;

	private String nonce_str;

	private String sign;

	private String sotpId;

	private String sotpCode;

	private String sotpCodePara;

	private String activeCode;

	private String dataInfo;

	private String protect;

	private String action;

	private String holdinfo;

	private String chello;

	private String cauth;

	private String cdata;

	private String pin;

	private String deldev;

	private String randa;

	private String randb;

	public String getSotpCodePara() {
		return sotpCodePara;
	}

	public void setSotpCodePara(String sotpCodePara) {
		this.sotpCodePara = sotpCodePara;
	}

	public String getSotpCode() {
		return sotpCode;
	}

	public void setSotpCode(String sotpCode) {
		this.sotpCode = sotpCode;
	}

	public String getSotpId() {
		return sotpId;
	}

	public void setSotpId(String sotpId) {
		this.sotpId = sotpId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getSms() {
		return sms;
	}

	public void setSms(String sms) {
		this.sms = sms;
	}

	public String getSotpPayInfo() {
		return sotpPayInfo;
	}

	public void setSotpPayInfo(String sotpPayInfo) {
		this.sotpPayInfo = sotpPayInfo;
	}

	public String getDevInfo() {
		return devInfo;
	}

	public void setDevInfo(String devInfo) {
		this.devInfo = devInfo;
	}

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getActiveCode() {
		return activeCode;
	}

	public void setActiveCode(String activeCode) {
		this.activeCode = activeCode;
	}

	public String getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(String dataInfo) {
		this.dataInfo = dataInfo;
	}

	public String getProtect() {
		return protect;
	}

	public void setProtect(String protect) {
		this.protect = protect;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getHoldinfo() {
		return holdinfo;
	}

	public void setHoldinfo(String holdinfo) {
		this.holdinfo = holdinfo;
	}

	public String getChello() {
		return chello;
	}

	public void setChello(String chello) {
		this.chello = chello;
	}

	public String getCauth() {
		return cauth;
	}

	public void setCauth(String cauth) {
		this.cauth = cauth;
	}

	public String getCdata() {
		return cdata;
	}

	public void setCdata(String cdata) {
		this.cdata = cdata;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getDeldev() {
		return deldev;
	}

	public void setDeldev(String deldev) {
		this.deldev = deldev;
	}

	public String getRanda() {
		return randa;
	}

	public void setRanda(String randa) {
		this.randa = randa;
	}

	public String getRandb() {
		return randb;
	}

	public void setRandb(String randb) {
		this.randb = randb;
	}

	public Map<String, Object> getSignParaMap() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appId", appId);
		paramMap.put("appKey", appKey);
		paramMap.put("type", Integer.toString(type));
		paramMap.put("userId", userId);
		paramMap.put("phoneNum", phoneNum);
		paramMap.put("sms", sms);
		paramMap.put("sotpPayInfo", sotpPayInfo);
		paramMap.put("devInfo", devInfo);
		paramMap.put("nonce_str", nonce_str);
		paramMap.put("sign", sign);
		paramMap.put("sotpCode", sotpCode);
		paramMap.put("sotpId", sotpId);
		paramMap.put("sotpCodePara", sotpCodePara);
		paramMap.put("activeCode", activeCode);
		paramMap.put("dataInfo", dataInfo);
		paramMap.put("protect", protect);
		paramMap.put("action", action);
		paramMap.put("holdinfo", holdinfo);
		paramMap.put("chello", chello);
		paramMap.put("cauth", cauth);
		paramMap.put("cdata", cdata);
		paramMap.put("pin", pin);
		paramMap.put("deldev", deldev);
		paramMap.put("randa", randa);
		paramMap.put("randb", randb);

		return paramMap;
	}
}
