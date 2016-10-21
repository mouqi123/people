package com.peopleNet.sotp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class sendRequest {

	public static String sendPost(String urll, String data) {
		
		 data = data.replaceAll("\\+", "%2B");
		 data = data.replaceAll(" ", "%20");
		BufferedReader in = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urll);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			out.write(data);
			out.flush();
			out.close();

			String line, result = "";
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			while ((line = in.readLine()) != null) {
				result += line + "\n";
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace(System.out);
		} finally {
			try {
				in.close();
				conn.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String sendGet(String urll) {

//		String urldata = urll.toString().replaceAll("\\+", "%2B");
//		urldata = urldata.replaceAll(" ", "%20");
//		urldata=urldata.replaceAll("(\r\n|\r|\n|\n\r)", "");   
		BufferedReader in = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urll);
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			String line, result = "";
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			while ((line = in.readLine()) != null) {
				result += line + "\n";
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace(System.out);
		} finally {
			try {
				in.close();
				conn.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		// String data =
		// "{\"type\":-1,\"status\":103,\"message\":{\"errorMsg\":\"para type
		// unkown.\"},\"serverTime\":\"2016/01/21 17:37:30\"}";
		// String core = "{\"serverTime\":\"2016/01/21
		// 17:39:47\",\"type\":-1,\"message\":{\"errorMsg\":\"para type
		// unkown.\"},\"status\":103}";
		// JSONObject jsonsotp = new JSONObject(data);
		// JSONObject json = new JSONObject(core);
		// System.out.println(jsonsotp.get("message"));
//		// System.out.println(json.get("message"));
//		String data = "http://192.168.1.189:8080/sotpAuth/getchallenge?type=1090&userId=1&phoneNum=13051126671&sotpId=0c56a6e2f6d12cf172f00000391";
//		String cade = sendGet(data);
//		String urldata = data.toString().replaceAll("\\+", "%2B");
//		urldata = urldata.replaceAll(" ", "%20");
//		System.out.println(urldata);
//		String data = "devInfo=ewogICJwcm9kdWN0X3R5cGUiIDogImlQaG9uZSA1IiwKICAiZGV2X3R5cGUiIDogIklvcyIsCiAgInV1aWQiIDogIkNBMEU4OEZBLUZEN0YtNDU2Ri05NTIzLUFEQUNBMTdEQTdENiIsCiAgIm1hYyIgOiAiMDAwMDAwMDAwMCIKfQ==";
//		String [] str=data.split("=",2);
//		String data="{\"username\":\"13146859037\",\"pwd\":\"123456\",\"devinfo\":{\"address\":\"北京\",\"phoneModel\":\"Android\",\"loginTime\":\"2016-05-04 11:10:01\"},\"service\":\"login\"}";
		String data="{\"service\":\"qiuckNote\",\"phoneNum\":\"15764256056\"}";
		String str=sendPost("http://192.168.70.3:8080/autonomousApp/face/controllers.do", data);
		System.out.println(str);
	}
}
