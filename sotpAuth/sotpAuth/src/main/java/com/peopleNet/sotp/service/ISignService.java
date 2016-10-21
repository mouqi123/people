package com.peopleNet.sotp.service;

import java.util.Map;

public interface ISignService {

	// 传入参数通过appKey签名
	public String signParaByAppKey(Map<String, Object> param, String appKey, String method);
	
	public String signParaBySessionKey(Map<String, Object> param, String sessionKey);

	public boolean verifySign(String appKey, Map<String, Object> paramMap, String sign);
	
	public boolean verifyClientSign(String pluginId, Map<String, Object> paramMap, String clientSign);
}
