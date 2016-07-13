package com.people.sotp.payment.dao;

import java.sql.SQLException;
import java.util.List;

import com.people.sotp.dataobject.AuditLogDO;
import com.people.sotp.dataobject.DealLogDO;

public interface LogDAO {

	/**
	 * 获取列表总数
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public Integer queryLogListCount(AuditLogDO model) throws SQLException;

	
	public Integer queryDealLogListCount(DealLogDO model) throws SQLException;
	/**
	 * 获取列表
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public List<AuditLogDO> queryLogList(AuditLogDO model) throws SQLException;
	public List<DealLogDO> queryDealLogList(DealLogDO model) throws SQLException;
	
	public int insertDealLog(DealLogDO logDO) throws SQLException;

}
