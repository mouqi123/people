package com.people.sotp.commons.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密解密工具
 * 
 */
public class CryptUtil {
	private static DES des;

	static {
		try {
			des = new DES();
		} catch (Exception e) {

		}
	}

	/**
	 * 解密函数
	 * 
	 * @param encryptString
	 *            String 已经加密的字符串
	 * @return String 解密后的字符串
	 */
	public static String decrypt(String encryptString) {
		if (des != null) {
			try {
				return des.decrypt(encryptString);
			} catch (Exception e) {
				return encryptString;
			}
		} else {
			return encryptString;
		}
	}

	/**
	 * 加密函数
	 * 
	 * @param originString
	 *            String 原始字符串，即需要加密的字符串
	 * @return String 加密后的字符串
	 */
	public static String encrypt(String originString) {
		if (des != null) {
			try {
				return des.encrypt(originString);
			} catch (Exception e) {
				return originString;
			}
		} else {
			return originString;
		}
	}

	public static String SHA1(String data) {
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			byte[] digest = sha1.digest(data.getBytes());
			return getHexString(digest);
		} catch (NoSuchAlgorithmException e) {
		}
		return data;
	}

	/**
	 * Converts data into a hex string (two characters per byte).
	 */
	public static String getHexString(byte[] data) {
		StringBuffer res = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int upper = (data[i] & 0xf0) >>> 4;
			int lower = data[i] & 0x0f;
			res.append(Integer.toHexString(upper));
			res.append(Integer.toHexString(lower));
		}
		return res.toString();
	}

	public static void main(String[] args) throws Exception {
		String originString = "123qwe";
		String encryptString = CryptUtil.encrypt(originString);
		System.out.println("原始串：" + originString);
		System.out.println("加密后：" + encryptString);
		System.out.println("解密后：" + CryptUtil.decrypt("171f68e9f2ab1c48b2f8c9e4c5b9541b"));

		// 代填主帐号密码解密
		// System.out.println("解密后："+new String(new
		// DesSecurity("12345678","12345678").decrypt("rN0tKHY8LLk=")));

	}
}
