package com.peopleNet.sotp.vo;

import java.util.List;

/**
 * Created by wangxin on 16/9/26.
 */
public class UserTrustLibInfo {

	// 可信设备列表
	private List<String> deviceIdList = null;
	// 可信插件列表
	private List<String> pluginIdList = null;
	// 可信手机号列表
	private List<String> userPhoneList = null;
	// 可信转账账户列表
	private List<String> recAccList = null;
	// 可信转账卡号列表
	private List<String> recCardList = null;
	private String userId;

	public UserTrustLibInfo(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<String> getDeviceIdList() {
		return deviceIdList;
	}

	public void setDeviceIdList(List<String> deviceIdList) {
		this.deviceIdList = deviceIdList;
	}

	public List<String> getRecCardList() {
		return recCardList;
	}

	public void setRecCardList(List<String> recCardList) {
		this.recCardList = recCardList;
	}

	public List<String> getRecAccList() {
		return recAccList;
	}

	public void setRecAccList(List<String> recAccList) {
		this.recAccList = recAccList;
	}

	public List<String> getUserPhoneList() {
		return userPhoneList;
	}

	public void setUserPhoneList(List<String> userPhoneList) {
		this.userPhoneList = userPhoneList;
	}

	public List<String> getPluginIdList() {
		return pluginIdList;
	}

	public void setPluginIdList(List<String> pluginIdList) {
		this.pluginIdList = pluginIdList;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("userTrustLibInfo detail:");
		sb.append("{userId:" + userId + "}");
		if (null != deviceIdList)
			sb.append("{deviciIdList:" + deviceIdList.toString() + "}");
		if (null != pluginIdList)
			sb.append("{pluginIdList:" + pluginIdList.toString() + "}");
		if (null != userPhoneList)
			sb.append("{userPhoneList:" + userPhoneList.toString() + "}");
		if (null != recAccList)
			sb.append("{recAccList:" + recAccList.toString() + "}");
		if (null != recCardList)
			sb.append("{recCardList:" + recCardList.toString() + "}");
		return sb.toString();
	}
}
