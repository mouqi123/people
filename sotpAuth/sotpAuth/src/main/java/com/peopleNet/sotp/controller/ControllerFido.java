package com.peopleNet.sotp.controller;

import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.constant.ServiceConstant;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.IParaHandle;
import com.peopleNet.sotp.serviceFido.IGeneralServiceFido;
import com.peopleNet.sotp.serviceFido.IPluginMagServiceFido;
import com.peopleNet.sotp.util.StringUtils;
import com.peopleNet.sotp.vo.ResultVO;
import com.peopleNet.sotp.vo.UserRequestMsgFido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ControllerFido {
	private static LogUtil logger = LogUtil.getLogger(ControllerFido.class);
	@Autowired
	private IPluginMagServiceFido pluginMagService;
	@Autowired
	private IGeneralServiceFido generalService;
	@Autowired
	private IParaHandle paraHandleService;
	@Autowired
	private IParaHandle paraHandle;

	@RequestMapping(value = "fidoportal")
	@ResponseBody
	public String auth(@ModelAttribute UserRequestMsgFido requestMsg, HttpServletRequest request) {

		String header = requestMsg.getHeader();
		String userInfo = requestMsg.getUserInfo();
		String pluginSign = requestMsg.getPluginSign();
		String tradeInfo = requestMsg.getTradeInfo();
		String op = "";
		String version = "";
		String errorMsg = "";
		String resultStr = "";
		UserRequestMsgFido newHeaderAndUser = null;

		// 如果参数pluginSign存在，解析pluginSign
		if (!StringUtils.isEmpty(pluginSign)) {
			UserRequestMsgFido newPluginSign = paraHandle.getNewPluginSign(requestMsg.getPluginSign());
			if (newPluginSign == null) {
				errorMsg = "pluginSign wrong.";
				logger.error(errorMsg);
				return errorMsg;
			}
			requestMsg.setPluginId(newPluginSign.getPluginId());
			requestMsg.setPluginHash(newPluginSign.getPluginHash());
		}

		// 如果参数tradeInfo存在
		if (!StringUtils.isEmpty(tradeInfo)) {
			UserRequestMsgFido newTradeInfo = paraHandle.getNewTradeInfo(requestMsg.getTradeInfo());
			if (newTradeInfo == null) {
				errorMsg = "tradeInfo wrong.";
				logger.error(errorMsg);
				return errorMsg;
			}
			requestMsg.setPayAction(newTradeInfo.getPayAction());
			requestMsg.setPayCard(newTradeInfo.getPayCard());
			requestMsg.setRecCard(newTradeInfo.getRecCard());
			requestMsg.setPrice(newTradeInfo.getPrice());
			requestMsg.setPayCardType(newTradeInfo.getPayCardType());
		}

		// 解析header和userInfo
		newHeaderAndUser = paraHandle.getNewHeaderAndUserInfo(header, userInfo);
		if (newHeaderAndUser == null) {
			errorMsg = "header or userInfo wrong.";
			logger.error(errorMsg);
			return errorMsg;
		}

		op = newHeaderAndUser.getOp();
		version = newHeaderAndUser.getUpv();
		requestMsg.setUpv(version);
		requestMsg.setOp(op);
		requestMsg.setPhoneNum(newHeaderAndUser.getPhoneNum());

		// TODO // 版本upv如何处理
		if (!ServiceConstant.AUTH_VERSION_3_0.equals(version)) {
			errorMsg = "auth version is not 3.0.";
			logger.error(errorMsg);
			return errorMsg;
		}
		if (null == op) {
			errorMsg = "op is null";
			logger.error(errorMsg);
			return errorMsg;
		} // 验证应用及签名信息
		ResultVO result = paraHandleService.checkSignature(requestMsg);
		if (result.getCode() != ErrorConstant.RET_OK) {
			logger.error(result.getMsg());
			return result.getMsg();
		}

		// op = requestMsg.getOp();

		// 获取service信息控制跳转
		// 插件下载
		if (ServiceConstant.PLUGIN_REG.equals(op)) {
			resultStr = pluginMagService.pluginReg(requestMsg, request);
			return resultStr;
		}

		// 注册认证、并按策略激活插件
		if (ServiceConstant.PLUGIN_REG_AUTH.equals(op)) {
			resultStr = pluginMagService.pluginRegAuth(requestMsg, request);
			return resultStr;
		}

		// 插件更新
		if (ServiceConstant.PLUGIN_UPDATE.equals(op)) {
			resultStr = pluginMagService.pluginupdate(requestMsg, request);
			return resultStr;
		}

		// 激活插件
		if (ServiceConstant.PLUGIN_ACTIVATE.equals(op)) {
			resultStr = pluginMagService.activePlugin(requestMsg, request);
			return resultStr;
		}

		// 安全认证申请
		if (ServiceConstant.BUSINESS_AUTH.equals(op)) {
			resultStr = generalService.verify(requestMsg, request);
			return resultStr;
		}

		// 安全认证响应
		if (ServiceConstant.BUSINESS_AUTH_RESPONSE.equals(op)) {
			resultStr = generalService.verifyResponse(requestMsg, request);
			return resultStr;
		}
		// 请求挑战码
		if (ServiceConstant.BUSINESS_GETCHALLENGE.equals(op)) {
			resultStr = generalService.getchallenge(requestMsg, request);
			return resultStr;
		}
		// 时间同步功能
		if (ServiceConstant.BUSINESS_TIME_SYNCHR.equals(op)) {
			resultStr = generalService.timeSynchr(requestMsg, request);
			return resultStr;
		}

		// 获取绑定设备列表
		if (ServiceConstant.PLUGIN_LISTALL.equals(op)) {
			resultStr = pluginMagService.getdevlist(requestMsg, request);
			return resultStr;
		}

		// 解绑插件
		if (ServiceConstant.PLUGIN_UNWRAP.equals(op)) {
			resultStr = pluginMagService.plugindel(requestMsg, request);
			return resultStr;
		}

		// 修改设备名称
		if (ServiceConstant.PLUGIN_SETDEVALIAS.equals(op)) {
			resultStr = pluginMagService.setDevAlias(requestMsg, request);
			return resultStr;
		}
		// 同步共享密钥
		if (ServiceConstant.PLUGIN_SYNSHAREKEY.equals(op)) {
			resultStr = pluginMagService.synShareKey(requestMsg, request);
			return resultStr;
		}
		// 转加密
		if (ServiceConstant.PLUGIN_CRYPTCONVERT.equals(op)) {
			resultStr = pluginMagService.cryptConvert(requestMsg, request);
			return resultStr;
		}
		// 检测app签名值和hash值
		if (ServiceConstant.BUSINESS_CHECKAPPSIGNHASH.equals(op)) {
			resultStr = generalService.checkAppSignHash(requestMsg, request);
			return resultStr;
		}
		// 检测设备Id和插件签名
		if (ServiceConstant.PLUGIN_CHECKDEVPLUGINHASH.equals(op)) {
			resultStr = pluginMagService.checkDevPluginhash(requestMsg, request);
			return resultStr;
		}
		// 生成会话密钥
		if (ServiceConstant.BUSINESS_GENSESSKEY.equals(op)) {
			resultStr = generalService.gensesskey(requestMsg, request);
			return resultStr;
		}
		// 本地数据加解密
		if (ServiceConstant.BUSINESS_DATAENCRYPTION.equals(op) || ServiceConstant.BUSINESS_DATADECRYPTION.equals(op)) {
			resultStr = generalService.crypto(requestMsg, request);
			return resultStr;
		}
		//
		//
		// // 获取插件状态
		// if (ServiceConstant.PLUGIN_GETSTATUS.equals(service)) {
		// resultStr = pluginMagService.getstatus(requestMsg, request);
		// return resultStr;
		// }
		//
		// // 协商会话秘钥第一步
		// //
		// if(ServiceConstant.BUSINESS_NEGOTIATESESSIONKEYS1.equals(service)){
		// // resultStr = generalService.verify(requestMsg, request);
		// // return resultStr;
		// // }
		// // //协商会话秘钥第二步
		// //
		// if(ServiceConstant.BUSINESS_NEGOTIATESESSIONKEYS2.equals(service)){
		// // resultStr = generalService.sotpVerifyClient(requestMsg, request);
		// // return resultStr;
		// // }
		// // //会话秘钥加密数据
		// // if(ServiceConstant.BUSINESS_SESSIONKEYENCRYPTION.equals(service)){
		// // resultStr = generalService.verify(requestMsg, request);
		// // return resultStr;
		// // }
		// // //会话秘钥解密数据
		// // if(ServiceConstant.BUSINESS_SESSIONKEYDECRYPTION.equals(service)){
		// // resultStr = generalService.verify(requestMsg, request);
		// // return resultStr;
		// // }
		// // 本地数据加解密
		// if (ServiceConstant.BUSINESS_DATAENCRYPTION.equals(service)
		// || ServiceConstant.BUSINESS_DATADECRYPTION.equals(service)) {
		// resultStr = generalService.crypto(requestMsg, request);
		// return resultStr;
		// }
		//

		// // 修改插件保护码
		// if (ServiceConstant.BUSINESS_MODSOTPIN.equals(service)) {
		// resultStr = generalService.modsotpin(requestMsg, request);
		// return resultStr;
		// }
		// // 修改预留信息
		// if (ServiceConstant.BUSINESS_MODHOLDINFO.equals(service)) {
		// resultStr = generalService.modholdinfo(requestMsg, request);
		// return resultStr;
		// }

		/*
		 * for 模拟系统
		 */
		if (ServiceConstant.PLUGIN_LISTALL_WEB.equals(op)) {
			resultStr = pluginMagService.getPluginListWeb(requestMsg, request);
			return resultStr;
		}
		if (ServiceConstant.PLUGIN_ACTIVE_WEB.equals(op)) {
			resultStr = pluginMagService.activePluginWeb(requestMsg, request);
			return resultStr;
		}
		return "service not right";
	}

}
