package com.people.sotp.payment.service;

import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.SotpAuthDO;

public interface SotpAuthService {

	/**
	 * 获取列表
	 * 
	 * @param model
	 *            实体类
	 * @return
	 */
	public ResultDO querySotpAuthList(SotpAuthDO model);

	/**
	 * 新增
	 * 
	 * @param model
	 * @return
	 */
	public ResultDO addSotpAuth(SotpAuthDO model);

	/**
	 * 修改
	 * 
	 * @param model
	 * @return
	 */
	public ResultDO updateSotpAuth(SotpAuthDO model);

	/**
	 * 删除
	 * 
	 * @param ids
	 */
	public void deleteSotpAuth(long[] ids);

}
