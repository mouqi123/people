package com.people.sotp.payment.service;

import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.AccountManageDO;

public interface AccountManageService {

	/**
	 * 获取账户列表,分页
	 * 
	 * @param model
	 *            实体类
	 * @return
	 */
	public ResultDO queryAccountManageList(AccountManageDO model);

	/**
	 * 获取账户列表
	 * 
	 * @param model
	 *            实体类
	 * @return
	 */
	public ResultDO queryAccountManageOne(AccountManageDO model);

	/**
	 * 新增账户
	 * 
	 * @param model
	 * @return
	 */
	public ResultDO addAccountManage(AccountManageDO model);

	/**
	 * 修改账户
	 * 
	 * @param model
	 * @return
	 */
	public ResultDO updateAccountManage(AccountManageDO model);

	/**
	 * 删除账户
	 * 
	 * @param ids
	 */
	public void deleteAccountManage(long[] ids);

}
