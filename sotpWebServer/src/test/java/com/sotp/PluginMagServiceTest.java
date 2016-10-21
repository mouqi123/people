package com.sotp;

import com.peopleNet.sotp.constant.ServiceConstant;
import com.sotp.service.IParamProduceService;

public class PluginMagServiceTest {

	public IParamProduceService paramProduceService;
	// 认证服务器 地址
	private String SOTPAUTH_IP = "192.168.1.199";
	private String SOTPAUTH_PORT = "8080";
	private String appId = "";
	private String appKey = "";
	private String major = "3";
	private String minor = "0";
	private String phoneNum = "13720099635";
	private String userId = phoneNum;

	public String downPlugin() {
		String res = null;

		// 下载插件所需参数
		String header = "";
		String userInfo = "";
		String devInfo = "";
		String appInfo = "";
		String businessName = "";
		String attachedInfo = "";
		String pluginVersion = "";
		String nonce_str = "";
		String sign = "";
		String op = ServiceConstant.PLUGIN_REG;

		// 生成所需参数的生成方法
		header = paramProduceService.genHeader(major, minor, op, appId);
		userInfo = paramProduceService.genUserInfo(phoneNum, userId);
		devInfo = paramProduceService.genDevInfo();
		appInfo = paramProduceService.genAppInfo();
		attachedInfo = paramProduceService.genAttachedInfo();

		// TODO 调用下载插件方法

		return res;
	}
}
