package com.people.sotp.urlcontrollers;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.people.sotp.commons.base.BaseController;
import com.people.sotp.commons.base.DataGrid;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.commons.util.Base64;
import com.people.sotp.dataobject.ApplyDO;
import com.people.sotp.dataobject.AuthDO;
import com.people.sotp.dataobject.CardDO;
import com.people.sotp.dataobject.DealTacticsDO;
import com.people.sotp.dataobject.MemberDO;
import com.people.sotp.dataobject.SotpAuthDO;
import com.people.sotp.payment.dao.DealTacticsDAO;
import com.people.sotp.payment.service.ApplyService;
import com.people.sotp.payment.service.LogService;
import com.people.sotp.payment.service.MemberService;

import net.sf.json.JSONObject;

@Service
public class SotpService extends BaseController {

	@Resource
	private SotpDao sotpDao;
	@Resource
	private DealTacticsDAO dealTacticsDAO;
	@Resource
	private MemberService memberService;
	@Resource
	private ApplyService ApplyService;
	@Autowired
	private LogService logService;

	private static Log log = LogFactory.getLog(SotpService.class);

	public static String project = "portal";

	/**
	 * 登录认证
	 * 
	 * @param phone
	 * @param pwd
	 * @return
	 */
	public String login(JSONObject json) {
		MemberDO userDO = new MemberDO();
		userDO.setPhone(json.getString("phoneNum"));
		userDO.setUserPwd(json.getString("pwd"));
		ResultDO resultDO = memberService.loginMember(userDO);
		StringBuffer jsondata = new StringBuffer();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("phone", json.getString("phoneNum"));
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String serverTime = dateFormat.format(now);
		if (resultDO.isSuccess()) {
			DataGrid grid = (DataGrid) resultDO.getModel("masterdata");
			List list = grid.getRows();
			AuthDO authDO = (AuthDO) list.get(0);
			CardDO card = new CardDO();
			try {
				card = sotpDao.onepayConsume(map);
			} catch (Exception e) {
				log.error("erroe===" + e);
			}
			String data = "{\"login\":" + authDO.getLogin() + ",\"epay\":" + authDO.getePay() + ",\"onepay\":"
					+ authDO.getOnePay() + ",\"onepaystatus\":" + card.getOneKeyStatus() + ",\"card\":\""
					+ card.getCardNumber() + "\"}";
			// 更新数据库登录时间
			MemberDO member = new MemberDO();
			member.setId(Long.parseLong(authDO.getMemberId()));
			member.setLoginTime(serverTime);
			memberService.updateMember(member);
			jsondata.append("{\"type\":" + json.getInt("type") + ",\"status\":0,\"message\":{");
			jsondata.append("\"data\":" + data + "");
			jsondata.append(",\"errorMsg\":\"\"},");
			jsondata.append("\"serverTime\":\"" + serverTime + "\"}");
			log.info("terminal======" + json.toString());
			log.info("Pc======" + jsondata.toString());
			return jsondata.toString();
		} else {
			jsondata.append("{\"type\":" + json.getInt("type") + ",\"status\":-1,\"message\":{");
			jsondata.append("\"data\":{}");
			jsondata.append(",\"errorMsg\":\"" + resultDO.getErrMsg() + "\"},");
			jsondata.append("\"serverTime\":\"" + serverTime + "\"}");
			return jsondata.toString();
		}

	}

	/**
	 * 增加银行卡
	 * 
	 * @param type
	 * @param cardNumber
	 * @param accountId
	 */
	public void addCard(int type, String cardNumber, String accountId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cardNumber", cardNumber);
		map.put("accountId", accountId);
	}

	/**
	 * 开通一键支付业务
	 * 
	 * @param type
	 * @param phoneNum
	 * @param operate
	 * @param card
	 * @param cardPwd
	 * @param sms
	 * @param payPwd
	 * @param printWriter
	 */
	public String openOnePay(JSONObject json) {
		ResultDO result = new ResultDO();
		StringBuffer data = new StringBuffer();
		Map<String, Object> map = new HashMap<String, Object>();
		int status = -1;
		map.put("phoneNum", json.getString("phoneNum"));
		map.put("operate", json.getString("operate"));
		map.put("card", json.getString("card"));
		map.put("cardPwd", json.getString("cardPwd"));
		map.put("sms", json.getString("sms"));
		try {
			if (json.getInt("operate") == 1) {
				map.put("cardPwd", json.getString("cardPwd"));
				map.put("payPwd", json.getString("payPwd"));
			}
			status = sotpDao.openOnePay(map);
			if (status != 0) {
				status = 0;
			}
			status = 0;
			data.append("\"\"");
			log.info("terminal======" + json.toString());
			log.info("Pc======" + utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg()));

			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());

		} catch (Exception e) {
			result.setErrMsg("开通或关闭一键支付失败");
			log.error("开通或关闭一键支付失败");
			log.error(e);
			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
		}

	}

	/**
	 * 设置业务的插件认证开关
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String businessOperate(JSONObject json) {
		ResultDO result = new ResultDO();
		StringBuffer data = new StringBuffer();
		String phoneNum = json.getString("phoneNum");
		String name = json.getString("name");
		int operate = json.getInt("operate");
		int status = -1;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("phoneNum", "'" + phoneNum + "'");
		map.put("name", name);
		map.put("operate", operate);
		try {
			status = sotpDao.businessOperate(map);
			if (status != 0) {
				status = 0;
				data.append("\"\"");
			}
		} catch (Exception e) {
			log.error("设置业务的插件认证开关失败");
			log.error(e);
		}
		log.info("terminal======" + json.toString());
		log.info("Pc======" + utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg()));

		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
	}

	/**
	 * 获取业务的插件认证列表
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String getBindlist(JSONObject json, PrintWriter printWriter) {
		ResultDO result = new ResultDO();
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuffer data = new StringBuffer();
		int status = -1;
		String phoneNum = json.getString("phoneNum");
		map.put("phoneNum", phoneNum);
		try {
			List<AuthDO> list = sotpDao.getBindlist(map);
			AuthDO authDO = (AuthDO) list.get(0);
			data.append("{\"login\":" + authDO.getLogin() + ",\"epay\":" + authDO.getePay() + ",\"onepay\":"
					+ authDO.getOnePay() + "}");
			status = 0;
		} catch (Exception e) {
			data.append("\"\"");
			result.setErrMsg("获取业务的插件认证列表失败");
			log.error("获取业务的插件认证列表失败");
			log.error(e);
		}
		log.info("terminal======" + json.toString());
		log.info("pc======" + utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg()));
		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
	}

	/**
	 * 支付请求
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String onepayConsume(JSONObject json) {
		ResultDO result = new ResultDO();
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuffer data = new StringBuffer();
		StringBuffer sotpGet = new StringBuffer();
		int status = -1;
		String phoneNum = json.getString("phoneNum");
		String tradeNum = json.getString("tradeNum");
		String tradeMoney = json.getString("tradeMoney");
		String card = json.getString("card");
		String payPwd = json.getString("payPwd");
		map.put("phone", phoneNum);
		map.put("tradeNum", tradeNum);
		map.put("tradeMoney", tradeMoney);
		map.put("cardNumber", card);
		map.put("payPwd", payPwd);

		try {
				
			if (!json.getString("sotp").equals("")) {
				String url = json.getString("sotp");
				url = url.replaceAll("(\r\n|\r|\n|\n\r)", "");
				Map<String, Object> sotpMap = new HashMap<String, Object>();
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
				JSONObject jsonsotp = JSONObject.fromObject(info);
				appKey = getAppKey(jsonsotp.getString("appId"));
				if (appKey == null) {
					data.append("\"\"");
					result.setErrMsg("sotp appId not match");
					return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
				}
				boolean falg = sotpAuthUtil.verifySotp(jsonsotp,jsonsotp.getString("appId"),appKey);
				if (!falg) {
					data.append("\"\"");
					result.setErrMsg("sign not match");
					return utilJson(json.getInt("type"), ErrorInfo.SignMatch, data.toString(), result.getErrMsg());
				}
				if (jsonsotp.getInt("status") == 0) {
					CardDO cardDO = sotpDao.onepayConsume(map);
					if (cardDO == null) {
						data.append("\"\"");
						result.setErrMsg("Bank card or password error");
						return utilJson(json.getInt("type"), ErrorInfo.CardAndPwd, data.toString(), result.getErrMsg());
					}
					BigDecimal num = new BigDecimal(tradeMoney);
					BigDecimal money = new BigDecimal(cardDO.getMoney());

					if (money.compareTo(num) == -1) {
						data.append("\"\"");
						result.setErrMsg("余额不足");
						return utilJson(json.getInt("type"), ErrorInfo.Balance, data.toString(), result.getErrMsg());
					} else {
						money = money.subtract(num);
						// map.clear();
						map.put("money", String.valueOf(money));
						sotpDao.openOnePay(map);
						data.append("\"\"");
						status = 0;
					}
					log.info("terminal======" + json.toString());
					if (status == 0) {
						DealTacticsDO deal = new DealTacticsDO();
						deal.setLastMoney(tradeMoney);
						dealTacticsDAO.updateDealTactics(deal);
						logService.insertDealLog(phoneNum, json.toString(), data.toString(), 0, null);
					} else {
						logService.insertDealLog(phoneNum, json.toString(), data.toString(), 0, null);
					}
				} else {
					JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
					result.setErrMsg(sotpJson.get("errorMsg").toString());
					data.append("\"\"");
					return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
				}
			} else {
				CardDO cardDO = sotpDao.onepayConsume(map);
				BigDecimal num = new BigDecimal(tradeMoney);
				BigDecimal money = new BigDecimal(cardDO.getMoney());

				if (money.compareTo(num) == -1) {
					data.append("\"\"");
					result.setErrMsg("余额不足");
					return utilJson(json.getInt("type"), ErrorInfo.Balance, data.toString(), result.getErrMsg());
				} else {
					money = money.subtract(num);
					// map.clear();
					map.put("money", String.valueOf(money));
					sotpDao.openOnePay(map);
					data.append("\"\"");
					status = 0;
				}
				log.info("terminal======" + json.toString());
				if (status == 0) {
					DealTacticsDO deal = new DealTacticsDO();
					deal.setLastMoney(tradeMoney);
					dealTacticsDAO.updateDealTactics(deal);
					logService.insertDealLog(phoneNum, json.toString(), data.toString(), 0, null);
				} else {
					logService.insertDealLog(phoneNum, json.toString(), data.toString(), 0, null);
				}

			}
		} catch (Exception e) {
			result.setErrMsg("支付请求失败");
			data.append("\"\"");
			log.error("支付请求失败");
			log.error(e);
			return utilJson(json.getInt("type"), -1, data.toString(), result.getErrMsg());
		}
		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
	}

	/**
	 * 获取e支付策略请求
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String epayPolicy(JSONObject json) {
		ResultDO result = new ResultDO();
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuffer data = new StringBuffer();
		int status = -1;
		StringBuffer sotpGet = new StringBuffer();
		String phoneNum = json.getString("phoneNum");
		String tradeMoney = json.getString("tradeMoney");
		map.put("phoneNum", phoneNum);
		map.put("tradeMoney", tradeMoney);
		try {
			DealTacticsDO TacticsDO = dealTacticsDAO.queryDealTacticsOne(map);
			BigDecimal num = new BigDecimal(tradeMoney);
			BigDecimal money = new BigDecimal(TacticsDO.getLimitMoney());
			BigDecimal lastMoner = new BigDecimal(TacticsDO.getLastMoney());
			BigDecimal trade = new BigDecimal(tradeMoney);
			if (num.compareTo(money) == 1 || lastMoner.compareTo(trade) == 0) {
				status = 0;
				data.append("{\"policy\":2,\"challenge\":\"\"}");
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
				log.info(sotpGet.toString());
				String info = sendRequest.sendGet(sotpGet.toString());
				
				log.info("terminal======" + json.toString());
				log.info("Pc======" + sotpGet.toString());
				log.info("sotp======" + info);
				
				JSONObject jsonsotp = JSONObject.fromObject(info);
				appKey = getAppKey(jsonsotp.getString("appId"));
				if (appKey == null) {
					data.append("\"\"");
					result.setErrMsg("sotp appId not match");
					return utilJson(json.getInt("type"), ErrorInfo.AppIdError, data.toString(), result.getErrMsg());
				}
				boolean falg = sotpAuthUtil.verifySotp(jsonsotp,jsonsotp.getString("appId"),appKey);
				if (!falg) {
					data.append("\"\"");
					result.setErrMsg("sign not match");
					return utilJson(json.getInt("type"), ErrorInfo.SignMatch, data.toString(), result.getErrMsg());
				}
				if (jsonsotp.getInt("status") != 0) {
					JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
					result.setErrMsg(sotpJson.get("errorMsg").toString());
					data.append("\"\"");
					status = jsonsotp.getInt("status");
				} else {
					status = 0;
					JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
					data.append("{\"policy\":1,\"challenge\":\"" + sotpJson.getString("data") + "\"}");
				}

			}
			
		} catch (Exception e) {
			data.append("\"\"");
			result.setErrMsg("获取e支付策略请求（输保护码 或 验证码）失败");
			log.error("获取e支付策略请求（输保护码 或 验证码）失败");
			log.error(e);
			return utilJson(json.getInt("type"), -1, data.toString(), result.getErrMsg());
		}
		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
	}

	/**
	 * 支付请求(e支付)
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String epayConsume(JSONObject json, PrintWriter printWriter) {
		ResultDO result = new ResultDO();
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuffer data = new StringBuffer();
		StringBuffer sotpGet = new StringBuffer();
		int status = -1;
		String phoneNum = json.getString("phoneNum");
		String tradeNum = json.getString("tradeNum");
		String tradeMoney = json.getString("tradeMoney");
		String card = json.getString("card");
		map.put("phone", phoneNum);
		map.put("tradeNum", tradeNum);
		map.put("tradeMoney", tradeMoney);
		map.put("cardNumber", card);

		
		try {
			StringBuffer shapara = new StringBuffer();
			shapara.append(tradeNum);
			shapara.append("|");
			shapara.append(tradeMoney);
			shapara.append("|");
			shapara.append(card);
		
			
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
			boolean falg = sotpAuthUtil.verifySotp(jsonsotp,jsonsotp.getString("appId"),appKey);
			if (!falg) {
				data.append("\"\"");
				result.setErrMsg("sign not match");
				return utilJson(json.getInt("type"), ErrorInfo.SignMatch, data.toString(), result.getErrMsg());
			}

			if (jsonsotp.getInt("status") == 0) {
				CardDO cardDO = sotpDao.onepayConsume(map);
				if (cardDO == null) {
					data.append("\"\"");
					result.setErrMsg("Bank card or password error");
					return utilJson(json.getInt("type"), ErrorInfo.CardAndPwd, data.toString(), result.getErrMsg());
				}
				BigDecimal num = new BigDecimal(tradeMoney);
				BigDecimal money = new BigDecimal(cardDO.getMoney());
				if (money.compareTo(num) == -1) {
					data.append("\"\"");
					result.setErrMsg("余额不足");
					return utilJson(json.getInt("type"), ErrorInfo.Balance, data.toString(), result.getErrMsg());
				} else {
					MemberDO memberDO = new MemberDO();
					memberDO.setPhone(phoneNum);
					MemberDO member = sotpDao.loginMember(memberDO);
					money = money.subtract(num);
					map.put("money", String.valueOf(money));
					sotpDao.openOnePay(map);
					DealTacticsDO deal = new DealTacticsDO();
					deal.setLastMoney(tradeMoney);
					deal.setMemberId(member.getId());
					dealTacticsDAO.updateDealTactics(deal);
					data.append("\"\"");
					status = 0;
				}
			} else {
				data.append("\"\"");
				JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
				result.setErrMsg(sotpJson.getString("errorMsg"));
				status = jsonsotp.getInt("status");
			}
			if (status == 0) {
				logService.insertDealLog(phoneNum, json.toString(), info, 0, null);
			} else {
				logService.insertDealLog(phoneNum, json.toString(), info, 1, null);
			}
		} catch (Exception e) {
			result.setErrMsg("支付失败");
			data.append("\"\"");
			log.error("支付失败");
			log.error(e);
			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
		}
		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
	}

	/**
	 * 登录SOTP认证
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String loginSotpverify(JSONObject json, PrintWriter printWriter) {
		ResultDO result = new ResultDO();
		StringBuffer data = new StringBuffer();
		StringBuffer sotpGet = new StringBuffer();
		int status = -1;
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
			boolean falg = sotpAuthUtil.verifySotp(jsonsotp,jsonsotp.getString("appId"),appKey);
			if (!falg) {
				data.append("\"\"");
				result.setErrMsg("sign not match");
				return utilJson(json.getInt("type"), ErrorInfo.SignMatch, data.toString(), result.getErrMsg());
			}

			
			
			if (jsonsotp.getInt("status") == 0) {
				status = 0;
				data.append("\"\"");
			} else {
				data.append("\"\"");
				JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
				result.setErrMsg(sotpJson.getString("errorMsg"));
				status = jsonsotp.getInt("status");
			}
		} catch (Exception e) {
			data.append("\"\"");
			result.setErrMsg("登录SOTP认证失败");
			log.error("登录SOTP认证失败");
			log.error(e);
			return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
		}
		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());
	}

	/**
	 * 设置（或获取）支付限额
	 * 
	 * @param json
	 * @param printWriter
	 */
	public String operateLimit(JSONObject json, PrintWriter printWriter) {
		ResultDO result = new ResultDO();
		String phoneNum = json.getString("phoneNum");
		int action = json.getInt("action");
		String limit = json.getString("limit");
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuffer data = new StringBuffer();
		int status = -1;
		map.put("phoneNum", phoneNum);
		if (action == 1) {
			try {
				String TacticsDO = sotpDao.epayPolicy(map);
				data.append("{\"limit\":\"" + TacticsDO + "\"}");
				status = 0;

			} catch (Exception e) {
				result.setErrMsg("获取支付限额失败");
				log.error("获取支付限额失败");
				log.error(e);
			}
		} else if (action == 2) {
			try {
				map.put("limitMoney", limit);
				status = sotpDao.updateoperateLimit(map);
				if (status == 0) {
					status = -1;
					result.setErrMsg("开通或关闭一键支付失败");
				} else {
					status = 0;
					data.append("\"\"");
				}
			} catch (Exception e) {
				result.setErrMsg("修改支付限额失败");
				log.error("修改支付限额失败");
				log.error(e);
				data.append("\"\"");
				return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());

			}
		}
		log.info("terminal======" + json.toString());
		log.info("Pc======" + utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg()));
		return utilJson(json.getInt("type"), status, data.toString(), result.getErrMsg());

	}

	/**
	 * 消费订单确认
	 * 
	 * @param json
	 * @param printWriter
	 * @return
	 */
	public String tradeDetail(JSONObject json, PrintWriter printWriter) {
		int status = 0;
		StringBuffer data = new StringBuffer();
		String tradeNum = "201233344";
		data.append("{\"tradeNum\":\"" + tradeNum + "\"}");
		log.info("terminal======" + json.toString());
		log.info("Pc======" + utilJson(json.getInt("type"), status, data.toString(), ""));
		return utilJson(json.getInt("type"), status, data.toString(), "");
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
		if (apply != null ) {
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
