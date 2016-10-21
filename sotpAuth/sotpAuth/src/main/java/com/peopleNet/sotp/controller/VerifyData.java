package com.peopleNet.sotp.controller;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.peopleNet.sotp.dal.model.appVersionInfoDto;
import com.peopleNet.sotp.dal.model.authFeatureDto;
import com.peopleNet.sotp.dal.model.pluginInfoDto;
import com.peopleNet.sotp.service.ICacheService;
import com.peopleNet.sotp.util.Base64;
import com.peopleNet.sotp.util.SHA;

public class VerifyData {
	private ICacheService cacheService;

	public VerifyData(ICacheService cacheService) {
		this.cacheService = cacheService;
	}

	// 校验硬件信息
	public VerifyRet verifyDevInfo(pluginInfoDto plInfo, authFeatureDto authFeature, String devInfo) {
		VerifyRet vr = new VerifyRet();

		if (plInfo == null || authFeature == null || StringUtils.isBlank(devInfo)) {
			vr.setOk(false);
			vr.setMsg(" plInfo/authFeature/devInfo is null");
			return vr;
		}

		// 插件类型
		// UNKOWN = 0; TEST = 1; IOS = 2; ANDROID_ARM = 3; ANDROID_X86 = 4;
		// ANDROID_MIPS = 5; WINDOWS_PHONE = 6; PC = 7;
		int plugintype = authFeature.getdevType();
		String pluginDevInfo = plugintype == 2 ? authFeature.getuuid() : authFeature.getimei();
		if (StringUtils.isBlank(pluginDevInfo)) {
			vr.setOk(false);
			vr.setMsg(" pluginDevInfo is null");
			return vr;
		}

		String shaOrgDev = SHA.SHA_people(pluginDevInfo);

		System.out.println("devInfo: [" + devInfo + "]");
		String json = Base64.decode(devInfo);
		System.out.println("json: [" + json + "]");

		JSONObject jsonObject = ControllerHelper.parseJsonString(json);

		if (null == jsonObject) {
			vr.setOk(false);
			vr.setMsg("dev format wrong");
			return vr;
		}

		String dev = jsonObject.getString("dev");
		if (!shaOrgDev.equals(dev)) {
			vr.setOk(false);
			vr.setMsg(" dev can not pair");
			return vr;
		}

		// 验证插件硬件信息是否匹配 增加app hash值、签名比对
		String hashValue = plInfo.getHashValue();
		String pluginval = jsonObject.getString("pluginval");
		if (!hashValue.equals(pluginval)) {
			vr.setOk(false);
			vr.setMsg(" pluginval can not pair(" + pluginval + " <> " + hashValue + ").");
			return vr;
		}

		appVersionInfoDto appVersionInfo = cacheService.getAPPVersionInfoByCode(plInfo.getAppCode());
		if (null == appVersionInfo) {
			vr.setOk(false);
			vr.setMsg(" no APPInfo or can not get APPInfo.");
			return vr;
		}

		String appHashValue = appVersionInfo.getHash_value();
		String appval = jsonObject.getString("appval");
		if (!appval.equals(appHashValue)) {
			vr.setOk(false);
			vr.setMsg(" appval can not pair(" + appval + " <> " + appHashValue + ").");
			return vr;
		}

		String appSignature = appVersionInfo.getSignature();
		String signval = jsonObject.getString("signval");
		if (!signval.equals(appSignature)) {
			vr.setOk(false);
			vr.setMsg(" signval can not pair(" + signval + " <> " + appSignature + ").");
			return vr;
		}

		vr.setOk(true);
		return vr;
	};

	public class VerifyRet {
		private boolean ok;

		private String msg;

		public boolean isOk() {
			return ok;
		}

		public void setOk(boolean ok) {
			this.ok = ok;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
}
