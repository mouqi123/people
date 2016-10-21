package com.people.sotp.payment.dao;

import java.sql.SQLException;
import java.util.List;

import com.people.sotp.dataobject.AccountManageDO;

public interface AccountManageDAO {

	/**
	 * 获取总数
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public Integer queryAccountManageListCount(AccountManageDO model) throws SQLException;

	/**
	 * 获取列表,分页
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public List<AccountManageDO> queryAccountManageList(AccountManageDO model) throws SQLException;

	/**
	 * 获取列表
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public List<AccountManageDO> queryAccountManageOne(AccountManageDO model) throws SQLException;

	/**
	 * 新增
	 * 
	 * @param model
	 * @return
	 * @throws SQLException
	 */
	public int addAccountManage(AccountManageDO model) throws SQLException;

	/**
	 * 修改
	 * 
	 * @param paramAccountManageDO
	 * @return
	 * @throws SQLException
	 */
	public int updateAccountManage(AccountManageDO model) throws SQLException;

	/**
	 * 删除
	 * 
	 * @param paramAccountManageDO
	 * @return
	 * @throws SQLException
	 */
	public int deleteAccountManage(AccountManageDO model) throws SQLException;

}
