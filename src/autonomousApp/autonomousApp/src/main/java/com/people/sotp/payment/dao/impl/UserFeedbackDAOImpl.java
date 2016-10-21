package com.people.sotp.payment.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.UserFeedbackDO;
import com.people.sotp.payment.dao.UserFeedbackDAO;
@Repository
public class UserFeedbackDAOImpl implements UserFeedbackDAO {
	
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
	public Integer queryUserFeedbackListCount(UserFeedbackDO userDO) throws SQLException {
		return baseDao.queryTotal("UserFeedback.selectCountAuditLog", userDO);
	}

	/**
	 * 获取用户列表,分页
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<UserFeedbackDO> queryUserFeedbackList(UserFeedbackDO userDO) throws SQLException {
		return (List<UserFeedbackDO>) baseDao.query("UserFeedback.selectAuditLogList", userDO);
	}
	/*
	 * 新增用户
	 */
	public int addUserFeedback(UserFeedbackDO userDO) throws SQLException {

		return baseDao.insert("UserFeedback.addUserfeedback", userDO);
	}

	
}
