package com.people.sotp.auditlog.dao;

import java.sql.SQLException;
import java.util.List;

import com.people.sotp.dataobject.AuditLogDO;

public interface AuditLogDAO {

	/**
	 * 插入日志
	 * 
	 * @param AuditLogDO
	 * @return
	 * @throws SQLException
	 */
	public int insertAuditLogo(AuditLogDO logDO) throws SQLException;

	/**
	 * 获取符合查询条件的日志总数
	 * 
	 * @param log
	 * @return
	 * @throws SQLException
	 */
	public Integer queryAuditLogListCount(AuditLogDO log) throws SQLException;

	/**
	 * 获取日志列表
	 * 
	 * @param log
	 * @return
	 * @throws SQLException
	 */
	public List<AuditLogDO> queryAuditLogList(AuditLogDO log) throws SQLException;

	/**
	 * 获取日志详情
	 * 
	 * @param log
	 * @return
	 * @throws SQLException
	 */
	public AuditLogDO getAuditLogInfo(AuditLogDO log) throws SQLException;
}
