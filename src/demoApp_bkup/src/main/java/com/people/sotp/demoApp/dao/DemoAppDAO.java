package com.people.sotp.demoApp.dao;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.ApplyDO;
import com.people.sotp.dataobject.LoginLogDO;
import com.people.sotp.dataobject.SotpAuthDO;
import com.people.sotp.dataobject.TransferLogDO;
import com.people.sotp.dataobject.UserDO;

@Repository
public class DemoAppDAO{
	
	@Resource
	private BaseDao baseDao;

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}
	
	/**
	 * 查询用户
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public UserDO queryUserOne(UserDO model) throws SQLException {
		return (UserDO) baseDao.queryOne("User.selectOneUser", model);
	}
	
	/**
	 * 新增用户
	 */
	public int addUser(UserDO user) throws SQLException {

		return baseDao.insert("User.addUser", user);
	}
	
	/**
	 * 修改用户
	 */
	public int updateUser(UserDO user) throws SQLException {

		return baseDao.insert("User.updateUser", user);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<LoginLogDO> queryLoginLogList(LoginLogDO loginLogDO) throws SQLException {
		return (List<LoginLogDO>) baseDao.query("Log.selectLoginLogList", loginLogDO);
	}
	
	@SuppressWarnings("unchecked")
	public List<TransferLogDO> queryTransferLogList(TransferLogDO TransferLogDO) throws SQLException {
		return (List<TransferLogDO>) baseDao.query("Log.selectTransferLogList", TransferLogDO);
	}
	
	
	/**
	 * add登录日志
	 */
	public int addLoginLog(LoginLogDO loginLogDO) throws SQLException {

		return baseDao.insert("Log.addLoginLog", loginLogDO);
	}
	/**
	 * add交易日志
	 */
	public int addTransferLog(TransferLogDO transferLogDO) throws SQLException {

		return baseDao.insert("Log.addTransferLog", transferLogDO);
	}
	
	/**
	 * 获取appkey
	 * 
	 * @param
	 * @return
	 * @throws SQLException
	 */
	public ApplyDO queryApplyOne(ApplyDO model) throws SQLException {
		return (ApplyDO) baseDao.queryOne("Apply.selectApplyOne", model);
	}
	/**
	 * 获取认证服务器地址
	 * @return
	 * @throws SQLException
	 */
	public SotpAuthDO selectService() throws SQLException {
		return (SotpAuthDO) baseDao.queryOne("SotpAuth.selectService", null);
	}

	
}
