package com.peopleNet.sotp.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.service.ISignService;
import com.peopleNet.sotp.thrift.service.SotpPlugin;
import com.peopleNet.sotp.util.Base64;
import com.peopleNet.sotp.util.CommonConfig;
import com.peopleNet.sotp.util.StringUtils;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

@Service
public class ControllerHelper {
	private static String checkSign = CommonConfig.get("check_sign");
	@Autowired
	private ISignService signService;
	
	/**
	 * 将json格式的字符串解析成Map对象
	 */
	private static HashMap<String, Object> toHashMap(JSONObject json) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		// 将json字符串转换成jsonObject

		Iterator it = json.keys();
		// 遍历jsonObject数据，添加到Map对象
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			Object value = (Object) json.get(key);
			data.put(key, value);
		}
		return data;
	}

	
	public static ModelAndView getView(String viewName, Map<String, Object> properties) {
		ModelAndView view = new ModelAndView();
		view.setViewName(viewName);
		if (properties != null) {
			view.addAllObjects(properties);
		}
		return view;
	}

	public static Map<String, Object> getResultMap(int type, int status, Object message, String serverTime) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("type", type);
		resultMap.put("status", status);
		if (message == null) {
			Map<String, Object> messageMap = new HashMap<String, Object>();
			messageMap.put("data", "");
			resultMap.put("message", messageMap);
		} else {
			resultMap.put("message", message);
		}
		resultMap.put("serverTime", serverTime == null ? "" : serverTime);
		return resultMap;
	}

	public static String getResultJsonString(int type, int status, Object message, String serverTime) {
		JSONObject result = new JSONObject();
		result.put("type", type);
		result.put("status", status);
		if (message == null) {
			Map<String, Object> messageMap = new HashMap<String, Object>();
			messageMap.put("data", "");
			result.put("message", messageMap);
		} else {
			result.put("message", message);
		}
		result.put("serverTime", serverTime == null ? "" : serverTime);
		return result.toString();
	}

	public static String getResultJsonStringV2(String appId, String nonce_str, String sign, int status, Object message,
	        String serverTime, String pluginId, String clientSign, String appKey, String sessionKey, ISignService signService) {
		JSONObject result = new JSONObject();
		if(Constant.ISCHECKSIGN.ISCHECK.equals(checkSign)){
			result.put("sign", sign);
			result.put("nonce_str", nonce_str);
		}
		result.put("appId", appId);
		result.put("status", status);
		/*
		 * if(data != null && data != ""){ result.put("data", data); } if(hwInfo
		 * != null && hwInfo != ""){ result.put("hwInfo", hwInfo); } if(errorMsg
		 * != null && errorMsg != ""){ result.put("errorMsg", errorMsg); }
		 */
		if (message == null) {
			Map<String, Object> messageMap = new HashMap<String, Object>();
			messageMap.put("data", "");
			result.put("message", messageMap);
		} else {
			result.put("message", message);
		}
		result.put("serverTime", serverTime == null ? "" : serverTime);
		if (!StringUtils.isEmpty(sessionKey)){
			Map<String, Object> clientParamMap = toHashMap(result);
			clientSign = signService.signParaBySessionKey(clientParamMap, sessionKey);
			result.put("clientSign", clientSign);
		}
		result.put("nonce_str", nonce_str);
		//Map<String, Object> paramMap = toHashMap(result);
		JSONObject resultJson = JSONObject.fromObject(result.toString());
		Map<String, Object> paramMap = toHashMap(resultJson);
		if(Constant.ISCHECKSIGN.ISCHECK.equals(checkSign)){
			sign = signService.signParaByAppKey(paramMap, appKey, Constant.SIGN_METHOD.SHA);
			result.put("sign", sign);
		}
		return result.toString();
	}

	public static String getResultJsonStringV2(String appId, String nonce_str, String sign, int type, int status,
	        Object message, String serverTime) {
		JSONObject result = new JSONObject();
		result.put("appId", appId);
		result.put("nonce_str", nonce_str);
		result.put("sign", sign);
		result.put("type", type);
		result.put("status", status);
		/*
		 * if(data != null && data != ""){ result.put("data", data); } if(hwInfo
		 * != null && hwInfo != ""){ result.put("hwInfo", hwInfo); } if(errorMsg
		 * != null && errorMsg != ""){ result.put("errorMsg", errorMsg); }
		 */
		if (message == null) {
			Map<String, Object> messageMap = new HashMap<String, Object>();
			messageMap.put("data", "");
			result.put("message", messageMap);
		} else {
			result.put("message", message);
		}
		result.put("serverTime", serverTime == null ? "" : serverTime);
		return result.toString();
	}

	public static String getDataJson(String header, String chalcode, String userInfo, int policy, SotpPlugin plugin) {
		JSONObject result = new JSONObject();
		String headstr = "";
		String userInfoStr = "";
		try {
			headstr = Base64.decode(header);
			userInfoStr = Base64.decode(userInfo);
		} catch (Exception e) {
			return null;
		}
		Map<String, Object> pluginMap = new HashMap<String, Object>();
		if (plugin != null) {
			pluginMap.put("content", plugin.getPlugin());
			pluginMap.put("attachment", plugin.getFile());
		}

		result.put("header", headstr);
		result.put("challenge", chalcode);
		result.put("userInfo", userInfoStr);
		result.put("policy", policy);
		result.put("plugin", pluginMap);
		return result.toString();
	}

	public static JSONObject parseJsonString(String jsonStr) {
		JSONObject jsonObject = null;
		try {
			jsonObject = JSONObject.fromObject(jsonStr);
		} catch (JSONException e) {
			// e.printStackTrace();
			return null;
		}

		return jsonObject;
	}
	public static String sortJsonString(JSONObject jsonStr){
		
		List<String> keys = new ArrayList<String>(jsonStr.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			Object value = ControllerHelper.getJsonElement(jsonStr, key);

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		
		
		return prestr;
	}
	
	public static String getJsonElement(JSONObject jsonObject, String element) {
		String val;
		if (jsonObject == null || element == null)
			return "";

		try {
			val = jsonObject.getString(element);
		} catch (JSONException e) {
			return "";
		}

		return val;
	}

}
