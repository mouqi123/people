package com.people.sotp.urlcontrollers;

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
import com.people.sotp.commons.util.SotpServerApi;
import com.people.sotp.dataobject.ApplyDO;
import com.people.sotp.dataobject.MemberDO;
import com.people.sotp.dataobject.SessionKeyDO;
import com.people.sotp.dataobject.SotpAuthDO;
import com.people.sotp.payment.dao.MemberDAO;
import com.people.sotp.payment.service.ApplyService;

import net.sf.json.JSONObject;

@Service
public class ManageSotpService {

	@Resource
	private SotpDao sotpDao;
	@Resource
	private MemberDAO memberDAO;
	@Resource
	private ApplyService ApplyService;

	private static Log log = LogFactory.getLog(ManageSotpService.class);

	/**
	 * 申请插件
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String applyPlugin(JSONObject json, PrintWriter printWriter) {
		// System.out.println("client
		// data------------------------------------------------------------"+json);
		ResultDO result = new ResultDO();
		String phoneNum = json.getString("phoneNum");
		String holdinfo = json.getString("holdinfo");
		StringBuffer sotpGet = new StringBuffer();
		StringBuffer data = new StringBuffer();
		int status = -1;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("phone", phoneNum);
		map.put("holdinfo", holdinfo);
		try {
			status = sotpDao.queryCardtCount(map);
			if (status == 0) {
				result.setErrMsg("用户不存在");
				data.append("\"\"");
				return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
			} else {
				Map<String, Object> sotpMap = new HashMap<String, Object>();
				String url = json.getString("sotp");
				url = url.replaceAll("(\r\n|\r|\n|\n\r)", "");
				sotpMap = sotpAuthUtil.getUrlParams(url);
				String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
				if (appKey == null) {
					data.append("\"\"");
					result.setErrMsg("appId not match");
					return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
				}
				SotpAuthDO service = new SotpAuthDO();
				service = sotpDao.selectService();
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
					return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
				}
				boolean falg = sotpAuthUtil.verifyPlugin(jsonsotp, jsonsotp.getString("appId"), appKey);
			//	boolean falg = sotpAuthUtil.verifySotp(jsonsotp, jsonsotp.getString("appId"), appKey);
				if (!falg) {
					data.append("\"\"");
					result.setErrMsg("sign not match");
					return utilJson(json.getInt("type"), ErrorInfo.SignMatch, data.toString(), result.getErrMsg());
				}
				JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
				if (jsonsotp.getInt("status") != 0) {
					result.setErrMsg(sotpJson.get("errorMsg").toString());
					data.append("\"\"");
					status = jsonsotp.getInt("status");
				} else {
					String hwInfo = null;
					if (sotpJson.has("hwInfo")) {
						hwInfo = sotpJson.getString("hwInfo");
					} else {
						hwInfo = "";
					}
					data.append("{\"plugin\":\"" + sotpJson.getString("data") + "\",\"component\":\"" + hwInfo + "\"}");
					result.setErrMsg("");

					String sotp = sotpDao.queryHoldInfoOne(map);
					if (null == sotp) {
						sotpDao.addHoldInfo(map);
					} else {
						sotpDao.editHoldInfo(map);
					}
					status = 0;
				}
				return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
			}
		} catch (Exception e) {
			result.setErrMsg("申请插件失败");
			e.printStackTrace();
			log.error("申请插件失败");
			log.error(e);
			return utilJson(json.getInt("type"), status, "\"\"", result.getErrMsg());
		}
	}

	/**
	 * 下载插件
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String downPlugin(JSONObject json, PrintWriter printWriter) {
		ResultDO result = new ResultDO();
		String phoneNum = json.getString("phoneNum");
		String downUrl = json.getString("downUrl");
		MemberDO member = new MemberDO();
		member.setPhone(phoneNum);
		int status = -1;
		StringBuffer sotpGet = new StringBuffer();
		StringBuffer data = new StringBuffer();
		try {
			MemberDO user = memberDAO.loginMember(member);
			user.setPluginId(downUrl);
			memberDAO.updateMember(user);
			SotpAuthDO service = new SotpAuthDO();
			service = sotpDao.selectService();

			String appId = json.getString("appId");
			// 生成六位随机数
			String nonce_str = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
			Map<String, Object> sotpMap = new HashMap<String, Object>();
			sotpMap.put("type", "1025");
			sotpMap.put("userId", String.valueOf(user.getId()));
			sotpMap.put("phoneNum", phoneNum);
			sotpMap.put("url", downUrl);
			sotpMap.put("appId", appId);
			sotpMap.put("nonce_str", nonce_str);
			String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
			if (appKey == null) {
				data.append("\"\"");
				result.setErrMsg("appId not match");
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			String sign = sotpAuthUtil.signParaByAppKey(sotpMap, appKey);
			sotpGet.append("http://" + service.getIp() + ":" + service.getPost() + "");
			sotpGet.append("/" + SotpService.project + "/plugindown?");
			sotpGet.append("type=1025&userId=" + user.getId() + "&phoneNum=" + phoneNum + "&url=" + downUrl + "");
			sotpGet.append("&appId=" + appId + "&nonce_str=" + nonce_str + "&sign=" + sign + "");

			String info = sendRequest.sendGet(sotpGet.toString());

			log.info("terminal======" + json.toString());
			log.info("Pc======" + sotpGet.toString());
			log.info("Sotp======" + info);

			JSONObject jsonsotp = JSONObject.fromObject(info);
			appKey = getAppKey(jsonsotp.getString("appId"));
			if (appKey == null) {
				data.append("\"\"");
				result.setErrMsg("sotp appId not match");
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			boolean falg = sotpAuthUtil.verifyPlugin(jsonsotp, jsonsotp.getString("appId"), appKey);
			if (!falg) {
				data.append("\"\"");
				result.setErrMsg("sign not match");
				return utilJson(json.getInt("type"), ErrorInfo.SignMatch, data.toString(), result.getErrMsg());
			}
			JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
			if (jsonsotp.getInt("status") == 0) {
				status = 0;
				data.append("{\"plugin\":\"" + sotpJson.getString("data") + "\"}");
			} else {
				data.append("\"\"");
				result.setErrMsg(jsonsotp.get("message").toString());
				status = jsonsotp.getInt("status");
			}

		} catch (Exception e) {
			result.setErrMsg("下载插件失败");
			log.error("下载插件失败");
			log.error(e);
			data.append("\"\"");
			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
		}
		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
	}

	/**
	 * 插件状态判断
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String pluginStatus(JSONObject json, PrintWriter printWriter) {
		ResultDO result = new ResultDO();
		int status = -1;
		StringBuffer sotpGet = new StringBuffer();
		StringBuffer data = new StringBuffer();
		try {

			Map<String, Object> sotpMap = new HashMap<String, Object>();
			String url = json.getString("sotp");
			url = url.replaceAll("(\r\n|\r|\n|\n\r)", "");
			sotpMap = sotpAuthUtil.getUrlParams(url);
			String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
			if (appKey == null) {
				data.append("\"\"");
				result.setErrMsg("appId not match");
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}

			SotpAuthDO service = new SotpAuthDO();
			service = sotpDao.selectService();
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
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			boolean falg = sotpAuthUtil.verifySotp(jsonsotp, jsonsotp.getString("appId"), appKey);
			if (!falg) {
				data.append("\"\"");
				result.setErrMsg("sign not match");
				return utilJson(json.getInt("type"), ErrorInfo.SignMatch, data.toString(), result.getErrMsg());
			}

			JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
			if (jsonsotp.getInt("status") == 0) {
				status = jsonsotp.getInt("status");
				if (sotpJson.has("data")) {
					data.append(sotpJson.getString("data"));
				} else {
					data.append("\"\"");
				}
			} else {
				status = jsonsotp.getInt("status");
				data.append("\"\"");
				result.setErrMsg(sotpJson.getString("errorMsg"));
			}
		} catch (Exception e) {
			result.setErrMsg("插件状态判断失败");
			log.error("插件状态判断失败");
			log.error(e);
			data.append("\"\"");
			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
		}
		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
	}

	/**
	 * 更新请求
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String pluginUpdateNew(JSONObject json, PrintWriter printWriter) {
		ResultDO result = new ResultDO();
		int status = -1;
		StringBuffer sotpGet = new StringBuffer();
		StringBuffer data = new StringBuffer();
		try {

			Map<String, Object> sotpMap = new HashMap<String, Object>();
			String url = json.getString("sotp");
			url = url.replaceAll("(\r\n|\r|\n|\n\r)", "");
			sotpMap = sotpAuthUtil.getUrlParams(url);
			String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
			if (appKey == null) {
				data.append("\"\"");
				result.setErrMsg("appId not match");
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			SotpAuthDO service = new SotpAuthDO();
			service = sotpDao.selectService();
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
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			boolean falg = sotpAuthUtil.verifyPlugin(jsonsotp, jsonsotp.getString("appId"), appKey);
			if (!falg) {
				data.append("\"\"");
				result.setErrMsg("sign not match");
				return utilJson(json.getInt("type"), ErrorInfo.SignMatch, data.toString(), result.getErrMsg());
			}

			JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
			String hwInfo = null;
			if (sotpJson.has("hwInfo")) {
				hwInfo = sotpJson.getString("hwInfo");
			} else {
				hwInfo = "";
			}
			if (jsonsotp.getInt("status") == 0) {
				status = 0;
				data.append("{\"plugin\":\"" + sotpJson.getString("data") + "\",\"component\":\"" + hwInfo + "\"}");
			} else {
				data.append("\"\"");
				result.setErrMsg(sotpJson.getString("errorMsg"));
				status = jsonsotp.getInt("status");
			}

		} catch (Exception e) {
			result.setErrMsg("更新请求失败");
			log.error("更新请求失败");
			log.error(e);
			data.append("\"\"");
			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
		}
		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
	}

	/**
	 * 获取设备列表
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String getDevlist(JSONObject json, PrintWriter printWriter) {
		int status = -1;
		StringBuffer sotpGet = new StringBuffer();
		StringBuffer data = new StringBuffer();
		ResultDO result = new ResultDO();
		try {

			Map<String, Object> sotpMap = new HashMap<String, Object>();
			String url = json.getString("sotp");
			url = url.replaceAll("(\r\n|\r|\n|\n\r)", "");
			sotpMap = sotpAuthUtil.getUrlParams(url);
			String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
			if (appKey == null) {
				data.append("\"\"");
				result.setErrMsg("appId not match");
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			SotpAuthDO service = new SotpAuthDO();
			service = sotpDao.selectService();
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
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			boolean falg = sotpAuthUtil.verifySotp(jsonsotp, jsonsotp.getString("appId"), appKey);
			if (!falg) {
				data.append("\"\"");
				result.setErrMsg("sign not match");
				return utilJson(json.getInt("type"), ErrorInfo.SignMatch, data.toString(), result.getErrMsg());
			}
			if (jsonsotp.getInt("status") == 0) {
				status = 0;
				JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
				data.append("{\"dev\":\"" + sotpJson.getString("data") + "\"}");
				result.setErrMsg("");
			} else {
				status = jsonsotp.getInt("status");
				data.append("{\"dev\":\"\"}");
				result.setErrMsg("");
			}

			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
		} catch (Exception e) {
			result.setErrMsg("获取设备列表失败");
			log.error("获取设备列表失败");
			log.error(e);
			data.append("\"\"");
			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
		}
	}

	/**
	 * 解绑设备
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String delBinddev(JSONObject json, PrintWriter printWriter) {
		int status = -1;
		StringBuffer sotpGet = new StringBuffer();
		StringBuffer data = new StringBuffer();

		ResultDO result = new ResultDO();
		try {

			Map<String, Object> sotpMap = new HashMap<String, Object>();
			String url = json.getString("sotp");
			url = url.replaceAll("(\r\n|\r|\n|\n\r)", "");
			sotpMap = sotpAuthUtil.getUrlParams(url);
			String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
			if (appKey == null) {
				data.append("\"\"");
				result.setErrMsg("appId not match");
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			SotpAuthDO service = new SotpAuthDO();
			service = sotpDao.selectService();
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
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			boolean falg = sotpAuthUtil.verifySotp(jsonsotp, jsonsotp.getString("appId"), appKey);
			if (!falg) {
				data.append("\"\"");
				result.setErrMsg("sign not match");
				return utilJson(json.getInt("type"), ErrorInfo.SignMatch, data.toString(), result.getErrMsg());
			}
			if (jsonsotp.getInt("status") == 0) {
				status = 0;
				data.append("\"\"");
			} else {
				JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
				data.append("\"\"");
				result.setErrMsg(sotpJson.getString("errorMsg"));
				status = jsonsotp.getInt("status");
			}
		} catch (Exception e) {
			log.error("解绑设备失败");
			log.error(e);
			data.append("\"\"");
			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
		}
		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
	}

	/**
	 * 请求验证码(挑战码)
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String reqChallengcode(JSONObject json, PrintWriter printWriter) {
		int status = -1;
		StringBuffer sotpGet = new StringBuffer();
		StringBuffer data = new StringBuffer();
		ResultDO result = new ResultDO();

		try {

			Map<String, Object> sotpMap = new HashMap<String, Object>();
			String url = json.getString("sotp");
			url = url.replaceAll("(\r\n|\r|\n|\n\r)", "");
			sotpMap = sotpAuthUtil.getUrlParams(url);
			String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
			if (appKey == null) {
				data.append("\"\"");
				result.setErrMsg("appId not match");
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			SotpAuthDO service = new SotpAuthDO();
			service = sotpDao.selectService();
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
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			boolean falg = sotpAuthUtil.verifySotp(jsonsotp, jsonsotp.getString("appId"), appKey);
			if (!falg) {
				data.append("\"\"");
				result.setErrMsg("sign not match");
				return utilJson(json.getInt("type"), ErrorInfo.SignMatch, data.toString(), result.getErrMsg());
			}
			JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
			if (jsonsotp.getInt("status") == 0) {
				status = 0;
				data.append("{\"challenge\":\"" + sotpJson.getString("data") + "\"}");
			} else {
				status = jsonsotp.getInt("status");
				data.append("\"\"");
				result.setErrMsg(sotpJson.getString("errorMsg"));
			}
		} catch (Exception e) {
			log.error("请求验证码(挑战码)失败");
			log.error(e);
			data.append("\"\"");
			return utilJson(json.getInt("type"), status, data.toString(), "");
		}
		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
	}

	/**
	 * 修改保护码
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String modProtect(JSONObject json, PrintWriter printWriter) {
		int status = -1;
		StringBuffer data = new StringBuffer();
		StringBuffer sotpGet = new StringBuffer();
		ResultDO result = new ResultDO();
		try {
			Map<String, Object> sotpMap = new HashMap<String, Object>();
			String url = json.getString("sotp");
			url = url.replaceAll("(\r\n|\r|\n|\n\r)", "");
			sotpMap = sotpAuthUtil.getUrlParams(url);
			String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
			if (appKey == null) {
				data.append("\"\"");
				result.setErrMsg("appId not match");
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			SotpAuthDO service = new SotpAuthDO();
			service = sotpDao.selectService();
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
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			boolean falg = sotpAuthUtil.verifySotp(jsonsotp, jsonsotp.getString("appId"), appKey);
			if (!falg) {
				data.append("\"\"");
				result.setErrMsg("sign not match");
				return utilJson(json.getInt("type"), ErrorInfo.SignMatch, data.toString(), result.getErrMsg());
			}
			if (jsonsotp.getInt("status") == 0) {
				status = 0;
				data.append("\"\"");
			} else {
				JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
				status = jsonsotp.getInt("status");
				data.append("\"\"");
				result.setErrMsg(sotpJson.get("errorMsg").toString());
			}

		} catch (Exception e) {
			log.error("修改保护码失败");
			log.error(e);
			data.append("\"\"");
			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
		}
		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
	}

	/**
	 * 预留信息（获取、修改）
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String holdInfo(JSONObject json, PrintWriter printWriter) {
		int status = -1;
		StringBuffer data = new StringBuffer();
		String phone = json.getString("phoneNum");
		ResultDO result = new ResultDO();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("phone", phone);

		try {
			// 连接sotp服务器
			if (json.getInt("action") == 1) {
				data.append("{\"holdinfo\":\"" + sotpDao.queryHoldInfoOne(map) + "\"}");
			} else {
				String holdinfo = json.getString("holdinfo");
				map.put("holdinfo", holdinfo);
				sotpDao.editHoldInfo(map);
				data.append("\"\"");
			}
			status = 0;
			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());

		} catch (Exception e) {
			log.error("erroe===" + e);
			data.append("\"\"");
			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
		}
	}

	/**
	 * 请求时间同步
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String genSynSrvtime(JSONObject json, PrintWriter printWriter) {
		ResultDO result = new ResultDO();
		StringBuffer data = new StringBuffer();
		StringBuffer sotpGet = new StringBuffer();
		int status = -1;
		try {

			Map<String, Object> sotpMap = new HashMap<String, Object>();
			String url = json.getString("sotp");
			url = url.replaceAll("(\r\n|\r|\n|\n\r)", "");

			// url=url.replaceAll("中文",
			// java.net.URLEncoder.encode("中文","utf-8"));
			sotpMap = sotpAuthUtil.getUrlParams(url);
			String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
			if (appKey == null) {
				data.append("\"\"");
				result.setErrMsg("appId not match");
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			SotpAuthDO service = new SotpAuthDO();
			service = sotpDao.selectService();
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
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			boolean falg = sotpAuthUtil.verifySotp(jsonsotp, jsonsotp.getString("appId"), appKey);
			if (!falg) {
				data.append("\"\"");
				result.setErrMsg("sign not match");
				return utilJson(json.getInt("type"), ErrorInfo.SignMatch, data.toString(), result.getErrMsg());
			}

			JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
			if (jsonsotp.getInt("status") == 0) {
				status = 0;
				if (sotpJson.containsKey("data")) {
					data.append("\"" + sotpJson.getString("data") + "\"");
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
			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
		}
		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
	}

	/*
	 * 数据加密
	 */
	public String dataencryption(JSONObject json, PrintWriter printWriter) {

		ResultDO result = new ResultDO();
		StringBuffer data = new StringBuffer();
		StringBuffer sotpGet = new StringBuffer();
		int status = -1;

		try {

			Map<String, Object> sotpMap = new HashMap<String, Object>();
			String url = json.getString("sotp");
			url = url.replaceAll("(\r\n|\r|\n|\n\r)", "");

			// url=url.replaceAll("中文",
			// java.net.URLEncoder.encode("中文","utf-8"));
			sotpMap = sotpAuthUtil.getUrlParams(url);
			String sotpId = (String) sotpMap.get("sotpId");
			String randa = (String) sotpMap.get("dataInfo"); // 移动端生成的随机数
			String randb = String.valueOf((int) ((Math.random() * 9 + 1) * 100000000)); // web端生成的随机数
			String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
			if (appKey == null) {
				data.append("\"\"");
				result.setErrMsg("appId not match");
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			SotpAuthDO service = new SotpAuthDO();
			service = sotpDao.selectService();
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
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			boolean falg = sotpAuthUtil.verifySotp(jsonsotp, jsonsotp.getString("appId"), appKey);
			if (!falg) {
				data.append("\"\"");
				result.setErrMsg("sign not match");
				return utilJson(json.getInt("type"), ErrorInfo.SignMatch, data.toString(), result.getErrMsg());
			}

			JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
			if (jsonsotp.getInt("status") == 0) {
				status = 0;
				if (sotpJson.containsKey("data")) {
					data.append("{ \"webNum\":\"" + randb + "\",\"encryptRandomNum\":\"" + sotpJson.getString("data")
							+ "\" }");
					SessionKeyDO session = new SessionKeyDO();
					session.setSotpId(sotpId);
					session = sotpDao.selectSessionKeyOne(session);
					if (session == null) {
						SessionKeyDO sess = new SessionKeyDO();
						sess.setSotpId(sotpId);
						sess.setRanda(randa);
						sess.setRandb(randb);
						sotpDao.addSessionKey(sess);
					} else {
						session.setRanda(randa);
						session.setRandb(randb);
						sotpDao.editSessionKey(session);
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
			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
		}
		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());

	}

	/*
	 * 生成sessionKey
	 */
	public String gensesskey(JSONObject json, PrintWriter printWriter) {
		SessionKeyDO session = new SessionKeyDO();
		ResultDO result = new ResultDO();
		StringBuffer data = new StringBuffer();
		StringBuffer sotpGet = new StringBuffer();
		int status = -1;
		try {
			Map<String, Object> sotpMap = new HashMap<String, Object>();
			String url = json.getString("sotp");
			sotpMap = sotpAuthUtil.getUrlParams(url);
			String sotpId = (String) sotpMap.get("sotpId");
			url = url.replaceAll("(\r\n|\r|\n|\n\r)", "");
			session.setSotpId((String) sotpMap.get("sotpId"));
			session = sotpDao.selectSessionKeyOne(session);
			url = url + "&randa=" + session.getRanda() + "&randb=" + session.getRandb() + "";
			String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
			if (appKey == null) {
				data.append("\"\"");
				result.setErrMsg("appId not match");
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			SotpAuthDO service = new SotpAuthDO();
			service = sotpDao.selectService();
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
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			boolean falg = sotpAuthUtil.verifySotp(jsonsotp, jsonsotp.getString("appId"), appKey);
			if (!falg) {
				data.append("\"\"");
				result.setErrMsg("sign not match");
				return utilJson(json.getInt("type"), ErrorInfo.SignMatch, data.toString(), result.getErrMsg());
			}

			JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
			if (jsonsotp.getInt("status") == 0) {
				status = 0;
				if (sotpJson.containsKey("data")) {
					data.append("{}");

					session.setSotpId(sotpId);
					session.setSessionKey(sotpJson.getString("data"));
					sotpDao.editSessionKey(session);
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
			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
		}
		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());

	}

	public String verifySessionKey(JSONObject json, PrintWriter printWriter) {

		StringBuffer data = new StringBuffer();
		ResultDO result = new ResultDO();
		SessionKeyDO session = new SessionKeyDO();
		session.setSotpId(json.getString("sotpId"));
		try {
			session = sotpDao.selectSessionKeyOne(session);
			String verifyInfo = json.getString("verifyInfo");
			System.out.println("------------------------verifyInfo" + verifyInfo);
			System.out.println("sessionKey---------------------" + session.getSessionKey());
			String str = SotpServerApi.sotpDataDecrypto(verifyInfo, session.getSessionKey());
			str = "hello" + str;
			data.append("{ \"handInfo\":\"" + SotpServerApi.sotpDataCrypto(str, session.getSessionKey()) + "\" }");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return utilJson(json.getInt("type"), 0, data.toString(), result.getErrMsg());
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
		apply = ApplyService.queryApplyOne(apply);
		if (apply != null) {
			return apply.getAppKey();
		} else {
			return null;
		}
	}

	public String verifyApp(JSONObject json, PrintWriter printWriter) {
		int status = -1;
		StringBuffer sotpGet = new StringBuffer();
		StringBuffer data = new StringBuffer();
		ResultDO result = new ResultDO();

		try {
			Map<String, Object> sotpMap = new HashMap<String, Object>();
			String url = json.getString("sotp");
			sotpMap = sotpAuthUtil.getUrlParams(url);

			String phoneNum = (String) sotpMap.get("phoneNum");
			MemberDO member = new MemberDO();
			member.setPhone(phoneNum);

			MemberDO user = memberDAO.loginMember(member);
			long userId = user.getId();
			sotpMap.put("userId", String.valueOf(userId));

			String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
			if (appKey == null) {
				data.append("\"\"");
				result.setErrMsg("appId not match");
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			SotpAuthDO serviceDO = new SotpAuthDO();
			serviceDO = sotpDao.selectService();
			sotpGet.append("http://" + serviceDO.getIp() + ":" + serviceDO.getPost() + "");
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
				return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
			}
			boolean falg = sotpAuthUtil.verifySotp(jsonsotp, jsonsotp.getString("appId"), appKey);
			if (!falg) {
				data.append("\"\"");
				result.setErrMsg("sign not match");
				return utilJson(json.getInt("type"), ErrorInfo.SignMatch, data.toString(), result.getErrMsg());
			}
			
			JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
			if (jsonsotp.getInt("status") == 0) {
				status = 0;
				if (sotpJson.containsKey("data")) {
					data.append("\"" + sotpJson.getString("data") + "\"");
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
			log.error("请求失败");
			log.error(e);
			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
		}
		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
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
	public String utilJson(int type, int status, String data, String errorMsg) {
		StringBuffer jsondata = new StringBuffer();
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String serverTime = dateFormat.format(now);
		jsondata.append("{\"type\":" + type + ",\"status\":" + status + ",\"message\":{");
		jsondata.append("\"data\":" + data + "");
		jsondata.append(",\"errorMsg\":\"" + errorMsg + "\"},");
		jsondata.append("\"serverTime\":\"" + serverTime + "\"}");
		return jsondata.toString();
	}

}
