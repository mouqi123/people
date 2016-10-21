package com.people.sotp.interfaces.service;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.people.sotp.commons.base.BaseController;
import com.people.sotp.commons.base.GlobalParam;
import com.people.sotp.dataobject.IssuerDO;
import com.people.sotp.dataobject.IssuerInfo;
import com.people.sotp.dataobject.MemberDO;
import com.people.sotp.payment.dao.impl.IssuerDAO;
import com.people.sotp.payment.dao.impl.MemberDAOImpl;

@Component
public class IssuerBusinessService extends BaseController {

	@Resource
	private IssuerDAO issuerDAO;

	@Resource
	private MemberDAOImpl memberDAO;

	private static Log log = LogFactory.getLog(IssuerBusinessService.class);

	public String addIssuerAccount(IssuerInfo issuerInfo, HttpServletRequest request) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		jsondata.append("{}");
		int status = 0;

		IssuerDO issuer = new IssuerDO();
		MemberDO member = (MemberDO) request.getSession().getAttribute("member");
		if (member == null) {
			errorMsg.append("please login!");
			return utilJson(issuerInfo.getService(), GlobalParam.NOT_LOGIN, jsondata.toString(), errorMsg.toString());
		}
		issuer.setAccount(issuerInfo.getAccount());
		issuer.setSecret(issuerInfo.getSecret());
		issuer.setIssuer(issuerInfo.getIssuer());
		issuer.setUserId(member.getId());
		log.debug("issuer imformation--------------" + issuer);

		try {
			List<IssuerDO> issuers = issuerDAO.queryIssuerList(member.getId());
			// 判断账户是否已经存在，若存在就不用插入数据库了。
			if (issuers != null || issuers.size() != 0) {
				for (IssuerDO e : issuers) {
					if (e.getAccount().equals(issuer.getAccount()) && e.getIssuer().equals(issuer.getIssuer())) {
						errorMsg.append("{}");
						return utilJson(issuerInfo.getService(), status, jsondata.toString(), errorMsg.toString());
					}
				}
			}
			issuerDAO.addIssuer(issuer);
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg.append("the server can't insert your issuer account");
			status = GlobalParam.ParameterError;
		}
		return utilJson(issuerInfo.getService(), status, jsondata.toString(), errorMsg.toString());
	}

	public String getIssuerAccounts(IssuerInfo issuerInfo, HttpServletRequest request) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		int status = 0;

		MemberDO member = (MemberDO) request.getSession().getAttribute("member");
		if (member == null) {
			jsondata.append("[]");
			errorMsg.append("please login!");
			return utilJson(issuerInfo.getService(), GlobalParam.NOT_LOGIN, jsondata.toString(), errorMsg.toString());
		}

		List<IssuerDO> issuers = null;

		try {
			issuers = issuerDAO.queryIssuerList(member.getId());
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg.append("server error");
			status = GlobalParam.ParameterError;
		}
		if (issuers == null || issuers.size() == 0) {
			jsondata.append("[]");
		} else {
			String[] excludeProperties = {"userId"};
			jsondata.append(" " + writeJsonByFilter(issuers, null, excludeProperties) + " ");
		}
		return utilJson(issuerInfo.getService(), status, jsondata.toString(), errorMsg.toString());
	}

	public String unbindAccount(IssuerInfo issuerInfo, HttpServletRequest request) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		int status = 0;
		jsondata.append("{}");

		MemberDO member = (MemberDO) request.getSession().getAttribute("member");
		if (member == null) {
			errorMsg.append("please login!");
			return utilJson(issuerInfo.getService(), GlobalParam.NOT_LOGIN, jsondata.toString(), errorMsg.toString());
		}
		IssuerDO issuerDO = new IssuerDO();

		String account = issuerInfo.getAccount();
		String issuer = issuerInfo.getIssuer();
		issuerDO.setAccount(account);
		issuerDO.setIssuer(issuer);
		issuerDO.setUserId(member.getId());
		try {
			issuerDAO.deleteIssuer(issuerDO);
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg.append("the server can't delete your issuer account");
			status = GlobalParam.ParameterError;
		}
		return utilJson(issuerInfo.getService(), status, jsondata.toString(), errorMsg.toString());
	}

	public String utilJson(String type, int status, String data, String errorMsg) {
		StringBuilder jsondata = new StringBuilder();
		jsondata.append("{\"service\":\"" + type + "\",\"status\":" + status + ",");
		jsondata.append("\"data\":" + data + "");
		jsondata.append(",\"errorMsg\":\"" + errorMsg + "\"}");
		return jsondata.toString();
	}
}
