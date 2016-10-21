package com.peopleNet.sotp.service.impl;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.drools.RuleEngine;
import com.peopleNet.sotp.exception.BusinessException;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.ICacheService;
import com.peopleNet.sotp.service.IRuleService;
import com.peopleNet.sotp.util.StringUtils;
import com.peopleNet.sotp.vo.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wangxin on 16/7/21.
 */
@Service
public class RuleServiceImpl implements IRuleService {
	private static LogUtil logger = LogUtil.getLogger(RuleServiceImpl.class);

	@Autowired
	private ICacheService cacheService;

	@Override
	public RiskControlResult verifyRule(UserRequestMsgFido requestMsg) {
        // 从requestMsg中得到线上风控模块的输入类
        UserAuthMsg authMsg = requestMsg.convertToAuthMsg();

        // 获取历史访问记录
        this.getHistoryVisitRecord(authMsg);

        // 获取用户相关的可信库数据
        UserTrustLibInfo userTrustLibInfo = generateUserTrustLibInfo(authMsg.getPhoneNum());
        logger.debug(userTrustLibInfo.toString());

        // 获取用户的个性化分析结果
		UserRuleInfo userRule = generateUserRule(authMsg.getPhoneNum());
		logger.debug(userRule.toString());

        RiskControlResult riskResult = new RiskControlResult(authMsg.getAuthEventID());
		RuleEngine.execute(authMsg, riskResult, userRule, userTrustLibInfo);
		logger.info("risk result:" + riskResult.toString());

		// 若verifyRuleResult字段>=1000,表示风险极大,退出认证流程,返回认证失败
		if (1000 <= riskResult.getRiskScore()) {
			logger.error("ruleEngine effects. " + riskResult.getMatchRiskRules().toString());
			return riskResult;
		}

        // 更新历史访问记录
	    this.updateHistoryVisitRecord(authMsg);
        return riskResult;
	}

    // 获取redis中保存的历史认证记录
    // 分为多个维度:每天认证数\过去15分钟\过去30分钟\过去60分钟\过去120分钟
    private void getHistoryVisitRecord(UserAuthMsg authMsg) {
        String userPhone = authMsg.getPhoneNum();
        String appId = authMsg.getAppID();
        // 获取该用户当天已认证次数
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new java.util.Date(System.currentTimeMillis()));
        String verifyRedisKey = today + "_vefiry_num";
        Long alreadyRequestNum = this.cacheService.getUserReqeustPluginNum(verifyRedisKey, authMsg.getPhoneNum(),
                authMsg.getAppID());
        if (null != alreadyRequestNum) {
            authMsg.setVerifyNumToday(alreadyRequestNum.intValue());
        }
        logger.debug("--" + authMsg.getVerifyNumToday());

        try {
            Long last1minsVisit = this.cacheService.fetchUserVisitorNumber(Constant.REDIS_CONSTANT.LAST1MIN, userPhone, appId, Constant.MILLISECONDS_1_MINITES);
            Long last3minsVisit = this.cacheService.fetchUserVisitorNumber(Constant.REDIS_CONSTANT.LAST3MIN, userPhone, appId, Constant.MILLISECONDS_3_MINITES);
            Long last15minsVisit = this.cacheService.fetchUserVisitorNumber(Constant.REDIS_CONSTANT.LAST15MIN, userPhone, appId, Constant.MILLISECONDS_15_MINITES);
            Long last30minsVisit = this.cacheService.fetchUserVisitorNumber(Constant.REDIS_CONSTANT.LAST30MIN, userPhone, appId, Constant.MILLISECONDS_30_MINITES);
            Long last60minsVisit = this.cacheService.fetchUserVisitorNumber(Constant.REDIS_CONSTANT.LAST60MIN, userPhone, appId, Constant.MILLISECONDS_60_MINITES);
            Long last120minsVisit = this.cacheService.fetchUserVisitorNumber(Constant.REDIS_CONSTANT.LAST120MIN, userPhone, appId, Constant.MILLISECONDS_120_MINITES);
            logger.debug("last1MinutesVisit: %d, last3MinutesVisit: %d, last15MinutesVisit: %d, last30MinutesVisit: %d, last60MinutesVisit: %d, last120MinitesVisit:%d", last1minsVisit, last3minsVisit, last15minsVisit, last30minsVisit, last60minsVisit, last120minsVisit);

            authMsg.setVisitNumLast1mins(last1minsVisit.intValue());
            authMsg.setVisitNumLast3mins(last3minsVisit.intValue());
            authMsg.setVisitNumLast15mins(last15minsVisit.intValue());
            authMsg.setVisitNumLast30mins(last30minsVisit.intValue());
            authMsg.setVisitNumLast60mins(last60minsVisit.intValue());
            authMsg.setVisitNumLast120mins(last120minsVisit.intValue());
        } catch (BusinessException e) {
            logger.debug("userPhone:%s, appId:%s, get history visit error!", userPhone, appId);
        }
    }

    // 更新用户历史记录
    private void updateHistoryVisitRecord(UserAuthMsg authMsg){
        String userPhone = authMsg.getPhoneNum();
        String appId = authMsg.getAppID();
        // 获取该用户当天已认证次数
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new java.util.Date(System.currentTimeMillis()));
        String verifyRedisKey = today + "_vefiry_num";

        try {
            // cacheService 内存中记录该用户当天认证次数
            this.cacheService.incUserReqeustPluginNum(verifyRedisKey, userPhone, appId);
            this.cacheService.setUserVisitorNumber(Constant.REDIS_CONSTANT.LAST1MIN, userPhone, appId);
            this.cacheService.setUserVisitorNumber(Constant.REDIS_CONSTANT.LAST3MIN, userPhone, appId);
            this.cacheService.setUserVisitorNumber(Constant.REDIS_CONSTANT.LAST15MIN, userPhone, appId);
            this.cacheService.setUserVisitorNumber(Constant.REDIS_CONSTANT.LAST30MIN, userPhone, appId);
            this.cacheService.setUserVisitorNumber(Constant.REDIS_CONSTANT.LAST60MIN, userPhone, appId);
            this.cacheService.setUserVisitorNumber(Constant.REDIS_CONSTANT.LAST120MIN, userPhone, appId);
        } catch (Exception e) {
            logger.error("insert into redis error. " + e.toString());
        }
    }

	// 获取redis中保存的用户可信库数据
	private UserTrustLibInfo generateUserTrustLibInfo(String userId) {
		UserTrustLibInfo trustLibInfo = new UserTrustLibInfo(userId);

		String userTrustLib = cacheService.getRule("13041276768");
		if (StringUtils.isEmpty(userTrustLib)) {
			return trustLibInfo;
		}

        /*
         格式如下:
         {"deviceIdList":["anil"],"plugIdList":["M00004A008V3.1ID01b57d6c89214acf"],"recAccList":[],"recCardList":[],"userPhoneList":["13041276768"]}
         */
		JSONObject result = JSONObject.fromObject(userTrustLib);
		Iterator it = result.keys();
		// 遍历jsonObject数据，添加到List对象
		while (it.hasNext()) {
			String key = String.valueOf(it.next());

			if ("empty".equals(key)) {
				continue;
			}
            JSONArray arrayJson = JSONArray.fromObject(result.get(key));
            if ("deviceIdList".equals(key)) {
                trustLibInfo.setDeviceIdList(JSONArray.toList(arrayJson, String.class, new JsonConfig()));
            } else if ("plugIdList".equals(key)) {
                trustLibInfo.setPluginIdList(JSONArray.toList(arrayJson, String.class, new JsonConfig()));
            } else if ("recAccList".equals(key)) {
                trustLibInfo.setRecAccList(JSONArray.toList(arrayJson, String.class, new JsonConfig()));
            } else if ("recCardList".equals(key)) {
                trustLibInfo.setRecCardList(JSONArray.toList(arrayJson, String.class, new JsonConfig()));
            } else if ("userPhoneList".equals(key)) {
                trustLibInfo.setUserPhoneList(JSONArray.toList(arrayJson, String.class, new JsonConfig()));
            }
		}
		return trustLibInfo;
	}

    // 获取redis中保存的用户个人数据分析结果
    private UserRuleInfo generateUserRule(String userId) {
        UserRuleInfo ruleInfo = new UserRuleInfo(userId);

        String userRule = cacheService.getRule("hadoop_2score");
        if (StringUtils.isEmpty(userRule)) {
            return ruleInfo;
        }

        /*
         格式如下:
         "{\"touserMap\": {\"abc\": 0.809}, \"priceMap\": {\"pay\": {\"p2\": 0.809}}, \"freqMap\": {\"pay\": {\"f2\": 0.542}}, \"timeSegmentsMap\": {\"t2\": 0.809}, \"phoneMap\": {\"18514767333\": 0.809}}"
         */
        JSONObject result = JSONObject.fromObject(userRule);
        Iterator it = result.keys();
        // 遍历jsonObject数据，添加到Map对象
        while (it.hasNext()) {
            String key = String.valueOf(it.next());

            if ("empty".equals(key)) {
                continue;
            }
            JSONObject mapJson = JSONObject.fromObject(result.get(key));
            Iterator itMap = mapJson.keys();
            Map<String, Double> smap = new HashMap<String, Double>();
            while (itMap.hasNext()) {
                String sKey = String.valueOf(itMap.next());
                Double svalue = Double.valueOf(mapJson.get(sKey).toString());
                smap.put(sKey, svalue);
            }
            if ("bizMap".equals(key)) {
                ruleInfo.setBizMap(smap);
            } else if ("phoneMap".equals(key)) {
                ruleInfo.setPhoneMap(smap);
            } else if ("priceMap".equals(key)) {
                ruleInfo.setPriceMap(smap);
            } else if ("timeSegmentsMap".equals(key)) {
                ruleInfo.setTimeSegMap(smap);
            }
        }
        return ruleInfo;
    }
}
