
package com.peopleNet.sotp.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import com.peopleNet.sotp.util.HttpRequestUtils;
import com.peopleNet.sotp.util.ParamHander;

public class UnitTestControllerV2 {

	private String appId = "dd007ad308f56ac07177fb3c7aaec2add65c4418"; //  545e8d3e0ecb42144d12fc2bb0e8f8ff61368411
	private String appKey = "7f3593402baf3e6c4b94b469587acedd05983e09";
	private String nonce_str = "";
	private String sign;
	private String version = "2.0";
	private String service;
	private String userId;
	private String phoneNum = "13121220659";  //  13777777777
	private String sotpCode;
	private String sotpId = "M00004A005VV0.1ID015657a8a4ae000";
	private String sms;
	private String pin;
	private String devInfo = ParamHander.genDevInfo();
	private String envInfo = ParamHander.genenvInfoV2("bb77f9d5991f09fae27455debd11d26bb37d5511",
	        "a18024e7d8662bb01459cffa11b25b4c298c45f7", "ad16e6cdb38495c78659b9348c517ab3f96f1613",
	        "da39a3ee5e6b4b0d3255bfef95601890afd80709");
	private String url;
	private String requestUri = "http://localhost:8080/portal";
	private Map<String, Object> parameterMap;

	// 申请插件
	@Test
	public void downLoadPlugin() {

		service = "zrauth.plugin.apply";
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}

		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("version", version);
		parameterMap.put("service", service);
		parameterMap.put("phoneNum", phoneNum);
		parameterMap.put("devInfo", devInfo);
		parameterMap.put("envInfo", envInfo);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);

	}

	// 插件更新
	@Test
	public void pluginupdate() {

		service = "zrauth.plugin.update";
		sotpCode = "111111";
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}

		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("version", version);
		parameterMap.put("service", service);
		parameterMap.put("phoneNum", phoneNum);
		parameterMap.put("sotpCode", sotpCode);
		parameterMap.put("sotpId", sotpId);
		parameterMap.put("envInfo", envInfo);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);

	}

	// 解绑插件
	@Test
	public void plugindel() {

		service = "zrauth.plugin.unwrap";
		String delPluginId = "M00004A005VV0.1ID015657a8a4ae000";
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("version", version);
		parameterMap.put("service", service);
		parameterMap.put("phoneNum", phoneNum);
		parameterMap.put("delPluginId", delPluginId);
		parameterMap.put("sotpId", sotpId);
		parameterMap.put("envInfo", envInfo);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

	// 设备列表
	@Test
	public void getdevlist() {

		service = "zrauth.plugin.listall";
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("version", version);
		parameterMap.put("service", service);
		parameterMap.put("phoneNum", phoneNum);
		parameterMap.put("sotpId", sotpId);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

	// 数据加解密
	@Test
	public void crypto() {

		service = "zrauth.business.dataencryption";
		String dataInfo = "yuanwen"; // 数据原文
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("version", version);
		parameterMap.put("service", service);
		parameterMap.put("phoneNum", phoneNum);
		parameterMap.put("sotpId", sotpId);
		parameterMap.put("dataInfo", dataInfo);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

	// 激活插件
	@Test
	public void activePlugin() {

		service = "zrauth.plugin.activate";
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		String activeCode = "123123";

		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("version", version);
		parameterMap.put("service", service);
		parameterMap.put("phoneNum", phoneNum);
		parameterMap.put("sotpId", sotpId);
		parameterMap.put("activeCode", activeCode);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

	// 请求挑战码
	@Test
	public void getchallenge() {

		service = "zrauth.business.getchallenge";
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("version", version);
		parameterMap.put("service", service);
		parameterMap.put("phoneNum", phoneNum);
		parameterMap.put("sotpId", sotpId);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

	// 安全认证
	@Test
	public void verify() {

		service = "zrauth.business.auth";
		String sotpCodePara = "111111";
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("version", version);
		parameterMap.put("service", service);
		parameterMap.put("phoneNum", phoneNum);
		parameterMap.put("sotpId", sotpId);
		parameterMap.put("sotpCode", sotpCode);
		parameterMap.put("sotpCodePara", sotpCodePara);
		parameterMap.put("envInfo", envInfo);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

	// 时间同步功能
	@Test
	public void timeSynchr() {

		service = "zrauth.business.time.synchr";
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("version", version);
		parameterMap.put("service", service);
		parameterMap.put("phoneNum", phoneNum);
		parameterMap.put("sotpId", sotpId);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

	// 密码键盘解密
	@Test
	public void decrypt() {

		service = "zrauth.business.keyboard.decryption";
		String dataInfo = "assadsa|sdsa";
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("version", version);
		parameterMap.put("service", service);
		parameterMap.put("phoneNum", phoneNum);
		parameterMap.put("sotpId", sotpId);
		parameterMap.put("dataInfo", dataInfo);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

	// 生成会话密钥
	@Test
	public void gensesskey() {

		service = "zrauth.business.gensesskey";
		String randa = "123";
		String randb = "234";
		String dataInfo = "qazwsx";
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("version", version);
		parameterMap.put("service", service);
		parameterMap.put("phoneNum", phoneNum);
		parameterMap.put("sotpId", sotpId);
		parameterMap.put("randa", randa);
		parameterMap.put("randb", randb);
		parameterMap.put("dataInfo", dataInfo);
		parameterMap.put("envInfo", envInfo);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

	// 修改插件保护码
	@Test
	public void modsotpin() {

		service = "zrauth.plugin.modifypin";
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		String newprotect = "12334";

		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("version", version);
		parameterMap.put("service", service);
		parameterMap.put("phoneNum", phoneNum);
		parameterMap.put("sotpId", sotpId);
		parameterMap.put("newprotect", newprotect);
		parameterMap.put("envInfo", envInfo);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

}
