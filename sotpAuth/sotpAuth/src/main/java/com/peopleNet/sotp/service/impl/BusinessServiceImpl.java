package com.peopleNet.sotp.service.impl;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.zip.CRC32;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.constant.ServiceConstant;
import com.peopleNet.sotp.context.UserThreadContext;
import com.peopleNet.sotp.controller.ControllerHelper;
import com.peopleNet.sotp.dal.model.*;
import com.peopleNet.sotp.exception.BusinessException;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.*;
import com.peopleNet.sotp.thrift.service.SotpPlugin;
import com.peopleNet.sotp.thrift.service.SotpRet;
import com.peopleNet.sotp.util.*;
import com.peopleNet.sotp.vo.ResultVO;
import com.peopleNet.sotp.vo.VerifyEntity;

@Service
public class BusinessServiceImpl implements IBusinessService {
	private static LogUtil logger = LogUtil.getLogger(BusinessServiceImpl.class);

	@Autowired
	private IInvokeInfoService invokeInfoService;
	@Autowired
	private IThriftInvokeService thriftInvokeService;
	@Autowired
	private ICacheService cacheService;
	@Autowired
	private IParaHandle paraHandle;
	@Autowired
	private IPluginService pluginService;
	@Autowired
	private IPolicyService policyService;
	@Autowired
	private IAnalysisService analysisService;
	@Autowired
	private IEncryptPluginInfoService pluginInfoMapper;

	private String useOnlineLog = CommonConfig.get("USE_ONLINELOG");

	public void setBusinessLog(String requestMsg, String responseMsg, int retcode, String errMsg) {

		// 参数格式错误报文， 不记录业务日志
		if (retcode < 200 && retcode > 100)
			return;

		invokeInfoDto invokeInfo = new invokeInfoDto();
		invokeInfo.setValueByUserContext(UserThreadContext.getContext());

		if (retcode == ErrorConstant.RET_OK)
			invokeInfo.setStatus(0);
		else
			invokeInfo.setStatus(1);

		invokeInfo.setErrorcode(Integer.toString(retcode));

		// 将返回状态置入线程变量中,供打印日志使用
		UserThreadContext.getContext().setStatus(retcode);

		invokeInfo.setRemark(errMsg);

		// 风险级别 TODO
		if (retcode == ErrorConstant.RET_OK) { // 正常交易
			invokeInfo.setRisklevel(1);
		} else if (retcode == ErrorConstant.PLUGIN_USELESS) { // 风险交易
			invokeInfo.setRisklevel(3);
		} else { // 异常交易
			invokeInfo.setRisklevel(2);
		}

		if (requestMsg != null) {
			int len = requestMsg.length();
			if (len > 256)
				invokeInfo.setRequestMsg(requestMsg.substring(0, 255));
			else
				invokeInfo.setRequestMsg(requestMsg);
			// invokeInfo.setRequestMsg(requestMsg);
		}

		if (responseMsg != null) {
			int len = responseMsg.length();
			if (len > 256)
				invokeInfo.setResponseMsg(responseMsg.substring(0, 255));
			else
				invokeInfo.setResponseMsg(responseMsg);
			// invokeInfo.setResponseMsg(responseMsg);
		}

		// 打印log信息
		if (retcode == ErrorConstant.RET_OK) {
			logger.info(invokeInfo.toString());
		} else {
			logger.error(invokeInfo.toString());
		}

		if (!StringUtils.isEmpty(useOnlineLog) && "true".equals(useOnlineLog)) {
			logger.debug("use online log!-----");
			this.invokeInfoService.insertBatch(invokeInfo);
			// invokeInfoMapper.insert(invokeInfo);
		}
	}

	public String decrypt(pluginInfoDto plugin, String source) {
		String dst = null;

		SotpRet sotpRes = null;
		try {
			sotpRes = thriftInvokeService.sotpDecryptV2(plugin.getPluginId(), plugin.getPluginKey(), source);
		} catch (TException e) {
			logger.error("invoke thrift decrypt error.msg:%s", e.toString());
		}
		if (sotpRes == null) {
			logger.error("decrypt sotpthorResponse is null.");
			return null;
		}
		if (sotpRes.status < 0) {
			logger.error("decrypt sotpthorResponse status<0. status:%d", sotpRes.getStatus());
			return null;
		}
		dst = sotpRes.getTitle();
		return dst;
	}

	public String encrypt(pluginInfoDto plugin, String source) {
		String dst = null;

		SotpRet sotpRes = null;
		try {
			sotpRes = thriftInvokeService.sotpEncryptV2(plugin.getPluginId(), plugin.getPluginKey(), source);
		} catch (TException e) {
			logger.error("invoke thrift encrypt error.msg:%s", e.toString());
		}
		if (sotpRes == null) {
			logger.error("encrypt sotpthorResponse is null.");
			return null;
		}

		if (sotpRes.status < 0) {
			logger.error("encrypt sotpthorResponse status<0. status:%d", sotpRes.getStatus());
			return null;
		}
		dst = sotpRes.getTitle();
		return dst;
	}

	public int verify(pluginInfoDto plugin, VerifyEntity verifyEntity) {
		if (null == plugin || null == verifyEntity) {
			logger.error("verify business: plugin or verifyEntiry is null");
			return ErrorConstant.THOR_PARA_ILLEGAL;
		}
		int ret = 0;

		try {
			ret = thriftInvokeService.sotpVerifyV2(verifyEntity.getOtpType(), plugin.getPluginId(),
			        plugin.getPluginKey(), verifyEntity.getNseconds(), verifyEntity.getWindowTime(),
			        verifyEntity.getPin(), verifyEntity.getChallenge(), verifyEntity.getDst());
		} catch (TException e) {
			logger.error("invoke thrift verify exception,msg:%s", e.toString());
			return ErrorConstant.THOR_CONNECT_TIMEOUT;
		}
		if (ret == ErrorConstant.RET_OK) {
			return ErrorConstant.RET_OK;
		}

		logger.error("THOR return error:" + ret);
		switch (ret) {
		case ErrorConstant.THOR_RES_PARA_ERR:
			ret = ErrorConstant.THOR_PARAFORMAT_ERR;
			break;
		case ErrorConstant.THOR_RES_SYSTEM_ERR:
			ret = ErrorConstant.THOR_SYSTEM_ERR;
			break;
		case ErrorConstant.THOR_RES_BASE64_ERR:
			ret = ErrorConstant.THOR_BASE64_ERR;
			break;
		case ErrorConstant.THOR_RES_MALLOC_ERR:
			ret = ErrorConstant.THOR_MALLOC_ERR;
			break;
		default:
			ret = ErrorConstant.THOR_RESP_UNKOWNERR;
		}

		return ret;
	}

	public String getChallengeCodeAndClear(pluginInfoDto plInfo) {
		String challengeCode = (String) this.cacheService
		        .get(Constant.REDIS_CONSTANT.PLUGIN_USER_CHALLENGE_CODE + plInfo.getPluginId());

		if (StringUtils.isEmpty(challengeCode)) {
			logger.error("challenge code time out!");
			return null;
		}
		this.cacheService.delete(Constant.REDIS_CONSTANT.PLUGIN_USER_CHALLENGE_CODE + plInfo.getPluginId());
		return challengeCode;
	}

	public String getChallengeCode(pluginInfoDto plInfo) {
		String challengeCode = (String) this.cacheService
		        .get(Constant.REDIS_CONSTANT.PLUGIN_USER_CHALLENGE_CODE + plInfo.getPluginId());

		if (challengeCode == null) {
			logger.error("challenge code time out!");
			return null;
		}
		return challengeCode;
	}

	public String getSessionKey(String pluginId) {
		String sessionKey = (String) this.cacheService
		        .get(Constant.REDIS_CONSTANT.PLUGIN_USER_SESSION_KEY + pluginId);

		if (sessionKey == null) {
			logger.error("sessionKey null!");
			return null;
		}
		return sessionKey;
	}
	
	public String getUserVerifyInfoIdAndClear(pluginInfoDto plInfo) {
		String userVerifyInfoId = (String) this.cacheService
		        .get(Constant.REDIS_CONSTANT.PLUGIN_USER_VERFIY_ID + plInfo.getPluginId());

		if (userVerifyInfoId == null) {
			logger.error("redis get userVerifyInfoId error!");
			return null;
		}
		this.cacheService.delete(Constant.REDIS_CONSTANT.PLUGIN_USER_VERFIY_ID + plInfo.getPluginId());
		return userVerifyInfoId;
	}

	public int getwindowTimeFromPolicy(pluginInfoDto plugin, RulePolicyDto verifyPolicyInfo) {
		int windowt = Constant.POLICY_DEFAULTVAL.POLICY_DEFAULTVAL_WINDOWTIME;
		if (verifyPolicyInfo.getAuthWindowSize() != null && verifyPolicyInfo.getAuthWindowSize() > 0)
			windowt = verifyPolicyInfo.getAuthWindowSize();

		return windowt;
	}

	public VerifyEntity getVerifyEntity(pluginInfoDto plugin, authFeatureDto authFeaInfo, String sotpCode,
	        String sotpCodePara, RulePolicyDto verifyPolicyInfo) {
		int windowt = this.getwindowTimeFromPolicy(plugin, verifyPolicyInfo);
		if (windowt < 0) {
			logger.error("in verifySotpCode function, get windowtime failed!");
			return null;
		}
		int nsenconds = Integer.parseInt(DateUtil.timeStamp(0));

		// 解析sotpCode
		String[] codesplit = sotpCode.split("@");
		if (codesplit.length != 2) {
			logger.error("verifSotpCode format error: " + sotpCode);
			return null;
		}
		int otptype;
		String pin = "", chall = "";
		switch (Integer.parseInt(codesplit[0])) {
		case 10:
			otptype = 1;
			break;
		case 20:
		case 21:
			otptype = 2;
			pin = plugin.getProtectCode();
			break;
		case 30:
			otptype = 3;
			pin = "";
			chall = sotpCodePara;
			break;
		case 40:
			otptype = 4;
			pin = plugin.getProtectCode();
			chall = sotpCodePara;
			break;
		case 41:
			otptype = 4;
			pin = this.getChallengeCodeAndClear(plugin);
			if (pin == null) {
				logger.error("otptype equals 4. get challenge code failed!");
				return null;
			}
			chall = sotpCodePara;
			break;
		default:
			logger.error("sotpCode type error: " + sotpCode);
			return null;
		}

		String dev = paraHandle.getDevInfo(authFeaInfo);
		String challenge = dev + "|" + pin + "|" + chall;

		VerifyEntity verifyEntity = new VerifyEntity();
		verifyEntity.setWindowTime(windowt);
		verifyEntity.setPin(pin);
		verifyEntity.setOtpType(otptype);
		verifyEntity.setChallenge(challenge);
		verifyEntity.setNseconds(nsenconds);
		verifyEntity.setDst(codesplit[1]);

		logger.debug(verifyEntity.toString());
		return verifyEntity;
	}

	public int verifySotpCode(pluginInfoDto plugin, authFeatureDto authFeaInfo, String encrptedSotpCode,
	        boolean sotpCodeIsEncrypt, String sotpPara, RulePolicyDto verifyPolicyInfo) {
		String decyptSotpCode = encrptedSotpCode;

		if (sotpCodeIsEncrypt) {
			decyptSotpCode = this.decrypt(plugin, encrptedSotpCode);
			if (null == decyptSotpCode) {
				logger.error("in verifySotpCode function, decrypt sotpCode failed!");
				return ErrorConstant.THOR_VERIFY_DECRYPT_SOTPCODE_ERROR;
			}
		}

		VerifyEntity verifyEntity = this.getVerifyEntity(plugin, authFeaInfo, decyptSotpCode, sotpPara,
		        verifyPolicyInfo);
		if (null == verifyEntity) {
			logger.error("in verifySotpCode function, get verifyEntity failed!");
			return ErrorConstant.THOR_VERIFY_GENERATE_ENTITY_ERROR;
		}

		return this.verify(plugin, verifyEntity);
	}

	public int verifySotpCodeWithChallenge(pluginInfoDto plugin, authFeatureDto authFeaInfo, String encrptedSotpCode,
	        boolean sotpCodeIsEncrypt, RulePolicyDto verifyPoliceInfo) {
		String challengeCode = this.getChallengeCodeAndClear(plugin);
		if (StringUtils.isEmpty(challengeCode)) {
			logger.error("in verifySotpCode function, get challenge code failed!");
			return ErrorConstant.THOR_VERIFY_GET_CHALLENGE_FAIL;
		}

		return this.verifySotpCode(plugin, authFeaInfo, encrptedSotpCode, sotpCodeIsEncrypt, challengeCode,
		        verifyPoliceInfo);
	}

	public int verifySotpCodeWithChallengeNotClear(pluginInfoDto plugin, authFeatureDto authFeaInfo,
	        String encrptedSotpCode, boolean sotpCodeIsEncrypt, RulePolicyDto verifyPoliceInfo) {
		String challengeCode = this.getChallengeCode(plugin);
		if (null == challengeCode) {
			logger.error("in verifySotpCode function, get challenge code failed!");
			return ErrorConstant.THOR_VERIFY_GET_CHALLENGE_FAIL;
		}

		return this.verifySotpCode(plugin, authFeaInfo, encrptedSotpCode, sotpCodeIsEncrypt, challengeCode,
		        verifyPoliceInfo);
	}

	/*
	 * 访问密码机请求插件 (tcp)
	 */
	public SotpPlugin genThorPlugin(String pluginVersion, authFeatureDto authFeaInfo, pluginInfoDto pluginInfo,
	        String appId, String serviceName, RulePolicyDto applyPolicyInfo) {
		int ret = 0;
		String pluginId = "";
		String seed = "";
		String plugin = "";
		String hwInfo = "";

		int plugintype = authFeaInfo.getdevType();
		authFeaInfo.setJoinId("3");
		String phonenum = pluginInfo.getBindUserphone();
		// String dev = paraHandle.getDevInfo(authFeaInfo);
		String dev = paraHandle.getDevInfoJson(authFeaInfo, phonenum, appId, applyPolicyInfo);
		logger.debug("TcpReqThorPlugin genSotpNew:" + phonenum + "," + dev);

		SotpPlugin sotpPluginInfo = null;
		try {
			// 申请插件的接口 格式： 手机号|app hash值|app 签名值
			StringBuilder strB = new StringBuilder(phonenum);
			char sep = Constant.seperator;

			appVersionInfoDto appVersionInfo = paraHandle.getAppVersionInfoByAppId(appId);
			if (appVersionInfo != null) {
				strB.append(sep).append(appVersionInfo.getHash_value()).append(sep)
				        .append(appVersionInfo.getSignature());
			} else {
				logger.error("can not get appversioninfo from cache.");
				return null;
			}
			// logger.debug("################## 申请插件接口：" + strB.toString());

			String busi_code = paraHandle.getBusinessCodeByAppInfoId(appVersionInfo.getApp_info_id());
			;
			String appinfo_code = paraHandle.getAppInfoCodeByAppInfoId(appVersionInfo.getApp_info_id());
			String version = appVersionInfo.getVersion();
			String busi_info = "M" + busi_code + "A" + appinfo_code + "V" + version;
			logger.debug("busi_info:" + busi_info + "plugintype:" + plugintype + "appId:" + appVersionInfo.getApp_code()
			        + "strBS:" + strB.toString() + "dev:" + dev);
			String sdk_secret = appVersionInfo.getSdk_secret();
			if (pluginVersion != "" && pluginVersion != null) {
				plugintype = Integer.parseInt(pluginVersion);
			}
			int isIntegrityCheck = this.policyService.getIsIntegrityCheckForApply(applyPolicyInfo);
			logger.debug("getIsIntegrityCheckForApply:" + isIntegrityCheck);
			if (isIntegrityCheck == 0) {
				String hashStr = "";
				logger.debug("################## 申请插件接口：" + hashStr);
				sotpPluginInfo = thriftInvokeService.genSotpV2(plugintype, busi_info, sdk_secret, hashStr, dev);
			} else {
				logger.debug("################## 申请插件接口：" + strB.toString());
				sotpPluginInfo = thriftInvokeService.genSotpV2(plugintype, busi_info, sdk_secret, strB.toString(), dev);
			}
		} catch (TException e) {
			logger.error("invoke thrift genSotpNew error.msg:%s", e.toString());
			return null;
		}

		if (sotpPluginInfo == null) {
			logger.error("sotpthorResponse is null.");
			return null;
		}
		ret = sotpPluginInfo.status;
		if (ret != ErrorConstant.RET_OK) {
			logger.error("invoke thrift to gen plugin return error status:" + ret);
			return null;
		}

		pluginId = sotpPluginInfo.getVersion();
		seed = sotpPluginInfo.getSeed();
		plugin = sotpPluginInfo.getPlugin();
		hwInfo = sotpPluginInfo.getFile();

		logger.debug("TcpReqThorPlugin pluginId:" + pluginId + ", seed:" + seed + ", plugin len:" + plugin.length()
		        + ", hwInfo len:" + hwInfo.length());

		Date date = new Date();
		pluginInfo.setPluginId(pluginId);
		pluginInfo.setPluginKey(seed);
		pluginInfo.setPluginType(plugintype);
		pluginInfo.setGenTime(date);
		pluginInfo.setStartTime(date);
		pluginInfo.setAppCode(appId);

		if (ServiceConstant.PLUGIN_REG.equals(serviceName) || ServiceConstant.PLUGIN_APPLY2.equals(serviceName)) {
			// 从策略中获取插件初始状态
			int pluginInitStatus = pluginService.getPluginInitStatus(appId);
			if (pluginInitStatus != Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY
			        && pluginInitStatus != Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NO_ACTIVATE) {
				logger.info("policy plugin init status is not right, set default: ready");
				pluginInitStatus = Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY;
			}
			pluginInfo.setStatus(pluginInitStatus);
		} else if (ServiceConstant.PLUGIN_UPDATE.equals(serviceName)
		        || ServiceConstant.PLUGIN_UPDATE2.equals(serviceName)) {
			pluginInfo.setStatus(Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);
		}

	 
		// sha1
		byte[] decode = Base64.decode(plugin.toCharArray());
//		logger.debug("plugin decode[" + ToStringBuilder.reflectionToString(decode));
//		pluginInfo.setHashValue(SHA.SHA_people(decode));
 
		 /*
		// crc32
		CRC32 crc32 = new CRC32();
        crc32.update(plugin.getBytes());
        pluginInfo.setHashValue(String.valueOf(crc32.getValue()));
 */
		
        // md5
        String md5Hash = MD5.genHash(decode);
        pluginInfo.setHashValue(md5Hash);
        
		return sotpPluginInfo;
	}

	public String generateChallenge() {
		int randNum = 0;
		String chalcode = "";
		Random rand = new Random();
		for (int i = 0; i < 6; i++) {
			randNum = rand.nextInt(10);
			chalcode += randNum;
		}
		return chalcode;
	}

	public boolean saveChallengeIntoCache(String appId, pluginInfoDto plInfo, String challenge,
	        RulePolicyDto verifyPolicyInfo) {
		// 保存各用户的挑战码到redis中
		int challengeCodeTimeout = policyService.getChallengeCodeTimeOut(verifyPolicyInfo);
		if (challengeCodeTimeout <= 0) {
			// 若策略中未配置挑战码超时时间，则默认为60s
			challengeCodeTimeout = 60;
		}
		try {
			this.cacheService.set(Constant.REDIS_CONSTANT.PLUGIN_USER_CHALLENGE_CODE + plInfo.getPluginId(), challenge,
			        challengeCodeTimeout);
		} catch (BusinessException e) {
			logger.error("save challenge code into redis error!");
			return false;
		}
		return true;
	}
	
	public boolean saveSessionKeyIntoCache(String appId, pluginInfoDto plInfo, String sessionKey) {
		// 保存各用户的sessionKey到redis中
		try {
			this.cacheService.set(Constant.REDIS_CONSTANT.PLUGIN_USER_SESSION_KEY + plInfo.getPluginId(), sessionKey,
			        -1);
		} catch (BusinessException e) {
			logger.error("save sessionKey into redis error!");
			return false;
		}
		return true;
	}

	public boolean saveAuthPolicyDtoIntoCache(String appId, pluginInfoDto plInfo, String businessName,
	        authPolicyDto authPolicy) {
		// 保存各用户的认证因素对象到redis中
		try {
			this.cacheService.set(
			        Constant.REDIS_CONSTANT.PLUGIN_AUTH_POLICY + plInfo.getPluginId() + "." + businessName, authPolicy,
			        Constant.REDIS_CONSTANT.AUTH_POLICY_WINDOWTIME);
		} catch (BusinessException e) {
			logger.error("save auth policy into redis error!");
			return false;
		}
		return true;
	}

	public authPolicyDto getAuthPolicyDtoFromCache(String appId, pluginInfoDto plInfo, String businessName) {
		// 获取认证因素对象从redis中
		authPolicyDto result = null;
		String key = Constant.REDIS_CONSTANT.PLUGIN_AUTH_POLICY + plInfo.getPluginId() + "." + businessName;
		result = (authPolicyDto) this.cacheService.get(key);
		this.cacheService.delete(key);
		return result;
	}

	@Override
	public ResultVO verifyByPolicy(String op, pluginInfoDto plugin, authFeatureDto authFeaInfo, int policy,
	        String authInfo, RulePolicyDto verifyPoliceInfo) {
		ResultVO result = new ResultVO();
		result.setCode(ErrorConstant.RET_OK);
		String errMsg = "";
		JSONObject jsonObject = null;
		
		if("0".equals(authInfo)){
			return result;
		}
		// base64 解码
		String authstr = "";
		try {
			authstr = Base64.decode(authInfo);
		} catch (Exception e) {
			errMsg = "Base64.decode error authInfo:" + authInfo;
			logger.error(errMsg);
			result.setCode(ErrorConstant.PARA_AUTHFORMAT_ERR);
			result.setMsg(errMsg);
			return result;
		}

		logger.debug("encode authInfo:" + authInfo);
		logger.debug("decode authstr len:" + authstr.length() + ", str:" + authstr + ".");

		// 解析json
		try {
			jsonObject = ControllerHelper.parseJsonString(authstr);
		} catch (JSONException e) {
			errMsg = "parseJsonString error authstr:" + authstr;
			logger.error(errMsg);
			result.setCode(ErrorConstant.PARA_AUTHFORMAT_ERR);
			result.setMsg(errMsg);
			return result;
		}

		if (jsonObject == null) {
			errMsg = "parseJsonString error authstr:" + authstr;
			logger.error(errMsg);
			result.setCode(ErrorConstant.PARA_AUTHFORMAT_ERR);
			result.setMsg(errMsg);
			return result;
		}
		String policyContent = this.analysisService.getPolicyContent(policy);
		if (policyContent == null) {
			errMsg = "getPolicyContent error:" + policyContent;
			logger.error(errMsg);
			result.setCode(ErrorConstant.PARA_AUTHFORMAT_ERR);
			result.setMsg(errMsg);
			return result;
		}
		String[] content = policyContent.split("\\|");
		// 申请插件时保存PIN码
		if (op.equals(ServiceConstant.PLUGIN_REG_AUTH)) {
			if (jsonObject.has("pinCode")) {
				JSONObject pinCodeJson = jsonObject.getJSONObject("pinCode");
				String pin = formatPinCode(plugin, pinCodeJson);
				logger.debug("apply plugin pin : " + pin);
				if (pin == null || pin == "") {
					errMsg = "auth para pinCode format error";
					logger.error(errMsg);
					result.setCode(ErrorConstant.PLUGIN_AUTH_PINCODE_ERROR);
					result.setMsg(errMsg);
					return result;
				}
				// 保存PIN码
				UpdateUtil updateInfo = new UpdateUtil();
				updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plugin.getPluginId());
				updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PROTECT_CODE, pin);
				Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
				this.pluginInfoMapper.updateHashMapByPluginId(updateMap);
				plugin.setProtectCode(pin);
			} else {
				errMsg = "auth para pinCode not exist";
				logger.error(errMsg);
				result.setCode(ErrorConstant.PLUGIN_AUTH_PINCODE_ERROR);
				result.setMsg(errMsg);
				return result;
			}
		}
		// TODO
		// 根据策略进行各个类型认证码的认证
		String sotpCodePara = "";
		for (int i = 0; i < content.length; i++) {
			String value = content[i];

			// 挑战型
			if ("challengeAuthCode".equals(value)) {
				if (jsonObject.has(value)) {
					String encrptedSotpCode = ControllerHelper.getJsonElement(jsonObject, value);
					int ret = this.verifySotpCodeWithChallengeNotClear(plugin, authFeaInfo, encrptedSotpCode, true,
					        verifyPoliceInfo);
					logger.debug("verify challengeAuthCode ret:" + ret);
					if (ret != ErrorConstant.RET_OK) {
						errMsg = "challengeAuthCode verify failed";
						logger.error(errMsg);
						result.setCode(ret);
						result.setMsg(errMsg);
						return result;
					}
				} else {
					errMsg = "auth para challengeAuthCode not exist";
					logger.error(errMsg);
					result.setCode(ErrorConstant.PLUGIN_AUTH_CHALLENGEAUTHCODE_ERROR);
					result.setMsg(errMsg);
					return result;
				}
			}
			// pinCode比对
			if ("pinCode".equals(value) && op.equals(ServiceConstant.BUSINESS_AUTH_RESPONSE)) {
				if (jsonObject.has(value)) {
					JSONObject pinCodeJson = jsonObject.getJSONObject("pinCode");
					String pinCode = formatPinCode(plugin, pinCodeJson);
					logger.debug("pinCode Client: " + pinCode + "pinCode Server: " + plugin.getProtectCode());
					if (pinCode == null) {
						errMsg = "pinCode format error";
						logger.error(errMsg);
						result.setCode(ErrorConstant.PLUGIN_AUTH_PINCODE_ERROR);
						result.setMsg(errMsg);
						return result;
					}
					// String pinCode =
					// ControllerHelper.getJsonElement(jsonObject, value);
					// pinCode = this.decrypt(plugin, pinCode);
					if (!pinCode.equals(plugin.getProtectCode())) {
						errMsg = "auth para pinCode error";
						logger.error(errMsg);
						result.setCode(ErrorConstant.PLUGIN_AUTH_PINCODE_ERROR);
						result.setMsg(errMsg);
						return result;
					}
				} else {
					errMsg = "auth para pinCode not exist";
					logger.error(errMsg);
					result.setCode(ErrorConstant.PLUGIN_AUTH_PINCODE_ERROR);
					result.setMsg(errMsg);
					return result;
				}
			}
			// pin码型
			if ("pinAuthCode".equals(value)) {
				if (jsonObject.has(value)) {
					String encrptedSotpCode = ControllerHelper.getJsonElement(jsonObject, value);
					int ret = this.verifySotpCode(plugin, authFeaInfo, encrptedSotpCode, true, sotpCodePara,
					        verifyPoliceInfo);
					logger.debug("verify pinAuthCode ret:" + ret);
					if (ret != ErrorConstant.RET_OK) {
						errMsg = "pinAuthCode verify failed";
						logger.error(errMsg);
						result.setCode(ErrorConstant.PLUGIN_AUTH_PINAUTHCODE_ERROR);
						result.setMsg(errMsg);
						return result;
					}
				} else {
					errMsg = "auth para pinAuthCode not exist";
					logger.error(errMsg);
					result.setCode(ErrorConstant.PLUGIN_AUTH_PINAUTHCODE_ERROR);
					result.setMsg(errMsg);
					return result;
				}
			}
			// 时间型
			if ("timeAuthCode".equals(value)) {
				if (jsonObject.has(value)) {
					String encrptedSotpCode = ControllerHelper.getJsonElement(jsonObject, value);
					int ret = this.verifySotpCodeWithChallengeNotClear(plugin, authFeaInfo, encrptedSotpCode, true,
					        verifyPoliceInfo);
					logger.debug("verify timeAuthCode ret:" + ret);
					if (ret != ErrorConstant.RET_OK) {
						errMsg = "timeAuthCode verify failed";
						logger.error(errMsg);
						result.setCode(ErrorConstant.PLUGIN_AUTH_TIMEAUTHCODE_ERROR);
						result.setMsg(errMsg);
						return result;
					}
				} else {
					errMsg = "auth para timeAuthCode not exist";
					logger.error(errMsg);
					result.setCode(ErrorConstant.PLUGIN_AUTH_TIMEAUTHCODE_ERROR);
					result.setMsg(errMsg);
					return result;
				}
			}
			// pin码和服务端挑战型
			if ("pinAndSCAuthCode".equals(value)) {
				if (jsonObject.has(value)) {
					String encrptedSotpCode = ControllerHelper.getJsonElement(jsonObject, value);
					int ret = this.verifySotpCodeWithChallengeNotClear(plugin, authFeaInfo, encrptedSotpCode, true,
					        verifyPoliceInfo);
					logger.debug("verify pinAndSCAuthCode ret:" + ret);
					if (ret != ErrorConstant.RET_OK) {
						errMsg = "pinAndSCAuthCode verify failed";
						logger.error(errMsg);
						result.setCode(ErrorConstant.PLUGIN_AUTH_PINANDCHANLLENGEAUTHCODE_ERROR);
						result.setMsg(errMsg);
						return result;
					}
				} else {
					errMsg = "auth para pinAndSCAuthCode not exist";
					logger.error(errMsg);
					result.setCode(ErrorConstant.PLUGIN_AUTH_PINANDCHANLLENGEAUTHCODE_ERROR);
					result.setMsg(errMsg);
					return result;
				}
			}
			// cCAuthCode – 客户端挑战型认证码
			// pinAndCCAuthCode – PIN码加客户端挑战型认证码
			// sCAndCCAuthCode -- 服务端加客户端挑战型认证码
			if ("cCAuthCode".equals(value) || "pinAndCCAuthCode".equals(value) || "sCAndCCAuthCode".equals(value)) {
				if (jsonObject.has(value)) {
					JSONObject jsonObjectCCAuthCode = jsonObject.getJSONObject(value);
					if (jsonObjectCCAuthCode.has("verifyCode") && jsonObjectCCAuthCode.has("param")) {
						String encrptedSotpCode = ControllerHelper.getJsonElement(jsonObjectCCAuthCode, "verifyCode");
						sotpCodePara = ControllerHelper.getJsonElement(jsonObjectCCAuthCode, "param");
						int ret = this.verifySotpCode(plugin, authFeaInfo, encrptedSotpCode, true, sotpCodePara,
						        verifyPoliceInfo);
						logger.debug("verify " + value + " ret:" + ret);
						if (ret != ErrorConstant.RET_OK) {
							errMsg = value + " verify failed";
							logger.error(errMsg);
							result.setCode(ErrorConstant.PLUGIN_AUTH_CLIENTCHANLLENGEAUTHCODE_ERROR);
							result.setMsg(errMsg);
							return result;
						}
					}
				} else {
					errMsg = "auth para " + value + " not exist";
					logger.error(errMsg);
					result.setCode(ErrorConstant.PLUGIN_AUTH_CLIENTCHANLLENGEAUTHCODE_ERROR);
					result.setMsg(errMsg);
					return result;
				}
			}
		}
		return result;
	}

	// 解析pinCode
	public String formatPinCode(pluginInfoDto plugin, JSONObject pinCodeJson) {
		String result = null;
		String errMsg = null;
		if (pinCodeJson.has("pin") && pinCodeJson.has("encodedType")) {
			String encodedType = ControllerHelper.getJsonElement(pinCodeJson, "encodedType");
			String pin = ControllerHelper.getJsonElement(pinCodeJson, "pin");
			if (encodedType.equals("default")) {
				result = pin;
			} else if (encodedType.equals("sotp")) {
				result = this.decrypt(plugin, pin);
			} else {
				errMsg = "pinCode format error";
				logger.error(errMsg);
				return result;
			}
		} else {
			errMsg = "pinCode format error";
			logger.error(errMsg);
			return result;
		}
		return result;
	}
}
