package com.people.sotp.payment.dao;

import java.sql.SQLException;
import java.util.List;

import com.people.sotp.dataobject.UserFeedbackDO;

public interface UserFeedbackDAO {
	
	
	
	/**
	 * 获取总数
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public Integer queryUserFeedbackListCount(UserFeedbackDO model) throws SQLException;

	/**
	 * 获取列表,分页
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public List<UserFeedbackDO> queryUserFeedbackList(UserFeedbackDO model) throws SQLException;
	
	
	/**
	 * 新增
	 * 
	 * @param model
	 * @return
	 * @throws SQLException
	 */
	public int addUserFeedback(UserFeedbackDO model) throws SQLException;

}
