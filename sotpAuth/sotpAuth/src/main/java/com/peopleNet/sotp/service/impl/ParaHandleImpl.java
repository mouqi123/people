package com.peopleNet.sotp.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.constant.Constant.REDIS_CONSTANT;
import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.constant.ServiceConstant;
import com.peopleNet.sotp.controller.ControllerHelper;
import com.peopleNet.sotp.dal.dao.appInfoDtoMapper;
import com.peopleNet.sotp.dal.dao.appVersionInfoDtoMapper;
import com.peopleNet.sotp.dal.dao.authFeatureDtoMapper;
import com.peopleNet.sotp.dal.model.*;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.*;
import com.peopleNet.sotp.thrift.service.PluginSaveHelper;
import com.peopleNet.sotp.util.Base64;
import com.peopleNet.sotp.util.CommonConfig;
import com.peopleNet.sotp.util.StringUtils;
import com.peopleNet.sotp.util.Util;
import com.peopleNet.sotp.vo.ResultVO;
import com.peopleNet.sotp.vo.UserRequestMsg;
import com.peopleNet.sotp.vo.UserRequestMsgFido;
import com.peopleNet.sotp.vo.UserRequestMsgV2;

@Service
public class ParaHandleImpl implements IParaHandle {
	private static String cachePrefix = CommonConfig.get("redis.cache.prefix");
	private static String checkSign = CommonConfig.get("check_sign");
	private static LogUtil log = LogUtil.getLogger(ParaHandleImpl.class);
	private String saveSessionKey = CommonConfig.get("SAVE_SESSIONKEY");
	
	@Autowired
	private RedisTemplate<String, java.io.Serializable> redisTemplate;
	@Autowired
	private IEncryptPluginInfoService pluginInfoMapper;
	@Autowired
	private authFeatureDtoMapper authFeatureMapper;
	@Autowired
	private appInfoDtoMapper appInfoMapper;
	@Autowired
	private appVersionInfoDtoMapper appVersionInfoMapper;
	@Autowired
	private ISignService signService;
	@Autowired
	private IBusinessService businessService;
	@Autowired
	private IPolicyService policyService;
	@Autowired
	private ICacheService cacheService;
	@Autowired
	private IAnalysisService analysisService;

	/**
	 * 将json格式的字符串解析成Map对象
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

	public pluginInfoDto getPluginfo(String sotpId) {
		Random random = new Random();
		int rand = random.nextInt() % 10;
		return pluginInfoMapper.selectByPluginIdOptimize(rand, sotpId);
		// return pluginInfoMapper.selectByPluginId(sotpId);
	}

	public authFeatureDto getAuthFeature(String sotpId) {
		Random random = new Random();
		int rand = random.nextInt() % 10;
		authFeatureDto authFeaInfo = null;
		try {
			authFeaInfo = authFeatureMapper.selectByPluginIdOptimize(rand, sotpId);
		} catch (SQLException e) {
			log.error("authFeatureMapper selectByPluginIdOptimize sql error.msg:%s", e.toString());
			return authFeaInfo;
		}
		return authFeaInfo;
		// return authFeatureMapper.selectByPluginId(sotpId);
	}

	public pluginInfoDto getOldPluginInfo(String devId, String phoneNum) {
		Random random = new Random();
		int rand = random.nextInt() % 10;
		return pluginInfoMapper.selectByDevIdAndPhoneNum(rand, devId, phoneNum);
	}

	public pluginInfoDto getNewPluginfo(String uid, String phonenum, String sotpPayInfo) {
		return this.parsePayInfo(sotpPayInfo, phonenum, uid);
	}

	public pluginInfoDto getNewPluginfoV2(String uid, String phonenum, String pin) {
		return this.parsePayInfoV2(pin, phonenum, uid);
	}

	public authFeatureDto getNewAuthFeature(String devInfo) {
		return this.parseDevInfo(devInfo);
	}

	public String getIsRootFromDevInfo(String devInfo) {
		return this.getRootFromDevInfo(devInfo);
	}

	// 获取header和userInfo、attachedInfo信息
	public UserRequestMsgFido getNewHeaderAndUserInfo(String headinfo, String userInfo) {
		return this.parseHeaderAndUserInfo(headinfo, userInfo);
	}

	// 获取attachedInfo信息
	public UserRequestMsgFido getattachedInfo(String attachedInfo) {
		return this.parseAttachedInfo(attachedInfo);
	}

	// 根据策略来解析auth信息
	public userInfoDto getAuth(String auth, int policy) {
		return this.parseAuthByPolicy(auth, policy);
	}

	public UserRequestMsgFido getNewPluginSign(String pluginSign) {
		return this.parsePluginSign(pluginSign);
	}

	public appInfoDto getAppInfo(String businessCode) {
		appInfoDto appInfo = null;
		try {
			appInfo = appInfoMapper.selectByBusinessCode(null);
		} catch (SQLException e) {
			log.error("appInfoMapper selectByBusinessCode sql error.msg:%s", e.toString());
			return appInfo;
		}
		return appInfo;
	}

	/*
	 * 返回： 正确:0 错误：错误码
	 */
	public ResultVO paraFormat(String requestMsg, int interfaceId) {

		ResultVO result = null;
		// 共性参数
		String phonenum = "", uid = "", sotpId = "", sotpCode = "", devInfo = "", sotpPayInfo = "", sms = "",
		        sotpCodePara = "", appId = "", sign = "", nonce_str = "", activeCode = "";
		Map<String, String> paramMap = getReqParamMap(requestMsg);
		if (null != paramMap) {
			phonenum = paramMap.get(Constant.PARA_NAME.PHONENUM);
			uid = paramMap.get(Constant.PARA_NAME.USERID);
			sotpId = paramMap.get(Constant.PARA_NAME.SOTPID);
			sotpCode = paramMap.get(Constant.PARA_NAME.SOTPCODE);
			devInfo = paramMap.get(Constant.PARA_NAME.DEVINFO);
			sotpPayInfo = paramMap.get(Constant.PARA_NAME.SOTPPAYINFO);
			sms = paramMap.get(Constant.PARA_NAME.SMS);
			sotpCodePara = paramMap.get(Constant.PARA_NAME.SOTPCODEPARA);
			appId = paramMap.get(Constant.PARA_NAME.APPID);
			sign = paramMap.get(Constant.PARA_NAME.SIGN);
			nonce_str = paramMap.get(Constant.PARA_NAME.NONCE_STR);
			activeCode = paramMap.get(Constant.PARA_NAME.ACTIVECODE);
		}

		// 参数长度判断
		result = paraLenVery(interfaceId, phonenum, uid, sotpId, sotpCode, devInfo, sotpPayInfo, sms, sotpCodePara,
		        appId, sign, nonce_str, activeCode);

		return result;
	}

	/*
	 * 返回： 正确:0 错误：错误码
	 */
	public ResultVO paraFormat(UserRequestMsg requestMsg) {

		ResultVO result = null;
		// 共性参数
		int interfaceId = requestMsg.getType();
		String phoneNum = requestMsg.getPhoneNum();
		String uid = requestMsg.getUserId();
		String sotpId = requestMsg.getSotpId();
		String sotpCode = requestMsg.getSotpCode();
		String devInfo = requestMsg.getDevInfo();
		String sotpPayInfo = requestMsg.getSotpPayInfo();
		String sms = requestMsg.getSms();
		String sotpCodePara = requestMsg.getSotpCodePara();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();
		String nonce_str = requestMsg.getNonce_str();
		String activeCode = requestMsg.getActiveCode();

		log.debug("receive parameter---- type:" + interfaceId + ", userId:" + uid + ", phoneNum:" + phoneNum + ", sms:"
		        + sms + ",sotpPayInfo:" + sotpPayInfo + ", devInfo:" + devInfo + ", appId:" + appId + ", sign:" + sign
		        + ",nonce_str:" + nonce_str + "activeCode:" + activeCode);
		// 参数长度判断
		result = paraLenVery(interfaceId, phoneNum, uid, sotpId, sotpCode, devInfo, sotpPayInfo, sms, sotpCodePara,
		        appId, sign, nonce_str, activeCode);

		return result;
	}

	/*
	 * 返回： 正确:0 错误：错误码
	 */
	public ResultVO paraFormatV2(UserRequestMsgV2 requestMsg) {

		ResultVO result = null;
		// 共性参数
		String service = requestMsg.getService();
		String phoneNum = requestMsg.getPhoneNum();
		String uid = requestMsg.getUserId();
		String sotpId = requestMsg.getSotpId();
		String sotpCode = requestMsg.getSotpCode();
		String devInfo = requestMsg.getDevInfo();
		String envInfo = requestMsg.getEnvInfo();
		String sotpPayInfo = requestMsg.getSotpPayInfo();
		String sms = requestMsg.getSms();
		String sotpCodePara = requestMsg.getSotpCodePara();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();
		String nonce_str = requestMsg.getNonce_str();
		String activeCode = requestMsg.getActiveCode();
		String activePluginId = requestMsg.getActivePluginId();
		String devId = requestMsg.getDevId();

		log.debug("receive parameter---- service:" + service + "userId:" + uid + ", phoneNum:" + phoneNum + ", sms:"
		        + sms + ",sotpPayInfo:" + sotpPayInfo + ", devInfo:" + devInfo + "envInfo:" + envInfo + ", appId:"
		        + appId + ", sign:" + sign + ",nonce_str:" + nonce_str + "activeCode:" + activeCode + "activePluginId:"
		        + activePluginId + ",devId:" + devId);
		// 参数长度判断
		result = paraLenVeryV2(service, phoneNum, uid, sotpId, sotpCode, devInfo, envInfo, sotpPayInfo, sms,
		        sotpCodePara, appId, sign, nonce_str, activeCode, activePluginId, devId);

		return result;
	}

	/*
	 * 返回： 正确:0 错误：错误码
	 */
	public ResultVO paraFormatFido(UserRequestMsgFido requestMsg) {

		ResultVO result = null;
		// 共性参数
		String header = requestMsg.getHeader();
		String userInfo = requestMsg.getUserInfo();
		String devInfo = requestMsg.getDevInfo();
		String appInfo = requestMsg.getAppInfo();
		String pluginSign = requestMsg.getPluginSign();
		String challengeAns = requestMsg.getChallengeAns();
		String auth = requestMsg.getAuth();
		String op = requestMsg.getOp();
		String phoneNum = requestMsg.getPhoneNum();
		String uid = requestMsg.getUserId();
		if (StringUtils.isEmpty(uid)) {
			uid = phoneNum;
		}
		String pluginId = requestMsg.getPluginId();
		String sotpCode = requestMsg.getSotpCode();
		String sms = requestMsg.getSms();
		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();
		String nonce_str = requestMsg.getNonce_str();
		String activeCode = requestMsg.getActiveCode();
		String pin = requestMsg.getPin();
		String businessName = requestMsg.getBusinessName();
		String attachedInfo = requestMsg.getAttachedInfo();
		String useCount = requestMsg.getUseCount();
		String grade = requestMsg.getGrade();

		log.debug("receive parameter---- op:" + op + "userId:" + uid + ", phoneNum:" + phoneNum + ", sms:" + sms
		        + ",header:" + header + ", devInfo:" + devInfo + ",appInfo:" + appInfo + ",userInfo:" + userInfo
		        + ",pluginSign:" + pluginSign + ",challengeAns:" + challengeAns + ",auth:" + auth + ",sotpCode:"
		        + sotpCode + ", appId:" + appId + ", sign:" + sign + ",nonce_str:" + nonce_str + ",activeCode:"
		        + activeCode + ",attachedInfo:" + attachedInfo + ",useCount:" + useCount + ",grade:" + grade);
		// 参数长度判断
		result = paraLenVeryFido(header, userInfo, devInfo, appInfo, pluginSign, challengeAns, auth, op, phoneNum, uid,
		        pluginId, sotpCode, sms, appId, sign, nonce_str, activeCode, pin, businessName, attachedInfo, useCount,
		        grade);

		return result;
	}

	/*
	 * 插件状态判断
	 */
	private ResultVO getstatusPara(pluginInfoDto plInfo, authFeatureDto authFeature, authFeatureDto newauthFeature,
	        String sotpId, String phonenum, String uid) {
		ResultVO result = new ResultVO();
		String errMsg = "";
		// 参数内容判断
		if (plInfo == null) {
			errMsg = "parameter sotpId db not exit:" + sotpId;
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PARACONT_PLUGINID_NOEXIST);
			return result;
		}

		if (authFeature == null) {
			errMsg = "authFeature not exist db.";
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PARACONT_PLUGINID_NOEXIST);
			return result;
		}

		// userId若存在
		if (uid != null) {
			if (!uid.equals(plInfo.getBindUserid())) {
				errMsg = "userId:" + uid + " != db" + plInfo.getBindUserid();
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARACONT_USERID_NOTMATCH_SID);
				return result;
			}
		}

		if (!phonenum.equals(plInfo.getBindUserphone())) {
			errMsg = "phoneNum:" + phonenum + " != db" + plInfo.getBindUserphone();
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PARACONT_PNUM_NOTMATCH_SID);
			return result;
		}

		// 硬件信息 比对
		// 安卓 imei
		// IOS uuid
		switch (newauthFeature.getdevType()) {
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_ARM:
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_X86:
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_MIPS:
			String newimei = newauthFeature.getimei();
			String oldimei = authFeature.getimei();
			if (!oldimei.equals(newimei)) {
				errMsg = "devInfo android imei:" + newimei + " != db imei:" + oldimei;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARACONT_DEV_NOTMATCH_SID);
				return result;
			}
			break;

		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_IOS:
			String newuuid = newauthFeature.getuuid();
			String olduuid = authFeature.getuuid();
			if (!newuuid.equals(olduuid)) {
				errMsg = "devInfo ios imei:" + newuuid + " != db imei:" + olduuid;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARACONT_DEV_NOTMATCH_SID);
				return result;
			}
			break;

		default:
			errMsg = "unkown dev type:" + newauthFeature.getdevType();
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PARA_DEVINFOFORMAT_ERR);
			return result;
		}

		return result;
	}

	/**
	 * 参数长度判断
	 *
	 * @return
	 */
	private ResultVO paraLenVery(int type, String phonenum, String uid, String sotpId, String sotpCode, String devInfo,
	        String sotpPayInfo, String sms, String sotpCodePara, String appId, String sign, String nonce_str,
	        String activeCode) {
		ResultVO result = new ResultVO();
		String errMsg = "";

		result.setCode(ErrorConstant.RET_OK);

		if (uid != null) {
			if (uid.length() > Constant.PARA_LEN.USERID_LEN) {
				errMsg = "parameter userId len not right:" + uid;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}

		if (phonenum == null || phonenum.length() > Constant.PARA_LEN.PHONENUM_LEN) {
			errMsg = "parameter phoneNum len not right:" + phonenum;
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PARA_LEN_ERR);
			return result;
		}

		if (appId == null || appId.length() > Constant.PARA_LEN.APPID_LEN) {
			errMsg = "parameter appId len not right:" + appId;
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PARA_LEN_ERR);
			return result;
		}

		if (sign == null || sign.length() > Constant.PARA_LEN.SIGN_LEN) {
			errMsg = "parameter sign len not right:" + sign;
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PARA_LEN_ERR);
			return result;
		}

		if (nonce_str == null || nonce_str.length() > Constant.PARA_LEN.NONCE_STR_LEN) {
			errMsg = "parameter nonce_str len not right:" + nonce_str;
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PARA_LEN_ERR);
			return result;
		}

		// 具体处理, 内容判断
		switch (type) {
		case Constant.SERVICE_TYPE.SOTP_PLUGIN_REGISTER_TYPE: // 申请插件
			if (sms != null && sms.length() > Constant.PARA_LEN.SMS_LEN) {
				errMsg = "parameter sms len not right:" + sms;
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				result.setMsg(errMsg);
			}

			if (sotpPayInfo != null && sotpPayInfo.length() > Constant.PARA_LEN.SOTPPAYINFO_LEN) {
				errMsg = "parameter sotpPayInfo len not right:" + sotpPayInfo;
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				result.setMsg(errMsg);
			}

			if (devInfo == null || devInfo.length() > Constant.PARA_LEN.DEVINFO_LEN) {
				errMsg = "parameter devInfo len not right:" + devInfo;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			break;

		case Constant.SERVICE_TYPE.SOTP_PLUGIN_UPDATE_TYPE: // 更新插件
			if (sotpId == null || sotpId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter sotpId len not right:" + sotpId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			break;

		case Constant.SERVICE_TYPE.SOTP_PLUGIN_DEL_TYPE: // 解绑插件
			if (sotpId == null || sotpId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter sotpId len not right:" + sotpId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			break;
		case Constant.SERVICE_TYPE.SOTP_GET_PLUGIN_STATUS: // 插件状态判断
			if (sotpId == null || sotpId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter sotpId len not right:" + sotpId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}

			if (sotpCode == null || sotpCode.length() > Constant.PARA_LEN.SOTPCODE_LEN) {
				errMsg = "parameter sotpCode len not right:" + sotpCode;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}

			if (devInfo == null || devInfo.length() > Constant.PARA_LEN.DEVINFO_LEN) {
				errMsg = "parameter devInfo len not right:" + devInfo;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			break;

		case Constant.SERVICE_TYPE.SOTP_GET_BIND_DEVLIST: // 插件绑定设备列表
			break;
		case Constant.SERVICE_TYPE.SOTP_CRYPTO_DATA_TYPE: // 加密
		case Constant.SERVICE_TYPE.SOTP_DECRYPTO_DATA_TYPE: // 解密
		case Constant.SERVICE_TYPE.SOTP_GET_CHALLENGE_CODE: // 请求挑战码
			if (sotpId == null || sotpId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter sotpId len not right:" + sotpId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			break;

		case Constant.SERVICE_TYPE.SOTP_VERIFY_SOTPCODE_TYPE: // 验证SOTP认证码
			if (sotpId == null || sotpId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter sotpId len not right:" + sotpId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}

			if (sotpCode == null || sotpCode.length() > Constant.PARA_LEN.SOTPCODE_LEN) {
				errMsg = "parameter sotpCode len not right:" + sotpCode;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			break;

		case Constant.SERVICE_TYPE.SOTP_VERIFY_ACTIVE_CODE: // 验证SOTP激活码
			if (activeCode == null || activeCode.length() > Constant.PARA_LEN.ACTIVECODE_LEN) {
				errMsg = "parameter activeCode len not right:" + activeCode;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			break;

		case Constant.SERVICE_TYPE.SOTP_MOD_PROTECT_CODE: // 修改保护码
		case Constant.SERVICE_TYPE.SOTP_MOD_HOLD_INFO: // 设置预留信息
			// TODO
			break;

		default:
			errMsg = "type not right:" + type;
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PARA_TYPE_UNKOWN);
			break;
		}

		return result;
	}

	/**
	 * 参数长度判断
	 *
	 * @return
	 */
	private ResultVO paraLenVeryV2(String service, String phonenum, String uid, String sotpId, String sotpCode,
	        String devInfo, String envInfo, String sotpPayInfo, String sms, String sotpCodePara, String appId,
	        String sign, String nonce_str, String activeCode, String activePluginId, String devId) {
		ResultVO result = new ResultVO();
		String errMsg = "";

		result.setCode(ErrorConstant.RET_OK);

		// 共性参数判断
		if (uid != null) {
			if (uid.length() > Constant.PARA_LEN.USERID_LEN) {
				errMsg = "parameter userId len not right:" + uid;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}

		if (StringUtils.isEmpty(appId) || appId.length() > Constant.PARA_LEN.APPID_LEN) {
			errMsg = "parameter appId len not right:" + appId;
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PARA_LEN_ERR);
			return result;
		}

		if (StringUtils.isEmpty(sign) || sign.length() > Constant.PARA_LEN.SIGN_LEN) {
			errMsg = "parameter sign len not right:" + sign;
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PARA_LEN_ERR);
			return result;
		}

		if (StringUtils.isEmpty(nonce_str) || nonce_str.length() > Constant.PARA_LEN.NONCE_STR_LEN) {
			errMsg = "parameter nonce_str len not right:" + nonce_str;
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PARA_LEN_ERR);
			return result;
		}

		// 申请插件参数判断
		if (ServiceConstant.PLUGIN_REG.equals(service) || ServiceConstant.PLUGIN_APPLY2.equals(service)) {
			if (!StringUtils.isEmpty(sms) && sms.length() > Constant.PARA_LEN.SMS_LEN) {
				errMsg = "parameter sms len not right:" + sms;
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				result.setMsg(errMsg);
			}

			if (StringUtils.isEmpty(phonenum) || phonenum.length() > Constant.PARA_LEN.PHONENUM_LEN) {
				errMsg = "parameter phoneNum len not right:" + phonenum;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}

			// 短信码验证处理
			result = policyService.smsVeryPolicy(sms, null, null);
			if (null == result || result.getCode() != ErrorConstant.RET_OK) {
				return result;
			}

			if (!StringUtils.isEmpty(sotpPayInfo) && sotpPayInfo.length() > Constant.PARA_LEN.SOTPPAYINFO_LEN) {
				errMsg = "parameter sotpPayInfo len not right:" + sotpPayInfo;
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				result.setMsg(errMsg);
			}

			if (StringUtils.isEmpty(devInfo) || devInfo.length() > Constant.PARA_LEN.DEVINFO_LEN) {
				errMsg = "parameter devInfo len not right:" + devInfo;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			if (StringUtils.isEmpty(envInfo)) {
				errMsg = "parameter envInfo len not right:" + envInfo;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}

		// 更新插件
		if (ServiceConstant.PLUGIN_UPDATE.equals(service) || ServiceConstant.PLUGIN_UPDATE2.equals(service)) {
			if (sotpId == null || sotpId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter sotpId len not right:" + sotpId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}

			if (StringUtils.isEmpty(phonenum) || phonenum.length() > Constant.PARA_LEN.PHONENUM_LEN) {
				errMsg = "parameter phoneNum len not right:" + phonenum;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}

		// 解绑插件
		if (ServiceConstant.PLUGIN_UNWRAP.equals(service)) {
			if (sotpId == null || sotpId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter sotpId len not right:" + sotpId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}

			if (StringUtils.isEmpty(phonenum) || phonenum.length() > Constant.PARA_LEN.PHONENUM_LEN) {
				errMsg = "parameter phoneNum len not right:" + phonenum;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}

		}

		// 激活插件
		if (ServiceConstant.PLUGIN_ACTIVATE.equals(service)) {
			if (StringUtils.isEmpty(activeCode) || activeCode.length() > Constant.PARA_LEN.ACTIVECODE_LEN) {
				errMsg = "parameter activeCode len not right:" + activeCode;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}

			if (StringUtils.isEmpty(phonenum) || phonenum.length() > Constant.PARA_LEN.PHONENUM_LEN) {
				errMsg = "parameter phoneNum len not right:" + phonenum;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}

		// 请求挑战码
		if (ServiceConstant.BUSINESS_GETCHALLENGE.equals(service)) {
			if (StringUtils.isEmpty(sotpId) || sotpId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter sotpId len not right:" + sotpId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}

			if (StringUtils.isEmpty(phonenum) || phonenum.length() > Constant.PARA_LEN.PHONENUM_LEN) {
				errMsg = "parameter phoneNum len not right:" + phonenum;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}
		// 安全认证
		if (ServiceConstant.BUSINESS_AUTH.equals(service)) {
			if (StringUtils.isEmpty(sotpId) || sotpId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter sotpId len not right:" + sotpId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}

			if (StringUtils.isEmpty(sotpCode) || sotpCode.length() > Constant.PARA_LEN.SOTPCODE_LEN) {
				errMsg = "parameter sotpCode len not right:" + sotpCode;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}

			if (StringUtils.isEmpty(phonenum) || phonenum.length() > Constant.PARA_LEN.PHONENUM_LEN) {
				errMsg = "parameter phoneNum len not right:" + phonenum;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}
		// 激活插件2
		if (ServiceConstant.PLUGIN_OTHERACTIVE.equals(service)) {
			if (StringUtils.isEmpty(activePluginId) || activePluginId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter activePluginId len not right:" + activePluginId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}

			if (StringUtils.isEmpty(phonenum) || phonenum.length() > Constant.PARA_LEN.PHONENUM_LEN) {
				errMsg = "parameter phoneNum len not right:" + phonenum;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}

		// 解锁插件
		if (ServiceConstant.PLUGIN_UNLOCK.equals(service)) {
			if (StringUtils.isEmpty(sotpId) || sotpId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter sotpId len not right:" + sotpId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}

			if (StringUtils.isEmpty(phonenum) || phonenum.length() > Constant.PARA_LEN.PHONENUM_LEN) {
				errMsg = "parameter phoneNum len not right:" + phonenum;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}
		// 解挂插件
		if (ServiceConstant.PLUGIN_UNHANG.equals(service)) {
			if (StringUtils.isEmpty(sotpId) || sotpId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter sotpId len not right:" + sotpId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}

			if (StringUtils.isEmpty(phonenum) || phonenum.length() > Constant.PARA_LEN.PHONENUM_LEN) {
				errMsg = "parameter phoneNum len not right:" + phonenum;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}
		// 挂起插件
		if (ServiceConstant.PLUGIN_HANGUP.equals(service)) {
			if (StringUtils.isEmpty(sotpId) || sotpId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter sotpId len not right:" + sotpId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}

			if (StringUtils.isEmpty(phonenum) || phonenum.length() > Constant.PARA_LEN.PHONENUM_LEN) {
				errMsg = "parameter phoneNum len not right:" + phonenum;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}
		// 获取插件状态
		if (ServiceConstant.PLUGIN_GETSTATUS.equals(service)) {
			if (StringUtils.isEmpty(devId) || devId.length() > Constant.PARA_LEN.DEVID_LEN || devId == "") {
				errMsg = "parameter devId len not right:" + devId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}

			if (StringUtils.isEmpty(phonenum) || phonenum.length() > Constant.PARA_LEN.PHONENUM_LEN) {
				errMsg = "parameter phoneNum len not right:" + phonenum;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}

		// 验证APP信息
		if (ServiceConstant.PLUGIN_VERIFYAPPINFO.equals(service)) {
			if (StringUtils.isEmpty(envInfo) || envInfo.length() > Constant.PARA_LEN.ENVINFO_LEN) {
				errMsg = "parameter envInfo len not right:" + envInfo;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}

		return result;
	}

	/**
	 * 参数长度判断for Fido V3.0
	 *
	 * @return
	 */
	private ResultVO paraLenVeryFido(String header, String userInfo, String devInfo, String appInfo, String pluginSign,
	        String challengeAns, String auth, String op, String phoneNum, String uid, String pluginId, String sotpCode,
	        String sms, String appId, String sign, String nonce_str, String activeCode, String pin, String businessName,
	        String attachedInfo, String useCount, String grade) {
		ResultVO result = new ResultVO();
		String errMsg = "";

		result.setCode(ErrorConstant.RET_OK);

		// 共性参数判断
		if (uid != null) {
			if (uid.length() > Constant.PARA_LEN.USERID_LEN) {
				errMsg = "parameter userId len not right:" + uid;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}

		if (!ServiceConstant.BUSINESS_CHECKAPPSIGNHASH.equals(op)) {
			if (StringUtils.isEmpty(phoneNum) || phoneNum.length() > Constant.PARA_LEN.PHONENUM_LEN) {
				errMsg = "parameter userInfo phoneNum len not right:" + phoneNum;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}

		if (StringUtils.isEmpty(appId) || appId.length() > Constant.PARA_LEN.APPID_LEN) {
			errMsg = "parameter appId len not right:" + appId;
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PARA_LEN_ERR);
			return result;
		}

		if (StringUtils.isEmpty(sign) || sign.length() > Constant.PARA_LEN.SIGN_LEN) {
			errMsg = "parameter sign len not right:" + sign;
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PARA_LEN_ERR);
			return result;
		}

		if (StringUtils.isEmpty(nonce_str) || nonce_str.length() > Constant.PARA_LEN.NONCE_STR_LEN) {
			errMsg = "parameter nonce_str len not right:" + nonce_str;
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PARA_LEN_ERR);
			return result;
		}

		if (StringUtils.isEmpty(header) || StringUtils.isEmpty(userInfo)) {
			errMsg = "parameter header/userInfo/devInfo/appInfo not right";
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PARA_LEN_ERR);
			return result;
		}

		// 申请插件参数判断
		if (ServiceConstant.PLUGIN_REG.equals(op)) {
			if (StringUtils.isEmpty(businessName) || businessName.length() > Constant.PARA_LEN.BUSINESSNAME) {
				errMsg = "parameter businessName len not right:" + businessName;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			if (StringUtils.isEmpty(attachedInfo)) {
				errMsg = "parameter attachedInfo len not right:" + attachedInfo;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}

		// 申请插件第二步
		if (ServiceConstant.PLUGIN_REG_AUTH.equals(op)) {
			if (StringUtils.isEmpty(challengeAns)) {
				errMsg = "parameter challengeAns len not right:" + challengeAns;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			if (StringUtils.isEmpty(businessName) || businessName.length() > Constant.PARA_LEN.BUSINESSNAME) {
				errMsg = "parameter businessName len not right:" + businessName;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			if (StringUtils.isEmpty(pluginSign) || StringUtils.isEmpty(pluginId)
			        || pluginId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter pluginSign pluginId len not right:" + pluginId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}

		// 认证参数判断
		if (ServiceConstant.BUSINESS_AUTH.equals(op)) {
			if (StringUtils.isEmpty(pluginSign) || StringUtils.isEmpty(pluginId)
			        || pluginId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter pluginSign pluginId len not right:" + pluginId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			if (StringUtils.isEmpty(businessName) || businessName.length() > Constant.PARA_LEN.BUSINESSNAME) {
				errMsg = "parameter businessName len not right:" + businessName;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			if (StringUtils.isEmpty(useCount)) {
				errMsg = "parameter useCount len not right:" + useCount;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			if (StringUtils.isEmpty(attachedInfo)) {
				errMsg = "parameter attachedInfo len not right:" + attachedInfo;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
//			if (StringUtils.isEmpty(grade)) {
//				errMsg = "parameter grade len not right:" + grade;
//				result.setMsg(errMsg);
//				result.setCode(ErrorConstant.PARA_LEN_ERR);
//				return result;
//			}
		}

		// 认证第二步
		if (ServiceConstant.BUSINESS_AUTH_RESPONSE.equals(op)) {
			if (StringUtils.isEmpty(challengeAns)) {
				errMsg = "parameter challengeAns len not right:" + challengeAns;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			if (StringUtils.isEmpty(businessName) || businessName.length() > Constant.PARA_LEN.BUSINESSNAME) {
				errMsg = "parameter businessName len not right:" + businessName;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			if (StringUtils.isEmpty(pluginSign) || StringUtils.isEmpty(pluginId)
			        || pluginId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter pluginSign pluginId len not right:" + pluginId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}

		// 更新插件
		if (ServiceConstant.PLUGIN_UPDATE.equals(op)) {
			if (StringUtils.isEmpty(pluginSign) || StringUtils.isEmpty(pluginId)
			        || pluginId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter pluginSign pluginId len not right:" + pluginId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			if (StringUtils.isEmpty(sotpCode) || sotpCode.length() > Constant.PARA_LEN.SOTPCODE_LEN) {
				errMsg = "parameter sotpCode len not right:" + sotpCode;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}
		// 激活插件
		if (ServiceConstant.PLUGIN_ACTIVATE.equals(op)) {
			if (StringUtils.isEmpty(pluginSign) || StringUtils.isEmpty(pluginId)
			        || pluginId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter pluginSign pluginId len not right:" + pluginId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			if (StringUtils.isEmpty(sotpCode) || sotpCode.length() > Constant.PARA_LEN.SOTPCODE_LEN) {
				errMsg = "parameter sotpCode len not right:" + sotpCode;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}
		// 请求挑战码

		// 解绑插件
		if (ServiceConstant.PLUGIN_UNWRAP.equals(op)) {
			if (StringUtils.isEmpty(pluginSign) || StringUtils.isEmpty(pluginId)
			        || pluginId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter pluginSign pluginId len not right:" + pluginId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			if (StringUtils.isEmpty(sotpCode) || sotpCode.length() > Constant.PARA_LEN.SOTPCODE_LEN) {
				errMsg = "parameter sotpCode len not right:" + sotpCode;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}

		// 获取绑定设备列表
		if (ServiceConstant.PLUGIN_LISTALL.equals(op)) {
			if (StringUtils.isEmpty(pluginSign) || StringUtils.isEmpty(pluginId)
			        || pluginId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter pluginSign pluginId len not right:" + pluginId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			if (StringUtils.isEmpty(sotpCode) || sotpCode.length() > Constant.PARA_LEN.SOTPCODE_LEN) {
				errMsg = "parameter sotpCode len not right:" + sotpCode;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}

		// 时间校准
		if (ServiceConstant.BUSINESS_TIME_SYNCHR.equals(op)) {
			if (StringUtils.isEmpty(pluginSign) || StringUtils.isEmpty(pluginId)
			        || pluginId.length() > Constant.PARA_LEN.SOTPID_LEN) {
				errMsg = "parameter pluginSign pluginId len not right:" + pluginId;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
			if (StringUtils.isEmpty(sotpCode) || sotpCode.length() > Constant.PARA_LEN.SOTPCODE_LEN) {
				errMsg = "parameter sotpCode len not right:" + sotpCode;
				result.setMsg(errMsg);
				result.setCode(ErrorConstant.PARA_LEN_ERR);
				return result;
			}
		}

		return result;
	}

	/*
	 * 解析 硬件信息
	 */
	private authFeatureDto parseDevInfo(String devInfo) {

		JSONObject jsonObject = null;
		// base64 解码
		String devstr = "";
		try {
			devstr = Base64.decode(devInfo);
		} catch (Exception e) {
			log.error("Base64.decode error devInfo:" + devInfo);
			return null;
		}

		log.debug("encode devInfo:" + devInfo);
		log.debug("decode devstr len:" + devstr.length() + ", str:" + devstr + ".");

		// 解析json
		try {
			jsonObject = ControllerHelper.parseJsonString(devstr);
		} catch (JSONException e) {
			log.error("parseJsonString error devInfo:" + devstr);
			return null;
		}

		if (jsonObject == null) {
			return null;
		}

		authFeatureDto newauthFeature = new authFeatureDto();
		// 赋值到 (新)硬件认证信息 private authFeatureDto newauthFeature
		newauthFeature.setmanufacturer(ControllerHelper.getJsonElement(jsonObject, "manufacturer"));
		newauthFeature.setProductType(ControllerHelper.getJsonElement(jsonObject, "product_type"));
		newauthFeature.setSystemVersion(ControllerHelper.getJsonElement(jsonObject, "system_version"));
		newauthFeature.setSdkVersion(ControllerHelper.getJsonElement(jsonObject, "sdk_version"));
		newauthFeature.setcpu(ControllerHelper.getJsonElement(jsonObject, "cpu"));
		newauthFeature.setuuid(ControllerHelper.getJsonElement(jsonObject, "uuid"));
		newauthFeature.setimei(ControllerHelper.getJsonElement(jsonObject, "imei"));
		newauthFeature.setimsi(ControllerHelper.getJsonElement(jsonObject, "imsi"));
		newauthFeature.setmac(ControllerHelper.getJsonElement(jsonObject, "mac"));
		newauthFeature.setlocation(ControllerHelper.getJsonElement(jsonObject, "location"));
		newauthFeature.setip(ControllerHelper.getJsonElement(jsonObject, "ip"));
		newauthFeature.setphone_num(ControllerHelper.getJsonElement(jsonObject, "phone_num"));
		newauthFeature.setDev_info(ControllerHelper.sortJsonString(jsonObject));
		if (jsonObject.has("dev_name")) {
			newauthFeature.setDevName(ControllerHelper.getJsonElement(jsonObject, "dev_name"));
		}
		if (jsonObject.has("devId")) {
			newauthFeature.setDevId(ControllerHelper.getJsonElement(jsonObject, "devId"));
		}
		if (jsonObject.has("join_id")) {
			newauthFeature.setJoinId(ControllerHelper.getJsonElement(jsonObject, "join_id"));
		}

		// 匹配移动终端平台类型
		String dev = jsonObject.getString("dev_type");
		if (dev == null) {
			return null;
		}

		if (dev.equals(Constant.MOBILE_TYPE.ANDROID)) {
			String cpu = ControllerHelper.getJsonElement(jsonObject, "cpu");
			if (cpu == null) {
				return null;
			}

			if (cpu.contains(Constant.MOBILE_TYPE.ANDROID_ARM)) {
				newauthFeature.setdevType(Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_ARM);
			} else if (cpu.contains(Constant.MOBILE_TYPE.ANDROID_X86)) {
				newauthFeature.setdevType(Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_X86);
			} else if (cpu.contains(Constant.MOBILE_TYPE.ANDROID_MIPS)) {
				newauthFeature.setdevType(Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_MIPS);
			} else {
				return null;
			}
		} else if (dev.equals(Constant.MOBILE_TYPE.IOS)) {
			newauthFeature.setdevType(Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_IOS);
		} else {
			return null;
		}

		return newauthFeature;
	}

	private String getRootFromDevInfo(String devInfo) {
		String result = Constant.ISROOT.NOTROOT;
		JSONObject jsonObject = null;
		// base64 解码
		String devstr = "";
		try {
			devstr = Base64.decode(devInfo);
		} catch (Exception e) {
			log.error("Base64.decode error devInfo:" + devInfo);
			return result;
		}

		log.debug("encode devInfo:" + devInfo);
		log.debug("decode devstr len:" + devstr.length() + ", str:" + devstr + ".");

		// 解析json
		try {
			jsonObject = ControllerHelper.parseJsonString(devstr);
		} catch (JSONException e) {
			log.error("parseJsonString error devInfo:" + devstr);
			return result;
		}

		if (jsonObject.has("isRoot")) {
			result = ControllerHelper.getJsonElement(jsonObject, "isRoot");
		}

		if (!(Constant.ISROOT.NOTROOT.equals(result)) && !(Constant.ISROOT.ISROOT.equals(result))) {
			result = Constant.ISROOT.NOTROOT;
		}
		return result;
	}

	/*
	 * 解析header and userInfo
	 */
	private UserRequestMsgFido parseHeaderAndUserInfo(String headInfo, String userInfo) {

		JSONObject jsonObjectHead = null;
		JSONObject jsonObjectUser = null;
		// base64 解码
		String headstr = "";
		String userInfoStr = "";
		try {
			headstr = Base64.decode(headInfo);
			userInfoStr = Base64.decode(userInfo);
		} catch (Exception e) {
			log.error("Base64.decode error headInfo:" + headInfo + "userInfo:" + userInfo);
			return null;
		}

		log.debug("encode headInfo:" + headInfo);
		log.debug("decode headstr len:" + headstr.length() + ", str:" + headstr + ".");
		log.debug("encode userInfo:" + userInfo);
		log.debug("decode userInfoStr len:" + userInfoStr.length() + ", userInfoStr:" + userInfoStr + ".");

		// 解析json
		try {
			System.out.println(headstr);
			jsonObjectHead = ControllerHelper.parseJsonString(headstr);
			System.out.println(jsonObjectHead.get("op"));
			jsonObjectUser = ControllerHelper.parseJsonString(userInfoStr);
		} catch (JSONException e) {
			log.error("parseJsonString error headInfo:" + headstr + "userInfo:" + userInfo);
			return null;
		}

		if (null == jsonObjectHead || null == jsonObjectUser) {
			return null;
		}

		UserRequestMsgFido newHeaderAndUser = new UserRequestMsgFido();

		if (jsonObjectHead.has("upv")) {
			log.debug(ControllerHelper.getJsonElement(jsonObjectHead, "upv"));
			JSONObject jsonObjectUpv = jsonObjectHead.getJSONObject("upv");
			if (jsonObjectUpv.has("major") && jsonObjectUpv.has("minor")) {
				String upv = ControllerHelper.getJsonElement(jsonObjectUpv, "major") + "."
				        + ControllerHelper.getJsonElement(jsonObjectUpv, "minor");
				newHeaderAndUser.setUpv(upv);
			}
		}
		newHeaderAndUser.setOp(ControllerHelper.getJsonElement(jsonObjectHead, "op"));
		String op = ControllerHelper.getJsonElement(jsonObjectHead, "op");
		if (!ServiceConstant.BUSINESS_CHECKAPPSIGNHASH.equals(op)) {
			newHeaderAndUser.setPhoneNum(ControllerHelper.getJsonElement(jsonObjectUser, "phoneNum"));
		}
		log.debug(newHeaderAndUser.getPhoneNum());
		return newHeaderAndUser;
	}

	/*
	 * 解析attachedInfo
	 */
	private UserRequestMsgFido parseAttachedInfo(String attachedInfo) {

		JSONObject jsonObjectAttach = null;
		// base64 解码
		String attachedInfoStr = "";
		try {
			attachedInfoStr = Base64.decode(attachedInfo);
		} catch (Exception e) {
			log.error("Base64.decode error attachedInfo:" + attachedInfo);
			return null;
		}

		log.debug("decode attachedInfoStr len:" + attachedInfoStr.length() + ", str:" + attachedInfoStr + ".");

		// 解析json
		try {
			jsonObjectAttach = ControllerHelper.parseJsonString(attachedInfoStr);
		} catch (JSONException e) {
			log.error("parseJsonString error attachedInfo:" + attachedInfo);
			return null;
		}

		if (null == jsonObjectAttach) {
			return null;
		}

		UserRequestMsgFido newAttach = new UserRequestMsgFido();
		if (jsonObjectAttach.has("location")) {
			newAttach.setLocation(ControllerHelper.getJsonElement(jsonObjectAttach, "location"));
		}
		if (jsonObjectAttach.has("ip")) {
			newAttach.setIp(ControllerHelper.getJsonElement(jsonObjectAttach, "ip"));
		}
		if (jsonObjectAttach.has("wifiInfo")) {
			newAttach.setWifiInfo(ControllerHelper.getJsonElement(jsonObjectAttach, "wifiInfo"));
		}
		return newAttach;
	}

	/*
	 * 解析auth
	 */
	private userInfoDto parseAuthByPolicy(String authInfo, int policy) {

		JSONObject jsonObject = null;
		// base64 解码
		String authstr = "";
		try {
			authstr = Base64.decode(authInfo);
		} catch (Exception e) {
			log.error("Base64.decode error authInfo:" + authInfo);
			return null;
		}

		log.debug("encode authInfo:" + authInfo);
		log.debug("decode authstr len:" + authstr.length() + ", str:" + authstr + ".");

		if(!"0".equals(authInfo)){
		// 解析json
		try {
			jsonObject = ControllerHelper.parseJsonString(authstr);
		} catch (JSONException e) {
			log.error("parseJsonString error authstr:" + authstr);
			return null;
		}

		if (jsonObject == null) {
			return null;
		}
		}
		
		userInfoDto newAuth = new userInfoDto();

		// 解析策略
		String policyContent = analysisService.getPolicyContent(policy);
		String[] content = policyContent.split("\\|");

		// TODO
		// 解析auth如何在数据库存储 待定

		for (int i = 0; i < content.length; i++) {
			String value = content[i];
			if ("userInfo".equals(value)) {
				if (jsonObject.has(value)) {
					JSONObject jsonObjectUserInfo = null;
					String userInfoStr = ControllerHelper.getJsonElement(jsonObject, "userInfo");
					try {
						jsonObjectUserInfo = ControllerHelper.parseJsonString(userInfoStr);
					} catch (JSONException e) {
						log.error("parseJsonString error userInfoStr:" + userInfoStr);
						return null;
					}
					if (jsonObjectUserInfo == null) {
						return null;
					}
					if (jsonObjectUserInfo.has("userId")) {
						newAuth.setUserId(ControllerHelper.getJsonElement(jsonObjectUserInfo, "userId"));
					}
					if (jsonObjectUserInfo.has("userName")) {
						newAuth.setUserName(ControllerHelper.getJsonElement(jsonObjectUserInfo, "userName"));
					}
					if (jsonObjectUserInfo.has("userPhone")) {
						newAuth.setUserPhone(ControllerHelper.getJsonElement(jsonObjectUserInfo, "userPhone"));
					}
					if (jsonObjectUserInfo.has("realName")) {
						newAuth.setRealName(ControllerHelper.getJsonElement(jsonObjectUserInfo, "realName"));
					}
					if (jsonObjectUserInfo.has("gender")) {
						newAuth.setGender(ControllerHelper.getJsonElement(jsonObjectUserInfo, "gender"));
					}
				} else {
					log.error("param error userInfo");
				}
			}
		}
		return newAuth;
	}

	/*
	 * 解析pluginSign
	 */
	private UserRequestMsgFido parsePluginSign(String pluginSign) {

		JSONObject jsonObjectPluginSign = null;
		// base64 解码
		String pluginSignStr = "";
		try {
			pluginSignStr = Base64.decode(pluginSign);
		} catch (Exception e) {
			log.error("Base64.decode error pluginSign:" + pluginSign);
			return null;
		}

		log.debug("encode pluginSign:" + pluginSign);
		log.debug("decode pluginSignStr len:" + pluginSignStr.length() + ", str:" + pluginSignStr + ".");

		// 解析json
		try {
			jsonObjectPluginSign = ControllerHelper.parseJsonString(pluginSignStr);
		} catch (JSONException e) {
			log.error("parseJsonString error pluginSignStr:" + pluginSignStr);
			return null;
		}

		if (null == jsonObjectPluginSign) {
			return null;
		}

		UserRequestMsgFido newPluginSign = new UserRequestMsgFido();

		newPluginSign.setPluginId(ControllerHelper.getJsonElement(jsonObjectPluginSign, "pluginId"));
		newPluginSign.setPluginHash(ControllerHelper.getJsonElement(jsonObjectPluginSign, "hash"));
		return newPluginSign;
	}

	/*
	 * 解析 支付信息 （保护码+预留信息）
	 */
	private pluginInfoDto parsePayInfo(String sotpPayInfo, String phonenum, String uid) {

		pluginInfoDto newplInfo = new pluginInfoDto();
		if (sotpPayInfo != null) {
			// base64 解码
			String payInfo = "";

			try {
				payInfo = Base64.decode(sotpPayInfo);
			} catch (Exception e) {
				log.error("Base64.decode error sotpPayInfo:" + sotpPayInfo);
				return null;
			}

			// 解析json
			JSONObject jsonObject;
			try {
				jsonObject = ControllerHelper.parseJsonString(payInfo);
			} catch (JSONException e) {
				log.error("parseJsonString error sotpPayInfo:" + payInfo);
				return null;
			}

			if (jsonObject == null) {
				return null;
			}

			String holdinfo = ControllerHelper.getJsonElement(jsonObject, "holdinfo");
			if (holdinfo == null || holdinfo.length() > Constant.PARA_LEN.HOLDINFO_LEN) {
				return null;
			}

			String protectcode = ControllerHelper.getJsonElement(jsonObject, "protectcode");
			if (protectcode == null || protectcode.length() > Constant.PARA_LEN.PEOTECTCODE_LEN) {
				return null;
			}
			newplInfo.setHoldInfo(holdinfo);
			newplInfo.setProtectCode(protectcode);
		}
		// 赋值到 private pluginInfoDto newplInfo; //新 插件信息记录
		newplInfo.setBindUserphone(phonenum);
		newplInfo.setBindUserid(uid);
		newplInfo.setTotalErrcnt(0);
		newplInfo.setTotalUsecnt(0);
		newplInfo.setVerifyErrcnt(0);

		return newplInfo;
	}

	/*
	 * 解析 支付信息 （保护码+预留信息）
	 */
	private pluginInfoDto parsePayInfoV2(String pin, String phonenum, String uid) {

		pluginInfoDto newplInfo = new pluginInfoDto();
		if (pin != null && pin != "") {
			newplInfo.setProtectCode(pin);
		}

		// 赋值到 private pluginInfoDto newplInfo; //新 插件信息记录
		newplInfo.setBindUserphone(phonenum);
		newplInfo.setBindUserid(uid);
		newplInfo.setTotalErrcnt(0);
		newplInfo.setTotalUsecnt(0);
		newplInfo.setVerifyErrcnt(0);

		return newplInfo;
	}

	/*
	 * 解析函数（可优化） TODO
	 */
	public String getReqParam(String requestMsg, String title) {
		if (requestMsg == null || requestMsg.length() <= 0)
			return null;

		if (title == null || title.length() <= 0)
			return null;

		String[] codesplit = requestMsg.split("&");

		for (String tmp : codesplit) {
			String[] split = tmp.split("=");
			if (split.length == 2) {
				if (split[0].equals(title))
					return split[1];
			}
		}
		return null;
	}

	public Map<String, String> getReqParamMap(String requestMsg) {
		if (requestMsg == null || requestMsg.length() <= 0)
			return null;

		String[] codesplit = requestMsg.split("&");

		Map<String, String> paramMap = new HashMap<String, String>();
		for (String tmp : codesplit) {
			String[] split = tmp.split("=");
			if (split.length == 2) {
				paramMap.put(split[0], split[1]);
			}
		}
		if (paramMap.size() > 0) {
			return paramMap;
		} else {
			return null;
		}
	}

	@Override
	public int updAppInfo(appInfoDto appInfoDto) {
		int ret = ErrorConstant.SQL_READMYSQL_ERR;
		try {
			ret = appInfoMapper.updAppInfo(appInfoDto);
		} catch (SQLException e) {
			log.error("appInfoMapper updAppInfo error.msg:%s", e.toString());
			return ret;
		}
		return ret;
	}

	@Override
	public appInfoDto getAppInfoByAppId(String appId) {
		appInfoDto appInfo = null;
		try {
			appInfo = appInfoMapper.selectAppInfoByAppId(appId);
		} catch (SQLException e) {
			log.error("appInfoMapper selectAppInfoByAppId sql error.msg:%s", e.toString());
			return appInfo;
		}
		return appInfo;
	}

	@Override
	public appVersionInfoDto getAppVersionInfoByAppId(String appId) {
		try {
			String key = cachePrefix + REDIS_CONSTANT.APPLY_CODE_KEY_PREFIX + appId;
			appVersionInfoDto c = (appVersionInfoDto) redisTemplate.opsForValue().get(key);
			// 没取到，读库取
			if (c == null) {
				synchronized (this) {
					if (!redisTemplate.hasKey(key)) {
						appVersionInfoDto appVersionInfo = null;
						try {
							appVersionInfo = this.appVersionInfoMapper.selectAppVersionInfoByAppId(appId);
						} catch (SQLException e) {
							log.error("appVersionInfoMapper selectAppVersionInfoByAppId sql error.msg:%s",
							        e.toString());
							return appVersionInfo;
						}
						if (appVersionInfo != null) {
							redisTemplate.opsForValue().set(key, appVersionInfo);
						}
					}
				}
				c = (appVersionInfoDto) redisTemplate.opsForValue().get(key);
			}
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		// 异常之后，直接读库返回结果
		appVersionInfoDto appVersionInfo = null;
		try {
			appVersionInfo = this.appVersionInfoMapper.selectAppVersionInfoByAppId(appId);
		} catch (SQLException e) {
			log.error("appVersionInfoMapper selectAppVersionInfoByAppId sql error.msg:%s", e.toString());
			return appVersionInfo;
		}
		return appVersionInfo;
	}

	@Override
	public String getBusinessCodeByAppInfoId(int appInfoId) {
		try {
			String businessCode = "";
			String key = cachePrefix + REDIS_CONSTANT.APPLY_CODE_KEY_PREFIX +"getBusinessCode."+ appInfoId;
			String c = (String) redisTemplate.opsForValue().get(key);
			
			
			if (c == null) {
				synchronized (this) {
					if (!redisTemplate.hasKey(key)) {
						try {
							businessCode = appInfoMapper.selectBusinessCodeByAppInfoId(appInfoId);
						} catch (SQLException e) {
							log.error("appInfoMapper selectBusinessCodeByAppInfoId sql error.msg:%s", e.toString());
							return businessCode;
						}
						if (businessCode != null) {
							redisTemplate.opsForValue().set(key, businessCode);
						}
					}
				}
				c = (String) redisTemplate.opsForValue().get(key);
			}
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
			String businessCode = "";
			try {
				businessCode = appInfoMapper.selectBusinessCodeByAppInfoId(appInfoId);
			} catch (SQLException e) {
				log.error("appInfoMapper selectBusinessCodeByAppInfoId sql error.msg:%s", e.toString());
				return businessCode;
			}
			return businessCode;
		}

	@Override
	public String getAppInfoCodeByAppInfoId(int appInfoId) {

		try {
			String appInfoCode = "";
			String key = cachePrefix + REDIS_CONSTANT.APPLY_CODE_KEY_PREFIX +"getAppInfoCode."+ appInfoId;
			String c = (String) redisTemplate.opsForValue().get(key);
			
			
			if (c == null) {
				synchronized (this) {
					if (!redisTemplate.hasKey(key)) {
						try {
							appInfoCode = appInfoMapper.selectAppInfoCodeByAppInfoId(appInfoId);
						} catch (SQLException e) {
							log.error("appInfoMapper selectAppInfoCodeByAppInfoId sql error.msg:%s", e.toString());
							return appInfoCode;
						}
						if (appInfoCode != null) {
							redisTemplate.opsForValue().set(key, appInfoCode);
						}
					}
				}
				c = (String) redisTemplate.opsForValue().get(key);
			}
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		
		String appInfoCode = "";
		try {
			appInfoCode = appInfoMapper.selectAppInfoCodeByAppInfoId(appInfoId);
		} catch (SQLException e) {
			log.error("appInfoMapper selectAppInfoCodeByAppInfoId sql error.msg:%s", e.toString());
			return appInfoCode;
		}
		return appInfoCode;
	}

	@Override
	public ResultVO checkSignature(UserRequestMsgV2 requestMsg) {
		ResultVO result = new ResultVO();
		if(!Constant.ISCHECKSIGN.ISCHECK.equals(checkSign)){
			return result;
		}
		
		String errMsg = "";

		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();
		if (null == appId || null == sign) {
			errMsg = "Illegal request: no appId or sign!";
			result.setCode(ErrorConstant.PARA_LACK_ITEM);
			result.setMsg(errMsg);
			return result;
		}

		// 应用信息验证
		appVersionInfoDto appVersionInfo = cacheService.getAPPVersionInfoByCode(appId);
		if (null == appVersionInfo) {
			errMsg = "Illegal request: wrong appId!";
			result.setCode(ErrorConstant.PARA_ILLEGAL);
			result.setMsg(errMsg);
			return result;
		}

		// 签名校验
		String appKey = appVersionInfo.getApp_key();
		Map<String, Object> paramMap = requestMsg.getSignParaMap();
		if (!signService.verifySign(appKey, paramMap, sign)) {
			errMsg = "Illegal request: wrong sign!";
			result.setCode(ErrorConstant.PARA_ILLEGAL);
			result.setMsg(errMsg);
			return result;
		}

		// 验证客户端签名
		String service = requestMsg.getService();
		if (!StringUtils.isEmpty(saveSessionKey) && "true".equals(saveSessionKey) &&
				!ServiceConstant.PLUGIN_REG.equals(service) && !ServiceConstant.PLUGIN_APPLY2.equals(service)&&
				!ServiceConstant.BUSINESS_GENSESSKEY.equals(service) && !ServiceConstant.BUSINESS_DATADECRYPTION.equals(service) &&
				!ServiceConstant.BUSINESS_DATAENCRYPTION.equals(service) && !ServiceConstant.PLUGIN_LISTALL.equals(service)) {
			// TODO
			String clientSign = requestMsg.getClientSign();
			String pluginId = requestMsg.getSotpId();
			// 取sessionKey
			Map<String, Object> clientParamMap = requestMsg.getClientSignParaMap();
			String sessionKey = businessService.getSessionKey(pluginId);
			if(StringUtils.isEmpty(sessionKey)){
				errMsg = "Illegal request: get sessionKey wrong!";
				result.setCode(ErrorConstant.PARA_ILLEGAL);
				result.setMsg(errMsg);
				return result;
			}
			if (!signService.verifyClientSign(sessionKey, clientParamMap, clientSign)) {
				errMsg = "Illegal request: wrong clientSign!";
				result.setCode(ErrorConstant.PARA_ILLEGAL);
				result.setMsg(errMsg);
				return result;
			}
		}
		return result;
	}

	@Override
	public ResultVO checkSignature(UserRequestMsgFido requestMsg) {
		ResultVO result = new ResultVO();
		String errMsg = "";

		String appId = requestMsg.getAppId();
		String sign = requestMsg.getSign();
		if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(sign)) {
			errMsg = "Illegal request: no appId or sign!";
			result.setCode(ErrorConstant.PARA_LACK_ITEM);
			result.setMsg(errMsg);
			return result;
		}

		// 应用信息验证
		appVersionInfoDto appVersionInfo = cacheService.getAPPVersionInfoByCode(appId);
		if (null == appVersionInfo) {
			errMsg = "Illegal request: wrong appId!";
			result.setCode(ErrorConstant.PARA_ILLEGAL);
			result.setMsg(errMsg);
			return result;
		}

		// 签名校验
		String appKey = appVersionInfo.getApp_key();
		Map<String, Object> paramMap = requestMsg.getSignParaMap();
		if (!signService.verifySign(appKey, paramMap, sign)) {
			errMsg = "Illegal request: wrong sign!";
			result.setCode(ErrorConstant.PARA_ILLEGAL);
			result.setMsg(errMsg);
			return result;
		}

		// 验证客户端签名
		String service = requestMsg.getOp();
		if ("true".equals(saveSessionKey) && !ServiceConstant.PLUGIN_REG_AUTH.equals(service) &&
				!ServiceConstant.PLUGIN_REG.equals(service) && !ServiceConstant.PLUGIN_APPLY2.equals(service)&&
				!ServiceConstant.BUSINESS_GENSESSKEY.equals(service) && !ServiceConstant.BUSINESS_DATADECRYPTION.equals(service) &&
				!ServiceConstant.BUSINESS_DATAENCRYPTION.equals(service)) {
			// TODO
			String clientSign = requestMsg.getClientSign();
			String pluginId = requestMsg.getPluginId();
			// 取sessionKey
			Map<String, Object> clientParamMap = requestMsg.getClientSignParaMap();
			String sessionKey = businessService.getSessionKey(pluginId);
			if(StringUtils.isEmpty(sessionKey)){
				errMsg = "Illegal request: get sessionKey wrong!";
				result.setCode(ErrorConstant.PARA_ILLEGAL);
				result.setMsg(errMsg);
				return result;
			}
			if (!signService.verifyClientSign(sessionKey, clientParamMap, clientSign)) {
				errMsg = "Illegal request: wrong clientSign!";
				result.setCode(ErrorConstant.PARA_ILLEGAL);
				result.setMsg(errMsg);
				return result;
			}
		}
				
		return result;
	}

	public String getDevInfo(authFeatureDto authFeaInfo) {
		int plugintype = authFeaInfo.getdevType();
		String dev = "";
		switch (plugintype) {
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_ARM:
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_MIPS:
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_X86:
			dev = authFeaInfo.getProductType() + "_" + authFeaInfo.getimei();
			break;

		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_IOS:
			dev = authFeaInfo.getProductType() + "-" + authFeaInfo.getuuid() + "-" + authFeaInfo.getmac();
			break;
		}
		return dev;
	}

	public String getDevInfoJson(authFeatureDto authFeaInfo, String phoneNum, String appId,
	        RulePolicyDto applyPolicyInfo) {
		int plugintype = authFeaInfo.getdevType();
		JSONObject result = new JSONObject();
		String dev = "";
		switch (plugintype) {
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_ARM:
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_MIPS:
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_X86:
			dev = authFeaInfo.getProductType() + "_" + authFeaInfo.getimei() + "_" + authFeaInfo.getmac();
			break;

		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_IOS:
			dev = authFeaInfo.getProductType() + "-" + authFeaInfo.getuuid() + "-" + authFeaInfo.getmac();
			break;
		}
		Map<String, Object> userInfoMap = new HashMap<String, Object>();
		userInfoMap.put("phoneNumber", phoneNum);
		int genType = policyService.getPluginType(applyPolicyInfo);
		log.debug("Apply Policy genType :" + genType);
		if (genType != 0 && (genType == Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_NOENCRYPT
		        || genType == Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_STATIC)) {
			userInfoMap.put("genType", genType);
		}
		result.put("userInfo", userInfoMap);
		result.put("devInfo", dev);

		return result.toString();
	}

	public String responseHandle(String appId, String service, int status, String pluginContent, String hwInfo,
	        String errMsg, String reqmsg, HttpServletRequest request, String pluginId) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String timeinfo = dateFormat.format(System.currentTimeMillis());

		String nonce_str = StringUtils.getRandomStr(10);

		Map<String, Object> message = new HashMap<String, Object>();
		if (pluginContent != null && pluginContent.length() > 0) {
			message.put("data", pluginContent);
		}

		if (hwInfo != null && hwInfo.length() > 0) {
			message.put("hwInfo", hwInfo);
		}

		if (ServiceConstant.PLUGIN_REG.equals(service) || ServiceConstant.PLUGIN_UPDATE.equals(service)
		        || ServiceConstant.PLUGIN_UPDATE2.equals(service) || ServiceConstant.PLUGIN_APPLY2.equals(service)) {
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
		}
		if (status != ErrorConstant.RET_OK) {
			if (errMsg != null && errMsg.length() > 0) {
				message.put("errorMsg", errMsg);
			}
		} else {
			// 保存返回状态信息到request中，以便统计接口成功与否
			request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		}

		appVersionInfoDto appVersionInfo = cacheService.getAPPVersionInfoByCode(appId);
		String appKey = appVersionInfo.getApp_key();
		String sessionKey = "";
		if (!StringUtils.isEmpty(saveSessionKey) && "true".equals(saveSessionKey) &&
				!ServiceConstant.PLUGIN_REG.equals(service) && !ServiceConstant.PLUGIN_APPLY2.equals(service)&&
				!ServiceConstant.BUSINESS_GENSESSKEY.equals(service)  && !ServiceConstant.BUSINESS_DATADECRYPTION.equals(service) &&
				!ServiceConstant.BUSINESS_DATAENCRYPTION.equals(service) && !ServiceConstant.PLUGIN_LISTALL.equals(service)) {
			sessionKey = businessService.getSessionKey(pluginId);
		}

		String sign = "";
		String clientSign = "";
		
		String jsonResStr = ControllerHelper.getResultJsonStringV2(appId, nonce_str, sign, status, message, timeinfo, pluginId, clientSign, appKey, sessionKey, signService);
		/*JSONObject result = JSONObject.fromObject(jsonResStr);
		paramMap = toHashMap(result);
		if(ServiceConstant.PLUGIN_REG.equals(service)|| ServiceConstant.PLUGIN_UPDATE.equals(service)){
			paramMap.remove("message");
		}
		sign = signService.signParaByAppKey(paramMap, appKey, Constant.SIGN_METHOD.SHA);
		jsonResStr = ControllerHelper.getResultJsonStringV2(appId, nonce_str, sign, status, message, timeinfo, "", clientSign);*/
		log.debug("sotpAuth response:" + jsonResStr);

		// 记录业务日志
		this.businessService.setBusinessLog(reqmsg, jsonResStr, status, errMsg);

		return jsonResStr;
	}

	public String responseHandleWithResult(String appId, String service, int status, String pluginContent,
	        String hwInfo, String errMsg, String reqmsg, HttpServletRequest request, Map<String, Object> resultMap) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String timeinfo = dateFormat.format(System.currentTimeMillis());

		String nonce_str = StringUtils.getRandomStr(10);

		Map<String, Object> message = new HashMap<String, Object>();
		/*
		 * if (pluginContent != null && pluginContent.length() > 0) {
		 * message.put("data", pluginContent); }
		 */

		StringBuilder sb = new StringBuilder();
		if (resultMap.containsKey("checkSign")) {
			sb.append("\"{\"checkSign\":\"" + resultMap.get("checkSign") + "\",");
		}

		if (resultMap.containsKey("checkHash")) {
			sb.append("\"checkHash\":\"" + resultMap.get("checkHash") + "\"}\"");
		}

		if (resultMap.containsKey("checkDev")) {
			sb.append("\"{\"checkDev\":\"" + resultMap.get("checkDev") + "\",");
		}

		if (resultMap.containsKey("checkPluginHash")) {
			sb.append("\"checkPluginHash\":\"" + resultMap.get("checkPluginHash") + "\"}\"");
		}

		message.put("data", sb.toString());
		if (hwInfo != null && hwInfo.length() > 0) {
			message.put("hwInfo", hwInfo);
		}

		if (ServiceConstant.PLUGIN_REG.equals(service) || ServiceConstant.PLUGIN_UPDATE.equals(service)
		        || ServiceConstant.PLUGIN_UPDATE2.equals(service) || ServiceConstant.PLUGIN_APPLY2.equals(service)) {
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
		}
		if (status != ErrorConstant.RET_OK) {
			if (errMsg != null && errMsg.length() > 0) {
				message.put("errorMsg", errMsg);
			}
		} else {
			// 保存返回状态信息到request中，以便统计接口成功与否
			request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		}

		appVersionInfoDto appVersionInfo = cacheService.getAPPVersionInfoByCode(appId);
		String appKey = appVersionInfo.getApp_key();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sign = "";
		String clientSign = "";
		String jsonResStr = ControllerHelper.getResultJsonStringV2(appId, nonce_str, sign, status, message, timeinfo, "", clientSign, appKey, "", signService);
		/*JSONObject result = JSONObject.fromObject(jsonResStr);
		paramMap = toHashMap(result);
		sign = signService.signParaByAppKey(paramMap, appKey, Constant.SIGN_METHOD.SHA);
		jsonResStr = ControllerHelper.getResultJsonStringV2(appId, nonce_str, sign, status, message, timeinfo, "", clientSign, appKey, sessionKey);*/
		log.debug("sotpAuth response:" + jsonResStr);

		// 记录业务日志
		this.businessService.setBusinessLog(reqmsg, jsonResStr, status, errMsg);

		return jsonResStr;
	}

	@Override
	public String responseHandleWithSotpInfo(String appId, String service, int status, String pluginContent,
	        String hwInfo, String errMsg, String reqmsg, HttpServletRequest request, String devId, String sotpId,
	        String sotpStatusStr, String devName, Object devlist) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String timeinfo = dateFormat.format(System.currentTimeMillis());

		String nonce_str = StringUtils.getRandomStr(10);

		Map<String, Object> message = new HashMap<String, Object>();
		if (pluginContent != null && pluginContent.length() > 0) {
			message.put("data", pluginContent);
		}
		if (devlist != null) {
			message.put("data", devlist);
		}

		if (hwInfo != null && hwInfo.length() > 0) {
			message.put("hwInfo", hwInfo);
		}

		if (devId != null && devId.length() > 0) {
			message.put("devId", devId);
		}

		if (devName != null && devName.length() > 0) {
			message.put("devName", devName);
		}

		if (sotpId != null && sotpId.length() > 0) {
			message.put("sotpId", sotpId);
		}

		if (sotpStatusStr != null && sotpStatusStr.length() > 0) {
			message.put("sotpStatus", sotpStatusStr);
		}

		if (ServiceConstant.PLUGIN_REG.equals(service) || ServiceConstant.PLUGIN_UPDATE.equals(service)
		        || ServiceConstant.PLUGIN_UPDATE2.equals(service) || ServiceConstant.PLUGIN_APPLY2.equals(service)) {
			// 释放该线程保存插件的缓存区
			PluginSaveHelper.release();
		}
		if (status != ErrorConstant.RET_OK) {
			if (errMsg != null && errMsg.length() > 0) {
				message.put("errorMsg", errMsg);
			}
		} else {
			// 保存返回状态信息到request中，以便统计接口成功与否
			request.setAttribute("_retStatus", ErrorConstant.RET_OK);
		}

		appVersionInfoDto appVersionInfo = cacheService.getAPPVersionInfoByCode(appId);
		String appKey = appVersionInfo.getApp_key();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sign = "";
		String clientSign = "";
		String jsonResStr = ControllerHelper.getResultJsonStringV2(appId, nonce_str, sign, status, message, timeinfo, "", clientSign, appKey, "", signService);
		/*JSONObject result = JSONObject.fromObject(jsonResStr);
		paramMap = toHashMap(result);
		sign = signService.signParaByAppKey(paramMap, appKey, Constant.SIGN_METHOD.SHA);
		jsonResStr = ControllerHelper.getResultJsonStringV2(appId, nonce_str, sign, status, message, timeinfo, "", clientSign, appKey, "");*/
		log.debug("sotpAuth response:" + jsonResStr);

		// 记录业务日志
		this.businessService.setBusinessLog(reqmsg, jsonResStr, status, errMsg);

		return jsonResStr;
	}

	@Override
    public UserRequestMsgFido getNewTradeInfo(String tradeInfo) {


		JSONObject jsonObjectTrade = null;
		// base64 解码
		String tradeInfoStr = "";
		try {
			tradeInfoStr = Base64.decode(tradeInfo);
		} catch (Exception e) {
			log.error("Base64.decode error attachedInfo:" + tradeInfo);
			return null;
		}

		    log.debug("decode tradeInfoStr len:" + tradeInfoStr.length() + ", str:" + tradeInfoStr + ".");

		// 解析json
		try {
			jsonObjectTrade = ControllerHelper.parseJsonString(tradeInfoStr);
		} catch (JSONException e) {
			log.error("parseJsonString error tradeInfo:" + tradeInfo);
			return null;
		}

		if (null == jsonObjectTrade) {
			return null;
		}

		UserRequestMsgFido newTradeInfo = new UserRequestMsgFido();

		newTradeInfo.setPayAction(ControllerHelper.getJsonElement(jsonObjectTrade, "action"));
		newTradeInfo.setPayCard( ControllerHelper.getJsonElement(jsonObjectTrade, "payCard"));
		newTradeInfo.setPayCardType( ControllerHelper.getJsonElement(jsonObjectTrade, "payCardType"));
		newTradeInfo.setRecCard(ControllerHelper.getJsonElement(jsonObjectTrade, "recCard"));
		newTradeInfo.setPrice(BigDecimal.valueOf(Double.valueOf((ControllerHelper.getJsonElement(jsonObjectTrade, "price")))));
		newTradeInfo.setPayee(ControllerHelper.getJsonElement(jsonObjectTrade, "payee"));
		
		return newTradeInfo;
 
    }
	public String filterRequestMap(Map<String, Object>map){
		log.debug("request map" +map.toString());
		Vector<String>vect=new Vector<String>();
		//筛选null和不用的字段
		for (String key : map.keySet()) {
			Object value = map.get(key);
			if(null==value|"".equals(value)|"appId".equals(key)|"version".equals(key)|"nonce_str".equals(key)|"sign".equals(key)|"service".equals(key)){
				vect.addElement(key);
			}
		}
		for (int i = 0; i < vect.size(); i++) {
			map.remove(vect.get(i));
		}
		log.debug("request map end" +map.toString());
		return map.toString();
	}

}
