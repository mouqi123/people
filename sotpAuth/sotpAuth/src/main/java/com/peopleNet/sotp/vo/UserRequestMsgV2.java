package com.peopleNet.sotp.vo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class UserRequestMsgV2 {

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

	private String newprotect;

	private String action;

	private String holdinfo;

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

	private String useCount;

	private String delPluginId;

	private String alias;

	private String businessId;

	private String shareKey;

	private String flag;

	private String transEncryData;

	private String pluginVersion;

	private String activePluginId;

	private String devId;

	private String pluginStatus;

	private int verifyRuleResult = 0;

	private String ruleMsg;

	private int verifyNumToday = 0;

	private String addr;

	private BigDecimal price;
	
	private String clientSign;

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getVerifyNumToday() {
		return verifyNumToday;
	}

	public void setVerifyNumToday(int verifyNumToday) {
		this.verifyNumToday = verifyNumToday;
	}

	public int getVerifyRuleResult() {
		return verifyRuleResult;
	}

	public void setVerifyRuleResult(int verifyRuleResult) {
		this.verifyRuleResult = verifyRuleResult;
	}

	public String getRuleMsg() {
		return ruleMsg;
	}

	public void setRuleMsg(String ruleMsg) {
		this.ruleMsg = ruleMsg;
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

	public String getUseCount() {
		return useCount;
	}

	public void setUseCount(String useCount) {
		this.useCount = useCount;
	}

	public String getDelPluginId() {
		return delPluginId;
	}

	public void setDelPluginId(String delPluginId) {
		this.delPluginId = delPluginId;
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

	public String getPluginVersion() {
		return pluginVersion;
	}

	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}

	public String getActivePluginId() {
		return activePluginId;
	}

	public void setActivePluginId(String activePluginId) {
		this.activePluginId = activePluginId;
	}

	public String getDevId() {
		return devId;
	}

	public void setDevId(String devId) {
		this.devId = devId;
	}

	public String getPluginStatus() {
		return pluginStatus;
	}

	public void setPluginStatus(String pluginStatus) {
		this.pluginStatus = pluginStatus;
	}

	public String getClientSign() {
		return clientSign;
	}

	public void setClientSign(String clientSign) {
		this.clientSign = clientSign;
	}

	public Map<String, Object> getSignParaMap() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appId", appId);
		paramMap.put("appKey", appKey);
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
		paramMap.put("newprotect", newprotect);
		paramMap.put("action", action);
		paramMap.put("holdinfo", holdinfo);
		paramMap.put("chello", chello);
		paramMap.put("cauth", cauth);
		paramMap.put("cdata", cdata);
		paramMap.put("pin", pin);
		paramMap.put("deldev", deldev);
		paramMap.put("randa", randa);
		paramMap.put("randb", randb);
		paramMap.put("service", service);
		paramMap.put("version", version);
		paramMap.put("envInfo", envInfo);
		paramMap.put("delPluginId", delPluginId);
		paramMap.put("alias", alias);
		paramMap.put("pluginVersion", pluginVersion);
		paramMap.put("activePluginId", activePluginId);
		paramMap.put("devId", devId);
		paramMap.put("pluginStatus", pluginStatus);
		paramMap.put("clientSign", clientSign);
		if (useCount != null) {
			paramMap.put("useCount", useCount);
		}
		
		return paramMap;
	}
	
	public Map<String, Object> getClientSignParaMap() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appId", appId);
		paramMap.put("userId", userId);
		paramMap.put("phoneNum", phoneNum);
		paramMap.put("sms", sms);
		paramMap.put("sotpPayInfo", sotpPayInfo);
		paramMap.put("devInfo", devInfo);
		paramMap.put("sotpCode", sotpCode);
		paramMap.put("sotpId", sotpId);
		paramMap.put("sotpCodePara", sotpCodePara);
		paramMap.put("activeCode", activeCode);
		paramMap.put("dataInfo", dataInfo);
		paramMap.put("newprotect", newprotect);
		paramMap.put("action", action);
		paramMap.put("holdinfo", holdinfo);
		paramMap.put("chello", chello);
		paramMap.put("cauth", cauth);
		paramMap.put("cdata", cdata);
		paramMap.put("pin", pin);
		paramMap.put("deldev", deldev);
		paramMap.put("randa", randa);
		paramMap.put("randb", randb);
		paramMap.put("service", service);
		paramMap.put("version", version);
		paramMap.put("envInfo", envInfo);
		paramMap.put("delPluginId", delPluginId);
		paramMap.put("alias", alias);
		paramMap.put("pluginVersion", pluginVersion);
		paramMap.put("activePluginId", activePluginId);
		paramMap.put("devId", devId);
		paramMap.put("pluginStatus", pluginStatus);
		paramMap.put("clientSign", clientSign);
		if (useCount != null) {
			paramMap.put("useCount", useCount);
		}
		return paramMap;
	}
}
