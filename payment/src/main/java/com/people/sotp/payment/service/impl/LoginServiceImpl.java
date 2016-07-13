package com.people.sotp.payment.service.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.commons.util.CryptUtil;
import com.people.sotp.dataobject.MasterDO;
import com.people.sotp.payment.dao.LoginDAO;
import com.people.sotp.payment.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService {
	@Resource
	private LoginDAO loginDAO;

	private static Log log = LogFactory.getLog(LoginServiceImpl.class);

	public ResultDO userLogin(MasterDO masterDO, String contextPath) {
		ResultDO result = new ResultDO();
		try {
			MasterDO master = loginDAO.getManagerInfo(masterDO);
			if (master == null) {
				result.setErrMsg("用户[" + masterDO.getUserName() + "]不存在！");
				log.error("登录失败：用户[" + masterDO.getUserName() + "]不存在！");
				return result;
			}

			// 帐号密码是否正确

			if (!master.getPassword().equals(CryptUtil.encrypt(masterDO.getPassword()))) {
				int count = master.getFailCount() + 1;
				master.setFailCount(count);
				loginDAO.updateManagerInfo(master);
				result.setErrMsg("密码不正确！");
				log.error("登录失败：密码不正确！" + masterDO.getPassword());
				return result;
			}
			if (master.getFailCount() >= 5) {
				// 非超级管理员登录失败五次 ，禁用帐号
				master.setStatus("2");
				loginDAO.updateManagerInfo(master);
				result.setErrMsg("连续登录失败5次，该用户已被禁用");
			}

			result.setModel("master", master);
		} catch (Exception e) {
			result.setErrMsg("用户登录验证异常,请联系管理员！");
			log.error("登录失败：用户登录验证异常,请联系管理员！");
			e.printStackTrace();
		}
		return result;
	}
}
