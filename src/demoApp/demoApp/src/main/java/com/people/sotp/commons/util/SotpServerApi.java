package com.people.sotp.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

public class SotpServerApi {
	private String host;
	private String port;
	private HttpSession session;

	// 日志
	// private final boolean DEBUG_LOG = true;

	// 连接sotp认证系统业务类型
	private final int PLUGINREG_TYPE = 1020;
	private final int CRYPTO_TYPE = 1061;
	private final int DECRYPTO_TYPE = 1062;

	// 默认参数
	private final String SOTPPAYINFO = "eyJob2xkaW5mbyI6IjEyMzQ1NiIsInByb3RlY3Rjb2RlIjoiMTIzNDU2In0=";

	// session参数
	private final String SOTP_USERNAME = "sotp_username";
	private final String SOTP_PLUGIN_ID = "sotp_plugin_id";
	private final String SOTP_CLIENT_R = "sotp_client_random";
	private final String SOTP_SERVER_R = "sotp_server_random";
	private final static String SOTP_KEY = "sotp_key";
	private final String WEB_CONTEXT = "sotpAuthjry";

	/**
	 * 调试日志
	 * 
	 * @param thost
	 * @param tport
	 * @param tsession
	 */
	public static void sotplog(String logmsg) {
		System.out.println("[SotpServerApi log]:" + logmsg);
	}

	/**
	 * 初始化 参数
	 * 
	 * @param args
	 * @return
	 */
	public SotpServerApi(String thost, String tport, HttpSession tsession) {
		this.host = thost;
		this.port = tport;
		this.session = tsession;
	}

	/**
	 * 下载插件
	 * 
	 * @param args
	 * @return
	 */
	public String sotpServerdown(String username, String devinfo) {
		String url = "http://" + host + ":" + port + "/" + WEB_CONTEXT + "/pluginreg?";
		url += "type=" + PLUGINREG_TYPE;
		url += "&phoneNum=" + username;
		url += "&sotpPayInfo=" + SOTPPAYINFO;
		url += "&devInfo=" + devinfo;

		String res = "";
		try {
			res = HttpUtils.get(url, "GB2312");
			SotpServerApi.sotplog("sotpServerdown url:" + url);
			SotpServerApi.sotplog("sotpServerdown res:" + res);
		} catch (Exception e1) {
			return retStr(-1, "", "sotpVerifyClient " + WEB_CONTEXT + " : " + e1.getMessage());
		}

		// json解析
		JSONObject jsonObject = null;
		JSONObject message = null;
		int status = -1;

		// 解析结果
		try {
			jsonObject = JSONObject.fromObject(res);
		} catch (Exception e) {
			SotpServerApi.sotplog("sotpServerdown " + WEB_CONTEXT + " res not json.");
			return retStr(-1, "", "sotpServerdown " + WEB_CONTEXT + " res not json.");
		}

		// 解析结果
		try {
			status = jsonObject.getInt("status");
			message = jsonObject.getJSONObject("message");
		} catch (Exception e) {
			SotpServerApi.sotplog("sotpServerdown " + WEB_CONTEXT + " res message error.");
			return retStr(-1, "", "sotpServerdown " + WEB_CONTEXT + " res message error.");
		}

		String data = "data";
		String err = "error";

		if (status == 0) {
			data = message.getString("data");
			String hwinfo = message.getString("hwInfo");
			data += "," + hwinfo;
		} else {
			err = message.getString("errorMsg");
		}

		return retStr(status, data, err);
	}

	/**
	 * 
	 * 生成服务器认证消息
	 * 
	 * @param status
	 * @param data
	 * @param errMsg
	 * @return
	 */
	public String sotpServerAuth(String username, String sotpId, String ctime) {
		// 生成随机数
		String srvR = Long.toString(System.currentTimeMillis());

		if (username == null || sotpId == null || ctime == null) {
			return retStr(-1, "", "sotpServerAuth username or sotpId, ctime null");
		}

		if (session == null) {
			return retStr(-1, "", "sotpServerAuth session null");
		}

		session.setAttribute(SOTP_USERNAME, username);
		session.setAttribute(SOTP_PLUGIN_ID, sotpId);
		session.setAttribute(SOTP_CLIENT_R, ctime);
		session.setAttribute(SOTP_SERVER_R, srvR);
		SotpServerApi.sotplog("sotpServerAuth srvR:" + srvR);

		return retStr(0, srvR, "");
	}

	/**
	 * 
	 * 验证客户端认证消息
	 * 
	 * @param status
	 * @param data
	 * @param errMsg
	 * @return
	 */
	public String sotpVerifyClient(String username, String sotpId, String ClientAuth) {
		if (username == null || sotpId == null || ClientAuth == null) {
			return retStr(-1, "", "sotpVerifyClient username or sotpId ClientAuth null");
		}
		if (session == null) {
			return retStr(-1, "", "sotpVerifyClient session null");
		}

		// 客户端、服务端随机数
		Object ctime_ = session.getAttribute(SOTP_CLIENT_R);
		Object srand_ = session.getAttribute(SOTP_SERVER_R);
		if (ctime_ == null) {
			return retStr(-1, "", "sotpVerifyClient can't get SOTP_CLIENT_R from session.");
		}
		if (srand_ == null) {
			return retStr(-1, "", "sotpVerifyClient can't get SOTP_SERVER_R from session.");
		}
		String ctime = ctime_.toString();
		String srand = srand_.toString();

		// 解密, 同时比对R, 生成sess_key
		String url = "", res = "";
		String data = "", err = "";

		url = "http://" + host + ":" + port + "/" + WEB_CONTEXT + "/gensesskey?";
		url += "type=" + DECRYPTO_TYPE;
		url += "&randa=" + ctime;
		url += "&randb=" + srand;
		url += "&sotpId=" + sotpId;
		url += "&dataInfo=" + ClientAuth;

		url = url.replaceAll("\\+", "%2B");
		try {
			res = HttpUtils.get(url, "GB2312");
			SotpServerApi.sotplog("sotpVerifyClient 协商会话密钥url:" + url);
			SotpServerApi.sotplog("sotpVerifyClient " + WEB_CONTEXT + " res:" + res);
		} catch (Exception e1) {
			return retStr(-1, "", "sotpVerifyClient " + WEB_CONTEXT + " : " + e1.getMessage());
		}

		// json解析
		JSONObject jsonObject = null;
		JSONObject message = null;
		int status = -1;

		// 解析结果
		try {
			jsonObject = JSONObject.fromObject(res);
		} catch (Exception e) {
			return retStr(-1, "", "sotpVerifyClient " + WEB_CONTEXT + " response not json.");
		}
		// 解析message
		try {
			status = jsonObject.getInt("status");
			message = jsonObject.getJSONObject("message");
		} catch (Exception e) {
			return retStr(-1, "", "sotpVerifyClient " + WEB_CONTEXT + " response message not json.");
		}
		if (status == 0) {
			data = message.getString("data");
		} else {
			err = message.getString("errorMsg");
			return retStr(status, "error ", err);
		}
		// 设置到session中
		session.setAttribute(SOTP_KEY, data);
		SotpServerApi.sotplog("sotpVerifyClient session key:" + data);

		// 加密 客户端T
		url = "http://" + host + ":" + port + "/" + WEB_CONTEXT + "/crypto?";
		url += "type=" + CRYPTO_TYPE;
		url += "&phoneNum=" + username;
		url += "&sotpId=" + sotpId;
		url += "&dataInfo=" + ctime;

		url = url.replaceAll("\\+", "%2B");
		try {
			res = HttpUtils.get(url, "GB2312");
			SotpServerApi.sotplog("sotpVerifyClient  加密 url:" + url);
			SotpServerApi.sotplog("sotpVerifyClient  " + WEB_CONTEXT + " res:" + res);
		} catch (Exception e1) {
			return retStr(-1, "", "sotpVerifyClient " + WEB_CONTEXT + " : " + e1.getMessage());
		}

		// 解析结果
		try {
			jsonObject = JSONObject.fromObject(res);
		} catch (Exception e) {
			return retStr(-1, "", "sotpVerifyClient  " + WEB_CONTEXT + " crypto response not json.");
		}
		try {
			status = jsonObject.getInt("status");
			message = jsonObject.getJSONObject("message");
		} catch (Exception e) {
			return retStr(-1, "", "sotpVerifyClient " + WEB_CONTEXT + " response message error.");
		}
		if (status == 0) {
			data = message.getString("data");
		} else {
			err = message.getString("errorMsg");
			return retStr(status, data, "");
		}
		return retStr(0, data, "");
	}

//	/**
//	 * 数据加密
//	 * 
//	 * @param status
//	 * @param data
//	 * @param errMsg
//	 * @return
//	 * @throws UnsupportedEncodingException
//	 */
//	public static String sotpDataCrypto(String plain, HttpSession session) {
//		if (plain == null || plain.length() <= 0) {
//			return retStr(-1, "", "plain null");
//		}
//		if (session == null) {
//			return retStr(-1, "", "session null");
//		}
//
//		Object key_ = session.getAttribute(SOTP_KEY);
//		if (key_ == null) {
//			return retStr(-1, "", "session key err.");
//		}
//		String key = key_.toString();
//
//		if (key == null || key.length() <= 0) {
//			return retStr(-1, "", "session key err.");
//		}
//		try {
//			String data = sotpDataCrypto(plain, key);
//			return retStr(0, data, "");
//		} catch (UnsupportedEncodingException e) {
//			return retStr(-1, "", e.getMessage());
//		} catch (IllegalArgumentException e) {
//			return retStr(-1, "", e.getMessage());
//		} catch (Exception e) {
//			return retStr(-1, "", e.getMessage());
//		}
//	}

	public static String sotpDataCrypto(String plain, String key) throws Exception {
		SotpServerApi.sotplog("sotpDataCrypto get session key:" + key);

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
				SotpServerApi.sotplog("sotpDataCrypto cipher:" + base64cipher);

				return base64cipher;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

	}

	/**
	 * 数据解密
	 * 
	 * @param status
	 * @param data
	 * @param errMsg
	 * @return
	 * @throws UnsupportedEncodingException
	 */
//	public static String sotpDataDecrypto(String cipher, String session) {
//		if (cipher == null || cipher.length() <= 0) {
//			return retStr(-1, "", "cipher null");
//		}
//		if (session == null) {
//			return retStr(-1, "", "session null");
//		}
//
//		Object key_ = session.getAttribute(SOTP_KEY);
//		if (key_ == null) {
//			return retStr(-1, "", "session key err.");
//		}
//		String key = key_.toString();

//		if (session == null || session.length() <= 0) {
//			return retStr(-1, "", "session key err.");
//		}
//		try {
//			String data = sotpDataDecrypto(cipher, session);
//			return retStr(0, data, "");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return retStr(-1, "", e.getMessage());
//		} 
//	}

	public static String sotpDataDecrypto(String cipher, String key) throws UnsupportedEncodingException,
	    IllegalArgumentException {
		SotpServerApi.sotplog("sotpDataDecrypto get session key:" + key);

		byte[] bmk = Base64.decode(key.toCharArray());
		byte[] text = Base64.decode(cipher.toCharArray());

		byte[] mbmk = new byte[16];
		System.arraycopy(bmk, 0, mbmk, 0, 16);

		SMS4 sMS4 = new SMS4();
		byte[] ecb;
		try {
			ecb = sMS4.decrypt(text, mbmk, 0, null);
			String plain = new String(ecb, "UTF-8");
			SotpServerApi.sotplog("sotpDataDecrypto plain:" + plain);
			return plain;
		} catch (IllegalArgumentException e) {
			throw e;
		}
	}

	/**
	 * 文件加密
	 * 
	 * @param status
	 * @param data
	 * @param errMsg
	 * @return
	 * @throws IOException
	 */
	public static String sotpFileCrypto(String sfile, String dfile, HttpSession session) {
		if (sfile == null || sfile.length() <= 0) {
			return retStr(-1, "", "sfile:" + sfile);
		}
		if (dfile == null || dfile.length() <= 0) {
			return retStr(-1, "", "dfile:" + dfile);
		}
		if (session == null) {
			return retStr(-1, "", "session null");
		}

		Object key_ = session.getAttribute(SOTP_KEY);
		if (key_ == null) {
			return retStr(-1, "", "session key err.");
		}
		String key = key_.toString();

		if (key == null || key.length() <= 0) {
			return retStr(-1, "", "session key err:" + key);
		}
		SotpServerApi.sotplog("session key=" + key);

		// 判断文件，文件大小
		File f = new File(sfile);
		if (!f.exists() || !f.isFile() || !f.canRead()) {
			return retStr(-1, "", "file doesn't exist or is not a file");
		}
		byte[] b = new byte[(int) f.length()];

		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {
			fis = new FileInputStream(f);
			fos = new FileOutputStream(dfile);

			if (fis.read(b) != -1) {
				// 先base64 编码 后加密
				String base64 = new String(Base64.encode(b));
				String crypto = sotpDataCrypto(base64, key);
				fos.write(crypto.getBytes());
				fos.flush();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return retStr(-1, "", e.getMessage());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return retStr(-1, "", e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return retStr(-1, "", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return retStr(-1, "", e.getMessage());
		} finally {
			try {
				if (fos != null) fos.close();
				if (fis != null) fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return retStr(0, "", "");
	}

	/**
	 * 文件解密
	 * 
	 * @param status
	 * @param data
	 * @param errMsg
	 * @return
	 * @throws IOException
	 */
	public static String sotpFileDecrypto(String sfile, String dfile, HttpSession session) {
		if (sfile == null || sfile.length() <= 0) {
			return retStr(-1, "", "sfile:" + sfile);
		}
		if (dfile == null || dfile.length() <= 0) {
			return retStr(-1, "", "dfile:" + dfile);
		}
		if (session == null) {
			return retStr(-1, "", "session null");
		}

		Object key_ = session.getAttribute(SOTP_KEY);
		if (key_ == null) {
			return retStr(-1, "", "session key err.");
		}
		String key = key_.toString();

		if (key == null || key.length() <= 0) {
			return retStr(-1, "", "session key err:" + key);
		}
		SotpServerApi.sotplog("session key=" + key);

		// 判断文件，文件大小
		File f = new File(sfile);
		if (!f.exists() || !f.isFile() || !f.canRead()) {
			return retStr(-1, "", "file doesn't exist or is not a file");
		}
		byte[] b = new byte[(int) f.length()];

		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(f);
			fos = new FileOutputStream(dfile);

			byte[] decode = null;

			if (fis.read(b) != -1) {
				// 先解密 后base64 解码
				String str = new String(b);
				String decrypto = sotpDataDecrypto(str, key);
				decode = Base64.decode(decrypto.toCharArray());
				fos.write(decode);
				fos.flush();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return retStr(-1, "", e.getMessage());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return retStr(-1, "", e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return retStr(-1, "", e.getMessage());
		} finally {
			try {
				if (fos != null) fos.close();
				if (fis != null) fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return retStr(0, "", "");
	}

	/**
	 * 封装 json 返回数据
	 * 
	 * @param status
	 * @param data
	 * @param errMsg
	 * @return
	 */
	private static String retStr(int status, String data, String errMsg) {
		JSONObject jsonObj = new JSONObject();

		jsonObj.accumulate("status", status);

		if (status == 0) {
			if (data != null && data.length() > 0) {
				jsonObj.accumulate("data", data);
			}
		} else {
			if (errMsg != null && errMsg.length() > 0) {
				jsonObj.accumulate("errMsg", errMsg);
			}
		}
		return jsonObj.toString();
	}

	/*
	 * 字符转换
	 */
	public static String revert(String str) {
		str = (str == null ? "" : str);
		if (str.indexOf("\\u") == -1)// 如果不是unicode码则原样返回
		  return str;
		StringBuffer sb = new StringBuffer(1000);

		for (int i = 0; i < str.length() - 6;) {
			String strTemp = str.substring(i, i + 6);
			String value = strTemp.substring(2);
			int c = 0;
			for (int j = 0; j < value.length(); j++) {
				char tempChar = value.charAt(j);
				int t = 0;
				switch (tempChar) {
					case 'a':
						t = 10;
						break;
					case 'b':
						t = 11;
						break;
					case 'c':
						t = 12;
						break;
					case 'd':
						t = 13;
						break;
					case 'e':
						t = 14;
						break;
					case 'f':
						t = 15;
						break;
					default:
						t = tempChar - 48;
						break;
				}

				c += t * ((int) Math.pow(16, (value.length() - j - 1)));
			}
			sb.append((char) c);
			i = i + 6;
		}
		return sb.toString();
	}

}
