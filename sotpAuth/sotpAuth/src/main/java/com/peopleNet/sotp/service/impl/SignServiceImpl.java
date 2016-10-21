package com.peopleNet.sotp.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.ISignService;
import com.peopleNet.sotp.util.Base64;
import com.peopleNet.sotp.util.MD5;
import com.peopleNet.sotp.util.SHA;
import com.peopleNet.sotp.util.SMS4;


@Service
public class SignServiceImpl implements ISignService {
	private static LogUtil logger = LogUtil.getLogger(SignServiceImpl.class);

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
	 * 除去数组中的空值和签名参数
	 *
	 * @param sArray
	 *            签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	public static Map<String, Object> clientSignParaFilter(Map<String, Object> sArray) {

		Map<String, Object> result = new HashMap<String, Object>();

		if (sArray == null || sArray.size() <= 0) {
			return result;
		}

		for (String key : sArray.keySet()) {
			Object value = sArray.get(key);
			if (value == null || value.equals("") || key.equalsIgnoreCase("clientSign")) {
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

	@Override
	public String signParaByAppKey(Map<String, Object> param, String appKey, String method) {
		// 去除空值和签名值
		Map<String, Object> sPara = paraFilter(param);
		// 按照“参数=参数值”的模式用“&”字符拼接成字符串
		String prestr = createLinkString(sPara);
		// 生成签名结果
		String mysign = null;
		if (Constant.SIGN_METHOD.SHA.equals(method)) {
			prestr = prestr + "&appKey=" + appKey;
			logger.debug("prestr:--------" + prestr);
			mysign = SHA.SHA_people(prestr);
		}
		if (Constant.SIGN_METHOD.MD5.equals(method)) {
			mysign = MD5.sign(prestr, appKey, "utf-8").toUpperCase();
		}
		return mysign;
	}

	public String signParaBySessionKey(Map<String, Object> param, String sessionKey) {
		// 去除空值和签名值
		Map<String, Object> sPara = clientSignParaFilter(param);
		// 按照“参数=参数值”的模式用“&”字符拼接成字符串
		String prestr = createLinkString(sPara);
		logger.debug("clientSign prestr: " + prestr);
		// 生成签名结果
		String mysign = null;
		try {
			System.out.println(sessionKey);
			mysign = SM4Crypto(prestr,sessionKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mysign;
	}
	
	private static String SM4Crypto(String plain, String key) throws Exception {

		byte[] bmk = Base64.decode(key.toCharArray());
		byte[] mbmk = new byte[16];
		System.arraycopy(bmk, 0, mbmk, 0, 16);


			try {
				// 加密
				SMS4 sMS4 = new SMS4();
				byte[] text = plain.getBytes("UTF-8");
				byte[] encrypt = sMS4.encrypt(text, mbmk, 0, null);
				char[] encode = Base64.encode(encrypt);
				String base64cipher = new String(encode);
				
				return base64cipher;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

	}
	
	private static String SM4Decrypto(String cipher, String key) throws UnsupportedEncodingException,
    IllegalArgumentException {

	byte[] bmk = Base64.decode(key.toCharArray());
	byte[] text = Base64.decode(cipher.toCharArray());

	byte[] mbmk = new byte[16];
	System.arraycopy(bmk, 0, mbmk, 0, 16);

	SMS4 sMS4 = new SMS4();
	byte[] ecb;
	try {
		ecb = sMS4.decrypt(text, mbmk, 0, null);
		String plain = new String(ecb, "UTF-8");
		return plain;
	} catch (IllegalArgumentException e) {
		throw e;
	}
}
	public boolean verifySign(String appKey, Map<String, Object> paramMap, String sign) {
		boolean ret = true;
		String signStr = this.signParaByAppKey(paramMap, appKey, Constant.SIGN_METHOD.SHA);
		logger.debug("sign:" + sign + "---signstr:" + signStr + "appkey:" + appKey);
		if ((null == signStr) || (!signStr.equals(sign))) {
			ret = false;
		}
		return ret;
	}
	
	public boolean verifyClientSign(String sessionKey, Map<String, Object> paramMap, String clientSign) {
		boolean ret = true;
		/*try {
			System.out.println(SM4Decrypto(clientSign,sessionKey));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		String signStr = this.signParaBySessionKey(paramMap, sessionKey);
		logger.debug("clientSign:" + clientSign + "---clientSign server:" + signStr + "sessionkey:" + clientSign);
		if ((null == signStr) || (!signStr.equals(clientSign))) {
			ret = false;
		}
		return ret;
	}
}
