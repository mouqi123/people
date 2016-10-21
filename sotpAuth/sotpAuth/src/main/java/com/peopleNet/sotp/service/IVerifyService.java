package com.peopleNet.sotp.service;

import com.peopleNet.sotp.dal.model.RulePolicyDto;
import com.peopleNet.sotp.dal.model.authFeatureDto;
import com.peopleNet.sotp.dal.model.pluginInfoDto;
import com.peopleNet.sotp.vo.ResultVO;

/**
 * 
 * @描述 验证插件\设备\人\app四者之间对应关系的接口
 * @author wangxin
 * @created_at 2016年04月25日
 */
public interface IVerifyService {
	public ResultVO verifyPluginStatusIsReady(pluginInfoDto plInfo, RulePolicyDto verifyPolicyInfo);

	public ResultVO verifyUserInfo(pluginInfoDto plInfo, String phoneNum);

	public ResultVO verifyDevInfoAndAppInfoAndPluginSign(String service, pluginInfoDto plInfo,
	        authFeatureDto authFeature, String envInfo, String appId, RulePolicyDto verifyPolicyInfo);

	public ResultVO verifyChallengeAns(pluginInfoDto plInfo, String challengeAns);

	public ResultVO verifyAll(String service, pluginInfoDto plInfo, authFeatureDto authFeature, String envInfo,
	        String phoneNum, String appId, RulePolicyDto verifyPolicyInfo);

	public ResultVO verifyAppInfo(String service, pluginInfoDto plInfo, String envInfo, String appId,
	        RulePolicyDto verifyPolicyInfo);

}
