package com.peopleNet.sotp.controller;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.controller.VerifyData.VerifyRet;
import com.peopleNet.sotp.dal.dao.authFeatureDtoMapper;
import com.peopleNet.sotp.dal.dao.pluginStatisticDtoMapper;
import com.peopleNet.sotp.dal.dao.userInfoDtoMapper;
import com.peopleNet.sotp.dal.model.RulePolicyDto;
import com.peopleNet.sotp.dal.model.appVersionInfoDto;
import com.peopleNet.sotp.dal.model.authFeatureDto;
import com.peopleNet.sotp.dal.model.pluginInfoDto;
import com.peopleNet.sotp.exception.BusinessException;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.*;
import com.peopleNet.sotp.thrift.service.PluginSaveHelper;
import com.peopleNet.sotp.thrift.service.SotpPlugin;
import com.peopleNet.sotp.thrift.service.SotpRet;
import com.peopleNet.sotp.util.*;
import com.peopleNet.sotp.util.Base64;
import com.peopleNet.sotp.vo.ResultVO;
import com.peopleNet.sotp.vo.UserRequestMsg;
import net.sf.json.JSONObject;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

//import org.junit.internal.runners.ErrorReportingRunner;

@Controller
public class PluginMagController {
	private static LogUtil logger = LogUtil.getLogger(PluginMagController.class);

	@Autowired
	private IEncryptPluginInfoService pluginInfoMapper;
	@Autowired
	private userInfoDtoMapper userInfoMapper;
	@Autowired
	private pluginStatisticDtoMapper pluginStatisticMapper;
	@Autowired
	private authFeatureDtoMapper authFeatureMapper;
	@Autowired
	private IPluginService pluginService;
	@Autowired
	private ICacheService cacheService;
	@Autowired
	private IThriftInvokeService thriftInvokeService;
	@Autowired
	private IBusinessService businessService;
	@Autowired
	private IParaHandle paraHandle;
	@Autowired
	private ISignService signService;
	@Autowired
	private IPolicyService policyService;
	@Autowired
	private IDBOperationService dbOperationService;

	private String THOR_URL = CommonConfig.get("THOR_URL");
	private int CONNECT_THOR_PROTOCOL = Integer.parseInt(CommonConfig.get("CONNECT_THOR_PROTOCOL").trim());
	private String PLUGIN_STRORE_MODE = CommonConfig.get("PLUGIN_SAVE_MODE").trim();

	/**
	 * 将json格式的字符串解析成Map对象
	 * <li>json格式：{"name":"admin","retries":"3fff","testname"
	 * "ddd","testretries":"fffffffff"}
	 */
	private static HashMap<String, Object> toHashMap(JSONObject json) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		// 将json字符串转换成jsonObject

		Iterator it = json.keys();
		// 遍历jsonObject数据，添加到Map对象
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			Object value = (Object) json.get(key);
			data.put(key, value);
		}
		return data;
	}

	@RequestMapping(value = "pluginmag")
	@ResponseBody
	public String pluginmagTest() throws BusinessException {
		String hello = "hello,pluginmag ok!";
		logger.debug(hello);
		return hello;
	}

	/*
	 * 解绑插件
	 */
	@RequestMapping(value = "plugindel")
	@ResponseBody
	public String plugindel(@ModelAttribute UserRequestMsg requestMsg, HttpServletRequest request) {

		String errMsg = "";
		String reqmsg = request.getQueryString();

		int type = requestMsg.getType();
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String sotpId = requestMsg.getSotpId();
		String deldev = requestMsg.getDeldev();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();

		// 业务类型判断
		if (type != Constant.SERVICE_TYPE.SOTP_PLUGIN_DEL_TYPE) {
			logger.error("parameter type not right. type:" + type);
			return pluginRetStrV2(appId, "", Constant.SERVICE_TYPE.SOTP_UNKOWN_TYPE, ErrorConstant.PARA_TYPE_UNKOWN, "",
			        "", "para type unkown.", reqmsg);
		}
		logger.debug("plugindel parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId
		        + ", deldev" + deldev);

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormat(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return pluginRetStrV2(appId, "", type, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg);
		}

		appVersionInfoDto appVersionInfo = paraHandle.getAppVersionInfoByAppId(appId);
		if (null == appVersionInfo) {
			errMsg = "appVersionInfo not exist db.";
			logger.error(errMsg);
			return pluginRetStrV2(appId, "", type, ErrorConstant.SYSTEM_MALLOC_ERR, "", "", errMsg, reqmsg);
		}

		// 签名校验
		String appKey = appVersionInfo.getApp_key();
		Map<String, Object> paramMap = requestMsg.getSignParaMap();
		if (verifySign(appKey, paramMap, sign) != ErrorConstant.RET_OK) {
			errMsg = "sign not match.";
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.PARACONT_SIGN_NOTMATCH, "", "", errMsg, reqmsg);
		}

		// 插件信息判断
		pluginInfoDto pInfo = paraHandle.getPluginfo(sotpId);
		if (pInfo == null) {
			logger.error("sotpId db not exist");
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", "",
			        "sotpId db not exist", reqmsg);
		}
		/*
		 * resultVO = policyService.pluginStatus(pInfo); if (null != resultVO &&
		 * resultVO.getCode() != ErrorConstant.RET_OK) {
		 * logger.error(resultVO.getMsg()); return pluginRetStrV2(appId, appKey,
		 * type, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg); }
		 */
		if (!pInfo.getBindUserphone().equals(phoneNum)) {
			logger.error("phoneNum not right");
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.PARACONT_PHONENUM_NOEXIST, "", "",
			        "phoneNum not right", reqmsg);
		}

		authFeatureDto tmpauthInfo = null;
		pluginInfoDto tpluginInfo = null;
		int flag = 0;
		List<pluginInfoDto> plInfolist = pluginInfoMapper.selectByphoneNum(phoneNum);
		for (pluginInfoDto tmp : plInfolist) {
			if (tmp != null) {
				try {
					tmpauthInfo = authFeatureMapper.selectByPluginId(tmp.getPluginId());
				} catch (SQLException e) {
					logger.error("authFeatureMapper selectByPluginId sql error.msg:%s", e.toString());
				}
				if (tmpauthInfo != null) {
					switch (tmpauthInfo.getdevType()) {
					case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_ARM:
					case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_MIPS:
					case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_X86:
						if (tmpauthInfo.getimei().equals(deldev)) {
							flag = 1;
							tpluginInfo = tmp;
						}
						break;
					case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_IOS:
						if (tmpauthInfo.getuuid().equals(deldev)) {
							flag = 1;
							tpluginInfo = tmp;
						}
						break;
					}
				}
			}
		}

		if (flag == 1) {
			if (tpluginInfo != null) {
				UpdateUtil updateInfo = new UpdateUtil();
				updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.STATUS,
				        Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
				updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID,
				        tpluginInfo.getPluginId());

				Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
				this.pluginInfoMapper.updateHashMapByPluginId(updateMap);
			}
		} else {
			logger.error("sotpid uid or dev not right.");
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.PLUGIN_DEL_UID_SID_ERR, "", "",
			        "sotpid uid or dev not right.", reqmsg);
		}

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return pluginRetStrV2(appId, appKey, type, ErrorConstant.RET_OK, "", "", "", reqmsg);
	}

	/*
	 * 插件管理请求 返回数据
	 */
	public String pluginRetStr(int type, int status, String pluginContent, String hwInfo, String errMsg,
	        String reqmsg) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String timeinfo = dateFormat.format(System.currentTimeMillis());

		Map<String, Object> message = new HashMap<String, Object>();
		if (pluginContent != null && pluginContent.length() > 0) {
			message.put("data", pluginContent);
		}

		if (hwInfo != null && hwInfo.length() > 0) {
			message.put("hwInfo", hwInfo);
		}
		if (Constant.SERVICE_TYPE.SOTP_PLUGIN_REGISTER_TYPE == type
		        || Constant.SERVICE_TYPE.SOTP_PLUGIN_UPDATE_TYPE == type) {
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
		}
		if (status != ErrorConstant.RET_OK) {
			if (errMsg != null && errMsg.length() > 0) {
				message.put("errorMsg", errMsg);
			}
		}
		String jsonResStr = ControllerHelper.getResultJsonString(type, status, message, timeinfo);
		logger.debug("sotpAuth response:" + jsonResStr);

		// 记录业务日志
		this.businessService.setBusinessLog(reqmsg, jsonResStr, status, errMsg);

		return jsonResStr;
	}

	/*
	 * 返回值加入appId、nonce_str、sign
	 */
	public String pluginRetStrV2(String appId, String appKey, int type, int status, String pluginContent, String hwInfo,
	        String errMsg, String reqmsg) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String timeinfo = dateFormat.format(System.currentTimeMillis());

		// TODO 生成随机数字符串
		Random rad = new Random();
		String nonce_str = rad.nextInt(1000) + "";

		Map<String, Object> message = new HashMap<String, Object>();
		if (pluginContent != null && pluginContent.length() > 0) {
			message.put("data", pluginContent);
		}

		if (hwInfo != null && hwInfo.length() > 0) {
			message.put("hwInfo", hwInfo);
		}

		/*
		 * if (hwInfo != null && hwInfo.length() > 0) { paramMap.put("hwInfo",
		 * hwInfo); }
		 */
		if (Constant.SERVICE_TYPE.SOTP_PLUGIN_REGISTER_TYPE == type
		        || Constant.SERVICE_TYPE.SOTP_PLUGIN_UPDATE_TYPE == type) {
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
		}
		if (status != ErrorConstant.RET_OK) {
			if (errMsg != null && errMsg.length() > 0) {
				message.put("errorMsg", errMsg);
			}
		}

		Map<String, Object> paramMap2 = new HashMap<String, Object>();

		String sign = "";
		String jsonResStr = ControllerHelper.getResultJsonStringV2(appId, nonce_str, sign, type, status, message,
		        timeinfo);
		JSONObject result = JSONObject.fromObject(jsonResStr);
		paramMap2 = toHashMap(result);
		sign = signService.signParaByAppKey(paramMap2, appKey, Constant.SIGN_METHOD.MD5);
		jsonResStr = ControllerHelper.getResultJsonStringV2(appId, nonce_str, sign, type, status, message, timeinfo);
		logger.debug("sotpAuth response:" + jsonResStr);

		// 记录业务日志
		this.businessService.setBusinessLog(reqmsg, jsonResStr, status, errMsg);

		return jsonResStr;
	}

	public int verifySign(String appKey, Map<String, Object> paramMap, String sign) {
		int ret = 0;
		String signStr = signService.signParaByAppKey(paramMap, appKey, Constant.SIGN_METHOD.MD5);
		logger.debug("sign:" + sign + "---signstr:" + signStr + "appkey:" + appKey);
		if (!signStr.equals(sign)) {
			ret = 1;
		}
		return ret;
	}

	/*
	 * 连接密码机验证身份码
	 */
	public int verifSotpCode(pluginInfoDto plInfo, authFeatureDto authFeaInfo, String sotpCode, String sotpCodePara) {

		int ret = 0;
		int otptype = 0;
		String post_data = "";
		String challenge = "";

		String pin = "";
		String chall = "";

		// redis 读验证策略 (取时间窗口)
		RulePolicyDto verifyPolicyInfo = null;
		try {
			verifyPolicyInfo = this.cacheService.getVerifyPolicy(Constant.POLICY_TYPE.VERIFY, plInfo.getAppCode());
		} catch (BusinessException e) {

			logger.error("getVerifyPolicy exception. msg:%s", e.toString());
			return ErrorConstant.SQL_READREDIS_ERR;
		}

		if (verifyPolicyInfo == null) {

			logger.error("redis RulePolicyDto getVerifyPolicy  null");
			return ErrorConstant.SQL_READREDIS_ERR;
		}

		int windowt = Constant.POLICY_DEFAULTVAL.POLICY_DEFAULTVAL_WINDOWTIME;
		if (verifyPolicyInfo.getAuthWindowSize() != null && verifyPolicyInfo.getAuthWindowSize() > 0)
			windowt = verifyPolicyInfo.getAuthWindowSize();

		int nsenconds = Integer.parseInt(DateUtil.timeStamp(0));

		// 解析sotpCode
		String[] codesplit = sotpCode.split("@");
		if (codesplit.length != 2) {

			logger.error("verifSotpCode format error: " + sotpCode);
			return ErrorConstant.PARA_SOTPCODEFORMAT_ERR;
		}

		switch (Integer.parseInt(codesplit[0])) {
		case 10:
			otptype = 1;
			break;

		case 20:
		case 21:
			otptype = 2;
			break;

		case 30:
			otptype = 3;
			pin = "";
			chall = sotpCodePara;
			break;

		case 40:
			otptype = 4;
			pin = plInfo.getProtectCode();
			chall = sotpCodePara;
			break;
		case 41:
			otptype = 4;
			// pin = plInfo.getChallengeCode();
			pin = (String) this.cacheService
			        .get(Constant.REDIS_CONSTANT.PLUGIN_USER_CHALLENGE_CODE + plInfo.getPluginId());

			if (pin == null) {
				logger.error("challenge code time out!");
				return ErrorConstant.PLUGIN_CHALLENGE_CODE_TIMEOUT;
			}
			this.cacheService.delete(Constant.REDIS_CONSTANT.PLUGIN_USER_CHALLENGE_CODE + plInfo.getPluginId());
			chall = sotpCodePara;
			break;
		default:
			logger.error("sotpCode type error: " + sotpCode);
			return ErrorConstant.PARA_SOTPCODETYPE_ERR;
		}

		int plugintype = authFeaInfo.getdevType();
		switch (plugintype) {
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_ARM:
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_MIPS:
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_X86:
			challenge = authFeaInfo.getProductType() + "-" + authFeaInfo.getimei() + "-" + authFeaInfo.getmac() + "|"
			        + pin + "|" + chall;
			break;

		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_IOS:
			challenge = authFeaInfo.getProductType() + "-" + authFeaInfo.getuuid() + "-" + authFeaInfo.getmac() + "|"
			        + pin + "|" + chall;
			break;
		}

		logger.info("verifSotpCode to thor para, time:" + nsenconds + ", window:" + windowt + ", pin:" + pin + ", chal:"
		        + challenge + ", sotpcode:" + codesplit[1]);

		switch (CONNECT_THOR_PROTOCOL) {
		case Constant.THOR_PROTOCOL.SOTP_CONNECT_THOR_PROTOCOL_HTTP:
			// 整合发送数据
			post_data = "type=1005";
			post_data += "&seed=" + plInfo.getPluginKey();
			post_data += "&otptype=" + otptype;
			post_data += "&challenge=" + challenge;
			post_data += "&data=" + codesplit[1];
			String reponsetContent = HttpUtils.post(THOR_URL, post_data);
			logger.debug("post_data:" + post_data);
			if (reponsetContent == null || reponsetContent.length() <= 0) {
				logger.error("http sotpthorReponse is null.");
				return ErrorConstant.THOR_CONNECT_TIMEOUT;
			}
			logger.debug("http sotp_crypto_machine verify result:" + reponsetContent);
			if (Integer.parseInt(reponsetContent.trim()) != 0)
				return ErrorConstant.THOR_CONNECT_TIMEOUT;

			break;

		case Constant.THOR_PROTOCOL.SOTP_CONNECT_THOR_PROTOCOL_TCP:
			try {
				ret = thriftInvokeService.sotpVerifyV2(otptype, plInfo.getPluginId(), plInfo.getPluginKey(), nsenconds,
				        windowt, pin, challenge, codesplit[1]);
			} catch (TException e) {
				logger.error("invoke thrift verify exception,msg:%s", e.toString());
				return ErrorConstant.THOR_CONNECT_TIMEOUT;
			}
			if (ret == ErrorConstant.RET_OK) {
				return ErrorConstant.RET_OK;
			}
			break;

		default:
			logger.error("connect thor protocol error");
			return ErrorConstant.THOR_CONNECT_PROTOCOL_ERR;
		}

		switch (ret) {
		case ErrorConstant.THOR_RES_PARA_ERR:
			logger.error("THOR return error:" + ret);
			ret = ErrorConstant.THOR_PARAFORMAT_ERR;
			break;
		case ErrorConstant.THOR_RES_SYSTEM_ERR:
			logger.error("THOR return error:" + ret);
			ret = ErrorConstant.THOR_SYSTEM_ERR;
			break;
		case ErrorConstant.THOR_RES_BASE64_ERR:
			logger.error("THOR return error:" + ret);
			ret = ErrorConstant.THOR_BASE64_ERR;
			break;
		case ErrorConstant.THOR_RES_MALLOC_ERR:
			logger.error("THOR return error:" + ret);
			ret = ErrorConstant.THOR_MALLOC_ERR;
			break;
		default:
			logger.error("THOR return unkown error:" + ret);
			ret = ErrorConstant.THOR_RESP_UNKOWNERR;
		}

		return ret;
	}

	/*
	 * 注册申请插件
	 */
	@RequestMapping(value = "pluginreg")
	@ResponseBody
	public String pluginreg(@ModelAttribute UserRequestMsg requestMsg, HttpServletRequest request) {

		int ret = ErrorConstant.RET_OK;
		String reqmsg = request.getQueryString();
		String errMsg = "";
		int pluginInitStatus = Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY;

		// 参数定义
		int type = requestMsg.getType();
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		if (null == userId) {
			userId = phoneNum;
		}
		String sotpPayInfo = requestMsg.getSotpPayInfo();
		String devInfo = requestMsg.getDevInfo();
		String sms = requestMsg.getSms();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();

		logger.debug("type:" + requestMsg.getType());
		logger.debug("reqmsg:" + reqmsg);
		// 业务类型判断
		if (type != Constant.SERVICE_TYPE.SOTP_PLUGIN_REGISTER_TYPE) {
			logger.error("pluginreg type not right.");
			return pluginRetStrV2(appId, "", Constant.SERVICE_TYPE.SOTP_UNKOWN_TYPE, ErrorConstant.PARA_TYPE_UNKOWN, "",
			        "", "para type unkown.", reqmsg);
		}

		// TODO 在paraFormat函数中完成参数的校验,包括商户信息及应用信息
		ResultVO resultVO = paraHandle.paraFormat(requestMsg);
		if (null == resultVO || resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return pluginRetStrV2(appId, "", type, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg);
		}

		// 应用信息验证
		appVersionInfoDto appVersionInfo = paraHandle.getAppVersionInfoByAppId(appId);
		if (null == appVersionInfo) {
			errMsg = "appVersionInfo not exist db.";
			logger.error(errMsg);
			return pluginRetStrV2(appId, "", type, ErrorConstant.SYSTEM_MALLOC_ERR, "", "", errMsg, reqmsg);
		}

		// 签名校验
		String appKey = appVersionInfo.getApp_key();
		Map<String, Object> paramMap = requestMsg.getSignParaMap();
		if (verifySign(appKey, paramMap, sign) != ErrorConstant.RET_OK) {
			errMsg = "sign not match.";
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.PARACONT_SIGN_NOTMATCH, "", "", errMsg, reqmsg);
		}

		// 获取申请策略
		RulePolicyDto applyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.APPLY, appId);
		if (applyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpapplypolicy is null";
			logger.error(errMsg);
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.POLICY_FETCH_ERROR, "", "", errMsg, reqmsg);
		}

		// 短信码验证处理
		resultVO = policyService.smsVeryPolicy(sms, cacheService, userInfoMapper);
		if (null == resultVO || resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return pluginRetStrV2(appId, appKey, type, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg);
		}

		// 插件申请策略
		// 添加根据应用id获取该应用特有策略的逻辑
		resultVO = policyService.pluginReqPolicy(phoneNum, appId, applyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return pluginRetStrV2(appId, appKey, type, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg);
		}

		// 插件初始状态
		pluginInitStatus = getPluginInitStatus(appId);
		if (pluginInitStatus != Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY
		        && pluginInitStatus != Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NO_ACTIVATE) {
			logger.info("policy plugin init status is not right, set default: ready");
			pluginInitStatus = Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY;
		}

		// 认证硬件信息
		authFeatureDto authFeaInfo = paraHandle.getNewAuthFeature(devInfo);
		if (authFeaInfo == null) {
			errMsg = "new authFeaInfo error.";
			logger.error(errMsg);
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.SYSTEM_MALLOC_ERR, "", "", errMsg, reqmsg);
		}

		// 插件信息
		pluginInfoDto pluginInfo = paraHandle.getNewPluginfo(userId, phoneNum, sotpPayInfo);
		if (pluginInfo == null) {
			errMsg = "new pluginInfo error.";
			logger.error(errMsg);
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.SYSTEM_MALLOC_ERR, "", "", errMsg, reqmsg);
		}

		SotpPlugin plugin = genThorPlugin(authFeaInfo, pluginInfo, appVersionInfo);
		if (null == plugin) {
			logger.error("generate plugin error!");

			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();

			return pluginRetStrV2(appId, appKey, type, ErrorConstant.THOR_SYSTEM_ERR, "", "", "generate plugin error!",
			        reqmsg);
		}

		// 根据申请策略设置插件初始状态
		pluginInfo.setStatus(pluginInitStatus);
		Date date = new Date(System.currentTimeMillis());
		pluginInfo.setStartTime(date);

		try {
			this.dbOperationService.insertRegPluginIntoDB(authFeaInfo, pluginInfo, phoneNum, userId, appId);
		} catch (Exception e) {
			logger.error("insert into db error. " + e.toString());

			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();

			return pluginRetStrV2(appId, appKey, type, ErrorConstant.SQL_WRITEDB_ERR, "", "", "insert into db error!",
			        reqmsg);
		}

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return pluginRetStrV2(appId, appKey, type, ret, plugin.getPlugin(), plugin.getFile(), "", reqmsg);
	}

	public int getPluginInitStatus(String appId) {
		RulePolicyDto reqPolicyInfo = null;
		int pluginInitStatus = Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY;
		try {
			reqPolicyInfo = cacheService.getApplyPolicy("pluginapplypolicy", appId);
		} catch (BusinessException e) {
			String errMsg = "redis getApplyPolicy exception. msg:%s" + e.toString();
			logger.error(errMsg);
		}
		logger.debug("pluginInitStatus: " + reqPolicyInfo.getPluginInitStatus());
		if (null == reqPolicyInfo.getPluginInitStatus()) {
			return pluginInitStatus;
		}
		pluginInitStatus = reqPolicyInfo.getPluginInitStatus();
		return pluginInitStatus;
	}

	/*
	 * 更新插件
	 */
	@RequestMapping(value = "pluginupdate")
	@ResponseBody
	public String pluginupdate(@ModelAttribute UserRequestMsg requestMsg, HttpServletRequest request) {

		String errMsg = "";
		String reqmsg = request.getQueryString();

		int type = requestMsg.getType();
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		if (null == userId) {
			userId = phoneNum;
		}
		String sotpId = requestMsg.getSotpId();
		String devInfo = requestMsg.getDevInfo();
		String sotpCode = requestMsg.getSotpCode();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();

		// 业务类型判断
		if (type != Constant.SERVICE_TYPE.SOTP_PLUGIN_UPDATE_TYPE) {
			logger.error("parameter type not right. type:" + type);
			return pluginRetStrV2(appId, "", Constant.SERVICE_TYPE.SOTP_UNKOWN_TYPE, ErrorConstant.PARA_TYPE_UNKOWN, "",
			        "", "para type unkown.", reqmsg);
		}
		logger.debug("pluginupdate parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId
		        + ", sotpCode:" + sotpCode);

		ResultVO resultVO = paraHandle.paraFormat(requestMsg);
		if (null == resultVO || resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return pluginRetStrV2(appId, "", type, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg);
		}

		// 应用信息验证
		appVersionInfoDto appVersionInfo = paraHandle.getAppVersionInfoByAppId(appId);
		if (null == appVersionInfo) {
			errMsg = "appVersionInfo not exist db.";
			logger.error(errMsg);
			return pluginRetStrV2(appId, "", type, ErrorConstant.SYSTEM_MALLOC_ERR, "", "", errMsg, reqmsg);
		}

		// 签名校验
		String appKey = appVersionInfo.getApp_key();
		Map<String, Object> paramMap = requestMsg.getSignParaMap();
		if (verifySign(appKey, paramMap, sign) != ErrorConstant.RET_OK) {
			errMsg = "sign not match.";
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.PARACONT_SIGN_NOTMATCH, "", "", errMsg, reqmsg);
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.POLICY_FETCH_ERROR, "", "", errMsg, reqmsg);
		}

		// 获取申请策略
		RulePolicyDto applyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.APPLY, appId);
		if (applyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpapplypolicy is null";
			logger.error(errMsg);
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.POLICY_FETCH_ERROR, "", "", errMsg, reqmsg);
		}

		// 插件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		if (plInfo == null) {
			errMsg = "pluginInfo is not exist in db.";
			logger.error(errMsg);
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", "", errMsg, reqmsg);
		}

		// 认证硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(sotpId);
		if (authFeaInfo == null) {
			errMsg = "authFeaInfo is not exist in db.";
			logger.error(errMsg);
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.PARACONT_DEVINFO_NOEXIST, "", "", errMsg, reqmsg);
		}

		// 验证插件硬件信息是否匹配
		VerifyData vd = new VerifyData(cacheService);
		VerifyRet vr = vd.verifyDevInfo(plInfo, authFeaInfo, devInfo);
		boolean verifyDevInfo = vr.isOk();
		if (!verifyDevInfo) {
			errMsg = "devInfo is not right!" + vr.getMsg();
			logger.error(errMsg);
			this.pluginService.updatePluginInfoStatusById(sotpId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.PLUGIN_USELESS, "", "", errMsg, reqmsg);
		}

		// 判断参数信息
		if (!plInfo.getBindUserphone().equals(phoneNum)) {
			logger.error("phoneNum not right");
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.PARACONT_PHONENUM_NOEXIST, "", "",
			        "phoneNum not right", reqmsg);
		}

		// 插件状态：是否为就绪
		resultVO = policyService.pluginStatus(plInfo, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return pluginRetStrV2(appId, appKey, type, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg);
		}

		// 连接密码机， 验证sotpCode(30)
		String challengeCode = (String) this.cacheService
		        .get(Constant.REDIS_CONSTANT.PLUGIN_USER_CHALLENGE_CODE + plInfo.getPluginId());
		// 多商户测试时直接设置挑战值，后续可以在index中将挑战值存到redis中
		// String challengeCode = "377891";

		if (challengeCode == null) {
			logger.error("challenge code time out!");
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.PLUGIN_CHALLENGE_CODE_TIMEOUT, "", "",
			        "challenge code time out!", reqmsg);
		}

		this.cacheService.delete(Constant.REDIS_CONSTANT.PLUGIN_USER_CHALLENGE_CODE + plInfo.getPluginId());
		// 对 sotpCode 进行解密
		SotpRet sotpRes = null;
		try {
			sotpRes = thriftInvokeService.sotpDecryptV2(plInfo.getPluginId(), plInfo.getPluginKey(), sotpCode);
		} catch (TException e) {
			logger.error("invoke thrift decryptNew error.msg:%s", e.toString());
		}
		if (sotpRes == null || sotpRes.status < 0) {
			logger.error("sotpthorReponse is null.");
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.THOR_CONNECT_TIMEOUT, "", "",
			        "connect sotp_crypto_machine wrong.", reqmsg);
		}
		String realSotpCode = sotpRes.getTitle();
		int ret = verifSotpCode(plInfo, authFeaInfo, realSotpCode, challengeCode);
		if (ret != ErrorConstant.RET_OK) {
			logger.error("getstatus verif challenge code error:", ret);
			return pluginRetStrV2(appId, appKey, type, ret, "", "", "getstatus verify challenge code error.", reqmsg);
		}

		// 插件申请策略
		resultVO = policyService.pluginReqPolicy(phoneNum, appId, applyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return pluginRetStrV2(appId, appKey, type, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg);
		}

		// 插件信息、硬件信息
		pluginInfoDto newpluginInfo = new pluginInfoDto();

		newpluginInfo.setHoldInfo(plInfo.getHoldInfo());
		newpluginInfo.setProtectCode(plInfo.getProtectCode());
		newpluginInfo.setBindUserphone(phoneNum);
		newpluginInfo.setBindUserid(userId);
		newpluginInfo.setTotalErrcnt(0);
		newpluginInfo.setTotalUsecnt(0);
		newpluginInfo.setVerifyErrcnt(0);

		// 分配插件
		SotpPlugin plugin = genThorPlugin(authFeaInfo, newpluginInfo, appVersionInfo);
		if (null == plugin) {
			logger.error("generate plugin error!");

			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();

			return pluginRetStrV2(appId, appKey, type, ErrorConstant.THOR_SYSTEM_ERR, "", "", "generate plugin error!",
			        reqmsg);
		}
		newpluginInfo.setStatus(Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);
		Date date = new Date(System.currentTimeMillis());
		newpluginInfo.setStartTime(date);
		newpluginInfo.setAppCode(appId);

		try {
			this.dbOperationService.insertUpdatePluginIntoDB(authFeaInfo, plInfo, newpluginInfo, appId);
		} catch (Exception e) {
			logger.error("insert into db error. " + e.toString());

			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();

			return pluginRetStrV2(appId, appKey, type, ErrorConstant.SQL_WRITEDB_ERR, "", "", "insert into db error!",
			        reqmsg);
		}

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return pluginRetStrV2(appId, appKey, type, ret, plugin.getPlugin(), plugin.getFile(), "", reqmsg);
	}

	/*
	 * 激活插件
	 */
	@RequestMapping(value = "activePlugin")
	@ResponseBody
	public String activePlugin(@ModelAttribute UserRequestMsg requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		int ret = ErrorConstant.RET_OK;
		String errMsg = "";

		int type = requestMsg.getType();
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String activeCode = requestMsg.getActiveCode();
		String sotpId = requestMsg.getSotpId();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();

		logger.debug("activePluginInterface activeCode-----" + activeCode);
		if (type != Constant.SERVICE_TYPE.SOTP_VERIFY_ACTIVE_CODE) {
			logger.error("parameter type not right. type:" + type);
			return pluginRetStrV2(appId, "", Constant.SERVICE_TYPE.SOTP_UNKOWN_TYPE, ErrorConstant.PARA_TYPE_UNKOWN, "",
			        "", "para type unkown.", reqmsg);
		}
		logger.debug("verify parameter phoneNum:" + phoneNum + ", activeCode:" + activeCode);

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormat(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return pluginRetStrV2(appId, "", type, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg);
		}

		// 应用信息校验
		appVersionInfoDto appVersionInfo = paraHandle.getAppVersionInfoByAppId(appId);
		if (null == appVersionInfo) {
			errMsg = "appVersionInfo not exist db.";
			logger.error(errMsg);
			return pluginRetStrV2(appId, "", type, ErrorConstant.SYSTEM_MALLOC_ERR, "", "", errMsg, reqmsg);
		}

		// 签名校验
		String appKey = appVersionInfo.getApp_key();
		Map<String, Object> paramMap = requestMsg.getSignParaMap();
		if (verifySign(appKey, paramMap, sign) != ErrorConstant.RET_OK) {
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.PARACONT_SIGN_NOTMATCH, "", "", errMsg, reqmsg);
		}

		// 获取验证策略
		RulePolicyDto activePolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.ACTIVE, appId);
		if (activePolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpactivepolicy is null";
			logger.error(errMsg);
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.POLICY_FETCH_ERROR, "", "", errMsg, reqmsg);
		}

		// 认证硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(sotpId);
		if (authFeaInfo == null) {
			errMsg = "authFeaInfo not exist db.";
			logger.error(errMsg);
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.PARACONT_DEVINFO_NOEXIST, "", null, errMsg,
			        reqmsg);
		}

		// 插件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		if (plInfo == null) {
			errMsg = "pluginId not exist db.";
			logger.error(errMsg);
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", null, errMsg,
			        reqmsg);
		}
		if (plInfo.getStatus() != Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NO_ACTIVATE) {
			errMsg = "plugin status is not right.";
			logger.error(errMsg);
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.PLUGIN_ACTIVECODE_STATUS_ERR, "", null, errMsg,
			        reqmsg);
		}

		// 验证激活策略
		resultVO = policyService.pluginActivePolicy(plInfo, activePolicyInfo);
		if (resultVO.getCode() == ErrorConstant.PLUGIN_HAVE_ACTIVE_MAX_TIMES) {
			// 激活次数过多，插件置为作废
			this.pluginService.updatePluginInfoStatusById(sotpId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
			logger.error(resultVO.getMsg());
			return pluginRetStrV2(appId, appKey, type, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg);
		}
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return pluginRetStrV2(appId, appKey, type, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg);
		}

		String realSotpCode = "30@" + activeCode;
		logger.debug(realSotpCode + "------" + realSotpCode.length() + "----------");
		// 验证身份码
		String pin = (String) this.cacheService
		        .get(Constant.REDIS_CONSTANT.PLUGIN_USER_CHALLENGE_CODE + plInfo.getPluginId());

		if (pin == null) {
			errMsg = "challenge code time out!";
			logger.error(errMsg);
			return pluginRetStrV2(appId, appKey, type, ErrorConstant.PLUGIN_CHALLENGE_CODE_TIMEOUT, "", null, errMsg,
			        reqmsg);
			// return ErrorConstant.PLUGIN_CHALLENGE_CODE_TIMEOUT;
		}
		this.cacheService.delete(Constant.REDIS_CONSTANT.PLUGIN_USER_CHALLENGE_CODE + plInfo.getPluginId());
		logger.debug("pin-------" + pin);
		ret = verifSotpCode(plInfo, authFeaInfo, realSotpCode, pin);
		if (ret != ErrorConstant.RET_OK) {
			errMsg = "active code error.please try again";
		}

		// 更新 激活计数
		UpdateUtil updateInfo = new UpdateUtil();
		if (ret != ErrorConstant.RET_OK) { // 错误
			// 激活次数加1
			plInfo.setActiveUseCnt(plInfo.getActiveUseCnt() + 1);
			updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.ACTIVE_USECNT, plInfo.getActiveUseCnt());
		} else { // 正确
			// 激活次数加1
			plInfo.setActiveUseCnt(plInfo.getActiveUseCnt() + 1);
			updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.ACTIVE_USECNT, plInfo.getActiveUseCnt());
			this.pluginService.updatePluginInfoStatusById(sotpId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);
		}
		updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plInfo.getPluginId());
		Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
		this.pluginInfoMapper.updateHashMapByPluginId(updateMap);

		// 正确
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return pluginRetStrV2(appId, appKey, type, ret, "", null, errMsg, reqmsg);

	}

	/*
	 * 访问密码机请求插件 (tcp)
	 */
	public SotpPlugin genThorPlugin(authFeatureDto authFeaInfo, pluginInfoDto pluginInfo,
	        appVersionInfoDto appVersionInfo) {
		int ret = 0;
		String pluginId = "";
		String seed = "";
		String plugin = "";
		String hwInfo = "";

		int plugintype = authFeaInfo.getdevType();
		String phonenum = pluginInfo.getBindUserphone();
		String dev = "";
		switch (plugintype) {
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_ARM:
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_MIPS:
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_X86:
			dev = authFeaInfo.getProductType() + "-" + authFeaInfo.getimei() + "-" + authFeaInfo.getmac();
			break;

		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_IOS:
			dev = authFeaInfo.getProductType() + "-" + authFeaInfo.getuuid() + "-" + authFeaInfo.getmac();
			break;
		}

		logger.debug("TcpReqThorPlugin genSotpNew:" + plugintype + "," + phonenum + "," + dev);

		SotpPlugin sotpPluginInfo = null;
		try {
			// 申请插件的接口 格式： 手机号|app hash值|app 签名值
			StringBuilder strB = new StringBuilder(phonenum);
			final char sep = 124;

			// appInfoDto appInfo = null;
			/*
			 * try { appInfo = cacheService.getAPPInfoByCode(new
			 * appInfoDto(plugintype)); } catch (BusinessException e) {
			 * e.printStackTrace(); }
			 */
			if (appVersionInfo != null) {
				strB.append(sep).append(appVersionInfo.getHash_value()).append(sep)
				        .append(appVersionInfo.getSignature());
			} else {
				logger.error("can not get appversioninfo from cache.");
				return null;
			}
			logger.debug("################## 申请插件接口：" + strB.toString());
			// sotpPluginInfo = thriftInvokeService.genSotpNew(plugintype,
			// strB.toString(), dev);
			// TODO 添加商户信息及商户秘钥

			String busi_code = paraHandle.getBusinessCodeByAppInfoId(appVersionInfo.getApp_info_id());
			;
			String appinfo_code = paraHandle.getAppInfoCodeByAppInfoId(appVersionInfo.getApp_info_id());
			String version = appVersionInfo.getVersion();
			String busi_info = "M" + busi_code + "A" + appinfo_code + "V" + version;
			logger.debug("busi_info:" + busi_info + "plugintype:" + plugintype + "appId:" + appVersionInfo.getApp_code()
			        + "strBS:" + strB.toString() + "dev:" + dev);
			String sdk_secret = appVersionInfo.getSdk_secret();
			sotpPluginInfo = thriftInvokeService.genSotpV2(plugintype, busi_info, sdk_secret, strB.toString(), dev);
		} catch (TException e) {
			logger.error("invoke thrift to generate plugin error. [error:%s][msg:%s]", e.toString(),
			        e.getMessage().toString());
			return null;
		}

		if (sotpPluginInfo == null) {
			logger.error("sotpthorReponse is null.");
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

		Date date = new Date(System.currentTimeMillis());

		// 设置 表项
		pluginInfo.setPluginId(pluginId);
		pluginInfo.setPluginKey(seed);
		pluginInfo.setPluginType(plugintype);
		pluginInfo.setGenTime(date);
		pluginInfo.setStatus(Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NO_ACTIVATE);

		byte[] decode = Base64.decode(plugin.toCharArray());
		logger.debug("plugin decode[" + ToStringBuilder.reflectionToString(decode));
		pluginInfo.setHashValue(SHA.SHA_people(decode));

		return sotpPluginInfo;
	}
}
