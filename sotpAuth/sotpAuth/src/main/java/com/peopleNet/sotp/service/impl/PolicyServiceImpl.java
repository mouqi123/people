package com.peopleNet.sotp.service.impl;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.controller.ControllerHelper;
import com.peopleNet.sotp.dal.dao.authFeatureDtoMapper;
import com.peopleNet.sotp.dal.dao.pluginStatisticDtoMapper;
import com.peopleNet.sotp.dal.dao.userInfoDtoMapper;
import com.peopleNet.sotp.dal.model.RulePolicyDto;
import com.peopleNet.sotp.dal.model.authFeatureDto;
import com.peopleNet.sotp.dal.model.authPolicyDto;
import com.peopleNet.sotp.dal.model.pluginInfoDto;
import com.peopleNet.sotp.dal.model.policyDetailDto;
import com.peopleNet.sotp.exception.BusinessException;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.*;
import com.peopleNet.sotp.util.Base64;
import com.peopleNet.sotp.util.DateUtil;
import com.peopleNet.sotp.util.StringUtils;
import com.peopleNet.sotp.util.UpdateUtil;
import com.peopleNet.sotp.vo.ResultVO;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@Service
public class PolicyServiceImpl implements IPolicyService {
	private static LogUtil log = LogUtil.getLogger(PolicyServiceImpl.class);

	@Autowired
	private ICacheService cacheService;

	@Autowired
	private IAnalysisService analysisService;
	@Autowired
	private pluginStatisticDtoMapper pluginStatisticMapper;
	@Autowired
	private IEncryptPluginInfoService pluginInfoMapper;
	@Autowired
	private IParaHandle paraHandle;
	@Autowired
	private authFeatureDtoMapper authFeatureMapper;
	/*
	 * 对已锁定插件进行解锁操作 返回 结果为该插件状态。若解锁成功，则返回ErrorConstant.RET_OK；
	 * 否则返回ErrorConstant.PLUGIN_LOCKED
	 */
	public int unlockPluginInfo(pluginInfoDto plInfo, RulePolicyDto verifyPolicyInfo) {
		int status = ErrorConstant.PLUGIN_LOCKED;

		int isUnlock = 0;
		if (verifyPolicyInfo.getIsUnlock() != null && verifyPolicyInfo.getIsUnlock() >= 0)
			isUnlock = verifyPolicyInfo.getIsUnlock();

		int autoUnlockTime = 0;
		if (verifyPolicyInfo.getAutoUnlockTime() != null && verifyPolicyInfo.getAutoUnlockTime() >= 0)
			autoUnlockTime = verifyPolicyInfo.getAutoUnlockTime();

		// 若允许自动解锁，先解锁
		if (plInfo.getStatus() == Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_LOCKED
		        && isUnlock == Constant.AUTOUNLOCK.ALLOW) {
			if (DateUtil.compareDate(plInfo.getErrDay(), autoUnlockTime * 3600000L)) {
				plInfo.setVerifyErrcnt(0);
				plInfo.setStatus(Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);

				UpdateUtil updateInfo = new UpdateUtil();
				updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.STATUS,
				        Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);
				updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.VERIFY_ERRCNT, 0);
				updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plInfo.getPluginId());
				Map<String, Object> updateInfoMap = updateInfo.getUpdateInfomap();
				this.pluginInfoMapper.updateHashMapByPluginId(updateInfoMap);

				return ErrorConstant.RET_OK;
			}
		}

		return status;
	}

	/*
	 * 插件状态 验证
	 */
	public ResultVO pluginStatus(pluginInfoDto plInfo, RulePolicyDto verifyPolicyIfo) {
		ResultVO result = new ResultVO();
		String errMsg = "";
		if (plInfo == null) {
			errMsg = "pluginStatus plInfo null";
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.SYSTEM_UNKOWN_ERR);
			return result;
		}

		if (plInfo.getStatus() != Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY) {
			int status = ErrorConstant.PLUGIN_UNKOWN_STATE;

			switch (plInfo.getStatus()) {
			case Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_HANGUP:
				status = ErrorConstant.PLUGIN_HANDUP;
				break;
			case Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_LOCKED:
				// 调用解锁函数检查是否可以自动解锁
				status = unlockPluginInfo(plInfo, verifyPolicyIfo);
				break;
			case Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NO_ACTIVATE:
				status = ErrorConstant.PLUGIN_NOTACTIVE;
				break;
			case Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_WAIT_DOWN:
				status = ErrorConstant.PLUGIN_WAITDOWN;
				break;
			case Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS:
				status = ErrorConstant.PLUGIN_USELESS;
				break;
			}
			if (status != ErrorConstant.RET_OK) {
				log.error("getstatus plugin not ready:" + plInfo.getStatus());
				errMsg = "plugin status not ready.status:" + plInfo.getStatus();
				result.setMsg(errMsg);
				result.setCode(status);
				return result;
			}
		}
		return result;
	}

	/*
	 * 插件更新策略
	 */
	public ResultVO updatePolicy(String appId, pluginInfoDto plInfo) {
		int updatecyle = 0;
		int uptotalUseCnt = 0;
		int uptotalErrCnt = 0;
		int starttime = 0;

		ResultVO result = new ResultVO();
		String errMsg = "";

		RulePolicyDto updatePolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.UPDATE, appId);
		if (updatePolicyInfo == null || plInfo == null) {
			errMsg = "system inner get updatePolicy null or plInfo null";
			log.error(errMsg);
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.SYSTEM_FUN_ERR);
			return result;
		}

		if (null == updatePolicyInfo.getUpdateCycle()) {
			log.error("redis updatePolicyInfo.getUpdateCycle() null, get default value 0");
		} else {
			updatecyle = updatePolicyInfo.getUpdateCycle();
		}

		if (null == updatePolicyInfo.getTotalCsecnt()) {
			log.error("redis updatePolicyInfo.getTotalCsecnt() null, get default value 0");
		} else {
			uptotalUseCnt = updatePolicyInfo.getTotalCsecnt();
		}

		if (null == updatePolicyInfo.getTotalErrcnt()) {
			log.error("redis updatePolicyInfo.getTotalErrcnt() null, get default value 0");
		} else {
			uptotalErrCnt = updatePolicyInfo.getTotalErrcnt();
		}

		starttime = Integer.parseInt(DateUtil.timeStamp(plInfo.getStartTime().getTime()));

		log.info("getstatus UpdatePolicy  update cycle:" + updatecyle + ", usenum:" + uptotalUseCnt + ", errnum:"
		        + uptotalErrCnt);
		log.info("getstatus plugin start time:" + starttime + ", now time:" + DateUtil.timeStamp(0) + ", usenum:"
		        + plInfo.getTotalUsecnt() + ", errnum:" + plInfo.getTotalErrcnt());

		// 比较更新周期
		int distime = Integer.parseInt(DateUtil.timeStamp(0)) - starttime;
		if (distime > updatecyle) {
			errMsg = "update time reach, need update.";
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PLUGIN_UPDATE_CYCLE);
			return result;
		}

		// 比较总验证次数
		if (plInfo.getTotalUsecnt() >= uptotalUseCnt) {
			errMsg = "total usenum reach, need update.";
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PLUGIN_UPDATE_USENUM);
			return result;
		}

		// 比较总验证错误数
		if (plInfo.getTotalErrcnt() >= uptotalErrCnt) {
			errMsg = "error count reach, need update.";
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PLUGIN_UPDATE_ERRNUM);
			return result;
		}

		return result;
	}

	// 短信码验证策略
	public ResultVO smsVeryPolicy(String sms, ICacheService cacheService, userInfoDtoMapper userInfoMapper) {

		ResultVO result = new ResultVO();
		String errMsg = "";
		if (sms == null) {
			result.setCode(ErrorConstant.RET_OK);
			result.setMsg(errMsg);
			return result;
		}

		// cacheService redis 中取 ，
		// 若存在，则比较。 不存在，则已超时，错误
		// TODO

		// userInfoMapper 数据库中存在用户， ok; 否则， 错误
		// TODO

		return result;
	}

	// 插件申请策略
	public ResultVO pluginReqPolicy(String phoneNum, String appId, RulePolicyDto applyPolicyInfo) {

		ResultVO result = new ResultVO();
		String errMsg = "";

		// TODO 根据策略设置插件初始状态，验证用户每天最多申请次数，
		// 用户每天申请插件的次数
		int reqnum = 0;
		if (null == applyPolicyInfo.getReqnum()) {
			log.debug("redis reqPolicyInfo.getReqnum null, get default value 0");
		} else {
			reqnum = applyPolicyInfo.getReqnum();
		}

		// 获取该用户当天已下载插件个数
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String today = dateFormat.format(new Date(System.currentTimeMillis()));
		Long alreadyRequestNum = this.cacheService.getUserReqeustPluginNum(today, phoneNum, appId);

		if (null != applyPolicyInfo.getReqnum() && null != alreadyRequestNum && alreadyRequestNum >= reqnum) {
			errMsg = "this user has already download max times today.";
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PLUGIN_HAVE_DOWNLOAD_MAX_TIMES);
			return result;
		}
		return result;
	}

	public ResultVO checkRootPolicy(RulePolicyDto rulePolicy) {
		ResultVO result = new ResultVO();
		result.setCode(ErrorConstant.RET_OK);
		String errMsg = "";

		if (rulePolicy.getIsAllowRoot() != null && rulePolicy.getIsAllowRoot() == Constant.ISALLOWROOT.NOTALLOWROOT) {
			errMsg = "verify policy not allow root device";
			result.setCode(ErrorConstant.DEV_ROOT_ERR);
			result.setMsg(errMsg);
		}
		return result;
	}

	// 认证码 验证策略，针对已加锁插件。若满足解锁条件，将解锁
	public ResultVO codeVerifyPolicy(pluginInfoDto plInfo, String appId, RulePolicyDto verifyPolicyInfo) {

		ResultVO result = new ResultVO();
		result.setCode(ErrorConstant.RET_OK);

		if (Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_LOCKED != plInfo.getStatus()) {
			return result;
		}

		int isUnlock = 0;
		if (verifyPolicyInfo.getIsUnlock() != null && verifyPolicyInfo.getIsUnlock() >= 0)
			isUnlock = verifyPolicyInfo.getIsUnlock();

		int autoUnlockTime = 0;
		if (verifyPolicyInfo.getAutoUnlockTime() != null && verifyPolicyInfo.getAutoUnlockTime() >= 0)
			autoUnlockTime = verifyPolicyInfo.getAutoUnlockTime();

		// 若允许自动解锁，先解锁
		if (plInfo.getStatus() == Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_LOCKED && isUnlock == 1) {
			if (DateUtil.compareDate(plInfo.getErrDay(), autoUnlockTime * 3600000L)) {
				plInfo.setVerifyErrcnt(0);
				plInfo.setStatus(Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);
			}
		}

		return result;
	}

	// 获取策略中配置的连续验证错误锁定次数
	public int getMaxErrorTime(RulePolicyDto verifyPolicyInfo) {
		int errorTimes = 0;
		if (verifyPolicyInfo.getErrorTimes() != null && verifyPolicyInfo.getErrorTimes() >= 0) {
			errorTimes = verifyPolicyInfo.getErrorTimes();
			log.info("redis PolicyServiceImpl getMaxErrorTime errorTimes:" + errorTimes);
		}

		return errorTimes;
	}

	public int getAutoUnlockTime(RulePolicyDto verifyPolicyInfo) {
		int autoUnlockTime = 0;
		if (verifyPolicyInfo.getAutoUnlockTime() != null && verifyPolicyInfo.getAutoUnlockTime() >= 0) {
			autoUnlockTime = verifyPolicyInfo.getAutoUnlockTime();
			log.info("redis PolicyServiceImpl getAutoUnlockTime autoUnlockTime:" + autoUnlockTime);
		}

		return autoUnlockTime;
	}

	public int getIsIntegrityCheck(RulePolicyDto verifyPolicyInfo) {
		int isIntegrityCheck = 1;
		if (verifyPolicyInfo.getIsIntegrityCheck() != null && verifyPolicyInfo.getIsIntegrityCheck() == 0) {
			isIntegrityCheck = verifyPolicyInfo.getIsIntegrityCheck();
			log.debug("redis PolicyServiceImpl getIsIntegrityCheck :" + isIntegrityCheck);
		}

		return isIntegrityCheck;
	}

	public int getIsTwoAuthIntegrityCheck(RulePolicyDto applyPolicyInfo) {
		int twoAuthIntegrityCheck = 1;
		if (applyPolicyInfo.getTwoAuthIntegrityCheck() != null && applyPolicyInfo.getTwoAuthIntegrityCheck() == 0) {
			twoAuthIntegrityCheck = applyPolicyInfo.getTwoAuthIntegrityCheck();
			log.debug("redis PolicyServiceImpl getIsIntegrityCheckForApply :" + twoAuthIntegrityCheck);
		}
		return twoAuthIntegrityCheck;
	}

	public int getPluginType(RulePolicyDto applyPolicyInfo) {
		int pluginType = 0;
		if (applyPolicyInfo.getPluginType() != null) {
			pluginType = applyPolicyInfo.getPluginType();
			log.debug("redis PolicyServiceImpl getPluginType :" + pluginType);
		}

		return pluginType;
	}

	public int getIsUnlock(RulePolicyDto verifyPolicyInfo) {
		int isUnlock = 0;
		if (verifyPolicyInfo.getIsUnlock() != null && verifyPolicyInfo.getIsUnlock() >= 0) {
			isUnlock = verifyPolicyInfo.getIsUnlock();
			log.info("redis PolicyServiceImpl getIsUnlock:" + isUnlock);
		}

		return isUnlock;
	}

	// 获取策略中配置的挑战码的超时时间
	public int getChallengeCodeTimeOut(RulePolicyDto verifyPolicyInfo) {
		int challengeCodeTimeout = 0;
		if (verifyPolicyInfo.getChallengeTimeout() != null && verifyPolicyInfo.getChallengeTimeout() >= 0) {
			challengeCodeTimeout = verifyPolicyInfo.getChallengeTimeout();
			log.info("redis PolicyServiceImpl getChallengeCodeTimeOut challengeCodeTimeout:" + challengeCodeTimeout);
		}

		return challengeCodeTimeout;
	}

	// 策略 转换 （数据库 policyDetail -> policyDetail）
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peopleNet.sotp.service.IPolicyService#dbPolicyCovert(java.util.List)
	 */
	public RulePolicyDto dbPolicyCovert(List<policyDetailDto> pList) {
		RulePolicyDto rulePolicy = new RulePolicyDto();
		String val = "";
		for (policyDetailDto tmp : pList) {

			val = tmp.getFieldValue();

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.AUTHWINDOWSIZE))
				rulePolicy.setAuthWindowSize(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.AUTOUNLOCKTIME))
				rulePolicy.setAutoUnlockTime(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.DEVICECNT))
				rulePolicy.setDeviceCnt(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.ERRORTIMES))
				rulePolicy.setErrorTimes(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.GENTYPE))
				rulePolicy.setGentype(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.ISUNLOCK))
				rulePolicy.setIsUnlock(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.MOTPTYPE))
				rulePolicy.setMotpType(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.PASSWORDLENGTH))
				rulePolicy.setPasswordLength(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.PASSWORDTYPE))
				rulePolicy.setPasswordType(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.PREGENERATCOUNT))
				rulePolicy.setPregeneratCount(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.PREGMONITORCOUNT))
				rulePolicy.setPregMonitorCount(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.REQNUM))
				rulePolicy.setReqnum(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.REQSMSNUM))
				rulePolicy.setReqsmsnum(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.SMSTIMEOUT))
				rulePolicy.setSmsTimeout(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.TIMEOUT))
				rulePolicy.setTimeout(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.TOTALCSECNT))
				rulePolicy.setTotalCsecnt(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.TOTALERRCNT))
				rulePolicy.setTotalErrcnt(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.UPDATECYCLE))
				rulePolicy.setUpdateCycle(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.CHALLENGECODETIMEOUT))
				rulePolicy.setChallengeTimeout(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.PLUGININITSTATUS))
				rulePolicy.setPluginInitStatus(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.ACTIVATIONCOUNT))
				rulePolicy.setActivationCount(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.ISINTEGRITYCHECK))
				rulePolicy.setIsIntegrityCheck(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.PLUGINTYPE))
				rulePolicy.setPluginType(Integer.parseInt(val));

			if (tmp.getFieldName().equals(Constant.POLICY_NAME.ISALLOWROOT))
				rulePolicy.setIsAllowRoot(Integer.parseInt(val));
		}

		return rulePolicy;
	}

	@Override
	public ResultVO pluginActivePolicy(pluginInfoDto plInfo, RulePolicyDto activePolicyInfo) {
		ResultVO result = new ResultVO();
		String errMsg = "";

		int activenum = 0;
		if (null == activePolicyInfo.getActivationCount()) {
			log.error("redis reqPolicyInfo.getActivationCount null, get default value 0");
		} else {
			activenum = activePolicyInfo.getActivationCount();
		}

		// 获取该该插件激活次数
		Long alreadyActiveNum = plInfo.getActiveUseCnt().longValue();

		if (null != activePolicyInfo.getActivationCount() && null != alreadyActiveNum
		        && alreadyActiveNum >= activenum) {
			errMsg = "this user has already active max times.";
			result.setMsg(errMsg);
			result.setCode(ErrorConstant.PLUGIN_HAVE_ACTIVE_MAX_TIMES);
			return result;
		}
		return result;
	}

	@Override
	public int getAuthPolicy(String appId, String businessName, String grade) {
		Integer authPolicy = cacheService.getAuthPolicy(appId, businessName);
		if (authPolicy == null) {
			authPolicy = -1;
		}

		return authPolicy.intValue();
	}

	@Override
	public authPolicyDto getAuthPolicyDto(String appId, String businessName, String grade, int riskScore) {
		authPolicyDto authPolicy = cacheService.getAuthPolicyDto(appId, businessName, grade);
		authPolicy = getDynamicPolicy(authPolicy, grade, riskScore);
		return authPolicy;
	}

	public ResultVO checkDevInfo(String appId, String devInfo, String policyType, authFeatureDto authFeatureInfo) {
		ResultVO result = new ResultVO();
		result.setCode(ErrorConstant.RET_OK);
		String errMsg = "";

		Map<Object, Object> devMap = new HashMap<>();
		// 读后台配置的策略到devMap中
		try {
			devMap = this.cacheService.getCheckDevInfoPolicy(appId);
		} catch (BusinessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (devMap == null || devMap.size() == 0) {
			log.info("get checkDevInfoPolicy null");
			return result;
		}
		if (devMap.containsKey(policyType)) {
			JSONObject jsonObject = null;
			// base64 解码
			String devstr = "";
			try {
				devstr = Base64.decode(devInfo);
			} catch (Exception e) {
				errMsg = "Base64.decode error devInfo:" + devInfo;
				log.error(errMsg);
				result.setCode(ErrorConstant.PARA_DEVINFOFORMAT_ERR);
				result.setMsg(errMsg);
				return result;
			}

			log.debug("encode devInfo:" + devInfo);
			log.debug("decode devstr len:" + devstr.length() + ", str:" + devstr + ".");

			// 解析json
			try {
				jsonObject = ControllerHelper.parseJsonString(devstr);
			} catch (JSONException e) {
				errMsg = "parseJsonString error devInfo:" + devstr;
				log.error(errMsg);
				result.setCode(ErrorConstant.PARA_DEVINFOFORMAT_ERR);
				result.setMsg(errMsg);
				return result;
			}

			if (jsonObject == null) {
				errMsg = "parseJsonString error devInfo:" + devstr;
				log.error(errMsg);
				result.setCode(ErrorConstant.PARA_DEVINFOFORMAT_ERR);
				result.setMsg(errMsg);
				return result;
			}
			result = this.checkDevInfoByPolicy(devMap, jsonObject, authFeatureInfo);
		}
		return result;
	}
	
	
	public ResultVO checkDevInfoByPolicy(Map<Object, Object> devMap, JSONObject jsonObject,
			authFeatureDto authFeatureInfo) {
		ResultVO result = new ResultVO();
		result.setCode(ErrorConstant.RET_OK);
		StringBuffer errstr= new StringBuffer();
		String errMsg = "";
		
		int num = 0;
		if (devMap.containsKey("imei")) {
			if (ControllerHelper.getJsonElement(jsonObject, "imei").equals(authFeatureInfo.getimei())) {
				num++;
			}   else{
				errstr.append("imei:"+ControllerHelper.getJsonElement(jsonObject, "ip")+",");
			}
		}
		if (devMap.containsKey("product_type")) {
			if (ControllerHelper.getJsonElement(jsonObject, "product_type").equals(authFeatureInfo.getProductType())) {
				num++;
			}   else{
				errstr.append("product_type:"+ControllerHelper.getJsonElement(jsonObject, "ip")+",");
			}
		}
		
		String checkVect=ControllerHelper.sortJsonString(jsonObject);
		
		double passPer = Float.valueOf(devMap.get("matchingRate").toString());
		
		double checkPer=getSimilarDegree(authFeatureInfo.getDev_info(), checkVect);

		//关键key匹配，匹配率大于80%更新设备信息
		
		if(num>Constant.CHECKDEVINFO.LEASTMATCH&&checkPer>passPer&&checkPer<Constant.CHECKDEVINFO.LEASTMATCH){
			try {
				authFeatureMapper.updateDevInfoByPluginId(checkVect, authFeatureInfo.getPluginId());
			} catch (SQLException e) {
				errMsg = "update devInfo erroe";
				log.error(errMsg);
				result.setCode(ErrorConstant.DEV_CHECK_FAIL);
				result.setMsg(errMsg);
				return result;
			}
		}
		
		// 比较通过率
		if (num<=1&&checkPer <Constant.CHECKDEVINFO.MINMATCH) {
			
			errMsg = "check devInfo not pass:"+errstr.toString();
			log.error(errMsg);
			result.setCode(ErrorConstant.DEV_CHECK_FAIL);
			result.setMsg(errMsg);
			return result;
		}
//		LXX  即使 匹配通过， 也把校验不通过的项,  写到errMsg中入库。
		result.setMsg(errstr.toString());
		return result;
	}
	public double getSimilarDegree(String vect,String checkVech){
		 
        //创建向量空间模型，使用map实现，主键为词项，值为长度为2的数组，存放着对应词项在字符串中的出现次数 
         Map<String, int[]> vectorSpace = new HashMap<String, int[]>(); 
         int[] itemCountArray = null;//为了避免频繁产生局部变量，所以将itemCountArray声明在此 
          
         //以空格为分隔符，分解字符串 
         String strArray[] = vect.split("&"); 
         for(int i=0; i<strArray.length; ++i) 
         { 
             if(vectorSpace.containsKey(strArray[i])) 
                 ++(vectorSpace.get(strArray[i])[0]); 
             else 
             { 
                 itemCountArray = new int[2]; 
                 itemCountArray[0] = 1; 
                 itemCountArray[1] = 0; 
                 vectorSpace.put(strArray[i], itemCountArray); 
             } 
         } 
          
         strArray = checkVech.split("&"); 
         for(int i=0; i<strArray.length; ++i) 
         { 
             if(vectorSpace.containsKey(strArray[i])) 
                 ++(vectorSpace.get(strArray[i])[1]); 
             else 
             { 
                 itemCountArray = new int[2]; 
                 itemCountArray[0] = 0; 
                 itemCountArray[1] = 1; 
                 vectorSpace.put(strArray[i], itemCountArray); 
             } 
         } 
          
         //计算相似度 
         double vector1Modulo = 0.00;//向量1的模 
         double vector2Modulo = 0.00;//向量2的模 
         double vectorProduct = 0.00; //向量积 
         Iterator iter = vectorSpace.entrySet().iterator(); 
          
         while(iter.hasNext()) 
         { 
             Map.Entry entry = (Map.Entry)iter.next(); 
             itemCountArray = (int[])entry.getValue(); 
              
             vector1Modulo += itemCountArray[0]*itemCountArray[0]; 
             vector2Modulo += itemCountArray[1]*itemCountArray[1]; 
              
             vectorProduct += itemCountArray[0]*itemCountArray[1]; 
         } 
          
         vector1Modulo = Math.sqrt(vector1Modulo); 
         vector2Modulo = Math.sqrt(vector2Modulo); 
          
         //返回相似度  
        return (vectorProduct/(vector1Modulo*vector2Modulo)); 
	}
	
	
	
	@Override
	public boolean checkUpdate(String devStr, String appId) {
		boolean result = false;
		String[] devContent = devStr.split("@");
		String devPluginId = devContent[3];
		String devPluginStatus = devContent[4];
		if (Integer.toString(Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY).equals(devPluginStatus)) {
			pluginInfoDto plInfo = paraHandle.getPluginfo(devPluginId);
			if (plInfo.getNeedUpdate() == Constant.PLUGIN_UPDATE.NOT_NEED_UPDATE) {
				// 检测是否要更新
				result = this.checkUpdatePolicy(appId, plInfo);
				if (result) {
					UpdateUtil updateInfo = new UpdateUtil();
					updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.NEED_UPDATE,
					        Constant.PLUGIN_UPDATE.NEED_UPDATE);
					updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plInfo.getPluginId());

					Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
					this.pluginInfoMapper.updateHashMapByPluginId(updateMap);
				}
			}
			if (plInfo.getNeedUpdate() == Constant.PLUGIN_UPDATE.NEED_UPDATE) {
				// 返回待更新
				result = true;
			}
		}
		return result;
	}

	@Override
	public boolean checkUpdateWithSotpIdAndStatus(String sotpId, String sotpStatus, String appId) {
		boolean result = false;
		String devPluginId = sotpId;
		String devPluginStatus = sotpStatus;
		if (Integer.toString(Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY).equals(devPluginStatus)) {
			pluginInfoDto plInfo = paraHandle.getPluginfo(devPluginId);
			if (plInfo.getNeedUpdate() == Constant.PLUGIN_UPDATE.NOT_NEED_UPDATE) {
				// 检测是否要更新
				result = this.checkUpdatePolicy(appId, plInfo);
				if (result) {
					UpdateUtil updateInfo = new UpdateUtil();
					updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.NEED_UPDATE,
					        Constant.PLUGIN_UPDATE.NEED_UPDATE);
					updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plInfo.getPluginId());

					Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
					this.pluginInfoMapper.updateHashMapByPluginId(updateMap);
				}
			}
			if (plInfo.getNeedUpdate() == Constant.PLUGIN_UPDATE.NEED_UPDATE) {
				// 返回待更新
				result = true;
			}
		}
		return result;
	}

	@Override
	public boolean checkNeedUpdate(pluginInfoDto plInfo, String appId) {
		boolean result = false;
		if (plInfo.getStatus() == Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY) {
			if (plInfo.getNeedUpdate() == Constant.PLUGIN_UPDATE.NOT_NEED_UPDATE) {
				// 检测是否要更新
				result = this.checkUpdatePolicy(appId, plInfo);
				if (result) {
					UpdateUtil updateInfo = new UpdateUtil();
					updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.NEED_UPDATE,
					        Constant.PLUGIN_UPDATE.NEED_UPDATE);
					updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plInfo.getPluginId());

					Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
					this.pluginInfoMapper.updateHashMapByPluginId(updateMap);
				}
			}
			if (plInfo.getNeedUpdate() == Constant.PLUGIN_UPDATE.NEED_UPDATE) {
				// 返回待更新
				result = true;
			}
		}
		return result;
	}

	public boolean checkUpdatePolicy(String appId, pluginInfoDto plInfo) {
		int updatecyle = 0;
		int uptotalUseCnt = 0;
		int uptotalErrCnt = 0;
		int starttime = 0;

		boolean result = false;
		String errMsg = "";

		RulePolicyDto updatePolicyInfo = this.cacheService.getPolicyCommon(Constant.POLICY_TYPE.UPDATE, appId);
		if (updatePolicyInfo == null || plInfo == null) {
			errMsg = "system inner get updatePolicy null or plInfo null";
			log.error(errMsg);
			return result;
		}

		if (null == updatePolicyInfo.getUpdateCycle()) {
			log.error("redis updatePolicyInfo.getUpdateCycle() null, get default value 0");
		} else {
			updatecyle = updatePolicyInfo.getUpdateCycle();
		}

		if (null == updatePolicyInfo.getTotalCsecnt()) {
			log.error("redis updatePolicyInfo.getTotalCsecnt() null, get default value 0");
		} else {
			uptotalUseCnt = updatePolicyInfo.getTotalCsecnt();
		}

		if (null == updatePolicyInfo.getTotalErrcnt()) {
			log.error("redis updatePolicyInfo.getTotalErrcnt() null, get default value 0");
		} else {
			uptotalErrCnt = updatePolicyInfo.getTotalErrcnt();
		}

		starttime = Integer.parseInt(DateUtil.timeStamp(plInfo.getStartTime().getTime()));

		log.debug("get UpdatePolicy  update cycle:" + updatecyle + ", usenum:" + uptotalUseCnt + ", errnum:"
		        + uptotalErrCnt);
		log.debug("get plugin start time:" + starttime + ", now time:" + DateUtil.timeStamp(0) + ", usenum:"
		        + plInfo.getTotalUsecnt() + ", errnum:" + plInfo.getTotalErrcnt());

		// 比较更新周期
		int distime = Integer.parseInt(DateUtil.timeStamp(0)) - starttime;
		if (distime > updatecyle) {
			return true;
		}

		// 比较总验证次数
		if (plInfo.getTotalUsecnt() >= uptotalUseCnt) {
			return true;
		}

		// 比较总验证错误数
		if (plInfo.getTotalErrcnt() >= uptotalErrCnt) {
			return true;
		}

		return result;
	}

	public authPolicyDto getDynamicPolicy(authPolicyDto authPolicy, String grade, int riskScore) {
		authPolicyDto result = authPolicy;
		if (!StringUtils.isEmpty(grade)) {
			try {
				int gradeInt = Integer.parseInt(grade);
				result = getDynamicPolicyByGrade(result, gradeInt);
			} catch (Exception e) {
				log.error("grade format error." + e.toString());
			}
		}

        // 若匹配了白名单规则,风控得分为-1000,则将所有认证因素清空
        if (riskScore < 0)
            result.setAuthFactorNum(0);

		// 根据风控得分获取策略并更新策略值
		if (riskScore > 0) {
			String riskDynamicPolicy = analysisService.getDynamicPolicyByRiskScore(riskScore);
			if (!StringUtils.isEmpty(riskDynamicPolicy)) {
				int policyNum = analysisService.addSinglePolicy(result.getAuthFactorNum(), riskDynamicPolicy);
				result.setAuthFactorNum(policyNum);
			}
		}
		return result;
	}

	public authPolicyDto getDynamicPolicyByGrade(authPolicyDto authPolicy, int grade) {
		int dynamicPolicy = authPolicy.getDynamicPolicy();
		int policyNum = authPolicy.getAuthFactorNum();
		if (dynamicPolicy == Constant.USEDYNAMICPOLICY.USEDYNAMICPOLICY) {
			String policyContent = analysisService.getPolicyContent(policyNum);
			String[] content = policyContent.split("\\|");
			float n = content.length;
			float m = (float)(Constant.APPGRADE.FULL - grade) / Constant.APPGRADE.FULL * n;
			if (m >= n) {
				m = n - 1;
			}
			StringBuilder policySB = new StringBuilder();
			for (int i = 0; i <= m; i++) {
				policySB.append(content[i]);
				policySB.append("|");
			}
			policyContent = policySB.substring(0, policySB.length() - 1).toString();
			authPolicy.setAuthFactorNum(analysisService.getPolicyNumber(policyContent));
		}
		return authPolicy;
	}

	@Override
	public int getIsIntegrityCheckForApply(RulePolicyDto applyPolicyInfo) {
		int isIntegrityCheck = 1;
		if (applyPolicyInfo.getIsIntegrityCheck() != null && applyPolicyInfo.getIsIntegrityCheck() == 0) {
			isIntegrityCheck = applyPolicyInfo.getIsIntegrityCheck();
			log.debug("redis PolicyServiceImpl getIsIntegrityCheckForApply :" + isIntegrityCheck);
		}

		return isIntegrityCheck;
	}
}
