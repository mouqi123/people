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
import com.people.sotp.commons.util.Base64;
import com.people.sotp.commons.util.RandomValue;
import com.people.sotp.commons.util.sendRequest;
import com.people.sotp.commons.util.sotpAuthUtil;
import com.people.sotp.dataobject.ApplyDO;
import com.people.sotp.dataobject.Card;
import com.people.sotp.dataobject.LoginLogDO;
import com.people.sotp.dataobject.SotpAuthDO;
import com.people.sotp.dataobject.TransferLogDO;
import com.people.sotp.dataobject.UserDO;
import com.people.sotp.demoApp.dao.DemoAppDAO;
import com.people.sotp.payment.dao.impl.CardDAO;

import net.sf.json.JSONObject;

@Service
public class DemoAppService extends BaseController {

	@Resource
	private DemoAppDAO demoAppDAo;

	@Resource
	private CardDAO cardDAO;

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
				userDo.setUserName(name);
				userDo.setIdentityNum(map.get("tel").toString());
				userDo.setCardNum(map.get("tel").toString());
				userDo.setBalance("1000000");
				userDo.setPhoneNum(json.getString("username"));
				userDo.setPwd(json.getString("pwd"));
				demoAppDAo.addUser(userDo);
				userDo = demoAppDAo.queryUserOne(userDo);

				Card card1 = new Card();
				card1.setBalance("100000000");
				card1.setCardNumber("1111111" + json.getString("username") + "1");
				card1.setCardType(1); // 1表示信用卡
				card1.setUserId(userDo.getId());
				card1.setBank("中国银行");

				Card card2 = new Card();
				card2.setBalance("100000000");
				card2.setCardNumber("1111111" + json.getString("username") + "2");
				card2.setCardType(2); // 1表示信用卡
				card2.setUserId(userDo.getId());
				card2.setBank("工商银行");

				cardDAO.addCard(card1);
				cardDAO.addCard(card2);
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

		try {
			String transInfo = json.getString("transInfo");
			JSONObject jsonsotp = JSONObject.fromObject(transInfo);

			String sotpInfo = json.getString("sotpInfo");
			sotpInfo = sotpInfo.replaceAll("(\r\n|\r|\n|\n\r)", "");
			Map<String, Object> sotpMap = new HashMap<String, Object>();
			sotpMap = sotpAuthUtil.getUrlParams(sotpInfo);
			sotpMap.put("tradeInfo", Base64.encode(transInfo));

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
			sotpAuth += "&tradeInfo=" + Base64.encode(transInfo);

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

			UserDO user = new UserDO();
			user.setPhoneNum(json.getString("username"));
			user = demoAppDAo.queryUserOne(user);
			Card card = new Card();
			card.setCardNumber(jsonsotp.getString("payCard"));
			card.setUserId(user.getId());
			card = cardDAO.queryCardOne(card);

			if (jsonAuth.getInt("status") == 0) {
				BigDecimal balance = new BigDecimal(card.getBalance());
				BigDecimal transMoney = new BigDecimal(jsonsotp.getString("price"));
				if (balance.compareTo(transMoney) == -1) {
					errorMsg.append("对不起，您的余额不足");
					jsondata.append("{}");
					return utilJson(json.getString("service"), GlobalParam.Balance, jsondata.toString(),
							errorMsg.toString());
				} else {
					balance = balance.subtract(transMoney);
					card.setBalance(balance.toString());
					cardDAO.updateCard(card);

					Card peerCard = new Card();
					peerCard.setCardNumber(jsonsotp.getString("recCard"));
					peerCard = cardDAO.queryCardOne(peerCard);
					if (peerCard == null) {
						peerCard = new Card();
						peerCard.setCardNumber("123456");
						peerCard.setCardType(1);
						peerCard.setUserId(1);
						peerCard = cardDAO.queryCardOne(peerCard);
					}
					BigDecimal peerBalance = new BigDecimal(peerCard.getBalance());
					peerBalance = peerBalance.add(transMoney);
					peerCard.setBalance(peerBalance.toString());
					cardDAO.updateCard(peerCard);
					status = 0;

				}

				TransferLogDO transferLog = new TransferLogDO();
				transferLog.setUserName(json.getString("username"));
				transferLog.setPeerName(jsonsotp.getString("payee"));
				transferLog.setPeerCount(jsonsotp.getString("recCard"));
				transferLog.setTransMoney(jsonsotp.getString("price"));
				transferLog.setTransferTime(jsonsotp.getString("peerTime"));
				transferLog.setStatus(status);
				transferLog.setType(2);
				transferLog.setIpAddress(jsonsotp.getString("IpAddress"));
				transferLog.setPhoneModel(jsonsotp.getString("phoneModel"));
				transferLog.setPayCard(jsonsotp.getString("payCard"));

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

		try {
			String dealInfo = json.getString("dealInfo");
			JSONObject dealjson = JSONObject.fromObject(dealInfo);

			String sotpInfo = json.getString("sotpInfo");
			sotpInfo = sotpInfo.replaceAll("(\r\n|\r|\n|\n\r)", "");
			Map<String, Object> sotpMap = new HashMap<String, Object>();
			sotpMap = sotpAuthUtil.getUrlParams(sotpInfo);
			sotpMap.put("tradeInfo", Base64.encode(dealInfo));

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
			sotpAuth += "&tradeInfo=" + Base64.encode(dealInfo);

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

			UserDO user = new UserDO();
			user.setPhoneNum(json.getString("username"));
			user = demoAppDAo.queryUserOne(user);
			Card card = new Card();
			card.setCardNumber(dealjson.getString("payCard"));
			card.setUserId(user.getId());
			card = cardDAO.queryCardOne(card);

			if (jsonAuth.getInt("status") == 0) {
				BigDecimal balance = new BigDecimal(card.getBalance());
				BigDecimal transMoney = new BigDecimal(dealjson.getString("price"));
				if (balance.compareTo(transMoney) == -1) {
					errorMsg.append("对不起，您的余额不足");
					jsondata.append("{}");
					return utilJson(json.getString("service"), GlobalParam.Balance, jsondata.toString(),
							errorMsg.toString());
				} else {
					balance = balance.subtract(transMoney);
					card.setBalance(balance.toString());
					cardDAO.updateCard(card);

					Card peerCard = new Card();
					peerCard.setCardNumber(dealjson.getString("recCard"));
					peerCard = cardDAO.queryCardOne(peerCard);
					if (peerCard == null) {
						peerCard = new Card();
						peerCard.setCardNumber("123456");
						peerCard.setCardType(1);
						peerCard.setUserId(1);
						peerCard = cardDAO.queryCardOne(peerCard);
					}
					BigDecimal peerBalance = new BigDecimal(peerCard.getBalance());
					peerBalance = peerBalance.add(transMoney);
					peerCard.setBalance(peerBalance.toString());
					cardDAO.updateCard(peerCard);
					status = 0;
				}

				TransferLogDO transferLog = new TransferLogDO();
				transferLog.setTransMoney(dealjson.getString("price"));
				transferLog.setTradeNum(dealjson.getString("tradeNum"));
				transferLog.setUserName(json.getString("username"));
				transferLog.setStatus(status);
				transferLog.setType(1);
				transferLog.setTransferTime(dealjson.getString("tradeTime"));
				transferLog.setIpAddress(dealjson.getString("IpAddress"));
				transferLog.setPhoneModel(dealjson.getString("phoneModel"));
				transferLog.setPayCard(dealjson.getString("payCard"));
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

	public String cardList(JSONObject json) {
		UserDO user = new UserDO();

		user.setPhoneNum(json.getString("username"));
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		int status = -1;

		List<Card> cards = null;
		try {
			user = demoAppDAo.queryUserOne(user);
			cards = cardDAO.queryCardList(user.getId());
			String[] includeProperties = { "cardNumber", "cardType", "balance", "bank", "phoneNumber" };
			jsondata.append(" " + writeJsonByFilter(cards, includeProperties, null) + " ");
			status = 0;
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg.append("服务器端错误");
		}

		return utilJson(json.getString("service"), status, jsondata.toString(), errorMsg.toString());
	}

	public String addCard(JSONObject json) {
		UserDO user = new UserDO();

		user.setPhoneNum(json.getString("username"));
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		int status = -1;

		JSONObject cardInfo = JSONObject.fromObject(json.getString("cardInfo"));
		try {
			user = demoAppDAo.queryUserOne(user);
			Card card = new Card();
			card.setUserId(user.getId());
			card.setCardNumber(cardInfo.getString("cardNumber"));

			Card old = cardDAO.queryCardOne(card);
			if (old != null) {
				jsondata.append("{}");
				errorMsg.append("卡号已经绑定");
				return utilJson(json.getString("service"), GlobalParam.UserExist, jsondata.toString(),
						errorMsg.toString());
			}
			if (cardInfo.containsKey("cardType"))
				card.setCardType(Integer.valueOf(cardInfo.getString("cardType")));
			else
				card.setCardType(RandomValue.randomCardType());
			if (cardInfo.containsKey("bank"))
				card.setBank(cardInfo.getString("bank"));
			else
				card.setBank(RandomValue.randomBank());
			card.setBalance("100000000");
			if(cardInfo.containsKey("phoneNumber"))
				card.setPhoneNumber(cardInfo.getString("phoneNumber"));

			cardDAO.addCard(card);
			jsondata.append("{}");
			errorMsg.append("{}");
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg.append("服务器端错误");
		}

		status = 0;
		return utilJson(json.getString("service"), status, jsondata.toString(), errorMsg.toString());
	}

	public String unbindCard(JSONObject json) {
		UserDO user = new UserDO();

		user.setPhoneNum(json.getString("username"));
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		int status = -1;

		JSONObject cardInfo = JSONObject.fromObject(json.getString("cardInfo"));
		try {
			user = demoAppDAo.queryUserOne(user);
			Card card = new Card();
			card.setUserId(user.getId());
			card.setCardNumber(cardInfo.getString("cardNumber"));
			cardDAO.deleteCard(card);
		} catch (Exception e) {
			e.printStackTrace();
			jsondata.append("{}");
			errorMsg.append("服务器端错误");
			return utilJson(json.getString("service"), status, jsondata.toString(), errorMsg.toString());
		}
		jsondata.append("{}");
		errorMsg.append("{}");
		status = 0;
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
