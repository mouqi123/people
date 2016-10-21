package com.people.sotp.payment.service;

import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.ApplyDO;

public interface ApplyService {

	/**
	 * 获取列表,分页
	 * 
	 * @param model
	 *            实体类
	 * @return
	 */
	public ResultDO queryApplyList(ApplyDO model);

	/**
	 * 获取列表
	 * 
	 * @param model
	 *            实体类
	 * @return
	 */
	public ApplyDO queryApplyOne(ApplyDO model);

	/**
	 * 新增
	 * 
	 * @param model
	 * @return
	 */
	public ResultDO addApply(ApplyDO model);

	/**
	 * 修改
	 * 
	 * @param model
	 * @return
	 */
	public ResultDO updateApply(ApplyDO model);

	/**
	 * 删除
	 * 
	 * @param ids
	 */
	public void deleteApply(long[] ids);

}
