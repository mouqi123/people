package com.people.sotp.payment.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.AccountManageDO;
import com.people.sotp.payment.dao.AccountManageDAO;

@Repository
public class AccountManageDAOImpl implements AccountManageDAO {
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
	public Integer queryAccountManageListCount(AccountManageDO userDO) throws SQLException {
		return baseDao.queryTotal("AccountManage.selectAccountManageCount", userDO);
	}

	/**
	 * 获取用户列表,分页
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<AccountManageDO> queryAccountManageList(AccountManageDO userDO) throws SQLException {
		return (List<AccountManageDO>) baseDao.query("AccountManage.selectAccountManageList", userDO);
	}

	/**
	 * 获取用户列表
	 * 
	 * @param
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<AccountManageDO> queryAccountManageOne(AccountManageDO model) throws SQLException {
		return (List<AccountManageDO>) baseDao.query("AccountManage.selectAccountManageOne", model);
	}

	/**
	 * 新增用户
	 */
	public int addAccountManage(AccountManageDO userDO) throws SQLException {

		return baseDao.insert("AccountManage.addAccountManage", userDO);
	}

	/**
	 * 更改用户
	 */
	public int updateAccountManage(AccountManageDO userDO) throws SQLException {
		return baseDao.update("AccountManage.updateAccountManage", userDO);
	}

	/**
	 * 删除用户
	 */
	public int deleteAccountManage(AccountManageDO userDO) throws SQLException {
		return baseDao.delete("AccountManage.deleteAccountManage", userDO);
	}

}
