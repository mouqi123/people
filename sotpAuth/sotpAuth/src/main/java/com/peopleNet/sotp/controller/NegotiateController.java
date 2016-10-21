package com.peopleNet.sotp.controller;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.dal.model.appVersionInfoDto;
import com.peopleNet.sotp.dal.model.pluginInfoDto;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.*;
import com.peopleNet.sotp.thrift.service.PluginSaveHelper;
import com.peopleNet.sotp.thrift.service.SotpRet;
import com.peopleNet.sotp.util.Base64;
import com.peopleNet.sotp.util.CommonConfig;
import com.peopleNet.sotp.util.SMS4;
import com.peopleNet.sotp.vo.ResultVO;
import com.peopleNet.sotp.vo.UserRequestMsg;
import net.sf.json.JSONObject;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

@Controller
public class NegotiateController {
	private static LogUtil logger = LogUtil.getLogger(NegotiateController.class);
	// session参数
	private final String SOTP_PLUGIN_ID = "sotp_plugin_id";
	private final String SOTP_CLIENT_R = "sotp_client_random";
	private final String SOTP_SERVER_R = "sotp_server_random";
	private final String SOTP_KEY = "sotp_key";
	@Autowired
	private IEncryptPluginInfoService pluginInfoMapper;
	@Autowired
	private IThriftInvokeService thriftInvokeService;
	@Autowired
	private IBusinessService businessService;
	@Autowired
	private IParaHandle paraHandle;
	@Autowired
	private ISignService signService;
	private int CONNECT_THOR_PROTOCOL = Integer.parseInt(CommonConfig.get("CONNECT_THOR_PROTOCOL").trim());

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

	/**
	 *
	 * 生成服务器认证消息
	 */
	@RequestMapping(value = "sotpServerAuth")
	@ResponseBody
	public String sotpServerAuth(@ModelAttribute UserRequestMsg requestMsg, HttpServletRequest request) {
		// type 1301 chello 存session
		int ret = ErrorConstant.RET_OK;
		String reqmsg = request.getQueryString();
		String errMsg = "";

		int type = requestMsg.getType();
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		if (null == userId) {
			userId = phoneNum;
		}
		String sotpId = requestMsg.getSotpId();
		String chello = requestMsg.getChello();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();

		if (type != Constant.SERVICE_TYPE.SOTP_SERVER_AUTH) {
			logger.error("parameter type not right. type:" + type);
			return retStrV2(appId, "", Constant.SERVICE_TYPE.SOTP_UNKOWN_TYPE, ErrorConstant.PARA_TYPE_UNKOWN, "",
			        "para type unkown.", null, reqmsg);
		}

		logger.debug("sotpServerAuth [type:" + type + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId + ", chello:"
		        + chello);

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

		HttpSession session = request.getSession();

		// 生成随机数
		String srvR = Long.toString(System.currentTimeMillis());

		session.setAttribute(SOTP_PLUGIN_ID, sotpId);
		session.setAttribute(SOTP_CLIENT_R, chello);
		session.setAttribute(SOTP_SERVER_R, srvR);

		// 正确 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ret);

		return retStrV2(appId, appKey, type, ret, srvR, "", null, reqmsg);
	}

	/**
	 *
	 * 验证客户端认证消息
	 *
	 */
	@RequestMapping(value = "sotpVerifyClient")
	@ResponseBody
	public String sotpVerifyClient(@ModelAttribute UserRequestMsg requestMsg, HttpServletRequest request) {

		// type 1302
		int ret = ErrorConstant.RET_OK;
		String reqmsg = request.getQueryString();
		String errMsg = "";

		int type = requestMsg.getType();
		String phoneNum = requestMsg.getPhoneNum();
		String userId = requestMsg.getUserId();
		if (null == userId) {
			userId = phoneNum;
		}
		String sotpId = requestMsg.getSotpId();
		String cauth = requestMsg.getCauth();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();

		if (type != Constant.SERVICE_TYPE.SOTP_VERIFY_CLIENT) {
			logger.error("parameter type not right. type:" + type);
			return retStrV2(appId, "", Constant.SERVICE_TYPE.SOTP_UNKOWN_TYPE, ErrorConstant.PARA_TYPE_UNKOWN, "",
			        "para type unkown.", null, reqmsg);
		}

		logger.debug(
		        "sotpServerAuth [type:" + type + ", phoneNum:" + phoneNum + ", sotpId:" + sotpId + ", cauth:" + cauth);

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

		// 客户端、服务端随机数
		HttpSession session = request.getSession();
		Object ctime_ = session.getAttribute(SOTP_CLIENT_R);
		Object srand_ = session.getAttribute(SOTP_SERVER_R);
		if (ctime_ == null) {
			return retStrV2(appId, appKey, type, -1, "", "sotpVerifyClient can't get SOTP_CLIENT_R from session.", null,
			        reqmsg);
		}
		if (srand_ == null) {
			return retStrV2(appId, appKey, type, -1, "", "sotpVerifyClient can't get SOTP_SERVER_R from session.", null,
			        reqmsg);
		}
		String ctime = ctime_.toString();
		String srand = srand_.toString();

		// 参数判断
		ResultVO resultVO = paraHandle.paraFormat(reqmsg, type);
		if (null != resultVO && resultVO.getCode() != ErrorConstant.RET_OK) {
			logger.error(resultVO.getMsg());
			return retStrV2(appId, appKey, type, resultVO.getCode(), "", resultVO.getMsg(), null, reqmsg);
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
		if (CONNECT_THOR_PROTOCOL == Constant.THOR_PROTOCOL.SOTP_CONNECT_THOR_PROTOCOL_HTTP) {
			logger.error("crypto connect sotp http protocol not write");
		} else if (CONNECT_THOR_PROTOCOL == Constant.THOR_PROTOCOL.SOTP_CONNECT_THOR_PROTOCOL_TCP) {
			try {
				String seed = pluginKey + ":" + ctime + ":" + srand;
				sotpRes = thriftInvokeService.sotpDecryptV2(sotpId, seed, cauth);
			} catch (TException e) {
				logger.error("invoke thrift encryptNew error.msg:%s", e.toString());
			}
		}
		if (sotpRes == null || sotpRes.status < 0) {
			logger.error("sotpthorReponse is null.");
			return retStrV2(appId, appKey, type, ErrorConstant.THOR_CONNECT_TIMEOUT, "",
			        "connect sotp_crypto_machine wrong.", null, reqmsg);
		}
		String sessionKey = sotpRes.getTitle();
		logger.info("sotp decrypt crypto:" + cauth + ", plain:" + sessionKey);
		// 返回结果 存session
		session.setAttribute(SOTP_KEY, sessionKey);

		// 把 ctime 加密返回
		if (CONNECT_THOR_PROTOCOL == Constant.THOR_PROTOCOL.SOTP_CONNECT_THOR_PROTOCOL_HTTP) {
			logger.error("crypto connect sotp http protocol not write");
		} else if (CONNECT_THOR_PROTOCOL == Constant.THOR_PROTOCOL.SOTP_CONNECT_THOR_PROTOCOL_TCP) {
			try {
				sotpRes = thriftInvokeService.sotpEncryptV2(sotpId, pluginKey, ctime);
			} catch (TException e) {
				logger.error("invoke thrift encryptNew error.msg:%s", e.toString());
			}
		}
		String crypto = sotpRes.getTitle();
		logger.info("sotp crypto plain:" + ctime + ", crypto:" + crypto);
		// 正确 保存返回状态信息到request中，以便统计接口成功与否
		request.setAttribute("_retStatus", ret);
		return retStrV2(appId, appKey, type, ret, crypto, "", null, reqmsg);
	}

	/**
	 *
	 * session key 加解密数据
	 *
	 */
	@RequestMapping(value = "sesskeycrypto")
	@ResponseBody
	public String sesskeycrypto(@ModelAttribute UserRequestMsg requestMsg, HttpServletRequest request) {

		// type 1303
		int ret = ErrorConstant.RET_OK;
		String reqmsg = request.getQueryString();
		String errMsg = "";

		int type = requestMsg.getType();
		String cdata = requestMsg.getCdata();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();

		if (type != Constant.SERVICE_TYPE.SESSKEY_CRYPTO) {
			logger.error("parameter type not right. type:" + type);
			return retStrV2(appId, "", Constant.SERVICE_TYPE.SOTP_UNKOWN_TYPE, ErrorConstant.PARA_TYPE_UNKOWN, "",
			        "para type unkown.", null, reqmsg);
		}

		logger.debug("sotpServerAuth [type:" + type + ", cdata:" + cdata);

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

		// 客户端、服务端随机数
		HttpSession session = request.getSession();
		Object sotpKey_ = session.getAttribute(SOTP_KEY);
		if (sotpKey_ == null) {
			return retStrV2(appId, appKey, type, -1, "", "sotpVerifyClient can't get SOTP_KEY from session.", null,
			        reqmsg);
		}

		String sotpKey = sotpKey_.toString();

		try {
			logger.debug("前********************" + cdata);
			String decrypto = sotpDataDecrypto(cdata, sotpKey);
			decrypto = decrypto + "1";
			String crypto = sotpDataCrypto(decrypto, sotpKey);
			logger.debug("后********************" + crypto);
			return retStrV2(appId, appKey, type, ret, crypto, "", null, reqmsg);
		} catch (UnsupportedEncodingException e) {
			return retStrV2(appId, appKey, type, -1, "", e.getMessage(), null, reqmsg);
		} catch (IllegalArgumentException e) {
			return retStrV2(appId, appKey, type, -1, "", e.getMessage(), null, reqmsg);
		}

	}

	private String sotpDataDecrypto(String cipher, String key)
	        throws UnsupportedEncodingException, IllegalArgumentException {
		logger.info("sotpDataDecrypto get session key:" + key);

		byte[] bmk = Base64.decode(key.toCharArray());
		byte[] text = Base64.decode(cipher.toCharArray());

		byte[] mbmk = new byte[16];
		System.arraycopy(bmk, 0, mbmk, 0, 16);

		SMS4 sMS4 = new SMS4();
		byte[] ecb;
		try {
			ecb = sMS4.decrypt(text, mbmk, 0, null);
			String plain = new String(ecb, "UTF-8");
			logger.info("sotpDataDecrypto plain:" + plain);
			return plain;
		} catch (IllegalArgumentException e) {
			throw e;
		}
	}

	private String sotpDataCrypto(String plain, String key)
	        throws UnsupportedEncodingException, IllegalArgumentException {
		logger.info("sotpDataCrypto get session key:" + key);

		byte[] bmk = Base64.decode(key.toCharArray());
		byte[] mbmk = new byte[16];
		System.arraycopy(bmk, 0, mbmk, 0, 16);

		try {
			// 加密
			SMS4 sMS4 = new SMS4();
			byte[] text = plain.getBytes("UTF-8");
			byte[] encrypt = sMS4.encrypt(text, mbmk, 0, null);
			char[] encode = Base64.encode(encrypt);
			String base64cipher = new String(encode);
			logger.info("sotpDataCrypto cipher:" + base64cipher);

			return base64cipher;
		} catch (Exception e) {
			throw e;
		}
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
}
