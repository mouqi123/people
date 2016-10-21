package com.peopleNet.sotp.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.springframework.stereotype.Controller;

import com.peopleNet.sotp.util.HttpRequestUtils;
import com.peopleNet.sotp.util.ParamHander;

@Controller
public class UnitTestController {

	private String header;
	private String attachedInfo;
	private String appId = "dd007ad308f56ac07177fb3c7aaec2add65c4418";
	private String businessName = "20160628";
	private String appKey = "7f3593402baf3e6c4b94b469587acedd05983e09";
	private String devInfo = ParamHander.genDevInfo();
	private String userInfo = ParamHander.genUserInfo("13121220659", null);
	private String appInfo = ParamHander.genAppInfo("bb77f9d5991f09fae27455debd11d26bb37d5511",
	        "a18024e7d8662bb01459cffa11b25b4c298c45f7", "da39a3ee5e6b4b0d3255bfef95601890afd80709",
	        "ad16e6cdb38495c78659b9348c517ab3f96f1613");
	private String pluginSign = ParamHander.genPluginSign("M00004A005VV0.1ID015657a8a4ae000",
	        "bb77f9d5991f09fae27455debd11d26bb37d5511");
	private String auth;
	private String sign;
	private String nonce_str = "";
	private String challengeAns;
	private String sotpCode;
	private Map<String, Object> parameterMap;
	private String url;
	private String requestUri = "http://192.168.20.10:8080/fidoportal";

	// 下載插件第一步
	@Test
	public void testDownLoadPlugin() {

		header = ParamHander.genHeader("3", "0", "zrauth.plugin.apply", appId);
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}

		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("header", header);
		parameterMap.put("appInfo", appInfo);
		parameterMap.put("devInfo", devInfo);
		parameterMap.put("businessName", businessName);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("userInfo", userInfo);
		parameterMap.put("attachedInfo", attachedInfo);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

	// 請求挑戰碼
	@Test
	public void getchallenge() {

		header = ParamHander.genHeader("3", "0", "zrauth.business.getchallenge", appId);
		attachedInfo = ParamHander.genAttachedInfo();
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}

		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("header", header);
		parameterMap.put("appInfo", appInfo);
		parameterMap.put("devInfo", devInfo);
		parameterMap.put("businessName", businessName);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("userInfo", userInfo);
		parameterMap.put("pluginSign", pluginSign);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

	// 下載插件第二步
	@Test
	public void testSecDownLoadPlugin() {

		header = ParamHander.genHeader("3", "0", "zrauth.plugin.apply.auth", appId);
		businessName = "20160628";
		attachedInfo = ParamHander.genAttachedInfo();
		challengeAns = "HXF/mDbelrE=";
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}

		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("header", header);
		parameterMap.put("appInfo", appInfo);
		parameterMap.put("attachedInfo", attachedInfo);
		parameterMap.put("devInfo", devInfo);
		parameterMap.put("businessName", businessName);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("userInfo", userInfo);
		parameterMap.put("challengeAns", challengeAns);
		parameterMap.put("pluginSign", pluginSign);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

	// 插件更新
	@Test
	public void updatePlugin() {

		header = ParamHander.genHeader("3", "0", "zrauth.plugin.update", appId);
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		sotpCode = "yo2/QmBPOZDwp48Mnc7xfw=="; // 时间型

		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("header", header);
		parameterMap.put("appInfo", appInfo);
		parameterMap.put("devInfo", devInfo);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("userInfo", userInfo);
		parameterMap.put("sotpCode", sotpCode);
		parameterMap.put("pluginSign", pluginSign);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);

	}

	// 插件激活
	@Test
	public void activePlugin() {

		header = ParamHander.genHeader("3", "0", "zrauth.plugin.activate", appId);
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		sotpCode = "111111";

		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("header", header);
		parameterMap.put("appInfo", appInfo);
		parameterMap.put("devInfo", devInfo);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("userInfo", userInfo);
		parameterMap.put("sotpCode", sotpCode);
		parameterMap.put("pluginSign", pluginSign);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

	// 获取设备列表
	@Test
	public void getdevlist() {

		header = ParamHander.genHeader("3", "0", "zrauth.plugin.setdevalias", appId);
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		sotpCode = "111111";

		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("header", header);
		parameterMap.put("appInfo", appInfo);
		parameterMap.put("devInfo", devInfo);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("userInfo", userInfo);
		parameterMap.put("sotpCode", sotpCode);
		parameterMap.put("pluginSign", pluginSign);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

	// 解绑插件
	@Test
	public void plugindel() {

		header = ParamHander.genHeader("3", "0", "zrauth.plugin.unwrap", appId);
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		sotpCode = "111111";

		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("header", header);
		parameterMap.put("appInfo", appInfo);
		parameterMap.put("devInfo", devInfo);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("userInfo", userInfo);
		parameterMap.put("sotpCode", sotpCode);
		parameterMap.put("pluginSign", pluginSign);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

	// 认证第一步
	@Test
	public void verify() {

		header = ParamHander.genHeader("3", "0", "zrauth.business.auth", appId);
		attachedInfo = ParamHander.genAttachedInfo();
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		businessName = "20160628";

		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("header", header);
		parameterMap.put("appInfo", appInfo);
		parameterMap.put("devInfo", devInfo);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("userInfo", userInfo);
		parameterMap.put("useCount", "100");
		parameterMap.put("grade", "80");
		parameterMap.put("businessName", businessName);
		parameterMap.put("attachedInfo", attachedInfo);
		parameterMap.put("pluginSign", pluginSign);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

	// 认证第二步
	@Test
	public void verifyResponse() {

		header = ParamHander.genHeader("3", "0", "zrauth.business.auth.response", appId);
		attachedInfo = ParamHander.genAttachedInfo();
		auth = ParamHander.genAuth("1453", "default");
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		businessName = "20160628";

		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("header", header);
		parameterMap.put("appInfo", appInfo);
		parameterMap.put("devInfo", devInfo);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("userInfo", userInfo);
		parameterMap.put("challengeAns", challengeAns);
		parameterMap.put("businessName", businessName);
		parameterMap.put("pluginSign", pluginSign);
		parameterMap.put("auth", auth);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);

	}

	// 时间同步
	@Test
	public void timeSynchr() {

		header = ParamHander.genHeader("3", "0", "zrauth.business.time.synchr", appId);
		attachedInfo = ParamHander.genAttachedInfo();
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			nonce_str += random.nextInt(10);
		}
		sotpCode = "111111";

		parameterMap = new HashMap();
		parameterMap.put("appId", appId);
		parameterMap.put("header", header);
		parameterMap.put("appInfo", appInfo);
		parameterMap.put("devInfo", devInfo);
		parameterMap.put("nonce_str", nonce_str);
		parameterMap.put("userInfo", userInfo);
		parameterMap.put("sotpCode", sotpCode);
		parameterMap.put("pluginSign", pluginSign);
		sign = ParamHander.genSign(parameterMap, appKey);
		parameterMap.put("sign", sign);
		url = ParamHander.genUrlFromMap(parameterMap);
		url = requestUri + url;
		String json = HttpRequestUtils.httpGet(url);
	}

}
