package com.people.sotp.common.cache;

import com.peopleNet.sotp.dal.model.RulePolicyDto;

public interface ICacheService {

	public void updatePolicyRedis(String policyCode, RulePolicyDto rulePolicyDO) throws Exception;

	// public void updatePluginRedis(PluginDO pluginDO) throws Exception;
}
