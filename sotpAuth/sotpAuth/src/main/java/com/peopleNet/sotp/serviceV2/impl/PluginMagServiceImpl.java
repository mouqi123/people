package com.peopleNet.sotp.serviceV2.impl;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.constant.ServiceConstant;
import com.peopleNet.sotp.controller.ControllerHelper;
import com.peopleNet.sotp.dal.dao.authFeatureDtoMapper;
import com.peopleNet.sotp.dal.dao.pluginStatisticDtoMapper;
import com.peopleNet.sotp.dal.dao.userInfoDtoMapper;
import com.peopleNet.sotp.dal.model.RulePolicyDto;
import com.peopleNet.sotp.dal.model.appVersionInfoDto;
import com.peopleNet.sotp.dal.model.authFeatureDto;
import com.peopleNet.sotp.dal.model.pluginInfoDto;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.*;
import com.peopleNet.sotp.serviceV2.IPluginMagService;
import com.peopleNet.sotp.thrift.service.PluginSaveHelper;
import com.peopleNet.sotp.thrift.service.SotpPlugin;
import com.peopleNet.sotp.thrift.service.SotpRet;
import com.peopleNet.sotp.util.Base64;
import com.peopleNet.sotp.util.CommonConfig;
import com.peopleNet.sotp.util.UpdateUtil;
import com.peopleNet.sotp.vo.ResultVO;
import com.peopleNet.sotp.vo.UserRequestMsgV2;

@Service
public class PluginMagServiceImpl implements IPluginMagService {
	private static LogUtil logger = LogUtil.getLogger(PluginMagServiceImpl.class);
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
	@Autowired
	private IVerifyService verifyService;

	private String THOR_URL = CommonConfig.get("THOR_URL");
	private int CONNECT_THOR_PROTOCOL = Integer.parseInt(CommonConfig.get("CONNECT_THOR_PROTOCOL").trim());
	private String PLUGIN_STRORE_MODE = CommonConfig.get("PLUGIN_SAVE_MODE").trim();

	/*
	 * 注册申请插件
	 */
	@Override
	public String pluginReg(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		int ret = ErrorConstant.RET_OK;
		String reqmsg = request.getQueryString();
		String errMsg = "";

		// 参数定义
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		if (null == userId) {
			userId = phoneNum;
		}
		String pluginVersion = requestMsg.getPluginVersion();
		String pin = requestMsg.getPin();
		String devInfo = requestMsg.getDevInfo();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();
		String envInfo = requestMsg.getEnvInfo();

		logger.debug("reqmsg:" + reqmsg);
		// 业务类型判断
		if (!ServiceConstant.PLUGIN_REG.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null == resultVO || resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, "");
		}

		// 获取申请策略
		RulePolicyDto applyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.APPLY, appId);
		if (applyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpapplypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", "", errMsg, reqmsg,
			        request, "");
		}

		// 插件申请策略
		resultVO = policyService.pluginReqPolicy(phoneNum, appId, applyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, "");
		}

		// 认证硬件信息
		authFeatureDto authFeaInfo = paraHandle.getNewAuthFeature(devInfo);
		if (authFeaInfo == null) {
			errMsg = "new authFeaInfo error.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.SYSTEM_MALLOC_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		// 插件信息
		pluginInfoDto pluginInfo = paraHandle.getNewPluginfoV2(userId, phoneNum, pin);
		if (pluginInfo == null) {
			errMsg = "new pluginInfo error.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.SYSTEM_MALLOC_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		resultVO = verifyService.verifyAppInfo(service, pluginInfo, envInfo, appId, applyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, "");
		}

		SotpPlugin plugin = businessService.genThorPlugin(pluginVersion, authFeaInfo, pluginInfo, appId, service,
		        applyPolicyInfo);
		if (null == plugin) {
			errMsg = "generate plugin failed!";
			logger.error(errMsg);
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
			return paraHandle.responseHandle(appId, service, ErrorConstant.THOR_SYSTEM_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

 		try {
			this.dbOperationService.insertRegPluginIntoDB(authFeaInfo, pluginInfo, phoneNum, userId, appId);
		} catch (Exception e) {
			errMsg = "insert into db error!";
			logger.error(errMsg + e.toString());
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
			return paraHandle.responseHandle(appId, service, ErrorConstant.SQL_WRITEDB_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK); 
		return paraHandle.responseHandle(appId, service, ret, plugin.getPlugin(), plugin.getFile(), "", reqmsg,
		        request, "");
	}

	/*
	 * 解绑插件
	 */
	@Override
	public String plugindel(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String errMsg = "";
		String reqmsg = request.getQueryString();

		String phoneNum = requestMsg.getPhoneNum();

		String sotpId = requestMsg.getSotpId();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();
		String delPluginId = requestMsg.getDelPluginId();

		// 业务类型判断
		if (!ServiceConstant.PLUGIN_UNWRAP.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
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
		pluginInfoDto pInfo = paraHandle.getPluginfo(sotpId);
		pluginInfoDto delpInfo = paraHandle.getPluginfo(delPluginId);
		if (pInfo == null || delpInfo == null) {
			errMsg = "sotpId or delPluginId db not exist";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGINID_NOEXIST, "", "", errMsg,
			        reqmsg, request, sotpId);
		}

		resultVO = verifyService.verifyPluginStatusIsReady(pInfo, verifyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		resultVO = verifyService.verifyUserInfo(pInfo, phoneNum);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		if (delPluginId == null || delPluginId.length() <= 0) {
			errMsg = "delPluginId is null";
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
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, "", "", "", reqmsg, request, sotpId);

	}

	/*
	 * 更新插件
	 */
	@Override
	public String pluginupdate(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String errMsg = "";
		String reqmsg = request.getQueryString();

		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		if (null == userId) {
			userId = phoneNum;
		}
		String pluginVersion = requestMsg.getPluginVersion();
		String sotpId = requestMsg.getSotpId();
		String envInfo = requestMsg.getEnvInfo();
		String sotpCode = requestMsg.getSotpCode();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();

		// 业务类型判断
		if (!ServiceConstant.PLUGIN_UPDATE.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null == resultVO || resultVO.getCode() != ErrorConstant.RET_OK) {
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

		// 获取申请策略
		RulePolicyDto applyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.APPLY, appId);
		if (applyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpapplypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", "", errMsg, reqmsg,
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
		int ret = businessService.verifySotpCodeWithChallenge(plInfo, authFeaInfo, sotpCode, true, verifyPolicyInfo);
		if (ret != ErrorConstant.RET_OK) {
			errMsg = "verifySotpCodeWithChallenge error. result:" + ret;
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ret, "", "", errMsg, reqmsg, request, sotpId);
		}

		// 插件申请策略
		resultVO = policyService.pluginReqPolicy(phoneNum, appId, applyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
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
			        request, sotpId);
		}

		try {
			this.dbOperationService.insertUpdatePluginIntoDB(authFeaInfo, plInfo, newpluginInfo, appId);
		} catch (Exception e) {
			errMsg = "insert into db error.";
			logger.error(errMsg + e.toString());
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
			return paraHandle.responseHandle(appId, service, ErrorConstant.SQL_WRITEDB_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ret, plugin.getPlugin(), plugin.getFile(), "", reqmsg,
		        request, sotpId);
	}

	/*
	 * 获取绑定设备列表 返回：devname@start_time@devId
	 */
	@Override
	public String getdevlist(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String service = requestMsg.getService();
		String sotpId = requestMsg.getSotpId();
		String appId = requestMsg.getAppId();
		String pluginStatus = requestMsg.getPluginStatus();

		if (!ServiceConstant.PLUGIN_LISTALL.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		// 判断 SOTPID
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("appId", appId);
		parms.put("phoneNum", phoneNum);

		StringBuilder devlist = null;
		List<String> hardwares = null;
		if (pluginStatus != null && pluginStatus != "") {
			int plStatus = Integer.parseInt(pluginStatus);
			parms.put("status", plStatus);
			try {
				hardwares = authFeatureMapper.selectByPhoneNumAndStatus(parms);
			} catch (SQLException e) {
				logger.error("authFeatureMapper selectByPhoneNumAndStatus sql error.msg:%s", e.toString());
			}
		} else {
			try {
				hardwares = authFeatureMapper.selectByPhoneNum(parms);
			} catch (SQLException e) {
				logger.error("authFeatureMapper selectByPhoneNum sql error.msg:%s", e.toString());
			}
		}

		if (hardwares == null || hardwares.isEmpty()) {
			return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, "", "", null, reqmsg, request, sotpId);
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
			if (devContent[3].equals(sotpId)) {
				devlist.insert(0, devMsg + "&");
			} else {
				devlist.append("&").append(devMsg);
			}
		}
		logger.debug("devlist:" + devlist);

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, devlist.toString(), "", "success",
		        reqmsg, request, sotpId);
	}

	@Override
	public String getPluginDevList(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String service = requestMsg.getService();
		String sotpId = requestMsg.getSotpId();
		String appId = requestMsg.getAppId();
		String devId = requestMsg.getDevId();
		String pluginStatus = requestMsg.getPluginStatus();

		if (!ServiceConstant.PLUGIN_GETDEVLIST.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		// 判断 SOTPID
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("appId", appId);
		parms.put("phoneNum", phoneNum);

		StringBuilder devlist = null;
		List<Map<String, String>> hardwares = null;
		if (pluginStatus != null && pluginStatus != "") {
			int plStatus = Integer.parseInt(pluginStatus);
			parms.put("status", plStatus);
			try {
				hardwares = authFeatureMapper.selectByPhoneNumAndStatusWithDevId(parms);
			} catch (SQLException e) {
				logger.error("authFeatureMapper selectByPhoneNumAndStatusWithDevId sql error.msg:%s", e.toString());
			}
		} else {
			try {
				hardwares = authFeatureMapper.selectByPhoneNumWithDevId(parms);
			} catch (SQLException e) {
				logger.error("authFeatureMapper selectByPhoneNumWithDevId sql error.msg:%s", e.toString());
			}
		}

		if (hardwares == null || hardwares.isEmpty()) {
			return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, "", "", null, reqmsg, request, sotpId);
		}

		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsondev = new JSONObject();
		for (int i = 0, len = hardwares.size(); i < len; i++) {
			Map<String, String> devMsg = hardwares.get(i);
			boolean needUpdate = policyService.checkUpdateWithSotpIdAndStatus(devMsg.get("sotpId"),
			        String.valueOf(devMsg.get("sotpStatus")), appId);
			jsondev.element("devName", devMsg.get("devName"));
			jsondev.element("devType", devMsg.get("devType"));
			jsondev.element("bindTime", devMsg.get("bindTime"));
			jsondev.element("sotpId", devMsg.get("sotpId"));
			if (devMsg.get("devId") == null || devMsg.get("devId") == "") {
				jsondev.element("devId", "");
			} else {
				jsondev.element("devId", devMsg.get("devId"));
			}
			if (needUpdate) {
				jsondev.element("sotpStatus", Integer.toString(Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NEEDUPDATE));
			} else {
				jsondev.element("sotpStatus", devMsg.get("sotpStatus"));
			}
			if (devMsg.get("sotpId").equals(sotpId)) {
				jsonArray.add(0, jsondev);
			} else {
				jsonArray.add(i, jsondev);
			}
		}
		jsonObject.element("devlist", jsonArray);
		logger.debug("devlist:" + devlist);
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandleWithSotpInfo(appId, service, ErrorConstant.RET_OK, "", "", "success", reqmsg,
		        request, "", "", "", "", jsonObject);
	}

	/*
	 * 激活插件
	 */
	@Override
	public String activePlugin(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		int ret = ErrorConstant.RET_OK;
		String errMsg = "";

		String service = requestMsg.getService();

		String activeCode = requestMsg.getActiveCode();
		String sotpId = requestMsg.getSotpId();
		String appId = requestMsg.getAppId();

		logger.debug("activePluginInterface activeCode-----" + activeCode);
		if (!ServiceConstant.PLUGIN_ACTIVATE.equals(service)) {
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

		// 获取激活策略
		RulePolicyDto activePolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.ACTIVE, appId);
		if (activePolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpactivepolicy is null";
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
		if (plInfo.getStatus() != Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NO_ACTIVATE) {
			errMsg = "plugin status is " + plInfo.getStatus();
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PLUGIN_ACTIVECODE_STATUS_ERR, "", null,
			        errMsg, reqmsg, request, sotpId);
		}

		// 验证激活策略
		resultVO = policyService.pluginActivePolicy(plInfo, activePolicyInfo);
		if (resultVO.getCode() == ErrorConstant.PLUGIN_HAVE_ACTIVE_MAX_TIMES) {
			// 激活次数过多，插件置为作废
			this.pluginService.updatePluginInfoStatusById(sotpId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
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
		return paraHandle.responseHandle(appId, service, ret, "", null, errMsg, reqmsg, request, sotpId);
	}

	/*
	 * 修改设备名称
	 */
	@Override
	public String setDevAlias(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		String sotpId = requestMsg.getSotpId();

		String alias = requestMsg.getAlias();
		// System.out.println(com.peopleNet.sotp.util.StringUtils.getEncoding(alias));
		try {
			alias = new String(alias.getBytes(com.peopleNet.sotp.util.StringUtils.getEncoding(alias)), "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();

		if (!ServiceConstant.PLUGIN_SETDEVALIAS.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		logger.debug("setDevAlias parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId
		        + ", alias:" + alias);

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
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
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, "", null, "", reqmsg, request, sotpId);
	}

	/*
	 * 同步共享密钥
	 */
	@Override
	public String synShareKey(UserRequestMsgV2 requestMsg, HttpServletRequest request) {

		String reqmsg = request.getQueryString();
		String errMsg = "";

		String businessId = requestMsg.getBusinessId();
		String action = requestMsg.getAction();
		String shareKey = requestMsg.getShareKey();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();

		if (!ServiceConstant.PLUGIN_SYNSHAREKEY.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		logger.debug("synsharekey parameter service:" + service + ", businessId:" + businessId + ", action:" + action
		        + ", sharekey:" + shareKey);

		// 参数判断
		/*
		 * ResultVO resultVO = paraHandle.paraFormat(reqmsg, type); if (null !=
		 * resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
		 * logger.error(resultVO.getMsg()); return retStr(type,
		 * resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg); }
		 */

		// 连接密码机进行加密转换
		SotpRet sotpRes = null;
		if (CONNECT_THOR_PROTOCOL == Constant.THOR_PROTOCOL.SOTP_CONNECT_THOR_PROTOCOL_HTTP) {

			logger.error("crypto connect sotp http protocol not write");
		} else if (CONNECT_THOR_PROTOCOL == Constant.THOR_PROTOCOL.SOTP_CONNECT_THOR_PROTOCOL_TCP) {
			try {
				sotpRes = thriftInvokeService.shareKey(Integer.parseInt(action), shareKey);
			} catch (TException e) {

				logger.error("invoke thrift encryptNew error.msg:%s", e.toString());
			}
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
	public String cryptConvert(UserRequestMsgV2 requestMsg, HttpServletRequest request) {

		String reqmsg = request.getQueryString();
		String errMsg = "";

		String flag = requestMsg.getFlag();
		String phoneNum = requestMsg.getPhoneNum();
		String sotpId = requestMsg.getSotpId();
		String transEncryData = requestMsg.getTransEncryData();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();

		if (!ServiceConstant.PLUGIN_CRYPTCONVERT.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		logger.debug("cryptconvert parameter flag:" + flag + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId
		        + ", transEncryData:" + transEncryData);

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
		if (CONNECT_THOR_PROTOCOL == Constant.THOR_PROTOCOL.SOTP_CONNECT_THOR_PROTOCOL_HTTP) {

			logger.error("crypto connect sotp http protocol not write");
		} else if (CONNECT_THOR_PROTOCOL == Constant.THOR_PROTOCOL.SOTP_CONNECT_THOR_PROTOCOL_TCP) {
			try {
				sotpRes = thriftInvokeService.transEncry(Integer.parseInt(flag), plInfo.getPluginKey(), transEncryData);
			} catch (TException e) {

				logger.error("invoke thrift encryptNew error.msg:%s", e.toString());
			}
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
	public String getstatus(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		int ret = ErrorConstant.RET_OK;
		String reqmsg = request.getQueryString();
		String errMsg = "";

		String phoneNum = requestMsg.getPhoneNum();

		String sotpId = requestMsg.getSotpId();
		String sotpCode = requestMsg.getSotpCode();
		String envInfo = requestMsg.getEnvInfo();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();

		if (!ServiceConstant.PLUGIN_GETSTATUS.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
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
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, "", "", "success", reqmsg, request, sotpId);
	}

	@Override
	public String pluginResissue(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		int ret = ErrorConstant.RET_OK;
		String reqmsg = request.getQueryString();
		String errMsg = "";

		// 参数定义
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		if (null == userId) {
			userId = phoneNum;
		}
		String pluginVersion = requestMsg.getPluginVersion();
		String pin = requestMsg.getPin();
		String devId = requestMsg.getDevId();
		String devInfo = requestMsg.getDevInfo();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();

		logger.debug("reqmsg:" + reqmsg);
		// 业务类型判断
		if (!ServiceConstant.PLUGIN_RESISSUE.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null == resultVO || resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
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

		// 获取申请策略
		RulePolicyDto applyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.APPLY, appId);
		if (applyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpapplypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", "", errMsg, reqmsg,
			        request, "");
		}

		// 插件申请策略
		resultVO = policyService.pluginReqPolicy(phoneNum, appId, applyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, "");
		}

		// TODO 根据设备ID和手机号获取原硬件和插件的信息
		pluginInfoDto oldPlInfo = paraHandle.getOldPluginInfo(devId, phoneNum);
		if (oldPlInfo == null) {
			errMsg = "old pluginInfo not exist.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PLUGIN_RESISSUE_OLDINFO_ERROR, "", "",
			        errMsg, reqmsg, request, "");
		}
		if (oldPlInfo.getStatus() == Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_HANGUP
		        || oldPlInfo.getStatus() == Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS) {
			errMsg = "old pluginInfo status :" + oldPlInfo.getStatus();
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PLUGIN_RESISSUE_OLDINFO_ERROR, "", "",
			        errMsg, reqmsg, request, "");
		}
		authFeatureDto oldAuthFeaInfo = paraHandle.getAuthFeature(oldPlInfo.getPluginId());
		if (oldAuthFeaInfo == null) {
			errMsg = "old AuthFeaInfo not exist.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PLUGIN_RESISSUE_OLDINFO_ERROR, "", "",
			        errMsg, reqmsg, request, "");
		}

		// 插件信息
		pluginInfoDto pluginInfo = paraHandle.getNewPluginfoV2(userId, phoneNum, pin);
		if (pluginInfo == null) {
			errMsg = "new pluginInfo error.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.SYSTEM_MALLOC_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		SotpPlugin plugin = businessService.genThorPlugin(pluginVersion, oldAuthFeaInfo, pluginInfo, appId, service,
		        applyPolicyInfo);
		if (null == plugin) {
			errMsg = "generate plugin failed!";
			logger.error(errMsg);
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
			return paraHandle.responseHandle(appId, service, ErrorConstant.THOR_SYSTEM_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		// 原插件状态
		oldAuthFeaInfo.setId(null);
		pluginInfo.setStatus(oldPlInfo.getStatus());
		/*
		 * if (oldPlInfo.getStatus() ==
		 * Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NO_ACTIVATE){
		 * pluginInfo.setStatus(Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY)
		 * ; }
		 */
		try {
			this.dbOperationService.insertRegPluginIntoDB(oldAuthFeaInfo, pluginInfo, phoneNum, userId, appId);
		} catch (Exception e) {
			errMsg = "insert into db error!";
			logger.error(errMsg + e.toString());
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
			return paraHandle.responseHandle(appId, service, ErrorConstant.SQL_WRITEDB_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandleWithSotpInfo(appId, service, ret, plugin.getPlugin(), plugin.getFile(), "",
		        reqmsg, request, oldAuthFeaInfo.getDevId(), pluginInfo.getPluginId(),
		        String.valueOf(pluginInfo.getStatus()), "", null);
	}

	@Override
	public String pluginGetStatus(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		String errMsg = "";
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		String sotpId = requestMsg.getSotpId();
		String devId = requestMsg.getDevId();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();
		int pluginStatus = Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS;

		if (!ServiceConstant.PLUGIN_GETSTATUS.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}
		logger.debug("pluginGetStatus parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId
		        + ", devId:" + devId);
		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}
		if (sotpId != null && sotpId != "") {
			pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
			// 硬件信息
			authFeatureDto authFeaInfo = paraHandle.getAuthFeature(sotpId);
			if (null == plInfo || null == authFeaInfo) {
				errMsg = "pluginInfo or authFeaInfo is not exist in db.";
				/*
				 * logger.debug(errMsg); return
				 * paraHandle.responseHandleWithSotpInfo(appId, service,
				 * ErrorConstant.RET_OK, null, null, null, reqmsg, request,
				 * devId, sotpId, "-1", null, null);
				 */
				logger.error(errMsg);
				return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST, "",
				        "", errMsg, reqmsg, request, sotpId);
			}
			// 验证用户信息
			resultVO = verifyService.verifyUserInfo(plInfo, phoneNum);
			if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
				logger.error(resultVO.getMsg());
				return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(),
				        reqmsg, request, sotpId);
			}
			// 插件状态
			pluginStatus = plInfo.getStatus();
			if (pluginStatus == Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY) {
				boolean needUpdate = policyService.checkNeedUpdate(plInfo, appId);
				if (needUpdate) {
					pluginStatus = Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NEEDUPDATE;
				}
			}
		} else {
			pluginInfoDto latestPlInfo = paraHandle.getOldPluginInfo(devId, phoneNum);
			if (latestPlInfo == null) {
				errMsg = "latest pluginInfo not exist.";
				/*
				 * logger.debug(errMsg); return
				 * paraHandle.responseHandleWithSotpInfo(appId, service,
				 * ErrorConstant.RET_OK, null, null, null, reqmsg, request,
				 * devId, sotpId, "-1", null, null);
				 */
				logger.error(errMsg);
				return paraHandle.responseHandle(appId, service, ErrorConstant.PLUGIN_RESISSUE_OLDINFO_ERROR, "", "",
				        errMsg, reqmsg, request, sotpId);
			}
			pluginStatus = latestPlInfo.getStatus();
		}

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandleWithSotpInfo(appId, service, ErrorConstant.RET_OK, "", null, "", reqmsg,
		        request, devId, sotpId, String.valueOf(pluginStatus), "", null);
	}

	@Override
	public String pluginUnlock(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		String errMsg = "";
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		String sotpId = requestMsg.getSotpId();
		// String devId = requestMsg.getDevId();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();

		if (!ServiceConstant.PLUGIN_UNLOCK.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}
		logger.debug("pluginUnlock parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId);
		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
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
		// 验证用户信息
		resultVO = verifyService.verifyUserInfo(plInfo, phoneNum);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}
		// 判断插件状态是否是锁定状态
		if (plInfo.getStatus() != Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_LOCKED) {
			errMsg = "plugin Unlock status is not locked. plugin status: " + plInfo.getStatus();
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PLUGIN_STATUS_ERROR, "", null, errMsg,
			        reqmsg, request, sotpId);
		}
		// 解锁插件
		this.pluginService.updatePluginInfoStatusById(sotpId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, "", null, "", reqmsg, request, sotpId);
	}

	@Override
	public String pluginUnhang(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		String errMsg = "";
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		String sotpId = requestMsg.getSotpId();
		// String devId = requestMsg.getDevId();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();

		if (!ServiceConstant.PLUGIN_UNHANG.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}
		logger.debug("pluginUnHang parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId);
		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
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
		// 验证用户信息
		resultVO = verifyService.verifyUserInfo(plInfo, phoneNum);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}
		// 判断插件状态是否是挂起状态
		if (plInfo.getStatus() != Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_HANGUP) {
			errMsg = "plugin Unhang status is not hangup. plugin status: " + plInfo.getStatus();
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PLUGIN_STATUS_ERROR, "", null, errMsg,
			        reqmsg, request, sotpId);
		}
		// 解挂插件
		this.pluginService.updatePluginInfoStatusById(sotpId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, "", null, "", reqmsg, request, sotpId);
	}

	@Override
	public String pluginHangup(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		String errMsg = "";
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		String sotpId = requestMsg.getSotpId();
		// String devId = requestMsg.getDevId();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();

		if (!ServiceConstant.PLUGIN_HANGUP.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}
		logger.debug("pluginHangup parameter userId:" + userId + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId);
		// 参数判断
		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
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
		// 验证用户信息
		resultVO = verifyService.verifyUserInfo(plInfo, phoneNum);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", null, resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}
		// 判断插件状态是否是就绪状态
		if (plInfo.getStatus() != Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY) {
			errMsg = "plugin hangup status is not ready. plugin status: " + plInfo.getStatus();
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PLUGIN_STATUS_ERROR, "", null, errMsg,
			        reqmsg, request, sotpId);
		}
		// 挂起插件
		this.pluginService.updatePluginInfoStatusById(sotpId, Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_HANGUP);
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, "", null, "", reqmsg, request, sotpId);
	}

	@Override
	public String pluginApply2(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		int ret = ErrorConstant.RET_OK;
		String reqmsg = request.getQueryString();
		String errMsg = "";

		// 参数定义
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		if (null == userId) {
			userId = phoneNum;
		}
		String pluginVersion = requestMsg.getPluginVersion();
		String pin = requestMsg.getPin();
		String devInfo = requestMsg.getDevInfo();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();

		logger.debug("reqmsg:" + reqmsg);
		// 业务类型判断
		if (!ServiceConstant.PLUGIN_APPLY2.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null == resultVO || resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
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

		// 获取申请策略
		RulePolicyDto applyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.APPLY, appId);
		if (applyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpapplypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", "", errMsg, reqmsg,
			        request, "");
		}

		// 从devInfo中获取isRoot
		String isRoot = paraHandle.getIsRootFromDevInfo(devInfo);
		// 已经root的设备验证策略是否允许
		if (Constant.ISROOT.ISROOT.equals(isRoot)) {
			resultVO = policyService.checkRootPolicy(applyPolicyInfo);
			if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
				logger.error(resultVO.getMsg());
				return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
				        request, "");
			}
		}
		// 插件申请策略
		resultVO = policyService.pluginReqPolicy(phoneNum, appId, applyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, "");
		}

		// 认证硬件信息
		authFeatureDto authFeaInfo = paraHandle.getNewAuthFeature(devInfo);
		if (authFeaInfo == null) {
			errMsg = "new authFeaInfo error.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.SYSTEM_MALLOC_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		// 插件信息
		pluginInfoDto pluginInfo = paraHandle.getNewPluginfoV2(userId, phoneNum, pin);
		if (pluginInfo == null) {
			errMsg = "new pluginInfo error.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.SYSTEM_MALLOC_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		SotpPlugin plugin = businessService.genThorPlugin(pluginVersion, authFeaInfo, pluginInfo, appId, service,
		        applyPolicyInfo);
		if (null == plugin) {
			errMsg = "generate plugin failed!";
			logger.error(errMsg);
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
			return paraHandle.responseHandle(appId, service, ErrorConstant.THOR_SYSTEM_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}

		try {
			this.dbOperationService.insertRegPluginIntoDB(authFeaInfo, pluginInfo, phoneNum, userId, appId);
		} catch (Exception e) {
			errMsg = "insert into db error!";
			logger.error(errMsg + e.toString());
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
			return paraHandle.responseHandle(appId, service, ErrorConstant.SQL_WRITEDB_ERR, "", "", errMsg, reqmsg,
			        request, "");
		}
		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandleWithSotpInfo(appId, service, ret, plugin.getPlugin(), plugin.getFile(), "",
		        reqmsg, request, authFeaInfo.getDevId(), pluginInfo.getPluginId(),
		        String.valueOf(pluginInfo.getStatus()), authFeaInfo.getDevName(), null);
	}

	/*
	 * 更新插件2
	 */
	@Override
	public String pluginUpdate2(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String errMsg = "";
		String reqmsg = request.getQueryString();

		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		if (null == userId) {
			userId = phoneNum;
		}
		String pluginVersion = requestMsg.getPluginVersion();
		String sotpId = requestMsg.getSotpId();
		String envInfo = requestMsg.getEnvInfo();
		String sotpCode = requestMsg.getSotpCode();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();

		// 业务类型判断
		if (!ServiceConstant.PLUGIN_UPDATE2.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null == resultVO || resultVO.getCode() != ErrorConstant.RET_OK) {
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

		// 获取申请策略
		RulePolicyDto applyPolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.APPLY, appId);
		if (applyPolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpapplypolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		// 获取更新策略
		RulePolicyDto updatePolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.UPDATE, appId);
		if (updatePolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpupdatepolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", "", errMsg, reqmsg,
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
		int ret = businessService.verifySotpCodeWithChallenge(plInfo, authFeaInfo, sotpCode, true, verifyPolicyInfo);
		if (ret != ErrorConstant.RET_OK) {
			errMsg = "verifySotpCodeWithChallenge error. result:" + ret;
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ret, "", "", errMsg, reqmsg, request, sotpId);
		}

		// 从envInfo中获取isRoot
		String isRoot = paraHandle.getIsRootFromDevInfo(envInfo);
		// 已经root的设备验证策略是否允许
		if (Constant.ISROOT.ISROOT.equals(isRoot)) {
			resultVO = policyService.checkRootPolicy(updatePolicyInfo);
			if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
				logger.error(resultVO.getMsg());
				return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
				        request, sotpId);
			}
		}
		// 插件申请策略
		resultVO = policyService.pluginReqPolicy(phoneNum, appId, applyPolicyInfo);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
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
			        request, sotpId);
		}

		try {
			this.dbOperationService.insertUpdatePluginIntoDB(authFeaInfo, plInfo, newpluginInfo, appId);
		} catch (Exception e) {
			errMsg = "insert into db error.";
			logger.error(errMsg + e.toString());
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
			return paraHandle.responseHandle(appId, service, ErrorConstant.SQL_WRITEDB_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		// 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		return paraHandle.responseHandleWithSotpInfo(appId, service, ret, plugin.getPlugin(), plugin.getFile(), "",
		        reqmsg, request, authFeaInfo.getDevId(), newpluginInfo.getPluginId(),
		        String.valueOf(newpluginInfo.getStatus()), "", null);
	}

	@Override
	public String pluginActive2(UserRequestMsgV2 requestMsg, HttpServletRequest request) {
		String reqmsg = request.getQueryString();
		int ret = ErrorConstant.RET_OK;
		String errMsg = "";

		String service = requestMsg.getService();
		String phoneNum = requestMsg.getPhoneNum();
		String sotpCode = requestMsg.getActiveCode();
		String sotpId = requestMsg.getSotpId();
		String activePluginId = requestMsg.getActivePluginId();
		String appId = requestMsg.getAppId();
		String envInfo = requestMsg.getEnvInfo();

		logger.debug("pluginActive2Interface sotpCode-----" + sotpCode);
		if (!ServiceConstant.PLUGIN_OTHERACTIVE.equals(service)) {
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

		// 获取激活策略
		RulePolicyDto activePolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.ACTIVE, appId);
		if (activePolicyInfo == null) {
			errMsg = "redis RulePolicyDto sotpactivepolicy is null";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.POLICY_FETCH_ERROR, "", null, errMsg, reqmsg,
			        request, sotpId);
		}

		// 从envInfo中获取isRoot
		String isRoot = paraHandle.getIsRootFromDevInfo(envInfo);
		// 已经root的设备验证策略是否允许
		if (Constant.ISROOT.ISROOT.equals(isRoot)) {
			resultVO = policyService.checkRootPolicy(activePolicyInfo);
			if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
				logger.error(resultVO.getMsg());
				return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
				        request, sotpId);
			}
		}
		// 待激活插件信息
		pluginInfoDto activePlInfo = paraHandle.getPluginfo(activePluginId);
		if (null == activePlInfo) {
			errMsg = "activePlInfo is not exist in db.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST, "", "",
			        errMsg, reqmsg, request, sotpId);
		}
		resultVO = verifyService.verifyUserInfo(activePlInfo, phoneNum);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			errMsg = "phoneNum not right";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_USERID_NOTMATCH_SID, "", "", errMsg,
			        reqmsg, request, sotpId);
		}
		// 使用已激活插件去激活未激活插件 已激活插件ID：sotpId 未激活插件activePluginId
		if (sotpId != null && sotpId != "" && sotpCode != null && sotpCode != "") {
			// 已激活插件信息
			pluginInfoDto plInfo = paraHandle.getPluginfo(sotpId);
			// 硬件信息
			authFeatureDto authFeaInfo = paraHandle.getAuthFeature(sotpId);
			if (null == plInfo || null == authFeaInfo) {
				errMsg = "pluginInfo or authFeaInfo is not exist in db.";
				logger.error(errMsg);
				return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST, "",
				        "", errMsg, reqmsg, request, sotpId);
			}
			// System.out.println(activePlInfo.getStatus());
			if (activePlInfo.getStatus() != Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NO_ACTIVATE) {
				errMsg = "activePlugin status is " + activePlInfo.getStatus();
				logger.error(errMsg);
				return paraHandle.responseHandle(appId, service, ErrorConstant.PLUGIN_ACTIVECODE_STATUS_ERR, "", null,
				        errMsg, reqmsg, request, sotpId);
			}
			// 验证激活策略
			resultVO = policyService.pluginActivePolicy(plInfo, activePolicyInfo);
			if (resultVO.getCode() == ErrorConstant.PLUGIN_HAVE_ACTIVE_MAX_TIMES) {
				// 激活次数过多，插件置为作废
				this.pluginService.updatePluginInfoStatusById(sotpId,
				        Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
				logger.error(resultVO.getMsg());
				return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
				        request, sotpId);
			}
			if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
				logger.error(resultVO.getMsg());
				return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
				        request, sotpId);
			}
			// 验证sotpCode
			ret = businessService.verifySotpCodeWithChallenge(plInfo, authFeaInfo, sotpCode, false, verifyPolicyInfo);
			if (ret != ErrorConstant.RET_OK) {
				errMsg = "active sotpCode error!";
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
			// 正确
			// 保存返回状态信息到request中，以便统计接口成功与否
			request.setAttribute("_retStatus", ErrorConstant.RET_OK);
			return paraHandle.responseHandle(appId, service, ret, "", null, errMsg, reqmsg, request, sotpId);
		}
		// 使用activePluginId激活插件
		else {
			// 更新 激活计数
			resultVO = verifyService.verifyUserInfo(activePlInfo, phoneNum);
			if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
				errMsg = "phoneNum not right";
				logger.error(errMsg);
				return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_USERID_NOTMATCH_SID, "", "",
				        errMsg, reqmsg, request, sotpId);
			}
			UpdateUtil updateInfo = new UpdateUtil();
			activePlInfo.setActiveUseCnt(activePlInfo.getActiveUseCnt() + 1);
			Date date = new Date();
			updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.START_TIME, date);
			updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.ACTIVE_USECNT, activePlInfo.getActiveUseCnt());
			this.pluginService.updatePluginInfoStatusById(activePluginId,
			        Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);
			updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, activePlInfo.getPluginId());
			Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
			this.pluginInfoMapper.updateHashMapByPluginId(updateMap);
			// 正确
			// 保存返回状态信息到request中，以便统计接口成功与否
			request.setAttribute("_retStatus", ErrorConstant.RET_OK);
			return paraHandle.responseHandle(appId, service, ret, "", null, errMsg, reqmsg, request, sotpId);
		}
	}

	/*
	 * 验证APP信息完整性
	 */
	@Override
	public String verifyAppInfo(UserRequestMsgV2 requestMsg, HttpServletRequest request) {

		String errMsg = "";
		String reqmsg = request.getQueryString();

		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		if (null == userId) {
			userId = phoneNum;
		}
		String pluginVersion = requestMsg.getPluginVersion();
		String sotpId = requestMsg.getSotpId();
		String appId = requestMsg.getAppId();
		String service = requestMsg.getService();
		String envInfo = requestMsg.getEnvInfo();
		int ret = ErrorConstant.RET_OK;

		// 业务类型判断
		if (!ServiceConstant.PLUGIN_VERIFYAPPINFO.equals(service)) {
			errMsg = "service type is not right.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		ResultVO resultVO = paraHandle.paraFormatV2(requestMsg);
		if (null == resultVO || resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return paraHandle.responseHandle(appId, service, resultVO.getCode(), "", "", resultVO.getMsg(), reqmsg,
			        request, sotpId);
		}

		String devJson = Base64.decode(envInfo);
		JSONObject jsonObject = ControllerHelper.parseJsonString(devJson);
		if (null == jsonObject) {
			errMsg = "dev format wrong";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_ILLEGAL, "", "", errMsg, reqmsg,
			        request, sotpId);
		}

		// 验证app信息
		appVersionInfoDto appVersionInfo = cacheService.getAPPVersionInfoByCode(appId);
		if (null == appVersionInfo) {
			errMsg = "no APPInfo or can not get APPInfo.";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ret, "", null, errMsg, reqmsg, request, sotpId);
		}
		String appHashValue = appVersionInfo.getHash_value();
		String appHash = jsonObject.getString("appval");
		if (StringUtils.isEmpty(appHash) || !appHash.equals(appHashValue)) {
			errMsg = " appHash can not pair(" + appHash + " <> " + appHashValue + ").";
			logger.error(errMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_HASH_NOTMATCH, "", "", errMsg,
			        reqmsg, request, sotpId);
		}

		String appSign = jsonObject.getString("signval");
		String appSignature = appVersionInfo.getSignature();
		if (StringUtils.isEmpty(appSign) || !appSign.equals(appSignature)) {
			errMsg = " signval can not pair(" + appSign + " <> " + appSignature + ").";
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARACONT_SIGN_NOTMATCH, "", "", errMsg,
			        reqmsg, request, sotpId);
		}
		return paraHandle.responseHandle(appId, service, ErrorConstant.RET_OK, "", "", "", reqmsg, request, sotpId);
	}

}
