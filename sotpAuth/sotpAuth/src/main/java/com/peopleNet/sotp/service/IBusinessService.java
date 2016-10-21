package com.peopleNet.sotp.service;

import com.peopleNet.sotp.dal.model.RulePolicyDto;
import com.peopleNet.sotp.dal.model.authFeatureDto;
import com.peopleNet.sotp.dal.model.authPolicyDto;
import com.peopleNet.sotp.dal.model.pluginInfoDto;
import com.peopleNet.sotp.thrift.service.SotpPlugin;
import com.peopleNet.sotp.vo.ResultVO;
import com.peopleNet.sotp.vo.VerifyEntity;

/**
 * 
 * @描述 业务参数判断、日志接口
 * @author hejh
 * @created_at 2015年11月20日
 */
public interface IBusinessService {

	// 记录业务日志
	public void setBusinessLog(String reqestMsg, String responseMsg, int retcode, String errMsg);

	public String decrypt(pluginInfoDto plugin, String source);

	public String encrypt(pluginInfoDto plugin, String source);

	public int verify(pluginInfoDto plugin, VerifyEntity verifyEntity);

	public String getChallengeCodeAndClear(pluginInfoDto plugin);

	public String getChallengeCode(pluginInfoDto plugin);
	
	public String getSessionKey(String pluginId);

	public String getUserVerifyInfoIdAndClear(pluginInfoDto plugin);

	public int verifySotpCode(pluginInfoDto plugin, authFeatureDto authFeaInfo, String encrptedSotpCode,
	        boolean sotpCodeIsEncrypt, String sotpPara, RulePolicyDto verifyPolicyInfo);

	public ResultVO verifyByPolicy(String op, pluginInfoDto plugin, authFeatureDto authFeaInfo, int policy,
	        String authInfo, RulePolicyDto verifyPolicyInfo);

	public int verifySotpCodeWithChallenge(pluginInfoDto plugin, authFeatureDto authFeaInfo, String encrptedSotpCode,
	        boolean sotpCodeIsEncrypt, RulePolicyDto verifyPolicyInfo);

	public SotpPlugin genThorPlugin(String pluginVersion, authFeatureDto authFeaInfo, pluginInfoDto pluginInfo,
	        String appId, String serviceName, RulePolicyDto applyPolicyInfo);

	public String generateChallenge();

	public boolean saveChallengeIntoCache(String appId, pluginInfoDto plInfo, String challenge,
	        RulePolicyDto verifyPolicyInfo);

	public boolean saveSessionKeyIntoCache(String appId, pluginInfoDto plInfo, String sessionKey);
	
	public boolean saveAuthPolicyDtoIntoCache(String appId, pluginInfoDto plInfo, String businessName,
	        authPolicyDto authPolicy);

	public authPolicyDto getAuthPolicyDtoFromCache(String appId, pluginInfoDto plInfo, String businessName);
}
