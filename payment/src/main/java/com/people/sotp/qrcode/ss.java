package com.people.sotp.qrcode;

import java.util.HashMap;
import java.util.Map;

public class ss {
	
public static void main(String[] args) {
	
	Map<String, Object> sotpMap = new HashMap<String,Object>();
	sotpMap.put("str", "123");
	
	
	
	
	sotpMap=qwe();
	System.out.println(sotpMap.toString());
	
}

public static Map<String, Object> qwe(){
	Map<String, Object> sotp = new HashMap<String,Object>();
	sotp.put("aa", "123");
	return sotp;
}
}
