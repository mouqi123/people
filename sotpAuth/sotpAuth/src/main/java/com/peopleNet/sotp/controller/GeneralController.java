package com.peopleNet.sotp.controller;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.controller.VerifyData.VerifyRet;
import com.peopleNet.sotp.dal.dao.authFeatureDtoMapper;
import com.peopleNet.sotp.dal.model.RulePolicyDto;
import com.peopleNet.sotp.dal.model.appVersionInfoDto;
import com.peopleNet.sotp.dal.model.authFeatureDto;
import com.peopleNet.sotp.dal.model.pluginInfoDto;
import com.peopleNet.sotp.exception.BusinessException;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.*;
import com.peopleNet.sotp.thrift.service.PluginSaveHelper;
import com.peopleNet.sotp.thrift.service.SotpRet;
import com.peopleNet.sotp.util.CommonConfig;
import com.peopleNet.sotp.util.DateUtil;
import com.peopleNet.sotp.util.HttpUtils;
import com.peopleNet.sotp.util.UpdateUtil;
import com.peopleNet.sotp.vo.AutoUnlockPlugin;
import com.peopleNet.sotp.vo.ResultVO;
import com.peopleNet.sotp.vo.UserRequestMsg;
import net.sf.json.JSONObject;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class GeneralController {
	private static LogUtil logger = LogUtil.getLogger(GeneralController.class);

	@Autowired
	private IEncryptPluginInfoService pluginInfoMapper;
	@Autowired
	private authFeatureDtoMapper authFeatureMapper;
	@Autowired
	private ICacheService cacheService;
	@Autowired
	private IThriftInvokeService thriftInvokeService;
	@Autowired
	private IBusinessService businessService;
	@Autowired
	private IParaHandle paraHandle;
	@Autowired
	private IPolicyService policyService;
	@Autowired
	private IPluginService pluginService;
	@Autowired
	private ISignService signService;

	private String THOR_URL = CommonConfig.get("THOR_URL");
	private int CONNECT_THOR_PROTOCOL = Integer.parseInt(CommonConfig.get("CONNECT_THOR_PROTOCOL").trim());
	/*
	 * @RequestMapping(value = "updAppInfo")
	 * 
	 * @ResponseBody public String teset(@RequestParam("type") int
	 * devType, @RequestParam(value = "hashVal") String hashVal,
	 * 
	 * @RequestParam("sigVal") String sigVal, HttpServletRequest request) {
	 * appInfoDto app = new appInfoDto(); app.setAppHashValue(hashVal);
	 * app.setAppSignature(sigVal); app.setDevType(devType);
	 * 
	 * cacheService.clrAppInfoDto(app); int i = paraHandle.updAppInfo(app); int
	 * status = i < 0 ? 1 : 0; return retStr(devType, status, "", "", null, "");
	 * }
	 */

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

	@RequestMapping(value = "index")
	@ResponseBody
	public String index(HttpServletRequest request, HttpServletResponse response) {
		String hello = "hello,everybody!";

		String obj = cacheService.getRule("hadoop_2score");
		logger.info("----");
		logger.info(obj);

		boolean ds = PluginSaveHelper.getAvailable();
		PluginSaveHelper.release();
		logger.info((true == ds ? "true" : "false"));

		String encoding = System.getProperty("file.encoding");
		logger.info("系统默认编码 :" + encoding);

		/*
		 * String testpara = request.getParameter("testpara");
		 * logger.debug(testpara);
		 */
		/*
		 * logger.debug("set redis cluster result:" +
		 * cacheService.testSet("keytest", "valuetest")); logger.debug(
		 * "get redis cluster result:" +
		 * cacheService.testGet("keytest").toString());
		 */
		/*
		 * userInfoDto userInfo = new userInfoDto();
		 * userInfo.setUserName("sotpName");
		 * cacheService.setUserInfoTest("username", userInfo);
		 * System.out.println((userInfoDto)cacheService.getUserInfoTest(
		 * "username"));
		 */
		return hello;
	}

	@RequestMapping(value = "crypto")
	@ResponseBody
	public String crypto(@ModelAttribute UserRequestMsg requestMsg, HttpServletRequest request) {

		String reqmsg = request.getQueryString();
		String errMsg = "";

		int type = requestMsg.getType();
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String sotpId = requestMsg.getSotpId();
		String handledata = requestMsg.getDataInfo();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();

		if (type != Constant.SERVICE_TYPE.SOTP_CRYPTO_DATA_TYPE
		        && type != Constant.SERVICE_TYPE.SOTP_DECRYPTO_DATA_TYPE) {
			logger.error("type not right.");
			return retStrV2(appId, "", Constant.SERVICE_TYPE.SOTP_UNKOWN_TYPE, ErrorConstant.PARA_TYPE_UNKOWN, "",
			        "type not right.", null, reqmsg);
		}

		logger.debug("crypto parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId
		        + ", dataInfo:" + handledata);

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormat(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return retStrV2(appId, "", type, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg);
		}

		if (handledata == null || handledata.length() <= 0) {
			logger.error("parameter dataInfo not exist.");
			return retStrV2(appId, "", type, ErrorConstant.PARA_LEN_ERR, "", "parameter dataInfo not exist.", null,
			        reqmsg);
		}

		// 应用信息校验
		appVersionInfoDto appVersionInfo = paraHandle.getAppVersionInfoByAppId(appId);
		if (null == appVersionInfo) {
			errMsg = "appVersionInfo not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, "", type, ErrorConstant.SYSTEM_MALLOC_ERR, "", errMsg, null, reqmsg);
		}

		// 签名校验
		String appKey = appVersionInfo.getApp_key();
		Map<String, Object> paramMap = requestMsg.getSignParaMap();
		if (verifySign(appKey, paramMap, sign) != ErrorConstant.RET_OK) {
			errMsg = "sign not match.";
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_SIGN_NOTMATCH, "", errMsg, null, reqmsg);
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return retStrV2(appId, appKey, type, ErrorConstant.POLICY_FETCH_ERROR, "", errMsg, null, reqmsg);
		}

		// get plugin key
		Random random = new Random();
		int rand = random.nextInt() % 10;
		String pluginKey = pluginInfoMapper.selectPluginKeyByPluginIdAndStatus(rand, sotpId,
		        Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);

		if (pluginKey == null) {
			logger.error("parameter pluginId not exist or not ready.");
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_PLUGINID_NOEXIST_OR_NOREADY, "",
			        "DB pluginId not exist or not ready.", null, reqmsg);
		}

		// 连接密码机验证
		SotpRet sotpRes = null;
		switch (type) {
		case Constant.SERVICE_TYPE.SOTP_CRYPTO_DATA_TYPE:
			try {
				sotpRes = thriftInvokeService.sotpEncryptV2(sotpId, pluginKey, handledata);
			} catch (TException e) {
				logger.error("invoke thrift encryptNew error.msg:%s", e.toString());
			}
			break;
		case Constant.SERVICE_TYPE.SOTP_DECRYPTO_DATA_TYPE:
			try {
				sotpRes = thriftInvokeService.sotpDecryptV2(sotpId, pluginKey, handledata);
			} catch (TException e) {
				logger.error("invoke thrift decryptNew error.msg:%s", e.toString());
			}
			break;
		default:
			break;
		}

		if (sotpRes == null || sotpRes.status < 0) {
			logger.error("sotpthorReponse is null.");
			return retStrV2(appId, appKey, type, ErrorConstant.THOR_CONNECT_TIMEOUT, "",
			        "connect sotp_crypto_machine wrong.", null, reqmsg);
		}

		logger.info("sotp crypto plain:" + handledata + ", crypto:" + sotpRes.getTitle());
		// 正确
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return retStrV2(appId, appKey, type, ErrorConstant.RET_OK, sotpRes.getTitle(), "", null, reqmsg);
	}

	@RequestMapping(value = "verify")
	@ResponseBody
	public String verify(@ModelAttribute UserRequestMsg requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		int ret = ErrorConstant.RET_OK;
		String errMsg = "";

		int type = requestMsg.getType();
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String sotpId = requestMsg.getSotpId();
		String sotpCode = requestMsg.getSotpCode();
		String sotpCodePara = requestMsg.getSotpCodePara();
		String devInfo = requestMsg.getDevInfo();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();

		if (type != Constant.SERVICE_TYPE.SOTP_VERIFY_SOTPCODE_TYPE) {
			logger.error("parameter type not right. type:" + type);
			return retStrV2(appId, "", Constant.SERVICE_TYPE.SOTP_UNKOWN_TYPE, ErrorConstant.PARA_TYPE_UNKOWN, "",
			        "para type unkown.", null, reqmsg);
		}
		logger.debug("verify parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId
		        + ", sotpCode:" + sotpCode + ", sotpCodePara:" + sotpCodePara);

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormat(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return retStrV2(appId, "", type, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg);
		}

		appVersionInfoDto appVersionInfo = paraHandle.getAppVersionInfoByAppId(appId);
		if (null == appVersionInfo) {
			errMsg = "appVersionInfo not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, "", type, ErrorConstant.SYSTEM_MALLOC_ERR, "", errMsg, null, reqmsg);
		}

		// 签名校验
		String appKey = appVersionInfo.getApp_key();
		Map<String, Object> paramMap = requestMsg.getSignParaMap();
		if (verifySign(appKey, paramMap, sign) != ErrorConstant.RET_OK) {
			errMsg = "sign not match.";
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_SIGN_NOTMATCH, "", errMsg, null, reqmsg);
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return retStrV2(appId, appKey, type, ErrorConstant.POLICY_FETCH_ERROR, "", errMsg, null, reqmsg);
		}

		// 认证硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(sotpId);
		if (authFeaInfo == null) {
			errMsg = "authFeaInfo not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_DEVINFO_NOEXIST, "", errMsg, null, reqmsg);
		}

		// 插件信息表
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		if (plInfo == null) {
			errMsg = "pluginId not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", errMsg, null, reqmsg);
		}

		// 验证插件硬件信息是否匹配
		VerifyData vd = new VerifyData(cacheService);
		VerifyRet vr = vd.verifyDevInfo(plInfo, authFeaInfo, devInfo);
		boolean verifyDevInfo = vr.isOk();
		if (!verifyDevInfo) {
			errMsg = "devInfo is not right!" + vr.getMsg();
			logger.error(errMsg);
			this.pluginService.updatePluginInfoStatusById(sotpId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
			return retStrV2(appId, appKey, type, ErrorConstant.PLUGIN_USELESS, "", errMsg, null, reqmsg);
		}

		// 认证码 验证策略判断
		resultVO = policyService.codeVerifyPolicy(plInfo, appId, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return retStrV2(appId, appKey, type, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg);
		}

		// 插件状态：是否为就绪
		resultVO = policyService.pluginStatus(plInfo, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return retStrV2(appId, appKey, type, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg);
		}

		// 对 sotpCode 进行解密
		SotpRet sotpRes = null;
		try {
			sotpRes = thriftInvokeService.sotpDecryptV2(plInfo.getPluginId(), plInfo.getPluginKey(), sotpCode);
		} catch (TException e) {
			logger.error("invoke thrift decryptNew error.msg:%s", e.toString());
		}
		if (sotpRes == null || sotpRes.status < 0) {
			logger.error("sotpthorReponse is null.");
			return retStrV2(appId, appKey, type, ErrorConstant.THOR_CONNECT_TIMEOUT, "",
			        "connect sotp_crypto_machine wrong.", null, reqmsg);
		}
		String realSotpCode = sotpRes.getTitle();
		// 测试时获取的挑战码
		// sotpCodePara = "377891";
		// 验证身份码
		ret = verifSotpCode(plInfo, authFeaInfo, realSotpCode, sotpCodePara);
		if (ret != ErrorConstant.RET_OK) {
			errMsg = "verify sotp code error.";
		}

		// 更新 插件验证 计数
		// 在此转换一下的原因是，需要将具体的时分秒带过去
		java.util.Date date = new java.util.Date();
		String fmtDate = DateUtil.formatDate(date);
		java.util.Date newDate = DateUtil.getDateTimeByString(fmtDate);

		UpdateUtil updateInfo = new UpdateUtil();
		if (ret != ErrorConstant.RET_OK) { // 错误
			plInfo.setTotalErrcnt(plInfo.getTotalErrcnt() + 1);
			plInfo.setVerifyErrcnt(plInfo.getVerifyErrcnt() + 1);
			// 插件验证错误次数 +1
			updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.TOTAL_ERRCNT, plInfo.getTotalErrcnt());
			updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.VERIFY_ERRCNT, plInfo.getVerifyErrcnt());
			updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.ERR_DAY, newDate);

			int maxErrorTimesInPolicy = policyService.getMaxErrorTime(verifyPolicyInfo);
			if (maxErrorTimesInPolicy > 0 && plInfo.getVerifyErrcnt() >= maxErrorTimesInPolicy) {
				updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.STATUS,
				        Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_LOCKED);

				// 将已加锁插件id保存在redis中，将有定时任务来实现解锁，增加每个app的解锁策略
				int autoUnlockTime = policyService.getAutoUnlockTime(verifyPolicyInfo);
				int isUnlock = policyService.getIsUnlock(verifyPolicyInfo);
				if (isUnlock == Constant.AUTOUNLOCK.ALLOW) {
					// 允许自动解锁
					AutoUnlockPlugin lockedPlugin = new AutoUnlockPlugin(plInfo.getPluginId(), autoUnlockTime, appId,
					        isUnlock);
					try {
						this.cacheService.setPluginObjIntoList(Constant.REDIS_PLUGIN_ID_SAVE_LIST_NAME.HAS_LOCKED_LIST,
						        lockedPlugin);
					} catch (BusinessException e) {
						logger.error("save locked plugin exception. msg:%s[pluginId:%s]", e.toString(),
						        plInfo.getPluginId());
					}
				}
			}
		} else { // 正确
			updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.VERIFY_ERRCNT, 0);
		}
		updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.TOTAL_USECNT, 1 + plInfo.getTotalUsecnt());
		updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plInfo.getPluginId());
		Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
		this.pluginInfoMapper.updateHashMapByPluginId(updateMap);

		// 正确
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return retStrV2(appId, appKey, type, ret, "", errMsg, null, reqmsg);
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

		// challenge = devsplit[1] + "-" + devsplit[2] + "-" + devsplit[3] + "|"
		// + pin + "|" + chall;

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
	 * sotp身份码 验证策略判断 判断: 连续验证错误锁定次数 是否自动解锁 自动解锁时间
	 */
	public int verifypolicy(pluginInfoDto plInfo) {
		return 0;
	}

	/*
	 * 判断插件状态
	 */
	@RequestMapping(value = "getstatus")
	@ResponseBody
	public String getstatus(@ModelAttribute UserRequestMsg requestMsg, HttpServletRequest request) {

		int ret = ErrorConstant.RET_OK;
		String reqmsg = request.getQueryString();
		String errMsg = "";

		int type = requestMsg.getType();
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String sotpId = requestMsg.getSotpId();
		String sotpCode = requestMsg.getSotpCode();
		String devInfo = requestMsg.getDevInfo();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();

		if (type != Constant.SERVICE_TYPE.SOTP_GET_PLUGIN_STATUS) {
			logger.error("parameter type not right. type:" + type);
			return retStrV2(appId, "", Constant.SERVICE_TYPE.SOTP_UNKOWN_TYPE, ErrorConstant.PARA_TYPE_UNKOWN, "",
			        "para type unkown.", null, reqmsg);
		}

		logger.debug("getstatus parameter userId:" + userId + ", phoneNum:" + phoneNum + ", devInfo" + devInfo
		        + ", sotpId:" + sotpId + ", sotpCode:" + sotpCode);

		ResultVO resultVO = paraHandle.paraFormat(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return retStrV2(appId, "", type, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg);
		}

		appVersionInfoDto appVersionInfo = paraHandle.getAppVersionInfoByAppId(appId);
		if (null == appVersionInfo) {
			errMsg = "appVersionInfo not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, "", type, ErrorConstant.SYSTEM_MALLOC_ERR, "", errMsg, null, reqmsg);
		}

		// 签名校验
		String appKey = appVersionInfo.getApp_key();
		Map<String, Object> paramMap = requestMsg.getSignParaMap();
		if (verifySign(appKey, paramMap, sign) != ErrorConstant.RET_OK) {
			errMsg = "sign not match.";
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_SIGN_NOTMATCH, "", errMsg, null, reqmsg);
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return retStrV2(appId, appKey, type, ErrorConstant.POLICY_FETCH_ERROR, "", errMsg, null, reqmsg);
		}

		// 插件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		if (plInfo == null) {
			errMsg = "pluginId not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", errMsg, null, reqmsg);
		}

		// 认证硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(sotpId);
		if (authFeaInfo == null) {
			errMsg = "pluginId or authFeaInfo not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_DEVINFO_NOEXIST, "", errMsg, null, reqmsg);
		}

		// 验证插件硬件信息是否匹配
		VerifyData vd = new VerifyData(cacheService);
		VerifyRet vr = vd.verifyDevInfo(plInfo, authFeaInfo, devInfo);
		boolean verifyDevInfo = vr.isOk();
		if (!verifyDevInfo) {
			errMsg = "devInfo is not right or illegal mobile use plugin." + vr.getMsg();
			logger.error(errMsg);
			this.pluginService.updatePluginInfoStatusById(sotpId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
			return retStrV2(appId, appKey, type, ErrorConstant.PLUGIN_USELESS, "", errMsg, null, reqmsg);
		}

		// 插件状态：是否为就绪
		resultVO = policyService.pluginStatus(plInfo, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return retStrV2(appId, appKey, type, ret, "", resultVO.getMsg(), null, reqmsg);
		}

		// 连接密码机， 验证sotpCode(30)
		String challengeCode = (String) this.cacheService
		        .get(Constant.REDIS_CONSTANT.PLUGIN_USER_CHALLENGE_CODE + plInfo.getPluginId());

		if (challengeCode == null) {
			logger.error("challenge code time out!");
			return retStrV2(appId, appKey, type, ErrorConstant.PLUGIN_CHALLENGE_CODE_TIMEOUT, "",
			        "challenge code time out!", null, reqmsg);
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
			return retStrV2(appId, appKey, type, ErrorConstant.THOR_CONNECT_TIMEOUT, "",
			        "connect sotp_crypto_machine wrong.", null, reqmsg);
		}
		String realSotpCode = sotpRes.getTitle();

		ret = verifSotpCode(plInfo, authFeaInfo, realSotpCode, challengeCode);
		if (ret != ErrorConstant.RET_OK) {
			logger.error("getstatus verif challenge code error:", ret);
			return retStrV2(appId, appKey, type, ret, "", "getstatus verify challenge code error.", null, reqmsg);
		}

		// 更新策略 判断
		resultVO = policyService.updatePolicy(appId, plInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return retStrV2(appId, appKey, type, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg);
		}

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return retStrV2(appId, appKey, type, ErrorConstant.RET_OK, "", "", null, reqmsg);
	}

	/*
	 * 获取绑定设备列表 返回：devname@start_time@devId
	 */
	@RequestMapping(value = "getdevlist")
	@ResponseBody
	public String getdevlist(@ModelAttribute UserRequestMsg requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		String errMsg = "";

		int type = requestMsg.getType();
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();

		if (type != Constant.SERVICE_TYPE.SOTP_GET_BIND_DEVLIST) {
			logger.error("parameter type not right. type:" + type);
			return retStrV2(appId, "", Constant.SERVICE_TYPE.SOTP_UNKOWN_TYPE, ErrorConstant.PARA_TYPE_UNKOWN, "",
			        "para type unkown.", null, reqmsg);
		}
		logger.debug("getdevlist parameter userId:" + userId + ", phoneNum:" + phoneNum);

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormat(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return retStrV2(appId, "", type, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg);
		}

		// 应用信息校验
		appVersionInfoDto appVersionInfo = paraHandle.getAppVersionInfoByAppId(appId);
		if (null == appVersionInfo) {
			errMsg = "appVersionInfo not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, "", type, ErrorConstant.SYSTEM_MALLOC_ERR, "", errMsg, null, reqmsg);
		}

		// 签名校验
		String appKey = appVersionInfo.getApp_key();
		Map<String, Object> paramMap = requestMsg.getSignParaMap();
		if (verifySign(appKey, paramMap, sign) != ErrorConstant.RET_OK) {
			errMsg = "sign not match.";
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_SIGN_NOTMATCH, "", errMsg, null, reqmsg);
		}

		// 判断 SOTPID
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("status", Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);
		parms.put("phoneNum", phoneNum);

		StringBuilder devlist = null;
		List<String> hardwares = null;
		try {
			hardwares = authFeatureMapper.selectByPhoneNum(parms);
		} catch (SQLException e) {
			logger.error("authFeatureMapper selectByPhoneNum sql error.msg:%s", e.toString());
		}
		if (hardwares == null || hardwares.isEmpty()) {
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_PHONENUM_NOEXIST, "", "", null, reqmsg);
		}
		devlist = new StringBuilder(hardwares.get(0));
		for (int i = 1, len = hardwares.size(); i < len; i++) {
			devlist.append("&").append(hardwares.get(i));
		}
		logger.debug("devlist:" + devlist);

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return retStrV2(appId, appKey, type, ErrorConstant.RET_OK, devlist.toString(), "", null, reqmsg);
	}

	/*
	 * 请求挑战码(验证码)
	 */
	@RequestMapping(value = "getchallenge")
	@ResponseBody
	public String getchallenge(@ModelAttribute UserRequestMsg requestMsg, HttpServletRequest request) {

		String reqmsg = request.getQueryString();
		int ret = ErrorConstant.RET_OK;
		String errMsg = "";

		int type = requestMsg.getType();
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String sotpId = requestMsg.getSotpId();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();

		if (type != Constant.SERVICE_TYPE.SOTP_GET_CHALLENGE_CODE) {
			logger.error("parameter type not right. type:" + type);
			return retStrV2(appId, "", Constant.SERVICE_TYPE.SOTP_UNKOWN_TYPE, ErrorConstant.PARA_TYPE_UNKOWN, "",
			        "para type unkown.", null, reqmsg);
		}
		logger.info("getchallenge parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId);

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormat(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return retStrV2(appId, "", type, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg);
		}

		appVersionInfoDto appVersionInfo = paraHandle.getAppVersionInfoByAppId(appId);
		if (null == appVersionInfo) {
			errMsg = "appVersionInfo not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, "", type, ErrorConstant.SYSTEM_MALLOC_ERR, "", errMsg, null, reqmsg);
		}

		// 签名校验
		String appKey = appVersionInfo.getApp_key();
		Map<String, Object> paramMap = requestMsg.getSignParaMap();
		if (verifySign(appKey, paramMap, sign) != ErrorConstant.RET_OK) {
			errMsg = "sign not match.";
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_SIGN_NOTMATCH, "", errMsg, null, reqmsg);
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return retStrV2(appId, appKey, type, ErrorConstant.POLICY_FETCH_ERROR, "", errMsg, null, reqmsg);
		}

		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		if (plInfo == null) {
			logger.error("pluginId not exist");
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", "pluginId not exist",
			        null, reqmsg);
		}

		// 插件状态：是否为就绪
		resultVO = policyService.pluginStatus(plInfo, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return retStrV2(appId, appKey, type, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg);
		}

		// 内容判断
		if (!StringUtils.isEmpty(userId) && !userId.equals(plInfo.getBindUserid())) {
			logger.error("userId:" + userId + " !=" + plInfo.getBindUserid());
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_USERID_NOTMATCH_SID, "",
			        "DB userId  not match pluginId.", "", reqmsg);
		}

		if (!phoneNum.equals(plInfo.getBindUserphone())) {
			logger.error("phoneNum:" + phoneNum + " !=" + plInfo.getBindUserphone());
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_PNUM_NOTMATCH_SID, "",
			        "DB phoneNum  not match pluginId.", "", reqmsg);
		}

		// 生成挑战码
		int randNum = 0;
		String chalcode = "";
		Random rand = new Random();
		for (int i = 0; i < 6; i++) {
			randNum = rand.nextInt(10);
			chalcode += randNum;
		}
		logger.info(phoneNum + " getchallenge code:" + chalcode);

		// 连接密码机加密验证码
		String reponsetContent = "";
		String post_data = "";
		// http
		if (CONNECT_THOR_PROTOCOL == Constant.THOR_PROTOCOL.SOTP_CONNECT_THOR_PROTOCOL_HTTP) {
			post_data = "type=" + 1001 + "&seed=" + plInfo.getPluginKey() + "&data=" + chalcode;
			try {
				reponsetContent = HttpUtils.post(THOR_URL, post_data);
			} catch (Exception E) {
				logger.error("connect THOR error:" + THOR_URL);
			}
			logger.info("THOR_URL:" + THOR_URL + post_data);
			logger.info("sotp_crypto_machine response:" + reponsetContent);
		}

		// tcp sotpRes null, 连接超时
		if (CONNECT_THOR_PROTOCOL == Constant.THOR_PROTOCOL.SOTP_CONNECT_THOR_PROTOCOL_TCP) {
			SotpRet sotpRes = null;
			try {
				sotpRes = thriftInvokeService.sotpEncryptV2(plInfo.getPluginId(), plInfo.getPluginKey(), chalcode);
			} catch (TException e) {
				logger.error("invoke thrift encryptNew error.msg:%s", e.toString());
				return retStrV2(appId, appKey, type, ErrorConstant.THOR_CONNECT_TIMEOUT, "", "connect thor exception.",
				        null, reqmsg);
			}

			if (sotpRes == null) {
				logger.error("connect thor Reponse null.");
				return retStrV2(appId, appKey, type, ErrorConstant.THOR_CONNECT_TIMEOUT, "",
				        "connect thor Reponse null.", null, reqmsg);
			}

			if (sotpRes.status != ErrorConstant.THOR_RES_OK) {
				switch (sotpRes.status) {
				case ErrorConstant.THOR_RES_PARA_ERR:
					logger.error("THOR return error:" + sotpRes.status);
					ret = ErrorConstant.THOR_PARAFORMAT_ERR;
					break;
				case ErrorConstant.THOR_RES_SYSTEM_ERR:
					logger.error("THOR return error:" + sotpRes.status);
					ret = ErrorConstant.THOR_SYSTEM_ERR;
					break;
				case ErrorConstant.THOR_RES_BASE64_ERR:
					logger.error("THOR return error:" + sotpRes.status);
					ret = ErrorConstant.THOR_BASE64_ERR;
					break;
				case ErrorConstant.THOR_RES_MALLOC_ERR:
					logger.error("THOR return error:" + sotpRes.status);
					ret = ErrorConstant.THOR_MALLOC_ERR;
					break;
				default:
					logger.error("THOR return unkown error:" + ret);
					ret = ErrorConstant.THOR_RESP_UNKOWNERR;
				}
			}

			if (ret != ErrorConstant.RET_OK) {
				return retStrV2(appId, appKey, type, ret, "", "thor Reponse status error.", null, reqmsg);
			}

			reponsetContent = sotpRes.getTitle();
		}
		/*
		 * // 写数据库 UpdateUtil updateInfo = new UpdateUtil();
		 * updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.
		 * CHALLENGE_CODE, chalcode);
		 * updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.
		 * PLUGIN_ID, plInfo.getPluginId());
		 *
		 * Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
		 * this.pluginInfoMapper.updateHashMapByPluginId(updateMap);
		 */

		// 保存各用户的挑战码到redis中
		int challengeCodeTimeout = policyService.getChallengeCodeTimeOut(verifyPolicyInfo);
		if (challengeCodeTimeout <= 0) {
			// 若策略中未配置挑战码超时时间，则默认为60s
			challengeCodeTimeout = 60;
		}
		try {
			this.cacheService.set(Constant.REDIS_CONSTANT.PLUGIN_USER_CHALLENGE_CODE + plInfo.getPluginId(), chalcode,
			        challengeCodeTimeout);
		} catch (BusinessException e) {
			logger.error("save challenge code into redis error!");
			return retStrV2(appId, appKey, type, ErrorConstant.REDIS_OP_ERROR, "",
			        "save challenge code into redis error!", null, reqmsg);
		}

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return retStrV2(appId, appKey, type, ErrorConstant.RET_OK, reponsetContent, "", null, reqmsg);
	}

	/*
	 * 修改插件的保护码
	 */
	@RequestMapping(value = "modsotpin")
	@ResponseBody
	public String modsotpin(@ModelAttribute UserRequestMsg requestMsg, HttpServletRequest request) {

		int ret = ErrorConstant.RET_OK;
		String reqmsg = request.getQueryString();
		String errMsg = "";

		int type = requestMsg.getType();
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String sotpId = requestMsg.getSotpId();
		String devInfo = requestMsg.getDevInfo();
		String sotpCode = requestMsg.getSotpCode();
		String protect = requestMsg.getProtect();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();

		if (type != Constant.SERVICE_TYPE.SOTP_MOD_PROTECT_CODE) {
			logger.error("parameter type not right. type:" + type);
			return retStrV2(appId, "", Constant.SERVICE_TYPE.SOTP_UNKOWN_TYPE, ErrorConstant.PARA_TYPE_UNKOWN, "",
			        "para type unkown.", null, reqmsg);
		}

		logger.debug("modsotpin parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId
		        + ", sotpCode:" + sotpCode + ", protect" + protect);

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormat(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return retStrV2(appId, "", type, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg);
		}

		appVersionInfoDto appVersionInfo = paraHandle.getAppVersionInfoByAppId(appId);
		if (null == appVersionInfo) {
			errMsg = "appVersionInfo not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, "", type, ErrorConstant.SYSTEM_MALLOC_ERR, "", errMsg, null, reqmsg);
		}

		// 签名校验
		String appKey = appVersionInfo.getApp_key();
		Map<String, Object> paramMap = requestMsg.getSignParaMap();
		if (verifySign(appKey, paramMap, sign) != ErrorConstant.RET_OK) {
			errMsg = "sign not match.";
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_SIGN_NOTMATCH, "", errMsg, null, reqmsg);
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return retStrV2(appId, appKey, type, ErrorConstant.POLICY_FETCH_ERROR, "", errMsg, null, reqmsg);
		}

		// 插件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		if (plInfo == null) {
			errMsg = "pluginId not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", errMsg, null, reqmsg);
		}

		// 认证硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(sotpId);
		if (authFeaInfo == null) {
			errMsg = "authFeaInfo not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_DEVINFO_NOEXIST, "", errMsg, null, reqmsg);
		}

		// 验证插件硬件信息是否匹配
		VerifyData vd = new VerifyData(cacheService);
		VerifyRet vr = vd.verifyDevInfo(plInfo, authFeaInfo, devInfo);
		boolean verifyDevInfo = vr.isOk();
		if (!verifyDevInfo) {
			errMsg = "devInfo is not right!" + vr.getMsg();
			logger.error(errMsg);
			this.pluginService.updatePluginInfoStatusById(sotpId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
			return retStrV2(appId, appKey, type, ErrorConstant.PLUGIN_USELESS, "", errMsg, null, reqmsg);
		}

		// 插件状态：是否为就绪
		resultVO = policyService.pluginStatus(plInfo, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return retStrV2(appId, appKey, type, ret, "", resultVO.getMsg(), null, reqmsg);
		}

		// 对 sotpCode 进行解密
		SotpRet sotpRes = null;
		try {
			sotpRes = thriftInvokeService.sotpDecryptV2(plInfo.getPluginId(), plInfo.getPluginKey(), sotpCode);
		} catch (TException e) {
			logger.error("invoke thrift decryptNew error.msg:%s", e.toString());
		}
		if (sotpRes == null || sotpRes.status < 0) {
			logger.error("sotpthorReponse is null.");
			return retStrV2(appId, appKey, type, ErrorConstant.THOR_CONNECT_TIMEOUT, "",
			        "connect sotp_crypto_machine wrong.", null, reqmsg);
		}
		String realSotpCode = sotpRes.getTitle();

		// 连接密码机， 验证sotpCode(30)
		ret = verifSotpCode(plInfo, authFeaInfo, realSotpCode, plInfo.getProtectCode());
		if (ret != ErrorConstant.RET_OK) {
			logger.error("modsotpin verif challenge code error:", ret);
			return retStrV2(appId, appKey, type, ret, "", "modsotpin verif challenge code error.", null, reqmsg);
		}

		// 更新插件信息中保护码
		UpdateUtil updateInfo = new UpdateUtil();
		updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PROTECT_CODE, protect);
		updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plInfo.getPluginId());

		Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
		this.pluginInfoMapper.updateHashMapByPluginId(updateMap);

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return retStrV2(appId, appKey, type, ErrorConstant.RET_OK, "", "", null, reqmsg);
	}

	/*
	 * 修改预留信息
	 */
	@RequestMapping(value = "modholdinfo")
	@ResponseBody
	public String modholdinfo(@ModelAttribute UserRequestMsg requestMsg, HttpServletRequest request) {

		int ret = ErrorConstant.RET_OK;
		String reqmsg = request.getQueryString();
		String msg = "";
		String errMsg = "";

		int type = requestMsg.getType();
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String sotpId = requestMsg.getSotpId();
		String devInfo = requestMsg.getDevInfo();
		String action = requestMsg.getAction();
		String holdinfo = requestMsg.getHoldinfo();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();

		if (type != Constant.SERVICE_TYPE.SOTP_MOD_HOLD_INFO) {
			logger.error("parameter type not right. type:" + type);
			return retStrV2(appId, "", Constant.SERVICE_TYPE.SOTP_UNKOWN_TYPE, ErrorConstant.PARA_TYPE_UNKOWN, "",
			        "para type unkown.", null, reqmsg);
		}

		logger.debug("modholdinfo parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId
		        + ", holdinfo:" + holdinfo);

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormat(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return retStrV2(appId, "", type, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg);
		}

		appVersionInfoDto appVersionInfo = paraHandle.getAppVersionInfoByAppId(appId);
		if (null == appVersionInfo) {
			errMsg = "appVersionInfo not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, "", type, ErrorConstant.SYSTEM_MALLOC_ERR, "", errMsg, null, reqmsg);
		}

		// 签名校验
		String appKey = appVersionInfo.getApp_key();
		Map<String, Object> paramMap = requestMsg.getSignParaMap();
		if (verifySign(appKey, paramMap, sign) != ErrorConstant.RET_OK) {
			errMsg = "sign not match.";
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_SIGN_NOTMATCH, "", errMsg, null, reqmsg);
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return retStrV2(appId, appKey, type, ErrorConstant.POLICY_FETCH_ERROR, "", errMsg, null, reqmsg);
		}

		// 认证硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(sotpId);
		if (authFeaInfo == null) {
			errMsg = "authFeaInfo not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_DEVINFO_NOEXIST, "", errMsg, null, reqmsg);
		}

		// 插件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		if (plInfo == null) {
			errMsg = "pluginId not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", errMsg, null, reqmsg);
		}

		// 验证插件硬件信息是否匹配
		VerifyData vd = new VerifyData(cacheService);
		VerifyRet vr = vd.verifyDevInfo(plInfo, authFeaInfo, devInfo);
		boolean verifyDevInfo = vr.isOk();
		if (!verifyDevInfo) {
			errMsg = "devInfo is not right or illegal mobile use plugin." + vr.getMsg();
			logger.error(errMsg);
			this.pluginService.updatePluginInfoStatusById(sotpId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
			return retStrV2(appId, appKey, type, ErrorConstant.PLUGIN_USELESS, "", errMsg, null, reqmsg);
		}

		// 插件状态：是否为就绪
		resultVO = policyService.pluginStatus(plInfo, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return retStrV2(appId, appKey, type, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg);
		}

		if (action.equals("1")) { // get 预留信息
			msg = plInfo.getHoldInfo();
		} else if (action.equals("2")) { // mod
			UpdateUtil updateInfo = new UpdateUtil();
			updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.HOLD_INFO, holdinfo);
			updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plInfo.getPluginId());

			Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
			this.pluginInfoMapper.updateHashMapByPluginId(updateMap);
		} else {
			logger.error("para action not right:" + action);
			return retStrV2(appId, appKey, type, ErrorConstant.PARA_ILLEGAL, "", "para action not right", null, reqmsg);
		}

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return retStrV2(appId, appKey, type, ret, msg, "", null, reqmsg);
	}

	/*
	 * 修改插件的pin码
	 */
	@RequestMapping(value = "setpin")
	@ResponseBody
	public String setpin(@ModelAttribute UserRequestMsg requestMsg, HttpServletRequest request) {

		String reqmsg = request.getQueryString();
		String errMsg = "";

		int type = requestMsg.getType();
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String sotpId = requestMsg.getSotpId();
		String pin = requestMsg.getPin();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();

		if (type != Constant.SERVICE_TYPE.SOTP_SET_PIN) {
			logger.error("parameter type not right. type:" + type);
			return retStrV2(appId, "", Constant.SERVICE_TYPE.SOTP_UNKOWN_TYPE, ErrorConstant.PARA_TYPE_UNKOWN, "",
			        "para type unkown.", null, reqmsg);
		}

		logger.debug("modsotpin parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId + ", pin"
		        + pin);

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormat(reqmsg, type);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return retStrV2(appId, "", type, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg);
		}

		appVersionInfoDto appVersionInfo = paraHandle.getAppVersionInfoByAppId(appId);
		if (null == appVersionInfo) {
			errMsg = "appVersionInfo not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, "", type, ErrorConstant.SYSTEM_MALLOC_ERR, "", errMsg, null, reqmsg);
		}

		// 签名校验
		String appKey = appVersionInfo.getApp_key();
		Map<String, Object> paramMap = requestMsg.getSignParaMap();
		if (verifySign(appKey, paramMap, sign) != ErrorConstant.RET_OK) {
			errMsg = "sign not match.";
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_SIGN_NOTMATCH, "", errMsg, null, reqmsg);
		}

		// 插件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		if (plInfo == null) {
			errMsg = "pluginId not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", errMsg, null, reqmsg);
		}

		// 认证硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(sotpId);
		if (authFeaInfo == null) {
			errMsg = "authFeaInfo not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_DEVINFO_NOEXIST, "", errMsg, null, reqmsg);
		}

		// 更新插件信息中保护码
		UpdateUtil updateInfo = new UpdateUtil();
		updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PROTECT_CODE, pin);
		updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plInfo.getPluginId());

		Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
		this.pluginInfoMapper.updateHashMapByPluginId(updateMap);
		// this .pluginInfoMapper.updatePluginProtectByPluginId(protect,sotpId);

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return retStrV2(appId, appKey, type, ErrorConstant.RET_OK, "", "", null, reqmsg);
	}

	/*
	 * 封装 json 返回数据
	 */
	public String retStr(int type, int status, String data, String errMsg, pluginInfoDto plInfo, String reqmsg) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String timeinfo = dateFormat.format(System.currentTimeMillis());

		Map<String, Object> message = new HashMap<String, Object>();
		if (data != null && data.length() > 0) {
			message.put("data", data);
		}
		if (status != ErrorConstant.RET_OK) {
			if (errMsg != null && errMsg.length() > 0) {
				message.put("errorMsg", errMsg);
			}
		}

		String jsonResStr = ControllerHelper.getResultJsonString(type, status, message, timeinfo);

		// 记录业务日志
		this.businessService.setBusinessLog(reqmsg, jsonResStr, status, errMsg);

		logger.debug("sotpAuth response:" + jsonResStr);
		return jsonResStr;
	}

	/*
	 * 返回值加入appId、nonce_str、sign
	 */
	public String retStrV2(String appId, String appKey, int type, int status, String pluginContent, String errMsg,
	        String hwInfo, String reqmsg) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String timeinfo = dateFormat.format(System.currentTimeMillis());

		// TODO 生成随机数字符串
		Random rad = new Random();
		String nonce_str = rad.nextInt(1000) + "";

		Map<String, Object> message = new HashMap<String, Object>();
		if (pluginContent != null && pluginContent.length() > 0) {
			message.put("data", pluginContent);
		}
		if (status != ErrorConstant.RET_OK) {
			if (errMsg != null && errMsg.length() > 0) {
				message.put("errorMsg", errMsg);
			}
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

	@RequestMapping(value = "system", method = RequestMethod.GET)
	public ModelAndView getSystemInfo() {
		ModelAndView view = new ModelAndView();
		view.setViewName("system");
		return view;
	}

	/*
	 * 生成会话密钥： session_key
	 */
	@RequestMapping(value = "gensesskey")
	@ResponseBody
	public String gensesskey(@ModelAttribute UserRequestMsg requestMsg, HttpServletRequest request) {

		String reqmsg = request.getQueryString();
		String errMsg = "";

		int type = requestMsg.getType();
		String randa = requestMsg.getRanda();
		String randb = requestMsg.getRandb();
		String sotpId = requestMsg.getSotpId();
		String datainfo = requestMsg.getDataInfo();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();

		logger.info("gensesskey parameter randa:" + randa + ", randb:" + randb + ", sotpId:" + sotpId + ", datainfo:"
		        + datainfo);

		appVersionInfoDto appVersionInfo = paraHandle.getAppVersionInfoByAppId(appId);
		if (null == appVersionInfo) {
			errMsg = "appVersionInfo not exist db.";
			logger.error(errMsg);
			return retStrV2(appId, "", type, ErrorConstant.SYSTEM_MALLOC_ERR, "", errMsg, null, reqmsg);
		}

		// 签名校验
		String appKey = appVersionInfo.getApp_key();
		Map<String, Object> paramMap = requestMsg.getSignParaMap();
		if (verifySign(appKey, paramMap, sign) != ErrorConstant.RET_OK) {
			errMsg = "sign not match.";
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_SIGN_NOTMATCH, "", errMsg, null, reqmsg);
		}

		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);

		// 参数判断
		/*
		 * ResultVO resultVO = paraHandle.paraFormat(reqmsg, type); if (null !=
		 * resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
		 * logger.error(resultVO.getMsg()); return retStr(type,
		 * resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg); }
		 */
		// get plugin key
		Random random = new Random();
		int rand = random.nextInt() % 10;
		String pluginKey = pluginInfoMapper.selectPluginKeyByPluginIdAndStatus(rand, sotpId,
		        Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);
		if (pluginKey == null) {
			logger.error("parameter pluginId not exist or not ready.");
			return retStrV2(appId, appKey, type, ErrorConstant.PARACONT_PLUGINID_NOEXIST_OR_NOREADY, "",
			        "DB pluginId not exist or not ready.", null, reqmsg);
		}

		// 连接密码机验证
		SotpRet sotpRes = null;
		try {

			pluginKey = plInfo.getPluginKey() + ":" + randa + ":" + randb;
			logger.debug("**********生成会话密钥   sotpId:" + sotpId + ", seed:" + ", data:" + datainfo);

			sotpRes = thriftInvokeService.sotpDecryptV2(plInfo.getPluginId(), pluginKey, datainfo);

			logger.debug("**********会话密钥   session key:" + sotpRes.getTitle());

		} catch (TException e) {
			logger.error("invoke thrift decryptNew error.msg:%s", e.toString());
		}

		if (sotpRes == null || sotpRes.status < 0) {
			logger.error("sotpthorReponse is null.");
			return retStrV2(appId, appKey, type, ErrorConstant.THOR_CONNECT_TIMEOUT, "",
			        "connect sotp_crypto_machine wrong.", null, reqmsg);
		}

		// 正确
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return retStrV2(appId, appKey, type, ErrorConstant.RET_OK, sotpRes.getTitle(), "", null, reqmsg);
	}
}
