package com.peopleNet.sotp.service.impl;

import com.peopleNet.sotp.service.IAnalysisService;
import com.peopleNet.sotp.util.CommonConfig;
import com.peopleNet.sotp.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class AnalysisServiceImpl implements IAnalysisService {

    /*
     * private static String[] policyContent = { "devInfo", "appHash",
     * "appSign", "pluginSign", "location", "ip", "wifiInfo", "userInfo",
     * "loginPwd", "payPwd", "fingerprint", "gesture", "voiceprint",
     * "faceprint", "eyeprint", "pinCode", "otpCode", "timeAuthCode",
     * "challengeAuthCode", "pinAuthCode", "pinAndChallengeAuthCode" };
     */
    private static String[] policyContent = {"userInfo", "loginPwd", "payPwd", "fingerprint", "gesture", "voiceprint",
            "faceprint", "eyeprint", "pinCode", "otpCode", "timeAuthCode", "challengeAuthCode", "pinAuthCode",
            "cCAuthCode", "pinAndCCAuthCode", "pinAndSCAuthCode", "sCAndCCAuthCode"};


    // 风险非常高场景的策略集合
    private static String[] policyLevel1 = {"payPwd"};

    // 风险较高场景的策略集合
    private static String[] policyLevel2 = {"fingerprint", "gesture", "voiceprint",
            "faceprint", "eyeprint", "pinCode", "otpCode", "pinAuthCode",
            "pinAndCCAuthCode", "pinAndSCAuthCode"};

    // 风险较低场景的策略集合
    private static String[] policyLevel3 = {"userInfo", "timeAuthCode", "challengeAuthCode",
            "cCAuthCode", "sCAndCCAuthCode"};


    private int FLOOROFLOWRISK = Integer.parseInt(CommonConfig.get("floorOfLowRisk").trim());
    private int FLOOROFMEDIANRISK = Integer.parseInt(CommonConfig.get("floorOfMediaRisk").trim());
    private int FLOOROFHIGHRISK = Integer.parseInt(CommonConfig.get("floorOfHighRisk").trim());
    private int CEILOFHIGHRISK = Integer.parseInt(CommonConfig.get("ceilOfHighRisk").trim());

    /*
     * getPolicyContent input: policy (int) output: 1. String, authentication
	 * factors separated by '|'
	 * eg:appHash|location|ip|payPwd|gesture|voiceprint|faceprint|timeAuthCode
	 * 2. null,if policy is invalid.
	 */
    @Override
    public String getPolicyContent(int policy) {

        // judge policy,if bit count gt 20, return null.
        if (policy > 0xfffffff) {
            return null;
        }
        int iPolicy = policy & 0xffffffff;
        List<String> policyContentList = new ArrayList<>();
        int cursor = 1;
        int compareCount = 1;
        while (compareCount <= 17) {
            if ((iPolicy & cursor) != 0) {
                policyContentList.add(policyContent[compareCount - 1]);
            }
            cursor <<= 1;
            compareCount += 1;

        }
        if (policyContentList.isEmpty())
            return "";

        StringBuilder response = new StringBuilder();
        for (String p : policyContentList) {
            response.append(p);
            response.append("|");
        }

        return response.substring(0, response.length() - 1).toString();
    }

    // 根据风险得分获取动态认证策略
    public String getDynamicPolicyByRiskScore(int riskScore) {
        Random random = new Random();
        if (riskScore < FLOOROFLOWRISK) {
            // 风控得分在(0,50)之间,风险极低,不额外添加策略
            return "";
        } else if (riskScore >= FLOOROFLOWRISK && riskScore < FLOOROFMEDIANRISK) {
            // 风控得分在[50,200)之间,风险偏低
            int rand = random.nextInt(policyLevel3.length);
            return policyLevel3[rand];
        } else if (riskScore >= FLOOROFMEDIANRISK && riskScore < FLOOROFHIGHRISK) {
            // 风控得分在[200,500)之间,风险高
            int rand = random.nextInt(policyLevel2.length);
            return policyLevel2[rand];
        } else if (riskScore >= FLOOROFHIGHRISK && riskScore < CEILOFHIGHRISK) {
            // 风控得分在[500,1000)之间,风险极高
            int rand = random.nextInt(policyLevel1.length);
            return policyLevel1[rand];
        } else {
            return "";
        }
    }

    /*
     * getPolicyNumber input: policy (String) output: 1. int, a number
     * representative of authentication factors 2. 0,if policy is null.
     */
    public int getPolicyNumber(String policy) {
        int policyNum = 0;
        if (policy == null || policy.isEmpty()) {
            return policyNum;
        }
        ;
        String[] content = policy.split("\\|");

        //去重
        boolean hasProcessed[] = new boolean[policyContent.length];
        for (int i = 0; i < hasProcessed.length; i++) {
            hasProcessed[i] = false;
        }

        for (int i = 0; i < content.length; i++) {
            for (int j = 0; j < policyContent.length; j++) {
                if (content[i].equals(policyContent[j])) {
                    if (!hasProcessed[j]) {
                        policyNum += Math.pow(2, j);
                        hasProcessed[j] = true;
                    }
                    break;
                }
            }
        }
        return policyNum;
    }

    // 增加单一策略,返回更新的策略值
    public int addSinglePolicy(int orgPolicyNum, String policy) {
        if (StringUtils.isEmpty(policy)) {
            return orgPolicyNum;
        }

        for (int j = 0; j < policyContent.length; j++) {
            if (policy.equals(policyContent[j])) {
                orgPolicyNum |= 1 << j;
                break;
            }
        }
        return orgPolicyNum;
    }
 
}
