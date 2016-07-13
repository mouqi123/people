package com.people.sotp.urlcontrollers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SHA {

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

	public static String SHA_people(String decript) {
		return SHA_people(decript.getBytes());
	}
}
