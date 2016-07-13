package com.people.sotp.payment.dao;

import java.sql.SQLException;
import java.util.List;

import com.people.sotp.dataobject.AuthDO;

public interface AuthDAO {

	/**
	 * 获取
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public Integer queryAuthListCount(AuthDO model) throws SQLException;

	/**
	 * 获取列表，分页
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public List<AuthDO> queryAuthList(AuthDO model) throws SQLException;

	/**
	 * 获取列表
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public List<AuthDO> queryAuthOne(AuthDO model) throws SQLException;

	/**
	 * 新增
	 * 
	 * @param model
	 * @return
	 * @throws SQLException
	 */
	public int addAuth(AuthDO model) throws SQLException;

	/**
	 * 修改
	 * 
	 * @param paramAuthDO
	 * @return
	 * @throws SQLException
	 */
	public int updateAuth(AuthDO model) throws SQLException;

	/**
	 * 删除
	 * 
	 * @param paramAuthDO
	 * @return
	 * @throws SQLException
	 */
	public int deleteAuth(AuthDO model) throws SQLException;

}
