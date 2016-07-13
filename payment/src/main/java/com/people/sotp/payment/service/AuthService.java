package com.people.sotp.payment.service;

import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.AuthDO;

public interface AuthService {

	/**
	 * 获取用户列表,分页
	 * 
	 * @param UserDO
	 *            实体类
	 * @return
	 */
	public ResultDO queryAuthList(AuthDO userDO);

	/**
	 * 获取用户列表
	 * 
	 * @param UserDO
	 *            实体类
	 * @return
	 */
	public ResultDO queryAuthOne(AuthDO userDO);

	/**
	 * 新增用户
	 * 
	 * @param userDO
	 * @return
	 */
	public ResultDO addAuth(AuthDO userDO);

	/**
	 * 修改用户
	 * 
	 * @param userDO
	 * @return
	 */
	public ResultDO updateAuth(AuthDO userDO);

	/**
	 * 删除用户
	 * 
	 * @param ids
	 */
	public void deleteAuth(long[] ids);

}
