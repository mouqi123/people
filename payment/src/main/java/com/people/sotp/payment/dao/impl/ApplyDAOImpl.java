package com.people.sotp.payment.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.ApplyDO;
import com.people.sotp.payment.dao.ApplyDAO;

@Repository
public class ApplyDAOImpl implements ApplyDAO {
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
	public Integer queryApplyListCount(ApplyDO userDO) throws SQLException {
		return baseDao.queryTotal("Apply.selectApplyCount", userDO);
	}

	/**
	 * 获取用户列表,分页
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<ApplyDO> queryApplyList(ApplyDO userDO) throws SQLException {
		return (List<ApplyDO>) baseDao.query("Apply.selectApplyList", userDO);
	}

	/**
	 * 获取用户列表
	 * 
	 * @param
	 * @return
	 * @throws SQLException
	 */
	public ApplyDO queryApplyOne(ApplyDO model) throws SQLException {
		return (ApplyDO) baseDao.queryOne("Apply.selectApplyOne", model);
	}

	/**
	 * 新增用户
	 */
	public int addApply(ApplyDO userDO) throws SQLException {

		return baseDao.insert("Apply.addApply", userDO);
	}

	/**
	 * 更改用户
	 */
	public int updateApply(ApplyDO userDO) throws SQLException {
		return baseDao.update("Apply.updateApply", userDO);
	}

	/**
	 * 删除用户
	 */
	public int deleteApply(ApplyDO userDO) throws SQLException {
		return baseDao.delete("Apply.deleteApply", userDO);
	}

}
