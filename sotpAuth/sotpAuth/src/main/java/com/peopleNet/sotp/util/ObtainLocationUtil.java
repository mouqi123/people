package com.peopleNet.sotp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class ObtainLocationUtil {

	public static String getCity(String latitude, String longidue) throws IOException {

		String city = "nil";
		String callback = "randerReverse";
		String output = "xml";
		String pois = "0";
		String ak = "HAZDCY8hIDoY56NZ3MaUdrXbtx8Fw2SM";

		String lat = java.net.URLEncoder.encode(latitude, "UTF-8");
		String lng = java.net.URLEncoder.encode(longidue, "UTF-8");

		String url = String.format(
		        "http://api.map.baidu.com/geocoder/v2/?ak=%s&callback=%s&location=%s,%s&output=%s&pois=%s", ak,
		        callback, lat, lng, output, pois);

		URL myURL = null;
		URLConnection httpsConn = null;
		InputStreamReader insr = null;
		BufferedReader br = null;

		try {
			myURL = new URL(url);
			httpsConn = (URLConnection) myURL.openConnection();
			if (httpsConn != null) {
				insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
				br = new BufferedReader(insr);
				String data = null;
				while ((data = br.readLine()) != null) {
					if (data.indexOf("<city>") >= 0) {
						city = data.substring(data.indexOf("<city>") + 6, data.indexOf("</city>"));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (insr != null) {
				insr.close();
			}
			if (br != null) {
				br.close();
			}
		}
		return city;
	}

//	 public static void main(String[] args) throws IOException {
//	    String city ="";
//	    city= getCity("39.916","116.3833");//Beijing
//	    System.out.println(city);
//	    city = getCity("39.977908", "116.249924");//Shanghai
//	    System.out.println(city);
//	    
//	    String location2 = "Latitude:39.977908,Longtitude:116.249924";
//	    String[] aa =location2.replaceAll(":", ",").split(",");
//	    System.out.println(aa[1]+aa[3]);
//	 }

}
