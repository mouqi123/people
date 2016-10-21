package com.people.sotp.payment.dao;

import java.sql.SQLException;
import java.util.List;

import com.people.sotp.dataobject.MemberDO;

public interface MemberDAO {

	/**
	 * 获取总数
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public Integer queryMemberListCount(MemberDO userDO) throws SQLException;

	/**
	 * 获取列表,分页
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public List<MemberDO> queryMemberList(MemberDO userDO) throws SQLException;

	/**
	 * 获取列表
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public List<MemberDO> queryMemberAuthList() throws SQLException;

	/**
	 * 获取列表
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public MemberDO queryMemberOne(MemberDO userDO) throws SQLException;

	/**
	 * 新增
	 * 
	 * @param userDO
	 * @return
	 * @throws SQLException
	 */
	public int addMember(MemberDO userDO) throws SQLException;

	/**
	 * 修改
	 * 
	 * @param paramMemberDO
	 * @return
	 * @throws SQLException
	 */
	public int updateMember(MemberDO userDO) throws SQLException;

	/**
	 * 删除用户
	 * 
	 * @param paramMemberDO
	 * @return
	 * @throws SQLException
	 */
	public int deleteMember(MemberDO userDO) throws SQLException;

	/**
	 * 登录
	 * 
	 * @param paramMemberDO
	 * @return
	 * @throws SQLException
	 */
	public MemberDO loginMember(MemberDO userDO) throws SQLException;

}
