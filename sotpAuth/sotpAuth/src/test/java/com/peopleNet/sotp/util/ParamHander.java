package com.peopleNet.sotp.util;

import java.util.*;

import net.sf.json.JSONObject;

public class ParamHander {

	public static String genHeader(String major, String minor, String op, String appId) {

		JSONObject jsonHeader = new JSONObject();
		jsonHeader.put("op", op);
		jsonHeader.put("appID", appId);
		JSONObject jsonUpv = new JSONObject();
		jsonUpv.put("minor", minor);
		jsonUpv.put("major", major);
		jsonHeader.put("upv", jsonUpv);
		String headerBase = Base64.encode(jsonHeader.toString());
		return headerBase;
	}

	public static String genUserInfo(String phoneNum, String userId) {

		JSONObject jsonUserInfo = new JSONObject();
		jsonUserInfo.put("phoneNum", phoneNum);
		jsonUserInfo.put("userId", userId);
		String userInfo = Base64.encode(jsonUserInfo.toString());
		return userInfo;
	}

	public static String genDevInfo() {

		JSONObject jsonDevInfo = new JSONObject();
		jsonDevInfo.put("manufacturer", "alps");
		jsonDevInfo.put("imei", "867167011305157");
		jsonDevInfo.put("cpu", "armeabi-v7a");
		jsonDevInfo.put("sdk_version", "15");
		jsonDevInfo.put("mac", "e8:d4:e0:43:a4:8a");
		jsonDevInfo.put("product_type", "K-Touch U81t");
		jsonDevInfo.put("ip", "192.168.99.204");
		jsonDevInfo.put("system_version", "4.0.4");
		jsonDevInfo.put("dev_type", "Android");
		String devInfoBase = Base64.encode(jsonDevInfo.toString());
		return devInfoBase;
	}

	public static String genAppInfo(String pluginval, String dev, String signVal, String appVal) {

		JSONObject jsonAppInfo = new JSONObject();
		jsonAppInfo.put("pluginval", pluginval);
		jsonAppInfo.put("dev", dev);
		jsonAppInfo.put("signval", signVal);
		jsonAppInfo.put("appval", appVal);
		String appInfoBase = Base64.encode(jsonAppInfo.toString());
		return appInfoBase;
	}

	public static String genAuth(String pin, String encodeType) {

		JSONObject jsonPinCode = new JSONObject();
		jsonPinCode.put("pin", pin);
		jsonPinCode.put("encodeType", encodeType);
		JSONObject jsonAuth = new JSONObject();
		jsonAuth.put("pinCode", jsonPinCode);
		String authBase = Base64.encode(jsonAuth.toString());
		return authBase;
	}

	public static String genAttachedInfo() {

		JSONObject jsonAttInfo = new JSONObject();
		jsonAttInfo.put("wifiInfo",
		        "SSID: kingdom, BSSID: 20:76:93:39:d3:a8, MAC: e8:d4:e0:43:a4:8a, Supplicant state: COMPLETED, RSSI: -65, Link speed: 135, Net ID: 1, Explicit connect: false");
		jsonAttInfo.put("ip", "192.168.99.204");
		String attInfoBase = Base64.encode(jsonAttInfo.toString());
		return attInfoBase;
	}

	public static String genPluginSign(String pluginId, String hash) {

		JSONObject jsonpluginSign = new JSONObject();
		jsonpluginSign.put("pluginId", pluginId);
		jsonpluginSign.put("hash", hash);
		String pluginSignBase = Base64.encode(jsonpluginSign.toString());
		return pluginSignBase;
	}

	public static String genenvInfoV2(String pluginval, String dev, String appval, String signval) {
		JSONObject jsonenvInfo = new JSONObject();
		jsonenvInfo.put("pluginval", pluginval);
		jsonenvInfo.put("dev", dev);
		jsonenvInfo.put("appval", appval);
		jsonenvInfo.put("signval", signval);
		String envInfoBase = Base64.encode(jsonenvInfo.toString());
		return envInfoBase;
	}

	public static String genSign(Map<String, Object> paramMap, String appKey) {
		// 去除空值和签名值
		Map<String, Object> sPara = paraFilter(paramMap);
		// 按照“参数=参数值”的模式用“&”字符拼接成字符串
		String prestr = createLinkString(sPara);
		// 生成签名结果
		String mysign = null;
		prestr = prestr + "&appKey=" + appKey;
		mysign = SHA.SHA_people(prestr);
		return mysign;
	}

	public static String genUrlFromMap(Map<String, Object> parameterMap) {
		Iterator entries = parameterMap.entrySet().iterator();
		StringBuffer uriStr = new StringBuffer();
		int i = 0;
		Map.Entry entry;
		String name = "";
		String value = "";
		while (entries.hasNext()) {
			if (i == 0) {
				uriStr.append("?");
			} else {
				uriStr.append("&");
			}
			entry = (Map.Entry) entries.next();
			name = (String) entry.getKey();
			value = (String) entry.getValue();
			uriStr.append(name + "=" + value);
			i++;
		}
		return uriStr.toString();
	}

	/**
	 * 除去数组中的空值和签名参数
	 *
	 * @param sArray
	 *            签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	public static Map<String, Object> paraFilter(Map<String, Object> sArray) {

		Map<String, Object> result = new HashMap<String, Object>();

		if (sArray == null || sArray.size() <= 0) {
			return result;
		}

		for (String key : sArray.keySet()) {
			Object value = sArray.get(key);
			if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
			        || key.equalsIgnoreCase("sign_type")) {
				continue;
			}
			result.put(key, value);
		}

		return result;
	}

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 *
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, Object> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			Object value = params.get(key);

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}
}
