package com.people.sotp.payment.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.AuditLogDO;
import com.people.sotp.dataobject.DealLogDO;
import com.people.sotp.payment.dao.LogDAO;

@Repository
public class LogDAOImpl implements LogDAO {
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
	public Integer queryLogListCount(AuditLogDO userDO) throws SQLException {
		return baseDao.queryTotal("AuditLog.selectCountAuditLog", userDO);
	}

	public Integer queryDealLogListCount(DealLogDO userDO) throws SQLException {
		return baseDao.queryTotal("AuditLog.selectCountDealLog", userDO);
	}
	
	/**
	 * 获取用户列表
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<AuditLogDO> queryLogList(AuditLogDO userDO) throws SQLException {
		return (List<AuditLogDO>) baseDao.query("AuditLog.selectAuditLogList", userDO);
	}
	@SuppressWarnings("unchecked")
	public List<DealLogDO> queryDealLogList(DealLogDO userDO) throws SQLException {
		return (List<DealLogDO>) baseDao.query("AuditLog.selectDealLogList", userDO);
	}
	
	@Override
	public int insertDealLog(DealLogDO logDO) throws SQLException {
		return baseDao.insert("AuditLog.addDealLog", logDO);
	}
	
}
