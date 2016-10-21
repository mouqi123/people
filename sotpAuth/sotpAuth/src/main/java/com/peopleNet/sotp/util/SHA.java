package com.peopleNet.sotp.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.peopleNet.sotp.log.LogUtil;

public class SHA {
	private static LogUtil log = LogUtil.getLogger(SHA.class);

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
			log.error("SHA error!");
		}
		return "";
	}

	public static String SHA_people(String decript) {
		String sha = null;
		try {
			sha = SHA_people(decript.getBytes(com.peopleNet.sotp.util.StringUtils.getEncoding(decript)));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sha;
	}
}
