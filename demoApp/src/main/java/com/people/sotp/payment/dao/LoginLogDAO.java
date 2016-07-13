package com.people.sotp.payment.dao;

import java.sql.SQLException;
import java.util.List;

import com.people.sotp.dataobject.LoginLogDO;

public interface LoginLogDAO {

	/**
	 * 获取总数
	 * 
	 * @param LoginLogDO
	 * @return
	 * @throws SQLException
	 */
	public Integer queryLoginLogListCount(LoginLogDO log) throws SQLException;

	/**
	 * 获取列表
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public List<LoginLogDO> queryLoginLogList(LoginLogDO log) throws SQLException;


	/**
	 * 新增
	 * 
	 * @param LoginLogDO
	 * @return
	 * @throws SQLException
	 */
	public int addLoginLog(LoginLogDO log) throws SQLException;


}
