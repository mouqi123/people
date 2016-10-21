package com.peopleNet.sotp.dal.model;

import java.io.Serializable;

/**
 * 规则策略对象
 * 
 * @author tianck
 */
public class RulePolicyDto implements Serializable {

	private static final long serialVersionUID = 1L;
	private long id;// 编号
	private String policyName;// 策略名称
	private String policyCode;// 策略编码
	private Integer policyStatus;// 策略状态
	private Integer motpType;// 手机口令类型
	private Integer passwordType;// 密码类型
	private Integer authWindowSize;// 时间窗口大小
	private Integer passwordLength;// 口令长度
	private Integer errorTimes;// 连续验证错误锁定次数
	private Integer isUnlock;// 是否自动解锁
	private Integer autoUnlockTime;// 自动解锁时间
	private Integer smsTimeout;// 短信验证码超时时间
	private Integer deviceCnt;// 用户与插件的绑定关系
	private Integer updateCycle;// 更新周期
	private Integer totalCsecnt;// 总使用（身份码验证）次数
	private Integer totalErrcnt;// 总验证错误数
	private Integer pregeneratCount;// 预生成数量
	private Integer pregMonitorCount;// 预生成数量阈值
	private Integer reqsmsnum;// 每个用户(手机号)每天允许申请插件请求短信码的次数
	private Integer reqnum;// 每个用户(手机号)每天允许申请(或更新)插件的次数
	private Integer timeout;// 插件申请待下载的超时时间
	private Integer gentype;// 插件生成方式
	private Integer challengeTimeout;// 挑战码超时时间
	private Integer pluginInitStatus;// 申请插件初始状态
	private Integer activationCount;// 激活次数
	private Integer isIntegrityCheck;// 是否校验app信息
	private Integer pluginType;// 插件类型
	private Integer isAllowRoot;// root的手机是否允许通过验证
	private Integer twoAuthIntegrityCheck;// 双向认证是否要完整性校验

	public Integer getTwoAuthIntegrityCheck() {
		return twoAuthIntegrityCheck;
	}

	public void setTwoAuthIntegrityCheck(Integer twoAuthIntegrityCheck) {
		this.twoAuthIntegrityCheck = twoAuthIntegrityCheck;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public String getPolicyCode() {
		return policyCode;
	}

	public void setPolicyCode(String policyCode) {
		this.policyCode = policyCode;
	}

	public Integer getPolicyStatus() {
		return policyStatus;
	}

	public void setPolicyStatus(Integer policyStatus) {
		this.policyStatus = policyStatus;
	}

	public Integer getMotpType() {
		return motpType;
	}

	public void setMotpType(Integer motpType) {
		this.motpType = motpType;
	}

	public Integer getPasswordType() {
		return passwordType;
	}

	public void setPasswordType(Integer passwordType) {
		this.passwordType = passwordType;
	}

	public Integer getAuthWindowSize() {
		return authWindowSize;
	}

	public void setAuthWindowSize(Integer authWindowSize) {
		this.authWindowSize = authWindowSize;
	}

	public Integer getPasswordLength() {
		return passwordLength;
	}

	public void setPasswordLength(Integer passwordLength) {
		this.passwordLength = passwordLength;
	}

	public Integer getErrorTimes() {
		return errorTimes;
	}

	public void setErrorTimes(Integer errorTimes) {
		this.errorTimes = errorTimes;
	}

	public Integer getIsUnlock() {
		return isUnlock;
	}

	public void setIsUnlock(Integer isUnlock) {
		this.isUnlock = isUnlock;
	}

	public Integer getAutoUnlockTime() {
		return autoUnlockTime;
	}

	public void setAutoUnlockTime(Integer autoUnlockTime) {
		this.autoUnlockTime = autoUnlockTime;
	}

	public Integer getSmsTimeout() {
		return smsTimeout;
	}

	public void setSmsTimeout(Integer smsTimeout) {
		this.smsTimeout = smsTimeout;
	}

	public Integer getDeviceCnt() {
		return deviceCnt;
	}

	public void setDeviceCnt(Integer deviceCnt) {
		this.deviceCnt = deviceCnt;
	}

	public Integer getUpdateCycle() {
		return updateCycle;
	}

	public void setUpdateCycle(Integer updateCycle) {
		this.updateCycle = updateCycle;
	}

	public Integer getTotalCsecnt() {
		return totalCsecnt;
	}

	public void setTotalCsecnt(Integer totalCsecnt) {
		this.totalCsecnt = totalCsecnt;
	}

	public Integer getTotalErrcnt() {
		return totalErrcnt;
	}

	public void setTotalErrcnt(Integer totalErrcnt) {
		this.totalErrcnt = totalErrcnt;
	}

	public Integer getPregeneratCount() {
		return pregeneratCount;
	}

	public void setPregeneratCount(Integer pregeneratCount) {
		this.pregeneratCount = pregeneratCount;
	}

	public Integer getPregMonitorCount() {
		return pregMonitorCount;
	}

	public void setPregMonitorCount(Integer pregMonitorCount) {
		this.pregMonitorCount = pregMonitorCount;
	}

	public Integer getReqsmsnum() {
		return reqsmsnum;
	}

	public void setReqsmsnum(Integer reqsmsnum) {
		this.reqsmsnum = reqsmsnum;
	}

	public Integer getReqnum() {
		return reqnum;
	}

	public void setReqnum(Integer reqnum) {
		this.reqnum = reqnum;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getGentype() {
		return gentype;
	}

	public void setGentype(Integer gentype) {
		this.gentype = gentype;
	}

	public Integer getChallengeTimeout() {
		return challengeTimeout;
	}

	public void setChallengeTimeout(Integer challengeTimeout) {
		this.challengeTimeout = challengeTimeout;
	}

	public Integer getPluginInitStatus() {
		return pluginInitStatus;
	}

	public void setPluginInitStatus(Integer pluginInitStatus) {
		this.pluginInitStatus = pluginInitStatus;
	}

	public Integer getActivationCount() {
		return activationCount;
	}

	public void setActivationCount(Integer activationCount) {
		this.activationCount = activationCount;
	}

	public Integer getIsIntegrityCheck() {
		return isIntegrityCheck;
	}

	public void setIsIntegrityCheck(Integer isIntegrityCheck) {
		this.isIntegrityCheck = isIntegrityCheck;
	}

	public Integer getPluginType() {
		return pluginType;
	}

	public void setPluginType(Integer pluginType) {
		this.pluginType = pluginType;
	}

	public Integer getIsAllowRoot() {
		return isAllowRoot;
	}

	public void setIsAllowRoot(Integer isAllowRoot) {
		this.isAllowRoot = isAllowRoot;
	}

}
