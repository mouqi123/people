package com.people.sotp.demoApp.controller;

import java.io.BufferedReader;


import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.people.sotp.commons.base.GlobalParam;
import com.people.sotp.demoApp.service.DemoAppService;
import com.people.sotp.demoApp.service.SotpService;

import net.sf.json.JSONObject;


@Controller
@RequestMapping("/face")
public class DemoAppController {

	@Resource
	private DemoAppService demoAppService;
	@Resource
	private SotpService sotpService;

	@RequestMapping(value = "/controllers.do", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void entrance(HttpServletRequest request, HttpServletResponse response, PrintWriter printWriter) {
		
		String service = null;
		try {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		String appMsg = sb.toString();
		String data = null;
		JSONObject json = JSONObject.fromObject(appMsg);
		service = json.getString("service");
		
		switch (service){
			 case "registerUser":
				 data =demoAppService.registerUser(json);
				 utilJson(data, printWriter);
				 break;
			 case "login":
				 data =demoAppService.login(json);
				 utilJson(data, printWriter);
				 break;
			 case "transfer":	//转账 
				 data =demoAppService.transfer(json);
				 utilJson(data, printWriter);
				 break;
			 case "deal":    //交易
				 data =demoAppService.deal(json);
				 utilJson(data, printWriter);
				 break;
			 case "accountInfo":	 
				 data =demoAppService.account(json);
				 utilJson(data, printWriter);
				 break;
			 case "loginLog":	 
				 data =demoAppService.loginLog(json);
				 utilJson(data, printWriter);
				 break;	 
			 case "Tlog":	 
				 data =demoAppService.transferLog(json);
				 utilJson(data, printWriter);
				 break;	
			 case "sotpLogin":	 
				 data =sotpService.pluginApply(json);
				 utilJson(data, printWriter);
				 break;		 
			 case "downPlugin":	 
				 data =sotpService.pluginApply(json);
				 utilJson(data, printWriter);
				 break;	
				 
			 case "pluginUpdateNew":	 
				 data =sotpService.pluginApply(json);
				 utilJson(data, printWriter);
				 break;			 
			
			 case "activatesPlugin":	 
				 data =sotpService.pluginApply(json);
				 utilJson(data, printWriter);
				 break;		
			 case "alreadyActivatesPlugin":	 
				 data =sotpService.pluginApply(json);
				 utilJson(data, printWriter);
				 break;		
				 
				 
			 case "reqChallengcode":	 
				 data =sotpService.pluginApply(json);
				 utilJson(data, printWriter);
				 break;			 
				 
			 case "timingCorrector":	 
				 data =sotpService.pluginApply(json);
				 utilJson(data, printWriter);
				 break;			 
				
			 case "getDevlist":	 
				 data =sotpService.pluginApply(json);
				 utilJson(data, printWriter);
				 break;		 
			
			 case "delBinddev":	 
				 data =sotpService.pluginApply(json);
				 utilJson(data, printWriter);
				 break;		 
				 
				 
			 default:
					StringBuffer error = new StringBuffer();
					Date now = new Date();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String serverTime = dateFormat.format(now);
					error.append("{\"service\":" + service + ",\"status\":"+GlobalParam.ParameterError+",");
					error.append("\"data\":\"{}\"");
					error.append(",\"errorMsg\":\"service不存在\"},");
					error.append("\"serverTime\":\"" + serverTime + "\"}");
					utilJson(error.toString(), printWriter);
					break;
			}
		
			
		
		
		
		
		
		
		
		
		
		
		
		
		
		} catch (Exception e) {
			StringBuffer error = new StringBuffer();
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String serverTime = dateFormat.format(now);
			error.append("{\"service\":\"" + service + "\",\"status\":"+GlobalParam.ParameterError+",");
			error.append("\"data\":\"\"");
			error.append(",\"errorMsg\":\"parameter error\",");
			error.append("\"serverTime\":\"" + serverTime + "\"}");
			utilJson(error.toString(), printWriter);
		}
					
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
	public void utilJson(String data, PrintWriter printWriter) {
		System.out.println(data);
		JSONObject json = JSONObject.fromObject(data);
		printWriter.write(json.toString());
		printWriter.flush();
		printWriter.close();
	}

}
