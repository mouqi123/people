package com.people.sotp.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import net.sf.json.JSONException;
import net.sf.json.JSONObject;

@Controller
public class MxController {
	// 认证服务器 地址
	private String SOTPAUTH_IP = "172.168.1.234";
	private String SOTPAUTH_PORT = "9090";
	// private String SOTPAUTH_IP = "192.168.1.176";
	// private String SOTPAUTH_PORT = "8080";

	@RequestMapping(value = "mx")
	@ResponseBody
	public String mx(HttpServletRequest request, HttpServletResponse response) throws JSONException,
	    UnsupportedEncodingException {

		JSONObject jsonObject = null;
		int status = 0;
		String tdata = "";
		String resdata = "";

		SotpServerApi srv = null;

		int type = 0;

		HttpSession session = request.getSession();
		System.out.println("**   **********session id :" + session.getId());

		try {
			type = Integer.parseInt(request.getParameter("type"));
		} catch (Exception e) {
			return "there is no type parameter";
		}

		// String tt = "085693498517159a52d0000009314@1452515596";
		// String[] tr = tt.split("@");
		// System.out.println("tr s:"+tr[0]);

		switch (type) {
			// 下载插件
			case 1001:
				srv = new SotpServerApi(SOTPAUTH_IP, SOTPAUTH_PORT, session);
				System.out.println("1001  session id :" + session.getId());
				String phoneNum = request.getParameter("phoneNum");
				String devInfo = request.getParameter("devInfo");

				// String res = srv.sotpServerdown(phoneNum, devInfo);
				String res = srv.sotpServerdown("MINXING", devInfo);

				jsonObject = JSONObject.fromObject(res);

				status = jsonObject.getInt("status");

				resdata = "status=" + status;

				if (status == 0) {
					tdata = jsonObject.getString("data");
					resdata += "&plugin=" + tdata;
				}
				break;

			// 挑战应答
			case 1002:
				srv = new SotpServerApi(SOTPAUTH_IP, SOTPAUTH_PORT, session);
				System.out.println("1002  session id :" + session.getId());
				phoneNum = request.getParameter("phoneNum");
				String chello = request.getParameter("chello");

				String[] str = chello.split("@");

				String sotpId = str[0];
				String clientTime = str[1];
				System.out.println("1002 phoneNum:" + phoneNum + ", sotpId:" + sotpId + ", clientTime:" + clientTime
				    + ", chello:" + chello);
				res = srv.sotpServerAuth(phoneNum, sotpId, clientTime);
				jsonObject = JSONObject.fromObject(res);

				status = jsonObject.getInt("status");

				resdata = "status=" + status;

				if (status == 0) {
					tdata = jsonObject.getString("data");
					resdata += "&shello=" + tdata;
				} else {
					System.out.println("1002 err:" + jsonObject.getString("errMsg"));
				}
				System.out.println("1002 return:" + resdata);
				break;

			// 客户端身份验证
			case 1003:
				srv = new SotpServerApi(SOTPAUTH_IP, SOTPAUTH_PORT, session);

				System.out.println("1003  session id :" + session.getId());

				phoneNum = request.getParameter("phoneNum");
				chello = request.getParameter("cauth");

				String[] cauthstr = chello.split("@");

				sotpId = cauthstr[0];
				String cauth = cauthstr[1];

				System.out.println("1003 phoneNum:" + phoneNum + ", sotpId:" + sotpId + ", cauth:" + cauth + ", cauth:"
				    + chello);

				res = srv.sotpVerifyClient(phoneNum, sotpId, cauth);
				System.out.println("1003 sotpVerifyClient res:" + res);
				jsonObject = JSONObject.fromObject(res);

				status = jsonObject.getInt("status");

				resdata = "status=" + status;

				if (status == 0) {
					tdata = jsonObject.getString("data");
					resdata += "&sauth=" + tdata;
				} else {
					System.out.println("1003 err:" + jsonObject.getString("errMsg"));
				}
				System.out.println("1003 return:" + resdata);
				break;

			// 加解密测试
			case 1004:
//				String cipherdata = request.getParameter("data");
//
//				System.out.println("1004 receive cipherdata:" + cipherdata);
//
//				// byte[] ecb = Base64.decode(cipherdata.toCharArray());
//
//				// System.out.println("1004 decipherdata:"+decipherdata);
//
//				// 测试数据解密接口
////				String plain = SotpServerApi.sotpDataDecrypto(cipherdata, session);
//				JSONObject obj = JSONObject.fromObject(plain);
//				String data = obj.getString("data");
//				System.out.println("1004 recive plain:" + plain);
//
//				// 测试数据加密接口
//				String cdata = SotpServerApi.sotpDataCrypto(data, session);
//				obj = JSONObject.fromObject(plain);
//				data = obj.getString("data");
//
//				if (data.equals(cipherdata)) {
//					System.out.println("1004 SotpServerApi.sotpDataCrypto sotpDataDecrypto  API test OK!");
//
//					// 测试文件加解密接口
//					MxController tmpmx = new MxController();
//					tmpmx.testSotpSrvapi(plain, cdata, session);
//
//				} else {
//					System.out.println("1004 SotpServerApi.sotpDataCrypto sotpDataDecrypto API test error!");
//				}
//
//				resdata = "status=0&cipher=" + cdata;
//				System.out.println("1004 send cipher:" + cdata);
//
//				break;

		}

		return resdata;
	}

	// 测试文件加解密接口
	/**
	 * 
	 * @param plain
	 *          明文
	 * @param cipher
	 *          密文
	 * @param session
	 */
	public void testSotpSrvapi(String plain, String cipher, HttpSession session) {
		System.out.println("plain 1[" + plain);
		try {
			System.out.println("plain 2[" + new String(plain.getBytes("ISO-8859-1"), "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		String prefix = null;
		if (File.separatorChar == '\\') {
			prefix = "D:\\";
		} else {
			prefix = "/home/work/";
		}
		String src = prefix + "plain.dat"; // 原文件
		String src2 = prefix + "plain2.dat"; // 解密后文件
		String dest = prefix + "cipher.dat"; // 加密后文件

		try {
			String ret = SotpServerApi.sotpFileCrypto(src, dest, session); // 加密成密文
			System.out.println("SotpServerApi.sotpFileCrypto [" + ret);

			byte[] src_ = readFromFile(src); // 参数传递 密文
			byte[] dest_ = readFromFile(dest); // 加密后密文
			System.out.println("src_[" + src_.length);
			System.out.println("dest_[" + dest_.length);
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ 1");

			ret = SotpServerApi.sotpFileDecrypto(dest, src2, session); // 密文解密成明文
			System.out.println("SotpServerApi.sotpFileDecrypto [" + ret);
			byte[] src2_ = readFromFile(src2); // 解密后明文
			System.out.println("src2_[" + src2_.length);
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ 2");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private byte[] readFromFile(String fName) {
		File f = new File(fName);
		FileInputStream fis = null;
		byte[] b = new byte[(int) f.length()];
		try {
			fis = new FileInputStream(f);
			if (fis.read(b) != -1) {
				return b;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
