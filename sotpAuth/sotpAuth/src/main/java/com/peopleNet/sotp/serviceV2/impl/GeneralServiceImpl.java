package com.peopleNet.sotp.serviceV2.impl;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.constant.ServiceConstant;
import com.peopleNet.sotp.controller.ControllerHelper;
import com.peopleNet.sotp.dal.model.RulePolicyDto;
import com.peopleNet.sotp.dal.model.appVersionInfoDto;
import com.peopleNet.sotp.dal.model.authFeatureDto;
import com.peopleNet.sotp.dal.model.pluginInfoDto;
import com.peopleNet.sotp.exception.BusinessException;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.IBusinessService;
import com.peopleNet.sotp.service.ICacheService;
import com.peopleNet.sotp.service.IEncryptPluginInfoService;
import com.peopleNet.sotp.service.IParaHandle;
import com.peopleNet.sotp.service.IPluginService;
import com.peopleNet.sotp.service.IPolicyService;
import com.peopleNet.sotp.service.IRuleService;
import com.peopleNet.sotp.service.IThriftInvokeService;
import com.peopleNet.sotp.service.IVerifyService;
import com.peopleNet.sotp.serviceV2.IGeneralService;
import com.peopleNet.sotp.thrift.service.SotpRet;
import com.peopleNet.sotp.util.ByteConvert;
import com.peopleNet.sotp.util.CommonConfig;
import com.peopleNet.sotp.util.DateUtil;
import com.peopleNet.sotp.util.SMS4;
import com.peopleNet.sotp.util.UpdateUtil;
import com.peopleNet.sotp.util.Util;
import com.peopleNet.sotp.vo.AutoUnlockPlugin;
import com.peopleNet.sotp.vo.ResultVO;
import com.peopleNet.sotp.vo.UserRequestMsgV2;

import net.sf.json.JSONObject;

@Service
public class GeneralServiceImpl implements IGeneralService {
	private static LogUtil logger = LogUtil.getLogger(PluginMagServiceImpl.class);
	// session参数
	private final String SOTP_PLUGIN_ID = "sotp_plugin_id";
	private final String SOTP_CLIENT_R = "sotp_client_random";
	private final String SOTP_SERVER_R = "sotp_server_random";
	private final String SOTP_KEY = "sotp_key";
	private String saveSessionKey = CommonConfig.get("SAVE_SESSIONKEY");
	@Autowired
	private IEncryptPluginInfoService pluginInfoMapper;
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
	private IVerifyService verifyService;
	@Autowired
	private IRuleService ruleService;
	private String THOR_URL = CommonConfig.get("THOR_URL");
	private int CONNECT_THOR_PROTOCOL = Integer.parseInt(CommonConfig.get("CONNECT_THOR_PROTOCOL").trim());
	private String useRuleEngine = CommonConfig.get("useRuleEngine");

	/*
	 * 请求挑战码(验证码)
	 */
	@Override
	public String getchallenge(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		int ret = ErrorConstant.RET_OK;
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String sotpId = requestMsg.getSotpId();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();

		if (!ServiceConstant.BUSINESS_GETCHALLENGE.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request,sotpId);
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, sotpId);
		}

		// 插件信息判断
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		if (plInfo == null) {
			errMsg = "sotpId db not exist";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", "", errMsg,
			        reqmsg, request, sotpId);
		}

		/*
		 * resultVO = verifyService.verifyPluginStatusIsReady(plInfo); if (null
		 * != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
		 * logger.error(resultVO.getMsg()); return
		 * paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "",
		 * resultVO.getMsg(), reqmsg, request, sotpId); }
		 */
		if (plInfo.getStatus() == Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS) {
			errMsg = "plugin status : 6";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PLUGIN_USELESS, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		resultVO = verifyService.verifyUserInfo(plInfo, phoneNum);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		// 生成挑战码
		String chalcode = businessService.generateChallenge();
		logger.info("phoneNum:" + phoneNum + " getchallenge code:" + chalcode);

		// 连接密码机加密验证码
		String reponsetContent = businessService.encrypt(plInfo, chalcode);
		if (null == reponsetContent) {
			errMsg = "encrypt challenge code failed! code = " + chalcode;
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.THOR_ENCRYPT_ERROR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		boolean saveChallengeStatus = businessService.saveChallengeIntoCache(appId, plInfo, chalcode, verifyPolicyInfo);
		if (!saveChallengeStatus) {
			errMsg = "save challenge code into redis error!";
			return paraHandle.responseHandle(appId, service, ErrorConstant.REDIS_OP_ERROR, "", null, errMsg, reqmsg,
			        request, sotpId);
		}

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, reponsetContent, null, "", reqmsg,
		        request, sotpId);
	}

	/*
	 * 安全认证
	 */
	@Override
	public String verify(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		int ret = ErrorConstant.RET_OK;
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String sotpId = requestMsg.getSotpId();
		String sotpCode = requestMsg.getSotpCode();
		String sotpCodePara = requestMsg.getSotpCodePara();
		String envInfo = requestMsg.getEnvInfo();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();
		String useCount = requestMsg.getUseCount();

		if (!ServiceConstant.BUSINESS_AUTH.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, sotpId);
		}

		// 从envInfo中获取isRoot
		String isRoot = paraHandle.getIsRootFromDevInfo(envInfo);
		// 已经root的设备验证策略是否允许
		if (Constant.ISROOT.ISROOT.equals(isRoot)) {
			resultVO = policyService.checkRootPolicy(verifyPolicyInfo);
			if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
				logger.error(resultVO.getMsg());
				return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
				        request, sotpId);
			}
		}

		// 插件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		// 硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(sotpId);
		if (null == plInfo || null == authFeaInfo) {
			errMsg = "pluginInfo or authFeaInfo is not exist in db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST, "", "",
			        errMsg, reqmsg, request, sotpId);
		}

		// 插件使用计数检查，防拷贝
		if (useCount != null && useCount != "") {
			if (plInfo.getUseCount() == null) {
				errMsg = "plugin get useCount error.";
				logger.error(errMsg);
				return paraHandle.responseHandle(appId, service, ErrorConstant.SQL_READMYSQL_ERR, "", "", errMsg,
				        reqmsg, request, sotpId);
			}
			if (plInfo.getUseCount() >= Integer.parseInt(useCount)) {
				this.pluginService.updatePluginInfoStatusById(sotpId,
				        Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
				errMsg = "plugin has been copied";
				logger.error(errMsg);
				return paraHandle.responseHandle(appId, service, ErrorConstant.PLUGIN_HAVE_BEEN_COPIED, "", "", errMsg,
				        reqmsg, request, sotpId);
			}
			// TODO :待优化[尽量只更新一次]
			// 未拷贝，插件使用次数+1
			UpdateUtil updateUseCountInfo = new UpdateUtil();
			updateUseCountInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.USE_COUNT, Integer.parseInt(useCount));
			updateUseCountInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plInfo.getPluginId());
			Map<String, Object> updateUseCountMap = updateUseCountInfo.getUpdateInfomap();
			this.pluginInfoMapper.updateHashMapByPluginId(updateUseCountMap);
		}

		// 验证设备\插件\app\用户的对应关系及插件状态
		resultVO = verifyService.verifyPluginStatusIsReady(plInfo, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}
		resultVO = verifyService.verifyAll(service, plInfo, authFeaInfo, envInfo, phoneNum, appId, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			this.pluginService.updatePluginInfoStatusById(sotpId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		// 认证码 验证策略判断
		resultVO = policyService.codeVerifyPolicy(plInfo, appId, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		// 验证sotpCode
		ret = businessService.verifySotpCode(plInfo, authFeaInfo, sotpCode, true, sotpCodePara, verifyPolicyInfo);
		if (ret != ErrorConstant.RET_OK) {
			errMsg = "verifySotpCode error. result:" + ret;
			logger.error(errMsg);
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

		return paraHandle.responseHandle(appId, service, ret, "", null, errMsg, reqmsg, request, sotpId);
	}

	/**
	 * 
	 * 生成服务器认证消息，协商会话秘钥第一步
	 */
	@Override
	public String sotpServerAuth(UserRequestMsgV2 requestMsg, HttpServletRequest request) {

		// type 1301 chello 存session
		int ret = ErrorConstant.RET_OK;
		String reqmsg = request.getQueryString();
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String sotpId = requestMsg.getSotpId();
		String chello = requestMsg.getChello();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();

		if (!ServiceConstant.BUSINESS_NEGOTIATESESSIONKEYS1.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		HttpSession session = request.getSession();

		// 生成随机数
		String srvR = Long.toString(System.currentTimeMillis());

		session.setAttribute(SOTP_PLUGIN_ID, sotpId);
		session.setAttribute(SOTP_CLIENT_R, chello);
		session.setAttribute(SOTP_SERVER_R, srvR);

		// 正确 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ret);

		return paraHandle.responseHandle(appId, service, ret, srvR, null, "", reqmsg, request, sotpId);
	}

	/**
	 * 
	 * 验证客户端认证消息，协商会话秘钥第二步
	 * 
	 */
	@Override
	public String sotpVerifyClient(UserRequestMsgV2 requestMsg, HttpServletRequest request) {

		int ret = ErrorConstant.RET_OK;
		String reqmsg = request.getQueryString();
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String sotpId = requestMsg.getSotpId();
		String cauth = requestMsg.getCauth();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();

		if (!ServiceConstant.BUSINESS_NEGOTIATESESSIONKEYS2.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);

		}

		// 客户端、服务端随机数
		HttpSession session = request.getSession();
		Object ctime_ = session.getAttribute(SOTP_CLIENT_R);
		Object srand_ = session.getAttribute(SOTP_SERVER_R);
		if (ctime_ == null) {
			return paraHandle.responseHandle(appId, service, -1, "", null,
			        "sotpVerifyClient can't get SOTP_CLIENT_R from session.", reqmsg, request, sotpId);
		}
		if (srand_ == null) {
			return paraHandle.responseHandle(appId, service, -1, "", null,
			        "sotpVerifyClient can't get SOTP_SERVER_R from session.", reqmsg, request, sotpId);
		}
		String ctime = ctime_.toString();
		String srand = srand_.toString();

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		// get plugin key
		Random random = new Random();
		int rand = random.nextInt() % 10;
		String pluginKey = pluginInfoMapper.selectPluginKeyByPluginIdAndStatus(rand, sotpId,
		        Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);
		if (pluginKey == null) {
			logger.error("parameter pluginId not exist or not ready.");
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGINID_NOEXIST_OR_NOREADY, "",
			        null, "DB pluginId not exist or not ready.", reqmsg, request, sotpId);
		}

		// 连接密码机验证
		SotpRet sotpRes = null;
		try {
			String seed = pluginKey + ":" + ctime + ":" + srand;
			sotpRes = thriftInvokeService.sotpDecryptV2(sotpId, seed, cauth);
		} catch (TException e) {
			logger.error("invoke thrift encryptNew error.msg:%s", e.toString());
		}
		if (sotpRes == null || sotpRes.status < 0) {
			logger.error("sotpthorReponse is null.");
			return paraHandle.responseHandle(appId, service, ErrorConstant.THOR_CONNECT_TIMEOUT, "", null,
			        "connect sotp_crypto_machine wrong.", reqmsg, request, sotpId);
		}
		String sessionKey = sotpRes.getTitle();
		logger.info("sotp decrypt crypto:" + cauth + ", plain:" + sessionKey);
		// 返回结果 存session
		session.setAttribute(SOTP_KEY, sessionKey);

		// 把 ctime 加密返回
		try {
			sotpRes = thriftInvokeService.sotpEncryptV2(sotpId, pluginKey, ctime);
		} catch (TException e) {
			logger.error("invoke thrift encryptNew error.msg:%s", e.toString());
		}
		String crypto = sotpRes.getTitle();
		logger.info("sotp crypto plain:" + ctime + ", crypto:" + crypto);
		// 正确 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ret);
		return paraHandle.responseHandle(appId, service, ret, crypto, null, "", reqmsg, request, sotpId);
	}

	/*
	 * 加解密数据
	 */
	@Override
	public String crypto(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String sotpId = requestMsg.getSotpId();
		String handledata = requestMsg.getDataInfo();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();

		if (!ServiceConstant.BUSINESS_DATADECRYPTION.equals(service)
		        && !ServiceConstant.BUSINESS_DATAENCRYPTION.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		logger.debug("crypto parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId
		        + ", dataInfo:" + handledata);

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}
		// TODO: 放到参数检查中
		if (handledata == null || handledata.length() <= 0) {
			logger.error("parameter dataInfo not exist.");
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_LEN_ERR, "", null,
			        "parameter dataInfo not exist.", reqmsg, request, sotpId);
		}

		// 插件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		if (null == plInfo) {
			errMsg = "pluginInfo is not exist in db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST, "", "",
			        errMsg, reqmsg, request, sotpId);
		}

		String result = null;
		if (ServiceConstant.BUSINESS_DATAENCRYPTION.equals(service)) {
			result = businessService.encrypt(plInfo, handledata);
		}
		if (ServiceConstant.BUSINESS_DATADECRYPTION.equals(service)) {
			result = businessService.decrypt(plInfo, handledata);
		}
		if (null == result) {
			errMsg = "invoke thor failed!";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.THOR_CONNECT_TIMEOUT, "", null, errMsg,
			        reqmsg, request, sotpId);
		}

		// 正确
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, result, null, "", reqmsg, request, sotpId);
	}

	/*
	 * 密码键盘解密
	 */
	public String decrypt(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		String errMsg = "";

		String appId = requestMsg.getAppId();
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		String sotpId = requestMsg.getSotpId();
		String dataInfo = requestMsg.getDataInfo();
		String service = requestMsg.getService();

		logger.debug("password keyboard decrypt parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:"
		        + sotpId + ", dataInfo:" + dataInfo);

		// 放到参数检查中
		if (dataInfo == null || dataInfo.length() <= 0) {
			errMsg = "parameter dataInfo not exist.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_LEN_ERR, "", null, errMsg, reqmsg,
			        request, sotpId);
		}

		// 解析dataInfo
		String cryptA = dataInfo.split("\\|")[0];
		String keyB = dataInfo.split("\\|")[1];
		if (StringUtils.isEmpty(cryptA) || StringUtils.isEmpty(keyB)) {
			errMsg = "cryptA or keyB is null ";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_ILLEGAL, "", null, errMsg, reqmsg,
			        request, sotpId);
		}
		logger.debug("cryptA :" + cryptA + "    seedB:" + keyB);

		// 插件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		if (null == plInfo) {
			errMsg = "pluginInfo is not exist in db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST, "", "",
			        errMsg, reqmsg, request, sotpId);
		}

		// 将密钥B送到密码机解密，得到密钥原文
		String resultB = businessService.decrypt(plInfo, keyB);
		if (null == resultB) {
			errMsg = "invoke thor failed!";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.THOR_CONNECT_TIMEOUT, "", null, errMsg,
			        reqmsg, request, sotpId);
		}

		// 使用SM4算法，将解析密钥B得到原文作为密钥， 解密A
		SMS4 sMS4 = new SMS4();
		logger.debug("--------------->cryptA:"+cryptA);
		byte[] bcryPtA = Base64.decodeBase64(cryptA.getBytes());
		logger.debug("bcryPtA:"+ByteConvert.bytesToHexString(bcryPtA)+"=======resultB:"+ByteConvert.bytesToHexString(bcryPtA).length());
		byte[] resultA = sMS4.decrypt(bcryPtA, resultB.getBytes(), 0, null);
		StringBuffer result = new StringBuffer();
		int num = resultA.length;
		char[] tChars = new char[num];
		for (int i = 0; i < num; i++) {
			tChars[i] = (char) resultA[i];
		}
		result.append(tChars);
		// 正确
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		String resultStr = result.toString();
		if(!StringUtils.isEmpty(resultStr)){
			if(resultStr.contains("\u0000")){
				resultStr = resultStr.trim();
  			}
		}
		
		String password = resultStr.split("\\|")[0];
		String useCount = resultStr.split("\\|")[1];
		
		// 插件使用计数检查
		if (useCount != null && useCount != "") {
			if (plInfo.getUseCount() == null) {
				errMsg = "plugin get useCount error.";
				logger.error(errMsg);
				return paraHandle.responseHandle(appId, service, ErrorConstant.SQL_READMYSQL_ERR, "", "", errMsg,
				        reqmsg, request, sotpId);
			}
			
			logger.debug("plInfo.getUseCount()="+plInfo.getUseCount()+"<==============>useCount="+useCount);
			
			if (plInfo.getUseCount() >= Integer.parseInt(useCount)) {
				errMsg = "keyboard decrypt decrypt failure";
				logger.error(errMsg);
				return paraHandle.responseHandle(appId, service, ErrorConstant.KEYBOARDDECRYPTION_ERROR, "", "", errMsg,
				        reqmsg, request, sotpId);
			}
			
			//插件使用次数+1
			UpdateUtil updateUseCountInfo = new UpdateUtil();
			updateUseCountInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.USE_COUNT, Integer.parseInt(useCount));
			updateUseCountInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plInfo.getPluginId());
			Map<String, Object> updateUseCountMap = updateUseCountInfo.getUpdateInfomap();
			this.pluginInfoMapper.updateHashMapByPluginId(updateUseCountMap);
		}
		
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK,password, null, "",
		        reqmsg, request, sotpId);
	}
	
	
	/*
	 *验证APP合法性  applegitimacy 
	 */
	
	@Override
	public String verifyapplegitimacy(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		int ret = ErrorConstant.RET_OK;
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String sotpId = requestMsg.getSotpId();
		String appId = requestMsg.getAppId();
		String sotpCode = requestMsg.getSotpCode();
		String service = requestMsg.getService();

		
		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		// 获取app信息
		appVersionInfoDto appVersionInfo = cacheService.getAPPVersionInfoByCode(appId);
		if (null == appVersionInfo) {
			errMsg = "no APPInfo or can not get APPInfo.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ret, "", null, errMsg, reqmsg, request, sotpId);
		}
		
		String appHashValue = appVersionInfo.getHash_value();
		String appSignature = appVersionInfo.getSignature();
		
		if(StringUtils.isEmpty(appHashValue)|| StringUtils.isEmpty(appSignature)){
			errMsg = "appHashValue or appSignature is null ";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ret, "", null, errMsg, reqmsg, request, sotpId);
		}
		
		logger.debug("appHashValue="+appHashValue+"---------appSignature="+appSignature);

		// 插件信息判断
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		if (plInfo == null) {
			errMsg = "sotpId db not exist";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", "", errMsg,
			        reqmsg, request, sotpId);
		}
	 
		if (plInfo.getStatus() == Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS) {
			errMsg = "plugin status : 6";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PLUGIN_USELESS, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		resultVO = verifyService.verifyUserInfo(plInfo, phoneNum);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}
		
		// 硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(sotpId);
		if (null == plInfo || null == authFeaInfo) {
			errMsg = "pluginInfo or authFeaInfo is not exist in db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST, "", "",
			        errMsg, reqmsg, request, sotpId);
		}
		
		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, sotpId);
		}
		
		sotpCode = "30@"+sotpCode;
		String challenge = businessService.getChallengeCodeAndClear(plInfo);
		String sotpCodePara =challenge+"_"+appHashValue;
		
		logger.debug("sotpCode:"+sotpCode+"-----sotpCodePara:"+sotpCodePara);
		// 验证sotpCode
		ret = businessService.verifySotpCode(plInfo, authFeaInfo, sotpCode, false, sotpCodePara, verifyPolicyInfo);
		if (ret != ErrorConstant.RET_OK) {
			errMsg = "verifySotpCode error. result:" + ret;
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, sotpId);
		}

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, "", null, "success", reqmsg,
		        request, sotpId);
	}

	/*
	 * 生成会话密钥： session_key
	 */
	@Override
	public String gensesskey(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		String errMsg = "";

		String randa = requestMsg.getRanda();
		String randb = requestMsg.getRandb();
		String sotpId = requestMsg.getSotpId();
		String datainfo = requestMsg.getDataInfo();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();
		String envInfo = requestMsg.getEnvInfo();

		logger.info("gensesskey parameter randa:" + randa + ", randb:" + randb + ", sotpId:" + sotpId + ", datainfo:"
		        + datainfo);

		appVersionInfoDto appVersionInfo = paraHandle.getAppVersionInfoByAppId(appId);
		if (null == appVersionInfo) {
			errMsg = "appVersionInfo not exist db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.SYSTEM_MALLOC_ERR, "", null, errMsg, reqmsg,
			        request, sotpId);
		}

		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		if (plInfo == null) {
			errMsg = "sotpId  db not exist";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", "", errMsg,
			        reqmsg, request, sotpId);
		}
		String devJson = com.peopleNet.sotp.util.Base64.decode(envInfo);
		JSONObject jsonObject = ControllerHelper.parseJsonString(devJson);
		if (null == jsonObject) {
			errMsg = "dev format wrong";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_ILLEGAL, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, sotpId);
		}

		ResultVO resultVO = verifyService.verifyAppInfo(service, plInfo, envInfo, appId, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		// get plugin key
		Random random = new Random();
		int rand = random.nextInt() % 10;
		String pluginKey = pluginInfoMapper.selectPluginKeyByPluginIdAndStatus(rand, sotpId,
		        Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);
		if (pluginKey == null) {
			logger.error("parameter pluginId not exist or not ready.");
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGINID_NOEXIST_OR_NOREADY, "",
			        null, "DB pluginId not exist or not ready.", reqmsg, request, sotpId);
		}

		// 连接密码机验证
		SotpRet sotpRes = null;
		try {
			pluginKey = plInfo.getPluginKey() + ":" + randa + ":" + randb;
			logger.debug("**********生成会话密钥   sotpId:" + sotpId + ", seed:" + pluginKey + ", data:" + datainfo);

			sotpRes = thriftInvokeService.sotpDecryptV2(plInfo.getPluginId(), pluginKey, datainfo);
			logger.debug("**********会话密钥   session key:" + sotpRes.getTitle());
		} catch (TException e) {
			logger.error("invoke thrift decryptNew error.msg:%s", e.toString());
		}

		if (sotpRes == null || sotpRes.status < 0) {
			logger.error("sotpthorReponse is null.");
			return paraHandle.responseHandle(appId, service, ErrorConstant.THOR_CONNECT_TIMEOUT, "", null,
			        "connect sotp_crypto_machine wrong.", reqmsg, request, sotpId);
		}
		
		String sessionKey = sotpRes.getTitle();
		logger.debug("sessionKey : " + sessionKey + "redisKey : " + plInfo.getPluginId());
		logger.debug("sessionKey :" +  Util.toHexString(sessionKey.getBytes()));
		// 保存会话密钥到缓存
		if (!StringUtils.isEmpty(saveSessionKey) && "true".equals(saveSessionKey)) {
			logger.debug("save sessionKey true!-----");
			boolean saveSessionKeyStatus = businessService.saveSessionKeyIntoCache(appId, plInfo, sessionKey);
			if (!saveSessionKeyStatus) {
				errMsg = "save sessionKey into redis error!";
				return paraHandle.responseHandle(appId, service, ErrorConstant.REDIS_OP_ERROR, "", null, errMsg, reqmsg,
				        request, sotpId);
			}
		}
		// 正确
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, sessionKey, null, "", reqmsg,
		        request, sotpId);

	}

	/*
	 * 修改插件的保护码
	 */
	@Override
	public String modsotpin(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		int ret = ErrorConstant.RET_OK;
		String reqmsg = request.getQueryString();
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String sotpId = requestMsg.getSotpId();
		String envInfo = requestMsg.getEnvInfo();
		String sotpCode = requestMsg.getSotpCode();
		String protect = requestMsg.getNewprotect();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();
		String service = requestMsg.getService();

		if (!ServiceConstant.BUSINESS_MODSOTPIN.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		logger.debug("modsotpin parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId
		        + ", sotpCode:" + sotpCode + ", protect" + protect);

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, sotpId);
		}

		// 插件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		// 硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(sotpId);
		if (null == plInfo || null == authFeaInfo) {
			errMsg = "pluginInfo or authFeaInfo is not exist in db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST, "", "",
			        errMsg, reqmsg, request, sotpId);
		}

		// 验证设备\插件\app\用户的对应关系及插件状态
		resultVO = verifyService.verifyPluginStatusIsReady(plInfo, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}
		resultVO = verifyService.verifyAll(service, plInfo, authFeaInfo, envInfo, phoneNum, appId, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			this.pluginService.updatePluginInfoStatusById(sotpId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		// 验证sotpCode(30)
		ret = businessService.verifySotpCode(plInfo, authFeaInfo, sotpCode, true, plInfo.getProtectCode(),
		        verifyPolicyInfo);
		if (ret != ErrorConstant.RET_OK) {
			errMsg = "verifySotpCode error. result:" + ret;
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ret, "", "", errMsg, reqmsg, request, sotpId);
		}

		// 更新插件信息中保护码
		UpdateUtil updateInfo = new UpdateUtil();
		updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PROTECT_CODE, protect);
		updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plInfo.getPluginId());

		Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
		this.pluginInfoMapper.updateHashMapByPluginId(updateMap);

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, "", null, "", reqmsg, request, sotpId);
	}

	/*
	 * 修改预留信息
	 */
	@Override
	public String modholdinfo(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		int ret = ErrorConstant.RET_OK;
		String reqmsg = request.getQueryString();
		String msg = "";
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String sotpId = requestMsg.getSotpId();
		String devInfo = requestMsg.getDevInfo();
		String action = requestMsg.getAction();
		String holdinfo = requestMsg.getHoldinfo();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();
		String service = requestMsg.getService();

		if (!ServiceConstant.BUSINESS_MODHOLDINFO.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		logger.debug("modholdinfo parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId
		        + ", holdinfo:" + holdinfo);

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, sotpId);
		}

		// 插件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		// 硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(sotpId);
		if (null == plInfo || null == authFeaInfo) {
			errMsg = "pluginInfo or authFeaInfo is not exist in db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST, "", "",
			        errMsg, reqmsg, request, sotpId);
		}

		// 验证设备\插件\app\用户的对应关系及插件状态
		resultVO = verifyService.verifyPluginStatusIsReady(plInfo, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}
		resultVO = verifyService.verifyAll(service, plInfo, authFeaInfo, devInfo, phoneNum, appId, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			this.pluginService.updatePluginInfoStatusById(sotpId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
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
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_ILLEGAL, "", null,
			        "para action not right", reqmsg, request, sotpId);
		}

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ret, msg, null, "", reqmsg, request, sotpId);
	}

	/*
	 * 时间校准功能
	 */
	@Override
	public String timeSynchr(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String pluginId = requestMsg.getSotpId();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();

		if (!ServiceConstant.BUSINESS_TIME_SYNCHR.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, pluginId);
		}

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, pluginId);
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, pluginId);
		}

		// 插件信息判断
		pluginInfoDto plInfo = paraHandle.getPluginfo(pluginId);
		if (plInfo == null) {
			errMsg = "sotpId db not exist";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", "", errMsg,
			        reqmsg, request, pluginId);
		}

		resultVO = verifyService.verifyPluginStatusIsReady(plInfo, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, pluginId);
		}

		resultVO = verifyService.verifyUserInfo(plInfo, phoneNum);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, pluginId);
		}
		// TODO 都返回了系统时间不就是服务端时间么
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String timeinfo = dateFormat.format(System.currentTimeMillis());
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, timeinfo, null, "", reqmsg, request, pluginId);
	}
}
