package com.peopleNet.sotp.vo;

import java.util.*;

public class RiskControlResult {
    // 认证事件ID
    private String authEventID;

    // 风控得分
    private int riskScore = 0;

    // 匹配到的所有规则所属大类及对应的规则编号
    private Map<String,List<String>> matchRiskRules = null;

    public RiskControlResult(String authEventID) {
        this.authEventID = authEventID;
    }

    public String getAuthEventID() {
        return authEventID;
    }

    public void setAuthEventID(String authEventID) {
        this.authEventID = authEventID;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public Map<String, List<String>> getMatchRiskRules() {
        return matchRiskRules;
    }

    public void setMatchRiskRules(Map<String, List<String>> matchRiskRules) {
        this.matchRiskRules = matchRiskRules;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("riskResult detail:");
        sb.append("{authEventID:" + authEventID + "}");
        sb.append("{riskScore:" + riskScore + "}");
        if (null != matchRiskRules) {
            sb.append(matchRiskRules.toString());
        }
        return sb.toString();
    }

    public void addMatchedRule(String category, String ruleItemNum) {
        boolean existed = false;
        if (null == matchRiskRules) {
            matchRiskRules = new HashMap<>();
        } else {
            Iterator<Map.Entry<String, List<String>>> entries = matchRiskRules.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, List<String>> entry = entries.next();
                String keyName = entry.getKey();
                if (category.equals(keyName)) {
                    existed = true;
                    entry.getValue().add(ruleItemNum);
                }
            }
        }
        if (false == existed) {
            List<String> ruleItemList = new ArrayList<>();
            ruleItemList.add(ruleItemNum);
            matchRiskRules.put(category, ruleItemList);
        }
    }
}
