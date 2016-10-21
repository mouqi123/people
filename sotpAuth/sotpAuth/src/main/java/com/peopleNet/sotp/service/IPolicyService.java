package com.peopleNet.sotp.service;

import java.util.List;

import com.peopleNet.sotp.dal.dao.userInfoDtoMapper;
import com.peopleNet.sotp.dal.model.RulePolicyDto;
import com.peopleNet.sotp.dal.model.authFeatureDto;
import com.peopleNet.sotp.dal.model.authPolicyDto;
import com.peopleNet.sotp.dal.model.pluginInfoDto;
import com.peopleNet.sotp.dal.model.policyDetailDto;
import com.peopleNet.sotp.vo.ResultVO;

/**
 * 
 * @描述 业务参数判断、日志接口
 * @author hejh
 * @created_at 2015年11月20日
 */
public interface IPolicyService {
	public int unlockPluginInfo(pluginInfoDto plInfo, RulePolicyDto verifyPolicyInfo);

	// 插件状态判断
	public ResultVO pluginStatus(pluginInfoDto plInfo, RulePolicyDto verifyPolicyIfo);

	// 插件更新策略
	public ResultVO updatePolicy(String appId, pluginInfoDto plInfo);

	// 短信码验证策略
	public ResultVO smsVeryPolicy(String sms, ICacheService cacheService, userInfoDtoMapper userInfoMapper);

	// 插件申请策略
	public ResultVO pluginReqPolicy(String phoneNum, String appId, RulePolicyDto applyPolicyInfo);

	// 检查手机硬件信息devInfo是否符合匹配标准  lxx
		public ResultVO checkDevInfo(String appId, String devInfo, String policyType, authFeatureDto authFeatureInfo);
	// 检查是否允许root设备进行认证
	public ResultVO checkRootPolicy(RulePolicyDto rulePolicy);

	// 插件解锁操作
	public ResultVO codeVerifyPolicy(pluginInfoDto plInfo, String appId, RulePolicyDto verifyPolicyInfo);

	// 插件激活策略
	public ResultVO pluginActivePolicy(pluginInfoDto plInfo, RulePolicyDto activePolicyInfo);

	// 策略 转换 （数据库 policyDetail -> policyDetail）
	public RulePolicyDto dbPolicyCovert(List<policyDetailDto> pList);

	// 获取策略中配置的连续验证错误锁定次数
	public int getMaxErrorTime(RulePolicyDto verifyPolicyInfo);

	// 获取策略中配置的挑战码的超时时间
	public int getChallengeCodeTimeOut(RulePolicyDto verifyPolicyInfo);

	// 获取策略中配置的自动解锁时间
	public int getAutoUnlockTime(RulePolicyDto verifyPolicyInfo);

	// 获取策略中配置的是否校验app信息
	public int getIsIntegrityCheck(RulePolicyDto rulePolicy);

	// 获取策略中配置双向认证的是否校验app信息
	public int getIsTwoAuthIntegrityCheck(RulePolicyDto rulePolicy);

	// 获取申请插件是否校验app信息
	public int getIsIntegrityCheckForApply(RulePolicyDto applyPolicyInfo);

	// 获取策略中配置的是否允许自动解锁
	public int getIsUnlock(RulePolicyDto verifyPolicyInfo);

	// 获取策略中配置的是否允许自动解锁
	public int getPluginType(RulePolicyDto applyPolicyInfo);

	// 获取设备列表时检测插件是否要更新
	public boolean checkUpdate(String devStr, String appId);

	// 获取设备列表时检测插件是否要更新
	public boolean checkUpdateWithSotpIdAndStatus(String sotpId, String sotpStatus, String appId);

	// 获取插件状态时检测插件是否要更新
	public boolean checkNeedUpdate(pluginInfoDto plInfo, String appId);

	// 获取认证策略的值
	public int getAuthPolicy(String appId, String businessName, String grade);

	// 获取认证策略的对象，基于手机评分和业务编码
	public authPolicyDto getAuthPolicyDto(String appId, String businessName, String grade, int riskScore);

}
