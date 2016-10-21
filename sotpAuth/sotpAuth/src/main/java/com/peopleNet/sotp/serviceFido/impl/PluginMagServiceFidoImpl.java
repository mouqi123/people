package com.peopleNet.sotp.serviceFido.impl;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.constant.ServiceConstant;
import com.peopleNet.sotp.controller.ControllerHelper;
import com.peopleNet.sotp.dal.dao.authFeatureDtoMapper;
import com.peopleNet.sotp.dal.model.*;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.*;
import com.peopleNet.sotp.serviceFido.IPluginMagServiceFido;
import com.peopleNet.sotp.thrift.service.PluginSaveHelper;
import com.peopleNet.sotp.thrift.service.SotpPlugin;
import com.peopleNet.sotp.thrift.service.SotpRet;
import com.peopleNet.sotp.util.Base64;
import com.peopleNet.sotp.util.SHA;
import com.peopleNet.sotp.util.UpdateUtil;
import com.peopleNet.sotp.vo.ResultVO;
import com.peopleNet.sotp.vo.UserRequestMsgFido;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PluginMagServiceFidoImpl implements IPluginMagServiceFido {
	private static LogUtil logger = LogUtil.getLogger(PluginMagServiceFidoImpl.class);
	@Autowired
	private IEncryptPluginInfoService pluginInfoMapper;
	@Autowired
	private authFeatureDtoMapper authFeatureMapper;
	@Autowired
	private IPluginService pluginService;
	@Autowired
	private ICacheService cacheService;
	@Autowired
	private IBusinessService businessService;
	@Autowired
	private IParaHandle paraHandle;
	@Autowired
	private IPolicyService policyService;
	@Autowired
	private IDBOperationService dbOperationService;
	@Autowired
	private IVerifyService verifyService;
	@Autowired
	private IThriftInvokeService thriftInvokeService;

	/*
	 * private String THOR_URL = CommonConfig.get("THOR_URL"); private int
	 * CONNECT_THOR_PROTOCOL =
	 * Integer.parseInt(CommonConfig.get("CONNECT_THOR_PROTOCOL").trim());
	 * private String PLUGIN_STRORE_MODE =
	 * CommonConfig.get("PLUGIN_SAVE_MODE").trim();
	 */

	/*
	 * 注册申请插件
	 */
	@Override
	public String pluginReg(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		int ret = ErrorConstant.RET_OK;
		String reqmsg = paraHandle.filterRequestMap(requestMsg.getSignParaMap());
		String errMsg = "";

		// 参数定义
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		if (StringUtils.isEmpty(userId)) {
			userId = phoneNum;
		}
		String pluginVersion = requestMsg.getPluginVersion();
		String devInfo = requestMsg.getDevInfo();
		String userInfo = requestMsg.getUserInfo();
		String appId = requestMsg.getAppId();
		String op = requestMsg.getOp();
		String header = requestMsg.getHeader();
		String businessName = requestMsg.getBusinessName();
		String attachedInfo = requestMsg.getAttachedInfo();

		logger.debug("reqmsg:" + reqmsg);
		// 业务类型判断
		if (!ServiceConstant.PLUGIN_REG.equals(op)) {
			errMsg = "op type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, op, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		ResultVO resultVO = paraHandle.paraFormatFido(requestMsg);
		if (resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, op, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg, request, "");
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, op, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, "");
		}

		// 获取申请策略
		RulePolicyDto applyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.APPLY, appId);
		if (applyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpapplypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, op, ErrorConstant.POLICY_FETCH_ERROR, "", "", errMsg, reqmsg,
			        request, "");
		}

		// 插件申请策略
		resultVO = policyService.pluginReqPolicy(phoneNum, appId, applyPolicyInfo);
		if (resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, op, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg, request, "");
		}

		// 认证硬件信息
		authFeatureDto authFeaInfo = paraHandle.getNewAuthFeature(devInfo);
		if (authFeaInfo == null) {
			errMsg = "new authFeaInfo error.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, op, ErrorConstant.SYSTEM_MALLOC_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		// 插件信息
		pluginInfoDto pluginInfo = paraHandle.getNewPluginfoV2(userId, phoneNum, "");
		if (pluginInfo == null) {
			errMsg = "new pluginInfo error.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, op, ErrorConstant.SYSTEM_MALLOC_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		SotpPlugin plugin = businessService.genThorPlugin(pluginVersion, authFeaInfo, pluginInfo, appId, op,
		        applyPolicyInfo);
		if (null == plugin) {
			errMsg = "generate plugin failed!";
			logger.error(errMsg);
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
			return paraHandle.responseHandle(appId, op, ErrorConstant.THOR_SYSTEM_ERR, "", "", errMsg, reqmsg, request, "");
		}
		pluginInfo.setStatus(Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NO_ACTIVATE);
		try {
			this.dbOperationService.insertRegPluginIntoDB(authFeaInfo, pluginInfo, phoneNum, userId, appId);
		} catch (Exception e) {
			errMsg = "insert into db error!";
			logger.error(errMsg + e.toString());
			return paraHandle.responseHandle(appId, op, ErrorConstant.SQL_WRITEDB_ERR, "", "", errMsg, reqmsg, request, "");
		} finally {
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
		}

		// 获取采集信息
		UserRequestMsgFido newAttach = paraHandle.getattachedInfo(attachedInfo);

		userVerifyInfoDto newUser = new userVerifyInfoDto();
		newUser.setUserId(userId);
		newUser.setPluginId(pluginInfo.getPluginId());
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
			return paraHandle.responseHandle(appId, op, ErrorConstant.SQL_WRITEDB_ERR, "", "", errMsg, reqmsg, request, "");
		} finally {
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
		}
		// 生成挑战码
		String chalcode = businessService.generateChallenge();
		logger.info("phoneNum:" + phoneNum + " getchallenge code:" + chalcode);

		boolean saveChallengeStatus = businessService.saveChallengeIntoCache(appId, pluginInfo, chalcode,
		        verifyPolicyInfo);
		if (!saveChallengeStatus) {
			errMsg = "save challenge code into redis error!";
			return paraHandle.responseHandle(appId, op, ErrorConstant.REDIS_OP_ERROR, "", null, errMsg, reqmsg,
			        request, "");
		}

		// 获取认证策略项

		authPolicyDto authPolicy = policyService.getAuthPolicyDto(appId, businessName, "", 0);
		if (authPolicy == null) {
			errMsg = "get authPolicy error, please check businessName.";
			return paraHandle.responseHandle(appId, op, ErrorConstant.SQL_READMYSQL_ERR, "", null, errMsg, reqmsg,
			        request, "");

		}
		if (authPolicy.getServiceType() == null || authPolicy.getServiceType() != 1) {
			errMsg = "get authPolicy error, please check businessName.";
			return paraHandle.responseHandle(appId, op, ErrorConstant.SQL_READMYSQL_ERR, "", null, errMsg, reqmsg,
			        request, "");

		}
		Integer policy = authPolicy.getAuthFactorNum();
		if (null == policy) {
			errMsg = " policy is null ";
			return paraHandle.responseHandle(appId, op, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, "");
		}
		// 获取返回的data格式
		String dataJson = ControllerHelper.getDataJson(header, chalcode, userInfo, policy, plugin);

		return paraHandle.responseHandle(appId, op, ret, dataJson, null, "", reqmsg, request, "");
	}

	/*
	 * 注册申请认证激活插件
	 */
	@Override
	public String pluginRegAuth(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		int ret = ErrorConstant.RET_OK;
		String reqmsg = paraHandle.filterRequestMap(requestMsg.getSignParaMap());
		String errMsg = "";

		// 参数定义
		String pluginId = requestMsg.getPluginId();
		String appId = requestMsg.getAppId();
		String op = requestMsg.getOp();
		String pluginHash = requestMsg.getPluginHash();
		String challengeAns = requestMsg.getChallengeAns();
		String auth = requestMsg.getAuth();
		String businessName = requestMsg.getBusinessName();
		String phoneNum = requestMsg.getPhoneNum();

		logger.debug("reqmsg:" + reqmsg);
		// 业务类型判断
		if (!ServiceConstant.PLUGIN_REG_AUTH.equals(op)) {
			errMsg = "op type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, op, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, pluginId);
		}

		ResultVO resultVO = paraHandle.paraFormatFido(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, op, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg, request, pluginId);
		}

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, op, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, pluginId);
		}

		// 验证插件硬件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(pluginId);
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(pluginId);
		if (null == plInfo || null == authFeaInfo) {
			errMsg = "pluginInfo or authFeaInfo is not exist in db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, op, ErrorConstant.PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST, "", "",
			        errMsg, reqmsg, request, pluginId);
		}

		// 挑战应答验证
		resultVO = verifyService.verifyChallengeAns(plInfo, challengeAns);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, op, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg, request, pluginId);
		}

		// 插件签名pluginSign验证
		if (!plInfo.getHashValue().equals(pluginHash)) {
			errMsg = "pluginhash not match pluginHashClient:" + pluginHash + "pluginServer:" + plInfo.getHashValue()
			        + "!";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, op, ErrorConstant.THOR_ENCRYPT_ERROR, "", "", errMsg, reqmsg,
			        request, pluginId);
		}

		authPolicyDto authPolicy = policyService.getAuthPolicyDto(appId, businessName, "", -1);
		if (authPolicy == null) {
			errMsg = "get authPolicy error, please check businessName.";
			return paraHandle.responseHandle(appId, op, ErrorConstant.SQL_READMYSQL_ERR, "", null, errMsg, reqmsg,
			        request, pluginId);
		}
		if (authPolicy.getServiceType() == null || authPolicy.getServiceType() != 1) {
			errMsg = "get authPolicy error, please check businessName.";
			return paraHandle.responseHandle(appId, op, ErrorConstant.SQL_READMYSQL_ERR, "", null, errMsg, reqmsg,
			        request, pluginId);

		}

		// 验证策略需要的认证因素
		Integer policy = authPolicy.getAuthFactorNum();
		if (null == policy) {
			errMsg = " policy is null ";
			return paraHandle.responseHandle(appId, op, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, pluginId);
		}

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

		// 根据策略选择是否激活插件
		int pluginInitStatus = pluginService.getPluginInitStatus(appId);
		if (pluginInitStatus != Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY
		        && pluginInitStatus != Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NO_ACTIVATE) {
			logger.info("policy plugin init status is not right, set default: ready");
			pluginInitStatus = Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY;
		}
		UpdateUtil updateInfo = new UpdateUtil();
		updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.STATUS, pluginInitStatus);
		updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, pluginId);

		Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
		this.pluginInfoMapper.updateHashMapByPluginId(updateMap);

		// 将状态更新为成功
		userVerifyInfoDto userInfo = new userVerifyInfoDto();
		String userVerifyInfoIdStr = businessService.getUserVerifyInfoIdAndClear(plInfo);
		if (!StringUtils.isEmpty(userVerifyInfoIdStr)) {
			userInfo.setId(Integer.parseInt(userVerifyInfoIdStr));
		}
		try {
			this.dbOperationService.updateStatus(userInfo);
		} catch (Exception e) {
			errMsg = "update status db error!";
			logger.error(errMsg + e.toString());
			return paraHandle.responseHandle(appId, op, ErrorConstant.SQL_WRITEDB_ERR, "", "", errMsg, reqmsg, request, pluginId);
		}
		return paraHandle.responseHandle(appId, op, ret, "", "", "", reqmsg, request, pluginId);
	}

	/*
	 * 解绑插件
	 */
	@Override
	public String plugindel(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		String errMsg = "";
		String reqmsg = paraHandle.filterRequestMap(requestMsg.getSignParaMap());

		String phoneNum = requestMsg.getPhoneNum();
		String sotpId = requestMsg.getPluginId();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getOp();
		String delPluginId = requestMsg.getDelPluginId();
		// 业务类型判断
		if (!ServiceConstant.PLUGIN_UNWRAP.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatFido(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
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

		// 插件信息判断
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		if (plInfo == null) {
			errMsg = "sotpId db not exist";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", "", errMsg,
			        reqmsg, request, sotpId);
		}
		// 硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(sotpId);
		if (null == plInfo || null == authFeaInfo) {
			errMsg = "pluginInfo or authFeaInfo is not exist in db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST, "", "",
			        errMsg, reqmsg, request, sotpId);
		}

		resultVO = verifyService.verifyPluginStatusIsReady(plInfo, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		resultVO = verifyService.verifyUserInfo(plInfo, phoneNum);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		if (StringUtils.isEmpty(delPluginId)) {
			errMsg = "delPluginId is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", "", errMsg,
			        reqmsg, request, sotpId);
		}
		pluginInfoDto delPlInfo = paraHandle.getPluginfo(sotpId);
		if (delPlInfo == null) {
			errMsg = "delPluginId db not exist";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", "", errMsg,
			        reqmsg, request, sotpId);
		}

		UpdateUtil updateInfo = new UpdateUtil();
		updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.STATUS,
		        Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
		updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, delPluginId);

		Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
		this.pluginInfoMapper.updateHashMapByPluginId(updateMap);

		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, "", "", "", reqmsg, request, sotpId);
	}

	/*
	 * 更新插件
	 */
	@Override
	public String pluginupdate(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		String errMsg = "";
		String reqmsg = paraHandle.filterRequestMap(requestMsg.getSignParaMap());

		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		if (StringUtils.isEmpty(userId)) {
			userId = phoneNum;
		}
		String pluginVersion = requestMsg.getPluginVersion();
		String pluginId = requestMsg.getPluginId();
		String appInfo = requestMsg.getAppInfo();
		String sotpCode = requestMsg.getSotpCode();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getOp();
		String devInfo = requestMsg.getDevInfo();
		// 业务类型判断
		if (!ServiceConstant.PLUGIN_UPDATE.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, pluginId);
		}

		ResultVO resultVO = paraHandle.paraFormatFido(requestMsg);

		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
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

		// 获取申请策略
		RulePolicyDto applyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.APPLY, appId);
		if (applyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpapplypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", "", errMsg, reqmsg,
			        request, pluginId);
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

		
		// 设备指纹 lxx
		int devType = authFeaInfo.getdevType();
		if (devType == Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_ARM || 
				devType == Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_MIPS ||
				devType == Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_X86){
			String policyType = "update";
			resultVO = policyService.checkDevInfo(appId, devInfo, policyType, authFeaInfo);
			if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
				logger.error(resultVO.getMsg());
				return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
				        request, pluginId);
			}
		}
		
		
		
		
		// 验证设备\插件\app\用户的对应关系及插件状态
		resultVO = verifyService.verifyPluginStatusIsReady(plInfo, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, pluginId);
		}
		resultVO = verifyService.verifyAll(service, plInfo, authFeaInfo, appInfo, phoneNum, appId, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			this.pluginService.updatePluginInfoStatusById(pluginId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, pluginId);
		}

		// 验证sotpCode
		int ret = businessService.verifySotpCodeWithChallenge(plInfo, authFeaInfo, sotpCode, true, verifyPolicyInfo);
		if (ret != ErrorConstant.RET_OK) {
			errMsg = "verifySotpCodeWithChallenge error. result:" + ret;
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ret, "", "", errMsg, reqmsg, request, pluginId);
		}

		// 插件申请策略
		resultVO = policyService.pluginReqPolicy(phoneNum, appId, applyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, pluginId);
		}

		// 插件信息、硬件信息
		pluginInfoDto newpluginInfo = new pluginInfoDto(plInfo.getHoldInfo(), plInfo.getProtectCode(), userId,
		        phoneNum);

		// 分配插件
		SotpPlugin plugin = businessService.genThorPlugin(pluginVersion, authFeaInfo, newpluginInfo, appId, service,
		        applyPolicyInfo);
		if (null == plugin) {
			errMsg = "generate plugin failed!";
			logger.error(errMsg);
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
			return paraHandle.responseHandle(appId, service, ErrorConstant.THOR_SYSTEM_ERR, "", "", errMsg, reqmsg,
			        request, pluginId);
		}

		try {
			this.dbOperationService.insertUpdatePluginIntoDB(authFeaInfo, plInfo, newpluginInfo, appId);
		} catch (Exception e) {
			errMsg = "insert into db error.";
			logger.error(errMsg + e.toString());
			return paraHandle.responseHandle(appId, service, ErrorConstant.SQL_WRITEDB_ERR, "", "", errMsg, reqmsg,
			        request, pluginId);
		} finally {
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
		}

		return paraHandle.responseHandle(appId, service, ret, plugin.getPlugin(), plugin.getFile(), "", reqmsg,
		        request, pluginId);
	}

	/*
	 * 获取绑定设备列表 返回：devname@start_time@devId
	 */
	@Override
	public String getdevlist(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		String reqmsg = paraHandle.filterRequestMap(requestMsg.getSignParaMap());
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String service = requestMsg.getOp();
		String sotpCode = requestMsg.getSotpCode();
		String pluginId = requestMsg.getPluginId();
		String appId = requestMsg.getAppId();

		if (!ServiceConstant.PLUGIN_LISTALL.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, pluginId);
		}

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatFido(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg,
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

		// 验证sotpCode
		int ret = businessService.verifySotpCodeWithChallenge(plInfo, authFeaInfo, sotpCode, true, verifyPolicyInfo);
		if (ret != ErrorConstant.RET_OK) {
			errMsg = "verifySotpCodeWithChallenge error. result:" + ret;
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ret, "", "", errMsg, reqmsg, request, pluginId);
		}

		// 判断 SOTPID
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("appId", appId);
		parms.put("phoneNum", phoneNum);

		StringBuilder devlist = null;
		List<String> hardwares = null;
		try {
			hardwares = authFeatureMapper.selectByPhoneNum(parms);
		} catch (SQLException e) {
			logger.error("authFeatureMapper selectByPhoneNum sql error.msg:%s", e.toString());
			return paraHandle.responseHandle(appId, service, ErrorConstant.SQL_OPS_ERR, "", "", errMsg, reqmsg,
			        request, pluginId);
		}

		if (CollectionUtils.isEmpty(hardwares)) {
			return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, "", "", null, reqmsg, request, pluginId);
		}

		devlist = new StringBuilder(hardwares.get(0));
		for (int i = 1, len = hardwares.size(); i < len; i++) {
			String devMsg = hardwares.get(i);
			String[] devContent = devMsg.split("@");
			boolean needUpdate = policyService.checkUpdate(devMsg, appId);
			if (needUpdate) {
				devMsg = devMsg.subSequence(0, devMsg.lastIndexOf("@") + 1)
				        + Integer.toString(Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NEEDUPDATE);
			}
			if (pluginId.equals(devContent[3])) {
				devlist.insert(0, devMsg + "&");
			} else {
				devlist.append("&").append(devMsg);
			}
		}
		logger.debug("devlist:" + devlist);

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, devlist.toString(), "", null, reqmsg,
		        request, pluginId);
	}

	/*
	 * 激活插件
	 */
	@Override
	public String activePlugin(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		String reqmsg = paraHandle.filterRequestMap(requestMsg.getSignParaMap());
		int ret = ErrorConstant.RET_OK;
		String errMsg = "";

		String service = requestMsg.getOp();
		String activeCode = requestMsg.getSotpCode();
		String pluginId = requestMsg.getPluginId();
		String activePluginId = requestMsg.getActivePluginId();
		if (StringUtils.isEmpty(activePluginId)) {
			activePluginId = pluginId;
		}
		String appId = requestMsg.getAppId();

		logger.debug("activePluginInterface activeCode-----" + activeCode);
		if (!ServiceConstant.PLUGIN_ACTIVATE.equals(service)) {
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
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, pluginId);
		}

		// 获取激活策略
		RulePolicyDto activePolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.ACTIVE, appId);
		if (activePolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpactivepolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, pluginId);
		}

		// 插件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(pluginId);
		pluginInfoDto activePlInfo = paraHandle.getPluginfo(activePluginId);
		// 硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(pluginId);
		if (null == plInfo || null == authFeaInfo || null == activePlInfo) {
			errMsg = "pluginInfo or authFeaInfo is not exist in db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST, "", "",
			        errMsg, reqmsg, request, pluginId);
		}
		if (!plInfo.getBindUserphone().equals(activePlInfo.getBindUserphone())){
			errMsg = "can not active other plugin because phoneNum can not pair";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PNUM_NOTMATCH_SID, "", "",
			        errMsg, reqmsg, request, pluginId);
		}
		if (activePlInfo.getStatus() != Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NO_ACTIVATE) {
			errMsg = "active plugin status is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PLUGIN_ACTIVECODE_STATUS_ERR, "", null,
			        errMsg, reqmsg, request, pluginId);
		}

		// 验证激活策略
		resultVO = policyService.pluginActivePolicy(plInfo, activePolicyInfo);
		if (resultVO.getCode() == ErrorConstant.PLUGIN_HAVE_ACTIVE_MAX_TIMES) {
			// 激活次数过多，插件置为作废
			this.pluginService.updatePluginInfoStatusById(pluginId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, pluginId);
		}
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, pluginId);
		}

		String realSotpCode = "30@" + activeCode;
		// 验证sotpCode
		ret = businessService.verifySotpCodeWithChallenge(plInfo, authFeaInfo, realSotpCode, false, verifyPolicyInfo);
		if (ret != ErrorConstant.RET_OK) {
			errMsg = "active code error!";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ret, "", "", errMsg, reqmsg, request, pluginId);
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
			Date date = new Date();
			updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.START_TIME, date);
			updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.ACTIVE_USECNT, plInfo.getActiveUseCnt());
			this.pluginService.updatePluginInfoStatusById(activePluginId,
			        Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);
		}
		updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plInfo.getPluginId());
		Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
		this.pluginInfoMapper.updateHashMapByPluginId(updateMap);

		return paraHandle.responseHandle(appId, service, ret, "", null, errMsg, reqmsg, request, pluginId);
	}

	/*
	 * 修改设备名称
	 */
	@Override
	public String setDevAlias(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		String reqmsg = paraHandle.filterRequestMap(requestMsg.getSignParaMap());
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();

		String sotpId = requestMsg.getPluginId();
		String alias = requestMsg.getAlias();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getOp();

		if (!ServiceConstant.PLUGIN_SETDEVALIAS.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		logger.debug("setDevAlias parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId
		        + ", alias:" + alias);

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatFido(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
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

		// 更新硬件插件信息中的设备名称
		try {
			this.authFeatureMapper.updateDevNameByPluginId(alias, sotpId);
		} catch (SQLException e) {
			errMsg = "authFeatureMapper updateDevNameByPluginId sql error" + e.toString();
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.SQL_WRITEDB_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, "", null, "", reqmsg, request, "");
	}

	/*
	 * 同步共享密钥
	 */
	@Override
	public String synShareKey(UserRequestMsgFido requestMsg, HttpServletRequest request) {

		String reqmsg = paraHandle.filterRequestMap(requestMsg.getSignParaMap());
		String errMsg = "";

		String businessId = requestMsg.getBusinessId();
		String action = requestMsg.getAction();
		String shareKey = requestMsg.getShareKey();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getOp();

		if (!ServiceConstant.PLUGIN_SYNSHAREKEY.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		logger.debug("synsharekey parameter service:" + service + ", businessId:" + businessId + ", action:" + action
		        + ", sharekey:" + shareKey);

		// 连接密码机进行加密转换
		SotpRet sotpRes = null;

		if (StringUtils.isEmpty(action) || StringUtils.isEmpty(shareKey)) {
			logger.error(" action:----- " + action + "shareKey :" + shareKey);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_ILLEGAL, "", "", errMsg, reqmsg,
			        request, "");
		}

		try {
			sotpRes = thriftInvokeService.shareKey(Integer.parseInt(action), shareKey);
		} catch (TException e) {
			logger.error("invoke thrift encryptNew error.msg:%s", e.toString());
			return paraHandle.responseHandle(appId, service, ErrorConstant.THOR_CONNECT_TIMEOUT, "", "", errMsg, reqmsg,
			        request, "");
		}

		if (sotpRes == null || sotpRes.status < 0) {
			errMsg = "sotpthorReponse is null.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.THOR_CONNECT_TIMEOUT, "", "", errMsg, reqmsg,
			        request, "");
		}

		logger.info("synShareKey:" + sotpRes.getStatus());

		// 正确
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, sotpRes.getTitle(), "", errMsg, reqmsg,
		        request, "");
	}

	/*
	 * 转加密
	 */
	public String cryptConvert(UserRequestMsgFido requestMsg, HttpServletRequest request) {

		String reqmsg = paraHandle.filterRequestMap(requestMsg.getSignParaMap());
		String errMsg = "";

		String flag = requestMsg.getFlag();
		String phoneNum = requestMsg.getPhoneNum();
		String sotpId = requestMsg.getPluginId();
		String transEncryData = requestMsg.getTransEncryData();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getOp();

		if (!ServiceConstant.PLUGIN_CRYPTCONVERT.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		logger.debug("cryptconvert parameter flag:" + flag + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId
		        + ", transEncryData:" + transEncryData);

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatFido(requestMsg);
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

		// 认证硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(sotpId);
		if (authFeaInfo == null) {
			errMsg = "authFeaInfo not exist db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_DEVINFO_NOEXIST, "", "", errMsg,
			        reqmsg, request, sotpId);
		}
		// 插件信息获取
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		if (plInfo == null) {
			errMsg = "pluginId not exist db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", "", errMsg,
			        reqmsg, request, sotpId);
		}
		// 手机号判断
		if (!plInfo.getBindUserphone().equals(phoneNum)) {
			errMsg = "phoneNum not right";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PHONENUM_NOEXIST, "", "", errMsg,
			        reqmsg, request, sotpId);
		}
		// 插件状态：是否为就绪

		resultVO = policyService.pluginStatus(plInfo, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", errMsg, reqmsg, request, sotpId);
		}

		// 连接密码机进行加密转换
		SotpRet sotpRes = null;
		try {
			sotpRes = thriftInvokeService.transEncry(Integer.parseInt(flag), plInfo.getPluginKey(), transEncryData);
		} catch (TException e) {

			logger.error("invoke thrift encryptNew error.msg:%s", e.toString());
		}

		if (sotpRes == null || sotpRes.status < 0) {
			errMsg = "sotpthorReponse is null.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.THOR_CONNECT_TIMEOUT, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		logger.info("transEncry:" + sotpRes.getTitle());
		// 正确
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, sotpRes.getTitle(), "", errMsg, reqmsg,
		        request, sotpId);
	}

	/*
	 * 判断插件状态
	 */
	@Override
	public String getstatus(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		int ret = ErrorConstant.RET_OK;
		String reqmsg = request.getQueryString();
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String sotpId = requestMsg.getPluginId();
		String sotpCode = requestMsg.getSotpCode();
		String envInfo = requestMsg.getEnvInfo();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getOp();

		if (!ServiceConstant.PLUGIN_GETSTATUS.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		ResultVO resultVO = paraHandle.paraFormatFido(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg,
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

		// 验证sotpCode
		ret = businessService.verifySotpCodeWithChallenge(plInfo, authFeaInfo, sotpCode, true, verifyPolicyInfo);
		if (ret != ErrorConstant.RET_OK) {
			errMsg = "verifySotpCodeWithChallenge error. result:" + ret;
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ret, "", "", errMsg, reqmsg, request, sotpId);
		}

		// 更新策略 判断
		resultVO = policyService.updatePolicy(appId, plInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg,
			        request, sotpId);
		}

		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, "", "", null, reqmsg, request, sotpId);
	}

	/*
	 * for 模拟系统
	 */
	/*
	 * 获取绑定插件列表返回给模拟系统
	 */
	@Override
	public String getPluginListWeb(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String service = requestMsg.getOp();
		String pluginId = requestMsg.getPluginId();
		String appId = requestMsg.getAppId();

		if (!ServiceConstant.PLUGIN_LISTALL_WEB.equals(service)) {
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

		// 判断 SOTPID
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("appId", appId);
		parms.put("phoneNum", phoneNum);

		StringBuilder devlist = null;
		List<String> hardwares = null;
		try {
			hardwares = authFeatureMapper.selectByPhoneNum(parms);
		} catch (SQLException e) {
			logger.error("authFeatureMapper selectByPhoneNum sql error.msg:%s", e.toString());
		}

		if (CollectionUtils.isEmpty(hardwares)) {
			return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, "", "", null, reqmsg, request, pluginId);
		}

		devlist = new StringBuilder(hardwares.get(0));
		for (int i = 1, len = hardwares.size(); i < len; i++) {
			String devMsg = hardwares.get(i);
			String[] devContent = devMsg.split("@");
			boolean needUpdate = policyService.checkUpdate(devMsg, appId);
			if (needUpdate) {
				devMsg = devMsg.subSequence(0, devMsg.lastIndexOf("@") + 1)
				        + Integer.toString(Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NEEDUPDATE);
			}
			if (pluginId!=null&&pluginId.equals(devContent[3])) {
				devlist.insert(0, devMsg + "&");
			} else {
				devlist.append("&").append(devMsg);
			}
		}
		logger.debug("devlist:" + devlist);

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, devlist.toString(), "", null, reqmsg,
		        request, pluginId);
	}

	/*
	 * 激活插件
	 */
	@Override
	public String activePluginWeb(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		int ret = ErrorConstant.RET_OK;
		String errMsg = "";

		String service = requestMsg.getOp();
		String activeCode = requestMsg.getSotpCode();
		String sotpId = requestMsg.getActivePluginId();
		String appId = requestMsg.getAppId();

		logger.debug("activePluginWebInterface activeCode-----" + activeCode);
		if (!ServiceConstant.PLUGIN_ACTIVE_WEB.equals(service)) {
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

		// 获取验证策略
		RulePolicyDto verifyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.VERIFY, appId);
		if (verifyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpverifypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, "");
		}

		// 获取激活策略
		RulePolicyDto activePolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.ACTIVE, appId);
		if (activePolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpactivepolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, "");
		}

		// 插件信息
		pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
		// 硬件信息
		authFeatureDto authFeaInfo = paraHandle.getAuthFeature(sotpId);
		if (null == plInfo || null == authFeaInfo) {
			errMsg = "pluginInfo or authFeaInfo is not exist in db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST, "", "",
			        errMsg, reqmsg, request, "");
		}
		if (plInfo.getStatus() != Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NO_ACTIVATE) {
			errMsg = "plugin status is " + plInfo.getStatus();
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PLUGIN_ACTIVECODE_STATUS_ERR, "", null,
			        errMsg, reqmsg, request, "");
		}

		// 验证激活策略
		resultVO = policyService.pluginActivePolicy(plInfo, activePolicyInfo);
		if (resultVO.getCode() == ErrorConstant.PLUGIN_HAVE_ACTIVE_MAX_TIMES) {
			// 激活次数过多，插件置为作废
			this.pluginService.updatePluginInfoStatusById(sotpId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, "");
		}
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, "");
		}

		String realSotpCode = "30@" + activeCode;
		// 验证sotpCode
		ret = businessService.verifySotpCodeWithChallenge(plInfo, authFeaInfo, realSotpCode, false, verifyPolicyInfo);
		if (ret != ErrorConstant.RET_OK) {
			errMsg = "active code error!";
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
			Date date = new Date();
			updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.START_TIME, date);
			updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.ACTIVE_USECNT, plInfo.getActiveUseCnt());
			this.pluginService.updatePluginInfoStatusById(sotpId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);
		}
		updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plInfo.getPluginId());
		Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
		this.pluginInfoMapper.updateHashMapByPluginId(updateMap);

		// 正确
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ret, "", null, errMsg, reqmsg, request, "");
	}

	@Override
	public String checkDevPluginhash(UserRequestMsgFido requestMsg, HttpServletRequest request) {
		int ret = ErrorConstant.RET_OK;
		String reqmsg = paraHandle.filterRequestMap(requestMsg.getSignParaMap());
		String errMsg = "";

		// 参数定义
		String pluginId = requestMsg.getPluginId();
		String appId = requestMsg.getAppId();
		String op = requestMsg.getOp();
		String pluginHash = requestMsg.getPluginHash();
		String appInfo = requestMsg.getPluginSign();

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatFido(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, op, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
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

		logger.debug("envInfo: [" + appInfo + "]");
		String devJson = Base64.decode(appInfo);
		logger.debug("json: [" + devJson + "]");
		JSONObject jsonObject = ControllerHelper.parseJsonString(devJson);
		if (null == jsonObject) {
			logger.error("dev format wrong");
		}

		int plugintype = authFeaInfo.getdevType();
		String pluginDevInfo = plugintype == 2 ? authFeaInfo.getuuid() : authFeaInfo.getimei();
		if (StringUtils.isEmpty(pluginDevInfo)) {
			logger.error(" pluginDevInfo is null");
		}
		String shaOrgDev = SHA.SHA_people(pluginDevInfo);
		String checkDev = "success";
		String dev = jsonObject.getString("pluginId");
		if (!shaOrgDev.equals(dev)) {
			checkDev = "fail";
			logger.error(" dev can not pair");
		}

		// 2.验证插件信息
		String hashValue = plInfo.getHashValue();
		String pluginval = jsonObject.getString("hash");
		String checkPluginHash = "success";
		if (StringUtils.isEmpty(hashValue) || !hashValue.equals(pluginval)) {
			checkPluginHash = "fail";
			logger.error("plugin can not pair(" + pluginval + " <> " + hashValue + ").");
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("checkDev", checkDev);
		resultMap.put("checkPluginHash", checkPluginHash);
		return paraHandle.responseHandleWithResult(appId, op, ErrorConstant.RET_OK, "", "", "", reqmsg, request,
		        resultMap);

	}
}
