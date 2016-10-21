package com.people.sotp.urlcontrollers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.people.sotp.commons.util.StringUtils;

import net.sf.json.JSONObject;

public class sotpAuthUtil {


	/*
	 * 接口名称： genRequestSotpAuthMsg 接口作用： 生成请求SOTP认证系统的GET报文 接口参数： 参数：sdkrequest
	 * 示例： key1=v1&key2=v2 appKey 返回值： string 示例 RequestSotpAuthMsg
	 */
	public static String genRequestSotpAuthMsg(Map<String, Object> sotpMap,String sotp, String appKey) {
		sotp=sotp.replaceAll("%2B", "\\+"); 
		sotpMap.clear();
		sotpMap=getUrlParams(sotp);
		// 生成随机数
		String nonce_str = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
		// 生成签名值
		sotpMap.put("nonce_str", nonce_str);
		System.out.println("签名前："+sotp);
		String sign = signParaByAppKey(sotpMap, appKey);

		// 生成请求SOTP认证系统的GET报文
		StringBuffer sotpGet = new StringBuffer();
		
		if(sotpMap.containsKey("alias")){
			String alias =(String) sotpMap.get("alias");
			try {
				sotp=sotp.replaceAll(alias,  java.net.URLEncoder.encode(alias,"utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		sotpGet.append(sotp + "&nonce_str=" + nonce_str + "&sign=" + sign + "");
		String sotpAuth = sotpGet.toString().replaceAll("\\+", "%2B");
		System.out.println("签名后："+sotpAuth);
		return sotpAuth;
	}

	/*
	 * 接口名称： getRequestAppid 接口作用： 认证系统响应报文的AppId 接口参数： 参数：sdkrequest 示例：
	 * {"appId":"e89aa9227b2a2d5","nonce_str":"796","sign":"a58ac3f7","status":0
	 * ,"message":{"errorMsg":""},"serverTime":"2016/04/15 22:04:25"} 返回值：
	 * string 示例 AppId
	 */
	public static String getAppId(String sdkrequest) {
		Map<String, Object> sotpMap = new HashMap<String, Object>();
		sotpMap = getUrlParams(sdkrequest);
		JSONObject jsonObject = JSONObject.fromObject(sdkrequest);
		sotpMap = toHashMap(jsonObject);
		String AppId = (String) sotpMap.get("appId");
		return AppId;
	}

	/*
	 * 4 接口名称： getResponseSign 接口作用： 验证 认证系统响应报文的合法性 接口参数： 参数：authResponse 示例：
	 * {"appId":"e89aa9227b2a2d5","nonce_str":"796","sign":"a58ac3f7","status":0
	 * ,"message":{"errorMsg":""},"serverTime":"2016/04/15 22:04:25"}
	 * 
	 * 返回值： Boolean
	 */
	public static boolean verifySotp(JSONObject jsonObject, String appId,String appKey) {
		boolean falg = false;
		Map<String, Object> sotpMap = toHashMap(jsonObject);
		sotpMap.remove("sign");

		String sign = signParaByAppKey(sotpMap, appKey);
		if (sign.equals(jsonObject.getString("sign"))) {
			falg = true;
		}
		return falg;
	}

	public static boolean verifyPlugin(JSONObject jsonObject, String appId, String appKey) {
		boolean falg = false;
		Map<String, Object> sotpMap = toHashMap(jsonObject);
		sotpMap.remove("sign");
		sotpMap.remove("message");

		String sign = signParaByAppKey(sotpMap, appKey);
		if (sign.equals(jsonObject.getString("sign"))) {
			falg = true;
		}
		return falg;
	}
	/**
	 * 将json格式的字符串解析成Map对象
	 * <li>json格式：{"name":"admin","retries":"3fff","testname"
	 * "ddd","testretries":"fffffffff"}
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
//			try {
//				result.put(key, new String(value.toString().getBytes("utf-8")));
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}

		return result;
	}

	/**
	 * 参数按照规则做sha加密
	 * 
	 * @param param
	 * @param appKey
	 * @return
	 */
	public static String signParaByAppKey(Map<String, Object> param, String appKey) {
		// 去除空值和签名值
		Map<String, Object> sPara = paraFilter(param);
		// 按照“参数=参数值”的模式用“&”字符拼接成字符串
		String prestr = createLinkString(sPara);
		// 生成签名结果
		prestr = prestr + "&appKey=" + appKey;
		System.out.println(prestr);
		String mysign = SHA_people(prestr);
		return mysign;
	}

	/**
	 * 把&拼接的String转map
	 * 
	 * @param param
	 * @return
	 */
	public static Map<String, Object> getUrlParams(String param) {
		Map<String, Object> map = new HashMap<String, Object>();
		if ("".equals(param) || null == param) {
			return map;
		}
		String[] params = param.split("&");

		for (int i = 0; i < params.length; i++) {
			String[] p = null;
			if (params[i].indexOf("sotpCode") != -1) {
				p = params[i].split("=", 2);
			} else if (params[i].indexOf("devInfo") != -1) {
				p = params[i].split("=", 2);
			} else if (params[i].indexOf("envInfo") != -1) {
				p = params[i].split("=", 2);
			}else if (params[i].indexOf("randa") != -1){
				p = params[i].split("=", 2);
			}else if(params[i].indexOf("dataInfo") != -1){
				p = params[i].split("=", 2);
			}
//			else if(params[i].indexOf("clientSign") != -1) {
//				p = params[i].split("=", 2);
//			}
			else {
				p = params[i].split("=");
			}
			if (p.length == 2) {
				map.put(p[0], p[1]);
			}
		}
		return map;
	}

	public static String SHA_people(String decript) {
		String sha = null;
		try {
			sha= SHA_people(decript.getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sha;
	}

	public static String SHA_people(byte[] decript) {
		try {
			MessageDigest digest = java.security.MessageDigest.getInstance("SHA");
			digest.update(decript);
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
		}
		return "";
	}
	
	public static void main(String[] args) {
		
		System.out.println(SHA_people("appId=545e8d3e0ecb42144d12fc2bb0e8f8ff61368411&message={\"data\":\"test的iphone@iPhone 5@2016/06/06 11:37:05@M00004A003V2.0abcd115754efee0004@3&testiphone@Simulator@2016/06/06 13:56:49@M00004A003V2.0abcd1575510ae0000c@3\"}&nonce_str=1338495054&serverTime=2016/06/06 13:57:36&status=0&appKey=45b33dd4a1eb894a5ff0f07e7097806fb1854d50"));
//		System.out.println("123");
//		System.out.println(SHA_people("appId=545e8d3e0ecb42144d12fc2bb0e8f8ff61368411&message={\"data\":\"test的iphone@Simulator@2016/06/06 10:48:32@M00004A003V2.0abcde5754e48d00030@3\"}&nonce_str=1390642722&serverTime=2016/06/06 11:09:26&status=0&appKey=45b33dd4a1eb894a5ff0f07e7097806fb1854d50"));
//		System.out.println("321");
	}

	   /**
  * byte转换为16进制
  */
 public static void printChart(byte[] bytes){
     for(int i = 0 ; i < bytes.length ; i++){
         String hex = Integer.toHexString(bytes[i] & 0xFF);
         if (hex.length() == 1) {
             hex = '0' + hex;
         }
         System.out.print(hex.toUpperCase() + " ");
     }
     System.out.println("");
 }
 
}
