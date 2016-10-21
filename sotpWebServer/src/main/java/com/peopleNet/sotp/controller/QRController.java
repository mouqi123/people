package com.peopleNet.sotp.controller;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.json.JSONObject;

import com.peopleNet.sotp.context.Constant;
import com.peopleNet.sotp.model.AutoAppDO;
import com.peopleNet.sotp.service.ICacheService;
import com.peopleNet.sotp.util.GlobalParam;
import com.peopleNet.sotp.util.sendRequest;
import com.peopleNet.sotp.util.sotpAuthUtil;


@Controller
@RequestMapping("/qrController")
public class QRController {
	
	@Autowired
	private ICacheService cacheService;
	
	@ResponseBody
	@RequestMapping(value = "obtainQrIdAndCode", method = RequestMethod.GET)
	public String obtainQrIdAndCode() {
		
		int randNum = 0;
		String chalcode = "";
		Random rand = new Random();
		for (int i = 0; i < 6; i++) {
			randNum = rand.nextInt(10);
			chalcode += randNum;
		}
		
		 UUID uuid = UUID.randomUUID();
		 int challengeCodeTimeout = 60;
		 try {
			cacheService.set(uuid.toString(), chalcode, challengeCodeTimeout);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 JSONObject jsonObj = new JSONObject();
		 jsonObj.put("qrId", getUUID());
		 jsonObj.put("chalcode", chalcode);
		 
		 return jsonObj.toString();
		
	} 

	@ResponseBody
	@RequestMapping(value = "authOtp", method = RequestMethod.GET)
	public String authOtp(@RequestBody AutoAppDO auto, HttpServletRequest request, HttpServletResponse response,
			PrintWriter printWriter){
		

		
		StringBuffer jsondata = new StringBuffer();
		StringBuffer sotpGet = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		int status=-1;
		
		
		String sotpInfo = auto.getSotpInfo();
		String qrId = auto.getQrId();
		sotpInfo=sotpInfo.replaceAll("(\r\n|\r|\n|\n\r)", "");
		Map<String, Object> sotpMap = new HashMap<String, Object>();
		sotpMap = sotpAuthUtil.getUrlParams(sotpInfo);
		
		if(auto.getBusinessName()!=null){
			sotpMap.put("businessName", auto.getBusinessName());
		}
		
		
		String appKey ="";
		if (appKey == null) {
			jsondata.append("{}");
			return utilJson(auto.getService(),GlobalParam.AppIdError, jsondata.toString(),"appId not match");
		}
		
		sotpGet.append("http://192.168.180.62:8080");
		
		String sotpAuth=sotpAuthUtil.genRequestSotpAuthMsg(sotpMap, sotpInfo, appKey);
		
		String info = sendRequest.sendPost(sotpGet.toString(),sotpAuth );
		
		JSONObject jsonsotp = net.sf.json.JSONObject.fromObject(info);
		
		appKey = "";
		if (appKey == null) {
			jsondata.append("{}");
			return utilJson(auto.getService(), GlobalParam.AppIdError, jsondata.toString(),"appId not match");
		}
//		boolean falg = sotpAuthUtil.verifySotp(jsonsotp,jsonsotp.getString("appId"),appKey);
//		if (!falg) {
//			jsondata.append("{}");
//			return utilJson(auto.getService(),GlobalParam.SignMatch, jsondata.toString(), "sign not match");
//		}
		
		JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
		status=jsonsotp.getInt("status");
	    cacheService.set(Constant.QRAUTHRSULT+qrId, status, 120);
		 
		
		return utilJson(auto.getService(),status, jsondata.toString(), errorMsg.toString());
	
	}
	
	
	@ResponseBody
	@RequestMapping(value = "obtainResult", method = RequestMethod.GET)
	public String obtainResult(String qrId) {
		String status="3";
		status = (String) cacheService.get(Constant.QRAUTHRSULT+qrId);
		return status;
	}
	
	
	
	
	
	/**
	 * 返回json
	 * 
	 * @param type
	 *            业务类型
	 * @param status
	 *            结果
	 * @param data
	 *            返回信息
	 * @param errorMsg
	 *            错误信息
	 * @param printWriter
	 */
	public String utilJson(String service,int status, String data, String errorMsg) {
		StringBuffer jsondata = new StringBuffer();
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String serverTime = dateFormat.format(now);
		jsondata.append("{\"service\": \""+service+"\" ");
		jsondata.append(",\"status\":" + status + ",");
		jsondata.append("\"data\":" + data + "");
		jsondata.append(",\"errorMsg\":\"" + errorMsg + "\",");
		jsondata.append("\"serverTime\":\"" + serverTime + "\"}");
		return jsondata.toString();
	}
	  
	  public static String getUUID() {  
	        UUID uuid = UUID.randomUUID();  
	        String str = uuid.toString();  
	        // 去掉"-"符号  
	        String temp = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);  
	        return temp;  
	    } 
	
}
