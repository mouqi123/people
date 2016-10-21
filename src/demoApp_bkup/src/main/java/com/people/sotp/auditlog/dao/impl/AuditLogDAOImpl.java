package com.people.sotp.auditlog.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.auditlog.dao.AuditLogDAO;
import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.AuditLogDO;

/**
 * 日志管理持久化实现类
 * 
 * @author tianchk
 */
@Repository
public class AuditLogDAOImpl implements AuditLogDAO {
	@Resource
	private BaseDao baseDao;

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	@Override
	public int insertAuditLogo(AuditLogDO logDO) throws SQLException {
		return baseDao.insert("AuditLog.addAuditLog", logDO);
	}

	@Override
	public Integer queryAuditLogListCount(AuditLogDO logDO) throws SQLException {
		return baseDao.queryTotal("AuditLog.selectCountAuditLog", logDO);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AuditLogDO> queryAuditLogList(AuditLogDO logDO) throws SQLException {
		return (List<AuditLogDO>) baseDao.query("AuditLog.selectAuditLogList", logDO);
	}

	@Override
	public AuditLogDO getAuditLogInfo(AuditLogDO log) throws SQLException {
		return (AuditLogDO) baseDao.queryOne("AuditLog.getAuditLogInfo", log);
	}

}
