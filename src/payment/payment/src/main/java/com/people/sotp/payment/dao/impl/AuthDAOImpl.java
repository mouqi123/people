package com.people.sotp.payment.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.AuthDO;
import com.people.sotp.payment.dao.AuthDAO;

@Repository
public class AuthDAOImpl implements AuthDAO {
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
	public Integer queryAuthListCount(AuthDO model) throws SQLException {
		return baseDao.queryTotal("Auth.selectAuthCount", model);
	}

	/**
	 * 获取用户列表,分页
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<AuthDO> queryAuthList(AuthDO model) throws SQLException {
		return (List<AuthDO>) baseDao.query("Auth.selectAuthList", model);
	}

	/**
	 * 获取用户列表
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<AuthDO> queryAuthOne(AuthDO model) throws SQLException {
		return (List<AuthDO>) baseDao.query("Auth.selectAuthOne", model);
	}

	/**
	 * 新增用户
	 */
	public int addAuth(AuthDO model) throws SQLException {

		return baseDao.insert("Auth.addAuth", model);
	}

	/**
	 * 更改用户
	 */
	public int updateAuth(AuthDO model) throws SQLException {
		return baseDao.update("Auth.updateAuth", model);
	}

	/**
	 * 删除用户
	 */
	public int deleteAuth(AuthDO model) throws SQLException {
		return baseDao.delete("Auth.deleteAuth", model);
	}

}
