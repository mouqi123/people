package com.people.sotp.interfaces.controller;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.people.sotp.commons.base.GlobalParam;
import com.people.sotp.dataobject.AutoAppDO;
import com.people.sotp.interfaces.service.BusinessService;
import com.people.sotp.interfaces.service.SotpService;


@Controller
@RequestMapping("/face")
public class AutoAppController {

	@Resource
	private BusinessService businessService;
	@Resource
	private SotpService sotpService;
	
	
	
	@RequestMapping(value = "/controllers.do", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void entrance(@ModelAttribute AutoAppDO auto, HttpServletRequest request, HttpServletResponse response,
			PrintWriter printWriter) {
		System.out.println(request.getRemoteAddr());
		String service = auto.getService();
		if (service == null || "".equals(service)||"null".equals(service)) {
			String data ="service is  null";
			utilJson(data, printWriter);
			return;
		}
		switch (service) {
			case "registerNote":
				utilJson(businessService.registerNote(auto), printWriter);
				break;
			case "registerUser":
				utilJson(businessService.registerUser(auto), printWriter);
				break;
				
			case "login":	
				utilJson(businessService.login(auto), printWriter);
				break;
				
			case "setUserInfo":	
				utilJson(businessService.setUserInfo(auto), printWriter);
				break;
				
			case "getUserInfo":
				utilJson(businessService.getUserInfo(auto), printWriter);
				break;
				
			case "setAuthentication":	
				utilJson(businessService.setAuthentication(auto), printWriter);
				break;
				
			case "getAuthentication":	
				utilJson(businessService.getAuthentication(auto), printWriter);
				break;
				
			case "loginLog"	:
				utilJson(businessService.loginLog(auto), printWriter);
				break;
				
			case "userFeedback":
				utilJson(businessService.userFeedback(auto), printWriter);
				break;
			case "uploadPortrait":   //上传头像

				utilJson(businessService.upPportrait(auto,request), printWriter);
				break;
				
				 
			case "oldPassword":
					utilJson(businessService.oldPassword(auto), printWriter); 
				 break;
			case "newPassword":
					utilJson(businessService.newPassword(auto), printWriter); 
				 break;
				 
			
				 
			 case "sotpLogin":	
				 utilJson(sotpService.pluginApply(auto), printWriter);
				 break;	
			
			 case "downPlugin":	 
				 utilJson(sotpService.pluginApply(auto), printWriter);
				 break;	
				 
			 case "pluginUpdateNew":	 
				 utilJson(sotpService.pluginApply(auto), printWriter);
				 break;			 
			
			 case "activatesPlugin":	 
				 utilJson(sotpService.pluginApply(auto), printWriter);
				 break;		
			 case "alreadyActivatesPlugin":	 
				 utilJson(sotpService.pluginApply(auto), printWriter);
				 break;		
				 
				 
			 case "reqChallengcode":	 
				 utilJson(sotpService.pluginApply(auto), printWriter);
				 break;			 
				 
			 case "timingCorrector":	 
				 utilJson(sotpService.pluginApply(auto), printWriter);
				 break;			 
				
			 case "getDevlist":	 
				 utilJson(sotpService.pluginApply(auto), printWriter);
				 break;		 
			
			 case "delBinddev":	 
				 utilJson(sotpService.pluginApply(auto), printWriter);
				 break;		 
				 
			 default:
					String data ="service 不存在";
					utilJson(data, printWriter);
					break;	 
				 
		}

	}

	
	
	/**
	 * 给终端返回
	 * @param data
	 * @param printWriter
	 */
	public void utilJson(String data, PrintWriter printWriter) {
		printWriter.write(data);
		System.out.println(data);
		printWriter.flush();
		printWriter.close();
	}
}
