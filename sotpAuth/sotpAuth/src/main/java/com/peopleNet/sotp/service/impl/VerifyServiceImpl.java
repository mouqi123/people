package com.peopleNet.sotp.service.impl;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.constant.ServiceConstant;
import com.peopleNet.sotp.controller.ControllerHelper;
import com.peopleNet.sotp.dal.model.RulePolicyDto;
import com.peopleNet.sotp.dal.model.appVersionInfoDto;
import com.peopleNet.sotp.dal.model.authFeatureDto;
import com.peopleNet.sotp.dal.model.pluginInfoDto;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.IBusinessService;
import com.peopleNet.sotp.service.ICacheService;
import com.peopleNet.sotp.service.IPolicyService;
import com.peopleNet.sotp.service.IVerifyService;
import com.peopleNet.sotp.util.Base64;
import com.peopleNet.sotp.util.SHA;
import com.peopleNet.sotp.vo.ResultVO;

/**
 * Created by wangxin on 16/4/25.
 */
@Service
public class VerifyServiceImpl implements IVerifyService {
	private static LogUtil logger = LogUtil.getLogger(VerifyServiceImpl.class);

	@Autowired
	private ICacheService cacheService;

	@Autowired
	private IPolicyService policyService;

	@Autowired
	private IBusinessService businessService;

	public ResultVO verifyUserInfo(pluginInfoDto plInfo, String phoneNum) {
		ResultVO result = new ResultVO();

		if (plInfo == null || StringUtils.isBlank(phoneNum)) {
			result.setCode(ErrorConstant.PARA_ILLEGAL);
			result.setMsg(" plInfo/authFeature/devInfo is null");
			return result;
		}

		// 判断参数信息
		if (!plInfo.getBindUserphone().equals(phoneNum)) {
			result.setCode(ErrorConstant.PARACONT_USERID_NOTMATCH_SID);
			result.setMsg("phoneNum not right");
			return result;
		}
		return result;
	}

	public ResultVO verifyPluginStatusIsReady(pluginInfoDto plInfo, RulePolicyDto verifyPolicyInfo) {
		ResultVO result = new ResultVO();

		if (plInfo == null) {
			result.setCode(ErrorConstant.PARA_ILLEGAL);
			result.setMsg(" plInfo is null");
			return result;
		}

		// 插件状态：是否为就绪
		return policyService.pluginStatus(plInfo, verifyPolicyInfo);
	}

	public ResultVO verifyAll(String service, pluginInfoDto plInfo, authFeatureDto authFeature, String envInfo,
	        String phoneNum, String appId, RulePolicyDto verifyPolicyInfo) {
		ResultVO result = new ResultVO();

		result = this.verifyDevInfoAndAppInfoAndPluginSign(service, plInfo, authFeature, envInfo, appId,
		        verifyPolicyInfo);
		if (null != result && result.getCode() != ErrorConstant.RET_OK) {
			return result;
		}

		result = this.verifyUserInfo(plInfo, phoneNum);
		if (null != result && result.getCode() != ErrorConstant.RET_OK) {
			return result;
		}

		return result;
	}

	// 校验APP完整性
	// 使用service参数来区分是否是双向认证接口
	public ResultVO verifyAppInfo(String service, pluginInfoDto plInfo, String envInfo, String appId,
	        RulePolicyDto verifyPolicyInfo) {
		ResultVO vr = new ResultVO();
		// 查看验证策略,检查是否需要验证app信息;如果不需要,在此return.
		int isIntegrityCheck = 0;
		if (ServiceConstant.BUSINESS_GENSESSKEY.equals(service)) {
			isIntegrityCheck = this.policyService.getIsTwoAuthIntegrityCheck(verifyPolicyInfo);
		} else {
			isIntegrityCheck = this.policyService.getIsIntegrityCheck(verifyPolicyInfo);
		}

		appVersionInfoDto appVersionInfo = null;
		// 验证APP信息
		if (isIntegrityCheck != 0) {
			appVersionInfo = cacheService.getAPPVersionInfoByCode(appId);
			if (null == appVersionInfo) {
				vr.setMsg("no APPInfo or can not get APPInfo.");
				vr.setCode(ErrorConstant.PARA_ILLEGAL);
				return vr;
			}

			String devJson = Base64.decode(envInfo);
			JSONObject jsonObject = ControllerHelper.parseJsonString(devJson);
			if (null == jsonObject) {
				vr.setMsg("dev format wrong");
				vr.setCode(ErrorConstant.PARA_ILLEGAL);
				return vr;
			}

			String appHashValue = appVersionInfo.getHash_value().trim();
			String appHash = jsonObject.getString("appval");
			if (StringUtils.isEmpty(appHash) || !appHash.equals(appHashValue)) {
				vr.setMsg(" appval can not pair(" + appHash + " <> " + appHashValue + ").");
				vr.setCode(ErrorConstant.PARA_ILLEGAL);
				return vr;
			}

			String appSign = jsonObject.getString("signval");
			String appSignature = appVersionInfo.getSignature();
			if (StringUtils.isEmpty(appSign) || !appSign.equals(appSignature)) {
				vr.setMsg(" signval can not pair(" + appSign + " <> " + appSignature + ").");
				vr.setCode(ErrorConstant.PARA_ILLEGAL);
				return vr;
			}
		}
		return vr;
	}

	// 校验硬件信息
	public ResultVO verifyDevInfoAndAppInfoAndPluginSign(String service, pluginInfoDto plInfo,
	        authFeatureDto authFeature, String envInfo, String appId, RulePolicyDto verifyPolicyInfo) {
		ResultVO vr = new ResultVO();

		if (plInfo == null || authFeature == null || StringUtils.isBlank(envInfo)) {
			vr.setCode(ErrorConstant.PARA_ILLEGAL);
			vr.setMsg(" plInfo/authFeature/devInfo is null");
			return vr;
		}

		logger.debug("envInfo: [" + envInfo + "]");
		String devJson = Base64.decode(envInfo);
		logger.debug("json: [" + devJson + "]");
		JSONObject jsonObject = ControllerHelper.parseJsonString(devJson);
		if (null == jsonObject) {
			vr.setCode(ErrorConstant.PARA_ILLEGAL);
			vr.setMsg("dev format wrong");
			return vr;
		}

		// 1. 验证硬件信息
		// 插件类型:UNKOWN = 0; TEST = 1; IOS = 2; ANDROID_ARM = 3; ANDROID_X86 = 4;
		// ANDROID_MIPS = 5; WINDOWS_PHONE = 6; PC = 7;
		int plugintype = authFeature.getdevType();
		String pluginDevInfo = plugintype == 2 ? authFeature.getuuid() : authFeature.getimei();
		if (StringUtils.isBlank(pluginDevInfo)) {
			vr.setCode(ErrorConstant.PARA_ILLEGAL);
			vr.setMsg(" pluginDevInfo is null");
			return vr;
		}
		String shaOrgDev = SHA.SHA_people(pluginDevInfo);

		String dev = jsonObject.getString("dev");
		if (!shaOrgDev.equals(dev)) {
			vr.setCode(ErrorConstant.PARA_ILLEGAL);
			vr.setMsg(" dev can not pair");
			return vr;
		}

		// 2.验证插件信息
		String hashValue = plInfo.getHashValue();
		String pluginval = jsonObject.getString("pluginval");
		if (StringUtils.isEmpty(hashValue) || !hashValue.equals(pluginval)) {
			vr.setCode(ErrorConstant.PARA_ILLEGAL);
			vr.setMsg("plugin can not pair(" + pluginval + " <> " + hashValue + ").");
			return vr;
		}
		vr = verifyAppInfo(service, plInfo, envInfo, appId, verifyPolicyInfo);
		return vr;
	}

	@Override
	public ResultVO verifyChallengeAns(pluginInfoDto plInfo, String challengeAns) {
		ResultVO result = new ResultVO();
		String errMsg = "";
		String challengeAnsStr = businessService.decrypt(plInfo, challengeAns);
		if (null == challengeAnsStr) {
			errMsg = "decrypt challenge code failed! code = " + challengeAns;
			logger.error(errMsg);
			result.setCode(ErrorConstant.THOR_ENCRYPT_ERROR);
			result.setMsg(errMsg);
			return result;
		}
		String challenge = businessService.getChallengeCode(plInfo);
		if (null == challenge) {
			errMsg = "read challenge code failed!";
			logger.error(errMsg);
			result.setCode(ErrorConstant.REDIS_OP_ERROR);
			result.setMsg(errMsg);
			return result;
		}
		if (!challengeAnsStr.equals(challenge)) {
			errMsg = "challengeAns failed!";
			logger.error(errMsg);
			result.setCode(ErrorConstant.PLUGIN_CHALLENGEANS_FAIL);
			result.setMsg(errMsg);
			return result;
		}
		return result;
	}

}
