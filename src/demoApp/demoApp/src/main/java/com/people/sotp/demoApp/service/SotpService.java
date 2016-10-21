package com.people.sotp.demoApp.service;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.people.sotp.commons.base.GlobalParam;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.commons.util.Base64;
import com.people.sotp.commons.util.SotpServerApi;
import com.people.sotp.commons.util.sendRequest;
import com.people.sotp.commons.util.sotpAuthUtil;
import com.people.sotp.dataobject.ApplyDO;
import com.people.sotp.dataobject.SessionKeyDO;
import com.people.sotp.dataobject.SotpAuthDO;
import com.people.sotp.dataobject.UserDO;
import com.people.sotp.demoApp.dao.DemoAppDAO;
import com.people.sotp.payment.dao.impl.SessionKeyDAO;

import net.sf.json.JSONObject;

@Service
public class SotpService {

	@Resource
	private DemoAppDAO demoAppDAo;

	@Resource
	SessionKeyDAO sessionKeyDAO;

	private static Log log = LogFactory.getLog(SotpService.class);

	public static String project = "fidoportal";

	/**
	 * 申请插件1
	 * 
	 * @param json
	 * @return
	 */
	public String pluginApply(JSONObject json) {

		StringBuffer jsondata = new StringBuffer();
		StringBuffer sotpGet = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		int status = -1;

		if (json.containsKey("loginPwd")) {
			json.remove("loginPwd");
		}
		if (json.containsKey("payPwd")) {
			json.remove("payPwd");
		}

		String sotpInfo = json.getString("sotpInfo");
		sotpInfo = sotpInfo.replaceAll("(\r\n|\r|\n|\n\r)", "");
		Map<String, Object> sotpMap = new HashMap<String, Object>();
		sotpMap = sotpAuthUtil.getUrlParams(sotpInfo);

		if (json.containsKey("transactionInfo")) {
			String transactionInfo = json.getString("transactionInfo");
			sotpMap.put("tradeInfo", Base64.encode(transactionInfo));
		}

		if (json.containsKey("businessName")) {
			sotpMap.put("businessName", json.getString("businessName"));
		}

		String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
		if (appKey == null) {
			jsondata.append("{}");
			return utilJson(json.getString("service"), GlobalParam.AppIdError, jsondata.toString(), "appId not match");
		}

		SotpAuthDO service = new SotpAuthDO();
		try {
			service = demoAppDAo.selectService();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		sotpGet.append("http://" + service.getIp() + ":" + service.getPost() + "");
		sotpGet.append("/" + project + "");

		String sotpAuth = sotpAuthUtil.genRequestSotpAuthMsg(sotpMap, sotpInfo, appKey);
		if (json.containsKey("transactionInfo")) {
			String transactionInfo = json.getString("transactionInfo");
			sotpAuth += "&tradeInfo=" + Base64.encode(transactionInfo);
		}

		String info = sendRequest.sendPost(sotpGet.toString(), sotpAuth);
		log.info("terminal======" + json.toString());
		log.info("Pc======" + sotpGet.toString() + "?" + sotpAuth);
		log.info("Sotp======" + info);

		JSONObject jsonsotp = JSONObject.fromObject(info);

		appKey = getAppKey(jsonsotp.getString("appId"));
		if (appKey == null) {
			jsondata.append("{}");
			return utilJson(json.getString("service"), GlobalParam.AppIdError, jsondata.toString(), "appId not match");
		}
		boolean falg = sotpAuthUtil.verifySotp(jsonsotp, jsonsotp.getString("appId"), appKey);
		if (!falg) {
			jsondata.append("{}");
			return utilJson(json.getString("service"), GlobalParam.SignMatch, jsondata.toString(), "sign not match");
		}

		JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
		status = jsonsotp.getInt("status");
		if (jsonsotp.getInt("status") == 0) {
			if (sotpJson.containsKey("hwInfo")) {
				jsondata.append(" " + sotpJson.toString() + " ");
			} else {
				if (sotpJson.containsKey("data")) {
					if (!jsonsotp.containsKey("clientSign")) {
						String d = sotpJson.getString("data");
						if (d.trim().charAt(0) == '{')
							jsondata.append(d);
						else
							jsondata.append("\"" + d + "\"");
					} else {
						jsondata.append("{");
						if (jsonsotp.containsKey("clientSign"))
							jsondata.append("\"clientSign\":\"" + jsonsotp.getString("clientSign") + "\",");
						boolean isObject = sotpJson.getString("data").trim().startsWith("{");
						if (isObject) {
							jsondata.append("\"dataInfo\":" + sotpJson.getString("data") + ",");

						} else {
							jsondata.append("\"dataInfo\":\"" + sotpJson.getString("data") + "\",");
						}
						jsondata.append("\"serverTime\":\"" + jsonsotp.getString("serverTime") + "\"");
						jsondata.append("}");
						// jsondata.append(sotpJson.getString("data"));
					}
				} else {
					jsondata.append("{}");
				}

			}

		} else {
			jsondata.append("{}");
			if (sotpJson.containsKey("errorMsg")) {
				errorMsg.append(sotpJson.getString("errorMsg"));
			}
		}

		return utilJson(json.getString("service"), status, jsondata.toString(), errorMsg.toString());
	}

	/*
	 * 下载插件 加入预留信息
	 * 
	 * @param json
	 * 
	 * @return
	 */

	public String downloadPlugin(JSONObject json) {
		String jsondata = pluginApply(json);// 认证
		// int status = -1;

		if (json.containsKey("holdInfo") && json.containsKey("username")) {
			UserDO user = new UserDO();
			JSONObject data = JSONObject.fromObject(jsondata);
			try {
				if (data.getInt("status") == 0 && json.getString("holdInfo") != null
						&& !"".equals(json.getString("holdInfo"))) {
					// 预留信息写入数据库
					user.setPhoneNum(json.getString("username"));
					/*
					 * 用户是否存在 user= demoAppDAo.queryUserOne(user);
					 * if(user==null){ return
					 * utilJson(json.getString("service"),GlobalParam.
					 * UserNotExist,jsondata.toString(),"用户不存在"); }
					 */
					user.setHoldInfo(json.getString("holdInfo"));
					demoAppDAo.updateUser(user);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				log.debug(e);
			}
		}

		return jsondata;
	}

	public String dataencryption(JSONObject json) {

		ResultDO result = new ResultDO();
		StringBuffer data = new StringBuffer();
		StringBuffer sotpGet = new StringBuffer();
		int status = -1;

		try {

			Map<String, Object> sotpMap = new HashMap<String, Object>();
			String url = json.getString("sotpInfo");
			url = url.replaceAll("(\r\n|\r|\n|\n\r)", "");

			// url=url.replaceAll("中文",
			// java.net.URLEncoder.encode("中文","utf-8"));
			sotpMap = sotpAuthUtil.getUrlParams(url);
			String sotpId = (String) json.get("sotpId");
			String randa = (String) sotpMap.get("dataInfo"); // 移动端生成的随机数
			String randb = String.valueOf((int) ((Math.random() * 9 + 1) * 100000000)); // web端生成的随机数
			String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
			if (appKey == null) {
				data.append("\"\"");
				result.setErrMsg("appId not match");
				return utilJson(json.getString("service"), GlobalParam.AppIdError, data.toString(), result.getErrMsg());
			}
			SotpAuthDO service = new SotpAuthDO();
			service = demoAppDAo.selectService();
			sotpGet.append("http://" + service.getIp() + ":" + service.getPost() + "");
			sotpGet.append("/" + SotpService.project + "?");
			sotpGet.append("" + sotpAuthUtil.genRequestSotpAuthMsg(sotpMap, url, appKey) + "");

			String info = sendRequest.sendGet(sotpGet.toString());
			log.info("terminal======" + json.toString());
			log.info("Pc======" + sotpGet.toString());
			log.info("Sotp======" + info);

			JSONObject jsonsotp = JSONObject.fromObject(info);

			appKey = getAppKey(jsonsotp.getString("appId"));
			if (appKey == null) {
				data.append("\"\"");
				result.setErrMsg("sotp appId not match");
				return utilJson(json.getString("service"), GlobalParam.AppIdError, data.toString(), result.getErrMsg());
			}
			boolean falg = sotpAuthUtil.verifySotp(jsonsotp, jsonsotp.getString("appId"), appKey);
			if (!falg) {
				data.append("\"\"");
				result.setErrMsg("sign not match");
				return utilJson(json.getString("service"), GlobalParam.SignMatch, data.toString(), result.getErrMsg());
			}

			JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
			if (jsonsotp.getInt("status") == 0) {
				status = 0;
				if (sotpJson.containsKey("data")) {
					data.append("{ \"webNum\":\"" + randb + "\",\"encryptRandomNum\":\"" + sotpJson.getString("data")
							+ "\" }");
					SessionKeyDO session = new SessionKeyDO();
					session.setSotpId(sotpId);
					session = sessionKeyDAO.selectSessionKeyOne(session);
					if (session == null) {
						SessionKeyDO sess = new SessionKeyDO();
						sess.setSotpId(sotpId);
						sess.setRanda(randa);
						sess.setRandb(randb);
						sessionKeyDAO.addSessionKey(sess);
					} else {
						session.setRanda(randa);
						session.setRandb(randb);
						sessionKeyDAO.editSessionKey(session);
					}
				} else {
					data.append("\"\"");
				}
			} else {
				data.append("\"\"");
				result.setErrMsg(sotpJson.getString("errorMsg"));
				status = jsonsotp.getInt("status");
			}
		} catch (Exception e) {
			data.append("\"\"");
			result.setErrMsg("请求失败");
			log.error("请求失败");
			log.error(e);
			return utilJson(json.getString("service"), status, data.toString(), result.getErrMsg());
		}
		return utilJson(json.getString("service"), status, data.toString(), result.getErrMsg());

	}

	public String gensesskey(JSONObject json) {
		SessionKeyDO session = new SessionKeyDO();
		ResultDO result = new ResultDO();
		StringBuffer data = new StringBuffer();
		StringBuffer sotpGet = new StringBuffer();
		int status = -1;
		try {
			Map<String, Object> sotpMap = new HashMap<String, Object>();
			String url = json.getString("sotpInfo");
			sotpMap = sotpAuthUtil.getUrlParams(url);
			String sotpId = (String) json.get("sotpId");
			url = url.replaceAll("(\r\n|\r|\n|\n\r)", "");
			session.setSotpId(sotpId);
			session = sessionKeyDAO.selectSessionKeyOne(session);
			url = url + "&randa=" + session.getRanda() + "&randb=" + session.getRandb() + "";
			String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
			if (appKey == null) {
				data.append("\"\"");
				result.setErrMsg("appId not match");
				return utilJson(json.getString("service"), GlobalParam.AppIdError, data.toString(), result.getErrMsg());
			}
			SotpAuthDO service = new SotpAuthDO();
			service = demoAppDAo.selectService();
			sotpGet.append("http://" + service.getIp() + ":" + service.getPost() + "");
			sotpGet.append("/" + SotpService.project + "?");
			sotpGet.append("" + sotpAuthUtil.genRequestSotpAuthMsg(sotpMap, url, appKey) + "");

			String info = sendRequest.sendGet(sotpGet.toString());
			log.info("terminal======" + json.toString());
			log.info("Pc======" + sotpGet.toString());
			log.info("Sotp======" + info);

			JSONObject jsonsotp = JSONObject.fromObject(info);

			appKey = getAppKey(jsonsotp.getString("appId"));
			if (appKey == null) {
				data.append("\"\"");
				result.setErrMsg("sotp appId not match");
				return utilJson(json.getString("service"), GlobalParam.AppIdError, data.toString(), result.getErrMsg());
			}
			boolean falg = sotpAuthUtil.verifySotp(jsonsotp, jsonsotp.getString("appId"), appKey);
			if (!falg) {
				data.append("\"\"");
				result.setErrMsg("sign not match");
				return utilJson(json.getString("service"), GlobalParam.SignMatch, data.toString(), result.getErrMsg());
			}

			JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
			if (jsonsotp.getInt("status") == 0) {
				status = 0;
				if (sotpJson.containsKey("data")) {
					data.append("{");
					if (jsonsotp.containsKey("clientSign"))
						data.append("\"clientSign\":\"" + jsonsotp.getString("clientSign") + "\",");
					data.append("\"serverTime\":\"" + jsonsotp.getString("serverTime") + "\"");
					data.append("}");

					session.setSotpId(sotpId);
					session.setSessionKey(sotpJson.getString("data"));
					sessionKeyDAO.editSessionKey(session);
				} else {
					data.append("\"\"");
				}
			} else {
				data.append("\"\"");
				result.setErrMsg(sotpJson.getString("errorMsg"));
				status = jsonsotp.getInt("status");
			}
		} catch (Exception e) {
			e.printStackTrace();
			data.append("\"\"");
			result.setErrMsg("请求失败");
			return utilJson(json.getString("service"), status, data.toString(), result.getErrMsg());
		}
		return utilJson(json.getString("service"), status, data.toString(), result.getErrMsg());

	}

	public String verifySessionKey(JSONObject json) {

		StringBuffer data = new StringBuffer();
		ResultDO result = new ResultDO();
		SessionKeyDO session = new SessionKeyDO();
		session.setSotpId(json.getString("sotpId"));
		try {
			session = sessionKeyDAO.selectSessionKeyOne(session);
			String verifyInfo = json.getString("verifyInfo");
			System.out.println("------------------------verifyInfo" + verifyInfo);
			System.out.println("sessionKey---------------------" + session.getSessionKey());
			String str = SotpServerApi.sotpDataDecrypto(verifyInfo, session.getSessionKey());
			str = "hello" + str;
			data.append("{ \"handInfo\":\"" + SotpServerApi.sotpDataCrypto(str, session.getSessionKey()) + "\" }");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return utilJson(json.getString("service"), 0, data.toString(), result.getErrMsg());
	}

	/**
	 * 通过appid获取appkey
	 * 
	 * @param appId
	 * @return
	 */
	public String getAppKey(String appId) {
		ApplyDO apply = new ApplyDO();
		apply.setAppId(appId);
		try {
			apply = demoAppDAo.queryApplyOne(apply);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (apply != null) {
			return apply.getAppKey();
		} else {
			return null;
		}
	}

	/**
	 * 返回json
	 * 
	 * @param type
	 *            业务类型
	 * @param status
	 *            结果
	 * @param data
	 *            返回信息
	 * @param errorMsg
	 *            错误信息
	 * @param printWriter
	 */
	public String utilJson(String service, int status, String data, String errorMsg) {
		StringBuffer jsondata = new StringBuffer();
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String serverTime = dateFormat.format(now);
		jsondata.append("{\"service\": \"" + service + "\" ");
		jsondata.append(",\"status\":" + status + ",");
		jsondata.append("\"data\":" + data + "");
		jsondata.append(",\"errorMsg\":\"" + errorMsg + "\",");
		jsondata.append("\"serverTime\":\"" + serverTime + "\"}");
		return jsondata.toString();
	}

}
