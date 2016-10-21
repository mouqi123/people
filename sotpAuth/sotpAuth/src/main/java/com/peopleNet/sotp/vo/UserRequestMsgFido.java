package com.peopleNet.sotp.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserRequestMsgFido {

	private String userId;

	private String phoneNum;

	private String sms;

	private String sotpPayInfo;

	private String devInfo;

	private String appId;

	private String appKey;

	private String nonce_str;

	private String sign;
	
	public String getClientSign() {
		return clientSign;
	}

	public void setClientSign(String clientSign) {
		this.clientSign = clientSign;
	}

	private String clientSign;

	private String pluginId;

	private String pluginHash;

	private String sotpCode;

	private String sotpCodePara;

	private String activeCode;

	private String dataInfo;

	private String newprotect;

	// 用于同步密钥时使用
	private String action;

	private String holdinfo;

	private String businessName;

	private String chello;

	private String cauth;

	private String cdata;

	private String pin;

	private String deldev;

	private String randa;

	private String randb;

	private String service;

	private String version;

	private String envInfo;

	private String header;

	private String upv;

	private String userInfo;

	private String appInfo;

	private String tradeInfo;

	private String auth;

	private String challengeAns;

	private String pluginSign;

	private String attachedInfo;

	private String location;

	private String ip;

	private String wifiInfo;

	private String useCount;

	private String delPluginId;

	private String activePluginId;

	private String pluginVersion;

	private String alias;

	private String businessId;

	private String shareKey;

	private String flag;

	private String transEncryData;

	private String grade;

	private String op;

	// 支付\转账卡号
	private String payCard;

	// 支付\转账卡类型
	private String payCardType;

	// 收账卡号
	private String recCard;

	// 设备ID
	private String deviceId;

	// 价格
	private BigDecimal price;

	// 行为:支付\转账\理财等
	private String payAction;

	// 收款人
	private String payee;

	public String getPayCard() {
		return payCard;
	}

	public void setPayCard(String payCard) {
		this.payCard = payCard;
	}

	public String getPayCardType() {
		return payCardType;
	}

	public void setPayCardType(String payCardType) {
		this.payCardType = payCardType;
	}

	public String getRecCard() {
		return recCard;
	}

	public void setRecCard(String recCard) {
		this.recCard = recCard;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getPayAction() {
		return payAction;
	}

	public void setPayAction(String payAction) {
		this.payAction = payAction;
	}

	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getDelPluginId() {
		return delPluginId;
	}

	public void setDelPluginId(String delPluginId) {
		this.delPluginId = delPluginId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getWifiInfo() {
		return wifiInfo;
	}

	public void setWifiInfo(String wifiInfo) {
		this.wifiInfo = wifiInfo;
	}

	public String getAttachedInfo() {
		return attachedInfo;
	}

	public void setAttachedInfo(String attachedInfo) {
		this.attachedInfo = attachedInfo;
	}

	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public String getUpv() {
		return upv;
	}

	public void setUpv(String upv) {
		this.upv = upv;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

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

	public String getEnvInfo() {
		return envInfo;
	}

	public void setEnvInfo(String envInfo) {
		this.envInfo = envInfo;
	}

	public String getNewprotect() {
		return newprotect;
	}

	public void setNewprotect(String newprotect) {
		this.newprotect = newprotect;
	}

	public String getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(String appInfo) {
		this.appInfo = appInfo;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getChallengeAns() {
		return challengeAns;
	}

	public void setChallengeAns(String challengeAns) {
		this.challengeAns = challengeAns;
	}

	public String getPluginSign() {
		return pluginSign;
	}

	public void setPluginSign(String pluginSign) {
		this.pluginSign = pluginSign;
	}

	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public String getPluginHash() {
		return pluginHash;
	}

	public void setPluginHash(String pluginHash) {
		this.pluginHash = pluginHash;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getUseCount() {
		return useCount;
	}

	public void setUseCount(String useCount) {
		this.useCount = useCount;
	}

	public String getActivePluginId() {
		return activePluginId;
	}

	public void setActivePluginId(String activePluginId) {
		this.activePluginId = activePluginId;
	}

	public String getPluginVersion() {
		return pluginVersion;
	}

	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getShareKey() {
		return shareKey;
	}

	public void setShareKey(String shareKey) {
		this.shareKey = shareKey;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getTransEncryData() {
		return transEncryData;
	}

	public void setTransEncryData(String transEncryData) {
		this.transEncryData = transEncryData;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getTradeInfo() {
		return tradeInfo;
	}

	public void setTradeInfo(String tradeInfo) {
		this.tradeInfo = tradeInfo;
	}

	public Map<String, Object> getSignParaMap() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appId", appId);
		paramMap.put("devInfo", devInfo);
		paramMap.put("header", header);
		paramMap.put("nonce_str", nonce_str);
		paramMap.put("sign", sign);
		paramMap.put("pin", pin);
		paramMap.put("appInfo", appInfo);
		paramMap.put("userInfo", userInfo);
		paramMap.put("auth", auth);
		paramMap.put("challengeAns", challengeAns);
		paramMap.put("pluginSign", pluginSign);
		paramMap.put("sotpCode", sotpCode);
		paramMap.put("businessName", businessName);
		paramMap.put("attachedInfo", attachedInfo);
		paramMap.put("useCount", useCount);
		paramMap.put("delPluginId", delPluginId);
		paramMap.put("activePluginId", activePluginId);
		paramMap.put("pluginVersion", pluginVersion);
		paramMap.put("alias", alias);
		paramMap.put("businessId", businessId);
		paramMap.put("shareKey", shareKey);
		paramMap.put("flag", flag);
		paramMap.put("transEncryData", transEncryData);
		paramMap.put("grade", grade);
		paramMap.put("tradeInfo", tradeInfo);
		paramMap.put("dataInfo", dataInfo);
		paramMap.put("clientSign", clientSign);
		return paramMap;
	}
	
	public Map<String, Object> getClientSignParaMap() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appId", appId);
		paramMap.put("devInfo", devInfo);
		paramMap.put("header", header);
		paramMap.put("clientSign", clientSign);
		paramMap.put("pin", pin);
		paramMap.put("appInfo", appInfo);
		paramMap.put("userInfo", userInfo);
		paramMap.put("auth", auth);
		paramMap.put("challengeAns", challengeAns);
		paramMap.put("pluginSign", pluginSign);
		paramMap.put("sotpCode", sotpCode);
		paramMap.put("attachedInfo", attachedInfo);
		paramMap.put("useCount", useCount);
		paramMap.put("delPluginId", delPluginId);
		paramMap.put("activePluginId", activePluginId);
		paramMap.put("pluginVersion", pluginVersion);
		paramMap.put("alias", alias);
		paramMap.put("businessId", businessId);
		paramMap.put("shareKey", shareKey);
		paramMap.put("flag", flag);
		paramMap.put("transEncryData", transEncryData);
		paramMap.put("grade", grade);
		return paramMap;
	}

	public UserAuthMsg convertToAuthMsg() {
		UserAuthMsg authMsg = new UserAuthMsg();
		authMsg.setAuthEventID(new Date().getTime() + "_" + this.getPhoneNum());
		authMsg.setAuthTime(new Date());
		authMsg.setUserId(this.getUserId());
		authMsg.setPhoneNum(this.getPhoneNum());
		authMsg.setDevInfo(this.getDevInfo());
		authMsg.setBusinessName(this.getBusinessName());
		authMsg.setService(this.getService());
		authMsg.setLocation(this.getLocation());
		authMsg.setIp(this.getIp());
		authMsg.setWifiInfo(this.getWifiInfo());
		authMsg.setPayAction(this.getPayAction());
		authMsg.setPayCard(this.getPayCard());
		authMsg.setPayCardType(this.getPayCardType());
		authMsg.setRecCard(this.getRecCard());
		authMsg.setPayee(this.getPayee());
		authMsg.setPrice(this.getPrice());
		authMsg.setAppID(this.getAppId());
		authMsg.setCityID("00001");
		authMsg.setPluginId(this.getPluginId());
		authMsg.setPluginStatus("3");
		authMsg.setErrCnt(0);

		return authMsg;
	}
}
