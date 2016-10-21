package com.people.sotp.interfaces.controller;

import java.io.PrintWriter;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.people.sotp.dataobject.AutoAppDO;
import com.people.sotp.dataobject.IssuerInfo;
import com.people.sotp.interfaces.service.UserManageService;
import com.people.sotp.interfaces.service.IssuerBusinessService;
import com.people.sotp.interfaces.service.SotpService;

@Controller
@RequestMapping("/face")
public class AutoAppController {

	@Resource
	private UserManageService userManageService;
	@Resource
	private SotpService sotpService;
	@Autowired
	private IssuerBusinessService issuerService;

	private static Log log = LogFactory.getLog(AutoAppController.class);

	@RequestMapping(value = "/controllers.do", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void entrance(@RequestBody AutoAppDO auto, HttpServletRequest request, HttpServletResponse response,
			PrintWriter printWriter) {
		log.info("remote address :" + request.getRemoteAddr());
		String service = auto.getService();
		if (service == null || "".equals(service) || "null".equals(service)) {
			String data = "service is  null";
			utilJson(data, printWriter);
			return;
		}
		switch (service) {
		case "setUserInfo":
			utilJson(userManageService.setUserInfo(auto), printWriter);
			break;

		case "getUserInfo":
			utilJson(userManageService.getUserInfo(auto), printWriter);
			break;

		case "setAuthentication":
			utilJson(userManageService.setAuthentication(auto), printWriter);
			break;

		case "getAuthentication":
			utilJson(userManageService.getAuthentication(auto), printWriter);
			break;

		case "loginLog":
			utilJson(userManageService.loginLog(auto), printWriter);
			break;

		case "userFeedback": // 用户反馈
			utilJson(userManageService.userFeedback(auto), printWriter);
			break;

		case "oldPassword": // 修改密码
			utilJson(userManageService.oldPassword(auto), printWriter);
			break;
		case "newPassword":
			utilJson(userManageService.newPassword(auto), printWriter);
			break;

		case "logout":
			utilJson(userManageService.logout(auto), printWriter);
			break;
		case "changePhoneNum":
			utilJson(userManageService.changePhoneNum(auto), printWriter);
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
		case "checkApp":
			utilJson(sotpService.pluginApply(auto), printWriter);
			break;
		case "checkDev":
			utilJson(sotpService.pluginApply(auto), printWriter);
			break;

		default:
			String data = "service 不存在";
			utilJson(data, printWriter);
			break;

		}

	}

	@RequestMapping(value = "/uploadData.do", method = RequestMethod.POST)
	@ResponseBody
	public void uploadData(@RequestParam String service, HttpServletRequest request, HttpServletResponse response,
			PrintWriter printWriter) {
		log.info("上传头像-----------------" + service);
		if (service == null || "".equals(service) || "null".equals(service)) {
			String data = "service is  null";
			utilJson(data, printWriter);
			return;
		}
		switch (service) {
		case "uploadPortrait": // 上传头像
			utilJson(userManageService.upPportrait(service, request), printWriter);
			break;

		default:
			String data = "service 不存在";
			utilJson(data, printWriter);
			break;
		}
	}

	@RequestMapping(value = "/issuer.do", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void issuer(@RequestBody IssuerInfo issuerInfo, HttpServletRequest request, HttpServletResponse response,
			PrintWriter printWriter) {
		log.info("remote address :" + request.getRemoteAddr());
		String service = issuerInfo.getService();
		if (service == null || "".equals(service) || "null".equals(service)) {
			String data = "service is  null";
			utilJson(data, printWriter);
			return;
		}
		switch (service) {
		case "addIssuerAccount":
			utilJson(issuerService.addIssuerAccount(issuerInfo, request), printWriter);
			break;
		case "getIssuerAccounts":
			utilJson(issuerService.getIssuerAccounts(issuerInfo, request), printWriter);
			break;
		case "unbindAccount":
			utilJson(issuerService.unbindAccount(issuerInfo, request), printWriter);
			break;
		default:
			String data = "service 不存在";
			utilJson(data, printWriter);
			break;
		}
	}

	@RequestMapping(value = "/common.do", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void common(@RequestBody HashMap<String, String> map, HttpServletRequest request,
			HttpServletResponse response, PrintWriter printWriter) {
		log.info(map);
		String service = map.get("service");
		if (service == null || "".equals(service) || "null".equals(service)) {
			String data = "service is  null";
			utilJson(data, printWriter);
			return;
		}
		switch (service) {
		case "saveGesture":
			utilJson(userManageService.saveGesture(map), printWriter);
			break;
		case "getGesture":
			utilJson(userManageService.getGesture(map), printWriter);
			break;
		default:
			String data = "service 不存在";
			utilJson(data, printWriter);
			break;
		}
		return;
	}

	@RequestMapping(value = "/userauth.do", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void userauth(@RequestBody AutoAppDO auto, HttpServletRequest request, HttpServletResponse response,
			PrintWriter printWriter) {
		log.info("remote address :" + request.getRemoteAddr());
		String service = auto.getService();
		if (service == null || "".equals(service) || "null".equals(service)) {
			String data = "service is  null";
			utilJson(data, printWriter);
			return;
		}
		switch (service) {
		case "registerNote":
			utilJson(userManageService.registerNote(auto, response), printWriter);
			break;
		case "registerUser":
			utilJson(userManageService.registerUser(auto, response), printWriter);
			break;
		case "login":
			utilJson(userManageService.login(auto, response), printWriter);
			break;
		case "findPwdCode":
			utilJson(userManageService.findPwdCode(auto, response), printWriter);
			break;
		case "findPassword":
			utilJson(userManageService.findPassword(auto, response), printWriter);
			break;
		case "quickNote":
			utilJson(userManageService.quickNote(auto, response), printWriter);
			break;
		case "quickLogin":
			utilJson(userManageService.quickLogin(auto, response), printWriter);
			break;
		default:
			String data = "service 不存在";
			utilJson(data, printWriter);
			break;
		}
	}


	/**
	 * 给终端返回
	 * 
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
