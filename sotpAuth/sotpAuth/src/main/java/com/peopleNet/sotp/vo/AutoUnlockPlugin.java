package com.peopleNet.sotp.vo;

import java.io.Serializable;

public class AutoUnlockPlugin implements Serializable {

	private static final long serialVersionUID = 1L;

	// 自动解锁插件Id
	private String pluginId;

	// 该插件对应的解锁时间
	private Integer autoUnlockTime;

	// 该插件对应的appCode
	private String appCode;

	private Integer isUnlock;// 是否自动解锁

	public AutoUnlockPlugin() {

	}

	public AutoUnlockPlugin(String pluginId, Integer autoUnlockTime, String appCode, Integer isUnlock) {
		super();
		this.pluginId = pluginId;
		this.autoUnlockTime = autoUnlockTime;
		this.appCode = appCode;
		this.isUnlock = isUnlock;
	}

	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public Integer getAutoUnlockTime() {
		return autoUnlockTime;
	}

	public void setAutoUnlockTime(Integer autoUnlockTime) {
		this.autoUnlockTime = autoUnlockTime;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public Integer getIsUnlock() {
		return isUnlock;
	}

	public void setIsUnlock(Integer isUnlock) {
		this.isUnlock = isUnlock;
	}

}
