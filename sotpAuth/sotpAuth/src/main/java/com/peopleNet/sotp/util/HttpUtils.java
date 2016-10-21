package com.peopleNet.sotp.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;

import com.peopleNet.sotp.log.LogUtil;

/**
 * HttpUtils
 * 
 */
public abstract class HttpUtils {
	private static LogUtil log = LogUtil.getLogger(HttpUtils.class);

	public static String formatHtml(String html) {
		if (html == null)
			return null;
		String result = html.replaceAll("\\>\\<", ">\n<");
		return result;
	}

	public static String postForHeader(String targetUrl, String output, String headerFieldName) {
		try {
			HttpURLConnection client = (HttpURLConnection) new URL(targetUrl).openConnection();
			client.setRequestMethod("POST");
			client.setDoOutput(true);

			OutputStream os = null;
			try {
				os = client.getOutputStream();
				os.write(output.getBytes());
				os.flush();
			} finally {
				if (os != null) {
					os.close();
				}
			}
			if (client.getResponseCode() == 201)
				return client.getHeaderField(headerFieldName);
			return null;

		} catch (Exception ex) {
			return null;
		}
	}

	public static String post(String targetUrl, String output) {
		try {
			HttpURLConnection client = (HttpURLConnection) new URL(targetUrl).openConnection();
			client.setRequestMethod("POST");
			client.setDoOutput(true);

			OutputStream os = null;
			try {
				os = client.getOutputStream();
				os.write(output.getBytes());
				os.flush();
			} finally {
				if (os != null) {
					os.close();
				}
			}

			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
				StringBuilder content = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					content.append(line);
				}
				return content.toString();
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception ex) {
			return null;
		}
	}

	public static String post(String targetUrl, String output, String Encoding) {
		try {
			HttpURLConnection client = (HttpURLConnection) new URL(targetUrl).openConnection();
			client.setRequestMethod("POST");
			client.setDoOutput(true);

			OutputStream os = null;
			try {
				os = client.getOutputStream();
				os.write(output.getBytes(Encoding));
				os.flush();
			} finally {
				if (os != null) {
					os.close();
				}
			}

			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(client.getInputStream(), Encoding));
				StringBuilder content = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					content.append(line);
				}
				return content.toString();
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception ex) {
			return null;
		}
	}

	public static String get(String url, String charset) {
		log.info(url);
		StringBuilder res = new StringBuilder();
		BufferedReader br = null;
		try {
			URLConnection client = new URL(url).openConnection();
			client.connect();
			br = new BufferedReader(new InputStreamReader(client.getInputStream(), charset));
			String line = null;
			while ((line = br.readLine()) != null) {
				res.append(line.trim() + "\n");
			}
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				if (br != null)
					br.close();
			} catch (Exception e) {
			}

			return null;
		}
		return res.toString().trim();

	}

	public static String postAObject(String targetUrl, Object obj) {
		log.info(targetUrl);
		try {
			HttpURLConnection client = (HttpURLConnection) new URL(targetUrl).openConnection();
			client.setRequestMethod("POST");
			client.setDoOutput(true);
			client.setRequestProperty("accept", "*/*");
			client.setRequestProperty("Content-type", "application/x-java-serialized-object");
			ObjectOutputStream os = null;
			try {
				os = new ObjectOutputStream(client.getOutputStream());
				os.writeObject(obj);
				os.flush();
			} finally {
				if (os != null) {
					os.close();
				}
			}
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
				StringBuilder content = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					content.append(line);
				}
				return content.toString();

			} finally {
				if (reader != null) {
					reader.close();
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static Object getAObject(String targetUrl) {
		log.info(targetUrl);
		ObjectInputStream ois = null;
		try {
			HttpURLConnection client = (HttpURLConnection) new URL(targetUrl).openConnection();
			client.setDoInput(true);
			client.setRequestProperty("accept", "*/*");
			client.setRequestProperty("Content-type", "application/x-java-serialized-object");
			client.connect();

			ois = new ObjectInputStream(client.getInputStream());
			return ois.readObject();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		return null;
	}

	public static String post(String targetUrl, String output, String Encoding, Map<String, String> properties) {
		try {
			HttpURLConnection client = (HttpURLConnection) new URL(targetUrl).openConnection();
			client.setRequestMethod("POST");
			client.setDoOutput(true);
			if (properties != null) {
				for (Entry<String, String> item : properties.entrySet()) {
					client.setRequestProperty(item.getKey(), item.getValue());
				}
			}
			OutputStream os = null;
			try {
				os = client.getOutputStream();
				os.write(output.getBytes(Encoding));
				os.flush();
			} finally {
				if (os != null) {
					os.close();
				}
			}

			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(client.getInputStream(), Encoding));
				StringBuilder content = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					content.append(line);
				}
				String session = client.getHeaderField("Set-Cookie");
				properties.put("Set-Cookie", session);
				return content.toString();
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception ex) {
			return null;
		}
	}

	public static String get(String url, String charset, Map<String, String> properties) {
		log.info(url);
		StringBuilder res = new StringBuilder();
		BufferedReader br = null;
		try {
			URLConnection client = new URL(url).openConnection();
			if (properties != null) {
				for (Entry<String, String> item : properties.entrySet()) {
					client.setRequestProperty(item.getKey(), item.getValue());
				}
			}
			client.connect();
			br = new BufferedReader(new InputStreamReader(client.getInputStream(), charset));
			String line = null;
			while ((line = br.readLine()) != null) {
				res.append(line.trim() + "\n");
			}
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				if (br != null)
					br.close();
			} catch (Exception e) {
			}

			return null;
		}
		return res.toString().trim();

	}

	public static void main(String[] argv) {
		String server = "http://db-testing-atd10.db01.baidu.com:8080/atp2/";
		String reportUrl = server + "web.do?action=viewReportById&id=" + 3044;
		String reportContent = HttpUtils.get(reportUrl, "GB2312");
		System.out.println("\n\n" + reportContent);
	}

}
