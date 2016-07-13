package com.people.sotp.payment.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.UserDO;
import com.people.sotp.payment.dao.MemberDAO;

@Repository
public class MemberDAOImpl implements MemberDAO {
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
	public Integer queryMemberListCount(UserDO UserDO) throws SQLException {
		return baseDao.queryTotal("User.selectMemberCount", UserDO);
	}

	/**
	 * 获取用户列表，分页
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<UserDO> queryMemberList(UserDO UserDO) throws SQLException {
		return (List<UserDO>) baseDao.query("User.selectMemberList", UserDO);
	}


	/**
	 * 查询用户
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public UserDO queryMemberOne(UserDO UserDO) throws SQLException {
		return (UserDO) baseDao.queryOne("User.selectOneUser",UserDO);
	}
	
	/**
	 * 查询
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public UserDO queryMemberIdentityOne(UserDO UserDO) throws SQLException {
		return (UserDO) baseDao.queryOne("User.selectIdentityOne",UserDO);
	}
	

	/**
	 * 新增用户
	 */
	public int addMember(UserDO UserDO) throws SQLException {

		return baseDao.insert("User.addUser", UserDO);
	}

	/**
	 * 更改用户
	 */
	public int updateMember(UserDO UserDO) throws SQLException {
		return baseDao.update("User.updateUser", UserDO);
	}

	/**
	 * 删除用户
	 */
	public int deleteMember(UserDO UserDO) throws SQLException {
		return baseDao.delete("User.deleteMember", UserDO);
	}

	/**
	 * 登录
	 */
	public UserDO loginMember(UserDO UserDO) throws SQLException {
		return (UserDO) baseDao.queryOne("User.selectOneMember", UserDO);
	}

}
