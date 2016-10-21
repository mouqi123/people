package com.people.sotp.demoApp.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.people.sotp.commons.base.BaseController;
import com.people.sotp.commons.base.GlobalParam;
import com.people.sotp.commons.util.RandomValue;
import com.people.sotp.commons.util.sendRequest;
import com.people.sotp.commons.util.sotpAuthUtil;
import com.people.sotp.dataobject.ApplyDO;
import com.people.sotp.dataobject.LoginLogDO;
import com.people.sotp.dataobject.SotpAuthDO;
import com.people.sotp.dataobject.TransferLogDO;
import com.people.sotp.dataobject.UserDO;
import com.people.sotp.demoApp.dao.DemoAppDAO;

import net.sf.json.JSONObject;

@Service
public class DemoAppService extends BaseController {

	@Resource
	private DemoAppDAO demoAppDAo;

	private static Log log = LogFactory.getLog(DemoAppDAO.class);

	public String registerUser(JSONObject json) {
		UserDO user = new UserDO();
		user.setPhoneNum(json.getString("username"));
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		jsondata.append("{}");
		int status = -1;
		try {

			user = demoAppDAo.queryUserOne(user);
			if (user != null) {
				errorMsg.append("账户已存在");
				return utilJson(json.getString("service"), GlobalParam.UserExist, jsondata.toString(),
						errorMsg.toString());
			} else {
				UserDO userDo = new UserDO();
				Map map = RandomValue.getAddress();
				String name = (String) map.get("name");
				userDo.setCardPwd("111111");
				userDo.setUserName(name);
				userDo.setIdentityNum(map.get("tel").toString());
				userDo.setCardNum(map.get("tel").toString());
				userDo.setBalance("1000000");
				userDo.setPhoneNum(json.getString("username"));
				userDo.setPwd(json.getString("pwd"));
				demoAppDAo.addUser(userDo);
				status = 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e);
		}

		return utilJson(json.getString("service"), status, jsondata.toString(), errorMsg.toString());
	}

	public String login(JSONObject json) {
		UserDO user = new UserDO();
		user.setPhoneNum(json.getString("username"));
		user.setPwd(json.getString("pwd"));
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		jsondata.append("{}");
		int status = -1;
		try {
			user = demoAppDAo.queryUserOne(user);
			if (user != null && !"".equals(user.getPhoneNum())) {
				status = 0;
			} else {
				errorMsg.append("用户名密码错误");
				return utilJson(json.getString("service"), GlobalParam.UserAndPwd, jsondata.toString(),
						errorMsg.toString());
			}
			String devinfo = json.getString("devinfo");
			JSONObject jsonsotp = JSONObject.fromObject(devinfo);
			LoginLogDO loginlog = new LoginLogDO();
			loginlog.setUserName(json.getString("username"));
			loginlog.setAddress(jsonsotp.getString("address"));
			loginlog.setPhoneModel(jsonsotp.getString("phoneModel"));
			loginlog.setLoginTime(jsonsotp.getString("loginTime"));
			loginlog.setStatus(status);
			demoAppDAo.addLoginLog(loginlog);

		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e);
		}
		return utilJson(json.getString("service"), status, jsondata.toString(), errorMsg.toString());
	}

	public String transfer(JSONObject json) {

		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		int status = -1;

		StringBuffer sotpGet = new StringBuffer();

		UserDO userDO = new UserDO();
		userDO.setPhoneNum(json.getString("username"));
		userDO.setCardPwd(json.getString("cardPwd"));

		try {
			userDO = demoAppDAo.queryUserOne(userDO);
			if (userDO != null) {
				errorMsg.append("支付密码不正确");
				return utilJson(json.getString("service"), GlobalParam.UserExist, jsondata.toString(),
						errorMsg.toString());
			}
			String transInfo = json.getString("transInfo");
			JSONObject jsonsotp = JSONObject.fromObject(transInfo);

			String sotpInfo = json.getString("sotpInfo");
			sotpInfo = sotpInfo.replaceAll("(\r\n|\r|\n|\n\r)", "");
			Map<String, Object> sotpMap = new HashMap<String, Object>();
			sotpMap = sotpAuthUtil.getUrlParams(sotpInfo);

			if (json.containsKey("businessName")) {
				sotpMap.put("businessName", json.getString("businessName"));
			}

			String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
			if (appKey == null) {
				jsondata.append("{}");
				return utilJson(json.getString("service"), GlobalParam.AppIdError, jsondata.toString(),
						"appId not match");
			}

			SotpAuthDO service = new SotpAuthDO();
			try {
				service = demoAppDAo.selectService();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			sotpGet.append("http://" + service.getIp() + ":" + service.getPost() + "");
			sotpGet.append("/" + SotpService.project + "");

			String sotpAuth = sotpAuthUtil.genRequestSotpAuthMsg(sotpMap, sotpInfo, appKey);

			// 拼接交易信息
			sotpAuth = sotpAuth + "&payee=" + jsonsotp.getString("peerCount") + "&price="
					+ jsonsotp.getString("transMoney") + "" + "&action=" + json.getString("service");

			log.info("请求sotp url---------------------------" + sotpGet);
			log.info("请求sotp 参数---------------------------" + sotpAuth);

			String info = sendRequest.sendPost(sotpGet.toString(), sotpAuth);
			log.info("terminal======" + json.toString());
			log.info("Pc======" + sotpGet.toString() + "?" + sotpAuth);
			log.info("Sotp======" + info);

			JSONObject jsonAuth = JSONObject.fromObject(info);

			appKey = getAppKey(jsonAuth.getString("appId"));
			if (appKey == null) {
				jsondata.append("{}");
				return utilJson(json.getString("service"), GlobalParam.AppIdError, jsondata.toString(),
						"appId not match");
			}
			boolean falg = sotpAuthUtil.verifySotp(jsonAuth, jsonAuth.getString("appId"), appKey);
			if (!falg) {
				jsondata.append("{}");
				return utilJson(json.getString("service"), GlobalParam.SignMatch, jsondata.toString(),
						"sign not match");
			}

			if (jsonAuth.getInt("status") == 0) {

				UserDO user = new UserDO();

				user.setPhoneNum(json.getString("username"));
				user = demoAppDAo.queryUserOne(user);
				BigDecimal balance = new BigDecimal(user.getBalance());
				BigDecimal transMoney = new BigDecimal(jsonsotp.getString("transMoney"));
				if (balance.compareTo(transMoney) == -1) {
					errorMsg.append("对不起，您的余额不足");
					jsondata.append("{}");
					return utilJson(json.getString("service"), GlobalParam.Balance, jsondata.toString(),
							errorMsg.toString());
				} else {
					balance = balance.subtract(transMoney);
					user.setBalance(balance.toString());
					demoAppDAo.updateUser(user);
					status = 0;

				}

				TransferLogDO transferLog = new TransferLogDO();
				transferLog.setUserName(json.getString("username"));
				transferLog.setPeerName(jsonsotp.getString("peerName"));
				transferLog.setPeerCount(jsonsotp.getString("peerCount"));
				transferLog.setTransMoney(jsonsotp.getString("transMoney"));
				transferLog.setTransferTime(jsonsotp.getString("peerTime"));
				transferLog.setStatus(status);
				transferLog.setType(2);
				transferLog.setIpAddress(jsonsotp.getString("IpAddress"));
				transferLog.setPhoneModel(jsonsotp.getString("phoneModel"));

				demoAppDAo.addTransferLog(transferLog);
				jsondata.append("{}");

			} else {
				status = jsonAuth.getInt("status");
				jsondata.append("{}");
				JSONObject ageJson = (JSONObject) jsonAuth.get("message");
				errorMsg.append(ageJson.getString("errorMsg"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e);
		}

		return utilJson(json.getString("service"), status, jsondata.toString(), errorMsg.toString());
	}

	public String deal(JSONObject json) {

		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		int status = -1;

		StringBuffer sotpGet = new StringBuffer();
		
		UserDO userDO = new UserDO();
		userDO.setPhoneNum(json.getString("username"));
		userDO.setCardPwd(json.getString("cardPwd"));

		try {
			
			userDO = demoAppDAo.queryUserOne(userDO);
			if (userDO != null) {
				errorMsg.append("支付密码不正确");
				return utilJson(json.getString("service"), GlobalParam.UserExist, jsondata.toString(),
						errorMsg.toString());
			}
			String dealInfo = json.getString("dealInfo");
			JSONObject dealjson = JSONObject.fromObject(dealInfo);

			String sotpInfo = json.getString("sotpInfo");
			sotpInfo = sotpInfo.replaceAll("(\r\n|\r|\n|\n\r)", "");
			Map<String, Object> sotpMap = new HashMap<String, Object>();
			sotpMap = sotpAuthUtil.getUrlParams(sotpInfo);

			if (json.containsKey("businessName")) {
				sotpMap.put("businessName", json.getString("businessName"));
			}

			String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
			if (appKey == null) {
				jsondata.append("{}");
				return utilJson(json.getString("service"), GlobalParam.AppIdError, jsondata.toString(),
						"appId not match");
			}

			SotpAuthDO service = new SotpAuthDO();
			try {
				service = demoAppDAo.selectService();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			sotpGet.append("http://" + service.getIp() + ":" + service.getPost() + "");
			sotpGet.append("/" + SotpService.project + "");

			String sotpAuth = sotpAuthUtil.genRequestSotpAuthMsg(sotpMap, sotpInfo, appKey);

			// 拼接交易信息
			sotpAuth = sotpAuth + "&payee=" + dealjson.getString("peerCount") + "&price="
					+ dealjson.getString("tradeMoney") + "" + "&action=pay";

			String info = sendRequest.sendPost(sotpGet.toString(), sotpAuth);
			log.info("terminal======" + json.toString());
			log.info("Pc======" + sotpGet.toString() + "?" + sotpAuth);
			log.info("Sotp======" + info);

			JSONObject jsonAuth = JSONObject.fromObject(info);

			appKey = getAppKey(jsonAuth.getString("appId"));
			if (appKey == null) {
				jsondata.append("{}");
				return utilJson(json.getString("service"), GlobalParam.AppIdError, jsondata.toString(),
						"appId not match");
			}
			boolean falg = sotpAuthUtil.verifySotp(jsonAuth, jsonAuth.getString("appId"), appKey);
			if (!falg) {
				jsondata.append("{}");
				return utilJson(json.getString("service"), GlobalParam.SignMatch, jsondata.toString(),
						"sign not match");
			}

			JSONObject sotpJson = (JSONObject) jsonAuth.get("message");
			if (jsonAuth.getInt("status") == 0) {

				UserDO user = new UserDO();

				user.setPhoneNum(json.getString("username"));
				user = demoAppDAo.queryUserOne(user);
				BigDecimal balance = new BigDecimal(user.getBalance());
				BigDecimal transMoney = new BigDecimal(dealjson.getString("tradeMoney"));
				if (balance.compareTo(transMoney) == -1) {
					errorMsg.append("对不起，您的余额不足");
					jsondata.append("{}");
					return utilJson(json.getString("service"), GlobalParam.Balance, jsondata.toString(),
							errorMsg.toString());
				} else {
					balance = balance.subtract(transMoney);
					user.setBalance(balance.toString());
					demoAppDAo.updateUser(user);
					status = 0;
				}

				TransferLogDO transferLog = new TransferLogDO();
				transferLog.setTransMoney(dealjson.getString("tradeMoney"));
				transferLog.setTradeNum(dealjson.getString("tradeNum"));
				transferLog.setUserName(json.getString("username"));
				transferLog.setStatus(status);
				transferLog.setType(1);
				transferLog.setTransferTime(dealjson.getString("tradeTime"));
				transferLog.setIpAddress(dealjson.getString("IpAddress"));
				transferLog.setPhoneModel(dealjson.getString("phoneModel"));
				demoAppDAo.addTransferLog(transferLog);
				jsondata.append("{}");

			} else {
				jsondata.append("{}");
				status = jsonAuth.getInt("status");
				if (sotpJson.containsKey("errorMsg")) {
					errorMsg.append(sotpJson.getString("errorMsg"));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e);
		}

		return utilJson(json.getString("service"), status, jsondata.toString(), errorMsg.toString());
	}

	public String account(JSONObject json) {
		UserDO user = new UserDO();

		user.setPhoneNum(json.getString("username"));
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		int status = -1;

		try {
			user = demoAppDAo.queryUserOne(user);
			jsondata.append("{  \"username\":\"" + user.getUserName() + "\",\"identityNum\":\"" + user.getIdentityNum()
					+ "\",\"cardNum\":\"" + user.getCardNum() + "\",\"balance\":\"" + user.getBalance() + "\"}");
			status = 0;

		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e);
		}

		return utilJson(json.getString("service"), status, jsondata.toString(), errorMsg.toString());
	}

	public String loginLog(JSONObject json) {
		LoginLogDO login = new LoginLogDO();
		login.setUserName(json.getString("username"));

		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		int status = -1;

		try {
			List<LoginLogDO> list = demoAppDAo.queryLoginLogList(login);
			jsondata.append("  " + writeJson(list) + " ");
			status = 0;
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e);
		}

		int num = jsondata.toString().indexOf("{");
		if (num < 0) {
			jsondata.append("{}");
		}

		return utilJson(json.getString("service"), status, jsondata.toString(), errorMsg.toString());
	}

	public String transferLog(JSONObject json) {
		TransferLogDO login = new TransferLogDO();
		login.setUserName(json.getString("username"));

		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		int status = -1;

		try {
			List<TransferLogDO> list = demoAppDAo.queryTransferLogList(login);
			jsondata.append("  " + writeJson(list) + " ");
			status = 0;
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e);
		}

		return utilJson(json.getString("service"), status, jsondata.toString(), errorMsg.toString());
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
	public String utilJson(String type, int status, String data, String errorMsg) {
		StringBuffer jsondata = new StringBuffer();
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String serverTime = dateFormat.format(now);
		jsondata.append("{\"service\":\"" + type + "\",\"status\":" + status + ",");
		jsondata.append("\"data\":" + data + "");
		jsondata.append(",\"errorMsg\":\"" + errorMsg + "\",");
		jsondata.append("\"serverTime\":\"" + serverTime + "\"}");
		return jsondata.toString();
	}

}
