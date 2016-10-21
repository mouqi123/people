package com.people.sotp.payment.dao.impl;

import java.sql.SQLException;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.MasterDO;
import com.people.sotp.payment.dao.LoginDAO;

@Repository
public class LoginDAOImpl implements LoginDAO {
	@Resource
	private BaseDao baseDao;

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	@Override
	public MasterDO getManagerInfo(MasterDO masterDO) throws SQLException {
		MasterDO managerDO = new MasterDO();
		managerDO = (MasterDO) baseDao.queryOne("Master.selectOneByName", masterDO);
		return managerDO;
	}

	@Override
	public void updateManagerInfo(MasterDO masterDO) throws SQLException {
		baseDao.update("Master.updateLoginFailCount", masterDO);
	}

	@Override
	public void cleanManagerInfo(MasterDO masterDO) throws SQLException {
		baseDao.update("Master.cleanLoginFailCount", masterDO);
	}
}
