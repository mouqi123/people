package com.people.sotp.payment.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.LoginLogDO;
import com.people.sotp.payment.dao.LoginLogDAO;

@Repository
public class LoginLogDAOImpl implements LoginLogDAO {
	@Resource
	private BaseDao baseDao;

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	/**
	 * 获取用户总数
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public Integer queryLoginLogListCount(LoginLogDO log) throws SQLException {
		return baseDao.queryTotal("LoginLog.selectLoginLogCount", log);
	}

	/**
	 * 获取用户列表,分页
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<LoginLogDO> queryLoginLogList(LoginLogDO log) throws SQLException {
		return (List<LoginLogDO>) baseDao.query("LoginLog.selectLoginLogList", log);
	}


	/**
	 * 新增用户
	 */
	public int addLoginLog(LoginLogDO log) throws SQLException {

		return baseDao.insert("LoginLog.addLoginLog", log);
	}


}
