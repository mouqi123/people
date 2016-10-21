package com.people.sotp.commons.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang.StringUtils;

public class HttpUtils {
	public static String get(String url, String charset) throws Exception {
		if (StringUtils.isBlank(url) || StringUtils.isBlank(charset)) {
			throw new Exception("url not be null and charset not be null.");
		}

		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		StringBuilder res = new StringBuilder();

		try {
			URL url0 = new URL(url);
			URLConnection client = url0.openConnection();
			// setConnectTimeout：设置连接主机超时（单位：毫秒）
			// setReadTimeout：设置从主机读取数据超时（单位：毫秒）
			client.setConnectTimeout(5000);
			client.setReadTimeout(10000);

			client.connect();

			is = client.getInputStream();
			isr = new InputStreamReader(is, charset);
			br = new BufferedReader(isr);

			String line = null;
			while ((line = br.readLine()) != null) {
				res.append(line.trim() + "\n");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (br != null) br.close();
			if (isr != null) isr.close();
			if (is != null) is.close();
		}
		return res.toString();
	}

}
