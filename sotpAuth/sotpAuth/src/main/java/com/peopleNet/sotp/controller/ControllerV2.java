package com.peopleNet.sotp.controller;

import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.constant.ServiceConstant;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.IParaHandle;
import com.peopleNet.sotp.serviceV2.IGeneralService;
import com.peopleNet.sotp.serviceV2.IPluginMagService;
import com.peopleNet.sotp.vo.ResultVO;
import com.peopleNet.sotp.vo.UserRequestMsgV2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ControllerV2 {
	private static LogUtil logger = LogUtil.getLogger(ControllerV2.class);
	@Autowired
	private IPluginMagService pluginMagService;
	@Autowired
	private IGeneralService generalService;
	@Autowired
	private IParaHandle paraHandleService;
	@Autowired
	private IParaHandle paraHandle;

	@RequestMapping(value = "portal")
	@ResponseBody
	public String auth(@ModelAttribute UserRequestMsgV2 requestMsg, HttpServletRequest request) {

		String service = requestMsg.getService();
		String version = requestMsg.getVersion();
		String appId = requestMsg.getAppId();
		String errorMsg = "";
		String resultStr = "";

		// 获取版本信息校验
		if (!ServiceConstant.AUTH_VERSION.equals(version)) {
			errorMsg = "auth version is not v2.";
			logger.error(errorMsg);
			return errorMsg;
		}

		// 验证应用及签名信息
		ResultVO result = paraHandleService.checkSignature(requestMsg);
		if ((null == result) || result.getCode() != ErrorConstant.RET_OK) {
			logger.error(result.getMsg());
			return paraHandle.responseHandle(appId, service, result.getCode(), "", "", result.getMsg(), request.getQueryString(),
			        request, "");
		}

		if (null == service) {
			errorMsg = "service is null";
			logger.error(errorMsg);
			return paraHandle.responseHandle(appId, service, ErrorConstant.PARA_SERVICE_ERR, "", "",errorMsg, request.getQueryString(),
			        request, "");
		}

		// 获取service信息控制跳转
		// 插件下载
		if (ServiceConstant.PLUGIN_REG.equals(service)) {
			resultStr = pluginMagService.pluginReg(requestMsg, request);
			return resultStr;
		}

		// 插件更新
		if (ServiceConstant.PLUGIN_UPDATE.equals(service)) {
			resultStr = pluginMagService.pluginupdate(requestMsg, request);
			return resultStr;
		}

		// 解绑插件
		if (ServiceConstant.PLUGIN_UNWRAP.equals(service)) {
			resultStr = pluginMagService.plugindel(requestMsg, request);
			return resultStr;
		}

		// 获取用户插件信息
		if (ServiceConstant.PLUGIN_LISTALL.equals(service)) {
			resultStr = pluginMagService.getdevlist(requestMsg, request);
			return resultStr;
		}

		// 获取用户插件信息xian
		if (ServiceConstant.PLUGIN_GETDEVLIST.equals(service)) {
			resultStr = pluginMagService.getPluginDevList(requestMsg, request);
			return resultStr;
		}

		// 激活插件
		if (ServiceConstant.PLUGIN_ACTIVATE.equals(service)) {
			resultStr = pluginMagService.activePlugin(requestMsg, request);
			return resultStr;
		}

		/*
		 * // 获取插件状态 if (ServiceConstant.PLUGIN_GETSTATUS.equals(service)) {
		 * resultStr = pluginMagService.getstatus(requestMsg, request); return
		 * resultStr; }
		 */

		// 请求挑战码
		if (ServiceConstant.BUSINESS_GETCHALLENGE.equals(service)) {
			resultStr = generalService.getchallenge(requestMsg, request);
			return resultStr;
		}
		// 安全认证
		if (ServiceConstant.BUSINESS_AUTH.equals(service)) {
			resultStr = generalService.verify(requestMsg, request);
			return resultStr;
		}

		// 时间同步功能
		if (ServiceConstant.BUSINESS_TIME_SYNCHR.equals(service)) {
			resultStr = generalService.timeSynchr(requestMsg, request);
			return resultStr;
		}

		// 本地数据加解密
		if (ServiceConstant.BUSINESS_DATAENCRYPTION.equals(service)
		        || ServiceConstant.BUSINESS_DATADECRYPTION.equals(service)) {
			resultStr = generalService.crypto(requestMsg, request);
			return resultStr;
		}

		// 密码键盘解密
		if (ServiceConstant.BUSINESS_KEYBOARDDECRYPTION.equals(service)) {
			resultStr = generalService.decrypt(requestMsg, request);
			return resultStr;
		}
		
		//验证APP合法性
		if (ServiceConstant.PLUGIN_VERIFYAPPLEGITIMACY.equals(service)) {
			resultStr = generalService.verifyapplegitimacy(requestMsg, request);
			return resultStr;
		}

		// 生成会话密钥
		if (ServiceConstant.BUSINESS_GENSESSKEY.equals(service)) {
			resultStr = generalService.gensesskey(requestMsg, request);
			return resultStr;
		}
		// 修改插件保护码
		if (ServiceConstant.BUSINESS_MODSOTPIN.equals(service)) {
			resultStr = generalService.modsotpin(requestMsg, request);
			return resultStr;
		}
		// 修改预留信息
		if (ServiceConstant.BUSINESS_MODHOLDINFO.equals(service)) {
			resultStr = generalService.modholdinfo(requestMsg, request);
			return resultStr;
		}
		// 修改设备名称
		if (ServiceConstant.PLUGIN_SETDEVALIAS.equals(service)) {
			resultStr = pluginMagService.setDevAlias(requestMsg, request);
			return resultStr;
		}
		// 同步共享密钥
		if (ServiceConstant.PLUGIN_SYNSHAREKEY.equals(service)) {
			resultStr = pluginMagService.synShareKey(requestMsg, request);
			return resultStr;
		}
		// 转加密
		if (ServiceConstant.PLUGIN_CRYPTCONVERT.equals(service)) {
			resultStr = pluginMagService.cryptConvert(requestMsg, request);
			return resultStr;
		}
		// 验证APP完整性
		if (ServiceConstant.PLUGIN_VERIFYAPPINFO.equals(service)) {
			resultStr = pluginMagService.verifyAppInfo(requestMsg, request);
			return resultStr;
		}

		// 补发插件
		if (ServiceConstant.PLUGIN_RESISSUE.equals(service)) {
			resultStr = pluginMagService.pluginResissue(requestMsg, request);
			return resultStr;
		}
		// 获取插件状态
		if (ServiceConstant.PLUGIN_GETSTATUS.equals(service)) {
			resultStr = pluginMagService.pluginGetStatus(requestMsg, request);
			return resultStr;
		}
		// 解锁插件
		if (ServiceConstant.PLUGIN_UNLOCK.equals(service)) {
			resultStr = pluginMagService.pluginUnlock(requestMsg, request);
			return resultStr;
		}
		// 解挂插件
		if (ServiceConstant.PLUGIN_UNHANG.equals(service)) {
			resultStr = pluginMagService.pluginUnhang(requestMsg, request);
			return resultStr;
		}
		// 挂起插件
		if (ServiceConstant.PLUGIN_HANGUP.equals(service)) {
			resultStr = pluginMagService.pluginHangup(requestMsg, request);
			return resultStr;
		}
		// 申请插件2
		if (ServiceConstant.PLUGIN_APPLY2.equals(service)) {
			resultStr = pluginMagService.pluginApply2(requestMsg, request);
			return resultStr;
		}
		// 更新插件2
		if (ServiceConstant.PLUGIN_UPDATE2.equals(service)) {
			resultStr = pluginMagService.pluginUpdate2(requestMsg, request);
			return resultStr;
		}
		// 已激活插件激活未激活插件
		if (ServiceConstant.PLUGIN_OTHERACTIVE.equals(service)) {
			resultStr = pluginMagService.pluginActive2(requestMsg, request);
			return resultStr;
		}
		
		errorMsg = "service not right";
		logger.error(errorMsg);
		return errorMsg;
	}

}
