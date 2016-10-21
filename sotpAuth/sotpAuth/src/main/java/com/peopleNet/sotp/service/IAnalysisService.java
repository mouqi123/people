package com.peopleNet.sotp.service;

public interface IAnalysisService {

	/*
	 * getPolicyContent input: policy (int) output: 1. String, authentication
	 * factors separated by '|'
	 * eg:appHash|location|ip|payPwd|gesture|voiceprint|faceprint|timeAuthCode
	 * 2. null,if policy is invalid.
	 */
	public String getPolicyContent(int policy);

	/*
	 * getPolicyNumber input: policy (String) output: 1. int, a number
	 * representative of authentication factors 2. 0,if policy is null.
	 */
	public int getPolicyNumber(String policy);

	// 根据风险得分获取动态认证策略
	public String getDynamicPolicyByRiskScore(int riskScore);

	// 增加单一策略,返回更新的策略值
	public int addSinglePolicy(int orgPolicyNum, String policy);
}
