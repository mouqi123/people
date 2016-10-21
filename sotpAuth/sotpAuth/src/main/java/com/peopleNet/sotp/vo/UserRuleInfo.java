package com.peopleNet.sotp.vo;

import java.util.*;

/**
 * Created by wangxin on 16/8/2.
 */
public class UserRuleInfo {

	private Map<String, Double> bizMap = null;
	private Map<String, Double> phoneMap = null;
	private Map<String, Double> priceMap = null;
	private Map<String, Double> timeSegMap = null;
	private String userId;

	public UserRuleInfo(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Map<String, Double> getBizMap() {
		return bizMap;
	}

	public void setBizMap(Map<String, Double> bizMap) {
		this.bizMap = resortMap(bizMap);
	}

	public Map<String, Double> getPhoneMap() {
		return phoneMap;
	}

	public void setPhoneMap(Map<String, Double> phoneMap) {
		this.phoneMap = resortMap(phoneMap);
	}

	public Map<String, Double> getPriceMap() {
		return priceMap;
	}

	public void setPriceMap(Map<String, Double> priceMap) {
		this.priceMap = resortMap(priceMap);
	}

	public Map<String, Double> getTimeSegMap() {
		return timeSegMap;
	}

	public void setTimeSegMap(Map<String, Double> timeSegMap) {
		this.timeSegMap = resortMap(timeSegMap);
	}

	private Map<String, Double> resortMap(Map<String, Double> srcMap) {
		if (null == srcMap || srcMap.isEmpty())
			return null;

		List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(srcMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			// 降序排序
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}

		});

		/*
		 * 把排序好的list依序放入LinkedHashMap中, LinkedHashMap可以按插入順序存放元素,
		 * 因此可以得到一個依value排序的map
		 */
		Map<String, Double> linkedHashMap = new LinkedHashMap<>();
		for (Map.Entry<String, Double> mapping : list)
			linkedHashMap.put(mapping.getKey(), mapping.getValue());
		return linkedHashMap;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("userRuleInfo detail:");
		sb.append("{userId:" + userId + "}");
		if (null != bizMap)
			sb.append(bizMap.toString());
		if (null != phoneMap)
			sb.append(phoneMap.toString());
		if (null != priceMap)
			sb.append(priceMap.toString());
		if (null != timeSegMap)
			sb.append(timeSegMap.toString());
		return sb.toString();
	}
}
