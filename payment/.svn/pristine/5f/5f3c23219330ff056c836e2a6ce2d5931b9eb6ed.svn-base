package com.people.sotp.payment.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.SotpAuthDO;
import com.people.sotp.payment.dao.SotpAuthDAO;

@Repository
public class SotpAuthDAOImpl implements SotpAuthDAO {
	@Resource
	private BaseDao baseDao;

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	/**
	 * 获取总数
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public Integer querySotpAuthListCount(SotpAuthDO model) throws SQLException {
		return baseDao.queryTotal("SotpAuth.selectSotpAuthCount", model);
	}

	/**
	 * 获取列表
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<SotpAuthDO> querySotpAuthList(SotpAuthDO model) throws SQLException {
		return (List<SotpAuthDO>) baseDao.query("SotpAuth.selectSotpAuthList", model);
	}

	/**
	 * 新增
	 */
	public int addSotpAuth(SotpAuthDO model) throws SQLException {

		return baseDao.insert("SotpAuth.addSotpAuth", model);
	}

	/**
	 * 更改
	 */
	public int updateSotpAuth(SotpAuthDO model) throws SQLException {
		return baseDao.update("SotpAuth.updateSotpAuth", model);
	}

	/**
	 * 删除
	 */
	public int deleteSotpAuth(SotpAuthDO model) throws SQLException {
		return baseDao.delete("SotpAuth.deleteSotpAuth", model);
	}

}
