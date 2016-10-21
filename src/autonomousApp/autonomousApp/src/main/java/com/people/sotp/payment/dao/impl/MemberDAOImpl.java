package com.people.sotp.payment.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.MemberDO;

@Repository
public class MemberDAOImpl {
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
	public Integer queryMemberListCount(MemberDO userDO) throws SQLException {
		return baseDao.queryTotal("Member.selectMemberCount", userDO);
	}

	/**
	 * 获取用户列表，分页
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<MemberDO> queryMemberList(MemberDO userDO) throws SQLException {
		return (List<MemberDO>) baseDao.query("Member.selectMemberList", userDO);
	}


	/**
	 * 查询用户
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public MemberDO queryMemberOne(MemberDO userDO) throws SQLException {
		return (MemberDO) baseDao.queryOne("Member.selectMemberOne",userDO);
	}
	
	/**
	 * 查询
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public MemberDO queryMemberIdentityOne(MemberDO userDO) throws SQLException {
		return (MemberDO) baseDao.queryOne("Member.selectIdentityOne",userDO);
	}
	

	/**
	 * 新增用户
	 */
	public int addMember(MemberDO userDO) throws SQLException {

		return baseDao.insert("Member.addMember", userDO);
	}

	/**
	 * 更改用户
	 */
	public int updateMember(MemberDO userDO) throws SQLException {
		return baseDao.update("Member.updateMember", userDO);
	}

	/**
	 * 删除用户
	 */
	public int deleteMember(MemberDO userDO) throws SQLException {
		return baseDao.delete("Member.deleteMember", userDO);
	}

	/**
	 * 登录
	 */
	public MemberDO loginMember(MemberDO userDO) throws SQLException {
		return (MemberDO) baseDao.queryOne("Member.selectOneMember", userDO);
	}

	public int updatePhoneNum(MemberDO userDO) throws SQLException{
		return baseDao.update("Member.updatePhoneNum", userDO);
	}
	

}
