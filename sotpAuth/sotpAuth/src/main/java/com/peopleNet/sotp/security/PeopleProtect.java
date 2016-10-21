package com.peopleNet.sotp.security;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;

import com.peopleNet.sotp.util.StringUtils;

public class PeopleProtect {
	// 增加 password 的复杂度
	private static final String password = new String(new byte[] { 82, 70, -29, -128, 47, -83, 56, 127, 42, 11, 59, 104,
	        122, 62, -43, 4, -27, 124, -80, 94, -75, -27, 103, 73 });

	public static void main(String[] args) {
		String strOld = "中国，你好!";
		System.out.println(StringUtils.getEncoding(strOld));

		System.out.println("原文[" + strOld);
		String transform = transform(strOld);
		System.out.println("加密[" + transform);
		String revert = revert(transform);
		System.out.println("解密[" + revert);

		System.out.println("中文");
		System.out.println(StringUtils.getEncoding("中文"));

		String encoding = System.getProperty("file.encoding");
		System.out.println("系统默认编码 :" + encoding);

		try {

			printChart("中文".getBytes("UTF-8"));

			printChart("中文".getBytes());

			printChart("中文".getBytes());

			printChart("中文".getBytes("utf-8"));

			printChart("中文".getBytes("GB2312"));

			printChart("中文".getBytes("ISO-8859-1"));

			System.out.println("-------------");

			System.out.println("中文".getBytes());

			System.out.println("中文".getBytes("utf-8"));

			System.out.println("中文".getBytes("GB2312"));

			System.out.println("中文".getBytes("ISO-8859-1"));

			System.out.println("-------------");

			System.out.println(new String("中文".getBytes()));

			System.out.println(new String("中文".getBytes(), "GB2312"));

			System.out.println(new String("中文".getBytes(), "ISO-8859-1"));

			System.out.println(new String("中文".getBytes("GB2312")));

			System.out.println("-------xxxx------");

			System.out.println(new String("中文".getBytes(StringUtils.getEncoding("中文")), "GB2312"));

			System.out.println(new String("中文".getBytes(StringUtils.getEncoding("中文")), "utf-8"));

			System.out.println(new String("中文".getBytes(StringUtils.getEncoding("中文")), "iso-8859-1"));

			System.out.println(new String("中文".getBytes(StringUtils.getEncoding("中文"))));

			System.out.println("-------------");

			System.out.println(new String("中文".getBytes("GB2312"), "GB2312"));

			System.out.println(new String("中文".getBytes("GB2312"), "ISO-8859-1"));

			System.out.println(new String("中文".getBytes("ISO-8859-1")));

			System.out.println(new String("中文".getBytes("ISO-8859-1"), "GB2312"));

			System.out.println(new String("中文".getBytes("ISO-8859-1"), "ISO-8859-1"));

			String zhongwen = new String(strOld.getBytes(), "UTF-8");
			System.out.println(StringUtils.getEncoding(zhongwen));
			System.out.println(zhongwen);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		System.out.println("-----1111111111--------");

		try {

			String textStr1 = new String(
			        new byte[] { (byte) 0xe4, (byte) 0xb8, (byte) 0xad, (byte) 0xe5, (byte) 0x9b, (byte) 0xbd },
			        "UTF-8");
			System.out.println(textStr1);

			textStr1 = new String(
			        new byte[] { (byte) 0xe4, (byte) 0xb8, (byte) 0xad, (byte) 0xe5, (byte) 0x9b, (byte) 0xbd },
			        "GB2312");
			System.out.println(textStr1);

			printChart(textStr1.getBytes());

			printChart(textStr1.getBytes("GB2312"));

			String textStr2 = new String(new byte[] { (byte) 0xd6, (byte) 0xd0, (byte) 0xb9, (byte) 0xfa }, "GB2312");
			System.out.println(textStr2);

			// textStr2 = new String(new byte[]{(byte)0xd6, (byte)0xd0,
			// (byte)0xb9, (byte)0xfa}, "UTF-8");
			// System.out.println(textStr2);

			System.out.println(StringUtils.getEncoding(textStr2));

			printChart(textStr2.getBytes("GB2312"));

			printChart(textStr2.getBytes("utf-8"));

			// textStr2 = new
			// String(textStr2.getBytes(StringUtils.getEncoding(textStr2)),
			// "UTF-8");
			textStr2 = new String(textStr2.getBytes("GB2312"), "UTF-8");
			System.out.println(textStr2);

			printChart(textStr2.getBytes());

			printChart(textStr2.getBytes("GB2312"));

			// %e4%b8%ad%e5%9b%bd
			// 汉字“中”用UTF-8进行URLEncode的时候，得到%e4%b8%ad(对应的ISO-8859-1的字符是ä¸­)
			String item = new String(new byte[] { (byte) 0xe4, (byte) 0xb8, (byte) 0xad }, "UTF-8");
			// 中
			System.out.println(item);

			item = new String(new byte[] { (byte) 0xe4, (byte) 0xb8, (byte) 0xad }, "ISO-8859-1");
			// ä¸­
			System.out.println(item);

			System.out.println(new BigInteger("253").toByteArray());
			System.out.println(Integer.toBinaryString(253));

			// 中
			item = new String(item.getBytes("ISO_8859_1"), "UTF-8");
			System.out.println(item);
			// ä¸­
			item = new String(item.getBytes("UTF-8"), "ISO_8859_1");
			System.out.println(item);

			// 汉字中以UTF-8编码为 %E4%B8%AD（3字节）
			System.out.println(URLEncoder.encode("中", "UTF-8"));
			// 汉字中以UTF-8编码为 %3F （1字节
			// 这是由于汉字在ISO-8859-1字符集中不存在，返回的是？在ISO-8859-1下的编码）
			System.out.println(URLEncoder.encode("中", "ISO-8859-1"));
			// 汉字中以UTF-8编码为 %D6%D0 （2字节）
			System.out.println(URLEncoder.encode("中", "GB2312"));

			// 把汉字中对应的UTF-8编码 %E4%B8%AD 用UTF-8解码得到正常的汉字 中
			System.out.println(URLDecoder.decode("%E4%B8%AD", "UTF-8"));
			// 把汉字中对应的ISO-8859-1编码 %3F 用ISO-8859-1解码得到?
			System.out.println(URLDecoder.decode("%3F", "ISO-8859-1"));
			// 把汉字中对应的GB2312编码 %D6%D0 用GB2312解码得到正常的汉字 中
			System.out.println(URLDecoder.decode("%D6%D0", "GB2312"));
			// 把汉字中对应的UTF-8编码 %E4%B8%AD 用ISO-8859-1解码
			// 得到字符ä¸­（这个就是所谓的乱码，其实是3字节%E4%B8%AD中每个字节对应的ISO-8859-1中的字符）
			// ISO-8859-1字符集使用了单字节内的所有空间
			System.out.println(URLDecoder.decode("%E4%B8%AD", "ISO-8859-1"));
			// 把汉字中对应的UTF-8编码 %E4%B8%AD 用GB2312解码
			// 得到字符涓�，因为前2字节 %E4%B8对应的GB2312的字符就是涓，而第3字节%AD在GB2312编码中不存在，故返回？
			System.out.println(URLDecoder.decode("%E4%B8%AD", "GB2312"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * byte转换为16进制
	 */
	public static void printChart(byte[] bytes) {
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase() + " ");
		}
		System.out.println("");
	}

	// 加密
	public static String transform(String strOld) {
		try {
			byte[] data = strOld.getBytes();
			byte[] encrypt = encrypt(data, password);
			return byte2hex(encrypt);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 解密
	public static String revert(String strNew) {
		try {
			byte[] data = hex2bytes(strNew);
			return new String(decrypt(data, password));
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 加密
	 * 
	 * @param datasource
	 *            byte[]
	 * @param password
	 *            String
	 * @return byte[]
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static byte[] encrypt(byte[] datasource, String password)
	        throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
	        IllegalBlockSizeException, BadPaddingException {
		SecureRandom random = new SecureRandom();
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
		// 现在，获取数据并加密
		// 正式执行加密操作
		return cipher.doFinal(datasource);
	}

	/**
	 * 解密
	 * 
	 * @param src
	 *            byte[]
	 * @param password
	 *            String
	 * @return byte[]
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src, String password) throws InvalidKeyException, NoSuchAlgorithmException,
	        InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		// DES算法要求有一个可信任的随机数源
		SecureRandom random = new SecureRandom();
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// 真正开始解密操作
		return cipher.doFinal(src);
	}

	/**
	 * 二行制字节数组转十六进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) { // 一个字节的数，
		// 转成16进制字符串
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			// 整数转成十六进制表示
			stmp = (Integer.toHexString(b[n] & 0xFF));
			stmp = stmp.length() == 1 ? "0" + stmp : stmp;
			hs = hs.concat(stmp);
		}
		return hs.toUpperCase(); // 转成大写
	}

	/**
	 * 十六进制字符串转二行制字节数组
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] hex2bytes(String str) {
		int len = str.length();
		byte[] buf = new byte[len / 2];
		String tmp = "";
		for (int i = 0; i < len; i += 2) {
			tmp = str.substring(i, i + 2);
			buf[i / 2] = (byte) Integer.parseInt(tmp, 16);
		}
		return buf;
	}

}
