package com.people.sotp.payment.dao;

import java.sql.SQLException;
import java.util.List;

import com.people.sotp.dataobject.ApplyDO;

public interface ApplyDAO {

	/**
	 * 获取总数
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public Integer queryApplyListCount(ApplyDO model) throws SQLException;

	/**
	 * 获取列表,分页
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public List<ApplyDO> queryApplyList(ApplyDO model) throws SQLException;

	/**
	 * 获取列表
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public ApplyDO queryApplyOne(ApplyDO model) throws SQLException;

	/**
	 * 新增
	 * 
	 * @param model
	 * @return
	 * @throws SQLException
	 */
	public int addApply(ApplyDO model) throws SQLException;

	/**
	 * 修改
	 * 
	 * @param paramApplyDO
	 * @return
	 * @throws SQLException
	 */
	public int updateApply(ApplyDO model) throws SQLException;

	/**
	 * 删除
	 * 
	 * @param paramApplyDO
	 * @return
	 * @throws SQLException
	 */
	public int deleteApply(ApplyDO model) throws SQLException;

}
