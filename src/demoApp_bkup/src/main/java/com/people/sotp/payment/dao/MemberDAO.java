package com.people.sotp.payment.dao;

import java.sql.SQLException;
import java.util.List;

import com.people.sotp.dataobject.UserDO;

public interface MemberDAO {

	/**
	 * 获取总数
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public Integer queryMemberListCount(UserDO UserDO) throws SQLException;

	/**
	 * 获取列表,分页
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public List<UserDO> queryMemberList(UserDO UserDO) throws SQLException;


	/**
	 * 查询单个用户
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public UserDO queryMemberOne(UserDO UserDO) throws SQLException;
	
	/**
	 * 查询单个用户
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public UserDO queryMemberIdentityOne(UserDO UserDO) throws SQLException;
	

	/**
	 * 新增
	 * 
	 * @param UserDO
	 * @return
	 * @throws SQLException
	 */
	public int addMember(UserDO UserDO) throws SQLException;

	/**
	 * 修改
	 * 
	 * @param paramUserDO
	 * @return
	 * @throws SQLException
	 */
	public int updateMember(UserDO UserDO) throws SQLException;

	/**
	 * 删除用户
	 * 
	 * @param paramUserDO
	 * @return
	 * @throws SQLException
	 */
	public int deleteMember(UserDO UserDO) throws SQLException;

	/**
	 * 登录
	 * 
	 * @param paramUserDO
	 * @return
	 * @throws SQLException
	 */
	public UserDO loginMember(UserDO UserDO) throws SQLException;

}
