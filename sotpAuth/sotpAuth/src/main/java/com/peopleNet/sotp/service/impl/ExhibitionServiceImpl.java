package com.peopleNet.sotp.service.impl;

import java.util.*;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.filter.RequestContextFilter;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.ICacheService;
import com.peopleNet.sotp.service.IExhibitionService;
import com.peopleNet.sotp.vo.ExhibitionLog;
import com.peopleNet.sotp.vo.InterfaceVisitStatistic;

@Service
public class ExhibitionServiceImpl implements IExhibitionService {

	private static LogUtil logger = LogUtil.getLogger(ExhibitionServiceImpl.class);
	@Autowired
	private ICacheService cacheService;

	@Override
	public JSONArray getCenterTradeAmount() {
		JSONArray list = new JSONArray();

		Long bjTrade = this.cacheService.getStatisticNum(Constant.EXHIBITION_NAME.BEIJING_TRADE);
		Long shTrade = this.cacheService.getStatisticNum(Constant.EXHIBITION_NAME.SHANGHAI_TRADE);
		Long gzTrade = this.cacheService.getStatisticNum(Constant.EXHIBITION_NAME.GUANGZHOU_TRADE);

		if (null != bjTrade) {
			JSONObject result = new JSONObject();
			result.put("name", "北京");
			result.put("value", bjTrade);
			list.add(result);
		}
		if (null != shTrade) {
			JSONObject result = new JSONObject();
			result.put("name", "上海");
			result.put("value", shTrade);
			list.add(result);
		}
		if (null != gzTrade) {
			JSONObject result = new JSONObject();
			result.put("name", "广州");
			result.put("value", gzTrade);
			list.add(result);
		}
		return list;
	}

	@Override
	public JSONArray getBusinessAmount() {
		JSONArray list = new JSONArray();

		Long ePay = this.cacheService.getStatisticNum(Constant.EXHIBITION_NAME.E_PAY);
		Long onePay = this.cacheService.getStatisticNum(Constant.EXHIBITION_NAME.ONE_PAY);
		Long quickPay = this.cacheService.getStatisticNum(Constant.EXHIBITION_NAME.QUICK_PAY);
		Long hce = this.cacheService.getStatisticNum(Constant.EXHIBITION_NAME.HCE);
		Long epos = this.cacheService.getStatisticNum(Constant.EXHIBITION_NAME.EPOS);

		if (null != ePay) {
			JSONObject result = new JSONObject();
			result.put("name", "e支付");
			result.put("value", ePay);
			list.add(result);
		}
		if (null != onePay) {
			JSONObject result = new JSONObject();
			result.put("name", "一键支付");
			result.put("value", onePay);
			list.add(result);
		}
		if (null != quickPay) {
			JSONObject result = new JSONObject();
			result.put("name", "快捷支付");
			result.put("value", quickPay);
			list.add(result);
		}
		if (null != hce) {
			JSONObject result = new JSONObject();
			result.put("name", "HCE");
			result.put("value", hce);
			list.add(result);
		}
		if (null != epos) {
			JSONObject result = new JSONObject();
			result.put("name", "ePos");
			result.put("value", epos);
			list.add(result);
		}
		return list;
	}

	@Override
	public JSONArray getCenterVerifyUserAmount() {
		JSONArray list = new JSONArray();

		Long bjUser = this.cacheService.getStatisticNum(Constant.EXHIBITION_NAME.BEIJING_USER);
		Long shUser = this.cacheService.getStatisticNum(Constant.EXHIBITION_NAME.SHANGHAI_USER);
		Long gzUser = this.cacheService.getStatisticNum(Constant.EXHIBITION_NAME.GUANGZHOU_USER);

		if (null != bjUser) {
			JSONObject result = new JSONObject();
			result.put("name", "北京");
			result.put("value", bjUser);
			list.add(result);
		}
		if (null != shUser) {
			JSONObject result = new JSONObject();
			result.put("name", "上海");
			result.put("value", shUser);
			list.add(result);
		}
		if (null != gzUser) {
			JSONObject result = new JSONObject();
			result.put("name", "广州");
			result.put("value", gzUser);
			list.add(result);
		}
		return list;
	}

	@Override
	public JSONArray getProvinceUserAmount() {
		JSONArray list = new JSONArray();

		Map<Object, Object> res = this.cacheService.getAmountMap(Constant.EXHIBITION_NAME.PROVINCE_USER_AMOUNT);

		HashMap<String, Long> hashMap = new HashMap<String, Long>();
		for (Entry<Object, Object> entry : res.entrySet()) {
			hashMap.put(entry.getKey().toString(), Long.valueOf(entry.getValue().toString()));
		}

		List<Map.Entry<String, Long>> entryList = new ArrayList<Map.Entry<String, Long>>(hashMap.entrySet());

		Collections.sort(entryList, new Comparator<Map.Entry<String, Long>>() {
			public int compare(Map.Entry<String, Long> mapping1, Map.Entry<String, Long> mapping2) {
				return mapping2.getValue().compareTo(mapping1.getValue());
			}
		});

		Iterator<Map.Entry<String, Long>> iter = entryList.iterator();
		Map.Entry<String, Long> tmpEntry = null;
		while (iter.hasNext()) {
			tmpEntry = iter.next();
			JSONObject result = new JSONObject();
			result.put("name", tmpEntry.getKey());
			result.put("value", tmpEntry.getValue());
			list.add(result);
		}
		return list;
	}

	@Override
	public JSONArray getRTCenterBusinessAmount() {
		JSONArray list = new JSONArray();

		Map<Object, Object> res = this.cacheService.getAmountMap(Constant.EXHIBITION_NAME.RT_CENTER_BUSINESS_AMOUNT);

		for (Entry<Object, Object> entry : res.entrySet()) {
			JSONObject result = new JSONObject();
			result.put("name", entry.getKey().toString());
			result.put("value", Long.valueOf(entry.getValue().toString()));
			list.add(result);
		}
		return list;
	}

	@Override
	public JSONArray getApiVisitorInfo() {
		JSONArray list = new JSONArray();

		Map<String, InterfaceVisitStatistic> res = RequestContextFilter.interfaceVisitNum;

		// Map<String, InterfaceVisitStatistic> res = this.cacheService
		// .getVisitorAmountMap(Constant.SYSTEM_INFO.API_VISITOR);

		for (Entry<String, InterfaceVisitStatistic> entry : res.entrySet()) {
			JSONObject result = new JSONObject();
			result.put("name", entry.getKey());
			result.put("value", entry.getValue().toString());
			list.add(result);
		}
		return list;
	}

	@Override
	public JSONArray getRealtimeAuthInfo(String centerCity) {
		JSONArray list = new JSONArray();

		if ("beijing".equals(centerCity)) {
			Map<Object, Object> res = this.cacheService.getRealTimeMap(Constant.EXHIBITION_NAME.BEIJING_REALTIME);
			for (Entry<Object, Object> entry : res.entrySet()) {
				JSONArray inlist = new JSONArray();
				JSONObject result1 = new JSONObject();
				result1.put("name", entry.getKey().toString());
				JSONObject result2 = new JSONObject();
				result2.put("name", "北京");
				result2.put("value", Long.valueOf(entry.getValue().toString()));
				inlist.add(result1);
				inlist.add(result2);
				list.add(inlist);
			}
		}

		if ("shanghai".equals(centerCity)) {
			Map<Object, Object> res = this.cacheService.getRealTimeMap(Constant.EXHIBITION_NAME.SHANGHAI_REALTIME);
			for (Entry<Object, Object> entry : res.entrySet()) {
				JSONArray inlist = new JSONArray();
				JSONObject result1 = new JSONObject();
				result1.put("name", entry.getKey().toString());
				JSONObject result2 = new JSONObject();
				result2.put("name", "上海");
				result2.put("value", Long.valueOf(entry.getValue().toString()));
				inlist.add(result1);
				inlist.add(result2);
				list.add(inlist);
			}
		}

		if ("guangzhou".equals(centerCity)) {
			Map<Object, Object> res = this.cacheService.getRealTimeMap(Constant.EXHIBITION_NAME.GUANGZHOU_REALTIME);
			for (Entry<Object, Object> entry : res.entrySet()) {
				JSONArray inlist = new JSONArray();
				JSONObject result1 = new JSONObject();
				result1.put("name", entry.getKey().toString());
				JSONObject result2 = new JSONObject();
				result2.put("name", "广州");
				result2.put("value", Long.valueOf(entry.getValue().toString()));
				inlist.add(result1);
				inlist.add(result2);
				list.add(inlist);
			}
		}
		return list;
	}

	@Override
	public JSONArray getTradeLog() {
		JSONArray list = new JSONArray();
		List<ExhibitionLog> tradeLogList = this.cacheService.getRealTimeLog(Constant.EXHIBITION_NAME.TRADE_LOG);
		if (null != tradeLogList && tradeLogList.size() > 0) {
			for (ExhibitionLog tradeLog : tradeLogList) {
				JSONObject obj = new JSONObject();
				obj = JSONObject.fromObject(tradeLog);
				list.add(obj);
			}
		}
		return list;
	}

	@Override
	public void setCenterTradeAmount() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBusinessAmount() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCenterVerifyUserAmount() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProvinceUserAmount() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRealtimeAuthInfo() {
		// TODO Auto-generated method stub

	}

}
