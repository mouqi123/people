package com.peopleNet.sotp.service;

import net.sf.json.JSONArray;

/**
 * 
 * @描述 获取展示数据接口
 * @author wangxin
 * @created_at 2015年11月11日
 */
public interface IExhibitionService {

	public void setCenterTradeAmount();

	public void setBusinessAmount();

	public void setCenterVerifyUserAmount();

	public void setProvinceUserAmount();

	public void setRealtimeAuthInfo();

	public JSONArray getCenterTradeAmount();

	public JSONArray getBusinessAmount();

	public JSONArray getCenterVerifyUserAmount();

	public JSONArray getProvinceUserAmount();

	public JSONArray getRTCenterBusinessAmount();

	public JSONArray getRealtimeAuthInfo(String centerCity);

	public JSONArray getTradeLog();

	public JSONArray getApiVisitorInfo();
}
