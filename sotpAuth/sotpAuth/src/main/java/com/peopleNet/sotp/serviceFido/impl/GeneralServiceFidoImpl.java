package com.peopleNet.sotp.serviceFido.impl;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.constant.ServiceConstant;
import com.peopleNet.sotp.controller.ControllerHelper;
import com.peopleNet.sotp.dal.model.*;
import com.peopleNet.sotp.exception.BusinessException;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.*;
import com.peopleNet.sotp.serviceFido.IGeneralServiceFido;
import com.peopleNet.sotp.thrift.service.PluginSaveHelper;
import com.peopleNet.sotp.thrift.service.SotpRet;
import com.peopleNet.sotp.util.Base64;
import com.peopleNet.sotp.util.CommonConfig;
import com.peopleNet.sotp.util.DateUtil;
import com.peopleNet.sotp.util.UpdateUtil;
import com.peopleNet.sotp.util.Util;
import com.peopleNet.sotp.vo.AutoUnlockPlugin;
import com.peopleNet.sotp.vo.ResultVO;
import com.peopleNet.sotp.vo.RiskControlResult;
import com.peopleNet.sotp.vo.UserRequestMsgFido;
import com.peopleNet.sotp.vo.UserRequestMsgV2;

import net.sf.json.JSONObject;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class GeneralServiceFidoImpl implements IGeneralServiceFido {
	private static LogUtil logger = LogUtil.getLogger(PluginMagServiceFidoImpl.class);
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
	private IDBOperationService dbOperationService;
	@Autowired
	private IRuleService ruleService;

	private String useRuleEngine = CommonConfig.get("useRuleEngine");

	// 直接返回认证失败的风控得分下限
	private int CEILOFHIGHRISK = Integer.parseInt(CommonConfig.get("ceilOfHighRisk").trim());

	/*
	 * 安全认证申请
	 */
	@Override
	public String verify(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		String reqmsg = paraHandle.filterRequestMap(requestMsg.getSignParaMap());
		int ret = ErrorConstant.RET_OK;
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		String pluginId = requestMsg.getPluginId();
		String appInfo = requestMsg.getAppInfo();
		String userInfo = requestMsg.getUserInfo();
		String appId = requestMsg.getAppId();
		String op = requestMsg.getOp();
		String header = requestMsg.getHeader();
		String businessName = requestMsg.getBusinessName();
		String attachedInfo = requestMsg.getAttachedInfo();
		String useCount = requestMsg.getUseCount();
		String grade = requestMsg.getGrade();
		String devInfo = requestMsg.getDevInfo();
		String service = requestMsg.getOp();
		
		
		if (!ServiceConstant.BUSINESS_AUTH.equals(op)) {
			errMsg = "op type is not right.";
			logger.error(errMsg);
			return paraHandle
			        .responseHandle(appId, op, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg, request, pluginId);
		}

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatFido(requestMsg);

		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, op, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, pluginId);
		}

        RiskControlResult riskResult = null;
		// 风控规则引擎
        if (!com.peopleNet.sotp.util.StringUtils.isEmpty(useRuleEngine) && "true".equals(useRuleEngine)) {
            riskResult = ruleService.verifyRule(requestMsg);
            if ( riskResult.getRiskScore() >= CEILOFHIGHRISK) {
                // 风险极高,直接返回认证失败
                errMsg = "risk control indicate verify failed!";
                return paraHandle.responseHandle(appId, op, ErrorConstant.RISK_HIGHEST, "", null, errMsg, reqmsg,
                        request, pluginId);
            }
        }

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, op, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, pluginId);
		}

		// 插件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(pluginId);
		// 硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(pluginId);
		if (null == plInfo || null == authFeaInfo) {
			errMsg = "pluginInfo or authFeaInfo is not exist in db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, op, ErrorConstant.PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST, "", "",
			        errMsg, reqmsg, request, pluginId);
		}

		// 设备指纹 lxx
				int devType = authFeaInfo.getdevType();
				if (devType == Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_ARM || 
						devType == Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_MIPS ||
						devType == Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_X86){
					String policyType = "auth";
					resultVO = policyService.checkDevInfo(appId, devInfo, policyType, authFeaInfo);
					if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
						logger.error(resultVO.getMsg());
						return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
						        request, pluginId);
					}
				}
		
		
		
		
		// 插件使用计数检查，防拷贝
		if (plInfo.getUseCount() >= Integer.parseInt(useCount)) {
			this.pluginService.updatePluginInfoStatusById(pluginId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
			errMsg = "plugin has been copied";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, op, ErrorConstant.PLUGIN_HAVE_BEEN_COPIED, "", "", errMsg, reqmsg,
			        request, pluginId);
		}
		// 未拷贝，插件使用次数+1
		UpdateUtil updateUseCountInfo = new UpdateUtil();
		updateUseCountInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.USE_COUNT, 1 + plInfo.getUseCount());
		updateUseCountInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plInfo.getPluginId());
		Map<String, Object> updateUseCountMap = updateUseCountInfo.getUpdateInfomap();
		this.pluginInfoMapper.updateHashMapByPluginId(updateUseCountMap);

		// 验证设备\插件\app\用户的对应关系及插件状态
		resultVO = verifyService.verifyPluginStatusIsReady(plInfo, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, op, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg, request, pluginId);
		}
		resultVO = verifyService.verifyAll(op, plInfo, authFeaInfo, appInfo, phoneNum, appId, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			this.pluginService.updatePluginInfoStatusById(pluginId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
			return paraHandle.responseHandle(appId, op, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg, request, pluginId);
		}

		// 认证码 验证策略判断
		resultVO = policyService.codeVerifyPolicy(plInfo, appId, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, op, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, pluginId);
		}

		// 获取采集信息
		UserRequestMsgFido newAttach = paraHandle.getattachedInfo(attachedInfo);

		userVerifyInfoDto newUser = new userVerifyInfoDto();
		newUser.setUserId(userId);
		newUser.setPluginId(plInfo.getPluginId());
		newUser.setServiceName(op);
		newUser.setBusinessName(businessName);
		// 保存采集信息
		try {
			this.dbOperationService.insertuserVerifyInfotoDB(newAttach, newUser);
			this.cacheService.set(Constant.REDIS_CONSTANT.PLUGIN_USER_VERFIY_ID + newUser.getPluginId(),
			        "" + newUser.getId(), Constant.REDIS_CONSTANT.USER_VERIFY_WINDOWTIME);
		} catch (Exception e) {
			errMsg = "insert into db error!";
			logger.error(errMsg + e.toString());
			return paraHandle.responseHandle(appId, op, ErrorConstant.SQL_WRITEDB_ERR, "", "", errMsg, reqmsg, request, pluginId);
		} finally {
			PluginSaveHelper.release();
		}

		// 生成挑战码
		String chalcode = businessService.generateChallenge();
		logger.info("phoneNum:" + phoneNum + " getchallenge code:" + chalcode);
		boolean saveChallengeStatus = businessService.saveChallengeIntoCache(appId, plInfo, chalcode, verifyPolicyInfo);
		if (!saveChallengeStatus) {
			errMsg = "save challenge code into redis error!";
			return paraHandle
			        .responseHandle(appId, op, ErrorConstant.REDIS_OP_ERROR, "", null, errMsg, reqmsg, request, pluginId);
		}

		// 获取认证策略(包括根据风控得分获取对应的认证规则)
		int riskScore = 0;
        if (null != riskResult)
            riskScore = riskResult.getRiskScore();
		authPolicyDto authPolicy = policyService.getAuthPolicyDto(appId, businessName, grade, riskScore);
		if (authPolicy == null) {
			errMsg = "get authPolicy error, please check businessName.";
			return paraHandle.responseHandle(appId, op, ErrorConstant.SQL_READMYSQL_ERR, "", null, errMsg, reqmsg,
			        request, pluginId);

		}
		if (authPolicy.getServiceType() == null || authPolicy.getServiceType() != 2) {
			errMsg = "get authPolicy error, please check businessName.";
			return paraHandle.responseHandle(appId, op, ErrorConstant.SQL_READMYSQL_ERR, "", null, errMsg, reqmsg,
			        request, pluginId);

		}
		boolean saveAuthPolicyDto = businessService.saveAuthPolicyDtoIntoCache(appId, plInfo, businessName, authPolicy);
		if (!saveAuthPolicyDto) {
			errMsg = "save auth policy into redis error!";
			return paraHandle
			        .responseHandle(appId, op, ErrorConstant.REDIS_OP_ERROR, "", null, errMsg, reqmsg, request, pluginId);
		}
		Integer policy = authPolicy.getAuthFactorNum();
		if (null == policy) {
			errMsg = " policy is null ";
			return paraHandle.responseHandle(appId, op, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, pluginId);
		}

		// 获取返回的data格式
		String dataJson = ControllerHelper.getDataJson(header, chalcode, userInfo, policy, null);

		return paraHandle.responseHandle(appId, op, ret, dataJson, null, "", reqmsg, request, pluginId);
	}

	/*
	 * 安全认证响应
	 */
	@Override
	public String verifyResponse(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		String reqmsg = paraHandle.filterRequestMap(requestMsg.getSignParaMap());
		int ret = ErrorConstant.RET_OK;
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();

		String pluginId = requestMsg.getPluginId();
		String appInfo = requestMsg.getAppInfo();
		String appId = requestMsg.getAppId();
		String op = requestMsg.getOp();
		String challengeAns = requestMsg.getChallengeAns();
		String auth = requestMsg.getAuth();
		String businessName = requestMsg.getBusinessName();

		if (!ServiceConstant.BUSINESS_AUTH_RESPONSE.equals(op)) {
			errMsg = "op type is not right.";
			logger.error(errMsg);
			return paraHandle
			        .responseHandle(appId, op, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg, request, pluginId);
		}

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatFido(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, op, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, pluginId);
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, op, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, pluginId);
		}

		// 插件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(pluginId);
		// 硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(pluginId);
		if (null == plInfo || null == authFeaInfo) {
			errMsg = "pluginInfo or authFeaInfo is not exist in db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, op, ErrorConstant.PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST, "", "",
			        errMsg, reqmsg, request, pluginId);
		}

		// 验证设备\插件\app\用户的对应关系及插件状态
		resultVO = verifyService.verifyPluginStatusIsReady(plInfo, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, op, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg, request, pluginId);
		}
		resultVO = verifyService.verifyAll(op, plInfo, authFeaInfo, appInfo, phoneNum, appId, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			this.pluginService.updatePluginInfoStatusById(pluginId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
			return paraHandle.responseHandle(appId, op, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg, request, pluginId);
		}

		// 认证码 验证策略判断
		resultVO = policyService.codeVerifyPolicy(plInfo, appId, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, op, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, pluginId);
		}

		// 挑战应答验证
		resultVO = verifyService.verifyChallengeAns(plInfo, challengeAns);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, op, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg, request, pluginId);
		}

		// 获取验证策略
		authPolicyDto authPolicy = businessService.getAuthPolicyDtoFromCache(appId, plInfo, businessName);
		if (authPolicy == null) {
			errMsg = "get authPolicy from redis error, please check businessName.";
			return paraHandle
			        .responseHandle(appId, op, ErrorConstant.REDIS_OP_ERROR, "", null, errMsg, reqmsg, request, pluginId);

		}
		if (authPolicy == null || authPolicy.getServiceType() != 2) {
			errMsg = "get authPolicy error, please check businessName.";
			return paraHandle
			        .responseHandle(appId, op, ErrorConstant.REDIS_OP_ERROR, "", null, errMsg, reqmsg, request, pluginId);

		}
		Integer policy = authPolicy.getAuthFactorNum();
		if (null == policy) {
			errMsg = " policy is null ";
			return paraHandle.responseHandle(appId, op, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, pluginId);
		}

		// 验证策略需要的认证因素
		resultVO = businessService.verifyByPolicy(op, plInfo, authFeaInfo, policy, auth, verifyPolicyInfo);
		if (resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, op, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg, request, pluginId);
		}

		// 获取认证信息userinfo
		userInfoDto newUser = paraHandle.getAuth(auth, policy);
		if (null == newUser) {
			errMsg = "newUser is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, op, ErrorConstant.PARA_ILLEGAL, "", "", errMsg, reqmsg, request, pluginId);
		}
		newUser.setAppId(appId);
		if (StringUtils.isEmpty(newUser.getUserPhone())) {
			newUser.setUserPhone(phoneNum);
		}

		// 保存采集信息
		try {
			this.dbOperationService.updateUserInfo(newUser);
		} catch (Exception e) {
			errMsg = "update userinfo db error!";
			logger.error(errMsg + e.toString());
			return paraHandle.responseHandle(appId, op, ErrorConstant.SQL_WRITEDB_ERR, "", "", errMsg, reqmsg, request, pluginId);
		}

		// 更新 插件验证 计数
		// 在此转换一下的原因是，需要将具体的时分秒带过去
		String fmtDate = DateUtil.formatDate(new Date());
		Date newDate = DateUtil.getDateTimeByString(fmtDate);

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

		// 将状态更新为成功
		userVerifyInfoDto userVerifyInfo = new userVerifyInfoDto();
		String userVerifyInfoIdStr = businessService.getUserVerifyInfoIdAndClear(plInfo);
		if (userVerifyInfoIdStr != null) {
			userVerifyInfo.setId(Integer.parseInt(userVerifyInfoIdStr));
		}
		try {
			this.dbOperationService.updateStatus(userVerifyInfo);
		} catch (Exception e) {
			errMsg = "update status db error!";
			logger.error(errMsg + e.toString());
			return paraHandle.responseHandle(appId, op, ErrorConstant.SQL_WRITEDB_ERR, "", "", errMsg, reqmsg, request, pluginId);
		}
		return paraHandle.responseHandle(appId, op, ret, "", "", errMsg, reqmsg, request, pluginId);
	}

	/*
	 * 请求挑战码(验证码)
	 */
	@Override
	public String getchallenge(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		String reqmsg = paraHandle.filterRequestMap(requestMsg.getSignParaMap());
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String pluginId = requestMsg.getPluginId();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getOp();

		if (!ServiceConstant.BUSINESS_GETCHALLENGE.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, pluginId);
		}

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatFido(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
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

		resultVO = verifyService.verifyUserInfo(plInfo, phoneNum);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, pluginId);
		}

		// 生成挑战码
		String chalcode = businessService.generateChallenge();
		logger.info("phoneNum:" + phoneNum + " getchallenge code:" + chalcode);

		// 连接密码机加密验证码
		String reponsetContent = businessService.encrypt(plInfo, chalcode);
		if (StringUtils.isEmpty(reponsetContent)) {
			errMsg = "encrypt challenge code failed! code = " + chalcode;
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.THOR_ENCRYPT_ERROR, "", "", errMsg, reqmsg,
			        request, pluginId);
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg,
			        reqmsg, request, pluginId);
		}

		boolean saveChallengeStatus = businessService.saveChallengeIntoCache(appId, plInfo, chalcode, verifyPolicyInfo);
		if (!saveChallengeStatus) {
			errMsg = "save challenge code into redis error!";
			return paraHandle.responseHandle(appId, service, ErrorConstant.REDIS_OP_ERROR, "", null, errMsg, reqmsg,
			        request, pluginId);
		}

		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, reponsetContent, null, "", reqmsg,
		        request, pluginId);
	}

	/*
	 * 时间校准功能
	 */
	@Override
	public String timeSynchr(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		String reqmsg = paraHandle.filterRequestMap(requestMsg.getSignParaMap());
		int ret = ErrorConstant.RET_OK;
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String pluginId = requestMsg.getPluginId();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getOp();
		String sotpCode = requestMsg.getSotpCode();

		if (!ServiceConstant.BUSINESS_TIME_SYNCHR.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, pluginId);
		}

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatFido(requestMsg);
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
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg,
			        reqmsg, request, pluginId);
		}

		// 插件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(pluginId);
		// 硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(pluginId);
		if (null == plInfo || null == authFeaInfo) {
			errMsg = "pluginInfo or authFeaInfo is not exist in db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST, "", "",
			        errMsg, reqmsg, request, pluginId);
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
		// 验证sotpCode
		ret = businessService.verifySotpCodeWithChallenge(plInfo, authFeaInfo, sotpCode, true, verifyPolicyInfo);
		if (ret != ErrorConstant.RET_OK) {
			errMsg = "verifySotpCodeWithChallenge error. result:" + ret;
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ret, "", "", errMsg, reqmsg, request, pluginId);
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String timeinfo = dateFormat.format(System.currentTimeMillis());
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, timeinfo, null, "", reqmsg, request, pluginId);
	}

	/*
	 * 检测app签名值和hash值
	 */
	@Override
	public String checkAppSignHash(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		String reqmsg = paraHandle.filterRequestMap(requestMsg.getSignParaMap());
		int ret = ErrorConstant.RET_OK;
		String errMsg = "";

		String appId = requestMsg.getAppId();
		String service = requestMsg.getOp();
		String appInfo = requestMsg.getAppInfo();

		if (!ServiceConstant.BUSINESS_CHECKAPPSIGNHASH.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatFido(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, "");
		}

		String devJson = Base64.decode(appInfo);
		JSONObject jsonObject = ControllerHelper.parseJsonString(devJson);
		if (null == jsonObject) {
			errMsg = "dev format wrong";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_ILLEGAL, "", "", errMsg, reqmsg,
			        request, "");
		}

		// 验证app信息
		appVersionInfoDto appVersionInfo = cacheService.getAPPVersionInfoByCode(appId);
		if (null == appVersionInfo) {
			errMsg = "no APPInfo or can not get APPInfo.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ret, "", null, errMsg, reqmsg, request, "");
		}
		String appHashValue = appVersionInfo.getHash_value();
		String appHash = jsonObject.getString("appval");
		String appHashRet = "fail";
		logger.debug(" appHash client: " + appHash + " appHashServer: " + appHashValue);
		if (!StringUtils.isEmpty(appHash) && appHash.equals(appHashValue)) {
			appHashRet = "success";
		}

		String appSign = jsonObject.getString("signval");
		String appSignRet = "fail";
		String appSignature = appVersionInfo.getSignature();
		logger.debug(" appSign client: " + appSign + " appSignServer: " + appSignature);
		if (!StringUtils.isEmpty(appSign) && appSign.equals(appSignature)) {
			appSignRet = "success";
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("checkSign", appSignRet);
		resultMap.put("checkHash", appHashRet);
		return paraHandle.responseHandleWithResult(appId, service, ErrorConstant.RET_OK, "", "", "", reqmsg, request,
		        resultMap);

	}
	
	/*
	 * 生成会话密钥： session_key
	 */
	@Override
	public String gensesskey(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		String errMsg = "";

		String randa = requestMsg.getRanda();
		String randb = requestMsg.getRandb();
		String sotpId = requestMsg.getPluginId();
		String datainfo = requestMsg.getDataInfo();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getOp();
		String envInfo = requestMsg.getAppInfo();

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
		if ("true".equals(saveSessionKey)) {
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
	 * 加解密数据
	 */
	@Override
	public String crypto(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String sotpId = requestMsg.getPluginId();
		String handledata = requestMsg.getDataInfo();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getOp();

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
		ResultVO resultVO = paraHandle.paraFormatFido(requestMsg);
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
}
