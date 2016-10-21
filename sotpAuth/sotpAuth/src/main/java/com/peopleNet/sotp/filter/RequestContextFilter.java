package com.peopleNet.sotp.filter;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.springframework.util.StringUtils;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.context.UserContext;
import com.peopleNet.sotp.context.UserThreadContext;
import com.peopleNet.sotp.controller.ControllerHelper;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.util.Base64;
import com.peopleNet.sotp.util.CommonConfig;
import com.peopleNet.sotp.util.HttpRequestUtils;
import com.peopleNet.sotp.util.ObtainLocationUtil;
import com.peopleNet.sotp.vo.InterfaceVisitStatistic;

@SuppressWarnings("serial")
public class RequestContextFilter extends HttpServlet implements Filter {
	public static Date systemStartTime;
	// 统计各接口访问信息
	public static Map<String, InterfaceVisitStatistic> interfaceVisitNum = new HashMap<String, InterfaceVisitStatistic>();
	private static LogUtil logger = LogUtil.getLogger(RequestContextFilter.class);

	// 各接口访问耗时统计
	// public static Map<String, Long> interfaceVisitTime = new HashMap<String,
	// Long>();

	public void init(FilterConfig filterConfig) throws ServletException {
		systemStartTime = new Date();
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
	        throws IOException, ServletException {
		logger.debug("execute requestContext filter-----start!!");
		HttpServletRequest request = (HttpServletRequest) req;
		/*
		 * // 获取request的ip地址信息 String clientIp =
		 * request.getHeader("x-forwarded-for"); if (clientIp == null ||
		 * clientIp.equalsIgnoreCase("unknown")) { clientIp =
		 * request.getRemoteAddr(); }
		 */

		// 获取请求参数，并保存到context中
		// String requestMsg = request.getQueryString();
		Map<String, String> paramMap = getReqParamMap(request);

		String serviceName = "";
		String interfaceName = "";
		UserContext context = new UserContext();
		if (null != paramMap) {
			String phonenum = paramMap.get(Constant.PARA_NAME.PHONENUM);
			String uid = paramMap.get(Constant.PARA_NAME.USERID);
			if(StringUtils.isEmpty(uid)){
				uid = phonenum;
			}
			String sotpId = paramMap.get(Constant.PARA_NAME.SOTPID);
			String type = paramMap.get(Constant.PARA_NAME.TYPE);

			serviceName = paramMap.get(Constant.PARA_NAME.SERVICENAME);
			String version = paramMap.get(Constant.PARA_NAME.VERSION);
			String appId = paramMap.get(Constant.PARA_NAME.APPID);
			String location = paramMap.get(Constant.PARA_NAME.LOCATION);
			String wifi = paramMap.get(Constant.PARA_NAME.WIFI);
			String payee = paramMap.get(Constant.PARA_NAME.PAYEE);
			String price = paramMap.get(Constant.PARA_NAME.PRICE);
			String action = paramMap.get(Constant.PARA_NAME.PAYACTION);
			String ip = paramMap.get(Constant.PARA_NAME.IP);
			String devId = paramMap.get(Constant.PARA_NAME.DEV_ID);
			String cardType = paramMap.get(Constant.PARA_NAME.CARDTYPE);
			String recCard = paramMap.get(Constant.PARA_NAME.RECCARD);
			String payCard = paramMap.get(Constant.PARA_NAME.PAYCARD);
			String cityId ="nil";
			if (!StringUtils.isEmpty(serviceName)) {
				interfaceName = serviceName;
			} else {
				if (!StringUtils.isEmpty(type)) {
					interfaceName = request.getServletPath() + "_" + type;
				}
			}
			
//		     String url = CommonConfig.get("PYTHON_URL").trim();
//			if(!"nil".equals(location) && !StringUtils.isEmpty(location) && location.contains("Latitude") && location.contains("Longtitude")){
//			    String[] aa =location.replaceAll(":", ",").split(",");
//			    String latitude = aa[1];
//			    String longidue = aa[3];
//			    longidue = longidue.substring(0, longidue.length()-1);
//			    	url = url+"?latnum="+latitude+"&lonnum="+longidue;
//			    	cityId = HttpRequestUtils.httpGet(url);
//			}
			
			context.setValue(uid, ip, phonenum, sotpId, interfaceName, appId, version, location,cityId, wifi, payee, price, action,devId,cardType,recCard,payCard);
		}
		UserThreadContext.setContext(context);

		InterfaceVisitStatistic visitInterface = null;

		// 只统计含有type字段或者service字段的接口
		if (!StringUtils.isEmpty(interfaceName)) {
			if (interfaceVisitNum.containsKey(interfaceName)) {
				synchronized (visitInterface = interfaceVisitNum.get(interfaceName)) {
					visitInterface.setVisitNum(visitInterface.getVisitNum() + 1);
				}
			} else {
				visitInterface = new InterfaceVisitStatistic();
				visitInterface.setVisitNum(Long.valueOf(1));
				interfaceVisitNum.put(interfaceName, visitInterface);
			}
		}
		chain.doFilter(req, resp);

		if (null == visitInterface || !interfaceVisitNum.containsKey(interfaceName)) {
			logger.debug("visitor statistic entity is null!");
			return;
		}
		synchronized (visitInterface) {
			logger.debug(visitInterface.toString());
			// 统计接口访问正确与否
			if (null != request.getAttribute("_retStatus") && 0 == ((int) request.getAttribute("_retStatus"))) {
				visitInterface.setSuccessNum(visitInterface.getSuccessNum() + 1);
			} else {
				visitInterface.setFailNum(visitInterface.getFailNum() + 1);
			}

			// 统计接口访问时间
			if (null != request.getAttribute("_visit_long_time")) {
				Long useTime = (Long) request.getAttribute("_visit_long_time");
				visitInterface.setVisitTime(visitInterface.getVisitTime() + useTime);
			}
		}
		logger.debug("excute requestContext filter-----end!!");
	}

	public Map<String, String> getReqParamMap(HttpServletRequest request) {

		Map<String, String> paramMap = new HashMap<String, String>();

		Enumeration requestEnum = request.getParameterNames();
		while (requestEnum.hasMoreElements()) {
			String paramName = (String) requestEnum.nextElement();

			String paramValue = request.getParameter(paramName);
			// 形成键值对应的map
			paramMap.put(paramName, paramValue);
		}

		if (paramMap.containsKey("header")) {
			String headInfo = paramMap.get("header");
			String userInfo = paramMap.get("userInfo");
			parseHeaderAndUserInfo(headInfo, userInfo, paramMap);
			if (paramMap.containsKey("pluginSign")) {
				String pluginSignInfo = paramMap.get("pluginSign");
				parsePluginSign(pluginSignInfo, paramMap);
			}
			if (paramMap.containsKey("attachedInfo")) {
				String attachedInfo = paramMap.get("attachedInfo");
				parseAttachedInfo(attachedInfo, paramMap);
			}
			if (paramMap.containsKey("appInfo")) {
				String appInfo = paramMap.get("appInfo");
				parseAppInfo(appInfo, paramMap);
			}
			if (paramMap.containsKey("tradeInfo")) {
				String tradeInfo = paramMap.get("tradeInfo");
				parseTradeInfo(tradeInfo, paramMap);
			}
		}

		if (paramMap.size() > 0) {
			return paramMap;
		} else {
			return null;
		}
	}

	/*
	 * 解析header and userInfo
	 */
	private void parseHeaderAndUserInfo(String headInfo, String userInfo, Map<String, String> paramMap) {
		JSONObject jsonObjectHead = null;
		JSONObject jsonObjectUser = null;
		// base64 解码
		String headstr = "";
		String userInfoStr = "";
		try {
			headstr = Base64.decode(headInfo);
			userInfoStr = Base64.decode(userInfo);
		} catch (Exception e) {
			logger.error("Base64.decode error headInfo:" + headInfo + "userInfo:" + userInfo);
			return;
		}

		logger.debug("encode headInfo:" + headInfo);
		logger.debug("decode headstr len:" + headstr.length() + ", str:" + headstr + ".");
		logger.debug("encode userInfo:" + userInfo);
		logger.debug("decode userInfoStr len:" + userInfoStr.length() + ", userInfoStr:" + userInfoStr + ".");

		// 解析json
		try {
			jsonObjectHead = ControllerHelper.parseJsonString(headstr);
			jsonObjectUser = ControllerHelper.parseJsonString(userInfoStr);
		} catch (JSONException e) {
			logger.error("parseJsonString error headInfo:" + headstr + "userInfo:" + userInfo);
			return;
		}

		if (null == jsonObjectHead || null == jsonObjectUser) {
			return;
		}

		if (jsonObjectHead.has("upv")) {
			logger.debug(ControllerHelper.getJsonElement(jsonObjectHead, "upv"));
			JSONObject jsonObjectUpv = jsonObjectHead.getJSONObject("upv");
			if (jsonObjectUpv.has("major") && jsonObjectUpv.has("minor")) {
				String upv = ControllerHelper.getJsonElement(jsonObjectUpv, "major") + "."
				        + ControllerHelper.getJsonElement(jsonObjectUpv, "minor");
				paramMap.put(Constant.PARA_NAME.VERSION, upv);
			}
		}

		paramMap.put(Constant.PARA_NAME.SERVICENAME, ControllerHelper.getJsonElement(jsonObjectHead, "op"));
		paramMap.put(Constant.PARA_NAME.PHONENUM, ControllerHelper.getJsonElement(jsonObjectUser, "phoneNum"));

		if (jsonObjectUser.containsKey("userId")) {
			paramMap.put(Constant.PARA_NAME.USERID, ControllerHelper.getJsonElement(jsonObjectUser, "userId"));
		}
	}

	/*
	 * 解析pluginSign
	 */
	private void parsePluginSign(String pluginSign, Map<String, String> paramMap) {
		JSONObject jsonObjectPluginSign = null;
		// base64 解码
		String pluginSignStr = "";
		try {
			pluginSignStr = Base64.decode(pluginSign);
		} catch (Exception e) {
			logger.error("Base64.decode error pluginSign:" + pluginSign);
			return;
		}

		logger.debug("encode pluginSign:" + pluginSign);
		logger.debug("decode pluginSignStr len:" + pluginSignStr.length() + ", str:" + pluginSignStr + ".");

		// 解析json
		try {
			jsonObjectPluginSign = ControllerHelper.parseJsonString(pluginSignStr);
		} catch (JSONException e) {
			logger.error("parseJsonString error pluginSignStr:" + pluginSignStr);
			return;
		}

		if (null == jsonObjectPluginSign) {
			return;
		}
		paramMap.put(Constant.PARA_NAME.SOTPID, ControllerHelper.getJsonElement(jsonObjectPluginSign, "pluginId"));
	}

	/*
	 * 解析attachedInfo
	 */
	private void parseAttachedInfo(String attachedInfo, Map<String, String> paramMap) {

		JSONObject jsonObjectAttach = null;
		// base64 解码
		String attachedInfoStr = "";
		try {
			attachedInfoStr = Base64.decode(attachedInfo);
		} catch (Exception e) {
			logger.error("Base64.decode error attachedInfo:" + attachedInfo);
			return;
		}

		logger.debug("decode attachedInfoStr len:" + attachedInfoStr.length() + ", str:" + attachedInfoStr + ".");

		// 解析json
		try {
			jsonObjectAttach = ControllerHelper.parseJsonString(attachedInfoStr);
		} catch (JSONException e) {
			logger.error("parseJsonString error attachedInfo:" + attachedInfo);
			return;
		}

		if (null == jsonObjectAttach) {
			return;
		}

		if (jsonObjectAttach.has("location")) {
			paramMap.put(Constant.PARA_NAME.LOCATION, ControllerHelper.getJsonElement(jsonObjectAttach, "location"));
		}
		if (jsonObjectAttach.has("ip")) {
			paramMap.put(Constant.PARA_NAME.IP, ControllerHelper.getJsonElement(jsonObjectAttach, "ip"));
		}
		if (jsonObjectAttach.has("wifiInfo")) {
			paramMap.put(Constant.PARA_NAME.WIFI, ControllerHelper.getJsonElement(jsonObjectAttach, "wifiInfo"));
		}
	}
	
	/*
	 * 解析appInfo
	 */
	
	private void parseAppInfo(String appInfo, Map<String, String> paramMap) {

		JSONObject jsonObjectAppInfo = null;
		// base64 解码
		String appInfoStr = "";
		try {
			appInfoStr = Base64.decode(appInfo);
		} catch (Exception e) {
			logger.error("Base64.decode error appInfo:" + appInfo);
			return;
		}

		logger.debug("decode appInfoStr len:" + appInfoStr.length() + ", str:" + appInfoStr + ".");

		// 解析json
		try {
			jsonObjectAppInfo = ControllerHelper.parseJsonString(appInfoStr);
		} catch (JSONException e) {
			logger.error("parseJsonString error appInfo:" + appInfo);
			return;
		}

		if (null == jsonObjectAppInfo) {
			return;
		}

		if (jsonObjectAppInfo.has("dev")) {
			paramMap.put(Constant.PARA_NAME.DEV_ID, ControllerHelper.getJsonElement(jsonObjectAppInfo, "dev"));
		}
	 
	}
	
	/*
	 * 解析tradeInfo
	 */
	private void parseTradeInfo(String tradeInfo, Map<String, String> paramMap) {

		JSONObject jsonObjectTrade = null;
		// base64 解码
		String tradeInfoStr = "";
		try {
			tradeInfoStr = Base64.decode(tradeInfo);
		} catch (Exception e) {
			logger.error("Base64.decode error attachedInfo:" + tradeInfo);
			return;
		}

		logger.debug("decode tradeInfoStr len:" + tradeInfoStr.length() + ", str:" + tradeInfoStr + ".");

		// 解析json
		try {
			jsonObjectTrade = ControllerHelper.parseJsonString(tradeInfoStr);
		} catch (JSONException e) {
			logger.error("parseJsonString error tradeInfo:" + tradeInfo);
			return;
		}

		if (null == jsonObjectTrade) {
			return;
		}

		if (jsonObjectTrade.has("payCard")) {
			paramMap.put(Constant.PARA_NAME.PAYCARD, ControllerHelper.getJsonElement(jsonObjectTrade, "payCard"));
		}
		if (jsonObjectTrade.has("payCardType")) {
			paramMap.put(Constant.PARA_NAME.CARDTYPE, ControllerHelper.getJsonElement(jsonObjectTrade, "payCardType"));
		}
		if (jsonObjectTrade.has("action")) {
			paramMap.put(Constant.PARA_NAME.PAYACTION, ControllerHelper.getJsonElement(jsonObjectTrade, "action"));
		}
		if (jsonObjectTrade.has("recCard")) {
			paramMap.put(Constant.PARA_NAME.RECCARD, ControllerHelper.getJsonElement(jsonObjectTrade, "recCard"));
		}
		if (jsonObjectTrade.has("price")) {
			paramMap.put(Constant.PARA_NAME.PRICE, ControllerHelper.getJsonElement(jsonObjectTrade, "price"));
		}
		if (jsonObjectTrade.has("payee")) {
			paramMap.put(Constant.PARA_NAME.PAYEE, ControllerHelper.getJsonElement(jsonObjectTrade, "payee"));
		}
	}
}
